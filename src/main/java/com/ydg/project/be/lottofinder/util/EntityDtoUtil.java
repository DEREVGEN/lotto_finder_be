package com.ydg.project.be.lottofinder.util;

import com.mongodb.client.model.geojson.Position;
import com.ydg.project.be.lottofinder.batch.dto.LottoResultDto;
import com.ydg.project.be.lottofinder.batch.dto.LottoStoreDto;
import com.ydg.project.be.lottofinder.batch.dto.WinStoreDto;
import com.ydg.project.be.lottofinder.dto.LottoResultResDto;
import com.ydg.project.be.lottofinder.dto.LottoStoreResDto;
import com.ydg.project.be.lottofinder.dto.WinStoreResDto;
import com.ydg.project.be.lottofinder.entity.LottoResultEntity;
import com.ydg.project.be.lottofinder.entity.LottoStoreEntity;
import com.ydg.project.be.lottofinder.entity.WinStoreEntity;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

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
        GeoJsonPoint geoJsonPoint = new GeoJsonPoint(storeDto.getLng(), storeDto.getLat());

        LottoStoreEntity lottoStore = new LottoStoreEntity(
                storeDto.getName(),
                storeDto.getAddress(),
                storeDto.getTel(),
                storeDto.getStoreFid(),
                geoJsonPoint
        );

        return lottoStore;
    }

    public static WinStoreEntity toEntity(WinStoreDto winStoreDto, int round) {
        WinStoreEntity winStore = new WinStoreEntity(winStoreDto.isAuto(), round, winStoreDto.getStoreFId());

        return winStore;
    }

    public static LottoResultResDto toDto(LottoResultEntity resultEntity) {
        LottoResultResDto resultResDto = new LottoResultResDto();

        resultResDto.setDate(resultEntity.getDate());
        resultResDto.setRound(resultEntity.getRound());
        resultResDto.setLottoNumbers(resultEntity.getWinNums());
        resultResDto.setWinPrize(resultEntity.getWinPrize());

        return resultResDto;
    }

    public static WinStoreResDto toDto(WinStoreEntity winStoreEntity, LottoStoreEntity storeEntity) {

        LottoStoreResDto lottoStoreDto = toDto(storeEntity);

        WinStoreResDto winStoreDto = new WinStoreResDto();
        winStoreDto.setAuto(winStoreEntity.isAuto());
        winStoreDto.setRound(winStoreEntity.getRound());
        winStoreDto.setLottoStore(lottoStoreDto);

        return winStoreDto;
    }

    public static LottoStoreResDto toDto(LottoStoreEntity storeEntity) {
        return LottoStoreResDto.builder()
                .storeName(storeEntity.getStoreName())
                .tel(storeEntity.getTel())
                .lat(storeEntity.getLocation().getY())
                .lng(storeEntity.getLocation().getX())
                .address(storeEntity.getAddress())
                .winRounds(storeEntity.getWinRounds())
                .build();
    }


}
