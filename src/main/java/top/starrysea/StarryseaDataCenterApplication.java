package top.starrysea;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@SpringBootApplication
public class StarryseaDataCenterApplication {

	public static void main(String[] args) {
		SpringApplication.run(StarryseaDataCenterApplication.class, args);
	}

	@Autowired
	private MappingMongoConverter mappingMongoConverter;

	@PostConstruct
	public void initMappingMongoConverter() {
		mappingMongoConverter.setMapKeyDotReplacement("\\+");
	}
}
