package net.minecraft.world.biome;

import java.util.Arrays;
import java.util.Random;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.BlockSand;
import net.minecraft.block.BlockSand.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.NoiseGeneratorPerlin;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenMesa extends BiomeGenBase {
   public IBlockState[] layerBlocks;
   public long seed;
   public NoiseGeneratorPerlin heightCutoffNoise;
   public NoiseGeneratorPerlin heightNoise;
   public NoiseGeneratorPerlin layerNoise;
   public boolean flag;
   public boolean flag2;

   public BiomeGenMesa(int id, boolean flag, boolean flag2) {
      super(id);
      this.flag = flag;
      this.flag2 = flag2;
      this.setDisableRain();
      this.setTemperatureRainfall(2.0F, 0.0F);
      this.spawnableCreatureList.clear();
      this.topBlock = Blocks.sand.getDefaultState().withProperty(BlockSand.VARIANT, EnumType.RED_SAND);
      this.fillerBlock = Blocks.stained_hardened_clay.getDefaultState();
      this.theBiomeDecorator.treesPerChunk = -999;
      this.theBiomeDecorator.deadBushPerChunk = 20;
      this.theBiomeDecorator.reedsPerChunk = 3;
      this.theBiomeDecorator.cactiPerChunk = 5;
      this.theBiomeDecorator.flowersPerChunk = 0;
      this.spawnableCreatureList.clear();
      if (flag2) {
         this.theBiomeDecorator.treesPerChunk = 5;
      }
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return this.worldGeneratorTrees;
   }

   @Override
   public int getFoliageColorAtPos(BlockPos pos) {
      return 10387789;
   }

   @Override
   public int getGrassColorAtPos(BlockPos pos) {
      return 9470285;
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      super.decorate(worldIn, rand, pos);
   }

   @Override
   public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      if (this.layerBlocks == null || this.seed != worldIn.getSeed()) {
         this.initLayerBlocks(worldIn.getSeed());
      }

      if (this.heightCutoffNoise == null || this.heightNoise == null || this.seed != worldIn.getSeed()) {
         Random random = new Random(this.seed);
         this.heightCutoffNoise = new NoiseGeneratorPerlin(random, 4);
         this.heightNoise = new NoiseGeneratorPerlin(random, 1);
      }

      this.seed = worldIn.getSeed();
      double d = 0.0;
      if (this.flag) {
         int n = (x & -16) + (z & 15);
         int n2 = (z & -16) + (x & 15);
         double min = Math.min(Math.abs(noiseVal), this.heightCutoffNoise.noise(n * 0.25, n2 * 0.25));
         if (min > 0.0) {
            double d2 = 0.001953125;
            double abs = Math.abs(this.heightNoise.noise(n * d2, n2 * d2));
            d = min * min * 2.5;
            double d3 = Math.ceil(abs * 50.0) + 14.0;
            if (d > d3) {
               d = d3;
            }

            d += 64.0;
         }
      }

      int n = x & 15;
      int n2 = z & 15;
      int seaLevel = worldIn.getSeaLevel();
      IBlockState defaultState = Blocks.stained_hardened_clay.getDefaultState();
      IBlockState fillerBlock = this.fillerBlock;
      int n3 = (int)(noiseVal / 3.0 + 3.0 + rand.nextDouble() * 0.25);
      boolean flag = Math.cos(noiseVal / 3.0 * Math.PI) > 0.0;
      int n4 = -1;
      boolean flag2 = false;

      for (int i = 255; i >= 0; i--) {
         if (chunkPrimerIn.getBlockState(n2, i, n).getBlock().getMaterial() == Material.air && i < (int)d) {
            chunkPrimerIn.setBlockState(n2, i, n, Blocks.stone.getDefaultState());
         }

         if (i <= rand.nextInt(5)) {
            chunkPrimerIn.setBlockState(n2, i, n, Blocks.bedrock.getDefaultState());
         } else {
            IBlockState blockState = chunkPrimerIn.getBlockState(n2, i, n);
            if (blockState.getBlock().getMaterial() == Material.air) {
               n4 = -1;
            } else if (blockState.getBlock() == Blocks.stone) {
               if (n4 == -1) {
                  flag2 = false;
                  if (n3 <= 0) {
                     defaultState = null;
                     fillerBlock = Blocks.stone.getDefaultState();
                  } else if (i >= seaLevel - 4 && i <= seaLevel + 1) {
                     defaultState = Blocks.stained_hardened_clay.getDefaultState();
                     fillerBlock = this.fillerBlock;
                  }

                  if (i < seaLevel && (defaultState == null || defaultState.getBlock().getMaterial() == Material.air)) {
                     defaultState = Blocks.water.getDefaultState();
                  }

                  n4 = n3 + Math.max(0, i - seaLevel);
                  if (i < seaLevel - 1) {
                     chunkPrimerIn.setBlockState(n2, i, n, fillerBlock);
                     if (fillerBlock.getBlock() == Blocks.stained_hardened_clay) {
                        chunkPrimerIn.setBlockState(
                           n2, i, n, fillerBlock.getBlock().getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE)
                        );
                     }
                  } else if (!this.flag2 || i <= 86 + n3 * 2) {
                     if (i <= seaLevel + 3 + n3) {
                        chunkPrimerIn.setBlockState(n2, i, n, this.topBlock);
                        flag2 = true;
                     } else {
                        IBlockState blockState2;
                        if (i < 64 || i > 127) {
                           blockState2 = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE);
                        } else if (flag) {
                           blockState2 = Blocks.hardened_clay.getDefaultState();
                        } else {
                           blockState2 = this.calculateLayerBlockState(x, i, z);
                        }

                        chunkPrimerIn.setBlockState(n2, i, n, blockState2);
                     }
                  } else if (flag) {
                     chunkPrimerIn.setBlockState(
                        n2, i, n, Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.COARSE_DIRT)
                     );
                  } else {
                     chunkPrimerIn.setBlockState(n2, i, n, Blocks.grass.getDefaultState());
                  }
               } else if (n4 > 0) {
                  n4--;
                  if (flag2) {
                     chunkPrimerIn.setBlockState(
                        n2, i, n, Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE)
                     );
                  } else {
                     IBlockState calculateLayerBlockState = this.calculateLayerBlockState(x, i, z);
                     chunkPrimerIn.setBlockState(n2, i, n, calculateLayerBlockState);
                  }
               }
            }
         }
      }
   }

   public void initLayerBlocks(long l) {
      this.layerBlocks = new IBlockState[64];
      Arrays.fill(this.layerBlocks, Blocks.hardened_clay.getDefaultState());
      Random random = new Random(l);
      this.layerNoise = new NoiseGeneratorPerlin(random, 1);

      for (int i = 0; i < 64; i++) {
         i += random.nextInt(5) + 1;
         if (i < 64) {
            this.layerBlocks[i] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.ORANGE);
         }
      }

      int n = random.nextInt(4) + 2;

      for (int i = 0; i < n; i++) {
         int n2 = random.nextInt(3) + 1;
         int nextInt = random.nextInt(64);

         for (int j = 0; nextInt + j < 64 && j < n2; j++) {
            this.layerBlocks[nextInt + j] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.YELLOW);
         }
      }

      int n2 = random.nextInt(4) + 2;

      for (int i = 0; i < n2; i++) {
         int n3 = random.nextInt(3) + 2;
         int nextInt = random.nextInt(64);

         for (int j = 0; nextInt + j < 64 && j < n3; j++) {
            this.layerBlocks[nextInt + j] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BROWN);
         }
      }

      int n3 = random.nextInt(4) + 2;

      for (int i = 0; i < n3; i++) {
         int n4 = random.nextInt(3) + 1;
         int nextInt = random.nextInt(64);

         for (int j = 0; nextInt + j < 64 && j < n4; j++) {
            this.layerBlocks[nextInt + j] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.RED);
         }
      }

      int n4 = random.nextInt(3) + 3;
      int n5 = 0;

      for (int i = 0; i < n4; i++) {
         byte b = 1;
         n5 += random.nextInt(16) + 4;

         for (int j = 0; n5 + j < 64 && j < b; j++) {
            this.layerBlocks[n5 + j] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.WHITE);
            if (n5 + j > 1 && random.nextBoolean()) {
               this.layerBlocks[n5 + j - 1] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
            }

            if (n5 + j < 63 && random.nextBoolean()) {
               this.layerBlocks[n5 + j + 1] = Blocks.stained_hardened_clay.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.SILVER);
            }
         }
      }
   }

   public IBlockState calculateLayerBlockState(int n, int n2, int n3) {
      int round = (int)Math.round(this.layerNoise.noise(n * 1.0 / 512.0, n * 1.0 / 512.0) * 2.0);
      return this.layerBlocks[(n2 + round + 64) % 64];
   }

   @Override
   public BiomeGenBase createMutatedBiome(int n) {
      boolean flag = this.biomeID == BiomeGenBase.mesa.biomeID;
      BiomeGenMesa biomeGenMesa = new BiomeGenMesa(n, flag, this.flag2);
      if (!flag) {
         biomeGenMesa.setHeight(height_LowHills);
         biomeGenMesa.setBiomeName(this.biomeName + " M");
      } else {
         biomeGenMesa.setBiomeName(this.biomeName + " (Bryce)");
      }

      biomeGenMesa.findBiomeGenBase(this.color, true);
      return biomeGenMesa;
   }
}
