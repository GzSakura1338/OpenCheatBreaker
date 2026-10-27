package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.resources.I18n;

public class GuiYesNo extends GuiScreen {
   public List<String> lines = Lists.newArrayList();
   public GuiYesNoCallback parentScreen;
   public String messageLine1;
   public String messageLine2;
   public String confirmButtonText;
   public String cancelButtonText;
   public int parentButtonClickedId;
   public int ticksUntilEnable;

   public GuiYesNo(GuiYesNoCallback guiYesNoCallback, String str, String str2, int n) {
      this.parentScreen = guiYesNoCallback;
      this.messageLine1 = str;
      this.messageLine2 = str2;
      this.parentButtonClickedId = n;
      this.confirmButtonText = I18n.format("gui.yes");
      this.cancelButtonText = I18n.format("gui.no");
   }

   public GuiYesNo(GuiYesNoCallback guiYesNoCallback, String str, String str2, String str3, String str4, int n) {
      this.parentScreen = guiYesNoCallback;
      this.messageLine1 = str;
      this.messageLine2 = str2;
      this.confirmButtonText = str3;
      this.cancelButtonText = str4;
      this.parentButtonClickedId = n;
   }

   @Override
   public void initGui() {
      this.buttonList.add(new GuiOptionButton(0, this.width / 2 - 155, this.height / 6 + 96, this.confirmButtonText));
      this.buttonList.add(new GuiOptionButton(1, this.width / 2 - 155 + 160, this.height / 6 + 96, this.cancelButtonText));
      this.lines.clear();
      this.lines.addAll(this.fontRendererObj.listFormattedStringToWidth(this.messageLine2, this.width - 50));
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      this.parentScreen.confirmClicked(button.id == 0, this.parentButtonClickedId);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.messageLine1, this.width / 2, 70, 16777215);
      int n = 90;

      for (String str : this.lines) {
         this.drawCenteredString(this.fontRendererObj, str, this.width / 2, n, 16777215);
         n += this.fontRendererObj.FONT_HEIGHT;
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
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
