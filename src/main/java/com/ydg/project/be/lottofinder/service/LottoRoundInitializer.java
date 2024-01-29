package com.ydg.project.be.lottofinder.service;

import com.ydg.project.be.lottofinder.repository.LottoResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LottoRoundInitializer implements CommandLineRunner {

    private final LottoInfoService lottoInfoService;
    private final LottoResultRepository lottoResultRepository;

    @Override
    public void run(String... args) throws Exception {
        // 최신 회차 데이터를 유지 시키기 위한 로직
        lottoResultRepository.findTopByOrderByRoundDesc()
                .map(lottoResult -> {
                    // 최신 회차데이터 초기화
                    lottoInfoService.updateLatestLottoRound(lottoResult.getRound());
                    return lottoResult;
                }).subscribe();
    }
}