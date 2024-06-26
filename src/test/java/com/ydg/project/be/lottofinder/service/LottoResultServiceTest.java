package com.ydg.project.be.lottofinder.service;

import com.ydg.project.be.lottofinder.dto.LottoResultResDto;
import com.ydg.project.be.lottofinder.entity.LottoResultEntity;
import com.ydg.project.be.lottofinder.provider.RecentRoundProvider;
import com.ydg.project.be.lottofinder.repository.LottoResultRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LottoResultServiceTest {

    @InjectMocks
    LottoResultService resultService;

    @Mock
    LottoResultRepository resultRepository;

    RecentRoundProvider roundProvider;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        roundProvider = new RecentRoundProvider();
        resultService = new LottoResultService(resultRepository, roundProvider);
    }

    @Test
    @DisplayName("Dto 변환 테스트")
    public void checkLottoResultDto() {
        LottoResultEntity lre = new LottoResultEntity(1101, "1,2,3,4,5,6,7", 1000000L, LocalDate.now());
        roundProvider.updateRound(1101);

        when(resultRepository.findByRound(1101)).thenReturn(Mono.just(lre));

        LottoResultResDto resultResDto = resultService.getRecentLottoResult().block();
        Assertions.assertEquals(lre.getRound(), resultResDto.getRound());
        Assertions.assertEquals(lre.getWinNums(), resultResDto.getLottoNumbers());
        Assertions.assertEquals(lre.getWinPrize(), resultResDto.getWinPrize());
        Assertions.assertEquals(lre.getDate(), resultResDto.getDate());
    }

    @Test
    @DisplayName("다수의 로또 결과로부터 Dto 변환 테스트")
    public void checkLottoResultsLotto() {
        // 7개의 데이터를 얻어오고, 상위 하나의 데이터의 프로퍼티에 하위의 데이터들이 들어가는 것을 테스트.
        List<LottoResultEntity> lreList = new ArrayList<>();

        for (int round = 1010; round >= 1004; round--) {
            lreList.add(new LottoResultEntity(round, "1,2,3,4,5,6,7", 100000L, LocalDate.now()));
        }

        roundProvider.updateRound(1010);

        when(resultRepository.findTop7ByRoundLessThanEqualOrderByRoundDesc(1010)).thenReturn(Flux.fromIterable(lreList));
        LottoResultResDto resultResDto = resultService.getLottoResults(1010).block();

        Assertions.assertEquals(lreList.get(0).getDate(), resultResDto.getDate());
        Assertions.assertEquals(lreList.get(0).getWinNums(), resultResDto.getLottoNumbers());
        Assertions.assertEquals(lreList.get(0).getWinPrize(), resultResDto.getWinPrize());
        Assertions.assertEquals(lreList.get(0).getRound(), resultResDto.getRound());

        for (int i = 1; i < 7; i++) {
            Assertions.assertEquals(lreList.get(i).getDate(), resultResDto.getLastLottoResults().get(i-1).getDate());
            Assertions.assertEquals(lreList.get(i).getWinNums(), resultResDto.getLastLottoResults().get(i-1).getLottoNumbers());
            Assertions.assertEquals(lreList.get(i).getWinPrize(), resultResDto.getLastLottoResults().get(i-1).getWinPrize());
            Assertions.assertEquals(lreList.get(i).getRound(), resultResDto.getLastLottoResults().get(i-1).getRound());
        }
    }
}