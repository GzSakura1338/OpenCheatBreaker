package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenHellLava extends WorldGenerator {
    public Block block;
    public boolean denySurfaceSpawn;

    public WorldGenHellLava(Block block, boolean flag) {
        this.block = block;
        this.denySurfaceSpawn = flag;
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        if (world.getBlock(n, n2 + 1, n3) != Blocks.netherrack) {
            return false;
        }

        if (world.getBlock(n, n2, n3).getMaterial() != Material.air && world.getBlock(n, n2, n3) != Blocks.netherrack) {
            return false;
        }

        int n4 = 0;
        if (world.getBlock(n - 1, n2, n3) == Blocks.netherrack) {
            n4++;
        }

        if (world.getBlock(n + 1, n2, n3) == Blocks.netherrack) {
            n4++;
        }

        if (world.getBlock(n, n2, n3 - 1) == Blocks.netherrack) {
            n4++;
        }

        if (world.getBlock(n, n2, n3 + 1) == Blocks.netherrack) {
            n4++;
        }

        if (world.getBlock(n, n2 - 1, n3) == Blocks.netherrack) {
            n4++;
        }

        int n5 = 0;
        if (world.isAirBlock(n - 1, n2, n3)) {
            n5++;
        }

        if (world.isAirBlock(n + 1, n2, n3)) {
            n5++;
        }

        if (world.isAirBlock(n, n2, n3 - 1)) {
            n5++;
        }

        if (world.isAirBlock(n, n2, n3 + 1)) {
            n5++;
        }

        if (world.isAirBlock(n, n2 - 1, n3)) {
            n5++;
        }

        if (!this.denySurfaceSpawn && n4 == 4 && n5 == 1 || n4 == 5) {
            world.setBlock(n, n2, n3, this.block, 0, 2);
            world.scheduledUpdatesAreImmediate = true;
            this.block.updateTick(world, n, n2, n3, random);
            world.scheduledUpdatesAreImmediate = false;
        }

        return true;
    }
}
