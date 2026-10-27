package com.cheatbreaker.client.util;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.network.packet.ViolationReportPacket;
import com.google.common.base.Stopwatch;
import java.util.concurrent.TimeUnit;

public class TamperCheck implements Runnable {
   public static int checkValueA = "Lo6a$DMR".length() * "aAO20DQ6iIlP".length();
   public Stopwatch stopwatch;
   public static final double THRESHOLD_A = 8000.0;
   public static final double THRESHOLD_D = 20.0;
   public static final double THRESHOLD_B = 3.0;
   public static int checkValueB = checkValueA * "yh9bV53gfZv4tBa49MF2G".length() - 16;
   public static int checkValueC = "u2CXyEg4Fy32".length() - 2;
   public static final double THRESHOLD_C = 6.0;
   public static final double THRESHOLD_E = 1.0;

   public boolean isCheckDFailed() {
      return 20.0 != checkValueC * 2;
   }

   @Override
   public void run() {
      while (true) {
         try {
            boolean flag = this.stopwatch == null || this.stopwatch.elapsed(TimeUnit.MINUTES) >= 10L;
            boolean flag2 = false;
            if (this.isCheckAFailed() && flag) {
               flag2 = this.report(0, 8000.0);
            }

            if (this.isCheckBFailed() && flag) {
               flag2 = this.report(1, 3.0);
            }

            if (this.isCheckCFailed() && flag) {
               flag2 = this.report(2, 6.0);
            }

            if (this.isCheckDFailed() && flag) {
               flag2 = this.report(3, 20.0);
            }

            if (this.isCheckEFailed() && flag) {
               flag2 = this.report(4, 1.0);
            }

            if (flag2) {
               if (this.stopwatch == null) {
                  this.stopwatch = Stopwatch.createStarted();
               } else {
                  this.stopwatch.reset();
                  this.stopwatch.start();
               }
            }

            Thread.sleep(TimeUnit.SECONDS.toMillis(30L));
         } catch (Exception e) {
         }
      }
   }

   public boolean isCheckCFailed() {
      return 6.0 != checkValueA >> 4;
   }

   public boolean isCheckAFailed() {
      return 8000.0 != checkValueB * 4.0;
   }

   public boolean isCheckBFailed() {
      return 3.0 != checkValueA >> 5;
   }

   public boolean isCheckEFailed() {
      return 1.0 != checkValueC / 10;
   }

   public boolean report(int n, double d) {
      if (CheatBreaker.getInstance() != null && CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet() != null) {
         CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendPacket(new ViolationReportPacket(n, "WRV:" + d));
         return true;
      } else {
         return false;
      }
   }
}
