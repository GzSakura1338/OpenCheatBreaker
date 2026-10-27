package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuRecipient;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.gui.spectator.categories.SpectatorDetails;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;

public class GuiSpectator extends Gui implements ISpectatorMenuRecipient {
   public static ResourceLocation WIDGETS_TEXTURE = new ResourceLocation("textures/gui/widgets.png");
   public static ResourceLocation SPECTATOR_TEXTURE = new ResourceLocation("textures/gui/spectator_widgets.png");
   public Minecraft client;
   public long lastInteractionTime;
   public SpectatorMenu spectatorMenu;

   public GuiSpectator(Minecraft mcIn) {
      this.client = mcIn;
   }

   public void selectSlot(int n) {
      this.lastInteractionTime = Minecraft.getSystemTime();
      if (this.spectatorMenu != null) {
         this.spectatorMenu.useCommand(n);
      } else {
         this.spectatorMenu = new SpectatorMenu(this);
      }
   }

   public float getSpectatorMenuHeight() {
      long l = this.lastInteractionTime - Minecraft.getSystemTime() + 5000L;
      return MathHelper.clamp_float((float)l / 2000.0F, 0.0F, 1.0F);
   }

   public void renderTooltip(ScaledResolution scaledResolution, float f) {
      if (this.spectatorMenu != null) {
         float spectatorMenuHeight = this.getSpectatorMenuHeight();
         if (spectatorMenuHeight <= 0.0F) {
            this.spectatorMenu.close();
         } else {
            int n = scaledResolution.getScaledWidth() / 2;
            float zLevel2 = zLevel;
            zLevel = -90.0F;
            float f2 = scaledResolution.getScaledHeight() - 22.0F * spectatorMenuHeight;
            SpectatorDetails currentState = this.spectatorMenu.getCurrentState();
            this.renderSpectatorMenu(scaledResolution, spectatorMenuHeight, n, f2, currentState);
            zLevel = zLevel2;
         }
      }
   }

   public void renderSpectatorMenu(ScaledResolution scaledResolution, float f, int n, float f2, SpectatorDetails spectatorDetails) {
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.color(1.0F, 1.0F, 1.0F, f);
      this.client.getTextureManager().bindTexture(WIDGETS_TEXTURE);
      this.drawTexturedModalRect(n - 91, f2, 0, 0, 182, 22);
      if (spectatorDetails.getSelectedSlot() >= 0) {
         this.drawTexturedModalRect(n - 91 - 1 + spectatorDetails.getSelectedSlot() * 20, f2 - 1.0F, 0, 22, 24, 22);
      }

      RenderHelper.enableGUIStandardItemLighting();

      for (int i = 0; i < 9; i++) {
         this.renderSpectatorCommand(i, scaledResolution.getScaledWidth() / 2 - 90 + i * 20 + 2, f2 + 3.0F, f, spectatorDetails.getCommand(i));
      }

      RenderHelper.disableStandardItemLighting();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableBlend();
   }

   public void renderSpectatorCommand(int n, int n2, float f, float f2, ISpectatorMenuObject spectatorMenuObject) {
      this.client.getTextureManager().bindTexture(SPECTATOR_TEXTURE);
      if (spectatorMenuObject != SpectatorMenu.BLANK_COMMAND) {
         int n3 = (int)(f2 * 255.0F);
         GlStateManager.pushMatrix();
         GlStateManager.translate(n2, f, 0.0F);
         float f3 = spectatorMenuObject.isEnabled() ? 1.0F : 0.25F;
         GlStateManager.color(f3, f3, f3, f2);
         spectatorMenuObject.render(f3, n3);
         GlStateManager.popMatrix();
         String str = String.valueOf(GameSettings.getKeyDisplayString(this.client.gameSettings.keyBindsHotbar[n].getKeyCode()));
         if (n3 > 3 && spectatorMenuObject.isEnabled()) {
            this.client
               .fontRendererObj
               .drawStringWithShadow(str, n2 + 19 - 2 - this.client.fontRendererObj.getStringWidth(str), f + 6.0F + 3.0F, 16777215 + (n3 << 24));
         }
      }
   }

   public void renderSelectedItem(ScaledResolution scaledResolution) {
      int n = (int)(this.getSpectatorMenuHeight() * 255.0F);
      if (n > 3 && this.spectatorMenu != null) {
         ISpectatorMenuObject selectedCommand = this.spectatorMenu.getSelectedCommand();
         String str = selectedCommand != SpectatorMenu.BLANK_COMMAND
            ? selectedCommand.getSpectatorName().getFormattedText()
            : this.spectatorMenu.getCurrentGroup().createChatComponentText().getFormattedText();
         if (str != null) {
            int n2 = (scaledResolution.getScaledWidth() - this.client.fontRendererObj.getStringWidth(str)) / 2;
            int n3 = scaledResolution.getScaledHeight() - 35;
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            this.client.fontRendererObj.drawStringWithShadow(str, n2, n3, 16777215 + (n << 24));
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
         }
      }
   }

   @Override
   public void handleSpectatorMenu(SpectatorMenu spectatorMenu) {
      this.spectatorMenu = null;
      this.lastInteractionTime = 0L;
   }

   public boolean isOpen() {
      return this.spectatorMenu != null;
   }

   public void cycleSlot(int n) {
      int n2 = this.spectatorMenu.getSelectedSlot() + n;

      while (n2 >= 0 && n2 <= 8 && (this.spectatorMenu.getCommand(n2) == SpectatorMenu.BLANK_COMMAND || !this.spectatorMenu.getCommand(n2).isEnabled())) {
         n2 += n;
      }

      if (n2 >= 0 && n2 <= 8) {
         this.spectatorMenu.useCommand(n2);
         this.lastInteractionTime = Minecraft.getSystemTime();
      }
   }

   public void useSelectedCommand() {
      this.lastInteractionTime = Minecraft.getSystemTime();
      if (this.isOpen()) {
         int selectedSlot = this.spectatorMenu.getSelectedSlot();
         if (selectedSlot != -1) {
            this.spectatorMenu.useCommand(selectedSlot);
         }
      } else {
         this.spectatorMenu = new SpectatorMenu(this);
      }
   }
}
