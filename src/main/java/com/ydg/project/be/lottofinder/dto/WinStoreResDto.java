package com.ydg.project.be.lottofinder.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WinStoreResDto {

    boolean isAuto;
    int round;
    LottoStoreResDto lottoStore;
}
