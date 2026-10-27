package com.cheatbreaker.client.event;

public class FloatEvent10 extends CancellableEvent {
   public float floatValue;

   public float getFloatValue() {
      return this.floatValue;
   }

   public FloatEvent10(float f) {
      this.floatValue = f;
   }

   public void setFloatValue(float f) {
      this.floatValue = f;
   }
}
