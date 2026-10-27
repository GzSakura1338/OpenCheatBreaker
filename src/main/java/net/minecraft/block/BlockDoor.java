package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDoor extends Block {
   public static PropertyDirection FACING = PropertyDirection.create("facing", Plane.HORIZONTAL);
   public static PropertyBool OPEN = PropertyBool.create("open");
   public static PropertyEnum<BlockDoor.EnumHingePosition> HINGE = PropertyEnum.create("hinge", BlockDoor.EnumHingePosition.class);
   public static PropertyBool POWERED = PropertyBool.create("powered");
   public static PropertyEnum<BlockDoor.EnumDoorHalf> HALF = PropertyEnum.create("half", BlockDoor.EnumDoorHalf.class);

   public BlockDoor(Material materialIn) {
      super(materialIn);
      this.setDefaultState(
         this.blockState
            .getBaseState()
            .withProperty(FACING, EnumFacing.NORTH)
            .withProperty(OPEN, false)
            .withProperty(HINGE, BlockDoor.EnumHingePosition.LEFT)
            .withProperty(POWERED, false)
            .withProperty(HALF, BlockDoor.EnumDoorHalf.LOWER)
      );
   }

   @Override
   public String getLocalizedName() {
      return StatCollector.translateToLocal((this.getUnlocalizedName() + ".name").replaceAll("tile", "item"));
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public boolean isPassable(IBlockAccess worldIn, BlockPos pos) {
      return isOpen(combineMetadata(worldIn, pos));
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public AxisAlignedBB getSelectedBoundingBox(World worldIn, BlockPos pos) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.getSelectedBoundingBox(worldIn, pos);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.getCollisionBoundingBox(worldIn, pos, state);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      this.setBoundBasedOnMeta(combineMetadata(worldIn, pos));
   }

   public void setBoundBasedOnMeta(int combinedMeta) {
      float f = 0.1875F;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
      EnumFacing facing = getFacing(combinedMeta);
      boolean isOpen2 = isOpen(combinedMeta);
      boolean isHingeLeft2 = isHingeLeft(combinedMeta);
      if (isOpen2) {
         if (facing == EnumFacing.EAST) {
            if (!isHingeLeft2) {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            } else {
               this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            }
         } else if (facing == EnumFacing.SOUTH) {
            if (!isHingeLeft2) {
               this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            }
         } else if (facing == EnumFacing.WEST) {
            if (!isHingeLeft2) {
               this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            } else {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            }
         } else if (facing == EnumFacing.NORTH) {
            if (!isHingeLeft2) {
               this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            } else {
               this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      } else if (facing == EnumFacing.EAST) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
      } else if (facing == EnumFacing.SOUTH) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
      } else if (facing == EnumFacing.WEST) {
         this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      } else if (facing == EnumFacing.NORTH) {
         this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
      }
   }

   @Override
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (this.blockMaterial == Material.iron) {
         return true;
      }

      BlockPos blockPos = state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
      IBlockState blockState = pos.equals(blockPos) ? state : worldIn.getBlockState(blockPos);
      if (blockState.getBlock() != this) {
         return false;
      }

      state = blockState.cycleProperty(OPEN);
      worldIn.setBlockState(blockPos, state, 2);
      worldIn.markBlockRangeForRenderUpdate(blockPos, pos);
      worldIn.playAuxSFXAtEntity(playerIn, state.getValue(OPEN) ? 1003 : 1006, pos, 0);
      return true;
   }

   public void toggleDoor(World worldIn, BlockPos pos, boolean open) {
      IBlockState blockState = worldIn.getBlockState(pos);
      if (blockState.getBlock() == this) {
         BlockPos blockPos = blockState.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER ? pos : pos.down();
         IBlockState blockState2 = pos == blockPos ? blockState : worldIn.getBlockState(blockPos);
         if (blockState2.getBlock() == this && blockState2.getValue(OPEN) != open) {
            worldIn.setBlockState(blockPos, blockState2.withProperty(OPEN, open), 2);
            worldIn.markBlockRangeForRenderUpdate(blockPos, pos);
            worldIn.playAuxSFXAtEntity((EntityPlayer)null, open ? 1003 : 1006, pos, 0);
         }
      }
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
         BlockPos down = pos.down();
         IBlockState blockState = worldIn.getBlockState(down);
         if (blockState.getBlock() != this) {
            worldIn.setBlockToAir(pos);
         } else if (neighborBlock != this) {
            this.onNeighborBlockChange(worldIn, down, blockState, neighborBlock);
         }
      } else {
         boolean flag = false;
         BlockPos up = pos.up();
         IBlockState blockState = worldIn.getBlockState(up);
         if (blockState.getBlock() != this) {
            worldIn.setBlockToAir(pos);
            flag = true;
         }

         if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down())) {
            worldIn.setBlockToAir(pos);
            flag = true;
            if (blockState.getBlock() == this) {
               worldIn.setBlockToAir(up);
            }
         }

         if (flag) {
            if (!worldIn.isRemote) {
               this.dropBlockAsItem(worldIn, pos, state, 0);
            }
         } else {
            boolean flag2 = worldIn.isBlockPowered(pos) || worldIn.isBlockPowered(up);
            if ((flag2 || neighborBlock.canProvidePower()) && neighborBlock != this && flag2 != blockState.getValue(POWERED)) {
               worldIn.setBlockState(up, blockState.withProperty(POWERED, flag2), 2);
               if (flag2 != state.getValue(OPEN)) {
                  worldIn.setBlockState(pos, state.withProperty(OPEN, flag2), 2);
                  worldIn.markBlockRangeForRenderUpdate(pos, pos);
                  worldIn.playAuxSFXAtEntity((EntityPlayer)null, flag2 ? 1003 : 1006, pos, 0);
               }
            }
         }
      }
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER ? null : this.getItem();
   }

   @Override
   public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.collisionRayTrace(worldIn, pos, start, end);
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return pos.getY() >= 255
         ? false
         : World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) && super.canPlaceBlockAt(worldIn, pos) && super.canPlaceBlockAt(worldIn, pos.up());
   }

   @Override
   public int getMobilityFlag() {
      return 1;
   }

   public static int combineMetadata(IBlockAccess worldIn, BlockPos pos) {
      IBlockState blockState = worldIn.getBlockState(pos);
      int metaFromState = blockState.getBlock().getMetaFromState(blockState);
      boolean isTop2 = isTop(metaFromState);
      IBlockState blockState2 = worldIn.getBlockState(pos.down());
      int metaFromState2 = blockState2.getBlock().getMetaFromState(blockState2);
      int n = isTop2 ? metaFromState2 : metaFromState;
      IBlockState blockState3 = worldIn.getBlockState(pos.up());
      int metaFromState3 = blockState3.getBlock().getMetaFromState(blockState3);
      int n2 = isTop2 ? metaFromState : metaFromState3;
      boolean flag = (n2 & 1) != 0;
      boolean flag2 = (n2 & 2) != 0;
      return removeHalfBit(n) | (isTop2 ? 8 : 0) | (flag ? 16 : 0) | (flag2 ? 32 : 0);
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return this.getItem();
   }

   public Item getItem() {
      return this == Blocks.iron_door
         ? Items.iron_door
         : (
            this == Blocks.spruce_door
               ? Items.spruce_door
               : (
                  this == Blocks.birch_door
                     ? Items.birch_door
                     : (
                        this == Blocks.jungle_door
                           ? Items.jungle_door
                           : (this == Blocks.acacia_door ? Items.acacia_door : (this == Blocks.dark_oak_door ? Items.dark_oak_door : Items.oak_door))
                     )
               )
         );
   }

   @Override
   public void onBlockHarvested(World worldIn, BlockPos pos, IBlockState state, EntityPlayer player) {
      BlockPos down = pos.down();
      if (player.capabilities.isCreativeMode && state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER && worldIn.getBlockState(down).getBlock() == this) {
         worldIn.setBlockToAir(down);
      }
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
      if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.LOWER) {
         IBlockState blockState = worldIn.getBlockState(pos.up());
         if (blockState.getBlock() == this) {
            state = state.withProperty(HINGE, blockState.getValue(HINGE)).withProperty(POWERED, blockState.getValue(POWERED));
         }
      } else {
         IBlockState blockState = worldIn.getBlockState(pos.down());
         if (blockState.getBlock() == this) {
            state = state.withProperty(FACING, blockState.getValue(FACING)).withProperty(OPEN, blockState.getValue(OPEN));
         }
      }

      return state;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return (meta & 8) > 0
         ? this.getDefaultState()
            .withProperty(HALF, BlockDoor.EnumDoorHalf.UPPER)
            .withProperty(HINGE, (meta & 1) > 0 ? BlockDoor.EnumHingePosition.RIGHT : BlockDoor.EnumHingePosition.LEFT)
            .withProperty(POWERED, (meta & 2) > 0)
         : this.getDefaultState()
            .withProperty(HALF, BlockDoor.EnumDoorHalf.LOWER)
            .withProperty(FACING, EnumFacing.getHorizontal(meta & 3).rotateYCCW())
            .withProperty(OPEN, (meta & 4) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int n = 0;
      if (state.getValue(HALF) == BlockDoor.EnumDoorHalf.UPPER) {
         n |= 8;
         if (state.getValue(HINGE) == BlockDoor.EnumHingePosition.RIGHT) {
            n |= 1;
         }

         if (state.getValue(POWERED)) {
            n |= 2;
         }
      } else {
         n |= state.getValue(FACING).rotateY().getHorizontalIndex();
         if (state.getValue(OPEN)) {
            n |= 4;
         }
      }

      return n;
   }

   public static int removeHalfBit(int meta) {
      return meta & 7;
   }

   public static boolean isOpen(IBlockAccess worldIn, BlockPos pos) {
      return isOpen(combineMetadata(worldIn, pos));
   }

   public static EnumFacing getFacing(IBlockAccess worldIn, BlockPos pos) {
      return getFacing(combineMetadata(worldIn, pos));
   }

   public static EnumFacing getFacing(int combinedMeta) {
      return EnumFacing.getHorizontal(combinedMeta & 3).rotateYCCW();
   }

   public static boolean isOpen(int combinedMeta) {
      return (combinedMeta & 4) != 0;
   }

   public static boolean isTop(int meta) {
      return (meta & 8) != 0;
   }

   public static boolean isHingeLeft(int combinedMeta) {
      return (combinedMeta & 16) != 0;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, HALF, FACING, OPEN, HINGE, POWERED);
   }

   public enum EnumDoorHalf implements IStringSerializable {
      UPPER,
      LOWER;

      @Override
      public String toString() {
         return this.getName();
      }

      @Override
      public String getName() {
         return this == UPPER ? "upper" : "lower";
      }
   }

   public enum EnumHingePosition implements IStringSerializable {
      LEFT,
      RIGHT;

      @Override
      public String toString() {
         return this.getName();
      }

      @Override
      public String getName() {
         return this == LEFT ? "left" : "right";
      }
   }
}
