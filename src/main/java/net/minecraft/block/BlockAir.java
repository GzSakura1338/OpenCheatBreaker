package net.minecraft.block;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockAir extends Block {
   public static Map mapOriginalOpacity = new IdentityHashMap();

   public BlockAir() {
      super(Material.air);
   }

   @Override
   public int getRenderType() {
      return -1;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean canCollideCheck(IBlockState state, boolean hitIfLiquid) {
      return false;
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
   }

   @Override
   public boolean isReplaceable(World worldIn, BlockPos pos) {
      return true;
   }

   public static void setLightOpacity(Block block, int n) {
      if (!mapOriginalOpacity.containsKey(block)) {
         mapOriginalOpacity.put(block, block.lightOpacity);
      }

      block.lightOpacity = n;
   }

   public static void restoreLightOpacity(Block block) {
      if (mapOriginalOpacity.containsKey(block)) {
         int n = (Integer)mapOriginalOpacity.get(block);
         setLightOpacity(block, n);
      }
   }
}
