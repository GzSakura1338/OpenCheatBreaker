package net.optifine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.ISaveHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorldServerOF extends WorldServer {
    public NextTickHashSet pendingTickListEntriesHashSet;
    public TreeSet pendingTickListEntriesTreeSet;
    public List pendingTickListEntriesThisTick = new ArrayList();
    public int lastViewDistance = 0;
    public boolean allChunksTicked = false;
    public Set setChunkCoordsToTickOnce = new HashSet();
    public Set limitedChunkSet = new HashSet();
    public static Logger logger = LogManager.getLogger();

    public WorldServerOF(MinecraftServer minecraftServer, ISaveHandler saveHandler, String str, int n, WorldSettings worldSettings, Profiler profiler) {
        super(minecraftServer, saveHandler, str, n, worldSettings, profiler);
        this.fixSetNextTicks();
    }

    @Override
    public void initialize(WorldSettings worldSettings) {
        super.initialize(worldSettings);
        this.fixSetNextTicks();
    }

    public void fixSetNextTicks() {
        try {
            Field[] declaredFields = WorldServer.class.getDeclaredFields();
            int findField = this.findField(declaredFields, Set.class, 0);
            int findField2 = this.findField(declaredFields, TreeSet.class, findField);
            int findField3 = this.findField(declaredFields, List.class, findField2);
            if (findField >= 0 && findField2 >= 0 && findField3 >= 0) {
                Field field = declaredFields[findField];
                Field fieldRef2 = declaredFields[findField2];
                Field fieldRef3 = declaredFields[findField3];
                field.setAccessible(true);
                fieldRef2.setAccessible(true);
                fieldRef3.setAccessible(true);
                this.pendingTickListEntriesTreeSet = (TreeSet)fieldRef2.get(this);
                this.pendingTickListEntriesThisTick = (List)fieldRef3.get(this);
                Set set = (Set)field.get(this);
                if (set instanceof NextTickHashSet) {
                    return;
                }

                this.pendingTickListEntriesHashSet = new NextTickHashSet(set);
                field.set(this, this.pendingTickListEntriesHashSet);
                Config.dbg("WorldServer.nextTickSet updated");
                return;
            }

            Config.warn("Error updating WorldServer.nextTickSet");
        } catch (Exception e) {
            Config.warn("Error setting WorldServer.nextTickSet: " + e.getMessage());
        }
    }

    public int findField(Field[] fields, Class clazz, int n) {
        if (n < 0) {
            return -1;
        }

        for (int i = n; i < fields.length; i++) {
            Field field = fields[i];
            if (field.getType() == clazz) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public List getPendingBlockUpdates(Chunk chunk, boolean flag) {
        if (this.pendingTickListEntriesHashSet != null && this.pendingTickListEntriesTreeSet != null && this.pendingTickListEntriesThisTick != null) {
            ArrayList list = null;
            ChunkCoordIntPair chunkCoordIntPair = chunk.getChunkCoordIntPair();
            int n = (chunkCoordIntPair.chunkXPos << 4) - 2;
            int n2 = n + 16 + 2;
            int n3 = (chunkCoordIntPair.chunkZPos << 4) - 2;
            int n4 = n3 + 16 + 2;

            for (int i = 0; i < 2; i++) {
                Iterator iterator;
                if (i != 0) {
                    iterator = this.pendingTickListEntriesThisTick.iterator();
                    if (!this.pendingTickListEntriesThisTick.isEmpty()) {
                        logger.debug("toBeTicked = " + this.pendingTickListEntriesThisTick.size());
                    }
                } else {
                    TreeSet treeSet = new TreeSet();

                    for (int j = -1; j <= 1; j++) {
                        for (int k = -1; k <= 1; k++) {
                            HashSet nextTickEntriesSet = this.pendingTickListEntriesHashSet.getNextTickEntriesSet(chunkCoordIntPair.chunkXPos + j, chunkCoordIntPair.chunkZPos + k);
                            treeSet.addAll(nextTickEntriesSet);
                        }
                    }

                    iterator = treeSet.iterator();
                }

                while (iterator.hasNext()) {
                    NextTickListEntry nextTickListEntry = (NextTickListEntry)iterator.next();
                    if (nextTickListEntry.xCoord >= n && nextTickListEntry.xCoord < n2 && nextTickListEntry.zCoord >= n3 && nextTickListEntry.zCoord < n4) {
                        if (flag) {
                            this.pendingTickListEntriesHashSet.remove(nextTickListEntry);
                            this.pendingTickListEntriesTreeSet.remove(nextTickListEntry);
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
        } else {
            return super.getPendingBlockUpdates(chunk, flag);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (!Config.isTimeDefault()) {
            this.fixWorldTime();
        }

        if (Config.waterOpacityChanged) {
            Config.waterOpacityChanged = false;
            ClearWater.updateWaterOpacity(Config.getGameSettings(), this);
        }
    }

    @Override
    public void updateWeather() {
        if (!Config.isWeatherEnabled()) {
            this.fixWorldWeather();
        }

        super.updateWeather();
    }

    public void fixWorldWeather() {
        if (this.worldInfo.isRaining() || this.worldInfo.isThundering()) {
            this.worldInfo.setRainTime(0);
            this.worldInfo.setRaining(false);
            this.setRainStrength(0.0F);
            this.worldInfo.setThunderTime(0);
            this.worldInfo.setThundering(false);
            this.setThunderStrength(0.0F);
            this.getServer().getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(2, 0.0F));
            this.getServer().getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(7, 0.0F));
            this.getServer().getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(8, 0.0F));
        }
    }

    public void fixWorldTime() {
        if (this.worldInfo.getGameType().getID() == 1) {
            long worldTime = this.getWorldTime();
            long l = worldTime % 24000L;
            if (Config.isTimeDayOnly()) {
                if (l <= 1000L) {
                    this.setWorldTime(worldTime - l + 1001L);
                }

                if (l >= 11000L) {
                    this.setWorldTime(worldTime - l + 24001L);
                }
            }

            if (Config.isTimeNightOnly()) {
                if (l <= 14000L) {
                    this.setWorldTime(worldTime - l + 14001L);
                }

                if (l >= 22000L) {
                    this.setWorldTime(worldTime - l + 24000L + 14001L);
                }
            }
        }
    }

    public void handleEntity(Entity entity) throws java.io.IOException {
        if (this.canSkipEntityUpdate(entity) && entity instanceof EntityLivingBase) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
            int entityAge = EntityUtils.getEntityAge(entityLivingBase);
            entityAge++;
            if (entityLivingBase instanceof EntityMob) {
                float brightness = entityLivingBase.getBrightness(1.0F);
                if (brightness > 0.5F) {
                    entityAge += 2;
                }
            }

            EntityUtils.setEntityAge(entityLivingBase, entityAge);
            if (entityLivingBase instanceof EntityLiving) {
                EntityLiving entityLiving = (EntityLiving)entityLivingBase;
                EntityUtils.despawnEntity(entityLiving);
            }
        } else {
            super.updateEntity(entity);
            if (Config.isSmoothWorld()) {
                Thread.currentThread();
                Thread.yield();
            }
        }
    }

    public boolean canSkipEntityUpdate(Entity entity) {
        if (!(entity instanceof EntityLivingBase)) {
            return false;
        }

        EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
        if (entityLivingBase.isChild()) {
            return false;
        }

        if (entityLivingBase.hurtTime > 0) {
            return false;
        }

        if (entity.ticksExisted < 20) {
            return false;
        }

        if (this.playerEntities.size() != 1) {
            return false;
        }

        Entity entity2 = this.playerEntities.get(0);
        double max = Math.max(Math.abs(entity.posX - entity2.posX) - 16.0, 0.0);
        double max2 = Math.max(Math.abs(entity.posZ - entity2.posZ) - 16.0, 0.0);
        double d = max * max + max2 * max2;
        return !entity.isInRangeToRenderDist(d);
    }

    @Override
    public void setActivePlayerChunksAndCheckLight() {
        super.setActivePlayerChunksAndCheckLight();
        this.limitedChunkSet.clear();
        int renderDistanceChunks = this.getRenderDistanceChunks();
        if (renderDistanceChunks > 10) {
            if (renderDistanceChunks != this.lastViewDistance) {
                this.lastViewDistance = renderDistanceChunks;
                this.allChunksTicked = false;
            } else if (!this.allChunksTicked) {
                this.allChunksTicked = true;
            } else {
                for (int i = 0; i < this.playerEntities.size(); i++) {
                    EntityPlayer entityPlayer = this.playerEntities.get(i);
                    int n = MathHelper.floor_double(entityPlayer.posX / 16.0);
                    int n2 = MathHelper.floor_double(entityPlayer.posZ / 16.0);
                    byte b = 10;

                    for (int j = -b; j <= b; j++) {
                        for (int k = -b; k <= b; k++) {
                            this.limitedChunkSet.add(new ChunkCoordIntPair(j + n, k + n2));
                        }
                    }
                }

                if (this.setChunkCoordsToTickOnce.size() > 0) {
                    this.limitedChunkSet.addAll(this.setChunkCoordsToTickOnce);
                    this.setChunkCoordsToTickOnce.clear();
                }
            }
        }
    }

    public void addChunkToTickOnce(int n, int n2) {
        int renderDistanceChunks = this.getRenderDistanceChunks();
        if (renderDistanceChunks > 10) {
            this.setChunkCoordsToTickOnce.add(new ChunkCoordIntPair(n, n2));
        }
    }

    @Override
    public void run() {
        Set activeChunkSet = this.activeChunkSet;
        if (this.limitedChunkSet.size() > 0) {
            this.activeChunkSet = this.limitedChunkSet;
        }

        super.run();
        this.activeChunkSet = activeChunkSet;
    }
}
