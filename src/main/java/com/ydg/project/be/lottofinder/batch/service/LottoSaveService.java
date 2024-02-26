package com.ydg.project.be.lottofinder.batch.service;

import com.ydg.project.be.lottofinder.batch.dto.WinStoreDto;
import com.ydg.project.be.lottofinder.entity.LottoResultEntity;
import com.ydg.project.be.lottofinder.entity.WinStoreEntity;
import com.ydg.project.be.lottofinder.batch.extractor.LottoResultExtractor;
import com.ydg.project.be.lottofinder.batch.extractor.WinStoreExtractor;
import com.ydg.project.be.lottofinder.repository.LottoResultRepository;
import com.ydg.project.be.lottofinder.repository.LottoStoreRepository;
import com.ydg.project.be.lottofinder.repository.WinStoreRepository;
import com.ydg.project.be.lottofinder.util.EntityDtoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class LottoSaveService {

    private final LottoResultRepository lottoResultRepository;
    private final WinStoreRepository winStoreRepository;

    private final LottoResultExtractor lottoResultExtractor;
    private final WinStoreExtractor winStoreExtractor;

    private final LottoStoreRepository lottoStoreRepository;


    public Mono<LottoResultEntity> saveLottoResult(int round) throws IOException, InterruptedException {
        return lottoResultExtractor.getLottoResult(round)
                .map(EntityDtoUtil::toEntity)
                .flatMap(lottoResultRepository::save);
    }

    public Flux<WinStoreEntity> saveWinStore(int round) throws IOException {
        return winStoreExtractor.getWinStoreDto(round)
                .flatMap(winStoreDto -> saveWinStore(winStoreDto, round));
    }


    public Mono<WinStoreEntity> saveWinStore(WinStoreDto winStoreDto, int round) {
        WinStoreEntity winStore = EntityDtoUtil.toEntity(winStoreDto, round);

        return winStoreRepository.save(winStore)
                .flatMap(
                        savedWinStore -> lottoStoreRepository.updateStoreWinRounds(winStoreDto.getStoreFid(), round)
                                .thenReturn(savedWinStore)
                );
    }
}
