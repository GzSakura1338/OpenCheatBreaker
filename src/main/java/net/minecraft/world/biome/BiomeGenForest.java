package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCanopyTree;
import net.minecraft.world.gen.feature.WorldGenForest;

public class BiomeGenForest extends BiomeGenBase {
   public int intValue2;
   public static WorldGenForest worldGenForest = new WorldGenForest(false, true);
   public static WorldGenForest worldGenForest2 = new WorldGenForest(false, false);
   public static WorldGenCanopyTree worldGenCanopyTree = new WorldGenCanopyTree(false);

   public BiomeGenForest(int id, int n) {
      super(id);
      this.intValue2 = n;
      this.theBiomeDecorator.treesPerChunk = 10;
      this.theBiomeDecorator.grassPerChunk = 2;
      if (this.intValue2 == 1) {
         this.theBiomeDecorator.treesPerChunk = 6;
         this.theBiomeDecorator.flowersPerChunk = 100;
         this.theBiomeDecorator.grassPerChunk = 1;
      }

      this.setFillerBlockMetadata(5159473);
      this.setTemperatureRainfall(0.7F, 0.8F);
      if (this.intValue2 == 2) {
         this.intValue = 353825;
         this.color = 3175492;
         this.setTemperatureRainfall(0.6F, 0.6F);
      }

      if (this.intValue2 == 0) {
         this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityWolf.class, 5, 4, 4));
      }

      if (this.intValue2 == 3) {
         this.theBiomeDecorator.treesPerChunk = -999;
      }
   }

   @Override
   public BiomeGenBase findBiomeGenBase(int colorIn, boolean flag) {
      if (this.intValue2 == 2) {
         this.intValue = 353825;
         this.color = colorIn;
         if (flag) {
            this.intValue = (this.intValue & 16711422) >> 1;
         }

         return this;
      } else {
         return super.findBiomeGenBase(colorIn, flag);
      }
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return this.intValue2 == 3 && rand.nextInt(3) > 0
         ? worldGenCanopyTree
         : (this.intValue2 != 2 && rand.nextInt(5) != 0 ? this.worldGeneratorTrees : worldGenForest2);
   }

   @Override
   public EnumFlowerType pickRandomFlower(Random rand, BlockPos pos) {
      if (this.intValue2 == 1) {
         double d = MathHelper.clamp_double((1.0 + GRASS_COLOR_NOISE.noise(pos.getX() / 48.0, pos.getZ() / 48.0)) / 2.0, 0.0, 0.9999);
         EnumFlowerType enumFlowerType = EnumFlowerType.values()[(int)(d * EnumFlowerType.values().length)];
         return enumFlowerType == EnumFlowerType.BLUE_ORCHID ? EnumFlowerType.POPPY : enumFlowerType;
      } else {
         return super.pickRandomFlower(rand, pos);
      }
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      if (this.intValue2 == 3) {
         for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
               int n = i * 4 + 1 + 8 + rand.nextInt(3);
               int n2 = j * 4 + 1 + 8 + rand.nextInt(3);
               BlockPos height = worldIn.getHeight(pos.add(n, 0, n2));
               if (rand.nextInt(20) == 0) {
                  WorldGenBigMushroom worldGenBigMushroom = new WorldGenBigMushroom();
                  worldGenBigMushroom.generate(worldIn, rand, height);
               } else {
                  WorldGenAbstractTree genBigTreeChance = this.genBigTreeChance(rand);
                  genBigTreeChance.setLeafRadius();
                  if (genBigTreeChance.generate(worldIn, rand, height)) {
                     genBigTreeChance.handleWorld(worldIn, rand, height);
                  }
               }
            }
         }
      }

      int n = rand.nextInt(5) - 3;
      if (this.intValue2 == 1) {
         n += 2;
      }

      for (int i = 0; i < n; i++) {
         int nextInt = rand.nextInt(3);
         if (nextInt == 0) {
            DOUBLE_PLANT_GENERATOR.setPlantType(EnumPlantType.SYRINGA);
         } else if (nextInt == 1) {
            DOUBLE_PLANT_GENERATOR.setPlantType(EnumPlantType.ROSE);
         } else if (nextInt == 2) {
            DOUBLE_PLANT_GENERATOR.setPlantType(EnumPlantType.PAEONIA);
         }

         for (int j = 0; j < 5; j++) {
            int n2 = rand.nextInt(16) + 8;
            int n3 = rand.nextInt(16) + 8;
            int nextInt2 = rand.nextInt(worldIn.getHeight(pos.add(n2, 0, n3)).getY() + 32);
            if (DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, new BlockPos(pos.getX() + n2, nextInt2, pos.getZ() + n3))) {
               break;
            }
         }
      }

      super.decorate(worldIn, rand, pos);
   }

   @Override
   public int getGrassColorAtPos(BlockPos pos) {
      int grassColorAtPos = super.getGrassColorAtPos(pos);
      return this.intValue2 == 3 ? (grassColorAtPos & 16711422) + 2634762 >> 1 : grassColorAtPos;
   }

   @Override
   public BiomeGenBase createMutatedBiome(int n) {
      if (this.biomeID == BiomeGenBase.forest.biomeID) {
         BiomeGenForest biomeGenForest = new BiomeGenForest(n, 1);
         biomeGenForest.setHeight(new BiomeGenBase.Height(this.minHeight, this.maxHeight + 0.2F));
         biomeGenForest.setBiomeName("Flower Forest");
         biomeGenForest.findBiomeGenBase(6976549, true);
         biomeGenForest.setFillerBlockMetadata(8233509);
         return biomeGenForest;
      } else {
         return this.biomeID != BiomeGenBase.birchForest.biomeID && this.biomeID != BiomeGenBase.birchForestHills.biomeID ? new BiomeGenMutated(n, this) {
            @Override
            public void decorate(World worldIn, Random rand, BlockPos pos) {
               this.baseBiome.decorate(worldIn, rand, pos);
            }
         } : new BiomeGenMutated(n, this) {
            @Override
            public WorldGenAbstractTree genBigTreeChance(Random rand) {
               return rand.nextBoolean() ? BiomeGenForest.worldGenForest : BiomeGenForest.worldGenForest2;
            }
         };
      }
   }
}
