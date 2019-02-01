package top.starrysea.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;
import top.starrysea.dto.Count;
import top.starrysea.keyword.KeywordChain;
import top.starrysea.keyword.KeywordFilter;
import top.starrysea.service.ISearchService;
import top.starrysea.vo.CountResource;
import top.starrysea.vo.SearchResource;

@RestController
@RequestMapping("/sdc")
public class SearchController {

	@Autowired
	private ISearchService searchService;
	@Autowired
	private KeywordFilter keywordFilter;

	@GetMapping("/search/{keyword}")
	public Mono<SearchResource> search(@PathVariable("keyword") String keyword) {
		return KeywordChain.startKeywordChain(keyword, keywordFilter::hasKeyword);
	}

	@GetMapping("/date/{year}/{month}")
	public Mono<CountResource> searchCountByMonth(@PathVariable("year") String year,
			@PathVariable("month") String month) {
		Mono<Count> serviceResult = searchService.searchCountServiceByMonth(year, month);
		return serviceResult.map((Count search) -> CountResource.of(search, year, month));
	}

	@GetMapping("/date/{year}")
	public Mono<CountResource> searchCountByYear(@PathVariable String year) {
		Mono<Count> serviceResult = searchService.searchCountServiceByYear(year);
		return serviceResult.map((Count search) -> CountResource.of(search, year));
	}

	@GetMapping("/date")
	public Mono<CountResource> searchCount() {
		Mono<Count> serviceResult = searchService.searchCountService();
		return serviceResult.map(CountResource::of);
	}
}
