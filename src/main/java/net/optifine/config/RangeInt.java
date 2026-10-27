package net.optifine.config;

public class RangeInt {
   public int min;
   public int max;

   public RangeInt(int n, int n2) {
      this.min = Math.min(n, n2);
      this.max = Math.max(n, n2);
   }

   public boolean isInRange(int n) {
      return n < this.min ? false : n <= this.max;
   }

   public int getMin() {
      return this.min;
   }

   public int getMax() {
      return this.max;
   }

   @Override
   public String toString() {
      return "min: " + this.min + ", max: " + this.max;
   }
}
