package top.starrysea.common;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;
import top.starrysea.redis.StarryseaRedisTemplate;

import java.time.Duration;
import java.util.function.Consumer;

public class RedisOperations {
    public static <T> Mono<T> getMono(ReactiveMongoRepository<T, String> repository, StarryseaRedisTemplate<T> template, String key, Consumer<T> consumer) {
        Mono<T> mono = template.get(key);
        return mono.switchIfEmpty(repository.findById(key).doOnNext(t -> template.set(key, t, Duration.ofHours(1)).subscribe())).doOnNext(consumer);
    }
}
