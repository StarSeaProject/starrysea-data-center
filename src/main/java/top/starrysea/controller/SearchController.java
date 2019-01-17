package top.starrysea.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import top.starrysea.dto.Count;
import top.starrysea.service.ISearchService;
import top.starrysea.vo.CountResource;

@RestController
public class SearchController {

	@Autowired
	private ISearchService searchService;

	@GetMapping("/search/count/{type}")
	public Mono<CountResource> searchCount(@PathVariable String type){
		Mono<Count> serviceResult = searchService.SearchCountService(type);
		return serviceResult.map(CountResource::of);
	}
}
