package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWire extends Block {
   public static PropertyBool POWERED = PropertyBool.create("powered");
   public static PropertyBool SUSPENDED = PropertyBool.create("suspended");
   public static PropertyBool ATTACHED = PropertyBool.create("attached");
   public static PropertyBool DISARMED = PropertyBool.create("disarmed");
   public static PropertyBool NORTH = PropertyBool.create("north");
   public static PropertyBool EAST = PropertyBool.create("east");
   public static PropertyBool SOUTH = PropertyBool.create("south");
   public static PropertyBool WEST = PropertyBool.create("west");

   public BlockTripWire() {
      super(Material.circuits);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(POWERED, false)
            .withProperty(SUSPENDED, false)
            .withProperty(ATTACHED, false)
            .withProperty(DISARMED, false)
            .withProperty(NORTH, false)
            .withProperty(EAST, false)
            .withProperty(SOUTH, false)
            .withProperty(WEST, false)
      );
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.15625F, 1.0F);
      this.setTickRandomly(true);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state.withProperty(NORTH, isConnectedTo(worldIn, pos, state, EnumFacing.NORTH))
         .withProperty(EAST, isConnectedTo(worldIn, pos, state, EnumFacing.EAST))
         .withProperty(SOUTH, isConnectedTo(worldIn, pos, state, EnumFacing.SOUTH))
         .withProperty(WEST, isConnectedTo(worldIn, pos, state, EnumFacing.WEST));
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
      return EnumWorldBlockLayer.TRANSLUCENT;
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.string;
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Items.string;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      boolean value = state.getValue(SUSPENDED);
      boolean flag = !World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
      if (value != flag) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      IBlockState blockState = worldIn.getBlockState(pos);
      boolean value = blockState.getValue(ATTACHED);
      boolean value2 = blockState.getValue(SUSPENDED);
      if (!value2) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.09375F, 1.0F);
      } else if (!value) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
      } else {
         this.setBlockBounds(0.0F, 0.0625F, 0.0F, 1.0F, 0.15625F, 1.0F);
      }
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      state = state.withProperty(SUSPENDED, !World.doesBlockHaveSolidTopSurface(worldIn, pos.down()));
      worldIn.setBlockState(pos, state, 3);
      this.notifyHook(worldIn, pos, state);
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      this.notifyHook(worldIn, pos, state.withProperty(POWERED, true));
   }

   @Override
   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      if (!worldIn.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears) {
         worldIn.setBlockState(pos, state.withProperty(DISARMED, Boolean.TRUE), 4);
      }
   }

   public void notifyHook(World worldIn, BlockPos pos, IBlockState state) {
      for (EnumFacing enumFacing : new EnumFacing[]{EnumFacing.SOUTH, EnumFacing.WEST}) {
         for (int i = 1; i < 42; i++) {
            BlockPos offset = pos.offset(enumFacing, i);
            IBlockState blockState = worldIn.getBlockState(offset);
            if (blockState.getBlock() == Blocks.tripwire_hook) {
               if (blockState.getValue(BlockTripWireHook.FACING) == enumFacing.getOpposite()) {
                  Blocks.tripwire_hook.update(worldIn, offset, blockState, false, true, i, state);
               }
               break;
            }

            if (blockState.getBlock() != Blocks.tripwire) {
               break;
            }
         }
      }
   }

   @Override
   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      if (!worldIn.isRemote && !state.getValue(POWERED)) {
         this.updateState(worldIn, pos);
      }
   }

   @Override
   public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote && worldIn.getBlockState(pos).getValue(POWERED)) {
         this.updateState(worldIn, pos);
      }
   }

   public void updateState(World worldIn, BlockPos pos) {
      IBlockState blockState = worldIn.getBlockState(pos);
      boolean value = blockState.getValue(POWERED);
      boolean flag = false;
      List list = worldIn.getEntitiesWithinAABBExcludingEntity(
         (Entity)null,
         new AxisAlignedBB(
            pos.getX() + this.minX, pos.getY() + this.minY, pos.getZ() + this.minZ, pos.getX() + this.maxX, pos.getY() + this.maxY, pos.getZ() + this.maxZ
         )
      );
      if (!list.isEmpty()) {
         for (Entity entity : (Iterable<Entity>)list) {
            if (!entity.doesEntityNotTriggerPressurePlate()) {
               flag = true;
               break;
            }
         }
      }

      if (flag != value) {
         blockState = blockState.withProperty(POWERED, flag);
         worldIn.setBlockState(pos, blockState, 3);
         this.notifyHook(worldIn, pos, blockState);
      }

      if (flag) {
         worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
      }
   }

   public static boolean isConnectedTo(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing direction) {
      BlockPos offset = pos.offset(direction);
      IBlockState blockState = worldIn.getBlockState(offset);
      Block block = blockState.getBlock();
      if (block == Blocks.tripwire_hook) {
         EnumFacing opposite = direction.getOpposite();
         return blockState.getValue(BlockTripWireHook.FACING) == opposite;
      } else if (block == Blocks.tripwire) {
         boolean value = state.getValue(SUSPENDED);
         boolean value2 = blockState.getValue(SUSPENDED);
         return value == value2;
      } else {
         return false;
      }
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState()
         .withProperty(POWERED, (meta & 1) > 0)
         .withProperty(SUSPENDED, (meta & 2) > 0)
         .withProperty(ATTACHED, (meta & 4) > 0)
         .withProperty(DISARMED, (meta & 8) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int n = 0;
      if (state.getValue(POWERED)) {
         n |= 1;
      }

      if (state.getValue(SUSPENDED)) {
         n |= 2;
      }

      if (state.getValue(ATTACHED)) {
         n |= 4;
      }

      if (state.getValue(DISARMED)) {
         n |= 8;
      }

      return n;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, POWERED, SUSPENDED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
   }
}
