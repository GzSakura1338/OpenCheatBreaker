package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockButton extends Block {
    public boolean wooden;

    public BlockButton(boolean wooden) {
        super(Material.circuits);
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.wooden = wooden;
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return null;
    }

    @Override
    public int tickRate(World worldIn) {
        return this.wooden ? 30 : 20;
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
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        int n = blockMetadata & 8;
        blockMetadata &= 7;
        if (side == 2 && worldIn.getBlock(x, y, z + 1).isNormalCube()) {
            blockMetadata = 4;
        } else if (side == 3 && worldIn.getBlock(x, y, z - 1).isNormalCube()) {
            blockMetadata = 3;
        } else if (side == 4 && worldIn.getBlock(x + 1, y, z).isNormalCube()) {
            blockMetadata = 2;
        } else if (side == 5 && worldIn.getBlock(x - 1, y, z).isNormalCube()) {
            blockMetadata = 1;
        } else {
            blockMetadata = this.findSolidSide(worldIn, x, y, z);
        }

        return blockMetadata + n;
    }

    public int findSolidSide(World worldIn, int x, int y, int z) {
        return worldIn.getBlock(x - 1, y, z).isNormalCube()
            ? 1
            : (
                worldIn.getBlock(x + 1, y, z).isNormalCube()
                    ? 2
                    : (worldIn.getBlock(x, y, z - 1).isNormalCube() ? 3 : (worldIn.getBlock(x, y, z + 1).isNormalCube() ? 4 : 1))
            );
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (this.canStay(worldIn, x, y, z)) {
            int n = worldIn.getBlockMetadata(x, y, z) & 7;
            boolean flag = !worldIn.getBlock(x - 1, y, z).isNormalCube() && n == 1;
            if (!worldIn.getBlock(x + 1, y, z).isNormalCube() && n == 2) {
                flag = true;
            }

            if (!worldIn.getBlock(x, y, z - 1).isNormalCube() && n == 3) {
                flag = true;
            }

            if (!worldIn.getBlock(x, y, z + 1).isNormalCube() && n == 4) {
                flag = true;
            }

            if (flag) {
                this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
                worldIn.setBlockToAir(x, y, z);
            }
        }
    }

    public boolean canStay(World worldIn, int x, int y, int z) {
        if (!this.canPlaceBlockAt(worldIn, x, y, z)) {
            this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
            worldIn.setBlockToAir(x, y, z);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        this.setBlockBoundsFromMeta(blockMetadata);
    }

    public void setBlockBoundsFromMeta(int meta) {
        int n = meta & 7;
        boolean flag = (meta & 8) > 0;
        float f = 0.375F;
        float f2 = 0.625F;
        float f3 = 0.1875F;
        float f4 = 0.125F;
        if (flag) {
            f4 = 0.0625F;
        }

        if (n == 1) {
            this.setBlockBounds(0.0F, f, 0.5F - f3, f4, f2, 0.5F + f3);
        } else if (n == 2) {
            this.setBlockBounds(1.0F - f4, f, 0.5F - f3, 1.0F, f2, 0.5F + f3);
        } else if (n == 3) {
            this.setBlockBounds(0.5F - f3, f, 0.0F, 0.5F + f3, f2, f4);
        } else if (n == 4) {
            this.setBlockBounds(0.5F - f3, f, 1.0F - f4, 0.5F + f3, f2, 1.0F);
        }
    }

    @Override
    public void onBlockClicked(World worldIn, int x, int y, int z, EntityPlayer player) {
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        int n = blockMetadata & 7;
        int n2 = 8 - (blockMetadata & 8);
        if (n2 == 0) {
            return true;
        }

        worldIn.setBlockMetadataWithNotify(x, y, z, n + n2, 3);
        worldIn.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
        worldIn.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.click", 0.3F, 0.6F);
        this.updateNeighbor(worldIn, x, y, z, n);
        worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn));
        return true;
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        if ((meta & 8) > 0) {
            int n = meta & 7;
            this.updateNeighbor(worldIn, x, y, z, n);
        }

        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        return (worldIn.getBlockMetadata(x, y, z) & 8) > 0 ? 15 : 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        if ((blockMetadata & 8) == 0) {
            return 0;
        }

        int n = blockMetadata & 7;
        return n == 5 && side == 1
            ? 15
            : (n == 4 && side == 2 ? 15 : (n == 3 && side == 3 ? 15 : (n == 2 && side == 4 ? 15 : (n == 1 && side == 5 ? 15 : 0))));
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!worldIn.isRemote) {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            if ((blockMetadata & 8) != 0) {
                if (this.wooden) {
                    this.activateButton(worldIn, x, y, z);
                } else {
                    worldIn.setBlockMetadataWithNotify(x, y, z, blockMetadata & 7, 3);
                    int n = blockMetadata & 7;
                    this.updateNeighbor(worldIn, x, y, z, n);
                    worldIn.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.click", 0.3F, 0.5F);
                    worldIn.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
                }
            }
        }
    }

    @Override
    public void setBlockBoundsForItemRender() {
        float f = 0.1875F;
        float f2 = 0.125F;
        float f3 = 0.125F;
        this.setBlockBounds(0.5F - f, 0.5F - f2, 0.5F - f3, 0.5F + f, 0.5F + f2, 0.5F + f3);
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn) {
        if (!worldIn.isRemote && this.wooden && (worldIn.getBlockMetadata(x, y, z) & 8) == 0) {
            this.activateButton(worldIn, x, y, z);
        }
    }

    public void activateButton(World worldIn, int x, int y, int z) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        int n = blockMetadata & 7;
        boolean flag = (blockMetadata & 8) != 0;
        this.setBlockBoundsFromMeta(blockMetadata);
        List entitiesWithinAABB = worldIn.getEntitiesWithinAABB(
            EntityArrow.class, AxisAlignedBB.getBoundingBox(x + this.minX, y + this.minY, z + this.minZ, x + this.maxX, y + this.maxY, z + this.maxZ)
        );
        boolean flag2 = !entitiesWithinAABB.isEmpty();
        if (flag2 && !flag) {
            worldIn.setBlockMetadataWithNotify(x, y, z, n | 8, 3);
            this.updateNeighbor(worldIn, x, y, z, n);
            worldIn.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            worldIn.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.click", 0.3F, 0.6F);
        }

        if (!flag2 && flag) {
            worldIn.setBlockMetadataWithNotify(x, y, z, n, 3);
            this.updateNeighbor(worldIn, x, y, z, n);
            worldIn.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
            worldIn.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.click", 0.3F, 0.5F);
        }

        if (flag2) {
            worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn));
        }
    }

    public void updateNeighbor(World worldIn, int x, int y, int z, int n) {
        worldIn.notifyBlocksOfNeighborChange(x, y, z, this);
        if (n == 1) {
            worldIn.notifyBlocksOfNeighborChange(x - 1, y, z, this);
        } else if (n == 2) {
            worldIn.notifyBlocksOfNeighborChange(x + 1, y, z, this);
        } else if (n == 3) {
            worldIn.notifyBlocksOfNeighborChange(x, y, z - 1, this);
        } else if (n == 4) {
            worldIn.notifyBlocksOfNeighborChange(x, y, z + 1, this);
        } else {
            worldIn.notifyBlocksOfNeighborChange(x, y - 1, z, this);
        }
    }

    @Override
    public void registerIcons(IIconRegister reg) {
    }
}
