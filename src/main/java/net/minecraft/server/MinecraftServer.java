package net.minecraft.server;

import com.google.common.base.Charsets;
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
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import javax.imageio.ImageIO;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.crash.CrashReport;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetworkSystem;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.ServerStatusResponse.MinecraftProtocolVersionIdentifier;
import net.minecraft.network.ServerStatusResponse.PlayerCountData;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.server.management.ServerConfigurationManager;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ReportedException;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldServerMulti;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class MinecraftServer implements IPlayerUsage, ICommandSender, Runnable {
    public static Logger logger = LogManager.getLogger();
    public static File USER_CACHE_FILE = new File("usercache.json");
    public static MinecraftServer mcServer;
    public PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("server", this, getCurrentTimeMillis());
    public List playersOnline = new ArrayList();
    public Profiler theProfiler = new Profiler();
    public ServerStatusResponse statusResponse = new ServerStatusResponse();
    public Random random = new Random();
    public final int serverPort = -1;
    public boolean serverRunning = true;
    public int maxPlayerIdleMinutes = 0;
    public long[] tickTimeArray = new long[100];
    public final String serverTexturePack = "";
    public WorldServer[] worldServers;
    public ServerConfigurationManager serverConfigManager;
    public long nanoTimeSinceStatusRefresh = 0L;
    public boolean serverStopped;
    public int tickCounter;
    public PlayerProfileCache profileCache = new PlayerProfileCache(this, USER_CACHE_FILE);
    public String currentTask;
    public int percentDone;
    public boolean onlineMode;
    public boolean canSpawnAnimals;
    public boolean canSpawnNPCs;
    public boolean pvpEnabled;
    public boolean allowFlight;
    public String motd;
    public int buildLimit;
    public Proxy serverProxy;
    public File anvilFile;
    public long[][] timeOfLastDimensionTick;
    public KeyPair serverKeyPair;
    public String serverOwner;
    public String folderName;
    public String worldName;
    public boolean isDemo;
    public boolean enableBonusChest;
    public boolean worldIsBeingDeleted;
    public NetworkSystem networkSystem;
    public boolean serverIsRunning;
    public long timeOfLastWarning;
    public String userMessage;
    public boolean startProfiling;
    public boolean isGamemodeForced;
    public ICommandManager commandManager;
    public ISaveFormat anvilConverterForAnvilFile;
    public YggdrasilAuthenticationService authService;
    public MinecraftSessionService sessionService;
    public GameProfileRepository profileRepo;

    public MinecraftServer(File workDir, Proxy proxy) {
        mcServer = this;
        this.serverProxy = proxy;
        this.anvilFile = workDir;
        this.networkSystem = new NetworkSystem(this);
        this.commandManager = new ServerCommandManager();
        this.anvilConverterForAnvilFile = new AnvilSaveConverter(workDir);
        this.authService = new YggdrasilAuthenticationService(proxy, UUID.randomUUID().toString());
        this.sessionService = this.authService.createMinecraftSessionService();
        this.profileRepo = this.authService.createProfileRepository();
    }

    public abstract boolean startServer() throws IOException;

    public void convertMapIfNeeded(String str) throws IOException {
        if (this.getActiveAnvilConverter().isOldMapFormat(str)) {
            logger.info("Converting map!");
            this.setUserMessage("menu.convertingLevel");
            this.getActiveAnvilConverter().convertMapFormat(str, new IProgressUpdate() {
                public long longValue = System.currentTimeMillis();

                @Override
                public void displaySavingString(String str2) {
                }

                @Override
                public void resetProgressAndMessage(String str2) {
                }

                @Override
                public void setLoadingProgress(int n) {
                    if (System.currentTimeMillis() - this.longValue >= 1000L) {
                        this.longValue = System.currentTimeMillis();
                        MinecraftServer.logger.info("Converting... " + n + "%");
                    }
                }

                @Override
                public void setDoneWorking() {
                }

                @Override
                public void displayLoadingString(String str2) {
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

    public void loadAllWorlds(String str, String str2, long l, WorldType worldType, String str3) throws IOException {
        this.convertMapIfNeeded(str);
        this.setUserMessage("menu.loadingLevel");
        this.worldServers = new WorldServer[3];
        this.timeOfLastDimensionTick = new long[this.worldServers.length][100];
        ISaveHandler saveLoader = this.anvilConverterForAnvilFile.getSaveLoader(str, true);
        WorldInfo loadWorldInfo = saveLoader.loadWorldInfo();
        WorldSettings worldSettings;
        if (loadWorldInfo == null) {
            worldSettings = new WorldSettings(l, this.getGameType(), this.canStructuresSpawn(), this.isHardcore(), worldType);
            worldSettings.setWorldName(str3);
        } else {
            worldSettings = new WorldSettings(loadWorldInfo);
        }

        if (this.enableBonusChest) {
            worldSettings.enableBonusChest();
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
                    this.worldServers[i] = new DemoWorldServer(this, saveLoader, str2, b, this.theProfiler);
                } else {
                    this.worldServers[i] = new WorldServer(this, saveLoader, str2, b, worldSettings, this.theProfiler);
                }
            } else {
                this.worldServers[i] = new WorldServerMulti(this, saveLoader, str2, b, worldSettings, this.worldServers[0], this.theProfiler);
            }

            this.worldServers[i].addWorldAccess(new WorldManager(this, this.worldServers[i]));
            if (!this.isSinglePlayer()) {
                this.worldServers[i].getWorldInfo().setGameType(this.getGameType());
            }

            this.serverConfigManager.setPlayerManager(this.worldServers);
        }

        this.setDifficultyForAllWorlds(this.getDifficulty());
        this.initialWorldChunkLoad();
    }

    public void initialWorldChunkLoad() {
        boolean flag = true;
        boolean flag2 = true;
        boolean flag3 = true;
        boolean flag4 = true;
        int n = 0;
        this.setUserMessage("menu.generatingTerrain");
        byte b = 0;
        logger.info("Preparing start region for level " + b);
        WorldServer worldServer = this.worldServers[b];
        ChunkCoordinates spawnPoint = worldServer.getSpawnPoint();
        long currentTimeMillis = getCurrentTimeMillis();

        for (int i = -192; i <= 192 && this.isServerRunning(); i += 16) {
            for (int j = -192; j <= 192 && this.isServerRunning(); j += 16) {
                long currentTimeMillis2 = getCurrentTimeMillis();
                if (currentTimeMillis2 - currentTimeMillis > 1000L) {
                    this.outputPercentRemaining("Preparing spawn area", n * 100 / 625);
                    currentTimeMillis = currentTimeMillis2;
                }

                n++;
                worldServer.theChunkProviderServer.loadChunk(spawnPoint.posX + i >> 4, spawnPoint.posZ + j >> 4);
            }
        }

        this.clearCurrentTask();
    }

    public abstract boolean canStructuresSpawn();

    public abstract GameType getGameType();

    public abstract EnumDifficulty getDifficulty();

    public abstract boolean isHardcore();

    public abstract int getOpPermissionLevel();

    public abstract boolean shouldBroadcastRconToOps();

    public void outputPercentRemaining(String message, int percent) {
        this.currentTask = message;
        this.percentDone = percent;
        logger.info(message + ": " + percent + "%");
    }

    public void clearCurrentTask() {
        this.currentTask = null;
        this.percentDone = 0;
    }

    public void saveAllWorlds(boolean dontLog) throws IOException {
        if (!this.worldIsBeingDeleted) {
            for (WorldServer worldServer : this.worldServers) {
                if (worldServer != null) {
                    if (!dontLog) {
                        logger.info("Saving chunks for level '" + worldServer.getWorldInfo().getWorldName() + "'/" + worldServer.provider.getDimensionName());
                    }

                    try {
                        worldServer.saveAllChunks(true, null);
                    } catch (MinecraftException e) {
                        logger.warn(e.getMessage());
                    }
                }
            }
        }
    }

    public void stopServer() throws IOException {
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

    @Override
    public void run() {
        try {
            if (this.startServer()) {
                long currentTimeMillis = getCurrentTimeMillis();
                long l = 0L;
                this.statusResponse.setServerDescription(new ChatComponentText(this.motd));
                this.statusResponse.setProtocolVersionInfo(new MinecraftProtocolVersionIdentifier("1.7.10", 5));
                this.addFaviconToStatusResponse(this.statusResponse);

                while (this.serverRunning) {
                    long currentTimeMillis2 = getCurrentTimeMillis();
                    long l2 = currentTimeMillis2 - currentTimeMillis;
                    if (l2 > 2000L && currentTimeMillis - this.timeOfLastWarning >= 15000L) {
                        logger.warn(
                            "Can't keep up! Did the system time change, or is the server overloaded? Running {}ms behind, skipping {} tick(s)", l2, l2 / 50L
                        );
                        l2 = 2000L;
                        this.timeOfLastWarning = currentTimeMillis;
                    }

                    if (l2 < 0L) {
                        logger.warn("Time ran backwards! Did the system time change?");
                        l2 = 0L;
                    }

                    l += l2;
                    currentTimeMillis = currentTimeMillis2;
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
                this.finalTick(null);
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
                this.stopServer();
                this.serverStopped = true;
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

    public void tick() throws IOException {
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
                gameProfiles[i] = ((EntityPlayerMP)this.serverConfigManager.playerEntityList.get(randomIntegerInRange + i)).getGameProfile();
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

    public void updateTimeLightAndEntities() throws IOException {
        this.theProfiler.startSection("levels");

        for (int i = 0; i < this.worldServers.length; i++) {
            long nanoTime = System.nanoTime();
            if (i == 0 || this.getAllowNether()) {
                WorldServer worldServer = this.worldServers[i];
                this.theProfiler.startSection(worldServer.getWorldInfo().getWorldName());
                this.theProfiler.startSection("pools");
                this.theProfiler.endSection();
                if (this.tickCounter % 20 == 0) {
                    this.theProfiler.startSection("timeSync");
                    this.serverConfigManager
                        .sendPacketToAllPlayersInDimension(
                            new S03PacketTimeUpdate(worldServer.getTotalWorldTime(), worldServer.getWorldTime(), worldServer.getGameRules().getGameRuleBooleanValue("doDaylightCycle")),
                            worldServer.provider.dimensionId
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
            ((IUpdatePlayerListBox)this.playersOnline.get(i)).update();
        }

        this.theProfiler.endSection();
    }

    public boolean getAllowNether() {
        return true;
    }

    public void startServerThread() {
        (new Thread("Server thread") {
            @Override
            public void run() {
                MinecraftServer.this.run();
            }
        }).start();
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
        return "1.7.10";
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
        report.getCategory().addCrashSectionCallable("Profiler Position", new Callable() {
            public String call() {
                return MinecraftServer.this.theProfiler.profilingEnabled ? MinecraftServer.this.theProfiler.getNameOfLastSection() : "N/A (disabled)";
            }
        });
        if (this.worldServers != null && this.worldServers.length > 0 && this.worldServers[0] != null) {
            report.getCategory().addCrashSectionCallable("Vec3 Pool Size", new Callable() {
                public String call() {
                    byte b = 0;
                    int n = 56 * b;
                    int n2 = n / 1024 / 1024;
                    byte b2 = 0;
                    int n3 = 56 * b2;
                    int n4 = n3 / 1024 / 1024;
                    return b + " (" + n + " bytes; " + n2 + " MB) allocated, " + b2 + " (" + n3 + " bytes; " + n4 + " MB) used";
                }
            });
        }

        if (this.serverConfigManager != null) {
            report.getCategory()
                .addCrashSectionCallable(
                    "Player Count",
                    new Callable() {
                        public String call() {
                            return MinecraftServer.this.serverConfigManager.getCurrentPlayerCount()
                                + " / "
                                + MinecraftServer.this.serverConfigManager.getMaxPlayers()
                                + "; "
                                + MinecraftServer.this.serverConfigManager.playerEntityList;
                        }
                    }
                );
        }

        return report;
    }

    public List getPossibleCompletions(ICommandSender sender, String input) {
        ArrayList list = new ArrayList();
        if (input.startsWith("/")) {
            input = input.substring(1);
            boolean flag = !input.contains(" ");
            List possibleCommands = this.commandManager.getPossibleCommands(sender, input);
            if (possibleCommands != null) {
                for (String str : (Iterable<String>)possibleCommands) {
                    if (flag) {
                        list.add("/" + str);
                    } else {
                        list.add(str);
                    }
                }
            }

            return list;
        } else {
            String[] split = input.split(" ", -1);
            String str = split[split.length - 1];

            for (String str2 : this.serverConfigManager.getAllUsernames()) {
                if (CommandBase.doesStringStartWith(str, str2)) {
                    list.add(str2);
                }
            }

            return list;
        }
    }

    public static MinecraftServer getServer() {
        return mcServer;
    }

    @Override
    public String getCommandSenderName() {
        return "Server";
    }

    @Override
    public void addChatMessage(IChatComponent message) {
        logger.info(message.getUnformattedText());
    }

    @Override
    public boolean canCommandSenderUseCommand(int permissionLevel, String command) {
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
                    worldServer.difficultySetting = EnumDifficulty.HARD;
                    worldServer.setAllowedSpawnTypes(true, true);
                } else if (this.isSinglePlayer()) {
                    worldServer.difficultySetting = difficulty;
                    worldServer.setAllowedSpawnTypes(worldServer.difficultySetting != EnumDifficulty.PEACEFUL, true);
                } else {
                    worldServer.difficultySetting = difficulty;
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

    public String getTexturePack() {
        return "";
    }

    @Override
    public void addServerStatsToSnooper(PlayerUsageSnooper playerSnooper) {
        playerSnooper.addClientStat("whitelist_enabled", false);
        playerSnooper.addClientStat("whitelist_count", 0);
        playerSnooper.addClientStat("players_current", this.getCurrentPlayerCount());
        playerSnooper.addClientStat("players_max", this.getMaxPlayers());
        playerSnooper.addClientStat("players_seen", this.serverConfigManager.getAvailablePlayerDat().length);
        playerSnooper.addClientStat("uses_auth", this.onlineMode);
        playerSnooper.addClientStat("gui_state", this.getGuiEnabled() ? "enabled" : "disabled");
        playerSnooper.addClientStat("run_time", (getCurrentTimeMillis() - playerSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L);
        playerSnooper.addClientStat("avg_tick_ms", (int)(MathHelper.average(this.tickTimeArray) * 1.0E-6));
        int n = 0;

        for (int i = 0; i < this.worldServers.length; i++) {
            if (this.worldServers[i] != null) {
                WorldServer worldServer = this.worldServers[i];
                WorldInfo worldInfo = worldServer.getWorldInfo();
                playerSnooper.addClientStat("world[" + n + "][dimension]", worldServer.provider.dimensionId);
                playerSnooper.addClientStat("world[" + n + "][mode]", worldInfo.getGameType());
                playerSnooper.addClientStat("world[" + n + "][difficulty]", worldServer.difficultySetting);
                playerSnooper.addClientStat("world[" + n + "][hardcore]", worldInfo.isHardcoreModeEnabled());
                playerSnooper.addClientStat("world[" + n + "][generator_name]", worldInfo.getTerrainType().getWorldTypeName());
                playerSnooper.addClientStat("world[" + n + "][generator_version]", worldInfo.getTerrainType().getGeneratorVersion());
                playerSnooper.addClientStat("world[" + n + "][height]", this.buildLimit);
                playerSnooper.addClientStat("world[" + n + "][chunks_loaded]", worldServer.getChunkProvider().getLoadedChunkCount());
                n++;
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
    public ChunkCoordinates getCommandSenderPosition() {
        return new ChunkCoordinates(0, 0, 0);
    }

    @Override
    public World getEntityWorld() {
        return this.worldServers[0];
    }

    public int getSpawnProtectionSize() {
        return 16;
    }

    public boolean isBlockProtected(World inWorld, int x, int y, int z, EntityPlayer player) {
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
    public IChatComponent getFormattedCommandSenderName() {
        return new ChatComponentText(this.getCommandSenderName());
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
}
