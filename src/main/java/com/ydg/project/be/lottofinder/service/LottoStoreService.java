package com.ydg.project.be.lottofinder.service;


import com.ydg.project.be.lottofinder.dto.LocationReqDto;
import com.ydg.project.be.lottofinder.dto.LottoStoreResDto;
import com.ydg.project.be.lottofinder.dto.WinStoreResDto;
import com.ydg.project.be.lottofinder.provider.RecentRoundProvider;
import com.ydg.project.be.lottofinder.repository.LottoStoreRepository;
import com.ydg.project.be.lottofinder.repository.WinStoreRepository;
import com.ydg.project.be.lottofinder.util.EntityDtoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class LottoStoreService {

    private final LottoStoreRepository storeRepository;
    private final WinStoreRepository winStoreRepository;
    private final RecentRoundProvider recentRoundProvider;

    public Flux<LottoStoreResDto> getLottoWinStoreNearUser(LocationReqDto locationReqDto) {
        GeoJsonPoint location = new GeoJsonPoint(locationReqDto.getLng(), locationReqDto.getLat());

        return storeRepository
                .findByLocationNearAndWinRoundsNotEmpty(location, 10000)
                .map(EntityDtoUtil::toDto);
    }

    public Flux<WinStoreResDto> getRecentWinLottoStore() {
        return getWinLottoStore(recentRoundProvider.getLatestLottoRound());
    }

    public Flux<WinStoreResDto> getWinLottoStore(int round) {
        return  winStoreRepository.findByRound(round).flatMap(
                winStore ->
                        storeRepository.findByStoreFid(winStore.getStoreFid())
                                .map(lottoStore -> EntityDtoUtil.toDto(winStore, lottoStore))
                                .flux()
        );
    }
}
