package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.GuiScreenServerDataEvent;
import com.cheatbreaker.client.network.ReconnectHandler;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.I18n;

public class AssetServerConnectingScreen extends GuiScreen {
    public long startMillis = System.currentTimeMillis();
    public ServerData serverData;
    public static final long CONSTANT_15000 = 15000L;
    public boolean flag;

    public void connect() throws java.io.IOException {
        GuiScreenServerDataEvent guiScreenServerDataEvent = new GuiScreenServerDataEvent(this.serverData, new ClassicMainMenuScreen());
        CheatBreaker.getInstance().getEventBus().handleEvent(guiScreenServerDataEvent);
        if (!guiScreenServerDataEvent.isCancelled()) {
            this.mc.displayGuiScreen(new GuiConnecting(new ClassicMainMenuScreen(), this.mc, this.serverData));
        }
    }

    @Override
    public void updateScreen() throws java.io.IOException {
        boolean flag = CheatBreaker.isOfflineMode() || ReconnectHandler.reconnectDisabled || System.currentTimeMillis() - this.startMillis > 15000L;
        if (!this.flag && (CheatBreaker.getInstance().isWebsocketConnected() || flag)) {
            this.flag = true;
            this.connect();
        }
    }

    public AssetServerConnectingScreen(ServerData serverData) {
        this.serverData = serverData;
    }

    @Override
    public void initGui() {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
    }

    @Override
    public void drawScreen(int n, int n2, float f) throws java.io.IOException {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, "Connecting to the player assets server...", this.width / 2, this.height / 2 - 50, 16777215);
        super.drawScreen(n, n2, f);
    }

    @Override
    public void actionPerformed(GuiButton guiButton) throws java.io.IOException {
        if (guiButton.id == 0) {
            this.mc.displayGuiScreen(new ClassicMainMenuScreen());
        }
    }
}
