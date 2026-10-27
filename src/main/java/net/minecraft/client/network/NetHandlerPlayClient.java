package net.minecraft.client.network;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.EntityS19PacketEntityStatusEvent;
import com.cheatbreaker.client.event.PacketEvent;
import com.cheatbreaker.client.event.S01PacketJoinGameEvent;
import com.cheatbreaker.client.event.S02PacketChatEvent;
import com.cheatbreaker.client.event.S03PacketTimeUpdateEvent;
import com.cheatbreaker.client.event.S06PacketUpdateHealthEvent;
import com.cheatbreaker.client.event.SimpleEvent12;
import com.cheatbreaker.client.event.SimpleEvent20;
import com.cheatbreaker.client.event.SimpleEvent3;
import com.cheatbreaker.client.event.StringByteArrayEvent;
import com.cheatbreaker.client.mod.impl.ToggleSprintMod;
import com.cheatbreaker.client.ui.screen.MainMenuScreen;
import com.google.common.collect.Maps;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.GuardianSound;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenBook;
import net.minecraft.client.gui.GuiScreenDemo;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData.ServerResourceMode;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityPickupFX;
import net.minecraft.client.player.inventory.ContainerLocalMenu;
import net.minecraft.client.player.inventory.LocalBlockIntercommunication;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.NpcMerchant;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
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
import net.minecraft.entity.item.EntityMinecart.EnumMinecartType;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.item.EntityTNTPrimed;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEgg;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.entity.projectile.EntitySmallFireball;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Items;
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C19PacketResourcePackStatus;

import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S08PacketPlayerPosLook.EnumFlags;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S0APacketUseBed;
import net.minecraft.network.play.server.S0BPacketAnimation;
import net.minecraft.network.play.server.S0CPacketSpawnPlayer;
import net.minecraft.network.play.server.S0DPacketCollectItem;
import net.minecraft.network.play.server.S0EPacketSpawnObject;
import net.minecraft.network.play.server.S0FPacketSpawnMob;
import net.minecraft.network.play.server.S10PacketSpawnPainting;
import net.minecraft.network.play.server.S11PacketSpawnExperienceOrb;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S13PacketDestroyEntities;
import net.minecraft.network.play.server.S14PacketEntity;
import net.minecraft.network.play.server.S18PacketEntityTeleport;
import net.minecraft.network.play.server.S19PacketEntityHeadLook;
import net.minecraft.network.play.server.S19PacketEntityStatus;
import net.minecraft.network.play.server.S1BPacketEntityAttach;
import net.minecraft.network.play.server.S1CPacketEntityMetadata;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1EPacketRemoveEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S20PacketEntityProperties;
import net.minecraft.network.play.server.S20PacketEntityProperties.Snapshot;
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
import net.minecraft.network.play.server.S22PacketMultiBlockChange.BlockUpdateData;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S24PacketBlockAction;
import net.minecraft.network.play.server.S25PacketBlockBreakAnim;
import net.minecraft.network.play.server.S26PacketMapChunkBulk;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.network.play.server.S28PacketEffect;
import net.minecraft.network.play.server.S29PacketSoundEffect;
import net.minecraft.network.play.server.S2APacketParticles;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S2CPacketSpawnGlobalEntity;
import net.minecraft.network.play.server.S2DPacketOpenWindow;
import net.minecraft.network.play.server.S2EPacketCloseWindow;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S30PacketWindowItems;
import net.minecraft.network.play.server.S31PacketWindowProperty;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S33PacketUpdateSign;
import net.minecraft.network.play.server.S34PacketMaps;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.network.play.server.S36PacketSignEditorOpen;
import net.minecraft.network.play.server.S37PacketStatistics;
import net.minecraft.network.play.server.S38PacketPlayerListItem;

import net.minecraft.network.play.server.S38PacketPlayerListItem.AddPlayerData;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;

