package net.minecraft.world.gen;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;

public class ChunkProviderFlat implements IChunkProvider {
    public Block[] cachedBlockIDs = new Block[256];
    public byte[] cachedBlockMetadata = new byte[256];
    public List structureGenerators = new ArrayList();
    public World worldObj;
    public Random random;
    public FlatGeneratorInfo flatWorldGenInfo;
    public boolean hasDecoration;
    public WorldGenLakes waterLakeGenerator;
    public WorldGenLakes lavaLakeGenerator;
    public boolean hasDungeons;

    public ChunkProviderFlat(World world, long l, boolean flag, String str) {
        this.worldObj = world;
        this.random = new Random(l);
        this.flatWorldGenInfo = FlatGeneratorInfo.createFlatGeneratorFromString(str);
        if (flag) {
            Map worldFeatures = this.flatWorldGenInfo.getWorldFeatures();
            if (worldFeatures.containsKey("village")) {
                Map map = (Map)worldFeatures.get("village");
                if (!map.containsKey("size")) {
                    map.put("size", "1");
                }

                this.structureGenerators.add(new MapGenVillage(map));
            }

            if (worldFeatures.containsKey("biome_1")) {
                this.structureGenerators.add(new MapGenScatteredFeature((Map)worldFeatures.get("biome_1")));
            }

            if (worldFeatures.containsKey("mineshaft")) {
                this.structureGenerators.add(new MapGenMineshaft((Map)worldFeatures.get("mineshaft")));
            }

            if (worldFeatures.containsKey("stronghold")) {
                this.structureGenerators.add(new MapGenStronghold((Map)worldFeatures.get("stronghold")));
            }
        }

        this.hasDecoration = this.flatWorldGenInfo.getWorldFeatures().containsKey("decoration");
        if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lake")) {
            this.waterLakeGenerator = new WorldGenLakes(Blocks.water);
        }

