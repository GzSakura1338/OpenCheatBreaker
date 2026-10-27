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
   public long counter;
   public long lastSyncHRClock;

   public Timer(float tps) {
      this.ticksPerSecond = tps;
      this.lastSyncSysClock = Minecraft.getSystemTime();
      this.lastSyncHRClock = System.nanoTime() / 1000000L;
   }

   public void updateTimer() {
      long systemTime = Minecraft.getSystemTime();
      long l = systemTime - this.lastSyncSysClock;
      long l2 = System.nanoTime() / 1000000L;
      double d = l2 / 1000.0;
      if (l <= 1000L && l >= 0L) {
         this.counter += l;
         if (this.counter > 1000L) {
            long l3 = l2 - this.lastSyncHRClock;
            double d2 = (double)this.counter / l3;
            this.timeSyncAdjustment = this.timeSyncAdjustment + (d2 - this.timeSyncAdjustment) * 0.2F;
            this.lastSyncHRClock = l2;
            this.counter = 0L;
         }

         if (this.counter < 0L) {
            this.lastSyncHRClock = l2;
         }
      } else {
         this.lastHRTime = d;
      }

      this.lastSyncSysClock = systemTime;
      double d2 = (d - this.lastHRTime) * this.timeSyncAdjustment;
      this.lastHRTime = d;
      d2 = MathHelper.clamp_double(d2, 0.0, 1.0);
      this.elapsedPartialTicks = (float)(this.elapsedPartialTicks + d2 * this.timerSpeed * this.ticksPerSecond);
      this.elapsedTicks = (int)this.elapsedPartialTicks;
      this.elapsedPartialTicks = this.elapsedPartialTicks - this.elapsedTicks;
      if (this.elapsedTicks > 10) {
         this.elapsedTicks = 10;
      }

      this.renderPartialTicks = this.elapsedPartialTicks;
   }
}
