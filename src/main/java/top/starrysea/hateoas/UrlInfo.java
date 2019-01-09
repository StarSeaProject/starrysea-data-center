package top.starrysea.hateoas;

public class UrlInfo {

	private StarryseaControllerHandle handle;
	private HttpMethod method;
	private String url;

	private UrlInfo(StarryseaControllerHandle handle, HttpMethod method, String url) {
		this.handle = handle;
		this.method = method;
		this.url = url;
	}

	public static UrlInfo of(Class<?> clazz, String methodStr, HttpMethod method, String url) {
		return new UrlInfo(StarryseaControllerHandle.of(clazz, methodStr), method, url);
	}

	public HttpMethod getMethod() {
		return method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public StarryseaControllerHandle getHandle() {
		return handle;
	}

}
