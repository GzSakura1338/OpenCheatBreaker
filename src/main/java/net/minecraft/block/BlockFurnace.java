package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class BlockFurnace extends BlockContainer {
   public static PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);
   public boolean isBurning;
   public static boolean keepInventory;

   public BlockFurnace(boolean isBurning) {
      super(Material.rock);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.isBurning = isBurning;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Item.getItemFromBlock(Blocks.furnace);
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.setDefaultFacing(worldIn, pos, state);
   }

   public void setDefaultFacing(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isRemote) {
         Block block = worldIn.getBlockState(pos.north()).getBlock();
         Block block2 = worldIn.getBlockState(pos.south()).getBlock();
         Block block3 = worldIn.getBlockState(pos.west()).getBlock();
         Block block4 = worldIn.getBlockState(pos.east()).getBlock();
         EnumFacing value = state.getValue(FACING);
         if (value == EnumFacing.NORTH && block.isFullBlock() && !block2.isFullBlock()) {
            value = EnumFacing.SOUTH;
         } else if (value == EnumFacing.SOUTH && block2.isFullBlock() && !block.isFullBlock()) {
            value = EnumFacing.NORTH;
         } else if (value == EnumFacing.WEST && block3.isFullBlock() && !block4.isFullBlock()) {
            value = EnumFacing.EAST;
         } else if (value == EnumFacing.EAST && block4.isFullBlock() && !block3.isFullBlock()) {
            value = EnumFacing.WEST;
         }

         worldIn.setBlockState(pos, state.withProperty(FACING, value), 2);
      }
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (this.isBurning) {
         EnumFacing value = state.getValue(FACING);
         double d = pos.getX() + 0.5;
         double d2 = pos.getY() + rand.nextDouble() * 6.0 / 16.0;
         double d3 = pos.getZ() + 0.5;
         double d4 = 0.52;
         double d5 = rand.nextDouble() * 0.6 - 0.3;
         switch (value) {
            case WEST:
               worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d - d4, d2, d3 + d5, 0.0, 0.0, 0.0);
               worldIn.spawnParticle(EnumParticleTypes.FLAME, d - d4, d2, d3 + d5, 0.0, 0.0, 0.0);
               break;
            case EAST:
               worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d + d4, d2, d3 + d5, 0.0, 0.0, 0.0);
               worldIn.spawnParticle(EnumParticleTypes.FLAME, d + d4, d2, d3 + d5, 0.0, 0.0, 0.0);
               break;
            case NORTH:
               worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d + d5, d2, d3 - d4, 0.0, 0.0, 0.0);
               worldIn.spawnParticle(EnumParticleTypes.FLAME, d + d5, d2, d3 - d4, 0.0, 0.0, 0.0);
               break;
            case SOUTH:
               worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d + d5, d2, d3 + d4, 0.0, 0.0, 0.0);
               worldIn.spawnParticle(EnumParticleTypes.FLAME, d + d5, d2, d3 + d4, 0.0, 0.0, 0.0);
         }
      }
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      }

      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityFurnace) {
         playerIn.displayGUIChest((TileEntityFurnace)tileEntity);
         playerIn.triggerAchievement(StatList.FURNACE_INTERACTION);
      }

      return true;
   }

   public static void setState(boolean active, World worldIn, BlockPos pos) {
      IBlockState blockState = worldIn.getBlockState(pos);
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      keepInventory = true;
      if (active) {
         worldIn.setBlockState(pos, Blocks.lit_furnace.getDefaultState().withProperty(FACING, blockState.getValue(FACING)), 3);
         worldIn.setBlockState(pos, Blocks.lit_furnace.getDefaultState().withProperty(FACING, blockState.getValue(FACING)), 3);
      } else {
         worldIn.setBlockState(pos, Blocks.furnace.getDefaultState().withProperty(FACING, blockState.getValue(FACING)), 3);
         worldIn.setBlockState(pos, Blocks.furnace.getDefaultState().withProperty(FACING, blockState.getValue(FACING)), 3);
      }

      keepInventory = false;
      if (tileEntity != null) {
         tileEntity.validate();
         worldIn.setTileEntity(pos, tileEntity);
      }
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityFurnace();
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
   }

   @Override
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
      if (stack.hasDisplayName()) {
         TileEntity tileEntity = worldIn.getTileEntity(pos);
         if (tileEntity instanceof TileEntityFurnace) {
            ((TileEntityFurnace)tileEntity).setCustomInventoryName(stack.getDisplayName());
         }
      }
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (!keepInventory) {
         TileEntity tileEntity = worldIn.getTileEntity(pos);
         if (tileEntity instanceof TileEntityFurnace) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityFurnace)tileEntity);
            worldIn.updateComparatorOutputLevel(pos, this);
         }
      }

      super.breakBlock(worldIn, pos, state);
   }

   @Override
   public boolean hasComparatorInputOverride() {
      return true;
   }

   @Override
   public int getComparatorInputOverride(World worldIn, BlockPos pos) {
      return Container.calcRedstone(worldIn.getTileEntity(pos));
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Item.getItemFromBlock(Blocks.furnace);
   }

   @Override
   public int getRenderType() {
      return 3;
   }

   @Override
   public IBlockState getStateForEntityRender(IBlockState state) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
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
