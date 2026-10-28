package net.optifine;

import java.util.Arrays;
import net.minecraft.block.Block;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ChunkCacheOF extends ChunkCache {
    public IBlockAccess blockAccess;
    public BlockPos position;
    public int[] combinedLights;
    public Block[] blocks;
    public static ArrayCache cacheCombinedLights = new ArrayCache(int.class, 16);
    public static ArrayCache cacheBlocks = new ArrayCache(Block.class, 16);
    public static final int ARRAY_SIZE = 8000;

    public ChunkCacheOF(World world, int n, int n2, int n3, int n4, int n5, int n6, int n7) {
        super(world, n, n2, n3, n4, n5, n6, n7);
        this.blockAccess = world;
        this.position = new BlockPos(n - n7, n2 - n7, n3 - n7);
    }

    @Override
    public int getLightBrightnessForSkyBlocks(int n, int n2, int n3, int n4) {
        if (this.combinedLights == null) {
            return this.getLightBrightnessForSkyBlocksRaw(n, n2, n3, n4);
        }

        int positionIndex = this.getPositionIndex(n, n2, n3);
        if (positionIndex >= 0 && positionIndex < this.combinedLights.length) {
            int n5 = this.combinedLights[positionIndex];
            if (n5 == -1) {
                n5 = this.getLightBrightnessForSkyBlocksRaw(n, n2, n3, n4);
                this.combinedLights[positionIndex] = n5;
            }

            return n5;
        } else {
            return this.getLightBrightnessForSkyBlocksRaw(n, n2, n3, n4);
        }
    }

    public int getLightBrightnessForSkyBlocksRaw(int n, int n2, int n3, int n4) {
        int lightBrightnessForSkyBlocks = this.blockAccess.getLightBrightnessForSkyBlocks(n, n2, n3, n4);
        if (Config.isDynamicLights() && !this.getBlock(n, n2, n3).isOpaqueCube()) {
            lightBrightnessForSkyBlocks = DynamicLights.getCombinedLight(n, n2, n3, lightBrightnessForSkyBlocks);
        }

        return lightBrightnessForSkyBlocks;
    }

    @Override
    public Block getBlock(int n, int n2, int n3) {
        if (this.blocks == null) {
            return this.blockAccess.getBlock(n, n2, n3);
        }

        int positionIndex = this.getPositionIndex(n, n2, n3);
        if (positionIndex >= 0 && positionIndex < this.blocks.length) {
            Block block = this.blocks[positionIndex];
            if (block == null) {
                block = this.blockAccess.getBlock(n, n2, n3);
                this.blocks[positionIndex] = block;
            }

            return block;
        } else {
            return this.blockAccess.getBlock(n, n2, n3);
        }
    }

    public int getPositionIndex(int n, int n2, int n3) {
        int n4 = n - this.position.getX();
        int n5 = n2 - this.position.getY();
        int n6 = n3 - this.position.getZ();
        return n4 >= 0 && n5 >= 0 && n6 >= 0 && n4 < 20 && n5 < 20 && n6 < 20 ? n4 * 400 + n6 * 20 + n5 : -1;
    }

    public void renderStart() {
        if (this.combinedLights == null) {
            this.combinedLights = (int[])cacheCombinedLights.allocate(8000);
        }

        Arrays.fill(this.combinedLights, -1);
        if (this.blocks == null) {
            this.blocks = (Block[])cacheBlocks.allocate(8000);
        }

        Arrays.fill(this.blocks, null);
    }

    public void renderFinish() {
        cacheCombinedLights.free(this.combinedLights);
        this.combinedLights = null;
        cacheBlocks.free(this.blocks);
        this.blocks = null;
    }
}
