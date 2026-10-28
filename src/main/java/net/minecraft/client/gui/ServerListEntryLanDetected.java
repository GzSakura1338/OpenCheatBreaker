package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiListExtended.IGuiListEntry;
import net.minecraft.client.network.LanServerDetector.LanServer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.resources.I18n;

public class ServerListEntryLanDetected implements IGuiListEntry {
    public long time = 0L;
    public GuiMultiplayer parent;
    public LanServer lanServer;
    public Minecraft client;

    public ServerListEntryLanDetected(GuiMultiplayer guiMultiplayer, LanServer lanServer) {
        this.parent = guiMultiplayer;
        this.lanServer = lanServer;
        this.client = Minecraft.getMinecraft();
    }

    @Override
    public void drawEntry(int n, int n2, int n3, int n4, int n5, Tessellator tessellator, int n6, int n7, boolean flag) {
        this.client.fontRendererObj.drawString(I18n.format("lanServer.title"), n2 + 32 + 3, n3 + 1, 16777215);
        this.client.fontRendererObj.drawString(this.lanServer.getServerMotd(), n2 + 32 + 3, n3 + 12, 8421504);
        if (this.client.gameSettings.hideServerAddress) {
            this.client.fontRendererObj.drawString(I18n.format("selectServer.hiddenAddress"), n2 + 32 + 3, n3 + 12 + 11, 3158064);
        } else {
            this.client.fontRendererObj.drawString(this.lanServer.getServerIpPort(), n2 + 32 + 3, n3 + 12 + 11, 3158064);
        }
    }

    @Override
    public boolean mousePressed(int n, int n2, int n3, int n4, int n5, int n6) throws java.io.IOException {
        this.parent.selectServer(n);
        if (Minecraft.getSystemTime() - this.time < 250L) {
            this.parent.connectToSelected();
        }

        this.time = Minecraft.getSystemTime();
        return false;
    }

    @Override
    public void mouseReleased(int n, int n2, int n3, int n4, int n5, int n6) {
    }

    public LanServer getLanServer() {
        return this.lanServer;
    }
}
