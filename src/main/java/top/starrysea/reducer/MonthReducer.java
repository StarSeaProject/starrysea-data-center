package top.starrysea.reducer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.starrysea.dto.Count;
import top.starrysea.mapreduce.MapReduceContext;
import top.starrysea.mapreduce.Reducer;
import top.starrysea.repository.CountRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

public class MonthReducer extends Reducer {
	private Map<String, Long> chatCount;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private CountRepository countRepository;

	@Override
	protected void reduce(MapReduceContext context) {
		chatCount = new ConcurrentHashMap<>();
		String fileNameWithoutExtension = getFileName().substring(0, getFileName().lastIndexOf('.'));
		analyze(getInputPath() + "/" + fileNameWithoutExtension);
	}

	private void analyze(String fileDirectory) {
		List<File> files = new ArrayList<>();
		File rootDir = new File(fileDirectory);
		File[] years = rootDir.listFiles();
		for (File i : years) {
			if (i.isDirectory()) {
				File[] months = i.listFiles();
				for (File j : months) {// 获取所有月份的目录
					if (j.isDirectory()) {
						files.add(j);
					}
				}
			}
		}
		CountDownLatch countDownLatch = new CountDownLatch(files.size());
		files.forEach(f -> managerThreadPool.apply(new ChatCount(f, countDownLatch)));
		try {
			countDownLatch.await();
			logger.info("对每月发言数的分析结束.");
			logger.info("共有{}个月.", chatCount.size());
			Count count = new Count();
			count.setType("month");
			count.setResult(chatCount);
			countRepository.findById("month").defaultIfEmpty(count).subscribe(chatCountTemp -> {
				chatCountTemp.getResult().putAll(chatCount);
				chatCountTemp.setResult(chatCount);
				countRepository.save(chatCountTemp).subscribe();
				logger.info("每月分析已存入数据库.");
			});
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
	}

	private class ChatCount implements Runnable {
		private File dirPath;
		private String pattern = "\\d{4}-\\d{2}-\\d{2} \\d{1,2}:\\d{2}:\\d{2} .+([<(]).+([>)])";
		// 用于判断单个群聊聊天记录开头(日期,昵称,QQ号或邮箱)的正则表达式
		private CountDownLatch countDownLatch;

		ChatCount(File dirPath, CountDownLatch countDownLatch) {
			this.dirPath = dirPath;
			this.countDownLatch = countDownLatch;
		}

		@Override
		public void run() {
			String dir = dirPath.toString();
			String date = dir.substring(dir.length() - 7);
			date = date.replace("\\", "/");
			File[] items = dirPath.listFiles();
			List<File> itemsArrayList = new ArrayList<>();
			for (File f : items) {
				if (f.isFile())
					itemsArrayList.add(f);
			}
			long count = itemsArrayList.stream().mapToLong(f -> {
				try {
					return Files.lines(f.toPath()).map(s -> s.replace("\ufeff", ""))
							.filter(s -> Pattern.matches(pattern, s)).count();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
					return 0;
				}
			}).sum();
			chatCount.put(date, count);
			countDownLatch.countDown();
		}
	}

	public Reducer setCountRepository(CountRepository countRepository) {
		this.countRepository = countRepository;
		return this;
	}
}
