package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.block.BlockTallGrass.EnumType;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBlockBlob;
import net.minecraft.world.gen.feature.WorldGenMegaPineTree;
import net.minecraft.world.gen.feature.WorldGenTaiga1;
import net.minecraft.world.gen.feature.WorldGenTaiga2;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenTaiga extends BiomeGenBase {
   public static WorldGenTaiga1 worldGenTaiga1 = new WorldGenTaiga1();
   public static WorldGenTaiga2 worldGenTaiga2 = new WorldGenTaiga2(false);
   public static WorldGenMegaPineTree worldGenMegaPineTree = new WorldGenMegaPineTree(false, false);
   public int intValue2;
   public static WorldGenMegaPineTree worldGenMegaPineTree2 = new WorldGenMegaPineTree(false, true);
   public static WorldGenBlockBlob worldGenBlockBlob = new WorldGenBlockBlob(Blocks.mossy_cobblestone, 0);

   public BiomeGenTaiga(int id, int n) {
      super(id);
      this.intValue2 = n;
      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityWolf.class, 8, 4, 4));
      this.theBiomeDecorator.treesPerChunk = 10;
      if (n != 1 && n != 2) {
         this.theBiomeDecorator.grassPerChunk = 1;
         this.theBiomeDecorator.mushroomsPerChunk = 1;
      } else {
         this.theBiomeDecorator.grassPerChunk = 7;
         this.theBiomeDecorator.deadBushPerChunk = 1;
         this.theBiomeDecorator.mushroomsPerChunk = 3;
      }
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return (this.intValue2 == 1 || this.intValue2 == 2) && rand.nextInt(3) == 0
         ? (this.intValue2 != 2 && rand.nextInt(13) != 0 ? worldGenMegaPineTree : worldGenMegaPineTree2)
         : (rand.nextInt(3) == 0 ? worldGenTaiga1 : worldGenTaiga2);
   }

   @Override
   public WorldGenerator getRandomWorldGenForGrass(Random rand) {
      return rand.nextInt(5) > 0 ? new WorldGenTallGrass(EnumType.FERN) : new WorldGenTallGrass(EnumType.GRASS);
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      if (this.intValue2 == 1 || this.intValue2 == 2) {
         int nextInt = rand.nextInt(3);

         for (int i = 0; i < nextInt; i++) {
            int n = rand.nextInt(16) + 8;
            int n2 = rand.nextInt(16) + 8;
            BlockPos height = worldIn.getHeight(pos.add(n, 0, n2));
            worldGenBlockBlob.generate(worldIn, rand, height);
         }
      }

      DOUBLE_PLANT_GENERATOR.setPlantType(EnumPlantType.FERN);

      for (int i = 0; i < 7; i++) {
         int n = rand.nextInt(16) + 8;
         int n2 = rand.nextInt(16) + 8;
         int nextInt = rand.nextInt(worldIn.getHeight(pos.add(n, 0, n2)).getY() + 32);
         DOUBLE_PLANT_GENERATOR.generate(worldIn, rand, pos.add(n, nextInt, n2));
      }

      super.decorate(worldIn, rand, pos);
   }

   @Override
   public void genTerrainBlocks(World worldIn, Random rand, ChunkPrimer chunkPrimerIn, int x, int z, double noiseVal) {
      if (this.intValue2 == 1 || this.intValue2 == 2) {
         this.topBlock = Blocks.grass.getDefaultState();
         this.fillerBlock = Blocks.dirt.getDefaultState();
         if (noiseVal > 1.75) {
            this.topBlock = Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.COARSE_DIRT);
         } else if (noiseVal > -0.95) {
            this.topBlock = Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.PODZOL);
         }
      }

      this.generateBiomeTerrain(worldIn, rand, chunkPrimerIn, x, z, noiseVal);
   }

   @Override
   public BiomeGenBase createMutatedBiome(int n) {
      return this.biomeID == BiomeGenBase.megaTaiga.biomeID
         ? new BiomeGenTaiga(n, 2)
            .findBiomeGenBase(5858897, true)
            .setBiomeName("Mega Spruce Taiga")
            .setFillerBlockMetadata(5159473)
            .setTemperatureRainfall(0.25F, 0.8F)
            .setHeight(new BiomeGenBase.Height(this.minHeight, this.maxHeight))
         : super.createMutatedBiome(n);
   }
}
