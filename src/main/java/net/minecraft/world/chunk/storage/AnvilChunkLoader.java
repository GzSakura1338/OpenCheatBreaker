package net.minecraft.world.chunk.storage;

import com.cheatbreaker.client.CheatBreaker;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilChunkLoader implements IThreadedFileIO, IChunkLoader {
   public static Logger logger = LogManager.getLogger();
   public Map<ChunkCoordIntPair, NBTTagCompound> chunksToRemove = new ConcurrentHashMap<>();
   public Set<ChunkCoordIntPair> pendingAnvilChunksCoordinates = Collections.newSetFromMap(new ConcurrentHashMap<>());
   public boolean isSaving = false;
   public Minecraft mc = Minecraft.getMinecraft();
   public File chunkSaveLocation;

   public AnvilChunkLoader(File chunkSaveLocationIn) {
      this.chunkSaveLocation = chunkSaveLocationIn;
   }

   @Override
   public Chunk loadChunk(World worldIn, int x, int z) throws IOException, InterruptedException, java.util.concurrent.ExecutionException {
      return CheatBreaker.getInstance().getKeybindSettings().multithreadedChunkLoading.getBooleanValue() && !this.mc.isSingleplayer()
         ? this.findChunk(worldIn, x, z)
         : this.createChunkCoordIntPair(worldIn, x, z);
   }

   public Chunk checkedReadChunkFromNBT(World worldIn, int x, int z, NBTTagCompound nbttagCompound) {
      if (!nbttagCompound.hasKey("Level", 10)) {
         logger.error("Chunk file at " + x + "," + z + " is missing level data, skipping");
         return null;
      }

      NBTTagCompound compoundTag = nbttagCompound.getCompoundTag("Level");
      if (!compoundTag.hasKey("Sections", 9)) {
         logger.error("Chunk file at " + x + "," + z + " is missing block data, skipping");
         return null;
      }

      Chunk readChunkFromNBT = this.readChunkFromNBT(worldIn, compoundTag);
      if (!readChunkFromNBT.isAtLocation(x, z)) {
         logger.error(
            "Chunk file at "
               + x
               + ","
               + z
               + " is in the wrong location; relocating. (Expected "
               + x
               + ", "
               + z
               + ", got "
               + readChunkFromNBT.xPosition
               + ", "
               + readChunkFromNBT.zPosition
               + ")"
         );
         compoundTag.setInteger("xPos", x);
         compoundTag.setInteger("zPos", z);
         readChunkFromNBT = this.readChunkFromNBT(worldIn, compoundTag);
      }

      return readChunkFromNBT;
   }

   @Override
   public void saveChunk(World worldIn, Chunk chunkIn) throws IOException, MinecraftException {
      worldIn.checkSessionLock();

      try {
         NBTTagCompound nbttagCompound = new NBTTagCompound();
         NBTTagCompound nbttagCompound2 = new NBTTagCompound();
         nbttagCompound.setTag("Level", nbttagCompound2);
         this.writeChunkToNBT(chunkIn, worldIn, nbttagCompound2);
         this.addChunkToPending(chunkIn.getChunkCoordIntPair(), nbttagCompound);
      } catch (Exception e) {
         logger.error("Failed to save chunk", e);
      }
   }

   public void addChunkToPending(ChunkCoordIntPair chunkCoordIntPair, NBTTagCompound nbttagCompound) {
      if (!this.pendingAnvilChunksCoordinates.contains(chunkCoordIntPair)) {
         this.chunksToRemove.put(chunkCoordIntPair, nbttagCompound);
      }

      ThreadedFileIOBase.getThreadedIOInstance().queueIO(this);
   }

   @Override
   public boolean writeNextIO() {
      if (this.chunksToRemove.isEmpty()) {
         if (this.isSaving) {
            logger.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", new Object[]{this.chunkSaveLocation.getName()});
         }

         return false;
      } else {
         ChunkCoordIntPair chunkCoordIntPair = this.chunksToRemove.keySet().iterator().next();

         boolean flag;
         try {
            this.pendingAnvilChunksCoordinates.add(chunkCoordIntPair);
            NBTTagCompound nbttagCompound = this.chunksToRemove.remove(chunkCoordIntPair);
            if (nbttagCompound != null) {
               try {
                  this.write(chunkCoordIntPair, nbttagCompound);
               } catch (Exception e) {
                  logger.error("Failed to save chunk", e);
               }
            }

            flag = true;
         } finally {
            this.pendingAnvilChunksCoordinates.remove(chunkCoordIntPair);
         }

         return flag;
      }
   }

   public void write(ChunkCoordIntPair chunkCoordIntPair, NBTTagCompound nbttagCompound) throws IOException {
      DataOutputStream chunkOutputStream = RegionFileCache.getChunkOutputStream(this.chunkSaveLocation, chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos);
      CompressedStreamTools.write(nbttagCompound, chunkOutputStream);
      chunkOutputStream.close();
   }

   @Override
   public void saveExtraChunkData(World worldIn, Chunk chunkIn) throws IOException {
   }

   @Override
   public void chunkTick() {
   }

   @Override
   public void saveExtraData() {
      try {
         this.isSaving = true;

         while (true) {
            if (this.writeNextIO()) {
            }
         }
      } finally {
         this.isSaving = false;
      }
   }

   public void writeChunkToNBT(Chunk chunkIn, World worldIn, NBTTagCompound nbttagCompound) {
      nbttagCompound.setByte("V", (byte)1);
      nbttagCompound.setInteger("xPos", chunkIn.xPosition);
      nbttagCompound.setInteger("zPos", chunkIn.zPosition);
      nbttagCompound.setLong("LastUpdate", worldIn.getTotalWorldTime());
      nbttagCompound.setIntArray("HeightMap", chunkIn.getHeightMap());
      nbttagCompound.setBoolean("TerrainPopulated", chunkIn.isTerrainPopulated());
      nbttagCompound.setBoolean("LightPopulated", chunkIn.isLightPopulated());
      nbttagCompound.setLong("InhabitedTime", chunkIn.getInhabitedTime());
      ExtendedBlockStorage[] blockStorageArray = chunkIn.getBlockStorageArray();
      NBTTagList nbttagList = new NBTTagList();
      boolean flag = !worldIn.provider.getHasNoSky();

      for (ExtendedBlockStorage extendedBlockStorage : blockStorageArray) {
         if (extendedBlockStorage != null) {
            NBTTagCompound nbttagCompound2 = new NBTTagCompound();
            nbttagCompound2.setByte("Y", (byte)(extendedBlockStorage.getYLocation() >> 4 & 0xFF));
            byte[] bytes = new byte[extendedBlockStorage.getData().length];
            NibbleArray nibbleArray = new NibbleArray();
            NibbleArray nibbleArray2 = null;

            for (int i = 0; i < extendedBlockStorage.getData().length; i++) {
               char c = extendedBlockStorage.getData()[i];
               int n = i & 15;
               int n2 = i >> 8 & 15;
               int n3 = i >> 4 & 15;
               if (c >> '\f' != 0) {
                  if (nibbleArray2 == null) {
                     nibbleArray2 = new NibbleArray();
                  }

                  nibbleArray2.set(n, n2, n3, c >> '\f');
               }

               bytes[i] = (byte)(c >> 4 & 0xFF);
               nibbleArray.set(n, n2, n3, c & 15);
            }

            nbttagCompound2.setByteArray("Blocks", bytes);
            nbttagCompound2.setByteArray("Data", nibbleArray.getData());
            if (nibbleArray2 != null) {
               nbttagCompound2.setByteArray("Add", nibbleArray2.getData());
            }

            nbttagCompound2.setByteArray("BlockLight", extendedBlockStorage.getBlocklightArray().getData());
            if (flag) {
               nbttagCompound2.setByteArray("SkyLight", extendedBlockStorage.getSkylightArray().getData());
            } else {
               nbttagCompound2.setByteArray("SkyLight", new byte[extendedBlockStorage.getBlocklightArray().getData().length]);
            }

            nbttagList.appendTag(nbttagCompound2);
         }
      }

      nbttagCompound.setTag("Sections", nbttagList);
      nbttagCompound.setByteArray("Biomes", chunkIn.getBiomeArray());
      chunkIn.setHasEntities(false);
      NBTTagList nbttagList2 = new NBTTagList();

      for (int i = 0; i < chunkIn.getEntityLists().length; i++) {
         for (Entity entity : chunkIn.getEntityLists()[i]) {
            NBTTagCompound nbttagCompound2 = new NBTTagCompound();
            if (entity.writeToNBTOptional(nbttagCompound2)) {
               chunkIn.setHasEntities(true);
               nbttagList2.appendTag(nbttagCompound2);
            }
         }
      }

      nbttagCompound.setTag("Entities", nbttagList2);
      NBTTagList nbttagList3 = new NBTTagList();

      for (TileEntity tileEntity : chunkIn.getTileEntityMap().values()) {
         NBTTagCompound nbttagCompound2 = new NBTTagCompound();
         tileEntity.writeToNBT(nbttagCompound2);
         nbttagList3.appendTag(nbttagCompound2);
      }

      nbttagCompound.setTag("TileEntities", nbttagList3);
      List pendingBlockUpdates = worldIn.getPendingBlockUpdates(chunkIn, false);
      if (pendingBlockUpdates != null) {
         long totalWorldTime = worldIn.getTotalWorldTime();
         NBTTagList nbttagList4 = new NBTTagList();

         for (NextTickListEntry nextTickListEntry : (Iterable<NextTickListEntry>)pendingBlockUpdates) {
            NBTTagCompound nbttagCompound2 = new NBTTagCompound();
            ResourceLocation nameForObject = Block.blockRegistry.getNameForObject(nextTickListEntry.getBlock());
            nbttagCompound2.setString("i", nameForObject == null ? "" : nameForObject.toString());
            nbttagCompound2.setInteger("x", nextTickListEntry.position.getX());
            nbttagCompound2.setInteger("y", nextTickListEntry.position.getY());
            nbttagCompound2.setInteger("z", nextTickListEntry.position.getZ());
            nbttagCompound2.setInteger("t", (int)(nextTickListEntry.scheduledTime - totalWorldTime));
            nbttagCompound2.setInteger("p", nextTickListEntry.priority);
            nbttagList4.appendTag(nbttagCompound2);
         }

         nbttagCompound.setTag("TileTicks", nbttagList4);
      }
   }

   public Chunk readChunkFromNBT(World worldIn, NBTTagCompound nbttagCompound) {
      int integer = nbttagCompound.getInteger("xPos");
      int integer2 = nbttagCompound.getInteger("zPos");
      Chunk chunk = new Chunk(worldIn, integer, integer2);
      chunk.setHeightMap(nbttagCompound.getIntArray("HeightMap"));
      chunk.setTerrainPopulated(nbttagCompound.getBoolean("TerrainPopulated"));
      chunk.setLightPopulated(nbttagCompound.getBoolean("LightPopulated"));
      chunk.setInhabitedTime(nbttagCompound.getLong("InhabitedTime"));
      NBTTagList tagList = nbttagCompound.getTagList("Sections", 10);
      byte b = 16;
      ExtendedBlockStorage[] extendedBlockStorages = new ExtendedBlockStorage[b];
      boolean flag = !worldIn.provider.getHasNoSky();

      for (int i = 0; i < tagList.tagCount(); i++) {
         NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
         byte byte2 = compoundTagAt.getByte("Y");
         ExtendedBlockStorage extendedBlockStorage = new ExtendedBlockStorage(byte2 << 4, flag);
         byte[] byteArray = compoundTagAt.getByteArray("Blocks");
         NibbleArray nibbleArray = new NibbleArray(compoundTagAt.getByteArray("Data"));
         NibbleArray nibbleArray2 = compoundTagAt.hasKey("Add", 7) ? new NibbleArray(compoundTagAt.getByteArray("Add")) : null;
         char[] chars = new char[byteArray.length];

         for (int j = 0; j < chars.length; j++) {
            int n = j & 15;
            int n2 = j >> 8 & 15;
            int n3 = j >> 4 & 15;
            int n4 = nibbleArray2 != null ? nibbleArray2.get(n, n2, n3) : 0;
            chars[j] = (char)(n4 << 12 | (byteArray[j] & 255) << 4 | nibbleArray.get(n, n2, n3));
         }

         extendedBlockStorage.setData(chars);
         extendedBlockStorage.setBlocklightArray(new NibbleArray(compoundTagAt.getByteArray("BlockLight")));
         if (flag) {
            extendedBlockStorage.setSkylightArray(new NibbleArray(compoundTagAt.getByteArray("SkyLight")));
         }

         extendedBlockStorage.removeInvalidBlocks();
         extendedBlockStorages[byte2] = extendedBlockStorage;
      }

      chunk.setStorageArrays(extendedBlockStorages);
      if (nbttagCompound.hasKey("Biomes", 7)) {
         chunk.setBiomeArray(nbttagCompound.getByteArray("Biomes"));
      }

      NBTTagList tagList2 = nbttagCompound.getTagList("Entities", 10);
      if (tagList2 != null) {
         for (int i = 0; i < tagList2.tagCount(); i++) {
            NBTTagCompound compoundTagAt = tagList2.getCompoundTagAt(i);
            Entity createEntityFromNBT = EntityList.createEntityFromNBT(compoundTagAt, worldIn);
            chunk.setHasEntities(true);
            if (createEntityFromNBT != null) {
               chunk.addEntity(createEntityFromNBT);
               Entity entity = createEntityFromNBT;

               for (NBTTagCompound nbttagCompound2 = compoundTagAt; nbttagCompound2.hasKey("Riding", 10); nbttagCompound2 = nbttagCompound2.getCompoundTag("Riding")) {
                  Entity createEntityFromNBT2 = EntityList.createEntityFromNBT(nbttagCompound2.getCompoundTag("Riding"), worldIn);
                  if (createEntityFromNBT2 != null) {
                     chunk.addEntity(createEntityFromNBT2);
                     entity.mountEntity(createEntityFromNBT2);
                  }

                  entity = createEntityFromNBT2;
               }
            }
         }
      }

      NBTTagList tagList3 = nbttagCompound.getTagList("TileEntities", 10);
      if (tagList3 != null) {
         for (int i = 0; i < tagList3.tagCount(); i++) {
            NBTTagCompound compoundTagAt = tagList3.getCompoundTagAt(i);
            TileEntity createAndLoadEntity = TileEntity.createAndLoadEntity(compoundTagAt);
            if (createAndLoadEntity != null) {
               chunk.addTileEntity(createAndLoadEntity);
            }
         }
      }

      if (nbttagCompound.hasKey("TileTicks", 9)) {
         NBTTagList tagList4 = nbttagCompound.getTagList("TileTicks", 10);
         if (tagList4 != null) {
            for (int i = 0; i < tagList4.tagCount(); i++) {
               NBTTagCompound compoundTagAt = tagList4.getCompoundTagAt(i);
               Block block;
               if (compoundTagAt.hasKey("i", 8)) {
                  block = Block.getBlockFromName(compoundTagAt.getString("i"));
               } else {
                  block = Block.getBlockById(compoundTagAt.getInteger("i"));
               }

               worldIn.scheduleBlockUpdate(
                  new BlockPos(compoundTagAt.getInteger("x"), compoundTagAt.getInteger("y"), compoundTagAt.getInteger("z")), block, compoundTagAt.getInteger("t"), compoundTagAt.getInteger("p")
               );
            }
         }
      }

      return chunk;
   }

   public Chunk createChunkCoordIntPair(World world, int n, int n2) throws IOException {
      ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(n, n2);
      NBTTagCompound nbttagCompound = this.chunksToRemove.get(chunkCoordIntPair);
      if (nbttagCompound == null) {
         DataInputStream chunkInputStream = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, n, n2);
         if (chunkInputStream == null) {
            return null;
         }

         nbttagCompound = CompressedStreamTools.read(chunkInputStream);
      }

      return this.checkedReadChunkFromNBT(world, n, n2, nbttagCompound);
   }

   public Chunk findChunk(World world, int n, int n2) throws InterruptedException, java.util.concurrent.ExecutionException {
      ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(n, n2);
      AtomicReference atomicReference = new AtomicReference<>(this.chunksToRemove.get(chunkCoordIntPair));
      ExecutorService newSingleThreadExecutor = Executors.newSingleThreadExecutor();
      Future submit = newSingleThreadExecutor.submit(() -> {
         if (atomicReference.get() == null) {
            DataInputStream chunkInputStream = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, n, n2);
            if (chunkInputStream == null) {
               return;
            }

            try {
               atomicReference.set(CompressedStreamTools.read(chunkInputStream));
            } catch (IOException e) {
               e.printStackTrace();
            }
         }
      });
      newSingleThreadExecutor.shutdown();
      return (Chunk)submit.get();
   }
}
