package top.starrysea.dto;

import org.springframework.data.annotation.Id;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Count {

    @Id
    private String type;
    private Map<String, Long> result;

    public String getType() {
        return type;
    }

    public Map<String, Long> getResult() {
        return result;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setResult(Map<String, Long> result) {
        this.result = result;
    }

    public static List<Map<String, Object>> mapToList(Map<String, Long> map){
        List<Map<String, Object>> list = new ArrayList<>();
        map.forEach((key,value)->{
            Map<String, Object> item = new HashMap<>();
            item.put("index",key);
            item.put("result",value);
            list.add(item);
        });
        return list;
    }
}
