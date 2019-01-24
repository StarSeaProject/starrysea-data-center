package top.starrysea.service;

import reactor.core.publisher.Mono;
import top.starrysea.dto.Count;

public interface ISearchService {

	Mono<Count> searchCountService(String year, String month);
	Mono<Count> searchCountService(String year);
	Mono<Count> searchCountService();
}
