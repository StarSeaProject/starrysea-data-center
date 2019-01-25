package top.starrysea.hateoas;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Resource {

	private List<Link> links;

	public Resource() {
		this.links = new ArrayList<>();
	}

	public void addLink(Link link) {
		links.add(link);
	}

	@JsonProperty("links")
	public List<Link> getLinks() {
		return links;
	}

	protected Link linkTo(Class<?> clazz, String method) {
		return linkTo(clazz, method, null);
	}

	protected Link linkTo(Class<?> clazz, String method, Map<String, String> inArg) {
		return linkTo(clazz, method, inArg, null);
	}

	protected Link linkTo(Class<?> clazz, String method, Map<String, String> inArg, Map<String, Object> template) {
		return LinkBinding.linkTo(clazz, method, inArg, template);
	}

}
