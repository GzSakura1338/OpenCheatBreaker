package net.minecraft.client.multiplayer;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.SimpleEvent3;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MovingSoundMinecart;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EntityFireworkStarterFX;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.storage.SaveHandlerMP;
import net.optifine.Config;
import net.optifine.DynamicLights;
import net.optifine.Reflector;

public class WorldClient extends World {
    public IntHashMap entityHashSet = new IntHashMap();
    public ChunkProviderClient clientChunkProvider;
    public Set entityList = new HashSet();
    public Set entitySpawnQueue = new HashSet();
    public Minecraft mc = Minecraft.getMinecraft();
    public Set previousActiveChunkSet = new HashSet();
    public boolean renderItemInFirstPerson = false;
    public NetHandlerPlayClient sendQueue;

    public WorldClient(NetHandlerPlayClient netHandlerPlayClient, WorldSettings worldSettings, int n, EnumDifficulty enumDifficulty, Profiler profiler) {
        super(new SaveHandlerMP(), "MpServer", WorldProvider.getProviderForDimension(n), worldSettings, profiler);
        this.sendQueue = netHandlerPlayClient;
        this.difficultySetting = enumDifficulty;
        this.mapStorage = netHandlerPlayClient.mapStorageOrigin;
        if (Reflector.ForgeWorld_finishSetup.exists()) {
            this.isRemote = true;
            Reflector.call(this, Reflector.ForgeWorld_finishSetup);
        }

        this.setSpawnLocation(8, 64, 8);
        Reflector.postForgeBusEvent(Reflector.WorldEvent_Load_Constructor, this);
    }

    @Override
    public void tick() {
        super.tick();
        this.setTime(this.getTotalWorldTime() + 1L);
        if (this.getGameRules().getGameRuleBooleanValue("doDaylightCycle")) {
            this.setWorldTime(this.getWorldTime() + 1L);
        }

        this.theProfiler.startSection("reEntryProcessing");

        for (int i = 0; i < 10 && !this.entitySpawnQueue.isEmpty(); i++) {
            Entity entity = (Entity)this.entitySpawnQueue.iterator().next();
            this.entitySpawnQueue.remove(entity);
            if (!this.loadedEntityList.contains(entity)) {
                this.spawnEntityInWorld(entity);
            }
        }

        this.theProfiler.endStartSection("connection");
        this.sendQueue.onNetworkTick();
        this.theProfiler.endStartSection("chunkCache");
        this.clientChunkProvider.unloadQueuedChunks();
        this.theProfiler.endStartSection("blocks");
        this.run();
        this.theProfiler.endSection();
    }

    public void invalidateBlockReceiveRegion(int n, int n2, int n3, int n4, int n5, int n6) {
    }

    @Override
    public IChunkProvider createChunkProvider() {
        this.clientChunkProvider = new ChunkProviderClient(this);
        return this.clientChunkProvider;
    }

    @Override
    public void run() {
        super.run();
        this.previousActiveChunkSet.retainAll(this.activeChunkSet);
        if (this.previousActiveChunkSet.size() == this.activeChunkSet.size()) {
            this.previousActiveChunkSet.clear();
        }

        int n = 0;

        for (ChunkCoordIntPair chunkCoordIntPair : (Iterable<ChunkCoordIntPair>)this.activeChunkSet) {
            if (!this.previousActiveChunkSet.contains(chunkCoordIntPair)) {
                int n2 = chunkCoordIntPair.chunkXPos * 16;
                int n3 = chunkCoordIntPair.chunkZPos * 16;
                this.theProfiler.startSection("getChunk");
                Chunk chunkFromChunkCoords = this.getChunkFromChunkCoords(chunkCoordIntPair.chunkXPos, chunkCoordIntPair.chunkZPos);
                this.process(n2, n3, chunkFromChunkCoords);
                this.theProfiler.endSection();
                this.previousActiveChunkSet.add(chunkCoordIntPair);
                if (++n >= 10) {
                    return;
                }
            }
        }
    }

    public void doPreChunk(int n, int n2, boolean flag) {
        if (flag) {
            this.clientChunkProvider.loadChunk(n, n2);
        } else {
            this.clientChunkProvider.unloadChunk(n, n2);
        }

        if (!flag) {
            this.markBlockRangeForRenderUpdate(n * 16, 0, n2 * 16, n * 16 + 15, 256, n2 * 16 + 15);
        }
    }

    @Override
    public boolean spawnEntityInWorld(Entity entity) {
        boolean spawnEntityInWorld = super.spawnEntityInWorld(entity);
        this.entityList.add(entity);
        if (!spawnEntityInWorld) {
            this.entitySpawnQueue.add(entity);
        } else if (entity instanceof EntityMinecart) {
            this.mc.getSoundHandler().playSound(new MovingSoundMinecart((EntityMinecart)entity));
        }

        return spawnEntityInWorld;
    }

    @Override
    public void removeEntity(Entity entity) {
        super.removeEntity(entity);
        this.entityList.remove(entity);
    }

    @Override
    public void onEntityAdded(Entity entity) {
        super.onEntityAdded(entity);
        if (this.entitySpawnQueue.contains(entity)) {
            this.entitySpawnQueue.remove(entity);
        }
    }

