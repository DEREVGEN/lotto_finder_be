package com.ydg.project.be.lottofinder.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class LottoResultResDto {
    private int round;
    private String lottoNumbers;
    private LocalDate date;
    private long winPrize;

    private List<LottoResultResDto> lastLottoResults = new ArrayList<>();

    public void addLottoResult(LottoResultResDto lottoResultResDto) {
        lastLottoResults.add(lottoResultResDto);
    }
}
