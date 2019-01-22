package top.starrysea.mapreduce;

import java.util.function.Function;

public abstract class Reducer implements Runnable {

	private MapReduceContext context;
	protected Function<Runnable, Void> managerThreadPool;

	@Override
	public final void run() {
		reduce(context);
	}

	public String getInputPath() {
		return context.getOutputPath();
	}

	public String getFileName() {
		return context.getOutputFileName() + "." + context.getOutputFileSubType();
	}

	@SuppressWarnings("unchecked")
	public void setContext(MapReduceContext context) {
		this.context = context;
		this.managerThreadPool = (Function<Runnable, Void>) context.getAttribute("managerThreadPool");
	}

	protected abstract void reduce(MapReduceContext context);
}
