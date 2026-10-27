package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.MapGenMineshaft;
import net.minecraft.world.gen.structure.MapGenScatteredFeature;
import net.minecraft.world.gen.structure.MapGenStronghold;
import net.minecraft.world.gen.structure.MapGenStructure;
import net.minecraft.world.gen.structure.MapGenVillage;
import net.minecraft.world.gen.structure.StructureOceanMonument;

public class ChunkProviderFlat implements IChunkProvider {
   public IBlockState[] cachedBlockIDs = new IBlockState[256];
   public List<MapGenStructure> structureGenerators = Lists.newArrayList();
   public World worldObj;
   public Random random;
   public FlatGeneratorInfo flatWorldGenInfo;
   public WorldGenLakes waterLakeGenerator;
   public WorldGenLakes lavaLakeGenerator;
   public boolean hasDungeons;
   public boolean hasDecoration;

   public ChunkProviderFlat(World worldIn, long seed, boolean generateStructures, String flatGeneratorSettings) {
      this.worldObj = worldIn;
      this.random = new Random(seed);
      this.flatWorldGenInfo = FlatGeneratorInfo.createFlatGeneratorFromString(flatGeneratorSettings);
      if (generateStructures) {
         Map worldFeatures = this.flatWorldGenInfo.getWorldFeatures();
         if (worldFeatures.containsKey("village")) {
            Map map = (Map)worldFeatures.get("village");
            if (!map.containsKey("size")) {
               map.put("size", "1");
            }

            this.structureGenerators.add(new MapGenVillage(map));
         }

         if (worldFeatures.containsKey("biome_1")) {
            this.structureGenerators.add(new MapGenScatteredFeature((Map<String, String>)worldFeatures.get("biome_1")));
         }

         if (worldFeatures.containsKey("mineshaft")) {
            this.structureGenerators.add(new MapGenMineshaft((Map<String, String>)worldFeatures.get("mineshaft")));
         }

         if (worldFeatures.containsKey("stronghold")) {
            this.structureGenerators.add(new MapGenStronghold((Map<String, String>)worldFeatures.get("stronghold")));
         }

         if (worldFeatures.containsKey("oceanmonument")) {
            this.structureGenerators.add(new StructureOceanMonument((Map<String, String>)worldFeatures.get("oceanmonument")));
         }
      }

      if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lake")) {
         this.waterLakeGenerator = new WorldGenLakes(Blocks.water);
      }

