package net.minecraft.world.chunk;

import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;

public interface IChunkProvider {
   boolean chunkExists(int n, int n2);

   Chunk provideChunk(int n, int n2);

   Chunk provideChunk(BlockPos blockPos);

   void populate(IChunkProvider chunkProvider, int n, int n2);

   boolean populateChunk(IChunkProvider chunkProvider, Chunk chunk, int n, int n2);

   boolean saveChunks(boolean flag, IProgressUpdate progressUpdate);

   boolean unloadQueuedChunks();

   boolean canSave();

   String makeString();

   List<SpawnListEntry> getPossibleCreatures(EnumCreatureType enumCreatureType, BlockPos blockPos);

   BlockPos getStrongholdGen(World world, String str, BlockPos blockPos);

   int getLoadedChunkCount();

   void recreateStructures(Chunk chunk, int n, int n2);

   void saveExtraData();
}
