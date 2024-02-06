package com.ydg.project.be.lottofinder.batch.dto;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LottoStoreDto {

    private String address;
    private String tel;
    private String name;
    private int storeFid;
    private double lng;
    private double lat;
}
