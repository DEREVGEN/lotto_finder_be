package com.ydg.project.be.lottofinder.service;

import com.ydg.project.be.lottofinder.dto.LottoResultResDto;
import com.ydg.project.be.lottofinder.exception.RoundNotFoundException;
import com.ydg.project.be.lottofinder.provider.RecentRoundProvider;
import com.ydg.project.be.lottofinder.repository.LottoResultRepository;
import com.ydg.project.be.lottofinder.util.EntityDtoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LottoResultServiceV2 {

    private final LottoResultRepository resultRepository;
    private final RecentRoundProvider recentRoundProvider;

    public Flux<LottoResultResDto> getRecentLottoResults() {
        return getLottoResults(recentRoundProvider.getLatestLottoRound());
    }

    public Flux<LottoResultResDto> getLottoResults(int round) {

        // 만약 DB에 저장된 회차 보다, 더 큰 회차를 조회 하는 경우 혹은 900회 미만을 조회하는 경우
        if (round > recentRoundProvider.getLatestLottoRound() || 900 >= round) {
            return Flux.error(RoundNotFoundException::new);
        }

        return resultRepository
                .findTop7ByRoundLessThanEqualOrderByRoundDesc(round)
                .map(EntityDtoUtil::toDto);
    }
}
