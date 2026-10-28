package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDragonEgg extends Block {
    public BlockDragonEgg() {
        super(Material.dragonEgg);
        this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 1.0F, 0.9375F);
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn));
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn));
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        this.handleWorld(worldIn, x, y, z);
    }

    public void handleWorld(World world, int n, int n2, int n3) {
        if (BlockFalling.canFallBelow(world, n, n2 - 1, n3) && n2 >= 0) {
            byte b = 32;
            if (!BlockFalling.fallInstantly && world.checkChunksExist(n - b, n2 - b, n3 - b, n + b, n2 + b, n3 + b)) {
                EntityFallingBlock entityFallingBlock = new EntityFallingBlock(world, n + 0.5F, n2 + 0.5F, n3 + 0.5F, this);
                world.spawnEntityInWorld(entityFallingBlock);
            } else {
                world.setBlockToAir(n, n2, n3);

                while (BlockFalling.canFallBelow(world, n, n2 - 1, n3) && n2 > 0) {
                    n2--;
                }

                if (n2 > 0) {
                    world.setBlock(n, n2, n3, this, 0, 2);
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        this.teleport(worldIn, x, y, z);
        return true;
    }

    @Override
    public void onBlockClicked(World worldIn, int x, int y, int z, EntityPlayer player) {
        this.teleport(worldIn, x, y, z);
    }

    public void teleport(World world, int n, int n2, int n3) {
        if (world.getBlock(n, n2, n3) == this) {
            for (int i = 0; i < 1000; i++) {
                int n4 = n + world.rand.nextInt(16) - world.rand.nextInt(16);
                int n5 = n2 + world.rand.nextInt(8) - world.rand.nextInt(8);
                int n6 = n3 + world.rand.nextInt(16) - world.rand.nextInt(16);
                if (world.getBlock(n4, n5, n6).blockMaterial == Material.air) {
                    if (!world.isRemote) {
                        world.setBlock(n4, n5, n6, this, world.getBlockMetadata(n, n2, n3), 2);
                        world.setBlockToAir(n, n2, n3);
                    } else {
                        short s = 128;

                        for (int j = 0; j < s; j++) {
                            double nextDouble = world.rand.nextDouble();
                            float f = (world.rand.nextFloat() - 0.5F) * 0.2F;
                            float f2 = (world.rand.nextFloat() - 0.5F) * 0.2F;
                            float f3 = (world.rand.nextFloat() - 0.5F) * 0.2F;
                            double d = n4 + (n - n4) * nextDouble + (world.rand.nextDouble() - 0.5) * 1.0 + 0.5;
                            double d2 = n5 + (n2 - n5) * nextDouble + world.rand.nextDouble() * 1.0 - 0.5;
                            double d3 = n6 + (n3 - n6) * nextDouble + (world.rand.nextDouble() - 0.5) * 1.0 + 0.5;
                            world.spawnParticle("portal", d, d2, d3, f, f2, f3);
                        }
                    }

                    return;
                }
            }
        }
    }

    @Override
    public int tickRate(World worldIn) {
        return 5;
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
    public boolean shouldSideBeRendered(IBlockAccess worldIn, int x, int y, int z, int side) {
        return true;
    }

    @Override
    public int getRenderType() {
        return 27;
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Item.getItemById(0);
    }
}
