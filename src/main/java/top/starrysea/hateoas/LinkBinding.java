package top.starrysea.hateoas;

import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;

@Component("linkBinding")
public class LinkBinding implements InitializingBean {

	@Autowired
	private RequestMappingHandlerMapping handlerMapping;
	private static Map<RequestMappingInfo, HandlerMethod> controllerHandlerMapping;

	public static Link linkTo(Class<?> clazz, String method, Map<String, String> inArg, Map<String, Object> template) {
		for (Map.Entry<RequestMappingInfo, HandlerMethod> m : controllerHandlerMapping.entrySet()) {
			HandlerMethod handlerMethod = m.getValue();
			if (handlerMethod.getMethod().getDeclaringClass() == clazz
					&& handlerMethod.getMethod().getName().equals(method)) {
				RequestMappingInfo requestMappingInfo = m.getKey();
				PathPattern pattern = requestMappingInfo.getPatternsCondition().getPatterns()
						.toArray(new PathPattern[0])[0];
				RequestMethod requestMethod = requestMappingInfo.getMethodsCondition().getMethods()
						.toArray(new RequestMethod[0])[0];
				Link link;
				String url = pattern.getPatternString();
				if (inArg != null) {
					for (Map.Entry<String, String> entry : inArg.entrySet()) {
						if (url.contains(entry.getKey())) {
							url = url.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue());
						}
					}
				}
				if (template != null) {
					link = new Link(url, requestMethod, template);
				} else {
					link = new Link(url, requestMethod);
				}
				return link;
			}
		}
		throw new NullPointerException("没有找到" + clazz.getName() + "类的" + method + "方法");
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		controllerHandlerMapping = handlerMapping.getHandlerMethods();
	}

}
