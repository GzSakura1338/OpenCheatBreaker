package net.minecraft.world.gen;

import java.util.List;
import java.util.Random;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenFire;
import net.minecraft.world.gen.feature.WorldGenGlowStone1;
import net.minecraft.world.gen.feature.WorldGenGlowStone2;
import net.minecraft.world.gen.feature.WorldGenHellLava;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenerator;
import net.minecraft.world.gen.structure.MapGenNetherBridge;

public class ChunkProviderHell implements IChunkProvider {
   public double[] slowsandNoise = new double[256];
   public double[] gravelNoise = new double[256];
   public double[] netherrackExclusivityNoise = new double[256];
   public WorldGenFire fireFeature = new WorldGenFire();
   public WorldGenGlowStone1 glowstoneFeature = new WorldGenGlowStone1();
   public WorldGenGlowStone2 glowstoneClusterFeature = new WorldGenGlowStone2();
   public double[] noiseField;
   public WorldGenerator quartzFeature = new WorldGenMinable(Blocks.quartz_ore.getDefaultState(), 14, BlockHelper.forBlock(Blocks.netherrack));
   public WorldGenHellLava worldGenHellLava = new WorldGenHellLava(Blocks.flowing_lava, true);
   public WorldGenHellLava worldGenHellLava2 = new WorldGenHellLava(Blocks.flowing_lava, false);
   public GeneratorBushFeature brownMushroomFeature = new GeneratorBushFeature(Blocks.brown_mushroom);
   public GeneratorBushFeature redMushroomFeature = new GeneratorBushFeature(Blocks.red_mushroom);
   public MapGenNetherBridge genNetherBridge = new MapGenNetherBridge();
   public MapGenBase netherCaveGenerator = new MapGenCavesHell();
   public World worldObj;
   public boolean hasStructures;
   public Random hellRNG;
   public NoiseGeneratorOctaves netherNoiseGen1;
   public NoiseGeneratorOctaves netherNoiseGen2;
   public NoiseGeneratorOctaves netherNoiseGen3;
   public NoiseGeneratorOctaves slowsandGravelNoiseGen;
   public NoiseGeneratorOctaves netherrackExculsivityNoiseGen;
   public NoiseGeneratorOctaves netherNoiseGen6;
   public NoiseGeneratorOctaves netherNoiseGen7;
   public double[] noiseData1;
   public double[] noiseData2;
   public double[] noiseData3;
   public double[] noiseData4;
   public double[] noiseData5;

   public ChunkProviderHell(World worldIn, boolean flag, long seed) {
      this.worldObj = worldIn;
      this.hasStructures = flag;
      this.hellRNG = new Random(seed);
      this.netherNoiseGen1 = new NoiseGeneratorOctaves(this.hellRNG, 16);
      this.netherNoiseGen2 = new NoiseGeneratorOctaves(this.hellRNG, 16);
      this.netherNoiseGen3 = new NoiseGeneratorOctaves(this.hellRNG, 8);
      this.slowsandGravelNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
      this.netherrackExculsivityNoiseGen = new NoiseGeneratorOctaves(this.hellRNG, 4);
      this.netherNoiseGen6 = new NoiseGeneratorOctaves(this.hellRNG, 10);
      this.netherNoiseGen7 = new NoiseGeneratorOctaves(this.hellRNG, 16);
      worldIn.setSeaLevel(63);
   }

