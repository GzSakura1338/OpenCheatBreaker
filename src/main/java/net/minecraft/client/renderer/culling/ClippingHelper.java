package net.minecraft.client.renderer.culling;

public class ClippingHelper {
   public float[][] frustum = new float[6][4];
   public float[] projectionMatrix = new float[16];
   public float[] modelviewMatrix = new float[16];
   public float[] clippingMatrix = new float[16];
   public boolean disabled = false;

   public float dot(float[] floats, float f, float f2, float f3) {
      return floats[0] * f + floats[1] * f2 + floats[2] * f3 + floats[3];
   }

   public boolean isBoxInFrustum(double d, double d2, double d3, double d4, double d5, double d6) {
      if (this.disabled) {
         return true;
      }

      float f = (float)d;
      float f2 = (float)d2;
      float f3 = (float)d3;
      float f4 = (float)d4;
      float f5 = (float)d5;
      float f6 = (float)d6;

      for (int i = 0; i < 6; i++) {
         float[] floats = this.frustum[i];
         float f7 = floats[0];
         float f8 = floats[1];
         float f9 = floats[2];
         float f10 = floats[3];
         if (f7 * f + f8 * f2 + f9 * f3 + f10 <= 0.0F
            && f7 * f4 + f8 * f2 + f9 * f3 + f10 <= 0.0F
            && f7 * f + f8 * f5 + f9 * f3 + f10 <= 0.0F
            && f7 * f4 + f8 * f5 + f9 * f3 + f10 <= 0.0F
            && f7 * f + f8 * f2 + f9 * f6 + f10 <= 0.0F
            && f7 * f4 + f8 * f2 + f9 * f6 + f10 <= 0.0F
            && f7 * f + f8 * f5 + f9 * f6 + f10 <= 0.0F
            && f7 * f4 + f8 * f5 + f9 * f6 + f10 <= 0.0F) {
            return false;
         }
      }

      return true;
   }

   public boolean isBoxInFrustumFully(double d, double d2, double d3, double d4, double d5, double d6) {
      if (this.disabled) {
         return true;
      }

      float f = (float)d;
      float f2 = (float)d2;
      float f3 = (float)d3;
      float f4 = (float)d4;
      float f5 = (float)d5;
      float f6 = (float)d6;

      for (int i = 0; i < 6; i++) {
         float[] floats = this.frustum[i];
         float f7 = floats[0];
         float f8 = floats[1];
         float f9 = floats[2];
         float f10 = floats[3];
         if (i < 4) {
            if (f7 * f + f8 * f2 + f9 * f3 + f10 <= 0.0F
               || f7 * f4 + f8 * f2 + f9 * f3 + f10 <= 0.0F
               || f7 * f + f8 * f5 + f9 * f3 + f10 <= 0.0F
               || f7 * f4 + f8 * f5 + f9 * f3 + f10 <= 0.0F
               || f7 * f + f8 * f2 + f9 * f6 + f10 <= 0.0F
               || f7 * f4 + f8 * f2 + f9 * f6 + f10 <= 0.0F
               || f7 * f + f8 * f5 + f9 * f6 + f10 <= 0.0F
               || f7 * f4 + f8 * f5 + f9 * f6 + f10 <= 0.0F) {
               return false;
            }
         } else if (f7 * f + f8 * f2 + f9 * f3 + f10 <= 0.0F
            && f7 * f4 + f8 * f2 + f9 * f3 + f10 <= 0.0F
            && f7 * f + f8 * f5 + f9 * f3 + f10 <= 0.0F
            && f7 * f4 + f8 * f5 + f9 * f3 + f10 <= 0.0F
            && f7 * f + f8 * f2 + f9 * f6 + f10 <= 0.0F
            && f7 * f4 + f8 * f2 + f9 * f6 + f10 <= 0.0F
            && f7 * f + f8 * f5 + f9 * f6 + f10 <= 0.0F
            && f7 * f4 + f8 * f5 + f9 * f6 + f10 <= 0.0F) {
            return false;
         }
      }

      return true;
   }
}
