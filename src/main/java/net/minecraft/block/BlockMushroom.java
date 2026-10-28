package net.minecraft.block;

import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.WorldGenBigMushroom;

public class BlockMushroom extends BlockBush implements IGrowable {
    public BlockMushroom() {
        float f = 0.2F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f * 2.0F, 0.5F + f);
        this.setTickRandomly(true);
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (random.nextInt(25) == 0) {
            byte b = 4;
            int n = 5;

            for (int i = x - b; i <= x + b; i++) {
                for (int j = z - b; j <= z + b; j++) {
                    for (int k = y - 1; k <= y + 1; k++) {
                        if (worldIn.getBlock(i, k, j) == this) {
                            if (--n <= 0) {
                                return;
                            }
                        }
                    }
                }
            }

            int n2 = x + random.nextInt(3) - 1;
            int n3 = y + random.nextInt(2) - random.nextInt(2);
            int n4 = z + random.nextInt(3) - 1;

            for (int i = 0; i < 4; i++) {
                if (worldIn.isAirBlock(n2, n3, n4) && this.canBlockStay(worldIn, n2, n3, n4)) {
                    x = n2;
                    y = n3;
                    z = n4;
                }

                n2 = x + random.nextInt(3) - 1;
                n3 = y + random.nextInt(2) - random.nextInt(2);
                n4 = z + random.nextInt(3) - 1;
            }

            if (worldIn.isAirBlock(n2, n3, n4) && this.canBlockStay(worldIn, n2, n3, n4)) {
                worldIn.setBlock(n2, n3, n4, this, 0, 2);
            }
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return super.canPlaceBlockAt(worldIn, x, y, z) && this.canBlockStay(worldIn, x, y, z);
    }

    @Override
    public boolean canPlaceBlockOn(Block ground) {
        return ground.isFullBlock();
    }

    @Override
    public boolean canBlockStay(World worldIn, int x, int y, int z) {
        if (y >= 0 && y < 256) {
            Block block = worldIn.getBlock(x, y - 1, z);
            return block == Blocks.mycelium
                || block == Blocks.dirt && worldIn.getBlockMetadata(x, y - 1, z) == 2
                || worldIn.getFullBlockLightValue(x, y, z) < 13 && this.canPlaceBlockOn(block);
        } else {
            return false;
        }
    }

    public boolean fertilizeMushroom(World world, int n, int n2, int n3, Random random) {
        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        world.setBlockToAir(n, n2, n3);
        WorldGenBigMushroom worldGenBigMushroom = null;
        if (this == Blocks.brown_mushroom) {
            worldGenBigMushroom = new WorldGenBigMushroom(0);
        } else if (this == Blocks.red_mushroom) {
            worldGenBigMushroom = new WorldGenBigMushroom(1);
        }

        if (worldGenBigMushroom != null && worldGenBigMushroom.generate(world, random, n, n2, n3)) {
            return true;
        }

        world.setBlock(n, n2, n3, this, blockMetadata, 3);
        return false;
    }

    @Override
    public boolean canFertilize(World worldIn, int x, int y, int z, boolean isClient) {
        return true;
    }

    @Override
    public boolean shouldFertilize(World worldIn, Random random, int x, int y, int z) {
        return random.nextFloat() < 0.4;
    }

    @Override
    public void fertilize(World worldIn, Random random, int x, int y, int z) {
        this.fertilizeMushroom(worldIn, x, y, z, random);
    }
}
