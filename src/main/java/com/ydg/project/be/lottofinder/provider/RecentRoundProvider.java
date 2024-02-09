package com.ydg.project.be.lottofinder.provider;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
public class RecentRoundProvider {

    // 로또의 최신 회차를 담는 변수
    @Getter
    private int latestLottoRound = 900;

    public void updateRound(int round) {
        latestLottoRound = round;
    }
}
