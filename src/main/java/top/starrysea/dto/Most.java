package top.starrysea.dto;

import java.util.List;
import java.util.Map;

import org.springframework.data.annotation.Id;

public class Most {

	@Id
	private String keyword;
	private List<Map<String,Object>> rating;

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public List<Map<String, Object>> getRating() {
		return rating;
	}

	public void setRating(List<Map<String, Object>> rating) {
		this.rating = rating;
	}

}
