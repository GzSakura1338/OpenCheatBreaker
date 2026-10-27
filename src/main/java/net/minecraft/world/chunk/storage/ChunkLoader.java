package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.NibbleArray;

public class ChunkLoader {
   public static ChunkLoader.AnvilConverterData load(NBTTagCompound nbt) {
      int integer = nbt.getInteger("xPos");
      int integer2 = nbt.getInteger("zPos");
      ChunkLoader.AnvilConverterData anvilConverterData = new ChunkLoader.AnvilConverterData(integer, integer2);
      anvilConverterData.blocks = nbt.getByteArray("Blocks");
      anvilConverterData.data = new NibbleArrayReader(nbt.getByteArray("Data"), 7);
      anvilConverterData.skyLight = new NibbleArrayReader(nbt.getByteArray("SkyLight"), 7);
      anvilConverterData.blockLight = new NibbleArrayReader(nbt.getByteArray("BlockLight"), 7);
      anvilConverterData.heightmap = nbt.getByteArray("HeightMap");
      anvilConverterData.terrainPopulated = nbt.getBoolean("TerrainPopulated");
      anvilConverterData.entities = nbt.getTagList("Entities", 10);
      anvilConverterData.tileEntities = nbt.getTagList("TileEntities", 10);
      anvilConverterData.tileTicks = nbt.getTagList("TileTicks", 10);

      try {
         anvilConverterData.lastUpdated = nbt.getLong("LastUpdate");
      } catch (ClassCastException e) {
         anvilConverterData.lastUpdated = nbt.getInteger("LastUpdate");
      }

      return anvilConverterData;
   }

   public static void convertToAnvilFormat(ChunkLoader.AnvilConverterData anvilConverterData, NBTTagCompound compound, WorldChunkManager worldChunkManager) {
      compound.setInteger("xPos", anvilConverterData.x);
      compound.setInteger("zPos", anvilConverterData.z);
      compound.setLong("LastUpdate", anvilConverterData.lastUpdated);
      int[] ints = new int[anvilConverterData.heightmap.length];

      for (int i = 0; i < anvilConverterData.heightmap.length; i++) {
         ints[i] = anvilConverterData.heightmap[i];
      }

      compound.setIntArray("HeightMap", ints);
      compound.setBoolean("TerrainPopulated", anvilConverterData.terrainPopulated);
      NBTTagList nbttagList = new NBTTagList();

      for (int i = 0; i < 8; i++) {
         boolean flag = true;

         for (int j = 0; j < 16 && flag; j++) {
            for (int k = 0; k < 16 && flag; k++) {
               for (int m = 0; m < 16; m++) {
                  int n = j << 11 | m << 7 | k + (i << 4);
                  byte b = anvilConverterData.blocks[n];
                  if (b != 0) {
                     flag = false;
                     break;
                  }
               }
            }
         }

         if (!flag) {
            byte[] bytes = new byte[4096];
            NibbleArray nibbleArray = new NibbleArray();
            NibbleArray nibbleArray2 = new NibbleArray();
            NibbleArray nibbleArray3 = new NibbleArray();

            for (int j = 0; j < 16; j++) {
               for (int k = 0; k < 16; k++) {
                  for (int m = 0; m < 16; m++) {
                     int n = j << 11 | m << 7 | k + (i << 4);
                     byte b = anvilConverterData.blocks[n];
                     bytes[k << 8 | m << 4 | j] = (byte)(b & 0xFF);
                     nibbleArray.set(j, k, m, anvilConverterData.data.get(j, k + (i << 4), m));
                     nibbleArray2.set(j, k, m, anvilConverterData.skyLight.get(j, k + (i << 4), m));
                     nibbleArray3.set(j, k, m, anvilConverterData.blockLight.get(j, k + (i << 4), m));
                  }
               }
            }

            NBTTagCompound nbttagCompound = new NBTTagCompound();
            nbttagCompound.setByte("Y", (byte)(i & 0xFF));
            nbttagCompound.setByteArray("Blocks", bytes);
            nbttagCompound.setByteArray("Data", nibbleArray.getData());
            nbttagCompound.setByteArray("SkyLight", nibbleArray2.getData());
            nbttagCompound.setByteArray("BlockLight", nibbleArray3.getData());
            nbttagList.appendTag(nbttagCompound);
         }
      }

      compound.setTag("Sections", nbttagList);
      byte[] bytes = new byte[256];
      MutableBlockPos mutableBlockPos = new MutableBlockPos();

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            mutableBlockPos.set(anvilConverterData.x << 4 | i, 0, anvilConverterData.z << 4 | j);
            bytes[j << 4 | i] = (byte)(worldChunkManager.getBiomeGenerator(mutableBlockPos, BiomeGenBase.DEFAULT).biomeID & 0xFF);
         }
      }

      compound.setByteArray("Biomes", bytes);
      compound.setTag("Entities", anvilConverterData.entities);
      compound.setTag("TileEntities", anvilConverterData.tileEntities);
      if (anvilConverterData.tileTicks != null) {
         compound.setTag("TileTicks", anvilConverterData.tileTicks);
      }
   }

   public static class AnvilConverterData {
      public long lastUpdated;
      public boolean terrainPopulated;
      public byte[] heightmap;
      public NibbleArrayReader blockLight;
      public NibbleArrayReader skyLight;
      public NibbleArrayReader data;
      public byte[] blocks;
      public NBTTagList entities;
      public NBTTagList tileEntities;
      public NBTTagList tileTicks;
      public int x;
      public int z;

      public AnvilConverterData(int xIn, int zIn) {
         this.x = xIn;
         this.z = zIn;
      }
   }
}
