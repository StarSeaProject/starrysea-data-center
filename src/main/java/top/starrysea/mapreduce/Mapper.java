package top.starrysea.mapreduce;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import top.starrysea.bo.SingleMessage;

public abstract class Mapper implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected String inputPath;
	protected String outputPath;
	private List<Reducer> reducers;
	private Function<Runnable, Void> managerThreadPool;
	private SingleMessage singleMessage;

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	public void setReducers(List<Reducer> reducers) {
		this.reducers = reducers;
	}

	public List<Reducer> getReducers() {
		return reducers;
	}

	public void setManagerThreadPool(Function<Runnable, Void> runReducerTask) {
		this.managerThreadPool = runReducerTask;
	}

	@Override
	public void run() {
		try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
			File inputDir = new File(inputPath);
			if (!inputDir.exists()) {
				inputDir.mkdirs();
				logger.info("{} 目录已创建", inputPath);
			}
			File outputDir = new File(outputPath);
			if (!outputDir.exists()) {
				outputDir.mkdirs();
				logger.info("{} 目录已创建", outputPath);
			}
			logger.info("现可将聊天记录文件放入{}/中,处理完成后将输出至{}/", inputPath, outputPath);
			Path path = inputDir.toPath();
			path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
			WatchKey key;
			boolean updated = false;
			while ((key = watchService.take()) != null) {
				List<WatchEvent<?>> list = key.pollEvents();
				for (WatchEvent<?> event : list) {
					// 因为jdk的watchservice在文件修改时会触发两次MODIFY事件
					// 分别是元数据修改和数据内容修改,而这个是OS层规定的,无法避免
					// 所以要把第一次的修改事件,也就是元数据修改的事件过滤掉
					if (!updated) {
						updated = true;
						continue;
					}
					logger.info("检测到文件变化: {} {}", event.context().toString(), event.kind().toString());
					updated = false;
					MapReduceContext context = new MapReduceContext(event.context().toString(), outputFileSubType(),
							inputPath, outputPath);
					split(context);
					reducers.stream().forEach(reducer -> {
						context.setAttribute("managerThreadPool", managerThreadPool);
						reducer.setContext(context);
						managerThreadPool.apply(reducer);
					});
				}
				key.reset();
			}
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void split(MapReduceContext context) {
		String fileName = context.getOutputFileName() + "." + context.getFileExtendsion();
		File file = new File(inputPath, fileName);
		try (Stream<String> stream = Files.lines(file.toPath())) {
			stream.forEach(s -> execStr(s.replace("\ufeff", ""), context));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		execStr(context);
		context.writeFlush();
		// 将最后的聊天记录送出
		logger.info("Map结果存储于{}/{}/", outputPath, fileName.substring(0, fileName.lastIndexOf('.')));
	}

	private void execStr(String str, MapReduceContext context) {
		String pattern = "\\d{4}-\\d{2}-\\d{2} \\d{1,2}:\\d{2}:\\d{2} .+([<(]).+([>)])";
		// 用于判断单个群聊聊天记录开头(日期,昵称,QQ号或邮箱)的正则表达式
		if (str == null)
			return;
		if (Pattern.matches(pattern, str)) {
			if (singleMessage != null) {
				map(singleMessage, context);
			}
			singleMessage = new SingleMessage();
			singleMessage.setHead(str);
		} else {
			if (singleMessage.getBody() == null) {
				singleMessage.setBody(str + "\\n");
			} else {
				singleMessage.setBody(singleMessage.getBody() + str + "\\n");
			}
		}
	}

	private void execStr(MapReduceContext context) {
		if (singleMessage == null)
			return;
		map(singleMessage, context);
	}

	protected abstract MapReduceContext map(SingleMessage singleMessage, MapReduceContext context);

	protected abstract String outputFileSubType();

}
