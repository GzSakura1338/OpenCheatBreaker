package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.mod.AbstractValueHudMod;
import com.cheatbreaker.client.mod.ValueRange;
import com.cheatbreaker.client.mod.setting.Setting;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.MathHelper;

public class FPSMod extends AbstractValueHudMod {
   public static String text = null;
   public Setting showMinimumFps;
   public static int intValue = 0;

   @Override
   public String getStaticText() {
      return Minecraft.getDebugFPS() + (this.showMinimumFps.getBooleanValue() ? "/" + this.getIntValue() : "");
   }

   @Override
   public ValueRange getHiddenValueRange() {
      return new ValueRange(0, 1000, 3000);
   }

   public FPSMod() {
      super("FPS");
      this.setDescription("Displays your frames per second.");
      this.handleString("[144 FPS]");
   }

   @Override
   public String getDynamicText() {
      return super.getDynamicText() == null ? null : this.getStaticText();
   }

   public int getIntValue() {
      if (Objects.equals(this.mc.debug, text)) {
         return intValue;
      }

      text = this.mc.debug;
      FrameTimer frameTimer = this.mc.getFrameTimer();
      long[] frames = frameTimer.getFrames();
      int index = frameTimer.getIndex();
      int lastIndex = frameTimer.getLastIndex();
      if (index == lastIndex) {
         return intValue;
      }

      int debugFPS = Minecraft.getDebugFPS();
      if (debugFPS <= 0) {
         debugFPS = 1;
      }

      long l = (long)(1.0 / debugFPS * 1.0E9);
      long l2 = 0L;

      for (int normalizeAngle = MathHelper.normalizeAngle(index - 1, frames.length);
         normalizeAngle != lastIndex && l2 < 1.0E9;
         normalizeAngle = MathHelper.normalizeAngle(normalizeAngle - 1, frames.length)
      ) {
         long l3 = frames[normalizeAngle];
         if (l3 > l) {
            l = l3;
         }

         l2 += l3;
      }

      double d = l / 1.0E9;
      intValue = (int)(1.0 / d);
      return intValue;
   }

   @Override
   public Number getNumericValue() {
      return Minecraft.getDebugFPS();
   }

   @Override
   public String getLabelText() {
      return "FPS";
   }

   @Override
   public void createGeneralOptions() {
      this.showMinimumFps = new Setting(this, "Show Minimum FPS").withHierarchyParents(this.generalOptions).changeValue(false);
      super.createGeneralOptions();
   }
}
