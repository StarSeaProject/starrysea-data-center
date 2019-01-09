package top.starrysea.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import top.starrysea.dto.Most;
import top.starrysea.repository.MostRepository;
import top.starrysea.service.ISearchService;

@Service("normalSearchService")
public class NormalSearchService implements ISearchService {

	@Autowired
	private MostRepository mostRepository;

	@Override
	public Mono<Most> searchMostService(String keyword) {
		return mostRepository.findById(keyword);
	}

}
