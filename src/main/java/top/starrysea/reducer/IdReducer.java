package top.starrysea.reducer;

import top.starrysea.dto.Count;
import top.starrysea.mapreduce.MapReduceContext;
import top.starrysea.mapreduce.ReduceResult;
import top.starrysea.mapreduce.Reducer;
import top.starrysea.repository.CountRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class IdReducer extends Reducer {

	@Autowired
	private CountRepository countRepository;

	public Reducer setCountRepository(CountRepository countRepository) {
		this.countRepository = countRepository;
		return this;
	}

	@Override
	protected ReduceResult reduce(File path) {
		long count = 0;
		try (Stream<String> line = Files.lines(path.toPath())) {
			count = line.map(s -> s.replace("\ufeff", "")).count();
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		String id = path.getName();
		id = id.substring(0, id.lastIndexOf('.'));
		id = id.replace('.', '^');
		return ReduceResult.of(id, count);
	}

	@Override
	protected void reduceFinish(Map<String, Long> reduceResult, MapReduceContext context) {
		Count count = new Count();
		count.setType("userId");
		count.setResult(reduceResult);
		countRepository.findById("userId").defaultIfEmpty(count).subscribe(chatCountTemp -> {
			chatCountTemp.getResult().putAll(reduceResult);
			chatCountTemp.setResult(reduceResult);
			countRepository.save(chatCountTemp).subscribe();
			logger.info("id分析已存入数据库.");
		});
	}

}
