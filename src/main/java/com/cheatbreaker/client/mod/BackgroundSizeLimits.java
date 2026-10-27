package com.cheatbreaker.client.mod;

public class BackgroundSizeLimits {
   public float minHeight;
   public float defaultHeight;
   public float maxHeight;
   public float minWidth;
   public float defaultWidth;
   public float maxWidth;

   public float getDefaultHeight() {
      return this.defaultHeight;
   }

   public float getDefaultWidth() {
      return this.defaultWidth;
   }

   public float getMaxWidth() {
      return this.maxWidth;
   }

   public BackgroundSizeLimits(float f, float f2, float f3, float f4, float f5, float f6) {
      this.minHeight = f;
      this.defaultHeight = f2;
      this.maxHeight = f3;
      this.minWidth = f4;
      this.defaultWidth = f5;
      this.maxWidth = f6;
   }

   public float getMinHeight() {
      return this.minHeight;
   }

   public float getMinWidth() {
      return this.minWidth;
   }

   public float getMaxHeight() {
      return this.maxHeight;
   }
}
