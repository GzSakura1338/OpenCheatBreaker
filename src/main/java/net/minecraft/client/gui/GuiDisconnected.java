package net.minecraft.client.gui;

import com.cheatbreaker.client.ui.screen.ClassicMainMenuScreen;
import java.util.List;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.IChatComponent;

public class GuiDisconnected extends GuiScreen {
    public GuiScreen parentScreen;
    public String reason;
    public List multilineMessage;
    public IChatComponent message;

    public GuiDisconnected(GuiScreen guiScreen, String str, IChatComponent chatComponent) {
        this.parentScreen = guiScreen;
        this.reason = I18n.format(str);
        this.message = chatComponent;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        int n = this.height / 2 - 30 + (this.multilineMessage != null ? (this.multilineMessage.size() + 1) * this.fontRendererObj.FONT_HEIGHT : 40);
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, n + 12, I18n.format("gui.toMenu")));
        this.buttonList.add(new GuiButton(1, this.width / 2 - 100, n + 36, "Reconnect"));
        this.multilineMessage = this.fontRendererObj.listFormattedStringToWidth(this.message.getFormattedText(), this.width - 50);
    }

    @Override
    public void actionPerformed(GuiButton button) throws java.io.IOException {
        if (button.id == 0) {
            this.mc.displayGuiScreen(this.parentScreen);
        }

        if (button.id == 0) {
            if (this.parentScreen instanceof GuiDisconnected) {
                this.mc.displayGuiScreen(new GuiMultiplayer(new ClassicMainMenuScreen()));
            } else {
                this.mc.displayGuiScreen(this.parentScreen);
            }
        }

        if (button.id == 1) {
            if (this.mc.serverData != null) {
                this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, this.mc.serverData));
            } else if (this.mc.currentServerData != null) {
                this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, this.mc.currentServerData));
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, this.reason, this.width / 2, this.height / 2 - 50, 11184810);
        int n = this.height / 2 - 30;
        if (this.multilineMessage != null) {
            for (String str : (Iterable<String>)this.multilineMessage) {
                this.drawCenteredString(this.fontRendererObj, str, this.width / 2, n, 16777215);
                n += this.fontRendererObj.FONT_HEIGHT;
            }
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
