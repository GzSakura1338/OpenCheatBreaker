package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRailBase extends Block {
   public boolean isPowered;

   public static boolean isRailBlock(World worldIn, BlockPos pos) {
      return isRailBlock(worldIn.getBlockState(pos));
   }

   public static boolean isRailBlock(IBlockState state) {
      Block block = state.getBlock();
      return block == Blocks.rail || block == Blocks.golden_rail || block == Blocks.detector_rail || block == Blocks.activator_rail;
   }

   public BlockRailBase(boolean isPowered) {
      super(Material.circuits);
      this.isPowered = isPowered;
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
      this.setCreativeTab(CreativeTabs.tabTransport);
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
   public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.collisionRayTrace(worldIn, pos, start, end);
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      IBlockState blockState = worldIn.getBlockState(pos);
      BlockRailBase.EnumRailDirection enumRailDirection = blockState.getBlock() == this ? blockState.getValue(this.getShapeProperty()) : null;
      if (enumRailDirection != null && enumRailDirection.isAscending()) {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.625F, 1.0F);
      } else {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
      }
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isRemote) {
         state = this.updateBlockState(worldIn, pos, state, true);
         if (this.isPowered) {
            this.onNeighborBlockChange(worldIn, pos, state, this);
         }
      }
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!worldIn.isRemote) {
         BlockRailBase.EnumRailDirection value = state.getValue(this.getShapeProperty());
         boolean flag = false;
         if (!World.doesBlockHaveSolidTopSurface(worldIn, pos.down())) {
            flag = true;
         }

         if (value == BlockRailBase.EnumRailDirection.ASCENDING_EAST && !World.doesBlockHaveSolidTopSurface(worldIn, pos.east())) {
            flag = true;
         } else if (value == BlockRailBase.EnumRailDirection.ASCENDING_WEST && !World.doesBlockHaveSolidTopSurface(worldIn, pos.west())) {
            flag = true;
         } else if (value == BlockRailBase.EnumRailDirection.ASCENDING_NORTH && !World.doesBlockHaveSolidTopSurface(worldIn, pos.north())) {
            flag = true;
         } else if (value == BlockRailBase.EnumRailDirection.ASCENDING_SOUTH && !World.doesBlockHaveSolidTopSurface(worldIn, pos.south())) {
            flag = true;
         }

         if (flag) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
         } else {
            this.onNeighborChangedInternal(worldIn, pos, state, neighborBlock);
         }
      }
   }

   public void onNeighborChangedInternal(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
   }

   public IBlockState updateBlockState(World worldIn, BlockPos blockPos, IBlockState blockState, boolean flag) {
      return worldIn.isRemote ? blockState : new BlockRailBase.Rail(worldIn, blockPos, blockState).getRealignedHelper(worldIn.isBlockPowered(blockPos), flag).getBlockState();
   }

   @Override
   public int getMobilityFlag() {
      return 0;
   }

   @Override
   public EnumWorldBlockLayer getBlockLayer() {
      return EnumWorldBlockLayer.CUTOUT;
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      super.breakBlock(worldIn, pos, state);
      if (state.getValue(this.getShapeProperty()).isAscending()) {
         worldIn.notifyNeighborsOfStateChange(pos.up(), this);
      }

      if (this.isPowered) {
         worldIn.notifyNeighborsOfStateChange(pos, this);
         worldIn.notifyNeighborsOfStateChange(pos.down(), this);
      }
   }

   public abstract IProperty<BlockRailBase.EnumRailDirection> getShapeProperty();

   public enum EnumRailDirection implements IStringSerializable {
      NORTH_SOUTH(0, "north_south"),
      EAST_WEST(1, "east_west"),
      ASCENDING_EAST(2, "ascending_east"),
      ASCENDING_WEST(3, "ascending_west"),
      ASCENDING_NORTH(4, "ascending_north"),
      ASCENDING_SOUTH(5, "ascending_south"),
      SOUTH_EAST(6, "south_east"),
      SOUTH_WEST(7, "south_west"),
      NORTH_WEST(8, "north_west"),
      NORTH_EAST(9, "north_east");

      public int meta;
      public String name;
      public static BlockRailBase.EnumRailDirection[] META_LOOKUP = new BlockRailBase.EnumRailDirection[values().length];

      EnumRailDirection(int meta, String name) {
         this.meta = meta;
         this.name = name;
      }

      public int getMetadata() {
         return this.meta;
      }

      @Override
      public String toString() {
         return this.name;
      }

      public boolean isAscending() {
         return this == ASCENDING_NORTH || this == ASCENDING_EAST || this == ASCENDING_SOUTH || this == ASCENDING_WEST;
      }

      public static BlockRailBase.EnumRailDirection byMetadata(int meta) {
         if (meta < 0 || meta >= META_LOOKUP.length) {
            meta = 0;
         }

         return META_LOOKUP[meta];
      }

      @Override
      public String getName() {
         return this.name;
      }

      static {
         for (BlockRailBase.EnumRailDirection enumRailDirection : values()) {
            META_LOOKUP[enumRailDirection.getMetadata()] = enumRailDirection;
         }
      }
   }

   public class Rail {
      public List<BlockPos> neighbors = Lists.newArrayList();
      public World world;
      public BlockPos pos;
      public IBlockState state;
      public BlockRailBase block;
      public boolean isPowered;

      public Rail(World worldIn, BlockPos pos, IBlockState state) {
         this.world = worldIn;
         this.pos = pos;
         this.state = state;
         this.block = (BlockRailBase)state.getBlock();
         BlockRailBase.EnumRailDirection value = state.getValue(BlockRailBase.this.getShapeProperty());
         this.isPowered = this.block.isPowered;
         this.computeNeighbors(value);
      }

      public void computeNeighbors(BlockRailBase.EnumRailDirection enumRailDirection) {
         this.neighbors.clear();
         switch (enumRailDirection) {
            case NORTH_SOUTH:
               this.neighbors.add(this.pos.north());
               this.neighbors.add(this.pos.south());
               break;
            case EAST_WEST:
               this.neighbors.add(this.pos.west());
               this.neighbors.add(this.pos.east());
               break;
            case ASCENDING_EAST:
               this.neighbors.add(this.pos.west());
               this.neighbors.add(this.pos.east().up());
               break;
            case ASCENDING_WEST:
               this.neighbors.add(this.pos.west().up());
               this.neighbors.add(this.pos.east());
               break;
            case ASCENDING_NORTH:
               this.neighbors.add(this.pos.north().up());
               this.neighbors.add(this.pos.south());
               break;
            case ASCENDING_SOUTH:
               this.neighbors.add(this.pos.north());
               this.neighbors.add(this.pos.south().up());
               break;
            case SOUTH_EAST:
               this.neighbors.add(this.pos.east());
               this.neighbors.add(this.pos.south());
               break;
            case SOUTH_WEST:
               this.neighbors.add(this.pos.west());
               this.neighbors.add(this.pos.south());
               break;
            case NORTH_WEST:
               this.neighbors.add(this.pos.west());
               this.neighbors.add(this.pos.north());
               break;
            case NORTH_EAST:
               this.neighbors.add(this.pos.east());
               this.neighbors.add(this.pos.north());
         }
      }

      public void correctNeighbors() {
         for (int i = 0; i < this.neighbors.size(); i++) {
            BlockRailBase.Rail findRailAt = this.findRailAt(this.neighbors.get(i));
            if (findRailAt != null && findRailAt.isNeighbor(this)) {
               this.neighbors.set(i, findRailAt.pos);
            } else {
               this.neighbors.remove(i--);
            }
         }
      }

      public boolean hasRailAt(BlockPos pos) {
         return BlockRailBase.isRailBlock(this.world, pos)
            || BlockRailBase.isRailBlock(this.world, pos.up())
            || BlockRailBase.isRailBlock(this.world, pos.down());
      }

      public BlockRailBase.Rail findRailAt(BlockPos pos) {
         IBlockState blockState = this.world.getBlockState(pos);
         if (BlockRailBase.isRailBlock(blockState)) {
            return BlockRailBase.this.new Rail(this.world, pos, blockState);
         }

         BlockPos up = pos.up();
         blockState = this.world.getBlockState(up);
         if (BlockRailBase.isRailBlock(blockState)) {
            return BlockRailBase.this.new Rail(this.world, up, blockState);
         }

         up = pos.down();
         blockState = this.world.getBlockState(up);
         return BlockRailBase.isRailBlock(blockState) ? BlockRailBase.this.new Rail(this.world, up, blockState) : null;
      }

      public boolean isNeighbor(BlockRailBase.Rail rail) {
         return this.isNeighbor(rail.pos);
      }

      public boolean isNeighbor(BlockPos blockPos) {
         for (int i = 0; i < this.neighbors.size(); i++) {
            BlockPos blockPos2 = this.neighbors.get(i);
            if (blockPos2.getX() == blockPos.getX() && blockPos2.getZ() == blockPos.getZ()) {
               return true;
            }
         }

         return false;
      }

      public int countAdjacentRails() {
         int n = 0;

         for (EnumFacing enumFacing : Plane.HORIZONTAL) {
            if (this.hasRailAt(this.pos.offset(enumFacing))) {
               n++;
            }
         }

         return n;
      }

      public boolean willAlignTo(BlockRailBase.Rail rail) {
         return this.isNeighbor(rail) || this.neighbors.size() != 2;
      }

      public void realignTo(BlockRailBase.Rail rail) {
         this.neighbors.add(rail.pos);
         BlockPos north = this.pos.north();
         BlockPos south = this.pos.south();
         BlockPos west = this.pos.west();
         BlockPos east = this.pos.east();
         boolean isNeighbor = this.isNeighbor(north);
         boolean isNeighbor2 = this.isNeighbor(south);
         boolean isNeighbor3 = this.isNeighbor(west);
         boolean isNeighbor4 = this.isNeighbor(east);
         BlockRailBase.EnumRailDirection enumRailDirection = null;
         if (isNeighbor || isNeighbor2) {
            enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         if (isNeighbor3 || isNeighbor4) {
            enumRailDirection = BlockRailBase.EnumRailDirection.EAST_WEST;
         }

         if (!this.isPowered) {
            if (isNeighbor2 && isNeighbor4 && !isNeighbor && !isNeighbor3) {
               enumRailDirection = BlockRailBase.EnumRailDirection.SOUTH_EAST;
            }

            if (isNeighbor2 && isNeighbor3 && !isNeighbor && !isNeighbor4) {
               enumRailDirection = BlockRailBase.EnumRailDirection.SOUTH_WEST;
            }

            if (isNeighbor && isNeighbor3 && !isNeighbor2 && !isNeighbor4) {
               enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_WEST;
            }

            if (isNeighbor && isNeighbor4 && !isNeighbor2 && !isNeighbor3) {
               enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_EAST;
            }
         }

         if (enumRailDirection == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
            if (BlockRailBase.isRailBlock(this.world, north.up())) {
               enumRailDirection = BlockRailBase.EnumRailDirection.ASCENDING_NORTH;
            }

            if (BlockRailBase.isRailBlock(this.world, south.up())) {
               enumRailDirection = BlockRailBase.EnumRailDirection.ASCENDING_SOUTH;
            }
         }

         if (enumRailDirection == BlockRailBase.EnumRailDirection.EAST_WEST) {
            if (BlockRailBase.isRailBlock(this.world, east.up())) {
               enumRailDirection = BlockRailBase.EnumRailDirection.ASCENDING_EAST;
            }

            if (BlockRailBase.isRailBlock(this.world, west.up())) {
               enumRailDirection = BlockRailBase.EnumRailDirection.ASCENDING_WEST;
            }
         }

         if (enumRailDirection == null) {
            enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         this.state = this.state.withProperty(this.block.getShapeProperty(), enumRailDirection);
         this.world.setBlockState(this.pos, this.state, 3);
      }

      public boolean willAlignTo(BlockPos blockPos) {
         BlockRailBase.Rail findRailAt = this.findRailAt(blockPos);
         if (findRailAt == null) {
            return false;
         }

         findRailAt.correctNeighbors();
         return findRailAt.willAlignTo(this);
      }

      public BlockRailBase.Rail getRealignedHelper(boolean flag, boolean flag2) {
         BlockPos north = this.pos.north();
         BlockPos south = this.pos.south();
         BlockPos west = this.pos.west();
         BlockPos east = this.pos.east();
         boolean willAlignTo = this.willAlignTo(north);
         boolean willAlignTo2 = this.willAlignTo(south);
         boolean willAlignTo3 = this.willAlignTo(west);
         boolean willAlignTo4 = this.willAlignTo(east);
         BlockRailBase.EnumRailDirection enumRailDirection = null;
         if ((willAlignTo || willAlignTo2) && !willAlignTo3 && !willAlignTo4) {
            enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         if ((willAlignTo3 || willAlignTo4) && !willAlignTo && !willAlignTo2) {
            enumRailDirection = BlockRailBase.EnumRailDirection.EAST_WEST;
         }

         if (!this.isPowered) {
            if (willAlignTo2 && willAlignTo4 && !willAlignTo && !willAlignTo3) {
               enumRailDirection = BlockRailBase.EnumRailDirection.SOUTH_EAST;
            }

            if (willAlignTo2 && willAlignTo3 && !willAlignTo && !willAlignTo4) {
               enumRailDirection = BlockRailBase.EnumRailDirection.SOUTH_WEST;
            }

            if (willAlignTo && willAlignTo3 && !willAlignTo2 && !willAlignTo4) {
               enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_WEST;
            }

            if (willAlignTo && willAlignTo4 && !willAlignTo2 && !willAlignTo3) {
               enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_EAST;
            }
         }

         if (enumRailDirection == null) {
            if (willAlignTo || willAlignTo2) {
               enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
            }

            if (willAlignTo3 || willAlignTo4) {
               enumRailDirection = BlockRailBase.EnumRailDirection.EAST_WEST;
            }

            if (!this.isPowered) {
               if (flag) {
                  if (willAlignTo2 && willAlignTo4) {
                     enumRailDirection = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                  }

                  if (willAlignTo3 && willAlignTo2) {
                     enumRailDirection = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                  }

                  if (willAlignTo4 && willAlignTo) {
                     enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_EAST;
                  }

                  if (willAlignTo && willAlignTo3) {
                     enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_WEST;
                  }
               } else {
                  if (willAlignTo && willAlignTo3) {
                     enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_WEST;
                  }

                  if (willAlignTo4 && willAlignTo) {
                     enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_EAST;
                  }

                  if (willAlignTo3 && willAlignTo2) {
                     enumRailDirection = BlockRailBase.EnumRailDirection.SOUTH_WEST;
                  }

                  if (willAlignTo2 && willAlignTo4) {
                     enumRailDirection = BlockRailBase.EnumRailDirection.SOUTH_EAST;
                  }
               }
            }
         }

         if (enumRailDirection == BlockRailBase.EnumRailDirection.NORTH_SOUTH) {
            if (BlockRailBase.isRailBlock(this.world, north.up())) {
               enumRailDirection = BlockRailBase.EnumRailDirection.ASCENDING_NORTH;
            }

            if (BlockRailBase.isRailBlock(this.world, south.up())) {
               enumRailDirection = BlockRailBase.EnumRailDirection.ASCENDING_SOUTH;
            }
         }

         if (enumRailDirection == BlockRailBase.EnumRailDirection.EAST_WEST) {
            if (BlockRailBase.isRailBlock(this.world, east.up())) {
               enumRailDirection = BlockRailBase.EnumRailDirection.ASCENDING_EAST;
            }

            if (BlockRailBase.isRailBlock(this.world, west.up())) {
               enumRailDirection = BlockRailBase.EnumRailDirection.ASCENDING_WEST;
            }
         }

         if (enumRailDirection == null) {
            enumRailDirection = BlockRailBase.EnumRailDirection.NORTH_SOUTH;
         }

         this.computeNeighbors(enumRailDirection);
         this.state = this.state.withProperty(this.block.getShapeProperty(), enumRailDirection);
         if (flag2 || this.world.getBlockState(this.pos) != this.state) {
            this.world.setBlockState(this.pos, this.state, 3);

            for (int i = 0; i < this.neighbors.size(); i++) {
               BlockRailBase.Rail findRailAt = this.findRailAt(this.neighbors.get(i));
               if (findRailAt != null) {
                  findRailAt.correctNeighbors();
                  if (findRailAt.willAlignTo(this)) {
                     findRailAt.realignTo(this);
                  }
               }
            }
         }

         return this;
      }

      public IBlockState getBlockState() {
         return this.state;
      }
   }
}
