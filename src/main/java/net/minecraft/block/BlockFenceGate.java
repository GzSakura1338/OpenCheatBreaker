package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFenceGate extends BlockDirectional {
   public static PropertyBool OPEN = PropertyBool.create("open");
   public static PropertyBool POWERED = PropertyBool.create("powered");
   public static PropertyBool IN_WALL = PropertyBool.create("in_wall");

   public BlockFenceGate(BlockPlanks.EnumType enumType) {
      super(Material.wood, enumType.getMapColor());
      this.setDefaultState(this.blockState.getBaseState().withProperty(OPEN, false).withProperty(POWERED, false).withProperty(IN_WALL, false));
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      Axis axis = state.getValue(FACING).getAxis();
      if (axis == Axis.Z
            && (
               worldIn.getBlockState(pos.west()).getBlock() == Blocks.cobblestone_wall
                  || worldIn.getBlockState(pos.east()).getBlock() == Blocks.cobblestone_wall
            )
         || axis == Axis.X
            && (
               worldIn.getBlockState(pos.north()).getBlock() == Blocks.cobblestone_wall
                  || worldIn.getBlockState(pos.south()).getBlock() == Blocks.cobblestone_wall
            )) {
         state = state.withProperty(IN_WALL, true);
      }

      return state;
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos.down()).getBlock().getMaterial().isSolid() ? super.canPlaceBlockAt(worldIn, pos) : false;
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      if (state.getValue(OPEN)) {
         return null;
      }

      Axis axis = state.getValue(FACING).getAxis();
      return axis == Axis.Z
         ? new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ() + 0.375F, pos.getX() + 1, pos.getY() + 1.5F, pos.getZ() + 0.625F)
         : new AxisAlignedBB(pos.getX() + 0.375F, pos.getY(), pos.getZ(), pos.getX() + 0.625F, pos.getY() + 1.5F, pos.getZ() + 1);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      Axis axis = worldIn.getBlockState(pos).getValue(FACING).getAxis();
      if (axis == Axis.Z) {
         this.setBlockBounds(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
      } else {
         this.setBlockBounds(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
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
   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return worldIn.getBlockState(pos).getValue(OPEN);
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState()
         .withProperty(FACING, placer.getHorizontalFacing())
         .withProperty(OPEN, false)
         .withProperty(POWERED, false)
         .withProperty(IN_WALL, false);
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (state.getValue(OPEN)) {
         state = state.withProperty(OPEN, false);
         worldIn.setBlockState(pos, state, 2);
      } else {
         EnumFacing fromAngle = EnumFacing.fromAngle(playerIn.rotationYaw);
         if (state.getValue(FACING) == fromAngle.getOpposite()) {
            state = state.withProperty(FACING, fromAngle);
         }

         state = state.withProperty(OPEN, true);
         worldIn.setBlockState(pos, state, 2);
      }

      worldIn.playAuxSFXAtEntity(playerIn, state.getValue(OPEN) ? 1003 : 1006, pos, 0);
      return true;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!worldIn.isRemote) {
         boolean isBlockPowered = worldIn.isBlockPowered(pos);
         if (isBlockPowered || neighborBlock.canProvidePower()) {
            if (isBlockPowered && !state.getValue(OPEN) && !state.getValue(POWERED)) {
               worldIn.setBlockState(pos, state.withProperty(OPEN, true).withProperty(POWERED, true), 2);
               worldIn.playAuxSFXAtEntity((EntityPlayer)null, 1003, pos, 0);
            } else if (!isBlockPowered && state.getValue(OPEN) && state.getValue(POWERED)) {
               worldIn.setBlockState(pos, state.withProperty(OPEN, false).withProperty(POWERED, false), 2);
               worldIn.playAuxSFXAtEntity((EntityPlayer)null, 1006, pos, 0);
            } else if (isBlockPowered != state.getValue(POWERED)) {
               worldIn.setBlockState(pos, state.withProperty(POWERED, isBlockPowered), 2);
            }
         }
      }
   }

   @Override
   public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
      return true;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState()
         .withProperty(FACING, EnumFacing.getHorizontal(meta))
         .withProperty(OPEN, (meta & 4) != 0)
         .withProperty(POWERED, (meta & 8) != 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int n = 0;
      n |= state.getValue(FACING).getHorizontalIndex();
      if (state.getValue(POWERED)) {
         n |= 8;
      }

      if (state.getValue(OPEN)) {
         n |= 4;
      }

      return n;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, OPEN, POWERED, IN_WALL);
   }
}
