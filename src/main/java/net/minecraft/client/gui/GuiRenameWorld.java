package net.minecraft.client.gui;

import java.io.IOException;
import net.minecraft.client.resources.I18n;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.lwjgl.input.Keyboard;

public class GuiRenameWorld extends GuiScreen {
   public GuiScreen parentScreen;
   public GuiTextField searchField;
   public String saveName;

   public GuiRenameWorld(GuiScreen parentScreenIn, String saveNameIn) {
      this.parentScreen = parentScreenIn;
      this.saveName = saveNameIn;
   }

   @Override
   public void updateScreen() {
      this.searchField.updateCursorCounter();
   }

   @Override
   public void initGui() {
      Keyboard.enableRepeatEvents(true);
      this.buttonList.clear();
      this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.format("selectWorld.renameButton")));
      this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
      ISaveFormat saveLoader = this.mc.getSaveLoader();
      WorldInfo worldInfo = saveLoader.getWorldInfo(this.saveName);
      String worldName = worldInfo.getWorldName();
      this.searchField = new GuiTextField(2, this.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
      this.searchField.setFocused(true);
      this.searchField.setText(worldName);
   }

   @Override
   public void onGuiClosed() {
      Keyboard.enableRepeatEvents(false);
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id == 1) {
            this.mc.displayGuiScreen(this.parentScreen);
         } else if (button.id == 0) {
            ISaveFormat saveLoader = this.mc.getSaveLoader();
            saveLoader.renameWorld(this.saveName, this.searchField.getText().trim());
            this.mc.displayGuiScreen(this.parentScreen);
         }
      }
   }

   @Override
   public void keyTyped(char typedChar, int keyCode) throws IOException {
      this.searchField.textboxKeyTyped(typedChar, keyCode);
      this.buttonList.get(0).enabled = this.searchField.getText().trim().length() > 0;
      if (keyCode == 28 || keyCode == 156) {
         this.actionPerformed(this.buttonList.get(0));
      }
   }

   @Override
   public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
      this.searchField.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.drawCenteredString(this.fontRendererObj, I18n.format("selectWorld.renameTitle"), this.width / 2, 20, 16777215);
      this.drawString(this.fontRendererObj, I18n.format("selectWorld.enterName"), this.width / 2 - 100, 47, 10526880);
      this.searchField.drawTextBox();
      super.drawScreen(mouseX, mouseY, partialTicks);
   }
}
