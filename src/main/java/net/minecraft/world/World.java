package net.minecraft.world;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.DoubleEvent1;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockHopper;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockSlab;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockStairs;
import net.minecraft.block.material.Material;
import net.minecraft.command.IEntitySelector;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.Direction;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;

public abstract class World implements IBlockAccess {
    public boolean scheduledUpdatesAreImmediate;
    public List loadedEntityList = new ArrayList();
    public List unloadedEntityList = new ArrayList();
    public List loadedTileEntityList = new ArrayList();
    public List addedTileEntityList = new ArrayList();
    public List tileEntitiesToBeRemoved = new ArrayList();
    public List<EntityPlayer> playerEntities = new ArrayList<>();
    public List weatherEffects = new ArrayList();
    public final long cloudColour = 16777215L;
    public int skylightSubtracted;
    public int updateLCG = new Random().nextInt();
    public final int DIST_HASH_MAGIC = 1013904223;
    public float prevRainingStrength;
    public float rainingStrength;
    public float prevThunderingStrength;
    public float thunderingStrength;
    public int lastLightningBolt;
    public EnumDifficulty difficultySetting;
    public Random rand = new Random();
    public List worldAccesses = new ArrayList();
    public VillageSiege villageSiegeObj = new VillageSiege(this);
    public Calendar theCalendar = Calendar.getInstance();
    public Scoreboard worldScoreboard = new Scoreboard();
    public Set activeChunkSet = new HashSet();
    public boolean findingSpawnPoint;
    public int ambientTickCountdown = this.rand.nextInt(12000);
    public boolean spawnHostileMobs = true;
    public boolean spawnPeacefulMobs = true;
    public ArrayList collidingBoundingBoxes = new ArrayList();
    public int[] lightUpdateBlockList = new int[32768];
    public ISaveHandler saveHandler;
    public boolean isRemote;
    public Profiler theProfiler;
    public WorldInfo worldInfo;
    public WorldProvider provider;
    public MapStorage mapStorage;
    public VillageCollection villageCollectionObj;
    public boolean processingLoadedTiles;
    public IChunkProvider chunkProvider;

    @Override
    public BiomeGenBase getBiomeGenForCoords(final int x, final int z) {
        if (this.blockExists(x, 0, z)) {
            Chunk chunkFromBlockCoords = this.getChunkFromBlockCoords(x, z);

            try {
                return chunkFromBlockCoords.getBiomeGenForWorldCoords(x & 15, z & 15, this.provider.worldChunkMgr);
            } catch (Throwable e) {
                CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Getting biome");
                CrashReportCategory makeCategory = makeCrashReport.makeCategory("Coordinates of biome request");
                makeCategory.addCrashSectionCallable("Location", new Callable() {
                    public String call() {
                        return CrashReportCategory.getLocationInfo(x, 0, z);
                    }
                });
                throw new ReportedException(makeCrashReport);
            }
        } else {
            return this.provider.worldChunkMgr.getBiomeGenAt(x, z);
        }
    }

    public WorldChunkManager getWorldChunkManager() {
        return this.provider.worldChunkMgr;
    }

    public World(ISaveHandler saveHandler, String str, WorldProvider worldProvider, WorldSettings worldSettings, Profiler profiler) {
        this.saveHandler = saveHandler;
        this.theProfiler = profiler;
        this.worldInfo = new WorldInfo(worldSettings, str);
        this.provider = worldProvider;
        this.mapStorage = new MapStorage(saveHandler);
        VillageCollection loadData = (VillageCollection)this.mapStorage.loadData(VillageCollection.class, "villages");
        if (loadData == null) {
            this.villageCollectionObj = new VillageCollection(this);
            this.mapStorage.setData("villages", this.villageCollectionObj);
        } else {
            this.villageCollectionObj = loadData;
            this.villageCollectionObj.setWorld(this);
        }

        worldProvider.registerWorld(this);
        this.chunkProvider = this.createChunkProvider();
        this.calculateInitialSkylight();
        this.calculateInitialWeather();
    }

    public World(ISaveHandler saveHandler, String str, WorldSettings worldSettings, WorldProvider worldProvider, Profiler profiler) {
        this.saveHandler = saveHandler;
        this.theProfiler = profiler;
        this.mapStorage = new MapStorage(saveHandler);
        this.worldInfo = saveHandler.loadWorldInfo();
        if (worldProvider != null) {
            this.provider = worldProvider;
        } else if (this.worldInfo != null && this.worldInfo.getDimension() != 0) {
            this.provider = WorldProvider.getProviderForDimension(this.worldInfo.getDimension());
        } else {
            this.provider = WorldProvider.getProviderForDimension(0);
        }

        if (this.worldInfo == null) {
            this.worldInfo = new WorldInfo(worldSettings, str);
        } else {
            this.worldInfo.setWorldName(str);
        }

        this.provider.registerWorld(this);
        this.chunkProvider = this.createChunkProvider();
        if (!this.worldInfo.isInitialized()) {
            try {
                this.initialize(worldSettings);
            } catch (Throwable e) {
                CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Exception initializing level");

                try {
                    this.addWorldInfoToCrashReport(makeCrashReport);
                } catch (Throwable e2) {
                }

                throw new ReportedException(makeCrashReport);
            }

            this.worldInfo.setServerInitialized(true);
        }

        VillageCollection loadData = (VillageCollection)this.mapStorage.loadData(VillageCollection.class, "villages");
        if (loadData == null) {
            this.villageCollectionObj = new VillageCollection(this);
            this.mapStorage.setData("villages", this.villageCollectionObj);
        } else {
            this.villageCollectionObj = loadData;
            this.villageCollectionObj.setWorld(this);
        }

        this.calculateInitialSkylight();
        this.calculateInitialWeather();
    }

    public abstract IChunkProvider createChunkProvider();

    public void initialize(WorldSettings worldSettings) {
        this.worldInfo.setServerInitialized(true);
    }

    public void setSpawnLocation() {
        this.setSpawnLocation(8, 64, 8);
    }

    public Block getTopBlock(int x, int z) {
        int n = 63;

        while (!this.isAirBlock(x, n + 1, z)) {
            n++;
        }

        return this.getBlock(x, n, z);
    }

    @Override
    public Block getBlock(int n, int n2, int n3) {
        if (n >= -30000000 && n3 >= -30000000 && n < 30000000 && n3 < 30000000 && n2 >= 0 && n2 < 256) {
            Chunk chunk = null;

            try {
                chunk = this.getChunkFromChunkCoords(n >> 4, n3 >> 4);
                return chunk.getBlock(n & 15, n2, n3 & 15);
            } catch (Throwable e) {
                CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Exception getting block type in world");
                CrashReportCategory makeCategory = makeCrashReport.makeCategory("Requested block coordinates");
                makeCategory.addCrashSection("Found chunk", chunk == null);
                makeCategory.addCrashSection("Location", CrashReportCategory.getLocationInfo(n, n2, n3));
                throw new ReportedException(makeCrashReport);
            }
        } else {
            return Blocks.air;
        }
    }

    @Override
    public boolean isAirBlock(int x, int y, int z) {
        return this.getBlock(x, y, z).getMaterial() == Material.air;
    }

    public boolean blockExists(int n, int n2, int n3) {
        return n2 >= 0 && n2 < 256 && this.chunkExists(n >> 4, n3 >> 4);
    }

    public boolean doChunksNearChunkExist(int n, int n2, int n3, int n4) {
        return this.checkChunksExist(n - n4, n2 - n4, n3 - n4, n + n4, n2 + n4, n3 + n4);
    }

