package top.starrysea.dto;

import java.util.Map;

import org.springframework.data.annotation.Id;

public class Repeat {
	
	@Id
	private String id;
	private Map<String, Long> result;

	public Map<String, Long> getResult() {
		return result;
	}

	public void setResult(Map<String, Long> result) {
		this.result = result;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
