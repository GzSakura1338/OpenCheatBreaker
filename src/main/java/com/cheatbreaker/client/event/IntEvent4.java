package com.cheatbreaker.client.event;

public class IntEvent4 extends Event {
   public int intValue;

   public int getIntValue() {
      return this.intValue;
   }

   public IntEvent4(int n) {
      this.intValue = n;
   }
}
