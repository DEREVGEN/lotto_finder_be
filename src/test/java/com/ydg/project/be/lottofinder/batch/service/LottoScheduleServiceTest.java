package com.ydg.project.be.lottofinder.batch.service;

import com.ydg.project.be.lottofinder.batch.dto.LottoResultDto;
import com.ydg.project.be.lottofinder.batch.dto.WinStoreDto;
import com.ydg.project.be.lottofinder.batch.exception.LottoResultNotUpdatedException;
import com.ydg.project.be.lottofinder.batch.exception.WinStoreNotUpdatedException;
import com.ydg.project.be.lottofinder.batch.extractor.LottoResultExtractor;
import com.ydg.project.be.lottofinder.batch.extractor.WinStoreExtractor;
import com.ydg.project.be.lottofinder.entity.LottoResultEntity;
import com.ydg.project.be.lottofinder.entity.WinStoreEntity;
import com.ydg.project.be.lottofinder.provider.RecentRoundProvider;
import com.ydg.project.be.lottofinder.repository.LottoResultRepository;
import com.ydg.project.be.lottofinder.repository.LottoStoreRepository;
import com.ydg.project.be.lottofinder.repository.WinStoreRepository;
import com.ydg.project.be.lottofinder.util.EntityDtoUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class LottoScheduleServiceTest {

    @InjectMocks
    LottoScheduleService lottoScheduleService;
    @Mock
    WinStoreRepository winStoreRepository;
    @Mock
    LottoResultRepository lottoResultRepository;
    @Mock
    LottoStoreRepository lottoStoreRepository;

    @Mock
    LottoResultExtractor lottoResultExtractor;
    @Mock
    WinStoreExtractor winStoreExtractor;

    RecentRoundProvider roundProvider;


    // 모든 테스트 케이스를 1110회차를 최신 회차로 가정.
    @BeforeEach
    public void init() {
        roundProvider = new RecentRoundProvider();
        roundProvider.updateRound(1110);

        lottoScheduleService = new LottoScheduleService(roundProvider, winStoreRepository, lottoResultRepository, lottoStoreRepository, lottoResultExtractor, winStoreExtractor);
    }


    @Test
    @DisplayName("로또 결과 API 로부터 데이터 획득 및 처리 - 성공")
    public void checkGetLottoResultFromApiTest() {
        LottoResultDto lottoResultDtoMock = mock(LottoResultDto.class);
        when(lottoResultExtractor.getLottoResultDeferred(eq(1111))).thenReturn(Mono.just(lottoResultDtoMock));

        LottoResultEntity lottoResultEntityMock = mock(LottoResultEntity.class);
        when(lottoResultRepository.save(any(LottoResultEntity.class))).thenReturn(Mono.just(lottoResultEntityMock));

        StepVerifier
                .create(lottoScheduleService.saveRecentRoundLottoResultFromLottoAPI())
                .expectNext(lottoResultEntityMock)
                .verifyComplete();

        assertEquals(roundProvider.getLatestLottoRound(), 1111);
    }

    @Test
    @DisplayName("로또 결과 API 로부터 데이터 획득 및 처리 - 에러, 1분 동안 60회의 시도 모두 실패시")
    public void checkGetLottoResultFromApiAllFailedTest() {
        when(lottoResultExtractor.getLottoResultDeferred(eq(1111)))
                .thenReturn(Mono.error(LottoResultNotUpdatedException::new));

        StepVerifier.withVirtualTime(() -> lottoScheduleService.saveRecentRoundLottoResultFromLottoAPI())
                .thenAwait(Duration.ofMinutes(60))
                .expectError()
                .verify();
    }

    @Test
    @DisplayName("로또 결과 API 로부터 데이터 획득 및 처리 - 에러, 4회의 실패 시도 후 성공")
    public void checkGetLottoResultFromApiSomeFailedTest() {

        LottoResultDto lottoResultDtoMock = mock(LottoResultDto.class);

        Mono<LottoResultDto> deferMock = Mono.defer(() -> {
            return lottoResultExtractor.getLottoResult(1111);
        });

        when(lottoResultExtractor.getLottoResultDeferred(1111)).thenReturn(deferMock);

        when(lottoResultExtractor.getLottoResult(1111))
                .thenReturn(Mono.error(LottoResultNotUpdatedException::new))
                .thenReturn(Mono.error(LottoResultNotUpdatedException::new))
                .thenReturn(Mono.error(LottoResultNotUpdatedException::new))
                .thenReturn(Mono.just(lottoResultDtoMock));

        LottoResultEntity lottoResultEntityMock = mock(LottoResultEntity.class);
        when(lottoResultRepository.save(any(LottoResultEntity.class))).thenReturn(Mono.just(lottoResultEntityMock));

        StepVerifier
                .withVirtualTime(() -> lottoScheduleService.saveRecentRoundLottoResultFromLottoAPI())
                .thenAwait(Duration.ofMinutes(4))
                .expectNext(lottoResultEntityMock)
                .verifyComplete();
    }

    @Test
    @DisplayName("당첨 가게 결과 API 로부터 데이터 획득 및 처리 - 에러, 4회의 실패 시도 후 성공")
    public void checkUpdateWinRoundsOfLottoStoreDuringSavingWinStoreSomeFailedTest() {

        WinStoreDto winStoreDtoMock = new WinStoreDto();
        winStoreDtoMock.setRound(1111);
        winStoreDtoMock.setStoreFid(1234);

        WinStoreEntity winStoreEntityMock = EntityDtoUtil.toEntity(winStoreDtoMock);

        Flux<WinStoreDto> parsingWinStoreDeferMock = Flux.defer(() -> {
            return winStoreExtractor.getWinStoreDto(1111);
        });

        when(winStoreExtractor.getWinStoreDtoDeferred(1111)).thenReturn(parsingWinStoreDeferMock);

        when(winStoreExtractor.getWinStoreDto(1111))
                .thenReturn(Flux.error(WinStoreNotUpdatedException::new))
                .thenReturn(Flux.error(WinStoreNotUpdatedException::new))
                .thenReturn(Flux.error(WinStoreNotUpdatedException::new))
                .thenReturn(Flux.just(winStoreDtoMock));

        when(winStoreRepository.save(any(WinStoreEntity.class))).thenReturn(Mono.just(winStoreEntityMock));
        when(lottoStoreRepository.updateStoreWinRounds(eq(1234), eq(1111))).thenReturn(Mono.empty());

        roundProvider.updateRound(1111);
        StepVerifier.withVirtualTime(() -> lottoScheduleService.saveWinStores(1110))
                .thenAwait(Duration.ofMinutes(4))
                .expectNext(winStoreEntityMock)
                .verifyComplete();
    }


    @Test
    @DisplayName("당첨 가게 결과 API 로부터 데이터 획득 및 처리 - 에러, 모두 실패")
    public void checkUpdateWinRoundsOfLottoStoreDuringSavingWinStoreAllFailedTest() {

        WinStoreDto winStoreDtoMock = new WinStoreDto();
        winStoreDtoMock.setRound(1111);
        winStoreDtoMock.setStoreFid(1234);

        Flux<WinStoreDto> parsingWinStoreDeferMock = Flux.defer(() -> {
            return winStoreExtractor.getWinStoreDto(1111);
        });

        when(winStoreExtractor.getWinStoreDtoDeferred(1111)).thenReturn(parsingWinStoreDeferMock);

        when(winStoreExtractor.getWinStoreDto(1111))
                .thenReturn(Flux.error(WinStoreNotUpdatedException::new))
                .thenReturn(Flux.error(WinStoreNotUpdatedException::new))
                .thenReturn(Flux.error(WinStoreNotUpdatedException::new));

        roundProvider.updateRound(1111);
        StepVerifier.withVirtualTime(() -> lottoScheduleService.saveWinStores(1110))
                .thenAwait(Duration.ofMinutes(60))
                .expectError()
                .verify();
    }


}