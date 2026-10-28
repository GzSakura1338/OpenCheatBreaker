package net.minecraft.client.multiplayer;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderClient implements IChunkProvider {
    public static Logger logger = LogManager.getLogger();
    public LongHashMap chunkMapping = new LongHashMap();
    public List chunkListing = new ArrayList();
    public Chunk blankChunk;
    public World worldObj;

    public ChunkProviderClient(World world) {
        this.blankChunk = new EmptyChunk(world, 0, 0);
        this.worldObj = world;
    }

    @Override
    public boolean chunkExists(int n, int n2) {
        return true;
    }

    public void unloadChunk(int n, int n2) {
        Chunk provideChunk = this.provideChunk(n, n2);
        if (!provideChunk.isEmpty()) {
            provideChunk.onChunkUnload();
        }

        this.chunkMapping.remove(ChunkCoordIntPair.chunkXZ2Int(n, n2));
        this.chunkListing.remove(provideChunk);
    }

    @Override
    public Chunk loadChunk(int n, int n2) {
        Chunk chunk = new Chunk(this.worldObj, n, n2);
        this.chunkMapping.add(ChunkCoordIntPair.chunkXZ2Int(n, n2), chunk);
        this.chunkListing.add(chunk);
        chunk.isChunkLoaded = true;
        return chunk;
    }

    @Override
    public Chunk provideChunk(int n, int n2) {
        Chunk valueByKey = (Chunk)this.chunkMapping.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(n, n2));
        return valueByKey == null ? this.blankChunk : valueByKey;
    }

    @Override
    public boolean saveChunks(boolean flag, IProgressUpdate progressUpdate) {
        return true;
    }

    @Override
    public void saveExtraData() {
    }

    @Override
    public boolean unloadQueuedChunks() {
        long currentTimeMillis = System.currentTimeMillis();

        for (Chunk chunk : (Iterable<Chunk>)this.chunkListing) {
            chunk.populateBlockEntities(System.currentTimeMillis() - currentTimeMillis > 5L);
        }

        if (System.currentTimeMillis() - currentTimeMillis > 100L) {
            logger.info("Warning: Clientside chunk ticking took {} ms", System.currentTimeMillis() - currentTimeMillis);
        }

        return false;
    }

    @Override
    public boolean canSave() {
        return false;
    }

    @Override
    public void populate(IChunkProvider chunkProvider, int n, int n2) {
    }

    @Override
    public String makeString() {
        return "MultiplayerChunkCache: " + this.chunkMapping.getNumHashElements() + ", " + this.chunkListing.size();
    }

    @Override
    public List getPossibleCreatures(EnumCreatureType enumCreatureType, int n, int n2, int n3) {
        return null;
    }

    @Override
    public ChunkPosition findClosestStructure(World world, String str, int n, int n2, int n3) {
        return null;
    }

    @Override
    public int getLoadedChunkCount() {
        return this.chunkListing.size();
    }

    @Override
    public void recreateStructures(int n, int n2) {
    }
}
