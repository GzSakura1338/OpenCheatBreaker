package net.minecraft.world;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.material.Material;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityTracker;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.INpc;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.ScoreboardSaveData;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Vec3;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.feature.WorldGeneratorBonusChest;
import net.minecraft.world.storage.ISaveHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldServer extends World {
    public static Logger logger = LogManager.getLogger();
    public SpawnerAnimals animalSpawner = new SpawnerAnimals();
    public WorldServer.ServerBlockEventList[] serverBlockEventListArray = new WorldServer.ServerBlockEventList[]{
        new WorldServer.ServerBlockEventList(null), new WorldServer.ServerBlockEventList(null)
    };
    public List pendingTickListEntriesThisTick = new ArrayList();
    public MinecraftServer mcServer;
    public EntityTracker theEntityTracker;
    public ChunkProviderServer theChunkProviderServer;
    public boolean disableLevelSaving;
    public boolean allPlayersSleeping;
    public int updateEntityTick;
    public PlayerManager thePlayerManager;
    public IntHashMap entityIdMap;
    public Set pendingTickListEntriesHashSet;
    public int blockEventCacheIndex;
    public static WeightedRandomChestContent[] bonusChestContent = new WeightedRandomChestContent[]{
        new WeightedRandomChestContent(Items.stick, 0, 1, 3, 10),
        new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.planks), 0, 1, 3, 10),
        new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.log), 0, 1, 3, 10),
        new WeightedRandomChestContent(Items.stone_axe, 0, 1, 1, 3),
        new WeightedRandomChestContent(Items.wooden_axe, 0, 1, 1, 5),
        new WeightedRandomChestContent(Items.stone_pickaxe, 0, 1, 1, 3),
        new WeightedRandomChestContent(Items.wooden_pickaxe, 0, 1, 1, 5),
        new WeightedRandomChestContent(Items.apple, 0, 2, 3, 5),
        new WeightedRandomChestContent(Items.bread, 0, 2, 3, 3),
        new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.log2), 0, 1, 3, 10)
    };
    public TreeSet pendingTickListEntriesTreeSet;
    public Teleporter worldTeleporter;

    public WorldServer(MinecraftServer minecraftServer, ISaveHandler saveHandler, String str, int n, WorldSettings worldSettings, Profiler profiler) {
        super(saveHandler, str, worldSettings, WorldProvider.getProviderForDimension(n), profiler);
        this.mcServer = minecraftServer;
        this.theEntityTracker = new EntityTracker(this);
        this.thePlayerManager = new PlayerManager(this);
        if (this.entityIdMap == null) {
            this.entityIdMap = new IntHashMap();
        }

        if (this.pendingTickListEntriesHashSet == null) {
            this.pendingTickListEntriesHashSet = new HashSet();
        }

        if (this.pendingTickListEntriesTreeSet == null) {
            this.pendingTickListEntriesTreeSet = new TreeSet();
        }

        this.worldTeleporter = new Teleporter(this);
        this.worldScoreboard = new ServerScoreboard(minecraftServer);
        ScoreboardSaveData loadData = (ScoreboardSaveData)this.mapStorage.loadData(ScoreboardSaveData.class, "scoreboard");
        if (loadData == null) {
            loadData = new ScoreboardSaveData();
            this.mapStorage.setData("scoreboard", loadData);
        }

        loadData.setScoreboard(this.worldScoreboard);
        ((ServerScoreboard)this.worldScoreboard).setScoreboardState(loadData);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getWorldInfo().isHardcoreModeEnabled() && this.difficultySetting != EnumDifficulty.HARD) {
            this.difficultySetting = EnumDifficulty.HARD;
        }

        this.provider.worldChunkMgr.cleanupCache();
        if (this.areAllPlayersAsleep()) {
            if (this.getGameRules().getGameRuleBooleanValue("doDaylightCycle")) {
                long l = this.worldInfo.getWorldTime() + 24000L;
                this.worldInfo.setWorldTime(l - l % 24000L);
            }

            this.wakeAllPlayers();
        }

        this.theProfiler.startSection("mobSpawner");
        if (this.getGameRules().getGameRuleBooleanValue("doMobSpawning")) {
            this.animalSpawner.findChunksForSpawning(this, this.spawnHostileMobs, this.spawnPeacefulMobs, this.worldInfo.getWorldTotalTime() % 400L == 0L);
        }

        this.theProfiler.endStartSection("chunkSource");
        this.chunkProvider.unloadQueuedChunks();
        int calculateSkylightSubtracted = this.calculateSkylightSubtracted(1.0F);
        if (calculateSkylightSubtracted != this.skylightSubtracted) {
            this.skylightSubtracted = calculateSkylightSubtracted;
        }

        this.worldInfo.incrementTotalWorldTime(this.worldInfo.getWorldTotalTime() + 1L);
        if (this.getGameRules().getGameRuleBooleanValue("doDaylightCycle")) {
            this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
        }

        this.theProfiler.endStartSection("tickPending");
        this.tickUpdates(false);
        this.theProfiler.endStartSection("tickBlocks");
        this.run();
        this.theProfiler.endStartSection("chunkMap");
        this.thePlayerManager.updatePlayerInstances();
        this.theProfiler.endStartSection("village");
        this.villageCollectionObj.tick();
        this.villageSiegeObj.tick();
        this.theProfiler.endStartSection("portalForcer");
        this.worldTeleporter.removeStalePortalLocations(this.getTotalWorldTime());
        this.theProfiler.endSection();
        this.run22();
    }

    public SpawnListEntry spawnRandomCreature(EnumCreatureType enumCreatureType, int n, int n2, int n3) {
        List possibleCreatures = this.getChunkProvider().getPossibleCreatures(enumCreatureType, n, n2, n3);
        return possibleCreatures != null && !possibleCreatures.isEmpty() ? (SpawnListEntry)WeightedRandom.getRandomItem(this.rand, possibleCreatures) : null;
    }

    @Override
    public void updateAllPlayersSleepingFlag() {
        this.allPlayersSleeping = !this.playerEntities.isEmpty();

        for (EntityPlayer entityPlayer : this.playerEntities) {
            if (!entityPlayer.isPlayerSleeping()) {
                this.allPlayersSleeping = false;
                break;
            }
        }
    }

    public void wakeAllPlayers() {
        this.allPlayersSleeping = false;

        for (EntityPlayer entityPlayer : this.playerEntities) {
            if (entityPlayer.isPlayerSleeping()) {
                entityPlayer.wakeUpPlayer(false, false, true);
            }
        }

        this.resetRainAndThunder();
    }

    public void resetRainAndThunder() {
        this.worldInfo.setRainTime(0);
        this.worldInfo.setRaining(false);
        this.worldInfo.setThunderTime(0);
        this.worldInfo.setThundering(false);
    }

    public boolean areAllPlayersAsleep() {
        if (this.allPlayersSleeping && !this.isRemote) {
            for (EntityPlayer entityPlayer : this.playerEntities) {
                if (!entityPlayer.isPlayerFullyAsleep()) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setSpawnLocation() {
        if (this.worldInfo.getSpawnY() <= 0) {
            this.worldInfo.setSpawnY(64);
        }

        int spawnX = this.worldInfo.getSpawnX();
        int spawnZ = this.worldInfo.getSpawnZ();
        int n = 0;

        while (this.getTopBlock(spawnX, spawnZ).getMaterial() == Material.air) {
            spawnX += this.rand.nextInt(8) - this.rand.nextInt(8);
            spawnZ += this.rand.nextInt(8) - this.rand.nextInt(8);
            if (++n == 10000) {
                break;
            }
        }

        this.worldInfo.setSpawnX(spawnX);
        this.worldInfo.setSpawnZ(spawnZ);
    }

    @Override
    public void run() {
        super.run();
        int n = 0;
        int n2 = 0;

        for (ChunkCoordIntPair chunkCoordIntPair : (Iterable<ChunkCoordIntPair>)this.activeChunkSet) {
            int n3 = chunkCoordIntPair.chunkXPos * 16;
            int n4 = chunkCoordIntPair.chunkZPos * 16;
            this.theProfiler.startSection("getChunk");
            Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos);
            this.process(n3, n4, chunkFromChunkCoords);
            this.theProfiler.endStartSection("tickChunk");
            chunkFromChunkCoords.populateBlockEntities(false);
            this.theProfiler.endStartSection("thunder");
            if (this.rand.nextInt(100000) == 0 && this.isRaining() && this.isThundering()) {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                int n5 = this.updateLCG >> 2;
                int n6 = n3 + (n5 & 15);
                int n7 = n4 + (n5 >> 8 & 15);
                int precipitationHeight = this.getPrecipitationHeight(n6, n7);
                if (this.isRainingAt(n6, precipitationHeight, n7)) {
                    this.addWeatherEffect(new EntityLightningBolt(this, n6, precipitationHeight, n7));
                }
            }

            this.theProfiler.endStartSection("iceandsnow");
            if (this.rand.nextInt(16) == 0) {
                this.updateLCG = this.updateLCG * 3 + 1013904223;
                int n5 = this.updateLCG >> 2;
                int n6 = n5 & 15;
                int n7 = n5 >> 8 & 15;
                int precipitationHeight = this.getPrecipitationHeight(n6 + n3, n7 + n4);
                if (this.isBlockFreezableNaturally(n6 + n3, precipitationHeight - 1, n7 + n4)) {
                    this.setBlock(n6 + n3, precipitationHeight - 1, n7 + n4, Blocks.ice);
                }

                if (this.isRaining() && this.canSnowAt(n6 + n3, precipitationHeight, n7 + n4, true)) {
                    this.setBlock(n6 + n3, precipitationHeight, n7 + n4, Blocks.snow_layer);
                }

                if (this.isRaining()) {
                    BiomeGenBase biomeGenForCoords = this.getBiomeGenForCoords(n6 + n3, n7 + n4);
                    if (biomeGenForCoords.canSpawnLightningBolt()) {
                        this.getBlock(n6 + n3, precipitationHeight - 1, n7 + n4).fillWithRain(this, n6 + n3, precipitationHeight - 1, n7 + n4);
                    }
                }
            }

            this.theProfiler.endStartSection("tickBlocks");

            for (ExtendedBlockStorage extendedBlockStorage : chunkFromChunkCoords.getBlockStorageArray()) {
                if (extendedBlockStorage != null && extendedBlockStorage.getNeedsRandomTick()) {
                    for (int i = 0; i < 3; i++) {
                        this.updateLCG = this.updateLCG * 3 + 1013904223;
                        int n5 = this.updateLCG >> 2;
                        int n6 = n5 & 15;
                        int n7 = n5 >> 8 & 15;
                        int n8 = n5 >> 16 & 15;
                        n2++;
                        Block blockByExtId = extendedBlockStorage.getBlockByExtId(n6, n8, n7);
                        if (blockByExtId.getTickRandomly()) {
                            n++;
                            blockByExtId.updateTick(this, n6 + n3, n8 + extendedBlockStorage.getYLocation(), n7 + n4, this.rand);
                        }
                    }
                }
            }

            this.theProfiler.endSection();
        }
    }

    @Override
    public boolean isBlockTickScheduledThisTick(int n, int n2, int n3, Block block) {
        NextTickListEntry nextTickListEntry = new NextTickListEntry(n, n2, n3, block);
        return this.pendingTickListEntriesThisTick.contains(nextTickListEntry);
    }

    @Override
    public void scheduleBlockUpdate(int n, int n2, int n3, Block block, int n4) {
        this.scheduleBlockUpdateWithPriority(n, n2, n3, block, n4, 0);
    }

    @Override
    public void scheduleBlockUpdateWithPriority(int n, int n2, int n3, Block block, int n4, int n5) {
        NextTickListEntry nextTickListEntry = new NextTickListEntry(n, n2, n3, block);
        byte b = 0;
        if (this.scheduledUpdatesAreImmediate && block.getMaterial() != Material.air) {
            if (block.requiresUpdates()) {
                b = 8;
                if (this.checkChunksExist(nextTickListEntry.xCoord - b, nextTickListEntry.yCoord - b, nextTickListEntry.zCoord - b, nextTickListEntry.xCoord + b, nextTickListEntry.yCoord + b, nextTickListEntry.zCoord + b)) {
                    Block block2 = this.getBlock(nextTickListEntry.xCoord, nextTickListEntry.yCoord, nextTickListEntry.zCoord);
                    if (block2.getMaterial() != Material.air && block2 == nextTickListEntry.getBlock()) {
                        block2.updateTick(this, nextTickListEntry.xCoord, nextTickListEntry.yCoord, nextTickListEntry.zCoord, this.rand);
                    }
                }

                return;
            }

            n4 = 1;
        }

        if (this.checkChunksExist(n - b, n2 - b, n3 - b, n + b, n2 + b, n3 + b)) {
            if (block.getMaterial() != Material.air) {
                nextTickListEntry.setScheduledTime(n4 + this.worldInfo.getWorldTotalTime());
                nextTickListEntry.setPriority(n5);
            }

            if (!this.pendingTickListEntriesHashSet.contains(nextTickListEntry)) {
                this.pendingTickListEntriesHashSet.add(nextTickListEntry);
                this.pendingTickListEntriesTreeSet.add(nextTickListEntry);
            }
        }
    }

    @Override
    public void process(int n, int n2, int n3, Block block, int n4, int n5) {
        NextTickListEntry nextTickListEntry = new NextTickListEntry(n, n2, n3, block);
        nextTickListEntry.setPriority(n5);
        if (block.getMaterial() != Material.air) {
            nextTickListEntry.setScheduledTime(n4 + this.worldInfo.getWorldTotalTime());
        }

        if (!this.pendingTickListEntriesHashSet.contains(nextTickListEntry)) {
            this.pendingTickListEntriesHashSet.add(nextTickListEntry);
            this.pendingTickListEntriesTreeSet.add(nextTickListEntry);
        }
    }

    @Override
    public void updateEntities() {
        if (this.playerEntities.isEmpty()) {
            if (this.updateEntityTick++ >= 1200) {
                return;
            }
        } else {
            this.resetUpdateEntityTick();
        }

        super.updateEntities();
    }

    public void resetUpdateEntityTick() {
        this.updateEntityTick = 0;
    }

    @Override
    public boolean tickUpdates(boolean flag) {
        int size = this.pendingTickListEntriesTreeSet.size();
        if (size != this.pendingTickListEntriesHashSet.size()) {
            throw new IllegalStateException("TickNextTick list out of synch");
        }

        if (size > 1000) {
            size = 1000;
        }

        this.theProfiler.startSection("cleaning");

        for (int i = 0; i < size; i++) {
            NextTickListEntry first = (NextTickListEntry)this.pendingTickListEntriesTreeSet.first();
            if (!flag && first.scheduledTime > this.worldInfo.getWorldTotalTime()) {
                break;
            }

            this.pendingTickListEntriesTreeSet.remove(first);
            this.pendingTickListEntriesHashSet.remove(first);
            this.pendingTickListEntriesThisTick.add(first);
        }

        this.theProfiler.endSection();
        this.theProfiler.startSection("ticking");
        Iterator iterator = this.pendingTickListEntriesThisTick.iterator();

        while (iterator.hasNext()) {
            NextTickListEntry nextTickListEntry = (NextTickListEntry)iterator.next();
            iterator.remove();
            byte b = 0;
            if (this.checkChunksExist(nextTickListEntry.xCoord - b, nextTickListEntry.yCoord - b, nextTickListEntry.zCoord - b, nextTickListEntry.xCoord + b, nextTickListEntry.yCoord + b, nextTickListEntry.zCoord + b)
                )
             {
                Block block = this.getBlock(nextTickListEntry.xCoord, nextTickListEntry.yCoord, nextTickListEntry.zCoord);
                if (block.getMaterial() != Material.air && Block.isEqualTo(block, nextTickListEntry.getBlock())) {
                    try {
                        block.updateTick(this, nextTickListEntry.xCoord, nextTickListEntry.yCoord, nextTickListEntry.zCoord, this.rand);
                    } catch (Throwable e) {
                        CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Exception while ticking a block");
                        CrashReportCategory makeCategory = makeCrashReport.makeCategory("Block being ticked");

                        int n;
                        try {
                            n = this.getBlockMetadata(nextTickListEntry.xCoord, nextTickListEntry.yCoord, nextTickListEntry.zCoord);
                        } catch (Throwable e2) {
                            n = -1;
                        }

                        CrashReportCategory.addBlockInfo(makeCategory, nextTickListEntry.xCoord, nextTickListEntry.yCoord, nextTickListEntry.zCoord, block, n);
                        throw new ReportedException(makeCrashReport);
                    }
                }
            } else {
                this.scheduleBlockUpdate(nextTickListEntry.xCoord, nextTickListEntry.yCoord, nextTickListEntry.zCoord, nextTickListEntry.getBlock(), 0);
            }
        }

        this.theProfiler.endSection();
        this.pendingTickListEntriesThisTick.clear();
        return !this.pendingTickListEntriesTreeSet.isEmpty();
    }

    @Override
    public List getPendingBlockUpdates(Chunk chunk, boolean flag) {
        ArrayList list = null;
        ChunkCoordIntPair chunkCoordIntPair = chunk.getChunkCoordIntPair();
        int n = (chunkCoordIntPair.chunkXPos << 4) - 2;
        int n2 = n + 16 + 2;
        int n3 = (chunkCoordIntPair.chunkZPos << 4) - 2;
        int n4 = n3 + 16 + 2;

        for (int i = 0; i < 2; i++) {
            Iterator iterator;
            if (i == 0) {
                iterator = this.pendingTickListEntriesTreeSet.iterator();
            } else {
                iterator = this.pendingTickListEntriesThisTick.iterator();
                if (!this.pendingTickListEntriesThisTick.isEmpty()) {
                    logger.debug("toBeTicked = " + this.pendingTickListEntriesThisTick.size());
                }
            }

            while (iterator.hasNext()) {
                NextTickListEntry nextTickListEntry = (NextTickListEntry)iterator.next();
                if (nextTickListEntry.xCoord >= n && nextTickListEntry.xCoord < n2 && nextTickListEntry.zCoord >= n3 && nextTickListEntry.zCoord < n4) {
                    if (flag) {
                        this.pendingTickListEntriesHashSet.remove(nextTickListEntry);
                        iterator.remove();
                    }

                    if (list == null) {
                        list = new ArrayList();
                    }

                    list.add(nextTickListEntry);
                }
            }
        }

        return list;
    }

    @Override
    public void updateEntityWithOptionalForce(Entity entity, boolean flag) throws java.io.IOException {
        if (!this.mcServer.getCanSpawnAnimals() && (entity instanceof EntityAnimal || entity instanceof EntityWaterMob)) {
            entity.setDead();
        }

        if (!this.mcServer.getCanSpawnNPCs() && entity instanceof INpc) {
            entity.setDead();
        }

        super.updateEntityWithOptionalForce(entity, flag);
    }

    @Override
    public IChunkProvider createChunkProvider() {
        IChunkLoader chunkLoader = this.saveHandler.getChunkLoader(this.provider);
        this.theChunkProviderServer = new ChunkProviderServer(this, chunkLoader, this.provider.createChunkGenerator());
        return this.theChunkProviderServer;
    }

    public List createArrayList(int n, int n2, int n3, int n4, int n5, int n6) {
        ArrayList list = new ArrayList();

        for (int i = 0; i < this.loadedTileEntityList.size(); i++) {
            TileEntity tileEntity = (TileEntity)this.loadedTileEntityList.get(i);
            if (tileEntity.xCoord >= n && tileEntity.yCoord >= n2 && tileEntity.zCoord >= n3 && tileEntity.xCoord < n4 && tileEntity.yCoord < n5 && tileEntity.zCoord < n6) {
                list.add(tileEntity);
            }
        }

        return list;
    }

    @Override
    public boolean canMineBlock(EntityPlayer player, int x, int y, int z) {
        return !this.mcServer.isBlockProtected(this, x, y, z, player);
    }

    @Override
    public void initialize(WorldSettings worldSettings) {
        if (this.entityIdMap == null) {
            this.entityIdMap = new IntHashMap();
        }

        if (this.pendingTickListEntriesHashSet == null) {
            this.pendingTickListEntriesHashSet = new HashSet();
        }

        if (this.pendingTickListEntriesTreeSet == null) {
            this.pendingTickListEntriesTreeSet = new TreeSet();
        }

        this.createSpawnPosition(worldSettings);
        super.initialize(worldSettings);
    }

    public void createSpawnPosition(WorldSettings worldSettings) {
        if (!this.provider.canRespawnHere()) {
            this.worldInfo.setSpawnPosition(0, this.provider.getAverageGroundLevel(), 0);
        } else {
            this.findingSpawnPoint = true;
            WorldChunkManager worldChunkMgr = this.provider.worldChunkMgr;
            List biomesToSpawnIn = worldChunkMgr.getBiomesToSpawnIn();
            Random random = new Random(this.getSeed());
            ChunkPosition findBiomePosition = worldChunkMgr.findBiomePosition(0, 0, 256, biomesToSpawnIn, random);
            int n = 0;
            int averageGroundLevel = this.provider.getAverageGroundLevel();
            int n2 = 0;
            if (findBiomePosition != null) {
                n = findBiomePosition.chunkPosX;
                n2 = findBiomePosition.chunkPosZ;
            } else {
                logger.warn("Unable to find spawn biome");
            }

            int n3 = 0;

            while (!this.provider.canCoordinateBeSpawn(n, n2)) {
                n += random.nextInt(64) - random.nextInt(64);
                n2 += random.nextInt(64) - random.nextInt(64);
                if (++n3 == 1000) {
                    break;
                }
            }

            this.worldInfo.setSpawnPosition(n, averageGroundLevel, n2);
            this.findingSpawnPoint = false;
            if (worldSettings.isBonusChestEnabled()) {
                this.createBonusChest();
            }
        }
    }

    public void createBonusChest() {
        WorldGeneratorBonusChest worldGeneratorBonusChest = new WorldGeneratorBonusChest(bonusChestContent, 10);

        for (int i = 0; i < 10; i++) {
            int n = this.worldInfo.getSpawnX() + this.rand.nextInt(6) - this.rand.nextInt(6);
            int n2 = this.worldInfo.getSpawnZ() + this.rand.nextInt(6) - this.rand.nextInt(6);
            int n3 = this.getTopSolidOrLiquidBlock(n, n2) + 1;
            if (worldGeneratorBonusChest.generate(this, this.rand, n, n3, n2)) {
                break;
            }
        }
    }

    public ChunkCoordinates getEntrancePortalLocation() {
        return this.provider.getEntrancePortalLocation();
    }

    public void saveAllChunks(boolean flag, IProgressUpdate progressUpdate) throws MinecraftException, java.io.IOException {
        if (this.chunkProvider.canSave()) {
            if (progressUpdate != null) {
                progressUpdate.displaySavingString("Saving level");
            }

            this.saveLevel();
            if (progressUpdate != null) {
                progressUpdate.displayLoadingString("Saving chunks");
            }

            this.chunkProvider.saveChunks(flag, progressUpdate);

            for (Chunk chunk : (Iterable<Chunk>)Lists.newArrayList(this.theChunkProviderServer.getChunks())) {
                if (chunk != null && !this.thePlayerManager.getValueByKey(chunk.xPosition, chunk.zPosition)) {
                    this.theChunkProviderServer.dropChunk(chunk.xPosition, chunk.zPosition);
                }
            }
        }
    }

    public void saveChunkData() {
        if (this.chunkProvider.canSave()) {
            this.chunkProvider.saveExtraData();
        }
    }

    public void saveLevel() throws MinecraftException {
        this.checkSessionLock();
        this.saveHandler.saveWorldInfoWithPlayer(this.worldInfo, this.mcServer.getConfigurationManager().getHostPlayerData());
        this.mapStorage.saveAllData();
    }

    @Override
    public void onEntityAdded(Entity entity) {
        super.onEntityAdded(entity);
        this.entityIdMap.addKey(entity.getEntityId(), entity);
        Entity[] parts = entity.getParts();
        if (parts != null) {
            for (int i = 0; i < parts.length; i++) {
                this.entityIdMap.addKey(parts[i].getEntityId(), parts[i]);
            }
        }
    }

    @Override
    public void onEntityRemoved(Entity entity) {
        super.onEntityRemoved(entity);
        this.entityIdMap.removeObject(entity.getEntityId());
        Entity[] parts = entity.getParts();
        if (parts != null) {
            for (int i = 0; i < parts.length; i++) {
                this.entityIdMap.removeObject(parts[i].getEntityId());
            }
        }
    }

    @Override
    public Entity getEntityByID(int n) {
        return (Entity)this.entityIdMap.lookup(n);
    }

    @Override
    public boolean addWeatherEffect(Entity entity) {
        if (super.addWeatherEffect(entity)) {
            this.mcServer
                .getConfigurationManager()
                .sendToAllNear(entity.posX, entity.posY, entity.posZ, 512.0, this.provider.dimensionId, new S2CPacketSpawnGlobalEntity(entity));
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void setEntityState(Entity entityIn, byte b) {
        this.getEntityTracker().sendToAllTrackingEntities(entityIn, new S19PacketEntityStatus(entityIn, b));
    }

    @Override
    public Explosion newExplosion(Entity entity, double d, double d2, double d3, float f, boolean flag, boolean flag2) {
        Explosion explosion = new Explosion(this, entity, d, d2, d3, f);
        explosion.isFlaming = flag;
        explosion.isSmoking = flag2;
        explosion.doExplosionA();
        explosion.doExplosionB(false);
        if (!flag2) {
            explosion.affectedBlockPositions.clear();
        }

        for (EntityPlayer entityPlayer : this.playerEntities) {
            if (entityPlayer.getDistanceSq(d, d2, d3) < 4096.0) {
                ((EntityPlayerMP)entityPlayer)
                    .playerNetServerHandler
                    .sendPacket(new S27PacketExplosion(d, d2, d3, f, explosion.affectedBlockPositions, (Vec3)explosion.getAffectedPlayers().get(entityPlayer)));
            }
        }

        return explosion;
    }

    @Override
    public void addBlockEvent(int x, int y, int z, Block blockIn, int eventId, int eventParameter) {
        BlockEventData blockEventData = new BlockEventData(x, y, z, blockIn, eventId, eventParameter);

        for (BlockEventData blockEventData2 : (Iterable<BlockEventData>)this.serverBlockEventListArray[this.blockEventCacheIndex]) {
            if (blockEventData2.equals(blockEventData)) {
                return;
            }
        }

        this.serverBlockEventListArray[this.blockEventCacheIndex].add(blockEventData);
    }

    public void run22() {
        while (!this.serverBlockEventListArray[this.blockEventCacheIndex].isEmpty()) {
            int blockEventCacheIndex = this.blockEventCacheIndex;
            this.blockEventCacheIndex ^= 1;

            for (BlockEventData blockEventData : (Iterable<BlockEventData>)this.serverBlockEventListArray[blockEventCacheIndex]) {
                if (this.checkBlockEventData(blockEventData)) {
                    this.mcServer
                        .getConfigurationManager()
                        .sendToAllNear(
                            blockEventData.getCoordX(),
                            blockEventData.getCoordY(),
                            blockEventData.getCoordZ(),
                            64.0,
                            this.provider.dimensionId,
                            new S24PacketBlockAction(
                                blockEventData.getCoordX(), blockEventData.getCoordY(), blockEventData.getCoordZ(), blockEventData.getBlock(), blockEventData.getEventID(), blockEventData.getEventParameter()
                            )
                        );
                }
            }

            this.serverBlockEventListArray[blockEventCacheIndex].clear();
        }
    }

    public boolean checkBlockEventData(BlockEventData blockEventData) {
        Block block = this.getBlock(blockEventData.getCoordX(), blockEventData.getCoordY(), blockEventData.getCoordZ());
        return block == blockEventData.getBlock()
            && block.onBlockEventReceived(this, blockEventData.getCoordX(), blockEventData.getCoordY(), blockEventData.getCoordZ(), blockEventData.getEventID(), blockEventData.getEventParameter());
    }

    public void flush() {
        this.saveHandler.flush();
    }

    @Override
    public void updateWeather() {
        boolean isRaining = this.isRaining();
        super.updateWeather();
        if (this.prevRainingStrength != this.rainingStrength) {
            this.mcServer
                .getConfigurationManager()
                .sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(7, this.rainingStrength), this.provider.dimensionId);
        }

        if (this.prevThunderingStrength != this.thunderingStrength) {
            this.mcServer
                .getConfigurationManager()
                .sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(8, this.thunderingStrength), this.provider.dimensionId);
        }

        if (isRaining != this.isRaining()) {
            if (isRaining) {
                this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(2, 0.0F));
            } else {
                this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(1, 0.0F));
            }

            this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(7, this.rainingStrength));
            this.mcServer.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(8, this.thunderingStrength));
        }
    }

    @Override
    public int getRenderDistanceChunks() {
        return this.mcServer.getConfigurationManager().getViewDistance();
    }

    public MinecraftServer getServer() {
        return this.mcServer;
    }

    public EntityTracker getEntityTracker() {
        return this.theEntityTracker;
    }

    public PlayerManager getPlayerManager() {
        return this.thePlayerManager;
    }

    public Teleporter getDefaultTeleporter() {
        return this.worldTeleporter;
    }

    public void handleString(String str, double d, double d2, double d3, int n, double d4, double d5, double d6, double d7) {
        S2APacketParticles s2APacketParticles = new S2APacketParticles(
            str, (float)d, (float)d2, (float)d3, (float)d4, (float)d5, (float)d6, (float)d7, n
        );

        for (int i = 0; i < this.playerEntities.size(); i++) {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP)this.playerEntities.get(i);
            ChunkCoordinates commandSenderPosition = entityPlayerMP.getCommandSenderPosition();
            double d8 = d - commandSenderPosition.posX;
            double d9 = d2 - commandSenderPosition.posY;
            double d10 = d3 - commandSenderPosition.posZ;
            double d11 = d8 * d8 + d9 * d9 + d10 * d10;
            if (d11 <= 256.0) {
                entityPlayerMP.playerNetServerHandler.sendPacket(s2APacketParticles);
            }
        }
    }

    public static class ServerBlockEventList extends ArrayList {
        public ServerBlockEventList() {
        }

        public ServerBlockEventList(Object obj) {
            this();
        }
    }
}
