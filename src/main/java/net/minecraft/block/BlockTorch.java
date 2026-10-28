package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockTorch extends Block {
    public BlockTorch() {
        super(Material.circuits);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
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
        return 2;
    }

    public boolean canPlaceTorchOn(World world, int n, int n2, int n3) {
        if (World.doesBlockHaveSolidTopSurface(world, n, n2, n3)) {
            return true;
        }

        Block block = world.getBlock(n, n2, n3);
        return block == Blocks.fence || block == Blocks.nether_brick_fence || block == Blocks.glass || block == Blocks.cobblestone_wall;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return worldIn.isBlockNormalCubeDefault(x - 1, y, z, true)
            || worldIn.isBlockNormalCubeDefault(x + 1, y, z, true)
            || worldIn.isBlockNormalCubeDefault(x, y, z - 1, true)
            || worldIn.isBlockNormalCubeDefault(x, y, z + 1, true)
            || this.canPlaceTorchOn(worldIn, x, y - 1, z);
    }

    @Override
    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
        int meta2 = meta;
        if (side == 1 && this.canPlaceTorchOn(worldIn, x, y - 1, z)) {
            meta2 = 5;
        }

        if (side == 2 && worldIn.isBlockNormalCubeDefault(x, y, z + 1, true)) {
            meta2 = 4;
        }

        if (side == 3 && worldIn.isBlockNormalCubeDefault(x, y, z - 1, true)) {
            meta2 = 3;
        }

        if (side == 4 && worldIn.isBlockNormalCubeDefault(x + 1, y, z, true)) {
            meta2 = 2;
        }

        if (side == 5 && worldIn.isBlockNormalCubeDefault(x - 1, y, z, true)) {
            meta2 = 1;
        }

        return meta2;
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        super.updateTick(worldIn, x, y, z, random);
        if (worldIn.getBlockMetadata(x, y, z) == 0) {
            this.onBlockAdded(worldIn, x, y, z);
        }
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        if (worldIn.getBlockMetadata(x, y, z) == 0) {
            if (worldIn.isBlockNormalCubeDefault(x - 1, y, z, true)) {
                worldIn.setBlockMetadataWithNotify(x, y, z, 1, 2);
            } else if (worldIn.isBlockNormalCubeDefault(x + 1, y, z, true)) {
                worldIn.setBlockMetadataWithNotify(x, y, z, 2, 2);
            } else if (worldIn.isBlockNormalCubeDefault(x, y, z - 1, true)) {
                worldIn.setBlockMetadataWithNotify(x, y, z, 3, 2);
            } else if (worldIn.isBlockNormalCubeDefault(x, y, z + 1, true)) {
                worldIn.setBlockMetadataWithNotify(x, y, z, 4, 2);
            } else if (this.canPlaceTorchOn(worldIn, x, y - 1, z)) {
                worldIn.setBlockMetadataWithNotify(x, y, z, 5, 2);
            }
        }

        this.dropTorchIfCantStay(worldIn, x, y, z);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        this.checkWorld(worldIn, x, y, z, neighbor);
    }

    public boolean checkWorld(World world, int n, int n2, int n3, Block block) {
        if (!this.dropTorchIfCantStay(world, n, n2, n3)) {
            return true;
        }

        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        boolean flag = !world.isBlockNormalCubeDefault(n - 1, n2, n3, true) && blockMetadata == 1;
        if (!world.isBlockNormalCubeDefault(n + 1, n2, n3, true) && blockMetadata == 2) {
            flag = true;
        }

        if (!world.isBlockNormalCubeDefault(n, n2, n3 - 1, true) && blockMetadata == 3) {
            flag = true;
        }

        if (!world.isBlockNormalCubeDefault(n, n2, n3 + 1, true) && blockMetadata == 4) {
            flag = true;
        }

        if (!this.canPlaceTorchOn(world, n, n2 - 1, n3) && blockMetadata == 5) {
            flag = true;
        }

        if (flag) {
            this.dropBlockAsItem(world, n, n2, n3, world.getBlockMetadata(n, n2, n3), 0);
            world.setBlockToAir(n, n2, n3);
            return true;
        } else {
            return false;
        }
    }

    public boolean dropTorchIfCantStay(World world, int n, int n2, int n3) {
        if (!this.canPlaceBlockAt(world, n, n2, n3)) {
            if (world.getBlock(n, n2, n3) == this) {
                this.dropBlockAsItem(world, n, n2, n3, world.getBlockMetadata(n, n2, n3), 0);
                world.setBlockToAir(n, n2, n3);
            }

            return false;
        } else {
            return true;
        }
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World worldIn, int x, int y, int z, Vec3 startVec, Vec3 endVec) {
        int n = worldIn.getBlockMetadata(x, y, z) & 7;
        float f = 0.15F;
        if (n == 1) {
            this.setBlockBounds(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
        } else if (n == 2) {
            this.setBlockBounds(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
        } else if (n == 3) {
            this.setBlockBounds(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
        } else if (n == 4) {
            this.setBlockBounds(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
        } else {
            f = 0.1F;
            this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
        }

        return super.collisionRayTrace(worldIn, x, y, z, startVec, endVec);
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        double d = x + 0.5F;
        double d2 = y + 0.7F;
        double d3 = z + 0.5F;
        double d4 = 0.22F;
        double d5 = 0.27F;
        if (blockMetadata == 1) {
            worldIn.spawnParticle("smoke", d - d5, d2 + d4, d3, 0.0, 0.0, 0.0);
            worldIn.spawnParticle("flame", d - d5, d2 + d4, d3, 0.0, 0.0, 0.0);
        } else if (blockMetadata == 2) {
            worldIn.spawnParticle("smoke", d + d5, d2 + d4, d3, 0.0, 0.0, 0.0);
            worldIn.spawnParticle("flame", d + d5, d2 + d4, d3, 0.0, 0.0, 0.0);
        } else if (blockMetadata == 3) {
            worldIn.spawnParticle("smoke", d, d2 + d4, d3 - d5, 0.0, 0.0, 0.0);
            worldIn.spawnParticle("flame", d, d2 + d4, d3 - d5, 0.0, 0.0, 0.0);
        } else if (blockMetadata == 4) {
            worldIn.spawnParticle("smoke", d, d2 + d4, d3 + d5, 0.0, 0.0, 0.0);
            worldIn.spawnParticle("flame", d, d2 + d4, d3 + d5, 0.0, 0.0, 0.0);
        } else {
            worldIn.spawnParticle("smoke", d, d2, d3, 0.0, 0.0, 0.0);
            worldIn.spawnParticle("flame", d, d2, d3, 0.0, 0.0, 0.0);
        }
    }
}
