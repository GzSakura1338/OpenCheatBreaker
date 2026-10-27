package com.cheatbreaker.client.event;

public class FloatEvent7 extends CancellableEvent {
   public float floatValue1;
   public float floatValue2;

   public FloatEvent7(float f, float f2) {
      this.floatValue1 = f;
      this.floatValue2 = f2;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public float getFloatValue1() {
      return this.floatValue1;
   }
}
