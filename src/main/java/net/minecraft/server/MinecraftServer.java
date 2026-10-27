package net.minecraft.server;

import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.Proxy;
import java.security.KeyPair;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.imageio.ImageIO;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.ServerStatusResponse.MinecraftProtocolVersionIdentifier;
import net.minecraft.network.ServerStatusResponse.PlayerCountData;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ITickable;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.util.Util;
import net.minecraft.util.Vec3;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer implements IThreadListener, ICommandSender, IPlayerUsage, Runnable {
   public static Logger logger = LogManager.getLogger();
   public static File USER_CACHE_FILE = new File("usercache.json");
   public static MinecraftServer mcServer;
   public PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("server", this, getCurrentTimeMillis());
   public List<ITickable> playersOnline = Lists.newArrayList();
   public Profiler theProfiler = new Profiler();
   public ServerStatusResponse statusResponse = new ServerStatusResponse();
   public Random random = new Random();
   public int serverPort = -1;
   public boolean serverRunning = true;
   public int maxPlayerIdleMinutes = 0;
   public long[] tickTimeArray = new long[100];
   public String resourcePackUrl = "";
   public WorldServer[] worldServers;
   public ServerConfigurationManager serverConfigManager;
   public String resourcePackHash = "";
   public boolean serverStopped;
   public int tickCounter;
   public long nanoTimeSinceStatusRefresh = 0L;
   public String currentTask;
   public int percentDone;
   public boolean onlineMode;
   public boolean canSpawnAnimals;
   public boolean canSpawnNPCs;
   public boolean pvpEnabled;
   public boolean allowFlight;
   public String motd;
   public int buildLimit;
   public Queue<FutureTask<?>> futureTaskQueue = Queues.newArrayDeque();
   public long currentTime = getCurrentTimeMillis();
   public long[][] timeOfLastDimensionTick;
   public KeyPair serverKeyPair;
   public String serverOwner;
   public String folderName;
   public String worldName;
   public boolean isDemo;
   public boolean enableBonusChest;
   public boolean worldIsBeingDeleted;
   public Proxy serverProxy;
   public File anvilFile;
   public boolean serverIsRunning;
   public long timeOfLastWarning;
   public String userMessage;
   public boolean startProfiling;
   public boolean isGamemodeForced;
   public NetworkSystem networkSystem;
   public PlayerProfileCache profileCache;
   public ICommandManager commandManager;
   public ISaveFormat anvilConverterForAnvilFile;
   public YggdrasilAuthenticationService authService;
   public MinecraftSessionService sessionService;
   public Thread serverThread;
   public GameProfileRepository profileRepo;

   public MinecraftServer(Proxy proxy, File workDir) {
      this.serverProxy = proxy;
      mcServer = this;
      this.anvilFile = null;
      this.networkSystem = null;
      this.profileCache = new PlayerProfileCache(this, workDir);
      this.commandManager = null;
      this.anvilConverterForAnvilFile = null;
      this.authService = new YggdrasilAuthenticationService(proxy, UUID.randomUUID().toString());
      this.sessionService = this.authService.createMinecraftSessionService();
      this.profileRepo = this.authService.createProfileRepository();
   }

   public MinecraftServer(File workDir, Proxy proxy, File profileCacheDir) {
      this.serverProxy = proxy;
      mcServer = this;
      this.anvilFile = workDir;
      this.networkSystem = new NetworkSystem(this);
      this.profileCache = new PlayerProfileCache(this, profileCacheDir);
      this.commandManager = this.createNewCommandManager();
      this.anvilConverterForAnvilFile = new AnvilSaveConverter(workDir);
      this.authService = new YggdrasilAuthenticationService(proxy, UUID.randomUUID().toString());
      this.sessionService = this.authService.createMinecraftSessionService();
      this.profileRepo = this.authService.createProfileRepository();
   }

   public ServerCommandManager createNewCommandManager() {
      return new ServerCommandManager();
   }

   public abstract boolean startServer() throws IOException;

   public void convertMapIfNeeded(String worldNameIn) {
      if (this.getActiveAnvilConverter().isOldMapFormat(worldNameIn)) {
         logger.info("Converting map!");
         this.setUserMessage("menu.convertingLevel");
         this.getActiveAnvilConverter().convertMapFormat(worldNameIn, new IProgressUpdate() {
            public long startTime = System.currentTimeMillis();

            @Override
            public void displaySavingString(String message) {
            }

            @Override
            public void resetProgressAndMessage(String message) {
            }

            @Override
            public void setLoadingProgress(int progress) {
               if (System.currentTimeMillis() - this.startTime >= 1000L) {
                  this.startTime = System.currentTimeMillis();
                  MinecraftServer.logger.info("Converting... " + progress + "%");
               }
            }

            @Override
            public void setDoneWorking() {
            }

            @Override
            public void displayLoadingString(String message) {
            }
         });
      }
   }

   public synchronized void setUserMessage(String message) {
      this.userMessage = message;
   }

   public synchronized String getUserMessage() {
      return this.userMessage;
   }

   public void loadAllWorlds(String saveName, String worldNameIn, long seed, WorldType type, String worldNameIn2) {
      this.convertMapIfNeeded(saveName);
      this.setUserMessage("menu.loadingLevel");
      this.worldServers = new WorldServer[3];
      this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
      ISaveHandler saveLoader = this.anvilConverterForAnvilFile.getSaveLoader(saveName, true);
      this.setResourcePackFromWorld(this.getFolderName(), saveLoader);
      WorldInfo loadWorldInfo = saveLoader.loadWorldInfo();
      WorldSettings worldSettings;
      if (loadWorldInfo == null) {
         if (this.isDemo()) {
            worldSettings = DemoWorldServer.demoWorldSettings;
         } else {
            worldSettings = new WorldSettings(seed, this.getGameType(), this.canStructuresSpawn(), this.isHardcore(), type);
            worldSettings.setWorldName(worldNameIn2);
            if (this.enableBonusChest) {
               worldSettings.enableBonusChest();
            }
         }

         loadWorldInfo = new WorldInfo(worldSettings, worldNameIn);
      } else {
         loadWorldInfo.setWorldName(worldNameIn);
         worldSettings = new WorldSettings(loadWorldInfo);
      }

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

            this.worldServers[i].initialize(worldSettings);
         } else {
            this.worldServers[i] = (WorldServer)new WorldServerMulti(this, saveLoader, b, this.worldServers[0], this.theProfiler).init();
         }

         this.worldServers[i].addWorldAccess(new WorldManager(this, this.worldServers[i]));
         if (!this.isSinglePlayer()) {
            this.worldServers[i].getWorldInfo().setGameType(this.getGameType());
         }
      }

      this.serverConfigManager.setPlayerManager(this.worldServers);
      this.setDifficultyForAllWorlds(this.getDifficulty());
      this.initialWorldChunkLoad();
   }

   public void initialWorldChunkLoad() {
      byte b = 16;
      byte b2 = 4;
      short s = 192;
      short s2 = 625;
      int n = 0;
      this.setUserMessage("menu.generatingTerrain");
      byte b3 = 0;
      logger.info("Preparing start region for level " + b3);
      WorldServer worldServer = this.worldServers[b3];
      BlockPos spawnPoint = worldServer.getSpawnPoint();
      long currentTimeMillis = getCurrentTimeMillis();

      for (int i = -192; i <= 192 && this.isServerRunning(); i += 16) {
         for (int j = -192; j <= 192 && this.isServerRunning(); j += 16) {
            long currentTimeMillis2 = getCurrentTimeMillis();
            if (currentTimeMillis2 - currentTimeMillis > 1000L) {
               this.outputPercentRemaining("Preparing spawn area", n * 100 / 625);
               currentTimeMillis = currentTimeMillis2;
            }

            n++;
            worldServer.theChunkProviderServer.loadChunk(spawnPoint.getX() + i >> 4, spawnPoint.getZ() + j >> 4);
         }
      }

      this.clearCurrentTask();
   }

   public void setResourcePackFromWorld(String worldNameIn, ISaveHandler saveHandlerIn) {
      File file = new File(saveHandlerIn.getWorldDirectory(), "resources.zip");
      if (file.isFile()) {
         this.setResourcePack("level://" + worldNameIn + "/" + file.getName(), "");
      }
   }

   public abstract boolean canStructuresSpawn();

   public abstract GameType getGameType();

   public abstract EnumDifficulty getDifficulty();

   public abstract boolean isHardcore();

   public abstract int getOpPermissionLevel();

   public abstract boolean shouldBroadcastRconToOps();

   public abstract boolean shouldBroadcastConsoleToOps();

   public void outputPercentRemaining(String message, int percent) {
      this.currentTask = message;
      this.percentDone = percent;
      logger.info(message + ": " + percent + "%");
   }

   public void clearCurrentTask() {
      this.currentTask = null;
      this.percentDone = 0;
   }

   public void saveAllWorlds(boolean dontLog) {
      if (!this.worldIsBeingDeleted) {
         for (WorldServer worldServer : this.worldServers) {
            if (worldServer != null) {
               if (!dontLog) {
                  logger.info("Saving chunks for level '" + worldServer.getWorldInfo().getWorldName() + "'/" + worldServer.provider.getDimensionName());
               }

               try {
                  worldServer.saveAllChunks(true, (IProgressUpdate)null);
               } catch (MinecraftException e) {
                  logger.warn(e.getMessage());
               }
            }
         }
      }
   }

   public void stopServer() {
      if (!this.worldIsBeingDeleted) {
         logger.info("Stopping server");
         if (this.getNetworkSystem() != null) {
            this.getNetworkSystem().terminateEndpoints();
         }

         if (this.serverConfigManager != null) {
            logger.info("Saving players");
            this.serverConfigManager.saveAllPlayerData();
            this.serverConfigManager.removeAllPlayers();
         }

         if (this.worldServers != null) {
            logger.info("Saving worlds");
            this.saveAllWorlds(false);

            for (int i = 0; i < this.worldServers.length; i++) {
               WorldServer worldServer = this.worldServers[i];
               worldServer.flush();
            }
         }

         if (this.usageSnooper.isSnooperRunning()) {
            this.usageSnooper.stopSnooper();
         }
      }
   }

   public boolean isServerRunning() {
      return this.serverRunning;
   }

   public void initiateShutdown() {
      this.serverRunning = false;
   }

   public void setInstance() {
      mcServer = this;
   }

   @Override
   public void run() {
      try {
         if (this.startServer()) {
            this.currentTime = getCurrentTimeMillis();
            long l = 0L;
            this.statusResponse.setServerDescription(new ChatComponentText(this.motd));
            this.statusResponse.setProtocolVersionInfo(new MinecraftProtocolVersionIdentifier("1.8.9", 47));
            this.addFaviconToStatusResponse(this.statusResponse);

            while (this.serverRunning) {
               long currentTimeMillis = getCurrentTimeMillis();
               long l2 = currentTimeMillis - this.currentTime;
               if (l2 > 2000L && this.currentTime - this.timeOfLastWarning >= 15000L) {
                  logger.warn(
                     "Can't keep up! Did the system time change, or is the server overloaded? Running {}ms behind, skipping {} tick(s)",
                     new Object[]{l2, l2 / 50L}
                  );
                  l2 = 2000L;
                  this.timeOfLastWarning = this.currentTime;
               }

               if (l2 < 0L) {
                  logger.warn("Time ran backwards! Did the system time change?");
                  l2 = 0L;
               }

               l += l2;
               this.currentTime = currentTimeMillis;
               if (this.worldServers[0].areAllPlayersAsleep()) {
                  this.tick();
                  l = 0L;
               } else {
                  while (l > 50L) {
                     l -= 50L;
                     this.tick();
                  }
               }

               Thread.sleep(Math.max(1L, 50L - l));
               this.serverIsRunning = true;
            }
         } else {
            this.finalTick((CrashReport)null);
         }
      } catch (Throwable e) {
         logger.error("Encountered an unexpected exception", e);
         CrashReport crashReport = null;
         if (e instanceof ReportedException) {
            crashReport = this.addServerInfoToCrashReport(((ReportedException)e).getCrashReport());
         } else {
            crashReport = this.addServerInfoToCrashReport(new CrashReport("Exception in server tick loop", e));
         }

         File file = new File(
            new File(this.getDataDirectory(), "crash-reports"), "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-server.txt"
         );
         if (crashReport.saveToFile(file)) {
            logger.error("This crash report has been saved to: " + file.getAbsolutePath());
         } else {
            logger.error("We were unable to save this crash report to disk.");
         }

         this.finalTick(crashReport);
      } finally {
         try {
            this.serverStopped = true;
            this.stopServer();
         } catch (Throwable e) {
            logger.error("Exception stopping the server", e);
         } finally {
            this.systemExitNow();
         }
      }
   }

   public void addFaviconToStatusResponse(ServerStatusResponse response) {
      File file = this.getFile("server-icon.png");
      if (file.isFile()) {
         ByteBuf buffer = Unpooled.buffer();

         try {
            BufferedImage read = ImageIO.read(file);
            Validate.validState(read.getWidth() == 64, "Must be 64 pixels wide", new Object[0]);
            Validate.validState(read.getHeight() == 64, "Must be 64 pixels high", new Object[0]);
            ImageIO.write(read, "PNG", new ByteBufOutputStream(buffer));
            ByteBuf encode = Base64.encode(buffer);
            response.setFavicon("data:image/png;base64," + encode.toString(Charsets.UTF_8));
         } catch (Exception e) {
            logger.error("Couldn't load server icon", e);
         } finally {
            buffer.release();
         }
      }
   }

   public File getDataDirectory() {
      return new File(".");
   }

   public void finalTick(CrashReport report) {
   }

   public void systemExitNow() {
   }

   public void tick() {
      long nanoTime = System.nanoTime();
      this.tickCounter++;
      if (this.startProfiling) {
         this.startProfiling = false;
         this.theProfiler.profilingEnabled = true;
         this.theProfiler.clearProfiling();
      }

      this.theProfiler.startSection("root");
      this.updateTimeLightAndEntities();
      if (nanoTime - this.nanoTimeSinceStatusRefresh >= 5000000000L) {
         this.nanoTimeSinceStatusRefresh = nanoTime;
         this.statusResponse.setPlayerCountData(new PlayerCountData(this.getMaxPlayers(), this.getCurrentPlayerCount()));
         GameProfile[] gameProfiles = new GameProfile[Math.min(this.getCurrentPlayerCount(), 12)];
         int randomIntegerInRange = MathHelper.getRandomIntegerInRange(this.random, 0, this.getCurrentPlayerCount() - gameProfiles.length);

         for (int i = 0; i < gameProfiles.length; i++) {
            gameProfiles[i] = this.serverConfigManager.getPlayerList().get(randomIntegerInRange + i).getGameProfile();
         }

         Collections.shuffle(Arrays.asList(gameProfiles));
         this.statusResponse.getPlayerCountData().setPlayers(gameProfiles);
      }

      if (this.tickCounter % 900 == 0) {
         this.theProfiler.startSection("save");
         this.serverConfigManager.saveAllPlayerData();
         this.saveAllWorlds(true);
         this.theProfiler.endSection();
      }

      this.theProfiler.startSection("tallying");
      this.tickTimeArray[this.tickCounter % 100] = System.nanoTime() - nanoTime;
      this.theProfiler.endSection();
      this.theProfiler.startSection("snooper");
      if (!this.usageSnooper.isSnooperRunning() && this.tickCounter > 100) {
         this.usageSnooper.startSnooper();
      }

      if (this.tickCounter % 6000 == 0) {
         this.usageSnooper.addMemoryStatsToSnooper();
      }

      this.theProfiler.endSection();
      this.theProfiler.endSection();
   }

   public void updateTimeLightAndEntities() {
      this.theProfiler.startSection("jobs");
      synchronized (this.futureTaskQueue) {
         while (!this.futureTaskQueue.isEmpty()) {
            Util.runTask(this.futureTaskQueue.poll(), logger);
         }
      }

      this.theProfiler.endStartSection("levels");

      for (int i = 0; i < this.worldServers.length; i++) {
         long nanoTime = System.nanoTime();
         if (i == 0 || this.getAllowNether()) {
            WorldServer worldServer = this.worldServers[i];
            this.theProfiler.startSection(worldServer.getWorldInfo().getWorldName());
            if (this.tickCounter % 20 == 0) {
               this.theProfiler.startSection("timeSync");
               this.serverConfigManager
                  .sendPacketToAllPlayersInDimension(
                     new S03PacketTimeUpdate(worldServer.getTotalWorldTime(), worldServer.getWorldTime(), worldServer.getGameRules().getBoolean("doDaylightCycle")),
                     worldServer.provider.getDimensionId()
                  );
               this.theProfiler.endSection();
            }

            this.theProfiler.startSection("tick");

            try {
               worldServer.tick();
            } catch (Throwable e) {
               CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Exception ticking world");
               worldServer.addWorldInfoToCrashReport(makeCrashReport);
               throw new ReportedException(makeCrashReport);
            }

            try {
               worldServer.updateEntities();
            } catch (Throwable e) {
               CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Exception ticking world entities");
               worldServer.addWorldInfoToCrashReport(makeCrashReport);
               throw new ReportedException(makeCrashReport);
            }

            this.theProfiler.endSection();
            this.theProfiler.startSection("tracker");
            worldServer.getEntityTracker().updateTrackedEntities();
            this.theProfiler.endSection();
            this.theProfiler.endSection();
         }

         this.timeOfLastDimensionTick[i][this.tickCounter % 100] = System.nanoTime() - nanoTime;
      }

      this.theProfiler.endStartSection("connection");
      this.getNetworkSystem().networkTick();
      this.theProfiler.endStartSection("players");
      this.serverConfigManager.onTick();
      this.theProfiler.endStartSection("tickables");

      for (int i = 0; i < this.playersOnline.size(); i++) {
         this.playersOnline.get(i).update();
      }

      this.theProfiler.endSection();
   }

   public boolean getAllowNether() {
      return true;
   }

   public void startServerThread() {
      this.serverThread = new Thread(this, "Server thread");
      this.serverThread.start();
   }

   public File getFile(String fileName) {
      return new File(this.getDataDirectory(), fileName);
   }

   public void logWarning(String msg) {
      logger.warn(msg);
   }

   public WorldServer worldServerForDimension(int dimension) {
      return dimension == -1 ? this.worldServers[1] : (dimension == 1 ? this.worldServers[2] : this.worldServers[0]);
   }

   public String getMinecraftVersion() {
      return "1.8.9";
   }

   public int getCurrentPlayerCount() {
      return this.serverConfigManager.getCurrentPlayerCount();
   }

   public int getMaxPlayers() {
      return this.serverConfigManager.getMaxPlayers();
   }

   public String[] getAllUsernames() {
      return this.serverConfigManager.getAllUsernames();
   }

   public GameProfile[] getGameProfiles() {
      return this.serverConfigManager.getAllProfiles();
   }

   public String getServerModName() {
      return "vanilla";
   }

   public CrashReport addServerInfoToCrashReport(CrashReport report) {
      report.getCategory().addCrashSectionCallable("Profiler Position", new Callable<String>() {
         public String call() throws Exception {
            return MinecraftServer.this.theProfiler.profilingEnabled ? MinecraftServer.this.theProfiler.getNameOfLastSection() : "N/A (disabled)";
         }
      });
      if (this.serverConfigManager != null) {
         report.getCategory()
            .addCrashSectionCallable(
               "Player Count",
               new Callable<String>() {
                  public String call() {
                     return MinecraftServer.this.serverConfigManager.getCurrentPlayerCount()
                        + " / "
                        + MinecraftServer.this.serverConfigManager.getMaxPlayers()
                        + "; "
                        + MinecraftServer.this.serverConfigManager.getPlayerList();
                  }
               }
            );
      }

      return report;
   }

   public List<String> getTabCompletions(ICommandSender sender, String input, BlockPos pos) {
      ArrayList newArrayList = Lists.newArrayList();
      if (input.startsWith("/")) {
         input = input.substring(1);
         boolean flag = !input.contains(" ");
         List tabCompletionOptions = this.commandManager.getTabCompletionOptions(sender, input, pos);
         if (tabCompletionOptions != null) {
            for (String str : (Iterable<String>)tabCompletionOptions) {
               if (flag) {
                  newArrayList.add("/" + str);
               } else {
                  newArrayList.add(str);
               }
            }
         }

         return newArrayList;
      } else {
         String[] split = input.split(" ", -1);
         String str = split[split.length - 1];

         for (String str2 : this.serverConfigManager.getAllUsernames()) {
            if (CommandBase.doesStringStartWith(str, str2)) {
               newArrayList.add(str2);
            }
         }

         return newArrayList;
      }
   }

   public static MinecraftServer getServer() {
      return mcServer;
   }

   public boolean isAnvilFileSet() {
      return this.anvilFile != null;
   }

   @Override
   public String getName() {
      return "Server";
   }

   @Override
   public void addChatMessage(IChatComponent component) {
      logger.info(component.getUnformattedText());
   }

   @Override
   public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
      return true;
   }

   public ICommandManager getCommandManager() {
      return this.commandManager;
   }

   public KeyPair getKeyPair() {
      return this.serverKeyPair;
   }

   public String getServerOwner() {
      return this.serverOwner;
   }

   public void setServerOwner(String owner) {
      this.serverOwner = owner;
   }

   public boolean isSinglePlayer() {
      return this.serverOwner != null;
   }

   public String getFolderName() {
      return this.folderName;
   }

   public void setFolderName(String name) {
      this.folderName = name;
   }

   public void setWorldName(String str) {
      this.worldName = str;
   }

   public String getWorldName() {
      return this.worldName;
   }

   public void setKeyPair(KeyPair keyPair) {
      this.serverKeyPair = keyPair;
   }

   public void setDifficultyForAllWorlds(EnumDifficulty difficulty) {
      for (int i = 0; i < this.worldServers.length; i++) {
         WorldServer worldServer = this.worldServers[i];
         if (worldServer != null) {
            if (worldServer.getWorldInfo().isHardcoreModeEnabled()) {
               worldServer.getWorldInfo().setDifficulty(EnumDifficulty.HARD);
               worldServer.setAllowedSpawnTypes(true, true);
            } else if (this.isSinglePlayer()) {
               worldServer.getWorldInfo().setDifficulty(difficulty);
               worldServer.setAllowedSpawnTypes(worldServer.getDifficulty() != EnumDifficulty.PEACEFUL, true);
            } else {
               worldServer.getWorldInfo().setDifficulty(difficulty);
               worldServer.setAllowedSpawnTypes(this.allowSpawnMonsters(), this.canSpawnAnimals);
            }
         }
      }
   }

   public boolean allowSpawnMonsters() {
      return true;
   }

   public boolean isDemo() {
      return this.isDemo;
   }

   public void setDemo(boolean demo) {
      this.isDemo = demo;
   }

   public void canCreateBonusChest(boolean enable) {
      this.enableBonusChest = enable;
   }

   public ISaveFormat getActiveAnvilConverter() {
      return this.anvilConverterForAnvilFile;
   }

   public void deleteWorldAndStopServer() {
      this.worldIsBeingDeleted = true;
      this.getActiveAnvilConverter().flushCache();

      for (int i = 0; i < this.worldServers.length; i++) {
         WorldServer worldServer = this.worldServers[i];
         if (worldServer != null) {
            worldServer.flush();
         }
      }

      this.getActiveAnvilConverter().deleteWorldDirectory(this.worldServers[0].getSaveHandler().getWorldDirectoryName());
      this.initiateShutdown();
   }

   public String getResourcePackUrl() {
      return this.resourcePackUrl;
   }

   public String getResourcePackHash() {
      return this.resourcePackHash;
   }

   public void setResourcePack(String url, String hash) {
      this.resourcePackUrl = url;
      this.resourcePackHash = hash;
   }

   @Override
   public void addServerStatsToSnooper(PlayerUsageSnooper playerSnooper) {
      playerSnooper.addClientStat("whitelist_enabled", false);
      playerSnooper.addClientStat("whitelist_count", 0);
      if (this.serverConfigManager != null) {
         playerSnooper.addClientStat("players_current", this.getCurrentPlayerCount());
         playerSnooper.addClientStat("players_max", this.getMaxPlayers());
         playerSnooper.addClientStat("players_seen", this.serverConfigManager.getAvailablePlayerDat().length);
      }

      playerSnooper.addClientStat("uses_auth", this.onlineMode);
      playerSnooper.addClientStat("gui_state", this.getGuiEnabled() ? "enabled" : "disabled");
      playerSnooper.addClientStat("run_time", (getCurrentTimeMillis() - playerSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L);
      playerSnooper.addClientStat("avg_tick_ms", (int)(MathHelper.average(this.tickTimeArray) * 1.0E-6));
      int n = 0;
      if (this.worldServers != null) {
         for (int i = 0; i < this.worldServers.length; i++) {
            if (this.worldServers[i] != null) {
               WorldServer worldServer = this.worldServers[i];
               WorldInfo worldInfo = worldServer.getWorldInfo();
               playerSnooper.addClientStat("world[" + n + "][dimension]", worldServer.provider.getDimensionId());
               playerSnooper.addClientStat("world[" + n + "][mode]", worldInfo.getGameType());
               playerSnooper.addClientStat("world[" + n + "][difficulty]", worldServer.getDifficulty());
               playerSnooper.addClientStat("world[" + n + "][hardcore]", worldInfo.isHardcoreModeEnabled());
               playerSnooper.addClientStat("world[" + n + "][generator_name]", worldInfo.getTerrainType().getWorldTypeName());
               playerSnooper.addClientStat("world[" + n + "][generator_version]", worldInfo.getTerrainType().getGeneratorVersion());
               playerSnooper.addClientStat("world[" + n + "][height]", this.buildLimit);
               playerSnooper.addClientStat("world[" + n + "][chunks_loaded]", worldServer.getChunkProvider().getLoadedChunkCount());
               n++;
            }
         }
      }

      playerSnooper.addClientStat("worlds", n);
   }

   @Override
   public void addServerTypeToSnooper(PlayerUsageSnooper playerSnooper) {
      playerSnooper.addStatToSnooper("singleplayer", this.isSinglePlayer());
      playerSnooper.addStatToSnooper("server_brand", this.getServerModName());
      playerSnooper.addStatToSnooper("gui_supported", GraphicsEnvironment.isHeadless() ? "headless" : "supported");
      playerSnooper.addStatToSnooper("dedicated", this.isDedicatedServer());
   }

   @Override
   public boolean isSnooperEnabled() {
      return true;
   }

   public abstract boolean isDedicatedServer();

   public boolean isServerInOnlineMode() {
      return this.onlineMode;
   }

   public void setOnlineMode(boolean online) {
      this.onlineMode = online;
   }

   public boolean getCanSpawnAnimals() {
      return this.canSpawnAnimals;
   }

   public void setCanSpawnAnimals(boolean spawnAnimals) {
      this.canSpawnAnimals = spawnAnimals;
   }

   public boolean getCanSpawnNPCs() {
      return this.canSpawnNPCs;
   }

   public abstract boolean shouldUseNativeTransport();

   public void setCanSpawnNPCs(boolean spawnNpcs) {
      this.canSpawnNPCs = spawnNpcs;
   }

   public boolean isPVPEnabled() {
      return this.pvpEnabled;
   }

   public void setAllowPvp(boolean allowPvp) {
      this.pvpEnabled = allowPvp;
   }

   public boolean isFlightAllowed() {
      return this.allowFlight;
   }

   public void setAllowFlight(boolean allow) {
      this.allowFlight = allow;
   }

   public abstract boolean isCommandBlockEnabled();

   public String getMOTD() {
      return this.motd;
   }

   public void setMOTD(String motdIn) {
      this.motd = motdIn;
   }

   public int getBuildLimit() {
      return this.buildLimit;
   }

   public void setBuildLimit(int maxBuildHeight) {
      this.buildLimit = maxBuildHeight;
   }

   public boolean isServerStopped() {
      return this.serverStopped;
   }

   public ServerConfigurationManager getConfigurationManager() {
      return this.serverConfigManager;
   }

   public void setConfigManager(ServerConfigurationManager configManager) {
      this.serverConfigManager = configManager;
   }

   public void setGameType(GameType gameMode) {
      for (int i = 0; i < this.worldServers.length; i++) {
         getServer().worldServers[i].getWorldInfo().setGameType(gameMode);
      }
   }

   public NetworkSystem getNetworkSystem() {
      return this.networkSystem;
   }

   public boolean serverIsInRunLoop() {
      return this.serverIsRunning;
   }

   public boolean getGuiEnabled() {
      return false;
   }

   public abstract String shareToLAN(GameType gameType, boolean flag);

   public int getTickCounter() {
      return this.tickCounter;
   }

   public void enableProfiling() {
      this.startProfiling = true;
   }

   public PlayerUsageSnooper getPlayerUsageSnooper() {
      return this.usageSnooper;
   }

   @Override
   public BlockPos getPosition() {
      return BlockPos.ORIGIN;
   }

   @Override
   public Vec3 getPositionVector() {
      return new Vec3(0.0, 0.0, 0.0);
   }

   @Override
   public World getEntityWorld() {
      return this.worldServers[0];
   }

   @Override
   public Entity getCommandSenderEntity() {
      return null;
   }

   public int getSpawnProtectionSize() {
      return 16;
   }

   public boolean isBlockProtected(World worldIn, BlockPos pos, EntityPlayer playerIn) {
      return false;
   }

   public boolean getForceGamemode() {
      return this.isGamemodeForced;
   }

   public Proxy getServerProxy() {
      return this.serverProxy;
   }

   public static long getCurrentTimeMillis() {
      return System.currentTimeMillis();
   }

   public int getMaxPlayerIdleMinutes() {
      return this.maxPlayerIdleMinutes;
   }

   public void setPlayerIdleTimeout(int idleTimeout) {
      this.maxPlayerIdleMinutes = idleTimeout;
   }

   @Override
   public IChatComponent getDisplayName() {
      return new ChatComponentText(this.getName());
   }

   public boolean isAnnouncingPlayerAchievements() {
      return true;
   }

   public MinecraftSessionService getMinecraftSessionService() {
      return this.sessionService;
   }

   public GameProfileRepository getGameProfileRepository() {
      return this.profileRepo;
   }

   public PlayerProfileCache getPlayerProfileCache() {
      return this.profileCache;
   }

   public ServerStatusResponse getServerStatusResponse() {
      return this.statusResponse;
   }

   public void refreshStatusNextTick() {
      this.nanoTimeSinceStatusRefresh = 0L;
   }

   public Entity getEntityFromUuid(UUID uuid) {
      for (WorldServer worldServer : this.worldServers) {
         if (worldServer != null) {
            Entity entityFromUuid = worldServer.getEntityFromUuid(uuid);
            if (entityFromUuid != null) {
               return entityFromUuid;
            }
         }
      }

      return null;
   }

   @Override
   public boolean sendCommandFeedback() {
      return getServer().worldServers[0].getGameRules().getBoolean("sendCommandFeedback");
   }

   @Override
   public void setCommandStat(Type type, int amount) {
   }

   public int getMaxWorldSize() {
      return 29999984;
   }

   public <V> ListenableFuture<V> callFromMainThread(Callable<V> callable) {
      Validate.notNull(callable);
      if (!this.isCallingFromMinecraftThread() && !this.isServerStopped()) {
         ListenableFutureTask listenableFutureTask = ListenableFutureTask.create(callable);
         synchronized (this.futureTaskQueue) {
            this.futureTaskQueue.add(listenableFutureTask);
            return listenableFutureTask;
         }
      } else {
         try {
            return Futures.immediateFuture(callable.call());
         } catch (Exception e) {
            return Futures.immediateFailedFuture(e);
         }
      }
   }

   @Override
   public ListenableFuture<Object> addScheduledTask(Runnable runnableToSchedule) {
      Validate.notNull(runnableToSchedule);
      return this.callFromMainThread(Executors.callable(runnableToSchedule));
   }

   @Override
   public boolean isCallingFromMinecraftThread() {
      return Thread.currentThread() == this.serverThread;
   }

   public int getNetworkCompressionTreshold() {
      return 256;
   }
}
