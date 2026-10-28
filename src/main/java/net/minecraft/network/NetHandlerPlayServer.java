package net.minecraft.network;

import com.cheatbreaker.client.util.SneakyThrow;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import io.netty.buffer.Unpooled;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
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
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumChatVisibility;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerBeacon;
import net.minecraft.inventory.ContainerMerchant;
import net.minecraft.inventory.ContainerRepair;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemEditableBook;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
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
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.client.C02PacketUseEntity.Action;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.network.play.server.S00PacketKeepAlive;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.network.play.server.S2FPacketSetSlot;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.network.play.server.S3APacketTabComplete;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IntHashMap;
import net.minecraft.util.ReportedException;
import net.minecraft.world.WorldServer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetHandlerPlayServer implements INetHandlerPlayServer {
    public static Logger logger = LogManager.getLogger();
    public IntHashMap transactions = new IntHashMap();
    public boolean hasMoved = true;
    public MinecraftServer serverController;
    public int networkTickCount;
    public int floatingTickCount;
    public boolean flag;
    public int intValue;
    public long lastPingTime;
    public static Random random = new Random();
    public long lastSentPingPacket;
    public int chatSpamThresholdCount;
    public int itemDropThreshold;
    public NetworkManager netManager;
    public double lastPosX;
    public double lastPosY;
    public double lastPosZ;
    public EntityPlayerMP playerEntity;

    public NetHandlerPlayServer(MinecraftServer server, NetworkManager networkManagerIn, EntityPlayerMP player) {
        this.serverController = server;
        this.netManager = networkManagerIn;
        networkManagerIn.setNetHandler(this);
        this.playerEntity = player;
        player.playerNetServerHandler = this;
    }

    @Override
    public void onNetworkTick() {
        this.flag = false;
        this.networkTickCount++;
        this.serverController.theProfiler.startSection("keepAlive");
        if (this.networkTickCount - this.lastSentPingPacket > 40L) {
            this.lastSentPingPacket = this.networkTickCount;
            this.lastPingTime = this.currentTimeMillis();
            this.intValue = (int)this.lastPingTime;
            this.sendPacket(new S00PacketKeepAlive(this.intValue));
        }

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
        this.netManager.scheduleOutboundPacket(new S40PacketDisconnect(chatComponentText), new GenericFutureListener() {
            public void operationComplete(Future future) {
                NetHandlerPlayServer.this.netManager.closeChannel(chatComponentText);
            }
        });
        this.netManager.disableAutoRead();
    }

    @Override
    public void processInput(C0CPacketInput packetIn) {
        this.playerEntity.setEntityActionState(packetIn.getStrafeSpeed(), packetIn.getForwardSpeed(), packetIn.isJumping(), packetIn.isSneaking());
    }

    @Override
    public void processPlayer(C03PacketPlayer packetIn) throws IOException {
        WorldServer worldServerForDimension = this.serverController.worldServerForDimension(this.playerEntity.dimension);
        this.flag = true;
        if (!this.playerEntity.playerConqueredTheEnd) {
            if (!this.hasMoved) {
                double d = packetIn.getPositionY() - this.lastPosY;
                if (packetIn.getPositionX() == this.lastPosX && d * d < 0.01 && packetIn.getPositionZ() == this.lastPosZ) {
                    this.hasMoved = true;
                }
            }

            if (this.hasMoved) {
                if (this.playerEntity.ridingEntity != null) {
                    float rotationYaw = this.playerEntity.rotationYaw;
                    float rotationPitch = this.playerEntity.rotationPitch;
                    this.playerEntity.ridingEntity.updateRiderPosition();
                    double posX = this.playerEntity.posX;
                    double posY = this.playerEntity.posY;
                    double posZ = this.playerEntity.posZ;
                    if (packetIn.getRotating()) {
                        rotationYaw = packetIn.getYaw();
                        rotationPitch = packetIn.getPitch();
                    }

                    this.playerEntity.onGround = packetIn.isOnGround();
                    this.playerEntity.onUpdateEntity();
                    this.playerEntity.yOffset2 = 0.0F;
                    this.playerEntity.setPositionAndRotation(posX, posY, posZ, rotationYaw, rotationPitch);
                    if (this.playerEntity.ridingEntity != null) {
                        this.playerEntity.ridingEntity.updateRiderPosition();
                    }

                    this.serverController.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);
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

                double posY = this.playerEntity.posY;
                this.lastPosX = this.playerEntity.posX;
                this.lastPosY = this.playerEntity.posY;
                this.lastPosZ = this.playerEntity.posZ;
                double posX = this.playerEntity.posX;
                double posY2 = this.playerEntity.posY;
                double posZ = this.playerEntity.posZ;
                float rotationYaw = this.playerEntity.rotationYaw;
                float rotationPitch = this.playerEntity.rotationPitch;
                if (packetIn.isPositionChanged() && packetIn.getPositionY() == -999.0 && packetIn.getStance() == -999.0) {
                    packetIn.setPositionChanged(false);
                }

                if (packetIn.isPositionChanged()) {
                    posX = packetIn.getPositionX();
                    posY2 = packetIn.getPositionY();
                    posZ = packetIn.getPositionZ();
                    double d = packetIn.getStance() - packetIn.getPositionY();
                    if (!this.playerEntity.isPlayerSleeping() && (d > 1.65 || d < 0.1)) {
                        this.kickPlayerFromServer("Illegal stance");
                        logger.warn(this.playerEntity.getCommandSenderName() + " had an illegal stance: " + d);
                        return;
                    }

                    if (Math.abs(packetIn.getPositionX()) > 3.2E7 || Math.abs(packetIn.getPositionZ()) > 3.2E7) {
                        this.kickPlayerFromServer("Illegal position");
                        return;
                    }
                }

                if (packetIn.getRotating()) {
                    rotationYaw = packetIn.getYaw();
                    rotationPitch = packetIn.getPitch();
                }

                this.playerEntity.onUpdateEntity();
                this.playerEntity.yOffset2 = 0.0F;
                this.playerEntity.setPositionAndRotation(this.lastPosX, this.lastPosY, this.lastPosZ, rotationYaw, rotationPitch);
                if (!this.hasMoved) {
                    return;
                }

                double d = posX - this.playerEntity.posX;
                double d2 = posY2 - this.playerEntity.posY;
                double d3 = posZ - this.playerEntity.posZ;
                double min = Math.min(Math.abs(d), Math.abs(this.playerEntity.motionX));
                double min2 = Math.min(Math.abs(d2), Math.abs(this.playerEntity.motionY));
                double min3 = Math.min(Math.abs(d3), Math.abs(this.playerEntity.motionZ));
                double d4 = min * min + min2 * min2 + min3 * min3;
                if (d4 > 100.0
                    && (!this.serverController.isSinglePlayer() || !this.serverController.getServerOwner().equals(this.playerEntity.getCommandSenderName()))) {
                    logger.warn(
                        this.playerEntity.getCommandSenderName()
                            + " moved too quickly! "
                            + d
                            + ","
                            + d2
                            + ","
                            + d3
                            + " ("
                            + min
                            + ", "
                            + min2
                            + ", "
                            + min3
                            + ")"
                    );
                    this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
                    return;
                }

                float f = 0.0625F;
                boolean isEmpty = worldServerForDimension.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().contract(f, f, f)).isEmpty();
                if (this.playerEntity.onGround && !packetIn.isOnGround() && d2 > 0.0) {
                    this.playerEntity.jump();
                }

                this.playerEntity.moveEntity(d, d2, d3);
                this.playerEntity.onGround = packetIn.isOnGround();
                this.playerEntity.addMovementStat(d, d2, d3);
                double d5 = d2;
                d = posX - this.playerEntity.posX;
                d2 = posY2 - this.playerEntity.posY;
                if (d2 > -0.5 || d2 < 0.5) {
                    d2 = 0.0;
                }

                d3 = posZ - this.playerEntity.posZ;
                d4 = d * d + d2 * d2 + d3 * d3;
                boolean flag = false;
                if (d4 > 0.0625 && !this.playerEntity.isPlayerSleeping() && !this.playerEntity.theItemInWorldManager.isCreative()) {
                    flag = true;
                    logger.warn(this.playerEntity.getCommandSenderName() + " moved wrongly!");
                }

                this.playerEntity.setPositionAndRotation(posX, posY2, posZ, rotationYaw, rotationPitch);
                boolean isEmpty2 = worldServerForDimension.getCollidingBoundingBoxes(this.playerEntity, this.playerEntity.boundingBox.copy().contract(f, f, f)).isEmpty();
                if (isEmpty && (flag || !isEmpty2) && !this.playerEntity.isPlayerSleeping()) {
                    this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, rotationYaw, rotationPitch);
                    return;
                }

                AxisAlignedBB addCoord = this.playerEntity.boundingBox.copy().expand(f, f, f).addCoord(0.0, -0.55, 0.0);
                if (this.serverController.isFlightAllowed() || this.playerEntity.theItemInWorldManager.isCreative() || worldServerForDimension.checkBlockCollision(addCoord)) {
                    this.floatingTickCount = 0;
                } else if (d5 >= -0.03125) {
                    this.floatingTickCount++;
                    if (this.floatingTickCount > 80) {
                        logger.warn(this.playerEntity.getCommandSenderName() + " was kicked for floating too long!");
                        this.kickPlayerFromServer("Flying is not enabled on this server");
                        return;
                    }
                }

                this.playerEntity.onGround = packetIn.isOnGround();
                this.serverController.getConfigurationManager().serverUpdateMountedMovingPlayer(this.playerEntity);
                this.playerEntity.handleFalling(this.playerEntity.posY - posY, packetIn.isOnGround());
            } else if (this.networkTickCount % 20 == 0) {
                this.setPlayerLocation(this.lastPosX, this.lastPosY, this.lastPosZ, this.playerEntity.rotationYaw, this.playerEntity.rotationPitch);
            }
        }
    }

    public void setPlayerLocation(double x, double y, double z, float yaw, float pitch) {
        this.hasMoved = false;
        this.lastPosX = x;
        this.lastPosY = y;
        this.lastPosZ = z;
        this.playerEntity.setPositionAndRotation(x, y, z, yaw, pitch);
        this.playerEntity.playerNetServerHandler.sendPacket(new S08PacketPlayerPosLook(x, y + 1.62F, z, yaw, pitch, false));
    }

    @Override
    public void processPlayerDigging(C07PacketPlayerDigging packetIn) {
        WorldServer worldServerForDimension = this.serverController.worldServerForDimension(this.playerEntity.dimension);
        this.playerEntity.markPlayerActive();
        if (packetIn.getDiggedBlockStatus() == 4) {
            this.playerEntity.dropOneItem(false);
        } else if (packetIn.getDiggedBlockStatus() == 3) {
            this.playerEntity.dropOneItem(true);
        } else if (packetIn.getDiggedBlockStatus() == 5) {
            this.playerEntity.stopUsingItem();
        } else {
            boolean flag = packetIn.getDiggedBlockStatus() == 0;
            if (packetIn.getDiggedBlockStatus() == 1) {
                flag = true;
            }

            if (packetIn.getDiggedBlockStatus() == 2) {
                flag = true;
            }

            int diggedBlockX = packetIn.getDiggedBlockX();
            int diggedBlockY = packetIn.getDiggedBlockY();
            int diggedBlockZ = packetIn.getDiggedBlockZ();
            if (flag) {
                double d = this.playerEntity.posX - (diggedBlockX + 0.5);
                double d2 = this.playerEntity.posY - (diggedBlockY + 0.5) + 1.5;
                double d3 = this.playerEntity.posZ - (diggedBlockZ + 0.5);
                double d4 = d * d + d2 * d2 + d3 * d3;
                if (d4 > 36.0) {
                    return;
                }

                if (diggedBlockY >= this.serverController.getBuildLimit()) {
                    return;
                }
            }

            if (packetIn.getDiggedBlockStatus() == 0) {
                if (!this.serverController.isBlockProtected(worldServerForDimension, diggedBlockX, diggedBlockY, diggedBlockZ, this.playerEntity)) {
                    this.playerEntity.theItemInWorldManager.onBlockClicked(diggedBlockX, diggedBlockY, diggedBlockZ, packetIn.getDiggingBlockFace());
                } else {
                    this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(diggedBlockX, diggedBlockY, diggedBlockZ, worldServerForDimension));
                }
            } else if (packetIn.getDiggedBlockStatus() == 2) {
                this.playerEntity.theItemInWorldManager.blockRemoving(diggedBlockX, diggedBlockY, diggedBlockZ);
                if (worldServerForDimension.getBlock(diggedBlockX, diggedBlockY, diggedBlockZ).getMaterial() != Material.air) {
                    this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(diggedBlockX, diggedBlockY, diggedBlockZ, worldServerForDimension));
                }
            } else if (packetIn.getDiggedBlockStatus() == 1) {
                this.playerEntity.theItemInWorldManager.cancelDestroyingBlock(diggedBlockX, diggedBlockY, diggedBlockZ);
                if (worldServerForDimension.getBlock(diggedBlockX, diggedBlockY, diggedBlockZ).getMaterial() != Material.air) {
                    this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(diggedBlockX, diggedBlockY, diggedBlockZ, worldServerForDimension));
                }
            }
        }
    }

    @Override
    public void processPlayerBlockPlacement(C08PacketPlayerBlockPlacement packetIn) throws IOException {
        WorldServer worldServerForDimension = this.serverController.worldServerForDimension(this.playerEntity.dimension);
        ItemStack currentItem = this.playerEntity.inventory.getCurrentItem();
        boolean flag = false;
        int placedBlockX = packetIn.getPlacedBlockX();
        int placedBlockY = packetIn.getPlacedBlockY();
        int placedBlockZ = packetIn.getPlacedBlockZ();
        int placedBlockDirection = packetIn.getPlacedBlockDirection();
        this.playerEntity.markPlayerActive();
        if (packetIn.getPlacedBlockDirection() == 255) {
            if (currentItem == null) {
                return;
            }

            this.playerEntity.theItemInWorldManager.tryUseItem(this.playerEntity, worldServerForDimension, currentItem);
        } else if (packetIn.getPlacedBlockY() < this.serverController.getBuildLimit() - 1
            || packetIn.getPlacedBlockDirection() != 1 && packetIn.getPlacedBlockY() < this.serverController.getBuildLimit()) {
            if (this.hasMoved
                && this.playerEntity.getDistanceSq(placedBlockX + 0.5, placedBlockY + 0.5, placedBlockZ + 0.5) < 64.0
                && !this.serverController.isBlockProtected(worldServerForDimension, placedBlockX, placedBlockY, placedBlockZ, this.playerEntity)) {
                this.playerEntity
                    .theItemInWorldManager
                    .activateBlockOrUseItem(
                        this.playerEntity, worldServerForDimension, currentItem, placedBlockX, placedBlockY, placedBlockZ, placedBlockDirection, packetIn.getFacingX(), packetIn.getFacingY(), packetIn.getFacingZ()
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
            this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(placedBlockX, placedBlockY, placedBlockZ, worldServerForDimension));
            if (placedBlockDirection == 0) {
                placedBlockY--;
            }

            if (placedBlockDirection == 1) {
                placedBlockY++;
            }

            if (placedBlockDirection == 2) {
                placedBlockZ--;
            }

            if (placedBlockDirection == 3) {
                placedBlockZ++;
            }

            if (placedBlockDirection == 4) {
                placedBlockX--;
            }

            if (placedBlockDirection == 5) {
                placedBlockX++;
            }

            this.playerEntity.playerNetServerHandler.sendPacket(new S23PacketBlockChange(placedBlockX, placedBlockY, placedBlockZ, worldServerForDimension));
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
    public void onDisconnect(IChatComponent reason) {
        logger.info(this.playerEntity.getCommandSenderName() + " lost connection: " + reason);
        this.serverController.refreshStatusNextTick();
        ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("multiplayer.player.left", this.playerEntity.getFormattedCommandSenderName());
        chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.YELLOW);
        this.serverController.getConfigurationManager().sendChatMsg(chatComponentTranslation);
        this.playerEntity.mountEntityAndWakeUp();
        this.serverController.getConfigurationManager().playerLoggedOut(this.playerEntity);
        if (this.serverController.isSinglePlayer() && this.playerEntity.getCommandSenderName().equals(this.serverController.getServerOwner())) {
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
            this.netManager.scheduleOutboundPacket(packetIn);
        } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Sending packet");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Packet being sent");
            makeCategory.addCrashSectionCallable("Packet class", new Callable() {
                public String call() {
                    return packetIn.getClass().getCanonicalName();
                }
            });
            throw new ReportedException(makeCrashReport);
        }
    }

    @Override
    public void processHeldItemChange(C09PacketHeldItemChange packetIn) {
        if (packetIn.getSlotId() >= 0 && packetIn.getSlotId() < InventoryPlayer.getHotbarSize()) {
            this.playerEntity.inventory.currentItem = packetIn.getSlotId();
            this.playerEntity.markPlayerActive();
        } else {
            logger.warn(this.playerEntity.getCommandSenderName() + " tried to set an invalid carried item");
        }
    }

    @Override
    public void processChatMessage(C01PacketChatMessage packetIn) {
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
                ChatComponentTranslation chatComponentTranslation = new ChatComponentTranslation("chat.type.text", this.playerEntity.getFormattedCommandSenderName(), message);
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
    public void processAnimation(C0APacketAnimation packetIn) {
        this.playerEntity.markPlayerActive();
        if (packetIn.getType() == 1) {
            this.playerEntity.swingItem();
        }
    }

    @Override
    public void processEntityAction(C0BPacketEntityAction packetIn) throws IOException {
        this.playerEntity.markPlayerActive();
        if (packetIn.getIntValue() == 1) {
            this.playerEntity.setSneaking(true);
        } else if (packetIn.getIntValue() == 2) {
            this.playerEntity.setSneaking(false);
        } else if (packetIn.getIntValue() == 4) {
            this.playerEntity.setSprinting(true);
        } else if (packetIn.getIntValue() == 5) {
            this.playerEntity.setSprinting(false);
        } else if (packetIn.getIntValue() == 3) {
            this.playerEntity.wakeUpPlayer(false, true, true);
            this.hasMoved = false;
        } else if (packetIn.getIntValue() == 6) {
            if (this.playerEntity.ridingEntity != null && this.playerEntity.ridingEntity instanceof EntityHorse) {
                ((EntityHorse)this.playerEntity.ridingEntity).setJumpPower(packetIn.getMountJumpHeight());
            }
        } else if (packetIn.getIntValue() == 7 && this.playerEntity.ridingEntity != null && this.playerEntity.ridingEntity instanceof EntityHorse) {
            ((EntityHorse)this.playerEntity.ridingEntity).openGUI(this.playerEntity);
        }
    }

    @Override
    public void processUseEntity(C02PacketUseEntity packetIn) throws IOException {
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
                if (packetIn.getAction() == Action.INTERACT) {
                    this.playerEntity.interactWith(entityFromWorld);
                } else if (packetIn.getAction() == Action.ATTACK) {
                    if (entityFromWorld instanceof EntityItem || entityFromWorld instanceof EntityXPOrb || entityFromWorld instanceof EntityArrow || entityFromWorld == this.playerEntity) {
                        this.kickPlayerFromServer("Attempting to attack an invalid entity");
                        this.serverController.logWarning("Player " + this.playerEntity.getCommandSenderName() + " tried to attack an invalid entity");
                        return;
                    }

                    this.playerEntity.attackTargetEntityWithCurrentItem(entityFromWorld);
                }
            }
        }
    }

    @Override
    public void processClientStatus(C16PacketClientStatus packetIn) {
        this.playerEntity.markPlayerActive();
        EnumState status = packetIn.getStatus();
        switch (status) {
            case PERFORM_RESPAWN:
                if (this.playerEntity.playerConqueredTheEnd) {
                    this.playerEntity = this.serverController.getConfigurationManager().recreatePlayerEntity(this.playerEntity, 0, true);
                } else if (this.playerEntity.getServerForPlayer().getWorldInfo().isHardcoreModeEnabled()) {
                    if (this.serverController.isSinglePlayer() && this.playerEntity.getCommandSenderName().equals(this.serverController.getServerOwner())) {
                        this.playerEntity.playerNetServerHandler.kickPlayerFromServer("You have died. Game over, man, it's game over!");
                        this.serverController.deleteWorldAndStopServer();
                    } else {
                        UserListBansEntry userListBansEntry = new UserListBansEntry(
                            this.playerEntity.getGameProfile(), null, "(You just lost the game)", null, "Death in Hardcore"
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
        this.playerEntity.closeContainer();
    }

    @Override
    public void processClickWindow(C0EPacketClickWindow packetIn) {
        this.playerEntity.markPlayerActive();
        if (this.playerEntity.openContainer.windowId == packetIn.getWindowId() && this.playerEntity.openContainer.getCanCraft(this.playerEntity)) {
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
                ArrayList list = new ArrayList();

                for (int i = 0; i < this.playerEntity.openContainer.inventorySlots.size(); i++) {
                    list.add(((Slot)this.playerEntity.openContainer.inventorySlots.get(i)).getStack());
                }

                this.playerEntity.updateCraftingInventory(this.playerEntity.openContainer, list);
            }
        }
    }

    @Override
    public void processEnchantItem(C11PacketEnchantItem packetIn) {
        this.playerEntity.markPlayerActive();
        if (this.playerEntity.openContainer.windowId == packetIn.getId() && this.playerEntity.openContainer.getCanCraft(this.playerEntity)) {
            this.playerEntity.openContainer.enchantItem(this.playerEntity, packetIn.getButton());
            this.playerEntity.openContainer.detectAndSendChanges();
        }
    }

    @Override
    public void processCreativeInventoryAction(C10PacketCreativeInventoryAction packetIn) {
        if (this.playerEntity.theItemInWorldManager.isCreative()) {
            boolean flag = packetIn.getSlotId() < 0;
            ItemStack stack = packetIn.getStack();
            boolean flag2 = packetIn.getSlotId() >= 1 && packetIn.getSlotId() < 36 + InventoryPlayer.getHotbarSize();
            boolean flag3 = stack == null || stack.getItem() != null;
            boolean flag4 = stack == null || stack.getMetadata() >= 0 && stack.stackSize <= 64 && stack.stackSize > 0;
            if (flag2 && flag3 && flag4) {
                if (stack == null) {
                    this.playerEntity.inventoryContainer.putStackInSlot(packetIn.getSlotId(), null);
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
        Short lookup = (Short)this.transactions.lookup(this.playerEntity.openContainer.windowId);
        if (lookup != null
            && packetIn.getUid() == lookup
            && this.playerEntity.openContainer.windowId == packetIn.getId()
            && !this.playerEntity.openContainer.getCanCraft(this.playerEntity)) {
            this.playerEntity.openContainer.setCanCraft(this.playerEntity, true);
        }
    }

    @Override
    public void processUpdateSign(C12PacketUpdateSign packetIn) {
        this.playerEntity.markPlayerActive();
        WorldServer worldServerForDimension = this.serverController.worldServerForDimension(this.playerEntity.dimension);
        if (worldServerForDimension.blockExists(packetIn.getX(), packetIn.getY(), packetIn.getZ())) {
            TileEntity tileEntity = worldServerForDimension.getTileEntity(packetIn.getX(), packetIn.getY(), packetIn.getZ());
            if (tileEntity instanceof TileEntitySign) {
                TileEntitySign tileEntitySign = (TileEntitySign)tileEntity;
                if (!tileEntitySign.getIsEditable() || tileEntitySign.getEditor() != this.playerEntity) {
                    this.serverController.logWarning("Player " + this.playerEntity.getCommandSenderName() + " just tried to change non-editable sign");
                    return;
                }
            }

            for (int i = 0; i < 4; i++) {
                boolean flag = true;
                if (packetIn.getLines()[i].length() > 15) {
                    flag = false;
                } else {
                    for (int j = 0; j < packetIn.getLines()[i].length(); j++) {
                        if (!ChatAllowedCharacters.isAllowedCharacter(packetIn.getLines()[i].charAt(j))) {
                            flag = false;
                        }
                    }
                }

                if (!flag) {
                    packetIn.getLines()[i] = "!?";
                }
            }

            if (tileEntity instanceof TileEntitySign) {
                int n = packetIn.getX();
                int n2 = packetIn.getY();
                int n3 = packetIn.getZ();
                TileEntitySign tileEntitySign = (TileEntitySign)tileEntity;
                System.arraycopy(packetIn.getLines(), 0, tileEntitySign.signText, 0, 4);
                tileEntitySign.markDirty();
                worldServerForDimension.markBlockForUpdate(n, n2, n3);
            }
        }
    }

    @Override
    public void processKeepAlive(C00PacketKeepAlive packetIn) {
        if (packetIn.getKey() == this.intValue) {
            int n = (int)(this.currentTimeMillis() - this.lastPingTime);
            this.playerEntity.ping = (this.playerEntity.ping * 3 + n) / 4;
        }
    }

    public long currentTimeMillis() {
        return System.nanoTime() / 1000000L;
    }

    @Override
    public void processPlayerAbilities(C13PacketPlayerAbilities packetIn) {
        this.playerEntity.capabilities.isFlying = packetIn.isFlying() && this.playerEntity.capabilities.allowFlying;
    }

    @Override
    public void processTabComplete(C14PacketTabComplete packetIn) {
        ArrayList newArrayList = Lists.newArrayList();

        for (String str : (Iterable<String>)this.serverController.getPossibleCompletions(this.playerEntity, packetIn.getMessage())) {
            newArrayList.add(str);
        }

        this.playerEntity.playerNetServerHandler.sendPacket(new S3APacketTabComplete((String[])newArrayList.toArray(new String[newArrayList.size()])));
    }

    @Override
    public void processClientSettings(C15PacketClientSettings packetIn) {
        this.playerEntity.handleC15PacketClientSettings(packetIn);
    }

    @Override
    public void processVanilla250Packet(C17PacketCustomPayload packetIn) {
        if ("MC|BEdit".equals(packetIn.getChannel())) {
            PacketBuffer packetBuffer = new PacketBuffer(Unpooled.wrappedBuffer(packetIn.getData()));

            try {
                ItemStack readItemStackFromBuffer = packetBuffer.readItemStackFromBuffer();
                if (readItemStackFromBuffer == null) {
                    return;
                }

                if (!ItemWritableBook.validBookPageTagContents(readItemStackFromBuffer.getTagCompound())) {
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
        } else if ("MC|BSign".equals(packetIn.getChannel())) {
            PacketBuffer packetBuffer = new PacketBuffer(Unpooled.wrappedBuffer(packetIn.getData()));

            label419: {
                label418: {
                    label417: {
                        try {
                            try {
                                ItemStack readItemStackFromBuffer = packetBuffer.readItemStackFromBuffer();
                                if (readItemStackFromBuffer == null) {
                                    break label419;
                                }

                                if (!ItemEditableBook.validBookTagContents(readItemStackFromBuffer.getTagCompound())) {
                                    throw new IOException("Invalid book tag!");
                                }

                                ItemStack currentItem = this.playerEntity.inventory.getCurrentItem();
                                if (currentItem != null) {
                                    if (readItemStackFromBuffer.getItem() == Items.written_book && currentItem.getItem() == Items.writable_book) {
                                        currentItem.setTagInfo("author", new NBTTagString(this.playerEntity.getCommandSenderName()));
                                        currentItem.setTagInfo("title", new NBTTagString(readItemStackFromBuffer.getTagCompound().getString("title")));
                                        currentItem.setTagInfo("pages", readItemStackFromBuffer.getTagCompound().getTagList("pages", 8));
                                        currentItem.setItem(Items.written_book);
                                    }
                                    break label418;
                                }
                                break label417;
                            } catch (Exception e) {
                                logger.error("Couldn't sign book", e);
                            }
                        } catch (Throwable e) {
                            packetBuffer.release();
                            throw SneakyThrow.sneakyThrow(e);
                        }

                        packetBuffer.release();
                        return;
                    }

                    packetBuffer.release();
                    return;
                }

                packetBuffer.release();
                return;
            }

            packetBuffer.release();
        } else {
            if ("MC|TrSel".equals(packetIn.getChannel())) {
                try {
                    DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(packetIn.getData()));
                    int readInt = dataInputStream.readInt();
                    Container openContainer = this.playerEntity.openContainer;
                    if (openContainer instanceof ContainerMerchant) {
                        ((ContainerMerchant)openContainer).setCurrentRecipeIndex(readInt);
                    }
                } catch (Exception e) {
                    logger.error("Couldn't select trade", e);
                }
            } else if ("MC|AdvCdm".equals(packetIn.getChannel())) {
                if (!this.serverController.isCommandBlockEnabled()) {
                    this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notEnabled"));
                } else if (this.playerEntity.canCommandSenderUseCommand(2, "") && this.playerEntity.capabilities.isCreativeMode) {
                    PacketBuffer packetBuffer = new PacketBuffer(Unpooled.wrappedBuffer(packetIn.getData()));

                    try {
                        byte readByte = packetBuffer.readByte();
                        CommandBlockLogic commandBlockLogic = null;
                        if (readByte == 0) {
                            TileEntity tileEntity = this.playerEntity.worldObj.getTileEntity(packetBuffer.readInt(), packetBuffer.readInt(), packetBuffer.readInt());
                            if (tileEntity instanceof TileEntityCommandBlock) {
                                commandBlockLogic = ((TileEntityCommandBlock)tileEntity).getCommandExecutor();
                            }
                        } else if (readByte == 1) {
                            Entity entityByID = this.playerEntity.worldObj.getEntityByID(packetBuffer.readInt());
                            if (entityByID instanceof EntityMinecartCommandBlock) {
                                commandBlockLogic = ((EntityMinecartCommandBlock)entityByID).getCommandExecutor();
                            }
                        }

                        String readStringFromBuffer = packetBuffer.readStringFromBuffer(packetBuffer.readableBytes());
                        if (commandBlockLogic != null) {
                            commandBlockLogic.setCommand(readStringFromBuffer);
                            commandBlockLogic.run();
                            this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.setCommand.success", readStringFromBuffer));
                        }
                    } catch (Exception e) {
                        logger.error("Couldn't set command block", e);
                    } finally {
                        packetBuffer.release();
                    }
                } else {
                    this.playerEntity.addChatMessage(new ChatComponentTranslation("advMode.notAllowed"));
                }
            } else if ("MC|Beacon".equals(packetIn.getChannel())) {
                if (this.playerEntity.openContainer instanceof ContainerBeacon) {
                    try {
                        DataInputStream dataInputStream = new DataInputStream(new ByteArrayInputStream(packetIn.getData()));
                        int readInt = dataInputStream.readInt();
                        int readInt2 = dataInputStream.readInt();
                        ContainerBeacon openContainer = (ContainerBeacon)this.playerEntity.openContainer;
                        Slot slot = openContainer.getSlot(0);
                        if (slot.getHasStack()) {
                            slot.decrStackSize(1);
                            TileEntityBeacon tileBeacon = openContainer.getTileBeacon();
                            tileBeacon.setPrimaryEffect(readInt);
                            tileBeacon.setSecondaryEffect(readInt2);
                            tileBeacon.markDirty();
                        }
                    } catch (Exception e) {
                        logger.error("Couldn't set beacon", e);
                    }
                }
            } else if ("MC|ItemName".equals(packetIn.getChannel()) && this.playerEntity.openContainer instanceof ContainerRepair) {
                ContainerRepair openContainer = (ContainerRepair)this.playerEntity.openContainer;
                if (packetIn.getData() != null && packetIn.getData().length >= 1) {
                    String filterAllowedCharacters = ChatAllowedCharacters.filterAllowedCharacters(new String(packetIn.getData(), Charsets.UTF_8));
                    if (filterAllowedCharacters.length() <= 30) {
                        openContainer.updateItemName(filterAllowedCharacters);
                    }
                } else {
                    openContainer.updateItemName("");
                }
            }
        }
    }

    @Override
    public void onConnectionStateTransition(EnumConnectionState oldState, EnumConnectionState newState) {
        if (newState != EnumConnectionState.PLAY) {
            throw new IllegalStateException("Unexpected change in protocol!");
        }
    }
}
