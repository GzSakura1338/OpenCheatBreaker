package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.world.World;

public class WorldGenTallGrass extends WorldGenerator {
    public Block block;
    public int tallGrassMetadata;

    public WorldGenTallGrass(Block block, int n) {
        this.block = block;
        this.tallGrassMetadata = n;
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        Block block;
        while (((block = world.getBlock(n, n2, n3)).getMaterial() == Material.air || block.getMaterial() == Material.leaves) && n2 > 0) {
            n2--;
        }

        for (int i = 0; i < 128; i++) {
            int n4 = n + random.nextInt(8) - random.nextInt(8);
            int n5 = n2 + random.nextInt(4) - random.nextInt(4);
            int n6 = n3 + random.nextInt(8) - random.nextInt(8);
            if (world.isAirBlock(n4, n5, n6) && this.block.canBlockStay(world, n4, n5, n6)) {
                world.setBlock(n4, n5, n6, this.block, this.tallGrassMetadata, 2);
            }
        }

        return true;
    }
}
