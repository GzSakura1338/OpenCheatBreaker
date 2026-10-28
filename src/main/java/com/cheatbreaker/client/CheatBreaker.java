package com.cheatbreaker.client;

import com.cheatbreaker.client.audio.AudioManager;
import com.cheatbreaker.client.config.ClientRestrictions;
import com.cheatbreaker.client.config.ConfigFileManager;
import com.cheatbreaker.client.config.ConfigManager;
import com.cheatbreaker.client.config.Profile;
import com.cheatbreaker.client.cosmetic.CosmeticsManager;
import com.cheatbreaker.client.discord.DiscordRpcManager;
import com.cheatbreaker.client.emote.EmoteManager;
import com.cheatbreaker.client.emote.EmoteModelLoader;
import com.cheatbreaker.client.event.BooleanIntEvent;
import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.EventBus;
import com.cheatbreaker.client.event.S01PacketJoinGameEvent;
import com.cheatbreaker.client.event.ScaledResolutionEvent3;
import com.cheatbreaker.client.event.SimpleEvent26;
import com.cheatbreaker.client.event.SimpleEvent28;
import com.cheatbreaker.client.event.SimpleEvent3;
import com.cheatbreaker.client.event.StringByteArrayEvent;
import com.cheatbreaker.client.event.WorldClientEvent;
import com.cheatbreaker.client.font.FontManager;
import com.cheatbreaker.client.friends.FriendsManager;
import com.cheatbreaker.client.manager.BlockedServersManager;
import com.cheatbreaker.client.manager.UiManager;
import com.cheatbreaker.client.mod.KeybindSettings;
import com.cheatbreaker.client.mod.ModManager;
import com.cheatbreaker.client.mod.impl.ClickRateLimiter;
import com.cheatbreaker.client.network.ClientNetHandler;
import com.cheatbreaker.client.network.PlayerDataManager;
import com.cheatbreaker.client.network.ServerTransferHandler;
import com.cheatbreaker.client.network.TimedEntry;
import com.cheatbreaker.client.network.WebsocketController;
import com.cheatbreaker.client.network.WebsocketNetHandler;
import com.cheatbreaker.client.radio.RadioManager;
import com.cheatbreaker.client.theme.Theme;
import com.cheatbreaker.client.title.TitleManager;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import com.cheatbreaker.client.ui.screen.PlayerViewer;
import com.cheatbreaker.client.ui.screen.ServiceStatus;
import com.cheatbreaker.client.util.DeveloperFlags;
import com.cheatbreaker.client.util.Logging;
import com.cheatbreaker.client.util.TamperCheck;
import com.cheatbreaker.client.worldborder.WorldBorderManager;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sound.sampled.AudioFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CheatBreaker {
    public Minecraft mc = Minecraft.getMinecraft();
    public List<String> serverMessages = new ArrayList<>();
    public List<ResourceLocation> defaultResources = new ArrayList<>();
    public List<ServiceStatus> serviceStatuses = new ArrayList<>();
    public ConfigFileManager configFileManager;
    public List<Session> sessions = new ArrayList<>();
    public List<Profile> profiles = new ArrayList<>();
    public WebsocketController websocketController;
    public Map<String, Theme> themes = new HashMap<>();
    public final String pluginMessageChannel = "cheatbreaker:pm";
    public final String binaryChannel = "CB-Binary";
    public static String websocketToken;
    public final String chatPrefix = "[CB] ";
    public static String launcherVersion;
    public String commitId = "?";
    public String gitCommit = "?";
    public String branch = "?";
    public String clientVersion = "?";
    public String clientBuildType = "?";
    public final boolean unusedFlag = false;
    public boolean websocketConnected = false;
    public GuiScreen guiScreen = null;
    public List<TimedEntry> friendsTyping = new ArrayList<>();
    public List<TimedEntry> localTyping = new ArrayList<>();
    public Logger logger = LogManager.getLogger("CheatBreaker Setup");
    public long startTime = System.currentTimeMillis();
    public DiscordRpcManager discordRpcManager;
    public static final boolean UNUSED_FLAG = false;
    public ConfigManager configManager;
    public EventBus eventBus;
    public FontManager fontManager;
    public boolean debug;
    public AudioManager audioManager;
    public static String installationId;
    public ModManager modManager;
    public ClientRestrictions clientRestrictions;
    public KeybindSettings keybindSettings;
    public static byte[] CHAT_AES_KEY = new byte[]{86, 79, 84, 69, 32, 84, 82, 85, 77, 80, 32, 50, 48, 50, 48, 33};
    public static AudioFormat audioFormat = new AudioFormat(8000.0F, 16, 1, true, true);
    public ClientNetHandler netHandler;
    public BlockedServersManager blockedServersManager;
    public CosmeticsManager cosmeticsManager;
    public EmoteManager emoteManager;
    public RadioManager radioManager;
    public static String clientName = new String(new byte[]{67, 104, 101, 97, 116, 66, 114, 101, 97, 107, 101, 114}, StandardCharsets.UTF_8);
    public FriendsManager friendsManager;
    public static CheatBreaker instance;
    public TitleManager titleManager;
    public WorldBorderManager worldBorderManager;
    public ServerTransferHandler serverTransferHandler;
    public WebsocketNetHandler playerassetsCheatbreakerNet;
    public PlayerDataManager playerDataManager;
    public UiManager uiManager;
    public ClickRateLimiter clickRateLimiter;

    public void initialize() throws java.io.IOException {
        this.mc.initializationScreen.setPhase("Player Viewer");
        PlayerViewer.createPreviewWorld();
        this.mc.initializationScreen.setPhase("Fonts");
        this.fontManager.initialize();
        this.mc.initializationScreen.setPhase("Profiles & Themes");
        this.configManager.initialize();
        Logging.debug("Loaded " + this.profiles.size() + " custom profiles");
        Logging.debug("Loaded " + this.themes.size() + " custom themes");
        this.mc.initializationScreen.setPhase("Configs");
        this.configFileManager = new ConfigFileManager();
        this.configFileManager.loadAll();
        this.keybindSettings.applyKeybinds();
        this.mc.initializationScreen.setPhase("Overlay");
        ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        FriendsScreen friendsScreen = new FriendsScreen();
        FriendsScreen.setInstance(friendsScreen);
        friendsScreen.setWorldAndResolution(this.mc, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
        this.mc.initializationScreen.setPhase("Player Assets");
        this.websocketController = new WebsocketController();

        try {
            this.websocketController.initialize();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.websocketController.startHeartbeat();

        try {
            if (this.getKeybindSettings().showDiscordRichPresence.getBooleanValue()) {
                this.mc.initializationScreen.setPhase("Discord");
                this.discordRpcManager.initialize();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.mc.initializationScreen.setPhase("Finishing");
        this.eventBus.handleEvent(new SimpleEvent28());
        new Thread(new TamperCheck()).start();
        Logging.debugTagged("Setup", "Finished startup in " + (System.currentTimeMillis() - this.startTime) + "ms!");
    }

    public boolean isDebug() {
        return this.debug;
    }

    public static boolean isAprilFoolsEnabled() {
        return false;
    }

    public List<ServiceStatus> getServiceStatuses() {
        return this.serviceStatuses;
    }

    public void setSessions(List<Session> list) {
        this.sessions = list;
    }

    public CheatBreaker() throws java.io.IOException {
        Logging.debugTagged("Setup", "Starting CheatBreaker setup");
        instance = this;
        this.discordRpcManager = new DiscordRpcManager();
        this.configManager = new ConfigManager();
        this.mc.initializationScreen.setPhase("EventBus");
        this.eventBus = new EventBus();
        this.mc.initializationScreen.setPhase("Properties");
        this.fontManager = new FontManager();
        this.fontManager.loadAppProperties();
        this.debug = DeveloperFlags.showInternalMods;
        this.mc.initializationScreen.setPhase("Audio");
        this.audioManager = new AudioManager();
        this.audioManager.refreshInputDevices();
        this.mc.initializationScreen.setPhase("Mods");
        this.modManager = new ModManager(this.eventBus);
        this.clientRestrictions = new ClientRestrictions();
        this.mc.initializationScreen.setPhase("Settings");
        this.keybindSettings = new KeybindSettings();
        this.mc.initializationScreen.setPhase("Network Manager");
        this.netHandler = new ClientNetHandler();
        this.blockedServersManager = new BlockedServersManager();
        this.blockedServersManager.initialize();
        this.mc.initializationScreen.setPhase("Cosmetics");
        this.cosmeticsManager = new CosmeticsManager();
        new EmoteModelLoader().load();
        this.emoteManager = new EmoteManager();
        this.mc.initializationScreen.setPhase("Dash");
        this.radioManager = new RadioManager();
        this.mc.initializationScreen.setPhase("Friends");
        this.friendsManager = new FriendsManager();
        this.mc.initializationScreen.setPhase("Titles");
        this.titleManager = new TitleManager();
        this.mc.initializationScreen.setPhase("World Border");
        this.worldBorderManager = new WorldBorderManager();
        this.mc.initializationScreen.setPhase("Transfer");
        this.serverTransferHandler = new ServerTransferHandler();
        this.mc.initializationScreen.setPhase("Profile");
        this.playerDataManager = new PlayerDataManager();
        this.mc.initializationScreen.setPhase("User Interface");
        this.uiManager = new UiManager();
        this.clickRateLimiter = new ClickRateLimiter();
        this.mc.initializationScreen.setPhase("Network Events");
        this.eventBus.checkClass2(SimpleEvent3.class, this.netHandler::onSimpleEvent3);
        this.eventBus.checkClass2(SimpleEvent26.class, this.netHandler::onSimpleEvent26);
        this.eventBus.checkClass2(StringByteArrayEvent.class, this.netHandler::onStringByteArrayEvent);
        this.eventBus.checkClass2(BooleanIntEvent.class, this.netHandler::onBooleanIntEvent);
        this.eventBus.checkClass2(S01PacketJoinGameEvent.class, this.netHandler::onJoinGame);
        this.eventBus.checkClass2(WorldClientEvent.class, this.netHandler::onWorldClientEvent);
        this.mc.initializationScreen.setPhase("Title Events");
        this.eventBus.checkClass2(ScaledResolutionEvent3.class, this.titleManager::onScaledResolutionEvent3);
        this.eventBus.checkClass2(ClientTickEvent.class, this.titleManager::onClientTickEvent);
    }

    public void setModManager(ModManager modManager) {
        this.modManager = modManager;
    }

    public long getStartTime() {
        return this.startTime;
    }

    public void setGuiScreen(GuiScreen guiScreen) {
        this.guiScreen = guiScreen;
    }

    public PlayerDataManager getPlayerDataManager() {
        return this.playerDataManager;
    }

    public List<ResourceLocation> getDefaultResources() {
        return this.defaultResources;
    }

    public List<Session> getSessions() {
        return this.sessions;
    }

    public void setThemes(Map<String, Theme> map) {
        this.themes = map;
    }

    public EmoteManager getEmoteManager() {
        return this.emoteManager;
    }

    public void setProfiles(List<Profile> list) {
        this.profiles = list;
    }

    public void setGitCommit(String str) {
        this.gitCommit = str;
    }

    public void setFontManager(FontManager fontManager) {
        this.fontManager = fontManager;
    }

    public List<Profile> getProfiles() {
        return this.profiles;
    }

    public WorldBorderManager getWorldBorderManager() {
        return this.worldBorderManager;
    }

    public KeybindSettings getKeybindSettings() {
        return this.keybindSettings;
    }

    public void setCommitId(String str) {
        this.commitId = str;
    }

    public List<TimedEntry> getFriendsTyping() {
        return this.friendsTyping;
    }

    public static CheatBreaker getInstance() {
        return instance;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public RadioManager getRadioManager() {
        return this.radioManager;
    }

    public GuiScreen getGuiScreen() {
        return this.guiScreen;
    }

    public static boolean isOfflineMode() {
        return !Boolean.getBoolean("cheatbreaker.online");
    }

    public static String getClientName() {
        return "CheatBreaker";
    }

    public String getCommitId() {
        return this.commitId;
    }

    public Map<String, Theme> getThemes() {
        return this.themes;
    }

    public void setBlockedServersManager(BlockedServersManager blockedServersManager) {
        this.blockedServersManager = blockedServersManager;
    }

    public TitleManager getTitleManager() {
        return this.titleManager;
    }

    public DiscordRpcManager getDiscordRpcManager() {
        return this.discordRpcManager;
    }

    public ModManager getModManager() {
        return this.modManager;
    }

    public void setServiceStatuses(List<ServiceStatus> list) {
        this.serviceStatuses = list;
    }

    public String getBinaryChannel() {
        return "CB-Binary";
    }

    public void setClientVersion(String str) {
        this.clientVersion = str;
    }

    public void setKeybindSettings(KeybindSettings keybindSettings) {
        this.keybindSettings = keybindSettings;
    }

    public boolean isAboutScreenEnabled() {
        return false;
    }

    public void setWebsocketController(WebsocketController websocketController) {
        this.websocketController = websocketController;
    }

    public String getChatPrefix() {
        return "[CB] ";
    }

    public boolean alwaysTrue() {
        return this.mc.getSession() != null && this.mc.getCurrentServerData() != null && this.mc.getCurrentServerData().flag3;
    }

    public ServerTransferHandler getServerTransferHandler() {
        return this.serverTransferHandler;
    }

    public String getBranch() {
        return this.branch;
    }

    public AudioManager getAudioManager() {
        return this.audioManager;
    }

    public ClientRestrictions getClientRestrictions() {
        return this.clientRestrictions;
    }

    public WebsocketNetHandler getPlayerassetsCheatbreakerNet() {
        return this.playerassetsCheatbreakerNet;
    }

    public Minecraft getMc() {
        return this.mc;
    }

    public void setClientBuildType(String str) {
        this.clientBuildType = str;
    }

    public CosmeticsManager getCosmeticsManager() {
        return this.cosmeticsManager;
    }

    public ClientNetHandler getNetHandler() {
        return this.netHandler;
    }

    public void setDiscordRpcManager(DiscordRpcManager discordRpcManager) {
        this.discordRpcManager = discordRpcManager;
    }

    public void setClickRateLimiter(ClickRateLimiter clickRateLimiter) {
        this.clickRateLimiter = clickRateLimiter;
    }

    public String getGitCommit() {
        return this.gitCommit;
    }

    public EventBus getEventBus() {
        return this.eventBus;
    }

    public boolean isWebsocketConnected() {
        return this.websocketConnected;
    }

    public WebsocketController getWebsocketController() {
        return this.websocketController;
    }

    public ClickRateLimiter getClickRateLimiter() {
        return this.clickRateLimiter;
    }

    public UiManager getUiManager() {
        return this.uiManager;
    }

    public ConfigManager getConfigManager() {
        return this.configManager;
    }

    public String getClientBuildType() {
        return this.clientBuildType;
    }

    public void setBranch(String str) {
        this.branch = str;
    }

    public List<TimedEntry> getLocalTyping() {
        return this.localTyping;
    }

    public List<String> getServerMessages() {
        return this.serverMessages;
    }

    public void setConfigManager(ConfigManager configManager) {
        this.configManager = configManager;
    }

    public String getPluginMessageChannel() {
        return "cheatbreaker:pm";
    }

    public String getClientVersion() {
        return this.clientVersion;
    }

    public void setWebsocketConnected(boolean flag) {
        this.websocketConnected = flag;
    }

    public void setUiManager(UiManager uiManager) {
        this.uiManager = uiManager;
    }

    public void setClientRestrictions(ClientRestrictions clientRestrictions) {
        this.clientRestrictions = clientRestrictions;
    }

    public FriendsManager getFriendsManager() {
        return this.friendsManager;
    }

    public void setConfigFileManager(ConfigFileManager configFileManager) {
        this.configFileManager = configFileManager;
    }

    public void setPlayerassetsCheatbreakerNet(WebsocketNetHandler websocketNetHandler) {
        this.playerassetsCheatbreakerNet = websocketNetHandler;
    }

    public ConfigFileManager getConfigFileManager() {
        return this.configFileManager;
    }

    public BlockedServersManager getBlockedServersManager() {
        return this.blockedServersManager;
    }

    public FontManager getFontManager() {
        return this.fontManager;
    }
}
