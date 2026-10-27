package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class GuiLockIconButton extends GuiButton {
   public boolean locked = false;

   public GuiLockIconButton(int n, int n2, int n3) {
      super(n, n2, n3, 20, 20, "");
   }

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean flag) {
      this.locked = flag;
   }

   @Override
   public void drawButton(Minecraft mc, int mouseX, int mouseY) {
      if (this.visible) {
         mc.getTextureManager().bindTexture(GuiButton.buttonTextures);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         boolean flag = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
         GuiLockIconButton.Icon icon;
         if (this.locked) {
            if (!this.enabled) {
               icon = GuiLockIconButton.Icon.LOCKED_DISABLED;
            } else if (flag) {
               icon = GuiLockIconButton.Icon.LOCKED_HOVER;
            } else {
               icon = GuiLockIconButton.Icon.LOCKED;
            }
         } else if (!this.enabled) {
            icon = GuiLockIconButton.Icon.UNLOCKED_DISABLED;
         } else if (flag) {
            icon = GuiLockIconButton.Icon.UNLOCKED_HOVER;
         } else {
            icon = GuiLockIconButton.Icon.UNLOCKED;
         }

         this.drawTexturedModalRect(this.xPosition, this.yPosition, icon.getU(), icon.getV(), this.width, this.height);
      }
   }

   enum Icon {
      LOCKED(0, 146),
      LOCKED_HOVER(0, 166),
      LOCKED_DISABLED(0, 186),
      UNLOCKED(20, 146),
      UNLOCKED_HOVER(20, 166),
      UNLOCKED_DISABLED(20, 186);

      public int u;
      public int v;

      Icon(int n, int n2) {
         this.u = n;
         this.v = n2;
      }

      public int getU() {
         return this.u;
      }

      public int getV() {
         return this.v;
      }
   }
}
