package net.minecraft.client.gui;

import com.cheatbreaker.client.ui.screen.MainMenuScreen;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

public class GuiDisconnected extends GuiScreen {
   public GuiScreen parentScreen;
   public String reason;
   public List<String> multilineMessage;
   public IChatComponent message;
   public int reasonHeight;

   public GuiDisconnected(GuiScreen screen, String reasonLocalizationKey, IChatComponent chatComp) {
      this.parentScreen = screen;
      this.reason = I18n.format(reasonLocalizationKey);
      this.message = chatComp;
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
   }

   @Override
   public void initGui() {
      this.buttonList.clear();
      this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
      this.reasonHeight = this.multilineMessage.size() * this.fontRendererObj.FONT_HEIGHT;
      this.buttonList
         .add(new GuiButton(0, this.width / 2 - 100, this.height / 2 + this.reasonHeight / 2 + this.fontRendererObj.FONT_HEIGHT, I18n.format("gui.toMenu")));
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 2 + this.reasonHeight / 2 + this.fontRendererObj.FONT_HEIGHT + 25, "Reconnect"));
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 0) {
         this.mc.displayGuiScreen(this.parentScreen);
      }

      if (button.id == 0) {
         if (this.parentScreen instanceof GuiDisconnected) {
            this.mc.displayGuiScreen(new GuiMultiplayer(new MainMenuScreen()));
         } else {
            this.mc.displayGuiScreen(this.parentScreen);
         }
      }

      if (button.id == 1) {
         if (this.mc.serverData != null) {
            this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, this.mc.serverData));
         } else if (this.mc.getCurrentServerData() != null) {
            this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, this.mc.getCurrentServerData()));
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(
         this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - this.reasonHeight / 2 - this.fontRendererObj.FONT_HEIGHT * 2, 11184810
      );
      int n = this.height / 2 - this.reasonHeight / 2;
      if (this.multilineMessage != null) {
         for (String str : this.multilineMessage) {
            this.drawCenteredString(this.fontRendererObj, str, this.width / 2, n, 16777215);
            n += this.fontRendererObj.FONT_HEIGHT;
         }
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
