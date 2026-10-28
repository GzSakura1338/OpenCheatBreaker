package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTrapDoor extends Block {
    public BlockTrapDoor(Material material) {
        super(material);
        float f = 0.5F;
        float f2 = 1.0F;
        this.setBlockBounds(0.5F - f, 0.0F, 0.5F - f, 0.5F + f, f2, 0.5F + f);
        this.setCreativeTab(CreativeTabs.tabRedstone);
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
    public boolean isPassable(IBlockAccess worldIn, int x, int y, int z) {
        return !isTrapdoorOpen(worldIn.getBlockMetadata(x, y, z));
    }

    @Override
    public int getRenderType() {
        return 0;
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
        this.process(worldIn.getBlockMetadata(x, y, z));
    }

    @Override
    public void setBlockBoundsForItemRender() {
        float f = 0.1875F;
        this.setBlockBounds(0.0F, 0.5F - f / 2.0F, 0.0F, 1.0F, 0.5F + f / 2.0F, 1.0F);
    }

    public void process(int n) {
        float f = 0.1875F;
        if ((n & 8) != 0) {
            this.setBlockBounds(0.0F, 1.0F - f, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, f, 1.0F);
        }

        if (isTrapdoorOpen(n)) {
            if ((n & 3) == 0) {
                this.setBlockBounds(0.0F, 0.0F, 1.0F - f, 1.0F, 1.0F, 1.0F);
            }

            if ((n & 3) == 1) {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, f);
            }

            if ((n & 3) == 2) {
                this.setBlockBounds(1.0F - f, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }

            if ((n & 3) == 3) {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, f, 1.0F, 1.0F);
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

        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        worldIn.setBlockMetadataWithNotify(x, y, z, blockMetadata ^ 4, 2);
        worldIn.playAuxSFXAtEntity(player, 1003, x, y, z, 0);
        return true;
    }

    public void handleWorld(World world, int n, int n2, int n3, boolean flag) {
        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        boolean flag2 = (blockMetadata & 4) > 0;
        if (flag2 != flag) {
            world.setBlockMetadataWithNotify(n, n2, n3, blockMetadata ^ 4, 2);
            world.playAuxSFXAtEntity(null, 1003, n, n2, n3, 0);
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!worldIn.isRemote) {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            int n = x;
            int n2 = z;
            if ((blockMetadata & 3) == 0) {
                n2 = z + 1;
            }

            if ((blockMetadata & 3) == 1) {
                n2--;
            }

            if ((blockMetadata & 3) == 2) {
                n = x + 1;
            }

            if ((blockMetadata & 3) == 3) {
                n--;
            }

            if (!isValidSupportBlock(worldIn.getBlock(n, y, n2))) {
                worldIn.setBlockToAir(x, y, z);
                this.dropBlockAsItem(worldIn, x, y, z, blockMetadata, 0);
            }

            boolean isBlockIndirectlyGettingPowered = worldIn.isBlockIndirectlyGettingPowered(x, y, z);
            if (isBlockIndirectlyGettingPowered || neighbor.canProvidePower()) {
                this.handleWorld(worldIn, x, y, z, isBlockIndirectlyGettingPowered);
            }
        }
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World worldIn, int x, int y, int z, Vec3 startVec, Vec3 endVec) {
        this.setBlockBoundsBasedOnState(worldIn, x, y, z);
        return super.collisionRayTrace(worldIn, x, y, z, startVec, endVec);
    }

    @Override
    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
        int n = 0;
        if (side == 2) {
            n = 0;
        }

        if (side == 3) {
            n = 1;
        }

        if (side == 4) {
            n = 2;
        }

        if (side == 5) {
            n = 3;
        }

        if (side != 1 && side != 0 && subY > 0.5F) {
            n |= 8;
        }

        return n;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, int x, int y, int z, int side) {
        if (side == 0) {
            return false;
        }

        if (side == 1) {
            return false;
        }

        if (side == 2) {
            z++;
        }

        if (side == 3) {
            z--;
        }

        if (side == 4) {
            x++;
        }

        if (side == 5) {
            x--;
        }

        return isValidSupportBlock(worldIn.getBlock(x, y, z));
    }

    public static boolean isTrapdoorOpen(int n) {
        return (n & 4) != 0;
    }

    public static boolean isValidSupportBlock(Block block) {
        return block.blockMaterial.isOpaque() && block.renderAsNormalBlock()
            || block == Blocks.glowstone
            || block instanceof BlockSlab
            || block instanceof BlockStairs;
    }
}
