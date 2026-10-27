package net.minecraft.block;

import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockPistonStructureHelper;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonBase extends Block {
   public static PropertyDirection FACING = PropertyDirection.create("facing");
   public static PropertyBool EXTENDED = PropertyBool.create("extended");
   public boolean isSticky;

   public BlockPistonBase(boolean isSticky) {
      super(Material.piston);
      this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(EXTENDED, false));
      this.isSticky = isSticky;
      this.setStepSound(soundTypePiston);
      this.setHardness(0.5F);
      this.setCreativeTab(CreativeTabs.tabRedstone);
   }

   @Override
   public boolean isOpaqueCube() {
      return false;
   }

   @Override
   public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
      worldIn.setBlockState(pos, state.withProperty(FACING, getFacingFromEntity(worldIn, pos, placer)), 2);
      if (!worldIn.isRemote) {
         this.checkForMove(worldIn, pos, state);
      }
   }

   @Override
   public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
      if (!worldIn.isRemote) {
         this.checkForMove(worldIn, pos, state);
      }
   }

   @Override
   public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
      if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null) {
         this.checkForMove(worldIn, pos, state);
      }
   }

   @Override
   public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
      return this.getDefaultState().withProperty(FACING, getFacingFromEntity(worldIn, pos, placer)).withProperty(EXTENDED, false);
   }

   public void checkForMove(World worldIn, BlockPos pos, IBlockState state) {
      EnumFacing value = state.getValue(FACING);
      boolean shouldBeExtended = this.shouldBeExtended(worldIn, pos, value);
      if (shouldBeExtended && !state.getValue(EXTENDED)) {
         if (new BlockPistonStructureHelper(worldIn, pos, value, true).canMove()) {
            worldIn.addBlockEvent(pos, this, 0, value.getIndex());
         }
      } else if (!shouldBeExtended && state.getValue(EXTENDED)) {
         worldIn.setBlockState(pos, state.withProperty(EXTENDED, false), 2);
         worldIn.addBlockEvent(pos, this, 1, value.getIndex());
      }
   }

   public boolean shouldBeExtended(World worldIn, BlockPos pos, EnumFacing facing) {
      for (EnumFacing enumFacing : EnumFacing.values()) {
         if (enumFacing != facing && worldIn.isSidePowered(pos.offset(enumFacing), enumFacing)) {
            return true;
         }
      }

      if (worldIn.isSidePowered(pos, EnumFacing.DOWN)) {
         return true;
      }

      BlockPos up = pos.up();

      for (EnumFacing enumFacing : EnumFacing.values()) {
         if (enumFacing != EnumFacing.DOWN && worldIn.isSidePowered(up.offset(enumFacing), enumFacing)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean onBlockEventReceived(World worldIn, BlockPos pos, IBlockState state, int eventID, int eventParam) {
      EnumFacing value = state.getValue(FACING);
      if (!worldIn.isRemote) {
         boolean shouldBeExtended = this.shouldBeExtended(worldIn, pos, value);
         if (shouldBeExtended && eventID == 1) {
            worldIn.setBlockState(pos, state.withProperty(EXTENDED, true), 2);
            return false;
         }

         if (!shouldBeExtended && eventID == 0) {
            return false;
         }
      }

      if (eventID == 0) {
         if (!this.doMove(worldIn, pos, value, true)) {
            return false;
         }

         worldIn.setBlockState(pos, state.withProperty(EXTENDED, true), 2);
         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, "tile.piston.out", 0.5F, worldIn.rand.nextFloat() * 0.25F + 0.6F);
      } else if (eventID == 1) {
         TileEntity tileEntity = worldIn.getTileEntity(pos.offset(value));
         if (tileEntity instanceof TileEntityPiston) {
            ((TileEntityPiston)tileEntity).clearPistonTileEntity();
         }

         worldIn.setBlockState(
            pos,
            Blocks.piston_extension
               .getDefaultState()
               .withProperty(BlockPistonMoving.FACING, value)
               .withProperty(BlockPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT),
            3
         );
         worldIn.setTileEntity(pos, BlockPistonMoving.newTileEntity(this.getStateFromMeta(eventParam), value, false, true));
         if (this.isSticky) {
            BlockPos blockPos = pos.add(value.getFrontOffsetX() * 2, value.getFrontOffsetY() * 2, value.getFrontOffsetZ() * 2);
            Block block = worldIn.getBlockState(blockPos).getBlock();
            boolean flag = false;
            if (block == Blocks.piston_extension) {
               TileEntity tileEntity2 = worldIn.getTileEntity(blockPos);
               if (tileEntity2 instanceof TileEntityPiston) {
                  TileEntityPiston tileEntityPiston = (TileEntityPiston)tileEntity2;
                  if (tileEntityPiston.getFacing() == value && tileEntityPiston.isExtending()) {
                     tileEntityPiston.clearPistonTileEntity();
                     flag = true;
                  }
               }
            }

            if (!flag
               && block.getMaterial() != Material.air
               && canPush(block, worldIn, blockPos, value.getOpposite(), false)
               && (block.getMobilityFlag() == 0 || block == Blocks.piston || block == Blocks.sticky_piston)) {
               this.doMove(worldIn, pos, value, false);
            }
         } else {
            worldIn.setBlockToAir(pos.offset(value));
         }

         worldIn.playSoundEffect(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, "tile.piston.in", 0.5F, worldIn.rand.nextFloat() * 0.15F + 0.6F);
      }

      return true;
   }

   @Override
   public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
      IBlockState blockState = worldIn.getBlockState(pos);
      if (blockState.getBlock() == this && blockState.getValue(EXTENDED)) {
         float f = 0.25F;
         EnumFacing value = blockState.getValue(FACING);
         if (value != null) {
            switch (value) {
               case DOWN:
                  this.setBlockBounds(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F);
                  break;
               case UP:
                  this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
                  break;
               case NORTH:
                  this.setBlockBounds(0.0F, 0.0F, 0.25F, 1.0F, 1.0F, 1.0F);
                  break;
               case SOUTH:
                  this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.75F);
                  break;
               case WEST:
                  this.setBlockBounds(0.25F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                  break;
               case EAST:
                  this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.75F, 1.0F, 1.0F);
            }
         }
      } else {
         this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      }
   }

   @Override
   public void setBlockBoundsForItemRender() {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
   }

   @Override
   public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
      this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
      super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
   }

   @Override
   public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
      this.setBlockBoundsBasedOnState(worldIn, pos);
      return super.getCollisionBoundingBox(worldIn, pos, state);
   }

   @Override
   public boolean isFullCube() {
      return false;
   }

   public static EnumFacing getFacing(int meta) {
      int n = meta & 7;
      return n > 5 ? null : EnumFacing.getFront(n);
   }

   public static EnumFacing getFacingFromEntity(World worldIn, BlockPos clickedBlock, EntityLivingBase entityIn) {
      if (MathHelper.abs((float)entityIn.posX - clickedBlock.getX()) < 2.0F && MathHelper.abs((float)entityIn.posZ - clickedBlock.getZ()) < 2.0F) {
         double d = entityIn.posY + entityIn.getEyeHeight();
         if (d - clickedBlock.getY() > 2.0) {
            return EnumFacing.UP;
         }

         if (clickedBlock.getY() - d > 0.0) {
            return EnumFacing.DOWN;
         }
      }

      return entityIn.getHorizontalFacing().getOpposite();
   }

   public static boolean canPush(Block blockIn, World worldIn, BlockPos pos, EnumFacing direction, boolean allowDestroy) {
      if (blockIn == Blocks.obsidian) {
         return false;
      }

      if (!worldIn.getWorldBorder().contains(pos)) {
         return false;
      }

      if (pos.getY() >= 0 && (direction != EnumFacing.DOWN || pos.getY() != 0)) {
         if (pos.getY() <= worldIn.getHeight() - 1 && (direction != EnumFacing.UP || pos.getY() != worldIn.getHeight() - 1)) {
            if (blockIn != Blocks.piston && blockIn != Blocks.sticky_piston) {
               if (blockIn.getBlockHardness(worldIn, pos) == -1.0F) {
                  return false;
               }

               if (blockIn.getMobilityFlag() == 2) {
                  return false;
               }

               if (blockIn.getMobilityFlag() == 1) {
                  if (!allowDestroy) {
                     return false;
                  }

                  return true;
               }
            } else if (worldIn.getBlockState(pos).getValue(EXTENDED)) {
               return false;
            }

            return !(blockIn instanceof ITileEntityProvider);
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean doMove(World worldIn, BlockPos pos, EnumFacing direction, boolean extending) {
      if (!extending) {
         worldIn.setBlockToAir(pos.offset(direction));
      }

      BlockPistonStructureHelper blockPistonStructureHelper = new BlockPistonStructureHelper(worldIn, pos, direction, extending);
      List blocksToMove = blockPistonStructureHelper.getBlocksToMove();
      List blocksToDestroy = blockPistonStructureHelper.getBlocksToDestroy();
      if (!blockPistonStructureHelper.canMove()) {
         return false;
      }

      int n = blocksToMove.size() + blocksToDestroy.size();
      Block[] blocks = new Block[n];
      EnumFacing enumFacing = extending ? direction : direction.getOpposite();

      for (int i = blocksToDestroy.size() - 1; i >= 0; i--) {
         BlockPos blockPos = (BlockPos)blocksToDestroy.get(i);
         Block block = worldIn.getBlockState(blockPos).getBlock();
         block.dropBlockAsItem(worldIn, blockPos, worldIn.getBlockState(blockPos), 0);
         worldIn.setBlockToAir(blockPos);
         blocks[--n] = block;
      }

      for (int i = blocksToMove.size() - 1; i >= 0; i--) {
         BlockPos blockPos = (BlockPos)blocksToMove.get(i);
         IBlockState blockState = worldIn.getBlockState(blockPos);
         Block block = blockState.getBlock();
         block.getMetaFromState(blockState);
         worldIn.setBlockToAir(blockPos);
         blockPos = blockPos.offset(enumFacing);
         worldIn.setBlockState(blockPos, Blocks.piston_extension.getDefaultState().withProperty(FACING, direction), 4);
         worldIn.setTileEntity(blockPos, BlockPistonMoving.newTileEntity(blockState, direction, extending, false));
         blocks[--n] = block;
      }

      BlockPos offset = pos.offset(direction);
      if (extending) {
         BlockPistonExtension.EnumPistonType enumPistonType = this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT;
         IBlockState withProperty = Blocks.piston_head
            .getDefaultState()
            .withProperty(BlockPistonExtension.FACING, direction)
            .withProperty(BlockPistonExtension.TYPE, enumPistonType);
         IBlockState withProperty2 = Blocks.piston_extension
            .getDefaultState()
            .withProperty(BlockPistonMoving.FACING, direction)
            .withProperty(BlockPistonMoving.TYPE, this.isSticky ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
         worldIn.setBlockState(offset, withProperty2, 4);
         worldIn.setTileEntity(offset, BlockPistonMoving.newTileEntity(withProperty, direction, true, false));
      }

      for (int i = blocksToDestroy.size() - 1; i >= 0; i--) {
         worldIn.notifyNeighborsOfStateChange((BlockPos)blocksToDestroy.get(i), blocks[n++]);
      }

      for (int i = blocksToMove.size() - 1; i >= 0; i--) {
         worldIn.notifyNeighborsOfStateChange((BlockPos)blocksToMove.get(i), blocks[n++]);
      }

      if (extending) {
         worldIn.notifyNeighborsOfStateChange(offset, Blocks.piston_head);
         worldIn.notifyNeighborsOfStateChange(pos, this);
      }

      return true;
   }

   @Override
   public IBlockState getStateForEntityRender(IBlockState state) {
      return this.getDefaultState().withProperty(FACING, EnumFacing.UP);
   }

   @Override
   public IBlockState getStateFromMeta(int meta) {
      return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(EXTENDED, (meta & 8) > 0);
   }

   @Override
   public int getMetaFromState(IBlockState state) {
      int n = 0;
      n |= state.getValue(FACING).getIndex();
      if (state.getValue(EXTENDED)) {
         n |= 8;
      }

      return n;
   }

   @Override
   public BlockState createBlockState() {
      return new BlockState(this, FACING, EXTENDED);
   }
}
