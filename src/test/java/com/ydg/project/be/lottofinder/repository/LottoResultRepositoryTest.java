package com.ydg.project.be.lottofinder.repository;

import com.ydg.project.be.lottofinder.entity.LottoResultEntity;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataMongoTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LottoResultRepositoryTest {

    @Autowired
    LottoResultRepository resultRepository;

    @BeforeAll
    public void setRandomData() {
        List<LottoResultEntity> lottoResultEntityList = new ArrayList<>();

        // generate random data
        for (int round = 1000; round < 1011; round++) {
            lottoResultEntityList.add(new LottoResultEntity(
                    round, "1,2,3,4,5,6,7",
                    (long) (1000000000+(Math.random() * 100000000)),
                    LocalDate.now())
            );
        }
        resultRepository
                .saveAll(lottoResultEntityList)
                .blockLast();
    }

    @Test
    @DisplayName("최신의 로또 결과 데이터 불러오기")
    public void shouldGetRecentResultTest() {
        Mono<LottoResultEntity> lottoResultEntityMono = resultRepository
                .findTopByOrderByRoundDesc().doOnNext(System.out::println);

        StepVerifier
                .create(lottoResultEntityMono)
                .assertNext(lre -> assertThat(lre.getRound()).isEqualTo(1010))
                .expectComplete()
                .verify(Duration.ofSeconds(3));
    }

    @Test
    @DisplayName("특정 회차에 대한 로또 결과 데이터 불러오기")
    public void shouldGetResultTest() {
        Mono<LottoResultEntity> lottoResultEntityMono = resultRepository
                .findByRound(1005);

        StepVerifier
                .create(lottoResultEntityMono)
                .assertNext(lre -> assertEquals(lre.getRound(), 1005))
                .expectComplete()
                .verify(Duration.ofSeconds(3));
    }

    @Test
    @DisplayName("해당 라운드에 대한 7개의 결과 데이터 확인")
    public void shouldGetSevenResultsWithRound() {
        Flux<LottoResultEntity> lottoResultEntityFlux = resultRepository
                .findTop7ByRoundLessThanEqualOrderByRoundDesc(1008);

        StepVerifier.create(lottoResultEntityFlux)
                .expectNextCount(7)
                .expectComplete()
                .verify(Duration.ofSeconds(3));
    }

    @Test
    @DisplayName("최신 라운드에 대한 7개의 최신 결과 데이터 확인")
    public void checkRecentResultsWithRound() {
        Flux<LottoResultEntity> lottoResultEntityFlux = resultRepository
                .findTop7ByOrderByRoundDesc()
                .filter(lre -> lre.getRound() >= 1004 && lre.getRound() < 1011);

        StepVerifier.create(lottoResultEntityFlux)
                .expectNextCount(7)
                .expectComplete()
                .verify(Duration.ofSeconds(3));
    }

    @Test
    @DisplayName("잘못된 데이터 입력에 대한 데이터 확인")
    public void checkErrorInvalidResult() {
        Mono<LottoResultEntity> lottoResultEntityMono = resultRepository
                .findByRound(0);

        StepVerifier.create(lottoResultEntityMono)
                .expectNextCount(0)
                .expectComplete()
                .verify(Duration.ofSeconds(3));
    }

    @Test
    @DisplayName("잘못된 데이터 입력에 대한 데이터 확인2")
    public void checkErrorInvalidResults() {
        Flux<LottoResultEntity> lottoResultEntityFlux = resultRepository
                .findTop7ByRoundLessThanEqualOrderByRoundDesc(0);

        StepVerifier.create(lottoResultEntityFlux)
                .expectNextCount(0)
                .expectComplete()
                .verify(Duration.ofSeconds(3));
    }
}