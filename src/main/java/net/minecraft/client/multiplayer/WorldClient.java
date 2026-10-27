package net.minecraft.client.multiplayer;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.SimpleEvent3;
import com.google.common.collect.Sets;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EntityFirework.StarterFX;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveDataMemoryStorage;
import net.minecraft.world.storage.SaveHandlerMP;
import net.minecraft.world.storage.WorldInfo;
import net.optifine.Config;
import net.optifine.CustomGuis;
import net.optifine.DynamicLights;
import net.optifine.override.PlayerControllerOF;
import net.optifine.reflect.Reflector;

public class WorldClient extends World {
   public Set<Entity> entityList = Sets.newHashSet();
   public ChunkProviderClient clientChunkProvider;
   public Set<Entity> entitySpawnQueue = Sets.newHashSet();
   public Minecraft mc = Minecraft.getMinecraft();
   public Set<ChunkCoordIntPair> previousActiveChunkSet = Sets.newHashSet();
   public boolean playerUpdate = false;
   public NetHandlerPlayClient sendQueue;

   public WorldClient(NetHandlerPlayClient netHandler, WorldSettings settings, int dimension, EnumDifficulty difficulty, Profiler profilerIn) {
      super(new SaveHandlerMP(), new WorldInfo(settings, "MpServer"), WorldProvider.getProviderForDimension(dimension), profilerIn, true);
      this.sendQueue = netHandler;
      this.getWorldInfo().setDifficulty(difficulty);
      this.provider.registerWorld(this);
      this.setSpawnPoint(new BlockPos(8, 64, 8));
      this.chunkProvider = this.createChunkProvider();
      this.mapStorage = new SaveDataMemoryStorage();
      this.calculateInitialSkylight();
      this.calculateInitialWeather();
      Reflector.postForgeBusEvent(Reflector.WorldEvent_Load_Constructor, this);
      if (this.mc.playerController != null && this.mc.playerController.getClass() == PlayerControllerMP.class) {
         this.mc.playerController = new PlayerControllerOF(this.mc, netHandler);
         CustomGuis.setPlayerControllerOF((PlayerControllerOF)this.mc.playerController);
      }
   }

   @Override
   public void tick() {
      super.tick();
      this.setTotalWorldTime(this.getTotalWorldTime() + 1L);
      if (this.getGameRules().getBoolean("doDaylightCycle")) {
         this.setWorldTime(this.getWorldTime() + 1L);
      }

      this.theProfiler.startSection("reEntryProcessing");

      for (int i = 0; i < 10 && !this.entitySpawnQueue.isEmpty(); i++) {
         Entity entity = this.entitySpawnQueue.iterator().next();
         this.entitySpawnQueue.remove(entity);
         if (!this.loadedEntityList.contains(entity)) {
            this.spawnEntityInWorld(entity);
         }
      }

      this.theProfiler.endStartSection("chunkCache");
      this.clientChunkProvider.unloadQueuedChunks();
      this.theProfiler.endStartSection("blocks");
      this.updateBlocks();
      this.theProfiler.endSection();
   }

   public void invalidateBlockReceiveRegion(int x1, int y1, int z1, int x2, int y2, int z2) {
   }

   @Override
   public IChunkProvider createChunkProvider() {
      this.clientChunkProvider = new ChunkProviderClient(this);
      return this.clientChunkProvider;
   }

