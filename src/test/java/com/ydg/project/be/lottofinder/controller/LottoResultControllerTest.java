package com.ydg.project.be.lottofinder.controller;

import com.ydg.project.be.lottofinder.entity.LottoResultEntity;
import com.ydg.project.be.lottofinder.repository.LottoResultRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;

@SpringBootTest
@AutoConfigureWebTestClient
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LottoResultControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    LottoResultRepository lottoResultRepository;

    @BeforeAll
    @DisplayName("DB에 데이터 세팅")
    public void setData() {
        for (int round = 1100; round >= 1094; round--) {
            LottoResultEntity lottoResult = new LottoResultEntity(round, "1,2,3,4,5,6,7", 1000000L, LocalDate.now());

            lottoResultRepository.save(lottoResult).block();
        }
    }


    @Test
    @DisplayName("해당 round에 대한 로또 결과 response 테스트")
    public void checkLottoReulstResponseWithRound() {

        webTestClient.get()
                .uri("/lotto/result/{round}", 1100)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.round", 1100);
    }

    @Test
    @DisplayName("해당 round에 대한 다수의 로또 결과 response 테스트")
    public void checkLottoResultsResponseWithRound() {
        webTestClient.get()
                .uri("/lotto/result/{round}/multiple", 1100)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.round").isEqualTo(1100)
                .jsonPath("$.lastLottoResults[0].round").isEqualTo(1099)
                .jsonPath("$.lastLottoResults[1].round").isEqualTo(1098)
                .jsonPath("$.lastLottoResults[2].round").isEqualTo(1097)
                .jsonPath("$.lastLottoResults[3].round").isEqualTo(1096)
                .jsonPath("$.lastLottoResults[4].round").isEqualTo(1095)
                .jsonPath("$.lastLottoResults[5].round").isEqualTo(1094);
    }

    @Test
    @DisplayName("해당 round에 대한 다수의 로또 결과 response 테스트 - v2")
    public void checkLottoResultsResponseWithRoundV2() {

        webTestClient.get()
                .uri("/lotto/result/v2/{round}/multiple", 1100)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].round").isEqualTo(1100)
                .jsonPath("$[1].round").isEqualTo(1099)
                .jsonPath("$[2].round").isEqualTo(1098)
                .jsonPath("$[3].round").isEqualTo(1097)
                .jsonPath("$[4].round").isEqualTo(1096)
                .jsonPath("$[5].round").isEqualTo(1095)
                .jsonPath("$[6].round").isEqualTo(1094);
    }

}