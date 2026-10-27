package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class BlockRailPowered extends BlockRailBase {
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

   public BlockRailPowered() {
      super(true);
      this.setDefaultState(this.blockState.getBaseState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH).withProperty(POWERED, false));
   }

   public boolean isPoweredByOtherRails(World worldIn, BlockPos pos, IBlockState state, boolean flag, int n) {
      if (n >= 8) {
         return false;
      }

      int n2 = pos.getX();
      int n3 = pos.getY();
      int n4 = pos.getZ();
      boolean flag2 = true;
      BlockRailBase.EnumRailDirection value = state.getValue(SHAPE);
      switch (value) {
         case NORTH_SOUTH:
            if (flag) {
               n4++;
            } else {
               n4--;
            }
            break;
         case EAST_WEST:
            if (flag) {
               n2--;
            } else {
               n2++;
            }
            break;
         case ASCENDING_EAST:
            if (flag) {
               n2--;
            } else {
               n2++;
               n3++;
               flag2 = false;
            }

            value = BlockRailBase.EnumRailDirection.EAST_WEST;
            break;
         case ASCENDING_WEST:
            if (flag) {
               n2--;
               n3++;
               flag2 = false;
            } else {
               n2++;
            }

            value = BlockRailBase.EnumRailDirection.EAST_WEST;
            break;
         case ASCENDING_NORTH:
            if (flag) {
               n4++;
            } else {
               n4--;
               n3++;
               flag2 = false;
            }

            value = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            break;
         case ASCENDING_SOUTH:
            if (flag) {
               n4++;
               n3++;
               flag2 = false;
            } else {
               n4--;
            }

            value = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
      }

      return this.canBePowered(worldIn, new BlockPos(n2, n3, n4), flag, n, value)
         ? true
         : flag2 && this.canBePowered(worldIn, new BlockPos(n2, n3 - 1, n4), flag, n, value);
   }

   public boolean canBePowered(World worldIn, BlockPos blockPos, boolean flag, int distance, BlockRailBase.EnumRailDirection enumRailDirection) {
      IBlockState blockState = worldIn.getBlockState(blockPos);
      if (blockState.getBlock() != this) {
         return false;
      }

      BlockRailBase.EnumRailDirection value = blockState.getValue(SHAPE);
      return enumRailDirection != BlockRailBase.EnumRailDirection.EAST_WEST
            || value != BlockRailBase.EnumRailDirection.NORTH_SOUTH
               && value != BlockRailBase.EnumRailDirection.ASCENDING_NORTH
               && value != BlockRailBase.EnumRailDirection.ASCENDING_SOUTH
         ? (
            enumRailDirection != BlockRailBase.EnumRailDirection.NORTH_SOUTH
                  || value != BlockRailBase.EnumRailDirection.EAST_WEST
                     && value != BlockRailBase.EnumRailDirection.ASCENDING_EAST
                     && value != BlockRailBase.EnumRailDirection.ASCENDING_WEST
               ? (blockState.getValue(POWERED) ? (worldIn.isBlockPowered(blockPos) ? true : this.isPoweredByOtherRails(worldIn, blockPos, blockState, flag, distance + 1)) : false)
               : false
         )
         : false;
   }

   @Override
   public void onNeighborChangedInternal(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      boolean value = state.getValue(POWERED);
      boolean flag = worldIn.isBlockPowered(pos)
         || this.isPoweredByOtherRails(worldIn, pos, state, true, 0)
         || this.isPoweredByOtherRails(worldIn, pos, state, false, 0);
      if (flag != value) {
         worldIn.setBlockState(pos, state.withProperty(POWERED, flag), 3);
         worldIn.notifyNeighborsOfStateChange(pos.down(), this);
         if (state.getValue(SHAPE).isAscending()) {
            worldIn.notifyNeighborsOfStateChange(pos.up(), this);
         }
      }
   }

   @Override
   public IProperty<BlockRailBase.EnumRailDirection> getShapeProperty() {
      return SHAPE;
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
