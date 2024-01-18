package com.ydg.project.be.lottofinder.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class WinStoreDto {

    private String name;
    private int storeFId;
    private boolean isAuto;
}
