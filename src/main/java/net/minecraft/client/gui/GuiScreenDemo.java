package net.minecraft.client.gui;

import java.io.IOException;
import java.net.URI;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiScreenDemo extends GuiScreen {
   public static Logger logger = LogManager.getLogger();
   public static ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation("textures/gui/demo_background.png");

   @Override
   public void initGui() {
      this.buttonList.clear();
      byte b = -16;
      this.buttonList.add(new GuiButton(1, this.width / 2 - 116, this.height / 2 + 62 + b, 114, 20, I18n.format("demo.help.buy")));
      this.buttonList.add(new GuiButton(2, this.width / 2 + 2, this.height / 2 + 62 + b, 114, 20, I18n.format("demo.help.later")));
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      switch (button.id) {
         case 1:
            button.enabled = false;

            try {
               Class forName = Class.forName("java.awt.Desktop");
               Object invoke = forName.getMethod("getDesktop").invoke(null);
               forName.getMethod("browse", URI.class).invoke(invoke, new URI("http://www.minecraft.net/store?source=demo"));
            } catch (Throwable e) {
               logger.error("Couldn't open link", e);
            }
            break;
         case 2:
            this.mc.displayGuiScreen((GuiScreen)null);
            this.mc.setIngameFocus();
      }
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
   }

   @Override
   public void drawDefaultBackground() {
      super.drawDefaultBackground();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
      int n = (this.width - 248) / 2;
      int n2 = (this.height - 166) / 2;
      this.drawTexturedModalRect(n, n2, 0, 0, 248, 166);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      int n = (this.width - 248) / 2 + 10;
      int n2 = (this.height - 166) / 2 + 8;
      this.fontRendererObj.drawString(I18n.format("demo.help.title"), n, n2, 2039583);
      n2 += 12;
      GameSettings gameSettings = this.mc.gameSettings;
      this.fontRendererObj
         .drawString(
            I18n.format(
               "demo.help.movementShort",
               GameSettings.getKeyDisplayString(gameSettings.keyBindForward.getKeyCode()),
               GameSettings.getKeyDisplayString(gameSettings.keyBindLeft.getKeyCode()),
               GameSettings.getKeyDisplayString(gameSettings.keyBindBack.getKeyCode()),
               GameSettings.getKeyDisplayString(gameSettings.keyBindRight.getKeyCode())
            ),
            n,
            n2,
            5197647
         );
      this.fontRendererObj.drawString(I18n.format("demo.help.movementMouse"), n, n2 + 12, 5197647);
      this.fontRendererObj.drawString(I18n.format("demo.help.jump", GameSettings.getKeyDisplayString(gameSettings.keyBindJump.getKeyCode())), n, n2 + 24, 5197647);
      this.fontRendererObj
         .drawString(I18n.format("demo.help.inventory", GameSettings.getKeyDisplayString(gameSettings.keyBindInventory.getKeyCode())), n, n2 + 36, 5197647);
      this.fontRendererObj.drawSplitString(I18n.format("demo.help.fullWrapped"), n, n2 + 68, 218, 2039583);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
