package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockFalling extends Block {
    public static boolean fallInstantly;

    public BlockFalling() {
        super(Material.sand);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    public BlockFalling(Material material) {
        super(material);
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn));
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn));
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!worldIn.isRemote) {
            this.handleWorld(worldIn, x, y, z);
        }
    }

    public void handleWorld(World world, int n, int n2, int n3) {
        if (canFallBelow(world, n, n2 - 1, n3) && n2 >= 0) {
            byte b = 32;
            if (fallInstantly || !world.checkChunksExist(n - b, n2 - b, n3 - b, n + b, n2 + b, n3 + b)) {
                world.setBlockToAir(n, n2, n3);

                while (canFallBelow(world, n, n2 - 1, n3) && n2 > 0) {
                    n2--;
                }

                if (n2 > 0) {
                    world.setBlock(n, n2, n3, this);
                }
            } else if (!world.isRemote) {
                EntityFallingBlock entityFallingBlock = new EntityFallingBlock(world, n + 0.5F, n2 + 0.5F, n3 + 0.5F, this, world.getBlockMetadata(n, n2, n3));
                this.onStartFalling(entityFallingBlock);
                world.spawnEntityInWorld(entityFallingBlock);
            }
        }
    }

    public void onStartFalling(EntityFallingBlock entityFallingBlock) {
    }

    @Override
    public int tickRate(World worldIn) {
        return 2;
    }

    public static boolean canFallBelow(World world, int n, int n2, int n3) {
        Block block = world.getBlock(n, n2, n3);
        if (block.blockMaterial == Material.air) {
            return true;
        }

        if (block == Blocks.fire) {
            return true;
        }

        Material blockMaterial = block.blockMaterial;
        return blockMaterial == Material.water || blockMaterial == Material.lava;
    }

    public void playSoundWhenFallen(World world, int n, int n2, int n3, int n4) {
    }
}
