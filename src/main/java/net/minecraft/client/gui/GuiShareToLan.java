package net.minecraft.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.util.ChatComponentStyle;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings.GameType;

public class GuiShareToLan extends GuiScreen {
    public String gameMode = "survival";
    public GuiButton allowCommandsButton;
    public GuiButton gameModeButton;
    public GuiScreen parent;
    public boolean allowCommands;

    public GuiShareToLan(GuiScreen guiScreen) {
        this.parent = guiScreen;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(101, this.width / 2 - 155, this.height - 28, 150, 20, I18n.format("lanServer.start")));
        this.buttonList.add(new GuiButton(102, this.width / 2 + 5, this.height - 28, 150, 20, I18n.format("gui.cancel")));
        this.buttonList.add(this.gameModeButton = new GuiButton(104, this.width / 2 - 155, 100, 150, 20, I18n.format("selectWorld.gameMode")));
        this.buttonList.add(this.allowCommandsButton = new GuiButton(103, this.width / 2 + 5, 100, 150, 20, I18n.format("selectWorld.allowCommands")));
        this.updateButtonTexts();
    }

    public void updateButtonTexts() {
        this.gameModeButton.displayString = I18n.format("selectWorld.gameMode") + " " + I18n.format("selectWorld.gameMode." + this.gameMode);
        this.allowCommandsButton.displayString = I18n.format("selectWorld.allowCommands") + " ";
        if (this.allowCommands) {
            this.allowCommandsButton.displayString = this.allowCommandsButton.displayString + I18n.format("options.on");
        } else {
            this.allowCommandsButton.displayString = this.allowCommandsButton.displayString + I18n.format("options.off");
        }
    }

    @Override
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        if (button.id == 102) {
            this.mc.displayGuiScreen(this.parent);
        } else if (button.id == 104) {
            if (this.gameMode.equals("survival")) {
                this.gameMode = "creative";
            } else if (this.gameMode.equals("creative")) {
                this.gameMode = "adventure";
            } else {
                this.gameMode = "survival";
            }

            this.updateButtonTexts();
        } else if (button.id == 103) {
            this.allowCommands = !this.allowCommands;
            this.updateButtonTexts();
        } else if (button.id == 101) {
            this.mc.displayGuiScreen(null);
            String shareToLAN = this.mc.getIntegratedServer().shareToLAN(GameType.getByName(this.gameMode), this.allowCommands);
            ChatComponentStyle chatComponentStyle;
            if (shareToLAN != null) {
                chatComponentStyle = new ChatComponentTranslation("commands.publish.started", shareToLAN);
            } else {
                chatComponentStyle = new ChatComponentText("commands.publish.failed");
            }

            this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponentStyle);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format("lanServer.title"), this.width / 2, 50, 16777215);
        this.drawCenteredString(this.fontRendererObj, I18n.format("lanServer.otherPlayers"), this.width / 2, 82, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
