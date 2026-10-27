package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.IntEvent2;
import com.cheatbreaker.client.event.IntEvent6;
import com.cheatbreaker.client.event.SimpleEvent3;
import com.cheatbreaker.client.event.SimpleEvent34;
import com.cheatbreaker.client.util.RateLimiter;

public class ClickRateLimiter {
   public RateLimiter leftClickLimiter = new RateLimiter();
   public RateLimiter rightClickLimiter = new RateLimiter();

   public void onIntEvent2(IntEvent2 intEvent2) {
      if (!intEvent2.isFlag()) {
         if (!this.getLimiter(intEvent2.getIntValue()).tryAcquire()) {
            intEvent2.setFlag(true);
         }
      }
   }

   public void onIntEvent6(IntEvent6 intEvent6) {
      this.getLimiter(intEvent6.getIntValue()).markUsed();
   }

   public ClickRateLimiter() {
      CheatBreaker.getInstance().getEventBus().checkClass2(IntEvent6.class, this::onIntEvent6);
      CheatBreaker.getInstance().getEventBus().checkClass2(SimpleEvent34.class, this::onSimpleEvent34);
      CheatBreaker.getInstance().getEventBus().checkClass2(IntEvent2.class, this::onIntEvent2);
      CheatBreaker.getInstance().getEventBus().checkClass2(SimpleEvent3.class, this::onSimpleEvent3);
   }

   public RateLimiter getLimiter(int n) {
      return n == 0 ? this.leftClickLimiter : this.rightClickLimiter;
   }

   public void onSimpleEvent34(SimpleEvent34 simpleEvent34) {
      this.leftClickLimiter.decay();
      this.rightClickLimiter.decay();
   }

   public void onSimpleEvent3(SimpleEvent3 simpleEvent3) {
      this.leftClickLimiter.reset();
      this.rightClickLimiter.reset();
   }
}
