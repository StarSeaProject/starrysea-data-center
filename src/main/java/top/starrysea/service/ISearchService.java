package top.starrysea.service;

import reactor.core.publisher.Mono;
import top.starrysea.dto.Count;
import top.starrysea.dto.Most;

public interface ISearchService {

	Mono<Most> searchMostService(String keyword);
	Mono<Count> SearchCountService(String type);
}
