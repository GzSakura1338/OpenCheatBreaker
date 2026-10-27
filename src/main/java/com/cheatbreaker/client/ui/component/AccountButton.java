package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.render.RenderUtils;
import com.cheatbreaker.client.ui.screen.LauncherAccount;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class AccountButton extends UIElement {
   public final boolean flag = true;
   public String text;
   public ResourceLocation resourceLocation;

   public ResourceLocation getResourceLocation() {
      return this.resourceLocation;
   }

   public AccountButton(LauncherAccount launcherAccount) {
      this.text = launcherAccount.getDisplayName();
      this.resourceLocation = CheatBreaker.getInstance().getFontManager().getHeadTexture(launcherAccount.getDisplayName());
   }

   @Override
   public void render(float f, float f2, boolean flag) {
      this.getClass();
      Gui.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, this.containsPoint(f, f2) ? -15395563 : -14540254);
      int n = -1;
      if (this.containsPoint(f, f2)) {
         n = -3092272;
      }

      if (this.text.length() > 9) {
         float f3 = this.getX() + this.width / 2.0F + 12.0F;
         float f4 = this.y + this.height / 2.0F - 4;
         CheatBreaker.getInstance().getFontManager().playRegular14.drawCenteredString(this.text, f3, f4, n);
      } else {
         float f3 = this.x + this.width / 2.0F + 12.0F;
         float f4 = this.y + this.height / 2.0F - 5;
         CheatBreaker.getInstance().getFontManager().playRegular16.drawCenteredString(this.text, f3, f4, n);
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      RenderUtils.drawSquareTextureViaRenderEngine(this.resourceLocation, 7.0F, this.x + 10.0F, this.y + 5.0F);
   }

   public void setResourceLocation(ResourceLocation resourceLocation) {
      this.resourceLocation = resourceLocation;
   }

   public String getText() {
      return this.text;
   }

   public void setText(String str) {
      this.text = str;
   }
}
