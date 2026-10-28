package net.minecraft.world.chunk;

import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;

public interface IChunkProvider {
    boolean chunkExists(int n, int n2);

    Chunk provideChunk(int n, int n2);

    Chunk loadChunk(int n, int n2);

    void populate(IChunkProvider chunkProvider, int n, int n2);

    boolean saveChunks(boolean flag, IProgressUpdate progressUpdate);

    boolean unloadQueuedChunks();

    boolean canSave();

    String makeString();

    List getPossibleCreatures(EnumCreatureType enumCreatureType, int n, int n2, int n3);

    ChunkPosition findClosestStructure(World world, String str, int n, int n2, int n3);

    int getLoadedChunkCount();

    void recreateStructures(int n, int n2);

    void saveExtraData();
}
