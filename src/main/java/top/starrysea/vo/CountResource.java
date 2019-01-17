package top.starrysea.vo;

import top.starrysea.controller.SearchController;
import top.starrysea.dto.Count;
import top.starrysea.hateoas.LinkBinding;
import top.starrysea.hateoas.Resource;

import java.util.HashMap;
import java.util.Map;

public class CountResource extends Resource {
    private String type;
    private Map<String, Long> result;

    private CountResource(Count search) {
        this.type = search.getType();
        this.result = search.getResult();
        Map<String, Object> args = new HashMap<>();
        args.put("type", search.getType());
        this.addLink(LinkBinding.linkTo(SearchController.class, "searchCount", args));
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
