package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderServer implements IChunkProvider {
    public static Logger logger = LogManager.getLogger();
    public Set droppedChunksSet = Collections.newSetFromMap(new ConcurrentHashMap());
    public boolean chunkLoadOverride = true;
    public LongHashMap id2ChunkMap = new LongHashMap();
    public List loadedChunks = new ArrayList();
    public Chunk dummyChunk;
    public WorldServer worldObj;
    public IChunkLoader chunkLoader;
    public IChunkProvider serverChunkGenerator;

    public ChunkProviderServer(WorldServer worldServer, IChunkLoader chunkLoader, IChunkProvider chunkProvider) {
        this.dummyChunk = new EmptyChunk(worldServer, 0, 0);
        this.worldObj = worldServer;
        this.chunkLoader = chunkLoader;
        this.serverChunkGenerator = chunkProvider;
    }

    @Override
    public boolean chunkExists(int n, int n2) {
        return this.id2ChunkMap.containsItem(ChunkCoordIntPair.chunkXZ2Int(n, n2));
    }

    public List getChunks() {
        return this.loadedChunks;
    }

    public void dropChunk(int n, int n2) {
        if (this.worldObj.provider.canRespawnHere()) {
            ChunkCoordinates spawnPoint = this.worldObj.getSpawnPoint();
            int n3 = n * 16 + 8 - spawnPoint.posX;
            int n4 = n2 * 16 + 8 - spawnPoint.posZ;
            short s = 128;
            if (n3 < -s || n3 > s || n4 < -s || n4 > s) {
                this.droppedChunksSet.add(ChunkCoordIntPair.chunkXZ2Int(n, n2));
            }
        } else {
            this.droppedChunksSet.add(ChunkCoordIntPair.chunkXZ2Int(n, n2));
        }
    }

    public void unloadAllChunks() {
        for (Chunk chunk : (Iterable<Chunk>)this.loadedChunks) {
            this.dropChunk(chunk.xPosition, chunk.zPosition);
        }
    }

    @Override
    public Chunk loadChunk(int n, int n2) {
        long chunkXZ2Int = ChunkCoordIntPair.chunkXZ2Int(n, n2);
        this.droppedChunksSet.remove(chunkXZ2Int);
        Chunk valueByKey = (Chunk)this.id2ChunkMap.getValueByKey(chunkXZ2Int);
        if (valueByKey == null) {
            valueByKey = this.loadChunkFromFile(n, n2);
            if (valueByKey == null) {
                if (this.serverChunkGenerator == null) {
                    valueByKey = this.dummyChunk;
                } else {
                    try {
                        valueByKey = this.serverChunkGenerator.provideChunk(n, n2);
                    } catch (Throwable e) {
                        CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Exception generating new chunk");
                        CrashReportCategory makeCategory = makeCrashReport.makeCategory("Chunk to be generated");
                        makeCategory.addCrashSection("Location", String.format("%d,%d", n, n2));
                        makeCategory.addCrashSection("Position hash", chunkXZ2Int);
                        makeCategory.addCrashSection("Generator", this.serverChunkGenerator.makeString());
                        throw new ReportedException(makeCrashReport);
                    }
                }
            }

            this.id2ChunkMap.add(chunkXZ2Int, valueByKey);
            this.loadedChunks.add(valueByKey);
            valueByKey.onChunkLoad();
            valueByKey.populateChunk(this, this, n, n2);
        }

        return valueByKey;
    }

    @Override
    public Chunk provideChunk(int n, int n2) {
        Chunk valueByKey = (Chunk)this.id2ChunkMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(n, n2));
        return valueByKey == null ? (!this.worldObj.findingSpawnPoint && !this.chunkLoadOverride ? this.dummyChunk : this.loadChunk(n, n2)) : valueByKey;
    }

    public Chunk loadChunkFromFile(int n, int n2) {
        if (this.chunkLoader == null) {
            return null;
        }

        try {
            Chunk loadChunk = this.chunkLoader.loadChunk(this.worldObj, n, n2);
            if (loadChunk != null) {
                loadChunk.lastSaveTime = this.worldObj.getTotalWorldTime();
                if (this.serverChunkGenerator != null) {
                    this.serverChunkGenerator.recreateStructures(n, n2);
                }
            }

            return loadChunk;
        } catch (Exception e) {
            logger.error("Couldn't load chunk", e);
            return null;
        }
    }

    public void saveChunkExtraData(Chunk chunk) {
        if (this.chunkLoader != null) {
            try {
                this.chunkLoader.saveExtraChunkData(this.worldObj, chunk);
            } catch (Exception e) {
                logger.error("Couldn't save entities", e);
            }
        }
    }

    public void saveChunkData(Chunk chunk) {
        if (this.chunkLoader != null) {
            try {
                chunk.lastSaveTime = this.worldObj.getTotalWorldTime();
                this.chunkLoader.saveChunk(this.worldObj, chunk);
            } catch (IOException e) {
                logger.error("Couldn't save chunk", e);
            } catch (MinecraftException e) {
                logger.error("Couldn't save chunk; already in use by another instance of Minecraft?", e);
            }
        }
    }

    @Override
    public void populate(IChunkProvider chunkProvider, int n, int n2) {
        Chunk provideChunk = this.provideChunk(n, n2);
        if (!provideChunk.isTerrainPopulated) {
            provideChunk.populate();
            if (this.serverChunkGenerator != null) {
                this.serverChunkGenerator.populate(chunkProvider, n, n2);
                provideChunk.setChunkModified();
            }
        }
    }

    @Override
    public boolean saveChunks(boolean flag, IProgressUpdate progressUpdate) {
        int n = 0;
        ArrayList newArrayList = Lists.newArrayList(this.loadedChunks);

        for (int i = 0; i < newArrayList.size(); i++) {
            Chunk chunk = (Chunk)newArrayList.get(i);
            if (flag) {
                this.saveChunkExtraData(chunk);
            }

            if (chunk.needsSaving(flag)) {
                this.saveChunkData(chunk);
                chunk.isModified = false;
                if (++n == 24 && !flag) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public void saveExtraData() {
        if (this.chunkLoader != null) {
            this.chunkLoader.saveExtraData();
        }
    }

    @Override
    public boolean unloadQueuedChunks() {
        if (!this.worldObj.disableLevelSaving) {
            for (int i = 0; i < 100; i++) {
                if (!this.droppedChunksSet.isEmpty()) {
                    Long longValue = (Long)this.droppedChunksSet.iterator().next();
                    Chunk valueByKey = (Chunk)this.id2ChunkMap.getValueByKey(longValue);
                    if (valueByKey != null) {
                        valueByKey.onChunkUnload();
                        this.saveChunkData(valueByKey);
                        this.saveChunkExtraData(valueByKey);
                        this.loadedChunks.remove(valueByKey);
                    }

                    this.droppedChunksSet.remove(longValue);
                    this.id2ChunkMap.remove(longValue);
                }
            }

            if (this.chunkLoader != null) {
                this.chunkLoader.chunkTick();
            }
        }

        return this.serverChunkGenerator.unloadQueuedChunks();
    }

    @Override
    public boolean canSave() {
        return !this.worldObj.disableLevelSaving;
    }

    @Override
    public String makeString() {
        return "ServerChunkCache: " + this.id2ChunkMap.getNumHashElements() + " Drop: " + this.droppedChunksSet.size();
    }

    @Override
    public List getPossibleCreatures(EnumCreatureType enumCreatureType, int n, int n2, int n3) {
        return this.serverChunkGenerator.getPossibleCreatures(enumCreatureType, n, n2, n3);
    }

    @Override
    public ChunkPosition findClosestStructure(World world, String str, int n, int n2, int n3) {
        return this.serverChunkGenerator.findClosestStructure(world, str, n, n2, n3);
    }

    @Override
    public int getLoadedChunkCount() {
        return this.id2ChunkMap.getNumHashElements();
    }

    @Override
    public void recreateStructures(int n, int n2) {
    }
}
