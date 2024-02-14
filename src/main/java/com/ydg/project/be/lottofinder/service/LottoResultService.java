package com.ydg.project.be.lottofinder.service;

import com.ydg.project.be.lottofinder.dto.LottoResultResDto;
import com.ydg.project.be.lottofinder.entity.LottoResultEntity;
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
        return resultRepository
                .findByRound(round)
                .map(EntityDtoUtil::toDto);
    }

    public Mono<LottoResultResDto> getLottoResults(int round) {
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
