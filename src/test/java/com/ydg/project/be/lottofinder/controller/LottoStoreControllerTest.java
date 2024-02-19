package com.ydg.project.be.lottofinder.controller;

import com.ydg.project.be.lottofinder.dto.WinStoreResDto;
import com.ydg.project.be.lottofinder.service.LottoStoreService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureWebTestClient
class LottoStoreControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    LottoStoreService lottoStoreService;

    @Test
    @DisplayName("사용자 인근 위치 컨트롤러 요청 에러 테스트")
    public void checkErrorNearStoresControllerWithReqDto() {

        webTestClient
                .post()
                .uri("/lotto/store/win/near")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"lat\": -91, \"lng\": 126.6705}")
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    @DisplayName("특정 라운드의 당첨가게 조회 테스트")
    public void checkWinStoreWithRound() {


        List<WinStoreResDto> winStoreResDtoList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            WinStoreResDto winStoreResDto = new WinStoreResDto();
            winStoreResDto.setRound(1000);
            winStoreResDto.setAuto(true);
            winStoreResDtoList.add(winStoreResDto);
        }

        when(lottoStoreService.getWinLottoStore(eq(1000)))
                .thenReturn(Flux.fromIterable(winStoreResDtoList));

        webTestClient
                .get()
                .uri("/lotto/store/win/1000")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[*].round").isArray()
                .jsonPath("$[*].round").value(hasItem(1000));
    }
}