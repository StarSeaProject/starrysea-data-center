package top.starrysea;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@SpringBootApplication
public class StarryseaDataCenterApplication {

	public static void main(String[] args) {
		SpringApplication.run(StarryseaDataCenterApplication.class, args);
	}

	@Bean
	public ReactiveRedisTemplate<String, Object> getReactiveRedisTemplate(
			ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
		RedisSerializer<String> stringSerializer = new StringRedisSerializer();
		RedisSerializer<Object> jsonSerializer = new GenericJackson2JsonRedisSerializer();
		RedisSerializationContext<String, Object> serializationContext = RedisSerializationContext
				.<String, Object>newSerializationContext().key(stringSerializer).value(jsonSerializer)
				.hashKey(jsonSerializer).hashValue(jsonSerializer).build();
		return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
	}

	@Autowired
	private MappingMongoConverter mappingMongoConverter;

	@PostConstruct
	public void initMappingMongoConverter() {
		mappingMongoConverter.setMapKeyDotReplacement("\\+");
	}
}
