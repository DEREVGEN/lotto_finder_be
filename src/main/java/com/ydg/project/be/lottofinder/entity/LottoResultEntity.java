package com.ydg.project.be.lottofinder.entity;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document("lottoResult")
@Getter
@ToString
public class LottoResultEntity {

    @Id
    private String id;
    private int round;
    private String winNums;
    private Long winPrize;
    private LocalDate date;

    public LottoResultEntity(int round, String winNums, Long winPrize, LocalDate date) {
        this.round = round;
        this.winNums = winNums;
        this.winPrize = winPrize;
        this.date = date;
    }
}
