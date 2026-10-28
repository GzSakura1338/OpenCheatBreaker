package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;

public class GuiErrorScreen extends GuiScreen {
    public String title;
    public String message;

    public GuiErrorScreen(String str, String str2) {
        this.title = str;
        this.message = str2;
    }

    @Override
    public void initGui() throws java.io.IOException {
        super.initGui();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, 140, I18n.format("gui.cancel")));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        drawVerticalGradient(0.0F, 0.0F, this.width, this.height, -12574688, -11530224);
        this.drawCenteredString(this.fontRendererObj, this.title, this.width / 2, 90, 16777215);
        this.drawCenteredString(this.fontRendererObj, this.message, this.width / 2, 110, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        this.mc.displayGuiScreen(null);
    }
}
