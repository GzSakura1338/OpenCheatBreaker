package net.minecraft.entity;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.ai.attributes.ServersideAttributeMap;
import net.minecraft.entity.item.EntityArmorStand;
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
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S14PacketEntity.S15PacketEntityRelMove;
import net.minecraft.network.play.server.S14PacketEntity.S16PacketEntityLook;
import net.minecraft.network.play.server.S14PacketEntity.S17PacketEntityLookMove;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EntityTrackerEntry {
   public static Logger logger = LogManager.getLogger();
   public Set<EntityPlayerMP> trackingPlayers = Sets.newHashSet();
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
   public int lastHeadMotion;
   public boolean playerEntitiesUpdated;
   public boolean onGround;

   public EntityTrackerEntry(Entity trackedEntityIn, int trackingDistanceThresholdIn, int updateFrequencyIn, boolean sendVelocityUpdatesIn) {
      this.trackedEntity = trackedEntityIn;
      this.trackingDistanceThreshold = trackingDistanceThresholdIn;
      this.updateFrequency = updateFrequencyIn;
      this.sendVelocityUpdates = sendVelocityUpdatesIn;
      this.encodedPosX = MathHelper.floor_double(trackedEntityIn.posX * 32.0);
      this.encodedPosY = MathHelper.floor_double(trackedEntityIn.posY * 32.0);
      this.encodedPosZ = MathHelper.floor_double(trackedEntityIn.posZ * 32.0);
      this.encodedRotationYaw = MathHelper.floor_float(trackedEntityIn.rotationYaw * 256.0F / 360.0F);
      this.encodedRotationPitch = MathHelper.floor_float(trackedEntityIn.rotationPitch * 256.0F / 360.0F);
      this.lastHeadMotion = MathHelper.floor_float(trackedEntityIn.getRotationYawHead() * 256.0F / 360.0F);
      this.onGround = trackedEntityIn.onGround;
   }

   @Override
   public boolean equals(Object obj) {
      return obj instanceof EntityTrackerEntry ? ((EntityTrackerEntry)obj).trackedEntity.getEntityId() == this.trackedEntity.getEntityId() : false;
   }

   @Override
   public int hashCode() {
      return this.trackedEntity.getEntityId();
   }

   public void updatePlayerList(List<EntityPlayer> players) {
      this.playerEntitiesUpdated = false;
      if (!this.firstUpdateDone || this.trackedEntity.getDistanceSq(this.lastTrackedEntityPosX, this.lastTrackedEntityPosY, this.lastTrackedEntityPosZ) > 16.0) {
         this.lastTrackedEntityPosX = this.trackedEntity.posX;
         this.lastTrackedEntityPosY = this.trackedEntity.posY;
         this.lastTrackedEntityPosZ = this.trackedEntity.posZ;
         this.firstUpdateDone = true;
         this.playerEntitiesUpdated = true;
         this.updatePlayerEntities(players);
      }

      if (this.entity != this.trackedEntity.ridingEntity || this.trackedEntity.ridingEntity != null && this.updateCounter % 60 == 0) {
         this.entity = this.trackedEntity.ridingEntity;
         this.sendPacketToTrackedPlayers(new S1BPacketEntityAttach(0, this.trackedEntity, this.trackedEntity.ridingEntity));
      }

      if (this.trackedEntity instanceof EntityItemFrame && this.updateCounter % 10 == 0) {
         EntityItemFrame trackedEntity = (EntityItemFrame)this.trackedEntity;
         ItemStack displayedItem = trackedEntity.getDisplayedItem();
         if (displayedItem != null && displayedItem.getItem() instanceof ItemMap) {
            MapData mapData = Items.filled_map.getMapData(displayedItem, this.trackedEntity.worldObj);

            for (EntityPlayer entityPlayer : players) {
               EntityPlayerMP entityPlayerMP = (EntityPlayerMP)entityPlayer;
               mapData.updateVisiblePlayers(entityPlayerMP, displayedItem);
               Packet createMapDataPacket = Items.filled_map.createMapDataPacket(displayedItem, this.trackedEntity.worldObj, entityPlayerMP);
               if (createMapDataPacket != null) {
                  entityPlayerMP.playerNetServerHandler.sendPacket(createMapDataPacket);
               }
            }
         }

         this.sendMetadataToAllAssociatedPlayers();
      }

      if (this.updateCounter % this.updateFrequency == 0 || this.trackedEntity.isAirBorne || this.trackedEntity.getDataWatcher().hasObjectChanged()) {
         if (this.trackedEntity.ridingEntity == null) {
            this.ticksSinceLastForcedTeleport++;
            int n = MathHelper.floor_double(this.trackedEntity.posX * 32.0);
            int n2 = MathHelper.floor_double(this.trackedEntity.posY * 32.0);
            int n3 = MathHelper.floor_double(this.trackedEntity.posZ * 32.0);
            int n4 = MathHelper.floor_float(this.trackedEntity.rotationYaw * 256.0F / 360.0F);
            int n5 = MathHelper.floor_float(this.trackedEntity.rotationPitch * 256.0F / 360.0F);
            int n6 = n - this.encodedPosX;
            int n7 = n2 - this.encodedPosY;
            int n8 = n3 - this.encodedPosZ;
            Packet packet = null;
            boolean flag = Math.abs(n6) >= 4 || Math.abs(n7) >= 4 || Math.abs(n8) >= 4 || this.updateCounter % 60 == 0;
            boolean flag2 = Math.abs(n4 - this.encodedRotationYaw) >= 4 || Math.abs(n5 - this.encodedRotationPitch) >= 4;
            if (this.updateCounter > 0 || this.trackedEntity instanceof EntityArrow) {
               if (n6 >= -128
                  && n6 < 128
                  && n7 >= -128
                  && n7 < 128
                  && n8 >= -128
                  && n8 < 128
                  && this.ticksSinceLastForcedTeleport <= 400
                  && !this.ridingEntity
                  && this.onGround == this.trackedEntity.onGround) {
                  if ((!flag || !flag2) && !(this.trackedEntity instanceof EntityArrow)) {
                     if (flag) {
                        packet = new S15PacketEntityRelMove(
                           this.trackedEntity.getEntityId(), (byte)n6, (byte)n7, (byte)n8, this.trackedEntity.onGround
                        );
                     } else if (flag2) {
                        packet = new S16PacketEntityLook(this.trackedEntity.getEntityId(), (byte)n4, (byte)n5, this.trackedEntity.onGround);
                     }
                  } else {
                     packet = new S17PacketEntityLookMove(
                        this.trackedEntity.getEntityId(), (byte)n6, (byte)n7, (byte)n8, (byte)n4, (byte)n5, this.trackedEntity.onGround
                     );
                  }
               } else {
                  this.onGround = this.trackedEntity.onGround;
                  this.ticksSinceLastForcedTeleport = 0;
                  packet = new S18PacketEntityTeleport(
                     this.trackedEntity.getEntityId(), n, n2, n3, (byte)n4, (byte)n5, this.trackedEntity.onGround
                  );
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
                  this.sendPacketToTrackedPlayers(
                     new S12PacketEntityVelocity(this.trackedEntity.getEntityId(), this.lastTrackedEntityMotionX, this.lastTrackedEntityMotionY, this.motionZ)
                  );
               }
            }

            if (packet != null) {
               this.sendPacketToTrackedPlayers(packet);
            }

            this.sendMetadataToAllAssociatedPlayers();
            if (flag) {
               this.encodedPosX = n;
               this.encodedPosY = n2;
               this.encodedPosZ = n3;
            }

            if (flag2) {
               this.encodedRotationYaw = n4;
               this.encodedRotationPitch = n5;
            }

            this.ridingEntity = false;
         } else {
            int n = MathHelper.floor_float(this.trackedEntity.rotationYaw * 256.0F / 360.0F);
            int n2 = MathHelper.floor_float(this.trackedEntity.rotationPitch * 256.0F / 360.0F);
            boolean flag = Math.abs(n - this.encodedRotationYaw) >= 4 || Math.abs(n2 - this.encodedRotationPitch) >= 4;
            if (flag) {
               this.sendPacketToTrackedPlayers(
                  new S16PacketEntityLook(this.trackedEntity.getEntityId(), (byte)n, (byte)n2, this.trackedEntity.onGround)
               );
               this.encodedRotationYaw = n;
               this.encodedRotationPitch = n2;
            }

            this.encodedPosX = MathHelper.floor_double(this.trackedEntity.posX * 32.0);
            this.encodedPosY = MathHelper.floor_double(this.trackedEntity.posY * 32.0);
            this.encodedPosZ = MathHelper.floor_double(this.trackedEntity.posZ * 32.0);
            this.sendMetadataToAllAssociatedPlayers();
            this.ridingEntity = true;
         }

         int n = MathHelper.floor_float(this.trackedEntity.getRotationYawHead() * 256.0F / 360.0F);
         if (Math.abs(n - this.lastHeadMotion) >= 4) {
            this.sendPacketToTrackedPlayers(new S19PacketEntityHeadLook(this.trackedEntity, (byte)n));
            this.lastHeadMotion = n;
         }

         this.trackedEntity.isAirBorne = false;
      }

      this.updateCounter++;
      if (this.trackedEntity.velocityChanged) {
         this.handlePacket(new S12PacketEntityVelocity(this.trackedEntity));
         this.trackedEntity.velocityChanged = false;
      }
   }

   public void sendMetadataToAllAssociatedPlayers() {
      DataWatcher dataWatcher = this.trackedEntity.getDataWatcher();
      if (dataWatcher.hasObjectChanged()) {
         this.handlePacket(new S1CPacketEntityMetadata(this.trackedEntity.getEntityId(), dataWatcher, false));
      }

      if (this.trackedEntity instanceof EntityLivingBase) {
         ServersideAttributeMap attributeMap = (ServersideAttributeMap)((EntityLivingBase)this.trackedEntity).getAttributeMap();
         Set attributeInstanceSet = attributeMap.getAttributeInstanceSet();
         if (!attributeInstanceSet.isEmpty()) {
            this.handlePacket(new S20PacketEntityProperties(this.trackedEntity.getEntityId(), attributeInstanceSet));
         }

         attributeInstanceSet.clear();
      }
   }

   public void sendPacketToTrackedPlayers(Packet packetIn) {
      for (EntityPlayerMP entityPlayerMP : this.trackingPlayers) {
         entityPlayerMP.playerNetServerHandler.sendPacket(packetIn);
      }
   }

   public void handlePacket(Packet packetIn) {
      this.sendPacketToTrackedPlayers(packetIn);
      if (this.trackedEntity instanceof EntityPlayerMP) {
         ((EntityPlayerMP)this.trackedEntity).playerNetServerHandler.sendPacket(packetIn);
      }
   }

   public void sendDestroyEntityPacketToTrackedPlayers() {
      for (EntityPlayerMP entityPlayerMP : this.trackingPlayers) {
         entityPlayerMP.removeEntity(this.trackedEntity);
      }
   }

   public void removeFromTrackedPlayers(EntityPlayerMP playerMP) {
      if (this.trackingPlayers.contains(playerMP)) {
         playerMP.removeEntity(this.trackedEntity);
         this.trackingPlayers.remove(playerMP);
      }
   }

   public void updatePlayerEntity(EntityPlayerMP playerMP) {
      if (playerMP != this.trackedEntity) {
         if (this.isSpectatedByPlayer(playerMP)) {
            if (!this.trackingPlayers.contains(playerMP) && (this.isPlayerWatchingThisChunk(playerMP) || this.trackedEntity.forceSpawn)) {
               this.trackingPlayers.add(playerMP);
               Packet createSpawnPacket = this.createSpawnPacket();
               playerMP.playerNetServerHandler.sendPacket(createSpawnPacket);
               if (!this.trackedEntity.getDataWatcher().getIsBlank()) {
                  playerMP.playerNetServerHandler
                     .sendPacket(new S1CPacketEntityMetadata(this.trackedEntity.getEntityId(), this.trackedEntity.getDataWatcher(), true));
               }

               NBTTagCompound nBTTagCompound = this.trackedEntity.getNBTTagCompound();
               if (nBTTagCompound != null) {
                  playerMP.playerNetServerHandler.sendPacket(new S49PacketUpdateEntityNBT(this.trackedEntity.getEntityId(), nBTTagCompound));
               }

               if (this.trackedEntity instanceof EntityLivingBase) {
                  ServersideAttributeMap attributeMap = (ServersideAttributeMap)((EntityLivingBase)this.trackedEntity).getAttributeMap();
                  Collection watchedAttributes = attributeMap.getWatchedAttributes();
                  if (!watchedAttributes.isEmpty()) {
                     playerMP.playerNetServerHandler.sendPacket(new S20PacketEntityProperties(this.trackedEntity.getEntityId(), watchedAttributes));
                  }
               }

               this.lastTrackedEntityMotionX = this.trackedEntity.motionX;
               this.lastTrackedEntityMotionY = this.trackedEntity.motionY;
               this.motionZ = this.trackedEntity.motionZ;
               if (this.sendVelocityUpdates && !(createSpawnPacket instanceof S0FPacketSpawnMob)) {
                  playerMP.playerNetServerHandler
                     .sendPacket(
                        new S12PacketEntityVelocity(
                           this.trackedEntity.getEntityId(), this.trackedEntity.motionX, this.trackedEntity.motionY, this.trackedEntity.motionZ
                        )
                     );
               }

               if (this.trackedEntity.ridingEntity != null) {
                  playerMP.playerNetServerHandler.sendPacket(new S1BPacketEntityAttach(0, this.trackedEntity, this.trackedEntity.ridingEntity));
               }

               if (this.trackedEntity instanceof EntityLiving && ((EntityLiving)this.trackedEntity).getLeashedToEntity() != null) {
                  playerMP.playerNetServerHandler
                     .sendPacket(new S1BPacketEntityAttach(1, this.trackedEntity, ((EntityLiving)this.trackedEntity).getLeashedToEntity()));
               }

               if (this.trackedEntity instanceof EntityLivingBase) {
                  for (int i = 0; i < 5; i++) {
                     ItemStack equipmentInSlot = ((EntityLivingBase)this.trackedEntity).getEquipmentInSlot(i);
                     if (equipmentInSlot != null) {
                        playerMP.playerNetServerHandler.sendPacket(new S04PacketEntityEquipment(this.trackedEntity.getEntityId(), i, equipmentInSlot));
                     }
                  }
               }

               if (this.trackedEntity instanceof EntityPlayer) {
                  EntityPlayer trackedEntity = (EntityPlayer)this.trackedEntity;
                  if (trackedEntity.isPlayerSleeping()) {
                     playerMP.playerNetServerHandler.sendPacket(new S0APacketUseBed(trackedEntity, new BlockPos(this.trackedEntity)));
                  }
               }

               if (this.trackedEntity instanceof EntityLivingBase) {
                  EntityLivingBase trackedEntity = (EntityLivingBase)this.trackedEntity;

                  for (PotionEffect potionEffect : trackedEntity.getActivePotionEffects()) {
                     playerMP.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(this.trackedEntity.getEntityId(), potionEffect));
                  }
               }
            }
         } else if (this.trackingPlayers.contains(playerMP)) {
            this.trackingPlayers.remove(playerMP);
            playerMP.removeEntity(this.trackedEntity);
         }
      }
   }

   public boolean isSpectatedByPlayer(EntityPlayerMP playerMP) {
      double d = playerMP.posX - this.encodedPosX / 32;
      double d2 = playerMP.posZ - this.encodedPosZ / 32;
      return d >= -this.trackingDistanceThreshold
         && d <= this.trackingDistanceThreshold
         && d2 >= -this.trackingDistanceThreshold
         && d2 <= this.trackingDistanceThreshold
         && this.trackedEntity.isSpectatedByPlayer(playerMP);
   }

   public boolean isPlayerWatchingThisChunk(EntityPlayerMP playerMP) {
      return playerMP.getServerForPlayer().getPlayerManager().isPlayerWatchingChunk(playerMP, this.trackedEntity.chunkCoordX, this.trackedEntity.chunkCoordZ);
   }

   public void updatePlayerEntities(List<EntityPlayer> players) {
      for (int i = 0; i < players.size(); i++) {
         this.updatePlayerEntity((EntityPlayerMP)players.get(i));
      }
   }

   public Packet createSpawnPacket() {
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
         return new S0EPacketSpawnObject(this.trackedEntity, 10, trackedEntity.getMinecartType().getNetworkID());
      }

      if (this.trackedEntity instanceof EntityBoat) {
         return new S0EPacketSpawnObject(this.trackedEntity, 1);
      }

      if (this.trackedEntity instanceof IAnimals) {
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

         s0EPacketSpawnObject.setSpeedX((int)(trackedEntity.accelerationX * 8000.0));
         s0EPacketSpawnObject.setSpeedY((int)(trackedEntity.accelerationY * 8000.0));
         s0EPacketSpawnObject.setSpeedZ((int)(trackedEntity.accelerationZ * 8000.0));
         return s0EPacketSpawnObject;
      } else if (this.trackedEntity instanceof EntityEgg) {
         return new S0EPacketSpawnObject(this.trackedEntity, 62);
      } else if (this.trackedEntity instanceof EntityTNTPrimed) {
         return new S0EPacketSpawnObject(this.trackedEntity, 50);
      } else if (this.trackedEntity instanceof EntityEnderCrystal) {
         return new S0EPacketSpawnObject(this.trackedEntity, 51);
      } else if (this.trackedEntity instanceof EntityFallingBlock) {
         EntityFallingBlock trackedEntity = (EntityFallingBlock)this.trackedEntity;
         return new S0EPacketSpawnObject(this.trackedEntity, 70, Block.getStateId(trackedEntity.getBlock()));
      } else if (this.trackedEntity instanceof EntityArmorStand) {
         return new S0EPacketSpawnObject(this.trackedEntity, 78);
      } else if (this.trackedEntity instanceof EntityPainting) {
         return new S10PacketSpawnPainting((EntityPainting)this.trackedEntity);
      } else if (this.trackedEntity instanceof EntityItemFrame) {
         EntityItemFrame trackedEntity = (EntityItemFrame)this.trackedEntity;
         S0EPacketSpawnObject s0EPacketSpawnObject = new S0EPacketSpawnObject(this.trackedEntity, 71, trackedEntity.facingDirection.getHorizontalIndex());
         BlockPos hangingPosition = trackedEntity.getHangingPosition();
         s0EPacketSpawnObject.setX(MathHelper.floor_float(hangingPosition.getX() * 32));
         s0EPacketSpawnObject.setY(MathHelper.floor_float(hangingPosition.getY() * 32));
         s0EPacketSpawnObject.setZ(MathHelper.floor_float(hangingPosition.getZ() * 32));
         return s0EPacketSpawnObject;
      } else if (this.trackedEntity instanceof EntityLeashKnot) {
         EntityLeashKnot trackedEntity = (EntityLeashKnot)this.trackedEntity;
         S0EPacketSpawnObject s0EPacketSpawnObject = new S0EPacketSpawnObject(this.trackedEntity, 77);
         BlockPos hangingPosition = trackedEntity.getHangingPosition();
         s0EPacketSpawnObject.setX(MathHelper.floor_float(hangingPosition.getX() * 32));
         s0EPacketSpawnObject.setY(MathHelper.floor_float(hangingPosition.getY() * 32));
         s0EPacketSpawnObject.setZ(MathHelper.floor_float(hangingPosition.getZ() * 32));
         return s0EPacketSpawnObject;
      } else if (this.trackedEntity instanceof EntityXPOrb) {
         return new S11PacketSpawnExperienceOrb((EntityXPOrb)this.trackedEntity);
      } else {
         throw new IllegalArgumentException("Don't know how to add " + this.trackedEntity.getClass() + "!");
      }
   }

   public void removeTrackedPlayerSymmetric(EntityPlayerMP playerMP) {
      if (this.trackingPlayers.contains(playerMP)) {
         this.trackingPlayers.remove(playerMP);
         playerMP.removeEntity(this.trackedEntity);
      }
   }
}
