package com.cheatbreaker.client.mod.impl;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.IResourcePackEvent;
import com.cheatbreaker.client.event.SimpleEvent49;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.mod.ModCategory;
import com.cheatbreaker.client.mod.setting.Setting;
import com.google.common.base.Throwables;
import net.minecraft.client.Minecraft;
import net.minecraft.client.shader.Shader;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderUniform;
import net.minecraft.util.ResourceLocation;
import net.optifine.Config;
import org.lwjgl.opengl.GL11;

public class MotionBlurMod extends AbstractMod {
   public int intValue = 0;
   public int intValue2 = 0;
   public int intValue3 = 0;
   public Setting blurType;
   public Setting applyInMenus;
   public Setting amount;
   public Setting color;
   public long longValue;

   public MotionBlurMod() {
      super("Motion Blur");
      this.setEnabledByDefault(false);
      if (Config.openGlVendor.contains("NVIDIA")) {
         this.blurType = new Setting(this, "Blur Type").changeValue("Accumulation").setStringValues("Phosphor", "Aggressive", "Accumulation");
      } else {
         this.blurType = new Setting(this, "Blur Type").changeValue("Aggressive").setStringValues("Phosphor", "Aggressive");
      }

      this.applyInMenus = new Setting(this, "Apply In Menus").changeValue(true).setCondition(() -> this.blurType.getValue().equals("Accumulation"));
      this.amount = new Setting(this, "Amount").setDescription("Change the amount of blur intensity.").changeValue(55.0F).setRange(0.0F, 100.0F).setUnit("%");
      this.color = new Setting(this, "Color")
         .setDescription("Change the color of the motion blur.")
         .changeValue(-1)
         .setRange(Integer.MIN_VALUE, Integer.MAX_VALUE)
         .setCondition(() -> this.blurType.getValue().equals("Phosphor"));
      this.setIcon(new ResourceLocation("client/icons/mods/swordblur.png"), 40, 40);
      this.setDescription("Blurs your surroundings while moving.");
      this.setAuthors("Fyu (Original)", "Moonsworth LLC (Newer shader)");
      this.setCategories(ModCategory.MISC);
      this.addEventHandler(ClientTickEvent.class, this::onClientTickEvent);
      this.addEventHandler(IResourcePackEvent.class, this::onIResourcePackEvent);
      this.addEventHandler(SimpleEvent49.class, this::onSimpleEvent49);
   }

   public void onClientTickEvent(ClientTickEvent clientTickEvent) {
      if (!this.blurType.getValue().equals("Accumulation")) {
         if (this.mc.currentScreen == null && !this.mc.entityRenderer.isShaderActive()) {
            this.run();
         }
      } else {
         if (!this.getDebugFPS() && this.mc.currentScreen == null) {
            this.intValue++;
            if (this.intValue > 500) {
               this.intValue = 0;
               this.intValue3 = 0;
               this.blurType.changeValue("Aggressive");
               CheatBreaker.getInstance()
                  .getModManager()
                  .notificationsMod
                  .handleString("Error", "Your framerate is too low! Switching to Aggressive Blur", 4000L);
            }
         } else if (this.applyInMenus.getBooleanValue() && !this.getDebugFPS() && this.mc.currentScreen != null) {
            this.intValue2++;
            if (this.intValue2 > 200) {
               this.intValue2 = 0;
               this.intValue3 = 0;
               this.applyInMenus.changeValue(false);
               CheatBreaker.getInstance()
                  .getModManager()
                  .notificationsMod
                  .handleString("Error", "Your framerate is too low! GUI Blur has been disabled.", 4000L);
            }
         } else if (this.getDebugFPS()) {
            this.intValue3++;
            if (this.intValue3 > 100) {
               this.intValue2 = 0;
               this.intValue = 0;
               this.intValue3 = 0;
            }
         }
      }
   }

   public float getFloatValue() {
      float f = this.amount.getFloatValue() * 0.9F;
      int debugFPS = Minecraft.getDebugFPS();
      return debugFPS > 200 ? f : (debugFPS > 120 ? 0.5F * f : 0.0F);
   }

   public void onIResourcePackEvent(IResourcePackEvent resourcePackEvent) {
      this.mc.entityRenderer.stopUseShader();
   }

   public float getFloatValue2() {
      if (Minecraft.getMinecraft().theWorld == null) {
         return 0.0F;
      }

      float f = this.getFloatValue() * 10.0F;
      if (Minecraft.getMinecraft().currentScreen == null) {
         this.longValue = System.currentTimeMillis();
         f = Math.min(f, 996.0F);
      } else {
         f = Math.min(f, 990.0F);
      }

      long l = System.currentTimeMillis() - this.longValue;
      return l > 10000L ? 0.0F : Math.max(f / 1000.0F, 0.0F);
   }

   public void run() {
      this.mc.entityRenderer.run2();
      ShaderGroup shaderGroup = Minecraft.getMinecraft().entityRenderer.getShaderGroup();

      try {
         if (this.mc.entityRenderer.isShaderActive() && this.mc.thePlayer != null) {
            for (Shader shader : shaderGroup.getListShaders()) {
               ShaderUniform shaderUniform = shader.getShaderManager().getShaderUniform("Phosphor");
               if (shaderUniform != null) {
                  float f = 1.0F - this.amount.getFloatValue() / 100.0F * 0.9F;
                  if (this.blurType.getValue().equals("Phosphor")) {
                     f = 0.7F + this.amount.getFloatValue() * 0.95F / 1000.0F * 3.0F - 0.01F;
                  }

                  int n = this.blurType.getValue().equals("Phosphor") ? this.color.getColorValue() : -1;
                  float f2 = (n >> 16 & 0xFF) / 255.0F;
                  float f3 = (n >> 8 & 0xFF) / 255.0F;
                  float f4 = (n & 0xFF) / 255.0F;
                  shaderUniform.set(f * f2, f * f3, f * f4);
               }
            }
         }
      } catch (IllegalArgumentException e) {
         Throwables.propagate(e);
      }
   }

   public void onSimpleEvent49(SimpleEvent49 simpleEvent49) {
      if (this.blurType.getValue().equals("Accumulation")) {
         if (this.applyInMenus.getBooleanValue() || this.mc.currentScreen == null) {
            float floatValue2 = this.getFloatValue2();
            GL11.glAccum(259, floatValue2);
            GL11.glAccum(256, 1.0F - floatValue2);
            GL11.glAccum(258, 1.0F);
         }
      }
   }

   public boolean getDebugFPS() {
      return Minecraft.getDebugFPS() > 120;
   }
}
