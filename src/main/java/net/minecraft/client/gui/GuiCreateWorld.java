package net.minecraft.client.gui;

import java.util.Random;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.MathHelper;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.WorldInfo;
import org.lwjgl.input.Keyboard;

public class GuiCreateWorld extends GuiScreen {
    public String gamemodeName = "survival";
    public GuiTextField levelNameField;
    public GuiTextField seedField;
    public String saveDirectoryName;
    public boolean structures = true;
    public String generatorOptions = "";
    public boolean tweakedCheats;
    public boolean cheatsEnabled;
    public boolean bonusChest;
    public boolean hardcore;
    public boolean creatingLevel;
    public boolean moreOptionsOpen;
    public GuiButton gameModeButton;
    public GuiButton moreWorldOptionsButton;
    public GuiButton generateStructuresButton;
    public GuiButton bonusChestButton;
    public GuiButton mapTypeSwitchButton;
    public GuiButton allowCommandsButton;
    public GuiButton customizeButton;
    public String firstGameModeDescriptionLine;
    public String secondGameModeDescriptionLine;
    public GuiScreen parent;
    public String seed;
    public int generatorType;
    public String levelName;
    public static String[] ILLEGAL_FOLDER_NAMES = new String[]{
        "CON",
        "COM",
        "PRN",
        "AUX",
        "CLOCK$",
        "NUL",
        "COM1",
        "COM2",
        "COM3",
        "COM4",
        "COM5",
        "COM6",
        "COM7",
        "COM8",
        "COM9",
        "LPT1",
        "LPT2",
        "LPT3",
        "LPT4",
        "LPT5",
        "LPT6",
        "LPT7",
        "LPT8",
        "LPT9"
    };

    public GuiCreateWorld(GuiScreen guiScreen) {
        this.parent = guiScreen;
        this.seed = "";
        this.levelName = I18n.format("selectWorld.newWorld");
    }

