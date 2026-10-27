package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockTorch extends Block {
   public static PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>() {
      public boolean apply(EnumFacing enumFacing) {
         return enumFacing != EnumFacing.DOWN;
      }
   });

   public BlockTorch() {
      super(Material.circuits);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
      this.setTickRandomly(true);
      this.setCreativeTab(CreativeTabs.tabDecorations);
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

   public boolean canPlaceOn(World worldIn, BlockPos pos) {
      if (World.doesBlockHaveSolidTopSurface(worldIn, pos)) {
         return true;
      }

      Block block = worldIn.getBlockState(pos).getBlock();
      return block instanceof BlockFence || block == Blocks.glass || block == Blocks.cobblestone_wall || block == Blocks.stained_glass;
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      for (EnumFacing enumFacing : FACING.getAllowedValues()) {
         if (this.canPlaceAt(worldIn, pos, enumFacing)) {
            return true;
         }
      }

      return false;
   }

   public boolean canPlaceAt(World worldIn, BlockPos pos, EnumFacing facing) {
      BlockPos offset = pos.offset(facing.getOpposite());
      boolean isHorizontal = facing.getAxis().isHorizontal();
      return isHorizontal && worldIn.isBlockNormalCube(offset, true) || facing.equals(EnumFacing.UP) && this.canPlaceOn(worldIn, offset);
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      if (this.canPlaceAt(worldIn, pos, facing)) {
         return this.getDefaultState().withProperty(FACING, facing);
      }

      for (EnumFacing enumFacing : Plane.HORIZONTAL) {
         if (worldIn.isBlockNormalCube(pos.offset(enumFacing.getOpposite()), true)) {
            return this.getDefaultState().withProperty(FACING, enumFacing);
         }
      }

      return this.getDefaultState();
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      this.checkForDrop(worldIn, pos, state);
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      this.onNeighborChangeInternal(worldIn, pos, state);
   }

   public boolean onNeighborChangeInternal(World worldIn, BlockPos pos, IBlockState state) {
      if (!this.checkForDrop(worldIn, pos, state)) {
         return true;
      }

      EnumFacing value = state.getValue(FACING);
      Axis axis = value.getAxis();
      EnumFacing opposite = value.getOpposite();
      boolean flag = false;
      if (axis.isHorizontal() && !worldIn.isBlockNormalCube(pos.offset(opposite), true)) {
         flag = true;
      } else if (axis.isVertical() && !this.canPlaceOn(worldIn, pos.offset(opposite))) {
         flag = true;
      }

      if (flag) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
         return true;
      } else {
         return false;
      }
   }

   public boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
      if (state.getBlock() == this && this.canPlaceAt(worldIn, pos, state.getValue(FACING))) {
         return true;
      }

      if (worldIn.getBlockState(pos).getBlock() == this) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }

      return false;
   }

   @Override
   public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
      EnumFacing value = worldIn.getBlockState(pos).getValue(FACING);
      float f = 0.15F;
      if (value == EnumFacing.EAST) {
         this.setBlockBounds(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
      } else if (value == EnumFacing.WEST) {
         this.setBlockBounds(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
      } else if (value == EnumFacing.SOUTH) {
         this.setBlockBounds(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
      } else if (value == EnumFacing.NORTH) {
         this.setBlockBounds(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
      } else {
         f = 0.1F;
         this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
      }

      return super.collisionRayTrace(worldIn, pos, start, end);
   }

   @Override
   public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
      EnumFacing value = state.getValue(FACING);
      double d = pos.getX() + 0.5;
      double d2 = pos.getY() + 0.7;
      double d3 = pos.getZ() + 0.5;
      double d4 = 0.22;
      double d5 = 0.27;
      if (value.getAxis().isHorizontal()) {
         EnumFacing opposite = value.getOpposite();
         worldIn.spawnParticle(
            EnumParticleTypes.SMOKE_NORMAL, d + d5 * opposite.getFrontOffsetX(), d2 + d4, d3 + d5 * opposite.getFrontOffsetZ(), 0.0, 0.0, 0.0
         );
         worldIn.spawnParticle(
            EnumParticleTypes.FLAME, d + d5 * opposite.getFrontOffsetX(), d2 + d4, d3 + d5 * opposite.getFrontOffsetZ(), 0.0, 0.0, 0.0
         );
      } else {
         worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d, d2, d3, 0.0, 0.0, 0.0);
         worldIn.spawnParticle(EnumParticleTypes.FLAME, d, d2, d3, 0.0, 0.0, 0.0);
      }
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      IBlockState defaultState = this.getDefaultState();
      switch (meta) {
         case 1:
            defaultState = defaultState.withProperty(FACING, EnumFacing.EAST);
            break;
         case 2:
            defaultState = defaultState.withProperty(FACING, EnumFacing.WEST);
            break;
         case 3:
            defaultState = defaultState.withProperty(FACING, EnumFacing.SOUTH);
            break;
         case 4:
            defaultState = defaultState.withProperty(FACING, EnumFacing.NORTH);
            break;
         case 5:
         default:
            defaultState = defaultState.withProperty(FACING, EnumFacing.UP);
      }

      return defaultState;
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int n = 0;
      switch (state.getValue(FACING)) {
         case EAST:
            n |= 1;
            break;
         case WEST:
            n |= 2;
            break;
         case SOUTH:
            n |= 3;
            break;
         case NORTH:
            n |= 4;
            break;
         case DOWN:
         case UP:
         default:
            n |= 5;
      }

      return n;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING);
   }
}
