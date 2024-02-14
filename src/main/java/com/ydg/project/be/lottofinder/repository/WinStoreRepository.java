package com.ydg.project.be.lottofinder.repository;

import com.ydg.project.be.lottofinder.entity.WinStoreEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface WinStoreRepository extends ReactiveMongoRepository<WinStoreEntity, String> {

    Flux<WinStoreEntity> findByRound(int round);
    Mono<WinStoreEntity> findTopByOrderByRoundDesc();
}
