package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
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
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneWire extends Block {
   public static PropertyEnum<BlockRedstoneWire.EnumAttachPosition> NORTH = PropertyEnum.create("north", BlockRedstoneWire.EnumAttachPosition.class);
   public static PropertyEnum<BlockRedstoneWire.EnumAttachPosition> EAST = PropertyEnum.create("east", BlockRedstoneWire.EnumAttachPosition.class);
   public static PropertyEnum<BlockRedstoneWire.EnumAttachPosition> SOUTH = PropertyEnum.create("south", BlockRedstoneWire.EnumAttachPosition.class);
   public static PropertyEnum<BlockRedstoneWire.EnumAttachPosition> WEST = PropertyEnum.create("west", BlockRedstoneWire.EnumAttachPosition.class);
   public static PropertyInteger POWER = PropertyInteger.create("power", 0, 15);
   public boolean canProvidePower = true;
   public Set<BlockPos> blocksNeedingUpdate = Sets.newHashSet();

   public BlockRedstoneWire() {
      super(Material.circuits);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(NORTH, BlockRedstoneWire.EnumAttachPosition.NONE)
            .withProperty(EAST, BlockRedstoneWire.EnumAttachPosition.NONE)
            .withProperty(SOUTH, BlockRedstoneWire.EnumAttachPosition.NONE)
            .withProperty(WEST, BlockRedstoneWire.EnumAttachPosition.NONE)
            .withProperty(POWER, 0)
      );
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      state = state.withProperty(WEST, this.getAttachPosition(worldIn, pos, EnumFacing.WEST));
      state = state.withProperty(EAST, this.getAttachPosition(worldIn, pos, EnumFacing.EAST));
      state = state.withProperty(NORTH, this.getAttachPosition(worldIn, pos, EnumFacing.NORTH));
      return state.withProperty(SOUTH, this.getAttachPosition(worldIn, pos, EnumFacing.SOUTH));
   }

   public BlockRedstoneWire.EnumAttachPosition getAttachPosition(IBlockAccess worldIn, BlockPos pos, EnumFacing direction) {
      BlockPos offset = pos.offset(direction);
      Block block = worldIn.getBlockState(pos.offset(direction)).getBlock();
      if (!canConnectTo(worldIn.getBlockState(offset), direction) && (block.isBlockNormalCube() || !canConnectUpwardsTo(worldIn.getBlockState(offset.down())))) {
         Block block2 = worldIn.getBlockState(pos.up()).getBlock();
         return !block2.isBlockNormalCube() && block.isBlockNormalCube() && canConnectUpwardsTo(worldIn.getBlockState(offset.up()))
            ? BlockRedstoneWire.EnumAttachPosition.UP
            : BlockRedstoneWire.EnumAttachPosition.NONE;
      } else {
         return BlockRedstoneWire.EnumAttachPosition.SIDE;
      }
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
   public int colorMultiplier(IBlockAccess worldIn, BlockPos pos, int renderPass) {
      IBlockState blockState = worldIn.getBlockState(pos);
      return blockState.getBlock() != this ? super.colorMultiplier(worldIn, pos, renderPass) : this.colorMultiplier(blockState.getValue(POWER));
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) || worldIn.getBlockState(pos.down()).getBlock() == Blocks.glowstone;
   }

   public IBlockState updateSurroundingRedstone(World worldIn, BlockPos pos, IBlockState state) {
      state = this.calculateCurrentChanges(worldIn, pos, pos, state);
      ArrayList newArrayList = Lists.newArrayList(this.blocksNeedingUpdate);
      this.blocksNeedingUpdate.clear();

      for (BlockPos blockPos : (Iterable<BlockPos>)newArrayList) {
         worldIn.notifyNeighborsOfStateChange(blockPos, this);
      }

      return state;
   }

   public IBlockState calculateCurrentChanges(World worldIn, BlockPos pos1, BlockPos pos2, IBlockState state) {
      IBlockState state2 = state;
      int value = state.getValue(POWER);
      int n = 0;
      n = this.getMaxCurrentStrength(worldIn, pos2, n);
      this.canProvidePower = false;
      int isBlockIndirectlyGettingPowered = worldIn.isBlockIndirectlyGettingPowered(pos1);
      this.canProvidePower = true;
      if (isBlockIndirectlyGettingPowered > 0 && isBlockIndirectlyGettingPowered > n - 1) {
         n = isBlockIndirectlyGettingPowered;
      }

      int n2 = 0;

      for (EnumFacing enumFacing : Plane.HORIZONTAL) {
         BlockPos offset = pos1.offset(enumFacing);
         boolean flag = offset.getX() != pos2.getX() || offset.getZ() != pos2.getZ();
         if (flag) {
            n2 = this.getMaxCurrentStrength(worldIn, offset, n2);
         }

         if (worldIn.getBlockState(offset).getBlock().isNormalCube() && !worldIn.getBlockState(pos1.up()).getBlock().isNormalCube()) {
            if (flag && pos1.getY() >= pos2.getY()) {
               n2 = this.getMaxCurrentStrength(worldIn, offset.up(), n2);
            }
         } else if (!worldIn.getBlockState(offset).getBlock().isNormalCube() && flag && pos1.getY() <= pos2.getY()) {
            n2 = this.getMaxCurrentStrength(worldIn, offset.down(), n2);
         }
      }

      if (n2 > n) {
         n = n2 - 1;
      } else if (n > 0) {
         n--;
      } else {
         n = 0;
      }

      if (isBlockIndirectlyGettingPowered > n - 1) {
         n = isBlockIndirectlyGettingPowered;
      }

      if (value != n) {
         state = state.withProperty(POWER, n);
         if (worldIn.getBlockState(pos1) == state2) {
            worldIn.setBlockState(pos1, state, 2);
         }

         this.blocksNeedingUpdate.add(pos1);

         for (EnumFacing enumFacing : EnumFacing.values()) {
            this.blocksNeedingUpdate.add(pos1.offset(enumFacing));
         }
      }

      return state;
   }

   public void notifyWireNeighborsOfStateChange(World worldIn, BlockPos pos) {
      if (worldIn.getBlockState(pos).getBlock() == this) {
         worldIn.notifyNeighborsOfStateChange(pos, this);

         for (EnumFacing enumFacing : EnumFacing.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumFacing), this);
         }
      }
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isRemote) {
         this.updateSurroundingRedstone(worldIn, pos, state);

         for (EnumFacing enumFacing : Plane.VERTICAL) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumFacing), this);
         }

         for (EnumFacing enumFacing : Plane.HORIZONTAL) {
            this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(enumFacing));
         }

         for (EnumFacing enumFacing : Plane.HORIZONTAL) {
            BlockPos offset = pos.offset(enumFacing);
            if (worldIn.getBlockState(offset).getBlock().isNormalCube()) {
               this.notifyWireNeighborsOfStateChange(worldIn, offset.up());
            } else {
               this.notifyWireNeighborsOfStateChange(worldIn, offset.down());
            }
         }
      }
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      super.breakBlock(worldIn, pos, state);
      if (!worldIn.isRemote) {
         for (EnumFacing enumFacing : EnumFacing.values()) {
            worldIn.notifyNeighborsOfStateChange(pos.offset(enumFacing), this);
         }

         this.updateSurroundingRedstone(worldIn, pos, state);

         for (EnumFacing enumFacing : Plane.HORIZONTAL) {
            this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(enumFacing));
         }

         for (EnumFacing enumFacing : Plane.HORIZONTAL) {
            BlockPos offset = pos.offset(enumFacing);
            if (worldIn.getBlockState(offset).getBlock().isNormalCube()) {
               this.notifyWireNeighborsOfStateChange(worldIn, offset.up());
            } else {
               this.notifyWireNeighborsOfStateChange(worldIn, offset.down());
            }
         }
      }
   }

   public int getMaxCurrentStrength(World worldIn, BlockPos pos, int strength) {
      if (worldIn.getBlockState(pos).getBlock() != this) {
         return strength;
      }

      int value = worldIn.getBlockState(pos).getValue(POWER);
      return value > strength ? value : strength;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!worldIn.isRemote) {
         if (this.canPlaceBlockAt(worldIn, pos)) {
            this.updateSurroundingRedstone(worldIn, pos, state);
         } else {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return Items.redstone;
   }

   @Override
   public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return !this.canProvidePower ? 0 : this.getWeakPower(worldIn, pos, state, side);
   }

   @Override
   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      if (!this.canProvidePower) {
         return 0;
      }

      int value = state.getValue(POWER);
      if (value == 0) {
         return 0;
      }

      if (side == EnumFacing.UP) {
         return value;
      }

      EnumSet noneOf = EnumSet.noneOf(EnumFacing.class);

      for (EnumFacing enumFacing : Plane.HORIZONTAL) {
         if (this.connectsTo(worldIn, pos, enumFacing)) {
            noneOf.add(enumFacing);
         }
      }

      if (side.getAxis().isHorizontal() && noneOf.isEmpty()) {
         return value;
      } else {
         return noneOf.contains(side) && !noneOf.contains(side.rotateYCCW()) && !noneOf.contains(side.rotateY()) ? value : 0;
      }
   }

   public boolean connectsTo(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      BlockPos offset = pos.offset(side);
      IBlockState blockState = worldIn.getBlockState(offset);
      Block block = blockState.getBlock();
      boolean isNormalCube = block.isNormalCube();
      boolean isNormalCube2 = worldIn.getBlockState(pos.up()).getBlock().isNormalCube();
      return !isNormalCube2 && isNormalCube && canConnectUpwardsTo(worldIn, offset.up())
         ? true
         : (
            canConnectTo(blockState, side)
               ? true
               : (
                  block == Blocks.powered_repeater && blockState.getValue(BlockRedstoneDiode.FACING) == side
                     ? true
                     : !isNormalCube && canConnectUpwardsTo(worldIn, offset.down())
               )
         );
   }

   public static boolean canConnectUpwardsTo(IBlockAccess worldIn, BlockPos pos) {
      return canConnectUpwardsTo(worldIn.getBlockState(pos));
   }

   public static boolean canConnectUpwardsTo(IBlockState state) {
      return canConnectTo(state, (EnumFacing)null);
   }

   public static boolean canConnectTo(IBlockState blockState, EnumFacing side) {
      Block block = blockState.getBlock();
      if (block == Blocks.redstone_wire) {
         return true;
      } else if (Blocks.unpowered_repeater.isAssociated(block)) {
         EnumFacing value = blockState.getValue(BlockRedstoneRepeater.FACING);
         return value == side || value.getOpposite() == side;
      } else {
         return block.canProvidePower() && side != null;
      }
   }

   @Override
   public boolean canProvidePower() {
      return this.canProvidePower;
   }

   public int colorMultiplier(int powerLevel) {
      float f = powerLevel / 15.0F;
      float f2 = f * 0.6F + 0.4F;
      if (powerLevel == 0) {
         f2 = 0.3F;
      }

      float f3 = f * f * 0.7F - 0.5F;
      float f4 = f * f * 0.6F - 0.7F;
      if (f3 < 0.0F) {
         f3 = 0.0F;
      }

      if (f4 < 0.0F) {
         f4 = 0.0F;
      }

      int n = MathHelper.clamp_int((int)(f2 * 255.0F), 0, 255);
      int n2 = MathHelper.clamp_int((int)(f3 * 255.0F), 0, 255);
      int n3 = MathHelper.clamp_int((int)(f4 * 255.0F), 0, 255);
      return 0xFF000000 | n << 16 | n2 << 8 | n3;
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      int value = state.getValue(POWER);
      if (value != 0) {
         double d = pos.getX() + 0.5 + (rand.nextFloat() - 0.5) * 0.2;
         double d2 = pos.getY() + 0.0625F;
         double d3 = pos.getZ() + 0.5 + (rand.nextFloat() - 0.5) * 0.2;
         float f = value / 15.0F;
         float f2 = f * 0.6F + 0.4F;
         float max = Math.max(0.0F, f * f * 0.7F - 0.5F);
         float max2 = Math.max(0.0F, f * f * 0.6F - 0.7F);
         worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d, d2, d3, f2, max, max2);
      }
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return Items.redstone;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(POWER, meta);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      return state.getValue(POWER);
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, NORTH, EAST, SOUTH, WEST, POWER);
   }

   enum EnumAttachPosition implements IStringSerializable {
      UP("up"),
      SIDE("side"),
      NONE("none");

      public String name;

      EnumAttachPosition(String name) {
         this.name = name;
      }

      @Override
      public String toString() {
         return this.getName();
      }

      @Override
      public String getName() {
         return this.name;
      }
   }
}
