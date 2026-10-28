package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockBasePressurePlate extends Block {
    public String name;

    public BlockBasePressurePlate(String name, Material materialIn) {
        super(materialIn);
        this.name = name;
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setTickRandomly(true);
        this.setBlockBoundsFromMeta(this.getMetaFromPower(15));
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        this.setBlockBoundsFromMeta(worldIn.getBlockMetadata(x, y, z));
    }

    public void setBlockBoundsFromMeta(int meta) {
        boolean flag = this.getPowerFromMeta(meta) > 0;
        float f = 0.0625F;
        if (flag) {
            this.setBlockBounds(f, 0.0F, f, 1.0F - f, 0.03125F, 1.0F - f);
        } else {
            this.setBlockBounds(f, 0.0F, f, 1.0F - f, 0.0625F, 1.0F - f);
        }
    }

    @Override
    public int tickRate(World worldIn) {
        return 20;
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
    public boolean isPassable(IBlockAccess worldIn, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, int x, int y, int z) {
        return World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) || BlockFence.isFence(worldIn.getBlock(x, y - 1, z));
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        boolean flag = !World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) && !BlockFence.isFence(worldIn.getBlock(x, y - 1, z));
        if (flag) {
            this.dropBlockAsItem(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z), 0);
            worldIn.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!worldIn.isRemote) {
            int powerFromMeta = this.getPowerFromMeta(worldIn.getBlockMetadata(x, y, z));
            if (powerFromMeta > 0) {
                this.setStateIfMobInteractsWithPlate(worldIn, x, y, z, powerFromMeta);
            }
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn) {
        if (!worldIn.isRemote) {
            int powerFromMeta = this.getPowerFromMeta(worldIn.getBlockMetadata(x, y, z));
            if (powerFromMeta == 0) {
                this.setStateIfMobInteractsWithPlate(worldIn, x, y, z, powerFromMeta);
            }
        }
    }

    public void setStateIfMobInteractsWithPlate(World worldIn, int x, int y, int z, int power) {
        int plateState = this.getPlateState(worldIn, x, y, z);
        boolean flag = power > 0;
        boolean flag2 = plateState > 0;
        if (power != plateState) {
            worldIn.setBlockMetadataWithNotify(x, y, z, this.getMetaFromPower(plateState), 2);
            this.updateNeighbors(worldIn, x, y, z);
            worldIn.markBlockRangeForRenderUpdate(x, y, z, x, y, z);
        }

        if (!flag2 && flag) {
            worldIn.playSoundEffect(x + 0.5, y + 0.1, z + 0.5, "random.click", 0.3F, 0.5F);
        } else if (flag2 && !flag) {
            worldIn.playSoundEffect(x + 0.5, y + 0.1, z + 0.5, "random.click", 0.3F, 0.6F);
        }

        if (flag2) {
            worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn));
        }
    }

    public AxisAlignedBB getSensitiveAABB(int x, int y, int z) {
        float f = 0.125F;
        return AxisAlignedBB.getBoundingBox(x + f, y, z + f, x + 1 - f, y + 0.25, z + 1 - f);
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        if (this.getPowerFromMeta(meta) > 0) {
            this.updateNeighbors(worldIn, x, y, z);
        }

        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
    }

    public void updateNeighbors(World worldIn, int x, int y, int z) {
        worldIn.notifyBlocksOfNeighborChange(x, y, z, this);
        worldIn.notifyBlocksOfNeighborChange(x, y - 1, z, this);
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        return this.getPowerFromMeta(worldIn.getBlockMetadata(x, y, z));
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        return side == 1 ? this.getPowerFromMeta(worldIn.getBlockMetadata(x, y, z)) : 0;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public void setBlockBoundsForItemRender() {
        float f = 0.5F;
        float f2 = 0.125F;
        float f3 = 0.5F;
        this.setBlockBounds(0.5F - f, 0.5F - f2, 0.5F - f3, 0.5F + f, 0.5F + f2, 0.5F + f3);
    }

    @Override
    public int getMobilityFlag() {
        return 1;
    }

    public abstract int getPlateState(World world, int n, int n2, int n3);

    public abstract int getPowerFromMeta(int n);

    public abstract int getMetaFromPower(int n);

    @Override
    public void registerIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.name);
    }
}
