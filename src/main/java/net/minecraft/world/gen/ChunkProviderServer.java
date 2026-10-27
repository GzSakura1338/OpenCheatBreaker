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
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ChunkProviderServer implements IChunkProvider {
   public static Logger logger = LogManager.getLogger();
   public Set<Long> droppedChunksSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
   public boolean chunkLoadOverride = true;
   public LongHashMap<Chunk> id2ChunkMap = new LongHashMap<>();
   public List<Chunk> loadedChunks = Lists.newArrayList();
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
   public boolean chunkExists(int x, int z) {
      return this.id2ChunkMap.containsItem(ChunkCoordIntPair.chunkXZ2Int(x, z));
   }

   public List<Chunk> getChunks() {
      return this.loadedChunks;
   }

   public void dropChunk(int x, int z) {
      if (this.worldObj.provider.canRespawnHere()) {
         if (!this.worldObj.isSpawnChunk(x, z)) {
            this.droppedChunksSet.add(ChunkCoordIntPair.chunkXZ2Int(x, z));
         }
      } else {
         this.droppedChunksSet.add(ChunkCoordIntPair.chunkXZ2Int(x, z));
      }
   }

   public void unloadAllChunks() {
      for (Chunk chunk : this.loadedChunks) {
         this.dropChunk(chunk.xPosition, chunk.zPosition);
      }
   }

   public Chunk loadChunk(int chunkX, int chunkZ) {
      long chunkXZ2Int = ChunkCoordIntPair.chunkXZ2Int(chunkX, chunkZ);
      this.droppedChunksSet.remove(chunkXZ2Int);
      Chunk valueByKey = this.id2ChunkMap.getValueByKey(chunkXZ2Int);
      if (valueByKey == null) {
         valueByKey = this.loadChunkFromFile(chunkX, chunkZ);
         if (valueByKey == null) {
            if (this.serverChunkGenerator == null) {
               valueByKey = this.dummyChunk;
            } else {
               try {
                  valueByKey = this.serverChunkGenerator.provideChunk(chunkX, chunkZ);
               } catch (Throwable e) {
                  CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Exception generating new chunk");
                  CrashReportCategory makeCategory = makeCrashReport.makeCategory("Chunk to be generated");
                  makeCategory.addCrashSection("Location", String.format("%d,%d", chunkX, chunkZ));
                  makeCategory.addCrashSection("Position hash", chunkXZ2Int);
                  makeCategory.addCrashSection("Generator", this.serverChunkGenerator.makeString());
                  throw new ReportedException(makeCrashReport);
               }
            }
         }

         this.id2ChunkMap.add(chunkXZ2Int, valueByKey);
         this.loadedChunks.add(valueByKey);
         valueByKey.onChunkLoad();
         valueByKey.populateChunk(this, this, chunkX, chunkZ);
      }

      return valueByKey;
   }

   @Override
   public Chunk provideChunk(int x, int z) {
      Chunk valueByKey = this.id2ChunkMap.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(x, z));
      return valueByKey == null ? (!this.worldObj.isFindingSpawnPoint() && !this.chunkLoadOverride ? this.dummyChunk : this.loadChunk(x, z)) : valueByKey;
   }

   public Chunk loadChunkFromFile(int x, int z) {
      if (this.chunkLoader == null) {
         return null;
      }

      try {
         Chunk loadChunk = this.chunkLoader.loadChunk(this.worldObj, x, z);
         if (loadChunk != null) {
            loadChunk.setLastSaveTime(this.worldObj.getTotalWorldTime());
            if (this.serverChunkGenerator != null) {
               this.serverChunkGenerator.recreateStructures(loadChunk, x, z);
            }
         }

         return loadChunk;
      } catch (Exception e) {
         logger.error("Couldn't load chunk", e);
         return null;
      }
   }

   public void saveChunkExtraData(Chunk chunkIn) {
      if (this.chunkLoader != null) {
         try {
            this.chunkLoader.saveExtraChunkData(this.worldObj, chunkIn);
         } catch (Exception e) {
            logger.error("Couldn't save entities", e);
         }
      }
   }

   public void saveChunkData(Chunk chunkIn) {
      if (this.chunkLoader != null) {
         try {
            chunkIn.setLastSaveTime(this.worldObj.getTotalWorldTime());
            this.chunkLoader.saveChunk(this.worldObj, chunkIn);
         } catch (IOException e) {
            logger.error("Couldn't save chunk", e);
         } catch (MinecraftException e) {
            logger.error("Couldn't save chunk; already in use by another instance of Minecraft?", e);
         }
      }
   }

   @Override
   public void populate(IChunkProvider chunkProvider, int x, int z) {
      Chunk provideChunk = this.provideChunk(x, z);
      if (!provideChunk.isTerrainPopulated()) {
         provideChunk.populate();
         if (this.serverChunkGenerator != null) {
            this.serverChunkGenerator.populate(chunkProvider, x, z);
            provideChunk.setChunkModified();
         }
      }
   }

   @Override
   public boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z) {
      if (this.serverChunkGenerator != null && this.serverChunkGenerator.populateChunk(chunkProvider, chunkIn, x, z)) {
         Chunk provideChunk = this.provideChunk(x, z);
         provideChunk.setChunkModified();
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean saveChunks(boolean saveAllChunks, IProgressUpdate progressCallback) {
      int n = 0;
      ArrayList newArrayList = Lists.newArrayList(this.loadedChunks);

      for (int i = 0; i < newArrayList.size(); i++) {
         Chunk chunk = (Chunk)newArrayList.get(i);
         if (saveAllChunks) {
            this.saveChunkExtraData(chunk);
         }

         if (chunk.needsSaving(saveAllChunks)) {
            this.saveChunkData(chunk);
            chunk.setModified(false);
            if (++n == 24 && !saveAllChunks) {
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
               Long longValue = this.droppedChunksSet.iterator().next();
               Chunk valueByKey = this.id2ChunkMap.getValueByKey(longValue);
               if (valueByKey != null) {
                  valueByKey.onChunkUnload();
                  this.saveChunkData(valueByKey);
                  this.saveChunkExtraData(valueByKey);
                  this.id2ChunkMap.remove(longValue);
                  this.loadedChunks.remove(valueByKey);
               }

               this.droppedChunksSet.remove(longValue);
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
   public List<SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPos pos) {
      return this.serverChunkGenerator.getPossibleCreatures(creatureType, pos);
   }

   @Override
   public BlockPos getStrongholdGen(World worldIn, String structureName, BlockPos position) {
      return this.serverChunkGenerator.getStrongholdGen(worldIn, structureName, position);
   }

   @Override
   public int getLoadedChunkCount() {
      return this.id2ChunkMap.getNumHashElements();
   }

   @Override
   public void recreateStructures(Chunk chunkIn, int x, int z) {
   }

   @Override
   public Chunk provideChunk(BlockPos blockPosIn) {
      return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
   }
}
