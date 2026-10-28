package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRailDetector extends BlockRailBase {
    public IIcon[] iIconArray;

    public BlockRailDetector() {
        super(true);
        this.setTickRandomly(true);
    }

    @Override
    public int tickRate(World worldIn) {
        return 20;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn) {
        if (!worldIn.isRemote) {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            if ((blockMetadata & 8) == 0) {
                this.handleWorld(worldIn, x, y, z, blockMetadata);
            }
        }
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!worldIn.isRemote) {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            if ((blockMetadata & 8) != 0) {
                this.handleWorld(worldIn, x, y, z, blockMetadata);
            }
        }
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        return (worldIn.getBlockMetadata(x, y, z) & 8) != 0 ? 15 : 0;
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        return (worldIn.getBlockMetadata(x, y, z) & 8) == 0 ? 0 : (side == 1 ? 15 : 0);
    }

    public void handleWorld(World world, int n, int n2, int n3, int n4) {
        boolean flag = (n4 & 8) != 0;
        boolean flag2 = false;
        float f = 0.125F;
        List entitiesWithinAABB = world.getEntitiesWithinAABB(
            EntityMinecart.class, AxisAlignedBB.getBoundingBox(n + f, n2, n3 + f, n + 1 - f, n2 + 1 - f, n3 + 1 - f)
        );
        if (!entitiesWithinAABB.isEmpty()) {
            flag2 = true;
        }

        if (flag2 && !flag) {
            world.setBlockMetadataWithNotify(n, n2, n3, n4 | 8, 3);
            world.notifyBlocksOfNeighborChange(n, n2, n3, this);
            world.notifyBlocksOfNeighborChange(n, n2 - 1, n3, this);
            world.markBlockRangeForRenderUpdate(n, n2, n3, n, n2, n3);
        }

        if (!flag2 && flag) {
            world.setBlockMetadataWithNotify(n, n2, n3, n4 & 7, 3);
            world.notifyBlocksOfNeighborChange(n, n2, n3, this);
            world.notifyBlocksOfNeighborChange(n, n2 - 1, n3, this);
            world.markBlockRangeForRenderUpdate(n, n2, n3, n, n2, n3);
        }

        if (flag2) {
            world.scheduleBlockUpdate(n, n2, n3, this, this.tickRate(world));
        }

        world.updateNeighborsAboutBlockChange(n, n2, n3, this);
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        super.onBlockAdded(worldIn, x, y, z);
        this.handleWorld(worldIn, x, y, z, worldIn.getBlockMetadata(x, y, z));
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World worldIn, int x, int y, int z, int side) {
        if ((worldIn.getBlockMetadata(x, y, z) & 8) > 0) {
            float f = 0.125F;
            List entitiesWithinAABB = worldIn.getEntitiesWithinAABB(
                EntityMinecartCommandBlock.class, AxisAlignedBB.getBoundingBox(x + f, y, z + f, x + 1 - f, y + 1 - f, z + 1 - f)
            );
            if (entitiesWithinAABB.size() > 0) {
                return ((EntityMinecartCommandBlock)entitiesWithinAABB.get(0)).getCommandExecutor().getSuccessCount();
            }

            List selectEntitiesWithinAABB = worldIn.selectEntitiesWithinAABB(
                EntityMinecart.class,
                AxisAlignedBB.getBoundingBox(x + f, y, z + f, x + 1 - f, y + 1 - f, z + 1 - f),
                IEntitySelector.selectInventories
            );
            if (selectEntitiesWithinAABB.size() > 0) {
                return Container.calcRedstoneFromInventory((IInventory)selectEntitiesWithinAABB.get(0));
            }
        }

        return 0;
    }

    @Override
    public void registerIcons(IIconRegister reg) {
        this.iIconArray = new IIcon[2];
        this.iIconArray[0] = reg.registerIcon(this.getTextureName());
        this.iIconArray[1] = reg.registerIcon(this.getTextureName() + "_powered");
    }

    @Override
    public IIcon getIcon(int side, int meta) {
        return (meta & 8) != 0 ? this.iIconArray[1] : this.iIconArray[0];
    }
}
