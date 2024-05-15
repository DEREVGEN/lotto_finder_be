package com.ydg.project.be.lottofinder.service;

import com.ydg.project.be.lottofinder.dto.LocationReqDto;
import com.ydg.project.be.lottofinder.dto.LottoStoreResDto;
import com.ydg.project.be.lottofinder.dto.WinStoreResDto;
import com.ydg.project.be.lottofinder.entity.LottoStoreEntity;
import com.ydg.project.be.lottofinder.entity.WinStoreEntity;
import com.ydg.project.be.lottofinder.repository.LottoStoreRepository;
import com.ydg.project.be.lottofinder.repository.WinStoreRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class LottoStoreServiceTest {

    @InjectMocks
    LottoStoreService lottoStoreService;

    @Mock
    LottoStoreRepository lottoStoreRepository;

    @Mock
    WinStoreRepository winStoreRepository;

    @Captor
    ArgumentCaptor<GeoJsonPoint> captor;

    @Test
    @DisplayName("사용자 위치에 대한 당첨상점 output 테스트")
    public void checkWinStoresNearUser() {
        List<LottoStoreEntity> storeEntityList = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            LottoStoreEntity lse = LottoStoreEntity.builder()
                    .storeFid(i+1)
                    .location(new GeoJsonPoint(i, i))
                    .build();
            storeEntityList.add(lse);
        }

        // 무조건 when 사용 할 때, double과 int를 구분해야함..
        when(lottoStoreRepository.findByLocationNearAndWinRoundsNotEmpty(any(GeoJsonPoint.class), eq(10000.0)))
                .thenReturn(Flux.fromIterable(storeEntityList));

        LocationReqDto locationReqDto = new LocationReqDto();
        locationReqDto.setLat(11);
        locationReqDto.setLng(12);

        Flux<LottoStoreResDto> lottoStoreResDtoFlux = lottoStoreService.getLottoWinStoreNearUser(locationReqDto);

        // 변환된 좌표 검사
        verify(lottoStoreRepository).findByLocationNearAndWinRoundsNotEmpty(captor.capture(), eq(10000.0));

        GeoJsonPoint geoJsonPoint = captor.getValue();

        Assertions.assertEquals(geoJsonPoint.getX(), 12);
        Assertions.assertEquals(geoJsonPoint.getY(), 11);

        // return 값 검사
        StepVerifier.create(lottoStoreResDtoFlux)
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    @DisplayName("당첨된 로또 가게 output 테스트")
    public void checkWinStoreWithRound() {

        List<WinStoreEntity> winStoreEntityList = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            WinStoreEntity winStoreEntity = new WinStoreEntity(true, 1002, i);
            winStoreEntityList.add(winStoreEntity);
        }

        LottoStoreEntity lottoStoreEntity = LottoStoreEntity.builder()
                .storeFid(2)
                .location(new GeoJsonPoint(10,20))
                .build();

        when(winStoreRepository.findByRound(1002))
                .thenReturn(Flux.fromIterable(winStoreEntityList));

        when(lottoStoreRepository.findByStoreFid(eq(1)))
                .thenReturn(Mono.just(lottoStoreEntity));

        when(lottoStoreRepository.findByStoreFid(not(eq(1))))
                .thenReturn(Mono.empty());

        Flux<WinStoreResDto> winStoreResDtoFlux =  lottoStoreService.getWinLottoStore(1002);


        StepVerifier.create(winStoreResDtoFlux)
                .expectNextCount(1)
                .verifyComplete();
    }

}