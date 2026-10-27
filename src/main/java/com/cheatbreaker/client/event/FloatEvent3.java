package com.cheatbreaker.client.event;

public class FloatEvent3 extends CancellableEvent {
   public float floatValue;

   public float getFloatValue() {
      return this.floatValue;
   }

   public FloatEvent3(float f) {
      this.floatValue = f;
   }
}