   @Override
   public void updateBlocks() {
      super.updateBlocks();
      this.previousActiveChunkSet.retainAll(this.activeChunkSet);
      if (this.previousActiveChunkSet.size() == this.activeChunkSet.size()) {
         this.previousActiveChunkSet.clear();
      }

      int n = 0;

      for (ChunkCoordIntPair chunkCoordIntPair : this.activeChunkSet) {
         if (!this.previousActiveChunkSet.contains(chunkCoordIntPair)) {
            int n2 = chunkCoordIntPair.chunkXPos * 16;
            int n3 = chunkCoordIntPair.chunkZPos * 16;
            this.theProfiler.startSection("getChunk");
            Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos);
            this.playMoodSoundAndCheckLight(n2, n3, chunkFromChunkCoords);
            this.theProfiler.endSection();
            this.previousActiveChunkSet.add(chunkCoordIntPair);
            if (++n >= 10) {
               return;
            }
         }
      }
   }

   public void doPreChunk(int chuncX, int chuncZ, boolean loadChunk) {
      if (loadChunk) {
         this.clientChunkProvider.loadChunk(chuncX, chuncZ);
      } else {
         this.clientChunkProvider.unloadChunk(chuncX, chuncZ);
      }

      if (!loadChunk) {
         this.markBlockRangeForRenderUpdate(chuncX * 16, 0, chuncZ * 16, chuncX * 16 + 15, 256, chuncZ * 16 + 15);
      }
   }

   @Override
   public boolean spawnEntityInWorld(Entity entityIn) {
      boolean spawnEntityInWorld = super.spawnEntityInWorld(entityIn);
      this.entityList.add(entityIn);
      if (!spawnEntityInWorld) {
         this.entitySpawnQueue.add(entityIn);
      } else if (entityIn instanceof EntityMinecart) {
         this.mc.getSoundHandler().playSound(new MovingSoundMinecart((EntityMinecart)entityIn));
      }

      return spawnEntityInWorld;
   }

   @Override
   public void removeEntity(Entity entityIn) {
      super.removeEntity(entityIn);
      this.entityList.remove(entityIn);
   }

   @Override
   public void onEntityAdded(Entity entityIn) {
      super.onEntityAdded(entityIn);
      if (this.entitySpawnQueue.contains(entityIn)) {
         this.entitySpawnQueue.remove(entityIn);
      }
   }

   @Override
   public void onEntityRemoved(Entity entityIn) {
      super.onEntityRemoved(entityIn);
      boolean flag = false;
      if (this.entityList.contains(entityIn)) {
         if (entityIn.isEntityAlive()) {
            this.entitySpawnQueue.add(entityIn);
            flag = true;
         } else {
            this.entityList.remove(entityIn);
         }
      }
   }

   public void addEntityToWorld(int entityID, Entity entityToSpawn) {
      Entity entityByID = this.getEntityByID(entityID);
      if (entityByID != null) {
         this.removeEntity(entityByID);
      }

      this.entityList.add(entityToSpawn);
      entityToSpawn.setEntityId(entityID);
      if (!this.spawnEntityInWorld(entityToSpawn)) {
         this.entitySpawnQueue.add(entityToSpawn);
      }

      this.entitiesById.addKey(entityID, entityToSpawn);
   }

   @Override
   public Entity getEntityByID(int id) {
      return id == this.mc.thePlayer.getEntityId() ? this.mc.thePlayer : super.getEntityByID(id);
   }

   public Entity removeEntityFromWorld(int entityID) {
      Entity removeObject = this.entitiesById.removeObject(entityID);
      if (removeObject != null) {
         this.entityList.remove(removeObject);
         this.removeEntity(removeObject);
      }

      return removeObject;
   }

   public boolean invalidateRegionAndSetBlock(BlockPos pos, IBlockState state) {
      int n = pos.getX();
      int n2 = pos.getY();
      int n3 = pos.getZ();
      this.invalidateBlockReceiveRegion(n, n2, n3, n, n2, n3);
      return super.setBlockState(pos, state, 3);
   }

   @Override
   public void sendQuittingDisconnectingPacket() {
      CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent3());
      this.sendQueue.getNetworkManager().closeChannel(new ChatComponentText("Quitting"));
   }

   @Override
   public void updateWeather() {
   }

   @Override
   public int getRenderDistanceChunks() {
      return this.mc.gameSettings.renderDistanceChunks;
   }

   public void doVoidFogParticles(int posX, int posY, int posZ) {
      byte b = 16;
      Random random = new Random();
      ItemStack heldItem = this.mc.thePlayer.getHeldItem();
      boolean flag = this.mc.playerController.getCurrentGameType() == GameType.CREATIVE
         && heldItem != null
         && Block.getBlockFromItem(heldItem.getItem()) == Blocks.barrier;
      MutableBlockPos mutableBlockPos = new MutableBlockPos();
      int n = CheatBreaker.getInstance().getKeybindSettings().lowerAnimationTicks.getBooleanValue() ? 100 : 1000;

      for (int i = 0; i < n; i++) {
         int n2 = posX + this.rand.nextInt(b) - this.rand.nextInt(b);
         int n3 = posY + this.rand.nextInt(b) - this.rand.nextInt(b);
         int n4 = posZ + this.rand.nextInt(b) - this.rand.nextInt(b);
         mutableBlockPos.set(n2, n3, n4);
         IBlockState blockState = this.getBlockState(mutableBlockPos);
         blockState.getBlock().randomDisplayTick(this, mutableBlockPos, blockState, random);
         if (flag && blockState.getBlock() == Blocks.barrier) {
            this.spawnParticle(EnumParticleTypes.BARRIER, n2 + 0.5F, n3 + 0.5F, n4 + 0.5F, 0.0, 0.0, 0.0);
         }
      }
   }

   public void removeAllEntities() {
      this.loadedEntityList.removeAll(this.unloadedEntityList);

      for (int i = 0; i < this.unloadedEntityList.size(); i++) {
         Entity entity = this.unloadedEntityList.get(i);
         int chunkCoordX = entity.chunkCoordX;
         int chunkCoordZ = entity.chunkCoordZ;
         if (entity.addedToChunk && this.isChunkLoaded(chunkCoordX, chunkCoordZ, true)) {
            this.getChunkFromChunkCoords(chunkCoordX, chunkCoordZ).removeEntity(entity);
         }
      }

      for (int i = 0; i < this.unloadedEntityList.size(); i++) {
         this.onEntityRemoved(this.unloadedEntityList.get(i));
      }

      this.unloadedEntityList.clear();

      for (int i = 0; i < this.loadedEntityList.size(); i++) {
         Entity entity = this.loadedEntityList.get(i);
         if (entity.ridingEntity != null) {
            if (!entity.ridingEntity.isDead && entity.ridingEntity.riddenByEntity == entity) {
               continue;
            }

            entity.ridingEntity.riddenByEntity = null;
            entity.ridingEntity = null;
         }

         if (entity.isDead) {
            int chunkCoordX = entity.chunkCoordX;
            int chunkCoordZ = entity.chunkCoordZ;
            if (entity.addedToChunk && this.isChunkLoaded(chunkCoordX, chunkCoordZ, true)) {
               this.getChunkFromChunkCoords(chunkCoordX, chunkCoordZ).removeEntity(entity);
            }

            this.loadedEntityList.remove(i--);
            this.onEntityRemoved(entity);
         }
      }
   }

   @Override
   public CrashReportCategory addWorldInfoToCrashReport(CrashReport report) {
      CrashReportCategory addWorldInfoToCrashReport = super.addWorldInfoToCrashReport(report);
      addWorldInfoToCrashReport.addCrashSectionCallable("Forced entities", new Callable<String>() {
         public String call() {
            return WorldClient.this.entityList.size() + " total; " + WorldClient.this.entityList.toString();
         }
      });
      addWorldInfoToCrashReport.addCrashSectionCallable("Retry entities", new Callable<String>() {
         public String call() {
            return WorldClient.this.entitySpawnQueue.size() + " total; " + WorldClient.this.entitySpawnQueue.toString();
         }
      });
      addWorldInfoToCrashReport.addCrashSectionCallable("Server brand", new Callable<String>() {
         public String call() throws Exception {
            return WorldClient.this.mc.thePlayer.getClientBrand();
         }
      });
      addWorldInfoToCrashReport.addCrashSectionCallable("Server type", new Callable<String>() {
         public String call() throws Exception {
            return WorldClient.this.mc.getIntegratedServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server";
         }
      });
      return addWorldInfoToCrashReport;
   }

   public void playSoundAtPos(BlockPos pos, String soundName, float volume, float pitch, boolean distanceDelay) {
      this.playSound(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, soundName, volume, pitch, distanceDelay);
   }

   @Override
   public void playSound(double x, double y, double z, String soundName, float volume, float pitch, boolean distanceDelay) {
      double distanceSq = this.mc.getRenderViewEntity().getDistanceSq(x, y, z);
      PositionedSoundRecord positionedSoundRecord = new PositionedSoundRecord(new ResourceLocation(soundName), volume, pitch, (float)x, (float)y, (float)z);
      if (distanceDelay && distanceSq > 100.0) {
         double d = Math.sqrt(distanceSq) / 40.0;
         this.mc.getSoundHandler().playDelayedSound(positionedSoundRecord, (int)(d * 20.0));
      } else {
         this.mc.getSoundHandler().playSound(positionedSoundRecord);
      }
   }

   @Override
   public void makeFireworks(double x, double y, double z, double motionX, double motionY, double motionZ, NBTTagCompound compund) {
      this.mc.effectRenderer.addEffect(new StarterFX(this, x, y, z, motionX, motionY, motionZ, this.mc.effectRenderer, compund));
   }

   public void setWorldScoreboard(Scoreboard scoreboardIn) {
      this.worldScoreboard = scoreboardIn;
   }

   @Override
   public void setWorldTime(long time) {
      if (time < 0L) {
         time = -time;
         this.getGameRules().setOrCreateGameRule("doDaylightCycle", "false");
      } else {
         this.getGameRules().setOrCreateGameRule("doDaylightCycle", "true");
      }

      super.setWorldTime(time);
   }

   @Override
   public int getCombinedLight(BlockPos blockPos, int n) {
      int combinedLight = super.getCombinedLight(blockPos, n);
      if (Config.isDynamicLights()) {
         combinedLight = DynamicLights.getCombinedLight(blockPos, combinedLight);
      }

      return combinedLight;
   }

   @Override
   public boolean setBlockState(BlockPos blockPos, IBlockState blockState, int n) {
      this.playerUpdate = this.isPlayerActing();
      boolean setBlockState = super.setBlockState(blockPos, blockState, n);
      this.playerUpdate = false;
      return setBlockState;
   }

   public boolean isPlayerActing() {
      if (this.mc.playerController instanceof PlayerControllerOF) {
         PlayerControllerOF playerController = (PlayerControllerOF)this.mc.playerController;
         return playerController.isActing();
      } else {
         return false;
      }
   }

   public boolean isPlayerUpdate() {
      return this.playerUpdate;
   }
}
