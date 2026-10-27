package com.cheatbreaker.client.event;

import net.minecraft.util.AxisAlignedBB;

public class AxisAlignedBBDoubleEvent extends CancellableEvent {
   public AxisAlignedBB axisAlignedBB;
   public double doubleValue1;
   public double doubleValue2;
   public double doubleValue3;
   public double doubleValue4;

   public double getDoubleValue2() {
      return this.doubleValue2;
   }

   public double getDoubleValue4() {
      return this.doubleValue4;
   }

   public AxisAlignedBB getAxisAlignedBB() {
      return this.axisAlignedBB;
   }

   public AxisAlignedBBDoubleEvent(AxisAlignedBB axisAlignedBB, double d, double d2, double d3, double d4) {
      this.axisAlignedBB = axisAlignedBB;
      this.doubleValue1 = d;
      this.doubleValue2 = d2;
      this.doubleValue3 = d3;
      this.doubleValue4 = d4;
   }

   public double getDoubleValue1() {
      return this.doubleValue1;
   }

   public double getDoubleValue3() {
      return this.doubleValue3;
   }
}
