package com.ydg.project.be.lottofinder.service;

import com.ydg.project.be.lottofinder.batch.dto.LottoResultDto;
import com.ydg.project.be.lottofinder.dto.LottoResultResDto;
import com.ydg.project.be.lottofinder.repository.LottoResultRepository;
import com.ydg.project.be.lottofinder.util.EntityDtoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class LottoInfoService {

    private final LottoResultRepository resultRepository;


    // 로또의 최신 회차를 담는 변수
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

    public Mono<LottoResultResDto> getLottoResult(int round) {
        return resultRepository
                .findByRound(round)
                .map(EntityDtoUtil::toDto);
    }

}
