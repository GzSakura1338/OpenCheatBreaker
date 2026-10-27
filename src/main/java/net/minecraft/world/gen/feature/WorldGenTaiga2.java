package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class WorldGenTaiga2 extends WorldGenAbstractTree {
   public static IBlockState LOG = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, EnumType.SPRUCE);
   public static IBlockState LEAVES = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, EnumType.SPRUCE)
      .withProperty(BlockLeaves.CHECK_DECAY, false);

   public WorldGenTaiga2(boolean flag) {
      super(flag);
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int n = rand.nextInt(4) + 6;
      int n2 = 1 + rand.nextInt(2);
      int n3 = n - n2;
      int n4 = 2 + rand.nextInt(2);
      boolean flag = true;
      if (position.getY() >= 1 && position.getY() + n + 1 <= 256) {
         for (int i = position.getY(); i <= position.getY() + 1 + n && flag; i++) {
            int n5 = 1;
            if (i - position.getY() < n2) {
               n5 = 0;
            } else {
               n5 = n4;
            }

            MutableBlockPos mutableBlockPos = new MutableBlockPos();

            for (int j = position.getX() - n5; j <= position.getX() + n5 && flag; j++) {
               for (int k = position.getZ() - n5; k <= position.getZ() + n5 && flag; k++) {
                  if (i >= 0 && i < 256) {
                     Block block = worldIn.getBlockState(mutableBlockPos.set(j, i, k)).getBlock();
                     if (block.getMaterial() != Material.air && block.getMaterial() != Material.leaves) {
                        flag = false;
                     }
                  } else {
                     flag = false;
                  }
               }
            }
         }

         if (!flag) {
            return false;
         }

         Block block = worldIn.getBlockState(position.down()).getBlock();
         if ((block == Blocks.grass || block == Blocks.dirt || block == Blocks.farmland) && position.getY() < 256 - n - 1) {
            this.setDirt(worldIn, position.down());
            int nextInt = rand.nextInt(2);
            int n5 = 1;
            byte b = 0;

            for (int i = 0; i <= n3; i++) {
               int n6 = position.getY() + n - i;

               for (int j = position.getX() - nextInt; j <= position.getX() + nextInt; j++) {
                  int n7 = j - position.getX();

                  for (int k = position.getZ() - nextInt; k <= position.getZ() + nextInt; k++) {
                     int n8 = k - position.getZ();
                     if (Math.abs(n7) != nextInt || Math.abs(n8) != nextInt || nextInt <= 0) {
                        BlockPos blockPos = new BlockPos(j, n6, k);
                        if (!worldIn.getBlockState(blockPos).getBlock().isFullBlock()) {
                           this.setBlockAndNotifyAdequately(worldIn, blockPos, LEAVES);
                        }
                     }
                  }
               }

               if (nextInt >= n5) {
                  nextInt = b;
                  b = 1;
                  if (++n5 > n4) {
                     n5 = n4;
                  }
               } else {
                  nextInt++;
               }
            }

            int nextInt2 = rand.nextInt(3);

            for (int i = 0; i < n - nextInt2; i++) {
               Block block2 = worldIn.getBlockState(position.up(i)).getBlock();
               if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves) {
                  this.setBlockAndNotifyAdequately(worldIn, position.up(i), LOG);
               }
            }

            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }
}
