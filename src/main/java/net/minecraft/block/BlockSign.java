package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSign extends BlockContainer {
    public boolean flag;
    public Class blockEntityClass;

    public BlockSign(Class clazz, boolean flag) {
        super(Material.wood);
        this.flag = flag;
        this.blockEntityClass = clazz;
        float f = 0.25F;
        float f2 = 1.0F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f2, 0.5F + f);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return Blocks.planks.getBlockTextureFromSide(side);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return null;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.getSelectedBoundingBoxFromPool(worldIn, x, y, z);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        if (!this.flag) {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            float f = 0.28125F;
            float f2 = 0.78125F;
            float f3 = 0.0F;
            float f4 = 1.0F;
            float f5 = 0.125F;
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            if (blockMetadata == 2) {
                this.setBlockBounds(f3, f, 1.0F - f5, f4, f2, 1.0F);
            }

            if (blockMetadata == 3) {
                this.setBlockBounds(f3, f, 0.0F, f4, f2, f5);
            }

            if (blockMetadata == 4) {
                this.setBlockBounds(1.0F - f5, f, f3, 1.0F, f2, f4);
            }

            if (blockMetadata == 5) {
                this.setBlockBounds(0.0F, f, f3, f5, f2, f4);
            }
        }
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        try {
            return (TileEntity)this.blockEntityClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Items.sign;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        boolean flag = false;
        if (this.flag) {
            if (!worldIn.getBlock(x, y - 1, z).getMaterial().isSolid()) {
                flag = true;
            }
        } else {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            flag = blockMetadata != 2 || !worldIn.getBlock(x, y, z + 1).getMaterial().isSolid();
            if (blockMetadata == 3 && worldIn.getBlock(x, y, z - 1).getMaterial().isSolid()) {
                flag = false;
            }

            if (blockMetadata == 4 && worldIn.getBlock(x + 1, y, z).getMaterial().isSolid()) {
                flag = false;
            }

            if (blockMetadata == 5 && worldIn.getBlock(x - 1, y, z).getMaterial().isSolid()) {
                flag = false;
            }
        }

        if (flag) {
            this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
            worldIn.setBlockToAir(x, y, z);
        }

        super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Items.sign;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
    }
}
