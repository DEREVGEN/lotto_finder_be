package com.ydg.project.be.lottofinder.batch.service;

import com.ydg.project.be.lottofinder.provider.RecentRoundProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class LottoScheduleService {

    private final RecentRoundProvider recentRoundProvider;
    private final LottoSaveService lottoSaveService;


    // 토요일 오후 9시 10분 0초 마다 실행.
    @Scheduled(cron = "0 10 21 * * 6")
    public void getRecentRoundLottoResultFromLottoAPI() throws IOException, InterruptedException {
        lottoSaveService.saveLottoResult(recentRoundProvider.getLatestLottoRound() + 1);
    }

    // 토요일 오후 9시 11분 0초 마다 실행.
    @Scheduled(cron = "0 11 21 * * 6")
    public void getRecentRoundLottoWinStoresFromLottoWeb() throws IOException {
        lottoSaveService.saveWinStore(recentRoundProvider.getLatestLottoRound());
    }


}
