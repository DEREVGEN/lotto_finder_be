package com.ydg.project.be.lottofinder.service;

import com.ydg.project.be.lottofinder.dto.LottoResultResDto;
import com.ydg.project.be.lottofinder.entity.LottoResultEntity;
import com.ydg.project.be.lottofinder.exception.RoundNotFoundException;
import com.ydg.project.be.lottofinder.provider.RecentRoundProvider;
import com.ydg.project.be.lottofinder.repository.LottoResultRepository;
import com.ydg.project.be.lottofinder.util.EntityDtoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LottoResultService {

    private final LottoResultRepository resultRepository;
    private final RecentRoundProvider recentRoundProvider;

    public Mono<LottoResultResDto> getRecentLottoResult() {
        return getLottoResult(recentRoundProvider.getLatestLottoRound());
    }

    public Mono<LottoResultResDto> getRecentLottoResults() {
        return getLottoResults(recentRoundProvider.getLatestLottoRound());
    }

    public Mono<LottoResultResDto> getLottoResult(int round) {

        // 만약 DB에 저장된 회차 보다, 더 큰 회차를 조회 하는 경우 혹은 900회 미만을 조회하는 경우
        if (round > recentRoundProvider.getLatestLottoRound() || 900 >= round) {
            return Mono.error(RoundNotFoundException::new);
        }

        return resultRepository
                .findByRound(round)
                .map(EntityDtoUtil::toDto);
    }

    public Mono<LottoResultResDto> getLottoResults(int round) {

        // 만약 DB에 저장된 회차 보다, 더 큰 회차를 조회 하는 경우 혹은 900회 미만을 조회하는 경우
        if (round > recentRoundProvider.getLatestLottoRound() || 900 >= round) {
            return Mono.error(RoundNotFoundException::new);
        }

        return resultRepository
                .findTop7ByRoundLessThanEqualOrderByRoundDesc(round)
                .collectList()
                .map(this::buildToDto);
    }

    private LottoResultResDto buildToDto(List<LottoResultEntity> lottoResultEntityList) {
        LottoResultResDto resultResDto = new LottoResultResDto();

        for (int i = 0; i < lottoResultEntityList.size(); i++) {
            LottoResultResDto lottoResultResDto = EntityDtoUtil.toDto(lottoResultEntityList.get(i));

            if (i == 0) {
                resultResDto.setLottoNumbers(lottoResultResDto.getLottoNumbers());
                resultResDto.setDate(lottoResultResDto.getDate());
                resultResDto.setWinPrize(lottoResultResDto.getWinPrize());
                resultResDto.setRound(lottoResultResDto.getRound());
            } else {
                resultResDto.addLottoResult(lottoResultResDto);
            }
        }

        return resultResDto;
    }
}
