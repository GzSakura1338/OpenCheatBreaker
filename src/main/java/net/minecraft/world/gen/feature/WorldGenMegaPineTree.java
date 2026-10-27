package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMegaPineTree extends WorldGenHugeTrees {
   public static IBlockState SPRUCE_LOGS = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, EnumType.SPRUCE);
   public static IBlockState SPRUCE_LEAVES = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, EnumType.SPRUCE)
      .withProperty(BlockLeaves.CHECK_DECAY, false);
   public static IBlockState PODZOL = Blocks.dirt.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.PODZOL);
   public boolean useBaseHeight;

   public WorldGenMegaPineTree(boolean flag, boolean flag2) {
      super(flag, 13, 15, SPRUCE_LOGS, SPRUCE_LEAVES);
      this.useBaseHeight = flag2;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int calculateMaxHeight = this.calculateMaxHeight(rand);
      if (!this.canGenerate(worldIn, rand, position, calculateMaxHeight)) {
         return false;
      }

      this.generateTopLeaves(worldIn, position.getX(), position.getZ(), position.getY() + calculateMaxHeight, 0, rand);

      for (int i = 0; i < calculateMaxHeight; i++) {
         Block block = worldIn.getBlockState(position.up(i)).getBlock();
         if (block.getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
            this.setBlockAndNotifyAdequately(worldIn, position.up(i), this.woodMetadata);
         }

         if (i < calculateMaxHeight - 1) {
            block = worldIn.getBlockState(position.add(1, i, 0)).getBlock();
            if (block.getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
               this.setBlockAndNotifyAdequately(worldIn, position.add(1, i, 0), this.woodMetadata);
            }

            block = worldIn.getBlockState(position.add(1, i, 1)).getBlock();
            if (block.getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
               this.setBlockAndNotifyAdequately(worldIn, position.add(1, i, 1), this.woodMetadata);
            }

            block = worldIn.getBlockState(position.add(0, i, 1)).getBlock();
            if (block.getMaterial() == Material.air || block.getMaterial() == Material.leaves) {
               this.setBlockAndNotifyAdequately(worldIn, position.add(0, i, 1), this.woodMetadata);
            }
         }
      }

      return true;
   }

   public void generateTopLeaves(World worldIn, int n, int n2, int n3, int n4, Random random) {
      int n5 = random.nextInt(5) + (this.useBaseHeight ? this.baseHeight : 3);
      int n6 = 0;

      for (int i = n3 - n5; i <= n3; i++) {
         int n7 = n3 - i;
         int n8 = n4 + MathHelper.floor_float((float)n7 / n5 * 3.5F);
         this.generateLeavesLimited(worldIn, new BlockPos(n, i, n2), n8 + (n7 > 0 && n8 == n6 && (i & 1) == 0 ? 1 : 0));
         n6 = n8;
      }
   }

   @Override
   public void handleWorld(World worldIn, Random random, BlockPos blockPos) {
      this.setPodzol(worldIn, blockPos.west().north());
      this.setPodzol(worldIn, blockPos.east(2).north());
      this.setPodzol(worldIn, blockPos.west().south(2));
      this.setPodzol(worldIn, blockPos.east(2).south(2));

      for (int i = 0; i < 5; i++) {
         int nextInt = random.nextInt(64);
         int n = nextInt % 8;
         int n2 = nextInt / 8;
         if (n == 0 || n == 7 || n2 == 0 || n2 == 7) {
            this.setPodzol(worldIn, blockPos.add(-3 + n, 0, -3 + n2));
         }
      }
   }

   public void setPodzol(World worldIn, BlockPos blockPos) {
      for (int i = -2; i <= 2; i++) {
         for (int j = -2; j <= 2; j++) {
            if (Math.abs(i) != 2 || Math.abs(j) != 2) {
               this.changeDirtToPodzol(worldIn, blockPos.add(i, 0, j));
            }
         }
      }
   }

   public void changeDirtToPodzol(World worldIn, BlockPos blockPos) {
      for (int i = 2; i >= -3; i--) {
         BlockPos up = blockPos.up(i);
         Block block = worldIn.getBlockState(up).getBlock();
         if (block == Blocks.grass || block == Blocks.dirt) {
            this.setBlockAndNotifyAdequately(worldIn, up, PODZOL);
            break;
         }

         if (block.getMaterial() != Material.air && i < 0) {
            break;
         }
      }
   }
}
