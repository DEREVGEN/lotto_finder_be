package com.ydg.project.be.lottofinder.repository;

import com.ydg.project.be.lottofinder.entity.LottoStoreEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LottoStoreRepository extends ReactiveMongoRepository<LottoStoreEntity, String>, LottoStoreCustomRepository {

    Mono<LottoStoreEntity> findByStoreFid(int storeFid);
}
