package top.starrysea;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import top.starrysea.mapper.DateMapper;
import top.starrysea.mapper.IdMapper;
import top.starrysea.mapreduce.StarryseaMapReduceManager;
import top.starrysea.reducer.DayReducer;
import top.starrysea.reducer.IdReducer;
import top.starrysea.reducer.MonthReducer;
import top.starrysea.reducer.YearReducer;
import top.starrysea.repository.CountRepository;

@Configuration
public class StarryseaMapReduceConfiguration {

	@Autowired
	private CountRepository countRepository;
	@Value("${starrysea.split.input}")
	private String inputPath;
	@Value("${starrysea.split.output}")
	private String outputPath;

	@Bean
	public StarryseaMapReduceManager getStarryseaMapReduceManager() {
		StarryseaMapReduceManager starryseaMapReduceManager = new StarryseaMapReduceManager(inputPath, outputPath);
		starryseaMapReduceManager.register(new DateMapper(), new MonthReducer().setCountRepository(countRepository),
				new DayReducer().setCountRepository(countRepository),
				new YearReducer().setCountRepository(countRepository));
		starryseaMapReduceManager.register(new IdMapper(), new IdReducer().setCountRepository(countRepository));
		starryseaMapReduceManager.run();
		return starryseaMapReduceManager;
	}
}
