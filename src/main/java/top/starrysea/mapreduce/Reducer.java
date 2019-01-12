package top.starrysea.mapreduce;

import java.util.function.Function;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public abstract class Reducer implements Runnable {

	protected String inputPath;
	private String fileName;
	protected ReactiveMongoRepository<?, ?> repository;
	protected Function<Runnable, Void> managerThreadPool;

	@Override
	public final void run() {
		reduce();
	}

	public String getInputPath() {
		return inputPath;
	}

	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileName() {
		return fileName;
	}

	public Reducer setRepository(ReactiveMongoRepository<?, ?> repository) {
		this.repository = repository;
		return this;
	}

	public void setManagerThreadPool(Function<Runnable, Void> managerThreadPool) {
		this.managerThreadPool = managerThreadPool;
	}

	protected abstract void reduce();
}
