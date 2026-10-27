package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.stats.StatList;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCake extends Block {
   public static PropertyInteger BITES = PropertyInteger.create("bites", 0, 6);

   public BlockCake() {
      super(Material.cake);
      this.setDefaultState(this.blockState.getBaseState().withProperty(BITES, 0));
      this.setTickRandomly(true);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      float f = 0.0625F;
      float f2 = (1 + worldIn.getBlockState(pos).getValue(BITES) * 2) / 16.0F;
      float f3 = 0.5F;
      this.setBlockBounds(f2, 0.0F, f, 1.0F - f, f3, 1.0F - f);
   }

   @Override
   public void setBlockBoundsForItemRender() {
      float f = 0.0625F;
      float f2 = 0.5F;
      this.setBlockBounds(f, 0.0F, f, 1.0F - f, f2, 1.0F - f);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      float f = 0.0625F;
      float f2 = (1 + state.getValue(BITES) * 2) / 16.0F;
      float f3 = 0.5F;
      return new AxisAlignedBB(pos.getX() + f2, pos.getY(), pos.getZ() + f, pos.getX() + 1 - f, pos.getY() + f3, pos.getZ() + 1 - f);
   }

   @Override
   public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
      return this.getCollisionBoundingBox(worldIn, pos, worldIn.getBlockState(pos));
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
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      this.eatCake(worldIn, pos, state, playerIn);
      return true;
   }

   @Override
   public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
      this.eatCake(worldIn, pos, worldIn.getBlockState(pos), playerIn);
   }

   public void eatCake(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (player.canEat(false)) {
         player.triggerAchievement(StatList.CAKE_SLICES_EATEN);
         player.getFoodStats().addStats(2, 0.1F);
         int value = state.getValue(BITES);
         if (value < 6) {
            worldIn.setBlockState(pos, state.withProperty(BITES, value + 1), 3);
         } else {
            worldIn.setBlockToAir(pos);
         }
      }
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return super.canPlaceBlockAt(worldIn, pos) ? this.canBlockStay(worldIn, pos) : false;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!this.canBlockStay(worldIn, pos)) {
         worldIn.setBlockToAir(pos);
      }
   }

   public boolean canBlockStay(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).getBlock().getMaterial().isSolid();
   }

   @Override
   public int quantityDropped(Random random) {
      return 0;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return null;
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Items.cake;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(BITES, meta);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(BITES);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, BITES);
   }

   @Override
   public int getComparatorInputOverride(World worldIn, BlockPos pos) {
      return (7 - worldIn.getBlockState(pos).getValue(BITES)) * 2;
   }

   @Override
   public boolean hasComparatorInputOverride() {
      return true;
   }
}
