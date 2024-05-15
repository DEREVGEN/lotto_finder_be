package com.ydg.project.be.lottofinder.batch.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WinStoreDto {

    private String name;
    private int storeFid;
    private boolean isAuto;
    private int round;
}
