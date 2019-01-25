package top.starrysea.vo;

import top.starrysea.controller.SearchController;
import top.starrysea.dto.Count;
import top.starrysea.hateoas.LinkBinding;
import top.starrysea.hateoas.Resource;

import java.util.HashMap;
import java.util.Map;

public class CountResource extends Resource {
	private String type;
	private Map<String, Long> result;

	private CountResource(Count search, String year, String month) {
		this.type = search.getType();
		this.result = search.getResult();
		Map<String, Object> args = new HashMap<>();
		String keyword = year + "-" + month + "-";
		Map<String, Long> newResult = new HashMap<>();
		this.result.forEach((key, value) -> {
			if (key.contains(keyword)) {
				newResult.put(key, value);
			}
		});
		this.result = newResult;
		args.put("year", year);
		args.put("month", month);
		this.addLink(LinkBinding.linkTo(SearchController.class, "searchCountByMonth", null, args));
	}

	private CountResource(Count search, String year) {
		this.type = search.getType();
		this.result = search.getResult();
		Map<String, Object> args = new HashMap<>();
		String keyword = year + "-";
		Map<String, Long> newResult = new HashMap<>();
		this.result.forEach((key, value) -> {
			if (key.contains(keyword)) {
				newResult.put(key, value);
			}
		});
		this.result = newResult;
		args.put("year", year);
		this.addLink(LinkBinding.linkTo(SearchController.class, "searchCountByYear", null, args));
	}

	private CountResource(Count search) {
		this.type = search.getType();
		this.result = search.getResult();
		Map<String, Object> args = new HashMap<>();
		this.addLink(LinkBinding.linkTo(SearchController.class, "searchCount", null, args));
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

	public Map<String, Long> getResult() {
		return result;
	}
}
