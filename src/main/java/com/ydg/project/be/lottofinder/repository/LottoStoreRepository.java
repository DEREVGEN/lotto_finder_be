package com.ydg.project.be.lottofinder.repository;

import com.ydg.project.be.lottofinder.entity.LottoStoreEntity;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface LottoStoreRepository extends ReactiveMongoRepository<LottoStoreEntity, String> {

    Mono<LottoStoreEntity> findByStoreFid(int storeFid);
}
