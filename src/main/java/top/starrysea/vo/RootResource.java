package top.starrysea.vo;

import top.starrysea.controller.SearchController;
import top.starrysea.hateoas.Resource;

public class RootResource extends Resource {

	public RootResource() {
		this.addLink(linkTo(SearchController.class, "searchCount"));
	}
}
