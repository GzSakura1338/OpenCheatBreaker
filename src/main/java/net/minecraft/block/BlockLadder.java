package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLadder extends Block {
    public BlockLadder() {
        super(Material.circuits);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.getSelectedBoundingBoxFromPool(worldIn, x, y, z);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        this.process(worldIn.getBlockMetadata(x, y, z));
    }

    public void process(int n) {
        float f = 0.125F;
        if (n == 2) {
            this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
        }

        if (n == 3) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
        }

        if (n == 4) {
            this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }

        if (n == 5) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
        }
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
    public int getRenderType() {
        return 8;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return worldIn.getBlock(x - 1, y, z).isNormalCube()
            || worldIn.getBlock(x + 1, y, z).isNormalCube()
            || worldIn.getBlock(x, y, z - 1).isNormalCube()
            || worldIn.getBlock(x, y, z + 1).isNormalCube();
    }

    @Override
    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
        int meta2 = meta;
        if ((meta == 0 || side == 2) && worldIn.getBlock(x, y, z + 1).isNormalCube()) {
            meta2 = 2;
        }

        if ((meta2 == 0 || side == 3) && worldIn.getBlock(x, y, z - 1).isNormalCube()) {
            meta2 = 3;
        }

        if ((meta2 == 0 || side == 4) && worldIn.getBlock(x + 1, y, z).isNormalCube()) {
            meta2 = 4;
        }

        if ((meta2 == 0 || side == 5) && worldIn.getBlock(x - 1, y, z).isNormalCube()) {
            meta2 = 5;
        }

        return meta2;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        boolean flag = blockMetadata == 2 && worldIn.getBlock(x, y, z + 1).isNormalCube();
        if (blockMetadata == 3 && worldIn.getBlock(x, y, z - 1).isNormalCube()) {
            flag = true;
        }

        if (blockMetadata == 4 && worldIn.getBlock(x + 1, y, z).isNormalCube()) {
            flag = true;
        }

        if (blockMetadata == 5 && worldIn.getBlock(x - 1, y, z).isNormalCube()) {
            flag = true;
        }

        if (!flag) {
            this.dropBlockAsItem(worldIn, x, y, z, blockMetadata, 0);
            worldIn.setBlockToAir(x, y, z);
        }

        super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
    }

    @Override
    public int quantityDropped(Random random) {
        return 1;
    }
}
