package com.ydg.project.be.lottofinder.repository;

import com.ydg.project.be.lottofinder.entity.LottoResultEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LottoResultRepository extends ReactiveMongoRepository<LottoResultEntity, String> {

    Mono<LottoResultEntity> findTopByOrderByRoundDesc();
    Mono<LottoResultEntity> findByRound(int round);

    // 해당 회차를 기준으로 7개의 데이터를 얻기위한 메소드
    Flux<LottoResultEntity> findTop7ByRoundLessThanEqualOrderByRoundDesc(int round);
    Flux<LottoResultEntity> findTop7ByOrderByRoundDesc();
}

