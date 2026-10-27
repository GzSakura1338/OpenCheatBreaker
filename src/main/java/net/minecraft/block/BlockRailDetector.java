package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.List;
import java.util.Random;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRailDetector extends BlockRailBase {
   public static PropertyEnum<BlockRailBase.EnumRailDirection> SHAPE = PropertyEnum.create(
      "shape",
      BlockRailBase.EnumRailDirection.class,
      new Predicate<BlockRailBase.EnumRailDirection>() {
         public boolean apply(BlockRailBase.EnumRailDirection enumRailDirection) {
            return enumRailDirection != BlockRailBase.EnumRailDirection.NORTH_EAST
               && enumRailDirection != BlockRailBase.EnumRailDirection.NORTH_WEST
               && enumRailDirection != BlockRailBase.EnumRailDirection.SOUTH_EAST
               && enumRailDirection != BlockRailBase.EnumRailDirection.SOUTH_WEST;
         }
      }
   );
   public static PropertyBool POWERED = PropertyBool.create("powered");

   public BlockRailDetector() {
      super(true);
      this.setDefaultState(this.blockState.getBaseState().withProperty(POWERED, false).withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH));
      this.setTickRandomly(true);
   }

   @Override
   public int tickRate(World worldIn) {
      return 20;
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   @Override
   public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
      if (!worldIn.isRemote && !state.getValue(POWERED)) {
         this.updatePoweredState(worldIn, pos, state);
      }
   }

   @Override
   public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
   }

   @Override
   public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      if (!worldIn.isRemote && state.getValue(POWERED)) {
         this.updatePoweredState(worldIn, pos, state);
      }
   }

   @Override
   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return state.getValue(POWERED) ? 15 : 0;
   }

   @Override
   public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return !state.getValue(POWERED) ? 0 : (side == EnumFacing.UP ? 15 : 0);
   }

   public void updatePoweredState(World worldIn, BlockPos pos, IBlockState state) {
      boolean value = state.getValue(POWERED);
      boolean flag = false;
      List findMinecarts = this.findMinecarts(worldIn, pos, EntityMinecart.class);
      if (!findMinecarts.isEmpty()) {
         flag = true;
      }

      if (flag && !value) {
         worldIn.setBlockState(pos, state.withProperty(POWERED, true), 3);
         worldIn.notifyNeighborsOfStateChange(pos, this);
         worldIn.notifyNeighborsOfStateChange(pos.down(), this);
         worldIn.markBlockRangeForRenderUpdate(pos, pos);
      }

      if (!flag && value) {
         worldIn.setBlockState(pos, state.withProperty(POWERED, false), 3);
         worldIn.notifyNeighborsOfStateChange(pos, this);
         worldIn.notifyNeighborsOfStateChange(pos.down(), this);
         worldIn.markBlockRangeForRenderUpdate(pos, pos);
      }

      if (flag) {
         worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
      }

      worldIn.updateComparatorOutputLevel(pos, this);
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      super.onBlockAdded(worldIn, pos, state);
      this.updatePoweredState(worldIn, pos, state);
   }

   @Override
   public IProperty<BlockRailBase.EnumRailDirection> getShapeProperty() {
      return SHAPE;
   }

   @Override
   public boolean hasComparatorInputOverride() {
      return true;
   }

   @Override
   public int getComparatorInputOverride(World worldIn, BlockPos pos) {
      if (worldIn.getBlockState(pos).getValue(POWERED)) {
         List findMinecarts = this.findMinecarts(worldIn, pos, EntityMinecartCommandBlock.class);
         if (!findMinecarts.isEmpty()) {
            return ((EntityMinecartCommandBlock)findMinecarts.get(0)).getCommandBlockLogic().getSuccessCount();
         }

         List findMinecarts2 = this.findMinecarts(worldIn, pos, EntityMinecart.class, EntitySelectors.selectInventories);
         if (!findMinecarts2.isEmpty()) {
            return Container.calcRedstoneFromInventory((IInventory)findMinecarts2.get(0));
         }
      }

      return 0;
   }

   public <T extends EntityMinecart> List<T> findMinecarts(World worldIn, BlockPos pos, Class<T> clazz, Predicate<Entity>... filter) {
      AxisAlignedBB dectectionBox = this.getDectectionBox(pos);
      return filter.length != 1 ? worldIn.getEntitiesWithinAABB(clazz, dectectionBox) : worldIn.getEntitiesWithinAABB(clazz, dectectionBox, filter[0]);
   }

   public AxisAlignedBB getDectectionBox(BlockPos pos) {
      float f = 0.2F;
      return new AxisAlignedBB(pos.getX() + 0.2F, pos.getY(), pos.getZ() + 0.2F, pos.getX() + 1 - 0.2F, pos.getY() + 1 - 0.2F, pos.getZ() + 1 - 0.2F);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.byMetadata(meta & 7)).withProperty(POWERED, (meta & 8) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int n = 0;
      n |= state.getValue(SHAPE).getMetadata();
      if (state.getValue(POWERED)) {
         n |= 8;
      }

      return n;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, SHAPE, POWERED);
   }
}
