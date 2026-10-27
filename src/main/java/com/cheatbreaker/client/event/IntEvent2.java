package com.cheatbreaker.client.event;

public class IntEvent2 extends CancellableEvent {
   public int intValue;

   public int getIntValue() {
      return this.intValue;
   }

   public IntEvent2(int n) {
      this.intValue = n;
   }
}
