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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

public class DayReducer extends Reducer {
	private Map<String, Long> chatCount;
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	private CountRepository countRepository;

	@Override
	protected void reduce(MapReduceContext context) {
		chatCount = new ConcurrentHashMap<>();
		String fileNameWithoutExtension = getFileName().substring(0, getFileName().lastIndexOf('.'));
		analyze(getInputPath() + "/" + fileNameWithoutExtension + "/byDate");
	}

	private void analyze(String fileDirectory) {
		List<File> fileList = new ArrayList<>();
		File rootDir = new File(fileDirectory);
		File[] files = rootDir.listFiles();
		for(File i :files){
			if(i.isFile())
				fileList.add(i);
		}
		CountDownLatch countDownLatch = new CountDownLatch(fileList.size());
		fileList.forEach(f -> managerThreadPool.apply(new ChatCount(f, countDownLatch)));
		try {
			countDownLatch.await();
			logger.info("对每日发言数的分析结束.");
			logger.info("共有{}天.", chatCount.size());
			Count count = new Count();
			count.setType("day");
			count.setResult(chatCount);
			countRepository.findById("day").defaultIfEmpty(count).subscribe(chatCountTemp -> {
				chatCountTemp.getResult().putAll(chatCount);
				chatCountTemp.setResult(chatCount);
				countRepository.save(chatCountTemp).subscribe();
				logger.info("每日分析已存入数据库.");
			});
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
	}

	private class ChatCount implements Runnable {
		private File path;
		private String date;
		private CountDownLatch countDownLatch;

		ChatCount(File path, CountDownLatch countDownLatch) {
			this.path = path;
			this.countDownLatch = countDownLatch;
		}

		@Override
		public void run() {
            date = path.getName();
            date = date.substring(0, date.lastIndexOf('.'));
			long count = 0;
			try {
				count = Files.lines(path.toPath()).map(s -> s.replace("\ufeff", ""))
				.count();
			} catch (IOException e) {
				logger.error(e.getMessage(), e);
			}
			if (chatCount.containsKey(date)) {
                chatCount.put(date, chatCount.get(date) + count);
			} else {
				chatCount.put(date, count);
			}
			countDownLatch.countDown();
		}
	}

	public Reducer setCountRepository(CountRepository countRepository) {
		this.countRepository = countRepository;
		return this;
	}
}
