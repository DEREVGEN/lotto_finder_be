package com.ydg.project.be.lottofinder.batch.extractor;

import com.ydg.project.be.lottofinder.batch.dto.WinStoreDto;
import com.ydg.project.be.lottofinder.batch.exception.LottoResultNotUpdatedException;
import com.ydg.project.be.lottofinder.batch.exception.WinStoreNotUpdatedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class WinStoreExtractorTest {

    WinStoreExtractor winStoreExtractor;

    @BeforeEach
    public void init() {
        winStoreExtractor = new WinStoreExtractor();
    }

    @Test
    @DisplayName("당첨 가게 파싱 테스트")
    public void checkWinStoresParsing() {
        Flux<WinStoreDto> winStoreDtoFlux = winStoreExtractor.getWinStoreDto(1000);

        StepVerifier.create(winStoreDtoFlux)
                .assertNext(winStoreDto -> {
                    assertEquals(winStoreDto.getName(), "행운식품");
                    assertEquals(winStoreDto.getStoreFid(), 11120016);
                })
                .assertNext(winStoreDto -> {
                    assertEquals(winStoreDto.getName(), "로또백화점 홍은점");
                    assertEquals(winStoreDto.getStoreFid(), 11140275);
                })
                .expectNextCount(20)
                .verifyComplete();
    }

    @Test
    @DisplayName("당첨 가게 파싱 오류 테스트")
    public void checkErrorWinStoresParsing() {
        Flux<WinStoreDto> winStoreDtoFlux = winStoreExtractor.getWinStoreDto(10000);

        StepVerifier.create(winStoreDtoFlux)
                .expectError(WinStoreNotUpdatedException.class)
                .verify();
    }
}