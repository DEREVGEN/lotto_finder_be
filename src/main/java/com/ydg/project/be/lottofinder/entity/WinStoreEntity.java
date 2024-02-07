package com.ydg.project.be.lottofinder.entity;

import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("winStore")
@Getter
@ToString
public class WinStoreEntity {

    @Id
    private String id;
    private boolean isAuto;
    private int round;
    private int storeFid;

    public WinStoreEntity(boolean isAuto, int round, int storeFid) {
        this.isAuto = isAuto;
        this.round = round;
        this.storeFid = storeFid;
    }
}
