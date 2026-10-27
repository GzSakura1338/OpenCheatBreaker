package com.cheatbreaker.client.mod;

public class ValueRange {
   public int min;
   public int defaultValue;
   public int max;

   public int getMax() {
      return this.max;
   }

   public int getMin() {
      return this.min;
   }

   public ValueRange(int n, int n2, int n3) {
      this.min = n;
      this.defaultValue = n2;
      this.max = n3;
   }

   public int getDefaultValue() {
      return this.defaultValue;
   }
}
