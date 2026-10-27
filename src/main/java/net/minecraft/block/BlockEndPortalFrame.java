package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.world.World;

public class BlockEndPortalFrame extends Block {
   public static PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);
   public static PropertyBool EYE = PropertyBool.create("eye");

   public BlockEndPortalFrame() {
      super(Material.rock, MapColor.greenColor);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(EYE, false));
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public void setBlockBoundsForItemRender() {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
   }

   @Override
   public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.8125F, 1.0F);
      super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
      if (worldIn.getBlockState(pos).getValue(EYE)) {
         this.setBlockBounds(0.3125F, 0.8125F, 0.3125F, 0.6875F, 1.0F, 0.6875F);
         super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
      }

      this.setBlockBoundsForItemRender();
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return null;
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite()).withProperty(EYE, false);
   }

   @Override
   public boolean hasComparatorInputOverride() {
      return true;
   }

   @Override
   public int getComparatorInputOverride(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos).getValue(EYE) ? 15 : 0;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(EYE, (meta & 4) != 0).withProperty(FACING, EnumFacing.getHorizontal(meta & 3));
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int n = 0;
      n |= state.getValue(FACING).getHorizontalIndex();
      if (state.getValue(EYE)) {
         n |= 4;
      }

      return n;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, EYE);
   }
}
