package net.minecraft.client.util;

import com.google.gson.JsonObject;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.JsonUtils;
import org.lwjgl.opengl.GL14;

public class JsonBlendingMode {
   public static JsonBlendingMode activeBlendState = null;
   public boolean separateBlend;
   public int srcRgb;
   public int dstRgb;
   public int srcAlpha;
   public int dstAlpha;
   public boolean blendDisabled;
   public int func;

   public JsonBlendingMode(boolean flag, boolean flag2, int n, int n2, int n3, int n4, int n5) {
      this.separateBlend = flag;
      this.srcRgb = n;
      this.dstRgb = n2;
      this.srcAlpha = n3;
      this.dstAlpha = n4;
      this.blendDisabled = flag2;
      this.func = n5;
   }

   public JsonBlendingMode() {
      this(false, true, 1, 0, 1, 0, 32774);
   }

   public JsonBlendingMode(int n, int n2, int n3) {
      this(false, false, n, n2, n, n2, n3);
   }

   public JsonBlendingMode(int n, int n2, int n3, int n4, int n5) {
      this(true, false, n, n2, n3, n4, n5);
   }

   public void enable() {
      if (!this.equals(activeBlendState)) {
         if (activeBlendState == null || this.blendDisabled != activeBlendState.isBlendDisabled()) {
            activeBlendState = this;
            if (this.blendDisabled) {
               GlStateManager.disableBlend();
               return;
            }

            GlStateManager.enableBlend();
         }

         GL14.glBlendEquation(this.func);
         if (this.separateBlend) {
            GlStateManager.tryBlendFuncSeparate(this.srcRgb, this.dstRgb, this.srcAlpha, this.dstAlpha);
         } else {
            GlStateManager.blendFunc(this.srcRgb, this.dstRgb);
         }
      }
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (!(obj instanceof JsonBlendingMode)) {
         return false;
      }

      JsonBlendingMode jsonBlendingMode = (JsonBlendingMode)obj;
      return this.func != jsonBlendingMode.func
         ? false
         : (
            this.dstAlpha != jsonBlendingMode.dstAlpha
               ? false
               : (
                  this.dstRgb != jsonBlendingMode.dstRgb
                     ? false
                     : (
                        this.blendDisabled != jsonBlendingMode.blendDisabled
                           ? false
                           : (this.separateBlend != jsonBlendingMode.separateBlend ? false : (this.srcAlpha != jsonBlendingMode.srcAlpha ? false : this.srcRgb == jsonBlendingMode.srcRgb))
                     )
               )
         );
   }

   @Override
   public int hashCode() {
      int srcRgb = this.srcRgb;
      srcRgb = 31 * srcRgb + this.srcAlpha;
      srcRgb = 31 * srcRgb + this.dstRgb;
      srcRgb = 31 * srcRgb + this.dstAlpha;
      srcRgb = 31 * srcRgb + this.func;
      srcRgb = 31 * srcRgb + (this.separateBlend ? 1 : 0);
      return 31 * srcRgb + (this.blendDisabled ? 1 : 0);
   }

   public boolean isBlendDisabled() {
      return this.blendDisabled;
   }

   public static JsonBlendingMode deserializeBlendState(JsonObject jsonObject) {
      if (jsonObject == null) {
         return new JsonBlendingMode();
      }

      int n = 32774;
      int n2 = 1;
      int n3 = 0;
      int n4 = 1;
      int n5 = 0;
      boolean flag = true;
      boolean flag2 = false;
      if (JsonUtils.isString(jsonObject, "func")) {
         n = getFuncFromString(jsonObject.get("func").getAsString());
         if (n != 32774) {
            flag = false;
         }
      }

      if (JsonUtils.isString(jsonObject, "srcrgb")) {
         n2 = getComponentFromString(jsonObject.get("srcrgb").getAsString());
         if (n2 != 1) {
            flag = false;
         }
      }

      if (JsonUtils.isString(jsonObject, "dstrgb")) {
         n3 = getComponentFromString(jsonObject.get("dstrgb").getAsString());
         if (n3 != 0) {
            flag = false;
         }
      }

      if (JsonUtils.isString(jsonObject, "srcalpha")) {
         n4 = getComponentFromString(jsonObject.get("srcalpha").getAsString());
         if (n4 != 1) {
            flag = false;
         }

         flag2 = true;
      }

      if (JsonUtils.isString(jsonObject, "dstalpha")) {
         n5 = getComponentFromString(jsonObject.get("dstalpha").getAsString());
         if (n5 != 0) {
            flag = false;
         }

         flag2 = true;
      }

      return flag ? new JsonBlendingMode() : (flag2 ? new JsonBlendingMode(n2, n3, n4, n5, n) : new JsonBlendingMode(n2, n3, n));
   }

   public static int getFuncFromString(String str) {
      String toLowerCase = str.trim().toLowerCase();
      return toLowerCase.equals("add")
         ? 32774
         : (
            toLowerCase.equals("subtract")
               ? 32778
               : (
                  toLowerCase.equals("reversesubtract")
                     ? 32779
                     : (toLowerCase.equals("reverse_subtract") ? 32779 : (toLowerCase.equals("min") ? 32775 : (toLowerCase.equals("max") ? 32776 : 32774)))
               )
         );
   }

   public static int getComponentFromString(String str) {
      String toLowerCase = str.trim().toLowerCase();
      toLowerCase = toLowerCase.replaceAll("_", "");
      toLowerCase = toLowerCase.replaceAll("one", "1");
      toLowerCase = toLowerCase.replaceAll("zero", "0");
      toLowerCase = toLowerCase.replaceAll("minus", "-");
      return toLowerCase.equals("0")
         ? 0
         : (
            toLowerCase.equals("1")
               ? 1
               : (
                  toLowerCase.equals("srccolor")
                     ? 768
                     : (
                        toLowerCase.equals("1-srccolor")
                           ? 769
                           : (
                              toLowerCase.equals("dstcolor")
                                 ? 774
                                 : (
                                    toLowerCase.equals("1-dstcolor")
                                       ? 775
                                       : (
                                          toLowerCase.equals("srcalpha")
                                             ? 770
                                             : (toLowerCase.equals("1-srcalpha") ? 771 : (toLowerCase.equals("dstalpha") ? 772 : (toLowerCase.equals("1-dstalpha") ? 773 : -1)))
                                       )
                                 )
                           )
                     )
               )
         );
   }
}
