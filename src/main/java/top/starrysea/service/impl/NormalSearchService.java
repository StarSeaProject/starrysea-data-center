package top.starrysea.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import top.starrysea.dto.Count;
import top.starrysea.repository.CountRepository;
import top.starrysea.service.ISearchService;

import java.util.HashMap;
import java.util.Map;

@Service("normalSearchService")
public class NormalSearchService implements ISearchService {

	@Autowired
	private CountRepository countRepository;

	@Override
	public Mono<Count> searchCountServiceByMonth(String year,String month) {
		return countRepository.findById("day").doOnNext(c -> {
			String keyword = year + "-" + month + "-";
			Map<String, Long> newResult = new HashMap<>();
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
		return countRepository.findById("month").doOnNext(c -> {
			String keyword = year + "-";
			Map<String, Long> newResult = new HashMap<>();
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
		return countRepository.findById("year");
	}
}
