package com.ydg.project.be.lottofinder.batch.extractor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ydg.project.be.lottofinder.batch.dto.LottoStoreDto;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


@Service
public class LottoStoreExtractor {
    private final URI lottoStoreUri = URI.create("https://dhlottery.co.kr/store.do?method=sellerInfo645Result");
    private HttpClient client = HttpClient.newHttpClient();
    private ObjectMapper om = new ObjectMapper();
    private Logger storeLogger = Logger.getLogger(this.getClass().getName());

    private List<String> areas =
            List.of("서울", "경기", "부산", "대구", "인천", "대전", "울산", "강원", "충북", "충남", "광주", "전북", "전남", "경북", "경남", "제주", "세종");

    public JsonNode getStoreJson(String area, int page) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(lottoStoreUri)
                .POST(HttpRequest.BodyPublishers.ofString("searchType=3&nowPage=" + page + "&sltSIDO2=" + area + "&sltGUGUN2=&rtlrSttus=001"))
                .setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return om.readTree(response.body());
    }

    public Flux<LottoStoreDto> getStoreDto() throws IOException, InterruptedException {

        List<LottoStoreDto> storeDtoList = new ArrayList<>();

        for (String area : areas) {

            for (int page = 1; ; page++) {
                JsonNode storeResJson = getStoreJson(area, page);

                JsonNode storesJson = storeResJson.get("arr");

                for (JsonNode storeJson : storesJson) {
                    LottoStoreDto lottoStoreDto = new LottoStoreDto();
                    lottoStoreDto.setAddress(escapeHtml(storeJson.get("BPLCDORODTLADRES").asText()));
                    lottoStoreDto.setTel(storeJson.get("RTLRSTRTELNO").asText());
                    lottoStoreDto.setName(escapeHtml(storeJson.get("FIRMNM").asText()));
                    lottoStoreDto.setLng(storeJson.get("LONGITUDE").asDouble());
                    lottoStoreDto.setLat(storeJson.get("LATITUDE").asDouble());
                    lottoStoreDto.setStoreFid(storeJson.get("RTLRID").asInt());

                    storeDtoList.add(lottoStoreDto);
                    storeLogger.info("parsing - " + lottoStoreDto);
                }

                boolean pageIsNext = storeResJson.get("pageIsNext").asBoolean();

                if (!pageIsNext)
                    break;
                Thread.sleep(1000);
            }
        }

        return Flux.fromIterable(storeDtoList);
    }

    private String escapeHtml(String name) {
        return StringEscapeUtils.unescapeHtml4(StringEscapeUtils.unescapeHtml4(name)).trim();
    }
}
