package top.starrysea.vo;

import top.starrysea.controller.SearchController;
import top.starrysea.dto.Count;
import top.starrysea.hateoas.Resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CountResource extends Resource {
	private String type;
	private Map<String, Long> result;

	private CountResource(Count search, String year, String month) {
		this.type = search.getType();
		this.result = search.getResult();
	}

	private CountResource(Count search, String year) {
		this.type = search.getType();
		this.result = search.getResult();
        List<Map<String, String>> inArgList = new ArrayList<>();
        this.result.forEach((key, value) -> {
            Map<String, String> inArgItem = new HashMap<>();
            inArgItem.put("year", key.substring(0, key.indexOf('-')));
            inArgItem.put("month", key.substring(key.indexOf('-') + 1));
            inArgList.add(inArgItem);
        });
		inArgList.forEach(m -> {
			Map<String, Object> templateMap = new HashMap<>();
			templateMap.put("date", m.get("year") + "-" + m.get("month"));
            //往links里面的template中加了date属性存储日期信息(其实就是result中的key),防止JS的object中纯数字key自动重新排序造成关联混乱
			this.addLink(linkTo(SearchController.class, "searchCountByMonth", m, templateMap));
		});
	}

	private CountResource(Count search) {
		this.type = search.getType();
		this.result = search.getResult();
        List<Map<String, String>> inArgList = new ArrayList<>();
        this.result.forEach((key, value) -> {
            Map<String, String> inArgItem = new HashMap<>();
            inArgItem.put("year", key);
            inArgList.add(inArgItem);
        });
		inArgList.forEach(m -> {
			Map<String, Object> templateMap = new HashMap<>();
			templateMap.put("date", m.get("year"));
            //往links里面的template中加了date属性存储日期信息(其实就是result中的key),防止JS的object中纯数字key自动重新排序造成关联混乱
			this.addLink(linkTo(SearchController.class, "searchCountByYear", m, templateMap));
		});
	}

	public static CountResource of(Count search, String year, String month) {
		return new CountResource(search, year, month);
	}

	public static CountResource of(Count search, String year) {
		return new CountResource(search, year);
	}

	public static CountResource of(Count search) {
		return new CountResource(search);
	}

	public String getType() {
		return type;
	}

	public Map<String, Long> getResult() {
		return result;
	}
}
