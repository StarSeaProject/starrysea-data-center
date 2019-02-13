package top.starrysea.reducer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import top.starrysea.bo.SingleMessage;
import top.starrysea.dto.Repeat;
import top.starrysea.mapreduce.MapReduceContext;
import top.starrysea.mapreduce.ReduceResult;
import top.starrysea.mapreduce.reducer.MapLongReducer;
import top.starrysea.repository.RepeatRepository;

@Component
public class DayRepeatReducer extends MapLongReducer {

	@Autowired
	private RepeatRepository repeatRepository;

	@Override
	protected ReduceResult<Map<String, Long>> reduce(File path) {
		Map<String, Long> result = new HashMap<>();
		try (Stream<String> line = Files.lines(path.toPath())) {
			result = line.map(SingleMessage::stringToMessage)
					.filter(singleMessage -> !singleMessage.getBody().equals("[图片]\\n\\n")
							|| !singleMessage.getBody().equals("[表情]\\n\\n"))
					.map(SingleMessage::getBody)
					.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
		Map<String, Long> theResult = new HashMap<>();
		for (Map.Entry<String, Long> entry : result.entrySet()) {
			if (entry.getValue() != 1L) {
				theResult.put(entry.getKey(), entry.getValue());
			}
		}
		String date = path.getName();
		date = date.substring(0, date.lastIndexOf('.'));
		return ReduceResult.of(date, theResult);
	}

	@Override
	protected void reduceFinish(Map<String, Map<String, Long>> reduceResult, MapReduceContext context) {
		for (Map.Entry<String, Map<String, Long>> entry : reduceResult.entrySet()) {
			Repeat repeat = new Repeat();
			repeat.setId(entry.getKey());
			repeat.setResult(entry.getValue());
			repeatRepository.findById(entry.getKey()).defaultIfEmpty(repeat).subscribe(repeatTemp -> {
				repeatTemp.getResult().putAll(entry.getValue());
				repeatRepository.save(repeatTemp).subscribe();
			});
		}
		logger.info("每日复读统计已存入数据库.");
	}

}
