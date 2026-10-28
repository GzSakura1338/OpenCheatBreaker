package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public abstract class WorldGenAbstractTree extends WorldGenerator {
    public WorldGenAbstractTree(boolean flag) {
        super(flag);
    }

    public boolean isBlockReplaceable(Block block) {
        return block.getMaterial() == Material.air
            || block.getMaterial() == Material.leaves
            || block == Blocks.grass
            || block == Blocks.dirt
            || block == Blocks.log
            || block == Blocks.log2
            || block == Blocks.sapling
            || block == Blocks.vine;
    }

    public void handleWorld(World world, Random random, int n, int n2, int n3) {
    }
}