import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S42PacketCombatEvent;
import net.minecraft.network.play.server.S43PacketCamera;
import net.minecraft.network.play.server.S44PacketWorldBorder;
import net.minecraft.network.play.server.S45PacketTitle;
import net.minecraft.network.play.server.S45PacketTitle.Type;
import net.minecraft.network.play.server.S46PacketSetCompressionLevel;
import net.minecraft.network.play.server.S47PacketPlayerListHeaderFooter;
import net.minecraft.network.play.server.S48PacketResourcePackSend;
import net.minecraft.network.play.server.S49PacketUpdateEntityNBT;
import net.minecraft.potion.PotionEffect;
import net.minecraft.realms.DisconnectedRealmsScreen;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team.EnumVisible;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBanner;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.util.StringUtils;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayClient implements INetHandlerPlayClient {
   public static Logger logger = LogManager.getLogger();
   public Map<UUID, NetworkPlayerInfo> playerInfoMap = Maps.newHashMap();
   public int currentServerMaxPlayers = 20;
   public boolean flag = false;
   public Random avRandomizer = new Random();
   public WorldClient clientWorldController;
   public boolean doneLoadingTerrain;
   public Minecraft gameController;
   public GuiScreen guiScreenServer;
   public NetworkManager netManager;
   public GameProfile profile;
   public static ListeningExecutorService listeningExecutorService = MoreExecutors.listeningDecorator(MoreExecutors.newDirectExecutorService());

   public NetHandlerPlayClient(Minecraft mcIn, GuiScreen guiScreen, NetworkManager networkManager, GameProfile gameProfile) {
      this.gameController = mcIn;
      this.guiScreenServer = guiScreen;
      this.netManager = networkManager;
      this.profile = gameProfile;
   }

   public void cleanup() {
      this.clientWorldController = null;
   }

   @Override
   public void handleJoinGame(S01PacketJoinGame packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.playerController = new PlayerControllerMP(this.gameController, this);
      this.clientWorldController = new WorldClient(
         this,
         new WorldSettings(0L, packetIn.getGameType(), false, packetIn.isHardcoreMode(), packetIn.getWorldType()),
         packetIn.getDimension(),
         packetIn.getDifficulty(),
         this.gameController.mcProfiler
      );
      this.gameController.gameSettings.difficulty = packetIn.getDifficulty();
      this.gameController.loadWorld(this.clientWorldController);
      this.gameController.thePlayer.dimension = packetIn.getDimension();
      this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
      this.gameController.thePlayer.setEntityId(packetIn.getEntityId());
      this.currentServerMaxPlayers = packetIn.getMaxPlayers();
      this.gameController.thePlayer.setReducedDebug(packetIn.isReducedDebugInfo());
      this.gameController.playerController.setGameType(packetIn.getGameType());
      this.gameController.gameSettings.sendSettingsToServer();
      this.netManager
         .sendPacket(new C17PacketCustomPayload("MC|Brand", new PacketBuffer(Unpooled.buffer()).writeString(ClientBrandRetriever.getClientModName())));
      CheatBreaker.getInstance().getEventBus().handleEvent(new S01PacketJoinGameEvent(packetIn));
   }

   @Override
   public void handleSpawnObject(S0EPacketSpawnObject packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      double d = packetIn.getX() / 32.0;
      double d2 = packetIn.getY() / 32.0;
      double d3 = packetIn.getZ() / 32.0;
      Entity entity = null;
      if (packetIn.getType() == 10) {
         entity = EntityMinecart.getMinecart(this.clientWorldController, d, d2, d3, EnumMinecartType.byNetworkID(packetIn.getDataId()));
      } else if (packetIn.getType() == 90) {
         Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getDataId());
         if (entityByID instanceof EntityPlayer) {
            entity = new EntityFishHook(this.clientWorldController, d, d2, d3, (EntityPlayer)entityByID);
         }

         packetIn.setDataId(0);
      } else if (packetIn.getType() == 60) {
         entity = new EntityArrow(this.clientWorldController, d, d2, d3);
      } else if (packetIn.getType() == 61) {
         entity = new EntitySnowball(this.clientWorldController, d, d2, d3);
      } else if (packetIn.getType() == 71) {
         entity = new EntityItemFrame(
            this.clientWorldController,
            new BlockPos(MathHelper.floor_double(d), MathHelper.floor_double(d2), MathHelper.floor_double(d3)),
            EnumFacing.getHorizontal(packetIn.getDataId())
         );
         packetIn.setDataId(0);
      } else if (packetIn.getType() == 77) {
         entity = new EntityLeashKnot(
            this.clientWorldController, new BlockPos(MathHelper.floor_double(d), MathHelper.floor_double(d2), MathHelper.floor_double(d3))
         );
         packetIn.setDataId(0);
      } else if (packetIn.getType() == 65) {
         entity = new EntityEnderPearl(this.clientWorldController, d, d2, d3);
      } else if (packetIn.getType() == 72) {
         entity = new EntityEnderEye(this.clientWorldController, d, d2, d3);
      } else if (packetIn.getType() == 76) {
         entity = new EntityFireworkRocket(this.clientWorldController, d, d2, d3, (ItemStack)null);
      } else if (packetIn.getType() == 63) {
         entity = new EntityLargeFireball(
            this.clientWorldController, d, d2, d3, packetIn.getSpeedX() / 8000.0, packetIn.getSpeedY() / 8000.0, packetIn.getSpeedZ() / 8000.0
         );
         packetIn.setDataId(0);
      } else if (packetIn.getType() == 64) {
         entity = new EntitySmallFireball(
            this.clientWorldController, d, d2, d3, packetIn.getSpeedX() / 8000.0, packetIn.getSpeedY() / 8000.0, packetIn.getSpeedZ() / 8000.0
         );
         packetIn.setDataId(0);
      } else if (packetIn.getType() == 66) {
         entity = new EntityWitherSkull(
            this.clientWorldController, d, d2, d3, packetIn.getSpeedX() / 8000.0, packetIn.getSpeedY() / 8000.0, packetIn.getSpeedZ() / 8000.0
         );
         packetIn.setDataId(0);
      } else if (packetIn.getType() == 62) {
         entity = new EntityEgg(this.clientWorldController, d, d2, d3);
      } else if (packetIn.getType() == 73) {
         entity = new EntityPotion(this.clientWorldController, d, d2, d3, packetIn.getDataId());
         packetIn.setDataId(0);
      } else if (packetIn.getType() == 75) {
         entity = new EntityExpBottle(this.clientWorldController, d, d2, d3);
         packetIn.setDataId(0);
      } else if (packetIn.getType() == 1) {
         entity = new EntityBoat(this.clientWorldController, d, d2, d3);
      } else if (packetIn.getType() == 50) {
         entity = new EntityTNTPrimed(this.clientWorldController, d, d2, d3, (EntityLivingBase)null);
      } else if (packetIn.getType() == 78) {
         entity = new EntityArmorStand(this.clientWorldController, d, d2, d3);
      } else if (packetIn.getType() == 51) {
         entity = new EntityEnderCrystal(this.clientWorldController, d, d2, d3);
      } else if (packetIn.getType() == 2) {
         entity = new EntityItem(this.clientWorldController, d, d2, d3);
      } else if (packetIn.getType() == 70) {
         entity = new EntityFallingBlock(this.clientWorldController, d, d2, d3, Block.getStateById(packetIn.getDataId() & 65535));
         packetIn.setDataId(0);
      }

      if (entity != null) {
         entity.serverPosX = packetIn.getX();
         entity.serverPosY = packetIn.getY();
         entity.serverPosZ = packetIn.getZ();
         entity.rotationPitch = packetIn.getPitch() * 360 / 256.0F;
         entity.rotationYaw = packetIn.getYaw() * 360 / 256.0F;
         Entity[] parts = entity.getParts();
         if (parts != null) {
            int n = packetIn.getEntityID() - entity.getEntityId();

            for (int i = 0; i < parts.length; i++) {
               parts[i].setEntityId(parts[i].getEntityId() + n);
            }
         }

         entity.setEntityId(packetIn.getEntityID());
         this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entity);
         if (packetIn.getDataId() > 0) {
            if (packetIn.getType() == 60) {
               Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getDataId());
               if (entityByID instanceof EntityLivingBase && entity instanceof EntityArrow) {
                  ((EntityArrow)entity).shootingEntity = entityByID;
               }
            }

            entity.setVelocity(packetIn.getSpeedX() / 8000.0, packetIn.getSpeedY() / 8000.0, packetIn.getSpeedZ() / 8000.0);
         }
      }
   }

   @Override
   public void handleSpawnExperienceOrb(S11PacketSpawnExperienceOrb packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityXPOrb entityXPOrb = new EntityXPOrb(
         this.clientWorldController, packetIn.getX() / 32.0, packetIn.getY() / 32.0, packetIn.getZ() / 32.0, packetIn.getXPValue()
      );
      entityXPOrb.serverPosX = packetIn.getX();
      entityXPOrb.serverPosY = packetIn.getY();
      entityXPOrb.serverPosZ = packetIn.getZ();
      entityXPOrb.rotationYaw = 0.0F;
      entityXPOrb.rotationPitch = 0.0F;
      entityXPOrb.setEntityId(packetIn.getEntityID());
      this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entityXPOrb);
   }

   @Override
   public void handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      double d = packetIn.getX() / 32.0;
      double d2 = packetIn.getY() / 32.0;
      double d3 = packetIn.getZ() / 32.0;
      EntityLightningBolt entityLightningBolt = null;
      if (packetIn.getEntityTypeId() == 1) {
         entityLightningBolt = new EntityLightningBolt(this.clientWorldController, d, d2, d3);
      }

      if (entityLightningBolt != null) {
         entityLightningBolt.serverPosX = packetIn.getX();
         entityLightningBolt.serverPosY = packetIn.getY();
         entityLightningBolt.serverPosZ = packetIn.getZ();
         entityLightningBolt.rotationYaw = 0.0F;
         entityLightningBolt.rotationPitch = 0.0F;
         entityLightningBolt.setEntityId(packetIn.getId());
         this.clientWorldController.addWeatherEffect(entityLightningBolt);
      }
   }

   @Override
   public void handleSpawnPainting(S10PacketSpawnPainting packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPainting entityPainting = new EntityPainting(this.clientWorldController, packetIn.getPosition(), packetIn.getFacing(), packetIn.getTitle());
      this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entityPainting);
   }

   @Override
   public void handleEntityVelocity(S12PacketEntityVelocity packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityID());
      if (entityByID != null) {
         entityByID.setVelocity(packetIn.getMotionX() / 8000.0, packetIn.getMotionY() / 8000.0, packetIn.getMotionZ() / 8000.0);
      }
   }

   @Override
   public void handleEntityMetadata(S1CPacketEntityMetadata packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityId());
      if (entityByID != null && packetIn.getTrackedValues() != null) {
         entityByID.getDataWatcher().updateWatchedObjectsFromList(packetIn.getTrackedValues());
      }
   }

   @Override
   public void handleSpawnPlayer(S0CPacketSpawnPlayer packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      double d = packetIn.getX() / 32.0;
      double d2 = packetIn.getY() / 32.0;
      double d3 = packetIn.getZ() / 32.0;
      float f = packetIn.getYaw() * 360 / 256.0F;
      float f2 = packetIn.getPitch() * 360 / 256.0F;
      EntityOtherPlayerMP entityOtherPlayerMP = new EntityOtherPlayerMP(this.gameController.theWorld, this.getPlayerInfo(packetIn.getPlayer()).getGameProfile());
      entityOtherPlayerMP.prevPosX = entityOtherPlayerMP.lastTickPosX = entityOtherPlayerMP.serverPosX = packetIn.getX();
      entityOtherPlayerMP.prevPosY = entityOtherPlayerMP.lastTickPosY = entityOtherPlayerMP.serverPosY = packetIn.getY();
      entityOtherPlayerMP.prevPosZ = entityOtherPlayerMP.lastTickPosZ = entityOtherPlayerMP.serverPosZ = packetIn.getZ();
      int currentItemID = packetIn.getCurrentItemID();
      if (currentItemID == 0) {
         entityOtherPlayerMP.inventory.mainInventory[entityOtherPlayerMP.inventory.currentItem] = null;
      } else {
         entityOtherPlayerMP.inventory.mainInventory[entityOtherPlayerMP.inventory.currentItem] = new ItemStack(Item.getItemById(currentItemID), 1, 0);
      }

      entityOtherPlayerMP.setPositionAndRotation(d, d2, d3, f, f2);
      this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), entityOtherPlayerMP);
      List dataTrackerEntries = packetIn.getDataTrackerEntries();
      if (dataTrackerEntries != null) {
         entityOtherPlayerMP.getDataWatcher().updateWatchedObjectsFromList(dataTrackerEntries);
      }
   }

   @Override
   public void handleEntityTeleport(S18PacketEntityTeleport packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityId());
      if (entityByID != null) {
         entityByID.serverPosX = packetIn.getX();
         entityByID.serverPosY = packetIn.getY();
         entityByID.serverPosZ = packetIn.getZ();
         double d = entityByID.serverPosX / 32.0;
         double d2 = entityByID.serverPosY / 32.0;
         double d3 = entityByID.serverPosZ / 32.0;
         float f = packetIn.getYaw() * 360 / 256.0F;
         float f2 = packetIn.getPitch() * 360 / 256.0F;
         if (Math.abs(entityByID.posX - d) < 0.03125 && Math.abs(entityByID.posY - d2) < 0.015625 && Math.abs(entityByID.posZ - d3) < 0.03125) {
            entityByID.setPositionAndRotation2(entityByID.posX, entityByID.posY, entityByID.posZ, f, f2, 3, true);
         } else {
            entityByID.setPositionAndRotation2(d, d2, d3, f, f2, 3, true);
         }

         entityByID.onGround = packetIn.getOnGround();
      }
   }

   @Override
   public void handleHeldItemChange(S09PacketHeldItemChange packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (packetIn.getHeldItemHotbarIndex() >= 0 && packetIn.getHeldItemHotbarIndex() < InventoryPlayer.getHotbarSize()) {
         this.gameController.thePlayer.inventory.currentItem = packetIn.getHeldItemHotbarIndex();
      }
   }

   @Override
   public void handleEntityMovement(S14PacketEntity packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = packetIn.getEntity(this.clientWorldController);
      if (entity != null) {
         entity.serverPosX = entity.serverPosX + packetIn.getX();
         entity.serverPosY = entity.serverPosY + packetIn.getY();
         entity.serverPosZ = entity.serverPosZ + packetIn.getZ();
         double d = entity.serverPosX / 32.0;
         double d2 = entity.serverPosY / 32.0;
         double d3 = entity.serverPosZ / 32.0;
         float f = packetIn.shouldRotate() ? packetIn.getYaw() * 360 / 256.0F : entity.rotationYaw;
         float f2 = packetIn.shouldRotate() ? packetIn.getPitch() * 360 / 256.0F : entity.rotationPitch;
         entity.setPositionAndRotation2(d, d2, d3, f, f2, 3, false);
         entity.onGround = packetIn.getOnGround();
      }
   }

   @Override
   public void handleEntityHeadLook(S19PacketEntityHeadLook packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = packetIn.getEntity(this.clientWorldController);
      if (entity != null) {
         float f = packetIn.getYaw() * 360 / 256.0F;
         entity.setRotationYawHead(f);
      }
   }

   @Override
   public void handleDestroyEntities(S13PacketDestroyEntities packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

      for (int i = 0; i < packetIn.getEntityIDs().length; i++) {
         this.clientWorldController.removeEntityFromWorld(packetIn.getEntityIDs()[i]);
      }
   }

   @Override
   public void handlePlayerPosLook(S08PacketPlayerPosLook packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP thePlayer = this.gameController.thePlayer;
      double d = packetIn.getX();
      double d2 = packetIn.getY();
      double d3 = packetIn.getZ();
      float yaw = packetIn.getYaw();
      float pitch = packetIn.getPitch();
      if (packetIn.getFlags().contains(EnumFlags.X)) {
         d += thePlayer.posX;
      } else {
         thePlayer.motionX = 0.0;
      }

      if (packetIn.getFlags().contains(EnumFlags.Y)) {
         d2 += thePlayer.posY;
      } else {
         thePlayer.motionY = 0.0;
      }

      if (packetIn.getFlags().contains(EnumFlags.Z)) {
         d3 += thePlayer.posZ;
      } else {
         thePlayer.motionZ = 0.0;
      }

      if (packetIn.getFlags().contains(EnumFlags.X_ROT)) {
         pitch += thePlayer.rotationPitch;
      }

      if (packetIn.getFlags().contains(EnumFlags.Y_ROT)) {
         yaw += thePlayer.rotationYaw;
      }

      thePlayer.setPositionAndRotation(d, d2, d3, yaw, pitch);
      this.netManager
         .sendPacket(
            new C06PacketPlayerPosLook(thePlayer.posX, thePlayer.getEntityBoundingBox().minY, thePlayer.posZ, thePlayer.rotationYaw, thePlayer.rotationPitch, false)
         );
      if (!this.doneLoadingTerrain) {
         this.gameController.thePlayer.prevPosX = this.gameController.thePlayer.posX;
         this.gameController.thePlayer.prevPosY = this.gameController.thePlayer.posY;
         this.gameController.thePlayer.prevPosZ = this.gameController.thePlayer.posZ;
         this.doneLoadingTerrain = true;
         this.gameController.displayGuiScreen((GuiScreen)null);
         CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent12());
      }
   }

   @Override
   public void handleMultiBlockChange(S22PacketMultiBlockChange packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

      for (BlockUpdateData blockUpdateData : packetIn.getChangedBlocks()) {
         this.clientWorldController.invalidateRegionAndSetBlock(blockUpdateData.getPos(), blockUpdateData.getBlockState());
      }
   }

   @Override
   public void handleChunkData(S21PacketChunkData packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (packetIn.shouldLoad()) {
         if (packetIn.getExtractedSize() == 0) {
            this.clientWorldController.doPreChunk(packetIn.getChunkX(), packetIn.getChunkZ(), false);
            return;
         }

         this.clientWorldController.doPreChunk(packetIn.getChunkX(), packetIn.getChunkZ(), true);
      }

      this.clientWorldController
         .invalidateBlockReceiveRegion(
            packetIn.getChunkX() << 4, 0, packetIn.getChunkZ() << 4, (packetIn.getChunkX() << 4) + 15, 256, (packetIn.getChunkZ() << 4) + 15
         );
      Chunk chunkFromChunkCoords = this.clientWorldController.getChunkFromChunkCoords(packetIn.getChunkX(), packetIn.getChunkZ());
      chunkFromChunkCoords.fillChunk(packetIn.getExtractedDataBytes(), packetIn.getExtractedSize(), packetIn.shouldLoad());
      this.clientWorldController
         .markBlockRangeForRenderUpdate(
            packetIn.getChunkX() << 4, 0, packetIn.getChunkZ() << 4, (packetIn.getChunkX() << 4) + 15, 256, (packetIn.getChunkZ() << 4) + 15
         );
      if (!packetIn.shouldLoad() || !(this.clientWorldController.provider instanceof WorldProviderSurface)) {
         chunkFromChunkCoords.resetRelightChecks();
      }
   }

   @Override
   public void handleBlockChange(S23PacketBlockChange packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.clientWorldController.invalidateRegionAndSetBlock(packetIn.getBlockPosition(), packetIn.getBlockState());
   }

   @Override
   public void handleDisconnect(S40PacketDisconnect packetIn) {
      this.netManager.closeChannel(packetIn.getReason());
   }

   @Override
   public void onDisconnect(IChatComponent reason) {
      this.gameController.loadWorld((WorldClient)null);
      CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent3());
      if (this.guiScreenServer != null) {
         if (this.guiScreenServer instanceof GuiScreenRealmsProxy) {
            this.gameController
               .displayGuiScreen(
                  new DisconnectedRealmsScreen(((GuiScreenRealmsProxy)this.guiScreenServer).getRealmsScreen(), "disconnect.lost", reason).getProxy()
               );
         } else {
            this.gameController.displayGuiScreen(new GuiDisconnected(this.guiScreenServer, "disconnect.lost", reason));
         }
      } else {
         this.gameController.displayGuiScreen(new GuiDisconnected(new GuiMultiplayer(new MainMenuScreen()), "disconnect.lost", reason));
      }
   }

   public void addToSendQueue(Packet packet) {
      CheatBreaker.getInstance().getEventBus().handleEvent(new PacketEvent(packet));
      this.netManager.sendPacket(packet);
   }

   @Override
   public void handleCollectItem(S0DPacketCollectItem packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getCollectedItemEntityID());
      EntityLivingBase entityByID2 = (EntityLivingBase)this.clientWorldController.getEntityByID(packetIn.getEntityID());
      if (entityByID2 == null) {
         entityByID2 = this.gameController.thePlayer;
      }

      if (entityByID != null) {
         if (entityByID instanceof EntityXPOrb) {
            this.clientWorldController
               .playSoundAtEntity(entityByID, "random.orb", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         } else {
            this.clientWorldController
               .playSoundAtEntity(entityByID, "random.pop", 0.2F, ((this.avRandomizer.nextFloat() - this.avRandomizer.nextFloat()) * 0.7F + 1.0F) * 2.0F);
         }

         this.gameController.effectRenderer.addEffect(new EntityPickupFX(this.clientWorldController, entityByID, entityByID2, 0.5F));
         this.clientWorldController.removeEntityFromWorld(packetIn.getCollectedItemEntityID());
      }
   }

   @Override
   public void handleChat(S02PacketChat packetIn) {
      CheatBreaker.getInstance().getEventBus().handleEvent(new S02PacketChatEvent(packetIn));
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (packetIn.getType() == 2) {
         this.gameController.ingameGUI.setRecordPlaying(packetIn.getChatComponent(), false);
      } else {
         this.gameController.ingameGUI.getChatGUI().printChatMessage(packetIn.getChatComponent());
      }
   }

   @Override
   public void handleAnimation(S0BPacketAnimation packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityID());
      if (entityByID != null) {
         if (packetIn.getAnimationType() == 0) {
            EntityLivingBase entityLivingBase = (EntityLivingBase)entityByID;
            entityLivingBase.swingItem();
         } else if (packetIn.getAnimationType() == 1) {
            entityByID.performHurtAnimation();
         } else if (packetIn.getAnimationType() == 2) {
            EntityPlayer entityPlayer = (EntityPlayer)entityByID;
            entityPlayer.wakeUpPlayer(false, false, false);
         } else if (packetIn.getAnimationType() == 4) {
            if (CheatBreaker.getInstance().getModManager().particlesMod.isEnabled()
               && !CheatBreaker.getInstance().getModManager().particlesMod.critParticles.getValue().equals("Never")) {
               this.gameController.effectRenderer.emitParticleAtEntity(entityByID, EnumParticleTypes.CRIT);
            }
         } else if (packetIn.getAnimationType() == 5
            && CheatBreaker.getInstance().getModManager().particlesMod.isEnabled()
            && !CheatBreaker.getInstance().getModManager().particlesMod.sharpnessParticles.getValue().equals("Never")) {
            this.gameController.effectRenderer.emitParticleAtEntity(entityByID, EnumParticleTypes.CRIT_MAGIC);
         }
      }
   }

   @Override
   public void handleUseBed(S0APacketUseBed packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      packetIn.getPlayer(this.clientWorldController).trySleep(packetIn.getBedPosition());
   }

   @Override
   public void handleSpawnMob(S0FPacketSpawnMob packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      double d = packetIn.getX() / 32.0;
      double d2 = packetIn.getY() / 32.0;
      double d3 = packetIn.getZ() / 32.0;
      float f = packetIn.getYaw() * 360 / 256.0F;
      float f2 = packetIn.getPitch() * 360 / 256.0F;
      EntityLivingBase createEntityByID = (EntityLivingBase)EntityList.createEntityByID(packetIn.getEntityType(), this.gameController.theWorld);
      createEntityByID.serverPosX = packetIn.getX();
      createEntityByID.serverPosY = packetIn.getY();
      createEntityByID.serverPosZ = packetIn.getZ();
      createEntityByID.renderYawOffset = createEntityByID.rotationYawHead = packetIn.getHeadPitch() * 360 / 256.0F;
      Entity[] parts = createEntityByID.getParts();
      if (parts != null) {
         int n = packetIn.getEntityID() - createEntityByID.getEntityId();

         for (int i = 0; i < parts.length; i++) {
            parts[i].setEntityId(parts[i].getEntityId() + n);
         }
      }

      createEntityByID.setEntityId(packetIn.getEntityID());
      createEntityByID.setPositionAndRotation(d, d2, d3, f, f2);
      createEntityByID.motionX = packetIn.getVelocityX() / 8000.0F;
      createEntityByID.motionY = packetIn.getVelocityY() / 8000.0F;
      createEntityByID.motionZ = packetIn.getVelocityZ() / 8000.0F;
      this.clientWorldController.addEntityToWorld(packetIn.getEntityID(), createEntityByID);
      List entries = packetIn.getEntries();
      if (entries != null) {
         createEntityByID.getDataWatcher().updateWatchedObjectsFromList(entries);
      }
   }

   @Override
   public void handleTimeUpdate(S03PacketTimeUpdate packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.theWorld.setTotalWorldTime(packetIn.getTotalWorldTime());
      S03PacketTimeUpdateEvent s03PacketTimeUpdateEvent = new S03PacketTimeUpdateEvent(packetIn);
      CheatBreaker.getInstance().getEventBus().handleEvent(s03PacketTimeUpdateEvent);
      if (!s03PacketTimeUpdateEvent.isFlag()) {
         this.gameController.theWorld.setWorldTime(packetIn.getWorldTime());
      }
   }

   @Override
   public void handleSpawnPosition(S05PacketSpawnPosition packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.thePlayer.setSpawnPoint(packetIn.getSpawnPos(), true);
      this.gameController.theWorld.getWorldInfo().setSpawn(packetIn.getSpawnPos());
   }

   @Override
   public void handleEntityAttach(S1BPacketEntityAttach packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityId());
      Entity entityByID2 = this.clientWorldController.getEntityByID(packetIn.getVehicleEntityId());
      if (packetIn.getLeash() == 0) {
         boolean flag = false;
         if (packetIn.getEntityId() == this.gameController.thePlayer.getEntityId()) {
            entityByID = this.gameController.thePlayer;
            if (entityByID2 instanceof EntityBoat) {
               ((EntityBoat)entityByID2).setIsBoatEmpty(false);
            }

            flag = entityByID.ridingEntity == null && entityByID2 != null;
         } else if (entityByID2 instanceof EntityBoat) {
            ((EntityBoat)entityByID2).setIsBoatEmpty(true);
         }

         if (entityByID == null) {
            return;
         }

         entityByID.mountEntity(entityByID2);
         if (flag) {
            GameSettings gameSettings = this.gameController.gameSettings;
            this.gameController
               .ingameGUI
               .setRecordPlaying(I18n.format("mount.onboard", GameSettings.getKeyDisplayString(gameSettings.keyBindSneak.getKeyCode())), false);
         }
      } else if (packetIn.getLeash() == 1 && entityByID instanceof EntityLiving) {
         if (entityByID2 != null) {
            ((EntityLiving)entityByID).setLeashedToEntity(entityByID2, false);
         } else {
            ((EntityLiving)entityByID).clearLeashed(false, false);
         }
      }
   }

   @Override
   public void handleEntityStatus(S19PacketEntityStatus packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = packetIn.getEntity(this.clientWorldController);
      if (entity != null) {
         if (packetIn.getOpCode() == 21) {
            this.gameController.getSoundHandler().playSound(new GuardianSound((EntityGuardian)entity));
         } else {
            entity.handleStatusUpdate(packetIn.getOpCode());
            CheatBreaker.getInstance().getEventBus().handleEvent(new EntityS19PacketEntityStatusEvent(packetIn, entity));
         }
      }
   }

   @Override
   public void handleUpdateHealth(S06PacketUpdateHealth packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.thePlayer.setPlayerSPHealth(packetIn.getHealth());
      this.gameController.thePlayer.getFoodStats().setFoodLevel(packetIn.getFoodLevel());
      this.gameController.thePlayer.getFoodStats().setFoodSaturationLevel(packetIn.getSaturationLevel());
      CheatBreaker.getInstance().getEventBus().handleEvent(new S06PacketUpdateHealthEvent(packetIn));
   }

   @Override
   public void handleSetExperience(S1FPacketSetExperience packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.thePlayer.setXPStats(packetIn.getBarProgress(), packetIn.getTotalExperience(), packetIn.getLevel());
   }

   @Override
   public void handleRespawn(S07PacketRespawn packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (packetIn.getDimensionID() != this.gameController.thePlayer.dimension) {
         this.doneLoadingTerrain = false;
         Scoreboard scoreboard = this.clientWorldController.getScoreboard();
         this.clientWorldController = new WorldClient(
            this,
            new WorldSettings(0L, packetIn.getGameType(), false, this.gameController.theWorld.getWorldInfo().isHardcoreModeEnabled(), packetIn.getWorldType()),
            packetIn.getDimensionID(),
            packetIn.getDifficulty(),
            this.gameController.mcProfiler
         );
         this.clientWorldController.setWorldScoreboard(scoreboard);
         this.gameController.loadWorld(this.clientWorldController);
         this.gameController.thePlayer.dimension = packetIn.getDimensionID();
         this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
      }

      this.gameController.setDimensionAndSpawnPlayer(packetIn.getDimensionID());
      this.gameController.playerController.setGameType(packetIn.getGameType());
   }

   @Override
   public void handleExplosion(S27PacketExplosion packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Explosion explosion = new Explosion(
         this.gameController.theWorld,
         (Entity)null,
         packetIn.getX(),
         packetIn.getY(),
         packetIn.getZ(),
         packetIn.getStrength(),
         packetIn.getAffectedBlockPositions()
      );
      explosion.doExplosionB(true);
      this.gameController.thePlayer.motionX = this.gameController.thePlayer.motionX + packetIn.getPlayerVelocityX();
      this.gameController.thePlayer.motionY = this.gameController.thePlayer.motionY + packetIn.getPlayerVelocityY();
      this.gameController.thePlayer.motionZ = this.gameController.thePlayer.motionZ + packetIn.getPlayerVelocityZ();
   }

   @Override
   public void handleOpenWindow(S2DPacketOpenWindow packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP thePlayer = this.gameController.thePlayer;
      if ("minecraft:container".equals(packetIn.getGuiId())) {
         thePlayer.displayGUIChest(new InventoryBasic(packetIn.getWindowTitle(), packetIn.getSlotCount()));
         thePlayer.openContainer.windowId = packetIn.getWindowId();
      } else if ("minecraft:villager".equals(packetIn.getGuiId())) {
         thePlayer.displayVillagerTradeGui(new NpcMerchant(thePlayer, packetIn.getWindowTitle()));
         thePlayer.openContainer.windowId = packetIn.getWindowId();
      } else if ("EntityHorse".equals(packetIn.getGuiId())) {
         Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityId());
         if (entityByID instanceof EntityHorse) {
            thePlayer.displayGUIHorse((EntityHorse)entityByID, new AnimalChest(packetIn.getWindowTitle(), packetIn.getSlotCount()));
            thePlayer.openContainer.windowId = packetIn.getWindowId();
         }
      } else if (!packetIn.hasSlots()) {
         thePlayer.displayGui(new LocalBlockIntercommunication(packetIn.getGuiId(), packetIn.getWindowTitle()));
         thePlayer.openContainer.windowId = packetIn.getWindowId();
      } else {
         ContainerLocalMenu containerLocalMenu = new ContainerLocalMenu(packetIn.getGuiId(), packetIn.getWindowTitle(), packetIn.getSlotCount());
         thePlayer.displayGUIChest(containerLocalMenu);
         thePlayer.openContainer.windowId = packetIn.getWindowId();
      }
   }

   @Override
   public void handleSetSlot(S2FPacketSetSlot packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP thePlayer = this.gameController.thePlayer;
      if (packetIn.getSyncId() == -1) {
         thePlayer.inventory.setItemStack(packetIn.getItemStack());
      } else {
         boolean flag = false;
         if (this.gameController.currentScreen instanceof GuiContainerCreative) {
            GuiContainerCreative currentScreen = (GuiContainerCreative)this.gameController.currentScreen;
            flag = currentScreen.getSelectedTabIndex() != CreativeTabs.tabInventory.getTabIndex();
         }

         if (packetIn.getSyncId() == 0 && packetIn.getSlot() >= 36 && packetIn.getSlot() < 45) {
            ItemStack stack = thePlayer.inventoryContainer.getSlot(packetIn.getSlot()).getStack();
            if (packetIn.getItemStack() != null && (stack == null || stack.stackSize < packetIn.getItemStack().stackSize)) {
               packetIn.getItemStack().animationsToGo = 5;
            }

            thePlayer.inventoryContainer.putStackInSlot(packetIn.getSlot(), packetIn.getItemStack());
         } else if (packetIn.getSyncId() == thePlayer.openContainer.windowId && (packetIn.getSyncId() != 0 || !flag)) {
            thePlayer.openContainer.putStackInSlot(packetIn.getSlot(), packetIn.getItemStack());
         }
      }
   }

   @Override
   public void handleConfirmTransaction(S32PacketConfirmTransaction packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Container container = null;
      EntityPlayerSP thePlayer = this.gameController.thePlayer;
      if (packetIn.getWindowId() == 0) {
         container = thePlayer.inventoryContainer;
      } else if (packetIn.getWindowId() == thePlayer.openContainer.windowId) {
         container = thePlayer.openContainer;
      }

      if (container != null && !packetIn.wasAccepted()) {
         this.addToSendQueue(new C0FPacketConfirmTransaction(packetIn.getWindowId(), packetIn.getActionNumber(), true));
      }
   }

   @Override
   public void handleWindowItems(S30PacketWindowItems packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP thePlayer = this.gameController.thePlayer;
      if (packetIn.getScreenId() == 0) {
         thePlayer.inventoryContainer.putStacksInSlots(packetIn.getItemStacks());
      } else if (packetIn.getScreenId() == thePlayer.openContainer.windowId) {
         thePlayer.openContainer.putStacksInSlots(packetIn.getItemStacks());
      }
   }

   @Override
   public void handleSignEditorOpen(S36PacketSignEditorOpen packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      TileEntity tileEntity = this.clientWorldController.getTileEntity(packetIn.getSignPosition());
      if (!(tileEntity instanceof TileEntitySign)) {
         tileEntity = new TileEntitySign();
         tileEntity.setWorldObj(this.clientWorldController);
         tileEntity.setPos(packetIn.getSignPosition());
      }

      this.gameController.thePlayer.openEditSign((TileEntitySign)tileEntity);
   }

   @Override
   public void handleUpdateSign(S33PacketUpdateSign packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      boolean flag = false;
      if (this.gameController.theWorld.isBlockLoaded(packetIn.getPos())) {
         TileEntity tileEntity = this.gameController.theWorld.getTileEntity(packetIn.getPos());
         if (tileEntity instanceof TileEntitySign) {
            TileEntitySign tileEntitySign = (TileEntitySign)tileEntity;
            if (tileEntitySign.getIsEditable()) {
               System.arraycopy(packetIn.getLines(), 0, tileEntitySign.signText, 0, 4);
               tileEntitySign.markDirty();
            }

            flag = true;
         }
      }

      if (!flag && this.gameController.thePlayer != null) {
         this.gameController
            .thePlayer
            .addChatMessage(
               new ChatComponentText("Unable to locate sign at " + packetIn.getPos().getX() + ", " + packetIn.getPos().getY() + ", " + packetIn.getPos().getZ())
            );
      }
   }

   @Override
   public void handleUpdateTileEntity(S35PacketUpdateTileEntity packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (this.gameController.theWorld.isBlockLoaded(packetIn.getPos())) {
         TileEntity tileEntity = this.gameController.theWorld.getTileEntity(packetIn.getPos());
         int tileEntityType = packetIn.getTileEntityType();
         if (tileEntityType == 1 && tileEntity instanceof TileEntityMobSpawner
            || tileEntityType == 2 && tileEntity instanceof TileEntityCommandBlock
            || tileEntityType == 3 && tileEntity instanceof TileEntityBeacon
            || tileEntityType == 4 && tileEntity instanceof TileEntitySkull
            || tileEntityType == 5 && tileEntity instanceof TileEntityFlowerPot
            || tileEntityType == 6 && tileEntity instanceof TileEntityBanner) {
            tileEntity.readFromNBT(packetIn.getNbtCompound());
         }
      }
   }

   @Override
   public void handleWindowProperty(S31PacketWindowProperty packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP thePlayer = this.gameController.thePlayer;
      if (thePlayer.openContainer != null && thePlayer.openContainer.windowId == packetIn.getWindowId()) {
         thePlayer.openContainer.updateProgressBar(packetIn.getVarIndex(), packetIn.getVarValue());
      }
   }

   @Override
   public void handleEntityEquipment(S04PacketEntityEquipment packetIn) {
      if (packetIn != null) {
         PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
         Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityID());
         if (entityByID != null) {
            entityByID.setCurrentItemOrArmor(packetIn.getEquipmentSlot(), packetIn.getItemStack());
         }
      }
   }

   @Override
   public void handleCloseWindow(S2EPacketCloseWindow packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.thePlayer.closeScreenAndDropStack();
   }

   @Override
   public void handleBlockAction(S24PacketBlockAction packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.theWorld.addBlockEvent(packetIn.getBlockPosition(), packetIn.getBlockType(), packetIn.getData1(), packetIn.getData2());
   }

   @Override
   public void handleBlockBreakAnim(S25PacketBlockBreakAnim packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.theWorld.sendBlockBreakProgress(packetIn.getBreakerId(), packetIn.getPosition(), packetIn.getProgress());
   }

   @Override
   public void handleMapChunkBulk(S26PacketMapChunkBulk packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

      for (int i = 0; i < packetIn.getChunkCount(); i++) {
         int chunkX = packetIn.getChunkX(i);
         int chunkZ = packetIn.getChunkZ(i);
         this.clientWorldController.doPreChunk(chunkX, chunkZ, true);
         this.clientWorldController.invalidateBlockReceiveRegion(chunkX << 4, 0, chunkZ << 4, (chunkX << 4) + 15, 256, (chunkZ << 4) + 15);
         Chunk chunkFromChunkCoords = this.clientWorldController.getChunkFromChunkCoords(chunkX, chunkZ);
         chunkFromChunkCoords.fillChunk(packetIn.getChunkBytes(i), packetIn.getChunkSize(i), true);
         this.clientWorldController.markBlockRangeForRenderUpdate(chunkX << 4, 0, chunkZ << 4, (chunkX << 4) + 15, 256, (chunkZ << 4) + 15);
         if (!(this.clientWorldController.provider instanceof WorldProviderSurface)) {
            chunkFromChunkCoords.resetRelightChecks();
         }
      }
   }

   @Override
   public void handleChangeGameState(S2BPacketChangeGameState packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP thePlayer = this.gameController.thePlayer;
      int gameState = packetIn.getGameState();
      float value = packetIn.getValue();
      int n = MathHelper.floor_float(value + 0.5F);
      if (gameState >= 0 && gameState < S2BPacketChangeGameState.MESSAGE_NAMES.length && S2BPacketChangeGameState.MESSAGE_NAMES[gameState] != null) {
         thePlayer.addChatComponentMessage(new ChatComponentTranslation(S2BPacketChangeGameState.MESSAGE_NAMES[gameState]));
      }

      if (gameState == 1) {
         this.clientWorldController.getWorldInfo().setRaining(true);
         this.clientWorldController.setRainStrength(0.0F);
      } else if (gameState == 2) {
         this.clientWorldController.getWorldInfo().setRaining(false);
         this.clientWorldController.setRainStrength(1.0F);
      } else if (gameState == 3) {
         this.gameController.playerController.setGameType(GameType.getByID(n));
      } else if (gameState == 4) {
         this.gameController.displayGuiScreen(new GuiWinGame());
      } else if (gameState == 5) {
         GameSettings gameSettings = this.gameController.gameSettings;
         if (value == 0.0F) {
            this.gameController.displayGuiScreen(new GuiScreenDemo());
         } else if (value == 101.0F) {
            this.gameController
               .ingameGUI
               .getChatGUI()
               .printChatMessage(
                  new ChatComponentTranslation(
                     "demo.help.movement",
                     GameSettings.getKeyDisplayString(gameSettings.keyBindForward.getKeyCode()),
                     GameSettings.getKeyDisplayString(gameSettings.keyBindLeft.getKeyCode()),
                     GameSettings.getKeyDisplayString(gameSettings.keyBindBack.getKeyCode()),
                     GameSettings.getKeyDisplayString(gameSettings.keyBindRight.getKeyCode())
                  )
               );
         } else if (value == 102.0F) {
            this.gameController
               .ingameGUI
               .getChatGUI()
               .printChatMessage(new ChatComponentTranslation("demo.help.jump", GameSettings.getKeyDisplayString(gameSettings.keyBindJump.getKeyCode())));
         } else if (value == 103.0F) {
            this.gameController
               .ingameGUI
               .getChatGUI()
               .printChatMessage(new ChatComponentTranslation("demo.help.inventory", GameSettings.getKeyDisplayString(gameSettings.keyBindInventory.getKeyCode())));
         }
      } else if (gameState == 6) {
         this.clientWorldController.playSound(thePlayer.posX, thePlayer.posY + thePlayer.getEyeHeight(), thePlayer.posZ, "random.successful_hit", 0.18F, 0.45F, false);
      } else if (gameState == 7) {
         this.clientWorldController.setRainStrength(value);
      } else if (gameState == 8) {
         this.clientWorldController.setThunderStrength(value);
      } else if (gameState == 10) {
         this.clientWorldController.spawnParticle(EnumParticleTypes.MOB_APPEARANCE, thePlayer.posX, thePlayer.posY, thePlayer.posZ, 0.0, 0.0, 0.0);
         this.clientWorldController.playSound(thePlayer.posX, thePlayer.posY, thePlayer.posZ, "mob.guardian.curse", 1.0F, 1.0F, false);
      }
   }

   @Override
   public void handleMaps(S34PacketMaps packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      MapData loadMapData = ItemMap.loadMapData(packetIn.getMapId(), this.gameController.theWorld);
      packetIn.setMapdataTo(loadMapData);
      this.gameController.entityRenderer.getMapItemRenderer().updateMapTexture(loadMapData);
   }

   @Override
   public void handleEffect(S28PacketEffect packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (packetIn.isSoundServerwide()) {
         this.gameController.theWorld.playBroadcastSound(packetIn.getSoundType(), packetIn.getSoundPos(), packetIn.getSoundData());
      } else {
         this.gameController.theWorld.playAuxSFX(packetIn.getSoundType(), packetIn.getSoundPos(), packetIn.getSoundData());
      }
   }

   @Override
   public void handleStatistics(S37PacketStatistics packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      boolean flag = false;

      for (Entry entry : packetIn.getStatMap().entrySet()) {
         StatBase key = (StatBase)entry.getKey();
         int value = (Integer)entry.getValue();
         if (key.isAchievement() && value > 0) {
            if (this.flag && this.gameController.thePlayer.getStatFileWriter().readStat(key) == 0) {
               Achievement achievement = (Achievement)key;
               this.gameController.guiAchievement.displayAchievement(achievement);
               if (key == AchievementList.openInventory) {
                  this.gameController.gameSettings.showInventoryAchievementHint = false;
                  this.gameController.gameSettings.saveOptions();
               }
            }

            flag = true;
         }

         this.gameController.thePlayer.getStatFileWriter().unlockAchievement(this.gameController.thePlayer, key, value);
      }

      if (!this.flag && !flag && this.gameController.gameSettings.showInventoryAchievementHint) {
         this.gameController.guiAchievement.displayUnformattedAchievement(AchievementList.openInventory);
      }

      this.flag = true;
      if (this.gameController.currentScreen instanceof IProgressMeter) {
         ((IProgressMeter)this.gameController.currentScreen).doneLoading();
      }
   }

   @Override
   public void handleEntityEffect(S1DPacketEntityEffect packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityId());
      if (entityByID instanceof EntityLivingBase) {
         PotionEffect potionEffect = new PotionEffect(packetIn.getEffectId(), packetIn.getDuration(), packetIn.getAmplifier(), false, packetIn.shouldShowParticles());
         potionEffect.setPotionDurationMax(packetIn.isPermanent());
         ((EntityLivingBase)entityByID).addPotionEffect(potionEffect);
      }
   }

   @Override
   public void handleCombatEvent(S42PacketCombatEvent packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entityByID = this.clientWorldController.getEntityByID(packetIn.attackerEntityId);
      if (!(entityByID instanceof EntityLivingBase)) {
         Object obj = null;
      }
   }

   @Override
   public void handleServerDifficulty(S41PacketServerDifficulty packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController.theWorld.getWorldInfo().setDifficulty(packetIn.getDifficulty());
      this.gameController.theWorld.getWorldInfo().setDifficultyLocked(packetIn.isDifficultyLocked());
   }

   @Override
   public void handleCamera(S43PacketCamera packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = packetIn.getEntity(this.clientWorldController);
      if (entity != null) {
         this.gameController.setRenderViewEntity(entity);
      }
   }

   @Override
   public void handleWorldBorder(S44PacketWorldBorder packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      packetIn.apply(this.clientWorldController.getWorldBorder());
   }

   @Override
   public void handleTitle(S45PacketTitle packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Type type = packetIn.getType();
      String str = null;
      String str2 = null;
      String str3 = packetIn.getMessage() != null ? packetIn.getMessage().getFormattedText() : "";
      switch (type) {
         case TITLE:
            str = str3;
            break;
         case SUBTITLE:
            str2 = str3;
            break;
         case RESET:
            this.gameController.ingameGUI.displayTitle("", "", -1, -1, -1);
            this.gameController.ingameGUI.setDefaultTitlesTimes();
            return;
      }

      this.gameController.ingameGUI.displayTitle(str, str2, packetIn.getFadeInTime(), packetIn.getDisplayTime(), packetIn.getFadeOutTime());
   }

   @Override
   public void handleSetCompressionLevel(S46PacketSetCompressionLevel packetIn) {
      if (!this.netManager.isLocalChannel()) {
         this.netManager.setCompressionTreshold(packetIn.getThreshold());
      }
   }

   @Override
   public void handlePlayerListHeaderFooter(S47PacketPlayerListHeaderFooter packetIn) {
      this.gameController.ingameGUI.getTabList().setHeader(packetIn.getHeader().getFormattedText().length() == 0 ? null : packetIn.getHeader());
      this.gameController.ingameGUI.getTabList().setFooter(packetIn.getFooter().getFormattedText().length() == 0 ? null : packetIn.getFooter());
   }

   @Override
   public void handleRemoveEntityEffect(S1EPacketRemoveEntityEffect packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityId());
      if (entityByID instanceof EntityLivingBase) {
         ((EntityLivingBase)entityByID).removePotionEffectClient(packetIn.getEffectId());
      }
   }

   @Override
   public void handlePlayerListItem(S38PacketPlayerListItem packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);

      for (AddPlayerData addPlayerData : packetIn.getEntries()) {
         if (packetIn.getAction() == S38PacketPlayerListItem.Action.REMOVE_PLAYER) {
            this.playerInfoMap.remove(addPlayerData.getProfile().getId());
         } else {
            NetworkPlayerInfo networkPlayerInfo = this.playerInfoMap.get(addPlayerData.getProfile().getId());
            if (packetIn.getAction() == S38PacketPlayerListItem.Action.ADD_PLAYER) {
               networkPlayerInfo = new NetworkPlayerInfo(addPlayerData);
               this.playerInfoMap.put(networkPlayerInfo.getGameProfile().getId(), networkPlayerInfo);
            }

            if (networkPlayerInfo != null) {
               switch (packetIn.getAction()) {
                  case ADD_PLAYER:
                     networkPlayerInfo.setGameType(addPlayerData.getGameMode());
                     networkPlayerInfo.setResponseTime(addPlayerData.getPing());
                     break;
                  case UPDATE_GAME_MODE:
                     networkPlayerInfo.setGameType(addPlayerData.getGameMode());
                     break;
                  case UPDATE_LATENCY:
                     networkPlayerInfo.setResponseTime(addPlayerData.getPing());
                     break;
                  case UPDATE_DISPLAY_NAME:
                     networkPlayerInfo.setDisplayName(addPlayerData.getDisplayName());
               }
            }
         }
      }
   }

   @Override
   public void handleKeepAlive(S00PacketKeepAlive packetIn) {
      CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent20());
      this.addToSendQueue(new C00PacketKeepAlive(packetIn.getId()));
   }

   @Override
   public void handlePlayerAbilities(S39PacketPlayerAbilities packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      EntityPlayerSP thePlayer = this.gameController.thePlayer;
      thePlayer.capabilities.isFlying = packetIn.isFlying();
      thePlayer.capabilities.isCreativeMode = packetIn.isCreativeMode();
      thePlayer.capabilities.disableDamage = packetIn.isInvulnerable();
      thePlayer.capabilities.allowFlying = packetIn.isAllowFlying();
      thePlayer.capabilities.setFlySpeed(packetIn.getFlySpeed());
      ToggleSprintMod.floatValue = packetIn.getFlySpeed();
      thePlayer.capabilities.setPlayerWalkSpeed(packetIn.getWalkSpeed());
   }

   @Override
   public void handleTabComplete(S3APacketTabComplete packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      String[] suggestions = packetIn.getSuggestions();
      if (this.gameController.currentScreen instanceof GuiChat) {
         GuiChat currentScreen = (GuiChat)this.gameController.currentScreen;
         currentScreen.onAutocompleteResponse(suggestions);
      }
   }

   @Override
   public void handleSoundEffect(S29PacketSoundEffect packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      this.gameController
         .theWorld
         .playSound(packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getSoundName(), packetIn.getVolume(), packetIn.getPitch(), false);
   }

   @Override
   public void handleResourcePack(S48PacketResourcePackSend packetIn) {
      final String uRL = packetIn.getURL();
      final String hash = packetIn.getHash();
      if (uRL.startsWith("level://")) {
         String substring = uRL.substring("level://".length());
         File file = new File(this.gameController.mcDataDir, "saves");
         File file2 = new File(file, substring);
         if (file2.isFile()) {
            this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.ACCEPTED));
            Futures.addCallback(
               this.gameController.getResourcePackRepository().setResourcePackInstance(file2),
               new FutureCallback<Object>() {
                  public void onSuccess(Object obj) {
                     NetHandlerPlayClient.this.netManager
                        .sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                  }

                  public void onFailure(Throwable throwable) {
                     NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                  }
               },
               listeningExecutorService
            );
         } else {
            this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
         }
      } else if (this.gameController.getCurrentServerData() != null
         && this.gameController.getCurrentServerData().getResourceMode() == ServerResourceMode.ENABLED) {
         this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.ACCEPTED));
         Futures.addCallback(this.gameController.getResourcePackRepository().downloadResourcePack(uRL, hash), new FutureCallback<Object>() {
            public void onSuccess(Object obj) {
               NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
            }

            public void onFailure(Throwable throwable) {
               NetHandlerPlayClient.this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
            }
         }, listeningExecutorService);
      } else if (this.gameController.getCurrentServerData() != null
         && this.gameController.getCurrentServerData().getResourceMode() != ServerResourceMode.PROMPT) {
         this.netManager.sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.DECLINED));
      } else {
         this.gameController
            .addScheduledTask(
               new Runnable() {
                  @Override
                  public void run() {
                     NetHandlerPlayClient.this.gameController
                        .displayGuiScreen(
                           new GuiYesNo(
                              new GuiYesNoCallback() {
                                 @Override
                                 public void confirmClicked(boolean result, int id) {
                                    NetHandlerPlayClient.this.gameController = Minecraft.getMinecraft();
                                    if (result) {
                                       if (NetHandlerPlayClient.this.gameController.getCurrentServerData() != null) {
                                          NetHandlerPlayClient.this.gameController
                                             .getCurrentServerData()
                                             .setResourceMode(ServerResourceMode.ENABLED);
                                       }

                                       NetHandlerPlayClient.this.netManager
                                          .sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.ACCEPTED));
                                       Futures.addCallback(
                                          NetHandlerPlayClient.this.gameController.getResourcePackRepository().downloadResourcePack(uRL, hash),
                                          new FutureCallback<Object>() {
                                             public void onSuccess(Object obj) {
                                                NetHandlerPlayClient.this.netManager
                                                   .sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.SUCCESSFULLY_LOADED));
                                             }

                                             public void onFailure(Throwable throwable) {
                                                NetHandlerPlayClient.this.netManager
                                                   .sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.FAILED_DOWNLOAD));
                                             }
                                          },
                                          NetHandlerPlayClient.listeningExecutorService
                                       );
                                    } else {
                                       if (NetHandlerPlayClient.this.gameController.getCurrentServerData() != null) {
                                          NetHandlerPlayClient.this.gameController
                                             .getCurrentServerData()
                                             .setResourceMode(ServerResourceMode.DISABLED);
                                       }

                                       NetHandlerPlayClient.this.netManager
                                          .sendPacket(new C19PacketResourcePackStatus(hash, C19PacketResourcePackStatus.Action.DECLINED));
                                    }

                                    ServerList.updateServerListEntry(NetHandlerPlayClient.this.gameController.getCurrentServerData());
                                    NetHandlerPlayClient.this.gameController.displayGuiScreen((GuiScreen)null);
                                 }
                              },
                              I18n.format("multiplayer.texturePrompt.line1"),
                              I18n.format("multiplayer.texturePrompt.line2"),
                              0
                           )
                        );
                  }
               }
            );
      }
   }

   @Override
   public void handleEntityNBT(S49PacketUpdateEntityNBT packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entity = packetIn.getEntity(this.clientWorldController);
      if (entity != null) {
         entity.clientUpdateEntityNBT(packetIn.getTagCompound());
      }
   }

   @Override
   public void handleCustomPayload(S3FPacketCustomPayload packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      PacketBuffer bufferData = packetIn.getBufferData();
      packetIn.getBufferData().markReaderIndex();
      byte[] bytes = new byte[bufferData.readableBytes()];
      bufferData.readBytes(bytes);
      CheatBreaker.getInstance().getEventBus().handleEvent(new StringByteArrayEvent(packetIn.getChannelName(), bytes));
      bufferData.resetReaderIndex();
      if ("MC|TrList".equals(packetIn.getChannelName())) {
         PacketBuffer bufferData2 = packetIn.getBufferData();

         try {
            int readInt = bufferData2.readInt();
            GuiScreen currentScreen = this.gameController.currentScreen;
            if (currentScreen != null && currentScreen instanceof GuiMerchant && readInt == this.gameController.thePlayer.openContainer.windowId) {
               IMerchant merchant = ((GuiMerchant)currentScreen).getMerchant();
               MerchantRecipeList readFromBuf = MerchantRecipeList.readFromBuf(bufferData2);
               merchant.setRecipes(readFromBuf);
            }
         } catch (IOException e) {
            logger.error("Couldn't load trade info", e);
         } finally {
            bufferData2.release();
         }
      } else if ("MC|Brand".equals(packetIn.getChannelName())) {
         this.gameController.thePlayer.setClientBrand(packetIn.getBufferData().readStringFromBuffer(32767));
      } else if ("MC|BOpen".equals(packetIn.getChannelName())) {
         ItemStack currentEquippedItem = this.gameController.thePlayer.getCurrentEquippedItem();
         if (currentEquippedItem != null && currentEquippedItem.getItem() == Items.written_book) {
            this.gameController.displayGuiScreen(new GuiScreenBook(this.gameController.thePlayer, currentEquippedItem, false));
         }
      }
   }

   @Override
   public void handleScoreboardObjective(S3BPacketScoreboardObjective packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Scoreboard scoreboard = this.clientWorldController.getScoreboard();
      if (packetIn.getMode() == 0) {
         ScoreObjective addScoreObjective = scoreboard.addScoreObjective(packetIn.getName(), IScoreObjectiveCriteria.DUMMY);
         addScoreObjective.setDisplayName(packetIn.getDisplayName());
         addScoreObjective.setRenderType(packetIn.getType());
      } else {
         ScoreObjective objective = scoreboard.getObjective(packetIn.getName());
         if (packetIn.getMode() == 1) {
            scoreboard.removeObjective(objective);
         } else if (packetIn.getMode() == 2) {
            objective.setDisplayName(packetIn.getDisplayName());
            objective.setRenderType(packetIn.getType());
         }
      }
   }

   @Override
   public void handleUpdateScore(S3CPacketUpdateScore packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Scoreboard scoreboard = this.clientWorldController.getScoreboard();
      ScoreObjective objective = scoreboard.getObjective(packetIn.getObjectiveName());
      if (packetIn.getScoreAction() == S3CPacketUpdateScore.Action.CHANGE) {
         Score valueFromObjective = scoreboard.getValueFromObjective(packetIn.getPlayerName(), objective);
         valueFromObjective.setScorePoints(packetIn.getScoreValue());
      } else if (packetIn.getScoreAction() == S3CPacketUpdateScore.Action.REMOVE) {
         if (StringUtils.isNullOrEmpty(packetIn.getObjectiveName())) {
            scoreboard.removeObjectiveFromEntity(packetIn.getPlayerName(), (ScoreObjective)null);
         } else if (objective != null) {
            scoreboard.removeObjectiveFromEntity(packetIn.getPlayerName(), objective);
         }
      }
   }

   @Override
   public void handleDisplayScoreboard(S3DPacketDisplayScoreboard packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Scoreboard scoreboard = this.clientWorldController.getScoreboard();
      if (packetIn.getName().length() == 0) {
         scoreboard.setObjectiveInDisplaySlot(packetIn.getSlot(), (ScoreObjective)null);
      } else {
         ScoreObjective objective = scoreboard.getObjective(packetIn.getName());
         scoreboard.setObjectiveInDisplaySlot(packetIn.getSlot(), objective);
      }
   }

   @Override
   public void handleTeams(S3EPacketTeams packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Scoreboard scoreboard = this.clientWorldController.getScoreboard();
      ScorePlayerTeam scorePlayerTeam;
      if (packetIn.getAction() == 0) {
         scorePlayerTeam = scoreboard.createTeam(packetIn.getName());
      } else {
         scorePlayerTeam = scoreboard.getTeam(packetIn.getName());
      }

      if ((packetIn.getAction() == 0 || packetIn.getAction() == 2) && scorePlayerTeam != null) {
         scorePlayerTeam.setTeamName(packetIn.getDisplayName());
         scorePlayerTeam.setNamePrefix(packetIn.getPrefix());
         scorePlayerTeam.setNameSuffix(packetIn.getSuffix());
         scorePlayerTeam.setChatFormat(EnumChatFormatting.byColorIndex(packetIn.getColor()));
         scorePlayerTeam.setFriendlyFlagsBitwise(packetIn.getFriendlyFlags());
         EnumVisible ruleByName = EnumVisible.getRuleByName(packetIn.getNameTagVisibility());
         if (ruleByName != null) {
            scorePlayerTeam.setNameTagVisibility(ruleByName);
         }
      }

      if (packetIn.getAction() == 0 || packetIn.getAction() == 3) {
         for (String str : packetIn.getPlayers()) {
            scoreboard.addPlayerToTeam(str, packetIn.getName());
         }
      }

      if (packetIn.getAction() == 4) {
         for (String str : packetIn.getPlayers()) {
            scoreboard.removePlayerFromTeam(str, scorePlayerTeam);
         }
      }

      if (packetIn.getAction() == 1) {
         scoreboard.removeTeam(scorePlayerTeam);
      }
   }

   @Override
   public void handleParticles(S2APacketParticles packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      if (packetIn.getParticleCount() == 0) {
         double d = packetIn.getParticleSpeed() * packetIn.getXOffset();
         double d2 = packetIn.getParticleSpeed() * packetIn.getYOffset();
         double d3 = packetIn.getParticleSpeed() * packetIn.getZOffset();

         try {
            this.clientWorldController
               .spawnParticle(
                  packetIn.getParticleType(),
                  packetIn.isLongDistance(),
                  packetIn.getXCoordinate(),
                  packetIn.getYCoordinate(),
                  packetIn.getZCoordinate(),
                  d,
                  d2,
                  d3,
                  packetIn.getParticleArgs()
               );
         } catch (Throwable e) {
            logger.warn("Could not spawn particle effect " + packetIn.getParticleType());
         }
      } else {
         for (int i = 0; i < packetIn.getParticleCount(); i++) {
            double d = this.avRandomizer.nextGaussian() * packetIn.getXOffset();
            double d2 = this.avRandomizer.nextGaussian() * packetIn.getYOffset();
            double d3 = this.avRandomizer.nextGaussian() * packetIn.getZOffset();
            double d4 = this.avRandomizer.nextGaussian() * packetIn.getParticleSpeed();
            double d5 = this.avRandomizer.nextGaussian() * packetIn.getParticleSpeed();
            double d6 = this.avRandomizer.nextGaussian() * packetIn.getParticleSpeed();

            try {
               this.clientWorldController
                  .spawnParticle(
                     packetIn.getParticleType(),
                     packetIn.isLongDistance(),
                     packetIn.getXCoordinate() + d,
                     packetIn.getYCoordinate() + d2,
                     packetIn.getZCoordinate() + d3,
                     d4,
                     d5,
                     d6,
                     packetIn.getParticleArgs()
                  );
            } catch (Throwable e) {
               logger.warn("Could not spawn particle effect " + packetIn.getParticleType());
               return;
            }
         }
      }
   }

   @Override
   public void handleEntityProperties(S20PacketEntityProperties packetIn) {
      PacketThreadUtil.checkThreadAndEnqueue(packetIn, this, this.gameController);
      Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityId());
      if (entityByID != null) {
         if (!(entityByID instanceof EntityLivingBase)) {
            throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entityByID + ")");
         }

         BaseAttributeMap attributeMap = ((EntityLivingBase)entityByID).getAttributeMap();

         for (Snapshot snapshot : packetIn.getEntries()) {
            IAttributeInstance attributeInstanceByName = attributeMap.getAttributeInstanceByName(snapshot.getId());
            if (attributeInstanceByName == null) {
               attributeInstanceByName = attributeMap.registerAttribute(new RangedAttribute((IAttribute)null, snapshot.getId(), 0.0, Double.MIN_NORMAL, Double.MAX_VALUE));
            }

            attributeInstanceByName.setBaseValue(snapshot.getBaseValue());
            attributeInstanceByName.removeAllModifiers();

            for (AttributeModifier attributeModifier : snapshot.getModifiers()) {
               attributeInstanceByName.applyModifier(attributeModifier);
            }
         }
      }
   }

   public NetworkManager getNetworkManager() {
      return this.netManager;
   }

   public Collection<NetworkPlayerInfo> getPlayerInfoMap() {
      return this.playerInfoMap.values();
   }

   public NetworkPlayerInfo getPlayerInfo(UUID uuid) {
      return this.playerInfoMap.get(uuid);
   }

   public NetworkPlayerInfo getPlayerInfo(String str) {
      for (NetworkPlayerInfo networkPlayerInfo : this.playerInfoMap.values()) {
         if (networkPlayerInfo.getGameProfile().getName().equals(str)) {
            return networkPlayerInfo;
         }
      }

      return null;
   }

   public GameProfile getGameProfile() {
      return this.profile;
   }
}
