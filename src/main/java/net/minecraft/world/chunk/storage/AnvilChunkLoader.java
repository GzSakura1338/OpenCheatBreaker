package net.minecraft.world.chunk.storage;

import com.cheatbreaker.client.CheatBreaker;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    public List chunksToRemove = new ArrayList();
    public Set pendingAnvilChunksCoordinates = new HashSet();
    public Object syncLockObject = new Object();
    public Minecraft mc = Minecraft.getMinecraft();
    public File chunkSaveLocation;

    public AnvilChunkLoader(File file) {
        this.chunkSaveLocation = file;
    }

    @Override
    public Chunk loadChunk(World world, int n, int n2) throws IOException, InterruptedException, java.util.concurrent.ExecutionException {
        return CheatBreaker.getInstance().getKeybindSettings().multithreadedChunkLoading.getBooleanValue() && !this.mc.isSingleplayer()
            ? this.findChunk(world, n, n2)
            : this.createChunkCoordIntPair(world, n, n2);
    }

    public Chunk checkedReadChunkFromNBT(World world, int n, int n2, NBTTagCompound nbttagCompound) {
        if (!nbttagCompound.hasKey("Level", 10)) {
            logger.error("Chunk file at " + n + "," + n2 + " is missing level data, skipping");
            return null;
        }

        if (!nbttagCompound.getCompoundTag("Level").hasKey("Sections", 9)) {
            logger.error("Chunk file at " + n + "," + n2 + " is missing block data, skipping");
            return null;
        }

        Chunk readChunkFromNBT = this.readChunkFromNBT(world, nbttagCompound.getCompoundTag("Level"));
        if (!readChunkFromNBT.isAtLocation(n, n2)) {
            logger.error(
                "Chunk file at "
                    + n
                    + ","
                    + n2
                    + " is in the wrong location; relocating. (Expected "
                    + n
                    + ", "
                    + n2
                    + ", got "
                    + readChunkFromNBT.xPosition
                    + ", "
                    + readChunkFromNBT.zPosition
                    + ")"
            );
            nbttagCompound.setInteger("xPos", n);
            nbttagCompound.setInteger("zPos", n2);
            readChunkFromNBT = this.readChunkFromNBT(world, nbttagCompound.getCompoundTag("Level"));
        }

        return readChunkFromNBT;
    }

    @Override
    public void saveChunk(World world, Chunk chunk) throws IOException, MinecraftException {
        world.checkSessionLock();

        try {
            NBTTagCompound nbttagCompound = new NBTTagCompound();
            NBTTagCompound nbttagCompound2 = new NBTTagCompound();
            nbttagCompound.setTag("Level", nbttagCompound2);
            this.writeChunkToNBT(chunk, world, nbttagCompound2);
            this.addChunkToPending(chunk.getChunkCoordIntPair(), nbttagCompound);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addChunkToPending(ChunkCoordIntPair chunkCoordIntPair, NBTTagCompound nbttagCompound) {
        Object syncLockObject = this.syncLockObject;
        synchronized (this.syncLockObject) {
            if (this.pendingAnvilChunksCoordinates.contains(chunkCoordIntPair)) {
                for (int i = 0; i < this.chunksToRemove.size(); i++) {
                    if (((AnvilChunkLoader.PendingChunk)this.chunksToRemove.get(i)).chunkCoordinate.equals(chunkCoordIntPair)) {
                        this.chunksToRemove.set(i, new AnvilChunkLoader.PendingChunk(chunkCoordIntPair, nbttagCompound));
                        return;
                    }
                }
            }

            this.chunksToRemove.add(new AnvilChunkLoader.PendingChunk(chunkCoordIntPair, nbttagCompound));
            this.pendingAnvilChunksCoordinates.add(chunkCoordIntPair);
            ThreadedFileIOBase.threadedIOInstance.queueIO(this);
        }
    }

    @Override
    public boolean writeNextIO() {
        AnvilChunkLoader.PendingChunk pendingChunk = null;
        Object syncLockObject = this.syncLockObject;
        synchronized (this.syncLockObject) {
            if (this.chunksToRemove.isEmpty()) {
                return false;
            }

            pendingChunk = (AnvilChunkLoader.PendingChunk)this.chunksToRemove.remove(0);
            this.pendingAnvilChunksCoordinates.remove(pendingChunk.chunkCoordinate);
        }

        if (pendingChunk != null) {
            try {
                this.writeChunkNBTTags(pendingChunk);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    public void writeChunkNBTTags(AnvilChunkLoader.PendingChunk pendingChunk) throws IOException {
        DataOutputStream chunkOutputStream = RegionFileCache.getChunkOutputStream(this.chunkSaveLocation, pendingChunk.chunkCoordinate.chunkXPos, pendingChunk.chunkCoordinate.chunkZPos);
        CompressedStreamTools.write(pendingChunk.nbtTags, chunkOutputStream);
        chunkOutputStream.close();
    }

    @Override
    public void saveExtraChunkData(World world, Chunk chunk) {
    }

    @Override
    public void chunkTick() {
    }

    @Override
    public void saveExtraData() {
        while (this.writeNextIO()) {
        }
    }

    public void writeChunkToNBT(Chunk chunk, World world, NBTTagCompound nbttagCompound) {
        nbttagCompound.setByte("V", (byte)1);
        nbttagCompound.setInteger("xPos", chunk.xPosition);
        nbttagCompound.setInteger("zPos", chunk.zPosition);
        nbttagCompound.setLong("LastUpdate", world.getTotalWorldTime());
        nbttagCompound.setIntArray("HeightMap", chunk.heightMap);
        nbttagCompound.setBoolean("TerrainPopulated", chunk.isTerrainPopulated);
        nbttagCompound.setBoolean("LightPopulated", chunk.isLightPopulated);
        nbttagCompound.setLong("InhabitedTime", chunk.inhabitedTime);
        ExtendedBlockStorage[] blockStorageArray = chunk.getBlockStorageArray();
        NBTTagList nbttagList = new NBTTagList();
        boolean flag = !world.provider.hasNoSky;
        ExtendedBlockStorage[] extendedBlockStorages = blockStorageArray;
        int length = blockStorageArray.length;

        for (int i = 0; i < length; i++) {
            ExtendedBlockStorage extendedBlockStorage = extendedBlockStorages[i];
            if (extendedBlockStorage != null) {
                NBTTagCompound nbttagCompound2 = new NBTTagCompound();
                nbttagCompound2.setByte("Y", (byte)(extendedBlockStorage.getYLocation() >> 4 & 0xFF));
                nbttagCompound2.setByteArray("Blocks", extendedBlockStorage.getBlockLSBArray());
                if (extendedBlockStorage.getBlockMSBArray() != null) {
                    nbttagCompound2.setByteArray("Add", extendedBlockStorage.getBlockMSBArray().data);
                }

                nbttagCompound2.setByteArray("Data", extendedBlockStorage.getMetadataArray().data);
                nbttagCompound2.setByteArray("BlockLight", extendedBlockStorage.getBlocklightArray().data);
                if (flag) {
                    nbttagCompound2.setByteArray("SkyLight", extendedBlockStorage.getSkylightArray().data);
                } else {
                    nbttagCompound2.setByteArray("SkyLight", new byte[extendedBlockStorage.getBlocklightArray().data.length]);
                }

                nbttagList.appendTag(nbttagCompound2);
            }
        }

        nbttagCompound.setTag("Sections", nbttagList);
        nbttagCompound.setByteArray("Biomes", chunk.getBiomeArray());
        chunk.hasEntities = false;
        NBTTagList nbttagList2 = new NBTTagList();

        for (int i = 0; i < chunk.entityLists.length; i++) {
            for (Entity entity : (Iterable<Entity>)chunk.entityLists[i]) {
                NBTTagCompound nbttagCompound2 = new NBTTagCompound();
                if (entity.writeToNBTOptional(nbttagCompound2)) {
                    chunk.hasEntities = true;
                    nbttagList2.appendTag(nbttagCompound2);
                }
            }
        }

        nbttagCompound.setTag("Entities", nbttagList2);
        NBTTagList nbttagList3 = new NBTTagList();

        for (TileEntity tileEntity : (Iterable<TileEntity>)chunk.chunkTileEntityMap.values()) {
            NBTTagCompound nbttagCompound2 = new NBTTagCompound();
            tileEntity.writeToNBT(nbttagCompound2);
            nbttagList3.appendTag(nbttagCompound2);
        }

        nbttagCompound.setTag("TileEntities", nbttagList3);
        List pendingBlockUpdates = world.getPendingBlockUpdates(chunk, false);
        if (pendingBlockUpdates != null) {
            long totalWorldTime = world.getTotalWorldTime();
            NBTTagList nbttagList4 = new NBTTagList();

            for (NextTickListEntry nextTickListEntry : (Iterable<NextTickListEntry>)pendingBlockUpdates) {
                NBTTagCompound nbttagCompound2 = new NBTTagCompound();
                nbttagCompound2.setInteger("i", Block.getIdFromBlock(nextTickListEntry.getBlock()));
                nbttagCompound2.setInteger("x", nextTickListEntry.xCoord);
                nbttagCompound2.setInteger("y", nextTickListEntry.yCoord);
                nbttagCompound2.setInteger("z", nextTickListEntry.zCoord);
                nbttagCompound2.setInteger("t", (int)(nextTickListEntry.scheduledTime - totalWorldTime));
                nbttagCompound2.setInteger("p", nextTickListEntry.priority);
                nbttagList4.appendTag(nbttagCompound2);
            }

            nbttagCompound.setTag("TileTicks", nbttagList4);
        }
    }

    public Chunk readChunkFromNBT(World world, NBTTagCompound nbttagCompound) {
        int integer = nbttagCompound.getInteger("xPos");
        int integer2 = nbttagCompound.getInteger("zPos");
        Chunk chunk = new Chunk(world, integer, integer2);
        chunk.heightMap = nbttagCompound.getIntArray("HeightMap");
        chunk.isTerrainPopulated = nbttagCompound.getBoolean("TerrainPopulated");
        chunk.isLightPopulated = nbttagCompound.getBoolean("LightPopulated");
        chunk.inhabitedTime = nbttagCompound.getLong("InhabitedTime");
        NBTTagList tagList = nbttagCompound.getTagList("Sections", 10);
        byte b = 16;
        ExtendedBlockStorage[] extendedBlockStorages = new ExtendedBlockStorage[b];
        boolean flag = !world.provider.hasNoSky;

        for (int i = 0; i < tagList.tagCount(); i++) {
            NBTTagCompound compoundTagAt = tagList.getCompoundTagAt(i);
            byte byte2 = compoundTagAt.getByte("Y");
            ExtendedBlockStorage extendedBlockStorage = new ExtendedBlockStorage(byte2 << 4, flag);
            extendedBlockStorage.setBlockLSBArray(compoundTagAt.getByteArray("Blocks"));
            if (compoundTagAt.hasKey("Add", 7)) {
                extendedBlockStorage.setBlockMSBArray(new NibbleArray(compoundTagAt.getByteArray("Add"), 4));
            }

            extendedBlockStorage.setBlockMetadataArray(new NibbleArray(compoundTagAt.getByteArray("Data"), 4));
            extendedBlockStorage.setBlocklightArray(new NibbleArray(compoundTagAt.getByteArray("BlockLight"), 4));
            if (flag) {
                extendedBlockStorage.setSkylightArray(new NibbleArray(compoundTagAt.getByteArray("SkyLight"), 4));
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
                Entity createEntityFromNBT = EntityList.createEntityFromNBT(compoundTagAt, world);
                chunk.hasEntities = true;
                if (createEntityFromNBT != null) {
                    chunk.addEntity(createEntityFromNBT);
                    Entity entity = createEntityFromNBT;

                    for (NBTTagCompound nbttagCompound2 = compoundTagAt; nbttagCompound2.hasKey("Riding", 10); nbttagCompound2 = nbttagCompound2.getCompoundTag("Riding")) {
                        Entity createEntityFromNBT2 = EntityList.createEntityFromNBT(nbttagCompound2.getCompoundTag("Riding"), world);
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
                    world.process(
                        compoundTagAt.getInteger("x"),
                        compoundTagAt.getInteger("y"),
                        compoundTagAt.getInteger("z"),
                        Block.getBlockById(compoundTagAt.getInteger("i")),
                        compoundTagAt.getInteger("t"),
                        compoundTagAt.getInteger("p")
                    );
                }
            }
        }

        return chunk;
    }

    public Chunk createChunkCoordIntPair(World world, int n, int n2) throws IOException {
        NBTTagCompound nbttagCompound = null;
        ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(n, n2);
        DataInputStream dataInputStream = null;
        synchronized (this.syncLockObject) {
            if (this.pendingAnvilChunksCoordinates.contains(chunkCoordIntPair)) {
                for (int i = 0; i < this.chunksToRemove.size(); i++) {
                    if (((AnvilChunkLoader.PendingChunk)this.chunksToRemove.get(i)).chunkCoordinate.equals(chunkCoordIntPair)) {
                        nbttagCompound = ((AnvilChunkLoader.PendingChunk)this.chunksToRemove.get(i)).nbtTags;
                        break;
                    }
                }
            }
        }

        if (nbttagCompound == null) {
            dataInputStream = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, n, n2);
            if (dataInputStream == null) {
                return null;
            }

            nbttagCompound = CompressedStreamTools.read(dataInputStream);
        }

        return this.checkedReadChunkFromNBT(world, n, n2, nbttagCompound);
    }

    public Chunk findChunk(World world, int n, int n2) throws InterruptedException, java.util.concurrent.ExecutionException {
        NBTTagCompound nbttagCompound = null;
        ChunkCoordIntPair chunkCoordIntPair = new ChunkCoordIntPair(n, n2);
        synchronized (this.syncLockObject) {
            if (this.pendingAnvilChunksCoordinates.contains(chunkCoordIntPair)) {
                for (Object obj : this.chunksToRemove) {
                    if (((AnvilChunkLoader.PendingChunk)obj).chunkCoordinate.equals(chunkCoordIntPair)) {
                        nbttagCompound = ((AnvilChunkLoader.PendingChunk)obj).nbtTags;
                        break;
                    }
                }
            }
        }

        AtomicReference atomicReference = new AtomicReference<>(nbttagCompound);
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

    public static class PendingChunk {
        public ChunkCoordIntPair chunkCoordinate;
        public NBTTagCompound nbtTags;

        public PendingChunk(ChunkCoordIntPair chunkCoordIntPair, NBTTagCompound nbttagCompound) {
            this.chunkCoordinate = chunkCoordIntPair;
            this.nbtTags = nbttagCompound;
        }
    }
}
