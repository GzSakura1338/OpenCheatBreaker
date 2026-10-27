package com.cheatbreaker.client.event;

public class FloatEvent2 extends CancellableEvent {
   public float eyeHeight;
   public float partialTicks;

   public void setEyeHeight(float f) {
      this.eyeHeight = f;
   }

   public float getEyeHeight() {
      return this.eyeHeight;
   }

   public float getPartialTicks() {
      return this.partialTicks;
   }

   public FloatEvent2(float f, float f2) {
      this.eyeHeight = f;
      this.partialTicks = f2;
   }
}
