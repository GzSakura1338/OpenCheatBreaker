package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.mod.AbstractMod;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.theme.Theme;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public abstract class Panel extends SettingComponent {
   public int intValue = 0;
   public int intValue2 = 0;
   public boolean flag = false;
   public boolean flag2 = false;
   public double doubleValue = 0.0;
   public final boolean flag3 = false;
   public Theme theme2 = CheatBreaker.getInstance().getConfigManager().theme;
   public float floatValue;
   public int intValue3;
   public int intValue4;

   @Override
   public void render(int n, int n2, float f) {
   }

   @Override
   public void mouseScrolled(int n) {
      if (n != 0 && this.intValue2 >= this.height) {
         this.doubleValue += n / 2;
      }
   }

   public abstract void selectMod(AbstractMod abstractMod);

   public void process(int n, int n2) {
      this.flag = true;
      GlStateManager.popMatrix();
      boolean flag = this.intValue2 > this.height;
      if (this.flag2 && !Mouse.isButtonDown(0)) {
         this.flag2 = false;
      }

      double d = this.height - 10;
      double intValue2 = this.intValue2;
      double d2 = d / intValue2 * 100.0;
      double d3 = d / 100.0 * d2;
      double d4 = this.intValue / 100.0 * d2;
      if (flag) {
         int height = this.height;
         boolean flag2 = n > (this.x + this.width - 9) * this.scale
            && n < (this.x + this.width - 3) * this.scale
            && n2 > (this.y + 11 - d4) * this.scale
            && n2 < (this.y + 8 + d3 - d4) * this.scale;
         boolean flag3 = n > (this.x + this.width - 9) * this.scale
            && n < (this.x + this.width - 3) * this.scale
            && n2 > (this.y + 11) * this.scale
            && n2 < (this.y + 6 + d - 3.0) * this.scale;
         if (this.flag2) {
            if (this.intValue != this.floatValue && this.floatValue != d3 / 2.0 && this.floatValue != d3 / 2.0 + -this.intValue2 + height) {
               if (n2 > (this.y + 11 + d3 - d3 / 4.0 - d4) * this.scale) {
                  this.intValue = (int)(this.intValue - intValue2 / 70.0);
               } else if (n2 < (this.y + 11 + d3 / 4.0 - d4) * this.scale) {
                  this.intValue = (int)(this.intValue + intValue2 / 70.0);
               }

               this.floatValue = this.intValue;
            } else if (n2 > (this.y + 11 + d3 - d3 / 4.0 - d4) * this.scale || n2 < (this.y + 11 + d3 / 4.0 - d4) * this.scale) {
               this.floatValue = 1.0F;
            }
         }

         if (this.intValue < -this.intValue2 + height) {
            this.intValue = -this.intValue2 + height;
            this.doubleValue = 0.0;
         }

         if (this.intValue > 0) {
            this.intValue = 0;
            this.doubleValue = 0.0;
         }

         RenderUtils.drawRoundedRectFilledPrecise(
            this.x + this.width - 6,
            this.y + 11,
            this.x + this.width - 4,
            this.y + 6 + d - 3.0,
            2.0,
            flag3 && !flag2 ? this.theme2.getTrackHoverColorValue() : this.theme2.getTrackColorValue2()
         );
         RenderUtils.drawRoundedRectFilledPrecise(
            this.x + this.width - 7,
            this.y + 11 - d4,
            this.x + this.width - 3,
            this.y + 8 + d3 - d4,
            4.0,
            !flag2 && !this.flag2 ? this.theme2.getThumbColorValue2() : this.theme2.getThumbHoverColorValue()
         );
      }

      if (!flag && this.intValue != 0) {
         this.intValue = 0;
      }
   }

   public Panel(float f, int n, int n2, int n3, int n4) {
      super(f);
      this.intValue3 = n;
      this.intValue4 = n2;
      this.x = n;
      this.y = n2;
      this.width = n3;
      this.height = n4;
   }

   public void process2(int n, int n2) {
      if (this.isMouseOver(n, n2)) {
         double round = Math.round(this.doubleValue / 25.0);
         this.doubleValue -= round;
         if (this.doubleValue != 0.0) {
            this.intValue = (int)(this.intValue + round);
         }
      } else {
         this.doubleValue = 0.0;
      }

      if (this.flag) {
         if (this.intValue < -this.intValue2 + this.height) {
            this.intValue = -this.intValue2 + this.height;
            this.doubleValue = 0.0;
         }

         if (this.intValue > 0) {
            this.intValue = 0;
            this.doubleValue = 0.0;
         }
      }

      GlStateManager.pushMatrix();
      GL11.glTranslatef(0.0F, this.intValue, 0.0F);
   }

   @Override
   public void mouseClicked(int n, int n2, int n3) {
      double d = this.height - 10;
      double intValue2 = this.intValue2;
      double d2 = d / intValue2 * 100.0;
      double d3 = d / 100.0 * d2;
      double d4 = this.intValue / 100.0 * d2;
      boolean flag = n > (this.x + this.width - 9) * this.scale
         && n < (this.x + this.width - 3) * this.scale
         && n2 > (this.y + 11 - d4) * this.scale
         && n2 < (this.y + 8 + d3 - d4) * this.scale;
      boolean flag2 = n > (this.x + this.width - 9) * this.scale
         && n < (this.x + this.width - 3) * this.scale
         && n2 > (this.y + 11) * this.scale
         && n2 < (this.y + 6 + d - 3.0) * this.scale;
      if (n3 == 0 && flag2 || flag) {
         this.flag2 = true;
      }
   }

   public abstract boolean hasSettings(AbstractMod abstractMod);
}
