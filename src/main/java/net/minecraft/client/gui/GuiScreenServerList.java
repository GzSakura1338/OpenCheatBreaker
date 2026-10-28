package net.minecraft.client.gui;

import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;
import org.lwjgl.input.Keyboard;

public class GuiScreenServerList extends GuiScreen {
    public GuiScreen parent;
    public ServerData info;
    public GuiTextField serverField;

    public GuiScreenServerList(GuiScreen guiScreen, ServerData serverData) {
        this.parent = guiScreen;
        this.info = serverData;
    }

    @Override
    public void updateScreen() {
        this.serverField.updateCursorCounter();
    }

    @Override
    public void initGui() {
        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 96 + 12, I18n.format("selectServer.select")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
        this.serverField = new GuiTextField(this.fontRendererObj, this.width / 2 - 100, 116, 200, 20);
        this.serverField.setMaxStringLength(128);
        this.serverField.setFocused(true);
        this.serverField.setText(this.mc.gameSettings.lastServer);
        this.buttonList.get(0).enabled = this.serverField.getText().length() > 0 && this.serverField.getText().split(":").length > 0;
    }

    @Override
    public void onGuiClosed() {
        Keyboard.enableRepeatEvents(false);
        this.mc.gameSettings.lastServer = this.serverField.getText();
        this.mc.gameSettings.saveOptions();
    }

    @Override
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        if (button.enabled) {
            if (button.id == 1) {
                this.parent.confirmClicked(false, 0);
            } else if (button.id == 0) {
                this.info.serverIP = this.serverField.getText();
                this.parent.confirmClicked(true, 0);
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws java.io.IOException {
        if (this.serverField.textboxKeyTyped(typedChar, keyCode)) {
            this.buttonList.get(0).enabled = this.serverField.getText().length() > 0 && this.serverField.getText().split(":").length > 0;
        } else if (keyCode == 28 || keyCode == 156) {
            this.actionPerformed(this.buttonList.get(0));
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws java.io.IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.serverField.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("selectServer.direct"), this.width / 2, 20, 16777215);
        this.drawString(this.fontRendererObj, I18n.format("addServer.enterIp"), this.width / 2 - 100, 100, 10526880);
        this.serverField.drawTextBox();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
