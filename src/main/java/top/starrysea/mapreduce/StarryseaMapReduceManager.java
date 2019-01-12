package top.starrysea.mapreduce;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import top.starrysea.mapper.DateMapper;
import top.starrysea.reducer.DateReducer;
import top.starrysea.repository.MostRepository;

@Component
public class StarryseaMapReduceManager implements InitializingBean {

	private ThreadPoolTaskExecutor threadPool;
	private List<MapperAndReduce> mapperAndReduces;

	private String inputPath;
	private String outputPath;

	@Autowired
	private MostRepository mostRepository;

	@Value("${starrysea.split.input}")
	public void setInputPath(String inputPath) {
		this.inputPath = inputPath;
	}

	@Value("${starrysea.split.output}")
	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}

	@PostConstruct
	private void init() {
		mapperAndReduces = new ArrayList<>();
		threadPool = new ThreadPoolTaskExecutor();
		threadPool.setCorePoolSize(Runtime.getRuntime().availableProcessors());
		threadPool.setMaxPoolSize(10);
		threadPool.setQueueCapacity(25);
		threadPool.initialize();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Mapper dateMapper = new DateMapper();
		dateMapper.setRepository(mostRepository);
		Reducer<Integer> dateReducer = new DateReducer();
		this.register(dateMapper, dateReducer);
		// this.run();
	}

	private StarryseaMapReduceManager register(Mapper mapper, Reducer<?>... reducers) {
		mapper.setInputPath(inputPath);
		mapper.setOutputPath(outputPath);
		mapper.setRunReducerTask(this::runCallableTask);
		mapperAndReduces.add(MapperAndReduce.of(mapper, reducers));
		return this;
	}

	private void run() {
		mapperAndReduces.stream().forEach(mapperAndReduce -> {
			Mapper mapper = mapperAndReduce.getMapper();
			threadPool.execute(mapper);
		});
	}

	private Future<?> runCallableTask(Callable<?> task) {
		return threadPool.submit(task);
	}

}
