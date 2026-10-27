package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockFlower;
import net.minecraft.block.BlockFlower.EnumFlowerType;
import net.minecraft.block.BlockStone;
import net.minecraft.block.BlockStone.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.ChunkProviderSettings;
import net.minecraft.world.gen.ChunkProviderSettings.Factory;
import net.minecraft.world.gen.GeneratorBushFeature;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;
import net.minecraft.world.gen.feature.WorldGenCactus;
import net.minecraft.world.gen.feature.WorldGenClay;
import net.minecraft.world.gen.feature.WorldGenDeadBush;
import net.minecraft.world.gen.feature.WorldGenFlowers;
import net.minecraft.world.gen.feature.WorldGenLiquids;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraft.world.gen.feature.WorldGenPumpkin;
import net.minecraft.world.gen.feature.WorldGenReed;
import net.minecraft.world.gen.feature.WorldGenSand;
import net.minecraft.world.gen.feature.WorldGenWaterlily;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeDecorator {
   public World currentWorld;
   public Random randomGenerator;
   public BlockPos startPos;
   public ChunkProviderSettings chunkProviderSettings;
   public WorldGenerator clayGen = new WorldGenClay(4);
   public WorldGenerator sandGen = new WorldGenSand(Blocks.sand, 7);
   public WorldGenerator gravelAsSandGen = new WorldGenSand(Blocks.gravel, 6);
   public WorldGenerator dirtGen;
   public WorldGenerator gravelGen;
   public WorldGenerator graniteGen;
   public WorldGenerator dioriteGen;
   public WorldGenerator andesiteGen;
   public WorldGenerator coalGen;
   public WorldGenerator ironGen;
   public WorldGenerator goldGen;
   public WorldGenerator redstoneGen;
   public WorldGenerator diamondGen;
   public WorldGenerator lapisGen;
   public WorldGenFlowers yellowFlowerGen = new WorldGenFlowers(Blocks.yellow_flower, EnumFlowerType.DANDELION);
   public WorldGenerator mushroomBrownGen = new GeneratorBushFeature(Blocks.brown_mushroom);
   public WorldGenerator mushroomRedGen = new GeneratorBushFeature(Blocks.red_mushroom);
   public WorldGenerator bigMushroomGen = new WorldGenBigMushroom();
   public WorldGenerator reedGen = new WorldGenReed();
   public WorldGenerator cactusGen = new WorldGenCactus();
   public WorldGenerator waterlilyGen = new WorldGenWaterlily();
   public int waterlilyPerChunk;
   public int treesPerChunk;
   public int flowersPerChunk = 2;
   public int grassPerChunk = 1;
   public int deadBushPerChunk;
   public int mushroomsPerChunk;
   public int reedsPerChunk;
   public int cactiPerChunk;
   public int sandPerChunk = 1;
   public int sandPerChunk2 = 3;
   public int clayPerChunk = 1;
   public int bigMushroomsPerChunk;
   public boolean generateLakes = true;

   public void decorate(World worldIn, Random random, BiomeGenBase biome, BlockPos blockPos) {
      if (this.currentWorld != null) {
         throw new RuntimeException("Already decorating");
      }

      this.currentWorld = worldIn;
      String generatorOptions = worldIn.getWorldInfo().getGeneratorOptions();
      if (generatorOptions != null) {
         this.chunkProviderSettings = Factory.jsonToFactory(generatorOptions).build();
      } else {
         this.chunkProviderSettings = Factory.jsonToFactory("").build();
      }

      this.randomGenerator = random;
      this.startPos = blockPos;
      this.dirtGen = new WorldGenMinable(Blocks.dirt.getDefaultState(), this.chunkProviderSettings.dirtSize);
      this.gravelGen = new WorldGenMinable(Blocks.gravel.getDefaultState(), this.chunkProviderSettings.gravelSize);
      this.graniteGen = new WorldGenMinable(
         Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.GRANITE), this.chunkProviderSettings.graniteSize
      );
      this.dioriteGen = new WorldGenMinable(
         Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.DIORITE), this.chunkProviderSettings.dioriteSize
      );
      this.andesiteGen = new WorldGenMinable(
         Blocks.stone.getDefaultState().withProperty(BlockStone.VARIANT, EnumType.ANDESITE), this.chunkProviderSettings.andesiteSize
      );
      this.coalGen = new WorldGenMinable(Blocks.coal_ore.getDefaultState(), this.chunkProviderSettings.coalSize);
      this.ironGen = new WorldGenMinable(Blocks.iron_ore.getDefaultState(), this.chunkProviderSettings.ironSize);
      this.goldGen = new WorldGenMinable(Blocks.gold_ore.getDefaultState(), this.chunkProviderSettings.goldSize);
      this.redstoneGen = new WorldGenMinable(Blocks.redstone_ore.getDefaultState(), this.chunkProviderSettings.redstoneSize);
      this.diamondGen = new WorldGenMinable(Blocks.diamond_ore.getDefaultState(), this.chunkProviderSettings.diamondSize);
      this.lapisGen = new WorldGenMinable(Blocks.lapis_ore.getDefaultState(), this.chunkProviderSettings.lapisSize);
      this.genDecorations(biome);
      this.currentWorld = null;
      this.randomGenerator = null;
   }

   public void genDecorations(BiomeGenBase biomeGenBaseIn) {
      this.generateOres();

      for (int i = 0; i < this.sandPerChunk2; i++) {
         int n = this.randomGenerator.nextInt(16) + 8;
         int n2 = this.randomGenerator.nextInt(16) + 8;
         this.sandGen.generate(this.currentWorld, this.randomGenerator, this.currentWorld.getTopSolidOrLiquidBlock(this.startPos.add(n, 0, n2)));
      }

      for (int i = 0; i < this.clayPerChunk; i++) {
         int n = this.randomGenerator.nextInt(16) + 8;
         int n2 = this.randomGenerator.nextInt(16) + 8;
         this.clayGen.generate(this.currentWorld, this.randomGenerator, this.currentWorld.getTopSolidOrLiquidBlock(this.startPos.add(n, 0, n2)));
      }

      for (int i = 0; i < this.sandPerChunk; i++) {
         int n = this.randomGenerator.nextInt(16) + 8;
         int n2 = this.randomGenerator.nextInt(16) + 8;
         this.gravelAsSandGen.generate(this.currentWorld, this.randomGenerator, this.currentWorld.getTopSolidOrLiquidBlock(this.startPos.add(n, 0, n2)));
      }

      int treesPerChunk = this.treesPerChunk;
      if (this.randomGenerator.nextInt(10) == 0) {
         treesPerChunk++;
      }

      for (int i = 0; i < treesPerChunk; i++) {
         int n = this.randomGenerator.nextInt(16) + 8;
         int n2 = this.randomGenerator.nextInt(16) + 8;
         WorldGenAbstractTree genBigTreeChance = biomeGenBaseIn.genBigTreeChance(this.randomGenerator);
         genBigTreeChance.setLeafRadius();
         BlockPos height = this.currentWorld.getHeight(this.startPos.add(n, 0, n2));
         if (genBigTreeChance.generate(this.currentWorld, this.randomGenerator, height)) {
            genBigTreeChance.handleWorld(this.currentWorld, this.randomGenerator, height);
         }
      }

      for (int i = 0; i < this.bigMushroomsPerChunk; i++) {
         int n = this.randomGenerator.nextInt(16) + 8;
         int n2 = this.randomGenerator.nextInt(16) + 8;
         this.bigMushroomGen.generate(this.currentWorld, this.randomGenerator, this.currentWorld.getHeight(this.startPos.add(n, 0, n2)));
      }

      for (int i = 0; i < this.flowersPerChunk; i++) {
         int n = this.randomGenerator.nextInt(16) + 8;
         int n2 = this.randomGenerator.nextInt(16) + 8;
         int n3 = this.currentWorld.getHeight(this.startPos.add(n, 0, n2)).getY() + 32;
         if (n3 > 0) {
            int nextInt = this.randomGenerator.nextInt(n3);
            BlockPos blockPos = this.startPos.add(n, nextInt, n2);
            EnumFlowerType pickRandomFlower = biomeGenBaseIn.pickRandomFlower(this.randomGenerator, blockPos);
            BlockFlower block = pickRandomFlower.getBlockType().getBlock();
            if (block.getMaterial() != Material.air) {
               this.yellowFlowerGen.setGeneratedBlock(block, pickRandomFlower);
               this.yellowFlowerGen.generate(this.currentWorld, this.randomGenerator, blockPos);
            }
         }
      }

      for (int i = 0; i < this.grassPerChunk; i++) {
         int n = this.randomGenerator.nextInt(16) + 8;
         int n2 = this.randomGenerator.nextInt(16) + 8;
         int n3 = this.currentWorld.getHeight(this.startPos.add(n, 0, n2)).getY() * 2;
         if (n3 > 0) {
            int nextInt = this.randomGenerator.nextInt(n3);
            biomeGenBaseIn.getRandomWorldGenForGrass(this.randomGenerator)
               .generate(this.currentWorld, this.randomGenerator, this.startPos.add(n, nextInt, n2));
         }
      }

      for (int i = 0; i < this.deadBushPerChunk; i++) {
         int n = this.randomGenerator.nextInt(16) + 8;
         int n2 = this.randomGenerator.nextInt(16) + 8;
         int n3 = this.currentWorld.getHeight(this.startPos.add(n, 0, n2)).getY() * 2;
         if (n3 > 0) {
            int nextInt = this.randomGenerator.nextInt(n3);
            new WorldGenDeadBush().generate(this.currentWorld, this.randomGenerator, this.startPos.add(n, nextInt, n2));
         }
      }

      for (int i = 0; i < this.waterlilyPerChunk; i++) {
         int n = this.randomGenerator.nextInt(16) + 8;
         int n2 = this.randomGenerator.nextInt(16) + 8;
         int n3 = this.currentWorld.getHeight(this.startPos.add(n, 0, n2)).getY() * 2;
         if (n3 > 0) {
            int nextInt = this.randomGenerator.nextInt(n3);
            BlockPos blockPos = this.startPos.add(n, nextInt, n2);

            while (blockPos.getY() > 0) {
               BlockPos down = blockPos.down();
               if (!this.currentWorld.isAirBlock(down)) {
                  break;
               }

               blockPos = down;
            }

            this.waterlilyGen.generate(this.currentWorld, this.randomGenerator, blockPos);
         }
      }

      for (int i = 0; i < this.mushroomsPerChunk; i++) {
         if (this.randomGenerator.nextInt(4) == 0) {
            int n = this.randomGenerator.nextInt(16) + 8;
            int n2 = this.randomGenerator.nextInt(16) + 8;
            BlockPos height = this.currentWorld.getHeight(this.startPos.add(n, 0, n2));
            this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, height);
         }

         if (this.randomGenerator.nextInt(8) == 0) {
            int n = this.randomGenerator.nextInt(16) + 8;
            int n2 = this.randomGenerator.nextInt(16) + 8;
            int n3 = this.currentWorld.getHeight(this.startPos.add(n, 0, n2)).getY() * 2;
            if (n3 > 0) {
               int nextInt = this.randomGenerator.nextInt(n3);
               BlockPos blockPos = this.startPos.add(n, nextInt, n2);
               this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator, blockPos);
            }
         }
      }

      if (this.randomGenerator.nextInt(4) == 0) {
         int n = this.randomGenerator.nextInt(16) + 8;
         int n2 = this.randomGenerator.nextInt(16) + 8;
         int n3 = this.currentWorld.getHeight(this.startPos.add(n, 0, n2)).getY() * 2;
         if (n3 > 0) {
            int nextInt = this.randomGenerator.nextInt(n3);
            this.mushroomBrownGen.generate(this.currentWorld, this.randomGenerator, this.startPos.add(n, nextInt, n2));
         }
      }

      if (this.randomGenerator.nextInt(8) == 0) {
         int n = this.randomGenerator.nextInt(16) + 8;
         int n2 = this.randomGenerator.nextInt(16) + 8;
         int n3 = this.currentWorld.getHeight(this.startPos.add(n, 0, n2)).getY() * 2;
         if (n3 > 0) {
            int nextInt = this.randomGenerator.nextInt(n3);
            this.mushroomRedGen.generate(this.currentWorld, this.randomGenerator, this.startPos.add(n, nextInt, n2));
         }
      }

      for (int i = 0; i < this.reedsPerChunk; i++) {
         int n = this.randomGenerator.nextInt(16) + 8;
         int n2 = this.randomGenerator.nextInt(16) + 8;
         int n3 = this.currentWorld.getHeight(this.startPos.add(n, 0, n2)).getY() * 2;
         if (n3 > 0) {
            int nextInt = this.randomGenerator.nextInt(n3);
            this.reedGen.generate(this.currentWorld, this.randomGenerator, this.startPos.add(n, nextInt, n2));
         }
      }

      for (int i = 0; i < 10; i++) {
         int n = this.randomGenerator.nextInt(16) + 8;
         int n2 = this.randomGenerator.nextInt(16) + 8;
         int n3 = this.currentWorld.getHeight(this.startPos.add(n, 0, n2)).getY() * 2;
         if (n3 > 0) {
            int nextInt = this.randomGenerator.nextInt(n3);
            this.reedGen.generate(this.currentWorld, this.randomGenerator, this.startPos.add(n, nextInt, n2));
         }
      }

      if (this.randomGenerator.nextInt(32) == 0) {
         int n = this.randomGenerator.nextInt(16) + 8;
         int n2 = this.randomGenerator.nextInt(16) + 8;
         int n3 = this.currentWorld.getHeight(this.startPos.add(n, 0, n2)).getY() * 2;
         if (n3 > 0) {
            int nextInt = this.randomGenerator.nextInt(n3);
            new WorldGenPumpkin().generate(this.currentWorld, this.randomGenerator, this.startPos.add(n, nextInt, n2));
         }
      }

      for (int i = 0; i < this.cactiPerChunk; i++) {
         int n = this.randomGenerator.nextInt(16) + 8;
         int n2 = this.randomGenerator.nextInt(16) + 8;
         int n3 = this.currentWorld.getHeight(this.startPos.add(n, 0, n2)).getY() * 2;
         if (n3 > 0) {
            int nextInt = this.randomGenerator.nextInt(n3);
            this.cactusGen.generate(this.currentWorld, this.randomGenerator, this.startPos.add(n, nextInt, n2));
         }
      }

      if (this.generateLakes) {
         for (int i = 0; i < 50; i++) {
            int n = this.randomGenerator.nextInt(16) + 8;
            int n2 = this.randomGenerator.nextInt(16) + 8;
            int n3 = this.randomGenerator.nextInt(248) + 8;
            if (n3 > 0) {
               int nextInt = this.randomGenerator.nextInt(n3);
               BlockPos blockPos = this.startPos.add(n, nextInt, n2);
               new WorldGenLiquids(Blocks.flowing_water).generate(this.currentWorld, this.randomGenerator, blockPos);
            }
         }

         for (int i = 0; i < 20; i++) {
            int n = this.randomGenerator.nextInt(16) + 8;
            int n2 = this.randomGenerator.nextInt(16) + 8;
            int nextInt = this.randomGenerator.nextInt(this.randomGenerator.nextInt(this.randomGenerator.nextInt(240) + 8) + 8);
            BlockPos blockPos = this.startPos.add(n, nextInt, n2);
            new WorldGenLiquids(Blocks.flowing_lava).generate(this.currentWorld, this.randomGenerator, blockPos);
         }
      }
   }

   public void genStandardOre1(int blockCount, WorldGenerator generator, int minHeight, int maxHeight) {
      if (maxHeight < minHeight) {
         int minHeight2 = minHeight;
         minHeight = maxHeight;
         maxHeight = minHeight2;
      } else if (maxHeight == minHeight) {
         if (minHeight < 255) {
            maxHeight++;
         } else {
            minHeight--;
         }
      }

      for (int i = 0; i < blockCount; i++) {
         BlockPos blockPos = this.startPos
            .add(this.randomGenerator.nextInt(16), this.randomGenerator.nextInt(maxHeight - minHeight) + minHeight, this.randomGenerator.nextInt(16));
         generator.generate(this.currentWorld, this.randomGenerator, blockPos);
      }
   }

   public void genStandardOre2(int blockCount, WorldGenerator generator, int centerHeight, int spread) {
      for (int i = 0; i < blockCount; i++) {
         BlockPos blockPos = this.startPos
            .add(
               this.randomGenerator.nextInt(16),
               this.randomGenerator.nextInt(spread) + this.randomGenerator.nextInt(spread) + centerHeight - spread,
               this.randomGenerator.nextInt(16)
            );
         generator.generate(this.currentWorld, this.randomGenerator, blockPos);
      }
   }

   public void generateOres() {
      this.genStandardOre1(
         this.chunkProviderSettings.dirtCount, this.dirtGen, this.chunkProviderSettings.dirtMinHeight, this.chunkProviderSettings.dirtMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.gravelCount, this.gravelGen, this.chunkProviderSettings.gravelMinHeight, this.chunkProviderSettings.gravelMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.dioriteCount, this.dioriteGen, this.chunkProviderSettings.dioriteMinHeight, this.chunkProviderSettings.dioriteMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.graniteCount, this.graniteGen, this.chunkProviderSettings.graniteMinHeight, this.chunkProviderSettings.graniteMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.andesiteCount, this.andesiteGen, this.chunkProviderSettings.andesiteMinHeight, this.chunkProviderSettings.andesiteMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.coalCount, this.coalGen, this.chunkProviderSettings.coalMinHeight, this.chunkProviderSettings.coalMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.ironCount, this.ironGen, this.chunkProviderSettings.ironMinHeight, this.chunkProviderSettings.ironMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.goldCount, this.goldGen, this.chunkProviderSettings.goldMinHeight, this.chunkProviderSettings.goldMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.redstoneCount, this.redstoneGen, this.chunkProviderSettings.redstoneMinHeight, this.chunkProviderSettings.redstoneMaxHeight
      );
      this.genStandardOre1(
         this.chunkProviderSettings.diamondCount, this.diamondGen, this.chunkProviderSettings.diamondMinHeight, this.chunkProviderSettings.diamondMaxHeight
      );
      this.genStandardOre2(
         this.chunkProviderSettings.lapisCount, this.lapisGen, this.chunkProviderSettings.lapisCenterHeight, this.chunkProviderSettings.lapisSpread
      );
   }
}
