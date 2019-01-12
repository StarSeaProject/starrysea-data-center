package top.starrysea.mapreduce;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import top.starrysea.mapper.DateMapper;
import top.starrysea.reducer.DateReducer;
import top.starrysea.repository.MostRepository;

@Component
public class StarryseaMapreduceManager implements InitializingBean {

	private static ThreadPoolTaskExecutor threadPool;
	private static List<MapperAndReduce> mapperAndReduces;

	private static String inputPath;
	private static String outputPath;

	@Autowired
	private MostRepository mostRepository;

	@Value("${starrysea.split.input}")
	public void setInputPath(String inputPath) {
		StarryseaMapreduceManager.inputPath = inputPath;
	}

	@Value("${starrysea.split.output}")
	public void setOutputPath(String outputPath) {
		StarryseaMapreduceManager.outputPath = outputPath;
	}

	static {
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
		StarryseaMapreduceManager.register(dateMapper, dateReducer);
		StarryseaMapreduceManager.run();
	}

	public static void register(Mapper mapper, Reducer<?>... reducers) {
		mapper.setInputPath(inputPath);
		mapper.setOutputPath(outputPath);
		mapperAndReduces.add(MapperAndReduce.of(mapper, reducers));
	}

	public static void run() {
		mapperAndReduces.stream().forEach(mapperAndReduce -> {
			Mapper mapper = mapperAndReduce.getMapper();
			threadPool.execute(mapper);
		});
	}

	public static Future<?> runCallableTask(Callable<?> task) {
		return threadPool.submit(task);
	}

}
