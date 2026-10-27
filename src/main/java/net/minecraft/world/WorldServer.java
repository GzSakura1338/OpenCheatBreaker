package net.minecraft.world;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEventData;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ReportedException;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.village.VillageCollection;
import net.minecraft.village.VillageSiege;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;
import net.minecraft.world.biome.WorldChunkManager;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.feature.WorldGeneratorBonusChest;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldServer extends World implements IThreadListener {
   public static Logger logger = LogManager.getLogger();
   public Set<NextTickListEntry> pendingTickListEntriesHashSet = Sets.newHashSet();
   public TreeSet<NextTickListEntry> pendingTickListEntriesTreeSet = new TreeSet<>();
   public Map<UUID, Entity> entitiesByUuid = Maps.newHashMap();
   public SpawnerAnimals mobSpawner = new SpawnerAnimals();
   public VillageSiege villageSiege = new VillageSiege(this);
   public WorldServer.ServerBlockEventList[] blockEventQueue = new WorldServer.ServerBlockEventList[]{
      new WorldServer.ServerBlockEventList(), new WorldServer.ServerBlockEventList()
   };
   public ChunkProviderServer theChunkProviderServer;
   public boolean disableLevelSaving;
   public boolean allPlayersSleeping;
   public int updateEntityTick;
   public List<NextTickListEntry> pendingTickListEntriesThisTick = Lists.newArrayList();
   public MinecraftServer mcServer;
   public EntityTracker theEntityTracker;
   public PlayerManager thePlayerManager;
   public int blockEventCacheIndex;
   public static List<WeightedRandomChestContent> bonusChestContent = Lists.newArrayList(
      new WeightedRandomChestContent[]{
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
      }
   );
   public Teleporter worldTeleporter;

   public WorldServer(MinecraftServer server, ISaveHandler saveHandlerIn, WorldInfo info, int dimensionId, Profiler profilerIn) {
      super(saveHandlerIn, info, WorldProvider.getProviderForDimension(dimensionId), profilerIn, false);
      this.mcServer = server;
      this.theEntityTracker = new EntityTracker(this);
      this.thePlayerManager = new PlayerManager(this);
      this.provider.registerWorld(this);
      this.chunkProvider = this.createChunkProvider();
      this.worldTeleporter = new Teleporter(this);
      this.calculateInitialSkylight();
      this.calculateInitialWeather();
      this.getWorldBorder().setSize(server.getMaxWorldSize());
   }

   @Override
   public World init() {
      this.mapStorage = new MapStorage(this.saveHandler);
      String fileNameForProvider = VillageCollection.fileNameForProvider(this.provider);
      VillageCollection loadData = (VillageCollection)this.mapStorage.loadData(VillageCollection.class, fileNameForProvider);
      if (loadData == null) {
         this.villageCollectionObj = new VillageCollection(this);
         this.mapStorage.setData(fileNameForProvider, this.villageCollectionObj);
      } else {
         this.villageCollectionObj = loadData;
         this.villageCollectionObj.setWorldsForAll(this);
      }

      this.worldScoreboard = new ServerScoreboard(this.mcServer);
      ScoreboardSaveData loadData2 = (ScoreboardSaveData)this.mapStorage.loadData(ScoreboardSaveData.class, "scoreboard");
      if (loadData2 == null) {
         loadData2 = new ScoreboardSaveData();
         this.mapStorage.setData("scoreboard", loadData2);
      }

      loadData2.setScoreboard(this.worldScoreboard);
      ((ServerScoreboard)this.worldScoreboard).setScoreboardState(loadData2);
      this.getWorldBorder().setCenter(this.worldInfo.getBorderCenterX(), this.worldInfo.getBorderCenterZ());
      this.getWorldBorder().setDamageAmount(this.worldInfo.getBorderDamagePerBlock());
      this.getWorldBorder().setDamageBuffer(this.worldInfo.getBorderSafeZone());
      this.getWorldBorder().setWarningDistance(this.worldInfo.getBorderWarningDistance());
      this.getWorldBorder().setWarningTime(this.worldInfo.getBorderWarningTime());
      if (this.worldInfo.getBorderLerpTime() > 0L) {
         this.getWorldBorder().setTransition(this.worldInfo.getBorderSize(), this.worldInfo.getBorderLerpTarget(), this.worldInfo.getBorderLerpTime());
      } else {
         this.getWorldBorder().setTransition(this.worldInfo.getBorderSize());
      }

      return this;
   }

   @Override
   public void tick() {
      super.tick();
      if (this.getWorldInfo().isHardcoreModeEnabled() && this.getDifficulty() != EnumDifficulty.HARD) {
         this.getWorldInfo().setDifficulty(EnumDifficulty.HARD);
      }

      this.provider.getWorldChunkManager().cleanupCache();
      if (this.areAllPlayersAsleep()) {
         if (this.getGameRules().getBoolean("doDaylightCycle")) {
            long l = this.worldInfo.getWorldTime() + 24000L;
            this.worldInfo.setWorldTime(l - l % 24000L);
         }

         this.wakeAllPlayers();
      }

      this.theProfiler.startSection("mobSpawner");
      if (this.getGameRules().getBoolean("doMobSpawning") && this.worldInfo.getTerrainType() != WorldType.DEBUG_WORLD) {
         this.mobSpawner.findChunksForSpawning(this, this.spawnHostileMobs, this.spawnPeacefulMobs, this.worldInfo.getWorldTotalTime() % 400L == 0L);
      }

      this.theProfiler.endStartSection("chunkSource");
      this.chunkProvider.unloadQueuedChunks();
      int calculateSkylightSubtracted = this.calculateSkylightSubtracted(1.0F);
      if (calculateSkylightSubtracted != this.getSkylightSubtracted()) {
         this.setSkylightSubtracted(calculateSkylightSubtracted);
      }

      this.worldInfo.setWorldTotalTime(this.worldInfo.getWorldTotalTime() + 1L);
      if (this.getGameRules().getBoolean("doDaylightCycle")) {
         this.worldInfo.setWorldTime(this.worldInfo.getWorldTime() + 1L);
      }

      this.theProfiler.endStartSection("tickPending");
      this.tickUpdates(false);
      this.theProfiler.endStartSection("tickBlocks");
      this.updateBlocks();
      this.theProfiler.endStartSection("chunkMap");
      this.thePlayerManager.updatePlayerInstances();
      this.theProfiler.endStartSection("village");
      this.villageCollectionObj.tick();
      this.villageSiege.tick();
      this.theProfiler.endStartSection("portalForcer");
      this.worldTeleporter.removeStalePortalLocations(this.getTotalWorldTime());
      this.theProfiler.endSection();
      this.sendQueuedBlockEvents();
   }

   public SpawnListEntry getSpawnListEntryForTypeAt(EnumCreatureType creatureType, BlockPos pos) {
      List possibleCreatures = this.getChunkProvider().getPossibleCreatures(creatureType, pos);
      return possibleCreatures != null && !possibleCreatures.isEmpty() ? (SpawnListEntry)WeightedRandom.getRandomItem(this.rand, possibleCreatures) : null;
   }

   public boolean canCreatureTypeSpawnHere(EnumCreatureType creatureType, SpawnListEntry spawnListEntry, BlockPos pos) {
      List possibleCreatures = this.getChunkProvider().getPossibleCreatures(creatureType, pos);
      return possibleCreatures != null && !possibleCreatures.isEmpty() ? possibleCreatures.contains(spawnListEntry) : false;
   }

   @Override
   public void updateAllPlayersSleepingFlag() {
      this.allPlayersSleeping = false;
      if (!this.playerEntities.isEmpty()) {
         int n = 0;
         int n2 = 0;

         for (EntityPlayer entityPlayer : this.playerEntities) {
            if (entityPlayer.isSpectator()) {
               n++;
            } else if (entityPlayer.isPlayerSleeping()) {
               n2++;
            }
         }

         this.allPlayersSleeping = n2 > 0 && n2 >= this.playerEntities.size() - n;
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
            if (entityPlayer.isSpectator() || !entityPlayer.isPlayerFullyAsleep()) {
               return false;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public void setInitialSpawnLocation() {
      if (this.worldInfo.getSpawnY() <= 0) {
         this.worldInfo.setSpawnY(this.getSeaLevel() + 1);
      }

      int spawnX = this.worldInfo.getSpawnX();
      int spawnZ = this.worldInfo.getSpawnZ();
      int n = 0;

      while (this.getGroundAboveSeaLevel(new BlockPos(spawnX, 0, spawnZ)).getMaterial() == Material.air) {
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
   public void updateBlocks() {
      super.updateBlocks();
      if (this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
         for (ChunkCoordIntPair chunkCoordIntPair : this.activeChunkSet) {
            this.getChunkFromChunkCoords(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos).populateBlockEntities(false);
         }
      } else {
         int n = 0;
         int n2 = 0;

         for (ChunkCoordIntPair chunkCoordIntPair : this.activeChunkSet) {
            int n3 = chunkCoordIntPair.chunkXPos * 16;
            int n4 = chunkCoordIntPair.chunkZPos * 16;
            this.theProfiler.startSection("getChunk");
            Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos);
            this.playMoodSoundAndCheckLight(n3, n4, chunkFromChunkCoords);
            this.theProfiler.endStartSection("tickChunk");
            chunkFromChunkCoords.populateBlockEntities(false);
            this.theProfiler.endStartSection("thunder");
            if (this.rand.nextInt(100000) == 0 && this.isRaining() && this.isThundering()) {
               this.updateLCG = this.updateLCG * 3 + 1013904223;
               int n5 = this.updateLCG >> 2;
               BlockPos adjustPosToNearbyEntity = this.adjustPosToNearbyEntity(new BlockPos(n3 + (n5 & 15), 0, n4 + (n5 >> 8 & 15)));
               if (this.isRainingAt(adjustPosToNearbyEntity)) {
                  this.addWeatherEffect(new EntityLightningBolt(this, adjustPosToNearbyEntity.getX(), adjustPosToNearbyEntity.getY(), adjustPosToNearbyEntity.getZ()));
               }
            }

            this.theProfiler.endStartSection("iceandsnow");
            if (this.rand.nextInt(16) == 0) {
               this.updateLCG = this.updateLCG * 3 + 1013904223;
               int n5 = this.updateLCG >> 2;
               BlockPos precipitationHeight = this.getPrecipitationHeight(new BlockPos(n3 + (n5 & 15), 0, n4 + (n5 >> 8 & 15)));
               BlockPos down = precipitationHeight.down();
               if (this.canBlockFreezeNoWater(down)) {
                  this.setBlockState(down, Blocks.ice.getDefaultState());
               }

               if (this.isRaining() && this.canSnowAt(precipitationHeight, true)) {
                  this.setBlockState(precipitationHeight, Blocks.snow_layer.getDefaultState());
               }

               if (this.isRaining() && this.getBiomeGenForCoords(down).canRain()) {
                  this.getBlockState(down).getBlock().fillWithRain(this, down);
               }
            }

            this.theProfiler.endStartSection("tickBlocks");
            int int2 = this.getGameRules().getInt("randomTickSpeed");
            if (int2 > 0) {
               for (ExtendedBlockStorage extendedBlockStorage : chunkFromChunkCoords.getBlockStorageArray()) {
                  if (extendedBlockStorage != null && extendedBlockStorage.getNeedsRandomTick()) {
                     for (int i = 0; i < int2; i++) {
                        this.updateLCG = this.updateLCG * 3 + 1013904223;
                        int n5 = this.updateLCG >> 2;
                        int n6 = n5 & 15;
                        int n7 = n5 >> 8 & 15;
                        int n8 = n5 >> 16 & 15;
                        n2++;
                        IBlockState blockState = extendedBlockStorage.get(n6, n8, n7);
                        Block block = blockState.getBlock();
                        if (block.getTickRandomly()) {
                           n++;
                           block.randomTick(this, new BlockPos(n6 + n3, n8 + extendedBlockStorage.getYLocation(), n7 + n4), blockState, this.rand);
                        }
                     }
                  }
               }
            }

            this.theProfiler.endSection();
         }
      }
   }

   public BlockPos adjustPosToNearbyEntity(BlockPos pos) {
      BlockPos precipitationHeight = this.getPrecipitationHeight(pos);
      AxisAlignedBB expand = new AxisAlignedBB(precipitationHeight, new BlockPos(precipitationHeight.getX(), this.getHeight(), precipitationHeight.getZ())).expand(3.0, 3.0, 3.0);
      List entitiesWithinAABB = this.getEntitiesWithinAABB(EntityLivingBase.class, expand, new Predicate<EntityLivingBase>() {
         public boolean apply(EntityLivingBase entityLivingBase) {
            return entityLivingBase != null && entityLivingBase.isEntityAlive() && WorldServer.this.canSeeSky(entityLivingBase.getPosition());
         }
      });
      return !entitiesWithinAABB.isEmpty() ? ((EntityLivingBase)entitiesWithinAABB.get(this.rand.nextInt(entitiesWithinAABB.size()))).getPosition() : precipitationHeight;
   }

   @Override
   public boolean isBlockTickPending(BlockPos pos, Block blockType) {
      NextTickListEntry nextTickListEntry = new NextTickListEntry(pos, blockType);
      return this.pendingTickListEntriesThisTick.contains(nextTickListEntry);
   }

   @Override
   public void scheduleUpdate(BlockPos pos, Block blockIn, int delay) {
      this.updateBlockTick(pos, blockIn, delay, 0);
   }

   @Override
   public void updateBlockTick(BlockPos pos, Block blockIn, int delay, int priority) {
      NextTickListEntry nextTickListEntry = new NextTickListEntry(pos, blockIn);
      byte b = 0;
      if (this.scheduledUpdatesAreImmediate && blockIn.getMaterial() != Material.air) {
         if (blockIn.requiresUpdates()) {
            b = 8;
            if (this.isAreaLoaded(nextTickListEntry.position.add(-b, -b, -b), nextTickListEntry.position.add(b, b, b))) {
               IBlockState blockState = this.getBlockState(nextTickListEntry.position);
               if (blockState.getBlock().getMaterial() != Material.air && blockState.getBlock() == nextTickListEntry.getBlock()) {
                  blockState.getBlock().updateTick(this, nextTickListEntry.position, blockState, this.rand);
               }
            }

            return;
         }

         delay = 1;
      }

      if (this.isAreaLoaded(pos.add(-b, -b, -b), pos.add(b, b, b))) {
         if (blockIn.getMaterial() != Material.air) {
            nextTickListEntry.setScheduledTime(delay + this.worldInfo.getWorldTotalTime());
            nextTickListEntry.setPriority(priority);
         }

         if (!this.pendingTickListEntriesHashSet.contains(nextTickListEntry)) {
            this.pendingTickListEntriesHashSet.add(nextTickListEntry);
            this.pendingTickListEntriesTreeSet.add(nextTickListEntry);
         }
      }
   }

   @Override
   public void scheduleBlockUpdate(BlockPos pos, Block blockIn, int delay, int priority) {
      NextTickListEntry nextTickListEntry = new NextTickListEntry(pos, blockIn);
      nextTickListEntry.setPriority(priority);
      if (blockIn.getMaterial() != Material.air) {
         nextTickListEntry.setScheduledTime(delay + this.worldInfo.getWorldTotalTime());
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
      if (this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
         return false;
      }

      int size = this.pendingTickListEntriesTreeSet.size();
      if (size != this.pendingTickListEntriesHashSet.size()) {
         throw new IllegalStateException("TickNextTick list out of synch");
      }

      if (size > 1000) {
         size = 1000;
      }

      this.theProfiler.startSection("cleaning");

      for (int i = 0; i < size; i++) {
         NextTickListEntry first = this.pendingTickListEntriesTreeSet.first();
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
         if (this.isAreaLoaded(nextTickListEntry.position.add(-b, -b, -b), nextTickListEntry.position.add(b, b, b))) {
            IBlockState blockState = this.getBlockState(nextTickListEntry.position);
            if (blockState.getBlock().getMaterial() != Material.air && Block.isEqualTo(blockState.getBlock(), nextTickListEntry.getBlock())) {
               try {
                  blockState.getBlock().updateTick(this, nextTickListEntry.position, blockState, this.rand);
               } catch (Throwable e) {
                  CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Exception while ticking a block");
                  CrashReportCategory makeCategory = makeCrashReport.makeCategory("Block being ticked");
                  CrashReportCategory.addBlockInfo(makeCategory, nextTickListEntry.position, blockState);
                  throw new ReportedException(makeCrashReport);
               }
            }
         } else {
            this.scheduleUpdate(nextTickListEntry.position, nextTickListEntry.getBlock(), 0);
         }
      }

      this.theProfiler.endSection();
      this.pendingTickListEntriesThisTick.clear();
      return !this.pendingTickListEntriesTreeSet.isEmpty();
   }

   @Override
   public List<NextTickListEntry> getPendingBlockUpdates(Chunk chunkIn, boolean flag) {
      ChunkCoordIntPair chunkCoordIntPair = chunkIn.getChunkCoordIntPair();
      int n = (chunkCoordIntPair.chunkXPos << 4) - 2;
      int n2 = n + 16 + 2;
      int n3 = (chunkCoordIntPair.chunkZPos << 4) - 2;
      int n4 = n3 + 16 + 2;
      return this.findList(new StructureBoundingBox(n, 0, n3, n2, 256, n4), flag);
   }

   @Override
   public List<NextTickListEntry> findList(StructureBoundingBox structureBB, boolean flag) {
      ArrayList list = null;

      for (int i = 0; i < 2; i++) {
         Iterator iterator;
         if (i == 0) {
            iterator = this.pendingTickListEntriesTreeSet.iterator();
         } else {
            iterator = this.pendingTickListEntriesThisTick.iterator();
         }

         while (iterator.hasNext()) {
            NextTickListEntry nextTickListEntry = (NextTickListEntry)iterator.next();
            BlockPos position = nextTickListEntry.position;
            if (position.getX() >= structureBB.minX && position.getX() < structureBB.maxX && position.getZ() >= structureBB.minZ && position.getZ() < structureBB.maxZ) {
               if (flag) {
                  this.pendingTickListEntriesHashSet.remove(nextTickListEntry);
                  iterator.remove();
               }

               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(nextTickListEntry);
            }
         }
      }

      return list;
   }

   @Override
   public void updateEntityWithOptionalForce(Entity entityIn, boolean forceUpdate) {
      if (!this.canSpawnAnimals() && (entityIn instanceof EntityAnimal || entityIn instanceof EntityWaterMob)) {
         entityIn.setDead();
      }

      if (!this.canSpawnNPCs() && entityIn instanceof INpc) {
         entityIn.setDead();
      }

      super.updateEntityWithOptionalForce(entityIn, forceUpdate);
   }

   public boolean canSpawnNPCs() {
      return this.mcServer.getCanSpawnNPCs();
   }

   public boolean canSpawnAnimals() {
      return this.mcServer.getCanSpawnAnimals();
   }

   @Override
   public IChunkProvider createChunkProvider() {
      IChunkLoader chunkLoader = this.saveHandler.getChunkLoader(this.provider);
      this.theChunkProviderServer = new ChunkProviderServer(this, chunkLoader, this.provider.createChunkGenerator());
      return this.theChunkProviderServer;
   }

   public List<TileEntity> getTileEntitiesIn(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      ArrayList newArrayList = Lists.newArrayList();

      for (int i = 0; i < this.loadedTileEntityList.size(); i++) {
         TileEntity tileEntity = this.loadedTileEntityList.get(i);
         BlockPos pos = tileEntity.getPos();
         if (pos.getX() >= minX && pos.getY() >= minY && pos.getZ() >= minZ && pos.getX() < maxX && pos.getY() < maxY && pos.getZ() < maxZ) {
            newArrayList.add(tileEntity);
         }
      }

      return newArrayList;
   }

   @Override
   public boolean isBlockModifiable(EntityPlayer player, BlockPos pos) {
      return !this.mcServer.isBlockProtected(this, pos, player) && this.getWorldBorder().contains(pos);
   }

   @Override
   public void initialize(WorldSettings settings) {
      if (!this.worldInfo.isInitialized()) {
         try {
            this.createSpawnPosition(settings);
            if (this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
               this.setDebugWorldSettings();
            }

            super.initialize(settings);
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
   }

   public void setDebugWorldSettings() {
      this.worldInfo.setMapFeaturesEnabled(false);
      this.worldInfo.setAllowCommands(true);
      this.worldInfo.setRaining(false);
      this.worldInfo.setThundering(false);
      this.worldInfo.setCleanWeatherTime(1000000000);
      this.worldInfo.setWorldTime(6000L);
      this.worldInfo.setGameType(WorldSettings.GameType.SPECTATOR);
      this.worldInfo.setHardcore(false);
      this.worldInfo.setDifficulty(EnumDifficulty.PEACEFUL);
      this.worldInfo.setDifficultyLocked(true);
      this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
   }

   public void createSpawnPosition(WorldSettings settings) {
      if (!this.provider.canRespawnHere()) {
         this.worldInfo.setSpawn(BlockPos.ORIGIN.up(this.provider.getAverageGroundLevel()));
      } else if (this.worldInfo.getTerrainType() == WorldType.DEBUG_WORLD) {
         this.worldInfo.setSpawn(BlockPos.ORIGIN.up());
      } else {
         this.findingSpawnPoint = true;
         WorldChunkManager worldChunkManager = this.provider.getWorldChunkManager();
         List biomesToSpawnIn = worldChunkManager.getBiomesToSpawnIn();
         Random random = new Random(this.getSeed());
         BlockPos findBiomePosition = worldChunkManager.findBiomePosition(0, 0, 256, biomesToSpawnIn, random);
         int n = 0;
         int averageGroundLevel = this.provider.getAverageGroundLevel();
         int n2 = 0;
         if (findBiomePosition != null) {
            n = findBiomePosition.getX();
            n2 = findBiomePosition.getZ();
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

         this.worldInfo.setSpawn(new BlockPos(n, averageGroundLevel, n2));
         this.findingSpawnPoint = false;
         if (settings.isBonusChestEnabled()) {
            this.createBonusChest();
         }
      }
   }

   public void createBonusChest() {
      WorldGeneratorBonusChest worldGeneratorBonusChest = new WorldGeneratorBonusChest(bonusChestContent, 10);

      for (int i = 0; i < 10; i++) {
         int n = this.worldInfo.getSpawnX() + this.rand.nextInt(6) - this.rand.nextInt(6);
         int n2 = this.worldInfo.getSpawnZ() + this.rand.nextInt(6) - this.rand.nextInt(6);
         BlockPos up = this.getTopSolidOrLiquidBlock(new BlockPos(n, 0, n2)).up();
         if (worldGeneratorBonusChest.generate(this, this.rand, up)) {
            break;
         }
      }
   }

   public BlockPos getSpawnCoordinate() {
      return this.provider.getSpawnCoordinate();
   }

   public void saveAllChunks(boolean flag, IProgressUpdate progressCallback) throws MinecraftException {
      if (this.chunkProvider.canSave()) {
         if (progressCallback != null) {
            progressCallback.displaySavingString("Saving level");
         }

         this.saveLevel();
         if (progressCallback != null) {
            progressCallback.displayLoadingString("Saving chunks");
         }

         this.chunkProvider.saveChunks(flag, progressCallback);

         for (Chunk chunk : Lists.newArrayList(this.theChunkProviderServer.getChunks())) {
            if (chunk != null && !this.thePlayerManager.hasPlayerInstance(chunk.xPosition, chunk.zPosition)) {
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
      this.worldInfo.setBorderSize(this.getWorldBorder().getDiameter());
      this.worldInfo.getBorderCenterX(this.getWorldBorder().getCenterX());
      this.worldInfo.getBorderCenterZ(this.getWorldBorder().getCenterZ());
      this.worldInfo.setBorderSafeZone(this.getWorldBorder().getDamageBuffer());
      this.worldInfo.setBorderDamagePerBlock(this.getWorldBorder().getDamageAmount());
      this.worldInfo.setBorderWarningDistance(this.getWorldBorder().getWarningDistance());
      this.worldInfo.setBorderWarningTime(this.getWorldBorder().getWarningTime());
      this.worldInfo.setBorderLerpTarget(this.getWorldBorder().getTargetSize());
      this.worldInfo.setBorderLerpTime(this.getWorldBorder().getTimeUntilTarget());
      this.saveHandler.saveWorldInfoWithPlayer(this.worldInfo, this.mcServer.getConfigurationManager().getHostPlayerData());
      this.mapStorage.saveAllData();
   }

   @Override
   public void onEntityAdded(Entity entityIn) {
      super.onEntityAdded(entityIn);
      this.entitiesById.addKey(entityIn.getEntityId(), entityIn);
      this.entitiesByUuid.put(entityIn.getUniqueID(), entityIn);
      Entity[] parts = entityIn.getParts();
      if (parts != null) {
         for (int i = 0; i < parts.length; i++) {
            this.entitiesById.addKey(parts[i].getEntityId(), parts[i]);
         }
      }
   }

   @Override
   public void onEntityRemoved(Entity entityIn) {
      super.onEntityRemoved(entityIn);
      this.entitiesById.removeObject(entityIn.getEntityId());
      this.entitiesByUuid.remove(entityIn.getUniqueID());
      Entity[] parts = entityIn.getParts();
      if (parts != null) {
         for (int i = 0; i < parts.length; i++) {
            this.entitiesById.removeObject(parts[i].getEntityId());
         }
      }
   }

   @Override
   public boolean addWeatherEffect(Entity entityIn) {
      if (super.addWeatherEffect(entityIn)) {
         this.mcServer
            .getConfigurationManager()
            .sendToAllNear(entityIn.posX, entityIn.posY, entityIn.posZ, 512.0, this.provider.getDimensionId(), new S2CPacketSpawnGlobalEntity(entityIn));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void setEntityState(Entity entityIn, byte state) {
      this.getEntityTracker().sendToAllTrackingEntities(entityIn, new S19PacketEntityStatus(entityIn, state));
   }

   @Override
   public Explosion newExplosion(Entity entityIn, double x, double y, double z, float strength, boolean isFlaming, boolean isSmoking) {
      Explosion explosion = new Explosion(this, entityIn, x, y, z, strength, isFlaming, isSmoking);
      explosion.doExplosionA();
      explosion.doExplosionB(false);
      if (!isSmoking) {
         explosion.clearAffectedBlockPositions();
      }

      for (EntityPlayer entityPlayer : this.playerEntities) {
         if (entityPlayer.getDistanceSq(x, y, z) < 4096.0) {
            ((EntityPlayerMP)entityPlayer)
               .playerNetServerHandler
               .sendPacket(new S27PacketExplosion(x, y, z, strength, explosion.getAffectedBlockPositions(), explosion.getPlayerKnockbackMap().get(entityPlayer)));
         }
      }

      return explosion;
   }

   @Override
   public void addBlockEvent(BlockPos pos, Block blockIn, int eventID, int eventParam) {
      BlockEventData blockEventData = new BlockEventData(pos, blockIn, eventID, eventParam);

      for (BlockEventData blockEventData2 : this.blockEventQueue[this.blockEventCacheIndex]) {
         if (blockEventData2.equals(blockEventData)) {
            return;
         }
      }

      this.blockEventQueue[this.blockEventCacheIndex].add(blockEventData);
   }

   public void sendQueuedBlockEvents() {
      while (!this.blockEventQueue[this.blockEventCacheIndex].isEmpty()) {
         int blockEventCacheIndex = this.blockEventCacheIndex;
         this.blockEventCacheIndex ^= 1;

         for (BlockEventData blockEventData : this.blockEventQueue[blockEventCacheIndex]) {
            if (this.fireBlockEvent(blockEventData)) {
               this.mcServer
                  .getConfigurationManager()
                  .sendToAllNear(
                     blockEventData.getPosition().getX(),
                     blockEventData.getPosition().getY(),
                     blockEventData.getPosition().getZ(),
                     64.0,
                     this.provider.getDimensionId(),
                     new S24PacketBlockAction(blockEventData.getPosition(), blockEventData.getBlock(), blockEventData.getEventID(), blockEventData.getEventParameter())
                  );
            }
         }

         this.blockEventQueue[blockEventCacheIndex].clear();
      }
   }

   public boolean fireBlockEvent(BlockEventData event) {
      IBlockState blockState = this.getBlockState(event.getPosition());
      return blockState.getBlock() == event.getBlock()
         ? blockState.getBlock().onBlockEventReceived(this, event.getPosition(), blockState, event.getEventID(), event.getEventParameter())
         : false;
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
            .sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(7, this.rainingStrength), this.provider.getDimensionId());
      }

      if (this.prevThunderingStrength != this.thunderingStrength) {
         this.mcServer
            .getConfigurationManager()
            .sendPacketToAllPlayersInDimension(new S2BPacketChangeGameState(8, this.thunderingStrength), this.provider.getDimensionId());
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

   public MinecraftServer getMinecraftServer() {
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

   public void spawnParticle(
      EnumParticleTypes particleType,
      double xCoord,
      double yCoord,
      double zCoord,
      int numberOfParticles,
      double xOffset,
      double yOffset,
      double zOffset,
      double particleSpeed,
      int... particleArguments
   ) {
      this.spawnParticle(particleType, false, xCoord, yCoord, zCoord, numberOfParticles, xOffset, yOffset, zOffset, particleSpeed, particleArguments);
   }

   public void spawnParticle(
      EnumParticleTypes particleType,
      boolean longDistance,
      double xCoord,
      double yCoord,
      double zCoord,
      int numberOfParticles,
      double xOffset,
      double yOffset,
      double zOffset,
      double particleSpeed,
      int... particleArguments
   ) {
      S2APacketParticles s2APacketParticles = new S2APacketParticles(
         particleType,
         longDistance,
         (float)xCoord,
         (float)yCoord,
         (float)zCoord,
         (float)xOffset,
         (float)yOffset,
         (float)zOffset,
         (float)particleSpeed,
         numberOfParticles,
         particleArguments
      );

      for (int i = 0; i < this.playerEntities.size(); i++) {
         EntityPlayerMP entityPlayerMP = (EntityPlayerMP)this.playerEntities.get(i);
         BlockPos position = entityPlayerMP.getPosition();
         double distanceSq = position.distanceSq(xCoord, yCoord, zCoord);
         if (distanceSq <= 256.0 || longDistance && distanceSq <= 65536.0) {
            entityPlayerMP.playerNetServerHandler.sendPacket(s2APacketParticles);
         }
      }
   }

   public Entity getEntityFromUuid(UUID uuid) {
      return this.entitiesByUuid.get(uuid);
   }

   @Override
   public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
      return this.mcServer.addScheduledTask(runnableToSchedule);
   }

   @Override
   public boolean isCallingFromMinecraftThread() {
      return this.mcServer.isCallingFromMinecraftThread();
   }

   static class ServerBlockEventList extends ArrayList<BlockEventData> {
      public ServerBlockEventList() {
      }
   }
}
