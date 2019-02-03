package top.starrysea.redis;

import java.lang.reflect.ParameterizedType;
import java.time.Duration;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import reactor.core.publisher.Mono;

public class StarryseaRedisTemplate<T> {

	protected ReactiveRedisTemplate<String, T> reactiveRedisTemplate;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public StarryseaRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
		Class<? extends StarryseaRedisTemplate> subClass = this.getClass();
		ParameterizedType type = (ParameterizedType) subClass.getGenericSuperclass();
		RedisSerializer<String> stringSerializer = new StringRedisSerializer();
		RedisSerializer<T> jsonSerializer = new Jackson2JsonRedisSerializer<>(
				(Class) (type.getActualTypeArguments())[0]);
		RedisSerializationContext<String, T> serializationContext = RedisSerializationContext
				.<String, T>newSerializationContext().key(stringSerializer).value(jsonSerializer)
				.hashKey(jsonSerializer).hashValue(jsonSerializer).build();
		reactiveRedisTemplate = new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
	}

	public Mono<T> get(String key) {
		return reactiveRedisTemplate.opsForValue().get(key);
	}

	public Mono<Boolean> set(String key, T value, Duration timeout) {
		return reactiveRedisTemplate.opsForValue().set(key, value, timeout);
	}

	public Mono<Boolean> del(String key) {
		return reactiveRedisTemplate.opsForValue().delete(key);
	}
	
	public Mono<Boolean> hasKey(String key){
		return reactiveRedisTemplate.hasKey(key);
	}
}
