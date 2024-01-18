package com.ydg.project.be.lottofinder.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@Setter
@ToString
public class LottoResultDto {

    private int n1, n2, n3, n4, n5, n6, bn;
    private int round;
    private LocalDate date;
    private Long winPrize;
}
