package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockSilverfish;
import net.minecraft.block.BlockSilverfish.EnumType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenHills extends BiomeGenBase {
   public WorldGenerator theWorldGenerator = new WorldGenMinable(
      Blocks.monster_egg.getDefaultState().withProperty(BlockSilverfish.VARIANT, EnumType.STONE), 9
   );
   public WorldGenTaiga2 worldGenTaiga2 = new WorldGenTaiga2(false);
   public int intValue5 = 0;
   public int intValue2 = 1;
   public int intValue3 = 2;
   public int intValue4 = this.intValue5;

   public BiomeGenHills(int id, boolean flag) {
      super(id);
      if (flag) {
         this.theBiomeDecorator.treesPerChunk = 3;
         this.intValue4 = this.intValue2;
      }
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return rand.nextInt(3) > 0 ? this.worldGenTaiga2 : super.genBigTreeChance(rand);
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      super.decorate(worldIn, rand, pos);
      int n = 3 + rand.nextInt(6);

      for (int i = 0; i < n; i++) {
         int nextInt = rand.nextInt(16);
         int n2 = rand.nextInt(28) + 4;
         int nextInt2 = rand.nextInt(16);
         BlockPos blockPos = pos.add(nextInt, n2, nextInt2);
         if (worldIn.getBlockState(blockPos).getBlock() == Blocks.stone) {
            worldIn.setBlockState(blockPos, Blocks.emerald_ore.getDefaultState(), 2);
         }
      }

      for (int i = 0; i < 7; i++) {
         int nextInt = rand.nextInt(16);
         int nextInt2 = rand.nextInt(64);
         int nextInt3 = rand.nextInt(16);
         this.theWorldGenerator.generate(worldIn, rand, pos.add(nextInt, nextInt2, nextInt3));
      }
   }

   @Override
   public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      this.topBlock = Blocks.grass.getDefaultState();
      this.fillerBlock = Blocks.dirt.getDefaultState();
      if ((noiseVal < -1.0 || noiseVal > 2.0) && this.intValue4 == this.intValue3) {
         this.topBlock = Blocks.gravel.getDefaultState();
         this.fillerBlock = Blocks.gravel.getDefaultState();
      } else if (noiseVal > 1.0 && this.intValue4 != this.intValue2) {
         this.topBlock = Blocks.stone.getDefaultState();
         this.fillerBlock = Blocks.stone.getDefaultState();
      }

      this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
   }

   public BiomeGenHills mutateHills(BiomeGenBase biomeGenBase) {
      this.intValue4 = this.intValue3;
      this.findBiomeGenBase(biomeGenBase.color, true);
      this.setBiomeName(biomeGenBase.biomeName + " M");
      this.setHeight(new BiomeGenBase.Height(biomeGenBase.minHeight, biomeGenBase.maxHeight));
      this.setTemperatureRainfall(biomeGenBase.temperature, biomeGenBase.rainfall);
      return this;
   }

   @Override
   public BiomeGenBase createMutatedBiome(int n) {
      return new BiomeGenHills(n, false).mutateHills(this);
   }
}
