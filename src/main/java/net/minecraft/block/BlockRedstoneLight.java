package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockRedstoneLight extends Block {
    public boolean powered;

    public BlockRedstoneLight(boolean flag) {
        super(Material.redstoneLight);
        this.powered = flag;
        if (flag) {
            this.setLightLevel(1.0F);
        }
    }

    @Override
    public void onBlockAdded(World worldIn, int x, int y, int z) {
        if (!worldIn.isRemote) {
            if (this.powered && !worldIn.isBlockIndirectlyGettingPowered(x, y, z)) {
                worldIn.scheduleBlockUpdate(x, y, z, this, 4);
            } else if (!this.powered && worldIn.isBlockIndirectlyGettingPowered(x, y, z)) {
                worldIn.setBlock(x, y, z, Blocks.lit_redstone_lamp, 0, 2);
            }
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, int x, int y, int z, Block neighbor) {
        if (!worldIn.isRemote) {
            if (this.powered && !worldIn.isBlockIndirectlyGettingPowered(x, y, z)) {
                worldIn.scheduleBlockUpdate(x, y, z, this, 4);
            } else if (!this.powered && worldIn.isBlockIndirectlyGettingPowered(x, y, z)) {
                worldIn.setBlock(x, y, z, Blocks.lit_redstone_lamp, 0, 2);
            }
        }
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (!worldIn.isRemote && this.powered && !worldIn.isBlockIndirectlyGettingPowered(x, y, z)) {
            worldIn.setBlock(x, y, z, Blocks.redstone_lamp, 0, 2);
        }
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Item.getItemFromBlock(Blocks.redstone_lamp);
    }

    @Override
    public Item getItem(World worldIn, int x, int y, int z) {
        return Item.getItemFromBlock(Blocks.redstone_lamp);
    }

    @Override
    public ItemStack createStackedBlock(int meta) {
        return new ItemStack(Blocks.redstone_lamp);
    }
}
