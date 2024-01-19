package com.ydg.project.be.lottofinder.repository;

import com.ydg.project.be.lottofinder.entity.LottoStoreEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class LottoStoreCustomRepositoryImpl implements LottoStoreCustomRepository{

    @Autowired
    ReactiveMongoTemplate mongoTemplate;

    @Override
    public Flux<LottoStoreEntity> findByLocationNearAndWinRoundsNotEmpty(GeoJsonPoint location, double maxDistance) {
        Query query = new Query(Criteria
                .where("location").nearSphere(location).maxDistance(maxDistance)
                .and("winRounds").not().size(0)
        ).limit(5);

        return mongoTemplate
                .find(query, LottoStoreEntity.class);
    }
}

