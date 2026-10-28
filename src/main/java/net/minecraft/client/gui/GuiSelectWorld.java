package net.minecraft.client.gui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.SaveFormatComparator;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuiSelectWorld extends GuiScreen implements GuiYesNoCallback {
    public static Logger logger = LogManager.getLogger();
    public DateFormat dateFormat = new SimpleDateFormat();
    public String title = "Select world";
    public String[] gameModeTexts = new String[3];
    public boolean joinedWorld;
    public int selectedWorld;
    public java.util.List worlds;
    public GuiSelectWorld.List list;
    public String defaultWorldName;
    public String mustConvertText;
    public GuiScreen parent;
    public boolean confimingDelete;
    public GuiButton deleteButton;
    public GuiButton selectButton;
    public GuiButton renameButton;
    public GuiButton recreateButton;

    public GuiSelectWorld(GuiScreen guiScreen) {
        this.parent = guiScreen;
    }

    @Override
    public void initGui() throws java.io.IOException {
        this.title = I18n.format("selectWorld.title");

        try {
            this.loadWorlds();
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
        this.list = new GuiSelectWorld.List();
        this.list.registerScrollButtons(4, 5);
        this.initButtons();
    }

    public void loadWorlds() throws AnvilConverterException {
        ISaveFormat saveLoader = this.mc.getSaveLoader();
        this.worlds = saveLoader.getSaveList();
        Collections.sort(this.worlds);
        this.selectedWorld = -1;
    }

    public String getWorldFileName(int n) {
        return ((SaveFormatComparator)this.worlds.get(n)).getFileName();
    }

    public String getWorldName(int n) {
        String displayName = ((SaveFormatComparator)this.worlds.get(n)).getDisplayName();
        if (displayName == null || MathHelper.stringNullOrLengthZero(displayName)) {
            displayName = I18n.format("selectWorld.world") + " " + (n + 1);
        }

        return displayName;
    }

    public void initButtons() {
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
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        if (button.enabled) {
            if (button.id == 2) {
                String worldName = this.getWorldName(this.selectedWorld);
                if (worldName != null) {
                    this.confimingDelete = true;
                    GuiYesNo createDeleteWarningScreen2 = createDeleteWarningScreen(this, worldName, this.selectedWorld);
                    this.mc.displayGuiScreen(createDeleteWarningScreen2);
                }
            } else if (button.id == 1) {
                this.joinWorld(this.selectedWorld);
            } else if (button.id == 3) {
                this.mc.displayGuiScreen(new GuiCreateWorld(this));
            } else if (button.id == 6) {
                this.mc.displayGuiScreen(new GuiRenameWorld(this, this.getWorldFileName(this.selectedWorld)));
            } else if (button.id == 0) {
                this.mc.displayGuiScreen(this.parent);
            } else if (button.id == 7) {
                GuiCreateWorld guiCreateWorld = new GuiCreateWorld(this);
                ISaveHandler saveLoader = this.mc.getSaveLoader().getSaveLoader(this.getWorldFileName(this.selectedWorld), false);
                WorldInfo loadWorldInfo = saveLoader.loadWorldInfo();
                saveLoader.flush();
                guiCreateWorld.copyWorld(loadWorldInfo);
                this.mc.displayGuiScreen(guiCreateWorld);
            } else {
                this.list.actionPerformed(button);
            }
        }
    }

    public void joinWorld(int n) throws java.io.IOException {
        this.mc.displayGuiScreen(null);
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
                this.mc.launchIntegratedServer(worldFileName, worldName, null);
            }
        }
    }

    @Override
    public void confirmClicked(boolean result, int id) throws java.io.IOException {
        if (this.confimingDelete) {
            this.confimingDelete = false;
            if (result) {
                ISaveFormat saveLoader = this.mc.getSaveLoader();
                saveLoader.flushCache();
                saveLoader.deleteWorldDirectory(this.getWorldFileName(id));

                try {
                    this.loadWorlds();
                } catch (AnvilConverterException e) {
                    logger.error("Couldn't load level list", e);
                }
            }

            this.mc.displayGuiScreen(this);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        this.list.drawScreen(mouseX, mouseY, partialTicks);
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 20, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public static GuiYesNo createDeleteWarningScreen(GuiYesNoCallback guiYesNoCallback, String str, int n) {
        String format = I18n.format("selectWorld.deleteQuestion");
        String str2 = "'" + str + "' " + I18n.format("selectWorld.deleteWarning");
        String format2 = I18n.format("selectWorld.deleteButton");
        String format3 = I18n.format("gui.cancel");
        return new GuiYesNo(guiYesNoCallback, format, str2, format2, format3, n);
    }

    public class List extends GuiSlot {
        public List() {
            super(GuiSelectWorld.this.mc, GuiSelectWorld.this.width, GuiSelectWorld.this.height, 32, GuiSelectWorld.this.height - 64, 36);
        }

        @Override
        public int getSize() {
            return GuiSelectWorld.this.worlds.size();
        }

        @Override
        public void elementClicked(int n, boolean flag, int n2, int n3) throws java.io.IOException {
            GuiSelectWorld.this.selectedWorld = n;
            boolean flag2 = GuiSelectWorld.this.selectedWorld >= 0 && GuiSelectWorld.this.selectedWorld < this.getSize();
            GuiSelectWorld.this.selectButton.enabled = flag2;
            GuiSelectWorld.this.deleteButton.enabled = flag2;
            GuiSelectWorld.this.renameButton.enabled = flag2;
            GuiSelectWorld.this.recreateButton.enabled = flag2;
            if (flag && flag2) {
                GuiSelectWorld.this.joinWorld(n);
            }
        }

        @Override
        public boolean isSelected(int n) {
            return n == GuiSelectWorld.this.selectedWorld;
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
        public void drawSlot(int n, int n2, int n3, int n4, Tessellator tessellator, int n5, int n6) {
            SaveFormatComparator saveFormatComparator = (SaveFormatComparator)GuiSelectWorld.this.worlds.get(n);
            String displayName = saveFormatComparator.getDisplayName();
            if (displayName == null || MathHelper.stringNullOrLengthZero(displayName)) {
                displayName = GuiSelectWorld.this.defaultWorldName + " " + (n + 1);
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

            GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, displayName, n2 + 2, n3 + 1, 16777215);
            GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, fileName, n2 + 2, n3 + 12, 8421504);
            GuiSelectWorld.this.drawString(GuiSelectWorld.this.fontRendererObj, str, n2 + 2, n3 + 12 + 10, 8421504);
        }
    }
}
