package top.starrysea.hateoas;

import java.util.Map;

public class Link {

	private String href;
	private HttpMethod method;
	private Map<String, Object> template;

	public Link(String href) {
		this.href = href;
		this.method = HttpMethod.GET;
	}

	public Link(String href, HttpMethod method) {
		this.href = href;
		this.method = method;
	}

	public Link(String href, HttpMethod method, Map<String, Object> template) {
		this.href = href;
		this.method = method;
		this.template = template;
	}

	public String getHref() {
		return href;
	}

	public HttpMethod getMethod() {
		return method;
	}

	public Map<String, Object> getTemplate() {
		return template;
	}

}