    @Override
    public void onEntityRemoved(Entity entity) {
        super.onEntityRemoved(entity);
        boolean flag = false;
        if (this.entityList.contains(entity)) {
            if (entity.isEntityAlive()) {
                this.entitySpawnQueue.add(entity);
                flag = true;
            } else {
                this.entityList.remove(entity);
            }
        }

        if (RenderManager.instance.getEntityRenderObject(entity).isStaticEntity() && !flag) {
            this.mc.renderGlobal.onStaticEntitiesChanged();
        }
    }

    public void addEntityToWorld(int n, Entity entity) {
        Entity entityByID = this.getEntityByID(n);
        if (entityByID != null) {
            this.removeEntity(entityByID);
        }

        this.entityList.add(entity);
        entity.setEntityId(n);
        if (!this.spawnEntityInWorld(entity)) {
            this.entitySpawnQueue.add(entity);
        }

        this.entityHashSet.addKey(n, entity);
        if (RenderManager.instance.getEntityRenderObject(entity).isStaticEntity()) {
            this.mc.renderGlobal.onStaticEntitiesChanged();
        }
    }

    @Override
    public Entity getEntityByID(int n) {
        return n == this.mc.thePlayer.getEntityId() ? this.mc.thePlayer : (Entity)this.entityHashSet.lookup(n);
    }

    public Entity removeEntityFromWorld(int n) {
        Entity removeObject = (Entity)this.entityHashSet.removeObject(n);
        if (removeObject != null) {
            this.entityList.remove(removeObject);
            this.removeEntity(removeObject);
        }

        return removeObject;
    }

    public boolean check(int n, int n2, int n3, Block block, int n4) {
        this.invalidateBlockReceiveRegion(n, n2, n3, n, n2, n3);
        return super.setBlock(n, n2, n3, block, n4, 3);
    }

    @Override
    public void sendQuittingDisconnectingPacket() {
        CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent3());
        this.sendQueue.getNetworkManager().closeChannel(new ChatComponentText("Quitting"));
    }

    @Override
    public void updateWeather() {
        super.updateWeather();
    }

    public void updateWeatherBody() {
        if (!this.provider.hasNoSky) {
        }
    }

    @Override
    public int getRenderDistanceChunks() {
        return this.mc.gameSettings.renderDistanceChunks;
    }

    public void doVoidFogParticles(int n, int n2, int n3) {
        byte b = 16;
        Random random = new Random();
        int n4 = CheatBreaker.getInstance().getKeybindSettings().lowerAnimationTicks.getBooleanValue() ? 100 : 1000;

        for (int i = 0; i < n4; i++) {
            int n5 = n + this.rand.nextInt(b) - this.rand.nextInt(b);
            int n6 = n2 + this.rand.nextInt(b) - this.rand.nextInt(b);
            int n7 = n3 + this.rand.nextInt(b) - this.rand.nextInt(b);
            Block block = this.getBlock(n5, n6, n7);
            if (block.getMaterial() == Material.air) {
                if (this.rand.nextInt(8) > n6 && this.provider.getWorldHasVoidParticles()) {
                    this.spawnParticle("depthsuspend", n5 + this.rand.nextFloat(), n6 + this.rand.nextFloat(), n7 + this.rand.nextFloat(), 0.0, 0.0, 0.0);
                }
            } else {
                block.randomDisplayTick(this, n5, n6, n7, random);
            }
        }
    }

    public void removeAllEntities() {
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

        for (int i = 0; i < this.loadedEntityList.size(); i++) {
            Entity entity = (Entity)this.loadedEntityList.get(i);
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
                if (entity.addedToChunk && this.chunkExists(chunkCoordX, chunkCoordZ)) {
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
        addWorldInfoToCrashReport.addCrashSectionCallable("Forced entities", () -> this.entityList.size() + " total; " + this.entityList.toString());
        addWorldInfoToCrashReport.addCrashSectionCallable("Retry entities", () -> this.entitySpawnQueue.size() + " total; " + this.entitySpawnQueue.toString());
        addWorldInfoToCrashReport.addCrashSectionCallable("Server brand", () -> this.mc.thePlayer.getClientBrand());
        addWorldInfoToCrashReport.addCrashSectionCallable(
            "Server type", () -> this.mc.getIntegratedServer() == null ? "Non-integrated multiplayer server" : "Integrated singleplayer server"
        );
        return addWorldInfoToCrashReport;
    }

    @Override
    public void playSound(double x, double y, double z, String soundName, float volume, float pitch, boolean distanceDelay) {
        double distanceSq = this.mc.renderViewEntity.getDistanceSq(x, y, z);
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
        this.mc.effectRenderer.addEffect(new EntityFireworkStarterFX(this, x, y, z, motionX, motionY, motionZ, this.mc.effectRenderer, compund));
    }

    public void setWorldScoreboard(Scoreboard scoreboard) {
        this.worldScoreboard = scoreboard;
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
    public int getLightBrightnessForSkyBlocks(int n, int n2, int n3, int n4) {
        int lightBrightnessForSkyBlocks = super.getLightBrightnessForSkyBlocks(n, n2, n3, n4);
        if (Config.isDynamicLights()) {
            if (this.renderItemInFirstPerson) {
                lightBrightnessForSkyBlocks = DynamicLights.getCombinedLight(this.mc.renderViewEntity, lightBrightnessForSkyBlocks);
            }

            if (!this.getBlock(n, n2, n3).isOpaqueCube()) {
                lightBrightnessForSkyBlocks = DynamicLights.getCombinedLight(n, n2, n3, lightBrightnessForSkyBlocks);
            }
        }

        return lightBrightnessForSkyBlocks;
    }
}