    public boolean checkChunksExist(int n, int n2, int n3, int n4, int n5, int n6) {
        if (n5 >= 0 && n2 < 256) {
            n >>= 4;
            n3 >>= 4;
            n4 >>= 4;
            n6 >>= 4;

            for (int i = n; i <= n4; i++) {
                for (int j = n3; j <= n6; j++) {
                    if (!this.chunkExists(i, j)) {
                        return false;
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public boolean chunkExists(int n, int n2) {
        return this.chunkProvider.chunkExists(n, n2);
    }

    public Chunk getChunkFromBlockCoords(int n, int n2) {
        return this.getChunkFromChunkCoords(n >> 4, n2 >> 4);
    }

    public Chunk getChunkFromChunkCoords(int n, int n2) {
        return this.chunkProvider.provideChunk(n, n2);
    }

    public boolean setBlock(int x, int y, int z, Block blockIn, int metadataIn, int flags) {
        if (x < -30000000 || z < -30000000 || x >= 30000000 || z >= 30000000) {
            return false;
        }

        if (y < 0) {
            return false;
        }

        if (y >= 256) {
            return false;
        }

        Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(x >> 4, z >> 4);
        Block block = null;
        if ((flags & 1) != 0) {
            block = chunkFromChunkCoords.getBlock(x & 15, y, z & 15);
        }

        boolean setBlockIDWithMetadata = chunkFromChunkCoords.setBlockIDWithMetadata(x & 15, y, z & 15, blockIn, metadataIn);
        this.theProfiler.startSection("checkLight");
        this.updateAllLightTypes(x, y, z);
        this.theProfiler.endSection();
        if (setBlockIDWithMetadata) {
            if ((flags & 2) != 0 && (!this.isRemote || (flags & 4) == 0) && chunkFromChunkCoords.isPopulated()) {
                this.markBlockForUpdate(x, y, z);
            }

            if (!this.isRemote && (flags & 1) != 0) {
                this.notifyBlockChange(x, y, z, block);
                if (blockIn.hasComparatorInputOverride()) {
                    this.updateNeighborsAboutBlockChange(x, y, z, blockIn);
                }
            }
        }

        return setBlockIDWithMetadata;
    }

    @Override
    public int getBlockMetadata(int n, int n2, int n3) {
        if (n < -30000000 || n3 < -30000000 || n >= 30000000 || n3 >= 30000000) {
            return 0;
        }

        if (n2 < 0) {
            return 0;
        }

        if (n2 >= 256) {
            return 0;
        }

        Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(n >> 4, n3 >> 4);
        n &= 15;
        n3 &= 15;
        return chunkFromChunkCoords.getBlockMetadata(n, n2, n3);
    }

    public boolean setBlockMetadataWithNotify(int n, int n2, int n3, int n4, int n5) {
        if (n < -30000000 || n3 < -30000000 || n >= 30000000 || n3 >= 30000000) {
            return false;
        }

        if (n2 < 0) {
            return false;
        }

        if (n2 >= 256) {
            return false;
        }

        Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(n >> 4, n3 >> 4);
        int n6 = n & 15;
        int n7 = n3 & 15;
        boolean setBlockMetadata = chunkFromChunkCoords.setBlockMetadata(n6, n2, n7, n4);
        if (setBlockMetadata) {
            Block block = chunkFromChunkCoords.getBlock(n6, n2, n7);
            if ((n5 & 2) != 0 && (!this.isRemote || (n5 & 4) == 0) && chunkFromChunkCoords.isPopulated()) {
                this.markBlockForUpdate(n, n2, n3);
            }

            if (!this.isRemote && (n5 & 1) != 0) {
                this.notifyBlockChange(n, n2, n3, block);
                if (block.hasComparatorInputOverride()) {
                    this.updateNeighborsAboutBlockChange(n, n2, n3, block);
                }
            }
        }

        return setBlockMetadata;
    }

    public boolean setBlockToAir(int x, int y, int z) {
        return this.setBlock(x, y, z, Blocks.air, 0, 3);
    }

    public boolean breakBlock(int x, int y, int z, boolean dropBlock) {
        Block block = this.getBlock(x, y, z);
        if (block.getMaterial() == Material.air) {
            return false;
        }

        int blockMetadata = this.getBlockMetadata(x, y, z);
        this.playAuxSFX(2001, x, y, z, Block.getIdFromBlock(block) + (blockMetadata << 12));
        if (dropBlock) {
            block.dropBlockAsItem(this, x, y, z, blockMetadata, 0);
        }

        return this.setBlock(x, y, z, Blocks.air, 0, 3);
    }

    public boolean setBlock(int x, int y, int z, Block blockType) {
        return this.setBlock(x, y, z, blockType, 0, 3);
    }

    public void markBlockForUpdate(int n, int n2, int n3) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            ((IWorldAccess)this.worldAccesses.get(i)).markBlockForUpdate(n, n2, n3);
        }
    }

    public void notifyBlockChange(int n, int n2, int n3, Block block) {
        this.notifyBlocksOfNeighborChange(n, n2, n3, block);
    }

    public void markBlocksDirtyVertical(int n, int n2, int n3, int n4) {
        if (n3 > n4) {
            int n5 = n4;
            n4 = n3;
            n3 = n5;
        }

        if (!this.provider.hasNoSky) {
            for (int i = n3; i <= n4; i++) {
                this.updateLightByType(EnumSkyBlock.Sky, n, i, n2);
            }
        }

        this.markBlockRangeForRenderUpdate(n, n3, n2, n, n4, n2);
    }

    public void markBlockRangeForRenderUpdate(int n, int n2, int n3, int n4, int n5, int n6) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            ((IWorldAccess)this.worldAccesses.get(i)).markBlockRangeForRenderUpdate(n, n2, n3, n4, n5, n6);
        }
    }

    public void notifyBlocksOfNeighborChange(int n, int n2, int n3, Block block) {
        this.notifyBlockOfNeighborChange(n - 1, n2, n3, block);
        this.notifyBlockOfNeighborChange(n + 1, n2, n3, block);
        this.notifyBlockOfNeighborChange(n, n2 - 1, n3, block);
        this.notifyBlockOfNeighborChange(n, n2 + 1, n3, block);
        this.notifyBlockOfNeighborChange(n, n2, n3 - 1, block);
        this.notifyBlockOfNeighborChange(n, n2, n3 + 1, block);
    }

    public void notifyBlocksOfNeighborChange(int n, int n2, int n3, Block block, int n4) {
        if (n4 != 4) {
            this.notifyBlockOfNeighborChange(n - 1, n2, n3, block);
        }

        if (n4 != 5) {
            this.notifyBlockOfNeighborChange(n + 1, n2, n3, block);
        }

        if (n4 != 0) {
            this.notifyBlockOfNeighborChange(n, n2 - 1, n3, block);
        }

        if (n4 != 1) {
            this.notifyBlockOfNeighborChange(n, n2 + 1, n3, block);
        }

        if (n4 != 2) {
            this.notifyBlockOfNeighborChange(n, n2, n3 - 1, block);
        }

        if (n4 != 3) {
            this.notifyBlockOfNeighborChange(n, n2, n3 + 1, block);
        }
    }

    public void notifyBlockOfNeighborChange(int n, int n2, int n3, final Block block) {
        if (!this.isRemote) {
            Block block2 = this.getBlock(n, n2, n3);

            try {
                block2.onNeighborBlockChange(this, n, n2, n3, block);
            } catch (Throwable e) {
                CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Exception while updating neighbours");
                CrashReportCategory makeCategory = makeCrashReport.makeCategory("Block being updated");

                int n4;
                try {
                    n4 = this.getBlockMetadata(n, n2, n3);
                } catch (Throwable e2) {
                    n4 = -1;
                }

                makeCategory.addCrashSectionCallable("Source block type", new Callable() {
                    public String call() {
                        try {
                            return String.format("ID #%d (%s // %s)", Block.getIdFromBlock(block), block.getUnlocalizedName(), block.getClass().getCanonicalName());
                        } catch (Throwable e2) {
                            return "ID #" + Block.getIdFromBlock(block);
                        }
                    }
                });
                CrashReportCategory.addBlockInfo(makeCategory, n, n2, n3, block2, n4);
                throw new ReportedException(makeCrashReport);
            }
        }
    }

    public boolean isBlockTickScheduledThisTick(int n, int n2, int n3, Block block) {
        return false;
    }

    public boolean canBlockSeeTheSky(int n, int n2, int n3) {
        return this.getChunkFromChunkCoords(n >> 4, n3 >> 4).canBlockSeeTheSky(n & 15, n2, n3 & 15);
    }

    public int getFullBlockLightValue(int n, int n2, int n3) {
        if (n2 < 0) {
            return 0;
        }

        if (n2 >= 256) {
            n2 = 255;
        }

        return this.getChunkFromChunkCoords(n >> 4, n3 >> 4).getBlockLightValue(n & 15, n2, n3 & 15, 0);
    }

    public int getBlockLightValue(int n, int n2, int n3) {
        return this.getBlockLightValue_do(n, n2, n3, true);
    }

    public int getBlockLightValue_do(int n, int n2, int n3, boolean flag) {
        if (n < -30000000 || n3 < -30000000 || n >= 30000000 || n3 >= 30000000) {
            return 15;
        }

        if (flag && this.getBlock(n, n2, n3).getUseNeighborBrightness()) {
            int n4 = this.getBlockLightValue_do(n, n2 + 1, n3, false);
            int n5 = this.getBlockLightValue_do(n + 1, n2, n3, false);
            int n6 = this.getBlockLightValue_do(n - 1, n2, n3, false);
            int n7 = this.getBlockLightValue_do(n, n2, n3 + 1, false);
            int n8 = this.getBlockLightValue_do(n, n2, n3 - 1, false);
            if (n5 > n4) {
                n4 = n5;
            }

            if (n6 > n4) {
                n4 = n6;
            }

            if (n7 > n4) {
                n4 = n7;
            }

            if (n8 > n4) {
                n4 = n8;
            }

            return n4;
        } else {
            if (n2 < 0) {
                return 0;
            }

            if (n2 >= 256) {
                n2 = 255;
            }

            Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(n >> 4, n3 >> 4);
            n &= 15;
            n3 &= 15;
            return chunkFromChunkCoords.getBlockLightValue(n, n2, n3, this.skylightSubtracted);
        }
    }

    public int getHeightValue(int n, int n2) {
        if (n >= -30000000 && n2 >= -30000000 && n < 30000000 && n2 < 30000000) {
            if (!this.chunkExists(n >> 4, n2 >> 4)) {
                return 0;
            }

            Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(n >> 4, n2 >> 4);
            return chunkFromChunkCoords.getHeightValue(n & 15, n2 & 15);
        } else {
            return 64;
        }
    }

    public int getChunkHeightMapMinimum(int n, int n2) {
        if (n >= -30000000 && n2 >= -30000000 && n < 30000000 && n2 < 30000000) {
            if (!this.chunkExists(n >> 4, n2 >> 4)) {
                return 0;
            }

            Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(n >> 4, n2 >> 4);
            return chunkFromChunkCoords.heightMapMinimum;
        } else {
            return 64;
        }
    }

    public int getSkyBlockTypeBrightness(EnumSkyBlock enumSkyBlock, int n, int n2, int n3) {
        if (this.provider.hasNoSky && enumSkyBlock == EnumSkyBlock.Sky) {
            return 0;
        }

        if (n2 < 0) {
            n2 = 0;
        }

        if (n2 >= 256) {
            return enumSkyBlock.defaultLightValue;
        }

        if (n >= -30000000 && n3 >= -30000000 && n < 30000000 && n3 < 30000000) {
            int n4 = n >> 4;
            int n5 = n3 >> 4;
            if (!this.chunkExists(n4, n5)) {
                return enumSkyBlock.defaultLightValue;
            }

            if (this.getBlock(n, n2, n3).getUseNeighborBrightness()) {
                int savedLightValue = this.getSavedLightValue(enumSkyBlock, n, n2 + 1, n3);
                int savedLightValue2 = this.getSavedLightValue(enumSkyBlock, n + 1, n2, n3);
                int savedLightValue3 = this.getSavedLightValue(enumSkyBlock, n - 1, n2, n3);
                int savedLightValue4 = this.getSavedLightValue(enumSkyBlock, n, n2, n3 + 1);
                int savedLightValue5 = this.getSavedLightValue(enumSkyBlock, n, n2, n3 - 1);
                if (savedLightValue2 > savedLightValue) {
                    savedLightValue = savedLightValue2;
                }

                if (savedLightValue3 > savedLightValue) {
                    savedLightValue = savedLightValue3;
                }

                if (savedLightValue4 > savedLightValue) {
                    savedLightValue = savedLightValue4;
                }

                if (savedLightValue5 > savedLightValue) {
                    savedLightValue = savedLightValue5;
                }

                return savedLightValue;
            } else {
                Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(n4, n5);
                return chunkFromChunkCoords.getSavedLightValue(enumSkyBlock, n & 15, n2, n3 & 15);
            }
        } else {
            return enumSkyBlock.defaultLightValue;
        }
    }

    public int getSavedLightValue(EnumSkyBlock enumSkyBlock, int n, int n2, int n3) {
        if (n2 < 0) {
            n2 = 0;
        }

        if (n2 >= 256) {
            n2 = 255;
        }

        if (n >= -30000000 && n3 >= -30000000 && n < 30000000 && n3 < 30000000) {
            int n4 = n >> 4;
            int n5 = n3 >> 4;
            if (!this.chunkExists(n4, n5)) {
                return enumSkyBlock.defaultLightValue;
            }

            Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(n4, n5);
            return chunkFromChunkCoords.getSavedLightValue(enumSkyBlock, n & 15, n2, n3 & 15);
        } else {
            return enumSkyBlock.defaultLightValue;
        }
    }

    public void setLightValue(EnumSkyBlock enumSkyBlock, int n, int n2, int n3, int n4) {
        if (n >= -30000000 && n3 >= -30000000 && n < 30000000 && n3 < 30000000 && n2 >= 0 && n2 < 256 && this.chunkExists(n >> 4, n3 >> 4)) {
            Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(n >> 4, n3 >> 4);
            chunkFromChunkCoords.setLightValue(enumSkyBlock, n & 15, n2, n3 & 15, n4);

            for (int i = 0; i < this.worldAccesses.size(); i++) {
                ((IWorldAccess)this.worldAccesses.get(i)).markBlockForRenderUpdate(n, n2, n3);
            }
        }
    }

    public void markBlockForRenderUpdate(int n, int n2, int n3) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            ((IWorldAccess)this.worldAccesses.get(i)).markBlockForRenderUpdate(n, n2, n3);
        }
    }

    @Override
    public int getLightBrightnessForSkyBlocks(int n, int n2, int n3, int n4) {
        int skyBlockTypeBrightness = this.getSkyBlockTypeBrightness(EnumSkyBlock.Sky, n, n2, n3);
        int skyBlockTypeBrightness2 = this.getSkyBlockTypeBrightness(EnumSkyBlock.Block, n, n2, n3);
        if (skyBlockTypeBrightness2 < n4) {
            skyBlockTypeBrightness2 = n4;
        }

        return skyBlockTypeBrightness << 20 | skyBlockTypeBrightness2 << 4;
    }

    public float getLightBrightness(int n, int n2, int n3) {
        return this.provider.lightBrightnessTable[this.getBlockLightValue(n, n2, n3)];
    }

    public boolean isDaytime() {
        return this.skylightSubtracted < 4;
    }

    public MovingObjectPosition rayTraceBlocks(Vec3 vec3, Vec3 vec32) {
        return this.rayTraceBlocks(vec3, vec32, false, false, false);
    }

    public MovingObjectPosition rayTraceBlocks(Vec3 vec3, Vec3 vec32, boolean flag) {
        return this.rayTraceBlocks(vec3, vec32, flag, false, false);
    }

    public MovingObjectPosition rayTraceBlocks(Vec3 vec3, Vec3 vec32, boolean flag, boolean flag2, boolean flag3) {
        if (Double.isNaN(vec3.xCoord) || Double.isNaN(vec3.yCoord) || Double.isNaN(vec3.zCoord)) {
            return null;
        }

        if (!Double.isNaN(vec32.xCoord) && !Double.isNaN(vec32.yCoord) && !Double.isNaN(vec32.zCoord)) {
            int n = MathHelper.floor_double(vec32.xCoord);
            int n2 = MathHelper.floor_double(vec32.yCoord);
            int n3 = MathHelper.floor_double(vec32.zCoord);
            int n4 = MathHelper.floor_double(vec3.xCoord);
            int n5 = MathHelper.floor_double(vec3.yCoord);
            int n6 = MathHelper.floor_double(vec3.zCoord);
            Block block = this.getBlock(n4, n5, n6);
            int blockMetadata = this.getBlockMetadata(n4, n5, n6);
            if ((!flag2 || block.getCollisionBoundingBoxFromPool(this, n4, n5, n6) != null) && block.canStopRayTrace(blockMetadata, flag)) {
                MovingObjectPosition collisionRayTrace = block.collisionRayTrace(this, n4, n5, n6, vec3, vec32);
                if (collisionRayTrace != null) {
                    return collisionRayTrace;
                }
            }

            MovingObjectPosition movingObjectPosition = null;
            blockMetadata = 200;

            while (blockMetadata-- >= 0) {
                if (Double.isNaN(vec3.xCoord) || Double.isNaN(vec3.yCoord) || Double.isNaN(vec3.zCoord)) {
                    return null;
                }

                if (n4 == n && n5 == n2 && n6 == n3) {
                    return flag3 ? movingObjectPosition : null;
                }

                boolean flag4 = true;
                boolean flag5 = true;
                boolean flag6 = true;
                double d = 999.0;
                double d2 = 999.0;
                double d3 = 999.0;
                if (n > n4) {
                    d = n4 + 1.0;
                } else if (n < n4) {
                    d = n4 + 0.0;
                } else {
                    flag4 = false;
                }

                if (n2 > n5) {
                    d2 = n5 + 1.0;
                } else if (n2 < n5) {
                    d2 = n5 + 0.0;
                } else {
                    flag5 = false;
                }

                if (n3 > n6) {
                    d3 = n6 + 1.0;
                } else if (n3 < n6) {
                    d3 = n6 + 0.0;
                } else {
                    flag6 = false;
                }

                double d4 = 999.0;
                double d5 = 999.0;
                double d6 = 999.0;
                double d7 = vec32.xCoord - vec3.xCoord;
                double d8 = vec32.yCoord - vec3.yCoord;
                double d9 = vec32.zCoord - vec3.zCoord;
                if (flag4) {
                    d4 = (d - vec3.xCoord) / d7;
                }

                if (flag5) {
                    d5 = (d2 - vec3.yCoord) / d8;
                }

                if (flag6) {
                    d6 = (d3 - vec3.zCoord) / d9;
                }

                boolean flag7 = false;
                byte b;
                if (d4 < d5 && d4 < d6) {
                    if (n > n4) {
                        b = 4;
                    } else {
                        b = 5;
                    }

                    vec3.xCoord = d;
                    vec3.yCoord += d8 * d4;
                    vec3.zCoord += d9 * d4;
                } else if (d5 < d6) {
                    if (n2 > n5) {
                        b = 0;
                    } else {
                        b = 1;
                    }

                    vec3.xCoord += d7 * d5;
                    vec3.yCoord = d2;
                    vec3.zCoord += d9 * d5;
                } else {
                    if (n3 > n6) {
                        b = 2;
                    } else {
                        b = 3;
                    }

                    vec3.xCoord += d7 * d6;
                    vec3.yCoord += d8 * d6;
                    vec3.zCoord = d3;
                }

                Vec3 createVectorHelper = Vec3.createVectorHelper(vec3.xCoord, vec3.yCoord, vec3.zCoord);
                n4 = (int)(createVectorHelper.xCoord = MathHelper.floor_double(vec3.xCoord));
                if (b == 5) {
                    n4--;
                    createVectorHelper.xCoord++;
                }

                n5 = (int)(createVectorHelper.yCoord = MathHelper.floor_double(vec3.yCoord));
                if (b == 1) {
                    n5--;
                    createVectorHelper.yCoord++;
                }

                n6 = (int)(createVectorHelper.zCoord = MathHelper.floor_double(vec3.zCoord));
                if (b == 3) {
                    n6--;
                    createVectorHelper.zCoord++;
                }

                Block block2 = this.getBlock(n4, n5, n6);
                int blockMetadata2 = this.getBlockMetadata(n4, n5, n6);
                if (!flag2 || block2.getCollisionBoundingBoxFromPool(this, n4, n5, n6) != null) {
                    if (block2.canStopRayTrace(blockMetadata2, flag)) {
                        MovingObjectPosition collisionRayTrace = block2.collisionRayTrace(this, n4, n5, n6, vec3, vec32);
                        if (collisionRayTrace != null) {
                            return collisionRayTrace;
                        }
                    } else {
                        movingObjectPosition = new MovingObjectPosition(n4, n5, n6, b, vec3, false);
                    }
                }
            }

            return flag3 ? movingObjectPosition : null;
        } else {
            return null;
        }
    }

    public void playSoundAtEntity(Entity entity, String str, float f, float f2) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            ((IWorldAccess)this.worldAccesses.get(i)).playSound(str, entity.posX, entity.posY - entity.yOffset, entity.posZ, f, f2);
        }
    }

    public void playSoundToNearExcept(EntityPlayer entityPlayer, String str, float f, float f2) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            ((IWorldAccess)this.worldAccesses.get(i)).playSoundToNearExcept(entityPlayer, str, entityPlayer.posX, entityPlayer.posY - entityPlayer.yOffset, entityPlayer.posZ, f, f2);
        }
    }

    public void playSoundEffect(double x, double y, double z, String soundName, float volume, float pitch) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            ((IWorldAccess)this.worldAccesses.get(i)).playSound(soundName, x, y, z, volume, pitch);
        }
    }

    public void playSound(double x, double y, double z, String soundName, float volume, float pitch, boolean distanceDelay) {
    }

    public void playRecord(String recordName, int x, int y, int z) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            ((IWorldAccess)this.worldAccesses.get(i)).playRecord(recordName, x, y, z);
        }
    }

    public void spawnParticle(String particleName, double x, double y, double z, double velocityX, double velocityY, double velocityZ) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            ((IWorldAccess)this.worldAccesses.get(i)).spawnParticle(particleName, x, y, z, velocityX, velocityY, velocityZ);
        }
    }

    public boolean addWeatherEffect(Entity entity) {
        this.weatherEffects.add(entity);
        return true;
    }

    public boolean spawnEntityInWorld(Entity entity) {
        int n = MathHelper.floor_double(entity.posX / 16.0);
        int n2 = MathHelper.floor_double(entity.posZ / 16.0);
        boolean forceSpawn = entity.forceSpawn;
        if (entity instanceof EntityPlayer) {
            forceSpawn = true;
        }

        if (!forceSpawn && !this.chunkExists(n, n2)) {
            return false;
        }

        if (entity instanceof EntityPlayer) {
            EntityPlayer entityPlayer = (EntityPlayer)entity;
            this.playerEntities.add(entityPlayer);
            this.updateAllPlayersSleepingFlag();
        }

        this.getChunkFromChunkCoords(n, n2).addEntity(entity);
        this.loadedEntityList.add(entity);
        this.onEntityAdded(entity);
        return true;
    }

    public void onEntityAdded(Entity entity) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            ((IWorldAccess)this.worldAccesses.get(i)).onEntityCreate(entity);
        }
    }

    public void onEntityRemoved(Entity entity) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            ((IWorldAccess)this.worldAccesses.get(i)).onEntityDestroy(entity);
        }
    }

    public void removeEntity(Entity entity) {
        if (entity.riddenByEntity != null) {
            entity.riddenByEntity.mountEntity(null);
        }

        if (entity.ridingEntity != null) {
            entity.mountEntity(null);
        }

        entity.setDead();
        if (entity instanceof EntityPlayer) {
            this.playerEntities.remove(entity);
            this.updateAllPlayersSleepingFlag();
            this.onEntityRemoved(entity);
        }
    }

    public void removePlayerEntityDangerously(Entity entity) {
        entity.setDead();
        if (entity instanceof EntityPlayer) {
            this.playerEntities.remove(entity);
            this.updateAllPlayersSleepingFlag();
        }

        int chunkCoordX = entity.chunkCoordX;
        int chunkCoordZ = entity.chunkCoordZ;
        if (entity.addedToChunk && this.chunkExists(chunkCoordX, chunkCoordZ)) {
            this.getChunkFromChunkCoords(chunkCoordX, chunkCoordZ).removeEntity(entity);
        }

        this.loadedEntityList.remove(entity);
        this.onEntityRemoved(entity);
    }

    public void addWorldAccess(IWorldAccess worldAccess) {
        this.worldAccesses.add(worldAccess);
    }

    public void removeWorldAccess(IWorldAccess worldAccess) {
        this.worldAccesses.remove(worldAccess);
    }

    public List getCollidingBoundingBoxes(Entity entity, AxisAlignedBB axisAlignedBB) {
        this.collidingBoundingBoxes.clear();
        int n = MathHelper.floor_double(axisAlignedBB.minX);
        int n2 = MathHelper.floor_double(axisAlignedBB.maxX + 1.0);
        int n3 = MathHelper.floor_double(axisAlignedBB.minY);
        int n4 = MathHelper.floor_double(axisAlignedBB.maxY + 1.0);
        int n5 = MathHelper.floor_double(axisAlignedBB.minZ);
        int n6 = MathHelper.floor_double(axisAlignedBB.maxZ + 1.0);

        for (int i = n; i < n2; i++) {
            for (int j = n5; j < n6; j++) {
                if (this.blockExists(i, 64, j)) {
                    for (int k = n3 - 1; k < n4; k++) {
                        Block block;
                        if (i >= -30000000 && i < 30000000 && j >= -30000000 && j < 30000000) {
                            block = this.getBlock(i, k, j);
                        } else {
                            block = Blocks.stone;
                        }

                        block.addCollisionBoxesToList(this, i, k, j, axisAlignedBB, this.collidingBoundingBoxes, entity);
                    }
                }
            }
        }

        double d = 0.25;
        List list = this.getEntitiesWithinAABBExcludingEntity(entity, axisAlignedBB.expand(d, d, d));

        for (int i = 0; i < list.size(); i++) {
            AxisAlignedBB boundingBox = ((Entity)list.get(i)).getBoundingBox();
            if (boundingBox != null && boundingBox.intersectsWith(axisAlignedBB)) {
                this.collidingBoundingBoxes.add(boundingBox);
            }

            boundingBox = entity.getCollisionBox((Entity)list.get(i));
            if (boundingBox != null && boundingBox.intersectsWith(axisAlignedBB)) {
                this.collidingBoundingBoxes.add(boundingBox);
            }
        }

        return this.collidingBoundingBoxes;
    }

    public List findList(AxisAlignedBB axisAlignedBB) {
        this.collidingBoundingBoxes.clear();
        int n = MathHelper.floor_double(axisAlignedBB.minX);
        int n2 = MathHelper.floor_double(axisAlignedBB.maxX + 1.0);
        int n3 = MathHelper.floor_double(axisAlignedBB.minY);
        int n4 = MathHelper.floor_double(axisAlignedBB.maxY + 1.0);
        int n5 = MathHelper.floor_double(axisAlignedBB.minZ);
        int n6 = MathHelper.floor_double(axisAlignedBB.maxZ + 1.0);

        for (int i = n; i < n2; i++) {
            for (int j = n5; j < n6; j++) {
                if (this.blockExists(i, 64, j)) {
                    for (int k = n3 - 1; k < n4; k++) {
                        Block block;
                        if (i >= -30000000 && i < 30000000 && j >= -30000000 && j < 30000000) {
                            block = this.getBlock(i, k, j);
                        } else {
                            block = Blocks.bedrock;
                        }

                        block.addCollisionBoxesToList(this, i, k, j, axisAlignedBB, this.collidingBoundingBoxes, null);
                    }
                }
            }
        }

        return this.collidingBoundingBoxes;
    }

    public int calculateSkylightSubtracted(float f) {
        float celestialAngle = this.getCelestialAngle(f);
        float f2 = 1.0F - (MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F);
        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        f2 = 1.0F - f2;
        f2 = (float)(f2 * (1.0 - this.getRainStrength(f) * 5.0F / 16.0));
        f2 = (float)(f2 * (1.0 - this.getWeightedThunderStrength(f) * 5.0F / 16.0));
        f2 = 1.0F - f2;
        return (int)(f2 * 11.0F);
    }

    public float getSunBrightness(float f) {
        float celestialAngle = this.getCelestialAngle(f);
        float f2 = 1.0F - (MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.2F);
        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        f2 = 1.0F - f2;
        f2 = (float)(f2 * (1.0 - this.getRainStrength(f) * 5.0F / 16.0));
        f2 = (float)(f2 * (1.0 - this.getWeightedThunderStrength(f) * 5.0F / 16.0));
        return f2 * 0.8F + 0.2F;
    }

    public Vec3 getSkyColor(Entity entity, float f) {
        float celestialAngle = this.getCelestialAngle(f);
        float f2 = MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        int n = MathHelper.floor_double(entity.posX);
        int n2 = MathHelper.floor_double(entity.posY);
        int n3 = MathHelper.floor_double(entity.posZ);
        BiomeGenBase biomeGenForCoords = this.getBiomeGenForCoords(n, n3);
        float floatTemperature = biomeGenForCoords.getFloatTemperature(n, n2, n3);
        int skyColorByTemp = biomeGenForCoords.getSkyColorByTemp(floatTemperature);
        float f3 = (skyColorByTemp >> 16 & 0xFF) / 255.0F;
        float f4 = (skyColorByTemp >> 8 & 0xFF) / 255.0F;
        float f5 = (skyColorByTemp & 0xFF) / 255.0F;
        f3 *= f2;
        f4 *= f2;
        f5 *= f2;
        float rainStrength = this.getRainStrength(f);
        if (rainStrength > 0.0F) {
            float f6 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.6F;
            float f7 = 1.0F - rainStrength * 0.75F;
            f3 = f3 * f7 + f6 * (1.0F - f7);
            f4 = f4 * f7 + f6 * (1.0F - f7);
            f5 = f5 * f7 + f6 * (1.0F - f7);
        }

        float weightedThunderStrength = this.getWeightedThunderStrength(f);
        if (weightedThunderStrength > 0.0F) {
            float f6 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.2F;
            float f7 = 1.0F - weightedThunderStrength * 0.75F;
            f3 = f3 * f7 + f6 * (1.0F - f7);
            f4 = f4 * f7 + f6 * (1.0F - f7);
            f5 = f5 * f7 + f6 * (1.0F - f7);
        }

        if (this.lastLightningBolt > 0) {
            float f6 = this.lastLightningBolt - f;
            if (f6 > 1.0F) {
                f6 = 1.0F;
            }

            f6 *= 0.45F;
            f3 = f3 * (1.0F - f6) + 0.8F * f6;
            f4 = f4 * (1.0F - f6) + 0.8F * f6;
            f5 = f5 * (1.0F - f6) + 1.0F * f6;
        }

        return Vec3.createVectorHelper(f3, f4, f5);
    }

    public float getCelestialAngle(float f) {
        return this.provider.calculateCelestialAngle(this.worldInfo.getWorldTime(), f);
    }

    public int getMoonPhase() {
        return this.provider.getMoonPhase(this.worldInfo.getWorldTime());
    }

    public float getCurrentMoonPhaseFactor() {
        return WorldProvider.moonPhaseFactors[this.provider.getMoonPhase(this.worldInfo.getWorldTime())];
    }

    public float getCelestialAngleRadians(float f) {
        float celestialAngle = this.getCelestialAngle(f);
        return celestialAngle * (float) Math.PI * 2.0F;
    }

    public Vec3 getCloudColour(float f) {
        float celestialAngle = this.getCelestialAngle(f);
        float f2 = MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.5F;
        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        float f3 = (float)(16777215L >> 16 & 255L) / 255.0F;
        float f4 = (float)(16777215L >> 8 & 255L) / 255.0F;
        float f5 = (float)(16777215L & 255L) / 255.0F;
        float rainStrength = this.getRainStrength(f);
        if (rainStrength > 0.0F) {
            float f6 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.6F;
            float f7 = 1.0F - rainStrength * 0.95F;
            f3 = f3 * f7 + f6 * (1.0F - f7);
            f4 = f4 * f7 + f6 * (1.0F - f7);
            f5 = f5 * f7 + f6 * (1.0F - f7);
        }

        f3 *= f2 * 0.9F + 0.1F;
        f4 *= f2 * 0.9F + 0.1F;
        f5 *= f2 * 0.85F + 0.15F;
        float weightedThunderStrength = this.getWeightedThunderStrength(f);
        if (weightedThunderStrength > 0.0F) {
            float f6 = (f3 * 0.3F + f4 * 0.59F + f5 * 0.11F) * 0.2F;
            float f7 = 1.0F - weightedThunderStrength * 0.95F;
            f3 = f3 * f7 + f6 * (1.0F - f7);
            f4 = f4 * f7 + f6 * (1.0F - f7);
            f5 = f5 * f7 + f6 * (1.0F - f7);
        }

        return Vec3.createVectorHelper(f3, f4, f5);
    }

    public Vec3 getFogColor(float f) {
        float celestialAngle = this.getCelestialAngle(f);
        return this.provider.getFogColor(celestialAngle, f);
    }

    public int getPrecipitationHeight(int n, int n2) {
        return this.getChunkFromBlockCoords(n, n2).getPrecipitationHeight(n & 15, n2 & 15);
    }

    public int getTopSolidOrLiquidBlock(int n, int n2) {
        Chunk chunkFromBlockCoords = this.getChunkFromBlockCoords(n, n2);
        int n3 = chunkFromBlockCoords.getTopFilledSegment() + 15;
        n &= 15;
        n2 &= 15;

        while (n3 > 0) {
            Block block = chunkFromBlockCoords.getBlock(n, n3, n2);
            if (block.getMaterial().blocksMovement() && block.getMaterial() != Material.leaves) {
                return n3 + 1;
            }

            n3--;
        }

        return -1;
    }

    public float getStarBrightness(float f) {
        float celestialAngle = this.getCelestialAngle(f);
        float f2 = 1.0F - (MathHelper.cos(celestialAngle * (float) Math.PI * 2.0F) * 2.0F + 0.25F);
        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        if (f2 > 1.0F) {
            f2 = 1.0F;
        }

        return f2 * f2 * 0.5F;
    }

    public void scheduleBlockUpdate(int n, int n2, int n3, Block block, int n4) {
    }

    public void scheduleBlockUpdateWithPriority(int n, int n2, int n3, Block block, int n4, int n5) {
    }

    public void process(int n, int n2, int n3, Block block, int n4, int n5) {
    }

    public void updateEntities() {
        this.theProfiler.startSection("entities");
        this.theProfiler.startSection("global");

        for (int i = 0; i < this.weatherEffects.size(); i++) {
            Entity entity = (Entity)this.weatherEffects.get(i);

            try {
                entity.ticksExisted++;
                entity.onUpdate();
            } catch (Throwable e) {
                CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Ticking entity");
                CrashReportCategory makeCategory = makeCrashReport.makeCategory("Entity being ticked");
                if (entity == null) {
                    makeCategory.addCrashSection("Entity", "~~NULL~~");
                } else {
                    entity.addEntityCrashInfo(makeCategory);
                }

                throw new ReportedException(makeCrashReport);
            }

            if (entity.isDead) {
                this.weatherEffects.remove(i--);
            }
        }

        this.theProfiler.endStartSection("remove");
        this.loadedEntityList.removeAll(this.unloadedEntityList);

        for (int i = 0; i < this.unloadedEntityList.size(); i++) {
            Entity entity = (Entity)this.unloadedEntityList.get(i);
            int chunkCoordX = entity.chunkCoordX;
            int chunkCoordZ = entity.chunkCoordZ;
            if (entity.addedToChunk && this.chunkExists(chunkCoordX, chunkCoordZ)) {
                this.getChunkFromChunkCoords(chunkCoordX, chunkCoordZ).removeEntity(entity);
            }
        }

        for (int i = 0; i < this.unloadedEntityList.size(); i++) {
            this.onEntityRemoved((Entity)this.unloadedEntityList.get(i));
        }

        this.unloadedEntityList.clear();
        this.theProfiler.endStartSection("regular");

        for (int i = 0; i < this.loadedEntityList.size(); i++) {
            Entity entity = (Entity)this.loadedEntityList.get(i);
            if (entity.ridingEntity != null) {
                if (!entity.ridingEntity.isDead && entity.ridingEntity.riddenByEntity == entity) {
                    continue;
                }

                entity.ridingEntity.riddenByEntity = null;
                entity.ridingEntity = null;
            }

            this.theProfiler.startSection("tick");
            if (!entity.isDead) {
                try {
                    this.updateEntity(entity);
                } catch (Throwable e) {
                    CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Ticking entity");
                    CrashReportCategory makeCategory = makeCrashReport.makeCategory("Entity being ticked");
                    entity.addEntityCrashInfo(makeCategory);
                    throw new ReportedException(makeCrashReport);
                }
            }

            this.theProfiler.endSection();
            this.theProfiler.startSection("remove");
            if (entity.isDead) {
                int chunkCoordX = entity.chunkCoordX;
                int chunkCoordZ = entity.chunkCoordZ;
                if (entity.addedToChunk && this.chunkExists(chunkCoordX, chunkCoordZ)) {
                    this.getChunkFromChunkCoords(chunkCoordX, chunkCoordZ).removeEntity(entity);
                }

                this.loadedEntityList.remove(i--);
                this.onEntityRemoved(entity);
            }

            this.theProfiler.endSection();
        }

        this.theProfiler.endStartSection("blockEntities");
        this.processingLoadedTiles = true;
        Iterator iterator = this.loadedTileEntityList.iterator();

        while (iterator.hasNext()) {
            TileEntity tileEntity = (TileEntity)iterator.next();
            if (!tileEntity.isInvalid() && tileEntity.hasWorldObj() && this.blockExists(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord)) {
                try {
                    tileEntity.updateEntity();
                } catch (Throwable e) {
                    CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Ticking block entity");
                    CrashReportCategory makeCategory = makeCrashReport.makeCategory("Block entity being ticked");
                    tileEntity.addInfoToCrashReport(makeCategory);
                    throw new ReportedException(makeCrashReport);
                }
            }

            if (tileEntity.isInvalid()) {
                iterator.remove();
                if (this.chunkExists(tileEntity.xCoord >> 4, tileEntity.zCoord >> 4)) {
                    Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(tileEntity.xCoord >> 4, tileEntity.zCoord >> 4);
                    if (chunkFromChunkCoords != null) {
                        chunkFromChunkCoords.removeTileEntity(tileEntity.xCoord & 15, tileEntity.yCoord, tileEntity.zCoord & 15);
                    }
                }
            }
        }

        this.processingLoadedTiles = false;
        if (!this.tileEntitiesToBeRemoved.isEmpty()) {
            this.loadedTileEntityList.removeAll(this.tileEntitiesToBeRemoved);
            this.tileEntitiesToBeRemoved.clear();
        }

        this.theProfiler.endStartSection("pendingBlockEntities");
        if (!this.addedTileEntityList.isEmpty()) {
            for (int i = 0; i < this.addedTileEntityList.size(); i++) {
                TileEntity tileEntity = (TileEntity)this.addedTileEntityList.get(i);
                if (!tileEntity.isInvalid()) {
                    if (!this.loadedTileEntityList.contains(tileEntity)) {
                        this.loadedTileEntityList.add(tileEntity);
                    }

                    if (this.chunkExists(tileEntity.xCoord >> 4, tileEntity.zCoord >> 4)) {
                        Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(tileEntity.xCoord >> 4, tileEntity.zCoord >> 4);
                        if (chunkFromChunkCoords != null) {
                            chunkFromChunkCoords.setBlockTileEntityInChunk(tileEntity.xCoord & 15, tileEntity.yCoord, tileEntity.zCoord & 15, tileEntity);
                        }
                    }

                    this.markBlockForUpdate(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
                }
            }

            this.addedTileEntityList.clear();
        }

        this.theProfiler.endSection();
        this.theProfiler.endSection();
    }

    public void addBlockEntities(Collection collection) {
        if (this.processingLoadedTiles) {
            this.addedTileEntityList.addAll(collection);
        } else {
            this.loadedTileEntityList.addAll(collection);
        }
    }

    public void updateEntity(Entity entity) throws java.io.IOException {
        this.updateEntityWithOptionalForce(entity, true);
    }

    public void updateEntityWithOptionalForce(Entity entity, boolean flag) throws java.io.IOException {
        int n = MathHelper.floor_double(entity.posX);
        int n2 = MathHelper.floor_double(entity.posZ);
        byte b = 32;
        if (!flag || this.checkChunksExist(n - b, 0, n2 - b, n + b, 0, n2 + b)) {
            entity.lastTickPosX = entity.posX;
            entity.lastTickPosY = entity.posY;
            entity.lastTickPosZ = entity.posZ;
            entity.prevRotationYaw = entity.rotationYaw;
            entity.prevRotationPitch = entity.rotationPitch;
            if (flag && entity.addedToChunk) {
                entity.ticksExisted++;
                if (entity.ridingEntity != null) {
                    entity.updateRidden();
                } else {
                    entity.onUpdate();
                }
            }

            this.theProfiler.startSection("chunkCheck");
            if (Double.isNaN(entity.posX) || Double.isInfinite(entity.posX)) {
                entity.posX = entity.lastTickPosX;
            }

            if (Double.isNaN(entity.posY) || Double.isInfinite(entity.posY)) {
                entity.posY = entity.lastTickPosY;
            }

            if (Double.isNaN(entity.posZ) || Double.isInfinite(entity.posZ)) {
                entity.posZ = entity.lastTickPosZ;
            }

            if (Double.isNaN(entity.rotationPitch) || Double.isInfinite(entity.rotationPitch)) {
                entity.rotationPitch = entity.prevRotationPitch;
            }

            if (Double.isNaN(entity.rotationYaw) || Double.isInfinite(entity.rotationYaw)) {
                entity.rotationYaw = entity.prevRotationYaw;
            }

            int n3 = MathHelper.floor_double(entity.posX / 16.0);
            int n4 = MathHelper.floor_double(entity.posY / 16.0);
            int n5 = MathHelper.floor_double(entity.posZ / 16.0);
            if (!entity.addedToChunk || entity.chunkCoordX != n3 || entity.chunkCoordY != n4 || entity.chunkCoordZ != n5) {
                if (entity.addedToChunk && this.chunkExists(entity.chunkCoordX, entity.chunkCoordZ)) {
                    this.getChunkFromChunkCoords(entity.chunkCoordX, entity.chunkCoordZ).removeEntityAtIndex(entity, entity.chunkCoordY);
                }

                if (this.chunkExists(n3, n5)) {
                    entity.addedToChunk = true;
                    this.getChunkFromChunkCoords(n3, n5).addEntity(entity);
                } else {
                    entity.addedToChunk = false;
                }
            }

            this.theProfiler.endSection();
            if (flag && entity.addedToChunk && entity.riddenByEntity != null) {
                if (!entity.riddenByEntity.isDead && entity.riddenByEntity.ridingEntity == entity) {
                    this.updateEntity(entity.riddenByEntity);
                } else {
                    entity.riddenByEntity.ridingEntity = null;
                    entity.riddenByEntity = null;
                }
            }
        }
    }

    public boolean checkNoEntityCollision(AxisAlignedBB axisAlignedBB) {
        return this.checkNoEntityCollision(axisAlignedBB, null);
    }

    public boolean checkNoEntityCollision(AxisAlignedBB axisAlignedBB, Entity entity) {
        List list = this.getEntitiesWithinAABBExcludingEntity(null, axisAlignedBB);

        for (int i = 0; i < list.size(); i++) {
            Entity entity2 = (Entity)list.get(i);
            if (!entity2.isDead && entity2.preventEntitySpawning && entity2 != entity) {
                return false;
            }
        }

        return true;
    }

    public boolean checkBlockCollision(AxisAlignedBB axisAlignedBB) {
        int n = MathHelper.floor_double(axisAlignedBB.minX);
        int n2 = MathHelper.floor_double(axisAlignedBB.maxX + 1.0);
        int n3 = MathHelper.floor_double(axisAlignedBB.minY);
        int n4 = MathHelper.floor_double(axisAlignedBB.maxY + 1.0);
        int n5 = MathHelper.floor_double(axisAlignedBB.minZ);
        int n6 = MathHelper.floor_double(axisAlignedBB.maxZ + 1.0);
        if (axisAlignedBB.minX < 0.0) {
            n--;
        }

        if (axisAlignedBB.minY < 0.0) {
            n3--;
        }

        if (axisAlignedBB.minZ < 0.0) {
            n5--;
        }

        for (int i = n; i < n2; i++) {
            for (int j = n3; j < n4; j++) {
                for (int k = n5; k < n6; k++) {
                    Block block = this.getBlock(i, j, k);
                    if (block.getMaterial() != Material.air) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isAnyLiquid(AxisAlignedBB axisAlignedBB) {
        int n = MathHelper.floor_double(axisAlignedBB.minX);
        int n2 = MathHelper.floor_double(axisAlignedBB.maxX + 1.0);
        int n3 = MathHelper.floor_double(axisAlignedBB.minY);
        int n4 = MathHelper.floor_double(axisAlignedBB.maxY + 1.0);
        int n5 = MathHelper.floor_double(axisAlignedBB.minZ);
        int n6 = MathHelper.floor_double(axisAlignedBB.maxZ + 1.0);
        if (axisAlignedBB.minX < 0.0) {
            n--;
        }

        if (axisAlignedBB.minY < 0.0) {
            n3--;
        }

        if (axisAlignedBB.minZ < 0.0) {
            n5--;
        }

        for (int i = n; i < n2; i++) {
            for (int j = n3; j < n4; j++) {
                for (int k = n5; k < n6; k++) {
                    Block block = this.getBlock(i, j, k);
                    if (block.getMaterial().isLiquid()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean containsFireSource(AxisAlignedBB axisAlignedBB) {
        int n = MathHelper.floor_double(axisAlignedBB.minX);
        int n2 = MathHelper.floor_double(axisAlignedBB.maxX + 1.0);
        int n3 = MathHelper.floor_double(axisAlignedBB.minY);
        int n4 = MathHelper.floor_double(axisAlignedBB.maxY + 1.0);
        int n5 = MathHelper.floor_double(axisAlignedBB.minZ);
        int n6 = MathHelper.floor_double(axisAlignedBB.maxZ + 1.0);
        if (this.checkChunksExist(n, n3, n5, n2, n4, n6)) {
            for (int i = n; i < n2; i++) {
                for (int j = n3; j < n4; j++) {
                    for (int k = n5; k < n6; k++) {
                        Block block = this.getBlock(i, j, k);
                        if (block == Blocks.fire || block == Blocks.flowing_lava || block == Blocks.lava) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public boolean handleMaterialAcceleration(AxisAlignedBB axisAlignedBB, Material material, Entity entity) {
        int n = MathHelper.floor_double(axisAlignedBB.minX);
        int n2 = MathHelper.floor_double(axisAlignedBB.maxX + 1.0);
        int n3 = MathHelper.floor_double(axisAlignedBB.minY);
        int n4 = MathHelper.floor_double(axisAlignedBB.maxY + 1.0);
        int n5 = MathHelper.floor_double(axisAlignedBB.minZ);
        int n6 = MathHelper.floor_double(axisAlignedBB.maxZ + 1.0);
        if (!this.checkChunksExist(n, n3, n5, n2, n4, n6)) {
            return false;
        }

        boolean flag = false;
        Vec3 createVectorHelper = Vec3.createVectorHelper(0.0, 0.0, 0.0);

        for (int i = n; i < n2; i++) {
            for (int j = n3; j < n4; j++) {
                for (int k = n5; k < n6; k++) {
                    Block block = this.getBlock(i, j, k);
                    if (block.getMaterial() == material) {
                        double d = j + 1 - BlockLiquid.getLiquidHeightPercent(this.getBlockMetadata(i, j, k));
                        if (n4 >= d) {
                            flag = true;
                            block.modifyEntityVelocity(this, i, j, k, entity, createVectorHelper);
                        }
                    }
                }
            }
        }

        if (createVectorHelper.lengthVector() > 0.0 && entity.isPushedByWater()) {
            createVectorHelper = createVectorHelper.normalize();
            double d = 0.014;
            entity.motionX = entity.motionX + createVectorHelper.xCoord * d;
            entity.motionY = entity.motionY + createVectorHelper.yCoord * d;
            entity.motionZ = entity.motionZ + createVectorHelper.zCoord * d;
        }

        return flag;
    }

    public boolean isMaterialInBB(AxisAlignedBB axisAlignedBB, Material material) {
        int n = MathHelper.floor_double(axisAlignedBB.minX);
        int n2 = MathHelper.floor_double(axisAlignedBB.maxX + 1.0);
        int n3 = MathHelper.floor_double(axisAlignedBB.minY);
        int n4 = MathHelper.floor_double(axisAlignedBB.maxY + 1.0);
        int n5 = MathHelper.floor_double(axisAlignedBB.minZ);
        int n6 = MathHelper.floor_double(axisAlignedBB.maxZ + 1.0);

        for (int i = n; i < n2; i++) {
            for (int j = n3; j < n4; j++) {
                for (int k = n5; k < n6; k++) {
                    if (this.getBlock(i, j, k).getMaterial() == material) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isAABBInMaterial(AxisAlignedBB axisAlignedBB, Material material) {
        int n = MathHelper.floor_double(axisAlignedBB.minX);
        int n2 = MathHelper.floor_double(axisAlignedBB.maxX + 1.0);
        int n3 = MathHelper.floor_double(axisAlignedBB.minY);
        int n4 = MathHelper.floor_double(axisAlignedBB.maxY + 1.0);
        int n5 = MathHelper.floor_double(axisAlignedBB.minZ);
        int n6 = MathHelper.floor_double(axisAlignedBB.maxZ + 1.0);

        for (int i = n; i < n2; i++) {
            for (int j = n3; j < n4; j++) {
                for (int k = n5; k < n6; k++) {
                    Block block = this.getBlock(i, j, k);
                    if (block.getMaterial() == material) {
                        int blockMetadata = this.getBlockMetadata(i, j, k);
                        double d = j + 1;
                        if (blockMetadata < 8) {
                            d = j + 1 - blockMetadata / 8.0;
                        }

                        if (d >= axisAlignedBB.minY) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public Explosion createExplosion(Entity entity, double d, double d2, double d3, float f, boolean flag) {
        return this.newExplosion(entity, d, d2, d3, f, false, flag);
    }

    public Explosion newExplosion(Entity entity, double d, double d2, double d3, float f, boolean flag, boolean flag2) {
        Explosion explosion = new Explosion(this, entity, d, d2, d3, f);
        explosion.isFlaming = flag;
        explosion.isSmoking = flag2;
        explosion.doExplosionA();
        explosion.doExplosionB(true);
        return explosion;
    }

    public float getBlockDensity(Vec3 vec3, AxisAlignedBB axisAlignedBB) {
        double d = 1.0 / ((axisAlignedBB.maxX - axisAlignedBB.minX) * 2.0 + 1.0);
        double d2 = 1.0 / ((axisAlignedBB.maxY - axisAlignedBB.minY) * 2.0 + 1.0);
        double d3 = 1.0 / ((axisAlignedBB.maxZ - axisAlignedBB.minZ) * 2.0 + 1.0);
        if (d >= 0.0 && d2 >= 0.0 && d3 >= 0.0) {
            int n = 0;
            int n2 = 0;

            for (float f = 0.0F; f <= 1.0F; f = (float)(f + d)) {
                for (float f2 = 0.0F; f2 <= 1.0F; f2 = (float)(f2 + d2)) {
                    for (float f3 = 0.0F; f3 <= 1.0F; f3 = (float)(f3 + d3)) {
                        double d4 = axisAlignedBB.minX + (axisAlignedBB.maxX - axisAlignedBB.minX) * f;
                        double d5 = axisAlignedBB.minY + (axisAlignedBB.maxY - axisAlignedBB.minY) * f2;
                        double d6 = axisAlignedBB.minZ + (axisAlignedBB.maxZ - axisAlignedBB.minZ) * f3;
                        if (this.rayTraceBlocks(Vec3.createVectorHelper(d4, d5, d6), vec3) == null) {
                            n++;
                        }

                        n2++;
                    }
                }
            }

            return (float)n / n2;
        } else {
            return 0.0F;
        }
    }

    public boolean extinguishFire(EntityPlayer player, int x, int y, int z, int side) {
        if (side == 0) {
            y--;
        }

        if (side == 1) {
            y++;
        }

        if (side == 2) {
            z--;
        }

        if (side == 3) {
            z++;
        }

        if (side == 4) {
            x--;
        }

        if (side == 5) {
            x++;
        }

        if (this.getBlock(x, y, z) == Blocks.fire) {
            this.playAuxSFXAtEntity(player, 1004, x, y, z, 0);
            this.setBlockToAir(x, y, z);
            return true;
        } else {
            return false;
        }
    }

    public String getDebugLoadedEntities() {
        return "All: " + this.loadedEntityList.size();
    }

    public String getProviderName() {
        return this.chunkProvider.makeString();
    }

    @Override
    public TileEntity getTileEntity(int x, int y, int z) {
        if (y >= 0 && y < 256) {
            TileEntity tileEntity = null;
            if (this.processingLoadedTiles) {
                for (int i = 0; i < this.addedTileEntityList.size(); i++) {
                    TileEntity tileEntity2 = (TileEntity)this.addedTileEntityList.get(i);
                    if (!tileEntity2.isInvalid() && tileEntity2.xCoord == x && tileEntity2.yCoord == y && tileEntity2.zCoord == z) {
                        tileEntity = tileEntity2;
                        break;
                    }
                }
            }

            if (tileEntity == null) {
                Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(x >> 4, z >> 4);
                if (chunkFromChunkCoords != null) {
                    tileEntity = chunkFromChunkCoords.getBlockTileEntityInChunk(x & 15, y, z & 15);
                }
            }

            if (tileEntity == null) {
                for (int i = 0; i < this.addedTileEntityList.size(); i++) {
                    TileEntity tileEntity2 = (TileEntity)this.addedTileEntityList.get(i);
                    if (!tileEntity2.isInvalid() && tileEntity2.xCoord == x && tileEntity2.yCoord == y && tileEntity2.zCoord == z) {
                        tileEntity = tileEntity2;
                        break;
                    }
                }
            }

            return tileEntity;
        } else {
            return null;
        }
    }

    public void setTileEntity(int x, int y, int z, TileEntity tileEntityIn) {
        if (tileEntityIn != null && !tileEntityIn.isInvalid()) {
            if (this.processingLoadedTiles) {
                tileEntityIn.xCoord = x;
                tileEntityIn.yCoord = y;
                tileEntityIn.zCoord = z;
                Iterator iterator = this.addedTileEntityList.iterator();

                while (iterator.hasNext()) {
                    TileEntity tileEntity = (TileEntity)iterator.next();
                    if (tileEntity.xCoord == x && tileEntity.yCoord == y && tileEntity.zCoord == z) {
                        tileEntity.invalidate();
                        iterator.remove();
                    }
                }

                this.addedTileEntityList.add(tileEntityIn);
            } else {
                this.loadedTileEntityList.add(tileEntityIn);
                Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(x >> 4, z >> 4);
                if (chunkFromChunkCoords != null) {
                    chunkFromChunkCoords.setBlockTileEntityInChunk(x & 15, y, z & 15, tileEntityIn);
                }
            }
        }
    }

    public void removeTileEntity(int x, int y, int z) {
        TileEntity tileEntity = this.getTileEntity(x, y, z);
        if (tileEntity != null && this.processingLoadedTiles) {
            tileEntity.invalidate();
            this.addedTileEntityList.remove(tileEntity);
        } else {
            if (tileEntity != null) {
                this.addedTileEntityList.remove(tileEntity);
                this.loadedTileEntityList.remove(tileEntity);
            }

            Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(x >> 4, z >> 4);
            if (chunkFromChunkCoords != null) {
                chunkFromChunkCoords.removeTileEntity(x & 15, y, z & 15);
            }
        }
    }

    public void markTileEntityForRemoval(TileEntity tileEntityIn) {
        this.tileEntitiesToBeRemoved.add(tileEntityIn);
    }

    public boolean isBlockFullCube(int x, int y, int z) {
        AxisAlignedBB collisionBoundingBoxFromPool = this.getBlock(x, y, z).getCollisionBoundingBoxFromPool(this, x, y, z);
        return collisionBoundingBoxFromPool != null && collisionBoundingBoxFromPool.getAverageEdgeLength() >= 1.0;
    }

    public static boolean doesBlockHaveSolidTopSurface(IBlockAccess worldIn, int x, int y, int z) {
        Block block = worldIn.getBlock(x, y, z);
        int blockMetadata = worldIn.getBlockMetadata(x, y, z);
        return block.getMaterial().isOpaque() && block.renderAsNormalBlock()
            || (
                block instanceof BlockStairs
                    ? (blockMetadata & 4) == 4
                    : (block instanceof BlockSlab ? (blockMetadata & 8) == 8 : block instanceof BlockHopper || block instanceof BlockSnow && (blockMetadata & 7) == 7)
            );
    }

    public boolean isBlockNormalCubeDefault(int x, int y, int z, boolean def) {
        if (x >= -30000000 && z >= -30000000 && x < 30000000 && z < 30000000) {
            Chunk provideChunk = this.chunkProvider.provideChunk(x >> 4, z >> 4);
            if (provideChunk != null && !provideChunk.isEmpty()) {
                Block block = this.getBlock(x, y, z);
                return block.getMaterial().isOpaque() && block.renderAsNormalBlock();
            } else {
                return def;
            }
        } else {
            return def;
        }
    }

    public void calculateInitialSkylight() {
        int calculateSkylightSubtracted = this.calculateSkylightSubtracted(1.0F);
        if (calculateSkylightSubtracted != this.skylightSubtracted) {
            this.skylightSubtracted = calculateSkylightSubtracted;
        }
    }

    public void setAllowedSpawnTypes(boolean hostile, boolean peaceful) {
        this.spawnHostileMobs = hostile;
        this.spawnPeacefulMobs = peaceful;
    }

    public void tick() {
        this.updateWeather();
    }

    public void calculateInitialWeather() {
        if (this.worldInfo.isRaining()) {
            this.rainingStrength = 1.0F;
            if (this.worldInfo.isThundering()) {
                this.thunderingStrength = 1.0F;
            }
        }
    }

    public void updateWeather() {
        if (!this.provider.hasNoSky && !this.isRemote) {
            int thunderTime = this.worldInfo.getThunderTime();
            if (thunderTime <= 0) {
                if (this.worldInfo.isThundering()) {
                    this.worldInfo.setThunderTime(this.rand.nextInt(12000) + 3600);
                } else {
                    this.worldInfo.setThunderTime(this.rand.nextInt(168000) + 12000);
                }
            } else {
                this.worldInfo.setThunderTime(--thunderTime);
                if (thunderTime <= 0) {
                    this.worldInfo.setThundering(!this.worldInfo.isThundering());
                }
            }

            this.prevThunderingStrength = this.thunderingStrength;
            if (this.worldInfo.isThundering()) {
                this.thunderingStrength = (float)(this.thunderingStrength + 0.01);
            } else {
                this.thunderingStrength = (float)(this.thunderingStrength - 0.01);
            }

            this.thunderingStrength = MathHelper.clamp_float(this.thunderingStrength, 0.0F, 1.0F);
            int rainTime = this.worldInfo.getRainTime();
            if (rainTime <= 0) {
                if (this.worldInfo.isRaining()) {
                    this.worldInfo.setRainTime(this.rand.nextInt(12000) + 12000);
                } else {
                    this.worldInfo.setRainTime(this.rand.nextInt(168000) + 12000);
                }
            } else {
                this.worldInfo.setRainTime(--rainTime);
                if (rainTime <= 0) {
                    this.worldInfo.setRaining(!this.worldInfo.isRaining());
                }
            }

            this.prevRainingStrength = this.rainingStrength;
            if (this.worldInfo.isRaining()) {
                this.rainingStrength = (float)(this.rainingStrength + 0.01);
            } else {
                this.rainingStrength = (float)(this.rainingStrength - 0.01);
            }

            this.rainingStrength = MathHelper.clamp_float(this.rainingStrength, 0.0F, 1.0F);
        }
    }

    public void setActivePlayerChunksAndCheckLight() {
        this.activeChunkSet.clear();
        this.theProfiler.startSection("buildList");

        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayer entityPlayer = this.playerEntities.get(i);
            int n = MathHelper.floor_double(entityPlayer.posX / 16.0);
            int n2 = MathHelper.floor_double(entityPlayer.posZ / 16.0);
            int renderDistanceChunks = this.getRenderDistanceChunks();

            for (int j = -renderDistanceChunks; j <= renderDistanceChunks; j++) {
                for (int k = -renderDistanceChunks; k <= renderDistanceChunks; k++) {
                    this.activeChunkSet.add(new ChunkCoordIntPair(j + n, k + n2));
                }
            }
        }

        this.theProfiler.endSection();
        if (this.ambientTickCountdown > 0) {
            this.ambientTickCountdown--;
        }

        this.theProfiler.startSection("playerCheckLight");
        if (!this.playerEntities.isEmpty()) {
            int nextInt = this.rand.nextInt(this.playerEntities.size());
            EntityPlayer entityPlayer = this.playerEntities.get(nextInt);
            int n = MathHelper.floor_double(entityPlayer.posX) + this.rand.nextInt(11) - 5;
            int n2 = MathHelper.floor_double(entityPlayer.posY) + this.rand.nextInt(11) - 5;
            int n3 = MathHelper.floor_double(entityPlayer.posZ) + this.rand.nextInt(11) - 5;
            this.updateAllLightTypes(n, n2, n3);
        }

        this.theProfiler.endSection();
    }

    public abstract int getRenderDistanceChunks();

    public void process(int n, int n2, Chunk chunk) {
        this.theProfiler.endStartSection("moodSound");
        if (this.ambientTickCountdown == 0 && !this.isRemote) {
            this.updateLCG = this.updateLCG * 3 + 1013904223;
            int n3 = this.updateLCG >> 2;
            int n4 = n3 & 15;
            int n5 = n3 >> 8 & 15;
            int n6 = n3 >> 16 & 0xFF;
            Block block = chunk.getBlock(n4, n6, n5);
            n4 += n;
            n5 += n2;
            if (block.getMaterial() == Material.air
                && this.getFullBlockLightValue(n4, n6, n5) <= this.rand.nextInt(8)
                && this.getSavedLightValue(EnumSkyBlock.Sky, n4, n6, n5) <= 0) {
                EntityPlayer closestPlayer = this.getClosestPlayer(n4 + 0.5, n6 + 0.5, n5 + 0.5, 8.0);
                if (closestPlayer != null && closestPlayer.getDistanceSq(n4 + 0.5, n6 + 0.5, n5 + 0.5) > 4.0) {
                    this.playSoundEffect(n4 + 0.5, n6 + 0.5, n5 + 0.5, "ambient.cave.cave", 0.7F, 0.8F + this.rand.nextFloat() * 0.2F);
                    this.ambientTickCountdown = this.rand.nextInt(12000) + 6000;
                }
            }
        }

        this.theProfiler.endStartSection("checkLight");
        chunk.enqueueRelightChecks();
    }

    public void run() {
        this.setActivePlayerChunksAndCheckLight();
    }

    public boolean isBlockFreezable(int x, int y, int z) {
        return this.canBlockFreeze(x, y, z, false);
    }

    public boolean isBlockFreezableNaturally(int x, int y, int z) {
        return this.canBlockFreeze(x, y, z, true);
    }

    public boolean canBlockFreeze(int x, int y, int z, boolean byWater) {
        BiomeGenBase biomeGenForCoords = this.getBiomeGenForCoords(x, z);
        float floatTemperature = biomeGenForCoords.getFloatTemperature(x, y, z);
        if (floatTemperature > 0.15F) {
            return false;
        }

        if (y >= 0 && y < 256 && this.getSavedLightValue(EnumSkyBlock.Block, x, y, z) < 10) {
            Block block = this.getBlock(x, y, z);
            if ((block == Blocks.water || block == Blocks.flowing_water) && this.getBlockMetadata(x, y, z) == 0) {
                if (!byWater) {
                    return true;
                }

                boolean flag = true;
                if (flag && this.getBlock(x - 1, y, z).getMaterial() != Material.water) {
                    flag = false;
                }

                if (flag && this.getBlock(x + 1, y, z).getMaterial() != Material.water) {
                    flag = false;
                }

                if (flag && this.getBlock(x, y, z - 1).getMaterial() != Material.water) {
                    flag = false;
                }

                if (flag && this.getBlock(x, y, z + 1).getMaterial() != Material.water) {
                    flag = false;
                }

                return !flag;
            }
        }

        return false;
    }

    public boolean canSnowAt(int x, int y, int z, boolean checkLight) {
        BiomeGenBase biomeGenForCoords = this.getBiomeGenForCoords(x, z);
        float floatTemperature = biomeGenForCoords.getFloatTemperature(x, y, z);
        if (floatTemperature > 0.15F) {
            return false;
        } else if (!checkLight) {
            return true;
        } else if (y >= 0 && y < 256 && this.getSavedLightValue(EnumSkyBlock.Block, x, y, z) < 10) {
            Block block = this.getBlock(x, y, z);
            return block.getMaterial() == Material.air && Blocks.snow_layer.canPlaceBlockAt(this, x, y, z);
        } else {
            return false;
        }
    }

    public boolean updateAllLightTypes(int x, int y, int z) {
        boolean flag = false;
        if (!this.provider.hasNoSky) {
            flag |= this.updateLightByType(EnumSkyBlock.Sky, x, y, z);
        }

        return flag | this.updateLightByType(EnumSkyBlock.Block, x, y, z);
    }

    public int computeLightValue(int x, int y, int z, EnumSkyBlock enumSkyBlock) {
        if (enumSkyBlock == EnumSkyBlock.Sky && this.canBlockSeeTheSky(x, y, z)) {
            return 15;
        }

        Block block = this.getBlock(x, y, z);
        int n = enumSkyBlock == EnumSkyBlock.Sky ? 0 : block.getLightValue();
        int lightOpacity = block.getLightOpacity();
        if (lightOpacity >= 15 && block.getLightValue() > 0) {
            lightOpacity = 1;
        }

        if (lightOpacity < 1) {
            lightOpacity = 1;
        }

        if (lightOpacity >= 15) {
            return 0;
        }

        if (n >= 14) {
            return n;
        }

        for (int i = 0; i < 6; i++) {
            int n2 = x + Facing.offsetsXForSide[i];
            int n3 = y + Facing.offsetsYForSide[i];
            int n4 = z + Facing.offsetsZForSide[i];
            int n5 = this.getSavedLightValue(enumSkyBlock, n2, n3, n4) - lightOpacity;
            if (n5 > n) {
                n = n5;
            }

            if (n >= 14) {
                return n;
            }
        }

        return n;
    }

    public boolean updateLightByType(EnumSkyBlock enumSkyBlock, int n, int n2, int n3) {
        if (!this.doChunksNearChunkExist(n, n2, n3, 17)) {
            return false;
        }

        int n4 = 0;
        int n5 = 0;
        this.theProfiler.startSection("getBrightness");
        int savedLightValue = this.getSavedLightValue(enumSkyBlock, n, n2, n3);
        int computeLightValue = this.computeLightValue(n, n2, n3, enumSkyBlock);
        if (computeLightValue > savedLightValue) {
            this.lightUpdateBlockList[n5++] = 133152;
        } else if (computeLightValue < savedLightValue) {
            this.lightUpdateBlockList[n5++] = 133152 | savedLightValue << 18;

            while (n4 < n5) {
                int n6 = this.lightUpdateBlockList[n4++];
                int n7 = (n6 & 63) - 32 + n;
                int n8 = (n6 >> 6 & 63) - 32 + n2;
                int n9 = (n6 >> 12 & 63) - 32 + n3;
                int n10 = n6 >> 18 & 15;
                int savedLightValue2 = this.getSavedLightValue(enumSkyBlock, n7, n8, n9);
                if (savedLightValue2 == n10) {
                    this.setLightValue(enumSkyBlock, n7, n8, n9, 0);
                    if (n10 > 0) {
                        int n11 = MathHelper.abs_int(n7 - n);
                        int n12 = MathHelper.abs_int(n8 - n2);
                        int n13 = MathHelper.abs_int(n9 - n3);
                        if (n11 + n12 + n13 < 17) {
                            for (int i = 0; i < 6; i++) {
                                int n14 = n7 + Facing.offsetsXForSide[i];
                                int n15 = n8 + Facing.offsetsYForSide[i];
                                int n16 = n9 + Facing.offsetsZForSide[i];
                                int max = Math.max(1, this.getBlock(n14, n15, n16).getLightOpacity());
                                savedLightValue2 = this.getSavedLightValue(enumSkyBlock, n14, n15, n16);
                                if (savedLightValue2 == n10 - max && n5 < this.lightUpdateBlockList.length) {
                                    this.lightUpdateBlockList[n5++] = n14 - n + 32 | n15 - n2 + 32 << 6 | n16 - n3 + 32 << 12 | n10 - max << 18;
                                }
                            }
                        }
                    }
                }
            }

            n4 = 0;
        }

        this.theProfiler.endSection();
        this.theProfiler.startSection("checkedPosition < toCheckCount");

        while (n4 < n5) {
            int n6 = this.lightUpdateBlockList[n4++];
            int n7 = (n6 & 63) - 32 + n;
            int n8 = (n6 >> 6 & 63) - 32 + n2;
            int n9 = (n6 >> 12 & 63) - 32 + n3;
            int savedLightValue2 = this.getSavedLightValue(enumSkyBlock, n7, n8, n9);
            int computeLightValue2 = this.computeLightValue(n7, n8, n9, enumSkyBlock);
            if (computeLightValue2 != savedLightValue2) {
                this.setLightValue(enumSkyBlock, n7, n8, n9, computeLightValue2);
                if (computeLightValue2 > savedLightValue2) {
                    int abs = Math.abs(n7 - n);
                    int abs2 = Math.abs(n8 - n2);
                    int abs3 = Math.abs(n9 - n3);
                    boolean flag = n5 < this.lightUpdateBlockList.length - 6;
                    if (abs + abs2 + abs3 < 17 && flag) {
                        if (this.getSavedLightValue(enumSkyBlock, n7 - 1, n8, n9) < computeLightValue2) {
                            this.lightUpdateBlockList[n5++] = n7 - 1 - n + 32 + (n8 - n2 + 32 << 6) + (n9 - n3 + 32 << 12);
                        }

                        if (this.getSavedLightValue(enumSkyBlock, n7 + 1, n8, n9) < computeLightValue2) {
                            this.lightUpdateBlockList[n5++] = n7 + 1 - n + 32 + (n8 - n2 + 32 << 6) + (n9 - n3 + 32 << 12);
                        }

                        if (this.getSavedLightValue(enumSkyBlock, n7, n8 - 1, n9) < computeLightValue2) {
                            this.lightUpdateBlockList[n5++] = n7 - n + 32 + (n8 - 1 - n2 + 32 << 6) + (n9 - n3 + 32 << 12);
                        }

                        if (this.getSavedLightValue(enumSkyBlock, n7, n8 + 1, n9) < computeLightValue2) {
                            this.lightUpdateBlockList[n5++] = n7 - n + 32 + (n8 + 1 - n2 + 32 << 6) + (n9 - n3 + 32 << 12);
                        }

                        if (this.getSavedLightValue(enumSkyBlock, n7, n8, n9 - 1) < computeLightValue2) {
                            this.lightUpdateBlockList[n5++] = n7 - n + 32 + (n8 - n2 + 32 << 6) + (n9 - 1 - n3 + 32 << 12);
                        }

                        if (this.getSavedLightValue(enumSkyBlock, n7, n8, n9 + 1) < computeLightValue2) {
                            this.lightUpdateBlockList[n5++] = n7 - n + 32 + (n8 - n2 + 32 << 6) + (n9 + 1 - n3 + 32 << 12);
                        }
                    }
                }
            }
        }

        this.theProfiler.endSection();
        return true;
    }

    public boolean tickUpdates(boolean flag) {
        return false;
    }

    public List getPendingBlockUpdates(Chunk chunk, boolean flag) {
        return null;
    }

    public List getEntitiesWithinAABBExcludingEntity(Entity entity, AxisAlignedBB axisAlignedBB) {
        return this.getEntitiesWithinAABBExcludingEntity(entity, axisAlignedBB, null);
    }

    public List getEntitiesWithinAABBExcludingEntity(Entity entity, AxisAlignedBB axisAlignedBB, IEntitySelector entitySelector) {
        ArrayList list = new ArrayList();
        int n = MathHelper.floor_double((axisAlignedBB.minX - 2.0) / 16.0);
        int n2 = MathHelper.floor_double((axisAlignedBB.maxX + 2.0) / 16.0);
        int n3 = MathHelper.floor_double((axisAlignedBB.minZ - 2.0) / 16.0);
        int n4 = MathHelper.floor_double((axisAlignedBB.maxZ + 2.0) / 16.0);

        for (int i = n; i <= n2; i++) {
            for (int j = n3; j <= n4; j++) {
                if (this.chunkExists(i, j)) {
                    this.getChunkFromChunkCoords(i, j).getEntitiesWithinAABBForEntity(entity, axisAlignedBB, list, entitySelector);
                }
            }
        }

        return list;
    }

    public List getEntitiesWithinAABB(Class clazz, AxisAlignedBB axisAlignedBB) {
        return this.selectEntitiesWithinAABB(clazz, axisAlignedBB, null);
    }

    public List selectEntitiesWithinAABB(Class clazz, AxisAlignedBB bb, IEntitySelector selector) {
        int n = MathHelper.floor_double((bb.minX - 2.0) / 16.0);
        int n2 = MathHelper.floor_double((bb.maxX + 2.0) / 16.0);
        int n3 = MathHelper.floor_double((bb.minZ - 2.0) / 16.0);
        int n4 = MathHelper.floor_double((bb.maxZ + 2.0) / 16.0);
        ArrayList list = new ArrayList();

        for (int i = n; i <= n2; i++) {
            for (int j = n3; j <= n4; j++) {
                if (this.chunkExists(i, j)) {
                    this.getChunkFromChunkCoords(i, j).getEntitiesOfTypeWithinAAAB(clazz, bb, list, selector);
                }
            }
        }

        return list;
    }

    public Entity findNearestEntityWithinAABB(Class clazz, AxisAlignedBB axisAlignedBB, Entity entity) {
        List entitiesWithinAABB = this.getEntitiesWithinAABB(clazz, axisAlignedBB);
        Entity entity2 = null;
        double d = Double.MAX_VALUE;

        for (int i = 0; i < entitiesWithinAABB.size(); i++) {
            Entity entity3 = (Entity)entitiesWithinAABB.get(i);
            if (entity3 != entity) {
                double distanceSqToEntity = entity.getDistanceSqToEntity(entity3);
                if (distanceSqToEntity <= d) {
                    entity2 = entity3;
                    d = distanceSqToEntity;
                }
            }
        }

        return entity2;
    }

    public abstract Entity getEntityByID(int n);

    public List getLoadedEntityList() {
        return this.loadedEntityList;
    }

    public void markTileEntityChunkModified(int n, int n2, int n3, TileEntity tileEntity) {
        if (this.blockExists(n, n2, n3)) {
            this.getChunkFromBlockCoords(n, n3).setChunkModified();
        }
    }

    public int countEntities(Class clazz) {
        int n = 0;

        for (int i = 0; i < this.loadedEntityList.size(); i++) {
            Entity entity = (Entity)this.loadedEntityList.get(i);
            if ((!(entity instanceof EntityLiving) || !((EntityLiving)entity).isNoDespawnRequired()) && clazz.isAssignableFrom(entity.getClass())) {
                n++;
            }
        }

        return n;
    }

    public void addLoadedEntities(List list) {
        this.loadedEntityList.addAll(list);

        for (int i = 0; i < list.size(); i++) {
            this.onEntityAdded((Entity)list.get(i));
        }
    }

    public void unloadEntities(List list) {
        this.unloadedEntityList.addAll(list);
    }

    public boolean canPlaceEntityOnSide(Block block, int n, int n2, int n3, boolean flag, int n4, Entity entity, ItemStack itemStack) {
        Block block2 = this.getBlock(n, n2, n3);
        AxisAlignedBB axisAlignedBB = flag ? null : block.getCollisionBoundingBoxFromPool(this, n, n2, n3);
        return (axisAlignedBB == null || this.checkNoEntityCollision(axisAlignedBB, entity))
            && (
                block2.getMaterial() == Material.circuits && block == Blocks.anvil
                    || block2.getMaterial().isReplaceable() && block.canReplace(this, n, n2, n3, n4, itemStack)
            );
    }

    public PathEntity getPathEntityToEntity(Entity entity, Entity entity2, float f, boolean flag, boolean flag2, boolean flag3, boolean flag4) {
        this.theProfiler.startSection("pathfind");
        int n = MathHelper.floor_double(entity.posX);
        int n2 = MathHelper.floor_double(entity.posY + 1.0);
        int n3 = MathHelper.floor_double(entity.posZ);
        int n4 = (int)(f + 16.0F);
        int n5 = n - n4;
        int n6 = n2 - n4;
        int n7 = n3 - n4;
        int n8 = n + n4;
        int n9 = n2 + n4;
        int n10 = n3 + n4;
        ChunkCache chunkCache = new ChunkCache(this, n5, n6, n7, n8, n9, n10, 0);
        PathEntity createEntityPathTo = new PathFinder(chunkCache, flag, flag2, flag3, flag4).createEntityPathTo(entity, entity2, f);
        this.theProfiler.endSection();
        return createEntityPathTo;
    }

    public PathEntity getEntityPathToXYZ(Entity entity, int n, int n2, int n3, float f, boolean flag, boolean flag2, boolean flag3, boolean flag4) {
        this.theProfiler.startSection("pathfind");
        int n4 = MathHelper.floor_double(entity.posX);
        int n5 = MathHelper.floor_double(entity.posY);
        int n6 = MathHelper.floor_double(entity.posZ);
        int n7 = (int)(f + 8.0F);
        int n8 = n4 - n7;
        int n9 = n5 - n7;
        int n10 = n6 - n7;
        int n11 = n4 + n7;
        int n12 = n5 + n7;
        int n13 = n6 + n7;
        ChunkCache chunkCache = new ChunkCache(this, n8, n9, n10, n11, n12, n13, 0);
        PathEntity createEntityPathTo = new PathFinder(chunkCache, flag, flag2, flag3, flag4).createEntityPathTo(entity, n, n2, n3, f);
        this.theProfiler.endSection();
        return createEntityPathTo;
    }

    @Override
    public int isBlockProvidingPowerTo(int x, int y, int z, int directionIn) {
        return this.getBlock(x, y, z).isProvidingStrongPower(this, x, y, z, directionIn);
    }

    public int getBlockPowerInput(int x, int y, int z) {
        byte b = 0;
        int max = Math.max(b, this.isBlockProvidingPowerTo(x, y - 1, z, 0));
        if (max >= 15) {
            return max;
        }

        max = Math.max(max, this.isBlockProvidingPowerTo(x, y + 1, z, 1));
        if (max >= 15) {
            return max;
        }

        max = Math.max(max, this.isBlockProvidingPowerTo(x, y, z - 1, 2));
        if (max >= 15) {
            return max;
        }

        max = Math.max(max, this.isBlockProvidingPowerTo(x, y, z + 1, 3));
        if (max >= 15) {
            return max;
        }

        max = Math.max(max, this.isBlockProvidingPowerTo(x - 1, y, z, 4));
        return max >= 15 ? max : Math.max(max, this.isBlockProvidingPowerTo(x + 1, y, z, 5));
    }

    public boolean getIndirectPowerOutput(int x, int y, int z, int directionIn) {
        return this.getIndirectPowerLevelTo(x, y, z, directionIn) > 0;
    }

    public int getIndirectPowerLevelTo(int x, int y, int z, int directionIn) {
        return this.getBlock(x, y, z).isNormalCube() ? this.getBlockPowerInput(x, y, z) : this.getBlock(x, y, z).isProvidingWeakPower(this, x, y, z, directionIn);
    }

    public boolean isBlockIndirectlyGettingPowered(int x, int y, int z) {
        return this.getIndirectPowerLevelTo(x, y - 1, z, 0) > 0
            || this.getIndirectPowerLevelTo(x, y + 1, z, 1) > 0
            || this.getIndirectPowerLevelTo(x, y, z - 1, 2) > 0
            || this.getIndirectPowerLevelTo(x, y, z + 1, 3) > 0
            || this.getIndirectPowerLevelTo(x - 1, y, z, 4) > 0
            || this.getIndirectPowerLevelTo(x + 1, y, z, 5) > 0;
    }

    public int getStrongestIndirectPower(int x, int y, int z) {
        int n = 0;

        for (int i = 0; i < 6; i++) {
            int indirectPowerLevelTo = this.getIndirectPowerLevelTo(x + Facing.offsetsXForSide[i], y + Facing.offsetsYForSide[i], z + Facing.offsetsZForSide[i], i);
            if (indirectPowerLevelTo >= 15) {
                return 15;
            }

            if (indirectPowerLevelTo > n) {
                n = indirectPowerLevelTo;
            }
        }

        return n;
    }

    public EntityPlayer getClosestPlayerToEntity(Entity entityIn, double distance) {
        return this.getClosestPlayer(entityIn.posX, entityIn.posY, entityIn.posZ, distance);
    }

    public EntityPlayer getClosestPlayer(double x, double y, double z, double distance) {
        double d = -1.0;
        EntityPlayer entityPlayer = null;

        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayer entityPlayer2 = this.playerEntities.get(i);
            double distanceSq = entityPlayer2.getDistanceSq(x, y, z);
            if ((distance < 0.0 || distanceSq < distance * distance) && (d == -1.0 || distanceSq < d)) {
                d = distanceSq;
                entityPlayer = entityPlayer2;
            }
        }

        return entityPlayer;
    }

    public EntityPlayer getClosestVulnerablePlayerToEntity(Entity entityIn, double distance) {
        return this.getClosestVulnerablePlayer(entityIn.posX, entityIn.posY, entityIn.posZ, distance);
    }

    public EntityPlayer getClosestVulnerablePlayer(double d, double d2, double d3, double d4) {
        double d5 = -1.0;
        EntityPlayer entityPlayer = null;

        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayer entityPlayer2 = this.playerEntities.get(i);
            if (!entityPlayer2.capabilities.disableDamage && entityPlayer2.isEntityAlive()) {
                double distanceSq = entityPlayer2.getDistanceSq(d, d2, d3);
                double d6 = d4;
                if (entityPlayer2.isSneaking()) {
                    d6 = d4 * 0.8F;
                }

                if (entityPlayer2.isInvisible()) {
                    float armorVisibility = entityPlayer2.getArmorVisibility();
                    if (armorVisibility < 0.1F) {
                        armorVisibility = 0.1F;
                    }

                    d6 *= 0.7F * armorVisibility;
                }

                if ((d4 < 0.0 || distanceSq < d6 * d6) && (d5 == -1.0 || distanceSq < d5)) {
                    d5 = distanceSq;
                    entityPlayer = entityPlayer2;
                }
            }
        }

        return entityPlayer;
    }

    public EntityPlayer getPlayerEntityByName(String name) {
        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayer entityPlayer = this.playerEntities.get(i);
            if (name.equals(entityPlayer.getCommandSenderName())) {
                return entityPlayer;
            }
        }

        return null;
    }

    public EntityPlayer getPlayerEntityByUUID(UUID uuid) {
        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayer entityPlayer = this.playerEntities.get(i);
            if (uuid.equals(entityPlayer.getUniqueID())) {
                return entityPlayer;
            }
        }

        return null;
    }

    public void sendQuittingDisconnectingPacket() {
    }

    public void checkSessionLock() throws MinecraftException {
        this.saveHandler.checkSessionLock();
    }

    public void setTime(long l) {
        this.worldInfo.incrementTotalWorldTime(l);
    }

    public long getSeed() {
        return this.worldInfo.getSeed();
    }

    public long getTotalWorldTime() {
        return this.worldInfo.getWorldTotalTime();
    }

    public long getWorldTime() {
        return this.worldInfo.getWorldTime();
    }

    public void setWorldTime(long time) {
        this.worldInfo.setWorldTime(time);
    }

    public ChunkCoordinates getSpawnPoint() {
        return new ChunkCoordinates(this.worldInfo.getSpawnX(), this.worldInfo.getSpawnY(), this.worldInfo.getSpawnZ());
    }

    public void setSpawnLocation(int n, int n2, int n3) {
        this.worldInfo.setSpawnPosition(n, n2, n3);
    }

    public void joinEntityInSurroundings(Entity entityIn) {
        int n = MathHelper.floor_double(entityIn.posX / 16.0);
        int n2 = MathHelper.floor_double(entityIn.posZ / 16.0);
        byte b = 2;

        for (int i = n - b; i <= n + b; i++) {
            for (int j = n2 - b; j <= n2 + b; j++) {
                this.getChunkFromChunkCoords(i, j);
            }
        }

        if (!this.loadedEntityList.contains(entityIn)) {
            this.loadedEntityList.add(entityIn);
        }
    }

    public boolean canMineBlock(EntityPlayer player, int x, int y, int z) {
        return true;
    }

    public void setEntityState(Entity entityIn, byte b) {
    }

    public IChunkProvider getChunkProvider() {
        return this.chunkProvider;
    }

    public void addBlockEvent(int x, int y, int z, Block blockIn, int eventId, int eventParameter) {
        blockIn.onBlockEventReceived(this, x, y, z, eventId, eventParameter);
    }

    public ISaveHandler getSaveHandler() {
        return this.saveHandler;
    }

    public WorldInfo getWorldInfo() {
        return this.worldInfo;
    }

    public GameRules getGameRules() {
        return this.worldInfo.getGameRulesInstance();
    }

    public void updateAllPlayersSleepingFlag() {
    }

    public float getWeightedThunderStrength(float f) {
        return (this.prevThunderingStrength + (this.thunderingStrength - this.prevThunderingStrength) * f) * this.getRainStrength(f);
    }

    public void setThunderStrength(float f) {
        this.prevThunderingStrength = f;
        this.thunderingStrength = f;
    }

    public float getRainStrength(float f) {
        return this.prevRainingStrength + (this.rainingStrength - this.prevRainingStrength) * f;
    }

    public void setRainStrength(float strength) {
        this.prevRainingStrength = strength;
        this.rainingStrength = strength;
    }

    public boolean isThundering() {
        return this.getWeightedThunderStrength(1.0F) > 0.9;
    }

    public boolean isRaining() {
        return this.getRainStrength(1.0F) > 0.2;
    }

    public boolean isRainingAt(int x, int y, int z) {
        if (!this.isRaining()) {
            return false;
        }

        if (!this.canBlockSeeTheSky(x, y, z)) {
            return false;
        }

        if (this.getPrecipitationHeight(x, z) > y) {
            return false;
        }

        BiomeGenBase biomeGenForCoords = this.getBiomeGenForCoords(x, z);
        return !biomeGenForCoords.getEnableSnow() && !this.canSnowAt(x, y, z, false) && biomeGenForCoords.canSpawnLightningBolt();
    }

    public boolean isBlockHighHumidity(int x, int y, int z) {
        BiomeGenBase biomeGenForCoords = this.getBiomeGenForCoords(x, z);
        return biomeGenForCoords.isHighHumidity();
    }

    public void setItemData(String str, WorldSavedData worldSavedData) {
        this.mapStorage.setData(str, worldSavedData);
    }

    public WorldSavedData loadItemData(Class clazz, String str) {
        return this.mapStorage.loadData(clazz, str);
    }

    public int getUniqueDataId(String str) {
        return this.mapStorage.getUniqueDataId(str);
    }

    public void playBroadcastSound(int n, int n2, int n3, int n4, int n5) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            ((IWorldAccess)this.worldAccesses.get(i)).broadcastSound(n, n2, n3, n4, n5);
        }
    }

    public void playAuxSFX(int n, int x, int y, int z, int n2) {
        this.playAuxSFXAtEntity(null, n, x, y, z, n2);
    }

    public void playAuxSFXAtEntity(EntityPlayer player, int n, int x, int y, int z, int n2) {
        try {
            for (int i = 0; i < this.worldAccesses.size(); i++) {
                ((IWorldAccess)this.worldAccesses.get(i)).playAuxSFX(player, n, x, y, z, n2);
            }
        } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Playing level event");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Level event being played");
            makeCategory.addCrashSection("Block coordinates", CrashReportCategory.getLocationInfo(x, y, z));
            makeCategory.addCrashSection("Event source", player);
            makeCategory.addCrashSection("Event type", n);
            makeCategory.addCrashSection("Event data", n2);
            throw new ReportedException(makeCrashReport);
        }
    }

    @Override
    public int getHeight() {
        return 256;
    }

    public int getActualHeight() {
        return this.provider.hasNoSky ? 128 : 256;
    }

    public Random setRandomSeed(int n, int n2, int n3) {
        long l = n * 341873128712L + n2 * 132897987541L + this.getWorldInfo().getSeed() + n3;
        this.rand.setSeed(l);
        return this.rand;
    }

    public ChunkPosition findClosestStructure(String type, int x, int y, int z) {
        return this.getChunkProvider().findClosestStructure(this, type, x, y, z);
    }

    @Override
    public boolean extendedLevelsInChunkCache() {
        return false;
    }

    public double getHorizon() {
        DoubleEvent1 doubleEvent1 = new DoubleEvent1(63.0);
        CheatBreaker.getInstance().getEventBus().handleEvent(doubleEvent1);
        return this.worldInfo.getTerrainType() == WorldType.FLAT ? 0.0 : doubleEvent1.getDoubleValue();
    }

    public CrashReportCategory addWorldInfoToCrashReport(CrashReport report) {
        CrashReportCategory makeCategoryDepth = report.makeCategoryDepth("Affected level", 1);
        makeCategoryDepth.addCrashSection("Level name", this.worldInfo == null ? "????" : this.worldInfo.getWorldName());
        makeCategoryDepth.addCrashSectionCallable("All players", new Callable() {
            public String call() {
                return World.this.playerEntities.size() + " total; " + World.this.playerEntities;
            }
        });
        makeCategoryDepth.addCrashSectionCallable("Chunk stats", new Callable() {
            public String call() {
                return World.this.chunkProvider.makeString();
            }
        });

        try {
            this.worldInfo.addToCrashReport(makeCategoryDepth);
        } catch (Throwable e) {
            makeCategoryDepth.addCrashSectionThrowable("Level Data Unobtainable", e);
        }

        return makeCategoryDepth;
    }

    public void destroyBlockInWorldPartially(int n, int x, int y, int z, int blockDamage) {
        for (int i = 0; i < this.worldAccesses.size(); i++) {
            IWorldAccess worldAccess = (IWorldAccess)this.worldAccesses.get(i);
            worldAccess.destroyBlockPartially(n, x, y, z, blockDamage);
        }
    }

    public Calendar getCurrentDate() {
        if (this.getTotalWorldTime() % 600L == 0L) {
            this.theCalendar.setTimeInMillis(MinecraftServer.getCurrentTimeMillis());
        }

        return this.theCalendar;
    }

    public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, NBTTagCompound compund) {
    }

    public Scoreboard getScoreboard() {
        return this.worldScoreboard;
    }

    public void updateNeighborsAboutBlockChange(int x, int yPos, int z, Block blockIn) {
        for (int i = 0; i < 4; i++) {
            int n = x + Direction.offsetX[i];
            int n2 = z + Direction.offsetZ[i];
            Block block = this.getBlock(n, yPos, n2);
            if (Blocks.unpowered_comparator.isComparator(block)) {
                block.onNeighborBlockChange(this, n, yPos, n2, blockIn);
            } else if (block.isNormalCube()) {
                n += Direction.offsetX[i];
                n2 += Direction.offsetZ[i];
                Block block2 = this.getBlock(n, yPos, n2);
                if (Blocks.unpowered_comparator.isComparator(block2)) {
                    block2.onNeighborBlockChange(this, n, yPos, n2, blockIn);
                }
            }
        }
    }

    public float getTensionFactorForBlock(double x, double y, double z) {
        return this.getLocalDifficulty(MathHelper.floor_double(x), MathHelper.floor_double(y), MathHelper.floor_double(z));
    }

    public float getLocalDifficulty(int x, int y, int z) {
        float f = 0.0F;
        boolean flag = this.difficultySetting == EnumDifficulty.HARD;
        if (this.blockExists(x, y, z)) {
            float currentMoonPhaseFactor = this.getCurrentMoonPhaseFactor();
            f += MathHelper.clamp_float((float)this.getChunkFromBlockCoords(x, z).inhabitedTime / 3600000.0F, 0.0F, 1.0F) * (flag ? 1.0F : 0.75F);
            f += currentMoonPhaseFactor * 0.25F;
        }

        if (this.difficultySetting == EnumDifficulty.EASY || this.difficultySetting == EnumDifficulty.PEACEFUL) {
            f *= this.difficultySetting.getDifficultyId() / 2.0F;
        }

        return MathHelper.clamp_float(f, 0.0F, flag ? 1.5F : 1.0F);
    }

    public void run2() {
        for (IWorldAccess worldAccess : (Iterable<IWorldAccess>)this.worldAccesses) {
            worldAccess.onStaticEntitiesChanged();
        }
    }

    public EntityPlayer findEntityPlayer(String str) {
        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayer entityPlayer = this.playerEntities.get(i);
            if (entityPlayer.isEntityAlive()
                && (str.equals(entityPlayer.getGameProfile().getId().toString()) || str.equals(entityPlayer.getGameProfile().getId().toString().replaceAll("-", "")))) {
                return entityPlayer;
            }
        }

        return null;
    }
}
