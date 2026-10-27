package net.minecraft.client.gui;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiSelectWorld extends GuiScreen implements GuiYesNoCallback {
   public static Logger logger = LogManager.getLogger();
   public DateFormat dateFormat = new SimpleDateFormat();
   public String screenTitle = "Select world";
   public String[] gameModeTexts = new String[4];
   public boolean joinedWorld;
   public int selectedIndex;
   public java.util.List<SaveFormatComparator> worlds;
   public GuiSelectWorld.List availableWorlds;
   public String defaultWorldName;
   public String mustConvertText;
   public GuiScreen parentScreen;
   public boolean confirmingDelete;
   public GuiButton deleteButton;
   public GuiButton selectButton;
   public GuiButton renameButton;
   public GuiButton recreateButton;

   public GuiSelectWorld(GuiScreen parentScreenIn) {
      this.parentScreen = parentScreenIn;
   }

   @Override
   public void initGui() {
      this.screenTitle = I18n.format("selectWorld.title");

      try {
         this.loadLevelList();
      } catch (AnvilConverterException e) {
         logger.error("Couldn't load level list", e);
         this.mc.displayGuiScreen(new GuiErrorScreen("Unable to load worlds", e.getMessage()));
         return;
      }

      this.defaultWorldName = I18n.format("selectWorld.world");
      this.mustConvertText = I18n.format("selectWorld.conversion");
      this.gameModeTexts[GameType.SURVIVAL.getID()] = I18n.format("gameMode.survival");
      this.gameModeTexts[GameType.CREATIVE.getID()] = I18n.format("gameMode.creative");
      this.gameModeTexts[GameType.ADVENTURE.getID()] = I18n.format("gameMode.adventure");
      this.gameModeTexts[GameType.SPECTATOR.getID()] = I18n.format("gameMode.spectator");
      this.availableWorlds = new GuiSelectWorld.List(this.mc);
      this.availableWorlds.registerScrollButtons(4, 5);
      this.addWorldSelectionButtons();
   }

   @Override
   public void handleMouseInput() throws IOException {
      super.handleMouseInput();
      this.availableWorlds.handleMouseInput();
   }

   public void loadLevelList() throws AnvilConverterException {
      ISaveFormat saveLoader = this.mc.getSaveLoader();
      this.worlds = saveLoader.getSaveList();
      Collections.sort(this.worlds);
      this.selectedIndex = -1;
   }

   public String getWorldFileName(int n) {
      return this.worlds.get(n).getFileName();
   }

   public String getWorldName(int n) {
      String displayName = this.worlds.get(n).getDisplayName();
      if (StringUtils.isEmpty(displayName)) {
         displayName = I18n.format("selectWorld.world") + " " + (n + 1);
      }

      return displayName;
   }

   public void addWorldSelectionButtons() {
      this.buttonList.add(this.selectButton = new GuiButton(1, this.width / 2 - 154, this.height - 52, 150, 20, I18n.format("selectWorld.select")));
      this.buttonList.add(new GuiButton(3, this.width / 2 + 4, this.height - 52, 150, 20, I18n.format("selectWorld.create")));
      this.buttonList.add(this.renameButton = new GuiButton(6, this.width / 2 - 154, this.height - 28, 72, 20, I18n.format("selectWorld.rename")));
      this.buttonList.add(this.deleteButton = new GuiButton(2, this.width / 2 - 76, this.height - 28, 72, 20, I18n.format("selectWorld.delete")));
      this.buttonList.add(this.recreateButton = new GuiButton(7, this.width / 2 + 4, this.height - 28, 72, 20, I18n.format("selectWorld.recreate")));
      this.buttonList.add(new GuiButton(0, this.width / 2 + 82, this.height - 28, 72, 20, I18n.format("gui.cancel")));
      this.selectButton.enabled = false;
      this.deleteButton.enabled = false;
      this.renameButton.enabled = false;
      this.recreateButton.enabled = false;
   }

   @Override
   public void actionPerformed(GuiButton button) throws IOException {
      if (button.enabled) {
         if (button.id == 2) {
            String worldName = this.getWorldName(this.selectedIndex);
            if (worldName != null) {
               this.confirmingDelete = true;
               GuiYesNo makeDeleteWorldYesNo2 = makeDeleteWorldYesNo(this, worldName, this.selectedIndex);
               this.mc.displayGuiScreen(makeDeleteWorldYesNo2);
            }
         } else if (button.id == 1) {
            this.joinWorld(this.selectedIndex);
         } else if (button.id == 3) {
            this.mc.displayGuiScreen(new GuiCreateWorld(this));
         } else if (button.id == 6) {
            this.mc.displayGuiScreen(new GuiRenameWorld(this, this.getWorldFileName(this.selectedIndex)));
         } else if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
         } else if (button.id == 7) {
            GuiCreateWorld guiCreateWorld = new GuiCreateWorld(this);
            ISaveHandler saveLoader = this.mc.getSaveLoader().getSaveLoader(this.getWorldFileName(this.selectedIndex), false);
            WorldInfo loadWorldInfo = saveLoader.loadWorldInfo();
            saveLoader.flush();
            guiCreateWorld.recreateFromExistingWorld(loadWorldInfo);
            this.mc.displayGuiScreen(guiCreateWorld);
         } else {
            this.availableWorlds.actionPerformed(button);
         }
      }
   }

   public void joinWorld(int n) {
      this.mc.displayGuiScreen((GuiScreen)null);
      if (!this.joinedWorld) {
         this.joinedWorld = true;
         String worldFileName = this.getWorldFileName(n);
         if (worldFileName == null) {
            worldFileName = "World" + n;
         }

         String worldName = this.getWorldName(n);
         if (worldName == null) {
            worldName = "World" + n;
         }

         if (this.mc.getSaveLoader().canLoadWorld(worldFileName)) {
            this.mc.launchIntegratedServer(worldFileName, worldName, (WorldSettings)null);
         }
      }
   }

   @Override
   public void confirmClicked(boolean result, int id) {
      if (this.confirmingDelete) {
         this.confirmingDelete = false;
         if (result) {
            ISaveFormat saveLoader = this.mc.getSaveLoader();
            saveLoader.flushCache();
            saveLoader.deleteWorldDirectory(this.getWorldFileName(id));

            try {
               this.loadLevelList();
            } catch (AnvilConverterException e) {
               logger.error("Couldn't load level list", e);
            }
         }

         this.mc.displayGuiScreen(this);
      }
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      this.availableWorlds.drawScreen(mouseX, mouseY, partialTicks);
      this.drawCenteredString(this.fontRendererObj, this.screenTitle, this.width / 2, 20, 16777215);
      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   public static GuiYesNo makeDeleteWorldYesNo(GuiYesNoCallback selectWorld, String name, int id) {
      String format = I18n.format("selectWorld.deleteQuestion");
      String str = "'" + name + "' " + I18n.format("selectWorld.deleteWarning");
      String format2 = I18n.format("selectWorld.deleteButton");
      String format3 = I18n.format("gui.cancel");
      return new GuiYesNo(selectWorld, format, str, format2, format3, id);
   }

   class List extends GuiSlot {
      public List(Minecraft mcIn) {
         super(mcIn, GuiSelectWorld.this.width, GuiSelectWorld.this.height, 32, GuiSelectWorld.this.height - 64, 36);
      }

      @Override
      public int getSize() {
         return GuiSelectWorld.this.worlds.size();
      }

      @Override
      public void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY) {
         GuiSelectWorld.this.selectedIndex = slotIndex;
         boolean flag = GuiSelectWorld.this.selectedIndex >= 0 && GuiSelectWorld.this.selectedIndex < this.getSize();
         GuiSelectWorld.this.selectButton.enabled = flag;
         GuiSelectWorld.this.deleteButton.enabled = flag;
         GuiSelectWorld.this.renameButton.enabled = flag;
         GuiSelectWorld.this.recreateButton.enabled = flag;
         if (isDoubleClick && flag) {
            GuiSelectWorld.this.joinWorld(slotIndex);
         }
      }

      @Override
      public boolean isSelected(int slotIndex) {
         return slotIndex == GuiSelectWorld.this.selectedIndex;
      }

      @Override
      public int getContentHeight() {
         return GuiSelectWorld.this.worlds.size() * 36;
      }

      @Override
      public void drawBackground() {
         GuiSelectWorld.this.drawDefaultBackground();
      }

      @Override
      public void drawSlot(int entryID, int n, int n2, int n3, int mouseXIn, int mouseYIn) {
         SaveFormatComparator saveFormatComparator = GuiSelectWorld.this.worlds.get(entryID);
         String displayName = saveFormatComparator.getDisplayName();
         if (StringUtils.isEmpty(displayName)) {
            displayName = GuiSelectWorld.this.defaultWorldName + " " + (entryID + 1);
         }

         String fileName = saveFormatComparator.getFileName();
         fileName = fileName + " (" + GuiSelectWorld.this.dateFormat.format(new Date(saveFormatComparator.getLastTimePlayed()));
         fileName = fileName + ")";
         String str = "";
         if (saveFormatComparator.requiresConversion()) {
            str = GuiSelectWorld.this.mustConvertText + " " + str;
         } else {
            str = GuiSelectWorld.this.gameModeTexts[saveFormatComparator.getEnumGameType().getID()];
            if (saveFormatComparator.isHardcoreModeEnabled()) {
               str = EnumChatFormatting.DARK_RED + I18n.format("gameMode.hardcore") + EnumChatFormatting.RESET;
            }

            if (saveFormatComparator.getCheatsEnabled()) {
               str = str + ", " + I18n.format("selectWorld.cheats");
            }
         }

         GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, displayName, n + 2, n2 + 1, 16777215);
         GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, fileName, n + 2, n2 + 12, 8421504);
         GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, str, n + 2, n2 + 12 + 10, 8421504);
      }
   }
}
