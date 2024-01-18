package com.ydg.project.be.lottofinder.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class StoreInfoDto {

    String storeName;
    String address;
    double lng;
    double lat;
    List<Integer> winRounds;
    String tel;
}
