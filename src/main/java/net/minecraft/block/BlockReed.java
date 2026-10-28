package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockReed extends Block {
    public BlockReed() {
        super(Material.plants);
        float f = 0.375F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
        this.setTickRandomly(true);
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if ((worldIn.getBlock(x, y - 1, z) == Blocks.reeds || this.checkWorld(worldIn, x, y, z)) && worldIn.isAirBlock(x, y + 1, z)) {
            int n = 1;

            while (worldIn.getBlock(x, y - n, z) == this) {
                n++;
            }

            if (n < 3) {
                int blockMetadata = worldIn.getBlockMetadata(x, y, z);
                if (blockMetadata == 15) {
                    worldIn.setBlock(x, y + 1, z, this);
                    worldIn.setBlockMetadataWithNotify(x, y, z, 0, 4);
                } else {
                    worldIn.setBlockMetadataWithNotify(x, y, z, blockMetadata + 1, 4);
                }
            }
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        Block block = worldIn.getBlock(x, y - 1, z);
        return block == this
            || (block == Blocks.grass || block == Blocks.dirt || block == Blocks.sand)
                && (
                    worldIn.getBlock(x - 1, y - 1, z).getMaterial() == Material.water
                        || worldIn.getBlock(x + 1, y - 1, z).getMaterial() == Material.water
                        || worldIn.getBlock(x, y - 1, z - 1).getMaterial() == Material.water
                        || worldIn.getBlock(x, y - 1, z + 1).getMaterial() == Material.water
                );
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        this.checkWorld(worldIn, x, y, z);
    }

    public boolean checkWorld(World world, int n, int n2, int n3) {
        if (!this.canBlockStay(world, n, n2, n3)) {
            this.dropBlockAsItem(world, n, n2, n3, world.getBlockMetadata(n, n2, n3), 0);
            world.setBlockToAir(n, n2, n3);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean canBlockStay(World worldIn, int x, int y, int z) {
        return this.canPlaceBlockAt(worldIn, x, y, z);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return null;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Items.reeds;
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
        return 1;
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Items.reeds;
    }

    @Override
    public int colorMultiplier(IBlockAccess worldIn, int x, int y, int z) {
        return worldIn.getBiomeGenForCoords(x, z).getBiomeGrassColor(x, y, z);
    }
}
