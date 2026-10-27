package net.minecraft.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;

public class BlockChest
extends BlockContainer {
    public static PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public int chestType;

    public BlockChest(int type) {
        super(Material.wood);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH));
        this.chestType = type;
        this.setCreativeTab(CreativeTabs.tabDecorations);
        this.setBlockBounds(0.0625f, 0.0f, 0.0625f, 0.9375f, 0.875f, 0.9375f);
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
    public int getRenderType() {
        return 2;
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos.north()).getBlock() == this) {
            this.setBlockBounds(0.0625f, 0.0f, 0.0f, 0.9375f, 0.875f, 0.9375f);
        } else if (worldIn.getBlockState(pos.south()).getBlock() == this) {
            this.setBlockBounds(0.0625f, 0.0f, 0.0625f, 0.9375f, 0.875f, 1.0f);
        } else if (worldIn.getBlockState(pos.west()).getBlock() == this) {
            this.setBlockBounds(0.0f, 0.0f, 0.0625f, 0.9375f, 0.875f, 0.9375f);
        } else if (worldIn.getBlockState(pos.east()).getBlock() == this) {
            this.setBlockBounds(0.0625f, 0.0f, 0.0625f, 1.0f, 0.875f, 0.9375f);
        } else {
            this.setBlockBounds(0.0625f, 0.0f, 0.0625f, 0.9375f, 0.875f, 0.9375f);
        }
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        this.checkForSurroundingChests(worldIn, pos, state);
        for (EnumFacing enumFacing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockPos = pos.offset(enumFacing);
            IBlockState iBlockState = worldIn.getBlockState(blockPos);
            if (iBlockState.getBlock() != this) continue;
            this.checkForSurroundingChests(worldIn, blockPos, iBlockState);
        }
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing());
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tileEntity;
        boolean bl;
        EnumFacing enumFacing = EnumFacing.getHorizontal(MathHelper.floor_double((double)(placer.rotationYaw * 4.0f / 360.0f) + 0.5) & 3).getOpposite();
        state = state.withProperty(FACING, enumFacing);
        BlockPos blockPos = pos.north();
        BlockPos blockPos2 = pos.south();
        BlockPos blockPos3 = pos.west();
        BlockPos blockPos4 = pos.east();
        boolean bl2 = this == worldIn.getBlockState(blockPos).getBlock();
        boolean bl3 = this == worldIn.getBlockState(blockPos2).getBlock();
        boolean bl4 = this == worldIn.getBlockState(blockPos3).getBlock();
        boolean bl5 = bl = this == worldIn.getBlockState(blockPos4).getBlock();
        if (!(bl2 || bl3 || bl4 || bl)) {
            worldIn.setBlockState(pos, state, 3);
        } else if (enumFacing.getAxis() != EnumFacing.Axis.X || !bl2 && !bl3) {
            if (enumFacing.getAxis() == EnumFacing.Axis.Z && (bl4 || bl)) {
                if (bl4) {
                    worldIn.setBlockState(blockPos3, state, 3);
                } else {
                    worldIn.setBlockState(blockPos4, state, 3);
                }
                worldIn.setBlockState(pos, state, 3);
            }
        } else {
            if (bl2) {
                worldIn.setBlockState(blockPos, state, 3);
            } else {
                worldIn.setBlockState(blockPos2, state, 3);
            }
            worldIn.setBlockState(pos, state, 3);
        }
        if (stack.hasDisplayName() && (tileEntity = worldIn.getTileEntity(pos)) instanceof TileEntityChest) {
            ((TileEntityChest)tileEntity).setCustomName(stack.getDisplayName());
        }
    }

    public IBlockState checkForSurroundingChests(World worldIn, BlockPos pos, IBlockState state) {
        if (worldIn.isRemote) {
            return state;
        }
        IBlockState iBlockState = worldIn.getBlockState(pos.north());
        IBlockState iBlockState2 = worldIn.getBlockState(pos.south());
        IBlockState iBlockState3 = worldIn.getBlockState(pos.west());
        IBlockState iBlockState4 = worldIn.getBlockState(pos.east());
        EnumFacing enumFacing = state.getValue(FACING);
        Block block = iBlockState.getBlock();
        Block block2 = iBlockState2.getBlock();
        Block block3 = iBlockState3.getBlock();
        Block block4 = iBlockState4.getBlock();
        if (block != this && block2 != this) {
            boolean bl = block.isFullBlock();
            boolean bl2 = block2.isFullBlock();
            if (block3 == this || block4 == this) {
                BlockPos blockPos = block3 == this ? pos.west() : pos.east();
                IBlockState iBlockState5 = worldIn.getBlockState(blockPos.north());
                IBlockState iBlockState6 = worldIn.getBlockState(blockPos.south());
                enumFacing = EnumFacing.SOUTH;
                EnumFacing enumFacing2 = block3 == this ? iBlockState3.getValue(FACING) : iBlockState4.getValue(FACING);
                if (enumFacing2 == EnumFacing.NORTH) {
                    enumFacing = EnumFacing.NORTH;
                }
                Block block5 = iBlockState5.getBlock();
                Block block6 = iBlockState6.getBlock();
                if ((bl || block5.isFullBlock()) && !bl2 && !block6.isFullBlock()) {
                    enumFacing = EnumFacing.SOUTH;
                }
                if ((bl2 || block6.isFullBlock()) && !bl && !block5.isFullBlock()) {
                    enumFacing = EnumFacing.NORTH;
                }
            }
        } else {
            BlockPos blockPos = block == this ? pos.north() : pos.south();
            IBlockState iBlockState7 = worldIn.getBlockState(blockPos.west());
            IBlockState iBlockState8 = worldIn.getBlockState(blockPos.east());
            enumFacing = EnumFacing.EAST;
            EnumFacing enumFacing3 = block == this ? iBlockState.getValue(FACING) : iBlockState2.getValue(FACING);
            if (enumFacing3 == EnumFacing.WEST) {
                enumFacing = EnumFacing.WEST;
            }
            Block block7 = iBlockState7.getBlock();
            Block block8 = iBlockState8.getBlock();
            if ((block3.isFullBlock() || block7.isFullBlock()) && !block4.isFullBlock() && !block8.isFullBlock()) {
                enumFacing = EnumFacing.EAST;
            }
            if ((block4.isFullBlock() || block8.isFullBlock()) && !block3.isFullBlock() && !block7.isFullBlock()) {
                enumFacing = EnumFacing.WEST;
            }
        }
        state = state.withProperty(FACING, enumFacing);
        worldIn.setBlockState(pos, state, 3);
        return state;
    }

    public IBlockState correctFacing(World worldIn, BlockPos pos, IBlockState state) {
        EnumFacing enumFacing = null;
        for (EnumFacing enumFacing2 : EnumFacing.Plane.HORIZONTAL) {
            IBlockState iBlockState = worldIn.getBlockState(pos.offset(enumFacing2));
            if (iBlockState.getBlock() == this) {
                return state;
            }
            if (!iBlockState.getBlock().isFullBlock()) continue;
            if (enumFacing != null) {
                enumFacing = null;
                break;
            }
            enumFacing = enumFacing2;
        }
        if (enumFacing != null) {
            return state.withProperty(FACING, enumFacing.getOpposite());
        }
        Object object = state.getValue(FACING);
        if (worldIn.getBlockState(pos.offset((EnumFacing)object)).getBlock().isFullBlock()) {
            object = ((EnumFacing)object).getOpposite();
        }
        if (worldIn.getBlockState(pos.offset((EnumFacing)object)).getBlock().isFullBlock()) {
            object = ((EnumFacing)object).rotateY();
        }
        if (worldIn.getBlockState(pos.offset((EnumFacing)object)).getBlock().isFullBlock()) {
            object = ((EnumFacing)object).getOpposite();
        }
        return state.withProperty(FACING, (EnumFacing)object);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        int n = 0;
        BlockPos blockPos = pos.west();
        BlockPos blockPos2 = pos.east();
        BlockPos blockPos3 = pos.north();
        BlockPos blockPos4 = pos.south();
        if (worldIn.getBlockState(blockPos).getBlock() == this) {
            if (this.isDoubleChest(worldIn, blockPos)) {
                return false;
            }
            ++n;
        }
        if (worldIn.getBlockState(blockPos2).getBlock() == this) {
            if (this.isDoubleChest(worldIn, blockPos2)) {
                return false;
            }
            ++n;
        }
        if (worldIn.getBlockState(blockPos3).getBlock() == this) {
            if (this.isDoubleChest(worldIn, blockPos3)) {
                return false;
            }
            ++n;
        }
        if (worldIn.getBlockState(blockPos4).getBlock() == this) {
            if (this.isDoubleChest(worldIn, blockPos4)) {
                return false;
            }
            ++n;
        }
        return n <= 1;
    }

    public boolean isDoubleChest(World worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos).getBlock() != this) {
            return false;
        }
        for (EnumFacing enumFacing : EnumFacing.Plane.HORIZONTAL) {
            if (worldIn.getBlockState(pos.offset(enumFacing)).getBlock() != this) continue;
            return true;
        }
        return false;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        super.onNeighborBlockChange(worldIn, pos, state, neighborBlock);
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileEntityChest) {
            tileEntity.updateContainingBlockInfo();
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof IInventory) {
            InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)((Object)tileEntity));
            worldIn.updateComparatorOutputLevel(pos, this);
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote) {
            return true;
        }
        ILockableContainer iLockableContainer = this.getLockableContainer(worldIn, pos);
        if (iLockableContainer != null) {
            playerIn.displayGUIChest(iLockableContainer);
            if (this.chestType == 0) {
                playerIn.triggerAchievement(StatList.CHEST_OPENED);
            } else if (this.chestType == 1) {
                playerIn.triggerAchievement(StatList.TRAPPED_CHEST_TRIGGERED);
            }
        }
        return true;
    }

    public ILockableContainer getLockableContainer(World worldIn, BlockPos pos) {
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (!(tileEntity instanceof TileEntityChest)) {
            return null;
        }
        ILockableContainer iLockableContainer = (TileEntityChest)tileEntity;
        if (this.isBlocked(worldIn, pos)) {
            return null;
        }
        for (EnumFacing enumFacing : EnumFacing.Plane.HORIZONTAL) {
            BlockPos blockPos = pos.offset(enumFacing);
            Block block = worldIn.getBlockState(blockPos).getBlock();
            if (block != this) continue;
            if (this.isBlocked(worldIn, blockPos)) {
                return null;
            }
            TileEntity tileEntity2 = worldIn.getTileEntity(blockPos);
            if (!(tileEntity2 instanceof TileEntityChest)) continue;
            if (enumFacing != EnumFacing.WEST && enumFacing != EnumFacing.NORTH) {
                iLockableContainer = new InventoryLargeChest("container.chestDouble", iLockableContainer, (TileEntityChest)tileEntity2);
                continue;
            }
            iLockableContainer = new InventoryLargeChest("container.chestDouble", (TileEntityChest)tileEntity2, iLockableContainer);
        }
        return iLockableContainer;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityChest();
    }

    @Override
    public boolean canProvidePower() {
        return this.chestType == 1;
    }

    @Override
    public int getWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
        if (!this.canProvidePower()) {
            return 0;
        }
        int n = 0;
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        if (tileEntity instanceof TileEntityChest) {
            n = ((TileEntityChest)tileEntity).numPlayersUsing;
        }
        return MathHelper.clamp_int(n, 0, 15);
    }

    @Override
    public int getStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
        return side == EnumFacing.UP ? this.getWeakPower(worldIn, pos, state, side) : 0;
    }

    public boolean isBlocked(World worldIn, BlockPos pos) {
        return this.isBelowSolidBlock(worldIn, pos) || this.isOcelotSittingOnChest(worldIn, pos);
    }

    public boolean isBelowSolidBlock(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos.up()).getBlock().isNormalCube();
    }

    public boolean isOcelotSittingOnChest(World worldIn, BlockPos pos) {
        for (Entity entity : worldIn.getEntitiesWithinAABB(EntityOcelot.class, new AxisAlignedBB(pos.getX(), pos.getY() + 1, pos.getZ(), pos.getX() + 1, pos.getY() + 2, pos.getZ() + 1))) {
            EntityOcelot entityOcelot = (EntityOcelot)entity;
            if (!entityOcelot.isSitting()) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World worldIn, BlockPos pos) {
        return Container.calcRedstoneFromInventory(this.getLockableContainer(worldIn, pos));
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing enumFacing = EnumFacing.getFront(meta);
        if (enumFacing.getAxis() == EnumFacing.Axis.Y) {
            enumFacing = EnumFacing.NORTH;
        }
        return this.getDefaultState().withProperty(FACING, enumFacing);
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
