package com.cheatbreaker.client.ui.component;

import com.cheatbreaker.client.render.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class IconComponent extends SettingComponent {
   public boolean visible = true;
   public boolean small;
   public String iconName;
   public boolean unusedFlag;
   public Panel panel;
   public boolean tiny;

   @Override
   public void render(int n, int n2, float f) {
      boolean isMouseOver = this.isMouseOver(n, n2);
      float f2 = this.small ? 4.0F : 8.0F;
      float f3 = this.tiny ? this.x + 2.0F : (this.small ? (float)(this.x + 1.6) : this.x + 6);
      float f4 = this.tiny ? this.y + 2.0F : (this.small ? (float)(this.y + 1.7) : this.y + 6);
      GlStateManager.pushMatrix();
      float red = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getRed();
      float green = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getGreen();
      float blue = RenderUtils.toColor(this.theme.getPrimaryTextColorValue()).getBlue();
      GlStateManager.color(red, green, blue, isMouseOver ? 0.8F : 0.45F);
      RenderUtils.drawSquareTextureViaRenderEngine(new ResourceLocation("client/icons/" + this.iconName), f2, f3, f4);
      GlStateManager.popMatrix();
   }

   public void setUnusedFlag(boolean flag) {
      this.unusedFlag = flag;
   }

   public void setTiny(boolean flag) {
      this.tiny = flag;
   }

   public void setVisible(boolean flag) {
      this.visible = flag;
   }

   public void setIconName(String str) {
      this.iconName = str;
   }

   public IconComponent(Panel panel, String str, int n, int n2, int n3, int n4, float f) {
      super(f);
      this.iconName = str;
      this.setBounds(n, n2, n3, n4);
      this.panel = panel;
   }

   public void setSmall(boolean flag) {
      this.small = flag;
   }

   @Override
   public void mouseClicked(int n, int n2, int n3) {
   }
}
