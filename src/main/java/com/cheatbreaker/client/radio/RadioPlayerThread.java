package com.cheatbreaker.client.radio;

import java.util.LinkedList;
import java.util.Queue;

public class RadioPlayerThread extends Thread {
    public Queue<Station> stationQueue = new LinkedList<>();

    @Override
    public void run() {
        try {
            while (true) {
                synchronized (this.stationQueue) {
                    this.stationQueue.wait();
                    Station poll = this.stationQueue.poll();
                    if (poll != null) {
                        poll.loadPlaylist();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void handleStation(Station station) {
        synchronized (this.stationQueue) {
            this.stationQueue.offer(station);
            this.stationQueue.notify();
        }
    }
}
