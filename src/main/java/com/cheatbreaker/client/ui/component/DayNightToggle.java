package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingType;
import com.cheatbreaker.client.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class DayNightToggle extends SettingComponent {
   public ResourceLocation sun64Png = new ResourceLocation("client/icons/sun-64.png");
   public ResourceLocation moon64Png = new ResourceLocation("client/icons/moon-64.png");
   public float floatValue = -1.0F;
   public boolean flag = false;
   public float floatValue2;
   public Setting setting2;

   @Override
   public void mouseClicked(int n, int n2, int n3) {
      short s = 170;
      boolean flag = n > (this.x + 170) * this.scale
         && n < (this.x + 170 + s - 2) * this.scale
         && n2 > (this.y + 4 + this.scrollOffsetY) * this.scale
         && n2 < (this.y + 20 + this.scrollOffsetY) * this.scale;
      if (n3 == 0 && flag) {
         this.flag = true;
      }
   }

   @Override
   public void render(int n, int n2, float f) {
      short s = 148;
      CheatBreaker.getInstance().getFontManager().ubuntuM16.drawString(this.setting2.getName().toUpperCase(), this.x + 10, this.y + 8, -1895825408);
      if (this.flag && !Mouse.isButtonDown(0)) {
         this.flag = false;
      }

      float f2 = this.x + 172 + s / 2.0F;
      float f3 = this.y - 2;
      CheatBreaker.getInstance().getFontManager().ubuntuM16.drawCenteredString("SERVER", f2, f3, -1895825408);
      Gui.drawRect(this.x + 172 + s / 2 - 0.5F, this.y + 8, this.x + 172 + s / 2 + 0.5F, this.y + 14, 1862270976);
      RenderUtils.drawTexture(this.moon64Png, this.x + 180 - 3.25F, this.y + 3, 7.5F, 7.5F);
      Gui.drawRect(this.x + 180 - 0.5F, this.y + 12, this.x + 180 + 0.5F, this.y + 14, 1862270976);
      RenderUtils.drawTexture(this.sun64Png, this.x + 170 + s - 10 - 5.0F, this.y + 2, 10.0F, 10.0F);
      Gui.drawRect(this.x + 170 + s - 10 - 0.5F, this.y + 12, this.x + 170 + s - 10 + 0.5F, this.y + 14, 1862270976);
      boolean flag = n > (this.x + 170) * this.scale
         && n < (this.x + 170 + s - 2) * this.scale
         && n2 > (this.y + 4 + this.scrollOffsetY) * this.scale
         && n2 < (this.y + 20 + this.scrollOffsetY) * this.scale;
      RenderUtils.drawRoundedRectFilledPrecise(this.x + 174, this.y + 16, this.x + 170 + s - 4, this.y + 18, 1.0, flag ? -1895825408 : 1862270976);
      double d = s - 18;
      if (this.setting2.getMinimumValue() != null && this.setting2.getMaximumValue() != null) {
         float parseFloat = Float.parseFloat("" + this.setting2.getMinimumValue());
         float parseFloat2 = Float.parseFloat("" + this.setting2.getMaximumValue());
         if (this.flag) {
            this.floatValue2 = (float)Math.round((parseFloat + (n - (this.x + 180) * this.scale) * ((parseFloat2 - parseFloat) / (d * this.scale))) * 100.0) / 100.0F;
            if (this.floatValue2 < -13490.0F && this.floatValue2 > -15490.0F) {
               this.floatValue2 = -14490.0F;
            }

            if (this.setting2.getType().equals(SettingType.INTEGER)) {
               this.floatValue2 = Math.round(this.floatValue2);
            }

            if (this.floatValue2 < parseFloat) {
               this.floatValue2 = parseFloat;
            } else if (this.floatValue2 > parseFloat2) {
               this.floatValue2 = parseFloat2;
            }

            switch (this.setting2.getType()) {
               case INTEGER:
                  this.setting2.changeValue(Integer.parseInt((int)this.floatValue2 + ""));
                  break;
               case FLOAT:
                  this.setting2.changeValue(this.floatValue2);
                  break;
               case DOUBLE:
                  this.setting2.changeValue(Double.parseDouble(this.floatValue2 + ""));
            }
         }

         float f4;
         float f5;
         f4 = (f4 = Float.parseFloat(this.setting2.getValue() + "")) < this.floatValue ? this.floatValue - f4 : (f5 = f4 - this.floatValue);
         float f6 = ((parseFloat2 - parseFloat) / 20.0F + f4 * 8.0F) / (Minecraft.getDebugFPS() + 1);
         if (f6 < 1.0E-4) {
            f6 = 1.0E-4F;
         }

         float f7;
         if (this.floatValue < (f7 = Float.parseFloat(this.setting2.getValue() + ""))) {
            this.floatValue = this.floatValue + f6 <= f7 ? (this.floatValue += f6) : f7;
         } else if (this.floatValue > f7) {
            this.floatValue = this.floatValue - f6 >= f7 ? (this.floatValue -= f6) : f7;
         }

         double d2 = 100.0F * ((this.floatValue - parseFloat) / (parseFloat2 - parseFloat));
         RenderUtils.drawRoundedRectFilledPrecise(this.x + 174, this.y + 16, this.x + 180 + d * d2 / 100.0, this.y + 18, 4.0, -12418828);
         GlStateManager.color(0.25F, 0.45F, 1.0F, 1.0F);
         RenderUtils.drawFilledCircle(this.x + 181.25F + d * d2 / 100.0, this.y + 17.25F, 4.5);
         if (this.floatValue == -14490.0F) {
            GlStateManager.color(0.25F, 0.45F, 1.0F, 1.0F);
         } else {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         }

         RenderUtils.drawFilledCircle(this.x + 181.25F + d * d2 / 100.0, this.y + 17.25F, 2.7F);
      }
   }

   public DayNightToggle(Setting setting, float f) {
      super(f);
      this.setting2 = setting;
      this.height = 22;
      this.floatValue = Float.parseFloat("" + setting.getValue());
   }
}
