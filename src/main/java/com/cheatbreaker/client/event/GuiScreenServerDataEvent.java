package com.cheatbreaker.client.event;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.ServerData;

public class GuiScreenServerDataEvent extends SimpleEvent45 {
    public ServerData serverData;
    public GuiScreen guiScreen;

    public GuiScreenServerDataEvent(ServerData serverData, GuiScreen guiScreen) {
        this.serverData = serverData;
        this.guiScreen = guiScreen;
    }

    public GuiScreen getGuiScreen() {
        return this.guiScreen;
    }

    public void setServerData(ServerData serverData) {
        this.serverData = serverData;
    }

    public ServerData getServerData() {
        return this.serverData;
    }

    public void setGuiScreen(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
    }
}
