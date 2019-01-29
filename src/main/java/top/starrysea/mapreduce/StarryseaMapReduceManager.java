package top.starrysea.mapreduce;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import top.starrysea.mapper.DateMapper;
import top.starrysea.mapper.IdMapper;
import top.starrysea.reducer.DayReducer;
import top.starrysea.reducer.IdReducer;
import top.starrysea.reducer.MonthReducer;
import top.starrysea.reducer.YearReducer;
import top.starrysea.repository.CountRepository;

@Component
public class StarryseaMapReduceManager implements InitializingBean {

	private ThreadPoolTaskExecutor mapperThreadPool;
	private ThreadPoolTaskExecutor reducerThreadPool;
	private List<MapperAndReduce> mapperAndReduces;

	@Value("${starrysea.split.input}")
	private String inputPath;
	@Value("${starrysea.split.output}")
	private String outputPath;

	@Autowired
	private CountRepository countRepository;

	@PostConstruct
	private void init() {
		mapperAndReduces = new ArrayList<>();
		mapperThreadPool = new ThreadPoolTaskExecutor();
		mapperThreadPool.setCorePoolSize(Runtime.getRuntime().availableProcessors());
		mapperThreadPool.setMaxPoolSize(10);
		mapperThreadPool.setQueueCapacity(25);
		mapperThreadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		mapperThreadPool.initialize();

		reducerThreadPool = new ThreadPoolTaskExecutor();
		reducerThreadPool.setCorePoolSize(Runtime.getRuntime().availableProcessors());
		reducerThreadPool.setMaxPoolSize(10);
		reducerThreadPool.setQueueCapacity(25);
		reducerThreadPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		reducerThreadPool.initialize();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		this.register(new DateMapper(), new MonthReducer().setCountRepository(countRepository),
				new DayReducer().setCountRepository(countRepository),
				new YearReducer().setCountRepository(countRepository));
		this.register(new IdMapper(), new IdReducer().setCountRepository(countRepository));
		this.run();
	}

	private StarryseaMapReduceManager register(Mapper mapper, Reducer... reducers) {
		mapper.setInputPath(inputPath);
		mapper.setOutputPath(outputPath);
		mapper.setManagerThreadPool(this::executeMapperTask);
		for (Reducer reducer : reducers) {
			reducer.setManagerThreadPool(this::executeReducerTask);
		}
		mapperAndReduces.add(MapperAndReduce.of(mapper, reducers));
		return this;
	}

	private void run() {
		mapperAndReduces.stream().forEach(mapperAndReduce -> mapperThreadPool.execute(mapperAndReduce.getMapper()));
	}

	private Void executeMapperTask(Runnable task) {
		mapperThreadPool.execute(task);
		return null;
	}

	private Void executeReducerTask(Runnable task) {
		reducerThreadPool.execute(task);
		return null;
	}

}
