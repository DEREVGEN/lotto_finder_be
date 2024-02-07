package com.ydg.project.be.lottofinder.controller;


import com.ydg.project.be.lottofinder.dto.LocationReqDto;
import com.ydg.project.be.lottofinder.dto.LottoStoreResDto;
import com.ydg.project.be.lottofinder.dto.WinStoreResDto;
import com.ydg.project.be.lottofinder.service.LottoStoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("lotto/store")
@RequiredArgsConstructor
public class LottoStoreController {

    private final LottoStoreService lottoStoreService;

    @GetMapping("win/recent")
    public Flux<WinStoreResDto> getRecentWinStore() {
        return lottoStoreService.getRecentWinLottoStore();
    }

    @GetMapping("win/{round}")
    public Flux<WinStoreResDto> getWinStore(@PathVariable("round") int round) {
        return lottoStoreService.getWinLottoStore(round);
    }

    @PostMapping("win/near")
    public Flux<LottoStoreResDto> getWinStoreNearUser(@Valid @RequestBody LocationReqDto locationReqDto) {
        return lottoStoreService.getLottoWinStoreNearUser(locationReqDto);
    }
}
