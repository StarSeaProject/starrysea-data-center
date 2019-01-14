package top.starrysea.mapreduce;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.List;
import java.util.concurrent.Future;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public abstract class Mapper implements Runnable {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	protected String inputPath;
	protected String outputPath;
	private List<Reducer> reducers;
	protected ReactiveMongoRepository<?, ?> repository;
	private Function<Runnable, Void> managerThreadPool;

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

	public Mapper setRepository(ReactiveMongoRepository<?, ?> repository) {
		this.repository = repository;
		return this;
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
					map(event);
					reducers.stream().forEach(reducer -> {
						reducer.setInputPath(outputPath);
						reducer.setFileName(event.context().toString());
						reducer.setManagerThreadPool(managerThreadPool);
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

	protected abstract void map(WatchEvent<?> event);

	protected abstract void mapReduceFinish(List<Future<?>> futures);
}
