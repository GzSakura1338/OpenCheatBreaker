package net.optifine.gui;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiOptionButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.optifine.Config;

public class GuiMessage extends GuiScreen {
   public List listLines2 = Lists.newArrayList();
   public GuiScreen parentScreen;
   public String messageLine1;
   public String messageLine2;
   public String confirmButtonText;
   public int ticksUntilEnable;

   public GuiMessage(GuiScreen guiScreen, String str, String str2) {
      this.parentScreen = guiScreen;
      this.messageLine1 = str;
      this.messageLine2 = str2;
      this.confirmButtonText = I18n.format("gui.done");
   }

   @Override
   public void initGui() {
      this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 74, this.height / 6 + 96, this.confirmButtonText));
      this.listLines2.clear();
      this.listLines2.addAll(this.fontRendererObj.listFormattedStringToWidth(this.messageLine2, this.width - 50));
   }

   @Override
   public void actionPerformed(GuiButton guiButton) {
      Config.getMinecraft().displayGuiScreen(this.parentScreen);
   }

   @Override
   public void drawScreen(int n, int n2, float f) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.messageLine1, this.width / 2, 70, 16777215);
      int n3 = 90;

      for (Object obj : this.listLines2) {
         String str = (String)obj;
         this.drawCenteredString(this.fontRendererObj, str, this.width / 2, n3, 16777215);
         n3 += this.fontRendererObj.FONT_HEIGHT;
      }

      super.drawScreen(n, n2, f);
   }

   public void setButtonDelay(int n) {
      this.ticksUntilEnable = n;

      for (GuiButton guiButton : this.buttonList) {
         guiButton.enabled = false;
      }
   }

   @Override
   public void updateScreen() {
      super.updateScreen();
      if (--this.ticksUntilEnable == 0) {
         for (GuiButton guiButton : this.buttonList) {
            guiButton.enabled = true;
         }
      }
   }
}
