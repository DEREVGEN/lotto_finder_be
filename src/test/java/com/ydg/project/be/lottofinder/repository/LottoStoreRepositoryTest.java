package com.ydg.project.be.lottofinder.repository;

import com.ydg.project.be.lottofinder.entity.LottoStoreEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

@DataMongoTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LottoStoreRepositoryTest {

    @Autowired
    LottoStoreRepository storeRepository;

    @Autowired
    ReactiveMongoTemplate mongoTemplate;

    @BeforeAll
    public void setStoreData() {
        // geo 인덱스 설정
        mongoTemplate.indexOps(LottoStoreEntity.class).ensureIndex(new GeospatialIndex("location").typed(GeoSpatialIndexType.GEO_2DSPHERE)).block();

        double[][] location = {
                {126.97703, 37.57851},
                {126.979825, 37.57044},
                {126.979454, 37.56809},
                {126.98937, 37.57131},
                {126.99187, 37.57070}
        };

        for (int i = 0; i < 5; i++) {
            LottoStoreEntity lse = LottoStoreEntity.builder()
                    .storeFid(i+1)
                    .location(new GeoJsonPoint(location[i][0], location[i][1]))
                    .build();
            lse.addRound(1);
            storeRepository.save(lse).block();
        }
    }

    @Test
    @DisplayName("사용자 인근 당첨된 상점 조회 테스트")
    public void checkStoresNearUserTest() {

        Flux<LottoStoreEntity> lottoStoreEntityFlux = storeRepository
                .findByLocationNearAndWinRoundsNotEmpty(new GeoJsonPoint(126.97703, 37.57851), 1000000000);

        StepVerifier.create(lottoStoreEntityFlux)
                .expectNextCount(5)
                .expectComplete()
                .verify(Duration.ofSeconds(3));
    }

    @Test
    @DisplayName("상점 조회 테스트")
    public void checkStoreTest() {
        Mono<LottoStoreEntity> lottoStoreEntityMono = storeRepository.findByStoreFid(1);

        StepVerifier.create(lottoStoreEntityMono)
                .expectNextCount(1)
                .expectComplete()
                .verify(Duration.ofSeconds(3));
    }

    @Test
    @DisplayName("당첨 회차 업데이트 테스트")
    public void checkUpdateWinRoundsTest() {
        Mono<LottoStoreEntity> lottoStoreEntityMono = storeRepository.updateStoreWinRounds(1, 1110)
                            .then(storeRepository.updateStoreWinRounds(1, 1120))
                            .then(storeRepository.updateStoreWinRounds(1, 1110))
                            .then(storeRepository.findByStoreFid(1));

        StepVerifier.create(lottoStoreEntityMono)
                .assertNext(lottoStoreEntity -> Assertions.assertEquals(lottoStoreEntity.getWinRounds(), List.of(1, 1110, 1120)))
                .verifyComplete();
    }

}