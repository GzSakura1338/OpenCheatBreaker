package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.render.RenderUtils;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class CrosshairComponent extends SettingComponent {
   @Override
   public void render(int n, int n2, float f) {
      Gui.drawRect(this.x + (this.width / 2 - 15) - 41, this.y + 4, this.x + (this.width / 2 - 15) + 41, this.y + 51, -16777216);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      RenderUtils.drawTexture(
         new ResourceLocation(
            "client/defaults/crosshair_" + CheatBreaker.getInstance().getModManager().crosshairMod.previewBackground.getStringValue().toLowerCase() + ".png"
         ),
         this.x + (this.width / 2 - 15) - 40,
         this.y + 5,
         80.0F,
         45.0F
      );
      Gui.zLevel = 0.0F;
      float f2 = this.x + this.width / 2 - 15;
      CheatBreaker.getInstance().getModManager().crosshairMod.render(f2, this.y + this.height / 2 + 3, false);
   }

   public CrosshairComponent(float f) {
      super(f);
      this.height = 50;
   }

   @Override
   public void mouseClicked(int n, int n2, int n3) {
   }
}
