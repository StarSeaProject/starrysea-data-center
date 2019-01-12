package top.starrysea.mapreduce;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

public abstract class Reducer<T> implements Callable<T> {

	protected String inputPath;
	private CountDownLatch countDownLatch;
	private boolean isFinish = false;

	@Override
	public final T call() {
		T result = reduce();
		if (isFinish)
			throw new IllegalStateException("没有调用finish()方法,请确保reducer的最后要调用");
		return result;
	}

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public void setCountDownLatch(CountDownLatch countDownLatch) {
		this.countDownLatch = countDownLatch;
	}

	protected final void finish() {
		isFinish = true;
		countDownLatch.countDown();
	}

	protected abstract T reduce();
}
