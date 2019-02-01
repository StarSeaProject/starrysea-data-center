package top.starrysea.keyword;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

import reactor.core.publisher.Mono;
import top.starrysea.vo.SearchResource;

public class KeywordChain {

	private String keyword;
	private Function<String, Set<String>> keywordFilter;

	private final Consumer<? super SearchResource> timeKeyword = searchResource -> {
		if (isValid("time")) {
			searchResource.addDateSearchLink();
		}
	};

	private final Consumer<? super SearchResource> idKeyword = searchResource -> {
		if (isValid("id")) {
			searchResource.addDateSearchLink();
		}
	};

	private boolean isValid(String type) {
		Set<String> validResource = keywordFilter.apply(keyword);
		return validResource.contains(type);
	}

	public static Mono<SearchResource> startKeywordChain(String keyword, Function<String, Set<String>> keywordFilter) {
		KeywordChain keywordChain = new KeywordChain();
		keywordChain.keyword = keyword;
		keywordChain.keywordFilter = keywordFilter;
		Mono<SearchResource> chain = Mono.justOrEmpty(new SearchResource());
		for (Consumer<? super SearchResource> consumer : keywordChain.getAllKeywordConsumer()) {
			chain = chain.doOnNext(consumer);
		}
		return chain;
	}

	private List<Consumer<? super SearchResource>> getAllKeywordConsumer() {
		return Arrays.asList(timeKeyword, idKeyword);
	}
}
