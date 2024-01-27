package com.ydg.project.be.lottofinder.batch.service;

import com.ydg.project.be.lottofinder.extractor.LottoStoreExtractor;
import com.ydg.project.be.lottofinder.repository.LottoStoreRepository;
import com.ydg.project.be.lottofinder.service.LottoSaveService;
import com.ydg.project.be.lottofinder.util.EntityDtoUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class BatchService {
    private final LottoStoreRepository lottoStoreRepository;
    private final LottoStoreExtractor lottoStoreExtractor;
    private final LottoSaveService lottoSaveService;

}
