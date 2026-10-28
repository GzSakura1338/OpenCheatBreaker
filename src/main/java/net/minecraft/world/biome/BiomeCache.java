package net.minecraft.world.biome;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LongHashMap;

public class BiomeCache {
    public LongHashMap cacheMap = new LongHashMap();
    public long lastCleanupTime;
    public List cache = new ArrayList();
    public WorldChunkManager chunkManager;

    public BiomeCache(WorldChunkManager worldChunkManager) {
        this.chunkManager = worldChunkManager;
    }

    public BiomeCache.Block getBiomeCacheBlock(int n, int n2) {
        n >>= 4;
        n2 >>= 4;
        long l = n & 4294967295L | (n2 & 4294967295L) << 32;
        BiomeCache.Block valueByKey = (BiomeCache.Block)this.cacheMap.getValueByKey(l);
        if (valueByKey == null) {
            valueByKey = new BiomeCache.Block(n, n2);
            this.cacheMap.add(l, valueByKey);
            this.cache.add(valueByKey);
        }

        valueByKey.lastAccessTime = MinecraftServer.getCurrentTimeMillis();
        return valueByKey;
    }

    public BiomeGenBase getBiomeGenAt(int n, int n2) {
        return this.getBiomeCacheBlock(n, n2).getBiomeGenAt(n, n2);
    }

    public void cleanupCache() {
        long currentTimeMillis = MinecraftServer.getCurrentTimeMillis();
        long l = currentTimeMillis - this.lastCleanupTime;
        if (l > 7500L || l < 0L) {
            this.lastCleanupTime = currentTimeMillis;

            for (int i = 0; i < this.cache.size(); i++) {
                BiomeCache.Block block = (BiomeCache.Block)this.cache.get(i);
                long l2 = currentTimeMillis - block.lastAccessTime;
                if (l2 > 30000L || l2 < 0L) {
                    this.cache.remove(i--);
                    long l3 = block.xPosition & 4294967295L | (block.zPosition & 4294967295L) << 32;
                    this.cacheMap.remove(l3);
                }
            }
        }
    }

    public BiomeGenBase[] getCachedBiomes(int n, int n2) {
        return this.getBiomeCacheBlock(n, n2).biomes;
    }

    public class Block {
        public float[] rainfallValues = new float[256];
        public BiomeGenBase[] biomes = new BiomeGenBase[256];
        public int xPosition;
        public int zPosition;
        public long lastAccessTime;

        public Block(int n, int n2) {
            this.xPosition = n;
            this.zPosition = n2;
            BiomeCache.this.chunkManager.getRainfall(this.rainfallValues, n << 4, n2 << 4, 16, 16);
            BiomeCache.this.chunkManager.getBiomeGenAt(this.biomes, n << 4, n2 << 4, 16, 16, false);
        }

        public BiomeGenBase getBiomeGenAt(int n, int n2) {
            return this.biomes[n & 15 | (n2 & 15) << 4];
        }
    }
}
