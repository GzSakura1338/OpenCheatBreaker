package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBehaviorDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.PositionImpl;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.RegistryDefaulted;
import net.minecraft.world.World;

public class BlockDispenser extends BlockContainer {
   public static PropertyDirection FACING = PropertyDirection.create("facing");
   public static PropertyBool TRIGGERED = PropertyBool.create("triggered");
   public static RegistryDefaulted<Item, IBehaviorDispenseItem> dispenseBehaviorRegistry = new RegistryDefaulted<>(new BehaviorDefaultDispenseItem());
   public Random rand = new Random();

   public BlockDispenser() {
      super(Material.rock);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TRIGGERED, false));
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   @Override
   public int tickRate(World worldIn) {
      return 4;
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      super.onBlockAdded(worldIn, pos, state);
      this.setDefaultDirection(worldIn, pos, state);
   }

   public void setDefaultDirection(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isRemote) {
         EnumFacing value = state.getValue(FACING);
         boolean isFullBlock = worldIn.getBlockState(pos.north()).getBlock().isFullBlock();
         boolean isFullBlock2 = worldIn.getBlockState(pos.south()).getBlock().isFullBlock();
         if (value == EnumFacing.NORTH && isFullBlock && !isFullBlock2) {
            value = EnumFacing.SOUTH;
         } else if (value == EnumFacing.SOUTH && isFullBlock2 && !isFullBlock) {
            value = EnumFacing.NORTH;
         } else {
            boolean isFullBlock3 = worldIn.getBlockState(pos.west()).getBlock().isFullBlock();
            boolean isFullBlock4 = worldIn.getBlockState(pos.east()).getBlock().isFullBlock();
            if (value == EnumFacing.WEST && isFullBlock3 && !isFullBlock4) {
               value = EnumFacing.EAST;
            } else if (value == EnumFacing.EAST && isFullBlock4 && !isFullBlock3) {
               value = EnumFacing.WEST;
            }
         }

         worldIn.setBlockState(pos, state.withProperty(FACING, value).withProperty(TRIGGERED, false), 2);
      }
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      }

      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityDispenser) {
         playerIn.displayGUIChest((TileEntityDispenser)tileEntity);
         if (tileEntity instanceof TileEntityDropper) {
            playerIn.triggerAchievement(StatList.INTERACTIONS_WITH_DROPPER);
         } else {
            playerIn.triggerAchievement(StatList.INTERACTIONS_WITH_DISPENSER);
         }
      }

      return true;
   }

   public void dispense(World worldIn, BlockPos pos) {
      BlockSourceImpl blockSourceImpl = new BlockSourceImpl(worldIn, pos);
      TileEntityDispenser blockTileEntity = blockSourceImpl.getBlockTileEntity();
      if (blockTileEntity != null) {
         int dispenseSlot = blockTileEntity.getDispenseSlot();
         if (dispenseSlot < 0) {
            worldIn.playAuxSFX(1001, pos, 0);
         } else {
            ItemStack stackInSlot = blockTileEntity.getStackInSlot(dispenseSlot);
            IBehaviorDispenseItem behavior = this.getBehavior(stackInSlot);
            if (behavior != IBehaviorDispenseItem.itemDispenseBehaviorProvider) {
               ItemStack dispense = behavior.dispense(blockSourceImpl, stackInSlot);
               blockTileEntity.setInventorySlotContents(dispenseSlot, dispense.stackSize <= 0 ? null : dispense);
            }
         }
      }
   }

   public IBehaviorDispenseItem getBehavior(ItemStack stack) {
      return dispenseBehaviorRegistry.getObject(stack == null ? null : stack.getItem());
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      boolean flag = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(pos.up());
      boolean value = state.getValue(TRIGGERED);
      if (flag && !value) {
         worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
         worldIn.setBlockState(pos, state.withProperty(TRIGGERED, true), 4);
      } else if (!flag && value) {
         worldIn.setBlockState(pos, state.withProperty(TRIGGERED, false), 4);
      }
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote) {
         this.dispense(worldIn, pos);
      }
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return new TileEntityDispenser();
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, BlockPistonBase.getFacingFromEntity(worldIn, pos, placer)).withProperty(TRIGGERED, false);
   }

   @Override
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      worldIn.setBlockState(pos, state.withProperty(FACING, BlockPistonBase.getFacingFromEntity(worldIn, pos, placer)), 2);
      if (stack.hasDisplayName()) {
         TileEntity tileEntity = worldIn.getTileEntity(pos);
         if (tileEntity instanceof TileEntityDispenser) {
            ((TileEntityDispenser)tileEntity).setCustomName(stack.getDisplayName());
         }
      }
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityDispenser) {
         InventoryHelper.dropInventoryItems(worldIn, pos, (TileEntityDispenser)tileEntity);
         worldIn.updateComparatorOutputLevel(pos, this);
      }

      super.breakBlock(worldIn, pos, state);
   }

   public static IPosition getDispensePosition(IBlockSource coords) {
      EnumFacing facing = getFacing(coords.getBlockMetadata());
      double d = coords.getX() + 0.7 * facing.getFrontOffsetX();
      double d2 = coords.getY() + 0.7 * facing.getFrontOffsetY();
      double d3 = coords.getZ() + 0.7 * facing.getFrontOffsetZ();
      return new PositionImpl(d, d2, d3);
   }

   public static EnumFacing getFacing(int meta) {
      return EnumFacing.getFront(meta & 7);
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
   public int getRenderType() {
      return 3;
   }

   @Override
   public IBlockState getStateForEntityRender(IBlockState state) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.SOUTH);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(TRIGGERED, (meta & 8) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int n = 0;
      n |= state.getValue(FACING).getIndex();
      if (state.getValue(TRIGGERED)) {
         n |= 8;
      }

      return n;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, TRIGGERED);
   }
}
