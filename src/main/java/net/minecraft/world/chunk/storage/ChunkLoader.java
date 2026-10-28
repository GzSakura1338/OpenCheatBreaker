package net.minecraft.world.chunk.storage;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.NibbleArray;

public class ChunkLoader {
    public static ChunkLoader.AnvilConverterData load(NBTTagCompound nbttagCompound) {
        int integer = nbttagCompound.getInteger("xPos");
        int integer2 = nbttagCompound.getInteger("zPos");
        ChunkLoader.AnvilConverterData anvilConverterData = new ChunkLoader.AnvilConverterData(integer, integer2);
        anvilConverterData.blocks = nbttagCompound.getByteArray("Blocks");
        anvilConverterData.data = new NibbleArrayReader(nbttagCompound.getByteArray("Data"), 7);
        anvilConverterData.skyLight = new NibbleArrayReader(nbttagCompound.getByteArray("SkyLight"), 7);
        anvilConverterData.blockLight = new NibbleArrayReader(nbttagCompound.getByteArray("BlockLight"), 7);
        anvilConverterData.heightmap = nbttagCompound.getByteArray("HeightMap");
        anvilConverterData.terrainPopulated = nbttagCompound.getBoolean("TerrainPopulated");
        anvilConverterData.entities = nbttagCompound.getTagList("Entities", 10);
        anvilConverterData.nbtTagList = nbttagCompound.getTagList("TileEntities", 10);
        anvilConverterData.nbtTagList2 = nbttagCompound.getTagList("TileTicks", 10);

        try {
            anvilConverterData.lastUpdated = nbttagCompound.getLong("LastUpdate");
        } catch (ClassCastException e) {
            anvilConverterData.lastUpdated = nbttagCompound.getInteger("LastUpdate");
        }

        return anvilConverterData;
    }

    public static void convertToAnvilFormat(ChunkLoader.AnvilConverterData anvilConverterData, NBTTagCompound nbttagCompound, WorldChunkManager worldChunkManager) {
        nbttagCompound.setInteger("xPos", anvilConverterData.x);
        nbttagCompound.setInteger("zPos", anvilConverterData.z);
        nbttagCompound.setLong("LastUpdate", anvilConverterData.lastUpdated);
        int[] ints = new int[anvilConverterData.heightmap.length];

        for (int i = 0; i < anvilConverterData.heightmap.length; i++) {
            ints[i] = anvilConverterData.heightmap[i];
        }

        nbttagCompound.setIntArray("HeightMap", ints);
        nbttagCompound.setBoolean("TerrainPopulated", anvilConverterData.terrainPopulated);
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
                NibbleArray nibbleArray = new NibbleArray(bytes.length, 4);
                NibbleArray nibbleArray2 = new NibbleArray(bytes.length, 4);
                NibbleArray nibbleArray3 = new NibbleArray(bytes.length, 4);

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

                NBTTagCompound nbttagCompound2 = new NBTTagCompound();
                nbttagCompound2.setByte("Y", (byte)(i & 0xFF));
                nbttagCompound2.setByteArray("Blocks", bytes);
                nbttagCompound2.setByteArray("Data", nibbleArray.data);
                nbttagCompound2.setByteArray("SkyLight", nibbleArray2.data);
                nbttagCompound2.setByteArray("BlockLight", nibbleArray3.data);
                nbttagList.appendTag(nbttagCompound2);
            }
        }

        nbttagCompound.setTag("Sections", nbttagList);
        byte[] bytes = new byte[256];

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                bytes[j << 4 | i] = (byte)(worldChunkManager.getBiomeGenAt(anvilConverterData.x << 4 | i, anvilConverterData.z << 4 | j).biomeID & 0xFF);
            }
        }

        nbttagCompound.setByteArray("Biomes", bytes);
        nbttagCompound.setTag("Entities", anvilConverterData.entities);
        nbttagCompound.setTag("TileEntities", anvilConverterData.nbtTagList);
        if (anvilConverterData.nbtTagList2 != null) {
            nbttagCompound.setTag("TileTicks", anvilConverterData.nbtTagList2);
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
        public NBTTagList nbtTagList;
        public NBTTagList nbtTagList2;
        public int x;
        public int z;

        public AnvilConverterData(int n, int n2) {
            this.x = n;
            this.z = n2;
        }
    }
}
