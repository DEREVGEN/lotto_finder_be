package com.ydg.project.be.lottofinder.repository;

import com.ydg.project.be.lottofinder.entity.LottoResultEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LottoResultRepository extends ReactiveMongoRepository<LottoResultEntity, String> {

    Mono<LottoResultEntity> findTopByOrderByRoundDesc();
    Mono<LottoResultEntity> findByRound(int round);
}
