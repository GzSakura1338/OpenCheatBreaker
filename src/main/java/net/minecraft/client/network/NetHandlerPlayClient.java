package net.minecraft.client.network;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.EntityS19PacketEntityStatusEvent;
import com.cheatbreaker.client.util.SneakyThrow;
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
import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.GuiMerchant;
import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiPlayerInfo;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenDemo;
import net.minecraft.client.gui.GuiScreenRealmsProxy;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import net.minecraft.client.gui.IProgressMeter;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerList;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.multiplayer.ServerData.ServerResourceMode;
import net.minecraft.client.particle.EntityCrit2FX;
import net.minecraft.client.particle.EntityPickupFX;
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
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.ai.attributes.RangedAttribute;
import net.minecraft.entity.effect.EntityLightningBolt;
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
import net.minecraft.inventory.AnimalChest;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMap;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C03PacketPlayer.C06PacketPlayerPosLook;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S04PacketEntityEquipment;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S06PacketUpdateHealth;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
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
import net.minecraft.network.play.server.S21PacketChunkData;
import net.minecraft.network.play.server.S22PacketMultiBlockChange;
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
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S3BPacketScoreboardObjective;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.network.play.server.S3DPacketDisplayScoreboard;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.network.play.server.S20PacketEntityProperties.Snapshot;
import net.minecraft.potion.PotionEffect;
import net.minecraft.realms.DisconnectedOnlineScreen;
import net.minecraft.scoreboard.IScoreObjectiveCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityBrewingStand;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.tileentity.TileEntityDropper;
import net.minecraft.tileentity.TileEntityFlowerPot;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.tileentity.TileEntityHopper;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.tileentity.TileEntitySkull;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.Explosion;
import net.minecraft.world.WorldProviderSurface;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.storage.MapData;
import net.minecraft.world.storage.MapStorage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayClient implements INetHandlerPlayClient {
    public static Logger logger = LogManager.getLogger();
    public MapStorage mapStorageOrigin = new MapStorage(null);
    public List playerInfoList = new ArrayList();
    public WorldClient clientWorldController;
    public boolean doneLoadingTerrain;
    public int currentServerMaxPlayers = 20;
    public Map playerInfoMap = new HashMap();
    public boolean flag = false;
    public Random avRandomizer = new Random();
    public Minecraft gameController;
    public GuiScreen guiScreenServer;
    public NetworkManager netManager;

    public NetHandlerPlayClient(Minecraft mc, GuiScreen guiScreen, NetworkManager networkManager) {
        this.gameController = mc;
        this.guiScreenServer = guiScreen;
        this.netManager = networkManager;
    }

    public void cleanup() {
        this.clientWorldController = null;
    }

    @Override
    public void onNetworkTick() {
    }

    @Override
    public void handleJoinGame(S01PacketJoinGame packetIn) throws IOException {
        this.gameController.playerController = new PlayerControllerMP(this.gameController, this);
        this.clientWorldController = new WorldClient(
            this,
            new WorldSettings(0L, packetIn.getGameMode(), false, packetIn.isHardcore(), packetIn.getGeneratorType()),
            packetIn.getChunkLoadDistance(),
            packetIn.getDifficulty(),
            this.gameController.mcProfiler
        );
        this.clientWorldController.isRemote = true;
        this.gameController.loadWorld(this.clientWorldController);
        this.gameController.thePlayer.dimension = packetIn.getChunkLoadDistance();
        this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
        this.gameController.thePlayer.setEntityId(packetIn.getEntityId());
        this.currentServerMaxPlayers = packetIn.getMaxPlayers();
        this.gameController.playerController.setGameType(packetIn.getGameMode());
        this.gameController.gameSettings.sendSettingsToServer();
        this.netManager.scheduleOutboundPacket(new C17PacketCustomPayload("MC|Brand", ClientBrandRetriever.getClientModName().getBytes(Charsets.UTF_8)));
        CheatBreaker.getInstance().getEventBus().handleEvent(new S01PacketJoinGameEvent(packetIn));
    }

    @Override
    public void handleSpawnObject(S0EPacketSpawnObject packetIn) {
        double d = packetIn.getX() / 32.0;
        double d2 = packetIn.getY() / 32.0;
        double d3 = packetIn.getZ() / 32.0;
        Entity entity = null;
        if (packetIn.getEntityData() == 10) {
            entity = EntityMinecart.createMinecart(this.clientWorldController, d, d2, d3, packetIn.getDataId());
        } else if (packetIn.getEntityData() == 90) {
            Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getDataId());
            if (entityByID instanceof EntityPlayer) {
                entity = new EntityFishHook(this.clientWorldController, d, d2, d3, (EntityPlayer)entityByID);
            }

            packetIn.setDataId(0);
        } else if (packetIn.getEntityData() == 60) {
            entity = new EntityArrow(this.clientWorldController, d, d2, d3);
        } else if (packetIn.getEntityData() == 61) {
            entity = new EntitySnowball(this.clientWorldController, d, d2, d3);
        } else if (packetIn.getEntityData() == 71) {
            entity = new EntityItemFrame(this.clientWorldController, (int)d, (int)d2, (int)d3, packetIn.getDataId());
            packetIn.setDataId(0);
        } else if (packetIn.getEntityData() == 77) {
            entity = new EntityLeashKnot(this.clientWorldController, (int)d, (int)d2, (int)d3);
            packetIn.setDataId(0);
        } else if (packetIn.getEntityData() == 65) {
            entity = new EntityEnderPearl(this.clientWorldController, d, d2, d3);
        } else if (packetIn.getEntityData() == 72) {
            entity = new EntityEnderEye(this.clientWorldController, d, d2, d3);
        } else if (packetIn.getEntityData() == 76) {
            entity = new EntityFireworkRocket(this.clientWorldController, d, d2, d3, null);
        } else if (packetIn.getEntityData() == 63) {
            entity = new EntityLargeFireball(
                this.clientWorldController, d, d2, d3, packetIn.getVelocityX() / 8000.0, packetIn.getVelocityY() / 8000.0, packetIn.getVelocityZ() / 8000.0
            );
            packetIn.setDataId(0);
        } else if (packetIn.getEntityData() == 64) {
            entity = new EntitySmallFireball(
                this.clientWorldController, d, d2, d3, packetIn.getVelocityX() / 8000.0, packetIn.getVelocityY() / 8000.0, packetIn.getVelocityZ() / 8000.0
            );
            packetIn.setDataId(0);
        } else if (packetIn.getEntityData() == 66) {
            entity = new EntityWitherSkull(
                this.clientWorldController, d, d2, d3, packetIn.getVelocityX() / 8000.0, packetIn.getVelocityY() / 8000.0, packetIn.getVelocityZ() / 8000.0
            );
            packetIn.setDataId(0);
        } else if (packetIn.getEntityData() == 62) {
            entity = new EntityEgg(this.clientWorldController, d, d2, d3);
        } else if (packetIn.getEntityData() == 73) {
            entity = new EntityPotion(this.clientWorldController, d, d2, d3, packetIn.getDataId());
            packetIn.setDataId(0);
        } else if (packetIn.getEntityData() == 75) {
            entity = new EntityExpBottle(this.clientWorldController, d, d2, d3);
            packetIn.setDataId(0);
        } else if (packetIn.getEntityData() == 1) {
            entity = new EntityBoat(this.clientWorldController, d, d2, d3);
        } else if (packetIn.getEntityData() == 50) {
            entity = new EntityTNTPrimed(this.clientWorldController, d, d2, d3, null);
        } else if (packetIn.getEntityData() == 51) {
            entity = new EntityEnderCrystal(this.clientWorldController, d, d2, d3);
        } else if (packetIn.getEntityData() == 2) {
            entity = new EntityItem(this.clientWorldController, d, d2, d3);
        } else if (packetIn.getEntityData() == 70) {
            entity = new EntityFallingBlock(
                this.clientWorldController, d, d2, d3, Block.getBlockById(packetIn.getDataId() & 65535), packetIn.getDataId() >> 16
            );
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
                int n = packetIn.getId() - entity.getEntityId();

                for (int i = 0; i < parts.length; i++) {
                    parts[i].setEntityId(parts[i].getEntityId() + n);
                }
            }

            entity.setEntityId(packetIn.getId());
            this.clientWorldController.addEntityToWorld(packetIn.getId(), entity);
            if (packetIn.getDataId() > 0) {
                if (packetIn.getEntityData() == 60) {
                    Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getDataId());
                    if (entityByID instanceof EntityLivingBase) {
                        EntityArrow entityArrow = (EntityArrow)entity;
                        entityArrow.shootingEntity = entityByID;
                    }
                }

                entity.setVelocity(packetIn.getVelocityX() / 8000.0, packetIn.getVelocityY() / 8000.0, packetIn.getVelocityZ() / 8000.0);
            }
        }
    }

    @Override
    public void handleSpawnExperienceOrb(S11PacketSpawnExperienceOrb packetIn) {
        EntityXPOrb entityXPOrb = new EntityXPOrb(this.clientWorldController, packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getExperience());
        entityXPOrb.serverPosX = packetIn.getX();
        entityXPOrb.serverPosY = packetIn.getY();
        entityXPOrb.serverPosZ = packetIn.getZ();
        entityXPOrb.rotationYaw = 0.0F;
        entityXPOrb.rotationPitch = 0.0F;
        entityXPOrb.setEntityId(packetIn.getId());
        this.clientWorldController.addEntityToWorld(packetIn.getId(), entityXPOrb);
    }

    @Override
    public void handleSpawnGlobalEntity(S2CPacketSpawnGlobalEntity packetIn) {
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
        EntityPainting entityPainting = new EntityPainting(
            this.clientWorldController, packetIn.getIntValue(), packetIn.getIntValue2(), packetIn.getIntValue3(), packetIn.getIntValue4(), packetIn.getName()
        );
        this.clientWorldController.addEntityToWorld(packetIn.getId(), entityPainting);
    }

    @Override
    public void handleEntityVelocity(S12PacketEntityVelocity packetIn) {
        Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getId());
        if (entityByID != null) {
            entityByID.setVelocity(packetIn.getVelocityX() / 8000.0, packetIn.getVelocityY() / 8000.0, packetIn.getVelocityZ() / 8000.0);
        }
    }

    @Override
    public void handleEntityMetadata(S1CPacketEntityMetadata packetIn) {
        Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getId());
        if (entityByID != null && packetIn.getTrackedValues() != null) {
            entityByID.getDataWatcher().updateWatchedObjectsFromList(packetIn.getTrackedValues());
        }
    }

    @Override
    public void handleSpawnPlayer(S0CPacketSpawnPlayer packetIn) {
        double d = packetIn.getX() / 32.0;
        double d2 = packetIn.getY() / 32.0;
        double d3 = packetIn.getZ() / 32.0;
        float f = packetIn.getYaw() * 360 / 256.0F;
        float f2 = packetIn.getPitch() * 360 / 256.0F;
        GameProfile gameProfile = packetIn.getGameProfile();
        EntityOtherPlayerMP entityOtherPlayerMP = new EntityOtherPlayerMP(this.gameController.theWorld, packetIn.getGameProfile());
        entityOtherPlayerMP.prevPosX = entityOtherPlayerMP.lastTickPosX = entityOtherPlayerMP.serverPosX = packetIn.getX();
        entityOtherPlayerMP.prevPosY = entityOtherPlayerMP.lastTickPosY = entityOtherPlayerMP.serverPosY = packetIn.getY();
        entityOtherPlayerMP.prevPosZ = entityOtherPlayerMP.lastTickPosZ = entityOtherPlayerMP.serverPosZ = packetIn.getZ();
        int handStackId = packetIn.getHandStackId();
        if (handStackId == 0) {
            entityOtherPlayerMP.inventory.mainInventory[entityOtherPlayerMP.inventory.currentItem] = null;
        } else {
            entityOtherPlayerMP.inventory.mainInventory[entityOtherPlayerMP.inventory.currentItem] = new ItemStack(Item.getItemById(handStackId), 1, 0);
        }

        entityOtherPlayerMP.setPositionAndRotation(d, d2, d3, f, f2);
        this.clientWorldController.addEntityToWorld(packetIn.getId(), entityOtherPlayerMP);
        List dataTrackerEntries = packetIn.getDataTrackerEntries();
        if (dataTrackerEntries != null) {
            entityOtherPlayerMP.getDataWatcher().updateWatchedObjectsFromList(dataTrackerEntries);
        }
    }

    @Override
    public void handleEntityTeleport(S18PacketEntityTeleport packetIn) {
        Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getId());
        if (entityByID != null) {
            entityByID.serverPosX = packetIn.getX();
            entityByID.serverPosY = packetIn.getY();
            entityByID.serverPosZ = packetIn.getZ();
            double d = entityByID.serverPosX / 32.0;
            double d2 = entityByID.serverPosY / 32.0 + 0.015625;
            double d3 = entityByID.serverPosZ / 32.0;
            float f = packetIn.getYaw() * 360 / 256.0F;
            float f2 = packetIn.getPitch() * 360 / 256.0F;
            entityByID.setPositionAndRotation2(d, d2, d3, f, f2, 3);
        }
    }

    @Override
    public void handleHeldItemChange(S09PacketHeldItemChange packetIn) {
        if (packetIn.getSlot() >= 0 && packetIn.getSlot() < InventoryPlayer.getHotbarSize()) {
            this.gameController.thePlayer.inventory.currentItem = packetIn.getSlot();
        }
    }

    @Override
    public void handleEntityMovement(S14PacketEntity packetIn) {
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
            entity.setPositionAndRotation2(d, d2, d3, f, f2, 3);
        }
    }

    @Override
    public void handleEntityHeadLook(S19PacketEntityHeadLook packetIn) {
        Entity entity = packetIn.getEntity(this.clientWorldController);
        if (entity != null) {
            float f = packetIn.getHeadYaw() * 360 / 256.0F;
            entity.setRotationYawHead(f);
        }
    }

    @Override
    public void handleDestroyEntities(S13PacketDestroyEntities packetIn) {
        for (int i = 0; i < packetIn.getEntityIds().length; i++) {
            this.clientWorldController.removeEntityFromWorld(packetIn.getEntityIds()[i]);
        }
    }

    @Override
    public void handlePlayerPosLook(S08PacketPlayerPosLook packetIn) throws IOException {
        EntityClientPlayerMP thePlayer = this.gameController.thePlayer;
        double d = packetIn.getX();
        double d2 = packetIn.getY();
        double d3 = packetIn.getZ();
        float yaw = packetIn.getYaw();
        float pitch = packetIn.getPitch();
        thePlayer.yOffset2 = 0.0F;
        thePlayer.motionX = thePlayer.motionY = thePlayer.motionZ = 0.0;
        thePlayer.setPositionAndRotation(d, d2, d3, yaw, pitch);
        this.netManager
            .scheduleOutboundPacket(
                new C06PacketPlayerPosLook(thePlayer.posX, thePlayer.boundingBox.minY, thePlayer.posY, thePlayer.posZ, packetIn.getYaw(), packetIn.getPitch(), packetIn.isFlag())
            );
        if (!this.doneLoadingTerrain) {
            this.gameController.thePlayer.prevPosX = this.gameController.thePlayer.posX;
            this.gameController.thePlayer.prevPosY = this.gameController.thePlayer.posY;
            this.gameController.thePlayer.prevPosZ = this.gameController.thePlayer.posZ;
            this.doneLoadingTerrain = true;
            this.gameController.displayGuiScreen(null);
            CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent12());
        }
    }

    @Override
    public void handleMultiBlockChange(S22PacketMultiBlockChange packetIn) {
        int n = packetIn.getPos().chunkXPos * 16;
        int n2 = packetIn.getPos().chunkZPos * 16;
        if (packetIn.getByteArray() != null) {
            DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(packetIn.getByteArray()));

            try {
                for (int i = 0; i < packetIn.getIntValue(); i++) {
                    short readShort = dataInputStream.readShort();
                    short readShort2 = dataInputStream.readShort();
                    int n3 = readShort2 >> 4 & 4095;
                    int n4 = readShort2 & 15;
                    int n5 = readShort >> 12 & 15;
                    int n6 = readShort >> 8 & 15;
                    int n7 = readShort & 255;
                    this.clientWorldController.check(n5 + n, n7, n6 + n2, Block.getBlockById(n3), n4);
                }
            } catch (IOException e) {
            }
        }
    }

    @Override
    public void handleChunkData(S21PacketChunkData packetIn) {
        if (packetIn.shouldLoad()) {
            if (packetIn.getSections() == 0) {
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
        chunkFromChunkCoords.fillChunk(packetIn.getByteArray(), packetIn.getSections(), packetIn.getIntValue(), packetIn.shouldLoad());
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
        this.clientWorldController.check(packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getBlock(), packetIn.getBlockData());
    }

    @Override
    public void handleDisconnect(S40PacketDisconnect packetIn) {
        this.netManager.closeChannel(packetIn.getReason());
    }

    @Override
    public void onDisconnect(IChatComponent reason) throws IOException {
        this.gameController.loadWorld(null);
        CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent3());
        if (this.guiScreenServer != null) {
            if (this.guiScreenServer instanceof GuiScreenRealmsProxy) {
                this.gameController
                    .displayGuiScreen(
                        new DisconnectedOnlineScreen(((GuiScreenRealmsProxy)this.guiScreenServer).getRealmsScreen(), "disconnect.lost", reason).getProxy()
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
        this.netManager.scheduleOutboundPacket(packet);
    }

    @Override
    public void handleCollectItem(S0DPacketCollectItem packetIn) {
        Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getChunkX());
        Entity entityByID2 = this.clientWorldController.getEntityByID(packetIn.getChunkZ());
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

            this.gameController.effectRenderer.addEffect(new EntityPickupFX(this.gameController.theWorld, entityByID, entityByID2, -0.5F));
            this.clientWorldController.removeEntityFromWorld(packetIn.getChunkX());
        }
    }

    @Override
    public void handleChat(S02PacketChat packetIn) {
        CheatBreaker.getInstance().getEventBus().handleEvent(new S02PacketChatEvent(packetIn));
        this.gameController.ingameGUI.getChatGUI().printChatMessage(packetIn.getMessage());
    }

    @Override
    public void handleAnimation(S0BPacketAnimation packetIn) {
        Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getId());
        boolean flag = CheatBreaker.getInstance().getModManager().particlesMod.showWhenDamaged.getValue().equals("Override");
        boolean flag2 = CheatBreaker.getInstance().getModManager().particlesMod.showWhenDamaged.getValue().equals("Vanilla");
        if (entityByID != null) {
            if (packetIn.getAnimationId() == 0) {
                EntityLivingBase entityLivingBase = (EntityLivingBase)entityByID;
                entityLivingBase.swingItem();
            } else if (packetIn.getAnimationId() == 1) {
                entityByID.performHurtAnimation();
            } else if (packetIn.getAnimationId() == 2) {
                EntityPlayer entityPlayer = (EntityPlayer)entityByID;
                entityPlayer.wakeUpPlayer(false, false, false);
            } else if (packetIn.getAnimationId() == 4) {
                if (!CheatBreaker.getInstance().getModManager().particlesMod.isEnabled() || flag2) {
                    EntityCrit2FX entityCrit2FX = new EntityCrit2FX(this.gameController.theWorld, entityByID);
                    this.gameController.effectRenderer.addEffect(entityCrit2FX);
                } else if (flag && CheatBreaker.getInstance().getModManager().particlesMod.critParticles.getValue().equals("Vanilla")) {
                    EntityCrit2FX entityCrit2FX = new EntityCrit2FX(
                        this.gameController.theWorld, entityByID, "crit", (Float)CheatBreaker.getInstance().getModManager().particlesMod.critMultiplier.getValue()
                    );
                    this.gameController.effectRenderer.addEffect(entityCrit2FX);
                }
            } else if (packetIn.getAnimationId() == 5) {
                if (!CheatBreaker.getInstance().getModManager().particlesMod.isEnabled() || flag2) {
                    EntityCrit2FX entityCrit2FX = new EntityCrit2FX(this.gameController.theWorld, entityByID, "magicCrit");
                    this.gameController.effectRenderer.addEffect(entityCrit2FX);
                } else if (flag && CheatBreaker.getInstance().getModManager().particlesMod.sharpnessParticles.getValue().equals("Vanilla")) {
                    EntityCrit2FX entityCrit2FX = new EntityCrit2FX(
                        this.gameController.theWorld,
                        entityByID,
                        "magicCrit",
                        (Float)CheatBreaker.getInstance().getModManager().particlesMod.sharpnessMultiplier.getValue()
                    );
                    this.gameController.effectRenderer.addEffect(entityCrit2FX);
                }
            }

            if (CheatBreaker.getInstance().getModManager().particlesMod.isEnabled()
                && flag
                && (packetIn.getAnimationId() == 4 || packetIn.getAnimationId() == 5)
                && !flag2) {
                if (CheatBreaker.getInstance().getModManager().particlesMod.critParticles.getValue().equals("Always")) {
                    EntityCrit2FX entityCrit2FX = new EntityCrit2FX(
                        this.gameController.theWorld, entityByID, "crit", (Float)CheatBreaker.getInstance().getModManager().particlesMod.critMultiplier.getValue()
                    );
                    this.gameController.effectRenderer.addEffect(entityCrit2FX);
                }

                if (CheatBreaker.getInstance().getModManager().particlesMod.sharpnessParticles.getValue().equals("Always")) {
                    EntityCrit2FX entityCrit2FX = new EntityCrit2FX(
                        this.gameController.theWorld,
                        entityByID,
                        "magicCrit",
                        (Float)CheatBreaker.getInstance().getModManager().particlesMod.sharpnessMultiplier.getValue()
                    );
                    this.gameController.effectRenderer.addEffect(entityCrit2FX);
                }
            }
        }
    }

    @Override
    public void handleUseBed(S0APacketUseBed packetIn) {
        packetIn.getPlayer(this.clientWorldController).sleepInBedAt(packetIn.getX(), packetIn.getY(), packetIn.getZ());
    }

    @Override
    public void handleSpawnMob(S0FPacketSpawnMob packetIn) {
        double d = packetIn.getX() / 32.0;
        double d2 = packetIn.getY() / 32.0;
        double d3 = packetIn.getZ() / 32.0;
        float f = packetIn.getYaw() * 360 / 256.0F;
        float f2 = packetIn.getPitch() * 360 / 256.0F;
        EntityLivingBase createEntityByID = (EntityLivingBase)EntityList.createEntityByID(packetIn.getEntityTypeId(), this.gameController.theWorld);
        createEntityByID.serverPosX = packetIn.getX();
        createEntityByID.serverPosY = packetIn.getY();
        createEntityByID.serverPosZ = packetIn.getZ();
        createEntityByID.rotationYawHead = packetIn.getHeadYaw() * 360 / 256.0F;
        Entity[] parts = createEntityByID.getParts();
        if (parts != null) {
            int n = packetIn.getId() - createEntityByID.getEntityId();

            for (int i = 0; i < parts.length; i++) {
                parts[i].setEntityId(parts[i].getEntityId() + n);
            }
        }

        createEntityByID.setEntityId(packetIn.getId());
        createEntityByID.setPositionAndRotation(d, d2, d3, f, f2);
        createEntityByID.motionX = packetIn.getVelocityX() / 8000.0F;
        createEntityByID.motionY = packetIn.getVelocityY() / 8000.0F;
        createEntityByID.motionZ = packetIn.getVelocityZ() / 8000.0F;
        this.clientWorldController.addEntityToWorld(packetIn.getId(), createEntityByID);
        List entries = packetIn.getEntries();
        if (entries != null) {
            createEntityByID.getDataWatcher().updateWatchedObjectsFromList(entries);
        }
    }

    @Override
    public void handleTimeUpdate(S03PacketTimeUpdate packetIn) {
        this.gameController.theWorld.setTime(packetIn.getTime());
        S03PacketTimeUpdateEvent s03PacketTimeUpdateEvent = new S03PacketTimeUpdateEvent(packetIn);
        CheatBreaker.getInstance().getEventBus().handleEvent(s03PacketTimeUpdateEvent);
        if (!s03PacketTimeUpdateEvent.isCancelled()) {
            this.gameController.theWorld.setWorldTime(packetIn.getTimeOfDay());
        }
    }

    @Override
    public void handleSpawnPosition(S05PacketSpawnPosition packetIn) {
        this.gameController.thePlayer.setSpawnChunk(new ChunkCoordinates(packetIn.getIntValue(), packetIn.getIntValue2(), packetIn.getIntValue3()), true);
        this.gameController.theWorld.getWorldInfo().setSpawnPosition(packetIn.getIntValue(), packetIn.getIntValue2(), packetIn.getIntValue3());
    }

    @Override
    public void handleEntityAttach(S1BPacketEntityAttach packetIn) {
        Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getId());
        Entity entityByID2 = this.clientWorldController.getEntityByID(packetIn.getHoldingEntityId());
        if (packetIn.getAttachedEntityId() == 0) {
            boolean flag = false;
            if (packetIn.getId() == this.gameController.thePlayer.getEntityId()) {
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
        } else if (packetIn.getAttachedEntityId() == 1 && entityByID != null && entityByID instanceof EntityLiving) {
            if (entityByID2 != null) {
                ((EntityLiving)entityByID).setLeashedToEntity(entityByID2, false);
            } else {
                ((EntityLiving)entityByID).clearLeashed(false, false);
            }
        }
    }

    @Override
    public void handleEntityStatus(S19PacketEntityStatus packetIn) {
        Entity entity = packetIn.getEntity(this.clientWorldController);
        if (entity != null) {
            entity.handleHealthUpdate(packetIn.getStatus());
            CheatBreaker.getInstance().getEventBus().handleEvent(new EntityS19PacketEntityStatusEvent(packetIn, entity));
        }
    }

    @Override
    public void handleUpdateHealth(S06PacketUpdateHealth packetIn) {
        this.gameController.thePlayer.setPlayerSPHealth(packetIn.getHealth());
        this.gameController.thePlayer.getFoodStats().setFoodLevel(packetIn.getFoodLevel());
        this.gameController.thePlayer.getFoodStats().setFoodSaturationLevel(packetIn.getSaturationLevel());
        CheatBreaker.getInstance().getEventBus().handleEvent(new S06PacketUpdateHealthEvent(packetIn));
    }

    @Override
    public void handleSetExperience(S1FPacketSetExperience packetIn) {
        this.gameController.thePlayer.setXPStats(packetIn.getBarProgress(), packetIn.getExperienceLevel(), packetIn.getExperience());
    }

    @Override
    public void handleRespawn(S07PacketRespawn packetIn) throws IOException {
        if (packetIn.getDimensionId() != this.gameController.thePlayer.dimension) {
            this.doneLoadingTerrain = false;
            Scoreboard scoreboard = this.clientWorldController.getScoreboard();
            this.clientWorldController = new WorldClient(
                this,
                new WorldSettings(
                    0L, packetIn.getGameType(), false, this.gameController.theWorld.getWorldInfo().isHardcoreModeEnabled(), packetIn.getGeneratorType()
                ),
                packetIn.getDimensionId(),
                packetIn.getDifficulty(),
                this.gameController.mcProfiler
            );
            this.clientWorldController.setWorldScoreboard(scoreboard);
            this.clientWorldController.isRemote = true;
            this.gameController.loadWorld(this.clientWorldController);
            this.gameController.thePlayer.dimension = packetIn.getDimensionId();
            this.gameController.displayGuiScreen(new GuiDownloadTerrain(this));
        }

        this.gameController.setDimensionAndSpawnPlayer(packetIn.getDimensionId());
        this.gameController.playerController.setGameType(packetIn.getGameType());
    }

    @Override
    public void handleExplosion(S27PacketExplosion packetIn) {
        Explosion explosion = new Explosion(this.gameController.theWorld, null, packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getRadius());
        explosion.affectedBlockPositions = packetIn.getAffectedBlocks();
        explosion.doExplosionB(true);
        this.gameController.thePlayer.motionX = this.gameController.thePlayer.motionX + packetIn.getPlayerVelocityX();
        this.gameController.thePlayer.motionY = this.gameController.thePlayer.motionY + packetIn.getPlayerVelocityY();
        this.gameController.thePlayer.motionZ = this.gameController.thePlayer.motionZ + packetIn.getPlayerVelocityZ();
    }

    @Override
    public void handleOpenWindow(S2DPacketOpenWindow packetIn) throws IOException {
        EntityClientPlayerMP thePlayer = this.gameController.thePlayer;
        switch (packetIn.getInventoryType()) {
            case 0:
                thePlayer.displayGUIChest(new InventoryBasic(packetIn.getName(), packetIn.hasSlots(), packetIn.getSlotCount()));
                thePlayer.openContainer.windowId = packetIn.getId();
                break;
            case 1:
                thePlayer.displayGUIWorkbench(MathHelper.floor_double(thePlayer.posX), MathHelper.floor_double(thePlayer.posY), MathHelper.floor_double(thePlayer.posZ));
                thePlayer.openContainer.windowId = packetIn.getId();
                break;
            case 2:
                TileEntityFurnace tileEntityFurnace = new TileEntityFurnace();
                if (packetIn.hasSlots()) {
                    tileEntityFurnace.setCustomInventoryName(packetIn.getName());
                }

                thePlayer.handleTileEntityFurnace(tileEntityFurnace);
                thePlayer.openContainer.windowId = packetIn.getId();
                break;
            case 3:
                TileEntityDispenser tileEntityDispenser = new TileEntityDispenser();
                if (packetIn.hasSlots()) {
                    tileEntityDispenser.setCustomName(packetIn.getName());
                }

                thePlayer.handleTileEntityDispenser(tileEntityDispenser);
                thePlayer.openContainer.windowId = packetIn.getId();
                break;
            case 4:
                thePlayer.displayGUIEnchantment(
                    MathHelper.floor_double(thePlayer.posX),
                    MathHelper.floor_double(thePlayer.posY),
                    MathHelper.floor_double(thePlayer.posZ),
                    packetIn.hasSlots() ? packetIn.getName() : null
                );
                thePlayer.openContainer.windowId = packetIn.getId();
                break;
            case 5:
                TileEntityBrewingStand tileEntityBrewingStand = new TileEntityBrewingStand();
                if (packetIn.hasSlots()) {
                    tileEntityBrewingStand.setCustomName(packetIn.getName());
                }

                thePlayer.handleTileEntityBrewingStand(tileEntityBrewingStand);
                thePlayer.openContainer.windowId = packetIn.getId();
                break;
            case 6:
                thePlayer.displayGUIMerchant(new NpcMerchant(thePlayer), packetIn.hasSlots() ? packetIn.getName() : null);
                thePlayer.openContainer.windowId = packetIn.getId();
                break;
            case 7:
                TileEntityBeacon tileEntityBeacon = new TileEntityBeacon();
                thePlayer.handleTileEntityBeacon(tileEntityBeacon);
                if (packetIn.hasSlots()) {
                    tileEntityBeacon.setCustomName(packetIn.getName());
                }

                thePlayer.openContainer.windowId = packetIn.getId();
                break;
            case 8:
                thePlayer.displayGUIAnvil(MathHelper.floor_double(thePlayer.posX), MathHelper.floor_double(thePlayer.posY), MathHelper.floor_double(thePlayer.posZ));
                thePlayer.openContainer.windowId = packetIn.getId();
                break;
            case 9:
                TileEntityHopper tileEntityHopper = new TileEntityHopper();
                if (packetIn.hasSlots()) {
                    tileEntityHopper.setCustomName(packetIn.getName());
                }

                thePlayer.handleTileEntityHopper(tileEntityHopper);
                thePlayer.openContainer.windowId = packetIn.getId();
                break;
            case 10:
                TileEntityDropper tileEntityDropper = new TileEntityDropper();
                if (packetIn.hasSlots()) {
                    tileEntityDropper.setCustomName(packetIn.getName());
                }

                thePlayer.handleTileEntityDispenser(tileEntityDropper);
                thePlayer.openContainer.windowId = packetIn.getId();
                break;
            case 11:
                Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityId());
                if (entityByID != null && entityByID instanceof EntityHorse) {
                    thePlayer.displayGUIHorse((EntityHorse)entityByID, new AnimalChest(packetIn.getName(), packetIn.hasSlots(), packetIn.getSlotCount()));
                    thePlayer.openContainer.windowId = packetIn.getId();
                }
        }
    }

    @Override
    public void handleSetSlot(S2FPacketSetSlot packetIn) {
        EntityClientPlayerMP thePlayer = this.gameController.thePlayer;
        if (packetIn.getSyncId() == -1) {
            thePlayer.inventory.setItemStack(packetIn.getItemStack());
        } else {
            boolean flag = false;
            if (this.gameController.currentScreen instanceof GuiContainerCreative) {
                GuiContainerCreative currentScreen = (GuiContainerCreative)this.gameController.currentScreen;
                flag = currentScreen.getSelectedTab() != CreativeTabs.tabInventory.getTabIndex();
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
        Container container = null;
        EntityClientPlayerMP thePlayer = this.gameController.thePlayer;
        if (packetIn.getId() == 0) {
            container = thePlayer.inventoryContainer;
        } else if (packetIn.getId() == thePlayer.openContainer.windowId) {
            container = thePlayer.openContainer;
        }

        if (container != null && !packetIn.wasAccepted()) {
            this.addToSendQueue(new C0FPacketConfirmTransaction(packetIn.getId(), packetIn.getActionId(), true));
        }
    }

    @Override
    public void handleWindowItems(S30PacketWindowItems packetIn) {
        EntityClientPlayerMP thePlayer = this.gameController.thePlayer;
        if (packetIn.getScreenId() == 0) {
            thePlayer.inventoryContainer.putStacksInSlots(packetIn.getSlotStacks());
        } else if (packetIn.getScreenId() == thePlayer.openContainer.windowId) {
            thePlayer.openContainer.putStacksInSlots(packetIn.getSlotStacks());
        }
    }

    @Override
    public void handleSignEditorOpen(S36PacketSignEditorOpen packetIn) throws IOException {
        TileEntity tileEntity = this.clientWorldController.getTileEntity(packetIn.getIntValue(), packetIn.getIntValue2(), packetIn.getIntValue3());
        if (tileEntity == null) {
            tileEntity = new TileEntitySign();
            tileEntity.setWorldObj(this.clientWorldController);
            tileEntity.xCoord = packetIn.getIntValue();
            tileEntity.yCoord = packetIn.getIntValue2();
            tileEntity.zCoord = packetIn.getIntValue3();
        }

        this.gameController.thePlayer.displayGUIEditSign(tileEntity);
    }

    @Override
    public void handleUpdateSign(S33PacketUpdateSign packetIn) {
        boolean flag = false;
        if (this.gameController.theWorld.blockExists(packetIn.getIntValue(), packetIn.getIntValue2(), packetIn.getIntValue3())) {
            TileEntity tileEntity = this.gameController.theWorld.getTileEntity(packetIn.getIntValue(), packetIn.getIntValue2(), packetIn.getIntValue3());
            if (tileEntity instanceof TileEntitySign) {
                TileEntitySign tileEntitySign = (TileEntitySign)tileEntity;
                if (tileEntitySign.getIsEditable()) {
                    for (int i = 0; i < 4; i++) {
                        tileEntitySign.signText[i] = packetIn.getStringArray()[i];
                    }

                    tileEntitySign.markDirty();
                }

                flag = true;
            }
        }

        if (!flag && this.gameController.thePlayer != null) {
            this.gameController
                .thePlayer
                .addChatMessage(
                    new ChatComponentText("Unable to locate sign at " + packetIn.getIntValue() + ", " + packetIn.getIntValue2() + ", " + packetIn.getIntValue3())
                );
        }
    }

    @Override
    public void handleUpdateTileEntity(S35PacketUpdateTileEntity packetIn) {
        if (this.gameController.theWorld.blockExists(packetIn.getX(), packetIn.getY(), packetIn.getZ())) {
            TileEntity tileEntity = this.gameController.theWorld.getTileEntity(packetIn.getX(), packetIn.getY(), packetIn.getZ());
            if (tileEntity != null) {
                if (packetIn.getTileEntityType() == 1 && tileEntity instanceof TileEntityMobSpawner) {
                    tileEntity.readFromNBT(packetIn.getNbtCompound());
                } else if (packetIn.getTileEntityType() == 2 && tileEntity instanceof TileEntityCommandBlock) {
                    tileEntity.readFromNBT(packetIn.getNbtCompound());
                } else if (packetIn.getTileEntityType() == 3 && tileEntity instanceof TileEntityBeacon) {
                    tileEntity.readFromNBT(packetIn.getNbtCompound());
                } else if (packetIn.getTileEntityType() == 4 && tileEntity instanceof TileEntitySkull) {
                    tileEntity.readFromNBT(packetIn.getNbtCompound());
                } else if (packetIn.getTileEntityType() == 5 && tileEntity instanceof TileEntityFlowerPot) {
                    tileEntity.readFromNBT(packetIn.getNbtCompound());
                }
            }
        }
    }

    @Override
    public void handleWindowProperty(S31PacketWindowProperty packetIn) {
        EntityClientPlayerMP thePlayer = this.gameController.thePlayer;
        if (thePlayer.openContainer != null && thePlayer.openContainer.windowId == packetIn.getSyncId()) {
            thePlayer.openContainer.updateProgressBar(packetIn.getPropertyId(), packetIn.getValue());
        }
    }

    @Override
    public void handleEntityEquipment(S04PacketEntityEquipment packetIn) {
        Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getId());
        if (entityByID != null) {
            entityByID.setCurrentItemOrArmor(packetIn.getSlot(), packetIn.getStack());
        }
    }

    @Override
    public void handleCloseWindow(S2EPacketCloseWindow packetIn) throws IOException {
        this.gameController.thePlayer.closeScreenNoPacket();
    }

    @Override
    public void handleBlockAction(S24PacketBlockAction packetIn) {
        this.gameController
            .theWorld
            .addBlockEvent(packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getBlockType(), packetIn.getData1(), packetIn.getData2());
    }

    @Override
    public void handleBlockBreakAnim(S25PacketBlockBreakAnim packetIn) {
        this.gameController
            .theWorld
            .destroyBlockInWorldPartially(packetIn.getEntityId(), packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getProgress());
    }

    @Override
    public void handleMapChunkBulk(S26PacketMapChunkBulk packetIn) {
        for (int i = 0; i < packetIn.getXPosLength(); i++) {
            int xPos = packetIn.getXPos(i);
            int zPos = packetIn.getZPos(i);
            this.clientWorldController.doPreChunk(xPos, zPos, true);
            this.clientWorldController.invalidateBlockReceiveRegion(xPos << 4, 0, zPos << 4, (xPos << 4) + 15, 256, (zPos << 4) + 15);
            Chunk chunkFromChunkCoords = this.clientWorldController.getChunkFromChunkCoords(xPos, zPos);
            chunkFromChunkCoords.fillChunk(packetIn.getDataBytes(i), packetIn.getIntArray()[i], packetIn.getIntArray2()[i], true);
            this.clientWorldController.markBlockRangeForRenderUpdate(xPos << 4, 0, zPos << 4, (xPos << 4) + 15, 256, (zPos << 4) + 15);
            if (!(this.clientWorldController.provider instanceof WorldProviderSurface)) {
                chunkFromChunkCoords.resetRelightChecks();
            }
        }
    }

    @Override
    public void handleChangeGameState(S2BPacketChangeGameState packetIn) throws IOException {
        EntityClientPlayerMP thePlayer = this.gameController.thePlayer;
        int changeType = packetIn.getChangeType();
        float value = packetIn.getValue();
        int n = MathHelper.floor_float(value + 0.5F);
        if (changeType >= 0 && changeType < S2BPacketChangeGameState.MESSAGE_NAMES.length && S2BPacketChangeGameState.MESSAGE_NAMES[changeType] != null) {
            thePlayer.addChatComponentMessage(new ChatComponentTranslation(S2BPacketChangeGameState.MESSAGE_NAMES[changeType]));
        }

        if (changeType == 1) {
            this.clientWorldController.getWorldInfo().setRaining(true);
            this.clientWorldController.setRainStrength(0.0F);
        } else if (changeType == 2) {
            this.clientWorldController.getWorldInfo().setRaining(false);
            this.clientWorldController.setRainStrength(1.0F);
        } else if (changeType == 3) {
            this.gameController.playerController.setGameType(GameType.getByID(n));
        } else if (changeType == 4) {
            this.gameController.displayGuiScreen(new GuiWinGame());
        } else if (changeType == 5) {
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
        } else if (changeType == 6) {
            this.clientWorldController.playSound(thePlayer.posX, thePlayer.posY + thePlayer.getEyeHeight(), thePlayer.posZ, "random.successful_hit", 0.18F, 0.45F, false);
        } else if (changeType == 7) {
            this.clientWorldController.setRainStrength(value);
        } else if (changeType == 8) {
            this.clientWorldController.setThunderStrength(value);
        }
    }

    @Override
    public void handleMaps(S34PacketMaps packetIn) {
        MapData loadMapData = ItemMap.loadMapData(packetIn.getMapId(), this.gameController.theWorld);
        loadMapData.updateMPMapData(packetIn.getData());
        this.gameController.entityRenderer.getMapItemRenderer().updateTexture(loadMapData);
    }

    @Override
    public void handleEffect(S28PacketEffect packetIn) {
        if (packetIn.isSoundServerwide()) {
            this.gameController
                .theWorld
                .playBroadcastSound(packetIn.getSoundType(), packetIn.getPosX(), packetIn.getPosY(), packetIn.getPosZ(), packetIn.getSoundData());
        } else {
            this.gameController.theWorld.playAuxSFX(packetIn.getSoundType(), packetIn.getPosX(), packetIn.getPosY(), packetIn.getPosZ(), packetIn.getSoundData());
        }
    }

    @Override
    public void handleStatistics(S37PacketStatistics packetIn) {
        boolean flag = false;

        for (Entry entry : (Iterable<Entry>)packetIn.getStatMap().entrySet()) {
            StatBase key = (StatBase)entry.getKey();
            int value = (Integer)entry.getValue();
            if (key.isAchievement() && value > 0) {
                if (this.flag && this.gameController.thePlayer.getStatFileWriter().writeStat(key) == 0) {
                    Achievement achievement = (Achievement)key;
                    this.gameController.guiAchievement.displayAchievement(achievement);
                    if (key == AchievementList.openInventory) {
                        this.gameController.gameSettings.showInventoryAchievementHint = false;
                        this.gameController.gameSettings.saveOptions();
                    }
                }

                flag = true;
            }

            this.gameController.thePlayer.getStatFileWriter().handleEntityPlayer(this.gameController.thePlayer, key, value);
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
        Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityId());
        if (entityByID instanceof EntityLivingBase) {
            PotionEffect potionEffect = new PotionEffect(packetIn.getEffectId(), packetIn.getDuration(), packetIn.getAmplifier());
            potionEffect.setPotionDurationMax(packetIn.isPermanent());
            ((EntityLivingBase)entityByID).addPotionEffect(potionEffect);
        }
    }

    @Override
    public void handleRemoveEntityEffect(S1EPacketRemoveEntityEffect packetIn) {
        Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getId());
        if (entityByID instanceof EntityLivingBase) {
            ((EntityLivingBase)entityByID).removePotionEffectClient(packetIn.getEffectType());
        }
    }

    @Override
    public void handlePlayerListItem(S38PacketPlayerListItem packetIn) {
        GuiPlayerInfo guiPlayerInfo = (GuiPlayerInfo)this.playerInfoMap.get(packetIn.getText());
        if (guiPlayerInfo == null && packetIn.isFlag()) {
            guiPlayerInfo = new GuiPlayerInfo(packetIn.getText());
            this.playerInfoMap.put(packetIn.getText(), guiPlayerInfo);
            this.playerInfoList.add(guiPlayerInfo);
        }

        if (guiPlayerInfo != null && !packetIn.isFlag()) {
            this.playerInfoMap.remove(packetIn.getText());
            this.playerInfoList.remove(guiPlayerInfo);
        }

        if (guiPlayerInfo != null && packetIn.isFlag()) {
            guiPlayerInfo.responseTime = packetIn.getIntValue();
        }
    }

    @Override
    public void handleKeepAlive(S00PacketKeepAlive packetIn) {
        CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent20());
        this.addToSendQueue(new C00PacketKeepAlive(packetIn.getId()));
    }

    @Override
    public void onConnectionStateTransition(EnumConnectionState oldState, EnumConnectionState newState) {
        throw new IllegalStateException("Unexpected protocol change!");
    }

    @Override
    public void handlePlayerAbilities(S39PacketPlayerAbilities packetIn) {
        EntityClientPlayerMP thePlayer = this.gameController.thePlayer;
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
        String[] suggestions = packetIn.getSuggestions();
        if (this.gameController.currentScreen instanceof GuiChat) {
            GuiChat currentScreen = (GuiChat)this.gameController.currentScreen;
            currentScreen.onAutocompleteResponse(suggestions);
        }
    }

    @Override
    public void handleSoundEffect(S29PacketSoundEffect packetIn) {
        this.gameController
            .theWorld
            .playSound(packetIn.getX(), packetIn.getY(), packetIn.getZ(), packetIn.getSound(), packetIn.getVolume(), packetIn.getPitch(), false);
    }

    @Override
    public void handleCustomPayload(S3FPacketCustomPayload packetIn) throws IOException {
        CheatBreaker.getInstance().getEventBus().handleEvent(new StringByteArrayEvent(packetIn.getChannel(), packetIn.getPayload()));
        if ("MC|TrList".equals(packetIn.getChannel())) {
            ByteBuf wrappedBuffer = Unpooled.wrappedBuffer(packetIn.getPayload());

            try {
                int readInt = wrappedBuffer.readInt();
                GuiScreen currentScreen = this.gameController.currentScreen;
                if (currentScreen != null && currentScreen instanceof GuiMerchant && readInt == this.gameController.thePlayer.openContainer.windowId) {
                    IMerchant merchant = ((GuiMerchant)currentScreen).getMerchant();
                    MerchantRecipeList fromPacket = MerchantRecipeList.fromPacket(new PacketBuffer(wrappedBuffer));
                    merchant.setRecipes(fromPacket);
                }
            } catch (IOException e) {
                logger.error("Couldn't load trade info", e);
            } finally {
                wrappedBuffer.release();
            }
        } else if ("MC|Brand".equals(packetIn.getChannel())) {
            this.gameController.thePlayer.setClientBrand(new String(packetIn.getPayload(), Charsets.UTF_8));
        } else if ("MC|RPack".equals(packetIn.getChannel())) {
            final String formatText2 = formatText(packetIn.getPayload());
            if (this.gameController.getCurrentServerData() != null && this.gameController.getCurrentServerData().getResourceMode() == ServerResourceMode.ENABLED) {
                this.gameController.getResourcePackRepository().obtainResourcePack(formatText2);
            } else if (this.gameController.getCurrentServerData() == null
                || this.gameController.getCurrentServerData().getResourceMode() == ServerResourceMode.PROMPT) {
                this.gameController.displayGuiScreen(new GuiYesNo(new GuiYesNoCallback() {
                    @Override
                    public void confirmClicked(boolean result, int id) throws IOException {
                        NetHandlerPlayClient.this.gameController = Minecraft.getMinecraft();
                        if (NetHandlerPlayClient.this.gameController.getCurrentServerData() != null) {
                            NetHandlerPlayClient.this.gameController.getCurrentServerData().setResourceMode(ServerResourceMode.ENABLED);
                            ServerList.updateServerListEntry(NetHandlerPlayClient.this.gameController.getCurrentServerData());
                        }

                        if (result) {
                            NetHandlerPlayClient.this.gameController.getResourcePackRepository().obtainResourcePack(formatText2);
                        }

                        NetHandlerPlayClient.this.gameController.displayGuiScreen(null);
                    }
                }, I18n.format("multiplayer.texturePrompt.line1"), I18n.format("multiplayer.texturePrompt.line2"), 0));
            }
        }
    }

    @Override
    public void handleScoreboardObjective(S3BPacketScoreboardObjective packetIn) {
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();
        if (packetIn.getMode() == 0) {
            ScoreObjective addScoreObjective = scoreboard.addScoreObjective(packetIn.getName(), IScoreObjectiveCriteria.DUMMY);
            addScoreObjective.setDisplayName(packetIn.getDisplayName());
        } else {
            ScoreObjective objective = scoreboard.getObjective(packetIn.getName());
            if (packetIn.getMode() == 1) {
                scoreboard.removeObjective(objective);
            } else if (packetIn.getMode() == 2) {
                objective.setDisplayName(packetIn.getDisplayName());
            }
        }
    }

    @Override
    public void handleUpdateScore(S3CPacketUpdateScore packetIn) {
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();
        ScoreObjective objective = scoreboard.getObjective(packetIn.getObjectiveName());
        if (packetIn.getIntValue() == 0) {
            Score valueFromObjective = scoreboard.getValueFromObjective(packetIn.getPlayerName(), objective);
            valueFromObjective.setScorePoints(packetIn.getScore());
        } else if (packetIn.getIntValue() == 1) {
            scoreboard.handleString2(packetIn.getPlayerName());
        }
    }

    @Override
    public void handleDisplayScoreboard(S3DPacketDisplayScoreboard packetIn) {
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();
        if (packetIn.getName().length() == 0) {
            scoreboard.setObjectiveInDisplaySlot(packetIn.getSlot(), null);
        } else {
            ScoreObjective objective = scoreboard.getObjective(packetIn.getName());
            scoreboard.setObjectiveInDisplaySlot(packetIn.getSlot(), objective);
        }
    }

    @Override
    public void handleTeams(S3EPacketTeams packetIn) {
        Scoreboard scoreboard = this.clientWorldController.getScoreboard();
        ScorePlayerTeam scorePlayerTeam;
        if (packetIn.getMode() == 0) {
            scorePlayerTeam = scoreboard.createTeam(packetIn.getTeamName());
        } else {
            scorePlayerTeam = scoreboard.getTeam(packetIn.getTeamName());
        }

        if (packetIn.getMode() == 0 || packetIn.getMode() == 2) {
            scorePlayerTeam.setTeamName(packetIn.getDisplayName());
            scorePlayerTeam.setNamePrefix(packetIn.getPlayerPrefix());
            scorePlayerTeam.setNameSuffix(packetIn.getNameTagVisibilityRule());
            scorePlayerTeam.setFriendlyFlagsBitwise(packetIn.getFlags());
        }

        if (packetIn.getMode() == 0 || packetIn.getMode() == 3) {
            for (String str : (Iterable<String>)packetIn.getPlayerList()) {
                scoreboard.addPlayerToTeam(str, packetIn.getTeamName());
            }
        }

        if (packetIn.getMode() == 4) {
            for (String str : (Iterable<String>)packetIn.getPlayerList()) {
                scoreboard.removePlayerFromTeam(str, scorePlayerTeam);
            }
        }

        if (packetIn.getMode() == 1) {
            scoreboard.removeTeam(scorePlayerTeam);
        }
    }

    @Override
    public void handleParticles(S2APacketParticles packetIn) {
        if (packetIn.getCount() == 0) {
            double d = packetIn.getSpeed() * packetIn.getOffsetX();
            double d2 = packetIn.getSpeed() * packetIn.getOffsetY();
            double d3 = packetIn.getSpeed() * packetIn.getOffsetZ();
            this.clientWorldController.spawnParticle(packetIn.getText(), packetIn.getX(), packetIn.getY(), packetIn.getZ(), d, d2, d3);
        } else {
            for (int i = 0; i < packetIn.getCount(); i++) {
                double d = this.avRandomizer.nextGaussian() * packetIn.getOffsetX();
                double d2 = this.avRandomizer.nextGaussian() * packetIn.getOffsetY();
                double d3 = this.avRandomizer.nextGaussian() * packetIn.getOffsetZ();
                double d4 = this.avRandomizer.nextGaussian() * packetIn.getSpeed();
                double d5 = this.avRandomizer.nextGaussian() * packetIn.getSpeed();
                double d6 = this.avRandomizer.nextGaussian() * packetIn.getSpeed();
                this.clientWorldController
                    .spawnParticle(packetIn.getText(), packetIn.getX() + d, packetIn.getY() + d2, packetIn.getZ() + d3, d4, d5, d6);
            }
        }
    }

    @Override
    public void handleEntityProperties(S20PacketEntityProperties packetIn) {
        Entity entityByID = this.clientWorldController.getEntityByID(packetIn.getEntityId());
        if (entityByID != null) {
            if (!(entityByID instanceof EntityLivingBase)) {
                throw new IllegalStateException("Server tried to update attributes of a non-living entity (actually: " + entityByID + ")");
            }

            BaseAttributeMap attributeMap = ((EntityLivingBase)entityByID).getAttributeMap();

            for (Snapshot snapshot : (Iterable<Snapshot>)packetIn.getEntries()) {
                IAttributeInstance attributeInstanceByName = attributeMap.getAttributeInstanceByName(snapshot.getText());
                if (attributeInstanceByName == null) {
                    attributeInstanceByName = attributeMap.registerAttribute(new RangedAttribute(snapshot.getText(), 0.0, Double.MIN_NORMAL, Double.MAX_VALUE));
                }

                attributeInstanceByName.setBaseValue(snapshot.getDoubleValue());
                attributeInstanceByName.removeAllModifiers();

                for (AttributeModifier attributeModifier : (Iterable<AttributeModifier>)snapshot.getList()) {
                    attributeInstanceByName.applyModifier(attributeModifier);
                }
            }
        }
    }

    public NetworkManager getNetworkManager() {
        return this.netManager;
    }

    public static String formatText(byte[] bytes) {
        String str = new String(bytes, Charsets.UTF_8);
        if (!str.startsWith("http://") && !str.startsWith("https://")) {
            PacketBuffer packetBuffer = new PacketBuffer(Unpooled.wrappedBuffer(bytes));

            try {
                String readStringFromBuffer = packetBuffer.readStringFromBuffer(32767);
                if (!packetBuffer.isReadable() && (readStringFromBuffer.startsWith("http://") || readStringFromBuffer.startsWith("https://"))) {
                    return readStringFromBuffer;
                }
            } catch (IOException e) {
                throw SneakyThrow.sneakyThrow(e);
            } catch (RuntimeException e) {
            } finally {
                packetBuffer.release();
            }

            return str;
        } else {
            return str;
        }
    }
}
