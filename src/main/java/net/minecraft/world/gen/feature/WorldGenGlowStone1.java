package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class WorldGenGlowStone1 extends WorldGenerator {
   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      if (!worldIn.isAirBlock(position)) {
         return false;
      }

      if (worldIn.getBlockState(position.up()).getBlock() != Blocks.netherrack) {
         return false;
      }

      worldIn.setBlockState(position, Blocks.glowstone.getDefaultState(), 2);

      for (int i = 0; i < 1500; i++) {
         BlockPos blockPos = position.add(rand.nextInt(8) - rand.nextInt(8), -rand.nextInt(12), rand.nextInt(8) - rand.nextInt(8));
         if (worldIn.getBlockState(blockPos).getBlock().getMaterial() == Material.air) {
            int n = 0;

            for (EnumFacing enumFacing : EnumFacing.values()) {
               if (worldIn.getBlockState(blockPos.offset(enumFacing)).getBlock() == Blocks.glowstone) {
                  n++;
               }

               if (n > 1) {
                  break;
               }
            }

            if (n == 1) {
               worldIn.setBlockState(blockPos, Blocks.glowstone.getDefaultState(), 2);
            }
         }
      }

      return true;
   }
}
