package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public abstract class WorldGenerator {
   public boolean doBlockNotify;

   public WorldGenerator() {
      this(false);
   }

   public WorldGenerator(boolean notify) {
      this.doBlockNotify = notify;
   }

   public abstract boolean generate(World world, Random random, BlockPos blockPos);

   public void setLeafRadius() {
   }

   public void setBlockAndNotifyAdequately(World worldIn, BlockPos pos, IBlockState state) {
      if (this.doBlockNotify) {
         worldIn.setBlockState(pos, state, 3);
      } else {
         worldIn.setBlockState(pos, state, 2);
      }
   }
}
