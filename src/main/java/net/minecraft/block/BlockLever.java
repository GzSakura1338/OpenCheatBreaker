package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLever extends Block {
    public BlockLever() {
        super(Material.circuits);
        this.setCreativeTab(CreativeTabs.tabRedstone);
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
        return 12;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, int x, int y, int z, int side) {
        return side == 0 && worldIn.getBlock(x, y + 1, z).isNormalCube()
            || side == 1 && World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z)
            || side == 2 && worldIn.getBlock(x, y, z + 1).isNormalCube()
            || side == 3 && worldIn.getBlock(x, y, z - 1).isNormalCube()
            || side == 4 && worldIn.getBlock(x + 1, y, z).isNormalCube()
            || side == 5 && worldIn.getBlock(x - 1, y, z).isNormalCube();
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return worldIn.getBlock(x - 1, y, z).isNormalCube()
            || worldIn.getBlock(x + 1, y, z).isNormalCube()
            || worldIn.getBlock(x, y, z - 1).isNormalCube()
            || worldIn.getBlock(x, y, z + 1).isNormalCube()
            || World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z)
            || worldIn.getBlock(x, y + 1, z).isNormalCube();
    }

    @Override
    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
        int n = meta & 8;
        int n2 = meta & 7;
        byte b = -1;
        if (side == 0 && worldIn.getBlock(x, y + 1, z).isNormalCube()) {
            b = 0;
        }

        if (side == 1 && World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z)) {
            b = 5;
        }

        if (side == 2 && worldIn.getBlock(x, y, z + 1).isNormalCube()) {
            b = 4;
        }

        if (side == 3 && worldIn.getBlock(x, y, z - 1).isNormalCube()) {
            b = 3;
        }

        if (side == 4 && worldIn.getBlock(x + 1, y, z).isNormalCube()) {
            b = 2;
        }

        if (side == 5 && worldIn.getBlock(x - 1, y, z).isNormalCube()) {
            b = 1;
        }

        return b + n;
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        int n = blockMetadata & 7;
        int n2 = blockMetadata & 8;
        if (n == invertMetadata(1)) {
            if ((MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5) & 1) == 0) {
                worldIn.setBlockMetadataWithNotify(x, y, z, 5 | n2, 2);
            } else {
                worldIn.setBlockMetadataWithNotify(x, y, z, 6 | n2, 2);
            }
        } else if (n == invertMetadata(0)) {
            if ((MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5) & 1) == 0) {
                worldIn.setBlockMetadataWithNotify(x, y, z, 7 | n2, 2);
            } else {
                worldIn.setBlockMetadataWithNotify(x, y, z, 0 | n2, 2);
            }
        }
    }

    public static int invertMetadata(int n) {
        switch (n) {
            case 0:
                return 0;
            case 1:
                return 5;
            case 2:
                return 4;
            case 3:
                return 3;
            case 4:
                return 2;
            case 5:
                return 1;
            default:
                return -1;
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (this.checkWorld(worldIn, x, y, z)) {
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

            if (!World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) && n == 5) {
                flag = true;
            }

            if (!World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) && n == 6) {
                flag = true;
            }

            if (!worldIn.getBlock(x, y + 1, z).isNormalCube() && n == 0) {
                flag = true;
            }

            if (!worldIn.getBlock(x, y + 1, z).isNormalCube() && n == 7) {
                flag = true;
            }

            if (flag) {
                this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
                worldIn.setBlockToAir(x, y, z);
            }
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
        int n = worldIn.getBlockMetadata(x, y, z) & 7;
        float f = 0.1875F;
        if (n == 1) {
            this.setBlockBounds(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
        } else if (n == 2) {
            this.setBlockBounds(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
        } else if (n == 3) {
            this.setBlockBounds(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
        } else if (n == 4) {
            this.setBlockBounds(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
        } else if (n == 5 || n == 6) {
            f = 0.25F;
            this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, 0.6F, 0.5F + f);
        } else if (n == 0 || n == 7) {
            f = 0.25F;
            this.setBlockBounds(0.5F - f, 0.4F, 0.5F - f, 0.5F + f, 1.0F, 0.5F + f);
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        if (worldIn.isRemote) {
            return true;
        }

        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        int n = blockMetadata & 7;
        int n2 = 8 - (blockMetadata & 8);
        worldIn.setBlockMetadataWithNotify(x, y, z, n + n2, 3);
        worldIn.playSoundEffect(x + 0.5, y + 0.5, z + 0.5, "random.click", 0.3F, n2 > 0 ? 0.6F : 0.5F);
        worldIn.notifyBlocksOfNeighborChange(x, y, z, this);
        if (n == 1) {
            worldIn.notifyBlocksOfNeighborChange(x - 1, y, z, this);
        } else if (n == 2) {
            worldIn.notifyBlocksOfNeighborChange(x + 1, y, z, this);
        } else if (n == 3) {
            worldIn.notifyBlocksOfNeighborChange(x, y, z - 1, this);
        } else if (n == 4) {
            worldIn.notifyBlocksOfNeighborChange(x, y, z + 1, this);
        } else if (n == 5 || n == 6) {
            worldIn.notifyBlocksOfNeighborChange(x, y - 1, z, this);
        } else if (n == 0 || n == 7) {
            worldIn.notifyBlocksOfNeighborChange(x, y + 1, z, this);
        }

        return true;
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        if ((meta & 8) > 0) {
            worldIn.notifyBlocksOfNeighborChange(x, y, z, this);
            int n = meta & 7;
            if (n == 1) {
                worldIn.notifyBlocksOfNeighborChange(x - 1, y, z, this);
            } else if (n == 2) {
                worldIn.notifyBlocksOfNeighborChange(x + 1, y, z, this);
            } else if (n == 3) {
                worldIn.notifyBlocksOfNeighborChange(x, y, z - 1, this);
            } else if (n == 4) {
                worldIn.notifyBlocksOfNeighborChange(x, y, z + 1, this);
            } else if (n == 5 || n == 6) {
                worldIn.notifyBlocksOfNeighborChange(x, y - 1, z, this);
            } else if (n == 0 || n == 7) {
                worldIn.notifyBlocksOfNeighborChange(x, y + 1, z, this);
            }
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
        return n == 0 && side == 0
            ? 15
            : (
                n == 7 && side == 0
                    ? 15
                    : (
                        n == 6 && side == 1
                            ? 15
                            : (
                                n == 5 && side == 1
                                    ? 15
                                    : (n == 4 && side == 2 ? 15 : (n == 3 && side == 3 ? 15 : (n == 2 && side == 4 ? 15 : (n == 1 && side == 5 ? 15 : 0))))
                            )
                    )
            );
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }
}
