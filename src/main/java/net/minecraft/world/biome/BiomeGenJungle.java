package net.minecraft.world.biome;

import java.util.Random;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenAbstractTree;
import net.minecraft.world.gen.feature.WorldGenMegaJungle;
import net.minecraft.world.gen.feature.WorldGenMelon;
import net.minecraft.world.gen.feature.WorldGenShrub;
import net.minecraft.world.gen.feature.WorldGenTallGrass;
import net.minecraft.world.gen.feature.WorldGenTrees;
import net.minecraft.world.gen.feature.WorldGenVines;
import net.minecraft.world.gen.feature.WorldGenerator;

public class BiomeGenJungle extends BiomeGenBase {
   public boolean flag;
   public static IBlockState JUNGLE_LOGS = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, BlockPlanks.EnumType.JUNGLE);
   public static IBlockState JUNGLE_LEAVES = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.JUNGLE)
      .withProperty(BlockLeaves.CHECK_DECAY, false);
   public static IBlockState OAK_LEAVES = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, BlockPlanks.EnumType.OAK)
      .withProperty(BlockLeaves.CHECK_DECAY, false);

   public BiomeGenJungle(int id, boolean flag) {
      super(id);
      this.flag = flag;
      if (flag) {
         this.theBiomeDecorator.treesPerChunk = 2;
      } else {
         this.theBiomeDecorator.treesPerChunk = 50;
      }

      this.theBiomeDecorator.grassPerChunk = 25;
      this.theBiomeDecorator.flowersPerChunk = 4;
      if (!flag) {
         this.spawnableMonsterList.add(new BiomeGenBase.SpawnListEntry(EntityOcelot.class, 2, 1, 1));
      }

      this.spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(EntityChicken.class, 10, 4, 4));
   }

   @Override
   public WorldGenAbstractTree genBigTreeChance(Random rand) {
      return rand.nextInt(10) == 0
         ? this.worldGeneratorBigTree
         : (
            rand.nextInt(2) == 0
               ? new WorldGenShrub(JUNGLE_LOGS, OAK_LEAVES)
               : (
                  !this.flag && rand.nextInt(3) == 0
                     ? new WorldGenMegaJungle(false, 10, 20, JUNGLE_LOGS, JUNGLE_LEAVES)
                     : new WorldGenTrees(false, 4 + rand.nextInt(7), JUNGLE_LOGS, JUNGLE_LEAVES, true)
               )
         );
   }

   @Override
   public WorldGenerator getRandomWorldGenForGrass(Random rand) {
      return rand.nextInt(4) == 0 ? new WorldGenTallGrass(BlockTallGrass.EnumType.FERN) : new WorldGenTallGrass(BlockTallGrass.EnumType.GRASS);
   }

   @Override
   public void decorate(World worldIn, Random rand, BlockPos pos) {
      super.decorate(worldIn, rand, pos);
      int n = rand.nextInt(16) + 8;
      int n2 = rand.nextInt(16) + 8;
      int nextInt = rand.nextInt(worldIn.getHeight(pos.add(n, 0, n2)).getY() * 2);
      new WorldGenMelon().generate(worldIn, rand, pos.add(n, nextInt, n2));
      WorldGenVines worldGenVines = new WorldGenVines();

      for (int i = 0; i < 50; i++) {
         nextInt = rand.nextInt(16) + 8;
         short s = 128;
         int n3 = rand.nextInt(16) + 8;
         worldGenVines.generate(worldIn, rand, pos.add(nextInt, 128, n3));
      }
   }
}
