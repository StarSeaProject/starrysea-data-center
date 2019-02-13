package top.starrysea.vo;

import java.util.HashMap;
import java.util.Map;

import top.starrysea.controller.SearchController;
import top.starrysea.hateoas.Resource;

public class RootResource extends Resource {

	public RootResource() {
		Map<String, Object> template = new HashMap<>();
		template.put("keyword", "");
		this.addLink(linkTo(SearchController.class, "search", null, template));
	}
}
