package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.setting.Setting;
import com.cheatbreaker.client.mod.setting.SettingType;
import com.cheatbreaker.client.render.RenderUtils;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;

public class RangeComponent extends SettingComponent {
   public float floatValue;
   public float floatValue2 = -1.0F;
   public boolean flag = false;
   public Setting setting2;

   @Override
   public void render(int n, int n2, float f) {
      short s = 148;
      float f2 = s - 18;
      CheatBreaker.getInstance().getFontManager().ubuntuM16.drawString(this.setting2.getName().toUpperCase(), this.x + 10, this.y + 2, -1895825408);
      if (this.flag && !Mouse.isButtonDown(0)) {
         this.flag = false;
      }

      Gui.drawRect(this.x + 179.5F, this.y + 5.5F, this.x + 180.5F + f2, this.y + 8.5F, -822083584);

      for (int i = 0; i < f2; i++) {
         int n3 = Color.HSBtoRGB(i / f2, 1.0F, 1.0F);
         Gui.drawRect(this.x + 180 + i, this.y + 6, this.x + 181 + i, this.y + 8, n3);
      }

      float parseFloat = Float.parseFloat("" + this.setting2.getMinimumValue());
      float parseFloat2 = Float.parseFloat("" + this.setting2.getMaximumValue());
      if (this.flag) {
         this.floatValue = (float)((parseFloat + (n - (this.x + 180) * this.scale) * ((parseFloat2 - parseFloat) / (f2 * this.scale))) * 100.0) / 100.0F;
         if (this.setting2.getType().equals(SettingType.INTEGER)) {
            this.floatValue = Math.round(this.floatValue);
         }

         if (this.floatValue < parseFloat) {
            this.floatValue = parseFloat;
         } else if (this.floatValue > parseFloat2) {
            this.floatValue = parseFloat2;
         }

         switch (this.setting2.getType()) {
            case INTEGER:
               this.setting2.changeValue(Integer.parseInt((int)this.floatValue + ""));
               break;
            case FLOAT:
               this.setting2.changeValue(this.floatValue);
               break;
            case DOUBLE:
               this.setting2.changeValue(Double.parseDouble(this.floatValue + ""));
         }
      }

      float f3;
      float f4;
      f3 = (f3 = Float.parseFloat(this.setting2.getValue() + "")) < this.floatValue2 ? this.floatValue2 - f3 : (f4 = f3 - this.floatValue2);
      float f5 = ((parseFloat2 - parseFloat) / 20.0F + f3 * 8.0F) / (Minecraft.getDebugFPS() + 1);
      if (f5 < 1.0E-4) {
         f5 = 1.0E-4F;
      }

      float f6;
      if (this.floatValue2 < (f6 = Float.parseFloat(this.setting2.getValue() + ""))) {
         this.floatValue2 = this.floatValue2 + f5 <= f6 ? (this.floatValue2 += f5) : f6;
      } else if (this.floatValue2 > f6) {
         this.floatValue2 = this.floatValue2 - f5 >= f6 ? (this.floatValue2 -= f5) : f6;
      }

      double d = 100.0F * ((this.floatValue2 - parseFloat) / (parseFloat2 - parseFloat));
      GlStateManager.color(0.25F, 0.45F, 1.0F, 1.0F);
      RenderUtils.drawFilledCircle(this.x + 181.25F + f2 * d / 100.0, this.y + 7.25F, 4.5);
      float value = (Float)this.setting2.getValue();
      int n3 = value == 0.0F ? -1 : Color.HSBtoRGB(value, 1.0F, 1.0F);
      GlStateManager.color((n3 >> 16 & 0xFF) / 255.0F, (n3 >> 8 & 0xFF) / 255.0F, (n3 & 0xFF) / 255.0F, 1.0F);
      RenderUtils.drawFilledCircle(this.x + 181.25F + f2 * d / 100.0, this.y + 7.25F, 2.7F);
   }

   public RangeComponent(Setting setting, float f) {
      super(f);
      this.setting2 = setting;
      this.height = 14;
      this.floatValue2 = Float.parseFloat("" + setting.getValue());
   }

   @Override
   public void mouseClicked(int n, int n2, int n3) {
      short s = 170;
      boolean flag = n > (this.x + 170) * this.scale
         && n < (this.x + 170 + s - 2) * this.scale
         && n2 > (this.y + 4 + this.scrollOffsetY) * this.scale
         && n2 < (this.y + 10 + this.scrollOffsetY) * this.scale;
      if (n3 == 0 && flag) {
         this.flag = true;
      }
   }
}
