package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLadder extends Block {
   public static PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);

   public BlockLadder() {
      super(Material.circuits);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
      this.setCreativeTab(CreativeTabs.tabDecorations);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.getCollisionBoundingBox(worldIn, pos, state);
   }

   @Override
   public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.getSelectedBoundingBox(worldIn, pos);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      IBlockState blockState = worldIn.getBlockState(pos);
      if (blockState.getBlock() == this) {
         float f = 0.125F;
         switch (blockState.getValue(FACING)) {
            case NORTH:
               this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
               break;
            case SOUTH:
               this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
               break;
            case WEST:
               this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
               break;
            case EAST:
            default:
               this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
         }
      }
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
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.west()).getBlock().isNormalCube()
         ? true
         : (
            worldIn.getBlockState(pos.east()).getBlock().isNormalCube()
               ? true
               : (worldIn.getBlockState(pos.north()).getBlock().isNormalCube() ? true : worldIn.getBlockState(pos.south()).getBlock().isNormalCube())
         );
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      if (facing.getAxis().isHorizontal() && this.canBlockStay(worldIn, pos, facing)) {
         return this.getDefaultState().withProperty(FACING, facing);
      }

      for (EnumFacing enumFacing : Plane.HORIZONTAL) {
         if (this.canBlockStay(worldIn, pos, enumFacing)) {
            return this.getDefaultState().withProperty(FACING, enumFacing);
         }
      }

      return this.getDefaultState();
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      EnumFacing value = state.getValue(FACING);
      if (!this.canBlockStay(worldIn, pos, value)) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }

      super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
   }

   public boolean canBlockStay(World worldIn, BlockPos pos, EnumFacing facing) {
      return worldIn.getBlockState(pos.offset(facing.getOpposite())).getBlock().isNormalCube();
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
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
