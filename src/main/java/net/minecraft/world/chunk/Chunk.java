package net.minecraft.world.chunk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chunk {
    public static Logger logger = LogManager.getLogger();
    public static boolean isLit;
    public ExtendedBlockStorage[] storageArrays = new ExtendedBlockStorage[16];
    public byte[] blockBiomeArray = new byte[256];
    public int[] precipitationHeightMap = new int[256];
    public boolean[] updateSkylightColumns = new boolean[256];
    public boolean isChunkLoaded;
    public Map chunkTileEntityMap = new HashMap();
    public int queuedLightChecks = 4096;
    public List[] entityLists = new List[16];
    public World worldObj;
    public boolean isGapLightingUpdated;
    public int xPosition;
    public int zPosition;
    public boolean isTerrainPopulated;
    public boolean isLightPopulated;
    public boolean blockEntitiesPopulated;
    public boolean isModified;
    public boolean hasEntities;
    public long lastSaveTime;
    public boolean sendUpdates;
    public int heightMapMinimum;
    public long inhabitedTime;
    public int[] heightMap;

    public Chunk(World world, int n, int n2) {
        this.worldObj = world;
        this.xPosition = n;
        this.zPosition = n2;
        this.heightMap = new int[256];

        for (int i = 0; i < this.entityLists.length; i++) {
            this.entityLists[i] = new ArrayList();
        }

        Arrays.fill(this.precipitationHeightMap, -999);
        Arrays.fill(this.blockBiomeArray, (byte)-1);
    }

    public Chunk(World world, Block[] blocks, int n, int n2) {
        this(world, n, n2);
        int n3 = blocks.length / 256;
        boolean flag = !world.provider.hasNoSky;

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < n3; k++) {
                    Block block = blocks[i << 11 | j << 7 | k];
                    if (block != null && block.getMaterial() != Material.air) {
                        int n4 = k >> 4;
                        if (this.storageArrays[n4] == null) {
                            this.storageArrays[n4] = new ExtendedBlockStorage(n4 << 4, flag);
                        }

                        this.storageArrays[n4].setExtBlockID(i, k & 15, j, block);
                    }
                }
            }
        }
    }

    public Chunk(World world, Block[] blocks, byte[] bytes, int n, int n2) {
        this(world, n, n2);
        int n3 = blocks.length / 256;
        boolean flag = !world.provider.hasNoSky;

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < n3; k++) {
                    int n4 = i * n3 * 16 | j * n3 | k;
                    Block block = blocks[n4];
                    if (block != null && block != Blocks.air) {
                        int n5 = k >> 4;
                        if (this.storageArrays[n5] == null) {
                            this.storageArrays[n5] = new ExtendedBlockStorage(n5 << 4, flag);
                        }

                        this.storageArrays[n5].setExtBlockID(i, k & 15, j, block);
                        this.storageArrays[n5].setExtBlockMetadata(i, k & 15, j, bytes[n4]);
                    }
                }
            }
        }
    }

    public boolean isAtLocation(int x, int z) {
        return x == this.xPosition && z == this.zPosition;
    }

    public int getHeightValue(int x, int z) {
        return this.heightMap[z << 4 | x];
    }

    public int getTopFilledSegment() {
        for (int i = this.storageArrays.length - 1; i >= 0; i--) {
            if (this.storageArrays[i] != null) {
                return this.storageArrays[i].getYLocation();
            }
        }

        return 0;
    }

    public ExtendedBlockStorage[] getBlockStorageArray() {
        return this.storageArrays;
    }

    public void generateHeightMap() {
        int topFilledSegment = this.getTopFilledSegment();
        this.heightMapMinimum = Integer.MAX_VALUE;

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                this.precipitationHeightMap[i + (j << 4)] = -999;

                for (int k = topFilledSegment + 16 - 1; k > 0; k--) {
                    Block block = this.getBlock(i, k - 1, j);
                    if (block.getLightOpacity() != 0) {
                        this.heightMap[j << 4 | i] = k;
                        if (k < this.heightMapMinimum) {
                            this.heightMapMinimum = k;
                        }
                        break;
                    }
                }
            }
        }

        this.isModified = true;
    }

    public void generateSkylightMap() {
        int topFilledSegment = this.getTopFilledSegment();
        this.heightMapMinimum = Integer.MAX_VALUE;

        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                this.precipitationHeightMap[i + (j << 4)] = -999;

                for (int k = topFilledSegment + 16 - 1; k > 0; k--) {
                    if (this.getBlockLightOpacity(i, k - 1, j) != 0) {
                        this.heightMap[j << 4 | i] = k;
                        if (k < this.heightMapMinimum) {
                            this.heightMapMinimum = k;
                        }
                        break;
                    }
                }

                if (!this.worldObj.provider.hasNoSky) {
                    int n = 15;
                    int n2 = topFilledSegment + 16 - 1;

                    while (true) {
                        int blockLightOpacity = this.getBlockLightOpacity(i, n2, j);
                        if (blockLightOpacity == 0 && n != 15) {
                            blockLightOpacity = 1;
                        }

                        n -= blockLightOpacity;
                        if (n > 0) {
                            ExtendedBlockStorage extendedBlockStorage = this.storageArrays[n2 >> 4];
                            if (extendedBlockStorage != null) {
                                extendedBlockStorage.setExtSkylightValue(i, n2 & 15, j, n);
                                this.worldObj.markBlockForRenderUpdate((this.xPosition << 4) + i, n2, (this.zPosition << 4) + j);
                            }
                        }

                        if (--n2 <= 0 || n <= 0) {
                            break;
                        }
                    }
                }
            }
        }

        this.isModified = true;
    }

    public void propagateSkylightOcclusion(int n, int n2) {
        this.updateSkylightColumns[n + n2 * 16] = true;
        this.isGapLightingUpdated = true;
    }

    public void recheckGaps(boolean flag) {
        this.worldObj.theProfiler.startSection("recheckGaps");
        if (this.worldObj.doChunksNearChunkExist(this.xPosition * 16 + 8, 0, this.zPosition * 16 + 8, 16)) {
            for (int i = 0; i < 16; i++) {
                for (int j = 0; j < 16; j++) {
                    if (this.updateSkylightColumns[i + j * 16]) {
                        this.updateSkylightColumns[i + j * 16] = false;
                        int heightValue = this.getHeightValue(i, j);
                        int n = this.xPosition * 16 + i;
                        int n2 = this.zPosition * 16 + j;
                        int chunkHeightMapMinimum = this.worldObj.getChunkHeightMapMinimum(n - 1, n2);
                        int chunkHeightMapMinimum2 = this.worldObj.getChunkHeightMapMinimum(n + 1, n2);
                        int chunkHeightMapMinimum3 = this.worldObj.getChunkHeightMapMinimum(n, n2 - 1);
                        int chunkHeightMapMinimum4 = this.worldObj.getChunkHeightMapMinimum(n, n2 + 1);
                        if (chunkHeightMapMinimum2 < chunkHeightMapMinimum) {
                            chunkHeightMapMinimum = chunkHeightMapMinimum2;
                        }

                        if (chunkHeightMapMinimum3 < chunkHeightMapMinimum) {
                            chunkHeightMapMinimum = chunkHeightMapMinimum3;
                        }

                        if (chunkHeightMapMinimum4 < chunkHeightMapMinimum) {
                            chunkHeightMapMinimum = chunkHeightMapMinimum4;
                        }

                        this.checkSkylightNeighborHeight(n, n2, chunkHeightMapMinimum);
                        this.checkSkylightNeighborHeight(n - 1, n2, heightValue);
                        this.checkSkylightNeighborHeight(n + 1, n2, heightValue);
                        this.checkSkylightNeighborHeight(n, n2 - 1, heightValue);
                        this.checkSkylightNeighborHeight(n, n2 + 1, heightValue);
                        if (flag) {
                            this.worldObj.theProfiler.endSection();
                            return;
                        }
                    }
                }
            }

            this.isGapLightingUpdated = false;
        }

        this.worldObj.theProfiler.endSection();
    }

    public void checkSkylightNeighborHeight(int n, int n2, int n3) {
        int heightValue = this.worldObj.getHeightValue(n, n2);
        if (heightValue > n3) {
            this.updateSkylightNeighborHeight(n, n2, n3, heightValue + 1);
        } else if (heightValue < n3) {
            this.updateSkylightNeighborHeight(n, n2, heightValue, n3 + 1);
        }
    }

    public void updateSkylightNeighborHeight(int n, int n2, int n3, int n4) {
        if (n4 > n3 && this.worldObj.doChunksNearChunkExist(n, 0, n2, 16)) {
            for (int i = n3; i < n4; i++) {
                this.worldObj.updateLightByType(EnumSkyBlock.Sky, n, i, n2);
            }

            this.isModified = true;
        }
    }

    public void relightBlock(int n, int n2, int n3) {
        int n4 = this.heightMap[n3 << 4 | n] & 0xFF;
        int n5 = n4;
        if (n2 > n4) {
            n5 = n2;
        }

        while (n5 > 0 && this.getBlockLightOpacity(n, n5 - 1, n3) == 0) {
            n5--;
        }

        if (n5 != n4) {
            this.worldObj.markBlocksDirtyVertical(n + this.xPosition * 16, n3 + this.zPosition * 16, n5, n4);
            this.heightMap[n3 << 4 | n] = n5;
            int n6 = this.xPosition * 16 + n;
            int n7 = this.zPosition * 16 + n3;
            if (!this.worldObj.provider.hasNoSky) {
                if (n5 < n4) {
                    for (int i = n5; i < n4; i++) {
                        ExtendedBlockStorage extendedBlockStorage = this.storageArrays[i >> 4];
                        if (extendedBlockStorage != null) {
                            extendedBlockStorage.setExtSkylightValue(n, i & 15, n3, 15);
                            this.worldObj.markBlockForRenderUpdate((this.xPosition << 4) + n, i, (this.zPosition << 4) + n3);
                        }
                    }
                } else {
                    for (int i = n4; i < n5; i++) {
                        ExtendedBlockStorage extendedBlockStorage = this.storageArrays[i >> 4];
                        if (extendedBlockStorage != null) {
                            extendedBlockStorage.setExtSkylightValue(n, i & 15, n3, 0);
                            this.worldObj.markBlockForRenderUpdate((this.xPosition << 4) + n, i, (this.zPosition << 4) + n3);
                        }
                    }
                }

                int n8 = 15;

                while (n5 > 0 && n8 > 0) {
                    int blockLightOpacity = this.getBlockLightOpacity(n, --n5, n3);
                    if (blockLightOpacity == 0) {
                        blockLightOpacity = 1;
                    }

                    n8 -= blockLightOpacity;
                    if (n8 < 0) {
                        n8 = 0;
                    }

                    ExtendedBlockStorage extendedBlockStorage = this.storageArrays[n5 >> 4];
                    if (extendedBlockStorage != null) {
                        extendedBlockStorage.setExtSkylightValue(n, n5 & 15, n3, n8);
                    }
                }
            }

            int n8 = this.heightMap[n3 << 4 | n];
            int n9 = n4;
            int n10 = n8;
            if (n8 < n4) {
                n9 = n8;
                n10 = n4;
            }

            if (n8 < this.heightMapMinimum) {
                this.heightMapMinimum = n8;
            }

            if (!this.worldObj.provider.hasNoSky) {
                this.updateSkylightNeighborHeight(n6 - 1, n7, n9, n10);
                this.updateSkylightNeighborHeight(n6 + 1, n7, n9, n10);
                this.updateSkylightNeighborHeight(n6, n7 - 1, n9, n10);
                this.updateSkylightNeighborHeight(n6, n7 + 1, n9, n10);
                this.updateSkylightNeighborHeight(n6, n7, n9, n10);
            }

            this.isModified = true;
        }
    }

    public int getBlockLightOpacity(int n, int n2, int n3) {
        return this.getBlock(n, n2, n3).getLightOpacity();
    }

    public Block getBlock(final int n, final int n2, final int n3) {
        Block air = Blocks.air;
        if (n2 >> 4 < this.storageArrays.length) {
            ExtendedBlockStorage extendedBlockStorage = this.storageArrays[n2 >> 4];
            if (extendedBlockStorage != null) {
                try {
                    air = extendedBlockStorage.getBlockByExtId(n, n2 & 15, n3);
                } catch (Throwable e) {
                    CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Getting block");
                    CrashReportCategory makeCategory = makeCrashReport.makeCategory("Block being got");
                    makeCategory.addCrashSectionCallable("Location", new Callable() {
                        public String call() {
                            return CrashReportCategory.getLocationInfo(n, n2, n3);
                        }
                    });
                    throw new ReportedException(makeCrashReport);
                }
            }
        }

        return air;
    }

    public int getBlockMetadata(int n, int n2, int n3) {
        if (n2 >> 4 >= this.storageArrays.length) {
            return 0;
        }

        ExtendedBlockStorage extendedBlockStorage = this.storageArrays[n2 >> 4];
        return extendedBlockStorage != null ? extendedBlockStorage.getExtBlockMetadata(n, n2 & 15, n3) : 0;
    }

    public boolean setBlockIDWithMetadata(int n, int n2, int n3, Block block, int n4) {
        int n5 = n3 << 4 | n;
        if (n2 >= this.precipitationHeightMap[n5] - 1) {
            this.precipitationHeightMap[n5] = -999;
        }

        int n6 = this.heightMap[n5];
        Block block2 = this.getBlock(n, n2, n3);
        int blockMetadata = this.getBlockMetadata(n, n2, n3);
        if (block2 == block && blockMetadata == n4) {
            return false;
        }

        ExtendedBlockStorage extendedBlockStorage = this.storageArrays[n2 >> 4];
        boolean flag = false;
        if (extendedBlockStorage == null) {
            if (block == Blocks.air) {
                return false;
            }

            extendedBlockStorage = this.storageArrays[n2 >> 4] = new ExtendedBlockStorage(n2 >> 4 << 4, !this.worldObj.provider.hasNoSky);
            flag = n2 >= n6;
        }

        int n7 = this.xPosition * 16 + n;
        int n8 = this.zPosition * 16 + n3;
        if (!this.worldObj.isRemote) {
            block2.onBlockPreDestroy(this.worldObj, n7, n2, n8, blockMetadata);
        }

        extendedBlockStorage.setExtBlockID(n, n2 & 15, n3, block);
        if (!this.worldObj.isRemote) {
            block2.breakBlock(this.worldObj, n7, n2, n8, block2, blockMetadata);
        } else if (block2 instanceof ITileEntityProvider && block2 != block) {
            this.worldObj.removeTileEntity(n7, n2, n8);
        }

        if (extendedBlockStorage.getBlockByExtId(n, n2 & 15, n3) != block) {
            return false;
        }

        extendedBlockStorage.setExtBlockMetadata(n, n2 & 15, n3, n4);
        if (flag) {
            this.generateSkylightMap();
        } else {
            int lightOpacity = block.getLightOpacity();
            int lightOpacity2 = block2.getLightOpacity();
            if (lightOpacity > 0) {
                if (n2 >= n6) {
                    this.relightBlock(n, n2 + 1, n3);
                }
            } else if (n2 == n6 - 1) {
                this.relightBlock(n, n2, n3);
            }

            if (lightOpacity != lightOpacity2
                && (
                    lightOpacity < lightOpacity2
                        || this.getSavedLightValue(EnumSkyBlock.Sky, n, n2, n3) > 0
                        || this.getSavedLightValue(EnumSkyBlock.Block, n, n2, n3) > 0
                )) {
                this.propagateSkylightOcclusion(n, n3);
            }
        }

        if (block2 instanceof ITileEntityProvider) {
            TileEntity blockTileEntityInChunk = this.getBlockTileEntityInChunk(n, n2, n3);
            if (blockTileEntityInChunk != null) {
                blockTileEntityInChunk.updateContainingBlockInfo();
            }
        }

        if (!this.worldObj.isRemote) {
            block.onBlockAdded(this.worldObj, n7, n2, n8);
        }

        if (block instanceof ITileEntityProvider) {
            TileEntity blockTileEntityInChunk = this.getBlockTileEntityInChunk(n, n2, n3);
            if (blockTileEntityInChunk == null) {
                blockTileEntityInChunk = ((ITileEntityProvider)block).createNewTileEntity(this.worldObj, n4);
                this.worldObj.setTileEntity(n7, n2, n8, blockTileEntityInChunk);
            }

            if (blockTileEntityInChunk != null) {
                blockTileEntityInChunk.updateContainingBlockInfo();
            }
        }

        this.isModified = true;
        return true;
    }

    public boolean setBlockMetadata(int n, int n2, int n3, int n4) {
        ExtendedBlockStorage extendedBlockStorage = this.storageArrays[n2 >> 4];
        if (extendedBlockStorage == null) {
            return false;
        }

        int extBlockMetadata = extendedBlockStorage.getExtBlockMetadata(n, n2 & 15, n3);
        if (extBlockMetadata == n4) {
            return false;
        }

        this.isModified = true;
        extendedBlockStorage.setExtBlockMetadata(n, n2 & 15, n3, n4);
        if (extendedBlockStorage.getBlockByExtId(n, n2 & 15, n3) instanceof ITileEntityProvider) {
            TileEntity blockTileEntityInChunk = this.getBlockTileEntityInChunk(n, n2, n3);
            if (blockTileEntityInChunk != null) {
                blockTileEntityInChunk.updateContainingBlockInfo();
                blockTileEntityInChunk.blockMetadata = n4;
            }
        }

        return true;
    }

    public int getSavedLightValue(EnumSkyBlock enumSkyBlock, int n, int n2, int n3) {
        ExtendedBlockStorage extendedBlockStorage = this.storageArrays[n2 >> 4];
        return extendedBlockStorage == null
            ? (this.canBlockSeeTheSky(n, n2, n3) ? enumSkyBlock.defaultLightValue : 0)
            : (
                enumSkyBlock == EnumSkyBlock.Sky
                    ? (this.worldObj.provider.hasNoSky ? 0 : extendedBlockStorage.getExtSkylightValue(n, n2 & 15, n3))
                    : (enumSkyBlock == EnumSkyBlock.Block ? extendedBlockStorage.getExtBlocklightValue(n, n2 & 15, n3) : enumSkyBlock.defaultLightValue)
            );
    }

    public void setLightValue(EnumSkyBlock enumSkyBlock, int n, int n2, int n3, int n4) {
        ExtendedBlockStorage extendedBlockStorage = this.storageArrays[n2 >> 4];
        if (extendedBlockStorage == null) {
            extendedBlockStorage = this.storageArrays[n2 >> 4] = new ExtendedBlockStorage(n2 >> 4 << 4, !this.worldObj.provider.hasNoSky);
            this.generateSkylightMap();
        }

        this.isModified = true;
        if (enumSkyBlock == EnumSkyBlock.Sky) {
            if (!this.worldObj.provider.hasNoSky) {
                extendedBlockStorage.setExtSkylightValue(n, n2 & 15, n3, n4);
            }
        } else if (enumSkyBlock == EnumSkyBlock.Block) {
            extendedBlockStorage.setExtBlocklightValue(n, n2 & 15, n3, n4);
        }
    }

    public int getBlockLightValue(int n, int n2, int n3, int n4) {
        ExtendedBlockStorage extendedBlockStorage = this.storageArrays[n2 >> 4];
        if (extendedBlockStorage != null) {
            int n5 = this.worldObj.provider.hasNoSky ? 0 : extendedBlockStorage.getExtSkylightValue(n, n2 & 15, n3);
            if (n5 > 0) {
                isLit = true;
            }

            n5 -= n4;
            int extBlocklightValue = extendedBlockStorage.getExtBlocklightValue(n, n2 & 15, n3);
            if (extBlocklightValue > n5) {
                n5 = extBlocklightValue;
            }

            return n5;
        } else {
            return !this.worldObj.provider.hasNoSky && n4 < EnumSkyBlock.Sky.defaultLightValue ? EnumSkyBlock.Sky.defaultLightValue - n4 : 0;
        }
    }

    public void addEntity(Entity entity) {
        this.hasEntities = true;
        int n = MathHelper.floor_double(entity.posX / 16.0);
        int n2 = MathHelper.floor_double(entity.posZ / 16.0);
        if (n != this.xPosition || n2 != this.zPosition) {
            logger.warn("Wrong location! " + entity + " (at " + n + ", " + n2 + " instead of " + this.xPosition + ", " + this.zPosition + ")");
            Thread.dumpStack();
        }

        int n3 = MathHelper.floor_double(entity.posY / 16.0);
        if (n3 < 0) {
            n3 = 0;
        }

        if (n3 >= this.entityLists.length) {
            n3 = this.entityLists.length - 1;
        }

        entity.addedToChunk = true;
        entity.chunkCoordX = this.xPosition;
        entity.chunkCoordY = n3;
        entity.chunkCoordZ = this.zPosition;
        this.entityLists[n3].add(entity);
    }

    public void removeEntity(Entity entity) {
        this.removeEntityAtIndex(entity, entity.chunkCoordY);
    }

    public void removeEntityAtIndex(Entity entity, int n) {
        if (n < 0) {
            n = 0;
        }

        if (n >= this.entityLists.length) {
            n = this.entityLists.length - 1;
        }

        this.entityLists[n].remove(entity);
    }

    public boolean canBlockSeeTheSky(int n, int n2, int n3) {
        return n2 >= this.heightMap[n3 << 4 | n];
    }

    public TileEntity getBlockTileEntityInChunk(int n, int n2, int n3) {
        ChunkPosition chunkPosition = new ChunkPosition(n, n2, n3);
        TileEntity tileEntity = (TileEntity)this.chunkTileEntityMap.get(chunkPosition);
        if (tileEntity == null) {
            Block block = this.getBlock(n, n2, n3);
            if (!block.hasTileEntity()) {
                return null;
            }

            tileEntity = ((ITileEntityProvider)block).createNewTileEntity(this.worldObj, this.getBlockMetadata(n, n2, n3));
            this.worldObj.setTileEntity(this.xPosition * 16 + n, n2, this.zPosition * 16 + n3, tileEntity);
        }

        if (tileEntity != null && tileEntity.isInvalid()) {
            this.chunkTileEntityMap.remove(chunkPosition);
            return null;
        } else {
            return tileEntity;
        }
    }

    public void addTileEntity(TileEntity tileEntity) {
        int n = tileEntity.xCoord - this.xPosition * 16;
        int yCoord = tileEntity.yCoord;
        int n2 = tileEntity.zCoord - this.zPosition * 16;
        this.setBlockTileEntityInChunk(n, yCoord, n2, tileEntity);
        if (this.isChunkLoaded) {
            this.worldObj.loadedTileEntityList.add(tileEntity);
        }
    }

    public void setBlockTileEntityInChunk(int n, int n2, int n3, TileEntity tileEntity) {
        ChunkPosition chunkPosition = new ChunkPosition(n, n2, n3);
        tileEntity.setWorldObj(this.worldObj);
        tileEntity.xCoord = this.xPosition * 16 + n;
        tileEntity.yCoord = n2;
        tileEntity.zCoord = this.zPosition * 16 + n3;
        if (this.getBlock(n, n2, n3) instanceof ITileEntityProvider) {
            if (this.chunkTileEntityMap.containsKey(chunkPosition)) {
                ((TileEntity)this.chunkTileEntityMap.get(chunkPosition)).invalidate();
            }

            tileEntity.validate();
            this.chunkTileEntityMap.put(chunkPosition, tileEntity);
        }
    }

    public void removeTileEntity(int n, int n2, int n3) {
        ChunkPosition chunkPosition = new ChunkPosition(n, n2, n3);
        if (this.isChunkLoaded) {
            TileEntity tileEntity = (TileEntity)this.chunkTileEntityMap.remove(chunkPosition);
            if (tileEntity != null) {
                tileEntity.invalidate();
            }
        }
    }

    public void onChunkLoad() {
        this.isChunkLoaded = true;
        this.worldObj.addBlockEntities(this.chunkTileEntityMap.values());

        for (int i = 0; i < this.entityLists.length; i++) {
            for (Entity entity : (Iterable<Entity>)this.entityLists[i]) {
                entity.onChunkLoad();
            }

            this.worldObj.addLoadedEntities(this.entityLists[i]);
        }
    }

    public void onChunkUnload() {
        this.isChunkLoaded = false;

        for (TileEntity tileEntity : (Iterable<TileEntity>)this.chunkTileEntityMap.values()) {
            this.worldObj.markTileEntityForRemoval(tileEntity);
        }

        for (int i = 0; i < this.entityLists.length; i++) {
            this.worldObj.unloadEntities(this.entityLists[i]);
        }
    }

    public void setChunkModified() {
        this.isModified = true;
    }

    public void getEntitiesWithinAABBForEntity(Entity entity, AxisAlignedBB axisAlignedBB, List list, IEntitySelector entitySelector) {
        int n = MathHelper.floor_double((axisAlignedBB.minY - 2.0) / 16.0);
        int n2 = MathHelper.floor_double((axisAlignedBB.maxY + 2.0) / 16.0);
        n = MathHelper.clamp_int(n, 0, this.entityLists.length - 1);
        n2 = MathHelper.clamp_int(n2, 0, this.entityLists.length - 1);

        for (int i = n; i <= n2; i++) {
            List list2 = this.entityLists[i];

            for (int j = 0; j < list2.size(); j++) {
                Entity entity2 = (Entity)list2.get(j);
                if (entity2 != entity && entity2.boundingBox.intersectsWith(axisAlignedBB) && (entitySelector == null || entitySelector.isEntityApplicable(entity2))) {
                    list.add(entity2);
                    Entity[] parts = entity2.getParts();
                    if (parts != null) {
                        for (int k = 0; k < parts.length; k++) {
                            entity2 = parts[k];
                            if (entity2 != entity && entity2.boundingBox.intersectsWith(axisAlignedBB) && (entitySelector == null || entitySelector.isEntityApplicable(entity2))) {
                                list.add(entity2);
                            }
                        }
                    }
                }
            }
        }
    }

    public void getEntitiesOfTypeWithinAAAB(Class clazz, AxisAlignedBB axisAlignedBB, List list, IEntitySelector entitySelector) {
        int n = MathHelper.floor_double((axisAlignedBB.minY - 2.0) / 16.0);
        int n2 = MathHelper.floor_double((axisAlignedBB.maxY + 2.0) / 16.0);
        n = MathHelper.clamp_int(n, 0, this.entityLists.length - 1);
        n2 = MathHelper.clamp_int(n2, 0, this.entityLists.length - 1);

        for (int i = n; i <= n2; i++) {
            List list2 = this.entityLists[i];

            for (int j = 0; j < list2.size(); j++) {
                Entity entity = (Entity)list2.get(j);
                if (clazz.isAssignableFrom(entity.getClass()) && entity.boundingBox.intersectsWith(axisAlignedBB) && (entitySelector == null || entitySelector.isEntityApplicable(entity))) {
                    list.add(entity);
                }
            }
        }
    }

    public boolean needsSaving(boolean flag) {
        if (flag) {
            if (this.hasEntities && this.worldObj.getTotalWorldTime() != this.lastSaveTime || this.isModified) {
                return true;
            }
        } else if (this.hasEntities && this.worldObj.getTotalWorldTime() >= this.lastSaveTime + 600L) {
            return true;
        }

        return this.isModified;
    }

    public Random getRandomWithSeed(long l) {
        return new Random(
            this.worldObj.getSeed()
                    + this.xPosition * this.xPosition * 4987142
                    + this.xPosition * 5947611
                    + this.zPosition * this.zPosition * 4392871L
                    + this.zPosition * 389711
                ^ l
        );
    }

    public boolean isEmpty() {
        return false;
    }

    public void populateChunk(IChunkProvider chunkProvider, IChunkProvider chunkProvider2, int n, int n2) {
        if (!this.isTerrainPopulated && chunkProvider.chunkExists(n + 1, n2 + 1) && chunkProvider.chunkExists(n, n2 + 1) && chunkProvider.chunkExists(n + 1, n2)) {
            chunkProvider.populate(chunkProvider2, n, n2);
        }

        if (chunkProvider.chunkExists(n - 1, n2)
            && !chunkProvider.provideChunk(n - 1, n2).isTerrainPopulated
            && chunkProvider.chunkExists(n - 1, n2 + 1)
            && chunkProvider.chunkExists(n, n2 + 1)
            && chunkProvider.chunkExists(n - 1, n2 + 1)) {
            chunkProvider.populate(chunkProvider2, n - 1, n2);
        }

        if (chunkProvider.chunkExists(n, n2 - 1)
            && !chunkProvider.provideChunk(n, n2 - 1).isTerrainPopulated
            && chunkProvider.chunkExists(n + 1, n2 - 1)
            && chunkProvider.chunkExists(n + 1, n2 - 1)
            && chunkProvider.chunkExists(n + 1, n2)) {
            chunkProvider.populate(chunkProvider2, n, n2 - 1);
        }

        if (chunkProvider.chunkExists(n - 1, n2 - 1)
            && !chunkProvider.provideChunk(n - 1, n2 - 1).isTerrainPopulated
            && chunkProvider.chunkExists(n, n2 - 1)
            && chunkProvider.chunkExists(n - 1, n2)) {
            chunkProvider.populate(chunkProvider2, n - 1, n2 - 1);
        }
    }

    public int getPrecipitationHeight(int n, int n2) {
        int n3 = n | n2 << 4;
        int n4 = this.precipitationHeightMap[n3];
        if (n4 == -999) {
            int n5 = this.getTopFilledSegment() + 15;
            n4 = -1;

            while (n5 > 0 && n4 == -1) {
                Block block = this.getBlock(n, n5, n2);
                Material material = block.getMaterial();
                if (!material.blocksMovement() && !material.isLiquid()) {
                    n5--;
                } else {
                    n4 = n5 + 1;
                }
            }

            this.precipitationHeightMap[n3] = n4;
        }

        return n4;
    }

    public void populateBlockEntities(boolean flag) {
        if (this.isGapLightingUpdated && !this.worldObj.provider.hasNoSky && !flag) {
            this.recheckGaps(this.worldObj.isRemote);
        }

        this.blockEntitiesPopulated = true;
        if (!this.isLightPopulated && this.isTerrainPopulated) {
            this.populate();
        }
    }

    public boolean isPopulated() {
        return this.blockEntitiesPopulated && this.isTerrainPopulated && this.isLightPopulated;
    }

    public ChunkCoordIntPair getChunkCoordIntPair() {
        return new ChunkCoordIntPair(this.xPosition, this.zPosition);
    }

    public boolean getAreLevelsEmpty(int n, int n2) {
        if (n < 0) {
            n = 0;
        }

        if (n2 >= 256) {
            n2 = 255;
        }

        for (int i = n; i <= n2; i += 16) {
            ExtendedBlockStorage extendedBlockStorage = this.storageArrays[i >> 4];
            if (extendedBlockStorage != null && !extendedBlockStorage.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public void setStorageArrays(ExtendedBlockStorage[] extendedBlockStorages) {
        this.storageArrays = extendedBlockStorages;
    }

    public void fillChunk(byte[] bytes, int n, int n2, boolean flag) {
        int n3 = 0;
        boolean flag2 = !this.worldObj.provider.hasNoSky;

        for (int i = 0; i < this.storageArrays.length; i++) {
            if ((n & 1 << i) != 0) {
                if (this.storageArrays[i] == null) {
                    this.storageArrays[i] = new ExtendedBlockStorage(i << 4, flag2);
                }

                byte[] blockLSBArray = this.storageArrays[i].getBlockLSBArray();
                System.arraycopy(bytes, n3, blockLSBArray, 0, blockLSBArray.length);
                n3 += blockLSBArray.length;
            } else if (flag && this.storageArrays[i] != null) {
                this.storageArrays[i] = null;
            }
        }

        for (int i = 0; i < this.storageArrays.length; i++) {
            if ((n & 1 << i) != 0 && this.storageArrays[i] != null) {
                NibbleArray metadataArray = this.storageArrays[i].getMetadataArray();
                System.arraycopy(bytes, n3, metadataArray.data, 0, metadataArray.data.length);
                n3 += metadataArray.data.length;
            }
        }

        for (int i = 0; i < this.storageArrays.length; i++) {
            if ((n & 1 << i) != 0 && this.storageArrays[i] != null) {
                NibbleArray blocklightArray = this.storageArrays[i].getBlocklightArray();
                System.arraycopy(bytes, n3, blocklightArray.data, 0, blocklightArray.data.length);
                n3 += blocklightArray.data.length;
            }
        }

        if (flag2) {
            for (int i = 0; i < this.storageArrays.length; i++) {
                if ((n & 1 << i) != 0 && this.storageArrays[i] != null) {
                    NibbleArray skylightArray = this.storageArrays[i].getSkylightArray();
                    System.arraycopy(bytes, n3, skylightArray.data, 0, skylightArray.data.length);
                    n3 += skylightArray.data.length;
                }
            }
        }

        for (int i = 0; i < this.storageArrays.length; i++) {
            if ((n2 & 1 << i) != 0) {
                if (this.storageArrays[i] == null) {
                    n3 += 2048;
                } else {
                    NibbleArray blockMSBArray = this.storageArrays[i].getBlockMSBArray();
                    if (blockMSBArray == null) {
                        blockMSBArray = this.storageArrays[i].createBlockMSBArray();
                    }

                    System.arraycopy(bytes, n3, blockMSBArray.data, 0, blockMSBArray.data.length);
                    n3 += blockMSBArray.data.length;
                }
            } else if (flag && this.storageArrays[i] != null && this.storageArrays[i].getBlockMSBArray() != null) {
                this.storageArrays[i].clearMSBArray();
            }
        }

        if (flag) {
            System.arraycopy(bytes, n3, this.blockBiomeArray, 0, this.blockBiomeArray.length);
            int n4 = n3 + this.blockBiomeArray.length;
        }

        for (int i = 0; i < this.storageArrays.length; i++) {
            if (this.storageArrays[i] != null && (n & 1 << i) != 0) {
                this.storageArrays[i].removeInvalidBlocks();
            }
        }

        this.isLightPopulated = true;
        this.isTerrainPopulated = true;
        this.generateHeightMap();

        for (TileEntity tileEntity : (Iterable<TileEntity>)this.chunkTileEntityMap.values()) {
            tileEntity.updateContainingBlockInfo();
        }
    }

    public BiomeGenBase getBiomeGenForWorldCoords(int n, int n2, WorldChunkManager worldChunkManager) {
        int n3 = this.blockBiomeArray[n2 << 4 | n] & 255;
        if (n3 == 255) {
            BiomeGenBase biomeGenAt = worldChunkManager.getBiomeGenAt((this.xPosition << 4) + n, (this.zPosition << 4) + n2);
            n3 = biomeGenAt.biomeID;
            this.blockBiomeArray[n2 << 4 | n] = (byte)(n3 & 0xFF);
        }

        return BiomeGenBase.getBiome(n3) == null ? BiomeGenBase.plains : BiomeGenBase.getBiome(n3);
    }

    public byte[] getBiomeArray() {
        return this.blockBiomeArray;
    }

    public void setBiomeArray(byte[] bytes) {
        this.blockBiomeArray = bytes;
    }

    public void resetRelightChecks() {
        this.queuedLightChecks = 0;
    }

    public void enqueueRelightChecks() {
        for (int i = 0; i < 8; i++) {
            if (this.queuedLightChecks >= 4096) {
                return;
            }

            int n = this.queuedLightChecks % 16;
            int n2 = this.queuedLightChecks / 16 % 16;
            int n3 = this.queuedLightChecks / 256;
            this.queuedLightChecks++;
            int n4 = (this.xPosition << 4) + n2;
            int n5 = (this.zPosition << 4) + n3;

            for (int j = 0; j < 16; j++) {
                int n6 = (n << 4) + j;
                if (this.storageArrays[n] == null && (j == 0 || j == 15 || n2 == 0 || n2 == 15 || n3 == 0 || n3 == 15)
                    || this.storageArrays[n] != null && this.storageArrays[n].getBlockByExtId(n2, j, n3).getMaterial() == Material.air) {
                    if (this.worldObj.getBlock(n4, n6 - 1, n5).getLightValue() > 0) {
                        this.worldObj.updateAllLightTypes(n4, n6 - 1, n5);
                    }

                    if (this.worldObj.getBlock(n4, n6 + 1, n5).getLightValue() > 0) {
                        this.worldObj.updateAllLightTypes(n4, n6 + 1, n5);
                    }

                    if (this.worldObj.getBlock(n4 - 1, n6, n5).getLightValue() > 0) {
                        this.worldObj.updateAllLightTypes(n4 - 1, n6, n5);
                    }

                    if (this.worldObj.getBlock(n4 + 1, n6, n5).getLightValue() > 0) {
                        this.worldObj.updateAllLightTypes(n4 + 1, n6, n5);
                    }

                    if (this.worldObj.getBlock(n4, n6, n5 - 1).getLightValue() > 0) {
                        this.worldObj.updateAllLightTypes(n4, n6, n5 - 1);
                    }

                    if (this.worldObj.getBlock(n4, n6, n5 + 1).getLightValue() > 0) {
                        this.worldObj.updateAllLightTypes(n4, n6, n5 + 1);
                    }

                    this.worldObj.updateAllLightTypes(n4, n6, n5);
                }
            }
        }
    }

    public void populate() {
        this.isTerrainPopulated = true;
        this.isLightPopulated = true;
        if (!this.worldObj.provider.hasNoSky) {
            if (this.worldObj.checkChunksExist(this.xPosition * 16 - 1, 0, this.zPosition * 16 - 1, this.xPosition * 16 + 1, 63, this.zPosition * 16 + 1)) {
                for (int i = 0; i < 16; i++) {
                    for (int j = 0; j < 16; j++) {
                        if (!this.updateLight(i, j)) {
                            this.isLightPopulated = false;
                            break;
                        }
                    }
                }

                if (this.isLightPopulated) {
                    Chunk chunkFromBlockCoords = this.worldObj.getChunkFromBlockCoords(this.xPosition * 16 - 1, this.zPosition * 16);
                    chunkFromBlockCoords.process(3);
                    chunkFromBlockCoords = this.worldObj.getChunkFromBlockCoords(this.xPosition * 16 + 16, this.zPosition * 16);
                    chunkFromBlockCoords.process(1);
                    chunkFromBlockCoords = this.worldObj.getChunkFromBlockCoords(this.xPosition * 16, this.zPosition * 16 - 1);
                    chunkFromBlockCoords.process(0);
                    chunkFromBlockCoords = this.worldObj.getChunkFromBlockCoords(this.xPosition * 16, this.zPosition * 16 + 16);
                    chunkFromBlockCoords.process(2);
                }
            } else {
                this.isLightPopulated = false;
            }
        }
    }

    public void process(int n) {
        if (this.isTerrainPopulated) {
            if (n == 3) {
                for (int i = 0; i < 16; i++) {
                    this.updateLight(15, i);
                }
            } else if (n == 1) {
                for (int i = 0; i < 16; i++) {
                    this.updateLight(0, i);
                }
            } else if (n == 0) {
                for (int i = 0; i < 16; i++) {
                    this.updateLight(i, 15);
                }
            } else if (n == 2) {
                for (int i = 0; i < 16; i++) {
                    this.updateLight(i, 0);
                }
            }
        }
    }

    public boolean updateLight(int n, int n2) {
        int topFilledSegment = this.getTopFilledSegment();
        boolean flag = false;
        boolean flag2 = false;

        int n3;
        for (n3 = topFilledSegment + 16 - 1; n3 > 63 || n3 > 0 && !flag2; n3--) {
            int blockLightOpacity = this.getBlockLightOpacity(n, n3, n2);
            if (blockLightOpacity == 255 && n3 < 63) {
                flag2 = true;
            }

            if (!flag && blockLightOpacity > 0) {
                flag = true;
            } else if (flag && blockLightOpacity == 0 && !this.worldObj.updateAllLightTypes(this.xPosition * 16 + n, n3, this.zPosition * 16 + n2)) {
                return false;
            }
        }

        for (; n3 > 0; n3--) {
            if (this.getBlock(n, n3, n2).getLightValue() > 0) {
                this.worldObj.updateAllLightTypes(this.xPosition * 16 + n, n3, this.zPosition * 16 + n2);
            }
        }

        return true;
    }
}
