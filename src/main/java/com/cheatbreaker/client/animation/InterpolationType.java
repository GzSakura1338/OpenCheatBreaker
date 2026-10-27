package com.cheatbreaker.client.animation;

import com.cheatbreaker.client.util.Interpolations;
import net.minecraft.util.MathHelper;

public enum InterpolationType {
   CONSTANT {
      @Override
      public float interpolate(Keyframe keyframe, float f, Keyframe keyframe2) {
         return keyframe.value;
      }
   },
   LINEAR {
      @Override
      public float interpolate(Keyframe keyframe, float f, Keyframe keyframe2) {
         return Keyframe.lerp(keyframe.value, keyframe2.value, f);
      }
   },
   BEZIER {
      @Override
      public float interpolate(Keyframe keyframe, float f, Keyframe keyframe2) {
         if (f <= 0.0F) {
            return keyframe.value;
         }

         if (f >= 1.0F) {
            return keyframe2.value;
         }

         float f2 = keyframe2.frame - keyframe.frame;
         float f3 = keyframe2.value - keyframe.value;
         if (f3 == 0.0F) {
            f3 = 1.0E-5F;
         }

         float f4 = (keyframe.rightHandleX - keyframe.frame) / f2;
         float f5 = (keyframe.rightHandleY - keyframe.value) / f3;
         float f6 = (keyframe2.leftHandleX - keyframe.frame) / f2;
         float f7 = (keyframe2.leftHandleY - keyframe.value) / f3;
         float f8 = 5.0E-4F;
         f8 = f3 == 0.0F ? f8 : Math.max(Math.min(f8, 1.0F / f3 * f8), 1.0E-5F);
         f4 = MathHelper.clamp_float(f4, 0.0F, 1.0F);
         f6 = MathHelper.clamp_float(f6, 0.0F, 1.0F);
         return Interpolations.bezier(0.0F, f5, f7, 1.0F, Interpolations.solveBezier(f4, f6, f, f8)) * f3 + keyframe.value;
      }
   };

   public static InterpolationType fromName(String str) {
      return Enum.valueOf(InterpolationType.class, str);
   }

   public abstract float interpolate(Keyframe keyframe, float f, Keyframe keyframe2);
}
