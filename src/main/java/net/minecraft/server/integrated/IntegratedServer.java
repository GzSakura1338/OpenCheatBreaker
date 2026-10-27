package net.minecraft.server.integrated;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.Futures;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.concurrent.Callable;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketThreadUtil;
import net.minecraft.network.play.server.S2BPacketChangeGameState;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.CryptManager;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.Util;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.optifine.ClearWater;
import net.optifine.Config;
import net.optifine.reflect.Reflector;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IntegratedServer extends MinecraftServer {
   public static Logger logger = LogManager.getLogger();
   public long ticksSaveLast = 0L;
   public World difficultyUpdateWorld = null;
   public boolean isGamePaused;
   public boolean isPublic;
   public ThreadLanServerPing lanServerPing;
   public BlockPos difficultyUpdatePos = null;
   public DifficultyInstance difficultyLast = null;
   public Minecraft mc;
   public WorldSettings theWorldSettings;

   public IntegratedServer(Minecraft mcIn) {
      super(mcIn.getProxy(), new File(mcIn.mcDataDir, USER_CACHE_FILE.getName()));
      this.mc = mcIn;
      this.theWorldSettings = null;
   }

   public IntegratedServer(Minecraft mcIn, String folderName, String worldName, WorldSettings settings) {
      super(new File(mcIn.mcDataDir, "saves"), mcIn.getProxy(), new File(mcIn.mcDataDir, USER_CACHE_FILE.getName()));
      this.setServerOwner(mcIn.getSession().getUsername());
      this.setFolderName(folderName);
      this.setWorldName(worldName);
      this.setDemo(mcIn.isDemo());
      this.canCreateBonusChest(settings.isBonusChestEnabled());
      this.setBuildLimit(256);
      this.setConfigManager(new IntegratedPlayerList(this));
      this.mc = mcIn;
      this.theWorldSettings = this.isDemo() ? DemoWorldServer.demoWorldSettings : settings;
      ISaveHandler saveLoader = this.getActiveAnvilConverter().getSaveLoader(folderName, false);
      WorldInfo loadWorldInfo = saveLoader.loadWorldInfo();
      if (loadWorldInfo != null) {
         NBTTagCompound playerNBTTagCompound = loadWorldInfo.getPlayerNBTTagCompound();
         if (playerNBTTagCompound != null && playerNBTTagCompound.hasKey("Dimension")) {
            int integer = playerNBTTagCompound.getInteger("Dimension");
            PacketThreadUtil.lastDimensionId = integer;
            this.mc.loadingScreen.setLoadingProgress(-1);
         }
      }
   }

   @Override
   public ServerCommandManager createNewCommandManager() {
      return new IntegratedServerCommandManager();
   }

   @Override
   public void loadAllWorlds(String saveName, String worldNameIn, long seed, WorldType type, String worldNameIn2) {
      this.convertMapIfNeeded(saveName);
      boolean exists = Reflector.DimensionManager.exists();
      if (!exists) {
         this.worldServers = new WorldServer[3];
         this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
      }

      ISaveHandler saveLoader = this.getActiveAnvilConverter().getSaveLoader(saveName, true);
      this.setResourcePackFromWorld(this.getFolderName(), saveLoader);
      WorldInfo loadWorldInfo = saveLoader.loadWorldInfo();
      if (loadWorldInfo == null) {
         loadWorldInfo = new WorldInfo(this.theWorldSettings, worldNameIn);
      } else {
         loadWorldInfo.setWorldName(worldNameIn);
      }

      if (exists) {
         WorldServer worldServer = this.isDemo()
            ? (WorldServer)new DemoWorldServer(this, saveLoader, loadWorldInfo, 0, this.theProfiler).init()
            : (WorldServer)new WorldServer(this, saveLoader, loadWorldInfo, 0, this.theProfiler).init();
         worldServer.initialize(this.theWorldSettings);
         Integer[] integers = (Integer[])Reflector.call(Reflector.DimensionManager_getStaticDimensionIDs);
         Integer[] integers2 = integers;
         int length = integers.length;

         for (int i = 0; i < length; i++) {
            int n = integers2[i];
            WorldServer worldServer2 = n == 0 ? worldServer : (WorldServer)new WorldServerMulti(this, saveLoader, n, worldServer, this.theProfiler).init();
            worldServer2.addWorldAccess(new WorldManager(this, worldServer2));
            if (!this.isSinglePlayer()) {
               worldServer2.getWorldInfo().setGameType(this.getGameType());
            }

            if (Reflector.EventBus.exists()) {
               Reflector.postForgeBusEvent(Reflector.WorldEvent_Load_Constructor, worldServer2);
            }
         }

         this.getConfigurationManager().setPlayerManager(new WorldServer[]{worldServer});
         if (worldServer.getWorldInfo().getDifficulty() == null) {
            this.setDifficultyForAllWorlds(this.mc.gameSettings.difficulty);
         }
      } else {
         for (int i = 0; i < this.worldServers.length; i++) {
            byte b = 0;
            if (i == 1) {
               b = -1;
            }

            if (i == 2) {
               b = 1;
            }

            if (i == 0) {
               if (this.isDemo()) {
                  this.worldServers[i] = (WorldServer)new DemoWorldServer(this, saveLoader, loadWorldInfo, b, this.theProfiler).init();
               } else {
                  this.worldServers[i] = (WorldServer)new WorldServer(this, saveLoader, loadWorldInfo, b, this.theProfiler).init();
               }

               this.worldServers[i].initialize(this.theWorldSettings);
            } else {
               this.worldServers[i] = (WorldServer)new WorldServerMulti(this, saveLoader, b, this.worldServers[0], this.theProfiler).init();
            }

            this.worldServers[i].addWorldAccess(new WorldManager(this, this.worldServers[i]));
         }

         this.getConfigurationManager().setPlayerManager(this.worldServers);
         if (this.worldServers[0].getWorldInfo().getDifficulty() == null) {
            this.setDifficultyForAllWorlds(this.mc.gameSettings.difficulty);
         }
      }

      this.initialWorldChunkLoad();
   }

   @Override
   public boolean startServer() throws IOException {
      logger.info("Starting integrated minecraft server version 1.9");
      this.setOnlineMode(true);
      this.setCanSpawnAnimals(true);
      this.setCanSpawnNPCs(true);
      this.setAllowPvp(true);
      this.setAllowFlight(true);
      logger.info("Generating keypair");
      this.setKeyPair(CryptManager.generateKeyPair());
      if (Reflector.FMLCommonHandler_handleServerAboutToStart.exists()) {
         Object obj = Reflector.call(Reflector.FMLCommonHandler_instance);
         if (!Reflector.callBoolean(obj, Reflector.FMLCommonHandler_handleServerAboutToStart, this)) {
            return false;
         }
      }

      this.loadAllWorlds(
         this.getFolderName(),
         this.getWorldName(),
         this.theWorldSettings.getSeed(),
         this.theWorldSettings.getTerrainType(),
         this.theWorldSettings.getWorldName()
      );
      this.setMOTD(this.getServerOwner() + " - " + this.worldServers[0].getWorldInfo().getWorldName());
      if (Reflector.FMLCommonHandler_handleServerStarting.exists()) {
         Object obj = Reflector.call(Reflector.FMLCommonHandler_instance);
         if (Reflector.FMLCommonHandler_handleServerStarting.getReturnType() == boolean.class) {
            return Reflector.callBoolean(obj, Reflector.FMLCommonHandler_handleServerStarting, this);
         }

         Reflector.callVoid(obj, Reflector.FMLCommonHandler_handleServerStarting, this);
      }

      return true;
   }

   @Override
   public void tick() {
      this.onTick();
      boolean isGamePaused = this.isGamePaused;
      this.isGamePaused = Minecraft.getMinecraft().getNetHandler() != null && Minecraft.getMinecraft().isGamePaused();
      if (!isGamePaused && this.isGamePaused) {
         logger.info("Saving and pausing game...");
         this.getConfigurationManager().saveAllPlayerData();
         this.saveAllWorlds(false);
      }

      if (this.isGamePaused) {
         synchronized (this.futureTaskQueue) {
            while (!this.futureTaskQueue.isEmpty()) {
               Util.runTask(this.futureTaskQueue.poll(), logger);
            }
         }
      } else {
         super.tick();
         if (this.mc.gameSettings.renderDistanceChunks != this.getConfigurationManager().getViewDistance()) {
            logger.info(
               "Changing view distance to {}, from {}",
               new Object[]{this.mc.gameSettings.renderDistanceChunks, this.getConfigurationManager().getViewDistance()}
            );
            this.getConfigurationManager().setViewDistance(this.mc.gameSettings.renderDistanceChunks);
         }

         if (this.mc.theWorld != null) {
            WorldInfo worldInfo = this.worldServers[0].getWorldInfo();
            WorldInfo worldInfo2 = this.mc.theWorld.getWorldInfo();
            if (!worldInfo.isDifficultyLocked() && worldInfo2.getDifficulty() != worldInfo.getDifficulty()) {
               logger.info("Changing difficulty to {}, from {}", new Object[]{worldInfo2.getDifficulty(), worldInfo.getDifficulty()});
               this.setDifficultyForAllWorlds(worldInfo2.getDifficulty());
            } else if (worldInfo2.isDifficultyLocked() && !worldInfo.isDifficultyLocked()) {
               logger.info("Locking difficulty to {}", new Object[]{worldInfo2.getDifficulty()});

               for (WorldServer worldServer : this.worldServers) {
                  if (worldServer != null) {
                     worldServer.getWorldInfo().setDifficultyLocked(true);
                  }
               }
            }
         }
      }
   }

   @Override
   public boolean canStructuresSpawn() {
      return false;
   }

   @Override
   public GameType getGameType() {
      return this.theWorldSettings.getGameType();
   }

   @Override
   public EnumDifficulty getDifficulty() {
      return this.mc.theWorld == null ? this.mc.gameSettings.difficulty : this.mc.theWorld.getWorldInfo().getDifficulty();
   }

   @Override
   public boolean isHardcore() {
      return this.theWorldSettings.getHardcoreEnabled();
   }

   @Override
   public boolean shouldBroadcastRconToOps() {
      return true;
   }

   @Override
   public boolean shouldBroadcastConsoleToOps() {
      return true;
   }

   @Override
   public void saveAllWorlds(boolean flag) {
      if (flag) {
         int tickCounter = this.getTickCounter();
         int ofAutoSaveTicks = this.mc.gameSettings.ofAutoSaveTicks;
         if (tickCounter < this.ticksSaveLast + ofAutoSaveTicks) {
            return;
         }

         this.ticksSaveLast = tickCounter;
      }

      super.saveAllWorlds(flag);
   }

   @Override
   public File getDataDirectory() {
      return this.mc.mcDataDir;
   }

   @Override
   public boolean isDedicatedServer() {
      return false;
   }

   @Override
   public boolean shouldUseNativeTransport() {
      return false;
   }

   @Override
   public void finalTick(CrashReport report) {
      this.mc.crashed(report);
   }

   @Override
   public CrashReport addServerInfoToCrashReport(CrashReport report) {
      report = super.addServerInfoToCrashReport(report);
      report.getCategory().addCrashSectionCallable("Type", new Callable<String>() {
         public String call() throws Exception {
            return "Integrated Server (map_client.txt)";
         }
      });
      report.getCategory()
         .addCrashSectionCallable(
            "Is Modded",
            new Callable<String>() {
               public String call() throws Exception {
                  String clientModName = ClientBrandRetriever.getClientModName();
                  if (!clientModName.equals("vanilla")) {
                     return "Definitely; Client brand changed to '" + clientModName + "'";
                  }

                  clientModName = IntegratedServer.this.getServerModName();
                  return !clientModName.equals("vanilla")
                     ? "Definitely; Server brand changed to '" + clientModName + "'"
                     : (
                        Minecraft.class.getSigners() == null
                           ? "Very likely; Jar signature invalidated"
                           : "Probably not. Jar signature remains and both client + server brands are untouched."
                     );
               }
            }
         );
      return report;
   }

   @Override
   public void setDifficultyForAllWorlds(EnumDifficulty difficulty) {
      super.setDifficultyForAllWorlds(difficulty);
      if (this.mc.theWorld != null) {
         this.mc.theWorld.getWorldInfo().setDifficulty(difficulty);
      }
   }

   @Override
   public void addServerStatsToSnooper(PlayerUsageSnooper playerSnooper) {
      super.addServerStatsToSnooper(playerSnooper);
      playerSnooper.addClientStat("snooper_partner", this.mc.getPlayerUsageSnooper().getUniqueID());
   }

   @Override
   public boolean isSnooperEnabled() {
      return Minecraft.getMinecraft().isSnooperEnabled();
   }

   @Override
   public String shareToLAN(GameType type, boolean allowCheats) {
      try {
         int n = -1;

         try {
            n = HttpUtil.getSuitableLanPort();
         } catch (IOException e) {
         }

         if (n <= 0) {
            n = 25564;
         }

         this.getNetworkSystem().addLanEndpoint((InetAddress)null, n);
         logger.info("Started on " + n);
         this.isPublic = true;
         this.lanServerPing = new ThreadLanServerPing(this.getMOTD(), n + "");
         this.lanServerPing.start();
         this.getConfigurationManager().setGameType(type);
         this.getConfigurationManager().setCommandsAllowedForAll(allowCheats);
         return n + "";
      } catch (IOException e) {
         return null;
      }
   }

   @Override
   public void stopServer() {
      super.stopServer();
      if (this.lanServerPing != null) {
         this.lanServerPing.interrupt();
         this.lanServerPing = null;
      }
   }

   @Override
   public void initiateShutdown() {
      if (!Reflector.MinecraftForge.exists() || this.isServerRunning()) {
         Futures.getUnchecked(this.addScheduledTask(new Runnable() {
            @Override
            public void run() {
               for (EntityPlayerMP entityPlayerMP : Lists.newArrayList(IntegratedServer.this.getConfigurationManager().getPlayerList())) {
                  IntegratedServer.this.getConfigurationManager().playerLoggedOut(entityPlayerMP);
               }
            }
         }));
      }

      super.initiateShutdown();
      if (this.lanServerPing != null) {
         this.lanServerPing.interrupt();
         this.lanServerPing = null;
      }
   }

   public void setStaticInstance() {
      this.setInstance();
   }

   public boolean getPublic() {
      return this.isPublic;
   }

   @Override
   public void setGameType(GameType gameMode) {
      this.getConfigurationManager().setGameType(gameMode);
   }

   @Override
   public boolean isCommandBlockEnabled() {
      return true;
   }

   @Override
   public int getOpPermissionLevel() {
      return 4;
   }

   public void onTick() {
      for (WorldServer worldServer : Arrays.asList(this.worldServers)) {
         this.onTick(worldServer);
      }
   }

   public DifficultyInstance getDifficultyAsync(World world, BlockPos blockPos) {
      this.difficultyUpdateWorld = world;
      this.difficultyUpdatePos = blockPos;
      return this.difficultyLast;
   }

   public void onTick(WorldServer worldServer) {
      if (!Config.isTimeDefault()) {
         this.fixWorldTime(worldServer);
      }

      if (!Config.isWeatherEnabled()) {
         this.fixWorldWeather(worldServer);
      }

      if (Config.waterOpacityChanged) {
         Config.waterOpacityChanged = false;
         ClearWater.updateWaterOpacity(Config.getGameSettings(), worldServer);
      }

      if (this.difficultyUpdateWorld == worldServer && this.difficultyUpdatePos != null) {
         this.difficultyLast = worldServer.getDifficultyForLocation(this.difficultyUpdatePos);
         this.difficultyUpdateWorld = null;
         this.difficultyUpdatePos = null;
      }
   }

   public void fixWorldWeather(WorldServer worldServer) {
      WorldInfo worldInfo = worldServer.getWorldInfo();
      if (worldInfo.isRaining() || worldInfo.isThundering()) {
         worldInfo.setRainTime(0);
         worldInfo.setRaining(false);
         worldServer.setRainStrength(0.0F);
         worldInfo.setThunderTime(0);
         worldInfo.setThundering(false);
         worldServer.setThunderStrength(0.0F);
         this.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(2, 0.0F));
         this.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(7, 0.0F));
         this.getConfigurationManager().sendPacketToAllPlayers(new S2BPacketChangeGameState(8, 0.0F));
      }
   }

   public void fixWorldTime(WorldServer worldServer) {
      WorldInfo worldInfo = worldServer.getWorldInfo();
      if (worldInfo.getGameType().getID() == 1) {
         long worldTime = worldServer.getWorldTime();
         long l = worldTime % 24000L;
         if (Config.isTimeDayOnly()) {
            if (l <= 1000L) {
               worldServer.setWorldTime(worldTime - l + 1001L);
            }

            if (l >= 11000L) {
               worldServer.setWorldTime(worldTime - l + 24001L);
            }
         }

         if (Config.isTimeNightOnly()) {
            if (l <= 14000L) {
               worldServer.setWorldTime(worldTime - l + 14001L);
            }

            if (l >= 22000L) {
               worldServer.setWorldTime(worldTime - l + 24000L + 14001L);
            }
         }
      }
   }
}
