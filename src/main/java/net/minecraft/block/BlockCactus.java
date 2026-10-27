package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class BlockCactus extends Block {
   public static PropertyInteger AGE = PropertyInteger.create("age", 0, 15);

   public BlockCactus() {
      super(Material.cactus);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0));
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      BlockPos up = pos.up();
      if (worldIn.isAirBlock(up)) {
         int n = 1;

         while (worldIn.getBlockState(pos.down(n)).getBlock() == this) {
            n++;
         }

         if (n < 3) {
            int value = state.getValue(AGE);
            if (value == 15) {
               worldIn.setBlockState(up, this.getDefaultState());
               IBlockState withProperty = state.withProperty(AGE, 0);
               worldIn.setBlockState(pos, withProperty, 4);
               this.onNeighborBlockChange(worldIn, up, withProperty, this);
            } else {
               worldIn.setBlockState(pos, state.withProperty(AGE, value + 1), 4);
            }
         }
      }
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      float f = 0.0625F;
      return new AxisAlignedBB(pos.getX() + f, pos.getY(), pos.getZ() + f, pos.getX() + 1 - f, pos.getY() + 1 - f, pos.getZ() + 1 - f);
   }

   @Override
   public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
      float f = 0.0625F;
      return new AxisAlignedBB(pos.getX() + f, pos.getY(), pos.getZ() + f, pos.getX() + 1 - f, pos.getY() + 1, pos.getZ() + 1 - f);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return super.canPlaceBlockAt(worldIn, pos) ? this.canBlockStay(worldIn, pos) : false;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!this.canBlockStay(worldIn, pos)) {
         worldIn.destroyBlock(pos, true);
      }
   }

   public boolean canBlockStay(World worldIn, BlockPos pos) {
      for (EnumFacing enumFacing : Plane.HORIZONTAL) {
         if (worldIn.getBlockState(pos.offset(enumFacing)).getBlock().getMaterial().isSolid()) {
            return false;
         }
      }

      Block block = worldIn.getBlockState(pos.down()).getBlock();
      return block == Blocks.cactus || block == Blocks.sand;
   }

   @Override
   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      entityIn.attackEntityFrom(DamageSource.cactus, 1.0F);
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(AGE, meta);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(AGE);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, AGE);
   }
}
