package com.cheatbreaker.client.event;

import net.minecraft.client.multiplayer.ServerData;

public class ServerDataEvent1 extends SimpleEvent45 {
    public ServerData serverData;

    public void setServerData(ServerData serverData) {
        this.serverData = serverData;
    }

    public ServerData getServerData() {
        return this.serverData;
    }

    public ServerDataEvent1(ServerData serverData) {
        this.serverData = serverData;
    }
}
