package net.dynu.wpeckers.walktraveler.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.text.DecimalFormat;

@Slf4j
public class GameServiceTest {

    private GameService gameService = new GameService(null, null, null, null, null, null);

    @Test
    public void test() {
        int nextInt = gameService.getNextInt();
        float change = gameService.getRandomChange();
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(5);
        log.info("Next int : {}, Change : {}", nextInt, df.format(change));
    }
}
