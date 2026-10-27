package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLever extends Block {
   public static PropertyEnum<BlockLever.EnumOrientation> FACING = PropertyEnum.create("facing", BlockLever.EnumOrientation.class);
   public static PropertyBool POWERED = PropertyBool.create("powered");

   public BlockLever() {
      super(Material.circuits);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, BlockLever.EnumOrientation.NORTH).withProperty(POWERED, false));
      this.setCreativeTab(CreativeTabs.tabRedstone);
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
      return canHoldLever(worldIn, pos, side.getOpposite());
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      for (EnumFacing enumFacing : EnumFacing.values()) {
         if (canHoldLever(worldIn, pos, enumFacing)) {
            return true;
         }
      }

      return false;
   }

   public static boolean canHoldLever(World world, BlockPos blockPos, EnumFacing enumFacing) {
      return BlockButton.canHoldButton(world, blockPos, enumFacing);
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      IBlockState withProperty = this.getDefaultState().withProperty(POWERED, false);
      if (canHoldLever(worldIn, pos, facing.getOpposite())) {
         return withProperty.withProperty(FACING, BlockLever.EnumOrientation.forFacings(facing, placer.getHorizontalFacing()));
      }

      for (EnumFacing enumFacing : Plane.HORIZONTAL) {
         if (enumFacing != facing && canHoldLever(worldIn, pos, enumFacing.getOpposite())) {
            return withProperty.withProperty(FACING, BlockLever.EnumOrientation.forFacings(enumFacing, placer.getHorizontalFacing()));
         }
      }

      return World.doesBlockHaveSolidTopSurface(worldIn, pos.down())
         ? withProperty.withProperty(FACING, BlockLever.EnumOrientation.forFacings(EnumFacing.UP, placer.getHorizontalFacing()))
         : withProperty;
   }

   public static int getMetadataForFacing(EnumFacing facing) {
      switch (facing) {
         case DOWN:
            return 0;
         case UP:
            return 5;
         case NORTH:
            return 4;
         case SOUTH:
            return 3;
         case WEST:
            return 2;
         case EAST:
            return 1;
         default:
            return -1;
      }
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (this.placeLever(worldIn, pos, state) && !canHoldLever(worldIn, pos, state.getValue(FACING).getFacing().getOpposite())) {
         this.dropBlockAsItem(worldIn, pos, state, 0);
         worldIn.setBlockToAir(pos);
      }
   }

   public boolean placeLever(World world, BlockPos blockPos, IBlockState blockState) {
      if (this.canPlaceBlockAt(world, blockPos)) {
         return true;
      }

      this.dropBlockAsItem(world, blockPos, blockState, 0);
      world.setBlockToAir(blockPos);
      return false;
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
            break;
         case UP_Z:
         case UP_X:
            f = 0.25F;
            this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
            break;
         case DOWN_X:
         case DOWN_Z:
            f = 0.25F;
            this.setBlockBounds(0.5F - f, 0.4F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
      }
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (worldIn.isRemote) {
         return true;
      }

      state = state.cycleProperty(POWERED);
      worldIn.setBlockState(pos, state, 3);
      worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, "random.click", 0.3F, state.getValue(POWERED) ? 0.6F : 0.5F);
      worldIn.notifyNeighborsOfStateChange(pos, this);
      EnumFacing facing = state.getValue(FACING).getFacing();
      worldIn.notifyNeighborsOfStateChange(pos.offset(facing.getOpposite()), this);
      return true;
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      if (state.getValue(POWERED)) {
         worldIn.notifyNeighborsOfStateChange(pos, this);
         EnumFacing facing = state.getValue(FACING).getFacing();
         worldIn.notifyNeighborsOfStateChange(pos.offset(facing.getOpposite()), this);
      }

      super.breakBlock(worldIn, pos, state);
   }

   @Override
   public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return state.getValue(POWERED) ? 15 : 0;
   }

   @Override
   public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
      return !state.getValue(POWERED) ? 0 : (state.getValue(FACING).getFacing() == side ? 15 : 0);
   }

   @Override
   public boolean canProvidePower() {
      return true;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, BlockLever.EnumOrientation.byMetadata(meta & 7)).withProperty(POWERED, (meta & 8) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int n = 0;
      n |= state.getValue(FACING).getMetadata();
      if (state.getValue(POWERED)) {
         n |= 8;
      }

      return n;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, POWERED);
   }

   public enum EnumOrientation implements IStringSerializable {
      DOWN_X(0, "down_x", EnumFacing.DOWN),
      EAST(1, "east", EnumFacing.EAST),
      WEST(2, "west", EnumFacing.WEST),
      SOUTH(3, "south", EnumFacing.SOUTH),
      NORTH(4, "north", EnumFacing.NORTH),
      UP_Z(5, "up_z", EnumFacing.UP),
      UP_X(6, "up_x", EnumFacing.UP),
      DOWN_Z(7, "down_z", EnumFacing.DOWN);

      public int meta;
      public String name;
      public EnumFacing facing;
      public static BlockLever.EnumOrientation[] META_LOOKUP = new BlockLever.EnumOrientation[values().length];

      EnumOrientation(int meta, String name, EnumFacing facing) {
         this.meta = meta;
         this.name = name;
         this.facing = facing;
      }

      public int getMetadata() {
         return this.meta;
      }

      public EnumFacing getFacing() {
         return this.facing;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public static BlockLever.EnumOrientation byMetadata(int meta) {
         if (meta < 0 || meta >= META_LOOKUP.length) {
            meta = 0;
         }

         return META_LOOKUP[meta];
      }

      public static BlockLever.EnumOrientation forFacings(EnumFacing clickedSide, EnumFacing entityFacing) {
         switch (clickedSide) {
            case DOWN:
               switch (entityFacing.getAxis()) {
                  case X:
                     return DOWN_X;
                  case Z:
                     return DOWN_Z;
                  default:
                     throw new IllegalArgumentException("Invalid entityFacing " + entityFacing + " for facing " + clickedSide);
               }
            case UP:
               switch (entityFacing.getAxis()) {
                  case X:
                     return UP_X;
                  case Z:
                     return UP_Z;
                  default:
                     throw new IllegalArgumentException("Invalid entityFacing " + entityFacing + " for facing " + clickedSide);
               }
            case NORTH:
               return NORTH;
            case SOUTH:
               return SOUTH;
            case WEST:
               return WEST;
            case EAST:
               return EAST;
            default:
               throw new IllegalArgumentException("Invalid facing: " + clickedSide);
         }
      }

      @Override
      public String getName() {
         return this.name;
      }

      static {
         for (BlockLever.EnumOrientation enumOrientation : values()) {
            META_LOOKUP[enumOrientation.getMetadata()] = enumOrientation;
         }
      }
   }
}
