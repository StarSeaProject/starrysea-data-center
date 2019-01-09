package top.starrysea.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import top.starrysea.dto.Most;

@Repository
public interface MostRepository extends ReactiveMongoRepository<Most, String> {

}
