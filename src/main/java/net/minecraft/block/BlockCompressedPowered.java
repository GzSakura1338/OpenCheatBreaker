package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;

public class BlockCompressedPowered extends Block {
   public BlockCompressedPowered(Material material, MapColor mapColor) {
      super(material, mapColor);
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   @Override
   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return 15;
   }
}
