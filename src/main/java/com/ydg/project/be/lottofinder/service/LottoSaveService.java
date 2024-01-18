package com.ydg.project.be.lottofinder.service;

import com.ydg.project.be.lottofinder.dto.WinStoreDto;
import com.ydg.project.be.lottofinder.entity.LottoStoreEntity;
import com.ydg.project.be.lottofinder.entity.WinStoreEntity;
import com.ydg.project.be.lottofinder.extractor.LottoResultExtractor;
import com.ydg.project.be.lottofinder.extractor.WinStoreExtractor;
import com.ydg.project.be.lottofinder.repository.LottoResultRepository;
import com.ydg.project.be.lottofinder.repository.WinStoreRepository;
import com.ydg.project.be.lottofinder.util.EntityDtoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class LottoSaveService {

    private final LottoResultRepository lottoResultRepository;
    private final WinStoreRepository winStoreRepository;

    private final LottoResultExtractor lottoResultExtractor;
    private final WinStoreExtractor winStoreExtractor;

    private final ReactiveMongoTemplate mongoTemplate;

    public void saveWinStore(int round) throws IOException {
        winStoreExtractor.getWinStoreDto(round)
                .map((winStoreDto) -> {
                    return saveWinStore(winStoreDto, round);
                }).flatMap(winStoreRepository::save)
                .subscribe();
    }

    @Transactional
    public void saveLottoResult(int round) throws IOException, InterruptedException {
        lottoResultExtractor.getLottoResult(round)
                .map(EntityDtoUtil::toEntity)
                .flatMap(lottoResultRepository::save)
                .subscribe();
    }

    private WinStoreEntity saveWinStore(WinStoreDto winStoreDto, int round) {
        WinStoreEntity winStore = EntityDtoUtil.toEntity(winStoreDto, round);

        Criteria criteria = Criteria.where("storeFid").is(winStoreDto.getStoreFId());
        Update update = new Update().push("winRounds", round);

        // 즉시 저장
        mongoTemplate.updateFirst(Query.query(criteria), update,  LottoStoreEntity.class).subscribe();

        return winStore;
    }
}
