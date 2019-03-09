package top.starrysea.vo;

import top.starrysea.controller.SearchController;
import top.starrysea.hateoas.Resource;

public class SearchResource extends Resource{
	
	public void addDateSearchLink() {
		this.addLink(linkTo(SearchController.class, "searchCount"));
	}
	public void addIdSearchLink(){
		this.addLink(linkTo(SearchController.class, "searchIdCount"));
	}
}
