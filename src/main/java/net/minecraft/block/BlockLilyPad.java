package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLilyPad extends BlockBush {
   public BlockLilyPad() {
      float f = 0.5F;
      float f2 = 0.015625F;
      this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f2, 0.5F + f);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
      if (collidingEntity == null || !(collidingEntity instanceof EntityBoat)) {
         super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
      }
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return new AxisAlignedBB(
         pos.getX() + this.minX, pos.getY() + this.minY, pos.getZ() + this.minZ, pos.getX() + this.maxX, pos.getY() + this.maxY, pos.getZ() + this.maxZ
      );
   }

   @Override
   public int getBlockColor() {
      return 7455580;
   }

   @Override
   public int getRenderColor(IBlockState state) {
      return 7455580;
   }

   @Override
   public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
      return 2129968;
   }

   @Override
   public boolean canPlaceBlockOn(Block ground) {
      return ground == Blocks.water;
   }

   @Override
   public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
      if (pos.getY() >= 0 && pos.getY() < 256) {
         IBlockState blockState = worldIn.getBlockState(pos.down());
         return blockState.getBlock().getMaterial() == Material.water && blockState.getValue(BlockLiquid.LEVEL) == 0;
      } else {
         return false;
      }
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return 0;
   }
}
