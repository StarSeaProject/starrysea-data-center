package top.starrysea;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import top.starrysea.keyword.KeywordFilter;
import top.starrysea.repository.KeywordRepository;

@Configuration
public class SearchKeywordConfig {

	@Bean
	public KeywordFilter getKeywordFilter(KeywordRepository keywordRepository) {
		KeywordFilter keywordFilter = new KeywordFilter();
		keywordRepository.findAll()
				.subscribe(keyword -> keywordFilter.addFilter(keyword.getId(), keyword.getKeywords()));
		return keywordFilter;
	}
}
