package net.minecraft.block;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneTorch extends BlockTorch {
    public boolean lit;
    public static Map turnOffEntries = new HashMap();

    public boolean checkWorld(World world, int n, int n2, int n3, boolean flag) {
        if (!turnOffEntries.containsKey(world)) {
            turnOffEntries.put(world, new ArrayList());
        }

        List list = (List)turnOffEntries.get(world);
        if (flag) {
            list.add(new BlockRedstoneTorch.Toggle(n, n2, n3, world.getTotalWorldTime()));
        }

        int n4 = 0;

        for (int i = 0; i < list.size(); i++) {
            BlockRedstoneTorch.Toggle toggle = (BlockRedstoneTorch.Toggle)list.get(i);
            if (toggle.intValue == n && toggle.intValue2 == n2 && toggle.intValue3 == n3) {
                if (++n4 >= 8) {
                    return true;
                }
            }
        }

        return false;
    }

    public BlockRedstoneTorch(boolean flag) {
        this.lit = flag;
        this.setTickRandomly(true);
        this.setCreativeTab(null);
    }

    @Override
    public int tickRate(World worldIn) {
        return 2;
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        if (worldIn.getBlockMetadata(x, y, z) == 0) {
            super.onBlockAdded(worldIn, x, y, z);
        }

        if (this.lit) {
            worldIn.notifyBlocksOfNeighborChange(x, y - 1, z, this);
            worldIn.notifyBlocksOfNeighborChange(x, y + 1, z, this);
            worldIn.notifyBlocksOfNeighborChange(x - 1, y, z, this);
            worldIn.notifyBlocksOfNeighborChange(x + 1, y, z, this);
            worldIn.notifyBlocksOfNeighborChange(x, y, z - 1, this);
            worldIn.notifyBlocksOfNeighborChange(x, y, z + 1, this);
        }
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        if (this.lit) {
            worldIn.notifyBlocksOfNeighborChange(x, y - 1, z, this);
            worldIn.notifyBlocksOfNeighborChange(x, y + 1, z, this);
            worldIn.notifyBlocksOfNeighborChange(x - 1, y, z, this);
            worldIn.notifyBlocksOfNeighborChange(x + 1, y, z, this);
            worldIn.notifyBlocksOfNeighborChange(x, y, z - 1, this);
            worldIn.notifyBlocksOfNeighborChange(x, y, z + 1, this);
        }
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        if (!this.lit) {
            return 0;
        }

        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        return blockMetadata == 5 && side == 1
            ? 0
            : (blockMetadata == 3 && side == 3 ? 0 : (blockMetadata == 4 && side == 2 ? 0 : (blockMetadata == 1 && side == 5 ? 0 : (blockMetadata == 2 && side == 4 ? 0 : 15))));
    }

    public boolean checkWorld(World world, int n, int n2, int n3) {
        int blockMetadata = world.getBlockMetadata(n, n2, n3);
        return blockMetadata == 5 && world.getIndirectPowerOutput(n, n2 - 1, n3, 0)
            || blockMetadata == 3 && world.getIndirectPowerOutput(n, n2, n3 - 1, 2)
            || blockMetadata == 4 && world.getIndirectPowerOutput(n, n2, n3 + 1, 3)
            || blockMetadata == 1 && world.getIndirectPowerOutput(n - 1, n2, n3, 4)
            || blockMetadata == 2 && world.getIndirectPowerOutput(n + 1, n2, n3, 5);
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        boolean checkWorld = this.checkWorld(worldIn, x, y, z);
        List list = (List)turnOffEntries.get(worldIn);

        while (list != null && !list.isEmpty() && worldIn.getTotalWorldTime() - ((BlockRedstoneTorch.Toggle)list.get(0)).longValue > 60L) {
            list.remove(0);
        }

        if (this.lit) {
            if (checkWorld) {
                worldIn.setBlock(x, y, z, Blocks.unlit_redstone_torch, worldIn.getBlockMetadata(x, y, z), 3);
                if (this.checkWorld(worldIn, x, y, z, true)) {
                    worldIn.playSoundEffect(x + 0.5F, y + 0.5F, z + 0.5F, "random.fizz", 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

                    for (int i = 0; i < 5; i++) {
                        double d = x + random.nextDouble() * 0.6 + 0.2;
                        double d2 = y + random.nextDouble() * 0.6 + 0.2;
                        double d3 = z + random.nextDouble() * 0.6 + 0.2;
                        worldIn.spawnParticle("smoke", d, d2, d3, 0.0, 0.0, 0.0);
                    }
                }
            }
        } else if (!checkWorld && !this.checkWorld(worldIn, x, y, z, false)) {
            worldIn.setBlock(x, y, z, Blocks.redstone_torch, worldIn.getBlockMetadata(x, y, z), 3);
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!this.checkWorld(worldIn, x, y, z, neighbor)) {
            boolean checkWorld = this.checkWorld(worldIn, x, y, z);
            if (this.lit && checkWorld || !this.lit && !checkWorld) {
                worldIn.scheduleBlockUpdate(x, y, z, this, this.tickRate(worldIn));
            }
        }
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess worldIn, int x, int y, int z, int side) {
        return side == 0 ? this.isProvidingWeakPower(worldIn, x, y, z, side) : 0;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemFromBlock(Blocks.redstone_torch);
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        if (this.lit) {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            double d = x + 0.5F + (random.nextFloat() - 0.5F) * 0.2;
            double d2 = y + 0.7F + (random.nextFloat() - 0.5F) * 0.2;
            double d3 = z + 0.5F + (random.nextFloat() - 0.5F) * 0.2;
            double d4 = 0.22F;
            double d5 = 0.27F;
            if (blockMetadata == 1) {
                worldIn.spawnParticle("reddust", d - d5, d2 + d4, d3, 0.0, 0.0, 0.0);
            } else if (blockMetadata == 2) {
                worldIn.spawnParticle("reddust", d + d5, d2 + d4, d3, 0.0, 0.0, 0.0);
            } else if (blockMetadata == 3) {
                worldIn.spawnParticle("reddust", d, d2 + d4, d3 - d5, 0.0, 0.0, 0.0);
            } else if (blockMetadata == 4) {
                worldIn.spawnParticle("reddust", d, d2 + d4, d3 + d5, 0.0, 0.0, 0.0);
            } else {
                worldIn.spawnParticle("reddust", d, d2, d3, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Item.getItemFromBlock(Blocks.redstone_torch);
    }

    @Override
    public boolean isAssociatedBlock(Block other) {
        return other == Blocks.unlit_redstone_torch || other == Blocks.redstone_torch;
    }

    public static class Toggle {
        public int intValue;
        public int intValue2;
        public int intValue3;
        public long longValue;

        public Toggle(int n, int n2, int n3, long l) {
            this.intValue = n;
            this.intValue2 = n2;
            this.intValue3 = n3;
            this.longValue = l;
        }
    }
}
