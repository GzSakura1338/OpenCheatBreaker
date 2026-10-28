package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockRedstoneDiode extends BlockDirectional {
    public boolean isRepeaterPowered;

    public BlockRedstoneDiode(boolean flag) {
        super(Material.circuits);
        this.isRepeaterPowered = flag;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.125F, 1.0F);
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) && super.canPlaceBlockAt(worldIn, x, y, z);
    }

    @Override
    public boolean canBlockStay(World worldIn, int x, int y, int z) {
        return World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) && super.canBlockStay(worldIn, x, y, z);
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        if (!this.isLocked(worldIn, x, y, z, blockMetadata)) {
            boolean isGettingInput = this.isGettingInput(worldIn, x, y, z, blockMetadata);
            if (this.isRepeaterPowered && !isGettingInput) {
                worldIn.setBlock(x, y, z, this.getBlockUnpowered(), blockMetadata, 2);
            } else if (!this.isRepeaterPowered) {
                worldIn.setBlock(x, y, z, this.getBlockPowered(), blockMetadata, 2);
                if (!isGettingInput) {
                    worldIn.scheduleBlockUpdateWithPriority(x, y, z, this.getBlockPowered(), this.setTickDelay(blockMetadata), -1);
                }
            }
        }
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return side == 0
            ? (this.isRepeaterPowered ? Blocks.redstone_torch.getBlockTextureFromSide(side) : Blocks.unlit_redstone_torch.getBlockTextureFromSide(side))
            : (side == 1 ? this.blockIcon : Blocks.double_stone_slab.getBlockTextureFromSide(1));
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return side != 0 && side != 1;
    }

    @Override
    public int getRenderType() {
        return 36;
    }

    public boolean check(int n) {
        return this.isRepeaterPowered;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        return this.isProvidingWeakPower(worldIn, x, y, z, side);
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        if (!this.check(blockMetadata)) {
            return 0;
        }

        int direction = getDirection(blockMetadata);
        return direction == 0 && side == 3
            ? this.getInputLevel(worldIn, x, y, z, blockMetadata)
            : (
                direction == 1 && side == 4
                    ? this.getInputLevel(worldIn, x, y, z, blockMetadata)
                    : (
                        direction == 2 && side == 2
                            ? this.getInputLevel(worldIn, x, y, z, blockMetadata)
                            : (direction == 3 && side == 5 ? this.getInputLevel(worldIn, x, y, z, blockMetadata) : 0)
                    )
            );
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!this.canBlockStay(worldIn, x, y, z)) {
            this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
            worldIn.setBlockToAir(x, y, z);
            worldIn.notifyBlocksOfNeighborChange(x + 1, y, z, this);
            worldIn.notifyBlocksOfNeighborChange(x - 1, y, z, this);
            worldIn.notifyBlocksOfNeighborChange(x, y, z + 1, this);
            worldIn.notifyBlocksOfNeighborChange(x, y, z - 1, this);
            worldIn.notifyBlocksOfNeighborChange(x, y - 1, z, this);
            worldIn.notifyBlocksOfNeighborChange(x, y + 1, z, this);
        } else {
            this.updatePoweredBlock(worldIn, x, y, z, neighbor);
        }
    }

    public void updatePoweredBlock(World world, int n, int n2, int n3, Block block) {
        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        if (!this.isLocked(world, n, n2, n3, blockMetadata)) {
            boolean isGettingInput = this.isGettingInput(world, n, n2, n3, blockMetadata);
            if ((this.isRepeaterPowered && !isGettingInput || !this.isRepeaterPowered && isGettingInput) && !world.isBlockTickScheduledThisTick(n, n2, n3, this)) {
                byte b = -1;
                if (this.isUnableToPowerBlockAtOutputFace(world, n, n2, n3, blockMetadata)) {
                    b = -3;
                } else if (this.isRepeaterPowered) {
                    b = -2;
                }

                world.scheduleBlockUpdateWithPriority(n, n2, n3, this, this.computeInt(blockMetadata), b);
            }
        }
    }

    public boolean isLocked(IBlockAccess blockAccess, int n, int n2, int n3, int n4) {
        return false;
    }

    public boolean isGettingInput(World world, int n, int n2, int n3, int n4) {
        return this.getInputStrength(world, n, n2, n3, n4) > 0;
    }

    public int getInputStrength(World world, int n, int n2, int n3, int n4) {
        int direction = getDirection(n4);
        int n5 = n + Direction.offsetX[direction];
        int n6 = n3 + Direction.offsetZ[direction];
        int indirectPowerLevelTo = world.getIndirectPowerLevelTo(n5, n2, n6, Direction.directionToFacing[direction]);
        return indirectPowerLevelTo >= 15 ? indirectPowerLevelTo : Math.max(indirectPowerLevelTo, world.getBlock(n5, n2, n6) == Blocks.redstone_wire ? world.getBlockMetadata(n5, n2, n6) : 0);
    }

    public int computeInt(IBlockAccess blockAccess, int n, int n2, int n3, int n4) {
        int direction = getDirection(n4);
        switch (direction) {
            case 0:
            case 2:
                return Math.max(this.computeInt2(blockAccess, n - 1, n2, n3, 4), this.computeInt2(blockAccess, n + 1, n2, n3, 5));
            case 1:
            case 3:
                return Math.max(this.computeInt2(blockAccess, n, n2, n3 + 1, 3), this.computeInt2(blockAccess, n, n2, n3 - 1, 2));
            default:
                return 0;
        }
    }

    public int computeInt2(IBlockAccess blockAccess, int n, int n2, int n3, int n4) {
        Block block = blockAccess.getBlock(n, n2, n3);
        return this.checkBlock(block)
            ? (block == Blocks.redstone_wire ? blockAccess.getBlockMetadata(n, n2, n3) : blockAccess.isBlockProvidingPowerTo(n, n2, n3, n4))
            : 0;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        int n = ((MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5) & 3) + 2) % 4;
        worldIn.setBlockMetadataWithNotify(x, y, z, n, 3);
        boolean isGettingInput = this.isGettingInput(worldIn, x, y, z, n);
        if (isGettingInput) {
            worldIn.scheduleBlockUpdate(x, y, z, this, 1);
        }
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        this.handleWorld(worldIn, x, y, z);
    }

    public void handleWorld(World world, int n, int n2, int n3) {
        int direction = getDirection(world.getBlockMetadata(n, n2, n3));
        if (direction == 1) {
            world.notifyBlockOfNeighborChange(n + 1, n2, n3, this);
            world.notifyBlocksOfNeighborChange(n + 1, n2, n3, this, 4);
        }

        if (direction == 3) {
            world.notifyBlockOfNeighborChange(n - 1, n2, n3, this);
            world.notifyBlocksOfNeighborChange(n - 1, n2, n3, this, 5);
        }

        if (direction == 2) {
            world.notifyBlockOfNeighborChange(n, n2, n3 + 1, this);
            world.notifyBlocksOfNeighborChange(n, n2, n3 + 1, this, 2);
        }

        if (direction == 0) {
            world.notifyBlockOfNeighborChange(n, n2, n3 - 1, this);
            world.notifyBlocksOfNeighborChange(n, n2, n3 - 1, this, 3);
        }
    }

    @Override
    public void onBlockDestroyedByPlayer(World worldIn, int x, int y, int z, int meta) {
        if (this.isRepeaterPowered) {
            worldIn.notifyBlocksOfNeighborChange(x + 1, y, z, this);
            worldIn.notifyBlocksOfNeighborChange(x - 1, y, z, this);
            worldIn.notifyBlocksOfNeighborChange(x, y, z + 1, this);
            worldIn.notifyBlocksOfNeighborChange(x, y, z - 1, this);
            worldIn.notifyBlocksOfNeighborChange(x, y - 1, z, this);
            worldIn.notifyBlocksOfNeighborChange(x, y + 1, z, this);
        }

        super.onBlockDestroyedByPlayer(worldIn, x, y, z, meta);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    public boolean checkBlock(Block block) {
        return block.canProvidePower();
    }

    public int getInputLevel(IBlockAccess blockAccess, int n, int n2, int n3, int n4) {
        return 15;
    }

    public static boolean isRedstoneRepeaterBlockID(Block block) {
        return Blocks.unpowered_repeater.isComparator(block) || Blocks.unpowered_comparator.isComparator(block);
    }

    public boolean isComparator(Block block) {
        return block == this.getBlockPowered() || block == this.getBlockUnpowered();
    }

    public boolean isUnableToPowerBlockAtOutputFace(World world, int n, int n2, int n3, int n4) {
        int direction = getDirection(n4);
        if (isRedstoneRepeaterBlockID(world.getBlock(n - Direction.offsetX[direction], n2, n3 - Direction.offsetZ[direction]))) {
            int blockMetadata = world.getBlockMetadata(n - Direction.offsetX[direction], n2, n3 - Direction.offsetZ[direction]);
            int direction2 = getDirection(blockMetadata);
            return direction2 != direction;
        } else {
            return false;
        }
    }

    public int setTickDelay(int n) {
        return this.computeInt(n);
    }

    public abstract int computeInt(int n);

    public abstract BlockRedstoneDiode getBlockPowered();

    public abstract BlockRedstoneDiode getBlockUnpowered();

    @Override
    public boolean isAssociatedBlock(Block other) {
        return this.isComparator(other);
    }
}
