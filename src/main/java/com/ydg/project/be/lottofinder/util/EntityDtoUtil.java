package com.ydg.project.be.lottofinder.util;

import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;
import com.ydg.project.be.lottofinder.dto.LottoResultDto;
import com.ydg.project.be.lottofinder.dto.LottoStoreDto;
import com.ydg.project.be.lottofinder.dto.WinStoreDto;
import com.ydg.project.be.lottofinder.entity.LottoResultEntity;
import com.ydg.project.be.lottofinder.entity.LottoStoreEntity;
import com.ydg.project.be.lottofinder.entity.WinStoreEntity;

import java.util.List;

public class EntityDtoUtil {
    public static LottoResultEntity toEntity(LottoResultDto resultDto) {

        StringBuilder sb = new StringBuilder();
        sb
                .append(resultDto.getN1()).append(',')
                .append(resultDto.getN2()).append(',')
                .append(resultDto.getN3()).append(',')
                .append(resultDto.getN4()).append(',')
                .append(resultDto.getN5()).append(',')
                .append(resultDto.getN6()).append(',')
                .append(resultDto.getBn());


        LottoResultEntity lottoResult = new LottoResultEntity(
                resultDto.getRound(),
                sb.toString(),
                resultDto.getWinPrize(),
                resultDto.getDate()
        );

        return lottoResult;
    }

    public static LottoStoreEntity toEntity(LottoStoreDto storeDto) {
        Point storeLocation = getPoint(storeDto.getLat(), storeDto.getLng());

        LottoStoreEntity lottoStore = new LottoStoreEntity(
                storeDto.getName(),
                storeDto.getAddress(),
                storeDto.getTel(),
                storeDto.getStoreFid(),
                storeLocation
        );

        return lottoStore;
    }

    private static Point getPoint(double lng, double lat) {
        return new Point(new Position(List.of(lat, lng)));
    }

    public static WinStoreEntity toEntity(WinStoreDto winStoreDto, int round) {
        WinStoreEntity winStore = new WinStoreEntity(winStoreDto.isAuto(), round, winStoreDto.getStoreFId());

        return winStore;
    }


}
