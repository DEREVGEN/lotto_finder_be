package com.ydg.project.be.lottofinder.controller;

import com.ydg.project.be.lottofinder.dto.LottoResultResDto;
import com.ydg.project.be.lottofinder.exception.RoundNotFoundException;
import com.ydg.project.be.lottofinder.service.LottoResultService;
import com.ydg.project.be.lottofinder.service.LottoResultServiceV2;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("lotto/result")
@RequiredArgsConstructor
public class LottoResultController {

    private final LottoResultService resultService;
    private final LottoResultServiceV2 resultServiceV2;

    @GetMapping("recent")
    public Mono<LottoResultResDto> getRecentLottoResult() {
        return resultService
                .getRecentLottoResult();
    }

    @GetMapping("recent/multiple")
    public Mono<LottoResultResDto> getRecentLottoResults() {
        return resultService
                .getRecentLottoResults();
    }

    @GetMapping("/v2/recent/multiple")
    public Flux<LottoResultResDto> getRecentLottoResultsWithV2() {
        return resultServiceV2
                .getRecentLottoResults();
    }

    @GetMapping("{round}")
    public Mono<LottoResultResDto> getLottoResult(@PathVariable("round") int round) {
        return resultService
                .getLottoResult(round)
                .onErrorResume(RoundNotFoundException.class, e -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Lotto result not found")));
    }

    @GetMapping("{round}/multiple")
    public Mono<LottoResultResDto> getLottoResults(@PathVariable("round") int round) {
        return resultService
                .getLottoResults(round)
                .onErrorResume(RoundNotFoundException.class, e -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Lotto result not found")));
    }

    @GetMapping("/v2/{round}/multiple")
    public Flux<LottoResultResDto> getLottoResultsWithV2(@PathVariable("round") int round) {
        return resultServiceV2
                .getLottoResults(round)
                .onErrorResume(RoundNotFoundException.class, e -> Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Lotto result not found")));
    }
}
