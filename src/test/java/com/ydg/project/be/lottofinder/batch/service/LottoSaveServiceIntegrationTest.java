package com.ydg.project.be.lottofinder.batch.service;

import com.ydg.project.be.lottofinder.batch.dto.WinStoreDto;
import com.ydg.project.be.lottofinder.batch.extractor.WinStoreExtractor;
import com.ydg.project.be.lottofinder.entity.LottoStoreEntity;
import com.ydg.project.be.lottofinder.entity.WinStoreEntity;
import com.ydg.project.be.lottofinder.repository.LottoResultRepository;
import com.ydg.project.be.lottofinder.repository.LottoStoreRepository;
import com.ydg.project.be.lottofinder.repository.WinStoreRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class LottoSaveServiceIntegrationTest {

    @Autowired
    LottoSaveService lottoSaveService;

    @MockBean
    WinStoreExtractor winStoreExtractor;

    @MockBean
    WinStoreRepository winStoreRepository;

    @MockBean
    LottoResultRepository lottoResultRepository;

    @Autowired
    LottoStoreRepository lottoStoreRepository;

    @Captor
    ArgumentCaptor<WinStoreEntity> winStoreEntityArgumentCaptor;

    @Test
    @DisplayName("최근 당첨가게 저장하고, 해당 가게의 회차 갱신 테스트")
    public void updateRecentRoundOfStoreAfterSavingRecentWinStore() throws IOException {

        WinStoreDto winStoreDto = new WinStoreDto();
        winStoreDto.setStoreFid(1);
        winStoreDto.setName("xx가게");
        winStoreDto.setAuto(true);

        LottoStoreEntity lottoStoreEntity = LottoStoreEntity.builder()
                .storeFid(winStoreDto.getStoreFid())
                .build();

        // lottoStore 이 저장되어 있다고 하고 가정,
        lottoStoreRepository.save(lottoStoreEntity).block();


        WinStoreEntity winStore = new WinStoreEntity(winStoreDto.isAuto(), 1000, winStoreDto.getStoreFid());

        // winStore에 대한 정보 추출 mock
        when(winStoreExtractor.getWinStoreDto(1000)).thenReturn(Flux.just(winStoreDto));

        // winStoreEntity, 레포지터리에 저장했다고 가정,
        when(winStoreRepository.save(any(WinStoreEntity.class))).thenReturn(Mono.just(winStore));

        // 해당 회차의 당첨가게 저장 메소드 호출
        lottoSaveService.saveWinStore(1000).blockLast();

        verify(winStoreRepository).save(winStoreEntityArgumentCaptor.capture());

        WinStoreEntity winStoreEntity = winStoreEntityArgumentCaptor.getValue();
        assertEquals(winStoreEntity.getRound(), 1000);
        assertEquals(winStoreEntity.getStoreFid(), winStoreDto.getStoreFid());
        assertEquals(winStoreEntity.isAuto(), winStoreDto.isAuto());


        // 당첨가게 저장 하면서, 로또 가게의 당첨회차 갱신이 잘되었는지 검사
        StepVerifier
                .create(lottoStoreRepository.findByStoreFid(winStoreEntity.getStoreFid()))
                .assertNext(lottoStore -> {
                    assertEquals(lottoStore.getWinRounds().size(), 1);
                    assertEquals(lottoStore.getWinRounds().get(0), 1000);
                })
                .verifyComplete();
    }

}