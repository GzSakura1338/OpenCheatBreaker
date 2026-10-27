package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenBlockBlob extends WorldGenerator {
   public Block block;
   public int radius;

   public WorldGenBlockBlob(Block block, int n) {
      super(false);
      this.block = block;
      this.radius = n;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      while (true) {
         label50: {
            if (position.getY() > 3) {
               if (worldIn.isAirBlock(position.down())) {
                  break label50;
               }

               Block block = worldIn.getBlockState(position.down()).getBlock();
               if (block != Blocks.grass && block != Blocks.dirt && block != Blocks.stone) {
                  break label50;
               }
            }

            if (position.getY() <= 3) {
               return false;
            }

            int radius = this.radius;

            for (int i = 0; radius >= 0 && i < 3; i++) {
               int n = radius + rand.nextInt(2);
               int n2 = radius + rand.nextInt(2);
               int n3 = radius + rand.nextInt(2);
               float f = (n + n2 + n3) * 0.333F + 0.5F;

               for (BlockPos blockPos : BlockPos.getAllInBox(position.add(-n, -n2, -n3), position.add(n, n2, n3))) {
                  if (blockPos.distanceSq(position) <= f * f) {
                     worldIn.setBlockState(blockPos, this.block.getDefaultState(), 4);
                  }
               }

               position = position.add(-(radius + 1) + rand.nextInt(2 + radius * 2), 0 - rand.nextInt(2), -(radius + 1) + rand.nextInt(2 + radius * 2));
            }

            return true;
         }

         position = position.down();
      }
   }
}
