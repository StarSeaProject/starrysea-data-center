package top.starrysea.service;

import reactor.core.publisher.Mono;
import top.starrysea.dto.Count;

public interface ISearchService {

	Mono<Count> searchCountServiceByMonth(String year, String month);
	Mono<Count> searchCountServiceByYear(String year);
	Mono<Count> searchCountService();
}
