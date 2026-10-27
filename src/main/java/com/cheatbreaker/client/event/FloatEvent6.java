package com.cheatbreaker.client.event;

public class FloatEvent6 extends Event {
   public float partialTicks;

   public float getPartialTicks() {
      return this.partialTicks;
   }

   public FloatEvent6(float f) {
      this.partialTicks = f;
   }
}
