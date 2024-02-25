package com.ydg.project.be.lottofinder.repository;

import com.ydg.project.be.lottofinder.entity.LottoStoreEntity;
import org.springframework.data.geo.Distance;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface LottoStoreCustomRepository {

    Flux<LottoStoreEntity> findByLocationNearAndWinRoundsNotEmpty(GeoJsonPoint location, double maxDistance);
    Mono<Void> updateStoreWinRounds(int storeFId, int round);
}
