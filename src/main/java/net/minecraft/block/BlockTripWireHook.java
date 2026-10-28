package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWireHook extends Block {
    public BlockTripWireHook() {
        super(Material.circuits);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setTickRandomly(true);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return null;
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
        return 29;
    }

    @Override
    public int tickRate(World worldIn) {
        return 10;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, int x, int y, int z, int side) {
        return side == 2 && worldIn.getBlock(x, y, z + 1).isNormalCube()
            || side == 3 && worldIn.getBlock(x, y, z - 1).isNormalCube()
            || side == 4 && worldIn.getBlock(x + 1, y, z).isNormalCube()
            || side == 5 && worldIn.getBlock(x - 1, y, z).isNormalCube();
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
        byte b = 0;
        if (side == 2 && worldIn.isBlockNormalCubeDefault(x, y, z + 1, true)) {
            b = 2;
        }

        if (side == 3 && worldIn.isBlockNormalCubeDefault(x, y, z - 1, true)) {
            b = 0;
        }

        if (side == 4 && worldIn.isBlockNormalCubeDefault(x + 1, y, z, true)) {
            b = 1;
        }

        if (side == 5 && worldIn.isBlockNormalCubeDefault(x - 1, y, z, true)) {
            b = 3;
        }

        return b;
    }

    @Override
    public void onPostBlockPlaced(World worldIn, int x, int y, int z, int meta) {
        this.handleWorld(worldIn, x, y, z, false, meta, false, -1, 0);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (neighbor != this && this.checkWorld(worldIn, x, y, z)) {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            int n = blockMetadata & 3;
            boolean flag = !worldIn.getBlock(x - 1, y, z).isNormalCube() && n == 3;
            if (!worldIn.getBlock(x + 1, y, z).isNormalCube() && n == 1) {
                flag = true;
            }

            if (!worldIn.getBlock(x, y, z - 1).isNormalCube() && n == 0) {
                flag = true;
            }

            if (!worldIn.getBlock(x, y, z + 1).isNormalCube() && n == 2) {
                flag = true;
            }

            if (flag) {
                this.dropBlockAsItem(worldIn, x, y, z, blockMetadata, 0);
                worldIn.setBlockToAir(x, y, z);
            }
        }
    }

    public void handleWorld(World world, int n, int n2, int n3, boolean flag, int n4, boolean flag2, int n5, int n6) {
        int n7 = n4 & 3;
        boolean flag3 = (n4 & 4) == 4;
        boolean flag4 = (n4 & 8) == 8;
        boolean flag5 = !flag;
        boolean flag6 = false;
        boolean flag7 = !World.doesBlockHaveSolidTopSurface(world, n, n2 - 1, n3);
        int n8 = Direction.offsetX[n7];
        int n9 = Direction.offsetZ[n7];
        int n10 = 0;
        int[] ints = new int[42];

        for (int i = 1; i < 42; i++) {
            int n11 = n + n8 * i;
            int n12 = n3 + n9 * i;
            Block block = world.getBlock(n11, n2, n12);
            if (block == Blocks.tripwire_hook) {
                int blockMetadata = world.getBlockMetadata(n11, n2, n12);
                if ((blockMetadata & 3) == Direction.rotateOpposite[n7]) {
                    n10 = i;
                }
                break;
            }

            if (block != Blocks.tripwire && i != n5) {
                ints[i] = -1;
                flag5 = false;
            } else {
                int n13 = i == n5 ? n6 : world.getBlockMetadata(n11, n2, n12);
                boolean flag8 = (n13 & 8) != 8;
                boolean flag9 = (n13 & 1) == 1;
                boolean flag10 = (n13 & 2) == 2;
                flag5 &= flag10 == flag7;
                flag6 |= flag8 && flag9;
                ints[i] = n13;
                if (i == n5) {
                    world.scheduleBlockUpdate(n, n2, n3, this, this.tickRate(world));
                    flag5 &= flag8;
                }
            }
        }

        flag5 &= n10 > 1;
        flag6 &= flag5;
        int n11 = (flag5 ? 4 : 0) | (flag6 ? 8 : 0);
        n4 = n7 | n11;
        if (n10 > 0) {
            int n12 = n + n8 * n10;
            int n13 = n3 + n9 * n10;
            int n14 = Direction.rotateOpposite[n7];
            world.setBlockMetadataWithNotify(n12, n2, n13, n14 | n11, 3);
            this.handleWorld(world, n12, n2, n13, n14);
            this.handleWorld(world, n12, n2, n13, flag5, flag6, flag3, flag4);
        }

        this.handleWorld(world, n, n2, n3, flag5, flag6, flag3, flag4);
        if (!flag) {
            world.setBlockMetadataWithNotify(n, n2, n3, n4, 3);
            if (flag2) {
                this.handleWorld(world, n, n2, n3, n7);
            }
        }

        if (flag3 != flag5) {
            for (int i = 1; i < n10; i++) {
                int n12 = n + n8 * i;
                int n13 = n3 + n9 * i;
                int n14 = ints[i];
                if (n14 >= 0) {
                    if (flag5) {
                        n14 |= 4;
                    } else {
                        n14 &= -5;
                    }

                    world.setBlockMetadataWithNotify(n12, n2, n13, n14, 3);
                }
            }
        }
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        this.handleWorld(worldIn, x, y, z, false, worldIn.getBlockMetadata(x, y, z), true, -1, 0);
    }

    public void handleWorld(World world, int n, int n2, int n3, boolean flag, boolean flag2, boolean flag3, boolean flag4) {
        if (flag2 && !flag4) {
            world.playSoundEffect(n + 0.5, n2 + 0.1, n3 + 0.5, "random.click", 0.4F, 0.6F);
        } else if (!flag2 && flag4) {
            world.playSoundEffect(n + 0.5, n2 + 0.1, n3 + 0.5, "random.click", 0.4F, 0.5F);
        } else if (flag && !flag3) {
            world.playSoundEffect(n + 0.5, n2 + 0.1, n3 + 0.5, "random.click", 0.4F, 0.7F);
        } else if (!flag && flag3) {
            world.playSoundEffect(n + 0.5, n2 + 0.1, n3 + 0.5, "random.bowhit", 0.4F, 1.2F / (world.rand.nextFloat() * 0.2F + 0.9F));
        }
    }

    public void handleWorld(World world, int n, int n2, int n3, int n4) {
        world.notifyBlocksOfNeighborChange(n, n2, n3, this);
        if (n4 == 3) {
            world.notifyBlocksOfNeighborChange(n - 1, n2, n3, this);
        } else if (n4 == 1) {
            world.notifyBlocksOfNeighborChange(n + 1, n2, n3, this);
        } else if (n4 == 0) {
            world.notifyBlocksOfNeighborChange(n, n2, n3 - 1, this);
        } else if (n4 == 2) {
            world.notifyBlocksOfNeighborChange(n, n2, n3 + 1, this);
        }
    }

    public boolean checkWorld(World world, int n, int n2, int n3) {
        if (!this.canPlaceBlockAt(world, n, n2, n3)) {
            this.dropBlockAsItem(world, n, n2, n3, world.getBlockMetadata(n, n2, n3), 0);
            world.setBlockToAir(n, n2, n3);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        int n = worldIn.getBlockMetadata(x, y, z) & 3;
        float f = 0.1875F;
        if (n == 3) {
            this.setBlockBounds(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
        } else if (n == 1) {
            this.setBlockBounds(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
        } else if (n == 0) {
            this.setBlockBounds(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
        } else if (n == 2) {
            this.setBlockBounds(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
        }
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        boolean flag = (meta & 4) == 4;
        boolean flag2 = (meta & 8) == 8;
        if (flag || flag2) {
            this.handleWorld(worldIn, x, y, z, true, meta, false, -1, 0);
        }

        if (flag2) {
            worldIn.notifyBlocksOfNeighborChange(x, y, z, this);
            int n = meta & 3;
            if (n == 3) {
                worldIn.notifyBlocksOfNeighborChange(x - 1, y, z, this);
            } else if (n == 1) {
                worldIn.notifyBlocksOfNeighborChange(x + 1, y, z, this);
            } else if (n == 0) {
                worldIn.notifyBlocksOfNeighborChange(x, y, z - 1, this);
            } else if (n == 2) {
                worldIn.notifyBlocksOfNeighborChange(x, y, z + 1, this);
            }
        }

        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        return (worldIn.getBlockMetadata(x, y, z) & 8) == 8 ? 15 : 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        if ((blockMetadata & 8) != 8) {
            return 0;
        }

        int n = blockMetadata & 3;
        return n == 2 && side == 2 ? 15 : (n == 0 && side == 3 ? 15 : (n == 1 && side == 4 ? 15 : (n == 3 && side == 5 ? 15 : 0)));
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }
}