        if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lava_lake")) {
            this.lavaLakeGenerator = new WorldGenLakes(Blocks.lava);
        }

        this.hasDungeons = this.flatWorldGenInfo.getWorldFeatures().containsKey("dungeon");

        for (FlatLayerInfo flatLayerInfo : (Iterable<FlatLayerInfo>)this.flatWorldGenInfo.getFlatLayers()) {
            for (int minY = flatLayerInfo.getMinY(); minY < flatLayerInfo.getMinY() + flatLayerInfo.getLayerCount(); minY++) {
                this.cachedBlockIDs[minY] = flatLayerInfo.getBlock();
                this.cachedBlockMetadata[minY] = (byte)flatLayerInfo.getFillBlockMeta();
            }
        }
    }

    @Override
    public Chunk loadChunk(int n, int n2) {
        return this.provideChunk(n, n2);
    }

    @Override
    public Chunk provideChunk(int n, int n2) {
        Chunk chunk = new Chunk(this.worldObj, n, n2);

        for (int i = 0; i < this.cachedBlockIDs.length; i++) {
            Block block = this.cachedBlockIDs[i];
            if (block != null) {
                int n3 = i >> 4;
                ExtendedBlockStorage extendedBlockStorage = chunk.getBlockStorageArray()[n3];
                if (extendedBlockStorage == null) {
                    extendedBlockStorage = new ExtendedBlockStorage(i, !this.worldObj.provider.hasNoSky);
                    chunk.getBlockStorageArray()[n3] = extendedBlockStorage;
                }

                for (int j = 0; j < 16; j++) {
                    for (int k = 0; k < 16; k++) {
                        extendedBlockStorage.setExtBlockID(j, i & 15, k, block);
                        extendedBlockStorage.setExtBlockMetadata(j, i & 15, k, this.cachedBlockMetadata[i]);
                    }
                }
            }
        }

        chunk.generateSkylightMap();
        BiomeGenBase[] loadBlockGeneratorData = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(null, n * 16, n2 * 16, 16, 16);
        byte[] biomeArray = chunk.getBiomeArray();

        for (int i = 0; i < biomeArray.length; i++) {
            biomeArray[i] = (byte)loadBlockGeneratorData[i].biomeID;
        }

        for (MapGenBase mapGenBase : (Iterable<MapGenBase>)this.structureGenerators) {
            mapGenBase.generate(this, this.worldObj, n, n2, null);
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    @Override
    public boolean chunkExists(int n, int n2) {
        return true;
    }

    @Override
    public void populate(IChunkProvider chunkProvider, int n, int n2) {
        int n3 = n * 16;
        int n4 = n2 * 16;
        BiomeGenBase biomeGenForCoords = this.worldObj.getBiomeGenForCoords(n3 + 16, n4 + 16);
        boolean flag = false;
        this.random.setSeed(this.worldObj.getSeed());
        long l = this.random.nextLong() / 2L * 2L + 1L;
        long l2 = this.random.nextLong() / 2L * 2L + 1L;
        this.random.setSeed(n * l + n2 * l2 ^ this.worldObj.getSeed());

        for (MapGenStructure mapGenStructure : (Iterable<MapGenStructure>)this.structureGenerators) {
            boolean generateStructuresInChunk = mapGenStructure.generateStructuresInChunk(this.worldObj, this.random, n, n2);
            if (mapGenStructure instanceof MapGenVillage) {
                flag |= generateStructuresInChunk;
            }
        }

        if (this.waterLakeGenerator != null && !flag && this.random.nextInt(4) == 0) {
            int n5 = n3 + this.random.nextInt(16) + 8;
            int nextInt = this.random.nextInt(256);
            int n6 = n4 + this.random.nextInt(16) + 8;
            this.waterLakeGenerator.generate(this.worldObj, this.random, n5, nextInt, n6);
        }

        if (this.lavaLakeGenerator != null && !flag && this.random.nextInt(8) == 0) {
            int n5 = n3 + this.random.nextInt(16) + 8;
            int nextInt = this.random.nextInt(this.random.nextInt(248) + 8);
            int n6 = n4 + this.random.nextInt(16) + 8;
            if (nextInt < 63 || this.random.nextInt(10) == 0) {
                this.lavaLakeGenerator.generate(this.worldObj, this.random, n5, nextInt, n6);
            }
        }

        if (this.hasDungeons) {
            for (int i = 0; i < 8; i++) {
                int n5 = n3 + this.random.nextInt(16) + 8;
                int nextInt = this.random.nextInt(256);
                int n6 = n4 + this.random.nextInt(16) + 8;
                new WorldGenDungeons().generate(this.worldObj, this.random, n5, nextInt, n6);
            }
        }

        if (this.hasDecoration) {
            biomeGenForCoords.decorate(this.worldObj, this.random, n3, n4);
        }
    }

    @Override
    public boolean saveChunks(boolean flag, IProgressUpdate progressUpdate) {
        return true;
    }

    @Override
    public void saveExtraData() {
    }

    @Override
    public boolean unloadQueuedChunks() {
        return false;
    }

    @Override
    public boolean canSave() {
        return true;
    }

    @Override
    public String makeString() {
        return "FlatLevelSource";
    }

    @Override
    public List getPossibleCreatures(EnumCreatureType enumCreatureType, int n, int n2, int n3) {
        BiomeGenBase biomeGenForCoords = this.worldObj.getBiomeGenForCoords(n, n3);
        return biomeGenForCoords.getSpawnableList(enumCreatureType);
    }

    @Override
    public ChunkPosition findClosestStructure(World world, String str, int n, int n2, int n3) {
        if ("Stronghold".equals(str)) {
            for (MapGenStructure mapGenStructure : (Iterable<MapGenStructure>)this.structureGenerators) {
                if (mapGenStructure instanceof MapGenStronghold) {
                    return mapGenStructure.getNearestInstance(world, n, n2, n3);
                }
            }
        }

        return null;
    }

    @Override
    public int getLoadedChunkCount() {
        return 0;
    }

    @Override
    public void recreateStructures(int n, int n2) {
        for (MapGenStructure mapGenStructure : (Iterable<MapGenStructure>)this.structureGenerators) {
            mapGenStructure.generate(this, this.worldObj, n, n2, null);
        }
    }
}
