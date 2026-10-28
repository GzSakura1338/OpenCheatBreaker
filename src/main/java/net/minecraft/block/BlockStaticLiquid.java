package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockStaticLiquid extends BlockLiquid {
    public BlockStaticLiquid(Material material) {
        super(material);
        this.setTickRandomly(false);
        if (material == Material.lava) {
            this.setTickRandomly(true);
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        super.onNeighborBlockChange(worldIn, x, y, z, neighbor);
        if (worldIn.getBlock(x, y, z) == this) {
            this.setNotStationary(worldIn, x, y, z);
        }
    }

    public void setNotStationary(World world, int n, int n2, int n3) {
        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        world.setBlock(n, n2, n3, Block.getBlockById(Block.getIdFromBlock(this) - 1), blockMetadata, 2);
        world.scheduleBlockUpdate(n, n2, n3, Block.getBlockById(Block.getIdFromBlock(this) - 1), this.tickRate(world));
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (this.blockMaterial == Material.lava) {
            int nextInt = random.nextInt(3);

            for (int i = 0; i < nextInt; i++) {
                x += random.nextInt(3) - 1;
                y++;
                z += random.nextInt(3) - 1;
                Block block = worldIn.getBlock(x, y, z);
                if (block.blockMaterial == Material.air) {
                    if (this.isFlammable(worldIn, x - 1, y, z)
                        || this.isFlammable(worldIn, x + 1, y, z)
                        || this.isFlammable(worldIn, x, y, z - 1)
                        || this.isFlammable(worldIn, x, y, z + 1)
                        || this.isFlammable(worldIn, x, y - 1, z)
                        || this.isFlammable(worldIn, x, y + 1, z)) {
                        worldIn.setBlock(x, y, z, Blocks.fire);
                        return;
                    }
                } else if (block.blockMaterial.blocksMovement()) {
                    return;
                }
            }

            if (nextInt == 0) {
                int n = x;
                int n2 = z;

                for (int i = 0; i < 3; i++) {
                    x = n + random.nextInt(3) - 1;
                    z = n2 + random.nextInt(3) - 1;
                    if (worldIn.isAirBlock(x, y + 1, z) && this.isFlammable(worldIn, x, y, z)) {
                        worldIn.setBlock(x, y + 1, z, Blocks.fire);
                    }
                }
            }
        }
    }

    public boolean isFlammable(World world, int n, int n2, int n3) {
        return world.getBlock(n, n2, n3).getMaterial().getCanBurn();
    }
}
