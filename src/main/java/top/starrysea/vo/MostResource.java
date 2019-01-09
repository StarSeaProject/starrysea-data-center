package top.starrysea.vo;

import java.util.HashMap;
import java.util.Map;

import top.starrysea.controller.SearchController;
import top.starrysea.dto.Most;
import top.starrysea.hateoas.LinkBinding;
import top.starrysea.hateoas.Resource;

public class MostResource extends Resource {

	private MostResource() {
		Map<String, Object> args = new HashMap<>();
		args.put("keyword", "123");
		this.addLink(LinkBinding.linkTo(SearchController.class, "searchMost", args));
	}

	public static MostResource of(Most search) {
		return new MostResource();
	}
}
