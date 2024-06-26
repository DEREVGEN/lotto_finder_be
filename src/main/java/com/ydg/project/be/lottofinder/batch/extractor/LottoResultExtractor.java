package com.ydg.project.be.lottofinder.batch.extractor;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ydg.project.be.lottofinder.batch.dto.LottoResultDto;
import com.ydg.project.be.lottofinder.batch.exception.LottoResultNotUpdatedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.logging.Logger;


@Service
@Slf4j
public class LottoResultExtractor {
    private final String lottoUrl = "https://www.dhlottery.co.kr/common.do?method=getLottoNumber&drwNo={drwNo}";
    private ObjectMapper om = new ObjectMapper();
    private HttpClient client = HttpClient.newHttpClient();
    private final Logger resultLogger = Logger.getLogger(this.getClass().getName());

    public Mono<LottoResultDto> getLottoResult(int round) {
        try {

            URI lottoResultUri = UriComponentsBuilder
                    .fromUriString(lottoUrl)
                    .build(round);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(lottoResultUri)
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            JsonNode lottoResultJson = om.readTree(response.body());

            // (데이터를 잘 받아온 경우) 만약, 갱신이 안되었을때,
            if (!lottoResultJson.get("returnValue").textValue().equals("success")) {
                return Mono.error(new LottoResultNotUpdatedException("Cannot parse result of round: " + round));
            }

            LottoResultDto lottoResultDto = new LottoResultDto();
            lottoResultDto.setN1(lottoResultJson.get("drwtNo1").asInt());
            lottoResultDto.setN2(lottoResultJson.get("drwtNo2").asInt());
            lottoResultDto.setN3(lottoResultJson.get("drwtNo3").asInt());
            lottoResultDto.setN4(lottoResultJson.get("drwtNo4").asInt());
            lottoResultDto.setN5(lottoResultJson.get("drwtNo5").asInt());
            lottoResultDto.setN6(lottoResultJson.get("drwtNo6").asInt());
            lottoResultDto.setBn(lottoResultJson.get("bnusNo").asInt());

            lottoResultDto.setDate(LocalDate.parse(lottoResultJson.get("drwNoDate").asText()));
            lottoResultDto.setRound(lottoResultJson.get("drwNo").asInt());
            lottoResultDto.setWinPrize(lottoResultJson.get("firstWinamnt").asLong());

            resultLogger.info("parsing lotto result - " + lottoResultDto);

            return Mono.just(lottoResultDto);
        } catch (IOException | InterruptedException e) {
            return Mono.error(e);
        }
    }


    public Mono<LottoResultDto> getLottoResultDeferred(int round) {
        return Mono.defer(() -> {
            return getLottoResult(round);
        });
    }

}
