package com.ydg.project.be.lottofinder.batch.extractor;

import com.ydg.project.be.lottofinder.batch.dto.LottoResultDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LottoResultExtractorTest {

    @Autowired
    LottoResultExtractor lottoResultExtractor;

    @Test
    @DisplayName("로또 결과 불러오기 & 파싱 테스트")
    public void checkLottoResultParsingTest() throws IOException, InterruptedException {
        LottoResultDto lottoResultDto = lottoResultExtractor.getLottoResult(1000).block();

        assertEquals(lottoResultDto.getRound(), 1000);
        assertEquals(lottoResultDto.getWinPrize(), 1246819620);
        assertEquals(lottoResultDto.getDate(), LocalDate.of(2022, 01, 29));
        assertEquals(lottoResultDto.getN1(), 2);
        assertEquals(lottoResultDto.getN2(), 8);
        assertEquals(lottoResultDto.getN3(), 19);
        assertEquals(lottoResultDto.getN4(), 22);
        assertEquals(lottoResultDto.getN5(), 32);
        assertEquals(lottoResultDto.getN6(), 42);
        assertEquals(lottoResultDto.getBn(), 39);
    }

    @Test
    @DisplayName("로또 결과 불러오기 & 파싱 오류 테스트")
    public void checkErrorLottoResultParsingTest() throws IOException, InterruptedException {
        // lottoResultExtractor.getLottoResult(-1) 호출 시 런타임 예외가 발생
        Mono<LottoResultDto> lottoResultDtoMono = lottoResultExtractor.getLottoResult(-1);

        StepVerifier.create(lottoResultDtoMono)
                .expectError(RuntimeException.class)
                .verify();
    }

}