package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.World;

public class BlockBush extends Block {
   public BlockBush() {
      this(Material.plants);
   }

   public BlockBush(Material materialIn) {
      this(materialIn, materialIn.getMaterialMapColor());
   }

   public BlockBush(Material material, MapColor mapColor) {
      super(material, mapColor);
      this.setTickRandomly(true);
      float f = 0.2F;
      this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 3.0F, 0.5F + f);
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return super.canPlaceBlockAt(worldIn, pos) && this.canPlaceBlockOn(worldIn.getBlockState(pos.down()).getBlock());
   }

   public boolean canPlaceBlockOn(Block ground) {
      return ground == Blocks.grass || ground == Blocks.dirt || ground == Blocks.farmland;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
      this.checkAndDropBlock(worldIn, pos, state);
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      this.checkAndDropBlock(worldIn, pos, state);
   }

   public void checkAndDropBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.canBlockStay(worldIn, pos, state)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockState(pos, Blocks.air.getDefaultState(), 3);
      }
   }

   public boolean canBlockStay(World worldIn, BlockPos pos, IBlockState state) {
      return this.canPlaceBlockOn(worldIn.getBlockState(pos.down()).getBlock());
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
   public boolean isFullCube() {
      return false;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }
}
