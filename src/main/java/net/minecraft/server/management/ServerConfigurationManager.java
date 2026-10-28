package net.minecraft.server.management;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.network.play.server.S07PacketRespawn;
import net.minecraft.network.play.server.S09PacketHeldItemChange;
import net.minecraft.network.play.server.S1DPacketEntityEffect;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.network.play.server.S38PacketPlayerListItem;
import net.minecraft.network.play.server.S39PacketPlayerAbilities;
import net.minecraft.network.play.server.S3EPacketTeams;
import net.minecraft.network.play.server.S3FPacketCustomPayload;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraft.world.storage.IPlayerFileData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ServerConfigurationManager {
    public static File FILE_PLAYERBANS = new File("banned-players.json");
    public static File FILE_IPBANS = new File("banned-ips.json");
    public static File FILE_OPS = new File("ops.json");
    public static File FILE_WHITELIST = new File("whitelist.json");
    public static Logger logger = LogManager.getLogger();
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
    public List playerEntityList = new ArrayList();
    public UserListBans bannedPlayers = new UserListBans(FILE_PLAYERBANS);
    public BanList bannedIPs = new BanList(FILE_IPBANS);
    public UserListOps ops = new UserListOps(FILE_OPS);
    public UserListWhitelist whiteListedPlayers = new UserListWhitelist(FILE_WHITELIST);
    public Map playerStatFiles = Maps.newHashMap();
    public MinecraftServer mcServer;
    public IPlayerFileData playerNBTManagerObj;
    public boolean whiteListEnforced;
    public int maxPlayers;
    public int viewDistance;
    public GameType gameType;
    public boolean commandsAllowedForAll;
    public int playerPingIndex;

    public ServerConfigurationManager(MinecraftServer server) {
        this.mcServer = server;
        this.bannedPlayers.setLanServer(false);
        this.bannedIPs.setLanServer(false);
        this.maxPlayers = 8;
    }

    public void initializeConnectionToPlayer(NetworkManager netManager, EntityPlayerMP player) {
        GameProfile gameProfile = player.getGameProfile();
        PlayerProfileCache playerProfileCache = this.mcServer.getPlayerProfileCache();
        GameProfile byUuid = playerProfileCache.getByUuid(gameProfile.getId());
        String str = byUuid == null ? gameProfile.getName() : byUuid.getName();
        playerProfileCache.add(gameProfile);
        NBTTagCompound readPlayerDataFromFile = this.readPlayerDataFromFile(player);
        player.setWorld(this.mcServer.worldServerForDimension(player.dimension));
        player.theItemInWorldManager.setWorld((WorldServer)player.worldObj);
        String str2 = "local";
        if (netManager.getRemoteAddress() != null) {
            str2 = netManager.getRemoteAddress().toString();
        }

        logger.info(
            player.getCommandSenderName()
                + "["
                + str2
                + "] logged in with entity id "
                + player.getEntityId()
                + " at ("
                + player.posX
                + ", "
                + player.posY
                + ", "
                + player.posZ
                + ")"
        );
        WorldServer worldServerForDimension = this.mcServer.worldServerForDimension(player.dimension);
        ChunkCoordinates spawnPoint = worldServerForDimension.getSpawnPoint();
        this.setGameMode(player, null, worldServerForDimension);
        NetHandlerPlayServer netHandlerPlayServer = new NetHandlerPlayServer(this.mcServer, netManager, player);
        netHandlerPlayServer.sendPacket(
            new S01PacketJoinGame(
                player.getEntityId(),
                player.theItemInWorldManager.getGameType(),
                worldServerForDimension.getWorldInfo().isHardcoreModeEnabled(),
                worldServerForDimension.provider.dimensionId,
                worldServerForDimension.difficultySetting,
                this.getMaxPlayers(),
                worldServerForDimension.getWorldInfo().getTerrainType()
            )
        );
        netHandlerPlayServer.sendPacket(new S3FPacketCustomPayload("MC|Brand", this.getServerInstance().getServerModName().getBytes(Charsets.UTF_8)));
        netHandlerPlayServer.sendPacket(new S05PacketSpawnPosition(spawnPoint.posX, spawnPoint.posY, spawnPoint.posZ));
        netHandlerPlayServer.sendPacket(new S39PacketPlayerAbilities(player.capabilities));
        netHandlerPlayServer.sendPacket(new S09PacketHeldItemChange(player.inventory.currentItem));
        player.getStatFile().updateStatSet();
        player.getStatFile().handleEntityPlayerMP2(player);
        this.sendScoreboard((ServerScoreboard)worldServerForDimension.getScoreboard(), player);
        this.mcServer.refreshStatusNextTick();
        ChatComponentTranslation chatComponentTranslation;
        if (!player.getCommandSenderName().equalsIgnoreCase(str)) {
            chatComponentTranslation = new ChatComponentTranslation("multiplayer.player.joined.renamed", player.getFormattedCommandSenderName(), str);
        } else {
            chatComponentTranslation = new ChatComponentTranslation("multiplayer.player.joined", player.getFormattedCommandSenderName());
        }

        chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.YELLOW);
        this.sendChatMsg(chatComponentTranslation);
        this.playerLoggedIn(player);
        netHandlerPlayServer.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        this.updateTimeAndWeatherForPlayer(player, worldServerForDimension);
        if (this.mcServer.getTexturePack().length() > 0) {
            player.requestTexturePackLoad(this.mcServer.getTexturePack());
        }

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            netHandlerPlayServer.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), potionEffect));
        }

        player.addSelfToInternalCraftingInventory();
        if (readPlayerDataFromFile != null && readPlayerDataFromFile.hasKey("Riding", 10)) {
            Entity createEntityFromNBT = EntityList.createEntityFromNBT(readPlayerDataFromFile.getCompoundTag("Riding"), worldServerForDimension);
            if (createEntityFromNBT != null) {
                createEntityFromNBT.forceSpawn = true;
                worldServerForDimension.spawnEntityInWorld(createEntityFromNBT);
                player.mountEntity(createEntityFromNBT);
                createEntityFromNBT.forceSpawn = false;
            }
        }
    }

    public void sendScoreboard(ServerScoreboard scoreboardIn, EntityPlayerMP player) {
        HashSet set = new HashSet();

        for (ScorePlayerTeam scorePlayerTeam : (Iterable<ScorePlayerTeam>)scoreboardIn.getTeams()) {
            player.playerNetServerHandler.sendPacket(new S3EPacketTeams(scorePlayerTeam, 0));
        }

        for (int i = 0; i < 3; i++) {
            ScoreObjective objectiveInDisplaySlot = scoreboardIn.getObjectiveInDisplaySlot(i);
            if (objectiveInDisplaySlot != null && !set.contains(objectiveInDisplaySlot)) {
                for (Packet packet : (Iterable<Packet>)scoreboardIn.createChangePackets(objectiveInDisplaySlot)) {
                    player.playerNetServerHandler.sendPacket(packet);
                }

                set.add(objectiveInDisplaySlot);
            }
        }
    }

    public void setPlayerManager(WorldServer[] worldServers) {
        this.playerNBTManagerObj = worldServers[0].getSaveHandler().getPlayerNBTManager();
    }

    public void handleEntityPlayerMP(EntityPlayerMP player, WorldServer worldIn) {
        WorldServer serverForPlayer = player.getServerForPlayer();
        if (worldIn != null) {
            worldIn.getPlayerManager().removePlayer(player);
        }

        serverForPlayer.getPlayerManager().addPlayer(player);
        serverForPlayer.theChunkProviderServer.loadChunk((int)player.posX >> 4, (int)player.posZ >> 4);
    }

    public int getEntityViewDistance() {
        return PlayerManager.getFurthestViewableBlock(this.getViewDistance());
    }

    public NBTTagCompound readPlayerDataFromFile(EntityPlayerMP player) {
        NBTTagCompound playerNBTTagCompound = this.mcServer.worldServers[0].getWorldInfo().getPlayerNBTTagCompound();
        NBTTagCompound nbttagCompound;
        if (player.getCommandSenderName().equals(this.mcServer.getServerOwner()) && playerNBTTagCompound != null) {
            player.readFromNBT(playerNBTTagCompound);
            nbttagCompound = playerNBTTagCompound;
            logger.debug("loading single player");
        } else {
            nbttagCompound = this.playerNBTManagerObj.readPlayerData(player);
        }

        return nbttagCompound;
    }

    public void writePlayerData(EntityPlayerMP player) {
        this.playerNBTManagerObj.writePlayerData(player);
        StatisticsFile statisticsFile = (StatisticsFile)this.playerStatFiles.get(player.getUniqueID());
        if (statisticsFile != null) {
            statisticsFile.save();
        }
    }

    public void playerLoggedIn(EntityPlayerMP player) {
        this.sendPacketToAllPlayers(new S38PacketPlayerListItem(player.getCommandSenderName(), true, 1000));
        this.playerEntityList.add(player);
        WorldServer worldServerForDimension = this.mcServer.worldServerForDimension(player.dimension);
        worldServerForDimension.spawnEntityInWorld(player);
        this.handleEntityPlayerMP(player, null);

        for (int i = 0; i < this.playerEntityList.size(); i++) {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP)this.playerEntityList.get(i);
            player.playerNetServerHandler.sendPacket(new S38PacketPlayerListItem(entityPlayerMP.getCommandSenderName(), true, entityPlayerMP.ping));
        }
    }

    public void serverUpdateMountedMovingPlayer(EntityPlayerMP player) {
        player.getServerForPlayer().getPlayerManager().updateMountedMovingPlayer(player);
    }

    public void playerLoggedOut(EntityPlayerMP player) {
        player.triggerAchievement(StatList.leaveGameStat);
        this.writePlayerData(player);
        WorldServer serverForPlayer = player.getServerForPlayer();
        if (player.ridingEntity != null) {
            serverForPlayer.removePlayerEntityDangerously(player.ridingEntity);
            logger.debug("removing player mount");
        }

        serverForPlayer.removeEntity(player);
        serverForPlayer.getPlayerManager().removePlayer(player);
        this.playerEntityList.remove(player);
        this.playerStatFiles.remove(player.getUniqueID());
        this.sendPacketToAllPlayers(new S38PacketPlayerListItem(player.getCommandSenderName(), false, 9999));
    }

    public String allowUserToConnect(SocketAddress address, GameProfile profile) {
        if (this.bannedPlayers.isBanned(profile)) {
            UserListBansEntry entry = (UserListBansEntry)this.bannedPlayers.getEntry(profile);
            String str = "You are banned from this server!\nReason: " + entry.getBanReason();
            if (entry.getBanEndDate() != null) {
                str = str + "\nYour ban will be removed on " + dateFormat.format(entry.getBanEndDate());
            }

            return str;
        } else {
            if (!this.canJoin(profile)) {
                return "You are not white-listed on this server!";
            }

            if (this.bannedIPs.isBanned(address)) {
                IPBanEntry banEntry = this.bannedIPs.getBanEntry(address);
                String str = "Your IP address is banned from this server!\nReason: " + banEntry.getBanReason();
                if (banEntry.getBanEndDate() != null) {
                    str = str + "\nYour ban will be removed on " + dateFormat.format(banEntry.getBanEndDate());
                }

                return str;
            } else {
                return this.playerEntityList.size() >= this.maxPlayers ? "The server is full!" : null;
            }
        }
    }

    public EntityPlayerMP createPlayerForUser(GameProfile profile) {
        UUID uUID = EntityPlayer.getUUID(profile);
        ArrayList newArrayList = Lists.newArrayList();

        for (int i = 0; i < this.playerEntityList.size(); i++) {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP)this.playerEntityList.get(i);
            if (entityPlayerMP.getUniqueID().equals(uUID)) {
                newArrayList.add(entityPlayerMP);
            }
        }

        for (EntityPlayerMP entityPlayerMP : (Iterable<EntityPlayerMP>)newArrayList) {
            entityPlayerMP.playerNetServerHandler.kickPlayerFromServer("You logged in from another location");
        }

        ItemInWorldManager itemInWorldManager;
        if (this.mcServer.isDemo()) {
            itemInWorldManager = new DemoWorldManager(this.mcServer.worldServerForDimension(0));
        } else {
            itemInWorldManager = new ItemInWorldManager(this.mcServer.worldServerForDimension(0));
        }

        return new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(0), profile, itemInWorldManager);
    }

    public EntityPlayerMP recreatePlayerEntity(EntityPlayerMP player, int dimension, boolean conqueredEnd) {
        player.getServerForPlayer().getEntityTracker().removePlayerFromTrackers(player);
        player.getServerForPlayer().getEntityTracker().untrackEntity(player);
        player.getServerForPlayer().getPlayerManager().removePlayer(player);
        this.playerEntityList.remove(player);
        this.mcServer.worldServerForDimension(player.dimension).removePlayerEntityDangerously(player);
        ChunkCoordinates bedLocation = player.getBedLocation();
        boolean isSpawnForced = player.isSpawnForced();
        player.dimension = dimension;
        ItemInWorldManager itemInWorldManager;
        if (this.mcServer.isDemo()) {
            itemInWorldManager = new DemoWorldManager(this.mcServer.worldServerForDimension(player.dimension));
        } else {
            itemInWorldManager = new ItemInWorldManager(this.mcServer.worldServerForDimension(player.dimension));
        }

        EntityPlayerMP entityPlayerMP = new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(player.dimension), player.getGameProfile(), itemInWorldManager);
        entityPlayerMP.playerNetServerHandler = player.playerNetServerHandler;
        entityPlayerMP.clonePlayer(player, conqueredEnd);
        entityPlayerMP.setEntityId(player.getEntityId());
        WorldServer worldServerForDimension = this.mcServer.worldServerForDimension(player.dimension);
        this.setGameMode(entityPlayerMP, player, worldServerForDimension);
        if (bedLocation != null) {
            ChunkCoordinates verifyRespawnCoordinates = EntityPlayer.verifyRespawnCoordinates(this.mcServer.worldServerForDimension(player.dimension), bedLocation, isSpawnForced);
            if (verifyRespawnCoordinates != null) {
                entityPlayerMP.setLocationAndAngles(verifyRespawnCoordinates.posX + 0.5F, verifyRespawnCoordinates.posY + 0.1F, verifyRespawnCoordinates.posZ + 0.5F, 0.0F, 0.0F);
                entityPlayerMP.setSpawnChunk(bedLocation, isSpawnForced);
            } else {
                entityPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(0, 0.0F));
            }
        }

        worldServerForDimension.theChunkProviderServer.loadChunk((int)entityPlayerMP.posX >> 4, (int)entityPlayerMP.posZ >> 4);

        while (!worldServerForDimension.getCollidingBoundingBoxes(entityPlayerMP, entityPlayerMP.boundingBox).isEmpty()) {
            entityPlayerMP.setPosition(entityPlayerMP.posX, entityPlayerMP.posY + 1.0, entityPlayerMP.posZ);
        }

        entityPlayerMP.playerNetServerHandler
            .sendPacket(
                new S07PacketRespawn(
                    entityPlayerMP.dimension, entityPlayerMP.worldObj.difficultySetting, entityPlayerMP.worldObj.getWorldInfo().getTerrainType(), entityPlayerMP.theItemInWorldManager.getGameType()
                )
            );
        ChunkCoordinates spawnPoint = worldServerForDimension.getSpawnPoint();
        entityPlayerMP.playerNetServerHandler.setPlayerLocation(entityPlayerMP.posX, entityPlayerMP.posY, entityPlayerMP.posZ, entityPlayerMP.rotationYaw, entityPlayerMP.rotationPitch);
        entityPlayerMP.playerNetServerHandler.sendPacket(new S05PacketSpawnPosition(spawnPoint.posX, spawnPoint.posY, spawnPoint.posZ));
        entityPlayerMP.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(entityPlayerMP.experience, entityPlayerMP.experienceTotal, entityPlayerMP.experienceLevel));
        this.updateTimeAndWeatherForPlayer(entityPlayerMP, worldServerForDimension);
        worldServerForDimension.getPlayerManager().addPlayer(entityPlayerMP);
        worldServerForDimension.spawnEntityInWorld(entityPlayerMP);
        this.playerEntityList.add(entityPlayerMP);
        entityPlayerMP.addSelfToInternalCraftingInventory();
        entityPlayerMP.setHealth(entityPlayerMP.getHealth());
        return entityPlayerMP;
    }

    public void transferPlayerToDimension(EntityPlayerMP player, int dimension) throws java.io.IOException {
        int dimension2 = player.dimension;
        WorldServer worldServerForDimension = this.mcServer.worldServerForDimension(player.dimension);
        player.dimension = dimension;
        WorldServer worldServerForDimension2 = this.mcServer.worldServerForDimension(player.dimension);
        player.playerNetServerHandler
            .sendPacket(
                new S07PacketRespawn(
                    player.dimension, player.worldObj.difficultySetting, player.worldObj.getWorldInfo().getTerrainType(), player.theItemInWorldManager.getGameType()
                )
            );
        worldServerForDimension.removePlayerEntityDangerously(player);
        player.isDead = false;
        this.transferEntityToWorld(player, dimension2, worldServerForDimension, worldServerForDimension2);
        this.handleEntityPlayerMP(player, worldServerForDimension);
        player.playerNetServerHandler.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
        player.theItemInWorldManager.setWorld(worldServerForDimension2);
        this.updateTimeAndWeatherForPlayer(player, worldServerForDimension2);
        this.syncPlayerInventory(player);

        for (PotionEffect potionEffect : player.getActivePotionEffects()) {
            player.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(player.getEntityId(), potionEffect));
        }
    }

    public void transferEntityToWorld(Entity entityIn, int n, WorldServer worldServer, WorldServer worldServer2) throws java.io.IOException {
        double posX = entityIn.posX;
        double posZ = entityIn.posZ;
        double d = 8.0;
        double posX2 = entityIn.posX;
        double posY = entityIn.posY;
        double posZ2 = entityIn.posZ;
        float rotationYaw = entityIn.rotationYaw;
        worldServer.theProfiler.startSection("moving");
        if (entityIn.dimension == -1) {
            posX /= d;
            posZ /= d;
            entityIn.setLocationAndAngles(posX, entityIn.posY, posZ, entityIn.rotationYaw, entityIn.rotationPitch);
            if (entityIn.isEntityAlive()) {
                worldServer.updateEntityWithOptionalForce(entityIn, false);
            }
        } else if (entityIn.dimension == 0) {
            posX *= d;
            posZ *= d;
            entityIn.setLocationAndAngles(posX, entityIn.posY, posZ, entityIn.rotationYaw, entityIn.rotationPitch);
            if (entityIn.isEntityAlive()) {
                worldServer.updateEntityWithOptionalForce(entityIn, false);
            }
        } else {
            ChunkCoordinates chunkCoordinates;
            if (n == 1) {
                chunkCoordinates = worldServer2.getSpawnPoint();
            } else {
                chunkCoordinates = worldServer2.getEntrancePortalLocation();
            }

            posX = chunkCoordinates.posX;
            entityIn.posY = chunkCoordinates.posY;
            posZ = chunkCoordinates.posZ;
            entityIn.setLocationAndAngles(posX, entityIn.posY, posZ, 90.0F, 0.0F);
            if (entityIn.isEntityAlive()) {
                worldServer.updateEntityWithOptionalForce(entityIn, false);
            }
        }

        worldServer.theProfiler.endSection();
        if (n != 1) {
            worldServer.theProfiler.startSection("placing");
            posX = MathHelper.clamp_int((int)posX, -29999872, 29999872);
            posZ = MathHelper.clamp_int((int)posZ, -29999872, 29999872);
            if (entityIn.isEntityAlive()) {
                entityIn.setLocationAndAngles(posX, entityIn.posY, posZ, entityIn.rotationYaw, entityIn.rotationPitch);
                worldServer2.getDefaultTeleporter().placeInPortal(entityIn, posX2, posY, posZ2, rotationYaw);
                worldServer2.spawnEntityInWorld(entityIn);
                worldServer2.updateEntityWithOptionalForce(entityIn, false);
            }

            worldServer.theProfiler.endSection();
        }

        entityIn.setWorld(worldServer2);
    }

    public void onTick() {
        if (++this.playerPingIndex > 600) {
            this.playerPingIndex = 0;
        }

        if (this.playerPingIndex < this.playerEntityList.size()) {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP)this.playerEntityList.get(this.playerPingIndex);
            this.sendPacketToAllPlayers(new S38PacketPlayerListItem(entityPlayerMP.getCommandSenderName(), true, entityPlayerMP.ping));
        }
    }

    public void sendPacketToAllPlayers(Packet packetIn) {
        for (int i = 0; i < this.playerEntityList.size(); i++) {
            ((EntityPlayerMP)this.playerEntityList.get(i)).playerNetServerHandler.sendPacket(packetIn);
        }
    }

    public void sendPacketToAllPlayersInDimension(Packet packetIn, int dimension) {
        for (int i = 0; i < this.playerEntityList.size(); i++) {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP)this.playerEntityList.get(i);
            if (entityPlayerMP.dimension == dimension) {
                entityPlayerMP.playerNetServerHandler.sendPacket(packetIn);
            }
        }
    }

    public String getPlayerNamesString(boolean includeUuid) {
        String str = "";
        ArrayList newArrayList = Lists.newArrayList(this.playerEntityList);

        for (int i = 0; i < newArrayList.size(); i++) {
            if (i > 0) {
                str = str + ", ";
            }

            str = str + ((EntityPlayerMP)newArrayList.get(i)).getCommandSenderName();
            if (includeUuid) {
                str = str + " (" + ((EntityPlayerMP)newArrayList.get(i)).getUniqueID().toString() + ")";
            }
        }

        return str;
    }

    public String[] getAllUsernames() {
        String[] strs = new String[this.playerEntityList.size()];

        for (int i = 0; i < this.playerEntityList.size(); i++) {
            strs[i] = ((EntityPlayerMP)this.playerEntityList.get(i)).getCommandSenderName();
        }

        return strs;
    }

    public GameProfile[] getAllProfiles() {
        GameProfile[] gameProfiles = new GameProfile[this.playerEntityList.size()];

        for (int i = 0; i < this.playerEntityList.size(); i++) {
            gameProfiles[i] = ((EntityPlayerMP)this.playerEntityList.get(i)).getGameProfile();
        }

        return gameProfiles;
    }

    public UserListBans getBannedPlayers() {
        return this.bannedPlayers;
    }

    public BanList getBannedIPs() {
        return this.bannedIPs;
    }

    public void addOp(GameProfile profile) {
        this.ops.addEntry(new UserListOpsEntry(profile, this.mcServer.getOpPermissionLevel()));
    }

    public void removeOp(GameProfile profile) {
        this.ops.removeEntry(profile);
    }

    public boolean canJoin(GameProfile profile) {
        return !this.whiteListEnforced || this.ops.hasEntry(profile) || this.whiteListedPlayers.hasEntry(profile);
    }

    public boolean canSendCommands(GameProfile profile) {
        return this.ops.hasEntry(profile)
            || this.mcServer.isSinglePlayer()
                && this.mcServer.worldServers[0].getWorldInfo().areCommandsAllowed()
                && this.mcServer.getServerOwner().equalsIgnoreCase(profile.getName())
            || this.commandsAllowedForAll;
    }

    public EntityPlayerMP getPlayerByUsername(String username) {
        for (EntityPlayerMP entityPlayerMP : (Iterable<EntityPlayerMP>)this.playerEntityList) {
            if (entityPlayerMP.getCommandSenderName().equalsIgnoreCase(username)) {
                return entityPlayerMP;
            }
        }

        return null;
    }

    public List findPlayers(
        ChunkCoordinates coordinates,
        int minRadius,
        int maxRadius,
        int maxAmount,
        int gameMode,
        int minXp,
        int maxXp,
        Map scoreboardData,
        String username,
        String teamName,
        World worldIn
    ) {
        if (this.playerEntityList.isEmpty()) {
            return Collections.emptyList();
        }

        List list = new ArrayList();
        boolean flag = maxAmount < 0;
        boolean flag2 = username != null && username.startsWith("!");
        boolean flag3 = teamName != null && teamName.startsWith("!");
        int n = minRadius * minRadius;
        int n2 = maxRadius * maxRadius;
        maxAmount = MathHelper.abs_int(maxAmount);
        if (flag2) {
            username = username.substring(1);
        }

        if (flag3) {
            teamName = teamName.substring(1);
        }

        for (int i = 0; i < this.playerEntityList.size(); i++) {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP)this.playerEntityList.get(i);
            if ((worldIn == null || entityPlayerMP.worldObj == worldIn) && (username == null || flag2 != username.equalsIgnoreCase(entityPlayerMP.getCommandSenderName()))) {
                if (teamName != null) {
                    Team team = entityPlayerMP.getTeam();
                    String str = team == null ? "" : team.getRegisteredName();
                    if (flag3 == teamName.equalsIgnoreCase(str)) {
                        continue;
                    }
                }

                if (coordinates != null && (minRadius > 0 || maxRadius > 0)) {
                    float f = coordinates.getDistanceSquaredToChunkCoordinates(entityPlayerMP.getCommandSenderPosition());
                    if (minRadius > 0 && f < n || maxRadius > 0 && f > n2) {
                        continue;
                    }
                }

                if (this.matchesScoreboardCriteria(entityPlayerMP, scoreboardData)
                    && (gameMode == GameType.NOT_SET.getID() || gameMode == entityPlayerMP.theItemInWorldManager.getGameType().getID())
                    && (minXp <= 0 || entityPlayerMP.experienceLevel >= minXp)
                    && entityPlayerMP.experienceLevel <= maxXp) {
                    list.add(entityPlayerMP);
                }
            }
        }

        if (coordinates != null) {
            Collections.sort(list, new PlayerPositionComparator(coordinates));
        }

        if (flag) {
            Collections.reverse(list);
        }

        if (maxAmount > 0) {
            list = list.subList(0, Math.min(maxAmount, list.size()));
        }

        return list;
    }

    public boolean matchesScoreboardCriteria(EntityPlayer player, Map scoreboardCriteria) {
        if (scoreboardCriteria != null && scoreboardCriteria.size() != 0) {
            for (Entry entry : (Iterable<Entry>)scoreboardCriteria.entrySet()) {
                String key = (String)entry.getKey();
                boolean flag = false;
                if (key.endsWith("_min") && key.length() > 4) {
                    flag = true;
                    key = key.substring(0, key.length() - 4);
                }

                Scoreboard worldScoreboard = player.getWorldScoreboard();
                ScoreObjective objective = worldScoreboard.getObjective(key);
                if (objective == null) {
                    return false;
                }

                Score valueFromObjective = player.getWorldScoreboard().getValueFromObjective(player.getCommandSenderName(), objective);
                int scorePoints = valueFromObjective.getScorePoints();
                if (scorePoints < (Integer)entry.getValue() && flag) {
                    return false;
                }

                if (scorePoints > (Integer)entry.getValue() && !flag) {
                    return false;
                }
            }

            return true;
        } else {
            return true;
        }
    }

    public void sendToAllNear(double x, double y, double z, double radius, int dimension, Packet packetIn) {
        this.sendToAllNearExcept(null, x, y, z, radius, dimension, packetIn);
    }

    public void sendToAllNearExcept(EntityPlayer entityPlayer, double x, double y, double z, double radius, int dimension, Packet packet) {
        for (int i = 0; i < this.playerEntityList.size(); i++) {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP)this.playerEntityList.get(i);
            if (entityPlayerMP != entityPlayer && entityPlayerMP.dimension == dimension) {
                double d = x - entityPlayerMP.posX;
                double d2 = y - entityPlayerMP.posY;
                double d3 = z - entityPlayerMP.posZ;
                if (d * d + d2 * d2 + d3 * d3 < radius * radius) {
                    entityPlayerMP.playerNetServerHandler.sendPacket(packet);
                }
            }
        }
    }

    public void saveAllPlayerData() {
        for (int i = 0; i < this.playerEntityList.size(); i++) {
            this.writePlayerData((EntityPlayerMP)this.playerEntityList.get(i));
        }
    }

    public void addWhitelistedPlayer(GameProfile profile) {
        this.whiteListedPlayers.addEntry(new UserListWhitelistEntry(profile));
    }

    public void removePlayerFromWhitelist(GameProfile profile) {
        this.whiteListedPlayers.removeEntry(profile);
    }

    public UserListWhitelist getWhitelistedPlayers() {
        return this.whiteListedPlayers;
    }

    public String[] getWhitelistedPlayerNames() {
        return this.whiteListedPlayers.getKeys();
    }

    public UserListOps getOppedPlayers() {
        return this.ops;
    }

    public String[] getOppedPlayerNames() {
        return this.ops.getKeys();
    }

    public void loadWhiteList() {
    }

    public void updateTimeAndWeatherForPlayer(EntityPlayerMP player, WorldServer worldIn) {
        player.playerNetServerHandler
            .sendPacket(
                new S03PacketTimeUpdate(worldIn.getTotalWorldTime(), worldIn.getWorldTime(), worldIn.getGameRules().getGameRuleBooleanValue("doDaylightCycle"))
            );
        if (worldIn.isRaining()) {
            player.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(1, 0.0F));
            player.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(7, worldIn.getRainStrength(1.0F)));
            player.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(8, worldIn.getWeightedThunderStrength(1.0F)));
        }
    }

    public void syncPlayerInventory(EntityPlayerMP player) {
        player.sendContainerToPlayer(player.inventoryContainer);
        player.setPlayerHealthUpdated();
        player.playerNetServerHandler.sendPacket(new S09PacketHeldItemChange(player.inventory.currentItem));
    }

    public int getCurrentPlayerCount() {
        return this.playerEntityList.size();
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public String[] getAvailablePlayerDat() {
        return this.mcServer.worldServers[0].getSaveHandler().getPlayerNBTManager().getAvailablePlayerDat();
    }

    public void setWhiteListEnabled(boolean whitelistEnabled) {
        this.whiteListEnforced = whitelistEnabled;
    }

    public List getPlayersMatchingAddress(String address) {
        ArrayList list = new ArrayList();

        for (EntityPlayerMP entityPlayerMP : (Iterable<EntityPlayerMP>)this.playerEntityList) {
            if (entityPlayerMP.getPlayerIP().equals(address)) {
                list.add(entityPlayerMP);
            }
        }

        return list;
    }

    public int getViewDistance() {
        return this.viewDistance;
    }

    public MinecraftServer getServerInstance() {
        return this.mcServer;
    }

    public NBTTagCompound getHostPlayerData() {
        return null;
    }

    public void setGameMode(GameType gameType) {
        this.gameType = gameType;
    }

    public void setGameMode(EntityPlayerMP entityPlayerMP, EntityPlayerMP entityPlayerMP2, World world) {
        if (entityPlayerMP2 != null) {
            entityPlayerMP.theItemInWorldManager.setGameType(entityPlayerMP2.theItemInWorldManager.getGameType());
        } else if (this.gameType != null) {
            entityPlayerMP.theItemInWorldManager.setGameType(this.gameType);
        }

        entityPlayerMP.theItemInWorldManager.initializeGameType(world.getWorldInfo().getGameType());
    }

    public void setCommandsAllowedForAll(boolean flag) {
        this.commandsAllowedForAll = flag;
    }

    public void removeAllPlayers() {
        for (int i = 0; i < this.playerEntityList.size(); i++) {
            ((EntityPlayerMP)this.playerEntityList.get(i)).playerNetServerHandler.kickPlayerFromServer("Server closed");
        }
    }

    public void sendChatMsgImpl(IChatComponent component, boolean isChat) {
        this.mcServer.addChatMessage(component);
        this.sendPacketToAllPlayers(new S02PacketChat(component, isChat));
    }

    public void sendChatMsg(IChatComponent component) {
        this.sendChatMsgImpl(component, true);
    }

    public StatisticsFile getPlayerStatsFile(EntityPlayer player) {
        UUID uniqueID = player.getUniqueID();
        StatisticsFile statisticsFile = uniqueID == null ? null : (StatisticsFile)this.playerStatFiles.get(uniqueID);
        if (statisticsFile == null) {
            File file = new File(this.mcServer.worldServerForDimension(0).getSaveHandler().getWorldDirectory(), "stats");
            File file2 = new File(file, uniqueID.toString() + ".json");
            if (!file2.exists()) {
                File file3 = new File(file, player.getCommandSenderName() + ".json");
                if (file3.exists() && file3.isFile()) {
                    file3.renameTo(file2);
                }
            }

            statisticsFile = new StatisticsFile(this.mcServer, file2);
            statisticsFile.run();
            this.playerStatFiles.put(uniqueID, statisticsFile);
        }

        return statisticsFile;
    }

    public void setViewDistance(int distance) {
        this.viewDistance = distance;
        if (this.mcServer.worldServers != null) {
            for (WorldServer worldServer : this.mcServer.worldServers) {
                if (worldServer != null) {
                    worldServer.getPlayerManager().applyViewDistance(distance);
                }
            }
        }
    }
}
