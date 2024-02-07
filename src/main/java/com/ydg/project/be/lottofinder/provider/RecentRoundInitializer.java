package com.ydg.project.be.lottofinder.provider;

import com.ydg.project.be.lottofinder.repository.LottoResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Profile("{dev, deploy}")
public class RecentRoundInitializer implements CommandLineRunner {

    private final RecentRoundProvider recentRoundProvider;
    private final LottoResultRepository lottoResultRepository;

    @Override
    public void run(String... args) {
        lottoResultRepository.findTopByOrderByRoundDesc()
                .map(lottoResult -> {
                    // 최신 회차데이터 초기화
                    recentRoundProvider.updateRound(lottoResult.getRound());
                    return lottoResult;
                }).subscribe();
    }
}
