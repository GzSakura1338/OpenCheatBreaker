package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRedstoneDiode extends BlockDirectional {
   public boolean isRepeaterPowered;

   public BlockRedstoneDiode(boolean powered) {
      super(Material.circuits);
      this.isRepeaterPowered = powered;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) ? super.canPlaceBlockAt(worldIn, pos) : false;
   }

   public boolean canBlockStay(World worldIn, BlockPos pos) {
      return World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
   }

   @Override
   public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!this.isLocked(worldIn, pos, state)) {
         boolean shouldBePowered = this.shouldBePowered(worldIn, pos, state);
         if (this.isRepeaterPowered && !shouldBePowered) {
            worldIn.setBlockState(pos, this.getUnpoweredState(state), 2);
         } else if (!this.isRepeaterPowered) {
            worldIn.setBlockState(pos, this.getPoweredState(state), 2);
            if (!shouldBePowered) {
               worldIn.updateBlockTick(pos, this.getPoweredState(state).getBlock(), this.getTickDelay(state), -1);
            }
         }
      }
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      return side.getAxis() != Axis.Y;
   }

   public boolean isPowered(IBlockState state) {
      return this.isRepeaterPowered;
   }

   @Override
   public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return this.getWeakPower(worldIn, pos, state, side);
   }

   @Override
   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return !this.isPowered(state) ? 0 : (state.getValue(FACING) == side ? this.getActiveSignal(worldIn, pos, state) : 0);
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (this.canBlockStay(worldIn, pos)) {
         this.updateState(worldIn, pos, state);
      } else {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);

         for (EnumFacing enumFacing : EnumFacing.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumFacing), this);
         }
      }
   }

   public void updateState(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.isLocked(worldIn, pos, state)) {
         boolean shouldBePowered = this.shouldBePowered(worldIn, pos, state);
         if ((this.isRepeaterPowered && !shouldBePowered || !this.isRepeaterPowered && shouldBePowered) && !worldIn.isBlockTickPending(pos, this)) {
            byte b = -1;
            if (this.isFacingTowardsRepeater(worldIn, pos, state)) {
               b = -3;
            } else if (this.isRepeaterPowered) {
               b = -2;
            }

            worldIn.updateBlockTick(pos, this, this.getDelay(state), b);
         }
      }
   }

   public boolean isLocked(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
      return false;
   }

   public boolean shouldBePowered(World worldIn, BlockPos pos, IBlockState state) {
      return this.calculateInputStrength(worldIn, pos, state) > 0;
   }

   public int calculateInputStrength(World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing value = state.getValue(FACING);
      BlockPos offset = pos.offset(value);
      int redstonePower = worldIn.getRedstonePower(offset, value);
      if (redstonePower >= 15) {
         return redstonePower;
      }

      IBlockState blockState = worldIn.getBlockState(offset);
      return Math.max(redstonePower, blockState.getBlock() == Blocks.redstone_wire ? blockState.getValue(BlockRedstoneWire.POWER) : 0);
   }

   public int getPowerOnSides(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
      EnumFacing value = state.getValue(FACING);
      EnumFacing rotateY = value.rotateY();
      EnumFacing rotateYCCW = value.rotateYCCW();
      return Math.max(this.getPowerOnSide(worldIn, pos.offset(rotateY), rotateY), this.getPowerOnSide(worldIn, pos.offset(rotateYCCW), rotateYCCW));
   }

   public int getPowerOnSide(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      IBlockState blockState = worldIn.getBlockState(pos);
      Block block = blockState.getBlock();
      return this.canPowerSide(block) ? (block == Blocks.redstone_wire ? blockState.getValue(BlockRedstoneWire.POWER) : worldIn.getStrongPower(pos, side)) : 0;
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing().getOpposite());
   }

   @Override
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      if (this.shouldBePowered(worldIn, pos, state)) {
         worldIn.scheduleUpdate(pos, this, 1);
      }
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.notifyNeighbors(worldIn, pos, state);
   }

   public void notifyNeighbors(World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing value = state.getValue(FACING);
      BlockPos offset = pos.offset(value.getOpposite());
      worldIn.notifyBlockOfStateChange(offset, this);
      worldIn.notifyNeighborsOfStateExcept(offset, this, value);
   }

   @Override
   public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
      if (this.isRepeaterPowered) {
         for (EnumFacing enumFacing : EnumFacing.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumFacing), this);
         }
      }

      super.onBlockDestroyedByPlayer(worldIn, pos, state);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   public boolean canPowerSide(Block blockIn) {
      return blockIn.canProvidePower();
   }

   public int getActiveSignal(IBlockAccess worldIn, BlockPos pos, IBlockState state) {
      return 15;
   }

   public static boolean isRedstoneRepeaterBlockID(Block blockIn) {
      return Blocks.unpowered_repeater.isAssociated(blockIn) || Blocks.unpowered_comparator.isAssociated(blockIn);
   }

   public boolean isAssociated(Block other) {
      return other == this.getPoweredState(this.getDefaultState()).getBlock() || other == this.getUnpoweredState(this.getDefaultState()).getBlock();
   }

   public boolean isFacingTowardsRepeater(World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing opposite = state.getValue(FACING).getOpposite();
      BlockPos offset = pos.offset(opposite);
      return isRedstoneRepeaterBlockID(worldIn.getBlockState(offset).getBlock()) ? worldIn.getBlockState(offset).getValue(FACING) != opposite : false;
   }

   public int getTickDelay(IBlockState state) {
      return this.getDelay(state);
   }

   public abstract int getDelay(IBlockState blockState);

   public abstract IBlockState getPoweredState(IBlockState blockState);

   public abstract IBlockState getUnpoweredState(IBlockState blockState);

   @Override
   public boolean isAssociatedBlock(Block other) {
      return this.isAssociated(other);
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }
}
