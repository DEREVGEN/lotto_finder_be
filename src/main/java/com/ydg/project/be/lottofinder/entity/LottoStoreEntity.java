package com.ydg.project.be.lottofinder.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
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
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;

    @Builder
    public LottoStoreEntity(String storeName, String address, String tel, int storeFid, GeoJsonPoint location) {
        this.storeName = storeName;
        this.address = address;
        this.tel = tel;
        this.storeFid = storeFid;
        this.location = location;
    }
}
