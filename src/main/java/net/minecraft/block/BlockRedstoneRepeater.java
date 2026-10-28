package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneRepeater extends BlockRedstoneDiode {
    public static double[] repeaterTorchOffset = new double[]{-0.0625, 0.0625, 0.1875, 0.3125};
    public static int[] repeaterState = new int[]{1, 2, 3, 4};

    public BlockRedstoneRepeater(boolean flag) {
        super(flag);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) {
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        int n = (blockMetadata & 12) >> 2;
        n = n + 1 << 2 & 12;
        worldIn.setBlockMetadataWithNotify(x, y, z, n | blockMetadata & 3, 3);
        return true;
    }

    @Override
    public int computeInt(int n) {
        return repeaterState[(n & 12) >> 2] * 2;
    }

    @Override
    public BlockRedstoneDiode getBlockPowered() {
        return Blocks.powered_repeater;
    }

    @Override
    public BlockRedstoneDiode getBlockUnpowered() {
        return Blocks.unpowered_repeater;
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Items.repeater;
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Items.repeater;
    }

    @Override
    public int getRenderType() {
        return 15;
    }

    @Override
    public boolean isLocked(IBlockAccess blockAccess, int n, int n2, int n3, int n4) {
        return this.computeInt(blockAccess, n, n2, n3, n4) > 0;
    }

    @Override
    public boolean checkBlock(Block block) {
        return isRedstoneRepeaterBlockID(block);
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        if (this.isRepeaterPowered) {
            int blockMetadata = worldIn.getBlockMetadata(x, y, z);
            int direction = getDirection(blockMetadata);
            double d = x + 0.5F + (random.nextFloat() - 0.5F) * 0.2;
            double d2 = y + 0.4F + (random.nextFloat() - 0.5F) * 0.2;
            double d3 = z + 0.5F + (random.nextFloat() - 0.5F) * 0.2;
            double d4 = 0.0;
            double d5 = 0.0;
            if (random.nextInt(2) == 0) {
                switch (direction) {
                    case 0:
                        d5 = -0.3125;
                        break;
                    case 1:
                        d4 = 0.3125;
                        break;
                    case 2:
                        d5 = 0.3125;
                        break;
                    case 3:
                        d4 = -0.3125;
                }
            } else {
                int n = (blockMetadata & 12) >> 2;
                switch (direction) {
                    case 0:
                        d5 = repeaterTorchOffset[n];
                        break;
                    case 1:
                        d4 = -repeaterTorchOffset[n];
                        break;
                    case 2:
                        d5 = -repeaterTorchOffset[n];
                        break;
                    case 3:
                        d4 = repeaterTorchOffset[n];
                }
            }

            worldIn.spawnParticle("reddust", d + d4, d2, d3 + d5, 0.0, 0.0, 0.0);
        }
    }

    @Override
    public void breakBlock(World worldIn, int x, int y, int z, Block blockBroken, int meta) {
        super.breakBlock(worldIn, x, y, z, blockBroken, meta);
        this.handleWorld(worldIn, x, y, z);
    }
}
