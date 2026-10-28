package net.minecraft.world.chunk.storage;

import java.io.IOException;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public interface IChunkLoader {
    Chunk loadChunk(World world, int n, int n2) throws IOException, InterruptedException, java.util.concurrent.ExecutionException;

    void saveChunk(World world, Chunk chunk) throws IOException, MinecraftException;

    void saveExtraChunkData(World world, Chunk chunk);

    void chunkTick();

    void saveExtraData();
}
