package com.wallstreetrookie.backend.repository;

import com.wallstreetrookie.backend.model.Challenge;
import com.wallstreetrookie.backend.model.enums.ChallengeType;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChallengeRepository extends MongoRepository<Challenge, String> {

    List<Challenge> findByType(ChallengeType type);
}
