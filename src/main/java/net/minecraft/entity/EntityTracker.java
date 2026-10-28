package net.minecraft.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.network.Packet;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTracker {
    public static Logger logger = LogManager.getLogger();
    public Set trackedEntities = new HashSet();
    public IntHashMap trackedEntityHashTable = new IntHashMap();
    public WorldServer theWorld;
    public int maxTrackingDistanceThreshold;

    public EntityTracker(WorldServer worldServer) {
        this.theWorld = worldServer;
        this.maxTrackingDistanceThreshold = worldServer.getServer().getConfigurationManager().getEntityViewDistance();
    }

    public void trackEntity(Entity entity) {
        if (entity instanceof EntityPlayerMP) {
            this.trackEntity(entity, 512, 2);
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP)entity;

            for (EntityTrackerEntry entityTrackerEntry : (Iterable<EntityTrackerEntry>)this.trackedEntities) {
                if (entityTrackerEntry.trackedEntity != entityPlayerMP) {
                    entityTrackerEntry.updatePlayerEntity(entityPlayerMP);
                }
            }
        } else if (entity instanceof EntityFishHook) {
            this.addEntityToTracker(entity, 64, 5, true);
        } else if (entity instanceof EntityArrow) {
            this.addEntityToTracker(entity, 64, 20, false);
        } else if (entity instanceof EntitySmallFireball) {
            this.addEntityToTracker(entity, 64, 10, false);
        } else if (entity instanceof EntityFireball) {
            this.addEntityToTracker(entity, 64, 10, false);
        } else if (entity instanceof EntitySnowball) {
            this.addEntityToTracker(entity, 64, 10, true);
        } else if (entity instanceof EntityEnderPearl) {
            this.addEntityToTracker(entity, 64, 10, true);
        } else if (entity instanceof EntityEnderEye) {
            this.addEntityToTracker(entity, 64, 4, true);
        } else if (entity instanceof EntityEgg) {
            this.addEntityToTracker(entity, 64, 10, true);
        } else if (entity instanceof EntityPotion) {
            this.addEntityToTracker(entity, 64, 10, true);
        } else if (entity instanceof EntityExpBottle) {
            this.addEntityToTracker(entity, 64, 10, true);
        } else if (entity instanceof EntityFireworkRocket) {
            this.addEntityToTracker(entity, 64, 10, true);
        } else if (entity instanceof EntityItem) {
            this.addEntityToTracker(entity, 64, 20, true);
        } else if (entity instanceof EntityMinecart) {
            this.addEntityToTracker(entity, 80, 3, true);
        } else if (entity instanceof EntityBoat) {
            this.addEntityToTracker(entity, 80, 3, true);
        } else if (entity instanceof EntitySquid) {
            this.addEntityToTracker(entity, 64, 3, true);
        } else if (entity instanceof EntityWither) {
            this.addEntityToTracker(entity, 80, 3, false);
        } else if (entity instanceof EntityBat) {
            this.addEntityToTracker(entity, 80, 3, false);
        } else if (entity instanceof IAnimals) {
            this.addEntityToTracker(entity, 80, 3, true);
        } else if (entity instanceof EntityDragon) {
            this.addEntityToTracker(entity, 160, 3, true);
        } else if (entity instanceof EntityTNTPrimed) {
            this.addEntityToTracker(entity, 160, 10, true);
        } else if (entity instanceof EntityFallingBlock) {
            this.addEntityToTracker(entity, 160, 20, true);
        } else if (entity instanceof EntityHanging) {
            this.addEntityToTracker(entity, 160, Integer.MAX_VALUE, false);
        } else if (entity instanceof EntityXPOrb) {
            this.addEntityToTracker(entity, 160, 20, true);
        } else if (entity instanceof EntityEnderCrystal) {
            this.addEntityToTracker(entity, 256, Integer.MAX_VALUE, false);
        }
    }

    public void trackEntity(Entity entity, int n, int n2) {
        this.addEntityToTracker(entity, n, n2, false);
    }

    public void addEntityToTracker(Entity entity, int n, final int n2, boolean flag) {
        if (n > this.maxTrackingDistanceThreshold) {
            n = this.maxTrackingDistanceThreshold;
        }

        try {
            if (this.trackedEntityHashTable.containsItem(entity.getEntityId())) {
                throw new IllegalStateException("Entity is already tracked!");
            }

            EntityTrackerEntry entityTrackerEntry = new EntityTrackerEntry(entity, n, n2, flag);
            this.trackedEntities.add(entityTrackerEntry);
            this.trackedEntityHashTable.addKey(entity.getEntityId(), entityTrackerEntry);
            entityTrackerEntry.updatePlayerEntities(this.theWorld.playerEntities);
        } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Adding entity to track");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Entity To Track");
            makeCategory.addCrashSection("Tracking range", n + " blocks");
            makeCategory.addCrashSectionCallable("Update interval", new Callable() {
                public String call() {
                    String str = "Once per " + n2 + " ticks";
                    if (n2 == Integer.MAX_VALUE) {
                        str = "Maximum (" + str + ")";
                    }

                    return str;
                }
            });
            entity.addEntityCrashInfo(makeCategory);
            CrashReportCategory makeCategory2 = makeCrashReport.makeCategory("Entity That Is Already Tracked");
            ((EntityTrackerEntry)this.trackedEntityHashTable.lookup(entity.getEntityId())).trackedEntity.addEntityCrashInfo(makeCategory2);

            try {
                throw new ReportedException(makeCrashReport);
            } catch (ReportedException e2) {
                logger.error("\"Silently\" catching entity tracking error.", e2);
            }
        }
    }

    public void untrackEntity(Entity entity) {
        if (entity instanceof EntityPlayerMP) {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP)entity;

            for (EntityTrackerEntry entityTrackerEntry : (Iterable<EntityTrackerEntry>)this.trackedEntities) {
                entityTrackerEntry.removeFromTrackedPlayers(entityPlayerMP);
            }
        }

        EntityTrackerEntry removeObject = (EntityTrackerEntry)this.trackedEntityHashTable.removeObject(entity.getEntityId());
        if (removeObject != null) {
            this.trackedEntities.remove(removeObject);
            removeObject.sendDestroyEntityPacketToTrackedPlayers();
        }
    }

    public void updateTrackedEntities() {
        ArrayList list = new ArrayList();

        for (EntityTrackerEntry entityTrackerEntry : (Iterable<EntityTrackerEntry>)this.trackedEntities) {
            entityTrackerEntry.updatePlayerList(this.theWorld.playerEntities);
            if (entityTrackerEntry.playerEntitiesUpdated && entityTrackerEntry.trackedEntity instanceof EntityPlayerMP) {
                list.add(entityTrackerEntry.trackedEntity);
            }
        }

        for (int i = 0; i < list.size(); i++) {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP)list.get(i);

            for (EntityTrackerEntry entityTrackerEntry : (Iterable<EntityTrackerEntry>)this.trackedEntities) {
                if (entityTrackerEntry.trackedEntity != entityPlayerMP) {
                    entityTrackerEntry.updatePlayerEntity(entityPlayerMP);
                }
            }
        }
    }

    public void sendToAllTrackingEntity(Entity entity, Packet packet) {
        EntityTrackerEntry lookup = (EntityTrackerEntry)this.trackedEntityHashTable.lookup(entity.getEntityId());
        if (lookup != null) {
            lookup.handlePacket(packet);
        }
    }

    public void sendToAllTrackingEntities(Entity entity, Packet packet) {
        EntityTrackerEntry lookup = (EntityTrackerEntry)this.trackedEntityHashTable.lookup(entity.getEntityId());
        if (lookup != null) {
            lookup.handlePacket2(packet);
        }
    }

    public void removePlayerFromTrackers(EntityPlayerMP entityPlayerMP) {
        for (EntityTrackerEntry entityTrackerEntry : (Iterable<EntityTrackerEntry>)this.trackedEntities) {
            entityTrackerEntry.removeTrackedPlayerSymmetric(entityPlayerMP);
        }
    }

    public void handleEntityPlayerMP(EntityPlayerMP entityPlayerMP, Chunk chunk) {
        for (EntityTrackerEntry entityTrackerEntry : (Iterable<EntityTrackerEntry>)this.trackedEntities) {
            if (entityTrackerEntry.trackedEntity != entityPlayerMP && entityTrackerEntry.trackedEntity.chunkCoordX == chunk.xPosition && entityTrackerEntry.trackedEntity.chunkCoordZ == chunk.zPosition) {
                entityTrackerEntry.updatePlayerEntity(entityPlayerMP);
            }
        }
    }
}