   public void process(int n, int n2, ChunkPrimer chunkPrimer) {
      byte b = 4;
      int n3 = this.worldObj.getSeaLevel() / 2 + 1;
      int n4 = b + 1;
      byte b2 = 17;
      int n5 = b + 1;
      this.noiseField = this.initializeNoiseField(this.noiseField, n * b, 0, n2 * b, n4, b2, n5);

      for (int i = 0; i < b; i++) {
         for (int j = 0; j < b; j++) {
            for (int k = 0; k < 16; k++) {
               double d = 0.125;
               double d2 = this.noiseField[((i + 0) * n5 + j + 0) * b2 + k + 0];
               double d3 = this.noiseField[((i + 0) * n5 + j + 1) * b2 + k + 0];
               double d4 = this.noiseField[((i + 1) * n5 + j + 0) * b2 + k + 0];
               double d5 = this.noiseField[((i + 1) * n5 + j + 1) * b2 + k + 0];
               double d6 = (this.noiseField[((i + 0) * n5 + j + 0) * b2 + k + 1] - d2) * d;
               double d7 = (this.noiseField[((i + 0) * n5 + j + 1) * b2 + k + 1] - d3) * d;
               double d8 = (this.noiseField[((i + 1) * n5 + j + 0) * b2 + k + 1] - d4) * d;
               double d9 = (this.noiseField[((i + 1) * n5 + j + 1) * b2 + k + 1] - d5) * d;

               for (int m = 0; m < 8; m++) {
                  double d10 = 0.25;
                  double d11 = d2;
                  double d12 = d3;
                  double d13 = (d4 - d2) * d10;
                  double d14 = (d5 - d3) * d10;

                  for (int n6 = 0; n6 < 4; n6++) {
                     double d15 = 0.25;
                     double d16 = d11;
                     double d17 = (d12 - d11) * d15;

                     for (int i5 = 0; i5 < 4; i5++) {
                        IBlockState blockState = null;
                        if (k * 8 + m < n3) {
                           blockState = Blocks.lava.getDefaultState();
                        }

                        if (d16 > 0.0) {
                           blockState = Blocks.netherrack.getDefaultState();
                        }

                        int n7 = n6 + i * 4;
                        int n8 = m + k * 8;
                        int n9 = i5 + j * 4;
                        chunkPrimer.setBlockState(n7, n8, n9, blockState);
                        d16 += d17;
                     }

                     d11 += d13;
                     d12 += d14;
                  }

                  d2 += d6;
                  d3 += d7;
                  d4 += d8;
                  d5 += d9;
               }
            }
         }
      }
   }

   public void process2(int n, int n2, ChunkPrimer chunkPrimer) {
      int n3 = this.worldObj.getSeaLevel() + 1;
      double d = 0.03125;
      this.slowsandNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.slowsandNoise, n * 16, n2 * 16, 0, 16, 16, 1, d, d, 1.0);
      this.gravelNoise = this.slowsandGravelNoiseGen.generateNoiseOctaves(this.gravelNoise, n * 16, 109, n2 * 16, 16, 1, 16, d, 1.0, d);
      this.netherrackExclusivityNoise = this.netherrackExculsivityNoiseGen
         .generateNoiseOctaves(this.netherrackExclusivityNoise, n * 16, n2 * 16, 0, 16, 16, 1, d * 2.0, d * 2.0, d * 2.0);

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            boolean flag = this.slowsandNoise[i + j * 16] + this.hellRNG.nextDouble() * 0.2 > 0.0;
            boolean flag2 = this.gravelNoise[i + j * 16] + this.hellRNG.nextDouble() * 0.2 > 0.0;
            int n4 = (int)(this.netherrackExclusivityNoise[i + j * 16] / 3.0 + 3.0 + this.hellRNG.nextDouble() * 0.25);
            int n5 = -1;
            IBlockState defaultState = Blocks.netherrack.getDefaultState();
            IBlockState defaultState2 = Blocks.netherrack.getDefaultState();

