package com.ydg.project.be.lottofinder.batch.service;

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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class LottoScheduleService {

    private final RecentRoundProvider recentRoundProvider;
    private final WinStoreRepository winStoreRepository;
    private final LottoResultRepository lottoResultRepository;
    private final LottoStoreRepository lottoStoreRepository;

    private final LottoResultExtractor lottoResultExtractor;
    private final WinStoreExtractor winStoreExtractor;

    // 토요일 오후 9시마다 실행.
    @Scheduled(cron = "0 0 21 * * 6")
    public void scheduledLottoParsingTask() {
        saveRecentRoundLottoResultFromLottoAPI()
                .thenMany(saveRecentRoundLottoWinStoresFromLottoWeb())
                .subscribe();
    }

    public Mono<LottoResultEntity> saveRecentRoundLottoResultFromLottoAPI() {

        log.info("lotto result batch executed from : " + (recentRoundProvider.getLatestLottoRound()+1));

        return lottoResultExtractor.getLottoResultDeferred(recentRoundProvider.getLatestLottoRound()+1)
                .retryWhen(Retry.fixedDelay(60, Duration.ofMinutes(1))
                        .filter(throwable -> throwable instanceof LottoResultNotUpdatedException))
                .map(EntityDtoUtil::toEntity)
                .flatMap(lottoResultRepository::save)
                .doOnSuccess(resultEntity -> recentRoundProvider.updateRound(recentRoundProvider.getLatestLottoRound() + 1));
    }

    public Flux<WinStoreEntity> saveRecentRoundLottoWinStoresFromLottoWeb() {
        log.info("lotto win store batch executed, parsing round : " + recentRoundProvider.getLatestLottoRound());

        return winStoreRepository
                .findTopByOrderByRoundDesc()
                .flatMapMany(winStoreEntity -> {
                        return saveWinStores(winStoreEntity.getRound());
                });
    }

    public Flux<WinStoreEntity> saveWinStores(int savedRecentRound) {
        int latestRound = recentRoundProvider.getLatestLottoRound();

        // DB 최신회차 ~ 최신 회차
        return Flux.range(savedRecentRound + 1, latestRound - savedRecentRound)
                .flatMap(winStoreExtractor::getWinStoreDtoDeferred)
                .flatMap(winStoreDto -> saveWinStore(EntityDtoUtil.toEntity(winStoreDto)))
                .retryWhen(Retry.fixedDelay(60, Duration.ofMinutes(1)).filter(throwable -> throwable instanceof WinStoreNotUpdatedException));
    }

    private Mono<WinStoreEntity> saveWinStore(WinStoreEntity winStore) {

        // 당첨가게 레포지터리에 저장
        return winStoreRepository.save(winStore)
                .flatMap(
                        // 로또 가게 레포지터리에, 해당 가게의 winRounds에 round 추가
                        savedWinStore -> lottoStoreRepository
                                .updateStoreWinRounds(winStore.getStoreFid(), winStore.getRound())
                                .thenReturn(savedWinStore)
                );
    }
}
