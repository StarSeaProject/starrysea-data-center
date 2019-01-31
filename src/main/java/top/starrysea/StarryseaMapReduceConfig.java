package top.starrysea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import top.starrysea.mapper.DateMapper;
import top.starrysea.mapper.IdMapper;
import top.starrysea.mapreduce.StarryseaMapReduceConfiguration;
import top.starrysea.mapreduce.StarryseaMapReduceManager;
import top.starrysea.reducer.DayReducer;
import top.starrysea.reducer.IdReducer;
import top.starrysea.reducer.MonthReducer;
import top.starrysea.reducer.YearReducer;
import top.starrysea.repository.CountRepository;

@Configuration
public class StarryseaMapReduceConfig {

	@Value("${starrysea.split.input}")
	private String inputPath;
	@Value("${starrysea.split.output}")
	private String outputPath;

	@Autowired
	private DateMapper dateMapper;
	@Autowired
	private MonthReducer monthReducer;
	@Autowired
	private DayReducer dayReducer;
	@Autowired
	private YearReducer yearReducer;

	@Autowired
	private IdMapper idMapper;
	@Autowired
	private IdReducer idReducer;

	@Bean
	public StarryseaMapReduceManager getStarryseaMapReduceManager(CountRepository countRepository) {
		StarryseaMapReduceManager starryseaMapReduceManager = new StarryseaMapReduceManager(
				StarryseaMapReduceConfiguration.of().input(inputPath).output(outputPath));
		starryseaMapReduceManager.register(dateMapper, monthReducer, dayReducer, yearReducer);
		starryseaMapReduceManager.register(idMapper, idReducer);
		starryseaMapReduceManager.run();
		return starryseaMapReduceManager;
	}
}
