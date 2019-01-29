package top.starrysea.mapreduce;

public class ReduceResult {

	private String group;
	private long result;

	private ReduceResult(String group, long result) {
		this.group = group;
		this.result = result;
	}

	public static ReduceResult of(String group, long result) {
		return new ReduceResult(group, result);
	}

	public String getGroup() {
		return group;
	}

	public long getResult() {
		return result;
	}

}
