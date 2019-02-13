package top.starrysea.redis;

import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.stereotype.Repository;

import top.starrysea.dto.Count;

@Repository
public class CountTemplate extends StarryseaRedisTemplate<Count> {

	public CountTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
		super(reactiveRedisConnectionFactory);
	}

}
