package top.starrysea.dto;

import java.util.Set;

import org.springframework.data.annotation.Id;

public class Keyword {

	@Id
	private String id;
	private Set<String> keywords;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Set<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(Set<String> keywords) {
		this.keywords = keywords;
	}

}
