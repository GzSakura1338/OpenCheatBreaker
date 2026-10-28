package net.minecraft.client.gui;

import com.cheatbreaker.client.ui.screen.ResourcePacksScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundEventAccessorComposite;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.GameSettings.Options;

public class GuiOptions extends GuiScreen implements GuiYesNoCallback {
    public static Options[] optionsArray = new Options[]{Options.FOV, Options.DIFFICULTY};
    public String title = "Options";
    public GuiScreen parent;
    public GameSettings game_settings_1;

    public GuiOptions(GuiScreen guiScreen, GameSettings gameSettings) {
        this.parent = guiScreen;
        this.game_settings_1 = gameSettings;
    }

    @Override
    public void initGui() {
        int n = 0;
        this.title = I18n.format("options.title");

        for (Options options : optionsArray) {
            if (options.getEnumFloat()) {
                this.buttonList
                    .add(new GuiOptionSlider(options.returnEnumOrdinal(), this.width / 2 - 155 + n % 2 * 160, this.height / 6 - 12 + 24 * (n >> 1), options));
            } else {
                GuiOptionButton guiOptionButton = new GuiOptionButton(
                    options.returnEnumOrdinal(),
                    this.width / 2 - 155 + n % 2 * 160,
                    this.height / 6 - 12 + 24 * (n >> 1),
                    options,
                    this.game_settings_1.getKeyBinding(options)
                );
                if (options == Options.DIFFICULTY && this.mc.theWorld != null && this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled()) {
                    guiOptionButton.enabled = false;
                    guiOptionButton.displayString = I18n.format("options.difficulty") + ": " + I18n.format("options.difficulty.hardcore");
                }

                this.buttonList.add(guiOptionButton);
            }

            n++;
        }

        this.buttonList.add(new GuiButton(110, this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, "Skin Customization..."));
        this.buttonList
            .add(
                new GuiButton(8675309, this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, "Super Secret Settings...") {
                    @Override
                    public void playPressSound(SoundHandler soundHandlerIn) {
                        SoundEventAccessorComposite randomSoundFromCategories = soundHandlerIn.getRandomSoundFromCategories(
                            SoundCategory.ANIMALS, SoundCategory.BLOCKS, SoundCategory.MOBS, SoundCategory.PLAYERS, SoundCategory.WEATHER
                        );
                        if (randomSoundFromCategories != null) {
                            soundHandlerIn.playSound(PositionedSoundRecord.createPositionedSoundRecord(randomSoundFromCategories.getSoundEventLocation(), 0.5F));
                        }
                    }
                }
            );
        this.buttonList.add(new GuiButton(106, this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.sounds")));
        this.buttonList.add(new GuiButton(101, this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.video")));
        this.buttonList.add(new GuiButton(100, this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.format("options.controls")));
        this.buttonList.add(new GuiButton(102, this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.format("options.language")));
        this.buttonList.add(new GuiButton(103, this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.multiplayer.title")));
        this.buttonList.add(new GuiButton(105, this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.format("options.resourcepack")));
        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")));
    }

    @Override
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        if (button.enabled) {
            if (button.id < 100 && button instanceof GuiOptionButton) {
                this.game_settings_1.setOptionValue(((GuiOptionButton)button).returnEnumOptions(), 1);
                button.displayString = this.game_settings_1.getKeyBinding(Options.getEnumOptions(button.id));
            }

            if (button.id == 8675309) {
                this.mc.entityRenderer.activateNextShader();
            }

            if (button.id == 101) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiVideoSettings(this, this.game_settings_1));
            }

            if (button.id == 100) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiControls(this, this.game_settings_1));
            }

            if (button.id == 102) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiLanguage(this, this.game_settings_1, this.mc.getLanguageManager()));
            }

            if (button.id == 103) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new ScreenChatOptions(this, this.game_settings_1));
            }

            if (button.id == 104) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiSnooper(this, this.game_settings_1));
            }

            if (button.id == 200) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.parent);
            }

            if (button.id == 105) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new ResourcePacksScreen(Minecraft.getMinecraft().currentScreen));
            }

            if (button.id == 106) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiScreenOptionsSounds(this, this.game_settings_1));
            }

            if (button.id == 110) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(new GuiCustomizeSkin(this));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 15, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
