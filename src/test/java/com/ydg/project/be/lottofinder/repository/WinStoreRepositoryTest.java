package com.ydg.project.be.lottofinder.repository;

import com.ydg.project.be.lottofinder.entity.WinStoreEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@TestPropertySource("classpath:application-test.yaml")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WinStoreRepositoryTest {

    @Autowired
    WinStoreRepository winStoreRepository;

    @BeforeAll
    public void setWinStoreData() {
        for (int round = 1000; round < 1010; round++) {
            WinStoreEntity winStoreEntity = new WinStoreEntity(true, round, round);
            winStoreRepository.save(winStoreEntity).block();
        }
    }

    @Test
    @DisplayName("당첨가게 조회 테스트")
    public void checkWinStoreWithRound() {
        Flux<WinStoreEntity> winStoreEntityFlux =  winStoreRepository.findByRound(1001);

        StepVerifier.create(winStoreEntityFlux)
                .assertNext(winStoreEntity -> assertEquals(1001, winStoreEntity.getRound()))
                .expectComplete()
                .verify(Duration.ofSeconds(3));
    }

    @Test
    @DisplayName("최신 round 조회 테스트")
    public void checkRecentRound() {
        Mono<WinStoreEntity> winStoreEntityMono = winStoreRepository.findTopByOrderByRoundDesc();

        StepVerifier.create(winStoreEntityMono)
                .assertNext(winStoreEntity -> assertEquals(winStoreEntity.getRound(), 1009))
                .verifyComplete();
    }

}