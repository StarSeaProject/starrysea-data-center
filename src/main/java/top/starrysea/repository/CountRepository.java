package top.starrysea.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import top.starrysea.dto.Count;

@Repository
public interface CountRepository extends ReactiveMongoRepository<Count, String> {

}
