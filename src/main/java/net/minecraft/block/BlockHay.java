package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.World;

public class BlockHay extends BlockRotatedPillar {
   public BlockHay() {
      super(Material.grass, MapColor.yellowColor);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AXIS, Axis.Y));
      this.setCreativeTab(CreativeTabs.tabBlock);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      Axis axis = Axis.Y;
      int n = meta & 12;
      if (n == 4) {
         axis = Axis.X;
      } else if (n == 8) {
         axis = Axis.Z;
      }

      return this.getDefaultState().withProperty(AXIS, axis);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int n = 0;
      Axis value = state.getValue(AXIS);
      if (value == Axis.X) {
         n |= 4;
      } else if (value == Axis.Z) {
         n |= 8;
      }

      return n;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, AXIS);
   }

   @Override
   public ItemStack createStackedBlock(IBlockState state) {
      return new ItemStack(Item.getItemFromBlock(this), 1, 0);
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return super.onBlockPlaced(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(AXIS, facing.getAxis());
   }
}
