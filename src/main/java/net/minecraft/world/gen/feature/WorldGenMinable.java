package net.minecraft.world.gen.feature;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class WorldGenMinable extends WorldGenerator {
   public IBlockState oreBlock;
   public int numberOfBlocks;
   public Predicate<IBlockState> predicate;

   public WorldGenMinable(IBlockState state, int blockCount) {
      this(state, blockCount, BlockHelper.forBlock(Blocks.stone));
   }

   public WorldGenMinable(IBlockState state, int blockCount, Predicate<IBlockState> predicate) {
      this.oreBlock = state;
      this.numberOfBlocks = blockCount;
      this.predicate = predicate;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      float f = rand.nextFloat() * (float) Math.PI;
      double d = position.getX() + 8 + MathHelper.sin(f) * this.numberOfBlocks / 8.0F;
      double d2 = position.getX() + 8 - MathHelper.sin(f) * this.numberOfBlocks / 8.0F;
      double d3 = position.getZ() + 8 + MathHelper.cos(f) * this.numberOfBlocks / 8.0F;
      double d4 = position.getZ() + 8 - MathHelper.cos(f) * this.numberOfBlocks / 8.0F;
      double d5 = position.getY() + rand.nextInt(3) - 2;
      double d6 = position.getY() + rand.nextInt(3) - 2;

      for (int i = 0; i < this.numberOfBlocks; i++) {
         float f2 = (float)i / this.numberOfBlocks;
         double d7 = d + (d2 - d) * f2;
         double d8 = d5 + (d6 - d5) * f2;
         double d9 = d3 + (d4 - d3) * f2;
         double d10 = rand.nextDouble() * this.numberOfBlocks / 16.0;
         double d11 = (MathHelper.sin((float) Math.PI * f2) + 1.0F) * d10 + 1.0;
         double d12 = (MathHelper.sin((float) Math.PI * f2) + 1.0F) * d10 + 1.0;
         int n = MathHelper.floor_double(d7 - d11 / 2.0);
         int n2 = MathHelper.floor_double(d8 - d12 / 2.0);
         int n3 = MathHelper.floor_double(d9 - d11 / 2.0);
         int n4 = MathHelper.floor_double(d7 + d11 / 2.0);
         int n5 = MathHelper.floor_double(d8 + d12 / 2.0);
         int n6 = MathHelper.floor_double(d9 + d11 / 2.0);

         for (int j = n; j <= n4; j++) {
            double d13 = (j + 0.5 - d7) / (d11 / 2.0);
            if (d13 * d13 < 1.0) {
               for (int k = n2; k <= n5; k++) {
                  double d14 = (k + 0.5 - d8) / (d12 / 2.0);
                  if (d13 * d13 + d14 * d14 < 1.0) {
                     for (int m = n3; m <= n6; m++) {
                        double d15 = (m + 0.5 - d9) / (d11 / 2.0);
                        if (d13 * d13 + d14 * d14 + d15 * d15 < 1.0) {
                           BlockPos blockPos = new BlockPos(j, k, m);
                           if (this.predicate.apply(worldIn.getBlockState(blockPos))) {
                              worldIn.setBlockState(blockPos, this.oreBlock, 2);
                           }
                        }
                     }
                  }
               }
            }
         }
      }

      return true;
   }
}
