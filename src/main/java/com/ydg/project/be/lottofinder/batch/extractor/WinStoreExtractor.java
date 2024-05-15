package com.ydg.project.be.lottofinder.batch.extractor;

import com.ydg.project.be.lottofinder.batch.dto.WinStoreDto;
import com.ydg.project.be.lottofinder.batch.exception.WinStoreNotUpdatedException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class WinStoreExtractor {

    private final String url = "https://dhlottery.co.kr/store.do?method=topStore&pageGubun=L645&drwNo=";

    public Flux<WinStoreDto> getWinStoreDto(int round){
        Document document = null;
        try {
            document = Jsoup
                    .connect(url + round)
                    .get();
        } catch (IOException e) {
            return Flux.error(e);
        }

        Elements winStoreRowElements = document.selectXpath("(//table[@class='tbl_data tbl_data_col'])[1]/tbody/tr");

        List<WinStoreDto> winStoreDtoList = new ArrayList<>();

        // 테이블의 첫번째 요소.
        Element firstElement = winStoreRowElements.first();
        // 데이터가 갱신이 안된 경우
        if (firstElement.selectXpath("td[1]").hasClass("nodata")) {
            return Flux.error(new WinStoreNotUpdatedException("Cannot parse win store of round: " + round));
        }

        for (Element row : winStoreRowElements) {
            WinStoreDto winStoreDto = new WinStoreDto();
            String name = row.selectXpath("td[2]").text();
            boolean isAuto = row.selectXpath("td[3]").text().equals("자동");
            String storeUniqueIdStr = row.selectXpath("td[5]/a").attr("onclick");
            int storeUniqueId = Integer.parseInt(storeUniqueIdStr.substring(storeUniqueIdStr.indexOf("(\'") + 2, storeUniqueIdStr.indexOf("\')")));


            winStoreDto.setName(name);
            winStoreDto.setAuto(isAuto);
            winStoreDto.setStoreFid(storeUniqueId);
            winStoreDto.setRound(round);
            winStoreDtoList.add(winStoreDto);
        }

        return Flux.fromIterable(winStoreDtoList);
    }

    public Flux<WinStoreDto> getWinStoreDtoDeferred(int round) {
        return Flux.defer(() -> {
            return getWinStoreDto(round);
        });
    }
}
