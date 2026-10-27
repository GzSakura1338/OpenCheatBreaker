package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Floats;
import com.google.common.util.concurrent.Futures;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.material.Material;
import net.minecraft.command.server.CommandBlockLogic;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer.EnumChatVisibility;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C02PacketUseEntity;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;

import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import net.minecraft.network.play.client.C0EPacketClickWindow;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C10PacketCreativeInventoryAction;
import net.minecraft.network.play.client.C11PacketEnchantItem;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.network.play.client.C13PacketPlayerAbilities;
import net.minecraft.network.play.client.C14PacketTabComplete;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C18PacketSpectate;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayServer implements INetHandlerPlayServer, ITickable {
   public static Logger logger = LogManager.getLogger();
   public IntHashMap<Short> transactions = new IntHashMap<>();
   public boolean hasMoved = true;
   public MinecraftServer serverController;
   public int networkTickCount;
   public int intValue;
   public int floatingTickCount;
   public boolean flag;
   public int intValue2;
   public long lastPingTime;
   public long lastSentPingPacket;
   public int chatSpamThresholdCount;
   public int itemDropThreshold;
   public NetworkManager netManager;
   public double lastPosX;
   public double lastPosY;
   public double lastPosZ;
   public EntityPlayerMP playerEntity;

   public NetHandlerPlayServer(MinecraftServer server, NetworkManager networkManagerIn, EntityPlayerMP playerIn) {
      this.serverController = server;
      this.netManager = networkManagerIn;
      networkManagerIn.setNetHandler(this);
      this.playerEntity = playerIn;
      playerIn.playerNetServerHandler = this;
   }

   @Override
   public void update() {
      this.flag = false;
      this.networkTickCount++;
      this.serverController.theProfiler.startSection("keepAlive");
      if (this.networkTickCount - this.lastSentPingPacket > 40L) {
         this.lastSentPingPacket = this.networkTickCount;
         this.lastPingTime = this.currentTimeMillis();
         this.intValue2 = (int)this.lastPingTime;
         this.sendPacket(new S00PacketKeepAlive(this.intValue2));
      }

      this.serverController.theProfiler.endSection();
      if (this.chatSpamThresholdCount > 0) {
         this.chatSpamThresholdCount--;
      }

      if (this.itemDropThreshold > 0) {
         this.itemDropThreshold--;
      }

      if (this.playerEntity.getLastActiveTime() > 0L
         && this.serverController.getMaxPlayerIdleMinutes() > 0
         && MinecraftServer.getCurrentTimeMillis() - this.playerEntity.getLastActiveTime() > this.serverController.getMaxPlayerIdleMinutes() * 1000 * 60) {
         this.kickPlayerFromServer("You have been idle for too long!");
      }
   }

   public NetworkManager getNetworkManager() {
      return this.netManager;
   }

   public void kickPlayerFromServer(String reason) {
      final ChatComponentText chatComponentText = new ChatComponentText(reason);
      this.netManager.sendPacket(new S40PacketDisconnect(chatComponentText), new GenericFutureListener<Future<? super Void>>() {
         public void operationComplete(Future<? super Void> future) throws Exception {
            NetHandlerPlayServer.this.netManager.closeChannel(chatComponentText);
         }
      });
      this.netManager.disableAutoRead();
      Futures.getUnchecked(this.serverController.addScheduledTask(new Runnable() {
         @Override
         public void run() {
            NetHandlerPlayServer.this.netManager.checkDisconnected();
         }
      }));
   }

   @Override
   public void processInput(C0CPacketInput packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.setEntityActionState(packetIn.getStrafeSpeed(), packetIn.getForwardSpeed(), packetIn.isJumping(), packetIn.isSneaking());
   }

   public boolean validatePlayerMove(C03PacketPlayer c03PacketPlayer) {
      return !Doubles.isFinite(c03PacketPlayer.getPositionX())
         || !Doubles.isFinite(c03PacketPlayer.getPositionY())
         || !Doubles.isFinite(c03PacketPlayer.getPositionZ())
         || !Floats.isFinite(c03PacketPlayer.getPitch())
         || !Floats.isFinite(c03PacketPlayer.getYaw());
   }

   @Override
   public void processPlayer(C03PacketPlayer packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      if (this.validatePlayerMove(packetIn)) {
         this.kickPlayerFromServer("Invalid move packet received");
      } else {
         WorldServer worldServerForDimension = this.serverController.worldServerForDimension(this.playerEntity.dimension);
         this.flag = true;
         if (!this.playerEntity.playerConqueredTheEnd) {
            double posX = this.playerEntity.posX;
            double posY = this.playerEntity.posY;
            double posZ = this.playerEntity.posZ;
            double d = 0.0;
            double d2 = packetIn.getPositionX() - this.lastPosX;
            double d3 = packetIn.getPositionY() - this.lastPosY;
            double d4 = packetIn.getPositionZ() - this.lastPosZ;
            if (packetIn.isMoving()) {
               d = d2 * d2 + d3 * d3 + d4 * d4;
               if (!this.hasMoved && d < 0.25) {
                  this.hasMoved = true;
               }
            }

            if (this.hasMoved) {
               this.intValue = this.networkTickCount;
               if (this.playerEntity.ridingEntity != null) {
                  float rotationYaw = this.playerEntity.rotationYaw;
                  float rotationPitch = this.playerEntity.rotationPitch;
                  this.playerEntity.ridingEntity.updateRiderPosition();
                  double posX2 = this.playerEntity.posX;
                  double posY2 = this.playerEntity.posY;
                  double posZ2 = this.playerEntity.posZ;
                  if (packetIn.getRotating()) {
                     rotationYaw = packetIn.getYaw();
                     rotationPitch = packetIn.getPitch();
                  }

                  this.playerEntity.onGround = packetIn.isOnGround();
                  this.playerEntity.onUpdateEntity();
                  this.playerEntity.setPositionAndRotation(posX2, posY2, posZ2, rotationYaw, rotationPitch);
                  if (this.playerEntity.ridingEntity != null) {
                     this.playerEntity.ridingEntity.updateRiderPosition();
                  }

                  this.serverController.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);
                  if (this.playerEntity.ridingEntity != null) {
                     if (d > 4.0) {
                        Entity ridingEntity = this.playerEntity.ridingEntity;
                        this.playerEntity.playerNetServerHandler.sendPacket(new S18PacketEntityTeleport(ridingEntity));
                        this.setPlayerLocation(
                           this.playerEntity.posX,
                           this.playerEntity.posY,
                           this.playerEntity.posZ,
                           this.playerEntity.rotationYaw,
                           this.playerEntity.rotationPitch
                        );
                     }

                     this.playerEntity.ridingEntity.isAirBorne = true;
                  }

                  if (this.hasMoved) {
                     this.lastPosX = this.playerEntity.posX;
                     this.lastPosY = this.playerEntity.posY;
                     this.lastPosZ = this.playerEntity.posZ;
                  }

                  worldServerForDimension.updateEntity(this.playerEntity);
                  return;
               }

               if (this.playerEntity.isPlayerSleeping()) {
                  this.playerEntity.onUpdateEntity();
                  this.playerEntity
                     .setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                  worldServerForDimension.updateEntity(this.playerEntity);
                  return;
               }

               double posY2 = this.playerEntity.posY;
               this.lastPosX = this.playerEntity.posX;
               this.lastPosY = this.playerEntity.posY;
               this.lastPosZ = this.playerEntity.posZ;
               double posX2 = this.playerEntity.posX;
               double posY3 = this.playerEntity.posY;
               double posZ2 = this.playerEntity.posZ;
               float rotationYaw = this.playerEntity.rotationYaw;
               float rotationPitch = this.playerEntity.rotationPitch;
               if (packetIn.isMoving() && packetIn.getPositionY() == -999.0) {
                  packetIn.setMoving(false);
               }

               if (packetIn.isMoving()) {
                  posX2 = packetIn.getPositionX();
                  posY3 = packetIn.getPositionY();
                  posZ2 = packetIn.getPositionZ();
                  if (Math.abs(packetIn.getPositionX()) > 3.0E7 || Math.abs(packetIn.getPositionZ()) > 3.0E7) {
                     this.kickPlayerFromServer("Illegal position");
                     return;
                  }
               }

               if (packetIn.getRotating()) {
                  rotationYaw = packetIn.getYaw();
                  rotationPitch = packetIn.getPitch();
               }

               this.playerEntity.onUpdateEntity();
               this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, rotationYaw, rotationPitch);
               if (!this.hasMoved) {
                  return;
               }

               double d5 = posX2 - this.playerEntity.posX;
               double d6 = posY3 - this.playerEntity.posY;
               double d7 = posZ2 - this.playerEntity.posZ;
               double d8 = this.playerEntity.motionX * this.playerEntity.motionX
                  + this.playerEntity.motionY * this.playerEntity.motionY
                  + this.playerEntity.motionZ * this.playerEntity.motionZ;
               double d9 = d5 * d5 + d6 * d6 + d7 * d7;
               if (d9 - d8 > 100.0
                  && (!this.serverController.isSinglePlayer() || !this.serverController.getServerOwner().equals(this.playerEntity.getName()))) {
                  logger.warn(
                     this.playerEntity.getName()
                        + " moved too quickly! "
                        + d5
                        + ","
                        + d6
                        + ","
                        + d7
                        + " ("
                        + d5
                        + ", "
                        + d6
                        + ", "
                        + d7
                        + ")"
                  );
                  this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                  return;
               }

               float f = 0.0625F;
               boolean isEmpty = worldServerForDimension.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.getEntityBoundingBox().contract(f, f, f))
                  .isEmpty();
               if (this.playerEntity.onGround && !packetIn.isOnGround() && d6 > 0.0) {
                  this.playerEntity.jump();
               }

               this.playerEntity.moveEntity(d5, d6, d7);
               this.playerEntity.onGround = packetIn.isOnGround();
               d5 = posX2 - this.playerEntity.posX;
               d6 = posY3 - this.playerEntity.posY;
               if (d6 > -0.5 || d6 < 0.5) {
                  d6 = 0.0;
               }

               d7 = posZ2 - this.playerEntity.posZ;
               d9 = d5 * d5 + d6 * d6 + d7 * d7;
               boolean flag = false;
               if (d9 > 0.0625 && !this.playerEntity.isPlayerSleeping() && !this.playerEntity.theItemInWorldManager.isCreative()) {
                  flag = true;
                  logger.warn(this.playerEntity.getName() + " moved wrongly!");
               }

               this.playerEntity.setPositionAndRotation(posX2, posY3, posZ2, rotationYaw, rotationPitch);
               this.playerEntity.addMovementStat(this.playerEntity.posX - posX, this.playerEntity.posY - posY, this.playerEntity.posZ - posZ);
               if (!this.playerEntity.noClip) {
                  boolean isEmpty2 = worldServerForDimension.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.getEntityBoundingBox().contract(f, f, f))
                     .isEmpty();
                  if (isEmpty && (flag || !isEmpty2) && !this.playerEntity.isPlayerSleeping()) {
                     this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, rotationYaw, rotationPitch);
                     return;
                  }
               }

               AxisAlignedBB addCoord = this.playerEntity.getEntityBoundingBox().expand(f, f, f).addCoord(0.0, -0.55, 0.0);
               if (this.serverController.isFlightAllowed() || this.playerEntity.capabilities.allowFlying || worldServerForDimension.checkBlockCollision(addCoord)) {
                  this.floatingTickCount = 0;
               } else if (d6 >= -0.03125) {
                  this.floatingTickCount++;
                  if (this.floatingTickCount > 80) {
                     logger.warn(this.playerEntity.getName() + " was kicked for floating too long!");
                     this.kickPlayerFromServer("Flying is not enabled on this server");
                     return;
                  }
               }

               this.playerEntity.onGround = packetIn.isOnGround();
               this.serverController.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);
               this.playerEntity.handleFalling(this.playerEntity.posY - posY2, packetIn.isOnGround());
            } else if (this.networkTickCount - this.intValue > 20) {
               this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
            }
         }
      }
   }

   public void setPlayerLocation(double x, double y, double z, float yaw, float pitch) {
      this.setPlayerLocation(x, y, z, yaw, pitch, Collections.emptySet());
   }

   public void setPlayerLocation(double x, double y, double z, float yaw, float pitch, Set<EnumFlags> relativeSet) {
      this.hasMoved = false;
      this.lastPosX = x;
      this.lastPosY = y;
      this.lastPosZ = z;
      if (relativeSet.contains(EnumFlags.X)) {
         this.lastPosX = this.lastPosX + this.playerEntity.posX;
      }

      if (relativeSet.contains(EnumFlags.Y)) {
         this.lastPosY = this.lastPosY + this.playerEntity.posY;
      }

      if (relativeSet.contains(EnumFlags.Z)) {
         this.lastPosZ = this.lastPosZ + this.playerEntity.posZ;
      }

      float yaw2 = yaw;
      float pitch2 = pitch;
      if (relativeSet.contains(EnumFlags.Y_ROT)) {
         yaw2 = yaw + this.playerEntity.rotationYaw;
      }

      if (relativeSet.contains(EnumFlags.X_ROT)) {
         pitch2 = pitch + this.playerEntity.rotationPitch;
      }

      this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, yaw2, pitch2);
      this.playerEntity.playerNetServerHandler.sendPacket(new S08PacketPlayerPosLook(x, y, z, yaw, pitch, relativeSet));
   }

   @Override
   public void processPlayerDigging(C07PacketPlayerDigging packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      WorldServer worldServerForDimension = this.serverController.worldServerForDimension(this.playerEntity.dimension);
      BlockPos position = packetIn.getPosition();
      this.playerEntity.markPlayerActive();
      switch (packetIn.getStatus()) {
         case DROP_ITEM:
            if (!this.playerEntity.isSpectator()) {
               this.playerEntity.dropOneItem(false);
            }

            return;
         case DROP_ALL_ITEMS:
            if (!this.playerEntity.isSpectator()) {
               this.playerEntity.dropOneItem(true);
            }

            return;
         case RELEASE_USE_ITEM:
            this.playerEntity.stopUsingItem();
            return;
         case START_DESTROY_BLOCK:
         case ABORT_DESTROY_BLOCK:
         case STOP_DESTROY_BLOCK:
            double d = this.playerEntity.posX - (position.getX() + 0.5);
            double d2 = this.playerEntity.posY - (position.getY() + 0.5) + 1.5;
            double d3 = this.playerEntity.posZ - (position.getZ() + 0.5);
            double d4 = d * d + d2 * d2 + d3 * d3;
            if (d4 > 36.0) {
               return;
            } else if (position.getY() >= this.serverController.getBuildLimit()) {
               return;
            } else {
               if (packetIn.getStatus() == C07PacketPlayerDigging.Action.START_DESTROY_BLOCK) {
                  if (!this.serverController.isBlockProtected(worldServerForDimension, position, this.playerEntity) && worldServerForDimension.getWorldBorder().contains(position)) {
                     this.playerEntity.theItemInWorldManager.onBlockClicked(position, packetIn.getFacing());
                  } else {
                     this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(worldServerForDimension, position));
                  }
               } else {
                  if (packetIn.getStatus() == C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK) {
                     this.playerEntity.theItemInWorldManager.blockRemoving(position);
                  } else if (packetIn.getStatus() == C07PacketPlayerDigging.Action.ABORT_DESTROY_BLOCK) {
                     this.playerEntity.theItemInWorldManager.cancelDestroyingBlock();
                  }

                  if (worldServerForDimension.getBlockState(position).getBlock().getMaterial() != Material.air) {
                     this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(worldServerForDimension, position));
                  }
               }

               return;
            }
         default:
            throw new IllegalArgumentException("Invalid player action");
      }
   }

   @Override
   public void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      WorldServer worldServerForDimension = this.serverController.worldServerForDimension(this.playerEntity.dimension);
      ItemStack currentItem = this.playerEntity.inventory.getCurrentItem();
      boolean flag = false;
      BlockPos position = packetIn.getPosition();
      EnumFacing front = EnumFacing.getFront(packetIn.getPlacedBlockDirection());
      this.playerEntity.markPlayerActive();
      if (packetIn.getPlacedBlockDirection() == 255) {
         if (currentItem == null) {
            return;
         }

         this.playerEntity.theItemInWorldManager.tryUseItem(this.playerEntity, worldServerForDimension, currentItem);
      } else if (position.getY() < this.serverController.getBuildLimit() - 1 || front != EnumFacing.UP && position.getY() < this.serverController.getBuildLimit()) {
         if (this.hasMoved
            && this.playerEntity.getDistanceSq(position.getX() + 0.5, position.getY() + 0.5, position.getZ() + 0.5) < 64.0
            && !this.serverController.isBlockProtected(worldServerForDimension, position, this.playerEntity)
            && worldServerForDimension.getWorldBorder().contains(position)) {
            this.playerEntity
               .theItemInWorldManager
               .activateBlockOrUseItem(
                  this.playerEntity,
                  worldServerForDimension,
                  currentItem,
                  position,
                  front,
                  packetIn.getPlacedBlockOffsetX(),
                  packetIn.getPlacedBlockOffsetY(),
                  packetIn.getPlacedBlockOffsetZ()
               );
         }

         flag = true;
      } else {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("build.tooHigh", this.serverController.getBuildLimit());
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
         this.playerEntity.playerNetServerHandler.sendPacket(new S02PacketChat(chatComponentTranslation));
         flag = true;
      }

      if (flag) {
         this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(worldServerForDimension, position));
         this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(worldServerForDimension, position.offset(front)));
      }

      currentItem = this.playerEntity.inventory.getCurrentItem();
      if (currentItem != null && currentItem.stackSize == 0) {
         this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = null;
         currentItem = null;
      }

      if (currentItem == null || currentItem.getMaxItemUseDuration() == 0) {
         this.playerEntity.isChangingQuantityOnly = true;
         this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem] = ItemStack.copyItemStack(
            this.playerEntity.inventory.mainInventory[this.playerEntity.inventory.currentItem]
         );
         Slot slotFromInventory = this.playerEntity.openContainer.getSlotFromInventory(this.playerEntity.inventory, this.playerEntity.inventory.currentItem);
         this.playerEntity.openContainer.detectAndSendChanges();
         this.playerEntity.isChangingQuantityOnly = false;
         if (!ItemStack.areItemStacksEqual(this.playerEntity.inventory.getCurrentItem(), packetIn.getStack())) {
            this.sendPacket(new S2FPacketSetSlot(this.playerEntity.openContainer.windowId, slotFromInventory.slotNumber, this.playerEntity.inventory.getCurrentItem()));
         }
      }
   }

   @Override
   public void handleSpectate(C18PacketSpectate packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      if (this.playerEntity.isSpectator()) {
         Entity entity = null;

         for (WorldServer worldServer : this.serverController.worldServers) {
            if (worldServer != null) {
               entity = packetIn.getEntity(worldServer);
               if (entity != null) {
                  break;
               }
            }
         }

         if (entity != null) {
            this.playerEntity.setSpectatingEntity(this.playerEntity);
            this.playerEntity.mountEntity((Entity)null);
            if (entity.worldObj != this.playerEntity.worldObj) {
               WorldServer serverForPlayer = this.playerEntity.getServerForPlayer();
               WorldServer worldObj = (WorldServer)entity.worldObj;
               this.playerEntity.dimension = entity.dimension;
               this.sendPacket(
                  new S07PacketRespawn(
                     this.playerEntity.dimension,
                     serverForPlayer.getDifficulty(),
                     serverForPlayer.getWorldInfo().getTerrainType(),
                     this.playerEntity.theItemInWorldManager.getGameType()
                  )
               );
               serverForPlayer.removePlayerEntityDangerously(this.playerEntity);
               this.playerEntity.isDead = false;
               this.playerEntity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
               if (this.playerEntity.isEntityAlive()) {
                  serverForPlayer.updateEntityWithOptionalForce(this.playerEntity, false);
                  worldObj.spawnEntityInWorld(this.playerEntity);
                  worldObj.updateEntityWithOptionalForce(this.playerEntity, false);
               }

               this.playerEntity.setWorld(worldObj);
               this.serverController.getConfigurationManager().preparePlayer(this.playerEntity, serverForPlayer);
               this.playerEntity.setPositionAndUpdate(entity.posX, entity.posY, entity.posZ);
               this.playerEntity.theItemInWorldManager.setWorld(worldObj);
               this.serverController.getConfigurationManager().updateTimeAndWeatherForPlayer(this.playerEntity, worldObj);
               this.serverController.getConfigurationManager().syncPlayerInventory(this.playerEntity);
            } else {
               this.playerEntity.setPositionAndUpdate(entity.posX, entity.posY, entity.posZ);
            }
         }
      }
   }

   @Override
   public void handleResourcePackStatus(C19PacketResourcePackStatus packetIn) {
   }

   @Override
   public void onDisconnect(IChatComponent reason) {
      logger.info(this.playerEntity.getName() + " lost connection: " + reason);
      this.serverController.refreshStatusNextTick();
      ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("multiplayer.player.left", this.playerEntity.getDisplayName());
      chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.YELLOW);
      this.serverController.getConfigurationManager().sendChatMsg(chatComponentTranslation);
      this.playerEntity.mountEntityAndWakeUp();
      this.serverController.getConfigurationManager().playerLoggedOut(this.playerEntity);
      if (this.serverController.isSinglePlayer() && this.playerEntity.getName().equals(this.serverController.getServerOwner())) {
         logger.info("Stopping singleplayer server as player logged out");
         this.serverController.initiateShutdown();
      }
   }

   public void sendPacket(final Packet packetIn) {
      if (packetIn instanceof S02PacketChat) {
         S02PacketChat packetIn2 = (S02PacketChat)packetIn;
         EnumChatVisibility chatVisibility = this.playerEntity.getChatVisibility();
         if (chatVisibility == EnumChatVisibility.HIDDEN) {
            return;
         }

         if (chatVisibility == EnumChatVisibility.SYSTEM && !packetIn2.isChat()) {
            return;
         }
      }

      try {
         this.netManager.sendPacket(packetIn);
      } catch (Throwable e) {
         CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Sending packet");
         CrashReportCategory makeCategory = makeCrashReport.makeCategory("Packet being sent");
         makeCategory.addCrashSectionCallable("Packet class", new Callable<String>() {
            public String call() throws Exception {
               return packetIn.getClass().getCanonicalName();
            }
         });
         throw new ReportedException(makeCrashReport);
      }
   }

   @Override
   public void processHeldItemChange(C09PacketHeldItemChange packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      if (packetIn.getSlotId() >= 0 && packetIn.getSlotId() < InventoryPlayer.getHotbarSize()) {
         this.playerEntity.inventory.currentItem = packetIn.getSlotId();
         this.playerEntity.markPlayerActive();
      } else {
         logger.warn(this.playerEntity.getName() + " tried to set an invalid carried item");
      }
   }

   @Override
   public void processChatMessage(C01PacketChatMessage packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      if (this.playerEntity.getChatVisibility() == EnumChatVisibility.HIDDEN) {
         ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("chat.cannotSend");
         chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.RED);
         this.sendPacket(new S02PacketChat(chatComponentTranslation));
      } else {
         this.playerEntity.markPlayerActive();
         String message = packetIn.getMessage();
         message = StringUtils.normalizeSpace(message);

         for (int i = 0; i < message.length(); i++) {
            if (!ChatAllowedCharacters.isAllowedCharacter(message.charAt(i))) {
               this.kickPlayerFromServer("Illegal characters in chat");
               return;
            }
         }

         if (message.startsWith("/")) {
            this.handleSlashCommand(message);
         } else {
            ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("chat.type.text", this.playerEntity.getDisplayName(), message);
            this.serverController.getConfigurationManager().sendChatMsgImpl(chatComponentTranslation, false);
         }

         this.chatSpamThresholdCount += 20;
         if (this.chatSpamThresholdCount > 200 && !this.serverController.getConfigurationManager().canSendCommands(this.playerEntity.getGameProfile())) {
            this.kickPlayerFromServer("disconnect.spam");
         }
      }
   }

   public void handleSlashCommand(String command) {
      this.serverController.getCommandManager().executeCommand(this.playerEntity, command);
   }

   @Override
   public void handleAnimation(C0APacketAnimation packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      this.playerEntity.swingItem();
   }

   @Override
   public void processEntityAction(C0BPacketEntityAction packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      switch (packetIn.getAction()) {
         case START_SNEAKING:
            this.playerEntity.setSneaking(true);
            break;
         case STOP_SNEAKING:
            this.playerEntity.setSneaking(false);
            break;
         case START_SPRINTING:
            this.playerEntity.setSprinting(true);
            break;
         case STOP_SPRINTING:
            this.playerEntity.setSprinting(false);
            break;
         case STOP_SLEEPING:
            this.playerEntity.wakeUpPlayer(false, true, true);
            this.hasMoved = false;
            break;
         case RIDING_JUMP:
            if (this.playerEntity.ridingEntity instanceof EntityHorse) {
               ((EntityHorse)this.playerEntity.ridingEntity).setJumpPower(packetIn.getAuxData());
            }
            break;
         case OPEN_INVENTORY:
            if (this.playerEntity.ridingEntity instanceof EntityHorse) {
               ((EntityHorse)this.playerEntity.ridingEntity).openGUI(this.playerEntity);
            }
            break;
         default:
            throw new IllegalArgumentException("Invalid client command!");
      }
   }

   @Override
   public void processUseEntity(C02PacketUseEntity packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      WorldServer worldServerForDimension = this.serverController.worldServerForDimension(this.playerEntity.dimension);
      Entity entityFromWorld = packetIn.getEntityFromWorld(worldServerForDimension);
      this.playerEntity.markPlayerActive();
      if (entityFromWorld != null) {
         boolean canEntityBeSeen = this.playerEntity.canEntityBeSeen(entityFromWorld);
         double d = 36.0;
         if (!canEntityBeSeen) {
            d = 9.0;
         }

         if (this.playerEntity.getDistanceSqToEntity(entityFromWorld) < d) {
            if (packetIn.getAction() == C02PacketUseEntity.Action.INTERACT) {
               this.playerEntity.interactWith(entityFromWorld);
            } else if (packetIn.getAction() == C02PacketUseEntity.Action.INTERACT_AT) {
               entityFromWorld.interactAt(this.playerEntity, packetIn.getHitVec());
            } else if (packetIn.getAction() == C02PacketUseEntity.Action.ATTACK) {
               if (entityFromWorld instanceof EntityItem || entityFromWorld instanceof EntityXPOrb || entityFromWorld instanceof EntityArrow || entityFromWorld == this.playerEntity) {
                  this.kickPlayerFromServer("Attempting to attack an invalid entity");
                  this.serverController.logWarning("Player " + this.playerEntity.getName() + " tried to attack an invalid entity");
                  return;
               }

               this.playerEntity.attackTargetEntityWithCurrentItem(entityFromWorld);
            }
         }
      }
   }

   @Override
   public void processClientStatus(C16PacketClientStatus packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      EnumState status = packetIn.getStatus();
      switch (status) {
         case PERFORM_RESPAWN:
            if (this.playerEntity.playerConqueredTheEnd) {
               this.playerEntity = this.serverController.getConfigurationManager().recreatePlayerEntity(this.playerEntity, 0, true);
            } else if (this.playerEntity.getServerForPlayer().getWorldInfo().isHardcoreModeEnabled()) {
               if (this.serverController.isSinglePlayer() && this.playerEntity.getName().equals(this.serverController.getServerOwner())) {
                  this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it's game over!");
                  this.serverController.deleteWorldAndStopServer();
               } else {
                  UserListBansEntry userListBansEntry = new UserListBansEntry(
                     this.playerEntity.getGameProfile(), (Date)null, "(You just lost the game)", (Date)null, "Death in Hardcore"
                  );
                  this.serverController.getConfigurationManager().getBannedPlayers().addEntry(userListBansEntry);
                  this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it's game over!");
               }
            } else {
               if (this.playerEntity.getHealth() > 0.0F) {
                  return;
               }

               this.playerEntity = this.serverController.getConfigurationManager().recreatePlayerEntity(this.playerEntity, 0, false);
            }
            break;
         case REQUEST_STATS:
            this.playerEntity.getStatFile().handleEntityPlayerMP(this.playerEntity);
            break;
         case OPEN_INVENTORY_ACHIEVEMENT:
            this.playerEntity.triggerAchievement(AchievementList.openInventory);
      }
   }

   @Override
   public void processCloseWindow(C0DPacketCloseWindow packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.closeContainer();
   }

   @Override
   public void processClickWindow(C0EPacketClickWindow packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      if (this.playerEntity.openContainer.windowId == packetIn.getWindowId() && this.playerEntity.openContainer.getCanCraft(this.playerEntity)) {
         if (this.playerEntity.isSpectator()) {
            ArrayList newArrayList = Lists.newArrayList();

            for (int i = 0; i < this.playerEntity.openContainer.inventorySlots.size(); i++) {
               newArrayList.add(this.playerEntity.openContainer.inventorySlots.get(i).getStack());
            }

            this.playerEntity.updateCraftingInventory(this.playerEntity.openContainer, newArrayList);
         } else {
            ItemStack slotClick = this.playerEntity.openContainer.slotClick(packetIn.getSlotId(), packetIn.getUsedButton(), packetIn.getMode(), this.playerEntity);
            if (ItemStack.areItemStacksEqual(packetIn.getClickedItem(), slotClick)) {
               this.playerEntity.playerNetServerHandler.sendPacket(new S32PacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber(), true));
               this.playerEntity.isChangingQuantityOnly = true;
               this.playerEntity.openContainer.detectAndSendChanges();
               this.playerEntity.updateHeldItem();
               this.playerEntity.isChangingQuantityOnly = false;
            } else {
               this.transactions.addKey(this.playerEntity.openContainer.windowId, packetIn.getActionNumber());
               this.playerEntity.playerNetServerHandler.sendPacket(new S32PacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber(), false));
               this.playerEntity.openContainer.setCanCraft(this.playerEntity, false);
               ArrayList newArrayList = Lists.newArrayList();

               for (int i = 0; i < this.playerEntity.openContainer.inventorySlots.size(); i++) {
                  newArrayList.add(this.playerEntity.openContainer.inventorySlots.get(i).getStack());
               }

               this.playerEntity.updateCraftingInventory(this.playerEntity.openContainer, newArrayList);
            }
         }
      }
   }

   @Override
   public void processEnchantItem(C11PacketEnchantItem packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      if (this.playerEntity.openContainer.windowId == packetIn.getWindowId()
         && this.playerEntity.openContainer.getCanCraft(this.playerEntity)
         && !this.playerEntity.isSpectator()) {
         this.playerEntity.openContainer.enchantItem(this.playerEntity, packetIn.getButton());
         this.playerEntity.openContainer.detectAndSendChanges();
      }
   }

   @Override
   public void processCreativeInventoryAction(C10PacketCreativeInventoryAction packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      if (this.playerEntity.theItemInWorldManager.isCreative()) {
         boolean flag = packetIn.getSlotId() < 0;
         ItemStack stack = packetIn.getStack();
         if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey("BlockEntityTag", 10)) {
            NBTTagCompound compoundTag = stack.getTagCompound().getCompoundTag("BlockEntityTag");
            if (compoundTag.hasKey("x") && compoundTag.hasKey("y") && compoundTag.hasKey("z")) {
               BlockPos blockPos = new BlockPos(compoundTag.getInteger("x"), compoundTag.getInteger("y"), compoundTag.getInteger("z"));
               TileEntity tileEntity = this.playerEntity.worldObj.getTileEntity(blockPos);
               if (tileEntity != null) {
                  NBTTagCompound nbttagCompound = new NBTTagCompound();
                  tileEntity.writeToNBT(nbttagCompound);
                  nbttagCompound.removeTag("x");
                  nbttagCompound.removeTag("y");
                  nbttagCompound.removeTag("z");
                  stack.setTagInfo("BlockEntityTag", nbttagCompound);
               }
            }
         }

         boolean flag2 = packetIn.getSlotId() >= 1 && packetIn.getSlotId() < 36 + InventoryPlayer.getHotbarSize();
         boolean flag3 = stack == null || stack.getItem() != null;
         boolean flag4 = stack == null || stack.getMetadata() >= 0 && stack.stackSize <= 64 && stack.stackSize > 0;
         if (flag2 && flag3 && flag4) {
            if (stack == null) {
               this.playerEntity.inventoryContainer.putStackInSlot(packetIn.getSlotId(), (ItemStack)null);
            } else {
               this.playerEntity.inventoryContainer.putStackInSlot(packetIn.getSlotId(), stack);
            }

            this.playerEntity.inventoryContainer.setCanCraft(this.playerEntity, true);
         } else if (flag && flag3 && flag4 && this.itemDropThreshold < 200) {
            this.itemDropThreshold += 20;
            EntityItem dropPlayerItemWithRandomChoice = this.playerEntity.dropPlayerItemWithRandomChoice(stack, true);
            if (dropPlayerItemWithRandomChoice != null) {
               dropPlayerItemWithRandomChoice.setAgeToCreativeDespawnTime();
            }
         }
      }
   }

   @Override
   public void processConfirmTransaction(C0FPacketConfirmTransaction packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      Short lookup = this.transactions.lookup(this.playerEntity.openContainer.windowId);
      if (lookup != null
         && packetIn.getUid() == lookup
         && this.playerEntity.openContainer.windowId == packetIn.getWindowId()
         && !this.playerEntity.openContainer.getCanCraft(this.playerEntity)
         && !this.playerEntity.isSpectator()) {
         this.playerEntity.openContainer.setCanCraft(this.playerEntity, true);
      }
   }

   @Override
   public void processUpdateSign(C12PacketUpdateSign packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.markPlayerActive();
      WorldServer worldServerForDimension = this.serverController.worldServerForDimension(this.playerEntity.dimension);
      BlockPos position = packetIn.getPosition();
      if (worldServerForDimension.isBlockLoaded(position)) {
         TileEntity tileEntity = worldServerForDimension.getTileEntity(position);
         if (!(tileEntity instanceof TileEntitySign)) {
            return;
         }

         TileEntitySign tileEntitySign = (TileEntitySign)tileEntity;
         if (!tileEntitySign.getIsEditable() || tileEntitySign.getPlayer() != this.playerEntity) {
            this.serverController.logWarning("Player " + this.playerEntity.getName() + " just tried to change non-editable sign");
            return;
         }

         IChatComponent[] lines = packetIn.getLines();

         for (int i = 0; i < lines.length; i++) {
            tileEntitySign.signText[i] = new ChatComponentText(EnumChatFormatting.getTextWithoutFormattingCodes(lines[i].getUnformattedText()));
         }

         tileEntitySign.markDirty();
         worldServerForDimension.markBlockForUpdate(position);
      }
   }

   @Override
   public void processKeepAlive(C00PacketKeepAlive packetIn) {
      if (packetIn.getKey() == this.intValue2) {
         int n = (int)(this.currentTimeMillis() - this.lastPingTime);
         this.playerEntity.ping = (this.playerEntity.ping * 3 + n) / 4;
      }
   }

   public long currentTimeMillis() {
      return System.nanoTime() / 1000000L;
   }

   @Override
   public void processPlayerAbilities(C13PacketPlayerAbilities packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.capabilities.isFlying = packetIn.isFlying() && this.playerEntity.capabilities.allowFlying;
   }

   @Override
   public void processTabComplete(C14PacketTabComplete packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      ArrayList newArrayList = Lists.newArrayList();

      for (String str : this.serverController.getTabCompletions(this.playerEntity, packetIn.getMessage(), packetIn.getTargetBlock())) {
         newArrayList.add(str);
      }

      this.playerEntity.playerNetServerHandler.sendPacket(new S3APacketTabComplete((String[])newArrayList.toArray(new String[newArrayList.size()])));
   }

   @Override
   public void processClientSettings(C15PacketClientSettings packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      this.playerEntity.handleClientSettings(packetIn);
   }

   @Override
   public void processVanilla250Packet(C17PacketCustomPayload packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.playerEntity.getServerForPlayer());
      if ("MC|BEdit".equals(packetIn.getChannelName())) {
         PacketBuffer packetBuffer = new PacketBuffer(Unpooled.wrappedBuffer(packetIn.getBufferData()));

         try {
            ItemStack readItemStackFromBuffer = packetBuffer.readItemStackFromBuffer();
            if (readItemStackFromBuffer == null) {
               return;
            }

            if (!ItemWritableBook.isNBTValid(readItemStackFromBuffer.getTagCompound())) {
               throw new IOException("Invalid book tag!");
            }

            ItemStack currentItem = this.playerEntity.inventory.getCurrentItem();
            if (currentItem == null) {
               return;
            }

            if (readItemStackFromBuffer.getItem() == Items.writable_book && readItemStackFromBuffer.getItem() == currentItem.getItem()) {
               currentItem.setTagInfo("pages", readItemStackFromBuffer.getTagCompound().getTagList("pages", 8));
            }
         } catch (Exception e) {
            logger.error("Couldn't handle book info", e);
            return;
         } finally {
            packetBuffer.release();
         }
      } else if ("MC|BSign".equals(packetIn.getChannelName())) {
         PacketBuffer packetBuffer = new PacketBuffer(Unpooled.wrappedBuffer(packetIn.getBufferData()));

         try {
            ItemStack readItemStackFromBuffer = packetBuffer.readItemStackFromBuffer();
            if (readItemStackFromBuffer == null) {
               return;
            }

            if (!ItemEditableBook.validBookTagContents(readItemStackFromBuffer.getTagCompound())) {
               throw new IOException("Invalid book tag!");
            }

            ItemStack currentItem = this.playerEntity.inventory.getCurrentItem();
            if (currentItem == null) {
               return;
            }

            if (readItemStackFromBuffer.getItem() == Items.written_book && currentItem.getItem() == Items.writable_book) {
               currentItem.setTagInfo("author", new NBTTagString(this.playerEntity.getName()));
               currentItem.setTagInfo("title", new NBTTagString(readItemStackFromBuffer.getTagCompound().getString("title")));
               currentItem.setTagInfo("pages", readItemStackFromBuffer.getTagCompound().getTagList("pages", 8));
               currentItem.setItem(Items.written_book);
            }
         } catch (Exception e) {
            logger.error("Couldn't sign book", e);
            return;
         } finally {
            packetBuffer.release();
         }
      } else {
         if ("MC|TrSel".equals(packetIn.getChannelName())) {
            try {
               int readInt = packetIn.getBufferData().readInt();
               Container openContainer = this.playerEntity.openContainer;
               if (openContainer instanceof ContainerMerchant) {
                  ((ContainerMerchant)openContainer).setCurrentRecipeIndex(readInt);
               }
            } catch (Exception e) {
               logger.error("Couldn't select trade", e);
            }
         } else if ("MC|AdvCdm".equals(packetIn.getChannelName())) {
            if (!this.serverController.isCommandBlockEnabled()) {
               this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notEnabled"));
            } else if (this.playerEntity.canCommandSenderUseCommand(2, "") && this.playerEntity.capabilities.isCreativeMode) {
               PacketBuffer bufferData = packetIn.getBufferData();

               try {
                  byte readByte = bufferData.readByte();
                  CommandBlockLogic commandBlockLogic = null;
                  if (readByte == 0) {
                     TileEntity tileEntity = this.playerEntity.worldObj.getTileEntity(new BlockPos(bufferData.readInt(), bufferData.readInt(), bufferData.readInt()));
                     if (tileEntity instanceof TileEntityCommandBlock) {
                        commandBlockLogic = ((TileEntityCommandBlock)tileEntity).getCommandBlockLogic();
                     }
                  } else if (readByte == 1) {
                     Entity entityByID = this.playerEntity.worldObj.getEntityByID(bufferData.readInt());
                     if (entityByID instanceof EntityMinecartCommandBlock) {
                        commandBlockLogic = ((EntityMinecartCommandBlock)entityByID).getCommandBlockLogic();
                     }
                  }

                  String readStringFromBuffer = bufferData.readStringFromBuffer(bufferData.readableBytes());
                  boolean readBoolean = bufferData.readBoolean();
                  if (commandBlockLogic != null) {
                     commandBlockLogic.setCommand(readStringFromBuffer);
                     commandBlockLogic.setTrackOutput(readBoolean);
                     if (!readBoolean) {
                        commandBlockLogic.setLastOutput((IChatComponent)null);
                     }

                     commandBlockLogic.updateCommand();
                     this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.setCommand.success", readStringFromBuffer));
                  }
               } catch (Exception e) {
                  logger.error("Couldn't set command block", e);
               } finally {
                  bufferData.release();
               }
            } else {
               this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notAllowed"));
            }
         } else if ("MC|Beacon".equals(packetIn.getChannelName())) {
            if (this.playerEntity.openContainer instanceof ContainerBeacon) {
               try {
                  PacketBuffer bufferData = packetIn.getBufferData();
                  int readInt = bufferData.readInt();
                  int readInt2 = bufferData.readInt();
                  ContainerBeacon openContainer = (ContainerBeacon)this.playerEntity.openContainer;
                  Slot slot = openContainer.getSlot(0);
                  if (slot.getHasStack()) {
                     slot.decrStackSize(1);
                     IInventory paymentInventory = openContainer.getPaymentInventory();
                     paymentInventory.setField(1, readInt);
                     paymentInventory.setField(2, readInt2);
                     paymentInventory.markDirty();
                  }
               } catch (Exception e) {
                  logger.error("Couldn't set beacon", e);
               }
            }
         } else if ("MC|ItemName".equals(packetIn.getChannelName()) && this.playerEntity.openContainer instanceof ContainerRepair) {
            ContainerRepair openContainer = (ContainerRepair)this.playerEntity.openContainer;
            if (packetIn.getBufferData() != null && packetIn.getBufferData().readableBytes() >= 1) {
               String filterAllowedCharacters = ChatAllowedCharacters.filterAllowedCharacters(packetIn.getBufferData().readStringFromBuffer(32767));
               if (filterAllowedCharacters.length() <= 30) {
                  openContainer.updateItemName(filterAllowedCharacters);
               }
            } else {
               openContainer.updateItemName("");
            }
         }
      }
   }
}
