package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenLiquids extends WorldGenerator {
   public Block block;

   public WorldGenLiquids(Block block) {
      this.block = block;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      if (worldIn.getBlockState(position.up()).getBlock() != Blocks.stone) {
         return false;
      }

      if (worldIn.getBlockState(position.down()).getBlock() != Blocks.stone) {
         return false;
      }

      if (worldIn.getBlockState(position).getBlock().getMaterial() != Material.air && worldIn.getBlockState(position).getBlock() != Blocks.stone) {
         return false;
      }

      int n = 0;
      if (worldIn.getBlockState(position.west()).getBlock() == Blocks.stone) {
         n++;
      }

      if (worldIn.getBlockState(position.east()).getBlock() == Blocks.stone) {
         n++;
      }

      if (worldIn.getBlockState(position.north()).getBlock() == Blocks.stone) {
         n++;
      }

      if (worldIn.getBlockState(position.south()).getBlock() == Blocks.stone) {
         n++;
      }

      int n2 = 0;
      if (worldIn.isAirBlock(position.west())) {
         n2++;
      }

      if (worldIn.isAirBlock(position.east())) {
         n2++;
      }

      if (worldIn.isAirBlock(position.north())) {
         n2++;
      }

      if (worldIn.isAirBlock(position.south())) {
         n2++;
      }

      if (n == 3 && n2 == 1) {
         worldIn.setBlockState(position, this.block.getDefaultState(), 2);
         worldIn.forceBlockUpdateTick(this.block, position, rand);
      }

      return true;
   }
}
