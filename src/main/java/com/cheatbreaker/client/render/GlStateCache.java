package com.cheatbreaker.client.render;

import net.minecraft.client.renderer.OpenGlHelper;
import org.lwjgl.opengl.GL11;

public class GlStateCache {
   public static ColorState colorState = new ColorState();
   public static BlendState blendState = new BlendState();
   public static int intValue = 7425;

   public static void process(float f, float f2, float f3, float f4) {
      if (f != colorState.floatValue || f2 != colorState.floatValue2 || f3 != colorState.floatValue3 || f4 != colorState.floatValue4) {
         colorState.floatValue = f;
         colorState.floatValue2 = f2;
         colorState.floatValue3 = f3;
         colorState.floatValue4 = f4;
         process(f, f2, f3, f4);
      }
   }

   public static void run2() {
      blendState.glCapability.run2();
   }

   public static void process(int n) {
      if (n != intValue) {
         intValue = n;
         GL11.glShadeModel(n);
      }
   }

   public static void process(int n, int n2) {
      if (n != blendState.intValue || n2 != blendState.intValue2) {
         blendState.intValue = n;
         blendState.intValue2 = n2;
         GL11.glBlendFunc(n, n2);
      }
   }

   public static void process(int n, int n2, int n3, int n4) {
      if (n != blendState.intValue || n2 != blendState.intValue2 || n3 != blendState.intValue3 || n4 != blendState.intValue4) {
         blendState.intValue = n;
         blendState.intValue2 = n2;
         blendState.intValue3 = n3;
         blendState.intValue4 = n4;
         OpenGlHelper.glBlendFunc(n, n2, n3, n4);
      }
   }

   public static void run() {
      blendState.glCapability.run();
   }
}
