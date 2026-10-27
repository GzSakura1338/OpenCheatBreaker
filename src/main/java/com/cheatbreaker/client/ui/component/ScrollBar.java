package com.cheatbreaker.client.ui.component;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ScrollBar extends UIElement {
   public float floatValue;
   public boolean flag;
   public double doubleValue;
   public float floatValue2;
   public boolean flag2;
   public float floatValue3;
   public UIElement uielement;
   public float floatValue4;
   public boolean flag3;

   public boolean check() {
      return this.floatValue4 > this.height;
   }

   @Override
   public void render(float f, float f2, boolean flag) {
      this.flag = true;
      GlStateManager.popMatrix();
      boolean check = this.check();
      if (this.flag2 && (!Mouse.isButtonDown(0) || !this.containsPoint(f, f2) || !flag)) {
         this.flag2 = false;
      }

      if (this.flag3 && !Mouse.isButtonDown(0)) {
         this.flag3 = false;
      }

      float height = this.height;
      float floatValue4 = this.floatValue4;
      float f3 = height / floatValue4 * 100.0F;
      float f4 = height / 100.0F * f3;
      float f5 = this.floatValue3 / 100.0F * f3;
      if (Mouse.isButtonDown(0) && this.flag3) {
         float f6 = f2 - this.y;
         float f7 = f6 / this.height;
         this.floatValue3 = -(this.floatValue4 * f7) + f4 / 2.0F;
      }

      if (check) {
         float height2 = this.height;
         boolean flag2 = f >= this.x && f <= this.x + this.width && f2 > this.y - f5 && f2 < this.y + f4 - f5;
         if (this.flag2) {
            if (this.floatValue3 != this.floatValue2 && this.floatValue2 != f4 / 2.0F && this.floatValue2 != f4 / 2.0F + -this.floatValue4 + height2) {
               if (f2 > this.y + f4 - f4 / 4.0F - f5) {
                  this.floatValue3 -= floatValue4 / 7.0F;
               } else if (f2 < this.y + f4 / 4.0F - f5) {
                  this.floatValue3 += floatValue4 / 7.0F;
               }

               this.floatValue2 = this.floatValue3;
            } else if (f2 > this.y + f4 - f4 / 4.0F - f5 || f2 < this.y + f4 / 4.0F - f5) {
               this.floatValue2 = 1.0F;
            }
         }

         if (this.floatValue3 < -this.floatValue4 + height2) {
            this.floatValue3 = -this.floatValue4 + height2;
            this.doubleValue = 0.0;
         }

         if (this.floatValue3 > 0.0F) {
            this.floatValue3 = 0.0F;
            this.doubleValue = 0.0;
         }

         Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -13158601);
         Gui.drawRect(this.x, this.y - f5, this.x + this.width, this.y + f4 - f5, !flag2 && !this.flag2 ? -4180940 : -52429);
      }

      if (!check && this.floatValue3 != 0.0F) {
         this.floatValue3 = 0.0F;
      }
   }

   public UIElement getUielement() {
      return this.uielement;
   }

   public ScrollBar(UIElement uielement) {
      this.uielement = uielement;
   }

   @Override
   public boolean containsPoint(float f, float f2) {
      return f >= this.x && f <= this.x + this.width && f2 > this.y && f2 < this.y + this.height;
   }

   public void process(float f, float f2, boolean flag) {
      if (flag && (this.uielement == null || this.uielement.containsPoint(f, f2)) && this.doubleValue != 0.0) {
         double d = this.doubleValue / 8.0;
         this.floatValue3 = (float)(this.floatValue3 - d);
         this.doubleValue -= d;
         if (Math.abs(this.doubleValue) < 0.1) {
            this.doubleValue = 0.0;
         }
      }

      if (this.flag) {
         if (this.floatValue3 < -this.floatValue4 + this.height) {
            this.floatValue3 = -this.floatValue4 + this.height;
            this.doubleValue = 0.0;
         }

         if (this.floatValue3 > 0.0F) {
            this.floatValue3 = 0.0F;
            this.doubleValue = 0.0;
         }
      }

      GlStateManager.pushMatrix();
      GL11.glTranslatef(0.0F, -this.floatValue3, 0.0F);
   }

   public void process2(float f, float f2, boolean flag) {
      this.flag = true;
      GlStateManager.popMatrix();
      boolean check = this.check();
      if (this.flag2 && (!Mouse.isButtonDown(0) || !this.containsPoint(f, f2) || !flag)) {
         this.flag2 = false;
      }

      if (this.flag3 && !Mouse.isButtonDown(0)) {
         this.flag3 = false;
      }

      float height = this.height;
      float floatValue4 = this.floatValue4;
      float f3 = height / floatValue4 * 100.0F;
      float f4 = height / 100.0F * f3;
      float f5 = this.floatValue3 / 100.0F * f3;
      if (Mouse.isButtonDown(0) && this.flag3) {
         float f6 = f2 - this.y;
         float f7 = f6 / this.height;
         this.floatValue3 = -(this.floatValue4 - this.height / 2.0F) + this.floatValue4 * f7 + f4 / 2.0F;
      }

      if (check) {
         float height2 = this.height;
         boolean flag2 = f >= this.x && f <= this.x + this.width && f2 > this.y - f5 && f2 < this.y + f4 - f5;
         if (this.flag2) {
            if (this.floatValue3 != this.floatValue2 && this.floatValue2 != f4 / 2.0F && this.floatValue2 != f4 / 2.0F + -this.floatValue4 + height2) {
               if (f2 > this.y + this.height - f4 - f4 / 4.0F + f5) {
                  this.floatValue3 = floatValue4 / 7.0F;
               } else if (f2 < this.y + this.height - f4 / 4.0F + f5) {
                  this.floatValue3 += floatValue4 / 7.0F;
               }

               this.floatValue2 = this.floatValue3;
            } else if (f2 > this.y + this.height - f4 - f4 / 4.0F + f5 || f2 < this.y + this.height - f4 / 4.0F - f5) {
               this.floatValue2 = 1.0F;
            }
         }

         if (this.floatValue3 < -this.floatValue4 + height2) {
            this.floatValue3 = -this.floatValue4 + height2;
            this.doubleValue = 0.0;
         }

         if (this.floatValue3 > 0.0F) {
            this.floatValue3 = 0.0F;
            this.doubleValue = 0.0;
         }

         Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, -13158601);
         Gui.drawRect(this.x, this.y + this.height + f5, this.x + this.width, this.y + this.height - f4 + f5, !flag2 && !this.flag2 ? -4180940 : -52429);
      }

      if (!check && this.floatValue3 != 0.0F) {
         this.floatValue3 = 0.0F;
      }
   }

   public boolean isFlag3() {
      return this.flag3;
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   @Override
   public void handleMouseInput() {
      int eventDWheel = Mouse.getEventDWheel();
      if (eventDWheel != 0 && this.floatValue4 >= this.height) {
         this.doubleValue += eventDWheel / 2.75F;
      }
   }

   public float getFloatValue3() {
      return this.floatValue3;
   }

   public void setFloatValue4(float f) {
      this.floatValue4 = f;
   }

   public void process3(float f, float f2, boolean flag) {
      if (flag && (this.uielement == null || this.uielement.containsPoint(f, f2)) && this.doubleValue != 0.0) {
         double d = this.doubleValue / 8.0;
         this.floatValue3 = (float)(this.floatValue3 + d);
         this.doubleValue -= d;
         if (Math.abs(this.doubleValue) < 0.1) {
            this.doubleValue = 0.0;
         }
      }

      if (this.flag) {
         if (this.floatValue3 < -this.floatValue4 + this.height) {
            this.floatValue3 = -this.floatValue4 + this.height;
            this.doubleValue = 0.0;
         }

         if (this.floatValue3 > 0.0F) {
            this.floatValue3 = 0.0F;
            this.doubleValue = 0.0;
         }
      }

      GlStateManager.pushMatrix();
      GL11.glTranslatef(0.0F, this.floatValue3, 0.0F);
   }

   @Override
   public boolean mouseClicked(float f, float f2, int n, boolean flag) {
      if (this.containsPoint(f, f2) && flag) {
         this.floatValue = f2 - this.y;
         this.flag3 = true;
      }

      return false;
   }
}
