package com.ydg.project.be.lottofinder.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class LottoResultResDto {
    int round;
    String lottoNumbers;
    LocalDate date;
    long winPrize;
}
