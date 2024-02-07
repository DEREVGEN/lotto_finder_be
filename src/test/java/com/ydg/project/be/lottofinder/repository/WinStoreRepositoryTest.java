package com.ydg.project.be.lottofinder.repository;

import com.ydg.project.be.lottofinder.entity.WinStoreEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@TestPropertySource("classpath:application-test.yaml")
class WinStoreRepositoryTest {

    @Autowired
    WinStoreRepository winStoreRepository;

    @Test
    @DisplayName("당첨가게 조회 테스트")
    public void checkWinStoreWithRound() {
        for (int round = 1000; round < 1010; round++) {
            WinStoreEntity winStoreEntity = new WinStoreEntity(true, round, round);
            winStoreRepository.save(winStoreEntity).block();
        }

        Flux<WinStoreEntity> winStoreEntityFlux =  winStoreRepository.findByRound(1001);

        StepVerifier.create(winStoreEntityFlux)
                .assertNext(winStoreEntity -> assertEquals(1001, winStoreEntity.getRound()))
                .expectComplete()
                .verify(Duration.ofSeconds(3));
    }
}