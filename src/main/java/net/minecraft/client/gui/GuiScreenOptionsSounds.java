package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class GuiScreenOptionsSounds extends GuiScreen {
    public String name = "Options";
    public GuiScreen parent;
    public GameSettings game_settings_4;
    public String off;

    public GuiScreenOptionsSounds(GuiScreen guiScreen, GameSettings gameSettings) {
        this.parent = guiScreen;
        this.game_settings_4 = gameSettings;
    }

    @Override
    public void initGui() {
        byte b = 0;
        this.name = I18n.format("options.sounds.title");
        this.off = I18n.format("options.off");
        this.buttonList
            .add(
                new GuiScreenOptionsSounds.Button(
                    SoundCategory.MASTER.getCategoryId(), this.width / 2 - 155 + b % 2 * 160, this.height / 6 - 12 + 24 * (b >> 1), SoundCategory.MASTER, true
                )
            );
        int n = b + 2;

        for (SoundCategory soundCategory : SoundCategory.values()) {
            if (soundCategory != SoundCategory.MASTER) {
                this.buttonList
                    .add(
                        new GuiScreenOptionsSounds.Button(
                            soundCategory.getCategoryId(), this.width / 2 - 155 + n % 2 * 160, this.height / 6 - 12 + 24 * (n >> 1), soundCategory, false
                        )
                    );
                n++;
            }
        }

        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 168, I18n.format("gui.done")));
    }

    @Override
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        if (button.enabled && button.id == 200) {
            this.mc.gameSettings.saveOptions();
            this.mc.displayGuiScreen(this.parent);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.name, this.width / 2, 15, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    public String getSoundVolume(SoundCategory soundCategory) {
        float soundLevel = this.game_settings_4.getSoundLevel(soundCategory);
        return soundLevel == 0.0F ? this.off : (int)(soundLevel * 100.0F) + "%";
    }

    public class Button extends GuiButton {
        public float floatValue2 = 1.0F;
        public SoundCategory soundCategory;
        public String text;
        public boolean flag;

        public Button(int n, int n2, int n3, SoundCategory soundCategory, boolean flag) {
            super(n, n2, n3, flag ? 310 : 150, 20, "");
            this.soundCategory = soundCategory;
            this.text = I18n.format("soundCategory." + soundCategory.getCategoryName());
            this.displayString = this.text + ": " + GuiScreenOptionsSounds.this.getSoundVolume(soundCategory);
            this.floatValue2 = GuiScreenOptionsSounds.this.game_settings_4.getSoundLevel(soundCategory);
        }

        @Override
        public int getHoverState(boolean mouseOver) {
            return 0;
        }

        @Override
        public void mouseDragged(Minecraft mc, int mouseX, int mouseY) {
            if (this.visible) {
                if (this.flag) {
                    this.floatValue2 = (float)(mouseX - (this.xPosition + 4)) / (this.width - 8);
                    if (this.floatValue2 < 0.0F) {
                        this.floatValue2 = 0.0F;
                    }

                    if (this.floatValue2 > 1.0F) {
                        this.floatValue2 = 1.0F;
                    }

                    mc.gameSettings.setSoundLevel(this.soundCategory, this.floatValue2);
                    mc.gameSettings.saveOptions();
                    this.displayString = this.text + ": " + GuiScreenOptionsSounds.this.getSoundVolume(this.soundCategory);
                }

                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                drawTexturedRect(this.xPosition + (int)(this.floatValue2 * (this.width - 8)), this.yPosition, 0.0F, 66.0F, 4, 20);
                drawTexturedRect(this.xPosition + (int)(this.floatValue2 * (this.width - 8)) + 4, this.yPosition, 196.0F, 66.0F, 4, 20);
            }
        }

        @Override
        public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) throws java.io.IOException {
            if (super.mousePressed(mc, mouseX, mouseY)) {
                this.floatValue2 = (float)(mouseX - (this.xPosition + 4)) / (this.width - 8);
                if (this.floatValue2 < 0.0F) {
                    this.floatValue2 = 0.0F;
                }

                if (this.floatValue2 > 1.0F) {
                    this.floatValue2 = 1.0F;
                }

                mc.gameSettings.setSoundLevel(this.soundCategory, this.floatValue2);
                mc.gameSettings.saveOptions();
                this.displayString = this.text + ": " + GuiScreenOptionsSounds.this.getSoundVolume(this.soundCategory);
                this.flag = true;
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void playPressSound(SoundHandler soundHandlerIn) {
        }

        @Override
        public void mouseReleased(int mouseX, int mouseY) {
            if (this.flag) {
                if (this.soundCategory == SoundCategory.MASTER) {
                    float f = 1.0F;
                } else {
                    GuiScreenOptionsSounds.this.game_settings_4.getSoundLevel(this.soundCategory);
                }

                GuiScreenOptionsSounds.this.mc
                    .getSoundHandler()
                    .playSound(PositionedSoundRecord.createPositionedSoundRecord(new ResourceLocation("gui.button.press"), 1.0F));
            }

            this.flag = false;
        }
    }
}
