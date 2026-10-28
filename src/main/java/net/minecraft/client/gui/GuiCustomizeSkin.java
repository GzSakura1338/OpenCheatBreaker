package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EnumPlayerModelParts;

public class GuiCustomizeSkin extends GuiScreen {
    public GuiScreen guiScreen;
    public String text;

    @Override
    public void initGui() {
        int n = 0;
        this.text = "Skin Customization";

        for (EnumPlayerModelParts enumPlayerModelParts : EnumPlayerModelParts.values()) {
            this.buttonList
                .add(new GuiCustomizeSkin.ButtonPart(enumPlayerModelParts.getPartId(), this.width / 2 - 155 + n % 2 * 160, this.height / 6 + 24 * (n >> 1), 150, 20, enumPlayerModelParts));
            n++;
        }

        if (n % 2 == 1) {
            n++;
        }

        this.buttonList.add(new GuiButton(200, this.width / 2 - 100, this.height / 6 + 24 * (n >> 1), I18n.format("gui.done")));
    }

    public String createStringBuilder(EnumPlayerModelParts enumPlayerModelParts) {
        boolean flag = enumPlayerModelParts == EnumPlayerModelParts.CAPE ? this.mc.gameSettings.showCape : this.mc.gameSettings.createHashSet().contains(enumPlayerModelParts);
        String str = flag ? "ON" : "OFF";
        return enumPlayerModelParts.getText2() + ": " + str;
    }

    @Override
    public void drawScreen(int n, int n2, float f) throws java.io.IOException {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.text, this.width / 2, 20, 16777215);
        super.drawScreen(n, n2, f);
    }

    public GuiCustomizeSkin(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
    }

    @Override
    public void actionPerformed(GuiButton guiButton) throws java.io.IOException {
        if (guiButton.enabled) {
            if (guiButton.id == 200) {
                this.mc.gameSettings.saveOptions();
                this.mc.displayGuiScreen(this.guiScreen);
            } else if (guiButton instanceof GuiCustomizeSkin.ButtonPart) {
                EnumPlayerModelParts playerModelParts = ((GuiCustomizeSkin.ButtonPart)guiButton).playerModelParts;
                if (playerModelParts == EnumPlayerModelParts.CAPE) {
                    this.mc.gameSettings.showCape = !this.mc.gameSettings.showCape;
                    this.mc.gameSettings.sendSettingsToServer();
                    this.mc.thePlayer.setHideCape(!this.mc.gameSettings.showCape);
                } else {
                    this.mc.gameSettings.handleEnumPlayerModelParts(playerModelParts);
                }

                guiButton.displayString = this.createStringBuilder(playerModelParts);
            }
        }
    }

    public class ButtonPart extends GuiButton {
        public EnumPlayerModelParts playerModelParts;

        public ButtonPart(int n, int n2, int n3, int n4, int n5, EnumPlayerModelParts enumPlayerModelParts) {
            super(n, n2, n3, n4, n5, GuiCustomizeSkin.this.createStringBuilder(enumPlayerModelParts));
            this.playerModelParts = enumPlayerModelParts;
        }
    }
}
