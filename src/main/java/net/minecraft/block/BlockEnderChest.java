package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryEnderChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockEnderChest extends BlockContainer {
   public static PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);

   public BlockEnderChest() {
      super(Material.rock);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.setCreativeTab(CreativeTabs.tabDecorations);
      this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.875F, 0.9375F);
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
   public int getRenderType() {
      return 2;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(Blocks.obsidian);
   }

   @Override
   public int quantityDropped(Random random) {
      return 8;
   }

   @Override
   public boolean canSilkHarvest() {
      return true;
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
   }

   @Override
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      InventoryEnderChest inventoryEnderChest = playerIn.getInventoryEnderChest();
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (inventoryEnderChest == null || !(tileEntity instanceof TileEntityEnderChest)) {
         return true;
      }

      if (worldIn.getBlockState(pos.up()).getBlock().isNormalCube()) {
         return true;
      }

      if (worldIn.isRemote) {
         return true;
      }

      inventoryEnderChest.setChestTileEntity((TileEntityEnderChest)tileEntity);
      playerIn.displayGUIChest(inventoryEnderChest);
      playerIn.triggerAchievement(StatList.ENDERCHEST_OPENED);
      return true;
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityEnderChest();
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      for (int i = 0; i < 3; i++) {
         int n = rand.nextInt(2) * 2 - 1;
         int n2 = rand.nextInt(2) * 2 - 1;
         double d = pos.getX() + 0.5 + 0.25 * n;
         double d2 = pos.getY() + rand.nextFloat();
         double d3 = pos.getZ() + 0.5 + 0.25 * n2;
         double d4 = rand.nextFloat() * n;
         double d5 = (rand.nextFloat() - 0.5) * 0.125;
         double d6 = rand.nextFloat() * n2;
         worldIn.spawnParticle(EnumParticleTypes.PORTAL, d, d2, d3, d4, d5, d6);
      }
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      EnumFacing front = EnumFacing.getFront(meta);
      if (front.getAxis() == Axis.Y) {
         front = EnumFacing.NORTH;
      }

      return this.getDefaultState().withProperty(FACING, front);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(FACING).getIndex();
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING);
   }
}
