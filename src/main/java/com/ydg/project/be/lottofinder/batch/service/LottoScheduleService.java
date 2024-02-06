package com.ydg.project.be.lottofinder.batch.service;

import com.ydg.project.be.lottofinder.batch.extractor.LottoResultExtractor;
import com.ydg.project.be.lottofinder.repository.LottoResultRepository;
import com.ydg.project.be.lottofinder.service.LottoInfoService;
import com.ydg.project.be.lottofinder.util.EntityDtoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class LottoScheduleService {

    private final LottoInfoService lottoInfoService;
    private final LottoSaveService lottoSaveService;


    // 토요일 오후 9시 10분 0초 마다 실행.
    @Scheduled(cron = "0 10 21 * * 6")
    public void getRecentRoundLottoResultFromLottoAPI() throws IOException, InterruptedException {
        lottoSaveService.saveLottoResult(lottoInfoService.getLatestLottoRound() + 1);
    }

    // 토요일 오후 9시 11분 0초 마다 실행.
    @Scheduled(cron = "0 11 21 * * 6")
    public void getRecentRoundLottoWinStoresFromLottoWeb() throws IOException {
        lottoSaveService.saveWinStore(lottoInfoService.getLatestLottoRound());
    }


}
