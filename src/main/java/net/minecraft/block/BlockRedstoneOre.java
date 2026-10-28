package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BlockRedstoneOre extends Block {
    public boolean lit;

    public BlockRedstoneOre(boolean flag) {
        super(Material.rock);
        if (flag) {
            this.setTickRandomly(true);
        }

        this.lit = flag;
    }

    @Override
    public int tickRate(World worldIn) {
        return 30;
    }

    @Override
    public void onBlockClicked(World worldIn, int x, int y, int z, EntityPlayer player) {
        this.handleWorld(worldIn, x, y, z);
        super.onBlockClicked(worldIn, x, y, z, player);
    }

    @Override
    public void onEntityWalking(World worldIn, int x, int y, int z, Entity entityIn) {
        this.handleWorld(worldIn, x, y, z);
        super.onEntityWalking(worldIn, x, y, z, entityIn);
    }

    @Override
    public boolean onBlockActivated(World worldIn, int x, int y, int z, EntityPlayer player, int side, float subX, float subY, float subZ) throws java.io.IOException {
        this.handleWorld(worldIn, x, y, z);
        return super.onBlockActivated(worldIn, x, y, z, player, side, subX, subY, subZ);
    }

    public void handleWorld(World world, int n, int n2, int n3) {
        this.handleWorld2(world, n, n2, n3);
        if (this == Blocks.redstone_ore) {
            world.setBlock(n, n2, n3, Blocks.lit_redstone_ore);
        }
    }

    @Override
    public void updateTick(World worldIn, int x, int y, int z, Random random) {
        if (this == Blocks.lit_redstone_ore) {
            worldIn.setBlock(x, y, z, Blocks.redstone_ore);
        }
    }

    @Override
    public Item getItemDropped(int meta, Random random, int fortune) {
        return Items.redstone;
    }

    @Override
    public int quantityDroppedWithBonus(int maxBonus, Random random) {
        return this.quantityDropped(random) + random.nextInt(maxBonus + 1);
    }

    @Override
    public int quantityDropped(Random random) {
        return 4 + random.nextInt(2);
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, int x, int y, int z, int meta, float chance, int fortune) {
        super.dropBlockAsItemWithChance(worldIn, x, y, z, meta, chance, fortune);
        if (this.getItemDropped(meta, worldIn.rand, fortune) != Item.getItemFromBlock(this)) {
            int n = 1 + worldIn.rand.nextInt(5);
            this.dropXpOnBlockBreak(worldIn, x, y, z, n);
        }
    }

    @Override
    public void randomDisplayTick(World worldIn, int x, int y, int z, Random random) {
        if (this.lit) {
            this.handleWorld2(worldIn, x, y, z);
        }
    }

    public void handleWorld2(World world, int n, int n2, int n3) {
        Random rand = world.rand;
        double d = 0.0625;

        for (int i = 0; i < 6; i++) {
            double d2 = n + rand.nextFloat();
            double d3 = n2 + rand.nextFloat();
            double d4 = n3 + rand.nextFloat();
            if (i == 0 && !world.getBlock(n, n2 + 1, n3).isOpaqueCube()) {
                d3 = n2 + 1 + d;
            }

            if (i == 1 && !world.getBlock(n, n2 - 1, n3).isOpaqueCube()) {
                d3 = n2 + 0 - d;
            }

            if (i == 2 && !world.getBlock(n, n2, n3 + 1).isOpaqueCube()) {
                d4 = n3 + 1 + d;
            }

            if (i == 3 && !world.getBlock(n, n2, n3 - 1).isOpaqueCube()) {
                d4 = n3 + 0 - d;
            }

            if (i == 4 && !world.getBlock(n + 1, n2, n3).isOpaqueCube()) {
                d2 = n + 1 + d;
            }

            if (i == 5 && !world.getBlock(n - 1, n2, n3).isOpaqueCube()) {
                d2 = n + 0 - d;
            }

            if (d2 < n || d2 > n + 1 || d3 < 0.0 || d3 > n2 + 1 || d4 < n3 || d4 > n3 + 1) {
                world.spawnParticle("reddust", d2, d3, d4, 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public ItemStack createStackedBlock(int meta) {
        return new ItemStack(Blocks.redstone_ore);
    }
}
