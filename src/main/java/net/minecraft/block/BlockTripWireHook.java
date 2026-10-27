package net.minecraft.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWireHook extends Block {
   public static PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);
   public static PropertyBool POWERED = PropertyBool.create("powered");
   public static PropertyBool ATTACHED = PropertyBool.create("attached");
   public static PropertyBool SUSPENDED = PropertyBool.create("suspended");

   public BlockTripWireHook() {
      super(Material.circuits);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(POWERED, false)
            .withProperty(ATTACHED, false)
            .withProperty(SUSPENDED, false)
      );
      this.setCreativeTab(CreativeTabs.tabRedstone);
      this.setTickRandomly(true);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      return state.withProperty(SUSPENDED, !World.doesBlockHaveSolidTopSurface(worldIn, pos.down()));
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
   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
      return side.getAxis().isHorizontal() && worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock().isNormalCube();
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      for (EnumFacing enumFacing : Plane.HORIZONTAL) {
         if (worldIn.getBlockState(pos.offset(enumFacing)).getBlock().isNormalCube()) {
            return true;
         }
      }

      return false;
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      IBlockState withProperty = this.getDefaultState().withProperty(POWERED, false).withProperty(ATTACHED, false).withProperty(SUSPENDED, false);
      if (facing.getAxis().isHorizontal()) {
         withProperty = withProperty.withProperty(FACING, facing);
      }

      return withProperty;
   }

   @Override
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      this.update(worldIn, pos, state, false, false, -1, (IBlockState)null);
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (neighborBlock != this && this.checkForDrop(worldIn, pos, state)) {
         EnumFacing value = state.getValue(FACING);
         if (!worldIn.getBlockState(pos.offset(value.getOpposite())).getBlock().isNormalCube()) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
         }
      }
   }

   public void update(World worldIn, BlockPos pos, IBlockState hookState, boolean flag, boolean flag2, int n, IBlockState blockState) {
      EnumFacing value = hookState.getValue(FACING);
      boolean value2 = hookState.getValue(ATTACHED);
      boolean value3 = hookState.getValue(POWERED);
      boolean flag3 = !World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
      boolean flag4 = !flag;
      boolean flag5 = false;
      int n2 = 0;
      IBlockState[] blockStates = new IBlockState[42];

      for (int i = 1; i < 42; i++) {
         BlockPos offset = pos.offset(value, i);
         IBlockState blockState2 = worldIn.getBlockState(offset);
         if (blockState2.getBlock() == Blocks.tripwire_hook) {
            if (blockState2.getValue(FACING) == value.getOpposite()) {
               n2 = i;
            }
            break;
         }

         if (blockState2.getBlock() != Blocks.tripwire && i != n) {
            blockStates[i] = null;
            flag4 = false;
         } else {
            if (i == n) {
               blockState2 = (IBlockState)MoreObjects.firstNonNull(blockState, blockState2);
            }

            boolean flag6 = !blockState2.getValue(BlockTripWire.DISARMED);
            boolean value4 = blockState2.getValue(BlockTripWire.POWERED);
            boolean value5 = blockState2.getValue(BlockTripWire.SUSPENDED);
            flag4 &= value5 == flag3;
            flag5 |= flag6 && value4;
            blockStates[i] = blockState2;
            if (i == n) {
               worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
               flag4 &= flag6;
            }
         }
      }

      flag4 &= n2 > 1;
      flag5 &= flag4;
      IBlockState withProperty = this.getDefaultState().withProperty(ATTACHED, flag4).withProperty(POWERED, flag5);
      if (n2 > 0) {
         BlockPos offset = pos.offset(value, n2);
         EnumFacing opposite = value.getOpposite();
         worldIn.setBlockState(offset, withProperty.withProperty(FACING, opposite), 3);
         this.updateNeighborsOnAxis(worldIn, offset, opposite);
         this.playSound(worldIn, offset, flag4, flag5, value2, value3);
      }

      this.playSound(worldIn, pos, flag4, flag5, value2, value3);
      if (!flag) {
         worldIn.setBlockState(pos, withProperty.withProperty(FACING, value), 3);
         if (flag2) {
            this.updateNeighborsOnAxis(worldIn, pos, value);
         }
      }

      if (value2 != flag4) {
         for (int i = 1; i < n2; i++) {
            BlockPos offset = pos.offset(value, i);
            IBlockState blockState2 = blockStates[i];
            if (blockState2 != null && worldIn.getBlockState(offset).getBlock() != Blocks.air) {
               worldIn.setBlockState(offset, blockState2.withProperty(ATTACHED, flag4), 3);
            }
         }
      }
   }

   @Override
   public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      this.update(worldIn, pos, state, false, true, -1, (IBlockState)null);
   }

   public void playSound(World worldIn, BlockPos pos, boolean flag, boolean flag2, boolean flag3, boolean flag4) {
      if (flag2 && !flag4) {
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, "random.click", 0.4F, 0.6F);
      } else if (!flag2 && flag4) {
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, "random.click", 0.4F, 0.5F);
      } else if (flag && !flag3) {
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, "random.click", 0.4F, 0.7F);
      } else if (!flag && flag3) {
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.1, pos.getZ() + 0.5, "random.bowhit", 0.4F, 1.2F / (worldIn.rand.nextFloat() * 0.2F + 0.9F));
      }
   }

   public void updateNeighborsOnAxis(World worldIn, BlockPos blockPos, EnumFacing enumFacing) {
      worldIn.notifyNeighborsOfStateChange(blockPos, this);
      worldIn.notifyNeighborsOfStateChange(blockPos.offset(enumFacing.getOpposite()), this);
   }

   public boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.canPlaceBlockAt(worldIn, pos)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
         return false;
      } else {
         return true;
      }
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      float f = 0.1875F;
      switch (worldIn.getBlockState(pos).getValue(FACING)) {
         case EAST:
            this.setBlockBounds(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
            break;
         case WEST:
            this.setBlockBounds(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
            break;
         case SOUTH:
            this.setBlockBounds(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
            break;
         case NORTH:
            this.setBlockBounds(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
      }
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      boolean value = state.getValue(ATTACHED);
      boolean value2 = state.getValue(POWERED);
      if (value || value2) {
         this.update(worldIn, pos, state, true, false, -1, (IBlockState)null);
      }

      if (value2) {
         worldIn.notifyNeighborsOfStateChange(pos, this);
         worldIn.notifyNeighborsOfStateChange(pos.offset(state.getValue(FACING).getOpposite()), this);
      }

      super.breakBlock(worldIn, pos, state);
   }

   @Override
   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return state.getValue(POWERED) ? 15 : 0;
   }

   @Override
   public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return !state.getValue(POWERED) ? 0 : (state.getValue(FACING) == side ? 15 : 0);
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT_MIPPED;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState()
         .withProperty(FACING, EnumFacing.getHorizontal(meta & 3))
         .withProperty(POWERED, (meta & 8) > 0)
         .withProperty(ATTACHED, (meta & 4) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int n = 0;
      n |= state.getValue(FACING).getHorizontalIndex();
      if (state.getValue(POWERED)) {
         n |= 8;
      }

      if (state.getValue(ATTACHED)) {
         n |= 4;
      }

      return n;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, POWERED, ATTACHED, SUSPENDED);
   }
}
