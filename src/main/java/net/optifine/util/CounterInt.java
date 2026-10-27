package net.optifine.util;

public class CounterInt {
   public int startValue;
   public int value;

   public CounterInt(int n) {
      this.startValue = n;
      this.value = n;
   }

   public synchronized int nextValue() {
      return this.value++;
   }

   public synchronized void reset() {
      this.value = this.startValue;
   }

   public int getValue() {
      return this.value;
   }
}
