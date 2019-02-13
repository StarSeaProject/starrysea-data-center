package top.starrysea.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

import top.starrysea.dto.Keyword;

@Repository
public interface KeywordRepository extends ReactiveMongoRepository<Keyword, String> {

}
