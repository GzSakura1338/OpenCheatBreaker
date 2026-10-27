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
   public WebsocketNetHandler playerassetsCheatbreakerNet;
   public List<ResourceLocation> defaultResources = new ArrayList<>();
   public List<ServiceStatus> serviceStatuses = new ArrayList<>();
   public List<Session> sessions = new ArrayList<>();
   public List<Profile> profiles = new ArrayList<>();
   public Map<String, Theme> themes = new HashMap<>();
   public static byte[] CHAT_AES_KEY = new byte[]{86, 79, 84, 69, 32, 84, 82, 85, 77, 80, 32, 50, 48, 50, 48, 33};
   public WebsocketController websocketController;
   public final String pluginMessageChannel = "cheatbreaker:pm";
   public final String binaryChannel = "CB-Binary";
   public final String chatPrefix = "[CB] ";
   public String commitId = "?";
   public String gitCommit = "?";
   public String branch = "?";
   public String clientVersion = "?";
   public String clientBuildType = "?";
   public static AudioFormat audioFormat = new AudioFormat(8000.0F, 16, 1, true, true);
   public final boolean unusedFlag = false;
   public boolean websocketConnected = false;
   public GuiScreen guiScreen = null;
   public List<TimedEntry> friendsTyping = new ArrayList<>();
   public List<TimedEntry> localTyping = new ArrayList<>();
   public Logger logger = LogManager.getLogger("CheatBreaker Setup");
   public static String launcherVersion;
   public long startTime = System.currentTimeMillis();
   public DiscordRpcManager discordRpcManager;
   public static String installationId;
   public ConfigManager configManager;
   public static CheatBreaker instance;
   public ConfigFileManager configFileManager;
   public EventBus eventBus;
   public FontManager fontManager;
   public boolean debug;
   public AudioManager audioManager;
   public static String websocketToken;
   public ModManager modManager;
   public static final boolean UNUSED_FLAG = false;
   public ClientRestrictions clientRestrictions;
   public KeybindSettings keybindSettings;
   public ClientNetHandler netHandler;
   public BlockedServersManager blockedServersManager;
   public CosmeticsManager cosmeticsManager;
   public EmoteManager emoteManager;
   public RadioManager radioManager;
   public FriendsManager friendsManager;
   public static String clientName = new String(new byte[]{67, 104, 101, 97, 116, 66, 114, 101, 97, 107, 101, 114}, StandardCharsets.UTF_8);
   public TitleManager titleManager;
   public WorldBorderManager worldBorderManager;
   public ServerTransferHandler serverTransferHandler;
   public PlayerDataManager playerDataManager;
   public UiManager uiManager;
   public ClickRateLimiter clickRateLimiter;

   public boolean isAboutScreenEnabled() {
      return false;
   }

   public BlockedServersManager getBlockedServersManager() {
      return this.blockedServersManager;
   }

   public String getCommitId() {
      return this.commitId;
   }

   public EventBus getEventBus() {
      return this.eventBus;
   }

   public boolean alwaysTrue() {
      return true;
   }

   public void setDiscordRpcManager(DiscordRpcManager discordRpcManager) {
      this.discordRpcManager = discordRpcManager;
   }

   public List<Profile> getProfiles() {
      return this.profiles;
   }

   public boolean isWebsocketConnected() {
      return this.websocketConnected;
   }

   public List<ServiceStatus> getServiceStatuses() {
      return this.serviceStatuses;
   }

   public void setGuiScreen(GuiScreen guiScreen) {
      this.guiScreen = guiScreen;
   }

   public void setBlockedServersManager(BlockedServersManager blockedServersManager) {
      this.blockedServersManager = blockedServersManager;
   }

   public List<String> getServerMessages() {
      return this.serverMessages;
   }

   public String getGitCommit() {
      return this.gitCommit;
   }

   public void initialize() {
      this.mc.stream.handleString2("Player Viewer");
      PlayerViewer.run();
      this.mc.stream.handleString2("Fonts");
      this.fontManager.run();
      this.mc.stream.handleString2("Profiles");
      this.configManager.run();
      Logging.debug("Created default configuration presets");
      Logging.debug("Loaded " + this.profiles.size() + " custom profiles");
      this.mc.stream.handleString2("Configs");
      this.configFileManager = new ConfigFileManager();
      this.configFileManager.loadAll();
      this.keybindSettings.applyKeybinds();
      this.mc.stream.handleString2("Overlay");
      Minecraft minecraft = Minecraft.getMinecraft();
      ScaledResolution scaledResolution = new ScaledResolution(minecraft);
      int scaledWidth = scaledResolution.getScaledWidth();
      int scaledHeight = scaledResolution.getScaledHeight();
      FriendsScreen friendsScreen = new FriendsScreen();
      FriendsScreen.setInstance(friendsScreen);
      friendsScreen.setWorldAndResolution(minecraft, scaledWidth, scaledHeight);
      Logging.debug("Loaded Overlay Gui");
      this.mc.stream.handleString2("Player Assets");
      this.websocketController = new WebsocketController();

      try {
         this.websocketController.run();
      } catch (Exception e) {
         e.printStackTrace();
      }

      this.websocketController.run3();

      try {
         if (this.getKeybindSettings().showDiscordRichPresence.getBooleanValue()) {
            this.mc.stream.handleString2("Discord");
            this.discordRpcManager.run();
         }
      } catch (Exception e) {
         e.printStackTrace();
      }

      this.mc.stream.handleString2("Finishing");
      this.eventBus.handleEvent(new SimpleEvent28());
      new Thread(new TamperCheck()).start();
      Logging.debug("Finished startup in " + (System.currentTimeMillis() - this.startTime) + "ms!");
   }

   public static boolean isAprilFoolsEnabled() {
      return false;
   }

   public GuiScreen getGuiScreen() {
      return this.guiScreen;
   }

   public DiscordRpcManager getDiscordRpcManager() {
      return this.discordRpcManager;
   }

   public void setUiManager(UiManager uiManager) {
      this.uiManager = uiManager;
   }

   public PlayerDataManager getPlayerDataManager() {
      return this.playerDataManager;
   }

   public void setPlayerassetsCheatbreakerNet(WebsocketNetHandler websocketNetHandler) {
      this.playerassetsCheatbreakerNet = websocketNetHandler;
   }

   public UiManager getUiManager() {
      return this.uiManager;
   }

   public ConfigManager getConfigManager() {
      return this.configManager;
   }

   public String getClientVersion() {
      return this.clientVersion;
   }

   public ClickRateLimiter getClickRateLimiter() {
      return this.clickRateLimiter;
   }

   public void setClickRateLimiter(ClickRateLimiter clickRateLimiter) {
      this.clickRateLimiter = clickRateLimiter;
   }

   public String getChatPrefix() {
      return "[CB] ";
   }

   public void setClientVersion(String str) {
      this.clientVersion = str;
   }

   public ClientNetHandler getNetHandler() {
      return this.netHandler;
   }

   public List<Session> getSessions() {
      return this.sessions;
   }

   public static boolean isOfflineMode() {
      return !Boolean.getBoolean("cheatbreaker.online");
   }

   public static String getClientName() {
      return "CheatBreaker";
   }

   public void setWebsocketConnected(boolean flag) {
      this.websocketConnected = flag;
   }

   public void setCommitId(String str) {
      this.commitId = str;
   }

   public CosmeticsManager getCosmeticsManager() {
      return this.cosmeticsManager;
   }

   public KeybindSettings getKeybindSettings() {
      return this.keybindSettings;
   }

   public WebsocketNetHandler getPlayerassetsCheatbreakerNet() {
      return this.playerassetsCheatbreakerNet;
   }

   public void setGitCommit(String str) {
      this.gitCommit = str;
   }

   public void setServiceStatuses(List<ServiceStatus> list) {
      this.serviceStatuses = list;
   }

   public void setBranch(String str) {
      this.branch = str;
   }

   public void setThemes(Map<String, Theme> map) {
      this.themes = map;
   }

   public String getBranch() {
      return this.branch;
   }

   public void setModManager(ModManager modManager) {
      this.modManager = modManager;
   }

   public String getClientBuildType() {
      return this.clientBuildType;
   }

   public ConfigFileManager getConfigFileManager() {
      return this.configFileManager;
   }

   public static CheatBreaker getInstance() {
      return instance;
   }

   public void setConfigManager(ConfigManager configManager) {
      this.configManager = configManager;
   }

   public void setProfiles(List<Profile> list) {
      this.profiles = list;
   }

   public FriendsManager getFriendsManager() {
      return this.friendsManager;
   }

   public ClientRestrictions getClientRestrictions() {
      return this.clientRestrictions;
   }

   public Minecraft getMc() {
      return this.mc;
   }

   public Logger getLogger() {
      return this.logger;
   }

   public long getStartTime() {
      return this.startTime;
   }

   public TitleManager getTitleManager() {
      return this.titleManager;
   }

   public List<TimedEntry> getFriendsTyping() {
      return this.friendsTyping;
   }

   public WebsocketController getWebsocketController() {
      return this.websocketController;
   }

   public List<ResourceLocation> getDefaultResources() {
      return this.defaultResources;
   }

   public Map<String, Theme> getThemes() {
      return this.themes;
   }

   public void setClientRestrictions(ClientRestrictions clientRestrictions) {
      this.clientRestrictions = clientRestrictions;
   }

   public ModManager getModManager() {
      return this.modManager;
   }

   public void setConfigFileManager(ConfigFileManager configFileManager) {
      this.configFileManager = configFileManager;
   }

   public EmoteManager getEmoteManager() {
      return this.emoteManager;
   }

   public WorldBorderManager getWorldBorderManager() {
      return this.worldBorderManager;
   }

   public AudioManager getAudioManager() {
      return this.audioManager;
   }

   public List<TimedEntry> getLocalTyping() {
      return this.localTyping;
   }

   public FontManager getFontManager() {
      return this.fontManager;
   }

   public RadioManager getRadioManager() {
      return this.radioManager;
   }

   public void setWebsocketController(WebsocketController websocketController) {
      this.websocketController = websocketController;
   }

   public ServerTransferHandler getServerTransferHandler() {
      return this.serverTransferHandler;
   }

   public void setClientBuildType(String str) {
      this.clientBuildType = str;
   }

   public CheatBreaker() {
      Logging.debugTagged("Setup", "Starting CheatBreaker setup");
      instance = this;
      this.discordRpcManager = new DiscordRpcManager();
      this.configManager = new ConfigManager();
      this.mc.stream.handleString2("EventBus");
      this.eventBus = new EventBus();
      this.mc.stream.handleString2("Properties");
      this.fontManager = new FontManager();
      this.fontManager.loadAppProperties();
      this.debug = DeveloperFlags.showInternalMods;
      this.mc.stream.handleString2("Audio");
      this.audioManager = new AudioManager();
      this.audioManager.run();
      this.mc.stream.handleString2("Mods");
      this.modManager = new ModManager(this.eventBus);
      this.clientRestrictions = new ClientRestrictions();
      this.mc.stream.handleString2("Settings");
      this.keybindSettings = new KeybindSettings();
      this.mc.stream.handleString2("Network Manager");
      this.netHandler = new ClientNetHandler();
      this.blockedServersManager = new BlockedServersManager();
      this.blockedServersManager.run();
      this.mc.stream.handleString2("Cosmetics");
      this.cosmeticsManager = new CosmeticsManager();
      new EmoteModelLoader().run();
      this.emoteManager = new EmoteManager();
      this.mc.stream.handleString2("Dash");
      this.radioManager = new RadioManager();
      this.mc.stream.handleString2("Friends");
      this.friendsManager = new FriendsManager();
      this.mc.stream.handleString2("Titles");
      this.titleManager = new TitleManager();
      this.mc.stream.handleString2("World Border");
      this.worldBorderManager = new WorldBorderManager();
      this.mc.stream.handleString2("Transfer");
      this.serverTransferHandler = new ServerTransferHandler();
      this.mc.stream.handleString2("Profile");
      this.playerDataManager = new PlayerDataManager();
      this.mc.stream.handleString2("User Interface");
      this.uiManager = new UiManager();
      this.clickRateLimiter = new ClickRateLimiter();
      this.mc.stream.handleString2("Network Events");
      this.eventBus.checkClass2(SimpleEvent3.class, this.netHandler::onSimpleEvent3);
      this.eventBus.checkClass2(SimpleEvent26.class, this.netHandler::onSimpleEvent26);
      this.eventBus.checkClass2(StringByteArrayEvent.class, this.netHandler::onStringByteArrayEvent);
      this.eventBus.checkClass2(BooleanIntEvent.class, this.netHandler::onBooleanIntEvent);
      this.eventBus.checkClass2(S01PacketJoinGameEvent.class, this.netHandler::onJoinGame);
      this.mc.stream.handleString2("Title Events");
      this.eventBus.checkClass2(ScaledResolutionEvent3.class, this.titleManager::onScaledResolutionEvent3);
      this.eventBus.checkClass2(ClientTickEvent.class, this.titleManager::onClientTickEvent);
   }

   public void setSessions(List<Session> list) {
      this.sessions = list;
   }

   public void setFontManager(FontManager fontManager) {
      this.fontManager = fontManager;
   }

   public String getPluginMessageChannel() {
      return "cheatbreaker:pm";
   }

   public boolean isDebug() {
      return this.debug;
   }

   public void setKeybindSettings(KeybindSettings keybindSettings) {
      this.keybindSettings = keybindSettings;
   }

   public String getBinaryChannel() {
      return "CB-Binary";
   }
}
