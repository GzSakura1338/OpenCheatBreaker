package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenReed extends WorldGenerator {
   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      for (int i = 0; i < 20; i++) {
         BlockPos blockPos = position.add(rand.nextInt(4) - rand.nextInt(4), 0, rand.nextInt(4) - rand.nextInt(4));
         if (worldIn.isAirBlock(blockPos)) {
            BlockPos down = blockPos.down();
            if (worldIn.getBlockState(down.west()).getBlock().getMaterial() == Material.water
               || worldIn.getBlockState(down.east()).getBlock().getMaterial() == Material.water
               || worldIn.getBlockState(down.north()).getBlock().getMaterial() == Material.water
               || worldIn.getBlockState(down.south()).getBlock().getMaterial() == Material.water) {
               int n = 2 + rand.nextInt(rand.nextInt(3) + 1);

               for (int j = 0; j < n; j++) {
                  if (Blocks.reeds.canBlockStay(worldIn, blockPos)) {
                     worldIn.setBlockState(blockPos.up(j), Blocks.reeds.getDefaultState(), 2);
                  }
               }
            }
         }
      }

      return true;
   }
}
