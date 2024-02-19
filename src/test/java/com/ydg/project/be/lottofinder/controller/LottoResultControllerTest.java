package com.ydg.project.be.lottofinder.controller;

import com.ydg.project.be.lottofinder.dto.LottoResultResDto;
import com.ydg.project.be.lottofinder.service.LottoResultService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureWebTestClient
class LottoResultControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    LottoResultService resultService;

    @Test
    @DisplayName("해당 round에 대한 로또 결과 response 테스트")
    public void checkLottoReulstResponseWithRound() throws Exception {
        LottoResultResDto resultResDto = setLottoRulestDto();

        when(resultService.getLottoResult(1100)).thenReturn(Mono.just(resultResDto));

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
        LottoResultResDto resultResDto = setLottoRulestDto();

        when(resultService.getLottoResults(1100)).thenReturn(Mono.just(resultResDto));

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

    public LottoResultResDto setLottoRulestDto() {
        LottoResultResDto resultResDto = new LottoResultResDto();
        resultResDto.setRound(1100);
        resultResDto.setLottoNumbers("1,2,3,4,5,6,7");
        resultResDto.setWinPrize(1000000);
        resultResDto.setDate(LocalDate.now());

        for (int round = 1099; round >= 1094; round--) {
            LottoResultResDto resultResDtoElement = new LottoResultResDto();
            resultResDtoElement.setRound(round);
            resultResDtoElement.setLottoNumbers("1,2,3,4,5,6,7");
            resultResDtoElement.setWinPrize(1000000);
            resultResDtoElement.setDate(LocalDate.now());

            resultResDto.addLottoResult(resultResDtoElement);
        }
        return resultResDto;
    }

}