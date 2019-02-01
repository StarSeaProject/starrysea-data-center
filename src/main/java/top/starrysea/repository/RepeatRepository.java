package top.starrysea.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import top.starrysea.dto.Repeat;

@Repository
public interface RepeatRepository extends ReactiveMongoRepository<Repeat, String> {

}
