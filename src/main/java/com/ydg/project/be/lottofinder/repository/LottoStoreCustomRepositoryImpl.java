package com.ydg.project.be.lottofinder.repository;

import com.ydg.project.be.lottofinder.entity.LottoStoreEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LottoStoreCustomRepositoryImpl implements LottoStoreCustomRepository{

    private final ReactiveMongoTemplate mongoTemplate;

    @Override
    public Flux<LottoStoreEntity> findByLocationNearAndWinRoundsNotEmpty(GeoJsonPoint location, double maxDistance) {
        Query query = new Query(Criteria
                .where("location").nearSphere(location).maxDistance(maxDistance)
                .and("winRounds").not().size(0)
        ).limit(5);

        return mongoTemplate
                .find(query, LottoStoreEntity.class);
    }

    @Override
    public Mono<Void> updateStoreWinRounds(int storeFid, int round) {
        Criteria criteria = Criteria.where("storeFid").is(storeFid);
        Update update = new Update().addToSet("winRounds", round);
        return mongoTemplate.updateFirst(Query.query(criteria), update, LottoStoreEntity.class).then();
    }
}

