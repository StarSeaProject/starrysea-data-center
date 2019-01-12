package top.starrysea.mapreduce;

import java.util.ArrayList;
import java.util.List;

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

	@Value("${starrysea.split.input}")
	private String inputPath;
	@Value("${starrysea.split.output}")
	private String outputPath;

	@Autowired
	private MostRepository mostRepository;

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
		this.register(new DateMapper(), new DateReducer().setRepository(mostRepository));
		this.run();
	}

	private StarryseaMapReduceManager register(Mapper mapper, Reducer... reducers) {
		mapper.setInputPath(inputPath);
		mapper.setOutputPath(outputPath);
		mapper.setManagerThreadPool(this::runCallableTask);
		mapperAndReduces.add(MapperAndReduce.of(mapper, reducers));
		return this;
	}

	private void run() {
		mapperAndReduces.stream().forEach(mapperAndReduce -> threadPool.execute(mapperAndReduce.getMapper()));
	}

	private Void runCallableTask(Runnable task) {
		threadPool.execute(task);
		return null;
	}

}
