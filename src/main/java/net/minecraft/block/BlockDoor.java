package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.IconFlipped;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDoor extends Block {
    public IIcon[] iIconArray;
    public IIcon[] iIconArray2;

    public BlockDoor(Material material) {
        super(material);
        float f = 0.5F;
        float f2 = 1.0F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f2, 0.5F + f);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return this.iIconArray2[0];
    }

    @Override
    public IIcon getIcon(IBlockAccess worldIn, int x, int y, int z, int side) {
        if (side != 1 && side != 0) {
            int fullMetadata = this.getFullMetadata(worldIn, x, y, z);
            int n = fullMetadata & 3;
            boolean flag = (fullMetadata & 4) != 0;
            boolean flag2 = false;
            boolean flag3 = (fullMetadata & 8) != 0;
            if (flag) {
                if (n == 0 && side == 2) {
                    flag2 = !flag2;
                } else if (n == 1 && side == 5) {
                    flag2 = !flag2;
                } else if (n == 2 && side == 3) {
                    flag2 = !flag2;
                } else if (n == 3 && side == 4) {
                    flag2 = !flag2;
                }
            } else {
                if (n == 0 && side == 5) {
                    flag2 = !flag2;
                } else if (n == 1 && side == 3) {
                    flag2 = !flag2;
                } else if (n == 2 && side == 4) {
                    flag2 = !flag2;
                } else if (n == 3 && side == 2) {
                    flag2 = !flag2;
                }

                if ((fullMetadata & 16) != 0) {
                    flag2 = !flag2;
                }
            }

            return flag3 ? this.iIconArray[flag2 ? 1 : 0] : this.iIconArray2[flag2 ? 1 : 0];
        } else {
            return this.iIconArray2[0];
        }
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iIconArray = new IIcon[2];
        this.iIconArray2 = new IIcon[2];
        this.iIconArray[0] = reg.registerIcon(this.getTextureName() + "_upper");
        this.iIconArray2[0] = reg.registerIcon(this.getTextureName() + "_lower");
        this.iIconArray[1] = new IconFlipped(this.iIconArray[0], true, false);
        this.iIconArray2[1] = new IconFlipped(this.iIconArray2[0], true, false);
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isPassable(IBlockAccess worldIn, int x, int y, int z) {
        int fullMetadata = this.getFullMetadata(worldIn, x, y, z);
        return (fullMetadata & 4) != 0;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int getRenderType() {
        return 7;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.getSelectedBoundingBoxFromPool(worldIn, x, y, z);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.getCollisionBoundingBoxFromPool(worldIn, x, y, z);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        this.setBoundingBoxWithData(this.getFullMetadata(worldIn, x, y, z));
    }

    public int getFullMetadata2(IBlockAccess blockAccess, int n, int n2, int n3) {
        return this.getFullMetadata(blockAccess, n, n2, n3) & 3;
    }

    public boolean getFullMetadata3(IBlockAccess blockAccess, int n, int n2, int n3) {
        return (this.getFullMetadata(blockAccess, n, n2, n3) & 4) != 0;
    }

    public void setBoundingBoxWithData(int n) {
        float f = 0.1875F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 2.0F, 1.0F);
        int n2 = n & 3;
        boolean flag = (n & 4) != 0;
        boolean flag2 = (n & 16) != 0;
        if (n2 == 0) {
            if (flag) {
                if (!flag2) {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                } else {
                    this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                }
            } else {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
            }
        } else if (n2 == 1) {
            if (flag) {
                if (!flag2) {
                    this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                }
            } else {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            }
        } else if (n2 == 2) {
            if (flag) {
                if (!flag2) {
                    this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
                } else {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
                }
            } else {
                this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
        } else if (n2 == 3) {
            if (flag) {
                if (!flag2) {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
                } else {
                    this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
            } else {
                this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            }
        }
    }

    @Override
    public void onBlockClicked(World worldIn, int x, int y, int z, EntityPlayer player) {
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        if (this.blockMaterial == Material.iron) {
            return true;
        }

        int fullMetadata = this.getFullMetadata(worldIn, x, y, z);
        int n = fullMetadata & 7;
        n ^= 4;
        if ((fullMetadata & 8) == 0) {
            worldIn.setBlockMetadataWithNotify(x, y, z, n, 2);
            worldIn.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
        } else {
            worldIn.setBlockMetadataWithNotify(x, y - 1, z, n, 2);
            worldIn.markBlockRangeForRenderUpdate(x, y - 1, z, x, y, z);
        }

        worldIn.playAuxSFXAtEntity(player, 1003, x, y, z, 0);
        return true;
    }

    public void handleWorld(World world, int n, int n2, int n3, boolean flag) {
        int fullMetadata = this.getFullMetadata(world, n, n2, n3);
        boolean flag2 = (fullMetadata & 4) != 0;
        if (flag2 != flag) {
            int n4 = fullMetadata & 7;
            n4 ^= 4;
            if ((fullMetadata & 8) == 0) {
                world.setBlockMetadataWithNotify(n, n2, n3, n4, 2);
                world.markBlockRangeForRenderUpdate(n, n2, n3, n, n2, n3);
            } else {
                world.setBlockMetadataWithNotify(n, n2 - 1, n3, n4, 2);
                world.markBlockRangeForRenderUpdate(n, n2 - 1, n3, n, n2, n3);
            }

            world.playAuxSFXAtEntity(null, 1003, n, n2, n3, 0);
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        if ((blockMetadata & 8) == 0) {
            boolean flag = false;
            if (worldIn.getBlock(x, y + 1, z) != this) {
                worldIn.setBlockToAir(x, y, z);
                flag = true;
            }

            if (!World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z)) {
                worldIn.setBlockToAir(x, y, z);
                flag = true;
                if (worldIn.getBlock(x, y + 1, z) == this) {
                    worldIn.setBlockToAir(x, y + 1, z);
                }
            }

            if (flag) {
                if (!worldIn.isRemote) {
                    this.dropBlockAsItem(worldIn, x, y, z, blockMetadata, 0);
                }
            } else {
                boolean flag2 = worldIn.isBlockIndirectlyGettingPowered(x, y, z) || worldIn.isBlockIndirectlyGettingPowered(x, y + 1, z);
                if ((flag2 || neighbor.canProvidePower()) && neighbor != this) {
                    this.handleWorld(worldIn, x, y, z, flag2);
                }
            }
        } else {
            if (worldIn.getBlock(x, y - 1, z) != this) {
                worldIn.setBlockToAir(x, y, z);
            }

            if (neighbor != this) {
                this.onNeighborBlockChange(worldIn, x, y - 1, z, neighbor);
            }
        }
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return (meta & 8) != 0 ? null : (this.blockMaterial == Material.iron ? Items.iron_door : Items.wooden_door);
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World worldIn, int x, int y, int z, Vec3 startVec, Vec3 endVec) {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.collisionRayTrace(worldIn, x, y, z, startVec, endVec);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return y < 255
            && World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z)
            && super.canPlaceBlockAt(worldIn, x, y, z)
            && super.canPlaceBlockAt(worldIn, x, y + 1, z);
    }

    @Override
    public int getMobilityFlag() {
        return 1;
    }

    public int getFullMetadata(IBlockAccess blockAccess, int n, int n2, int n3) {
        int blockMetadata = blockAccess.getBlockMetadata(n, n2, n3);
        boolean flag = (blockMetadata & 8) != 0;
        int n4;
        int n5;
        if (flag) {
            n4 = blockAccess.getBlockMetadata(n, n2 - 1, n3);
            n5 = blockMetadata;
        } else {
            n4 = blockMetadata;
            n5 = blockAccess.getBlockMetadata(n, n2 + 1, n3);
        }

        boolean flag2 = (n5 & 1) != 0;
        return n4 & 7 | (flag ? 8 : 0) | (flag2 ? 16 : 0);
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return this.blockMaterial == Material.iron ? Items.iron_door : Items.wooden_door;
    }

    @Override
    public void onBlockHarvested(World worldIn, int x, int y, int z, int meta, EntityPlayer player) {
        if (player.capabilities.isCreativeMode && (meta & 8) != 0 && worldIn.getBlock(x, y - 1, z) == this) {
            worldIn.setBlockToAir(x, y - 1, z);
        }
    }
}
