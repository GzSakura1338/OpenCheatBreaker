package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Direction;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTripWire extends Block {
    public BlockTripWire() {
        super(Material.circuits);
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.15625F, 1.0F);
        this.setTickRandomly(true);
    }

    @Override
    public int tickRate(World worldIn) {
        return 10;
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
    public int getRenderBlockPass() {
        return 1;
    }

    @Override
    public int getRenderType() {
        return 30;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Items.string;
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Items.string;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        boolean flag = (blockMetadata & 2) == 2;
        boolean flag2 = !World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z);
        if (flag != flag2) {
            this.dropBlockAsItem(worldIn, x, y, z, blockMetadata, 0);
            worldIn.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, int x, int y, int z) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        boolean flag = (blockMetadata & 4) == 4;
        boolean flag2 = (blockMetadata & 2) == 2;
        if (!flag2) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.09375F, 1.0F);
        } else if (!flag) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        } else {
            this.setBlockBounds(0.0F, 0.0625F, 0.0F, 1.0F, 0.15625F, 1.0F);
        }
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        int n = World.doesBlockHaveSolidTopSurface(worldIn, x, y - 1, z) ? 0 : 2;
        worldIn.setBlockMetadataWithNotify(x, y, z, n, 3);
        this.handleWorld(worldIn, x, y, z, n);
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        this.handleWorld(worldIn, x, y, z, meta | 1);
    }

    @Override
    public void onBlockHarvested(World worldIn, int x, int y, int z, int meta, EntityPlayer player) {
        if (!worldIn.isRemote && player.getCurrentEquippedItem() != null && player.getCurrentEquippedItem().getItem() == Items.shears) {
            worldIn.setBlockMetadataWithNotify(x, y, z, meta | 8, 4);
        }
    }

    public void handleWorld(World world, int n, int n2, int n3, int n4) {
        for (int i = 0; i < 2; i++) {
            for (int j = 1; j < 42; j++) {
                int n5 = n + Direction.offsetX[i] * j;
                int n6 = n3 + Direction.offsetZ[i] * j;
                Block block = world.getBlock(n5, n2, n6);
                if (block == Blocks.tripwire_hook) {
                    int n7 = world.getBlockMetadata(n5, n2, n6) & 3;
                    if (n7 == Direction.rotateOpposite[i]) {
                        Blocks.tripwire_hook.handleWorld(world, n5, n2, n6, false, world.getBlockMetadata(n5, n2, n6), true, j, n4);
                    }
                    break;
                }

                if (block != Blocks.tripwire) {
                    break;
                }
            }
        }
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, int x, int y, int z, Entity entityIn) {
        if (!worldIn.isRemote && (worldIn.getBlockMetadata(x, y, z) & 1) != 1) {
            this.handleWorld(worldIn, x, y, z);
        }
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!worldIn.isRemote && (worldIn.getBlockMetadata(x, y, z) & 1) == 1) {
            this.handleWorld(worldIn, x, y, z);
        }
    }

    public void handleWorld(World world, int n, int n2, int n3) {
        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        boolean flag = (blockMetadata & 1) == 1;
        boolean flag2 = false;
        List list = world.getEntitiesWithinAABBExcludingEntity(
            null, AxisAlignedBB.getBoundingBox(n + this.minX, n2 + this.minY, n3 + this.minZ, n + this.maxX, n2 + this.maxY, n3 + this.maxZ)
        );
        if (!list.isEmpty()) {
            for (Entity entity : (Iterable<Entity>)list) {
                if (!entity.doesEntityNotTriggerPressurePlate()) {
                    flag2 = true;
                    break;
                }
            }
        }

        if (flag2 && !flag) {
            blockMetadata |= 1;
        }

        if (!flag2 && flag) {
            blockMetadata &= -2;
        }

        if (flag2 != flag) {
            world.setBlockMetadataWithNotify(n, n2, n3, blockMetadata, 3);
            this.handleWorld(world, n, n2, n3, blockMetadata);
        }

        if (flag2) {
            world.scheduleBlockUpdate(n, n2, n3, this, this.tickRate(world));
        }
    }

    public static boolean checkIBlockAccess(IBlockAccess blockAccess, int n, int n2, int n3, int n4, int n5) {
        int n6 = n + Direction.offsetX[n5];
        int n7 = n3 + Direction.offsetZ[n5];
        Block block = blockAccess.getBlock(n6, n2, n7);
        boolean flag = (n4 & 2) == 2;
        if (block == Blocks.tripwire_hook) {
            int blockMetadata = blockAccess.getBlockMetadata(n6, n2, n7);
            int n8 = blockMetadata & 3;
            return n8 == Direction.rotateOpposite[n5];
        } else if (block == Blocks.tripwire) {
            int blockMetadata = blockAccess.getBlockMetadata(n6, n2, n7);
            boolean flag2 = (blockMetadata & 2) == 2;
            return flag == flag2;
        } else {
            return false;
        }
    }
}