      if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lava_lake")) {
         this.lavaLakeGenerator = new WorldGenLakes(Blocks.lava);
      }

      this.hasDungeons = this.flatWorldGenInfo.getWorldFeatures().containsKey("dungeon");
      int n = 0;
      int n2 = 0;
      boolean flag = true;

      for (FlatLayerInfo flatLayerInfo : this.flatWorldGenInfo.getFlatLayers()) {
         for (int minY = flatLayerInfo.getMinY(); minY < flatLayerInfo.getMinY() + flatLayerInfo.getLayerCount(); minY++) {
            IBlockState layerMaterial = flatLayerInfo.getLayerMaterial();
            if (layerMaterial.getBlock() != Blocks.air) {
               flag = false;
               this.cachedBlockIDs[minY] = layerMaterial;
            }
         }

         if (flatLayerInfo.getLayerMaterial().getBlock() == Blocks.air) {
            n2 += flatLayerInfo.getLayerCount();
         } else {
            n += flatLayerInfo.getLayerCount() + n2;
            n2 = 0;
         }
      }

      worldIn.setSeaLevel(n);
      this.hasDecoration = flag ? false : this.flatWorldGenInfo.getWorldFeatures().containsKey("decoration");
   }

   @Override
   public Chunk provideChunk(int x, int z) {
      ChunkPrimer chunkPrimer = new ChunkPrimer();

      for (int i = 0; i < this.cachedBlockIDs.length; i++) {
         IBlockState blockState = this.cachedBlockIDs[i];
         if (blockState != null) {
            for (int j = 0; j < 16; j++) {
               for (int k = 0; k < 16; k++) {
                  chunkPrimer.setBlockState(j, i, k, blockState);
               }
            }
         }
      }

      for (MapGenBase mapGenBase : this.structureGenerators) {
         mapGenBase.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      Chunk chunk = new Chunk(this.worldObj, chunkPrimer, x, z);
      BiomeGenBase[] loadBlockGeneratorData = this.worldObj.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[])null, x * 16, z * 16, 16, 16);
      byte[] biomeArray = chunk.getBiomeArray();

      for (int i = 0; i < biomeArray.length; i++) {
         biomeArray[i] = (byte)loadBlockGeneratorData[i].biomeID;
      }

      chunk.generateSkylightMap();
      return chunk;
   }

   @Override
   public boolean chunkExists(int x, int z) {
      return true;
   }

   @Override
   public void populate(IChunkProvider chunkProvider, int x, int z) {
      int n = x * 16;
      int n2 = z * 16;
      BlockPos blockPos = new BlockPos(n, 0, n2);
      BiomeGenBase biomeGenForCoords = this.worldObj.getBiomeGenForCoords(new BlockPos(n + 16, 0, n2 + 16));
      boolean flag = false;
      this.random.setSeed(this.worldObj.getSeed());
      long l = this.random.nextLong() / 2L * 2L + 1L;
      long l2 = this.random.nextLong() / 2L * 2L + 1L;
      this.random.setSeed(x * l + z * l2 ^ this.worldObj.getSeed());
      ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(x, z);

      for (MapGenStructure mapGenStructure : this.structureGenerators) {
         boolean generateStructure = mapGenStructure.generateStructure(this.worldObj, this.random, chunkCoordIntPair);
         if (mapGenStructure instanceof MapGenVillage) {
            flag |= generateStructure;
         }
      }

      if (this.waterLakeGenerator != null && !flag && this.random.nextInt(4) == 0) {
         this.waterLakeGenerator
            .generate(this.worldObj, this.random, blockPos.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
      }

      if (this.lavaLakeGenerator != null && !flag && this.random.nextInt(8) == 0) {
         BlockPos blockPos2 = blockPos.add(this.random.nextInt(16) + 8, this.random.nextInt(this.random.nextInt(248) + 8), this.random.nextInt(16) + 8);
         if (blockPos2.getY() < this.worldObj.getSeaLevel() || this.random.nextInt(10) == 0) {
            this.lavaLakeGenerator.generate(this.worldObj, this.random, blockPos2);
         }
      }

      if (this.hasDungeons) {
         for (int i = 0; i < 8; i++) {
            new WorldGenDungeons()
               .generate(this.worldObj, this.random, blockPos.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
         }
      }

      if (this.hasDecoration) {
         biomeGenForCoords.decorate(this.worldObj, this.random, blockPos);
      }
   }

   @Override
   public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z) {
      return false;
   }

   @Override
   public boolean saveChunks(boolean saveAllChunks, IProgressUpdate progressCallback) {
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
   public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      BiomeGenBase biomeGenForCoords = this.worldObj.getBiomeGenForCoords(pos);
      return biomeGenForCoords.getSpawnableList(creatureType);
   }

   @Override
   public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
      if ("Stronghold".equals(structureName)) {
         for (MapGenStructure mapGenStructure : this.structureGenerators) {
            if (mapGenStructure instanceof MapGenStronghold) {
               return mapGenStructure.getClosestStrongholdPos(worldIn, position);
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
   public void recreateStructures(Chunk chunkIn, int x, int z) {
      for (MapGenStructure mapGenStructure : this.structureGenerators) {
         mapGenStructure.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
      }
   }

   @Override
   public Chunk provideChunk(BlockPos blockPosIn) {
      return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
   }
}
