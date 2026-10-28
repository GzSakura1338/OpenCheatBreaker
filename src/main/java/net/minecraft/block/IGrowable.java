package net.minecraft.block;

import java.util.Random;
import net.minecraft.world.World;

public interface IGrowable {
    boolean canFertilize(World world, int n, int n2, int n3, boolean flag);

    boolean shouldFertilize(World world, Random random, int n, int n2, int n3);

    void fertilize(World world, Random random, int n, int n2, int n3);
}
