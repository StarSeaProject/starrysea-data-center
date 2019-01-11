package top.starrysea.mapreduce;

import java.util.Arrays;
import java.util.List;

public class MapperAndReduce {

	private Mapper mapper;
	private List<Reducer<?>> reducers;

	private MapperAndReduce(Mapper mapper, List<Reducer<?>> reducers) {
		mapper.setReducers(reducers);
		this.mapper = mapper;
		this.reducers = reducers;
	}

	public static MapperAndReduce of(Mapper mapper, Reducer<?>... reducers) {
		return new MapperAndReduce(mapper, Arrays.asList(reducers));
	}

	public Mapper getMapper() {
		return mapper;
	}

	public List<Reducer<?>> getReducers() {
		return reducers;
	}

}
