package top.starrysea.keyword;

import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

public class KeywordFilter {

	private static final String UTF8 = "UTF-8";
	private Map<String, BloomFilter<String>> filters = new HashMap<>();

	public void addFilter(String type, Set<String> keywords) {
		BloomFilter<String> filter = BloomFilter.create(Funnels.stringFunnel(Charset.forName(UTF8)), 100000);
		keywords.stream().forEach(filter::put);
		filters.put(type, filter);
	}

	public Set<String> hasKeyword(String keyword) {
		List<String> subKeywords = Arrays.asList(keyword.split(" "));
		return filters.entrySet().stream()
				.filter(filter -> subKeywords.stream().distinct().filter(subKeyword -> !subKeyword.equals(""))
						.anyMatch(subKeyword -> filter.getValue().mightContain(subKeyword)))
				.map(Map.Entry::getKey).collect(Collectors.toSet());
	}
}
