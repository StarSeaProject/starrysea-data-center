package top.starrysea.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import top.starrysea.common.RedisOperations;
import top.starrysea.dto.Count;
import top.starrysea.redis.CountTemplate;
import top.starrysea.repository.CountRepository;
import top.starrysea.service.ISearchService;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service("normalSearchService")
public class NormalSearchService implements ISearchService {

	@Autowired
	private CountRepository countRepository;

	@Autowired
	private CountTemplate countTemplate;

	@Override
	public Mono<Count> searchCountServiceByMonth(String year,String month) {
		return RedisOperations.getMono(countRepository, countTemplate, "day", c -> {
			String keyword = year + "-" + month + "-";
			Map<String, Long> newResult = new TreeMap<>();
			//使用TreeMap自动排序,下同
			c.getResult().forEach((key, value) -> {
				if (key.contains(keyword)) {
					newResult.put(key, value);
				}
			});
			c.setResult(newResult);
		});
	}

	@Override
	public Mono<Count> searchCountServiceByYear(String year) {
		return RedisOperations.getMono(countRepository, countTemplate, "month", c -> {
			String keyword = year + "-";
			Map<String, Long> newResult = new TreeMap<>();
			c.getResult().forEach((key, value) -> {
				if (key.contains(keyword)) {
					newResult.put(key, value);
				}
			});
			c.setResult(newResult);
		});
	}

	@Override
	public Mono<Count> searchCountService() {
		return RedisOperations.getMono(countRepository, countTemplate, "year", c -> {
			Map<String, Long> newResult = new TreeMap<>();
			c.getResult().forEach(newResult::put);
			c.setResult(newResult);
		});
	}
	@Override
    public Mono<Count> searchCountServiceById() {
        return RedisOperations.getMono(countRepository, countTemplate, "userId", c -> {
            Map<String, Long> newResult = new HashMap<>();
            c.getResult().forEach((s, l) -> {
                s = s.replace('^', '.');
                newResult.put(s, l);
            });
            c.setResult(newResult);
        });
	}
}
