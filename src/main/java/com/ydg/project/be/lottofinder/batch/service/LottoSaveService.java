package com.ydg.project.be.lottofinder.batch.service;

import com.ydg.project.be.lottofinder.batch.dto.WinStoreDto;
import com.ydg.project.be.lottofinder.entity.LottoResultEntity;
import com.ydg.project.be.lottofinder.entity.LottoStoreEntity;
import com.ydg.project.be.lottofinder.entity.WinStoreEntity;
import com.ydg.project.be.lottofinder.batch.extractor.LottoResultExtractor;
import com.ydg.project.be.lottofinder.batch.extractor.WinStoreExtractor;
import com.ydg.project.be.lottofinder.provider.RecentRoundProvider;
import com.ydg.project.be.lottofinder.repository.LottoResultRepository;
import com.ydg.project.be.lottofinder.repository.WinStoreRepository;
import com.ydg.project.be.lottofinder.util.EntityDtoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
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

    private final ReactiveMongoTemplate mongoTemplate;

    public Flux<WinStoreEntity> saveWinStore(int round) throws IOException {
        return winStoreExtractor.getWinStoreDto(round)
                .map((winStoreDto) -> saveWinStore(winStoreDto, round))
                .flatMap(winStoreRepository::save);
    }

    public Mono<LottoResultEntity> saveLottoResult(int round) throws IOException, InterruptedException {
        return lottoResultExtractor.getLottoResult(round)
                .map(EntityDtoUtil::toEntity)
                .flatMap(lottoResultRepository::save);
    }

    public WinStoreEntity saveWinStore(WinStoreDto winStoreDto, int round) {
        WinStoreEntity winStore = EntityDtoUtil.toEntity(winStoreDto, round);

        Criteria criteria = Criteria.where("storeFid").is(winStoreDto.getStoreFId());
        Update update = new Update().push("winRounds", round);

        // 당첨가게의 회차수의 배열에 해당 라운드 추가.
        mongoTemplate.updateFirst(Query.query(criteria), update,  LottoStoreEntity.class).subscribe();

        return winStore;
    }
}
