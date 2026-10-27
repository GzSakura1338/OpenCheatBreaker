package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.material.Material;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;

public class BiomeGenSwamp extends BiomeGenBase {
   public BiomeGenSwamp(int id) {
      super(id);
      this.theBiomeDecorator.treesPerChunk = 2;
      this.theBiomeDecorator.flowersPerChunk = 1;
      this.theBiomeDecorator.deadBushPerChunk = 1;
      this.theBiomeDecorator.mushroomsPerChunk = 8;
      this.theBiomeDecorator.reedsPerChunk = 10;
      this.theBiomeDecorator.clayPerChunk = 1;
      this.theBiomeDecorator.waterlilyPerChunk = 4;
      this.theBiomeDecorator.sandPerChunk2 = 0;
      this.theBiomeDecorator.sandPerChunk = 0;
      this.theBiomeDecorator.grassPerChunk = 5;
      this.waterColorMultiplier = 14745518;
      this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntitySlime.class, 1, 1, 1));
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return this.worldGeneratorSwamp;
   }

   @Override
   public int getGrassColorAtPos(BlockPos pos) {
      double noise = GRASS_COLOR_NOISE.noise(pos.getX() * 0.0225, pos.getZ() * 0.0225);
      return noise < -0.1 ? 5011004 : 6975545;
   }

   @Override
   public int getFoliageColorAtPos(BlockPos pos) {
      return 6975545;
   }

   @Override
   public EnumFlowerType pickRandomFlower(Random rand, BlockPos pos) {
      return EnumFlowerType.BLUE_ORCHID;
   }

   @Override
   public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      double noise = GRASS_COLOR_NOISE.noise(x * 0.25, z * 0.25);
      if (noise > 0.0) {
         int n = x & 15;
         int n2 = z & 15;

         for (int i = 255; i >= 0; i--) {
            if (chunkPrimerIn.getBlockState(n2, i, n).getBlock().getMaterial() != Material.air) {
               if (i == 62 && chunkPrimerIn.getBlockState(n2, i, n).getBlock() != Blocks.water) {
                  chunkPrimerIn.setBlockState(n2, i, n, Blocks.water.getDefaultState());
                  if (noise < 0.12) {
                     chunkPrimerIn.setBlockState(n2, i + 1, n, Blocks.waterlily.getDefaultState());
                  }
               }
               break;
            }
         }
      }

      this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
   }
}
