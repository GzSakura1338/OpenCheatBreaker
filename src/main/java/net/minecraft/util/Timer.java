package net.minecraft.util;

import net.minecraft.client.Minecraft;

public class Timer {
    public float timerSpeed = 1.0F;
    public double lastHRTime;
    public int elapsedTicks;
    public float renderPartialTicks;
    public double timeSyncAdjustment = 1.0;
    public float elapsedPartialTicks;
    public float ticksPerSecond;
    public long lastSyncSysClock;
    public long longValue;
    public long lastSyncHRClock;

    public Timer(float f) {
        this.ticksPerSecond = f;
        this.lastSyncSysClock = Minecraft.getSystemTime();
        this.lastSyncHRClock = System.nanoTime() / 1000000L;
    }

    public void updateTimer() {
        long systemTime = Minecraft.getSystemTime();
        long l = systemTime - this.lastSyncSysClock;
        long l2 = System.nanoTime() / 1000000L;
        double d = l2 / 1000.0;
        if (l <= 1000L && l >= 0L) {
            this.longValue += l;
            if (this.longValue > 1000L) {
                long l3 = l2 - this.lastSyncHRClock;
                double d2 = (double)this.longValue / l3;
                this.timeSyncAdjustment = this.timeSyncAdjustment + (d2 - this.timeSyncAdjustment) * 0.2F;
                this.lastSyncHRClock = l2;
                this.longValue = 0L;
            }

            if (this.longValue < 0L) {
                this.lastSyncHRClock = l2;
            }
        } else {
            this.lastHRTime = d;
        }

        this.lastSyncSysClock = systemTime;
        double d2 = (d - this.lastHRTime) * this.timeSyncAdjustment;
        this.lastHRTime = d;
        if (d2 < 0.0) {
            d2 = 0.0;
        }

        if (d2 > 1.0) {
            d2 = 1.0;
        }

        this.elapsedPartialTicks = (float)(this.elapsedPartialTicks + d2 * this.timerSpeed * this.ticksPerSecond);
        this.elapsedTicks = (int)this.elapsedPartialTicks;
        this.elapsedPartialTicks = this.elapsedPartialTicks - this.elapsedTicks;
        if (this.elapsedTicks > 10) {
            this.elapsedTicks = 10;
        }

        this.renderPartialTicks = this.elapsedPartialTicks;
    }
}
