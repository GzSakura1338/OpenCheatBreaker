package com.cheatbreaker.client.minimap;

import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;

public interface IMap {
    void setPermissions(boolean flag, boolean flag2);

    void chunkCalc(Chunk chunk);

    float getPercentX();

    void drawMinimap(Minecraft mc);

    void forceFullRender(boolean flag);

    float getPercentY();

    void onTickInGame(Minecraft mc) throws java.io.IOException;

    String getCurrentWorldName();
}
