package com.ydg.project.be.lottofinder.service;

import com.ydg.project.be.lottofinder.dto.LocationReqDto;
import com.ydg.project.be.lottofinder.dto.LottoResultResDto;
import com.ydg.project.be.lottofinder.dto.LottoStoreResDto;
import com.ydg.project.be.lottofinder.dto.WinStoreResDto;
import com.ydg.project.be.lottofinder.entity.LottoResultEntity;
import com.ydg.project.be.lottofinder.repository.LottoResultRepository;
import com.ydg.project.be.lottofinder.repository.LottoStoreRepository;
import com.ydg.project.be.lottofinder.repository.WinStoreRepository;
import com.ydg.project.be.lottofinder.util.EntityDtoUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LottoInfoService {

    private final LottoResultRepository resultRepository;
    private final LottoStoreRepository storeRepository;
    private final WinStoreRepository winStoreRepository;


    // 로또의 최신 회차를 담는 변수
    @Getter
    private int latestLottoRound = -1;

    public void updateLatestLottoRound(int round) {
        // 초기에 LottoRoundInitializer 을 통해 초기화 됨.
        latestLottoRound = round;
    }

    public Mono<LottoResultResDto> getRecentLottoResult() {
        return resultRepository
                .findTopByOrderByRoundDesc()
                .map(EntityDtoUtil::toDto);
    }

    public Mono<LottoResultResDto> getRecentLottoResults() {
        return resultRepository
                .findTop7ByOrderByRoundDesc()
                .collectList()
                .map(this::buildToDto);
    }

    public Mono<LottoResultResDto> getLottoResult(int round) {
        return resultRepository
                .findByRound(round)
                .map(EntityDtoUtil::toDto);
    }

    public Mono<LottoResultResDto> getLottoResults(int round) {
         return resultRepository
                 .findTop7ByRoundLessThanEqualOrderByRoundDesc(round)
                 .collectList()
                 .map(this::buildToDto);
    }

    private LottoResultResDto buildToDto(List<LottoResultEntity> lottoResultEntityList) {
        LottoResultResDto resultResDto = new LottoResultResDto();

        for (int i = 0; i < lottoResultEntityList.size(); i++) {
            LottoResultResDto lottoResultResDto = EntityDtoUtil.toDto(lottoResultEntityList.get(i));

            if (i == 0) {
                resultResDto.setLottoNumbers(lottoResultResDto.getLottoNumbers());
                resultResDto.setDate(lottoResultResDto.getDate());
                resultResDto.setWinPrize(lottoResultResDto.getWinPrize());
                resultResDto.setRound(lottoResultResDto.getRound());
            } else {
                resultResDto.addLottoResult(lottoResultResDto);
            }
        }

        return resultResDto;
    }

    @Transactional
    public Flux<WinStoreResDto> getRecentWinLottoStore() {
        return getWinLottoStore(latestLottoRound);
    }

    @Transactional
    public Flux<WinStoreResDto> getWinLottoStore(int round) {
        return  winStoreRepository.findByRound(round).flatMap(
                winStore ->
                        storeRepository.findByStoreFid(winStore.getStoreFid())
                                .map(lottoStore -> EntityDtoUtil.toDto(winStore, lottoStore))
                                .flux()
        );
    }

    @Transactional
    public Flux<LottoStoreResDto> getLottoStoreNearUser(LocationReqDto locationReqDto) {
        GeoJsonPoint location = new GeoJsonPoint(locationReqDto.getLng(), locationReqDto.getLat());

        return storeRepository
                .findByLocationNearAndWinRoundsNotEmpty(location, 10000)
                .map(EntityDtoUtil::toDto);
    }

}
