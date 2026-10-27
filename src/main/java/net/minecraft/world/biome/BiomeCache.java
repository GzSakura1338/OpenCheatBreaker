package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LongHashMap;

public class BiomeCache {
   public LongHashMap<BiomeCache.Block> cacheMap = new LongHashMap<>();
   public long lastCleanupTime;
   public List<BiomeCache.Block> cache = Lists.newArrayList();
   public WorldChunkManager chunkManager;

   public BiomeCache(WorldChunkManager chunkManagerIn) {
      this.chunkManager = chunkManagerIn;
   }

   public BiomeCache.Block getBiomeCacheBlock(int x, int z) {
      x >>= 4;
      z >>= 4;
      long l = x & 4294967295L | (z & 4294967295L) << 32;
      BiomeCache.Block valueByKey = this.cacheMap.getValueByKey(l);
      if (valueByKey == null) {
         valueByKey = new BiomeCache.Block(x, z);
         this.cacheMap.add(l, valueByKey);
         this.cache.add(valueByKey);
      }

      valueByKey.lastAccessTime = MinecraftServer.getCurrentTimeMillis();
      return valueByKey;
   }

   public BiomeGenBase findBiomeGenBase(int x, int z, BiomeGenBase biomeGenBase) {
      BiomeGenBase biomeGenAt = this.getBiomeCacheBlock(x, z).getBiomeGenAt(x, z);
      return biomeGenAt == null ? biomeGenBase : biomeGenAt;
   }

   public void cleanupCache() {
      long currentTimeMillis = MinecraftServer.getCurrentTimeMillis();
      long l = currentTimeMillis - this.lastCleanupTime;
      if (l > 7500L || l < 0L) {
         this.lastCleanupTime = currentTimeMillis;

         for (int i = 0; i < this.cache.size(); i++) {
            BiomeCache.Block block = this.cache.get(i);
            long l2 = currentTimeMillis - block.lastAccessTime;
            if (l2 > 30000L || l2 < 0L) {
               this.cache.remove(i--);
               long l3 = block.xPosition & 4294967295L | (block.zPosition & 4294967295L) << 32;
               this.cacheMap.remove(l3);
            }
         }
      }
   }

   public BiomeGenBase[] getCachedBiomes(int x, int z) {
      return this.getBiomeCacheBlock(x, z).biomes;
   }

   public class Block {
      public float[] rainfallValues = new float[256];
      public BiomeGenBase[] biomes = new BiomeGenBase[256];
      public int xPosition;
      public int zPosition;
      public long lastAccessTime;

      public Block(int x, int z) {
         this.xPosition = x;
         this.zPosition = z;
         BiomeCache.this.chunkManager.getRainfall(this.rainfallValues, x << 4, z << 4, 16, 16);
         BiomeCache.this.chunkManager.getBiomeGenAt(this.biomes, x << 4, z << 4, 16, 16, false);
      }

      public BiomeGenBase getBiomeGenAt(int x, int z) {
         return this.biomes[x & 15 | (z & 15) << 4];
      }
   }
}
