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

public class WorldGenTaiga1 extends WorldGenAbstractTree {
   public static IBlockState LOG = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, EnumType.SPRUCE);
   public static IBlockState LEAVES = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, EnumType.SPRUCE)
      .withProperty(BlockLeaves.CHECK_DECAY, false);

   public WorldGenTaiga1() {
      super(false);
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int n = rand.nextInt(5) + 7;
      int n2 = n - rand.nextInt(2) - 3;
      int n3 = n - n2;
      int n4 = 1 + rand.nextInt(n3 + 1);
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
                  if (i < 0 || i >= 256) {
                     flag = false;
                  } else if (!this.isBlockReplaceable(worldIn.getBlockState(mutableBlockPos.set(j, i, k)).getBlock())) {
                     flag = false;
                  }
               }
            }
         }

         if (!flag) {
            return false;
         }

         Block block = worldIn.getBlockState(position.down()).getBlock();
         if ((block == Blocks.grass || block == Blocks.dirt) && position.getY() < 256 - n - 1) {
            this.setDirt(worldIn, position.down());
            int n5 = 0;

            for (int i = position.getY() + n; i >= position.getY() + n2; i--) {
               for (int j = position.getX() - n5; j <= position.getX() + n5; j++) {
                  int n6 = j - position.getX();

                  for (int k = position.getZ() - n5; k <= position.getZ() + n5; k++) {
                     int n7 = k - position.getZ();
                     if (Math.abs(n6) != n5 || Math.abs(n7) != n5 || n5 <= 0) {
                        BlockPos blockPos = new BlockPos(j, i, k);
                        if (!worldIn.getBlockState(blockPos).getBlock().isFullBlock()) {
                           this.setBlockAndNotifyAdequately(worldIn, blockPos, LEAVES);
                        }
                     }
                  }
               }

               if (n5 >= 1 && i == position.getY() + n2 + 1) {
                  n5--;
               } else if (n5 < n4) {
                  n5++;
               }
            }

            for (int i = 0; i < n - 1; i++) {
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
