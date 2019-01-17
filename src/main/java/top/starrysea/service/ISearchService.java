package top.starrysea.service;

import reactor.core.publisher.Mono;
import top.starrysea.dto.Count;

public interface ISearchService {

	Mono<Count> SearchCountService(String type);
}
