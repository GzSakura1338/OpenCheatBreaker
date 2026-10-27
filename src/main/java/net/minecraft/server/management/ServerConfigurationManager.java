package net.minecraft.server.management;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.Unpooled;
import java.io.File;
import java.net.SocketAddress;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
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
import net.minecraft.network.play.server.S41PacketServerDifficulty;
import net.minecraft.network.play.server.S44PacketWorldBorder;

import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.StatList;
import net.minecraft.stats.StatisticsFile;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.border.IBorderListener;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.demo.DemoWorldManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.WorldInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class ServerConfigurationManager {
   public static File FILE_PLAYERBANS = new File("banned-players.json");
   public static File FILE_IPBANS = new File("banned-ips.json");
   public static File FILE_OPS = new File("ops.json");
   public static File FILE_WHITELIST = new File("whitelist.json");
   public static Logger logger = LogManager.getLogger();
   public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
   public List<EntityPlayerMP> playerEntityList = Lists.newArrayList();
   public Map<UUID, EntityPlayerMP> uuidToPlayerMap = Maps.newHashMap();
   public UserListBans bannedPlayers = new UserListBans(FILE_PLAYERBANS);
   public BanList bannedIPs = new BanList(FILE_IPBANS);
   public UserListOps ops = new UserListOps(FILE_OPS);
   public UserListWhitelist whiteListedPlayers = new UserListWhitelist(FILE_WHITELIST);
   public Map<UUID, StatisticsFile> playerStatFiles = Maps.newHashMap();
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

   public void initializeConnectionToPlayer(NetworkManager netManager, EntityPlayerMP playerIn) {
      GameProfile gameProfile = playerIn.getGameProfile();
      PlayerProfileCache playerProfileCache = this.mcServer.getPlayerProfileCache();
      GameProfile profileByUUID = playerProfileCache.getProfileByUUID(gameProfile.getId());
      String str = profileByUUID == null ? gameProfile.getName() : profileByUUID.getName();
      playerProfileCache.addEntry(gameProfile);
      NBTTagCompound readPlayerDataFromFile = this.readPlayerDataFromFile(playerIn);
      playerIn.setWorld(this.mcServer.worldServerForDimension(playerIn.dimension));
      playerIn.theItemInWorldManager.setWorld((WorldServer)playerIn.worldObj);
      String str2 = "local";
      if (netManager.getRemoteAddress() != null) {
         str2 = netManager.getRemoteAddress().toString();
      }

      logger.info(
         playerIn.getName()
            + "["
            + str2
            + "] logged in with entity id "
            + playerIn.getEntityId()
            + " at ("
            + playerIn.posX
            + ", "
            + playerIn.posY
            + ", "
            + playerIn.posZ
            + ")"
      );
      WorldServer worldServerForDimension = this.mcServer.worldServerForDimension(playerIn.dimension);
      WorldInfo worldInfo = worldServerForDimension.getWorldInfo();
      BlockPos spawnPoint = worldServerForDimension.getSpawnPoint();
      this.setPlayerGameTypeBasedOnOther(playerIn, (EntityPlayerMP)null, worldServerForDimension);
      NetHandlerPlayServer netHandlerPlayServer = new NetHandlerPlayServer(this.mcServer, netManager, playerIn);
      netHandlerPlayServer.sendPacket(
         new S01PacketJoinGame(
            playerIn.getEntityId(),
            playerIn.theItemInWorldManager.getGameType(),
            worldInfo.isHardcoreModeEnabled(),
            worldServerForDimension.provider.getDimensionId(),
            worldServerForDimension.getDifficulty(),
            this.getMaxPlayers(),
            worldInfo.getTerrainType(),
            worldServerForDimension.getGameRules().getBoolean("reducedDebugInfo")
         )
      );
      netHandlerPlayServer.sendPacket(new S3FPacketCustomPayload("MC|Brand", new PacketBuffer(Unpooled.buffer()).writeString(this.getServerInstance().getServerModName())));
      netHandlerPlayServer.sendPacket(new S41PacketServerDifficulty(worldInfo.getDifficulty(), worldInfo.isDifficultyLocked()));
      netHandlerPlayServer.sendPacket(new S05PacketSpawnPosition(spawnPoint));
      netHandlerPlayServer.sendPacket(new S39PacketPlayerAbilities(playerIn.capabilities));
      netHandlerPlayServer.sendPacket(new S09PacketHeldItemChange(playerIn.inventory.currentItem));
      playerIn.getStatFile().updateStatSet();
      playerIn.getStatFile().sendAchievements(playerIn);
      this.sendScoreboard((ServerScoreboard)worldServerForDimension.getScoreboard(), playerIn);
      this.mcServer.refreshStatusNextTick();
      ChatComponentTranslation chatComponentTranslation;
      if (!playerIn.getName().equalsIgnoreCase(str)) {
         chatComponentTranslation = new ChatComponentTranslation("multiplayer.player.joined.renamed", playerIn.getDisplayName(), str);
      } else {
         chatComponentTranslation = new ChatComponentTranslation("multiplayer.player.joined", playerIn.getDisplayName());
      }

      chatComponentTranslation.getChatStyle().setColor(EnumChatFormatting.YELLOW);
      this.sendChatMsg(chatComponentTranslation);
      this.playerLoggedIn(playerIn);
      netHandlerPlayServer.setPlayerLocation(playerIn.posX, playerIn.posY, playerIn.posZ, playerIn.rotationYaw, playerIn.rotationPitch);
      this.updateTimeAndWeatherForPlayer(playerIn, worldServerForDimension);
      if (this.mcServer.getResourcePackUrl().length() > 0) {
         playerIn.loadResourcePack(this.mcServer.getResourcePackUrl(), this.mcServer.getResourcePackHash());
      }

      for (PotionEffect potionEffect : playerIn.getActivePotionEffects()) {
         netHandlerPlayServer.sendPacket(new S1DPacketEntityEffect(playerIn.getEntityId(), potionEffect));
      }

      playerIn.addSelfToInternalCraftingInventory();
      if (readPlayerDataFromFile != null && readPlayerDataFromFile.hasKey("Riding", 10)) {
         Entity createEntityFromNBT = EntityList.createEntityFromNBT(readPlayerDataFromFile.getCompoundTag("Riding"), worldServerForDimension);
         if (createEntityFromNBT != null) {
            createEntityFromNBT.forceSpawn = true;
            worldServerForDimension.spawnEntityInWorld(createEntityFromNBT);
            playerIn.mountEntity(createEntityFromNBT);
            createEntityFromNBT.forceSpawn = false;
         }
      }
   }

   public void sendScoreboard(ServerScoreboard scoreboardIn, EntityPlayerMP playerIn) {
      HashSet newHashSet = Sets.newHashSet();

      for (ScorePlayerTeam scorePlayerTeam : scoreboardIn.getTeams()) {
         playerIn.playerNetServerHandler.sendPacket(new S3EPacketTeams(scorePlayerTeam, 0));
      }

      for (int i = 0; i < 19; i++) {
         ScoreObjective objectiveInDisplaySlot = scoreboardIn.getObjectiveInDisplaySlot(i);
         if (objectiveInDisplaySlot != null && !newHashSet.contains(objectiveInDisplaySlot)) {
            for (Packet packet : scoreboardIn.createChangePackets(objectiveInDisplaySlot)) {
               playerIn.playerNetServerHandler.sendPacket(packet);
            }

            newHashSet.add(objectiveInDisplaySlot);
         }
      }
   }

   public void setPlayerManager(WorldServer[] worldServers) {
      this.playerNBTManagerObj = worldServers[0].getSaveHandler().getPlayerNBTManager();
      worldServers[0].getWorldBorder().addListener(new IBorderListener() {
         @Override
         public void onSizeChanged(WorldBorder border, double newSize) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.SET_SIZE));
         }

         @Override
         public void onTransitionStarted(WorldBorder border, double oldSize, double newSize, long time) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.LERP_SIZE));
         }

         @Override
         public void onCenterChanged(WorldBorder border, double x, double z) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.SET_CENTER));
         }

         @Override
         public void onWarningTimeChanged(WorldBorder border, int newTime) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.SET_WARNING_TIME));
         }

         @Override
         public void onWarningDistanceChanged(WorldBorder border, int newDistance) {
            ServerConfigurationManager.this.sendPacketToAllPlayers(new S44PacketWorldBorder(border, S44PacketWorldBorder.Action.SET_WARNING_BLOCKS));
         }

         @Override
         public void onDamageAmountChanged(WorldBorder border, double newAmount) {
         }

         @Override
         public void onDamageBufferChanged(WorldBorder border, double newSize) {
         }
      });
   }

   public void preparePlayer(EntityPlayerMP playerIn, WorldServer worldIn) {
      WorldServer serverForPlayer = playerIn.getServerForPlayer();
      if (worldIn != null) {
         worldIn.getPlayerManager().removePlayer(playerIn);
      }

      serverForPlayer.getPlayerManager().addPlayer(playerIn);
      serverForPlayer.theChunkProviderServer.loadChunk((int)playerIn.posX >> 4, (int)playerIn.posZ >> 4);
   }

   public int getEntityViewDistance() {
      return PlayerManager.getFurthestViewableBlock(this.getViewDistance());
   }

   public NBTTagCompound readPlayerDataFromFile(EntityPlayerMP playerIn) {
      NBTTagCompound playerNBTTagCompound = this.mcServer.worldServers[0].getWorldInfo().getPlayerNBTTagCompound();
      NBTTagCompound nbttagCompound;
      if (playerIn.getName().equals(this.mcServer.getServerOwner()) && playerNBTTagCompound != null) {
         playerIn.readFromNBT(playerNBTTagCompound);
         nbttagCompound = playerNBTTagCompound;
         logger.debug("loading single player");
      } else {
         nbttagCompound = this.playerNBTManagerObj.readPlayerData(playerIn);
      }

      return nbttagCompound;
   }

   public void writePlayerData(EntityPlayerMP playerIn) {
      this.playerNBTManagerObj.writePlayerData(playerIn);
      StatisticsFile statisticsFile = this.playerStatFiles.get(playerIn.getUniqueID());
      if (statisticsFile != null) {
         statisticsFile.saveStatFile();
      }
   }

   public void playerLoggedIn(EntityPlayerMP playerIn) {
      this.playerEntityList.add(playerIn);
      this.uuidToPlayerMap.put(playerIn.getUniqueID(), playerIn);
      this.sendPacketToAllPlayers(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.ADD_PLAYER, playerIn));
      WorldServer worldServerForDimension = this.mcServer.worldServerForDimension(playerIn.dimension);
      worldServerForDimension.spawnEntityInWorld(playerIn);
      this.preparePlayer(playerIn, (WorldServer)null);

      for (int i = 0; i < this.playerEntityList.size(); i++) {
         EntityPlayerMP entityPlayerMP = this.playerEntityList.get(i);
         playerIn.playerNetServerHandler.sendPacket(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.ADD_PLAYER, entityPlayerMP));
      }
   }

   public void serverUpdateMountedMovingPlayer(EntityPlayerMP playerIn) {
      playerIn.getServerForPlayer().getPlayerManager().updateMountedMovingPlayer(playerIn);
   }

   public void playerLoggedOut(EntityPlayerMP playerIn) {
      playerIn.triggerAchievement(StatList.leaveGameStat);
      this.writePlayerData(playerIn);
      WorldServer serverForPlayer = playerIn.getServerForPlayer();
      if (playerIn.ridingEntity != null) {
         serverForPlayer.removePlayerEntityDangerously(playerIn.ridingEntity);
         logger.debug("removing player mount");
      }

      serverForPlayer.removeEntity(playerIn);
      serverForPlayer.getPlayerManager().removePlayer(playerIn);
      this.playerEntityList.remove(playerIn);
      UUID uniqueID = playerIn.getUniqueID();
      EntityPlayerMP entityPlayerMP = this.uuidToPlayerMap.get(uniqueID);
      if (entityPlayerMP == playerIn) {
         this.uuidToPlayerMap.remove(uniqueID);
         this.playerStatFiles.remove(uniqueID);
      }

      this.sendPacketToAllPlayers(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.REMOVE_PLAYER, playerIn));
   }

   public String allowUserToConnect(SocketAddress address, GameProfile profile) {
      if (this.bannedPlayers.isBanned(profile)) {
         UserListBansEntry entry = this.bannedPlayers.getEntry(profile);
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
            return this.playerEntityList.size() >= this.maxPlayers && !this.bypassesPlayerLimit(profile) ? "The server is full!" : null;
         }
      }
   }

   public EntityPlayerMP createPlayerForUser(GameProfile profile) {
      UUID uUID = EntityPlayer.getUUID(profile);
      ArrayList newArrayList = Lists.newArrayList();

      for (int i = 0; i < this.playerEntityList.size(); i++) {
         EntityPlayerMP entityPlayerMP = this.playerEntityList.get(i);
         if (entityPlayerMP.getUniqueID().equals(uUID)) {
            newArrayList.add(entityPlayerMP);
         }
      }

      EntityPlayerMP entityPlayerMP = this.uuidToPlayerMap.get(profile.getId());
      if (entityPlayerMP != null && !newArrayList.contains(entityPlayerMP)) {
         newArrayList.add(entityPlayerMP);
      }

      for (EntityPlayerMP entityPlayerMP2 : (Iterable<EntityPlayerMP>)newArrayList) {
         entityPlayerMP2.playerNetServerHandler.kickPlayerFromServer("You logged in from another location");
      }

      ItemInWorldManager itemInWorldManager;
      if (this.mcServer.isDemo()) {
         itemInWorldManager = new DemoWorldManager(this.mcServer.worldServerForDimension(0));
      } else {
         itemInWorldManager = new ItemInWorldManager(this.mcServer.worldServerForDimension(0));
      }

      return new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(0), profile, itemInWorldManager);
   }

   public EntityPlayerMP recreatePlayerEntity(EntityPlayerMP playerIn, int dimension, boolean conqueredEnd) {
      playerIn.getServerForPlayer().getEntityTracker().removePlayerFromTrackers(playerIn);
      playerIn.getServerForPlayer().getEntityTracker().untrackEntity(playerIn);
      playerIn.getServerForPlayer().getPlayerManager().removePlayer(playerIn);
      this.playerEntityList.remove(playerIn);
      this.mcServer.worldServerForDimension(playerIn.dimension).removePlayerEntityDangerously(playerIn);
      BlockPos bedLocation = playerIn.getBedLocation();
      boolean isSpawnForced = playerIn.isSpawnForced();
      playerIn.dimension = dimension;
      ItemInWorldManager itemInWorldManager;
      if (this.mcServer.isDemo()) {
         itemInWorldManager = new DemoWorldManager(this.mcServer.worldServerForDimension(playerIn.dimension));
      } else {
         itemInWorldManager = new ItemInWorldManager(this.mcServer.worldServerForDimension(playerIn.dimension));
      }

      EntityPlayerMP entityPlayerMP = new EntityPlayerMP(this.mcServer, this.mcServer.worldServerForDimension(playerIn.dimension), playerIn.getGameProfile(), itemInWorldManager);
      entityPlayerMP.playerNetServerHandler = playerIn.playerNetServerHandler;
      entityPlayerMP.clonePlayer(playerIn, conqueredEnd);
      entityPlayerMP.setEntityId(playerIn.getEntityId());
      entityPlayerMP.setCommandStats(playerIn);
      WorldServer worldServerForDimension = this.mcServer.worldServerForDimension(playerIn.dimension);
      this.setPlayerGameTypeBasedOnOther(entityPlayerMP, playerIn, worldServerForDimension);
      if (bedLocation != null) {
         BlockPos bedSpawnLocation = EntityPlayer.getBedSpawnLocation(this.mcServer.worldServerForDimension(playerIn.dimension), bedLocation, isSpawnForced);
         if (bedSpawnLocation != null) {
            entityPlayerMP.setLocationAndAngles(bedSpawnLocation.getX() + 0.5F, bedSpawnLocation.getY() + 0.1F, bedSpawnLocation.getZ() + 0.5F, 0.0F, 0.0F);
            entityPlayerMP.setSpawnPoint(bedLocation, isSpawnForced);
         } else {
            entityPlayerMP.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(0, 0.0F));
         }
      }

      worldServerForDimension.theChunkProviderServer.loadChunk((int)entityPlayerMP.posX >> 4, (int)entityPlayerMP.posZ >> 4);

      while (!worldServerForDimension.getCollidingBoundingBoxes(entityPlayerMP, entityPlayerMP.getEntityBoundingBox()).isEmpty() && entityPlayerMP.posY < 256.0) {
         entityPlayerMP.setPosition(entityPlayerMP.posX, entityPlayerMP.posY + 1.0, entityPlayerMP.posZ);
      }

      entityPlayerMP.playerNetServerHandler
         .sendPacket(
            new S07PacketRespawn(
               entityPlayerMP.dimension, entityPlayerMP.worldObj.getDifficulty(), entityPlayerMP.worldObj.getWorldInfo().getTerrainType(), entityPlayerMP.theItemInWorldManager.getGameType()
            )
         );
      BlockPos spawnPoint = worldServerForDimension.getSpawnPoint();
      entityPlayerMP.playerNetServerHandler.setPlayerLocation(entityPlayerMP.posX, entityPlayerMP.posY, entityPlayerMP.posZ, entityPlayerMP.rotationYaw, entityPlayerMP.rotationPitch);
      entityPlayerMP.playerNetServerHandler.sendPacket(new S05PacketSpawnPosition(spawnPoint));
      entityPlayerMP.playerNetServerHandler.sendPacket(new S1FPacketSetExperience(entityPlayerMP.experience, entityPlayerMP.experienceTotal, entityPlayerMP.experienceLevel));
      this.updateTimeAndWeatherForPlayer(entityPlayerMP, worldServerForDimension);
      worldServerForDimension.getPlayerManager().addPlayer(entityPlayerMP);
      worldServerForDimension.spawnEntityInWorld(entityPlayerMP);
      this.playerEntityList.add(entityPlayerMP);
      this.uuidToPlayerMap.put(entityPlayerMP.getUniqueID(), entityPlayerMP);
      entityPlayerMP.addSelfToInternalCraftingInventory();
      entityPlayerMP.setHealth(entityPlayerMP.getHealth());
      return entityPlayerMP;
   }

   public void transferPlayerToDimension(EntityPlayerMP playerIn, int dimension) {
      int dimension2 = playerIn.dimension;
      WorldServer worldServerForDimension = this.mcServer.worldServerForDimension(playerIn.dimension);
      playerIn.dimension = dimension;
      WorldServer worldServerForDimension2 = this.mcServer.worldServerForDimension(playerIn.dimension);
      playerIn.playerNetServerHandler
         .sendPacket(
            new S07PacketRespawn(
               playerIn.dimension,
               playerIn.worldObj.getDifficulty(),
               playerIn.worldObj.getWorldInfo().getTerrainType(),
               playerIn.theItemInWorldManager.getGameType()
            )
         );
      worldServerForDimension.removePlayerEntityDangerously(playerIn);
      playerIn.isDead = false;
      this.transferEntityToWorld(playerIn, dimension2, worldServerForDimension, worldServerForDimension2);
      this.preparePlayer(playerIn, worldServerForDimension);
      playerIn.playerNetServerHandler.setPlayerLocation(playerIn.posX, playerIn.posY, playerIn.posZ, playerIn.rotationYaw, playerIn.rotationPitch);
      playerIn.theItemInWorldManager.setWorld(worldServerForDimension2);
      this.updateTimeAndWeatherForPlayer(playerIn, worldServerForDimension2);
      this.syncPlayerInventory(playerIn);

      for (PotionEffect potionEffect : playerIn.getActivePotionEffects()) {
         playerIn.playerNetServerHandler.sendPacket(new S1DPacketEntityEffect(playerIn.getEntityId(), potionEffect));
      }
   }

   public void transferEntityToWorld(Entity entityIn, int n, WorldServer oldWorldIn, WorldServer toWorldIn) {
      double posX = entityIn.posX;
      double posZ = entityIn.posZ;
      double d = 8.0;
      float rotationYaw = entityIn.rotationYaw;
      oldWorldIn.theProfiler.startSection("moving");
      if (entityIn.dimension == -1) {
         posX = MathHelper.clamp_double(posX / d, toWorldIn.getWorldBorder().minX() + 16.0, toWorldIn.getWorldBorder().maxX() - 16.0);
         posZ = MathHelper.clamp_double(posZ / d, toWorldIn.getWorldBorder().minZ() + 16.0, toWorldIn.getWorldBorder().maxZ() - 16.0);
         entityIn.setLocationAndAngles(posX, entityIn.posY, posZ, entityIn.rotationYaw, entityIn.rotationPitch);
         if (entityIn.isEntityAlive()) {
            oldWorldIn.updateEntityWithOptionalForce(entityIn, false);
         }
      } else if (entityIn.dimension == 0) {
         posX = MathHelper.clamp_double(posX * d, toWorldIn.getWorldBorder().minX() + 16.0, toWorldIn.getWorldBorder().maxX() - 16.0);
         posZ = MathHelper.clamp_double(posZ * d, toWorldIn.getWorldBorder().minZ() + 16.0, toWorldIn.getWorldBorder().maxZ() - 16.0);
         entityIn.setLocationAndAngles(posX, entityIn.posY, posZ, entityIn.rotationYaw, entityIn.rotationPitch);
         if (entityIn.isEntityAlive()) {
            oldWorldIn.updateEntityWithOptionalForce(entityIn, false);
         }
      } else {
         BlockPos blockPos;
         if (n == 1) {
            blockPos = toWorldIn.getSpawnPoint();
         } else {
            blockPos = toWorldIn.getSpawnCoordinate();
         }

         posX = blockPos.getX();
         entityIn.posY = blockPos.getY();
         posZ = blockPos.getZ();
         entityIn.setLocationAndAngles(posX, entityIn.posY, posZ, 90.0F, 0.0F);
         if (entityIn.isEntityAlive()) {
            oldWorldIn.updateEntityWithOptionalForce(entityIn, false);
         }
      }

      oldWorldIn.theProfiler.endSection();
      if (n != 1) {
         oldWorldIn.theProfiler.startSection("placing");
         posX = MathHelper.clamp_int((int)posX, -29999872, 29999872);
         posZ = MathHelper.clamp_int((int)posZ, -29999872, 29999872);
         if (entityIn.isEntityAlive()) {
            entityIn.setLocationAndAngles(posX, entityIn.posY, posZ, entityIn.rotationYaw, entityIn.rotationPitch);
            toWorldIn.getDefaultTeleporter().placeInPortal(entityIn, rotationYaw);
            toWorldIn.spawnEntityInWorld(entityIn);
            toWorldIn.updateEntityWithOptionalForce(entityIn, false);
         }

         oldWorldIn.theProfiler.endSection();
      }

      entityIn.setWorld(toWorldIn);
   }

   public void onTick() {
      if (++this.playerPingIndex > 600) {
         this.sendPacketToAllPlayers(new S38PacketPlayerListItem(S38PacketPlayerListItem.Action.UPDATE_LATENCY, this.playerEntityList));
         this.playerPingIndex = 0;
      }
   }

   public void sendPacketToAllPlayers(Packet packetIn) {
      for (int i = 0; i < this.playerEntityList.size(); i++) {
         this.playerEntityList.get(i).playerNetServerHandler.sendPacket(packetIn);
      }
   }

   public void sendPacketToAllPlayersInDimension(Packet packetIn, int dimension) {
      for (int i = 0; i < this.playerEntityList.size(); i++) {
         EntityPlayerMP entityPlayerMP = this.playerEntityList.get(i);
         if (entityPlayerMP.dimension == dimension) {
            entityPlayerMP.playerNetServerHandler.sendPacket(packetIn);
         }
      }
   }

   public void sendMessageToAllTeamMembers(EntityPlayer player, IChatComponent message) {
      Team team = player.getTeam();
      if (team != null) {
         for (String str : team.getMembershipCollection()) {
            EntityPlayerMP playerByUsername = this.getPlayerByUsername(str);
            if (playerByUsername != null && playerByUsername != player) {
               playerByUsername.addChatMessage(message);
            }
         }
      }
   }

   public void sendMessageToTeamOrEvryPlayer(EntityPlayer player, IChatComponent message) {
      Team team = player.getTeam();
      if (team == null) {
         this.sendChatMsg(message);
      } else {
         for (int i = 0; i < this.playerEntityList.size(); i++) {
            EntityPlayerMP entityPlayerMP = this.playerEntityList.get(i);
            if (entityPlayerMP.getTeam() != team) {
               entityPlayerMP.addChatMessage(message);
            }
         }
      }
   }

   public String formatText(boolean flag) {
      String str = "";
      ArrayList newArrayList = Lists.newArrayList(this.playerEntityList);

      for (int i = 0; i < newArrayList.size(); i++) {
         if (i > 0) {
            str = str + ", ";
         }

         str = str + ((EntityPlayerMP)newArrayList.get(i)).getName();
         if (flag) {
            str = str + " (" + ((EntityPlayerMP)newArrayList.get(i)).getUniqueID().toString() + ")";
         }
      }

      return str;
   }

   public String[] getAllUsernames() {
      String[] strs = new String[this.playerEntityList.size()];

      for (int i = 0; i < this.playerEntityList.size(); i++) {
         strs[i] = this.playerEntityList.get(i).getName();
      }

      return strs;
   }

   public GameProfile[] getAllProfiles() {
      GameProfile[] gameProfiles = new GameProfile[this.playerEntityList.size()];

      for (int i = 0; i < this.playerEntityList.size(); i++) {
         gameProfiles[i] = this.playerEntityList.get(i).getGameProfile();
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
      this.ops.addEntry(new UserListOpsEntry(profile, this.mcServer.getOpPermissionLevel(), this.ops.bypassesPlayerLimit(profile)));
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
      for (EntityPlayerMP entityPlayerMP : this.playerEntityList) {
         if (entityPlayerMP.getName().equalsIgnoreCase(username)) {
            return entityPlayerMP;
         }
      }

      return null;
   }

   public void sendToAllNear(double x, double y, double z, double radius, int dimension, Packet packetIn) {
      this.sendToAllNearExcept((EntityPlayer)null, x, y, z, radius, dimension, packetIn);
   }

   public void sendToAllNearExcept(EntityPlayer entityPlayer, double x, double y, double z, double radius, int dimension, Packet packet) {
      for (int i = 0; i < this.playerEntityList.size(); i++) {
         EntityPlayerMP entityPlayerMP = this.playerEntityList.get(i);
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
         this.writePlayerData(this.playerEntityList.get(i));
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

   public void updateTimeAndWeatherForPlayer(EntityPlayerMP playerIn, WorldServer worldIn) {
      WorldBorder worldBorder = this.mcServer.worldServers[0].getWorldBorder();
      playerIn.playerNetServerHandler.sendPacket(new S44PacketWorldBorder(worldBorder, S44PacketWorldBorder.Action.INITIALIZE));
      playerIn.playerNetServerHandler
         .sendPacket(new S03PacketTimeUpdate(worldIn.getTotalWorldTime(), worldIn.getWorldTime(), worldIn.getGameRules().getBoolean("doDaylightCycle")));
      if (worldIn.isRaining()) {
         playerIn.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(1, 0.0F));
         playerIn.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(7, worldIn.getRainStrength(1.0F)));
         playerIn.playerNetServerHandler.sendPacket(new S2BPacketChangeGameState(8, worldIn.getThunderStrength(1.0F)));
      }
   }

   public void syncPlayerInventory(EntityPlayerMP playerIn) {
      playerIn.sendContainerToPlayer(playerIn.inventoryContainer);
      playerIn.setPlayerHealthUpdated();
      playerIn.playerNetServerHandler.sendPacket(new S09PacketHeldItemChange(playerIn.inventory.currentItem));
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

   public List<EntityPlayerMP> getPlayersMatchingAddress(String address) {
      ArrayList newArrayList = Lists.newArrayList();

      for (EntityPlayerMP entityPlayerMP : this.playerEntityList) {
         if (entityPlayerMP.getPlayerIP().equals(address)) {
            newArrayList.add(entityPlayerMP);
         }
      }

      return newArrayList;
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

   public void setGameType(GameType gameType) {
      this.gameType = gameType;
   }

   public void setPlayerGameTypeBasedOnOther(EntityPlayerMP entityPlayerMP, EntityPlayerMP entityPlayerMP2, World worldIn) {
      if (entityPlayerMP2 != null) {
         entityPlayerMP.theItemInWorldManager.setGameType(entityPlayerMP2.theItemInWorldManager.getGameType());
      } else if (this.gameType != null) {
         entityPlayerMP.theItemInWorldManager.setGameType(this.gameType);
      }

      entityPlayerMP.theItemInWorldManager.initializeGameType(worldIn.getWorldInfo().getGameType());
   }

   public void setCommandsAllowedForAll(boolean flag) {
      this.commandsAllowedForAll = flag;
   }

   public void removeAllPlayers() {
      for (int i = 0; i < this.playerEntityList.size(); i++) {
         this.playerEntityList.get(i).playerNetServerHandler.kickPlayerFromServer("Server closed");
      }
   }

   public void sendChatMsgImpl(IChatComponent component, boolean isChat) {
      this.mcServer.addChatMessage(component);
      byte b = (byte)(isChat ? 1 : 0);
      this.sendPacketToAllPlayers(new S02PacketChat(component, b));
   }

   public void sendChatMsg(IChatComponent component) {
      this.sendChatMsgImpl(component, true);
   }

   public StatisticsFile getPlayerStatsFile(EntityPlayer playerIn) {
      UUID uniqueID = playerIn.getUniqueID();
      StatisticsFile statisticsFile = uniqueID == null ? null : this.playerStatFiles.get(uniqueID);
      if (statisticsFile == null) {
         File file = new File(this.mcServer.worldServerForDimension(0).getSaveHandler().getWorldDirectory(), "stats");
         File file2 = new File(file, uniqueID.toString() + ".json");
         if (!file2.exists()) {
            File file3 = new File(file, playerIn.getName() + ".json");
            if (file3.exists() && file3.isFile()) {
               file3.renameTo(file2);
            }
         }

         statisticsFile = new StatisticsFile(this.mcServer, file2);
         statisticsFile.readStatFile();
         this.playerStatFiles.put(uniqueID, statisticsFile);
      }

      return statisticsFile;
   }

   public void setViewDistance(int distance) {
      this.viewDistance = distance;
      if (this.mcServer.worldServers != null) {
         for (WorldServer worldServer : this.mcServer.worldServers) {
            if (worldServer != null) {
               worldServer.getPlayerManager().setPlayerViewRadius(distance);
            }
         }
      }
   }

   public List<EntityPlayerMP> getPlayerList() {
      return this.playerEntityList;
   }

   public EntityPlayerMP getPlayerByUUID(UUID playerUUID) {
      return this.uuidToPlayerMap.get(playerUUID);
   }

   public boolean bypassesPlayerLimit(GameProfile gameProfile) {
      return false;
   }
}
