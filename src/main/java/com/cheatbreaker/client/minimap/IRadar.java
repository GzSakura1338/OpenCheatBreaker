package com.cheatbreaker.client.minimap;

import net.minecraft.client.Minecraft;

public interface IRadar {
    void onTickInGame(Minecraft mc, RadarContext radarContext);

    void run();
}
