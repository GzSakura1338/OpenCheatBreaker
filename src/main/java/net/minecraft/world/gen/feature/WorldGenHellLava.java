package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class WorldGenHellLava extends WorldGenerator {
   public Block block;
   public boolean denySurfaceSpawn;

   public WorldGenHellLava(Block block, boolean flag) {
      this.block = block;
      this.denySurfaceSpawn = flag;
   }

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      if (worldIn.getBlockState(position.up()).getBlock() != Blocks.netherrack) {
         return false;
      }

      if (worldIn.getBlockState(position).getBlock().getMaterial() != Material.air && worldIn.getBlockState(position).getBlock() != Blocks.netherrack) {
         return false;
      }

      int n = 0;
      if (worldIn.getBlockState(position.west()).getBlock() == Blocks.netherrack) {
         n++;
      }

      if (worldIn.getBlockState(position.east()).getBlock() == Blocks.netherrack) {
         n++;
      }

      if (worldIn.getBlockState(position.north()).getBlock() == Blocks.netherrack) {
         n++;
      }

      if (worldIn.getBlockState(position.south()).getBlock() == Blocks.netherrack) {
         n++;
      }

      if (worldIn.getBlockState(position.down()).getBlock() == Blocks.netherrack) {
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

      if (worldIn.isAirBlock(position.down())) {
         n2++;
      }

      if (!this.denySurfaceSpawn && n == 4 && n2 == 1 || n == 5) {
         worldIn.setBlockState(position, this.block.getDefaultState(), 2);
         worldIn.forceBlockUpdateTick(this.block, position, rand);
      }

      return true;
   }
}
