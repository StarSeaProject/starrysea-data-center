package top.starrysea.reducer;

import top.starrysea.mapreduce.Reducer;

public class DateReducer extends Reducer<Integer> {

	@Override
	protected Integer reduce() {
		return 1;
	}

}
