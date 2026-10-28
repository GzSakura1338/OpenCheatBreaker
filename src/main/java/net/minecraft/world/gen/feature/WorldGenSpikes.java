package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class WorldGenSpikes extends WorldGenerator {
    public Block block;

    public WorldGenSpikes(Block block) {
        this.block = block;
    }

    @Override
    public boolean generate(World world, Random random, int n, int n2, int n3) {
        if (world.isAirBlock(n, n2, n3) && world.getBlock(n, n2 - 1, n3) == this.block) {
            int n4 = random.nextInt(32) + 6;
            int n5 = random.nextInt(4) + 1;

            for (int i = n - n5; i <= n + n5; i++) {
                for (int j = n3 - n5; j <= n3 + n5; j++) {
                    int n6 = i - n;
                    int n7 = j - n3;
                    if (n6 * n6 + n7 * n7 <= n5 * n5 + 1 && world.getBlock(i, n2 - 1, j) != this.block) {
                        return false;
                    }
                }
            }

            for (int i = n2; i < n2 + n4 && i < 256; i++) {
                for (int j = n - n5; j <= n + n5; j++) {
                    for (int k = n3 - n5; k <= n3 + n5; k++) {
                        int n6 = j - n;
                        int n7 = k - n3;
                        if (n6 * n6 + n7 * n7 <= n5 * n5 + 1) {
                            world.setBlock(j, i, k, Blocks.obsidian, 0, 2);
                        }
                    }
                }
            }

            EntityEnderCrystal entityEnderCrystal = new EntityEnderCrystal(world);
            entityEnderCrystal.setLocationAndAngles(n + 0.5F, n2 + n4, n3 + 0.5F, random.nextFloat() * 360.0F, 0.0F);
            world.spawnEntityInWorld(entityEnderCrystal);
            world.setBlock(n, n2 + n4, n3, Blocks.bedrock, 0, 2);
            return true;
        } else {
            return false;
        }
    }
}
