package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BiomeGenPlains extends BiomeGenBase {
   public boolean flag;

   public BiomeGenPlains(int id) {
      super(id);
      this.setTemperatureRainfall(0.8F, 0.4F);
      this.setHeight(height_LowPlains);
      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityHorse.class, 5, 2, 6));
      this.theBiomeDecorator.treesPerChunk = -999;
      this.theBiomeDecorator.flowersPerChunk = 4;
      this.theBiomeDecorator.grassPerChunk = 10;
   }

   @Override
   public EnumFlowerType pickRandomFlower(Random rand, BlockPos pos) {
      double noise = GRASS_COLOR_NOISE.noise(pos.getX() / 200.0, pos.getZ() / 200.0);
      if (noise < -0.8) {
         int nextInt = rand.nextInt(4);
         switch (nextInt) {
            case 0:
               return EnumFlowerType.ORANGE_TULIP;
            case 1:
               return EnumFlowerType.RED_TULIP;
            case 2:
               return EnumFlowerType.PINK_TULIP;
            case 3:
            default:
               return EnumFlowerType.WHITE_TULIP;
         }
      } else if (rand.nextInt(3) > 0) {
         int nextInt = rand.nextInt(3);
         return nextInt == 0 ? EnumFlowerType.POPPY : (nextInt == 1 ? EnumFlowerType.HOUSTONIA : EnumFlowerType.OXEYE_DAISY);
      } else {
         return EnumFlowerType.DANDELION;
      }
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      double noise = GRASS_COLOR_NOISE.noise((pos.getX() + 8) / 200.0, (pos.getZ() + 8) / 200.0);
      if (noise < -0.8) {
         this.theBiomeDecorator.flowersPerChunk = 15;
         this.theBiomeDecorator.grassPerChunk = 5;
      } else {
         this.theBiomeDecorator.flowersPerChunk = 4;
         this.theBiomeDecorator.grassPerChunk = 10;
         DOUBLE_PLANT_GENERATOR.setPlantType(EnumPlantType.GRASS);

         for (int i = 0; i < 7; i++) {
            int n = rand.nextInt(16) + 8;
            int n2 = rand.nextInt(16) + 8;
            int nextInt = rand.nextInt(worldIn.getHeight(pos.add(n, 0, n2)).getY() + 32);
            DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(n, nextInt, n2));
         }
      }

      if (this.flag) {
         DOUBLE_PLANT_GENERATOR.setPlantType(EnumPlantType.SUNFLOWER);

         for (int i = 0; i < 10; i++) {
            int n = rand.nextInt(16) + 8;
            int n2 = rand.nextInt(16) + 8;
            int nextInt = rand.nextInt(worldIn.getHeight(pos.add(n, 0, n2)).getY() + 32);
            DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(n, nextInt, n2));
         }
      }

      super.decorate(worldIn, rand, pos);
   }

   @Override
   public BiomeGenBase createMutatedBiome(int n) {
      BiomeGenPlains biomeGenPlains = new BiomeGenPlains(n);
      biomeGenPlains.setBiomeName("Sunflower Plains");
      biomeGenPlains.flag = true;
      biomeGenPlains.setColor(9286496);
      biomeGenPlains.intValue = 14273354;
      return biomeGenPlains;
   }
}
