package com.cheatbreaker.client.event;

public class DoubleEvent1 extends CancellableEvent {
   public double doubleValue;

   public double getDoubleValue() {
      return this.doubleValue;
   }

   public DoubleEvent1(double d) {
      this.doubleValue = d;
   }

   public void setDoubleValue(double d) {
      this.doubleValue = d;
   }
}
