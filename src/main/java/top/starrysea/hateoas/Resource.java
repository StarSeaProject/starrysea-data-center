package top.starrysea.hateoas;

import java.util.ArrayList;
import java.util.List;

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

}
