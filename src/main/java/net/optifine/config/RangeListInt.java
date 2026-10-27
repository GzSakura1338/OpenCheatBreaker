package net.optifine.config;

import net.optifine.Config;

public class RangeListInt {
   public RangeInt[] ranges = new RangeInt[0];

   public RangeListInt() {
   }

   public RangeListInt(RangeInt rangeInt) {
      this.addRange(rangeInt);
   }

   public void addRange(RangeInt rangeInt) {
      this.ranges = (RangeInt[])Config.addObjectToArray(this.ranges, rangeInt);
   }

   public boolean isInRange(int n) {
      for (int i = 0; i < this.ranges.length; i++) {
         RangeInt rangeInt = this.ranges[i];
         if (rangeInt.isInRange(n)) {
            return true;
         }
      }

      return false;
   }

   public int getCountRanges() {
      return this.ranges.length;
   }

   public RangeInt getRange(int n) {
      return this.ranges[n];
   }

   @Override
   public String toString() {
      StringBuffer sb = new StringBuffer();
      sb.append("[");

      for (int i = 0; i < this.ranges.length; i++) {
         RangeInt rangeInt = this.ranges[i];
         if (i > 0) {
            sb.append(", ");
         }

         sb.append(rangeInt.toString());
      }

      sb.append("]");
      return sb.toString();
   }
}
