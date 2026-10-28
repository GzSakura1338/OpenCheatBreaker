package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.MapColor;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockStairs extends Block {
    public static int[][] ignoredCornersByDirection = new int[][]{{2, 6}, {3, 7}, {2, 3}, {6, 7}, {0, 4}, {1, 5}, {0, 1}, {4, 5}};
    public Block block;
    public int intValue;
    public boolean isRayTraced;
    public int rayTracePass;

    public BlockStairs(Block block, int n) {
        super(block.blockMaterial);
        this.block = block;
        this.intValue = n;
        this.setHardness(block.blockHardness);
        this.setResistance(block.blockResistance / 3.0F);
        this.setStepSound(block.stepSound);
        this.setLightOpacity(255);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        if (this.isRayTraced) {
            this.setBlockBounds(
                0.5F * (this.rayTracePass % 2),
                0.5F * (this.rayTracePass / 2 % 2),
                0.5F * (this.rayTracePass / 4 % 2),
                0.5F + 0.5F * (this.rayTracePass % 2),
                0.5F + 0.5F * (this.rayTracePass / 2 % 2),
                0.5F + 0.5F * (this.rayTracePass / 4 % 2)
            );
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        }
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
        return 10;
    }

    public void setBaseBounds(IBlockAccess blockAccess, int n, int n2, int n3) {
        int blockMetadata = blockAccess.getBlockMetadata(n, n2, n3);
        if ((blockMetadata & 4) != 0) {
            this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
    }

    public static boolean isBlockStairs(Block block) {
        return block instanceof BlockStairs;
    }

    public boolean checkIBlockAccess(IBlockAccess blockAccess, int n, int n2, int n3, int n4) {
        Block block = blockAccess.getBlock(n, n2, n3);
        return isBlockStairs(block) && blockAccess.getBlockMetadata(n, n2, n3) == n4;
    }

    public boolean checkIBlockAccess(IBlockAccess blockAccess, int n, int n2, int n3) {
        int blockMetadata = blockAccess.getBlockMetadata(n, n2, n3);
        int n4 = blockMetadata & 3;
        float f = 0.5F;
        float f2 = 1.0F;
        if ((blockMetadata & 4) != 0) {
            f = 0.0F;
            f2 = 0.5F;
        }

        float f3 = 0.0F;
        float f4 = 1.0F;
        float f5 = 0.0F;
        float f6 = 0.5F;
        boolean flag = true;
        if (n4 == 0) {
            f3 = 0.5F;
            f6 = 1.0F;
            Block block = blockAccess.getBlock(n + 1, n2, n3);
            int blockMetadata2 = blockAccess.getBlockMetadata(n + 1, n2, n3);
            if (isBlockStairs(block) && (blockMetadata & 4) == (blockMetadata2 & 4)) {
                int n5 = blockMetadata2 & 3;
                if (n5 == 3 && !this.checkIBlockAccess(blockAccess, n, n2, n3 + 1, blockMetadata)) {
                    f6 = 0.5F;
                    flag = false;
                } else if (n5 == 2 && !this.checkIBlockAccess(blockAccess, n, n2, n3 - 1, blockMetadata)) {
                    f5 = 0.5F;
                    flag = false;
                }
            }
        } else if (n4 == 1) {
            f4 = 0.5F;
            f6 = 1.0F;
            Block block = blockAccess.getBlock(n - 1, n2, n3);
            int blockMetadata2 = blockAccess.getBlockMetadata(n - 1, n2, n3);
            if (isBlockStairs(block) && (blockMetadata & 4) == (blockMetadata2 & 4)) {
                int n5 = blockMetadata2 & 3;
                if (n5 == 3 && !this.checkIBlockAccess(blockAccess, n, n2, n3 + 1, blockMetadata)) {
                    f6 = 0.5F;
                    flag = false;
                } else if (n5 == 2 && !this.checkIBlockAccess(blockAccess, n, n2, n3 - 1, blockMetadata)) {
                    f5 = 0.5F;
                    flag = false;
                }
            }
        } else if (n4 == 2) {
            f5 = 0.5F;
            f6 = 1.0F;
            Block block = blockAccess.getBlock(n, n2, n3 + 1);
            int blockMetadata2 = blockAccess.getBlockMetadata(n, n2, n3 + 1);
            if (isBlockStairs(block) && (blockMetadata & 4) == (blockMetadata2 & 4)) {
                int n5 = blockMetadata2 & 3;
                if (n5 == 1 && !this.checkIBlockAccess(blockAccess, n + 1, n2, n3, blockMetadata)) {
                    f4 = 0.5F;
                    flag = false;
                } else if (n5 == 0 && !this.checkIBlockAccess(blockAccess, n - 1, n2, n3, blockMetadata)) {
                    f3 = 0.5F;
                    flag = false;
                }
            }
        } else if (n4 == 3) {
            Block block = blockAccess.getBlock(n, n2, n3 - 1);
            int blockMetadata2 = blockAccess.getBlockMetadata(n, n2, n3 - 1);
            if (isBlockStairs(block) && (blockMetadata & 4) == (blockMetadata2 & 4)) {
                int n5 = blockMetadata2 & 3;
                if (n5 == 1 && !this.checkIBlockAccess(blockAccess, n + 1, n2, n3, blockMetadata)) {
                    f4 = 0.5F;
                    flag = false;
                } else if (n5 == 0 && !this.checkIBlockAccess(blockAccess, n - 1, n2, n3, blockMetadata)) {
                    f3 = 0.5F;
                    flag = false;
                }
            }
        }

        this.setBlockBounds(f3, f, f5, f4, f2, f6);
        return flag;
    }

    public boolean checkIBlockAccess2(IBlockAccess blockAccess, int n, int n2, int n3) {
        int blockMetadata = blockAccess.getBlockMetadata(n, n2, n3);
        int n4 = blockMetadata & 3;
        float f = 0.5F;
        float f2 = 1.0F;
        if ((blockMetadata & 4) != 0) {
            f = 0.0F;
            f2 = 0.5F;
        }

        float f3 = 0.0F;
        float f4 = 0.5F;
        float f5 = 0.5F;
        float f6 = 1.0F;
        boolean flag = false;
        if (n4 == 0) {
            Block block = blockAccess.getBlock(n - 1, n2, n3);
            int blockMetadata2 = blockAccess.getBlockMetadata(n - 1, n2, n3);
            if (isBlockStairs(block) && (blockMetadata & 4) == (blockMetadata2 & 4)) {
                int n5 = blockMetadata2 & 3;
                if (n5 == 3 && !this.checkIBlockAccess(blockAccess, n, n2, n3 - 1, blockMetadata)) {
                    f5 = 0.0F;
                    f6 = 0.5F;
                    flag = true;
                } else if (n5 == 2 && !this.checkIBlockAccess(blockAccess, n, n2, n3 + 1, blockMetadata)) {
                    f5 = 0.5F;
                    f6 = 1.0F;
                    flag = true;
                }
            }
        } else if (n4 == 1) {
            Block block = blockAccess.getBlock(n + 1, n2, n3);
            int blockMetadata2 = blockAccess.getBlockMetadata(n + 1, n2, n3);
            if (isBlockStairs(block) && (blockMetadata & 4) == (blockMetadata2 & 4)) {
                f3 = 0.5F;
                f4 = 1.0F;
                int n5 = blockMetadata2 & 3;
                if (n5 == 3 && !this.checkIBlockAccess(blockAccess, n, n2, n3 - 1, blockMetadata)) {
                    f5 = 0.0F;
                    f6 = 0.5F;
                    flag = true;
                } else if (n5 == 2 && !this.checkIBlockAccess(blockAccess, n, n2, n3 + 1, blockMetadata)) {
                    f5 = 0.5F;
                    f6 = 1.0F;
                    flag = true;
                }
            }
        } else if (n4 == 2) {
            Block block = blockAccess.getBlock(n, n2, n3 - 1);
            int blockMetadata2 = blockAccess.getBlockMetadata(n, n2, n3 - 1);
            if (isBlockStairs(block) && (blockMetadata & 4) == (blockMetadata2 & 4)) {
                f5 = 0.0F;
                f6 = 0.5F;
                int n5 = blockMetadata2 & 3;
                if (n5 == 1 && !this.checkIBlockAccess(blockAccess, n - 1, n2, n3, blockMetadata)) {
                    flag = true;
                } else if (n5 == 0 && !this.checkIBlockAccess(blockAccess, n + 1, n2, n3, blockMetadata)) {
                    f3 = 0.5F;
                    f4 = 1.0F;
                    flag = true;
                }
            }
        } else if (n4 == 3) {
            Block block = blockAccess.getBlock(n, n2, n3 + 1);
            int blockMetadata2 = blockAccess.getBlockMetadata(n, n2, n3 + 1);
            if (isBlockStairs(block) && (blockMetadata & 4) == (blockMetadata2 & 4)) {
                int n5 = blockMetadata2 & 3;
                if (n5 == 1 && !this.checkIBlockAccess(blockAccess, n - 1, n2, n3, blockMetadata)) {
                    flag = true;
                } else if (n5 == 0 && !this.checkIBlockAccess(blockAccess, n + 1, n2, n3, blockMetadata)) {
                    f3 = 0.5F;
                    f4 = 1.0F;
                    flag = true;
                }
            }
        }

        if (flag) {
            this.setBlockBounds(f3, f, f5, f4, f2, f6);
        }

        return flag;
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, int x, int y, int z, AxisAlignedBB mask, List list, Entity collider) {
        this.setBaseBounds(worldIn, x, y, z);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        boolean checkIBlockAccess = this.checkIBlockAccess(worldIn, x, y, z);
        super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        if (checkIBlockAccess && this.checkIBlockAccess2(worldIn, x, y, z)) {
            super.addCollisionBoxesToList(worldIn, x, y, z, mask, list, collider);
        }

        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        this.block.randomDisplayTick(worldIn, x, y, z, random);
    }

    @Override
    public void onBlockClicked(World worldIn, int x, int y, int z, EntityPlayer player) {
        this.block.onBlockClicked(worldIn, x, y, z, player);
    }

    @Override
    public void onBlockDestroyedByPlayer(World worldIn, int x, int y, int z, int meta) {
        this.block.onBlockDestroyedByPlayer(worldIn, x, y, z, meta);
    }

    @Override
    public int getMixedBrightnessForBlock(IBlockAccess worldIn, int x, int y, int z) {
        return this.block.getMixedBrightnessForBlock(worldIn, x, y, z);
    }

    @Override
    public float getExplosionResistance(Entity exploder) {
        return this.block.getExplosionResistance(exploder);
    }

    @Override
    public int getRenderBlockPass() {
        return this.block.getRenderBlockPass();
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return this.block.getIcon(side, this.intValue);
    }

    @Override
    public int tickRate(World worldIn) {
        return this.block.tickRate(worldIn);
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World worldIn, int x, int y, int z) {
        return this.block.getSelectedBoundingBoxFromPool(worldIn, x, y, z);
    }

    @Override
    public void modifyEntityVelocity(World worldIn, int x, int y, int z, Entity entityIn, Vec3 velocity) {
        this.block.modifyEntityVelocity(worldIn, x, y, z, entityIn, velocity);
    }

    @Override
    public boolean isCollidable() {
        return this.block.isCollidable();
    }

    @Override
    public boolean canStopRayTrace(int meta, boolean includeLiquid) {
        return this.block.canStopRayTrace(meta, includeLiquid);
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return this.block.canPlaceBlockAt(worldIn, x, y, z);
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        this.onNeighborBlockChange(worldIn, x, y, z, Blocks.air);
        this.block.onBlockAdded(worldIn, x, y, z);
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        this.block.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    @Override
    public void onEntityWalking(World worldIn, int x, int y, int z, Entity entityIn) {
        this.block.onEntityWalking(worldIn, x, y, z, entityIn);
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        this.block.updateTick(worldIn, x, y, z, random);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) throws java.io.IOException {
        return this.block.onBlockActivated(worldIn, x, y, z, player, 0, 0.0F, 0.0F, 0.0F);
    }

    @Override
    public void onBlockDestroyedByExplosion(World worldIn, int x, int y, int z, Explosion explosionIn) {
        this.block.onBlockDestroyedByExplosion(worldIn, x, y, z, explosionIn);
    }

    @Override
    public MapColor getMapColor(int meta) {
        return this.block.getMapColor(this.intValue);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, int x, int y, int z, EntityLivingBase placer, ItemStack itemIn) {
        int n = MathHelper.floor_double(placer.rotationYaw * 4.0F / 360.0F + 0.5) & 3;
        int n2 = worldIn.getBlockMetadata(x, y, z) & 4;
        if (n == 0) {
            worldIn.setBlockMetadataWithNotify(x, y, z, 2 | n2, 2);
        }

        if (n == 1) {
            worldIn.setBlockMetadataWithNotify(x, y, z, 1 | n2, 2);
        }

        if (n == 2) {
            worldIn.setBlockMetadataWithNotify(x, y, z, 3 | n2, 2);
        }

        if (n == 3) {
            worldIn.setBlockMetadataWithNotify(x, y, z, 0 | n2, 2);
        }
    }

    @Override
    public int onBlockPlaced(World worldIn, int x, int y, int z, int side, float subX, float subY, float subZ, int meta) {
        return side == 0 || side != 1 && !(subY <= 0.5) ? meta | 4 : meta;
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World worldIn, int x, int y, int z, Vec3 startVec, Vec3 endVec) {
        MovingObjectPosition[] movingObjectPositions = new MovingObjectPosition[8];
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        int n = blockMetadata & 3;
        boolean flag = (blockMetadata & 4) == 4;
        int[] ints = ignoredCornersByDirection[n + (flag ? 4 : 0)];
        this.isRayTraced = true;

        for (int i = 0; i < 8; i++) {
            this.rayTracePass = i;
            int[] ints2 = ints;
            int length = ints.length;

            for (int j = 0; j < length; j++) {
                int n2 = ints2[j];
                if (n2 == i) {
                }
            }

            movingObjectPositions[i] = super.collisionRayTrace(worldIn, x, y, z, startVec, endVec);
        }

        int[] ints2 = ints;
        int length = ints.length;

        for (int i = 0; i < length; i++) {
            int n2 = ints2[i];
            movingObjectPositions[n2] = null;
        }

        MovingObjectPosition movingObjectPosition = null;
        double d = 0.0;
        MovingObjectPosition[] movingObjectPositions2 = movingObjectPositions;
        int length2 = movingObjectPositions.length;

        for (int i = 0; i < length2; i++) {
            MovingObjectPosition movingObjectPosition2 = movingObjectPositions2[i];
            if (movingObjectPosition2 != null) {
                double squareDistanceTo = movingObjectPosition2.hitVec.squareDistanceTo(endVec);
                if (squareDistanceTo > d) {
                    movingObjectPosition = movingObjectPosition2;
                    d = squareDistanceTo;
                }
            }
        }

        return movingObjectPosition;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
    }
}
