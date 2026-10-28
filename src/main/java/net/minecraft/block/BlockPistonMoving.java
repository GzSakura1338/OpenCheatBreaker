package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonMoving extends BlockContainer {
    public BlockPistonMoving() {
        super(Material.piston);
        this.setHardness(-1.0F);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        TileEntity tileEntity = worldIn.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityPiston) {
            ((TileEntityPiston)tileEntity).clearPistonTileEntity();
        } else {
            super.breakBlock(worldIn, x, y, z, blockBroken, meta);
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return false;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        if (!worldIn.isRemote && worldIn.getTileEntity(x, y, z) == null) {
            worldIn.setBlockToAir(x, y, z);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return null;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune) {
        if (!worldIn.isRemote) {
            TileEntityPiston tileEntity = this.getTileEntity(worldIn, x, y, z);
            if (tileEntity != null) {
                tileEntity.getStoredBlockID().dropBlockAsItem(worldIn, x, y, z, tileEntity.getBlockMetadata(), 0);
            }
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!worldIn.isRemote) {
            worldIn.getTileEntity(x, y, z);
        }
    }

    public static TileEntity getTileEntity(Block block, int n, int n2, boolean flag, boolean flag2) {
        return new TileEntityPiston(block, n, n2, flag, flag2);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        TileEntityPiston tileEntity = this.getTileEntity(worldIn, x, y, z);
        if (tileEntity == null) {
            return null;
        }

        float amountExtended = tileEntity.getAmountExtended(0.0F);
        if (tileEntity.isExtending()) {
            amountExtended = 1.0F - amountExtended;
        }

        return this.findAxisAlignedBB(worldIn, x, y, z, tileEntity.getStoredBlockID(), amountExtended, tileEntity.getPistonOrientation());
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        TileEntityPiston tileEntity = this.getTileEntity(worldIn, x, y, z);
        if (tileEntity != null) {
            Block storedBlockID = tileEntity.getStoredBlockID();
            if (storedBlockID == this || storedBlockID.getMaterial() == Material.air) {
                return;
            }

            storedBlockID.setBlockBoundsBasedOnState(worldIn, x, y, z);
            float amountExtended = tileEntity.getAmountExtended(0.0F);
            if (tileEntity.isExtending()) {
                amountExtended = 1.0F - amountExtended;
            }

            int pistonOrientation = tileEntity.getPistonOrientation();
            this.minX = storedBlockID.getBlockBoundsMinX() - Facing.offsetsXForSide[pistonOrientation] * amountExtended;
            this.minY = storedBlockID.getBlockBoundsMinY() - Facing.offsetsYForSide[pistonOrientation] * amountExtended;
            this.minZ = storedBlockID.getBlockBoundsMinZ() - Facing.offsetsZForSide[pistonOrientation] * amountExtended;
            this.maxX = storedBlockID.getBlockBoundsMaxX() - Facing.offsetsXForSide[pistonOrientation] * amountExtended;
            this.maxY = storedBlockID.getBlockBoundsMaxY() - Facing.offsetsYForSide[pistonOrientation] * amountExtended;
            this.maxZ = storedBlockID.getBlockBoundsMaxZ() - Facing.offsetsZForSide[pistonOrientation] * amountExtended;
        }
    }

    public AxisAlignedBB findAxisAlignedBB(World world, int n, int n2, int n3, Block block, float f, int n4) {
        if (block != this && block.getMaterial() != Material.air) {
            AxisAlignedBB collisionBoundingBoxFromPool = block.getCollisionBoundingBoxFromPool(world, n, n2, n3);
            if (collisionBoundingBoxFromPool == null) {
                return null;
            }

            if (Facing.offsetsXForSide[n4] < 0) {
                collisionBoundingBoxFromPool.minX = collisionBoundingBoxFromPool.minX - Facing.offsetsXForSide[n4] * f;
            } else {
                collisionBoundingBoxFromPool.maxX = collisionBoundingBoxFromPool.maxX - Facing.offsetsXForSide[n4] * f;
            }

            if (Facing.offsetsYForSide[n4] < 0) {
                collisionBoundingBoxFromPool.minY = collisionBoundingBoxFromPool.minY - Facing.offsetsYForSide[n4] * f;
            } else {
                collisionBoundingBoxFromPool.maxY = collisionBoundingBoxFromPool.maxY - Facing.offsetsYForSide[n4] * f;
            }

            if (Facing.offsetsZForSide[n4] < 0) {
                collisionBoundingBoxFromPool.minZ = collisionBoundingBoxFromPool.minZ - Facing.offsetsZForSide[n4] * f;
            } else {
                collisionBoundingBoxFromPool.maxZ = collisionBoundingBoxFromPool.maxZ - Facing.offsetsZForSide[n4] * f;
            }

            return collisionBoundingBoxFromPool;
        } else {
            return null;
        }
    }

    public TileEntityPiston getTileEntity(IBlockAccess blockAccess, int n, int n2, int n3) {
        TileEntity tileEntity = blockAccess.getTileEntity(n, n2, n3);
        return tileEntity instanceof TileEntityPiston ? (TileEntityPiston)tileEntity : null;
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Item.getItemById(0);
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon("piston_top_normal");
    }
}
