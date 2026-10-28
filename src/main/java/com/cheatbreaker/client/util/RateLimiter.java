package com.cheatbreaker.client.util;

import java.util.ArrayDeque;
import java.util.Deque;

public class RateLimiter {
    public int minInterval = -1;
    public double burstRate = -1.0;
    public int maxPerSecond = -1;
    public Deque<Long> timestamps = new ArrayDeque<>();
    public long lastTime = 0L;
    public static final double EPSILON = 1.0E-9;
    public double burstCount = 0.0;

    public boolean tryAcquire() {
        long currentTimeMillis = System.currentTimeMillis();
        if (this.minInterval >= 0 && currentTimeMillis - this.lastTime < this.minInterval) {
            return false;
        }

        if (this.burstRate >= 0.0 && this.burstCount > 2.0) {
            return false;
        }

        if (this.burstRate >= 0.0) {
            this.burstCount++;
        }

        if (this.maxPerSecond >= 0) {
            this.timestamps.removeIf(longValue -> currentTimeMillis - longValue >= 1000L);
            if (this.timestamps.size() >= this.maxPerSecond) {
                return false;
            }
        }

        return true;
    }

    public void decay() {
        long currentTimeMillis = System.currentTimeMillis();
        if (this.burstRate >= 0.0) {
            this.burstCount = Math.max(this.burstCount - (this.burstRate + 1.0E-9), 0.0);
        }

        if (this.maxPerSecond >= 0) {
            this.timestamps.removeIf(longValue -> currentTimeMillis - longValue >= 1000L);
        }
    }

    public int getMinInterval() {
        return this.minInterval;
    }

    public long getLastTime() {
        return this.lastTime;
    }

    public void setMaxPerSecond(int n) {
        this.maxPerSecond = n;
    }

    public void markUsed() {
        long currentTimeMillis = System.currentTimeMillis();
        if (this.minInterval >= 0 && currentTimeMillis - this.lastTime >= this.minInterval) {
            this.lastTime = currentTimeMillis;
        }

        if (this.maxPerSecond >= 0) {
            this.timestamps.add(currentTimeMillis);
        }
    }

    public void setLastTime(long l) {
        this.lastTime = l;
    }

    public int getMaxPerSecond() {
        return this.maxPerSecond;
    }

    public double getBurstCount() {
        return this.burstCount;
    }

    public double getBurstRate() {
        return this.burstRate;
    }

    public void setMinInterval(int n) {
        this.minInterval = n;
    }

    public Deque<Long> getTimestamps() {
        return this.timestamps;
    }

    public void setBurstCount(double d) {
        this.burstCount = d;
    }

    public void reset() {
        this.minInterval = -1;
        this.burstRate = -1.0;
        this.maxPerSecond = -1;
        this.timestamps.clear();
        this.lastTime = 0L;
        this.burstCount = 0.0;
    }

    public void setBurstRate(double d) {
        this.burstRate = d;
    }
}
