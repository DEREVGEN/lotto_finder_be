package com.ydg.project.be.lottofinder.batch.extractor;

import com.ydg.project.be.lottofinder.batch.dto.WinStoreDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource("classpath:application-test.yaml")
class WinStoreExtractorTest {

    @Autowired
    WinStoreExtractor winStoreExtractor;

    @Test
    @DisplayName("당첨 가게 파싱 테스트")
    public void checkWinStoresParsing() throws IOException {
        Flux<WinStoreDto> winStoreDtoFlux = winStoreExtractor.getWinStoreDto(1000);

        StepVerifier.create(winStoreDtoFlux)
                .assertNext(winStoreDto -> {
                    assertEquals(winStoreDto.getName(), "행운식품");
                    assertEquals(winStoreDto.getStoreFId(), 11120016);
                })
                .assertNext(winStoreDto -> {
                    assertEquals(winStoreDto.getName(), "로또백화점 홍은점");
                    assertEquals(winStoreDto.getStoreFId(), 11140275);
                })
                .expectNextCount(20)
                .verifyComplete();
    }
}