package net.minecraft.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockRailPowered extends BlockRailBase {
    public IIcon iIcon2;

    public BlockRailPowered() {
        super(true);
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return (meta & 8) == 0 ? this.blockIcon : this.iIcon2;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        super.registerIcons(reg);
        this.iIcon2 = reg.registerIcon(this.getTextureName() + "_powered");
    }

    public boolean checkWorld(World world, int n, int n2, int n3, int n4, boolean flag, int n5) {
        if (n5 >= 8) {
            return false;
        }

        int n6 = n4 & 7;
        boolean flag2 = true;
        switch (n6) {
            case 0:
                if (flag) {
                    n3++;
                } else {
                    n3--;
                }
                break;
            case 1:
                if (flag) {
                    n--;
                } else {
                    n++;
                }
                break;
            case 2:
                if (flag) {
                    n--;
                } else {
                    n++;
                    n2++;
                    flag2 = false;
                }

                n6 = 1;
                break;
            case 3:
                if (flag) {
                    n--;
                    n2++;
                    flag2 = false;
                } else {
                    n++;
                }

                n6 = 1;
                break;
            case 4:
                if (flag) {
                    n3++;
                } else {
                    n3--;
                    n2++;
                    flag2 = false;
                }

                n6 = 0;
                break;
            case 5:
                if (flag) {
                    n3++;
                    n2++;
                    flag2 = false;
                } else {
                    n3--;
                }

                n6 = 0;
        }

        return this.checkWorld(world, n, n2, n3, flag, n5, n6) || flag2 && this.checkWorld(world, n, n2 - 1, n3, flag, n5, n6);
    }

    public boolean checkWorld(World world, int n, int n2, int n3, boolean flag, int n4, int n5) {
        Block block = world.getBlock(n, n2, n3);
        if (block == this) {
            int blockMetadata = world.getBlockMetadata(n, n2, n3);
            int n6 = blockMetadata & 7;
            if (n5 == 1 && (n6 == 0 || n6 == 4 || n6 == 5)) {
                return false;
            }

            if (n5 == 0 && (n6 == 1 || n6 == 2 || n6 == 3)) {
                return false;
            }

            if ((blockMetadata & 8) != 0) {
                if (world.isBlockIndirectlyGettingPowered(n, n2, n3)) {
                    return true;
                }

                return this.checkWorld(world, n, n2, n3, blockMetadata, flag, n4 + 1);
            }
        }

        return false;
    }

    @Override
    public void onRedstoneSignal(World world, int n, int n2, int n3, int n4, int n5, Block block) {
        boolean isBlockIndirectlyGettingPowered = world.isBlockIndirectlyGettingPowered(n, n2, n3);
        isBlockIndirectlyGettingPowered = isBlockIndirectlyGettingPowered || this.checkWorld(world, n, n2, n3, n4, true, 0) || this.checkWorld(world, n, n2, n3, n4, false, 0);
        boolean flag = false;
        if (isBlockIndirectlyGettingPowered && (n4 & 8) == 0) {
            world.setBlockMetadataWithNotify(n, n2, n3, n5 | 8, 3);
            flag = true;
        } else if (!isBlockIndirectlyGettingPowered && (n4 & 8) != 0) {
            world.setBlockMetadataWithNotify(n, n2, n3, n5, 3);
            flag = true;
        }

        if (flag) {
            world.notifyBlocksOfNeighborChange(n, n2 - 1, n3, this);
            if (n5 == 2 || n5 == 3 || n5 == 4 || n5 == 5) {
                world.notifyBlocksOfNeighborChange(n, n2 + 1, n3, this);
            }
        }
    }
}
