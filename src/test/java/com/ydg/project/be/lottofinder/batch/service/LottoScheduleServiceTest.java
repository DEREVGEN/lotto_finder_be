package com.ydg.project.be.lottofinder.batch.service;

import com.ydg.project.be.lottofinder.entity.LottoResultEntity;
import com.ydg.project.be.lottofinder.entity.WinStoreEntity;
import com.ydg.project.be.lottofinder.provider.RecentRoundProvider;
import com.ydg.project.be.lottofinder.repository.WinStoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@SpringBootTest
class LottoScheduleServiceTest {

    @Autowired
    RecentRoundProvider recentRoundProvider;

    @Autowired
    LottoScheduleService lottoScheduleService;

    @MockBean
    LottoSaveService lottoSaveService;

    @MockBean
    WinStoreRepository winStoreRepository;

    @Captor
    ArgumentCaptor<Integer> captor;

    @Test
    @DisplayName("lotto result parsing 후, 저장하고 나서, 최신회차가 업데이트 되는지 테스트")
    public void checkLatestLottoRoundAfterLottoResultParsing() throws IOException, InterruptedException {

        LottoResultEntity lottoResultEntity = new LottoResultEntity(1000, "1,2,3,4,5,6,7", 123456L, LocalDate.now());

        recentRoundProvider.updateRound(1000);
        when(lottoSaveService.saveLottoResult(eq(1001)))
                .thenReturn(Mono.just(lottoResultEntity));
        when(lottoSaveService.saveLottoResult(eq(1002)))
                .thenReturn(Mono.just(lottoResultEntity));
        when(lottoSaveService.saveLottoResult(eq(1003)))
                .thenReturn(Mono.just(lottoResultEntity));
        // 최신회차 초과시 에러
        when(lottoSaveService.saveLottoResult(eq(1004)))
                .thenReturn(Mono.error(new RuntimeException("can not parsing result of round : " + 1004)));

        lottoScheduleService.saveRecentRoundLottoResultFromLottoAPI();

        assertEquals(recentRoundProvider.getLatestLottoRound(), 1003);
    }

    @Test
    @DisplayName("당첨가게 저장시, 데이터베이스로부터 최신의 회차를 얻고, 로또 결과의 최신회차까지 저장 테스트")
    public void checkSaveWinStoreFromDBRoundToRecentRound() throws IOException {
        // 로또 결과의 최신회차
        recentRoundProvider.updateRound(1000);

        // DB에 저장된 가장 나중의 회차 데이터
        WinStoreEntity winStoreEntity = new WinStoreEntity(true, 995, 1234);

        for (int round = 996; round <= 1000; round++) {
            when(lottoSaveService.saveWinStore(round)).thenReturn(Flux.just(new WinStoreEntity(true, round, 1234)));
        }

        when(winStoreRepository.findTopByOrderByRoundDesc()).thenReturn(Mono.just(winStoreEntity));

        lottoScheduleService.saveRecentRoundLottoWinStoresFromLottoWeb();
    }

}