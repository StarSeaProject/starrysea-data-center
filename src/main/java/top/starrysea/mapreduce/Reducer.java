package top.starrysea.mapreduce;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Reducer implements Runnable {

	protected final Logger logger = LoggerFactory.getLogger(this.getClass());
	private MapReduceContext context;
	protected Function<Runnable, Void> managerThreadPool;
	private ConcurrentHashMap<String, AtomicLong> reduceResult;

	@Override
	public final void run() {
		reduceResult = new ConcurrentHashMap<>();
		String fileNameWithoutExtension = getFileName().substring(0, getFileName().lastIndexOf('.'));
		analyze(getInputPath() + "/" + fileNameWithoutExtension + "/" + context.getOutputFileSubType());
	}

	public String getInputPath() {
		return context.getOutputPath();
	}

	public String getFileName() {
		return context.getOutputFileName() + "." + context.getOutputFileSubType();
	}

	public void setContext(MapReduceContext context) {
		this.context = context;
	}

	public void setManagerThreadPool(Function<Runnable, Void> managerThreadPool) {
		this.managerThreadPool = managerThreadPool;
	}

	private void analyze(String fileDirectory) {
		List<File> fileList = new ArrayList<>();
		File rootDir = new File(fileDirectory);
		File[] files = rootDir.listFiles();
		for (File i : files) {
			if (i.isFile())
				fileList.add(i);
		}
		CountDownLatch countDownLatch = new CountDownLatch(fileList.size());
		fileList.stream().forEach(f -> managerThreadPool.apply(new ReduceTask(f, countDownLatch)));
		try {
			countDownLatch.await();
			Map<String, Long> finalResult = new HashMap<>();
			for (Map.Entry<String, AtomicLong> entry : reduceResult.entrySet()) {
				finalResult.put(entry.getKey(), entry.getValue().get());
			}
			reduceFinish(finalResult, context);
		} catch (InterruptedException e) {
			logger.error(e.getMessage(), e);
			Thread.currentThread().interrupt();
		}
	}

	private class ReduceTask implements Runnable {
		private File path;
		private CountDownLatch countDownLatch;

		ReduceTask(File path, CountDownLatch countDownLatch) {
			this.path = path;
			this.countDownLatch = countDownLatch;
		}

		@Override
		public void run() {
			ReduceResult aReduceResult = reduce(path);
			AtomicLong oldResult = new AtomicLong();
			if (reduceResult.containsKey(aReduceResult.getGroup())) {
				oldResult = reduceResult.get(aReduceResult.getGroup());
			}
			oldResult.addAndGet(aReduceResult.getResult());
			reduceResult.put(aReduceResult.getGroup(), oldResult);
			countDownLatch.countDown();
		}
	}

	protected abstract ReduceResult reduce(File path);

	protected abstract void reduceFinish(Map<String, Long> reduceResult, MapReduceContext context);
}
