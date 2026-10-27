package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;

public class GuiSnooper extends GuiScreen {
   public java.util.List<String> entryNames = Lists.newArrayList();
   public java.util.List<String> entryValues = Lists.newArrayList();
   public GuiScreen parent;
   public GameSettings game_settings_2;
   public String title;
   public String[] description;
   public GuiSnooper.List entryList;
   public GuiButton enableButton;

   public GuiSnooper(GuiScreen guiScreen, GameSettings gameSettings) {
      this.parent = guiScreen;
      this.game_settings_2 = gameSettings;
   }

   @Override
   public void initGui() {
      this.title = I18n.format("options.snooper.title");
      String format = I18n.format("options.snooper.desc");
      ArrayList newArrayList = Lists.newArrayList();

      for (String str : this.fontRendererObj.listFormattedStringToWidth(format, this.width - 30)) {
         newArrayList.add(str);
      }

      this.description = (String[])newArrayList.toArray(new String[newArrayList.size()]);
      this.entryNames.clear();
      this.entryValues.clear();
      this.buttonList
         .add(
            this.enableButton = new GuiButton(
               1, this.width / 2 - 152, this.height - 30, 150, 20, this.game_settings_2.getKeyBinding(Options.SNOOPER_ENABLED)
            )
         );
      this.buttonList.add(new GuiButton(2, this.width / 2 + 2, this.height - 30, 150, 20, I18n.format("gui.done")));
      boolean flag = this.mc.getIntegratedServer() != null && this.mc.getIntegratedServer().getPlayerUsageSnooper() != null;

      for (Entry entry : new TreeMap<>(this.mc.getPlayerUsageSnooper().getCurrentStats()).entrySet()) {
         this.entryNames.add((flag ? "C " : "") + (String)entry.getKey());
         this.entryValues.add(this.fontRendererObj.trimStringToWidth((String)entry.getValue(), this.width - 220));
      }

      if (flag) {
         for (Entry entry : new TreeMap<>(this.mc.getIntegratedServer().getPlayerUsageSnooper().getCurrentStats()).entrySet()) {
            this.entryNames.add("S " + (String)entry.getKey());
            this.entryValues.add(this.fontRendererObj.trimStringToWidth((String)entry.getValue(), this.width - 220));
         }
      }

      this.entryList = new GuiSnooper.List();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.entryList.handleMouseInput();
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id == 2) {
            this.game_settings_2.saveOptions();
            this.game_settings_2.saveOptions();
            this.mc.displayGuiScreen(this.parent);
         }

         if (button.id == 1) {
            this.game_settings_2.setOptionValue(Options.SNOOPER_ENABLED, 1);
            this.enableButton.displayString = this.game_settings_2.getKeyBinding(Options.SNOOPER_ENABLED);
         }
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.drawDefaultBackground();
      this.entryList.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 8, 16777215);
      int n = 22;

      for (String str : this.description) {
         this.drawCenteredString(this.fontRendererObj, str, this.width / 2, n, 8421504);
         n += this.fontRendererObj.FONT_HEIGHT;
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   class List extends GuiSlot {
      public List() {
         super(
            GuiSnooper.this.mc, GuiSnooper.this.width, GuiSnooper.this.height, 80, GuiSnooper.this.height - 40, GuiSnooper.this.fontRendererObj.FONT_HEIGHT + 1
         );
      }

      @Override
      public int getSize() {
         return GuiSnooper.this.entryNames.size();
      }

      @Override
      public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
      }

      @Override
      public boolean isSelected(int slotIndex) {
         return false;
      }

      @Override
      public void drawBackground() {
      }

      @Override
      public void drawSlot(int entryID, int n, int n2, int n3, int mouseXIn, int mouseYIn) {
         GuiSnooper.this.fontRendererObj.drawString(GuiSnooper.this.entryNames.get(entryID), 10.0F, n2, 16777215);
         GuiSnooper.this.fontRendererObj.drawString(GuiSnooper.this.entryValues.get(entryID), 230.0F, n2, 16777215);
      }

      @Override
      public int getScrollBarX() {
         return this.width - 10;
      }
   }
}
