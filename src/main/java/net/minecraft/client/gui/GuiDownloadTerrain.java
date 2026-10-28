package net.minecraft.client.gui;

import com.cheatbreaker.client.ui.screen.ClassicMainMenuScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.play.client.C00PacketKeepAlive;

public class GuiDownloadTerrain extends GuiScreen {
    public NetHandlerPlayClient netHandlerPlayClient;
    public int progress;
    public long longValue;

    public GuiDownloadTerrain(NetHandlerPlayClient netHandlerPlayClient) {
        this.netHandlerPlayClient = netHandlerPlayClient;
        this.longValue = System.currentTimeMillis();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
    }

    @Override
    public void updateScreen() {
        this.progress++;
        if (this.netHandlerPlayClient != null) {
            if (System.currentTimeMillis() - this.longValue >= 5000L && this.buttonList.isEmpty()) {
                this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 120 + 36, 99, 20, "Reconnect"));
                this.buttonList.add(new GuiButton(2, this.width / 2 + 2, this.height / 4 + 120 + 36, 99, 20, "Disconnect"));
            }

            if (this.progress % 20 == 0) {
                this.netHandlerPlayClient.addToSendQueue(new C00PacketKeepAlive());
            }

            if (this.netHandlerPlayClient != null) {
                this.netHandlerPlayClient.onNetworkTick();
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) throws java.io.IOException {
        this.drawBackground(0);
        this.drawCenteredString(this.fontRendererObj, I18n.format("multiplayer.downloadingTerrain"), this.width / 2, this.height / 2 - 50, 16777215);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    @Override
    public void actionPerformed(GuiButton guiButton) throws java.io.IOException {
        if (this.mc.currentServerData != null && this.mc.theWorld != null) {
            this.mc.theWorld.sendQuittingDisconnectingPacket();
            this.mc.loadWorld((WorldClient)null);
        }

        if (guiButton.id == 1) {
            this.mc.displayGuiScreen(new GuiConnecting(this, this.mc, this.mc.serverData));
        }

        if (guiButton.id == 2) {
            this.mc.displayGuiScreen(new ClassicMainMenuScreen());
        }
    }
}
