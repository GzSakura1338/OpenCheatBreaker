package com.cheatbreaker.client.util;

import net.minecraft.util.MathHelper;

public class Interpolations {
   public static double solveBezier(double d, double d2, double d3, double d4) {
      double d5 = d3;
      double bezier2 = bezier(0.0, d, d2, 1.0, d3);
      double copySign = Math.copySign(0.1F, d3 - bezier2);

      while (Math.abs(d3 - bezier2) > d4) {
         double d6 = copySign;
         bezier2 = bezier(0.0, d, d2, 1.0, d5 += copySign);
         if (Math.copySign(copySign, d3 - bezier2) != d6) {
            copySign *= -0.25;
         }
      }

      return d5;
   }

   public static double lerpYaw(double d, double d2, double d3) {
      d = MathHelper.wrapAngleTo180_double(d);
      d2 = MathHelper.wrapAngleTo180_double(d2);
      return lerp(d, normalizeAngle(d, d2), d3);
   }

   public static double lerp(double d, double d2, double d3) {
      return d + (d2 - d) * d3;
   }

   public static float solveBezier(float f, float f2, float f3) {
      return solveBezier(f, f2, f3, 5.0E-4F);
   }

   public static float solveBezier(float f, float f2, float f3, float f4) {
      float f5 = f3;
      float bezier2 = bezier(0.0F, f, f2, 1.0F, f3);
      float copySign = Math.copySign(0.1F, f3 - bezier2);

      while (Math.abs(f3 - bezier2) > f4) {
         float f6 = copySign;
         bezier2 = bezier(0.0F, f, f2, 1.0F, f5 += copySign);
         if (Math.copySign(copySign, f3 - bezier2) != f6) {
            copySign *= -0.25F;
         }
      }

      return f5;
   }

   public static double solveBezier(double d, double d2, float f) {
      return solveBezier(d, d2, f, 5.0E-4F);
   }

   public static double bezier(double d, double d2, double d3, double d4, double d5) {
      double lerp2 = lerp(d, d2, d5);
      double lerp3 = lerp(d2, d3, d5);
      double lerp4 = lerp(d3, d4, d5);
      double lerp5 = lerp(lerp2, lerp3, d5);
      double lerp6 = lerp(lerp3, lerp4, d5);
      return lerp(lerp5, lerp6, d5);
   }

   public static float lerpYaw(float f, float f2, float f3) {
      f = MathHelper.wrapAngleTo180_float(f);
      f2 = MathHelper.wrapAngleTo180_float(f2);
      return lerp(f, normalizeAngle(f, f2), f3);
   }

   public static double cubicHermite(double d, double d2, double d3, double d4, double d5) {
      double d6 = -0.5 * d + 1.5 * d2 - 1.5 * d3 + 0.5 * d4;
      double d7 = d - 2.5 * d2 + 2.0 * d3 - 0.5 * d4;
      double d8 = -0.5 * d + 0.5 * d3;
      return ((d6 * d5 + d7) * d5 + d8) * d5 + d2;
   }

   public static float normalizeAngle(float f, float f2) {
      float f3 = f - f2;
      if (!(f3 > 180.0F) && !(f3 < -180.0F)) {
         return f2;
      }

      f3 = Math.copySign(360.0F - Math.abs(f3), f3);
      return f + f3;
   }

   public static double cubicYaw(double d, double d2, double d3, double d4, double d5) {
      d = MathHelper.wrapAngleTo180_double(d);
      d2 = MathHelper.wrapAngleTo180_double(d2);
      d3 = MathHelper.wrapAngleTo180_double(d3);
      d4 = MathHelper.wrapAngleTo180_double(d4);
      d2 = normalizeAngle(d, d2);
      d3 = normalizeAngle(d2, d3);
      d4 = normalizeAngle(d3, d4);
      return cubic(d, d2, d3, d4, d5);
   }

   public static double normalizeAngle(double d, double d2) {
      double d3 = d - d2;
      if (!(d3 > 180.0) && !(d3 < -180.0)) {
         return d2;
      }

      d3 = Math.copySign(360.0 - Math.abs(d3), d3);
      return d + d3;
   }

   public static float cubicYaw(float f, float f2, float f3, float f4, float f5) {
      f = MathHelper.wrapAngleTo180_float(f);
      f2 = MathHelper.wrapAngleTo180_float(f2);
      f3 = MathHelper.wrapAngleTo180_float(f3);
      f4 = MathHelper.wrapAngleTo180_float(f4);
      f2 = normalizeAngle(f, f2);
      f3 = normalizeAngle(f2, f3);
      f4 = normalizeAngle(f3, f4);
      return cubic(f, f2, f3, f4, f5);
   }

   public static float cubic(float f, float f2, float f3, float f4, float f5) {
      float f6 = f4 - f3 - f + f2;
      float f7 = f - f2 - f6;
      float f8 = f3 - f;
      return ((f6 * f5 + f7) * f5 + f8) * f5 + f2;
   }

   public static double cubic(double d, double d2, double d3, double d4, double d5) {
      double d6 = d4 - d3 - d + d2;
      double d7 = d - d2 - d6;
      double d8 = d3 - d;
      return ((d6 * d5 + d7) * d5 + d8) * d5 + d2;
   }

   public static float bezier(float f, float f2, float f3, float f4, float f5) {
      float lerp2 = lerp(f, f2, f5);
      float lerp3 = lerp(f2, f3, f5);
      float lerp4 = lerp(f3, f4, f5);
      float lerp5 = lerp(lerp2, lerp3, f5);
      float lerp6 = lerp(lerp3, lerp4, f5);
      return lerp(lerp5, lerp6, f5);
   }

   public static double cubicHermiteYaw(float f, float f2, float f3, float f4, float f5) {
      f = MathHelper.wrapAngleTo180_float(f);
      f2 = MathHelper.wrapAngleTo180_float(f2);
      f3 = MathHelper.wrapAngleTo180_float(f3);
      f4 = MathHelper.wrapAngleTo180_float(f4);
      f2 = normalizeAngle(f, f2);
      f3 = normalizeAngle(f2, f3);
      f4 = normalizeAngle(f3, f4);
      return cubicHermite(f, f2, f3, f4, f5);
   }

   public static float lerp(float f, float f2, float f3) {
      return f + (f2 - f) * f3;
   }
}
