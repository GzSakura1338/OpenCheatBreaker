package com.cheatbreaker.client.minimap;

import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;

public class CachedChunk {
   public int unusedValue1;
   public int chunkX = 0;
   public int chunkZ = 0;
   public int unusedValue2;
   public boolean loaded = false;
   public Chunk chunk;

   public boolean needsSaving() {
      return this.chunk.isLoaded() && this.chunk.needsSaving(false);
   }

   public boolean updateLoadedState() {
      boolean flag = false;
      if (!this.loaded) {
         this.chunk = Minecraft.getMinecraft().theWorld.getChunkFromChunkCoords(this.chunkX, this.chunkZ);
         if (this.chunk.isLoaded()) {
            this.loaded = true;
            flag = true;
         }
      } else if (this.loaded && !this.chunk.isLoaded()) {
         this.loaded = false;
         flag = true;
      }

      return flag;
   }

   public CachedChunk(int n, int n2) {
      this.chunkX = n;
      this.chunkZ = n2;
      this.chunk = Minecraft.getMinecraft().theWorld.getChunkFromChunkCoords(n, n2);
      this.loaded = this.chunk.isLoaded();
   }

   public void check(IMap map, boolean flag) {
      if (this.updateLoadedState() || flag && this.needsSaving()) {
         map.chunkCalc(this.chunk);
         if (flag) {
            this.chunk.setModified(false);
         }
      }
   }
}
