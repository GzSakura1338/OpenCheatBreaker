package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenShrub extends WorldGenTrees {
   public IBlockState woodMetadata;
   public IBlockState leavesMetadata;

   public WorldGenShrub(IBlockState blockState, IBlockState blockState2) {
      super(false);
      this.woodMetadata = blockState;
      this.leavesMetadata = blockState2;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      Block block;
      while (
         ((block = worldIn.getBlockState(position).getBlock()).getMaterial() == Material.air || block.getMaterial() == Material.leaves) && position.getY() > 0
      ) {
         position = position.down();
      }

      Block block2 = worldIn.getBlockState(position).getBlock();
      if (block2 == Blocks.dirt || block2 == Blocks.grass) {
         position = position.up();
         this.setBlockAndNotifyAdequately(worldIn, position, this.woodMetadata);

         for (int i = position.getY(); i <= position.getY() + 2; i++) {
            int n = i - position.getY();
            int n2 = 2 - n;

            for (int j = position.getX() - n2; j <= position.getX() + n2; j++) {
               int n3 = j - position.getX();

               for (int k = position.getZ() - n2; k <= position.getZ() + n2; k++) {
                  int n4 = k - position.getZ();
                  if (Math.abs(n3) != n2 || Math.abs(n4) != n2 || rand.nextInt(2) != 0) {
                     BlockPos blockPos = new BlockPos(j, i, k);
                     if (!worldIn.getBlockState(blockPos).getBlock().isFullBlock()) {
                        this.setBlockAndNotifyAdequately(worldIn, blockPos, this.leavesMetadata);
                     }
                  }
               }
            }
         }
      }

      return true;
   }
}
