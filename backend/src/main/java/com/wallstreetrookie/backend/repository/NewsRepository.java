package com.wallstreetrookie.backend.repository;

import com.wallstreetrookie.backend.model.News;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NewsRepository extends MongoRepository<News, String> {

    List<News> findByGameSessionId(String gameSessionId);

    List<News> findByAffectedSector(String sector);
}