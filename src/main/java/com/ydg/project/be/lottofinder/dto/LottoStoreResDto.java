package com.ydg.project.be.lottofinder.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class LottoStoreResDto {

    String storeName;
    String address;
    double lng;
    double lat;
    List<Integer> winRounds;
    String tel;
}
