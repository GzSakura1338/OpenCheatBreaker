package com.cheatbreaker.client.radio;

import com.cheatbreaker.client.CheatBreaker;
import java.time.Duration;
import java.time.LocalDateTime;

public class RadioStartThread extends Thread {
    @Override
    public void run() {
        while (true) {
            try {
                Station station;
                if ((station = CheatBreaker.getInstance().getRadioManager().getStation()) != null
                    && station.getStartTime() != null
                    && Duration.between(station.getStartTime(), LocalDateTime.now()).toMillis() / 1000L >= station.getDuration() + 2) {
                    station.loadPlaylist();
                    Thread.sleep(4000L);
                }

                Thread.sleep(1000L);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
