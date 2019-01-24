package top.starrysea.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import top.starrysea.dto.Count;
import top.starrysea.repository.CountRepository;
import top.starrysea.service.ISearchService;

@Service("normalSearchService")
public class NormalSearchService implements ISearchService {

	@Autowired
	private CountRepository countRepository;

	@Override
	public Mono<Count> searchCountService(String year,String month) {
		return countRepository.findById("day");
	}

	@Override
	public Mono<Count> searchCountService(String year) {
		return countRepository.findById("month");
	}

	@Override
	public Mono<Count> searchCountService() {
		return countRepository.findById("year");
	}
}
