package net.minecraft.entity;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityEnderEye;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.item.EntityFireworkRocket;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S14PacketEntity.S15PacketEntityRelMove;
import net.minecraft.network.play.server.S14PacketEntity.S16PacketEntityLook;
import net.minecraft.network.play.server.S14PacketEntity.S17PacketEntityLookMove;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MathHelper;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTrackerEntry {
    public static Logger logger = LogManager.getLogger();
    public Set trackingPlayers = new HashSet();
    public Entity trackedEntity;
    public int trackingDistanceThreshold;
    public int updateFrequency;
    public boolean sendVelocityUpdates;
    public int encodedPosX;
    public int encodedPosY;
    public int encodedPosZ;
    public int encodedRotationYaw;
    public double lastTrackedEntityMotionX;
    public double lastTrackedEntityMotionY;
    public double motionZ;
    public int updateCounter;
    public double lastTrackedEntityPosX;
    public double lastTrackedEntityPosY;
    public double lastTrackedEntityPosZ;
    public boolean firstUpdateDone;
    public int encodedRotationPitch;
    public int ticksSinceLastForcedTeleport;
    public Entity entity;
    public boolean ridingEntity;
    public boolean playerEntitiesUpdated;
    public int lastHeadMotion;

    public EntityTrackerEntry(Entity entity, int n, int n2, boolean flag) {
        this.trackedEntity = entity;
        this.trackingDistanceThreshold = n;
        this.updateFrequency = n2;
        this.sendVelocityUpdates = flag;
        this.encodedPosX = MathHelper.floor_double(entity.posX * 32.0);
        this.encodedPosY = MathHelper.floor_double(entity.posY * 32.0);
        this.encodedPosZ = MathHelper.floor_double(entity.posZ * 32.0);
        this.encodedRotationYaw = MathHelper.floor_float(entity.rotationYaw * 256.0F / 360.0F);
        this.encodedRotationPitch = MathHelper.floor_float(entity.rotationPitch * 256.0F / 360.0F);
        this.lastHeadMotion = MathHelper.floor_float(entity.getRotationYawHead() * 256.0F / 360.0F);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof EntityTrackerEntry && ((EntityTrackerEntry)obj).trackedEntity.getEntityId() == this.trackedEntity.getEntityId();
    }

    @Override
    public int hashCode() {
        return this.trackedEntity.getEntityId();
    }

    public void updatePlayerList(List list) {
        this.playerEntitiesUpdated = false;
        if (!this.firstUpdateDone || this.trackedEntity.getDistanceSq(this.lastTrackedEntityPosX, this.lastTrackedEntityPosY, this.lastTrackedEntityPosZ) > 16.0) {
            this.lastTrackedEntityPosX = this.trackedEntity.posX;
            this.lastTrackedEntityPosY = this.trackedEntity.posY;
            this.lastTrackedEntityPosZ = this.trackedEntity.posZ;
            this.firstUpdateDone = true;
            this.playerEntitiesUpdated = true;
            this.updatePlayerEntities(list);
        }

        if (this.entity != this.trackedEntity.ridingEntity || this.trackedEntity.ridingEntity != null && this.updateCounter % 60 == 0) {
            this.entity = this.trackedEntity.ridingEntity;
            this.handlePacket(new S1BPacketEntityAttach(0, this.trackedEntity, this.trackedEntity.ridingEntity));
        }

        if (this.trackedEntity instanceof EntityItemFrame && this.updateCounter % 10 == 0) {
            EntityItemFrame trackedEntity = (EntityItemFrame)this.trackedEntity;
            ItemStack displayedItem = trackedEntity.getDisplayedItem();
            if (displayedItem != null && displayedItem.getItem() instanceof ItemMap) {
                MapData mapData = Items.filled_map.getMapData(displayedItem, this.trackedEntity.worldObj);

                for (EntityPlayer entityPlayer : (Iterable<EntityPlayer>)list) {
                    EntityPlayerMP entityPlayerMP = (EntityPlayerMP)entityPlayer;
                    mapData.updateVisiblePlayers(entityPlayerMP, displayedItem);
                    Packet createMapDataPacket = Items.filled_map.createMapDataPacket(displayedItem, this.trackedEntity.worldObj, entityPlayerMP);
                    if (createMapDataPacket != null) {
                        entityPlayerMP.playerNetServerHandler.sendPacket(createMapDataPacket);
                    }
                }
            }

            this.sendMetadataToAllAssociatedPlayers();
        } else if (this.updateCounter % this.updateFrequency == 0 || this.trackedEntity.isAirBorne || this.trackedEntity.getDataWatcher().hasObjectChanged()) {
            if (this.trackedEntity.ridingEntity == null) {
                this.ticksSinceLastForcedTeleport++;
                int multiplyBy32AndRound = this.trackedEntity.myEntitySize.multiplyBy32AndRound(this.trackedEntity.posX);
                int n = MathHelper.floor_double(this.trackedEntity.posY * 32.0);
                int multiplyBy32AndRound2 = this.trackedEntity.myEntitySize.multiplyBy32AndRound(this.trackedEntity.posZ);
                int n2 = MathHelper.floor_float(this.trackedEntity.rotationYaw * 256.0F / 360.0F);
                int n3 = MathHelper.floor_float(this.trackedEntity.rotationPitch * 256.0F / 360.0F);
                int n4 = multiplyBy32AndRound - this.encodedPosX;
                int n5 = n - this.encodedPosY;
                int n6 = multiplyBy32AndRound2 - this.encodedPosZ;
                Packet packet = null;
                boolean flag = Math.abs(n4) >= 4 || Math.abs(n5) >= 4 || Math.abs(n6) >= 4 || this.updateCounter % 60 == 0;
                boolean flag2 = Math.abs(n2 - this.encodedRotationYaw) >= 4 || Math.abs(n3 - this.encodedRotationPitch) >= 4;
                if (this.updateCounter > 0 || this.trackedEntity instanceof EntityArrow) {
                    if (n4 < -128
                        || n4 >= 128
                        || n5 < -128
                        || n5 >= 128
                        || n6 < -128
                        || n6 >= 128
                        || this.ticksSinceLastForcedTeleport > 400
                        || this.ridingEntity) {
                        this.ticksSinceLastForcedTeleport = 0;
                        packet = new S18PacketEntityTeleport(this.trackedEntity.getEntityId(), multiplyBy32AndRound, n, multiplyBy32AndRound2, (byte)n2, (byte)n3);
                    } else if (flag && flag2) {
                        packet = new S17PacketEntityLookMove(this.trackedEntity.getEntityId(), (byte)n4, (byte)n5, (byte)n6, (byte)n2, (byte)n3);
                    } else if (flag) {
                        packet = new S15PacketEntityRelMove(this.trackedEntity.getEntityId(), (byte)n4, (byte)n5, (byte)n6);
                    } else if (flag2) {
                        packet = new S16PacketEntityLook(this.trackedEntity.getEntityId(), (byte)n2, (byte)n3);
                    }
                }

                if (this.sendVelocityUpdates) {
                    double d = this.trackedEntity.motionX - this.lastTrackedEntityMotionX;
                    double d2 = this.trackedEntity.motionY - this.lastTrackedEntityMotionY;
                    double d3 = this.trackedEntity.motionZ - this.motionZ;
                    double d4 = 0.02;
                    double d5 = d * d + d2 * d2 + d3 * d3;
                    if (d5 > d4 * d4
                        || d5 > 0.0 && this.trackedEntity.motionX == 0.0 && this.trackedEntity.motionY == 0.0 && this.trackedEntity.motionZ == 0.0) {
                        this.lastTrackedEntityMotionX = this.trackedEntity.motionX;
                        this.lastTrackedEntityMotionY = this.trackedEntity.motionY;
                        this.motionZ = this.trackedEntity.motionZ;
                        this.handlePacket(
                            new S12PacketEntityVelocity(this.trackedEntity.getEntityId(), this.lastTrackedEntityMotionX, this.lastTrackedEntityMotionY, this.motionZ)
                        );
                    }
                }

                if (packet != null) {
                    this.handlePacket(packet);
                }

                this.sendMetadataToAllAssociatedPlayers();
                if (flag) {
                    this.encodedPosX = multiplyBy32AndRound;
                    this.encodedPosY = n;
                    this.encodedPosZ = multiplyBy32AndRound2;
                }

                if (flag2) {
                    this.encodedRotationYaw = n2;
                    this.encodedRotationPitch = n3;
                }

                this.ridingEntity = false;
            } else {
                int n = MathHelper.floor_float(this.trackedEntity.rotationYaw * 256.0F / 360.0F);
                int n2 = MathHelper.floor_float(this.trackedEntity.rotationPitch * 256.0F / 360.0F);
                boolean flag = Math.abs(n - this.encodedRotationYaw) >= 4 || Math.abs(n2 - this.encodedRotationPitch) >= 4;
                if (flag) {
                    this.handlePacket(new S16PacketEntityLook(this.trackedEntity.getEntityId(), (byte)n, (byte)n2));
                    this.encodedRotationYaw = n;
                    this.encodedRotationPitch = n2;
                }

                this.encodedPosX = this.trackedEntity.myEntitySize.multiplyBy32AndRound(this.trackedEntity.posX);
                this.encodedPosY = MathHelper.floor_double(this.trackedEntity.posY * 32.0);
                this.encodedPosZ = this.trackedEntity.myEntitySize.multiplyBy32AndRound(this.trackedEntity.posZ);
                this.sendMetadataToAllAssociatedPlayers();
                this.ridingEntity = true;
            }

            int n = MathHelper.floor_float(this.trackedEntity.getRotationYawHead() * 256.0F / 360.0F);
            if (Math.abs(n - this.lastHeadMotion) >= 4) {
                this.handlePacket(new S19PacketEntityHeadLook(this.trackedEntity, (byte)n));
                this.lastHeadMotion = n;
            }

            this.trackedEntity.isAirBorne = false;
        }

        this.updateCounter++;
        if (this.trackedEntity.velocityChanged) {
            this.handlePacket2(new S12PacketEntityVelocity(this.trackedEntity));
            this.trackedEntity.velocityChanged = false;
        }
    }

    public void sendMetadataToAllAssociatedPlayers() {
        DataWatcher dataWatcher = this.trackedEntity.getDataWatcher();
        if (dataWatcher.hasObjectChanged()) {
            this.handlePacket2(new S1CPacketEntityMetadata(this.trackedEntity.getEntityId(), dataWatcher, false));
        }

        if (this.trackedEntity instanceof EntityLivingBase) {
            ServersideAttributeMap attributeMap = (ServersideAttributeMap)((EntityLivingBase)this.trackedEntity).getAttributeMap();
            Set attributeInstanceSet = attributeMap.getAttributeInstanceSet();
            if (!attributeInstanceSet.isEmpty()) {
                this.handlePacket2(new S20PacketEntityProperties(this.trackedEntity.getEntityId(), attributeInstanceSet));
            }

            attributeInstanceSet.clear();
        }
    }

    public void handlePacket(Packet packet) {
        for (EntityPlayerMP entityPlayerMP : (Iterable<EntityPlayerMP>)this.trackingPlayers) {
            entityPlayerMP.playerNetServerHandler.sendPacket(packet);
        }
    }

    public void handlePacket2(Packet packet) {
        this.handlePacket(packet);
        if (this.trackedEntity instanceof EntityPlayerMP) {
            ((EntityPlayerMP)this.trackedEntity).playerNetServerHandler.sendPacket(packet);
        }
    }

    public void sendDestroyEntityPacketToTrackedPlayers() {
        for (EntityPlayerMP entityPlayerMP : (Iterable<EntityPlayerMP>)this.trackingPlayers) {
            entityPlayerMP.stopTracking(this.trackedEntity);
        }
    }

    public void removeFromTrackedPlayers(EntityPlayerMP entityPlayerMP) {
        if (this.trackingPlayers.contains(entityPlayerMP)) {
            entityPlayerMP.stopTracking(this.trackedEntity);
            this.trackingPlayers.remove(entityPlayerMP);
        }
    }

    public void updatePlayerEntity(EntityPlayerMP entityPlayerMP) {
        if (entityPlayerMP != this.trackedEntity) {
            double d = entityPlayerMP.posX - this.encodedPosX / 32;
            double d2 = entityPlayerMP.posZ - this.encodedPosZ / 32;
            if (d >= -this.trackingDistanceThreshold
                && d <= this.trackingDistanceThreshold
                && d2 >= -this.trackingDistanceThreshold
                && d2 <= this.trackingDistanceThreshold) {
                if (!this.trackingPlayers.contains(entityPlayerMP) && (this.isPlayerWatchingThisChunk(entityPlayerMP) || this.trackedEntity.forceSpawn)) {
                    this.trackingPlayers.add(entityPlayerMP);
                    Packet packet = this.getPacket();
                    entityPlayerMP.playerNetServerHandler.sendPacket(packet);
                    if (!this.trackedEntity.getDataWatcher().getIsBlank()) {
                        entityPlayerMP.playerNetServerHandler
                            .sendPacket(new S1CPacketEntityMetadata(this.trackedEntity.getEntityId(), this.trackedEntity.getDataWatcher(), true));
                    }

                    if (this.trackedEntity instanceof EntityLivingBase) {
                        ServersideAttributeMap attributeMap = (ServersideAttributeMap)((EntityLivingBase)this.trackedEntity).getAttributeMap();
                        Collection watchedAttributes = attributeMap.getWatchedAttributes();
                        if (!watchedAttributes.isEmpty()) {
                            entityPlayerMP.playerNetServerHandler.sendPacket(new S20PacketEntityProperties(this.trackedEntity.getEntityId(), watchedAttributes));
                        }
                    }

                    this.lastTrackedEntityMotionX = this.trackedEntity.motionX;
                    this.lastTrackedEntityMotionY = this.trackedEntity.motionY;
                    this.motionZ = this.trackedEntity.motionZ;
                    if (this.sendVelocityUpdates && !(packet instanceof S0FPacketSpawnMob)) {
                        entityPlayerMP.playerNetServerHandler
                            .sendPacket(
                                new S12PacketEntityVelocity(
                                    this.trackedEntity.getEntityId(), this.trackedEntity.motionX, this.trackedEntity.motionY, this.trackedEntity.motionZ
                                )
                            );
                    }

                    if (this.trackedEntity.ridingEntity != null) {
                        entityPlayerMP.playerNetServerHandler.sendPacket(new S1BPacketEntityAttach(0, this.trackedEntity, this.trackedEntity.ridingEntity));
                    }

                    if (this.trackedEntity instanceof EntityLiving && ((EntityLiving)this.trackedEntity).getLeashedToEntity() != null) {
                        entityPlayerMP.playerNetServerHandler
                            .sendPacket(new S1BPacketEntityAttach(1, this.trackedEntity, ((EntityLiving)this.trackedEntity).getLeashedToEntity()));
                    }

                    if (this.trackedEntity instanceof EntityLivingBase) {
                        for (int i = 0; i < 5; i++) {
                            ItemStack equipmentInSlot = ((EntityLivingBase)this.trackedEntity).getEquipmentInSlot(i);
                            if (equipmentInSlot != null) {
                                entityPlayerMP.playerNetServerHandler.sendPacket(new S04PacketEntityEquipment(this.trackedEntity.getEntityId(), i, equipmentInSlot));
                            }
                        }
                    }

                    if (this.trackedEntity instanceof EntityPlayer) {
                        EntityPlayer trackedEntity = (EntityPlayer)this.trackedEntity;
                        if (trackedEntity.isPlayerSleeping()) {
                            entityPlayerMP.playerNetServerHandler
                                .sendPacket(
                                    new S0APacketUseBed(
                                        trackedEntity,
                                        MathHelper.floor_double(this.trackedEntity.posX),
                                        MathHelper.floor_double(this.trackedEntity.posY),
                                        MathHelper.floor_double(this.trackedEntity.posZ)
                                    )
                                );
                        }
                    }

                    if (this.trackedEntity instanceof EntityLivingBase) {
                        EntityLivingBase trackedEntity = (EntityLivingBase)this.trackedEntity;

                        for (PotionEffect potionEffect : trackedEntity.getActivePotionEffects()) {
                            entityPlayerMP.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(this.trackedEntity.getEntityId(), potionEffect));
                        }
                    }
                }
            } else if (this.trackingPlayers.contains(entityPlayerMP)) {
                this.trackingPlayers.remove(entityPlayerMP);
                entityPlayerMP.stopTracking(this.trackedEntity);
            }
        }
    }

    public boolean isPlayerWatchingThisChunk(EntityPlayerMP entityPlayerMP) {
        return entityPlayerMP.getServerForPlayer().getPlayerManager().isPlayerWatchingChunk(entityPlayerMP, this.trackedEntity.chunkCoordX, this.trackedEntity.chunkCoordZ);
    }

    public void updatePlayerEntities(List list) {
        for (int i = 0; i < list.size(); i++) {
            this.updatePlayerEntity((EntityPlayerMP)list.get(i));
        }
    }

    public Packet getPacket() {
        if (this.trackedEntity.isDead) {
            logger.warn("Fetching addPacket for removed entity");
        }

        if (this.trackedEntity instanceof EntityItem) {
            return new S0EPacketSpawnObject(this.trackedEntity, 2, 1);
        }

        if (this.trackedEntity instanceof EntityPlayerMP) {
            return new S0CPacketSpawnPlayer((EntityPlayer)this.trackedEntity);
        }

        if (this.trackedEntity instanceof EntityMinecart) {
            EntityMinecart trackedEntity = (EntityMinecart)this.trackedEntity;
            return new S0EPacketSpawnObject(this.trackedEntity, 10, trackedEntity.getMinecartType());
        }

        if (this.trackedEntity instanceof EntityBoat) {
            return new S0EPacketSpawnObject(this.trackedEntity, 1);
        }

        if (this.trackedEntity instanceof IAnimals || this.trackedEntity instanceof EntityDragon) {
            this.lastHeadMotion = MathHelper.floor_float(this.trackedEntity.getRotationYawHead() * 256.0F / 360.0F);
            return new S0FPacketSpawnMob((EntityLivingBase)this.trackedEntity);
        }

        if (this.trackedEntity instanceof EntityFishHook) {
            EntityPlayer angler = ((EntityFishHook)this.trackedEntity).angler;
            return new S0EPacketSpawnObject(this.trackedEntity, 90, angler != null ? angler.getEntityId() : this.trackedEntity.getEntityId());
        }

        if (this.trackedEntity instanceof EntityArrow) {
            Entity shootingEntity = ((EntityArrow)this.trackedEntity).shootingEntity;
            return new S0EPacketSpawnObject(this.trackedEntity, 60, shootingEntity != null ? shootingEntity.getEntityId() : this.trackedEntity.getEntityId());
        }

        if (this.trackedEntity instanceof EntitySnowball) {
            return new S0EPacketSpawnObject(this.trackedEntity, 61);
        }

        if (this.trackedEntity instanceof EntityPotion) {
            return new S0EPacketSpawnObject(this.trackedEntity, 73, ((EntityPotion)this.trackedEntity).getPotionDamage());
        }

        if (this.trackedEntity instanceof EntityExpBottle) {
            return new S0EPacketSpawnObject(this.trackedEntity, 75);
        }

        if (this.trackedEntity instanceof EntityEnderPearl) {
            return new S0EPacketSpawnObject(this.trackedEntity, 65);
        }

        if (this.trackedEntity instanceof EntityEnderEye) {
            return new S0EPacketSpawnObject(this.trackedEntity, 72);
        }

        if (this.trackedEntity instanceof EntityFireworkRocket) {
            return new S0EPacketSpawnObject(this.trackedEntity, 76);
        }

        if (this.trackedEntity instanceof EntityFireball) {
            EntityFireball trackedEntity = (EntityFireball)this.trackedEntity;
            S0EPacketSpawnObject s0EPacketSpawnObject = null;
            byte b = 63;
            if (this.trackedEntity instanceof EntitySmallFireball) {
                b = 64;
            } else if (this.trackedEntity instanceof EntityWitherSkull) {
                b = 66;
            }

            if (trackedEntity.shootingEntity != null) {
                s0EPacketSpawnObject = new S0EPacketSpawnObject(this.trackedEntity, b, ((EntityFireball)this.trackedEntity).shootingEntity.getEntityId());
            } else {
                s0EPacketSpawnObject = new S0EPacketSpawnObject(this.trackedEntity, b, 0);
            }

            s0EPacketSpawnObject.setVelocityX((int)(trackedEntity.accelerationX * 8000.0));
            s0EPacketSpawnObject.setVelocityY((int)(trackedEntity.accelerationY * 8000.0));
            s0EPacketSpawnObject.setVelocityZ((int)(trackedEntity.accelerationZ * 8000.0));
            return s0EPacketSpawnObject;
        } else if (this.trackedEntity instanceof EntityEgg) {
            return new S0EPacketSpawnObject(this.trackedEntity, 62);
        } else if (this.trackedEntity instanceof EntityTNTPrimed) {
            return new S0EPacketSpawnObject(this.trackedEntity, 50);
        } else if (this.trackedEntity instanceof EntityEnderCrystal) {
            return new S0EPacketSpawnObject(this.trackedEntity, 51);
        } else if (this.trackedEntity instanceof EntityFallingBlock) {
            EntityFallingBlock trackedEntity = (EntityFallingBlock)this.trackedEntity;
            return new S0EPacketSpawnObject(this.trackedEntity, 70, Block.getIdFromBlock(trackedEntity.getBlock()) | trackedEntity.metadata << 16);
        } else if (this.trackedEntity instanceof EntityPainting) {
            return new S10PacketSpawnPainting((EntityPainting)this.trackedEntity);
        } else if (this.trackedEntity instanceof EntityItemFrame) {
            EntityItemFrame trackedEntity = (EntityItemFrame)this.trackedEntity;
            S0EPacketSpawnObject s0EPacketSpawnObject = new S0EPacketSpawnObject(this.trackedEntity, 71, trackedEntity.hangingDirection);
            s0EPacketSpawnObject.setX(MathHelper.floor_float(trackedEntity.intValue * 32));
            s0EPacketSpawnObject.setY(MathHelper.floor_float(trackedEntity.intValue2 * 32));
            s0EPacketSpawnObject.setZ(MathHelper.floor_float(trackedEntity.intValue3 * 32));
            return s0EPacketSpawnObject;
        } else if (this.trackedEntity instanceof EntityLeashKnot) {
            EntityLeashKnot trackedEntity = (EntityLeashKnot)this.trackedEntity;
            S0EPacketSpawnObject s0EPacketSpawnObject = new S0EPacketSpawnObject(this.trackedEntity, 77);
            s0EPacketSpawnObject.setX(MathHelper.floor_float(trackedEntity.intValue * 32));
            s0EPacketSpawnObject.setY(MathHelper.floor_float(trackedEntity.intValue2 * 32));
            s0EPacketSpawnObject.setZ(MathHelper.floor_float(trackedEntity.intValue3 * 32));
            return s0EPacketSpawnObject;
        } else if (this.trackedEntity instanceof EntityXPOrb) {
            return new S11PacketSpawnExperienceOrb((EntityXPOrb)this.trackedEntity);
        } else {
            throw new IllegalArgumentException("Don't know how to add " + this.trackedEntity.getClass() + "!");
        }
    }

    public void removeTrackedPlayerSymmetric(EntityPlayerMP entityPlayerMP) {
        if (this.trackingPlayers.contains(entityPlayerMP)) {
            this.trackingPlayers.remove(entityPlayerMP);
            entityPlayerMP.stopTracking(this.trackedEntity);
        }
    }
}
