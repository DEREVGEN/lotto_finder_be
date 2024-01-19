package com.ydg.project.be.lottofinder.controller;


import com.ydg.project.be.lottofinder.dto.LocationReqDto;
import com.ydg.project.be.lottofinder.dto.LottoStoreResDto;
import com.ydg.project.be.lottofinder.dto.WinStoreResDto;
import com.ydg.project.be.lottofinder.repository.LottoStoreRepository;
import com.ydg.project.be.lottofinder.service.LottoInfoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("lotto/store")
@RequiredArgsConstructor
public class LottoStoreController {
    private final LottoStoreRepository storeRepository;

    private final LottoInfoService infoService;

    @GetMapping("win/recent")
    public Flux<WinStoreResDto> getRecentWinStore() {
        return infoService.getRecentWinLottoStore();
    }

    @GetMapping("win/{round}")
    public Flux<WinStoreResDto> getWinStore(@PathVariable("round") int round) {
        return infoService.getWinLottoStore(round);
    }

    @PostMapping("win/near")
    public Flux<LottoStoreResDto> getWinStoreNearUser(@Valid @RequestBody LocationReqDto locationReqDto) {
        return infoService.getLottoStoreNearUser(locationReqDto);
    }

}
