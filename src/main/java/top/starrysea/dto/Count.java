package top.starrysea.dto;

import org.springframework.data.annotation.Id;

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
}
