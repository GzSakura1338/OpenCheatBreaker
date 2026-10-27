package com.cheatbreaker.client.mod.impl;

public class FoodValue {
   public int intValue;
   public float floatValue;

   public float getFloatValue() {
      return this.intValue * this.floatValue * 2.0F;
   }

   @Override
   public int hashCode() {
      int intValue = this.intValue;
      return 31 * intValue + (this.floatValue != 0.0F ? Float.floatToIntBits(this.floatValue) : 0);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof FoodValue)) {
         return false;
      }

      FoodValue foodValue = (FoodValue)obj;
      return this.intValue == foodValue.intValue && Float.compare(foodValue.floatValue, this.floatValue) == 0;
   }

   public FoodValue(int n, float f) {
      this.intValue = n;
      this.floatValue = f;
   }
}
