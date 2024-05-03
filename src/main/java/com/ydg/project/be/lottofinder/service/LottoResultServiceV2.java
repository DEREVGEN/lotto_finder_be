package com.ydg.project.be.lottofinder.service;

import com.ydg.project.be.lottofinder.dto.LottoResultResDto;
import com.ydg.project.be.lottofinder.provider.RecentRoundProvider;
import com.ydg.project.be.lottofinder.repository.LottoResultRepository;
import com.ydg.project.be.lottofinder.util.EntityDtoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class LottoResultServiceV2 {

    private final LottoResultRepository resultRepository;
    private final RecentRoundProvider recentRoundProvider;

    public Flux<LottoResultResDto> getRecentLottoResults() {
        return getLottoResults(recentRoundProvider.getLatestLottoRound());
    }

    public Flux<LottoResultResDto> getLottoResults(int round) {
        return resultRepository
                .findTop7ByRoundLessThanEqualOrderByRoundDesc(round)
                .map(EntityDtoUtil::toDto);
    }
}
