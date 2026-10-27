package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.optifine.gui.GuiButtonOF;
import net.optifine.gui.GuiScreenCapeOF;

public class GuiCustomizeSkin extends GuiScreen {
   public GuiScreen parentScreen;
   public String title;

   public GuiCustomizeSkin(GuiScreen parentScreenIn) {
      this.parentScreen = parentScreenIn;
   }

   @Override
   public void initGui() {
      int n = 0;
      this.title = I18n.format("options.skinCustomisation.title");

      for (EnumPlayerModelParts enumPlayerModelParts : EnumPlayerModelParts.values()) {
         this.buttonList
            .add(new GuiCustomizeSkin.ButtonPart(enumPlayerModelParts.getPartId(), this.width / 2 - 155 + n % 2 * 160, this.height / 6 + 24 * (n >> 1), 150, 20, enumPlayerModelParts));
         n++;
      }

      if (n % 2 == 1) {
         n++;
      }

      this.buttonList.add(new GuiButtonOF(210, this.width / 2 - 100, this.height / 6 + 24 * (n >> 1), I18n.format("of.options.skinCustomisation.ofCape")));
      n += 2;
      this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 24 * (n >> 1), I18n.format("gui.done")));
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id == 210) {
            this.mc.displayGuiScreen(new GuiScreenCapeOF(this));
         }

         if (button.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.parentScreen);
         } else if (button instanceof GuiCustomizeSkin.ButtonPart) {
            EnumPlayerModelParts playerModelParts = ((GuiCustomizeSkin.ButtonPart)button).playerModelParts;
            this.mc.gameSettings.switchModelPartEnabled(playerModelParts);
            button.displayString = this.getPlayerModelPartDisplayString(playerModelParts);
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 20, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public String getPlayerModelPartDisplayString(EnumPlayerModelParts playerModelParts) {
      String str;
      if (this.mc.gameSettings.getModelParts().contains(playerModelParts)) {
         str = I18n.format("options.on");
      } else {
         str = I18n.format("options.off");
      }

      return playerModelParts.getOptionName().getFormattedText() + ": " + str;
   }

   class ButtonPart extends GuiButton {
      public EnumPlayerModelParts playerModelParts;

      public ButtonPart(int n, int n2, int n3, int n4, int n5, EnumPlayerModelParts playerModelParts) {
         super(n, n2, n3, n4, n5, GuiCustomizeSkin.this.getPlayerModelPartDisplayString(playerModelParts));
         this.playerModelParts = playerModelParts;
      }
   }
}
