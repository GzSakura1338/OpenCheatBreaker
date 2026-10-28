package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;

public class MapGenBase {
    public int range = 8;
    public Random rand = new Random();
    public World worldObj;

    public void generate(IChunkProvider chunkProvider, World world, int n, int n2, Block[] blocks) {
        int range = this.range;
        this.worldObj = world;
        this.rand.setSeed(world.getSeed());
        long nextLong = this.rand.nextLong();
        long nextLong2 = this.rand.nextLong();

        for (int i = n - range; i <= n + range; i++) {
            for (int j = n2 - range; j <= n2 + range; j++) {
                long l = i * nextLong;
                long l2 = j * nextLong2;
                this.rand.setSeed(l ^ l2 ^ world.getSeed());
                this.handleWorld(world, i, j, n, n2, blocks);
            }
        }
    }

    public void handleWorld(World worldIn, int n, int n2, int n3, int n4, Block[] blocks) {
    }
}
