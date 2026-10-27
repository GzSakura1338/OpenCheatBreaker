package net.minecraft.world.gen.feature;

import com.google.common.base.Predicates;
import java.util.Random;
import net.minecraft.block.BlockSand;

import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSlab.EnumBlockHalf;
import net.minecraft.block.BlockStoneSlab;

import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.pattern.BlockStateHelper;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.world.World;

public class WorldGenDesertWells extends WorldGenerator {
   public static BlockStateHelper sandType = BlockStateHelper.forBlock(Blocks.sand).where(BlockSand.VARIANT, Predicates.equalTo(BlockSand.EnumType.SAND));
   public IBlockState sandstoneSlab = Blocks.stone_slab
      .getDefaultState()
      .withProperty(BlockStoneSlab.VARIANT, BlockStoneSlab.EnumType.SAND)
      .withProperty(BlockSlab.HALF, EnumBlockHalf.BOTTOM);
   public IBlockState sandstone = Blocks.sandstone.getDefaultState();
   public IBlockState flowingWater = Blocks.flowing_water.getDefaultState();

   @Override
   public boolean generate(World worldIn, Random rand, BlockPos position) {
      while (worldIn.isAirBlock(position) && position.getY() > 2) {
         position = position.down();
      }

      if (!sandType.apply(worldIn.getBlockState(position))) {
         return false;
      }

      for (int i = -2; i <= 2; i++) {
         for (int j = -2; j <= 2; j++) {
            if (worldIn.isAirBlock(position.add(i, -1, j)) && worldIn.isAirBlock(position.add(i, -2, j))) {
               return false;
            }
         }
      }

      for (int i = -1; i <= 0; i++) {
         for (int j = -2; j <= 2; j++) {
            for (int k = -2; k <= 2; k++) {
               worldIn.setBlockState(position.add(j, i, k), this.sandstone, 2);
            }
         }
      }

      worldIn.setBlockState(position, this.flowingWater, 2);

      for (EnumFacing enumFacing : Plane.HORIZONTAL) {
         worldIn.setBlockState(position.offset(enumFacing), this.flowingWater, 2);
      }

      for (int i = -2; i <= 2; i++) {
         for (int j = -2; j <= 2; j++) {
            if (i == -2 || i == 2 || j == -2 || j == 2) {
               worldIn.setBlockState(position.add(i, 1, j), this.sandstone, 2);
            }
         }
      }

      worldIn.setBlockState(position.add(2, 1, 0), this.sandstoneSlab, 2);
      worldIn.setBlockState(position.add(-2, 1, 0), this.sandstoneSlab, 2);
      worldIn.setBlockState(position.add(0, 1, 2), this.sandstoneSlab, 2);
      worldIn.setBlockState(position.add(0, 1, -2), this.sandstoneSlab, 2);

      for (int i = -1; i <= 1; i++) {
         for (int j = -1; j <= 1; j++) {
            if (i == 0 && j == 0) {
               worldIn.setBlockState(position.add(i, 4, j), this.sandstone, 2);
            } else {
               worldIn.setBlockState(position.add(i, 4, j), this.sandstoneSlab, 2);
            }
         }
      }

      for (int i = 1; i <= 3; i++) {
         worldIn.setBlockState(position.add(-1, i, -1), this.sandstone, 2);
         worldIn.setBlockState(position.add(-1, i, 1), this.sandstone, 2);
         worldIn.setBlockState(position.add(1, i, -1), this.sandstone, 2);
         worldIn.setBlockState(position.add(1, i, 1), this.sandstone, 2);
      }

      return true;
   }
}
