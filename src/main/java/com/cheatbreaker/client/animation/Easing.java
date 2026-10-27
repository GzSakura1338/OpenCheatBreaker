package com.cheatbreaker.client.animation;

import com.cheatbreaker.client.util.Interpolations;

public enum Easing {
   LINEAR("LINEAR", 0, "linear") {
      @Override
      public float computeFloat(float f, float f2, float f3) {
         return Interpolations.lerp(f, f2, f3);
      }
   },
   QUAD_IN("QUAD_IN", 1, "quad_in") {
      @Override
      public float computeFloat(float f, float f2, float f3) {
         return f + (f2 - f) * f3 * f3;
      }
   },
   QUAD_OUT("QUAD_OUT", 2, "quad_out") {
      @Override
      public float computeFloat(float f, float f2, float f3) {
         return f - (f2 - f) * f3 * (f3 - 2.0F);
      }
   },
   QUAD_INOUT("QUAD_INOUT", 3, "quad_inout") {
      @Override
      public float computeFloat(float f, float f2, float f3) {
         f3 *= 2.0F;
         if (f3 < 1.0F) {
            return f + (f2 - f) / 2.0F * f3 * f3;
         }

         f3--;
         return f - (f2 - f) / 2.0F * (f3 * (f3 - 2.0F) - 1.0F);
      }
   },
   CUBIC_IN("CUBIC_IN", 4, "cubic_in") {
      @Override
      public float computeFloat(float f, float f2, float f3) {
         return f + (f2 - f) * f3 * f3 * f3;
      }
   },
   CUBIC_OUT("CUBIC_OUT", 5, "cubic_out") {
      @Override
      public float computeFloat(float f, float f2, float f3) {
         f3--;
         return f + (f2 - f) * (f3 * f3 * f3 + 1.0F);
      }
   },
   CUBIC_INOUT("CUBIC_INOUT", 6, "cubic_inout") {
      @Override
      public float computeFloat(float f, float f2, float f3) {
         f3 *= 2.0F;
         if (f3 < 1.0F) {
            return f + (f2 - f) / 2.0F * f3 * f3 * f3;
         }

         f3 -= 2.0F;
         return f + (f2 - f) / 2.0F * (f3 * f3 * f3 + 2.0F);
      }
   },
   EXP_IN("EXP_IN", 7, "exp_in") {
      @Override
      public float computeFloat(float f, float f2, float f3) {
         return f + (f2 - f) * (float)Math.pow(2.0, 10.0F * (f3 - 1.0F));
      }
   },
   EXP_OUT("EXP_OUT", 8, "exp_out") {
      @Override
      public float computeFloat(float f, float f2, float f3) {
         return f + (f2 - f) * (float)(-Math.pow(2.0, -10.0F * f3) + 1.0);
      }
   },
   EXP_INOUT("EXP_INOUT", 9, "exp_inout") {
      @Override
      public float computeFloat(float f, float f2, float f3) {
         if (f3 == 0.0F) {
            return f;
         }

         if (f3 == 1.0F) {
            return f2;
         }

         f3 *= 2.0F;
         if (f3 < 1.0F) {
            return f + (f2 - f) / 2.0F * (float)Math.pow(2.0, 10.0F * (f3 - 1.0F));
         }

         f3--;
         return f + (f2 - f) / 2.0F * (float)(-Math.pow(2.0, -10.0F * f3) + 2.0);
      }
   };

   public String text;

   public abstract float computeFloat(float f, float f2, float f3);

   Easing(String str, int n, String str2) {
      this.text = str2;
   }
}
