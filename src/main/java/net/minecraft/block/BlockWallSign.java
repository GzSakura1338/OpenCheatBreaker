package net.minecraft.block;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockWallSign extends BlockSign {
   public static PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);

   public BlockWallSign() {
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      EnumFacing value = worldIn.getBlockState(pos).getValue(FACING);
      float f = 0.28125F;
      float f2 = 0.78125F;
      float f3 = 0.0F;
      float f4 = 1.0F;
      float f5 = 0.125F;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      switch (value) {
         case NORTH:
            this.setBlockBounds(f3, f, 1.0F - f5, f4, f2, 1.0F);
            break;
         case SOUTH:
            this.setBlockBounds(f3, f, 0.0F, f4, f2, f5);
            break;
         case WEST:
            this.setBlockBounds(1.0F - f5, f, f3, 1.0F, f2, f4);
            break;
         case EAST:
            this.setBlockBounds(0.0F, f, f3, f5, f2, f4);
      }
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      EnumFacing value = state.getValue(FACING);
      if (!worldIn.getBlockState(pos.offset(value.getOpposite())).getBlock().getMaterial().isSolid()) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }

      super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
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
