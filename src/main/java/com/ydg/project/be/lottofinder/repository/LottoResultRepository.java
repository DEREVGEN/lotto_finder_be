package com.ydg.project.be.lottofinder.repository;

import com.ydg.project.be.lottofinder.entity.LottoResultEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface LottoResultRepository extends ReactiveMongoRepository<LottoResultEntity, String> {
}
