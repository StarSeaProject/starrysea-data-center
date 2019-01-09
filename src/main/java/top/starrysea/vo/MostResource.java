package top.starrysea.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.starrysea.controller.SearchController;
import top.starrysea.dto.Most;
import top.starrysea.hateoas.LinkBinding;
import top.starrysea.hateoas.Resource;

public class MostResource extends Resource {

	private String keyword;
	private List<Map<String, Object>> rating;

	private MostResource(Most search) {
		this.keyword = search.getKeyword();
		this.rating = search.getRating();
		Map<String, Object> args = new HashMap<>();
		args.put("keyword", search.getKeyword());
		this.addLink(LinkBinding.linkTo(SearchController.class, "searchMost", args));
	}

	public static MostResource of(Most search) {
		return new MostResource(search);
	}

	public String getKeyword() {
		return keyword;
	}

	public List<Map<String, Object>> getRating() {
		return rating;
	}

}
