package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReed extends Block {
   public static PropertyInteger AGE = PropertyInteger.create("age", 0, 15);

   public BlockReed() {
      super(Material.plants);
      this.setDefaultState(this.blockState.getBaseState().withProperty(AGE, 0));
      float f = 0.375F;
      this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
      this.setTickRandomly(true);
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if ((worldIn.getBlockState(pos.down()).getBlock() == Blocks.reeds || this.checkForDrop(worldIn, pos, state)) && worldIn.isAirBlock(pos.up())) {
         int n = 1;

         while (worldIn.getBlockState(pos.down(n)).getBlock() == this) {
            n++;
         }

         if (n < 3) {
            int value = state.getValue(AGE);
            if (value == 15) {
               worldIn.setBlockState(pos.up(), this.getDefaultState());
               worldIn.setBlockState(pos, state.withProperty(AGE, 0), 4);
            } else {
               worldIn.setBlockState(pos, state.withProperty(AGE, value + 1), 4);
            }
         }
      }
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      Block block = worldIn.getBlockState(pos.down()).getBlock();
      if (block == this) {
         return true;
      }

      if (block != Blocks.grass && block != Blocks.dirt && block != Blocks.sand) {
         return false;
      }

      for (EnumFacing enumFacing : Plane.HORIZONTAL) {
         if (worldIn.getBlockState(pos.offset(enumFacing).down()).getBlock().getMaterial() == Material.water) {
            return true;
         }
      }

      return false;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      this.checkForDrop(worldIn, pos, state);
   }

   public boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
      if (this.canBlockStay(worldIn, pos)) {
         return true;
      }

      this.dropBlockAsItem(worldIn, pos, state, 0);
      worldIn.setBlockToAir(pos);
      return false;
   }

   public boolean canBlockStay(World worldIn, BlockPos pos) {
      return this.canPlaceBlockAt(worldIn, pos);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      return null;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.reeds;
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Items.reeds;
   }

   @Override
   public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
      return worldIn.getBiomeGenForCoords(pos).getGrassColorAtPos(pos);
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