    @Override
    public void updateScreen() {
        this.levelNameField.updateCursorCounter();
        this.seedField.updateCursorCounter();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("selectWorld.create")));
        this.buttonList.add(new GuiButton(1, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));
        this.buttonList.add(this.gameModeButton = new GuiButton(2, this.width / 2 - 75, 115, 150, 20, I18n.format("selectWorld.gameMode")));
        this.buttonList.add(this.moreWorldOptionsButton = new GuiButton(3, this.width / 2 - 75, 187, 150, 20, I18n.format("selectWorld.moreWorldOptions")));
        this.buttonList.add(this.generateStructuresButton = new GuiButton(4, this.width / 2 - 155, 100, 150, 20, I18n.format("selectWorld.mapFeatures")));
        this.generateStructuresButton.visible = false;
        this.buttonList.add(this.bonusChestButton = new GuiButton(7, this.width / 2 + 5, 151, 150, 20, I18n.format("selectWorld.bonusItems")));
        this.bonusChestButton.visible = false;
        this.buttonList.add(this.mapTypeSwitchButton = new GuiButton(5, this.width / 2 + 5, 100, 150, 20, I18n.format("selectWorld.mapType")));
        this.mapTypeSwitchButton.visible = false;
        this.buttonList.add(this.allowCommandsButton = new GuiButton(6, this.width / 2 - 155, 151, 150, 20, I18n.format("selectWorld.allowCommands")));
        this.allowCommandsButton.visible = false;
        this.buttonList.add(this.customizeButton = new GuiButton(8, this.width / 2 + 5, 120, 150, 20, I18n.format("selectWorld.customizeType")));
        this.customizeButton.visible = false;
        this.levelNameField = new GuiTextField(this.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
        this.levelNameField.setFocused(true);
        this.levelNameField.setText(this.levelName);
        this.seedField = new GuiTextField(this.fontRendererObj, this.width / 2 - 100, 60, 200, 20);
        this.seedField.setText(this.seed);
        this.setMoreOptionsOpen(this.moreOptionsOpen);
        this.updateSaveFolderName();
        this.updateSettingsLabels();
    }

    public void updateSaveFolderName() {
        this.saveDirectoryName = this.levelNameField.getText().trim();

        for (char c : ChatAllowedCharacters.allowedCharactersArray) {
            this.saveDirectoryName = this.saveDirectoryName.replace(c, '_');
        }

        if (MathHelper.stringNullOrLengthZero(this.saveDirectoryName)) {
            this.saveDirectoryName = "World";
        }

        this.saveDirectoryName = checkDirectoryName(this.mc.getSaveLoader(), this.saveDirectoryName);
    }

    public void updateSettingsLabels() {
        this.gameModeButton.displayString = I18n.format("selectWorld.gameMode") + " " + I18n.format("selectWorld.gameMode." + this.gamemodeName);
        this.firstGameModeDescriptionLine = I18n.format("selectWorld.gameMode." + this.gamemodeName + ".line1");
        this.secondGameModeDescriptionLine = I18n.format("selectWorld.gameMode." + this.gamemodeName + ".line2");
        this.generateStructuresButton.displayString = I18n.format("selectWorld.mapFeatures") + " ";
        if (this.structures) {
            this.generateStructuresButton.displayString = this.generateStructuresButton.displayString + I18n.format("options.on");
        } else {
            this.generateStructuresButton.displayString = this.generateStructuresButton.displayString + I18n.format("options.off");
        }

        this.bonusChestButton.displayString = I18n.format("selectWorld.bonusItems") + " ";
        if (this.bonusChest && !this.hardcore) {
            this.bonusChestButton.displayString = this.bonusChestButton.displayString + I18n.format("options.on");
        } else {
            this.bonusChestButton.displayString = this.bonusChestButton.displayString + I18n.format("options.off");
        }

        this.mapTypeSwitchButton.displayString = I18n.format("selectWorld.mapType")
            + " "
            + I18n.format(WorldType.worldTypes[this.generatorType].getTranslateName());
        this.allowCommandsButton.displayString = I18n.format("selectWorld.allowCommands") + " ";
        if (this.tweakedCheats && !this.hardcore) {
            this.allowCommandsButton.displayString = this.allowCommandsButton.displayString + I18n.format("options.on");
        } else {
            this.allowCommandsButton.displayString = this.allowCommandsButton.displayString + I18n.format("options.off");
        }
    }

    public static String checkDirectoryName(ISaveFormat saveFormat, String str) {
        str = str.replaceAll("[\\./\"]", "_");

        for (String str2 : ILLEGAL_FOLDER_NAMES) {
            if (str.equalsIgnoreCase(str2)) {
                str = "_" + str + "_";
            }
        }

        while (saveFormat.getWorldInfo(str) != null) {
            str = str + "-";
        }

        return str;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
    }

    @Override
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        if (button.enabled) {
            if (button.id == 1) {
                this.mc.displayGuiScreen(this.parent);
            } else if (button.id == 0) {
                this.mc.displayGuiScreen(null);
                if (this.creatingLevel) {
                    return;
                }

                this.creatingLevel = true;
                long nextLong = new Random().nextLong();
                String text = this.seedField.getText();
                if (!MathHelper.stringNullOrLengthZero(text)) {
                    try {
                        long parseLong = Long.parseLong(text);
                        if (parseLong != 0L) {
                            nextLong = parseLong;
                        }
                    } catch (NumberFormatException e) {
                        nextLong = text.hashCode();
                    }
                }

                GameType byName = GameType.getByName(this.gamemodeName);
                WorldSettings worldSettings = new WorldSettings(nextLong, byName, this.structures, this.hardcore, WorldType.worldTypes[this.generatorType]);
                worldSettings.setWorldName(this.generatorOptions);
                if (this.bonusChest && !this.hardcore) {
                    worldSettings.enableBonusChest();
                }

                if (this.tweakedCheats && !this.hardcore) {
                    worldSettings.enableCommands();
                }

                this.mc.launchIntegratedServer(this.saveDirectoryName, this.levelNameField.getText().trim(), worldSettings);
            } else if (button.id == 3) {
                this.toggleMoreOptions();
            } else if (button.id == 2) {
                if (this.gamemodeName.equals("survival")) {
                    if (!this.cheatsEnabled) {
                        this.tweakedCheats = false;
                    }

                    this.hardcore = false;
                    this.gamemodeName = "hardcore";
                    this.hardcore = true;
                    this.allowCommandsButton.enabled = false;
                    this.bonusChestButton.enabled = false;
                    this.updateSettingsLabels();
                } else if (this.gamemodeName.equals("hardcore")) {
                    if (!this.cheatsEnabled) {
                        this.tweakedCheats = true;
                    }

                    this.hardcore = false;
                    this.gamemodeName = "creative";
                    this.updateSettingsLabels();
                    this.hardcore = false;
                    this.allowCommandsButton.enabled = true;
                    this.bonusChestButton.enabled = true;
                } else {
                    if (!this.cheatsEnabled) {
                        this.tweakedCheats = false;
                    }

                    this.gamemodeName = "survival";
                    this.updateSettingsLabels();
                    this.allowCommandsButton.enabled = true;
                    this.bonusChestButton.enabled = true;
                    this.hardcore = false;
                }

                this.updateSettingsLabels();
            } else if (button.id == 4) {
                this.structures = !this.structures;
                this.updateSettingsLabels();
            } else if (button.id == 7) {
                this.bonusChest = !this.bonusChest;
                this.updateSettingsLabels();
            } else if (button.id == 5) {
                this.generatorType++;
                if (this.generatorType >= WorldType.worldTypes.length) {
                    this.generatorType = 0;
                }

                while (WorldType.worldTypes[this.generatorType] == null || !WorldType.worldTypes[this.generatorType].getCanBeCreated()) {
                    this.generatorType++;
                    if (this.generatorType >= WorldType.worldTypes.length) {
                        this.generatorType = 0;
                    }
                }

                this.generatorOptions = "";
                this.updateSettingsLabels();
                this.setMoreOptionsOpen(this.moreOptionsOpen);
            } else if (button.id == 6) {
                this.cheatsEnabled = true;
                this.tweakedCheats = !this.tweakedCheats;
                this.updateSettingsLabels();
            } else if (button.id == 8) {
                this.mc.displayGuiScreen(new GuiCreateFlatWorld(this, this.generatorOptions));
            }
        }
    }

    public void toggleMoreOptions() {
        this.setMoreOptionsOpen(!this.moreOptionsOpen);
    }

    public void setMoreOptionsOpen(boolean flag) {
        this.moreOptionsOpen = flag;
        this.gameModeButton.visible = !this.moreOptionsOpen;
        this.generateStructuresButton.visible = this.moreOptionsOpen;
        this.bonusChestButton.visible = this.moreOptionsOpen;
        this.mapTypeSwitchButton.visible = this.moreOptionsOpen;
        this.allowCommandsButton.visible = this.moreOptionsOpen;
        this.customizeButton.visible = this.moreOptionsOpen && WorldType.worldTypes[this.generatorType] == WorldType.FLAT;
        if (this.moreOptionsOpen) {
            this.moreWorldOptionsButton.displayString = I18n.format("gui.done");
        } else {
            this.moreWorldOptionsButton.displayString = I18n.format("selectWorld.moreWorldOptions");
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws java.io.IOException {
        if (this.levelNameField.isFocused() && !this.moreOptionsOpen) {
            this.levelNameField.textboxKeyTyped(typedChar, keyCode);
            this.levelName = this.levelNameField.getText();
        } else if (this.seedField.isFocused() && this.moreOptionsOpen) {
            this.seedField.textboxKeyTyped(typedChar, keyCode);
            this.seed = this.seedField.getText();
        }

        if (keyCode == 28 || keyCode == 156) {
            this.actionPerformed(this.buttonList.get(0));
        }

        this.buttonList.get(0).enabled = this.levelNameField.getText().length() > 0;
        this.updateSaveFolderName();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws java.io.IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        if (this.moreOptionsOpen) {
            this.seedField.mouseClicked(mouseX, mouseY, mouseButton);
        } else {
            this.levelNameField.mouseClicked(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("selectWorld.create"), this.width / 2, 20, -1);
        if (this.moreOptionsOpen) {
            this.drawString(this.fontRendererObj, I18n.format("selectWorld.enterSeed"), this.width / 2 - 100, 47, -6250336);
            this.drawString(this.fontRendererObj, I18n.format("selectWorld.seedInfo"), this.width / 2 - 100, 85, -6250336);
            this.drawString(this.fontRendererObj, I18n.format("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, -6250336);
            this.drawString(this.fontRendererObj, I18n.format("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, -6250336);
            this.seedField.drawTextBox();
            if (WorldType.worldTypes[this.generatorType].showWorldInfoNotice()) {
                this.fontRendererObj
                    .drawSplitString(
                        I18n.format(WorldType.worldTypes[this.generatorType].getInfoTranslationKey()),
                        this.mapTypeSwitchButton.xPosition + 2,
                        this.mapTypeSwitchButton.yPosition + 22,
                        this.mapTypeSwitchButton.getButtonWidth(),
                        10526880
                    );
            }
        } else {
            this.drawString(this.fontRendererObj, I18n.format("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
            this.drawString(this.fontRendererObj, I18n.format("selectWorld.resultFolder") + " " + this.saveDirectoryName, this.width / 2 - 100, 85, -6250336);
            this.levelNameField.drawTextBox();
            this.drawString(this.fontRendererObj, this.firstGameModeDescriptionLine, this.width / 2 - 100, 137, -6250336);
            this.drawString(this.fontRendererObj, this.secondGameModeDescriptionLine, this.width / 2 - 100, 149, -6250336);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public void copyWorld(WorldInfo worldInfo) {
        this.levelName = I18n.format("selectWorld.newWorld.copyOf", worldInfo.getWorldName());
        this.seed = worldInfo.getSeed() + "";
        this.generatorType = worldInfo.getTerrainType().getWorldTypeID();
        this.generatorOptions = worldInfo.getGeneratorOptions();
        this.structures = worldInfo.isMapFeaturesEnabled();
        this.tweakedCheats = worldInfo.areCommandsAllowed();
        if (worldInfo.isHardcoreModeEnabled()) {
            this.gamemodeName = "hardcore";
        } else if (worldInfo.getGameType().isSurvivalOrAdventure()) {
            this.gamemodeName = "survival";
        } else if (worldInfo.getGameType().isCreative()) {
            this.gamemodeName = "creative";
        }
    }
}