            for (int k = 127; k >= 0; k--) {
               if (k < 127 - this.hellRNG.nextInt(5) && k > this.hellRNG.nextInt(5)) {
                  IBlockState blockState = chunkPrimer.getBlockState(j, k, i);
                  if (blockState.getBlock() == null || blockState.getBlock().getMaterial() == Material.air) {
                     n5 = -1;
                  } else if (blockState.getBlock() == Blocks.netherrack) {
                     if (n5 == -1) {
                        if (n4 <= 0) {
                           defaultState = null;
                           defaultState2 = Blocks.netherrack.getDefaultState();
                        } else if (k >= n3 - 4 && k <= n3 + 1) {
                           defaultState = Blocks.netherrack.getDefaultState();
                           defaultState2 = Blocks.netherrack.getDefaultState();
                           if (flag2) {
                              defaultState = Blocks.gravel.getDefaultState();
                              defaultState2 = Blocks.netherrack.getDefaultState();
                           }

                           if (flag) {
                              defaultState = Blocks.soul_sand.getDefaultState();
                              defaultState2 = Blocks.soul_sand.getDefaultState();
                           }
                        }

                        if (k < n3 && (defaultState == null || defaultState.getBlock().getMaterial() == Material.air)) {
                           defaultState = Blocks.lava.getDefaultState();
                        }

                        n5 = n4;
                        if (k >= n3 - 1) {
                           chunkPrimer.setBlockState(j, k, i, defaultState);
                        } else {
                           chunkPrimer.setBlockState(j, k, i, defaultState2);
                        }
                     } else if (n5 > 0) {
                        n5--;
                        chunkPrimer.setBlockState(j, k, i, defaultState2);
                     }
                  }
               } else {
                  chunkPrimer.setBlockState(j, k, i, Blocks.bedrock.getDefaultState());
               }
            }
         }
      }
   }

   @Override
   public Chunk provideChunk(int x, int z) {
      this.hellRNG.setSeed(x * 341873128712L + z * 132897987541L);
      ChunkPrimer chunkPrimer = new ChunkPrimer();
      this.process(x, z, chunkPrimer);
      this.process2(x, z, chunkPrimer);
      this.netherCaveGenerator.generate(this, this.worldObj, x, z, chunkPrimer);
      if (this.hasStructures) {
         this.genNetherBridge.generate(this, this.worldObj, x, z, chunkPrimer);
      }

      Chunk chunk = new Chunk(this.worldObj, chunkPrimer, x, z);
      BiomeGenBase[] loadBlockGeneratorData = this.worldObj.getWorldChunkManager().loadBlockGeneratorData((BiomeGenBase[])null, x * 16, z * 16, 16, 16);
      byte[] biomeArray = chunk.getBiomeArray();

      for (int i = 0; i < biomeArray.length; i++) {
         biomeArray[i] = (byte)loadBlockGeneratorData[i].biomeID;
      }

      chunk.resetRelightChecks();
      return chunk;
   }

   public double[] initializeNoiseField(double[] doubles, int n, int n2, int n3, int n4, int n5, int n6) {
      if (doubles == null) {
         doubles = new double[n4 * n5 * n6];
      }

      double d = 684.412;
      double d2 = 2053.236;
      this.noiseData4 = this.netherNoiseGen6.generateNoiseOctaves(this.noiseData4, n, n2, n3, n4, 1, n6, 1.0, 0.0, 1.0);
      this.noiseData5 = this.netherNoiseGen7.generateNoiseOctaves(this.noiseData5, n, n2, n3, n4, 1, n6, 100.0, 0.0, 100.0);
      this.noiseData1 = this.netherNoiseGen3.generateNoiseOctaves(this.noiseData1, n, n2, n3, n4, n5, n6, d / 80.0, d2 / 60.0, d / 80.0);
      this.noiseData2 = this.netherNoiseGen1.generateNoiseOctaves(this.noiseData2, n, n2, n3, n4, n5, n6, d, d2, d);
      this.noiseData3 = this.netherNoiseGen2.generateNoiseOctaves(this.noiseData3, n, n2, n3, n4, n5, n6, d, d2, d);
      int n7 = 0;
      double[] doubles2 = new double[n5];

      for (int i = 0; i < n5; i++) {
         doubles2[i] = Math.cos(i * Math.PI * 6.0 / n5) * 2.0;
         double d3 = i;
         if (i > n5 / 2) {
            d3 = n5 - 1 - i;
         }

         if (d3 < 4.0) {
            d3 = 4.0 - d3;
            doubles2[i] -= d3 * d3 * d3 * 10.0;
         }
      }

      for (int i = 0; i < n4; i++) {
         for (int j = 0; j < n6; j++) {
            double d3 = 0.0;

            for (int k = 0; k < n5; k++) {
               double d4 = 0.0;
               double d5 = doubles2[k];
               double d6 = this.noiseData2[n7] / 512.0;
               double d7 = this.noiseData3[n7] / 512.0;
               double d8 = (this.noiseData1[n7] / 10.0 + 1.0) / 2.0;
               if (d8 < 0.0) {
                  d4 = d6;
               } else if (d8 > 1.0) {
                  d4 = d7;
               } else {
                  d4 = d6 + (d7 - d6) * d8;
               }

               d4 -= d5;
               if (k > n5 - 4) {
                  double d9 = (k - (n5 - 4)) / 3.0F;
                  d4 = d4 * (1.0 - d9) + -10.0 * d9;
               }

               if (k < d3) {
                  double d9 = (d3 - k) / 4.0;
                  d9 = MathHelper.clamp_double(d9, 0.0, 1.0);
                  d4 = d4 * (1.0 - d9) + -10.0 * d9;
               }

               doubles[n7] = d4;
               n7++;
            }
         }
      }

      return doubles;
   }

   @Override
   public boolean chunkExists(int x, int z) {
      return true;
   }

   @Override
   public void populate(IChunkProvider chunkProvider, int x, int z) {
      BlockFalling.fallInstantly = true;
      BlockPos blockPos = new BlockPos(x * 16, 0, z * 16);
      ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(x, z);
      this.genNetherBridge.generateStructure(this.worldObj, this.hellRNG, chunkCoordIntPair);

      for (int i = 0; i < 8; i++) {
         this.worldGenHellLava2
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
      }

      for (int i = 0; i < this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1) + 1; i++) {
         this.fireFeature
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
      }

      for (int i = 0; i < this.hellRNG.nextInt(this.hellRNG.nextInt(10) + 1); i++) {
         this.glowstoneFeature
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(120) + 4, this.hellRNG.nextInt(16) + 8));
      }

      for (int i = 0; i < 10; i++) {
         this.glowstoneClusterFeature
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
      }

      if (this.hellRNG.nextBoolean()) {
         this.brownMushroomFeature
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
      }

      if (this.hellRNG.nextBoolean()) {
         this.redMushroomFeature
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16) + 8, this.hellRNG.nextInt(128), this.hellRNG.nextInt(16) + 8));
      }

      for (int i = 0; i < 16; i++) {
         this.quartzFeature.generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16), this.hellRNG.nextInt(108) + 10, this.hellRNG.nextInt(16)));
      }

      for (int i = 0; i < 16; i++) {
         this.worldGenHellLava
            .generate(this.worldObj, this.hellRNG, blockPos.add(this.hellRNG.nextInt(16), this.hellRNG.nextInt(108) + 10, this.hellRNG.nextInt(16)));
      }

      BlockFalling.fallInstantly = false;
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
      return "HellRandomLevelSource";
   }

   @Override
   public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      if (creatureType == EnumCreatureType.MONSTER) {
         if (this.genNetherBridge.checkBlockPos(pos)) {
            return this.genNetherBridge.getSpawnList();
         }

         if (this.genNetherBridge.isPositionInStructure(this.worldObj, pos) && this.worldObj.getBlockState(pos.down()).getBlock() == Blocks.nether_brick) {
            return this.genNetherBridge.getSpawnList();
         }
      }

      BiomeGenBase biomeGenForCoords = this.worldObj.getBiomeGenForCoords(pos);
      return biomeGenForCoords.getSpawnableList(creatureType);
   }

   @Override
   public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
      return null;
   }

   @Override
   public int getLoadedChunkCount() {
      return 0;
   }

   @Override
   public void recreateStructures(Chunk chunkIn, int x, int z) {
      this.genNetherBridge.generate(this, this.worldObj, x, z, (ChunkPrimer)null);
   }

   @Override
   public Chunk provideChunk(BlockPos blockPosIn) {
      return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
   }
}
