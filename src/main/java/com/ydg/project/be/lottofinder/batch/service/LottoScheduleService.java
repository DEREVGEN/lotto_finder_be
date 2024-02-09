package com.ydg.project.be.lottofinder.batch.service;

import com.ydg.project.be.lottofinder.entity.WinStoreEntity;
import com.ydg.project.be.lottofinder.provider.RecentRoundProvider;
import com.ydg.project.be.lottofinder.repository.WinStoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
@Slf4j
public class LottoScheduleService {

    private final RecentRoundProvider recentRoundProvider;
    private final LottoSaveService lottoSaveService;
    private final WinStoreRepository winStoreRepository;


    // 토요일 오후 9시 10분 0초 마다 실행.
    @Scheduled(cron = "0 10 21 * * 6")
    public void saveRecentRoundLottoResultFromLottoAPI() {

        log.info("lotto result batch excuted from : " + recentRoundProvider.getLatestLottoRound());

        while(true) {
             try {
                 lottoSaveService.saveLottoResult(recentRoundProvider.getLatestLottoRound() + 1).block();
                 recentRoundProvider.updateRound(recentRoundProvider.getLatestLottoRound() + 1);
             } catch (Exception e) {
                 break;
             }
        }
    }

    // 토요일 오후 9시 11분 0초 마다 실행.
    @Scheduled(cron = "0 11 21 * * 6")
    public void saveRecentRoundLottoWinStoresFromLottoWeb() {
        winStoreRepository
                .findTopByOrderByRoundDesc()
                .flatMapMany(winStoreEntity -> {
                    try {
                        return saveWinStores(winStoreEntity.getRound());
                    } catch (IOException e) {
                        return Mono.error(e);
                    }
                })
                .subscribe();
    }

    private Flux<WinStoreEntity> saveWinStores(int savedRecentRound) throws IOException {
        int latestRound = recentRoundProvider.getLatestLottoRound();
        return Flux.range(savedRecentRound + 1, latestRound - savedRecentRound)
                .flatMap(round -> {
                    try {
                        log.info("save win stores in round of " + round);
                        return lottoSaveService.saveWinStore(round);
                    } catch (IOException e) {
                        return Flux.error(new RuntimeException(e));
                    }
                });
    }
}
