package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.animation.ColorAnimation;
import com.cheatbreaker.client.animation.EaseInOutAnimation;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.LauncherAccount;
import com.cheatbreaker.client.ui.screen.MenuScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class AccountSwitcher extends UIElement {
   public ColorAnimation colorAnimation = new ColorAnimation(1342177279, -1353670564);
   public float floatValue;
   public boolean flag;
   public ColorAnimation colorAnimation2 = new ColorAnimation(444958085, 1063565678);
   public float floatValue2;
   public ColorAnimation colorAnimation3 = new ColorAnimation(444958085, 1062577506);
   public EaseInOutAnimation easeInOutAnimation = new EaseInOutAnimation(300L);
   public ScrollBar scrollBar = new ScrollBar(this);
   public MenuScreen menuScreen;
   public ResourceLocation resourceLocation;
   public String text;

   @Override
   public void handleMouseInput() {
      this.scrollBar.handleMouseInput();
   }

   public void setText(String str) {
      this.text = str;
   }

   public float computeFloat(float f) {
      return 22.0F + f + 10.0F;
   }

   @Override
   public boolean mouseClicked(float f, float f2, int n, boolean flag) {
      if (!flag) {
         return false;
      }

      if (this.easeInOutAnimation.isTriggered()) {
         this.scrollBar.mouseClicked(f, f2, n, flag);
         int n2 = 1;

         for (LauncherAccount launcherAccount : this.menuScreen.getAccounts()) {
            if (!launcherAccount.getDisplayName().equals(this.mc.getSession().getUsername())) {
               float f3 = this.x;
               float f4 = this.x + this.width;
               float f5 = this.y + this.floatValue2 + n2 * 16 - 8.0F;
               float f6 = f5 + 16.0F;
               boolean flag2 = f > f3
                  && f < f4
                  && f2 - this.scrollBar.getFloatValue3() > f5
                  && f2 - this.scrollBar.getFloatValue3() < f6
                  && flag
                  && !this.scrollBar.containsPoint(f, f2)
                  && !this.scrollBar.isFlag3();
               if (flag2) {
                  Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
                  this.menuScreen.switchAccount(launcherAccount.getDisplayName());
               }

               n2++;
            }
         }
      }

      return false;
   }

   @Override
   public void render(float f, float f2, boolean flag) {
      boolean flag2 = flag && this.containsPoint(f, f2);
      this.floatValue = Math.min(this.menuScreen.getAccounts().size() * 16 + 12, 120);
      RenderUtils.drawBorderedGradientRect(
         this.x,
         this.y,
         this.x + this.width,
         this.y + this.floatValue2,
         this.colorAnimation.getColor(flag2).getRGB(),
         this.colorAnimation2.getColor(flag2).getRGB(),
         this.colorAnimation3.getColor(flag2).getRGB()
      );
      float f3 = 6.0F;
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      RenderUtils.drawSquareTextureViaRenderEngine(this.resourceLocation, f3, this.x + 4.0F, this.y + this.floatValue2 / 2.0F - f3);
      float f4 = this.x + 22.0F;
      float f5 = this.y + 4.5F;
      CheatBreaker.getInstance().getFontManager().robotoRegular13.drawString(this.text, f4, f5, -1342177281);
      float updateToggle = this.easeInOutAnimation.updateToggle(this.containsPoint(f, f2) && flag);
      if (this.easeInOutAnimation.isRunning() && this.menuScreen.getAccounts().size() > 0) {
         this.setBounds(this.x, this.y, this.width, this.floatValue2 + this.floatValue * updateToggle);
         this.flag = true;
      } else if (!this.easeInOutAnimation.isRunning() && !this.containsPoint(f, f2)) {
         this.flag = false;
      }

      if (this.flag) {
         float f6 = 0.5F;
         float f7 = this.y + this.height + f6;
         float f8 = this.y + 5.0F + this.floatValue2;
         if (f7 > f8) {
            Gui.drawRectWithOuterBorder(this.x + 1.0F, f8, this.x + this.width - 1.0F, f7, f6, 1342177279, 444958085);
         }

         GlStateManager.pushMatrix();
         GL11.glEnable(3089);
         RenderUtils.scissorScaled(
            (int)this.x,
            (int)(this.y + this.floatValue2),
            (int)(this.x + this.width),
            (int)(this.y + this.floatValue2 + 7.0F + (this.height - this.floatValue2 - 6.0F) * updateToggle),
            (int)(this.menuScreen.getScaledResolution().getScaleFactor() * this.menuScreen.getUiScale()),
            (int)this.menuScreen.getScaledHeight()
         );
         this.scrollBar.process3(f, f2, flag);
         int n = 1;

         for (LauncherAccount launcherAccount : this.menuScreen.getAccounts()) {
            if (!launcherAccount.getDisplayName().equals(this.mc.getSession().getUsername())) {
               float f9 = this.x;
               float f10 = this.x + this.width;
               float f11 = this.y + this.floatValue2 + n * 16 - 8.0F;
               float f12 = f11 + 16.0F;
               boolean flag3 = f > f9
                  && f < f10
                  && f2 - this.scrollBar.getFloatValue3() > f11
                  && f2 - this.scrollBar.getFloatValue3() < f12
                  && flag
                  && !this.scrollBar.containsPoint(f, f2)
                  && !this.scrollBar.isFlag3();
               GlStateManager.color(1.0F, 1.0F, 1.0F, flag3 ? 1.0F : 0.7F);
               RenderUtils.drawSquareTextureViaRenderEngine(launcherAccount.getResourceLocation(), f3, this.x + 4.0F, f11 + 8.0F - f3);
               CheatBreaker.getInstance()
                  .getFontManager()
                  .robotoRegular13
                  .drawString(launcherAccount.getDisplayName(), this.x + 22.0F, f11 + 4.0F, flag3 ? -1 : -1342177281);
               n++;
            }
         }

         this.scrollBar.render(f, f2, flag);
         GL11.glDisable(3089);
         GlStateManager.popMatrix();
      }
   }

   public void setResourceLocation(ResourceLocation resourceLocation) {
      this.resourceLocation = resourceLocation;
   }

   public void setBounds() {
      this.setBounds(this.x, this.y, this.width, this.height);
   }

   @Override
   public void setBounds(float f, float f2, float f3, float f4) {
      super.setBounds(f, f2, f3, f4);
      if (this.floatValue2 == 0.0F) {
         this.floatValue2 = f4;
      }

      this.floatValue = Math.min(this.menuScreen.getAccounts().size() * 16 + 12, 120);
      this.scrollBar.setBounds(f + f3 - 5.0F, f2 + this.floatValue2 + 6.0F, 4.0F, this.floatValue - 7.0F);
      this.scrollBar.setFloatValue4(this.menuScreen.getAccounts().size() * 16 + 4);
   }

   public AccountSwitcher(MenuScreen menuScreen, String str, ResourceLocation resourceLocation) {
      this.menuScreen = menuScreen;
      this.resourceLocation = resourceLocation;
      this.text = str;
   }
}
