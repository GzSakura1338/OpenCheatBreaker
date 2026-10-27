package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonMoving extends BlockContainer {
   public static PropertyDirection FACING = BlockPistonExtension.FACING;
   public static PropertyEnum<BlockPistonExtension.EnumPistonType> TYPE = BlockPistonExtension.TYPE;

   public BlockPistonMoving() {
      super(Material.piston);
      this.setDefaultState(
         this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, BlockPistonExtension.EnumPistonType.DEFAULT)
      );
      this.setHardness(-1.0F);
   }

   @Override
   public TileEntity createNewTileEntity(World worldIn, int meta) {
      return null;
   }

   public static TileEntity newTileEntity(IBlockState state, EnumFacing facing, boolean extending, boolean renderHead) {
      return new TileEntityPiston(state, facing, extending, renderHead);
   }

   @Override
   public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      if (tileEntity instanceof TileEntityPiston) {
         ((TileEntityPiston)tileEntity).clearPistonTileEntity();
      } else {
         super.breakBlock(worldIn, pos, state);
      }
   }

   @Override
   public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
      return false;
   }

   @Override
   public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
      return false;
   }

   @Override
   public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
      BlockPos offset = pos.offset(state.getValue(FACING).getOpposite());
      IBlockState blockState = worldIn.getBlockState(offset);
      if (blockState.getBlock() instanceof BlockPistonBase && blockState.getValue(BlockPistonBase.EXTENDED)) {
         worldIn.setBlockToAir(offset);
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
   public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
      if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null) {
         worldIn.setBlockToAir(pos);
         return true;
      } else {
         return false;
      }
   }

   @Override
   public Item getItemDropped(IBlockState state, Random rand, int fortune) {
      return null;
   }

   @Override
   public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
      if (!worldIn.isRemote) {
         TileEntityPiston tileEntity = this.getTileEntity(worldIn, pos);
         if (tileEntity != null) {
            IBlockState pistonState = tileEntity.getPistonState();
            pistonState.getBlock().dropBlockAsItem(worldIn, pos, pistonState, 0);
         }
      }
   }

   @Override
   public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
      return null;
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!worldIn.isRemote) {
         worldIn.getTileEntity(pos);
      }
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      TileEntityPiston tileEntity = this.getTileEntity(worldIn, pos);
      if (tileEntity == null) {
         return null;
      }

      float progress = tileEntity.getProgress(0.0F);
      if (tileEntity.isExtending()) {
         progress = 1.0F - progress;
      }

      return this.getBoundingBox(worldIn, pos, tileEntity.getPistonState(), progress, tileEntity.getFacing());
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      TileEntityPiston tileEntity = this.getTileEntity(worldIn, pos);
      if (tileEntity != null) {
         IBlockState pistonState = tileEntity.getPistonState();
         Block block = pistonState.getBlock();
         if (block == this || block.getMaterial() == Material.air) {
            return;
         }

         float progress = tileEntity.getProgress(0.0F);
         if (tileEntity.isExtending()) {
            progress = 1.0F - progress;
         }

         block.setBlockBoundsBasedOnState(worldIn, pos);
         if (block == Blocks.piston || block == Blocks.sticky_piston) {
            progress = 0.0F;
         }

         EnumFacing facing = tileEntity.getFacing();
         this.minX = block.getBlockBoundsMinX() - facing.getFrontOffsetX() * progress;
         this.minY = block.getBlockBoundsMinY() - facing.getFrontOffsetY() * progress;
         this.minZ = block.getBlockBoundsMinZ() - facing.getFrontOffsetZ() * progress;
         this.maxX = block.getBlockBoundsMaxX() - facing.getFrontOffsetX() * progress;
         this.maxY = block.getBlockBoundsMaxY() - facing.getFrontOffsetY() * progress;
         this.maxZ = block.getBlockBoundsMaxZ() - facing.getFrontOffsetZ() * progress;
      }
   }

   public AxisAlignedBB getBoundingBox(World worldIn, BlockPos pos, IBlockState extendingBlock, float progress, EnumFacing direction) {
      if (extendingBlock.getBlock() != this && extendingBlock.getBlock().getMaterial() != Material.air) {
         AxisAlignedBB collisionBoundingBox = extendingBlock.getBlock().getCollisionBoundingBox(worldIn, pos, extendingBlock);
         if (collisionBoundingBox == null) {
            return null;
         }

         double minX = collisionBoundingBox.minX;
         double minY = collisionBoundingBox.minY;
         double minZ = collisionBoundingBox.minZ;
         double maxX = collisionBoundingBox.maxX;
         double maxY = collisionBoundingBox.maxY;
         double maxZ = collisionBoundingBox.maxZ;
         if (direction.getFrontOffsetX() < 0) {
            minX -= direction.getFrontOffsetX() * progress;
         } else {
            maxX -= direction.getFrontOffsetX() * progress;
         }

         if (direction.getFrontOffsetY() < 0) {
            minY -= direction.getFrontOffsetY() * progress;
         } else {
            maxY -= direction.getFrontOffsetY() * progress;
         }

         if (direction.getFrontOffsetZ() < 0) {
            minZ -= direction.getFrontOffsetZ() * progress;
         } else {
            maxZ -= direction.getFrontOffsetZ() * progress;
         }

         return new AxisAlignedBB(minX, minY, minZ, maxX, maxY, maxZ);
      } else {
         return null;
      }
   }

   public TileEntityPiston getTileEntity(IBlockAccess worldIn, BlockPos pos) {
      TileEntity tileEntity = worldIn.getTileEntity(pos);
      return tileEntity instanceof TileEntityPiston ? (TileEntityPiston)tileEntity : null;
   }

   @Override
   public Item getItem(World worldIn, BlockPos pos) {
      return null;
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState()
         .withProperty(FACING, BlockPistonExtension.getFacing(meta))
         .withProperty(TYPE, (meta & 8) > 0 ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int n = 0;
      n |= state.getValue(FACING).getIndex();
      if (state.getValue(TYPE) == BlockPistonExtension.EnumPistonType.STICKY) {
         n |= 8;
      }

      return n;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, TYPE);
   }
}
