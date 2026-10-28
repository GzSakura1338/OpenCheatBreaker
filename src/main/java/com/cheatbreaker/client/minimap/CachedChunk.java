package com.cheatbreaker.client.minimap;

import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;

public class CachedChunk {
    public int chunkX = 0;
    public int intValue;
    public int chunkZ = 0;
    public boolean loaded = false;
    public int intValue2;
    public Chunk chunk;

    public boolean updateLoadedState() {
        boolean flag = false;
        if (!this.loaded) {
            this.chunk = Minecraft.getMinecraft().theWorld.getChunkFromChunkCoords(this.chunkX, this.chunkZ);
            if (this.chunk.isChunkLoaded) {
                this.loaded = true;
                flag = true;
            }
        } else if (this.loaded && !this.chunk.isChunkLoaded) {
            this.loaded = false;
            flag = true;
        }

        return flag;
    }

    public CachedChunk(int n, int n2) {
        this.chunkX = n;
        this.chunkZ = n2;
        this.chunk = Minecraft.getMinecraft().theWorld.getChunkFromChunkCoords(n, n2);
        this.loaded = this.chunk.isChunkLoaded;
    }

    public void check(IMap map, boolean flag) {
        if (this.updateLoadedState() || flag && this.isLoadedAndModified()) {
            map.chunkCalc(this.chunk);
            if (flag) {
                this.chunk.isModified = false;
            }
        }
    }

    public boolean isLoadedAndModified() {
        return this.chunk.isChunkLoaded && this.chunk.isModified;
    }
}
