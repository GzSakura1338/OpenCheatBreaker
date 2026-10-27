package net.optifine.shaders.config;

public class RenderScale {
   public float scale = 1.0F;
   public float offsetX = 0.0F;
   public float offsetY = 0.0F;

   public RenderScale(float f, float f2, float f3) {
      this.scale = f;
      this.offsetX = f2;
      this.offsetY = f3;
   }

   public float getScale() {
      return this.scale;
   }

   public float getOffsetX() {
      return this.offsetX;
   }

   public float getOffsetY() {
      return this.offsetY;
   }

   @Override
   public String toString() {
      return "" + this.scale + ", " + this.offsetX + ", " + this.offsetY;
   }
}
