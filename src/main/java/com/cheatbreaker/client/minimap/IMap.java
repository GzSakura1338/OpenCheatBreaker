package com.cheatbreaker.client.minimap;

import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;

public interface IMap {
   float getPercentX();

   String getCurrentWorldName();

   float getPercentY();

   void onTickInGame(Minecraft mc);

   void setPermissions(boolean flag, boolean flag2);

   void drawMinimap(Minecraft mc);

   void forceFullRender(boolean flag);

   void chunkCalc(Chunk chunk);
}
