package net.minecraft.server.integrated;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import net.minecraft.client.ClientBrandRetriever;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ThreadLanServerPing;
import net.minecraft.crash.CrashReport;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.CryptManager;
import net.minecraft.util.HttpUtil;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldManager;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.WorldType;
import net.minecraft.world.demo.DemoWorldServer;
import net.minecraft.world.storage.ISaveHandler;
import net.optifine.Reflector;
import net.optifine.WorldServerMultiOF;
import net.optifine.WorldServerOF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IntegratedServer extends MinecraftServer {
    public static Logger logger = LogManager.getLogger();
    public Minecraft mc;
    public WorldSettings theWorldSettings;
    public boolean isGamePaused;
    public boolean isPublic;
    public ThreadLanServerPing lanServerPing;

    public IntegratedServer(Minecraft mc, String str, String str2, WorldSettings worldSettings) {
        super(new File(mc.mcDataDir, "saves"), mc.getProxy());
        this.setServerOwner(mc.getSession().getUsername());
        this.setFolderName(str);
        this.setWorldName(str2);
        this.setDemo(mc.isDemo());
        this.canCreateBonusChest(worldSettings.isBonusChestEnabled());
        this.setBuildLimit(256);
        this.setConfigManager(new IntegratedPlayerList(this));
        this.mc = mc;
        this.theWorldSettings = worldSettings;
        Reflector.callVoid(Reflector.ModLoader_registerServer, this);
    }

    @Override
    public void loadAllWorlds(String str, String str2, long l, WorldType worldType, String str3) throws IOException {
        this.convertMapIfNeeded(str);
        ISaveHandler saveLoader = this.getActiveAnvilConverter().getSaveLoader(str, true);
        if (Reflector.DimensionManager.exists()) {
            WorldServer worldServer = this.isDemo()
                ? new DemoWorldServer(this, saveLoader, str2, 0, this.theProfiler)
                : new WorldServerOF(this, saveLoader, str2, 0, this.theWorldSettings, this.theProfiler);
            Integer[] integers = (Integer[])Reflector.call(Reflector.DimensionManager_getStaticDimensionIDs);
            Integer[] integers2 = integers;
            int length = integers.length;

            for (int i = 0; i < length; i++) {
                int n = integers2[i];
                WorldServer worldServer2 = n == 0 ? worldServer : new WorldServerMultiOF(this, saveLoader, str2, n, this.theWorldSettings, worldServer, this.theProfiler);
                worldServer2.addWorldAccess(new WorldManager(this, worldServer2));
                if (!this.isSinglePlayer()) {
                    worldServer2.getWorldInfo().setGameType(this.getGameType());
                }

                if (Reflector.EventBus.exists()) {
                    Reflector.postForgeBusEvent(Reflector.WorldEvent_Load_Constructor, worldServer2);
                }
            }

            this.getConfigurationManager().setPlayerManager(new WorldServer[]{worldServer});
        } else {
            this.worldServers = new WorldServer[3];
            this.timeOfLastDimensionTick = new long[this.worldServers.length][100];

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
                        this.worldServers[i] = new WorldServerOF(this, saveLoader, str2, b, this.theWorldSettings, this.theProfiler);
                    }
                } else {
                    this.worldServers[i] = new WorldServerMultiOF(this, saveLoader, str2, b, this.theWorldSettings, this.worldServers[0], this.theProfiler);
                }

                this.worldServers[i].addWorldAccess(new WorldManager(this, this.worldServers[i]));
                this.getConfigurationManager().setPlayerManager(this.worldServers);
            }
        }

        this.setDifficultyForAllWorlds(this.getDifficulty());
        this.initialWorldChunkLoad();
    }

    @Override
    public boolean startServer() throws IOException {
        logger.info("Starting integrated minecraft server version 1.7.10");
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
            this.theWorldSettings.getGeneratorOptions()
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
    public void tick() throws IOException {
        boolean isGamePaused = this.isGamePaused;
        this.isGamePaused = Minecraft.getMinecraft().getNetHandler() != null && Minecraft.getMinecraft().isGamePaused();
        if (!isGamePaused && this.isGamePaused) {
            logger.info("Saving and pausing game...");
            this.getConfigurationManager().saveAllPlayerData();
            this.saveAllWorlds(false);
        }

        if (!this.isGamePaused) {
            super.tick();
            if (this.mc.gameSettings.renderDistanceChunks != this.getConfigurationManager().getViewDistance()) {
                logger.info(
                    "Changing view distance to {}, from {}",
                    new Object[]{this.mc.gameSettings.renderDistanceChunks, this.getConfigurationManager().getViewDistance()}
                );
                this.getConfigurationManager().setViewDistance(this.mc.gameSettings.renderDistanceChunks);
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
        return this.mc.gameSettings.difficulty;
    }

    @Override
    public boolean isHardcore() {
        return this.theWorldSettings.getHardcoreEnabled();
    }

    @Override
    public boolean shouldBroadcastRconToOps() {
        return false;
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
    public void finalTick(CrashReport report) {
        this.mc.crashed(report);
    }

    @Override
    public CrashReport addServerInfoToCrashReport(CrashReport report) {
        report = super.addServerInfoToCrashReport(report);
        report.getCategory().addCrashSectionCallable("Type", () -> "Integrated Server (map_client.txt)");
        report.getCategory()
            .addCrashSectionCallable(
                "Is Modded",
                () -> {
                    String clientModName = ClientBrandRetriever.getClientModName();
                    if (!clientModName.equals("vanilla")) {
                        return "Definitely; Client brand changed to '" + clientModName + "'";
                    }

                    clientModName = this.getServerModName();
                    return !clientModName.equals("vanilla")
                        ? "Definitely; Server brand changed to '" + clientModName + "'"
                        : (
                            Minecraft.class.getSigners() == null
                                ? "Very likely; Jar signature invalidated"
                                : "Probably not. Jar signature remains and both client + server brands are untouched."
                        );
                }
            );
        return report;
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
            this.getConfigurationManager().setGameMode(type);
            this.getConfigurationManager().setCommandsAllowedForAll(allowCheats);
            return n + "";
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void stopServer() throws IOException {
        super.stopServer();
        if (this.lanServerPing != null) {
            this.lanServerPing.interrupt();
            this.lanServerPing = null;
        }
    }

    @Override
    public void initiateShutdown() {
        super.initiateShutdown();
        if (this.lanServerPing != null) {
            this.lanServerPing.interrupt();
            this.lanServerPing = null;
        }
    }

    public boolean getPublic() {
        return this.isPublic;
    }

    @Override
    public void setGameType(GameType gameMode) {
        this.getConfigurationManager().setGameMode(gameMode);
    }

    @Override
    public boolean isCommandBlockEnabled() {
        return true;
    }

    @Override
    public int getOpPermissionLevel() {
        return 4;
    }
}
