package top.starrysea.hateoas;

import java.util.Map;

import org.springframework.web.bind.annotation.RequestMethod;

public class Link {

	private String href;
	private RequestMethod method;
	private Map<String, Object> template;

	Link(String href) {
		this.href = href;
		this.method = RequestMethod.GET;
	}

	Link(String href, RequestMethod method) {
		this.href = href;
		this.method = method;
	}

	Link(String href, RequestMethod method, Map<String, Object> template) {
		this.href = href;
		this.method = method;
		this.template = template;
	}

	public String getHref() {
		return href;
	}

	public RequestMethod getMethod() {
		return method;
	}

	public Map<String, Object> getTemplate() {
		return template;
	}

}
