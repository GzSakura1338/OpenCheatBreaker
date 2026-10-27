package com.cheatbreaker.client.event;

public class DoubleEvent2 extends CancellableEvent {
   public double doubleValue;

   public DoubleEvent2(double d) {
      this.doubleValue = d;
   }

   public double getDoubleValue() {
      return this.doubleValue;
   }

   public void setDoubleValue(double d) {
      this.doubleValue = d;
   }
}
