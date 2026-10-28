package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockSlab extends Block {
    public boolean isFullBlock;

    public BlockSlab(boolean flag, Material material) {
        super(material);
        this.isFullBlock = flag;
        if (flag) {
            this.fullBlock = true;
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }

        this.setLightOpacity(255);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        if (this.isFullBlock) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            boolean flag = (worldIn.getBlockMetadata(x, y, z) & 8) != 0;
            if (flag) {
                this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
            }
        }
    }

    @Override
    public void setBlockBoundsForItemRender() {
        if (this.isFullBlock) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list, Entity collider) {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
    }

    @Override
    public boolean isOpaqueCube() {
        return this.isFullBlock;
    }

    @Override
    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
        return this.isFullBlock ? meta : (side == 0 || side != 1 && !(subY <= 0.5) ? meta | 8 : meta);
    }

    @Override
    public int quantityDropped(Random random) {
        return this.isFullBlock ? 2 : 1;
    }

    @Override
    public int damageDropped(int meta) {
        return meta & 7;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return this.isFullBlock;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        if (this.isFullBlock) {
            return super.shouldSideBeRendered(worldIn, x, y, z, side);
        }

        if (side != 1 && side != 0 && !super.shouldSideBeRendered(worldIn, x, y, z, side)) {
            return false;
        }

        int n = x + Facing.offsetsXForSide[Facing.oppositeSide[side]];
        int n2 = y + Facing.offsetsYForSide[Facing.oppositeSide[side]];
        int n3 = z + Facing.offsetsZForSide[Facing.oppositeSide[side]];
        boolean flag = (worldIn.getBlockMetadata(n, n2, n3) & 8) != 0;
        return flag
            ? side == 0
                || side == 1 && super.shouldSideBeRendered(worldIn, x, y, z, side)
                || !isSlab(worldIn.getBlock(x, y, z))
                || (worldIn.getBlockMetadata(x, y, z) & 8) == 0
            : side == 1
                || side == 0 && super.shouldSideBeRendered(worldIn, x, y, z, side)
                || !isSlab(worldIn.getBlock(x, y, z))
                || (worldIn.getBlockMetadata(x, y, z) & 8) != 0;
    }

    public static boolean isSlab(Block block) {
        return block == Blocks.stone_slab || block == Blocks.wooden_slab;
    }

    public abstract String getFullSlabName(int n);

    @Override
    public int getDamageValue(World worldIn, int x, int y, int z) {
        return super.getDamageValue(worldIn, x, y, z) & 7;
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return isSlab(this)
            ? Item.getItemFromBlock(this)
            : (
                this == Blocks.double_stone_slab
                    ? Item.getItemFromBlock(Blocks.stone_slab)
                    : (this == Blocks.double_wooden_slab ? Item.getItemFromBlock(Blocks.wooden_slab) : Item.getItemFromBlock(Blocks.stone_slab))
            );
    }
}
