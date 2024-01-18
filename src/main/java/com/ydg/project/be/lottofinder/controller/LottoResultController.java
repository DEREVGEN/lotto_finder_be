package com.ydg.project.be.lottofinder.controller;

import com.ydg.project.be.lottofinder.batch.dto.LottoResultDto;
import com.ydg.project.be.lottofinder.dto.LottoResultResDto;
import com.ydg.project.be.lottofinder.service.LottoInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("lotto/result")
@RequiredArgsConstructor
public class LottoResultController {

    private final LottoInfoService infoService;

    @GetMapping("recent")
    public Mono<LottoResultResDto> getRecentLottoResult() {
        return infoService
                .getRecentLottoResult();
    }

    @GetMapping("{round}")
    public Mono<LottoResultResDto> getLottoResult(@PathVariable("round") int round) {
        return infoService
                .getLottoResult(round);
    }
}
