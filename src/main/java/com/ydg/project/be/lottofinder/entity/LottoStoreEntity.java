package com.ydg.project.be.lottofinder.entity;

import com.mongodb.client.model.geojson.Point;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document("lottoStore")
@Getter
@ToString
public class LottoStoreEntity {

    @Id
    private String id;
    private String storeName;
    private String address;
    private String tel;
    private int storeFid;
    private List<Integer> winRounds = new ArrayList<>();
    private Point location;

    @Builder
    public LottoStoreEntity(String storeName, String address, String tel, int storeFid, Point location) {
        this.storeName = storeName;
        this.address = address;
        this.tel = tel;
        this.storeFid = storeFid;
        this.location = location;
    }
}
