package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class BlockDynamicLiquid extends BlockLiquid {
    public int neighborSourceBlocks;
    public boolean[] booleanArray = new boolean[4];
    public int[] intArray = new int[4];

    public BlockDynamicLiquid(Material material) {
        super(material);
    }

    public void handleWorld3(World world, int n, int n2, int n3) {
        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        world.setBlock(n, n2, n3, Block.getBlockById(Block.getIdFromBlock(this) + 1), blockMetadata, 2);
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        int computeInt = this.computeInt(worldIn, x, y, z);
        byte b = 1;
        if (this.blockMaterial == Material.lava && !worldIn.provider.isHellWorld) {
            b = 2;
        }

        boolean flag = true;
        int tickRate = this.tickRate(worldIn);
        if (computeInt > 0) {
            byte b2 = -100;
            this.neighborSourceBlocks = 0;
            int computeInt2 = this.computeInt(worldIn, x - 1, y, z, b2);
            computeInt2 = this.computeInt(worldIn, x + 1, y, z, computeInt2);
            computeInt2 = this.computeInt(worldIn, x, y, z - 1, computeInt2);
            computeInt2 = this.computeInt(worldIn, x, y, z + 1, computeInt2);
            int n = computeInt2 + b;
            if (n >= 8 || computeInt2 < 0) {
                n = -1;
            }

            if (this.computeInt(worldIn, x, y + 1, z) >= 0) {
                int computeInt3 = this.computeInt(worldIn, x, y + 1, z);
                if (computeInt3 >= 8) {
                    n = computeInt3;
                } else {
                    n = computeInt3 + 8;
                }
            }

            if (this.neighborSourceBlocks >= 2 && this.blockMaterial == Material.water) {
                if (worldIn.getBlock(x, y - 1, z).getMaterial().isSolid()) {
                    n = 0;
                } else if (worldIn.getBlock(x, y - 1, z).getMaterial() == this.blockMaterial && worldIn.getBlockMetadata(x, y - 1, z) == 0) {
                    n = 0;
                }
            }

            if (this.blockMaterial == Material.lava && computeInt < 8 && n < 8 && n > computeInt && random.nextInt(4) != 0) {
                tickRate *= 4;
            }

            if (n == computeInt) {
                if (flag) {
                    this.handleWorld3(worldIn, x, y, z);
                }
            } else {
                computeInt = n;
                if (n < 0) {
                    worldIn.setBlockToAir(x, y, z);
                } else {
                    worldIn.setBlockMetadataWithNotify(x, y, z, n, 2);
                    worldIn.scheduleBlockUpdate(x, y, z, this, tickRate);
                    worldIn.notifyBlocksOfNeighborChange(x, y, z, this);
                }
            }
        } else {
            this.handleWorld3(worldIn, x, y, z);
        }

        if (this.checkWorld2(worldIn, x, y - 1, z)) {
            if (this.blockMaterial == Material.lava && worldIn.getBlock(x, y - 1, z).getMaterial() == Material.water) {
                worldIn.setBlock(x, y - 1, z, Blocks.stone);
                this.handleWorld2(worldIn, x, y - 1, z);
                return;
            }

            if (computeInt >= 8) {
                this.handleWorld(worldIn, x, y - 1, z, computeInt);
            } else {
                this.handleWorld(worldIn, x, y - 1, z, computeInt + 8);
            }
        } else if (computeInt >= 0 && (computeInt == 0 || this.checkWorld(worldIn, x, y - 1, z))) {
            boolean[] booleanArray = this.getBooleanArray(worldIn, x, y, z);
            int n = computeInt + b;
            if (computeInt >= 8) {
                n = 1;
            }

            if (n >= 8) {
                return;
            }

            if (booleanArray[0]) {
                this.handleWorld(worldIn, x - 1, y, z, n);
            }

            if (booleanArray[1]) {
                this.handleWorld(worldIn, x + 1, y, z, n);
            }

            if (booleanArray[2]) {
                this.handleWorld(worldIn, x, y, z - 1, n);
            }

            if (booleanArray[3]) {
                this.handleWorld(worldIn, x, y, z + 1, n);
            }
        }
    }

    public void handleWorld(World world, int n, int n2, int n3, int n4) {
        if (this.checkWorld2(world, n, n2, n3)) {
            Block block = world.getBlock(n, n2, n3);
            if (this.blockMaterial == Material.lava) {
                this.handleWorld2(world, n, n2, n3);
            } else {
                block.dropBlockAsItem(world, n, n2, n3, world.getBlockMetadata(n, n2, n3), 0);
            }

            world.setBlock(n, n2, n3, this, n4, 3);
        }
    }

    public int computeInt(World world, int n, int n2, int n3, int n4, int n5) {
        int n6 = 1000;

        for (int i = 0; i < 4; i++) {
            if ((i != 0 || n5 != 1) && (i != 1 || n5 != 0) && (i != 2 || n5 != 3) && (i != 3 || n5 != 2)) {
                int n7 = n;
                int n8 = n3;
                if (i == 0) {
                    n7 = n - 1;
                }

                if (i == 1) {
                    n7++;
                }

                if (i == 2) {
                    n8 = n3 - 1;
                }

                if (i == 3) {
                    n8++;
                }

                if (!this.checkWorld(world, n7, n2, n8)
                    && (world.getBlock(n7, n2, n8).getMaterial() != this.blockMaterial || world.getBlockMetadata(n7, n2, n8) != 0)) {
                    if (!this.checkWorld(world, n7, n2 - 1, n8)) {
                        return n4;
                    }

                    if (n4 < 4) {
                        int computeInt = this.computeInt(world, n7, n2, n8, n4 + 1, i);
                        if (computeInt < n6) {
                            n6 = computeInt;
                        }
                    }
                }
            }
        }

        return n6;
    }

    public boolean[] getBooleanArray(World world, int n, int n2, int n3) {
        for (int i = 0; i < 4; i++) {
            this.intArray[i] = 1000;
            int n4 = n;
            int n5 = n3;
            if (i == 0) {
                n4 = n - 1;
            }

            if (i == 1) {
                n4++;
            }

            if (i == 2) {
                n5 = n3 - 1;
            }

            if (i == 3) {
                n5++;
            }

            if (!this.checkWorld(world, n4, n2, n5)
                && (world.getBlock(n4, n2, n5).getMaterial() != this.blockMaterial || world.getBlockMetadata(n4, n2, n5) != 0)) {
                if (this.checkWorld(world, n4, n2 - 1, n5)) {
                    this.intArray[i] = this.computeInt(world, n4, n2, n5, 1, i);
                } else {
                    this.intArray[i] = 0;
                }
            }
        }

        int n4 = this.intArray[0];

        for (int i = 1; i < 4; i++) {
            if (this.intArray[i] < n4) {
                n4 = this.intArray[i];
            }
        }

        for (int i = 0; i < 4; i++) {
            this.booleanArray[i] = this.intArray[i] == n4;
        }

        return this.booleanArray;
    }

    public boolean checkWorld(World world, int n, int n2, int n3) {
        Block block = world.getBlock(n, n2, n3);
        return block == Blocks.wooden_door
            || block == Blocks.iron_door
            || block == Blocks.standing_sign
            || block == Blocks.ladder
            || block == Blocks.reeds
            || block.blockMaterial == Material.portal
            || block.blockMaterial.blocksMovement();
    }

    public int computeInt(World world, int n, int n2, int n3, int n4) {
        int computeInt = this.computeInt(world, n, n2, n3);
        if (computeInt < 0) {
            return n4;
        }

        if (computeInt == 0) {
            this.neighborSourceBlocks++;
        }

        if (computeInt >= 8) {
            computeInt = 0;
        }

        return n4 >= 0 && computeInt >= n4 ? n4 : computeInt;
    }

    public boolean checkWorld2(World world, int n, int n2, int n3) {
        Material material = world.getBlock(n, n2, n3).getMaterial();
        return material != this.blockMaterial && material != Material.lava && !this.checkWorld(world, n, n2, n3);
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        super.onBlockAdded(worldIn, x, y, z);
        if (worldIn.getBlock(x, y, z) == this) {
            worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn));
        }
    }

    @Override
    public boolean requiresUpdates() {
        return true;
    }
}
