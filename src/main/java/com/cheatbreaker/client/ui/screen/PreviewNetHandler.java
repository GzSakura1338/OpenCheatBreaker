package com.cheatbreaker.client.ui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;

public class PreviewNetHandler extends NetHandlerPlayClient {
    public PreviewNetHandler(Minecraft mc) {
        super(mc, mc.currentScreen, new DummyNetworkManager());
    }
}
