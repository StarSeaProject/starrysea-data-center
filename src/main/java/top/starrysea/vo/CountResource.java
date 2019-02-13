package top.starrysea.vo;

import top.starrysea.controller.SearchController;
import top.starrysea.dto.Count;
import top.starrysea.hateoas.RelType;
import top.starrysea.hateoas.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountResource extends Resource {
	private String type;
	private Map<String, Long> result;
	private List<Map<String, Object>> resultList;

	private CountResource(Count search, String year, String month) {
		this.type = search.getType();
		this.result = search.getResult();
		this.resultList = Count.mapToList(this.result);
		Map<String, String> inArg = new HashMap<>();
		inArg.put("year", year);
		this.addLink(linkTo(SearchController.class, "searchCountByYear", inArg, null, RelType.PREV));
	}

	private CountResource(Count search, String year) {
		this.type = search.getType();
		this.result = search.getResult();
		List<Map<String, String>> inArgList = new ArrayList<>();
		this.result.forEach((key, value) -> {
			Map<String, String> inArgItem = new HashMap<>();
			inArgItem.put("year", key.substring(0, key.indexOf('-')));
			inArgItem.put("month", key.substring(key.indexOf('-') + 1));
			inArgList.add(inArgItem);
		});
		this.resultList = Count.mapToList(this.result);
		inArgList.forEach(m -> this.addLink(linkTo(SearchController.class, "searchCountByMonth", m, null)));
		this.addLink(linkTo(SearchController.class, "searchCount", null, null, RelType.PREV));
	}

	private CountResource(Count search) {
		this.type = search.getType();
		this.result = search.getResult();
		List<Map<String, String>> inArgList = new ArrayList<>();
		this.result.forEach((key, value) -> {
			Map<String, String> inArgItem = new HashMap<>();
			inArgItem.put("year", key);
			inArgList.add(inArgItem);
		});
		this.resultList = Count.mapToList(this.result);
		inArgList.forEach(m -> this.addLink(linkTo(SearchController.class, "searchCountByYear", m, null)));
	}

	public static CountResource of(Count search, String year, String month) {
		return new CountResource(search, year, month);
	}

	public static CountResource of(Count search, String year) {
		return new CountResource(search, year);
	}

	public static CountResource of(Count search) {
		return new CountResource(search);
	}

	public String getType() {
		return type;
	}

	public List<Map<String, Object>> getResultList() {
		return resultList;
	}
}
