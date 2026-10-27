package com.cheatbreaker.client.event;

public class FloatEvent4 extends CancellableEvent {
   public float floatValue1;
   public float floatValue2;
   public float floatValue3;
   public float floatValue4;

   public float getFloatValue4() {
      return this.floatValue4;
   }

   public FloatEvent4(float f, float f2, float f3, float f4) {
      this.floatValue1 = f;
      this.floatValue2 = f2;
      this.floatValue3 = f3;
      this.floatValue4 = f4;
   }

   public void setFloatValue3(float f) {
      this.floatValue3 = f;
   }

   public void setFloatValue2(float f) {
      this.floatValue2 = f;
   }

   public float getFloatValue3() {
      return this.floatValue3;
   }

   public float getFloatValue1() {
      return this.floatValue1;
   }

   public void setFloatValue1(float f) {
      this.floatValue1 = f;
   }

   public float getFloatValue2() {
      return this.floatValue2;
   }

   public void setFloatValue4(float f) {
      this.floatValue4 = f;
   }
}
