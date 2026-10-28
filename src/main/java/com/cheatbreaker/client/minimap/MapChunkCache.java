package com.cheatbreaker.client.minimap;

import net.minecraft.client.Minecraft;
import net.minecraft.world.chunk.Chunk;

public class MapChunkCache {
    public Chunk chunk = null;
    public boolean initialized = false;
    public int width;
    public int height;
    public CachedChunk[] chunks;
    public IMap map;

    public void update(boolean flag) {
        if (this.initialized) {
            for (int i = this.height - 1; i >= 0; i--) {
                for (int j = 0; j < this.width; j++) {
                    this.chunks[j + i * this.width].check(this.map, flag);
                }
            }
        }
    }

    public MapChunkCache(int n, int n2, IMap map) {
        this.width = n;
        this.height = n2;
        this.chunks = new CachedChunk[n * n2];
        this.map = map;
    }

    public void shiftX(int n) {
        if (n > 0) {
            System.arraycopy(this.chunks, n, this.chunks, 0, this.chunks.length - n);
        } else if (n < 0) {
            System.arraycopy(this.chunks, 0, this.chunks, -n, this.chunks.length + n);
        }
    }

    public void fill(int n, int n2) {
        Chunk chunkFromBlockCoords = Minecraft.getMinecraft().theWorld.getChunkFromBlockCoords(n, n2);
        int n3 = this.width / 2;
        int n4 = this.height / 2;

        for (int i = 0; i < this.height; i++) {
            for (int j = 0; j < this.width; j++) {
                this.chunks[j + i * this.width] = new CachedChunk(chunkFromBlockCoords.xPosition - (n3 - j), chunkFromBlockCoords.zPosition - (n4 - i));
            }
        }

        this.initialized = true;
    }

    public void moveTo(int n, int n2) {
        Chunk chunkFromBlockCoords = Minecraft.getMinecraft().theWorld.getChunkFromBlockCoords(n, n2);
        if (chunkFromBlockCoords != this.chunk) {
            if (this.chunk == null) {
                this.fill(n, n2);
                this.chunk = chunkFromBlockCoords;
                return;
            }

            int n3 = this.width / 2;
            int n4 = this.height / 2;
            int n5 = chunkFromBlockCoords.xPosition - this.chunk.xPosition;
            int n6 = chunkFromBlockCoords.zPosition - this.chunk.zPosition;
            if (Math.abs(n5) < this.width && Math.abs(n6) < this.height && chunkFromBlockCoords.worldObj.equals(this.chunk.worldObj)) {
                this.shiftX(n5);
                this.shiftZ(n6);

                for (int i = n6 > 0 ? this.height - n6 : 0; i < (n6 > 0 ? this.height : -n6); i++) {
                    for (int j = 0; j < this.width; j++) {
                        this.chunks[j + i * this.width] = new CachedChunk(chunkFromBlockCoords.xPosition - (n3 - j), chunkFromBlockCoords.zPosition - (n4 - i));
                    }
                }

                for (int i = 0; i < this.height; i++) {
                    for (int j = n5 > 0 ? this.width - n5 : 0; j < (n5 > 0 ? this.width : -n5); j++) {
                        this.chunks[j + i * this.width] = new CachedChunk(chunkFromBlockCoords.xPosition - (n3 - j), chunkFromBlockCoords.zPosition - (n4 - i));
                    }
                }
            } else {
                this.fill(n, n2);
            }

            this.chunk = chunkFromBlockCoords;
        }
    }

    public void shiftZ(int n) {
        if (n > 0) {
            System.arraycopy(this.chunks, n * this.width, this.chunks, 0, this.chunks.length - n * this.width);
        } else if (n < 0) {
            System.arraycopy(this.chunks, 0, this.chunks, -n * this.width, this.chunks.length + n * this.width);
        }
    }
}
