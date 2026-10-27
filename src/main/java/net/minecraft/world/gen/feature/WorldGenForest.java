package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockOldLeaf;
import net.minecraft.block.BlockOldLog;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.world.World;

public class WorldGenForest extends WorldGenAbstractTree {
   public static IBlockState LOG = Blocks.log.getDefaultState().withProperty(BlockOldLog.VARIANT, EnumType.BIRCH);
   public static IBlockState LEAVES = Blocks.leaves
      .getDefaultState()
      .withProperty(BlockOldLeaf.VARIANT, EnumType.BIRCH)
      .withProperty(BlockOldLeaf.CHECK_DECAY, false);
   public boolean useExtraRandomHeight;

   public WorldGenForest(boolean flag, boolean flag2) {
      super(flag);
      this.useExtraRandomHeight = flag2;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      int n = rand.nextInt(3) + 5;
      if (this.useExtraRandomHeight) {
         n += rand.nextInt(7);
      }

      boolean flag = true;
      if (position.getY() >= 1 && position.getY() + n + 1 <= 256) {
         for (int i = position.getY(); i <= position.getY() + 1 + n; i++) {
            byte b = 1;
            if (i == position.getY()) {
               b = 0;
            }

            if (i >= position.getY() + 1 + n - 2) {
               b = 2;
            }

            MutableBlockPos mutableBlockPos = new MutableBlockPos();

            for (int j = position.getX() - b; j <= position.getX() + b && flag; j++) {
               for (int k = position.getZ() - b; k <= position.getZ() + b && flag; k++) {
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
         if ((block == Blocks.grass || block == Blocks.dirt || block == Blocks.farmland) && position.getY() < 256 - n - 1) {
            this.setDirt(worldIn, position.down());

            for (int i = position.getY() - 3 + n; i <= position.getY() + n; i++) {
               int n2 = i - (position.getY() + n);
               int n3 = 1 - n2 / 2;

               for (int j = position.getX() - n3; j <= position.getX() + n3; j++) {
                  int n4 = j - position.getX();

                  for (int k = position.getZ() - n3; k <= position.getZ() + n3; k++) {
                     int n5 = k - position.getZ();
                     if (Math.abs(n4) != n3 || Math.abs(n5) != n3 || rand.nextInt(2) != 0 && n2 != 0) {
                        BlockPos blockPos = new BlockPos(j, i, k);
                        Block block2 = worldIn.getBlockState(blockPos).getBlock();
                        if (block2.getMaterial() == Material.air || block2.getMaterial() == Material.leaves) {
                           this.setBlockAndNotifyAdequately(worldIn, blockPos, LEAVES);
                        }
                     }
                  }
               }
            }

            for (int i = 0; i < n; i++) {
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
