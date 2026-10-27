package net.minecraft.client.settings;

import com.cheatbreaker.client.CheatBreaker;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer.EnumChatVisibility;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.optifine.ClearWater;
import net.optifine.Config;
import net.optifine.CustomColors;
import net.optifine.CustomGuis;
import net.optifine.CustomSky;
import net.optifine.DynamicLights;
import net.optifine.Lang;
import net.optifine.NaturalTextures;
import net.optifine.RandomEntities;
import net.optifine.reflect.Reflector;
import net.optifine.shaders.Shaders;
import net.optifine.util.KeyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;

public class GameSettings {
   public static Logger logger = LogManager.getLogger();
   public static Gson gson = new Gson();
   public static ParameterizedType typeListString = new ParameterizedType() {
      @Override
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class};
      }

      @Override
      public Type getRawType() {
         return List.class;
      }

      @Override
      public Type getOwnerType() {
         return null;
      }
   };
   public static String[] GUISCALES = new String[]{"options.guiScale.auto", "options.guiScale.small", "options.guiScale.normal", "options.guiScale.large"};
   public static String[] PARTICLES = new String[]{"options.particles.all", "options.particles.decreased", "options.particles.minimal"};
   public static String[] AMBIENT_OCCLUSIONS = new String[]{"options.ao.off", "options.ao.min", "options.ao.max"};
   public static String[] STREAM_COMPRESSIONS = new String[]{
      "options.stream.compression.low", "options.stream.compression.medium", "options.stream.compression.high"
   };
   public static String[] STREAM_CHAT_MODES = new String[]{
      "options.stream.chat.enabled.streaming", "options.stream.chat.enabled.always", "options.stream.chat.enabled.never"
   };
   public static String[] STREAM_CHAT_FILTER_MODES = new String[]{
      "options.stream.chat.userFilter.all", "options.stream.chat.userFilter.subs", "options.stream.chat.userFilter.mods"
   };
   public static String[] STREAM_MIC_MODES = new String[]{"options.stream.mic_toggle.mute", "options.stream.mic_toggle.talk"};
   public static String[] CLOUDS_TYPES = new String[]{"options.off", "options.graphics.fast", "options.graphics.fancy"};
   public float mouseSensitivity = 0.5F;
   public boolean invertMouse;
   public int renderDistanceChunks = -1;
   public boolean viewBobbing = true;
   public boolean anaglyph;
   public boolean fboEnable = true;
   public int limitFramerate = 120;
   public int clouds = 2;
   public boolean fancyGraphics = true;
   public int ambientOcclusion = 2;
   public List<String> resourcePacks = Lists.newArrayList();
   public List<String> incompatibleResourcePacks = Lists.newArrayList();
   public EnumChatVisibility chatVisibility = EnumChatVisibility.FULL;
   public boolean chatColours = true;
   public boolean chatLinks = true;
   public boolean chatLinksPrompt = true;
   public float chatOpacity = 1.0F;
   public boolean snooperEnabled = true;
   public boolean fullScreen;
   public boolean enableVsync = true;
   public boolean useVbo = false;
   public boolean allowBlockAlternatives = true;
   public boolean reducedDebugInfo = false;
   public boolean hideServerAddress;
   public boolean advancedItemTooltips;
   public boolean pauseOnLostFocus = true;
   public Set<EnumPlayerModelParts> setModelParts = Sets.newHashSet(EnumPlayerModelParts.values());
   public boolean touchscreen;
   public int overrideWidth;
   public int overrideHeight;
   public boolean heldItemTooltips = true;
   public float chatScale = 1.0F;
   public float chatWidth = 1.0F;
   public float chatHeightUnfocused = 0.44366196F;
   public float chatHeightFocused = 1.0F;
   public boolean showInventoryAchievementHint = true;
   public int mipmapLevels = 4;
   public Map<SoundCategory, Float> mapSoundLevels = Maps.newEnumMap(SoundCategory.class);
   public float streamBytesPerPixel = 0.5F;
   public float streamMicVolume = 1.0F;
   public float streamGameVolume = 1.0F;
   public float streamKbps = 0.5412844F;
   public float streamFps = 0.31690142F;
   public int streamCompression = 1;
   public boolean streamSendMetadata = true;
   public String streamPreferredServer = "";
   public int streamChatEnabled = 0;
   public int streamChatUserFilter = 0;
   public int streamMicToggleBehavior = 0;
   public boolean useNativeTransport = true;
   public boolean entityShadows = true;
   public boolean realmsNotifications = true;
   public KeyBinding keyBindForward = new KeyBinding("key.forward", 17, "key.categories.movement");
   public KeyBinding keyBindLeft = new KeyBinding("key.left", 30, "key.categories.movement");
   public KeyBinding keyBindBack = new KeyBinding("key.back", 31, "key.categories.movement");
   public KeyBinding keyBindRight = new KeyBinding("key.right", 32, "key.categories.movement");
   public KeyBinding keyBindJump = new KeyBinding("key.jump", 57, "key.categories.movement");
   public KeyBinding keyBindSneak = new KeyBinding("key.sneak", 42, "key.categories.movement");
   public KeyBinding keyBindSprint = new KeyBinding("key.sprint", 29, "key.categories.movement");
   public KeyBinding keyBindInventory = new KeyBinding("key.inventory", 18, "key.categories.inventory");
   public KeyBinding keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
   public KeyBinding keyBindDrop = new KeyBinding("key.drop", 16, "key.categories.gameplay");
   public KeyBinding keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
   public KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
   public KeyBinding keyBindChat = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
   public KeyBinding keyBindPlayerList = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
   public KeyBinding keyBindCommand = new KeyBinding("key.command", 53, "key.categories.multiplayer");
   public KeyBinding keyBindScreenshot = new KeyBinding("key.screenshot", 60, "key.categories.misc");
   public KeyBinding keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
   public KeyBinding keyBindSmoothCamera = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
   public KeyBinding keyBindStreamStartStop = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
   public KeyBinding keyBindSpectatorOutlines = new KeyBinding("key.spectatorOutlines", 0, "key.categories.misc");
   public KeyBinding[] keyBindsHotbar = new KeyBinding[]{
      new KeyBinding("key.hotbar.1", 2, "key.categories.inventory"),
      new KeyBinding("key.hotbar.2", 3, "key.categories.inventory"),
      new KeyBinding("key.hotbar.3", 4, "key.categories.inventory"),
      new KeyBinding("key.hotbar.4", 5, "key.categories.inventory"),
      new KeyBinding("key.hotbar.5", 6, "key.categories.inventory"),
      new KeyBinding("key.hotbar.6", 7, "key.categories.inventory"),
      new KeyBinding("key.hotbar.7", 8, "key.categories.inventory"),
      new KeyBinding("key.hotbar.8", 9, "key.categories.inventory"),
      new KeyBinding("key.hotbar.9", 10, "key.categories.inventory")
   };
   public int ofFogType = 1;
   public float ofFogStart = 0.8F;
   public int ofMipmapType = 0;
   public boolean ofOcclusionFancy = false;
   public boolean hideGUI;
   public int thirdPersonView;
   public boolean showDebugInfo;
   public boolean showDebugProfilerChart;
   public boolean showLagometer;
   public boolean ofSmoothFps = false;
   public boolean smoothCamera;
   public boolean debugCamEnable;
   public boolean ofSmoothWorld = Config.isSingleProcessor();
   public float gammaSetting;
   public float saturation;
   public int guiScale;
   public int particleSetting;
   public boolean ofLazyChunkLoading = Config.isSingleProcessor();
   public boolean ofRenderRegions = false;
   public boolean ofSmartAnimations = false;
   public float ofAoLevel = 1.0F;
   public int ofAaLevel = 0;
   public int ofAfLevel = 1;
   public int ofClouds = 0;
   public float ofCloudsHeight = 0.0F;
   public int ofTrees = 0;
   public int ofRain = 0;
   public int ofDroppedItems = 0;
   public int ofBetterGrass = 3;
   public int ofAutoSaveTicks = 4000;
   public boolean ofLagometer = false;
   public boolean ofProfiler = false;
   public boolean ofShowFps = false;
   public boolean ofWeather = true;
   public boolean ofSky = true;
   public boolean ofStars = true;
   public boolean ofSunMoon = true;
   public int ofVignette = 0;
   public int ofChunkUpdates = 1;
   public boolean ofChunkUpdatesDynamic = false;
   public int ofTime = 0;
   public boolean ofClearWater = false;
   public boolean ofBetterSnow = false;
   public String ofFullscreenMode = "Default";
   public boolean ofSwampColors = true;
   public boolean ofRandomEntities = true;
   public boolean ofSmoothBiomes = true;
   public boolean ofCustomFonts = true;
   public boolean ofCustomColors = true;
   public boolean ofCustomSky = true;
   public boolean ofShowCapes = true;
   public int ofConnectedTextures = 2;
   public boolean ofCustomItems = true;
   public boolean ofNaturalTextures = false;
   public boolean ofEmissiveTextures = true;
   public boolean ofFastMath = false;
   public boolean ofFastRender = false;
   public int ofTranslucentBlocks = 0;
   public boolean ofDynamicFov = true;
   public boolean ofAlternateBlocks = true;
   public int ofDynamicLights = 3;
   public boolean ofCustomEntityModels = true;
   public boolean ofCustomGuis = true;
   public boolean ofShowGlErrors = true;
   public int ofScreenshotSize = 1;
   public int ofAnimatedWater = 0;
   public int ofAnimatedLava = 0;
   public boolean ofAnimatedFire = true;
   public boolean ofAnimatedPortal = true;
   public boolean ofAnimatedRedstone = true;
   public boolean ofAnimatedExplosion = true;
   public boolean ofAnimatedFlame = true;
   public boolean ofAnimatedSmoke = true;
   public boolean ofVoidParticles = true;
   public boolean ofWaterParticles = true;
   public boolean ofRainSplash = true;
   public boolean ofPortalParticles = true;
   public boolean ofPotionParticles = true;
   public boolean ofFireworkParticles = true;
   public boolean ofDrippingWaterLava = true;
   public boolean ofAnimatedTerrain = true;
   public boolean ofAnimatedTextures = true;
   public KeyBinding[] keyBindings = (KeyBinding[])ArrayUtils.addAll(
      new KeyBinding[]{
         this.keyBindAttack,
         this.keyBindUseItem,
         this.keyBindForward,
         this.keyBindLeft,
         this.keyBindBack,
         this.keyBindRight,
         this.keyBindJump,
         this.keyBindSneak,
         this.keyBindSprint,
         this.keyBindDrop,
         this.keyBindInventory,
         this.keyBindChat,
         this.keyBindPlayerList,
         this.keyBindPickBlock,
         this.keyBindCommand,
         this.keyBindScreenshot,
         this.keyBindTogglePerspective,
         this.keyBindSmoothCamera,
         this.keyBindStreamStartStop,
         this.keyBindSpectatorOutlines
      },
      this.keyBindsHotbar
   );
   public EnumDifficulty difficulty = EnumDifficulty.NORMAL;
   public String lastServer = "";
   public float fovSetting = 70.0F;
   public String language = "en_US";
   public boolean forceUnicodeFont = false;
   public Minecraft mc;
   public File optionsFile;
   public static final int OFF = 3;
   public static final int SMART = 4;
   public static final String DEFAULT_STR = "Default";
   public static int[] OF_TREES_VALUES = new int[]{0, 1, 4, 2};
   public static int[] OF_DYNAMIC_LIGHTS = new int[]{3, 1, 2};
   public static String[] KEYS_DYNAMIC_LIGHTS = new String[]{"options.off", "options.graphics.fast", "options.graphics.fancy"};
   public File optionsFileOF;
   public KeyBinding ofKeyBindZoom;
   public static final int CONSTANT_1 = 1;
   public static final int CONSTANT_0 = 0;
   public static final int CONSTANT_1_2 = 1;
   public float floatValue;
   public static final int CONSTANT_2 = 2;
   public static final int CONSTANT_2_2 = 2;
   public static final int CONSTANT_0_2 = 0;

   public GameSettings(Minecraft mcIn, File optionsFileIn) {
      this.mc = mcIn;
      this.optionsFile = new File(optionsFileIn, "options.txt");
      if (mcIn.isJava64bit() && Runtime.getRuntime().maxMemory() >= 1000000000L) {
         GameSettings.Options.RENDER_DISTANCE.setValueMax(32.0F);
         long l = 1000000L;
         if (Runtime.getRuntime().maxMemory() >= 1500L * l) {
            GameSettings.Options.RENDER_DISTANCE.setValueMax(48.0F);
         }

         if (Runtime.getRuntime().maxMemory() >= 2500L * l) {
            GameSettings.Options.RENDER_DISTANCE.setValueMax(64.0F);
         }
      } else {
         GameSettings.Options.RENDER_DISTANCE.setValueMax(16.0F);
      }

      this.renderDistanceChunks = mcIn.isJava64bit() ? 12 : 8;
      this.optionsFileOF = new File(optionsFileIn, "optionsof.txt");
      this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
      this.ofKeyBindZoom = new KeyBinding("of.key.zoom", 46, "key.categories.misc");
      this.keyBindings = (KeyBinding[])ArrayUtils.add(this.keyBindings, this.ofKeyBindZoom);
      KeyUtils.fixKeyConflicts(this.keyBindings, new KeyBinding[]{this.ofKeyBindZoom});
      this.renderDistanceChunks = 8;
      this.loadOptions();
      Config.initGameSettings(this);
   }

   public GameSettings() {
   }

   public static String getKeyDisplayString(int key) {
      return key < 0
         ? I18n.format("key.mouseButton", key + 101)
         : (key < 256 ? Keyboard.getKeyName(key) : String.format("%c", (char)(key - 256)).toUpperCase());
   }

   public static boolean isKeyDown(KeyBinding key) {
      return key.getKeyCode() == 0 ? false : (key.getKeyCode() < 0 ? Mouse.isButtonDown(key.getKeyCode() + 100) : Keyboard.isKeyDown(key.getKeyCode()));
   }

   public void setOptionKeyBinding(KeyBinding key, int keyCode) {
      key.setKeyCode(keyCode);
      key.run2();
      this.saveOptions();
   }

   public void setOptionFloatValue(GameSettings.Options settingsOption, float value) {
      this.setOptionFloatValueOF(settingsOption, value);
      if (settingsOption == GameSettings.Options.SENSITIVITY) {
         this.mouseSensitivity = value;
      }

      if (settingsOption == GameSettings.Options.FOV) {
         this.fovSetting = value;
      }

      if (settingsOption == GameSettings.Options.GAMMA) {
         CheatBreaker.getInstance().getKeybindSettings().fullbright.changeValue(false);
         this.gammaSetting = value;
      }

      if (settingsOption == GameSettings.Options.FRAMERATE_LIMIT) {
         this.limitFramerate = (int)value;
         this.enableVsync = false;
         if (this.limitFramerate <= 0) {
            this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
            this.enableVsync = true;
         }

         this.updateVSync();
      }

      if (settingsOption == GameSettings.Options.CHAT_OPACITY) {
         this.chatOpacity = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (settingsOption == GameSettings.Options.CHAT_HEIGHT_FOCUSED) {
         this.chatHeightFocused = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (settingsOption == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED) {
         this.chatHeightUnfocused = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (settingsOption == GameSettings.Options.CHAT_WIDTH) {
         this.chatWidth = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (settingsOption == GameSettings.Options.CHAT_SCALE) {
         this.chatScale = value;
         this.mc.ingameGUI.getChatGUI().refreshChat();
      }

      if (settingsOption == GameSettings.Options.MIPMAP_LEVELS) {
         int mipmapLevels = this.mipmapLevels;
         this.mipmapLevels = (int)value;
         if (mipmapLevels != value) {
            this.mc.getTextureMapBlocks().setMipmapLevels(this.mipmapLevels);
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            this.mc.getTextureMapBlocks().setBlurMipmapDirect(false, this.mipmapLevels > 0);
            this.mc.scheduleResourcesRefresh();
         }
      }

      if (settingsOption == GameSettings.Options.BLOCK_ALTERNATIVES) {
         this.allowBlockAlternatives = !this.allowBlockAlternatives;
         this.mc.renderGlobal.loadRenderers();
      }

      if (settingsOption == GameSettings.Options.RENDER_DISTANCE) {
         this.renderDistanceChunks = (int)value;
         this.mc.renderGlobal.setDisplayListEntitiesDirty();
      }
   }

   public void setOptionValue(GameSettings.Options settingsOption, int value) {
      this.setOptionValueOF(settingsOption, value);
      if (settingsOption == GameSettings.Options.INVERT_MOUSE) {
         this.invertMouse = !this.invertMouse;
      }

      if (settingsOption == GameSettings.Options.GUI_SCALE) {
         this.guiScale += value;
         if (GuiScreen.isShiftKeyDown()) {
            this.guiScale = 0;
         }

         DisplayMode largestDisplayMode = Config.getLargestDisplayMode();
         int n = largestDisplayMode.getWidth() / 320;
         int n2 = largestDisplayMode.getHeight() / 240;
         int min = Math.min(n, n2);
         if (this.guiScale < 0) {
            this.guiScale = min - 1;
         }

         if (this.mc.isUnicode() && this.guiScale % 2 != 0) {
            this.guiScale += value;
         }

         if (this.guiScale < 0 || this.guiScale >= min) {
            this.guiScale = 0;
         }
      }

      if (settingsOption == GameSettings.Options.PARTICLES) {
         this.particleSetting = (this.particleSetting + value) % 3;
      }

      if (settingsOption == GameSettings.Options.VIEW_BOBBING) {
         this.viewBobbing = !this.viewBobbing;
      }

      if (settingsOption == GameSettings.Options.RENDER_CLOUDS) {
         this.clouds = (this.clouds + value) % 3;
      }

      if (settingsOption == GameSettings.Options.FORCE_UNICODE_FONT) {
         this.forceUnicodeFont = !this.forceUnicodeFont;
         this.mc.fontRendererObj.setUnicodeFlag(this.mc.getLanguageManager().isCurrentLocaleUnicode() || this.forceUnicodeFont);
      }

      if (settingsOption == GameSettings.Options.FBO_ENABLE) {
         this.fboEnable = !this.fboEnable;
      }

      if (settingsOption == GameSettings.Options.ANAGLYPH) {
         if (!this.anaglyph && Config.isShaders()) {
            Config.showGuiMessage(Lang.get("of.message.an.shaders1"), Lang.get("of.message.an.shaders2"));
            return;
         }

         this.anaglyph = !this.anaglyph;
         this.mc.refreshResources();
      }

      if (settingsOption == GameSettings.Options.GRAPHICS) {
         this.fancyGraphics = !this.fancyGraphics;
         this.updateRenderClouds();
         this.mc.renderGlobal.loadRenderers();
      }

      if (settingsOption == GameSettings.Options.AMBIENT_OCCLUSION) {
         this.ambientOcclusion = (this.ambientOcclusion + value) % 3;
         this.mc.renderGlobal.loadRenderers();
      }

      if (settingsOption == GameSettings.Options.CHAT_VISIBILITY) {
         this.chatVisibility = EnumChatVisibility.getEnumChatVisibility((this.chatVisibility.getChatVisibility() + value) % 3);
      }

      if (settingsOption == GameSettings.Options.STREAM_COMPRESSION) {
         this.streamCompression = (this.streamCompression + value) % 3;
      }

      if (settingsOption == GameSettings.Options.STREAM_SEND_METADATA) {
         this.streamSendMetadata = !this.streamSendMetadata;
      }

      if (settingsOption == GameSettings.Options.STREAM_CHAT_ENABLED) {
         this.streamChatEnabled = (this.streamChatEnabled + value) % 3;
      }

      if (settingsOption == GameSettings.Options.STREAM_CHAT_USER_FILTER) {
         this.streamChatUserFilter = (this.streamChatUserFilter + value) % 3;
      }

      if (settingsOption == GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
         this.streamMicToggleBehavior = (this.streamMicToggleBehavior + value) % 2;
      }

      if (settingsOption == GameSettings.Options.CHAT_COLOR) {
         this.chatColours = !this.chatColours;
      }

      if (settingsOption == GameSettings.Options.CHAT_LINKS) {
         this.chatLinks = !this.chatLinks;
      }

      if (settingsOption == GameSettings.Options.CHAT_LINKS_PROMPT) {
         this.chatLinksPrompt = !this.chatLinksPrompt;
      }

      if (settingsOption == GameSettings.Options.SNOOPER_ENABLED) {
         this.snooperEnabled = !this.snooperEnabled;
      }

      if (settingsOption == GameSettings.Options.TOUCHSCREEN) {
         this.touchscreen = !this.touchscreen;
      }

      if (settingsOption == GameSettings.Options.USE_FULLSCREEN) {
         this.fullScreen = !this.fullScreen;
         if (this.mc.isFullScreen() != this.fullScreen) {
            this.mc.toggleFullscreen();
         }
      }

      if (settingsOption == GameSettings.Options.ENABLE_VSYNC) {
         this.enableVsync = !this.enableVsync;
         Display.setVSyncEnabled(this.enableVsync);
      }

      if (settingsOption == GameSettings.Options.USE_VBO) {
         this.useVbo = !this.useVbo;
         this.mc.renderGlobal.loadRenderers();
      }

      if (settingsOption == GameSettings.Options.BLOCK_ALTERNATIVES) {
         this.allowBlockAlternatives = !this.allowBlockAlternatives;
         this.mc.renderGlobal.loadRenderers();
      }

      if (settingsOption == GameSettings.Options.REDUCED_DEBUG_INFO) {
         this.reducedDebugInfo = !this.reducedDebugInfo;
      }

      if (settingsOption == GameSettings.Options.ENTITY_SHADOWS) {
         this.entityShadows = !this.entityShadows;
         CheatBreaker.getInstance().getKeybindSettings().entityShadows.changeValue(this.entityShadows);
      }

      if (settingsOption == GameSettings.Options.REALMS_NOTIFICATIONS) {
         this.realmsNotifications = !this.realmsNotifications;
      }

      this.saveOptions();
   }

   public float getOptionFloatValue(GameSettings.Options settingOption) {
      float optionFloatValueOF = this.getOptionFloatValueOF(settingOption);
      return optionFloatValueOF != Float.MAX_VALUE
         ? optionFloatValueOF
         : (
            settingOption == GameSettings.Options.FOV
               ? this.fovSetting
               : (
                  settingOption == GameSettings.Options.GAMMA
                     ? this.gammaSetting
                     : (
                        settingOption == GameSettings.Options.SATURATION
                           ? this.saturation
                           : (
                              settingOption == GameSettings.Options.SENSITIVITY
                                 ? this.mouseSensitivity
                                 : (
                                    settingOption == GameSettings.Options.CHAT_OPACITY
                                       ? this.chatOpacity
                                       : (
                                          settingOption == GameSettings.Options.CHAT_HEIGHT_FOCUSED
                                             ? this.chatHeightFocused
                                             : (
                                                settingOption == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED
                                                   ? this.chatHeightUnfocused
                                                   : (
                                                      settingOption == GameSettings.Options.CHAT_SCALE
                                                         ? this.chatScale
                                                         : (
                                                            settingOption == GameSettings.Options.CHAT_WIDTH
                                                               ? this.chatWidth
                                                               : (
                                                                  settingOption == GameSettings.Options.FRAMERATE_LIMIT
                                                                     ? this.limitFramerate
                                                                     : (
                                                                        settingOption == GameSettings.Options.MIPMAP_LEVELS
                                                                           ? this.mipmapLevels
                                                                           : (
                                                                              settingOption == GameSettings.Options.RENDER_DISTANCE
                                                                                 ? this.renderDistanceChunks
                                                                                 : (
                                                                                    settingOption == GameSettings.Options.STREAM_BYTES_PER_PIXEL
                                                                                       ? this.streamBytesPerPixel
                                                                                       : (
                                                                                          settingOption == GameSettings.Options.STREAM_VOLUME_MIC
                                                                                             ? this.streamMicVolume
                                                                                             : (
                                                                                                settingOption == GameSettings.Options.STREAM_VOLUME_SYSTEM
                                                                                                   ? this.streamGameVolume
                                                                                                   : (
                                                                                                      settingOption == GameSettings.Options.STREAM_KBPS
                                                                                                         ? this.streamKbps
                                                                                                         : (
                                                                                                            settingOption == GameSettings.Options.STREAM_FPS
                                                                                                               ? this.streamFps
                                                                                                               : 0.0F
                                                                                                         )
                                                                                                   )
                                                                                             )
                                                                                       )
                                                                                 )
                                                                           )
                                                                     )
                                                               )
                                                         )
                                                   )
                                             )
                                       )
                                 )
                           )
                     )
               )
         );
   }

   public boolean getOptionOrdinalValue(GameSettings.Options settingOption) {
      switch (settingOption) {
         case INVERT_MOUSE:
            return this.invertMouse;
         case VIEW_BOBBING:
            return this.viewBobbing;
         case ANAGLYPH:
            return this.anaglyph;
         case FBO_ENABLE:
            return this.fboEnable;
         case CHAT_COLOR:
            return this.chatColours;
         case CHAT_LINKS:
            return this.chatLinks;
         case CHAT_LINKS_PROMPT:
            return this.chatLinksPrompt;
         case SNOOPER_ENABLED:
            return this.snooperEnabled;
         case USE_FULLSCREEN:
            return this.fullScreen;
         case ENABLE_VSYNC:
            return this.enableVsync;
         case USE_VBO:
            return this.useVbo;
         case TOUCHSCREEN:
            return this.touchscreen;
         case STREAM_SEND_METADATA:
            return this.streamSendMetadata;
         case FORCE_UNICODE_FONT:
            return this.forceUnicodeFont;
         case BLOCK_ALTERNATIVES:
            return this.allowBlockAlternatives;
         case REDUCED_DEBUG_INFO:
            return this.reducedDebugInfo;
         case ENTITY_SHADOWS:
            return this.entityShadows;
         case REALMS_NOTIFICATIONS:
            return this.realmsNotifications;
         default:
            return false;
      }
   }

   public static String getTranslation(String[] strArray, int index) {
      if (index < 0 || index >= strArray.length) {
         index = 0;
      }

      return I18n.format(strArray[index]);
   }

   public String getKeyBinding(GameSettings.Options settingOption) {
      String keyBindingOF = this.getKeyBindingOF(settingOption);
      if (keyBindingOF != null) {
         return keyBindingOF;
      }

      String str = I18n.format(settingOption.getEnumString()) + ": ";
      if (!settingOption.getEnumFloat()) {
         if (settingOption.getEnumBoolean()) {
            boolean optionOrdinalValue = this.getOptionOrdinalValue(settingOption);
            return optionOrdinalValue ? str + I18n.format("options.on") : str + I18n.format("options.off");
         }

         if (settingOption == GameSettings.Options.GUI_SCALE) {
            return this.guiScale >= GUISCALES.length ? str + this.guiScale + "x" : str + getTranslation(GUISCALES, this.guiScale);
         }

         if (settingOption == GameSettings.Options.CHAT_VISIBILITY) {
            return str + I18n.format(this.chatVisibility.getResourceKey());
         }

         if (settingOption == GameSettings.Options.PARTICLES) {
            return str + getTranslation(PARTICLES, this.particleSetting);
         }

         if (settingOption == GameSettings.Options.AMBIENT_OCCLUSION) {
            return str + getTranslation(AMBIENT_OCCLUSIONS, this.ambientOcclusion);
         }

         if (settingOption == GameSettings.Options.RENDER_CLOUDS) {
            return str + getTranslation(CLOUDS_TYPES, this.clouds);
         }

         if (settingOption == GameSettings.Options.GRAPHICS) {
            if (this.fancyGraphics) {
               return str + I18n.format("options.graphics.fancy");
            }

            String str2 = "options.graphics.fast";
            return str + I18n.format("options.graphics.fast");
         } else {
            return str;
         }
      } else {
         float optionFloatValue = this.getOptionFloatValue(settingOption);
         float normalizeValue = settingOption.normalizeValue(optionFloatValue);
         return settingOption == GameSettings.Options.MIPMAP_LEVELS && optionFloatValue >= 4.0
            ? str + Lang.get("of.general.max")
            : (
               settingOption == GameSettings.Options.SENSITIVITY
                  ? (
                     normalizeValue == 0.0F
                        ? str + I18n.format("options.sensitivity.min")
                        : (normalizeValue == 1.0F ? str + I18n.format("options.sensitivity.max") : str + (int)(normalizeValue * 200.0F) + "%")
                  )
                  : (
                     settingOption == GameSettings.Options.FOV
                        ? (optionFloatValue == 70.0F ? str + I18n.format("options.fov.min") : (optionFloatValue == 110.0F ? str + I18n.format("options.fov.max") : str + (int)optionFloatValue))
                        : (
                           settingOption == GameSettings.Options.FRAMERATE_LIMIT
                              ? (optionFloatValue == settingOption.valueMax ? str + I18n.format("options.framerateLimit.max") : str + (int)optionFloatValue + " fps")
                              : (
                                 settingOption == GameSettings.Options.RENDER_CLOUDS
                                    ? (optionFloatValue == settingOption.valueMin ? str + I18n.format("options.cloudHeight.min") : str + ((int)optionFloatValue + 128))
                                    : (
                                       settingOption == GameSettings.Options.GAMMA
                                          ? (
                                             normalizeValue == 0.0F
                                                ? str + I18n.format("options.gamma.min")
                                                : (normalizeValue == 1.0F ? str + I18n.format("options.gamma.max") : str + "+" + (int)(normalizeValue * 100.0F) + "%")
                                          )
                                          : (
                                             settingOption == GameSettings.Options.SATURATION
                                                ? str + (int)(normalizeValue * 400.0F) + "%"
                                                : (
                                                   settingOption == GameSettings.Options.CHAT_OPACITY
                                                      ? str + (int)(normalizeValue * 90.0F + 10.0F) + "%"
                                                      : (
                                                         settingOption == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED
                                                            ? str + GuiNewChat.calculateChatboxHeight(normalizeValue) + "px"
                                                            : (
                                                               settingOption == GameSettings.Options.CHAT_HEIGHT_FOCUSED
                                                                  ? str + GuiNewChat.calculateChatboxHeight(normalizeValue) + "px"
                                                                  : (
                                                                     settingOption == GameSettings.Options.CHAT_WIDTH
                                                                        ? str + GuiNewChat.calculateChatboxWidth(normalizeValue) + "px"
                                                                        : (
                                                                           settingOption == GameSettings.Options.RENDER_DISTANCE
                                                                              ? str + (int)optionFloatValue + " chunks"
                                                                              : (
                                                                                 settingOption == GameSettings.Options.MIPMAP_LEVELS
                                                                                    ? (optionFloatValue == 0.0F ? str + I18n.format("options.off") : str + (int)optionFloatValue)
                                                                                    : (
                                                                                       normalizeValue == 0.0F
                                                                                          ? str + I18n.format("options.off")
                                                                                          : str + (int)(normalizeValue * 100.0F) + "%"
                                                                                    )
                                                                              )
                                                                        )
                                                                  )
                                                            )
                                                      )
                                                )
                                          )
                                    )
                              )
                        )
                  )
            );
      }
   }

   public void loadOptions() {
      FileInputStream fileInputStream = null;

      label553: {
         try {
            if (this.optionsFile.exists()) {
               BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream = new FileInputStream(this.optionsFile)));
               this.mapSoundLevels.clear();

               String str;
               while ((str = bufferedReader.readLine()) != null) {
                  try {
                     String[] split = str.split(":");
                     if (split[0].equals("mouseSensitivity")) {
                        this.mouseSensitivity = this.parseFloat(split[1]);
                     }

                     if (split[0].equals("fov")) {
                        this.fovSetting = this.parseFloat(split[1]) * 40.0F + 70.0F;
                     }

                     if (split[0].equalsIgnoreCase("oldGamma")) {
                        this.floatValue = this.parseFloat(split[1]);
                     }

                     if (split[0].equals("gamma")) {
                        this.gammaSetting = this.parseFloat(split[1]);
                     }

                     if (split[0].equals("saturation")) {
                        this.saturation = this.parseFloat(split[1]);
                     }

                     if (split[0].equals("invertYMouse")) {
                        this.invertMouse = split[1].equals("true");
                     }

                     if (split[0].equals("renderDistance")) {
                        this.renderDistanceChunks = Integer.parseInt(split[1]);
                     }

                     if (split[0].equals("guiScale")) {
                        this.guiScale = Integer.parseInt(split[1]);
                     }

                     if (split[0].equals("particles")) {
                        this.particleSetting = Integer.parseInt(split[1]);
                     }

                     if (split[0].equals("bobView")) {
                        this.viewBobbing = split[1].equals("true");
                     }

                     if (split[0].equals("anaglyph3d")) {
                        this.anaglyph = split[1].equals("true");
                     }

                     if (split[0].equals("maxFps")) {
                        this.limitFramerate = Integer.parseInt(split[1]);
                        if (this.enableVsync) {
                           this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                        }

                        if (this.limitFramerate <= 0) {
                           this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                        }
                     }

                     if (split[0].equals("fboEnable")) {
                        this.fboEnable = split[1].equals("true");
                     }

                     if (split[0].equals("difficulty")) {
                        this.difficulty = EnumDifficulty.getDifficultyEnum(Integer.parseInt(split[1]));
                     }

                     if (split[0].equals("fancyGraphics")) {
                        this.fancyGraphics = split[1].equals("true");
                        this.updateRenderClouds();
                     }

                     if (split[0].equals("ao")) {
                        if (split[1].equals("true")) {
                           this.ambientOcclusion = 2;
                        } else if (split[1].equals("false")) {
                           this.ambientOcclusion = 0;
                        } else {
                           this.ambientOcclusion = Integer.parseInt(split[1]);
                        }
                     }

                     if (split[0].equals("renderClouds")) {
                        switch (split[1]) {
                           case "true":
                              this.clouds = 2;
                              break;
                           case "false":
                              this.clouds = 0;
                              break;
                           case "fast":
                              this.clouds = 1;
                        }
                     }

                     if (split[0].equals("resourcePacks")) {
                        this.resourcePacks = (List<String>)gson.fromJson(str.substring(str.indexOf(58) + 1), typeListString);
                        if (this.resourcePacks == null) {
                           this.resourcePacks = Lists.newArrayList();
                        }
                     }

                     if (split[0].equals("incompatibleResourcePacks")) {
                        this.incompatibleResourcePacks = (List<String>)gson.fromJson(str.substring(str.indexOf(58) + 1), typeListString);
                        if (this.incompatibleResourcePacks == null) {
                           this.incompatibleResourcePacks = Lists.newArrayList();
                        }
                     }

                     if (split[0].equals("lastServer") && split.length >= 2) {
                        this.lastServer = str.substring(str.indexOf(58) + 1);
                     }

                     if (split[0].equals("lang") && split.length >= 2) {
                        this.language = split[1];
                     }

                     if (split[0].equals("chatVisibility")) {
                        this.chatVisibility = EnumChatVisibility.getEnumChatVisibility(Integer.parseInt(split[1]));
                     }

                     if (split[0].equals("chatColors")) {
                        this.chatColours = split[1].equals("true");
                     }

                     if (split[0].equals("chatLinks")) {
                        this.chatLinks = split[1].equals("true");
                     }

                     if (split[0].equals("chatLinksPrompt")) {
                        this.chatLinksPrompt = split[1].equals("true");
                     }

                     if (split[0].equals("chatOpacity")) {
                        this.chatOpacity = this.parseFloat(split[1]);
                     }

                     if (split[0].equals("snooperEnabled")) {
                        this.snooperEnabled = split[1].equals("true");
                     }

                     if (split[0].equals("fullscreen")) {
                        this.fullScreen = split[1].equals("true");
                     }

                     if (split[0].equals("enableVsync")) {
                        this.enableVsync = split[1].equals("true");
                        if (this.enableVsync) {
                           this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                        }

                        this.updateVSync();
                     }

                     if (split[0].equals("useVbo")) {
                        this.useVbo = split[1].equals("true");
                     }

                     if (split[0].equals("hideServerAddress")) {
                        this.hideServerAddress = split[1].equals("true");
                     }

                     if (split[0].equals("advancedItemTooltips")) {
                        this.advancedItemTooltips = split[1].equals("true");
                     }

                     if (split[0].equals("pauseOnLostFocus")) {
                        this.pauseOnLostFocus = split[1].equals("true");
                     }

                     if (split[0].equals("touchscreen")) {
                        this.touchscreen = split[1].equals("true");
                     }

                     if (split[0].equals("overrideHeight")) {
                        this.overrideHeight = Integer.parseInt(split[1]);
                     }

                     if (split[0].equals("overrideWidth")) {
                        this.overrideWidth = Integer.parseInt(split[1]);
                     }

                     if (split[0].equals("heldItemTooltips")) {
                        this.heldItemTooltips = split[1].equals("true");
                     }

                     if (split[0].equals("chatHeightFocused")) {
                        this.chatHeightFocused = this.parseFloat(split[1]);
                     }

                     if (split[0].equals("chatHeightUnfocused")) {
                        this.chatHeightUnfocused = this.parseFloat(split[1]);
                     }

                     if (split[0].equals("chatScale")) {
                        this.chatScale = this.parseFloat(split[1]);
                     }

                     if (split[0].equals("chatWidth")) {
                        this.chatWidth = this.parseFloat(split[1]);
                     }

                     if (split[0].equals("showInventoryAchievementHint")) {
                        this.showInventoryAchievementHint = split[1].equals("true");
                     }

                     if (split[0].equals("mipmapLevels")) {
                        this.mipmapLevels = Integer.parseInt(split[1]);
                     }

                     if (split[0].equals("streamBytesPerPixel")) {
                        this.streamBytesPerPixel = this.parseFloat(split[1]);
                     }

                     if (split[0].equals("streamMicVolume")) {
                        this.streamMicVolume = this.parseFloat(split[1]);
                     }

                     if (split[0].equals("streamSystemVolume")) {
                        this.streamGameVolume = this.parseFloat(split[1]);
                     }

                     if (split[0].equals("streamKbps")) {
                        this.streamKbps = this.parseFloat(split[1]);
                     }

                     if (split[0].equals("streamFps")) {
                        this.streamFps = this.parseFloat(split[1]);
                     }

                     if (split[0].equals("streamCompression")) {
                        this.streamCompression = Integer.parseInt(split[1]);
                     }

                     if (split[0].equals("streamSendMetadata")) {
                        this.streamSendMetadata = split[1].equals("true");
                     }

                     if (split[0].equals("streamPreferredServer") && split.length >= 2) {
                        this.streamPreferredServer = str.substring(str.indexOf(58) + 1);
                     }

                     if (split[0].equals("streamChatEnabled")) {
                        this.streamChatEnabled = Integer.parseInt(split[1]);
                     }

                     if (split[0].equals("streamChatUserFilter")) {
                        this.streamChatUserFilter = Integer.parseInt(split[1]);
                     }

                     if (split[0].equals("streamMicToggleBehavior")) {
                        this.streamMicToggleBehavior = Integer.parseInt(split[1]);
                     }

                     if (split[0].equals("forceUnicodeFont")) {
                        this.forceUnicodeFont = split[1].equals("true");
                     }

                     if (split[0].equals("allowBlockAlternatives")) {
                        this.allowBlockAlternatives = split[1].equals("true");
                     }

                     if (split[0].equals("reducedDebugInfo")) {
                        this.reducedDebugInfo = split[1].equals("true");
                     }

                     if (split[0].equals("useNativeTransport")) {
                        this.useNativeTransport = split[1].equals("true");
                     }

                     if (split[0].equals("entityShadows")) {
                        this.entityShadows = split[1].equals("true");
                     }

                     if (split[0].equals("realmsNotifications")) {
                        this.realmsNotifications = split[1].equals("true");
                     }

                     for (KeyBinding keyBinding : this.keyBindings) {
                        if (split[0].equals("key_" + keyBinding.getKeyDescription())) {
                           keyBinding.setKeyCode(Integer.parseInt(split[1]));
                        }
                     }

                     for (SoundCategory soundCategory : SoundCategory.values()) {
                        if (split[0].equals("soundCategory_" + soundCategory.getCategoryName())) {
                           this.mapSoundLevels.put(soundCategory, this.parseFloat(split[1]));
                        }
                     }

                     for (EnumPlayerModelParts enumPlayerModelParts : EnumPlayerModelParts.values()) {
                        if (split[0].equals("modelPart_" + enumPlayerModelParts.getPartName())) {
                           this.setModelPartEnabled(enumPlayerModelParts, split[1].equals("true"));
                        }
                     }
                  } catch (Exception e) {
                     logger.warn("Skipping bad option: " + str);
                     e.printStackTrace();
                  }
               }

               KeyBinding.resetKeyBindingArrayAndHash();
               bufferedReader.close();
               break label553;
            }
         } catch (Exception e) {
            logger.error("Failed to load options", e);
            break label553;
         } finally {
            IOUtils.closeQuietly(fileInputStream);
         }

         return;
      }

      this.loadOfOptions();
   }

   public float parseFloat(String str) {
      return str.equals("true") ? 1.0F : (str.equals("false") ? 0.0F : Float.parseFloat(str));
   }

   public void saveOptions() {
      if (Reflector.FMLClientHandler.exists()) {
         Object obj = Reflector.call(Reflector.FMLClientHandler_instance);
         if (obj != null && Reflector.callBoolean(obj, Reflector.FMLClientHandler_isLoading)) {
            return;
         }
      }

      try {
         PrintWriter printWriter = new PrintWriter(new FileWriter(this.optionsFile));
         printWriter.println("invertYMouse:" + this.invertMouse);
         printWriter.println("mouseSensitivity:" + this.mouseSensitivity);
         printWriter.println("fov:" + (this.fovSetting - 70.0F) / 40.0F);
         printWriter.println("gamma:" + this.gammaSetting);
         if (!CheatBreaker.getInstance().getKeybindSettings().fullbright.getBooleanValue()) {
            printWriter.println("oldGamma:" + this.floatValue);
         }

         printWriter.println("saturation:" + this.saturation);
         printWriter.println("renderDistance:" + this.renderDistanceChunks);
         printWriter.println("guiScale:" + this.guiScale);
         printWriter.println("particles:" + this.particleSetting);
         printWriter.println("bobView:" + this.viewBobbing);
         printWriter.println("anaglyph3d:" + this.anaglyph);
         printWriter.println("maxFps:" + this.limitFramerate);
         printWriter.println("fboEnable:" + this.fboEnable);
         printWriter.println("difficulty:" + this.difficulty.getDifficultyId());
         printWriter.println("fancyGraphics:" + this.fancyGraphics);
         printWriter.println("ao:" + this.ambientOcclusion);
         switch (this.clouds) {
            case 0:
               printWriter.println("renderClouds:false");
               break;
            case 1:
               printWriter.println("renderClouds:fast");
               break;
            case 2:
               printWriter.println("renderClouds:true");
         }

         printWriter.println("resourcePacks:" + gson.toJson(this.resourcePacks));
         printWriter.println("incompatibleResourcePacks:" + gson.toJson(this.incompatibleResourcePacks));
         printWriter.println("lastServer:" + this.lastServer);
         printWriter.println("lang:" + this.language);
         printWriter.println("chatVisibility:" + this.chatVisibility.getChatVisibility());
         printWriter.println("chatColors:" + this.chatColours);
         printWriter.println("chatLinks:" + this.chatLinks);
         printWriter.println("chatLinksPrompt:" + this.chatLinksPrompt);
         printWriter.println("chatOpacity:" + this.chatOpacity);
         printWriter.println("snooperEnabled:" + this.snooperEnabled);
         printWriter.println("fullscreen:" + this.fullScreen);
         printWriter.println("enableVsync:" + this.enableVsync);
         printWriter.println("useVbo:" + this.useVbo);
         printWriter.println("hideServerAddress:" + this.hideServerAddress);
         printWriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
         printWriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
         printWriter.println("touchscreen:" + this.touchscreen);
         printWriter.println("overrideWidth:" + this.overrideWidth);
         printWriter.println("overrideHeight:" + this.overrideHeight);
         printWriter.println("heldItemTooltips:" + this.heldItemTooltips);
         printWriter.println("chatHeightFocused:" + this.chatHeightFocused);
         printWriter.println("chatHeightUnfocused:" + this.chatHeightUnfocused);
         printWriter.println("chatScale:" + this.chatScale);
         printWriter.println("chatWidth:" + this.chatWidth);
         printWriter.println("showInventoryAchievementHint:" + this.showInventoryAchievementHint);
         printWriter.println("mipmapLevels:" + this.mipmapLevels);
         printWriter.println("streamBytesPerPixel:" + this.streamBytesPerPixel);
         printWriter.println("streamMicVolume:" + this.streamMicVolume);
         printWriter.println("streamSystemVolume:" + this.streamGameVolume);
         printWriter.println("streamKbps:" + this.streamKbps);
         printWriter.println("streamFps:" + this.streamFps);
         printWriter.println("streamCompression:" + this.streamCompression);
         printWriter.println("streamSendMetadata:" + this.streamSendMetadata);
         printWriter.println("streamPreferredServer:" + this.streamPreferredServer);
         printWriter.println("streamChatEnabled:" + this.streamChatEnabled);
         printWriter.println("streamChatUserFilter:" + this.streamChatUserFilter);
         printWriter.println("streamMicToggleBehavior:" + this.streamMicToggleBehavior);
         printWriter.println("forceUnicodeFont:" + this.forceUnicodeFont);
         printWriter.println("allowBlockAlternatives:" + this.allowBlockAlternatives);
         printWriter.println("reducedDebugInfo:" + this.reducedDebugInfo);
         printWriter.println("useNativeTransport:" + this.useNativeTransport);
         printWriter.println("entityShadows:" + this.entityShadows);
         printWriter.println("realmsNotifications:" + this.realmsNotifications);

         for (KeyBinding keyBinding : this.keyBindings) {
            printWriter.println("key_" + keyBinding.getKeyDescription() + ":" + keyBinding.getKeyCode());
         }

         for (SoundCategory soundCategory : SoundCategory.values()) {
            printWriter.println("soundCategory_" + soundCategory.getCategoryName() + ":" + this.getSoundLevel(soundCategory));
         }

         for (EnumPlayerModelParts enumPlayerModelParts : EnumPlayerModelParts.values()) {
            printWriter.println("modelPart_" + enumPlayerModelParts.getPartName() + ":" + this.setModelParts.contains(enumPlayerModelParts));
         }

         printWriter.close();
      } catch (Exception e) {
         logger.error("Failed to save options", e);
      }

      this.saveOfOptions();
      this.sendSettingsToServer();
   }

   public float getSoundLevel(SoundCategory sndCategory) {
      return this.mapSoundLevels.containsKey(sndCategory) ? this.mapSoundLevels.get(sndCategory) : 1.0F;
   }

   public void setSoundLevel(SoundCategory sndCategory, float soundLevel) {
      this.mc.getSoundHandler().setSoundLevel(sndCategory, soundLevel);
      this.mapSoundLevels.put(sndCategory, soundLevel);
   }

   public void sendSettingsToServer() {
      if (this.mc.thePlayer != null) {
         int n = 0;

         for (EnumPlayerModelParts enumPlayerModelParts : this.setModelParts) {
            n |= enumPlayerModelParts.getPartMask();
         }

         this.mc
            .thePlayer
            .sendQueue
            .addToSendQueue(new C15PacketClientSettings(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColours, n));
      }
   }

   public Set<EnumPlayerModelParts> getModelParts() {
      return ImmutableSet.copyOf(this.setModelParts);
   }

   public void setModelPartEnabled(EnumPlayerModelParts modelPart, boolean enable) {
      if (enable) {
         this.setModelParts.add(modelPart);
      } else {
         this.setModelParts.remove(modelPart);
      }

      this.sendSettingsToServer();
   }

   public void switchModelPartEnabled(EnumPlayerModelParts modelPart) {
      if (!this.getModelParts().contains(modelPart)) {
         this.setModelParts.add(modelPart);
      } else {
         this.setModelParts.remove(modelPart);
      }

      this.sendSettingsToServer();
   }

   public int shouldRenderClouds() {
      return this.renderDistanceChunks >= 4 ? this.clouds : 0;
   }

   public boolean isUsingNativeTransport() {
      return this.useNativeTransport;
   }

   public void setOptionFloatValueOF(GameSettings.Options options, float f) {
      if (options == GameSettings.Options.CLOUD_HEIGHT) {
         this.ofCloudsHeight = f;
         this.mc.renderGlobal.resetClouds();
      }

      if (options == GameSettings.Options.AO_LEVEL) {
         this.ofAoLevel = f;
         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.AA_LEVEL) {
         int n = (int)f;
         if (n > 0 && Config.isShaders()) {
            Config.showGuiMessage(Lang.get("of.message.aa.shaders1"), Lang.get("of.message.aa.shaders2"));
            return;
         }

         int[] ints = new int[]{0, 2, 4, 6, 8, 12, 16};
         this.ofAaLevel = 0;

         for (int i = 0; i < ints.length; i++) {
            if (n >= ints[i]) {
               this.ofAaLevel = ints[i];
            }
         }

         this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
      }

      if (options == GameSettings.Options.AF_LEVEL) {
         int n = (int)f;
         if (n > 1 && Config.isShaders()) {
            Config.showGuiMessage(Lang.get("of.message.af.shaders1"), Lang.get("of.message.af.shaders2"));
            return;
         }

         this.ofAfLevel = 1;

         while (this.ofAfLevel * 2 <= n) {
            this.ofAfLevel *= 2;
         }

         this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
         this.mc.refreshResources();
      }

      if (options == GameSettings.Options.MIPMAP_TYPE) {
         int n = (int)f;
         this.ofMipmapType = Config.limit(n, 0, 3);
         this.mc.refreshResources();
      }

      if (options == GameSettings.Options.FULLSCREEN_MODE) {
         int n = (int)f - 1;
         String[] displayModeNames = Config.getDisplayModeNames();
         if (n < 0 || n >= displayModeNames.length) {
            this.ofFullscreenMode = "Default";
            return;
         }

         this.ofFullscreenMode = displayModeNames[n];
      }
   }

   public float getOptionFloatValueOF(GameSettings.Options options) {
      if (options == GameSettings.Options.CLOUD_HEIGHT) {
         return this.ofCloudsHeight;
      }

      if (options == GameSettings.Options.AO_LEVEL) {
         return this.ofAoLevel;
      }

      if (options == GameSettings.Options.AA_LEVEL) {
         return this.ofAaLevel;
      }

      if (options == GameSettings.Options.AF_LEVEL) {
         return this.ofAfLevel;
      }

      if (options == GameSettings.Options.MIPMAP_TYPE) {
         return this.ofMipmapType;
      }

      if (options != GameSettings.Options.FRAMERATE_LIMIT) {
         if (options == GameSettings.Options.FULLSCREEN_MODE) {
            if (this.ofFullscreenMode.equals("Default")) {
               return 0.0F;
            }

            List asList = Arrays.asList(Config.getDisplayModeNames());
            int indexOf = asList.indexOf(this.ofFullscreenMode);
            return indexOf < 0 ? 0.0F : indexOf + 1;
         } else {
            return Float.MAX_VALUE;
         }
      } else {
         return this.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() && this.enableVsync ? 0.0F : this.limitFramerate;
      }
   }

   public void setOptionValueOF(GameSettings.Options options, int n) {
      if (options == GameSettings.Options.FOG_FANCY) {
         switch (this.ofFogType) {
            case 1:
               this.ofFogType = 2;
               if (!Config.isFancyFogAvailable()) {
                  this.ofFogType = 3;
               }
               break;
            case 2:
               this.ofFogType = 3;
               break;
            case 3:
               this.ofFogType = 1;
               break;
            default:
               this.ofFogType = 1;
         }
      }

      if (options == GameSettings.Options.FOG_START) {
         this.ofFogStart += 0.2F;
         if (this.ofFogStart > 0.81F) {
            this.ofFogStart = 0.2F;
         }
      }

      if (options == GameSettings.Options.SMOOTH_FPS) {
         this.ofSmoothFps = !this.ofSmoothFps;
      }

      if (options == GameSettings.Options.SMOOTH_WORLD) {
         this.ofSmoothWorld = !this.ofSmoothWorld;
         Config.updateThreadPriorities();
      }

      if (options == GameSettings.Options.CLOUDS) {
         this.ofClouds++;
         if (this.ofClouds > 3) {
            this.ofClouds = 0;
         }

         this.updateRenderClouds();
         this.mc.renderGlobal.resetClouds();
      }

      if (options == GameSettings.Options.TREES) {
         this.ofTrees = nextValue(this.ofTrees, OF_TREES_VALUES);
         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.DROPPED_ITEMS) {
         this.ofDroppedItems++;
         if (this.ofDroppedItems > 2) {
            this.ofDroppedItems = 0;
         }
      }

      if (options == GameSettings.Options.RAIN) {
         this.ofRain++;
         if (this.ofRain > 3) {
            this.ofRain = 0;
         }
      }

      if (options == GameSettings.Options.ANIMATED_WATER) {
         this.ofAnimatedWater++;
         if (this.ofAnimatedWater == 1) {
            this.ofAnimatedWater++;
         }

         if (this.ofAnimatedWater > 2) {
            this.ofAnimatedWater = 0;
         }
      }

      if (options == GameSettings.Options.ANIMATED_LAVA) {
         this.ofAnimatedLava++;
         if (this.ofAnimatedLava == 1) {
            this.ofAnimatedLava++;
         }

         if (this.ofAnimatedLava > 2) {
            this.ofAnimatedLava = 0;
         }
      }

      if (options == GameSettings.Options.ANIMATED_FIRE) {
         this.ofAnimatedFire = !this.ofAnimatedFire;
      }

      if (options == GameSettings.Options.ANIMATED_PORTAL) {
         this.ofAnimatedPortal = !this.ofAnimatedPortal;
      }

      if (options == GameSettings.Options.ANIMATED_REDSTONE) {
         this.ofAnimatedRedstone = !this.ofAnimatedRedstone;
      }

      if (options == GameSettings.Options.ANIMATED_EXPLOSION) {
         this.ofAnimatedExplosion = !this.ofAnimatedExplosion;
      }

      if (options == GameSettings.Options.ANIMATED_FLAME) {
         this.ofAnimatedFlame = !this.ofAnimatedFlame;
      }

      if (options == GameSettings.Options.ANIMATED_SMOKE) {
         this.ofAnimatedSmoke = !this.ofAnimatedSmoke;
      }

      if (options == GameSettings.Options.VOID_PARTICLES) {
         this.ofVoidParticles = !this.ofVoidParticles;
      }

      if (options == GameSettings.Options.WATER_PARTICLES) {
         this.ofWaterParticles = !this.ofWaterParticles;
      }

      if (options == GameSettings.Options.PORTAL_PARTICLES) {
         this.ofPortalParticles = !this.ofPortalParticles;
      }

      if (options == GameSettings.Options.POTION_PARTICLES) {
         this.ofPotionParticles = !this.ofPotionParticles;
      }

      if (options == GameSettings.Options.FIREWORK_PARTICLES) {
         this.ofFireworkParticles = !this.ofFireworkParticles;
      }

      if (options == GameSettings.Options.DRIPPING_WATER_LAVA) {
         this.ofDrippingWaterLava = !this.ofDrippingWaterLava;
      }

      if (options == GameSettings.Options.ANIMATED_TERRAIN) {
         this.ofAnimatedTerrain = !this.ofAnimatedTerrain;
      }

      if (options == GameSettings.Options.ANIMATED_TEXTURES) {
         this.ofAnimatedTextures = !this.ofAnimatedTextures;
      }

      if (options == GameSettings.Options.RAIN_SPLASH) {
         this.ofRainSplash = !this.ofRainSplash;
      }

      if (options == GameSettings.Options.LAGOMETER) {
         this.ofLagometer = !this.ofLagometer;
      }

      if (options == GameSettings.Options.SHOW_FPS) {
         this.ofShowFps = !this.ofShowFps;
      }

      if (options == GameSettings.Options.AUTOSAVE_TICKS) {
         short s = 900;
         this.ofAutoSaveTicks = Math.max(this.ofAutoSaveTicks / s * s, s);
         this.ofAutoSaveTicks *= 2;
         if (this.ofAutoSaveTicks > 32 * s) {
            this.ofAutoSaveTicks = s;
         }
      }

      if (options == GameSettings.Options.BETTER_GRASS) {
         this.ofBetterGrass++;
         if (this.ofBetterGrass > 3) {
            this.ofBetterGrass = 1;
         }

         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.CONNECTED_TEXTURES) {
         this.ofConnectedTextures++;
         if (this.ofConnectedTextures > 3) {
            this.ofConnectedTextures = 1;
         }

         if (this.ofConnectedTextures == 2) {
            this.mc.renderGlobal.loadRenderers();
         } else {
            this.mc.refreshResources();
         }
      }

      if (options == GameSettings.Options.WEATHER) {
         this.ofWeather = !this.ofWeather;
      }

      if (options == GameSettings.Options.SKY) {
         this.ofSky = !this.ofSky;
      }

      if (options == GameSettings.Options.STARS) {
         this.ofStars = !this.ofStars;
      }

      if (options == GameSettings.Options.SUN_MOON) {
         this.ofSunMoon = !this.ofSunMoon;
      }

      if (options == GameSettings.Options.VIGNETTE) {
         this.ofVignette++;
         if (this.ofVignette > 2) {
            this.ofVignette = 0;
         }
      }

      if (options == GameSettings.Options.CHUNK_UPDATES) {
         this.ofChunkUpdates++;
         if (this.ofChunkUpdates > 5) {
            this.ofChunkUpdates = 1;
         }
      }

      if (options == GameSettings.Options.CHUNK_UPDATES_DYNAMIC) {
         this.ofChunkUpdatesDynamic = !this.ofChunkUpdatesDynamic;
      }

      if (options == GameSettings.Options.TIME) {
         this.ofTime++;
         if (this.ofTime > 2) {
            this.ofTime = 0;
         }
      }

      if (options == GameSettings.Options.CLEAR_WATER) {
         this.ofClearWater = !this.ofClearWater;
         this.updateWaterOpacity();
      }

      if (options == GameSettings.Options.PROFILER) {
         this.ofProfiler = !this.ofProfiler;
      }

      if (options == GameSettings.Options.BETTER_SNOW) {
         this.ofBetterSnow = !this.ofBetterSnow;
         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.SWAMP_COLORS) {
         this.ofSwampColors = !this.ofSwampColors;
         CustomColors.updateUseDefaultGrassFoliageColors();
         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.RANDOM_ENTITIES) {
         this.ofRandomEntities = !this.ofRandomEntities;
         RandomEntities.update();
      }

      if (options == GameSettings.Options.SMOOTH_BIOMES) {
         this.ofSmoothBiomes = !this.ofSmoothBiomes;
         CustomColors.updateUseDefaultGrassFoliageColors();
         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.CUSTOM_FONTS) {
         this.ofCustomFonts = !this.ofCustomFonts;
         this.mc.fontRendererObj.onResourceManagerReload(Config.getResourceManager());
         this.mc.standardGalacticFontRenderer.onResourceManagerReload(Config.getResourceManager());
      }

      if (options == GameSettings.Options.CUSTOM_COLORS) {
         this.ofCustomColors = !this.ofCustomColors;
         CustomColors.update();
         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.CUSTOM_ITEMS) {
         this.ofCustomItems = !this.ofCustomItems;
         this.mc.refreshResources();
      }

      if (options == GameSettings.Options.CUSTOM_SKY) {
         this.ofCustomSky = !this.ofCustomSky;
         CustomSky.update();
      }

      if (options == GameSettings.Options.SHOW_CAPES) {
         this.ofShowCapes = !this.ofShowCapes;
      }

      if (options == GameSettings.Options.NATURAL_TEXTURES) {
         this.ofNaturalTextures = !this.ofNaturalTextures;
         NaturalTextures.update();
         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.EMISSIVE_TEXTURES) {
         this.ofEmissiveTextures = !this.ofEmissiveTextures;
         this.mc.refreshResources();
      }

      if (options == GameSettings.Options.FAST_MATH) {
         this.ofFastMath = !this.ofFastMath;
         MathHelper.fastMath = this.ofFastMath;
      }

      if (options == GameSettings.Options.FAST_RENDER) {
         if (!this.ofFastRender && Config.isShaders()) {
            Config.showGuiMessage(Lang.get("of.message.fr.shaders1"), Lang.get("of.message.fr.shaders2"));
            return;
         }

         this.ofFastRender = !this.ofFastRender;
         if (this.ofFastRender) {
            this.mc.entityRenderer.stopUseShader();
         }

         Config.updateFramebufferSize();
      }

      if (options == GameSettings.Options.TRANSLUCENT_BLOCKS) {
         if (this.ofTranslucentBlocks == 0) {
            this.ofTranslucentBlocks = 1;
         } else if (this.ofTranslucentBlocks == 1) {
            this.ofTranslucentBlocks = 2;
         } else if (this.ofTranslucentBlocks == 2) {
            this.ofTranslucentBlocks = 0;
         } else {
            this.ofTranslucentBlocks = 0;
         }

         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.LAZY_CHUNK_LOADING) {
         this.ofLazyChunkLoading = !this.ofLazyChunkLoading;
      }

      if (options == GameSettings.Options.RENDER_REGIONS) {
         this.ofRenderRegions = !this.ofRenderRegions;
         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.SMART_ANIMATIONS) {
         this.ofSmartAnimations = !this.ofSmartAnimations;
         this.mc.renderGlobal.loadRenderers();
      }

      if (options == GameSettings.Options.DYNAMIC_FOV) {
         this.ofDynamicFov = !this.ofDynamicFov;
      }

      if (options == GameSettings.Options.ALTERNATE_BLOCKS) {
         this.ofAlternateBlocks = !this.ofAlternateBlocks;
         this.mc.refreshResources();
      }

      if (options == GameSettings.Options.DYNAMIC_LIGHTS) {
         this.ofDynamicLights = nextValue(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
         DynamicLights.removeLights(this.mc.renderGlobal);
      }

      if (options == GameSettings.Options.SCREENSHOT_SIZE) {
         this.ofScreenshotSize++;
         if (this.ofScreenshotSize > 4) {
            this.ofScreenshotSize = 1;
         }

         if (!OpenGlHelper.isFramebufferEnabled()) {
            this.ofScreenshotSize = 1;
         }
      }

      if (options == GameSettings.Options.CUSTOM_ENTITY_MODELS) {
         this.ofCustomEntityModels = !this.ofCustomEntityModels;
         this.mc.refreshResources();
      }

      if (options == GameSettings.Options.CUSTOM_GUIS) {
         this.ofCustomGuis = !this.ofCustomGuis;
         CustomGuis.update();
      }

      if (options == GameSettings.Options.SHOW_GL_ERRORS) {
         this.ofShowGlErrors = !this.ofShowGlErrors;
      }

      if (options == GameSettings.Options.HELD_ITEM_TOOLTIPS) {
         this.heldItemTooltips = !this.heldItemTooltips;
      }

      if (options == GameSettings.Options.ADVANCED_TOOLTIPS) {
         this.advancedItemTooltips = !this.advancedItemTooltips;
      }
   }

   public String getKeyBindingOF(GameSettings.Options options) {
      String str = I18n.format(options.getEnumString()) + ": ";
      if (str == null) {
         str = options.getEnumString();
      }

      if (options == GameSettings.Options.RENDER_DISTANCE) {
         int optionFloatValue = (int)this.getOptionFloatValue(options);
         String format = I18n.format("options.renderDistance.tiny");
         byte b = 2;
         if (optionFloatValue >= 4) {
            format = I18n.format("options.renderDistance.short");
            b = 4;
         }

         if (optionFloatValue >= 8) {
            format = I18n.format("options.renderDistance.normal");
            b = 8;
         }

         if (optionFloatValue >= 16) {
            format = I18n.format("options.renderDistance.far");
            b = 16;
         }

         if (optionFloatValue >= 32) {
            format = Lang.get("of.options.renderDistance.extreme");
            b = 32;
         }

         if (optionFloatValue >= 48) {
            format = Lang.get("of.options.renderDistance.insane");
            b = 48;
         }

         if (optionFloatValue >= 64) {
            format = Lang.get("of.options.renderDistance.ludicrous");
            b = 64;
         }

         int n = this.renderDistanceChunks - b;
         String str2 = format;
         if (n > 0) {
            str2 = format + "+";
         }

         return str + optionFloatValue + " " + str2 + "";
      } else if (options == GameSettings.Options.FOG_FANCY) {
         switch (this.ofFogType) {
            case 1:
               return str + Lang.getFast();
            case 2:
               return str + Lang.getFancy();
            case 3:
               return str + Lang.getOff();
            default:
               return str + Lang.getOff();
         }
      } else {
         if (options == GameSettings.Options.FOG_START) {
            return str + this.ofFogStart;
         }

         if (options == GameSettings.Options.MIPMAP_TYPE) {
            switch (this.ofMipmapType) {
               case 0:
                  return str + Lang.get("of.options.mipmap.nearest");
               case 1:
                  return str + Lang.get("of.options.mipmap.linear");
               case 2:
                  return str + Lang.get("of.options.mipmap.bilinear");
               case 3:
                  return str + Lang.get("of.options.mipmap.trilinear");
               default:
                  return str + "of.options.mipmap.nearest";
            }
         } else {
            if (options == GameSettings.Options.SMOOTH_FPS) {
               return this.ofSmoothFps ? str + Lang.getOn() : str + Lang.getOff();
            }

            if (options == GameSettings.Options.SMOOTH_WORLD) {
               return this.ofSmoothWorld ? str + Lang.getOn() : str + Lang.getOff();
            }

            if (options == GameSettings.Options.CLOUDS) {
               switch (this.ofClouds) {
                  case 1:
                     return str + Lang.getFast();
                  case 2:
                     return str + Lang.getFancy();
                  case 3:
                     return str + Lang.getOff();
                  default:
                     return str + Lang.getDefault();
               }
            } else if (options == GameSettings.Options.TREES) {
               switch (this.ofTrees) {
                  case 1:
                     return str + Lang.getFast();
                  case 2:
                     return str + Lang.getFancy();
                  case 3:
                  default:
                     return str + Lang.getDefault();
                  case 4:
                     return str + Lang.get("of.general.smart");
               }
            } else if (options == GameSettings.Options.DROPPED_ITEMS) {
               switch (this.ofDroppedItems) {
                  case 1:
                     return str + Lang.getFast();
                  case 2:
                     return str + Lang.getFancy();
                  default:
                     return str + Lang.getDefault();
               }
            } else if (options == GameSettings.Options.RAIN) {
               switch (this.ofRain) {
                  case 1:
                     return str + Lang.getFast();
                  case 2:
                     return str + Lang.getFancy();
                  case 3:
                     return str + Lang.getOff();
                  default:
                     return str + Lang.getDefault();
               }
            } else if (options == GameSettings.Options.ANIMATED_WATER) {
               switch (this.ofAnimatedWater) {
                  case 1:
                     return str + Lang.get("of.options.animation.dynamic");
                  case 2:
                     return str + Lang.getOff();
                  default:
                     return str + Lang.getOn();
               }
            } else if (options == GameSettings.Options.ANIMATED_LAVA) {
               switch (this.ofAnimatedLava) {
                  case 1:
                     return str + Lang.get("of.options.animation.dynamic");
                  case 2:
                     return str + Lang.getOff();
                  default:
                     return str + Lang.getOn();
               }
            } else {
               if (options == GameSettings.Options.ANIMATED_FIRE) {
                  return this.ofAnimatedFire ? str + Lang.getOn() : str + Lang.getOff();
               }

               if (options == GameSettings.Options.ANIMATED_PORTAL) {
                  return this.ofAnimatedPortal ? str + Lang.getOn() : str + Lang.getOff();
               }

               if (options == GameSettings.Options.ANIMATED_REDSTONE) {
                  return this.ofAnimatedRedstone ? str + Lang.getOn() : str + Lang.getOff();
               }

               if (options == GameSettings.Options.ANIMATED_EXPLOSION) {
                  return this.ofAnimatedExplosion ? str + Lang.getOn() : str + Lang.getOff();
               }

               if (options == GameSettings.Options.ANIMATED_FLAME) {
                  return this.ofAnimatedFlame ? str + Lang.getOn() : str + Lang.getOff();
               }

               if (options == GameSettings.Options.ANIMATED_SMOKE) {
                  return this.ofAnimatedSmoke ? str + Lang.getOn() : str + Lang.getOff();
               }

               if (options == GameSettings.Options.VOID_PARTICLES) {
                  return this.ofVoidParticles ? str + Lang.getOn() : str + Lang.getOff();
               }

               if (options == GameSettings.Options.WATER_PARTICLES) {
                  return this.ofWaterParticles ? str + Lang.getOn() : str + Lang.getOff();
               }

               if (options == GameSettings.Options.PORTAL_PARTICLES) {
                  return this.ofPortalParticles ? str + Lang.getOn() : str + Lang.getOff();
               }

               if (options == GameSettings.Options.POTION_PARTICLES) {
                  return this.ofPotionParticles ? str + Lang.getOn() : str + Lang.getOff();
               }

               if (options == GameSettings.Options.FIREWORK_PARTICLES) {
                  return this.ofFireworkParticles ? str + Lang.getOn() : str + Lang.getOff();
               }

               if (options == GameSettings.Options.DRIPPING_WATER_LAVA) {
                  return this.ofDrippingWaterLava ? str + Lang.getOn() : str + Lang.getOff();
               }

               if (options == GameSettings.Options.ANIMATED_TERRAIN) {
                  return this.ofAnimatedTerrain ? str + Lang.getOn() : str + Lang.getOff();
               }

               if (options == GameSettings.Options.ANIMATED_TEXTURES) {
                  return this.ofAnimatedTextures ? str + Lang.getOn() : str + Lang.getOff();
               }

               if (options == GameSettings.Options.RAIN_SPLASH) {
                  return this.ofRainSplash ? str + Lang.getOn() : str + Lang.getOff();
               }

               if (options == GameSettings.Options.LAGOMETER) {
                  return this.ofLagometer ? str + Lang.getOn() : str + Lang.getOff();
               }

               if (options == GameSettings.Options.SHOW_FPS) {
                  return this.ofShowFps ? str + Lang.getOn() : str + Lang.getOff();
               }

               if (options == GameSettings.Options.AUTOSAVE_TICKS) {
                  short s = 900;
                  return this.ofAutoSaveTicks <= s
                     ? str + Lang.get("of.options.save.45s")
                     : (
                        this.ofAutoSaveTicks <= 2 * s
                           ? str + Lang.get("of.options.save.90s")
                           : (
                              this.ofAutoSaveTicks <= 4 * s
                                 ? str + Lang.get("of.options.save.3min")
                                 : (
                                    this.ofAutoSaveTicks <= 8 * s
                                       ? str + Lang.get("of.options.save.6min")
                                       : (
                                          this.ofAutoSaveTicks <= 16 * s
                                             ? str + Lang.get("of.options.save.12min")
                                             : str + Lang.get("of.options.save.24min")
                                       )
                                 )
                           )
                     );
               }

               if (options == GameSettings.Options.BETTER_GRASS) {
                  switch (this.ofBetterGrass) {
                     case 1:
                        return str + Lang.getFast();
                     case 2:
                        return str + Lang.getFancy();
                     default:
                        return str + Lang.getOff();
                  }
               } else if (options == GameSettings.Options.CONNECTED_TEXTURES) {
                  switch (this.ofConnectedTextures) {
                     case 1:
                        return str + Lang.getFast();
                     case 2:
                        return str + Lang.getFancy();
                     default:
                        return str + Lang.getOff();
                  }
               } else {
                  if (options == GameSettings.Options.WEATHER) {
                     return this.ofWeather ? str + Lang.getOn() : str + Lang.getOff();
                  }

                  if (options == GameSettings.Options.SKY) {
                     return this.ofSky ? str + Lang.getOn() : str + Lang.getOff();
                  }

                  if (options == GameSettings.Options.STARS) {
                     return this.ofStars ? str + Lang.getOn() : str + Lang.getOff();
                  }

                  if (options == GameSettings.Options.SUN_MOON) {
                     return this.ofSunMoon ? str + Lang.getOn() : str + Lang.getOff();
                  }

                  if (options == GameSettings.Options.VIGNETTE) {
                     switch (this.ofVignette) {
                        case 1:
                           return str + Lang.getFast();
                        case 2:
                           return str + Lang.getFancy();
                        default:
                           return str + Lang.getDefault();
                     }
                  } else {
                     if (options == GameSettings.Options.CHUNK_UPDATES) {
                        return str + this.ofChunkUpdates;
                     }

                     if (options == GameSettings.Options.CHUNK_UPDATES_DYNAMIC) {
                        return this.ofChunkUpdatesDynamic ? str + Lang.getOn() : str + Lang.getOff();
                     }

                     if (options == GameSettings.Options.TIME) {
                        return this.ofTime == 1
                           ? str + Lang.get("of.options.time.dayOnly")
                           : (this.ofTime == 2 ? str + Lang.get("of.options.time.nightOnly") : str + Lang.getDefault());
                     }

                     if (options == GameSettings.Options.CLEAR_WATER) {
                        return this.ofClearWater ? str + Lang.getOn() : str + Lang.getOff();
                     }

                     if (options == GameSettings.Options.AA_LEVEL) {
                        String str2 = "";
                        if (this.ofAaLevel != Config.getAntialiasingLevel()) {
                           str2 = " (" + Lang.get("of.general.restart") + ")";
                        }

                        return this.ofAaLevel == 0 ? str + Lang.getOff() + str2 : str + this.ofAaLevel + str2;
                     } else if (options == GameSettings.Options.AF_LEVEL) {
                        return this.ofAfLevel == 1 ? str + Lang.getOff() : str + this.ofAfLevel;
                     } else if (options == GameSettings.Options.PROFILER) {
                        return this.ofProfiler ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.BETTER_SNOW) {
                        return this.ofBetterSnow ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.SWAMP_COLORS) {
                        return this.ofSwampColors ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.RANDOM_ENTITIES) {
                        return this.ofRandomEntities ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.SMOOTH_BIOMES) {
                        return this.ofSmoothBiomes ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.CUSTOM_FONTS) {
                        return this.ofCustomFonts ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.CUSTOM_COLORS) {
                        return this.ofCustomColors ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.CUSTOM_SKY) {
                        return this.ofCustomSky ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.SHOW_CAPES) {
                        return this.ofShowCapes ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.CUSTOM_ITEMS) {
                        return this.ofCustomItems ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.NATURAL_TEXTURES) {
                        return this.ofNaturalTextures ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.EMISSIVE_TEXTURES) {
                        return this.ofEmissiveTextures ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.FAST_MATH) {
                        return this.ofFastMath ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.FAST_RENDER) {
                        return this.ofFastRender ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.TRANSLUCENT_BLOCKS) {
                        return this.ofTranslucentBlocks == 1
                           ? str + Lang.getFast()
                           : (this.ofTranslucentBlocks == 2 ? str + Lang.getFancy() : str + Lang.getDefault());
                     } else if (options == GameSettings.Options.LAZY_CHUNK_LOADING) {
                        return this.ofLazyChunkLoading ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.RENDER_REGIONS) {
                        return this.ofRenderRegions ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.SMART_ANIMATIONS) {
                        return this.ofSmartAnimations ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.DYNAMIC_FOV) {
                        return this.ofDynamicFov ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.ALTERNATE_BLOCKS) {
                        return this.ofAlternateBlocks ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.DYNAMIC_LIGHTS) {
                        int indexOf2 = indexOf(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
                        return str + getTranslation(KEYS_DYNAMIC_LIGHTS, indexOf2);
                     } else if (options == GameSettings.Options.SCREENSHOT_SIZE) {
                        return this.ofScreenshotSize <= 1 ? str + Lang.getDefault() : str + this.ofScreenshotSize + "x";
                     } else if (options == GameSettings.Options.CUSTOM_ENTITY_MODELS) {
                        return this.ofCustomEntityModels ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.CUSTOM_GUIS) {
                        return this.ofCustomGuis ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.SHOW_GL_ERRORS) {
                        return this.ofShowGlErrors ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.FULLSCREEN_MODE) {
                        return this.ofFullscreenMode.equals("Default") ? str + Lang.getDefault() : str + this.ofFullscreenMode;
                     } else if (options == GameSettings.Options.HELD_ITEM_TOOLTIPS) {
                        return this.heldItemTooltips ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.ADVANCED_TOOLTIPS) {
                        return this.advancedItemTooltips ? str + Lang.getOn() : str + Lang.getOff();
                     } else if (options == GameSettings.Options.FRAMERATE_LIMIT) {
                        float optionFloatValue = this.getOptionFloatValue(options);
                        return optionFloatValue == 0.0F
                           ? str + Lang.get("of.options.framerateLimit.vsync")
                           : (optionFloatValue == options.valueMax ? str + I18n.format("options.framerateLimit.max") : str + (int)optionFloatValue + " fps");
                     } else {
                        return null;
                     }
                  }
               }
            }
         }
      }
   }

   public void loadOfOptions() {
      try {
         File optionsFileOF = this.optionsFileOF;
         if (!optionsFileOF.exists()) {
            optionsFileOF = this.optionsFile;
         }

         if (!optionsFileOF.exists()) {
            return;
         }

         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(optionsFileOF), "UTF-8"));
         String str = "";

         while ((str = bufferedReader.readLine()) != null) {
            try {
               String[] split = str.split(":");
               if (split[0].equals("ofRenderDistanceChunks") && split.length >= 2) {
                  this.renderDistanceChunks = Integer.valueOf(split[1]);
                  this.renderDistanceChunks = Config.limit(this.renderDistanceChunks, 2, 1024);
               }

               if (split[0].equals("ofFogType") && split.length >= 2) {
                  this.ofFogType = Integer.valueOf(split[1]);
                  this.ofFogType = Config.limit(this.ofFogType, 1, 3);
               }

               if (split[0].equals("ofFogStart") && split.length >= 2) {
                  this.ofFogStart = Float.valueOf(split[1]);
                  if (this.ofFogStart < 0.2F) {
                     this.ofFogStart = 0.2F;
                  }

                  if (this.ofFogStart > 0.81F) {
                     this.ofFogStart = 0.8F;
                  }
               }

               if (split[0].equals("ofMipmapType") && split.length >= 2) {
                  this.ofMipmapType = Integer.valueOf(split[1]);
                  this.ofMipmapType = Config.limit(this.ofMipmapType, 0, 3);
               }

               if (split[0].equals("ofOcclusionFancy") && split.length >= 2) {
                  this.ofOcclusionFancy = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofSmoothFps") && split.length >= 2) {
                  this.ofSmoothFps = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofSmoothWorld") && split.length >= 2) {
                  this.ofSmoothWorld = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofAoLevel") && split.length >= 2) {
                  this.ofAoLevel = Float.valueOf(split[1]);
                  this.ofAoLevel = Config.limit(this.ofAoLevel, 0.0F, 1.0F);
               }

               if (split[0].equals("ofClouds") && split.length >= 2) {
                  this.ofClouds = Integer.valueOf(split[1]);
                  this.ofClouds = Config.limit(this.ofClouds, 0, 3);
                  this.updateRenderClouds();
               }

               if (split[0].equals("ofCloudsHeight") && split.length >= 2) {
                  this.ofCloudsHeight = Float.valueOf(split[1]);
                  this.ofCloudsHeight = Config.limit(this.ofCloudsHeight, 0.0F, 1.0F);
               }

               if (split[0].equals("ofTrees") && split.length >= 2) {
                  this.ofTrees = Integer.valueOf(split[1]);
                  this.ofTrees = limit(this.ofTrees, OF_TREES_VALUES);
               }

               if (split[0].equals("ofDroppedItems") && split.length >= 2) {
                  this.ofDroppedItems = Integer.valueOf(split[1]);
                  this.ofDroppedItems = Config.limit(this.ofDroppedItems, 0, 2);
               }

               if (split[0].equals("ofRain") && split.length >= 2) {
                  this.ofRain = Integer.valueOf(split[1]);
                  this.ofRain = Config.limit(this.ofRain, 0, 3);
               }

               if (split[0].equals("ofAnimatedWater") && split.length >= 2) {
                  this.ofAnimatedWater = Integer.valueOf(split[1]);
                  this.ofAnimatedWater = Config.limit(this.ofAnimatedWater, 0, 2);
               }

               if (split[0].equals("ofAnimatedLava") && split.length >= 2) {
                  this.ofAnimatedLava = Integer.valueOf(split[1]);
                  this.ofAnimatedLava = Config.limit(this.ofAnimatedLava, 0, 2);
               }

               if (split[0].equals("ofAnimatedFire") && split.length >= 2) {
                  this.ofAnimatedFire = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofAnimatedPortal") && split.length >= 2) {
                  this.ofAnimatedPortal = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofAnimatedRedstone") && split.length >= 2) {
                  this.ofAnimatedRedstone = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofAnimatedExplosion") && split.length >= 2) {
                  this.ofAnimatedExplosion = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofAnimatedFlame") && split.length >= 2) {
                  this.ofAnimatedFlame = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofAnimatedSmoke") && split.length >= 2) {
                  this.ofAnimatedSmoke = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofVoidParticles") && split.length >= 2) {
                  this.ofVoidParticles = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofWaterParticles") && split.length >= 2) {
                  this.ofWaterParticles = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofPortalParticles") && split.length >= 2) {
                  this.ofPortalParticles = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofPotionParticles") && split.length >= 2) {
                  this.ofPotionParticles = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofFireworkParticles") && split.length >= 2) {
                  this.ofFireworkParticles = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofDrippingWaterLava") && split.length >= 2) {
                  this.ofDrippingWaterLava = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofAnimatedTerrain") && split.length >= 2) {
                  this.ofAnimatedTerrain = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofAnimatedTextures") && split.length >= 2) {
                  this.ofAnimatedTextures = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofRainSplash") && split.length >= 2) {
                  this.ofRainSplash = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofLagometer") && split.length >= 2) {
                  this.ofLagometer = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofShowFps") && split.length >= 2) {
                  this.ofShowFps = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofAutoSaveTicks") && split.length >= 2) {
                  this.ofAutoSaveTicks = Integer.valueOf(split[1]);
                  this.ofAutoSaveTicks = Config.limit(this.ofAutoSaveTicks, 40, 40000);
               }

               if (split[0].equals("ofBetterGrass") && split.length >= 2) {
                  this.ofBetterGrass = Integer.valueOf(split[1]);
                  this.ofBetterGrass = Config.limit(this.ofBetterGrass, 1, 3);
               }

               if (split[0].equals("ofConnectedTextures") && split.length >= 2) {
                  this.ofConnectedTextures = Integer.valueOf(split[1]);
                  this.ofConnectedTextures = Config.limit(this.ofConnectedTextures, 1, 3);
               }

               if (split[0].equals("ofWeather") && split.length >= 2) {
                  this.ofWeather = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofSky") && split.length >= 2) {
                  this.ofSky = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofStars") && split.length >= 2) {
                  this.ofStars = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofSunMoon") && split.length >= 2) {
                  this.ofSunMoon = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofVignette") && split.length >= 2) {
                  this.ofVignette = Integer.valueOf(split[1]);
                  this.ofVignette = Config.limit(this.ofVignette, 0, 2);
               }

               if (split[0].equals("ofChunkUpdates") && split.length >= 2) {
                  this.ofChunkUpdates = Integer.valueOf(split[1]);
                  this.ofChunkUpdates = Config.limit(this.ofChunkUpdates, 1, 5);
               }

               if (split[0].equals("ofChunkUpdatesDynamic") && split.length >= 2) {
                  this.ofChunkUpdatesDynamic = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofTime") && split.length >= 2) {
                  this.ofTime = Integer.valueOf(split[1]);
                  this.ofTime = Config.limit(this.ofTime, 0, 2);
               }

               if (split[0].equals("ofClearWater") && split.length >= 2) {
                  this.ofClearWater = Boolean.valueOf(split[1]);
                  this.updateWaterOpacity();
               }

               if (split[0].equals("ofAaLevel") && split.length >= 2) {
                  this.ofAaLevel = Integer.valueOf(split[1]);
                  this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
               }

               if (split[0].equals("ofAfLevel") && split.length >= 2) {
                  this.ofAfLevel = Integer.valueOf(split[1]);
                  this.ofAfLevel = Config.limit(this.ofAfLevel, 1, 16);
               }

               if (split[0].equals("ofProfiler") && split.length >= 2) {
                  this.ofProfiler = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofBetterSnow") && split.length >= 2) {
                  this.ofBetterSnow = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofSwampColors") && split.length >= 2) {
                  this.ofSwampColors = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofRandomEntities") && split.length >= 2) {
                  this.ofRandomEntities = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofSmoothBiomes") && split.length >= 2) {
                  this.ofSmoothBiomes = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofCustomFonts") && split.length >= 2) {
                  this.ofCustomFonts = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofCustomColors") && split.length >= 2) {
                  this.ofCustomColors = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofCustomItems") && split.length >= 2) {
                  this.ofCustomItems = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofCustomSky") && split.length >= 2) {
                  this.ofCustomSky = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofShowCapes") && split.length >= 2) {
                  this.ofShowCapes = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofNaturalTextures") && split.length >= 2) {
                  this.ofNaturalTextures = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofEmissiveTextures") && split.length >= 2) {
                  this.ofEmissiveTextures = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofLazyChunkLoading") && split.length >= 2) {
                  this.ofLazyChunkLoading = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofRenderRegions") && split.length >= 2) {
                  this.ofRenderRegions = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofSmartAnimations") && split.length >= 2) {
                  this.ofSmartAnimations = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofDynamicFov") && split.length >= 2) {
                  this.ofDynamicFov = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofAlternateBlocks") && split.length >= 2) {
                  this.ofAlternateBlocks = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofDynamicLights") && split.length >= 2) {
                  this.ofDynamicLights = Integer.valueOf(split[1]);
                  this.ofDynamicLights = limit(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
               }

               if (split[0].equals("ofScreenshotSize") && split.length >= 2) {
                  this.ofScreenshotSize = Integer.valueOf(split[1]);
                  this.ofScreenshotSize = Config.limit(this.ofScreenshotSize, 1, 4);
               }

               if (split[0].equals("ofCustomEntityModels") && split.length >= 2) {
                  this.ofCustomEntityModels = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofCustomGuis") && split.length >= 2) {
                  this.ofCustomGuis = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofShowGlErrors") && split.length >= 2) {
                  this.ofShowGlErrors = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofFullscreenMode") && split.length >= 2) {
                  this.ofFullscreenMode = split[1];
               }

               if (split[0].equals("ofFastMath") && split.length >= 2) {
                  this.ofFastMath = Boolean.valueOf(split[1]);
                  MathHelper.fastMath = this.ofFastMath;
               }

               if (split[0].equals("ofFastRender") && split.length >= 2) {
                  this.ofFastRender = Boolean.valueOf(split[1]);
               }

               if (split[0].equals("ofTranslucentBlocks") && split.length >= 2) {
                  this.ofTranslucentBlocks = Integer.valueOf(split[1]);
                  this.ofTranslucentBlocks = Config.limit(this.ofTranslucentBlocks, 0, 2);
               }

               if (split[0].equals("key_" + this.ofKeyBindZoom.getKeyDescription())) {
                  this.ofKeyBindZoom.setKeyCode(Integer.parseInt(split[1]));
               }
            } catch (Exception e) {
               Config.dbg("Skipping bad option: " + str);
               e.printStackTrace();
            }
         }

         KeyUtils.fixKeyConflicts(this.keyBindings, new KeyBinding[]{this.ofKeyBindZoom});
         KeyBinding.resetKeyBindingArrayAndHash();
         bufferedReader.close();
      } catch (Exception e) {
         Config.warn("Failed to load options");
         e.printStackTrace();
      }
   }

   public void saveOfOptions() {
      try {
         PrintWriter printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(this.optionsFileOF), "UTF-8"));
         printWriter.println("ofFogType:" + this.ofFogType);
         printWriter.println("ofFogStart:" + this.ofFogStart);
         printWriter.println("ofMipmapType:" + this.ofMipmapType);
         printWriter.println("ofOcclusionFancy:" + this.ofOcclusionFancy);
         printWriter.println("ofSmoothFps:" + this.ofSmoothFps);
         printWriter.println("ofSmoothWorld:" + this.ofSmoothWorld);
         printWriter.println("ofAoLevel:" + this.ofAoLevel);
         printWriter.println("ofClouds:" + this.ofClouds);
         printWriter.println("ofCloudsHeight:" + this.ofCloudsHeight);
         printWriter.println("ofTrees:" + this.ofTrees);
         printWriter.println("ofDroppedItems:" + this.ofDroppedItems);
         printWriter.println("ofRain:" + this.ofRain);
         printWriter.println("ofAnimatedWater:" + this.ofAnimatedWater);
         printWriter.println("ofAnimatedLava:" + this.ofAnimatedLava);
         printWriter.println("ofAnimatedFire:" + this.ofAnimatedFire);
         printWriter.println("ofAnimatedPortal:" + this.ofAnimatedPortal);
         printWriter.println("ofAnimatedRedstone:" + this.ofAnimatedRedstone);
         printWriter.println("ofAnimatedExplosion:" + this.ofAnimatedExplosion);
         printWriter.println("ofAnimatedFlame:" + this.ofAnimatedFlame);
         printWriter.println("ofAnimatedSmoke:" + this.ofAnimatedSmoke);
         printWriter.println("ofVoidParticles:" + this.ofVoidParticles);
         printWriter.println("ofWaterParticles:" + this.ofWaterParticles);
         printWriter.println("ofPortalParticles:" + this.ofPortalParticles);
         printWriter.println("ofPotionParticles:" + this.ofPotionParticles);
         printWriter.println("ofFireworkParticles:" + this.ofFireworkParticles);
         printWriter.println("ofDrippingWaterLava:" + this.ofDrippingWaterLava);
         printWriter.println("ofAnimatedTerrain:" + this.ofAnimatedTerrain);
         printWriter.println("ofAnimatedTextures:" + this.ofAnimatedTextures);
         printWriter.println("ofRainSplash:" + this.ofRainSplash);
         printWriter.println("ofLagometer:" + this.ofLagometer);
         printWriter.println("ofShowFps:" + this.ofShowFps);
         printWriter.println("ofAutoSaveTicks:" + this.ofAutoSaveTicks);
         printWriter.println("ofBetterGrass:" + this.ofBetterGrass);
         printWriter.println("ofConnectedTextures:" + this.ofConnectedTextures);
         printWriter.println("ofWeather:" + this.ofWeather);
         printWriter.println("ofSky:" + this.ofSky);
         printWriter.println("ofStars:" + this.ofStars);
         printWriter.println("ofSunMoon:" + this.ofSunMoon);
         printWriter.println("ofVignette:" + this.ofVignette);
         printWriter.println("ofChunkUpdates:" + this.ofChunkUpdates);
         printWriter.println("ofChunkUpdatesDynamic:" + this.ofChunkUpdatesDynamic);
         printWriter.println("ofTime:" + this.ofTime);
         printWriter.println("ofClearWater:" + this.ofClearWater);
         printWriter.println("ofAaLevel:" + this.ofAaLevel);
         printWriter.println("ofAfLevel:" + this.ofAfLevel);
         printWriter.println("ofProfiler:" + this.ofProfiler);
         printWriter.println("ofBetterSnow:" + this.ofBetterSnow);
         printWriter.println("ofSwampColors:" + this.ofSwampColors);
         printWriter.println("ofRandomEntities:" + this.ofRandomEntities);
         printWriter.println("ofSmoothBiomes:" + this.ofSmoothBiomes);
         printWriter.println("ofCustomFonts:" + this.ofCustomFonts);
         printWriter.println("ofCustomColors:" + this.ofCustomColors);
         printWriter.println("ofCustomItems:" + this.ofCustomItems);
         printWriter.println("ofCustomSky:" + this.ofCustomSky);
         printWriter.println("ofShowCapes:" + this.ofShowCapes);
         printWriter.println("ofNaturalTextures:" + this.ofNaturalTextures);
         printWriter.println("ofEmissiveTextures:" + this.ofEmissiveTextures);
         printWriter.println("ofLazyChunkLoading:" + this.ofLazyChunkLoading);
         printWriter.println("ofRenderRegions:" + this.ofRenderRegions);
         printWriter.println("ofSmartAnimations:" + this.ofSmartAnimations);
         printWriter.println("ofDynamicFov:" + this.ofDynamicFov);
         printWriter.println("ofAlternateBlocks:" + this.ofAlternateBlocks);
         printWriter.println("ofDynamicLights:" + this.ofDynamicLights);
         printWriter.println("ofScreenshotSize:" + this.ofScreenshotSize);
         printWriter.println("ofCustomEntityModels:" + this.ofCustomEntityModels);
         printWriter.println("ofCustomGuis:" + this.ofCustomGuis);
         printWriter.println("ofShowGlErrors:" + this.ofShowGlErrors);
         printWriter.println("ofFullscreenMode:" + this.ofFullscreenMode);
         printWriter.println("ofFastMath:" + this.ofFastMath);
         printWriter.println("ofFastRender:" + this.ofFastRender);
         printWriter.println("ofTranslucentBlocks:" + this.ofTranslucentBlocks);
         printWriter.println("key_" + this.ofKeyBindZoom.getKeyDescription() + ":" + this.ofKeyBindZoom.getKeyCode());
         printWriter.close();
      } catch (Exception e) {
         Config.warn("Failed to save options");
         e.printStackTrace();
      }
   }

   public void updateRenderClouds() {
      switch (this.ofClouds) {
         case 1:
            this.clouds = 1;
            break;
         case 2:
            this.clouds = 2;
            break;
         case 3:
            this.clouds = 0;
            break;
         default:
            if (this.fancyGraphics) {
               this.clouds = 2;
            } else {
               this.clouds = 1;
            }
      }
   }

   public void resetSettings() {
      this.renderDistanceChunks = 8;
      this.viewBobbing = true;
      this.anaglyph = false;
      this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
      this.enableVsync = false;
      this.updateVSync();
      this.mipmapLevels = 4;
      this.fancyGraphics = true;
      this.ambientOcclusion = 2;
      this.clouds = 2;
      this.fovSetting = 70.0F;
      this.gammaSetting = 0.0F;
      this.guiScale = 0;
      this.particleSetting = 0;
      this.heldItemTooltips = true;
      this.useVbo = false;
      this.forceUnicodeFont = false;
      this.ofFogType = 1;
      this.ofFogStart = 0.8F;
      this.ofMipmapType = 0;
      this.ofOcclusionFancy = false;
      this.ofSmartAnimations = false;
      this.ofSmoothFps = false;
      Config.updateAvailableProcessors();
      this.ofSmoothWorld = Config.isSingleProcessor();
      this.ofLazyChunkLoading = false;
      this.ofRenderRegions = false;
      this.ofFastMath = false;
      this.ofFastRender = false;
      this.ofTranslucentBlocks = 0;
      this.ofDynamicFov = true;
      this.ofAlternateBlocks = true;
      this.ofDynamicLights = 3;
      this.ofScreenshotSize = 1;
      this.ofCustomEntityModels = true;
      this.ofCustomGuis = true;
      this.ofShowGlErrors = true;
      this.ofAoLevel = 1.0F;
      this.ofAaLevel = 0;
      this.ofAfLevel = 1;
      this.ofClouds = 0;
      this.ofCloudsHeight = 0.0F;
      this.ofTrees = 0;
      this.ofRain = 0;
      this.ofBetterGrass = 3;
      this.ofAutoSaveTicks = 4000;
      this.ofLagometer = false;
      this.ofShowFps = false;
      this.ofProfiler = false;
      this.ofWeather = true;
      this.ofSky = true;
      this.ofStars = true;
      this.ofSunMoon = true;
      this.ofVignette = 0;
      this.ofChunkUpdates = 1;
      this.ofChunkUpdatesDynamic = false;
      this.ofTime = 0;
      this.ofClearWater = false;
      this.ofBetterSnow = false;
      this.ofFullscreenMode = "Default";
      this.ofSwampColors = true;
      this.ofRandomEntities = true;
      this.ofSmoothBiomes = true;
      this.ofCustomFonts = true;
      this.ofCustomColors = true;
      this.ofCustomItems = true;
      this.ofCustomSky = true;
      this.ofShowCapes = true;
      this.ofConnectedTextures = 2;
      this.ofNaturalTextures = false;
      this.ofEmissiveTextures = true;
      this.ofAnimatedWater = 0;
      this.ofAnimatedLava = 0;
      this.ofAnimatedFire = true;
      this.ofAnimatedPortal = true;
      this.ofAnimatedRedstone = true;
      this.ofAnimatedExplosion = true;
      this.ofAnimatedFlame = true;
      this.ofAnimatedSmoke = true;
      this.ofVoidParticles = true;
      this.ofWaterParticles = true;
      this.ofRainSplash = true;
      this.ofPortalParticles = true;
      this.ofPotionParticles = true;
      this.ofFireworkParticles = true;
      this.ofDrippingWaterLava = true;
      this.ofAnimatedTerrain = true;
      this.ofAnimatedTextures = true;
      Shaders.setShaderPack("OFF");
      Shaders.configAntialiasingLevel = 0;
      Shaders.uninit();
      Shaders.storeConfig();
      this.updateWaterOpacity();
      this.mc.refreshResources();
      this.saveOptions();
   }

   public void updateVSync() {
      Display.setVSyncEnabled(this.enableVsync);
   }

   public void updateWaterOpacity() {
      if (Config.isIntegratedServerRunning()) {
         Config.waterOpacityChanged = true;
      }

      ClearWater.updateWaterOpacity(this, this.mc.theWorld);
   }

   public void setAllAnimations(boolean flag) {
      int n = flag ? 0 : 2;
      this.ofAnimatedWater = n;
      this.ofAnimatedLava = n;
      this.ofAnimatedFire = flag;
      this.ofAnimatedPortal = flag;
      this.ofAnimatedRedstone = flag;
      this.ofAnimatedExplosion = flag;
      this.ofAnimatedFlame = flag;
      this.ofAnimatedSmoke = flag;
      this.ofVoidParticles = flag;
      this.ofWaterParticles = flag;
      this.ofRainSplash = flag;
      this.ofPortalParticles = flag;
      this.ofPotionParticles = flag;
      this.ofFireworkParticles = flag;
      this.particleSetting = flag ? 0 : 2;
      this.ofDrippingWaterLava = flag;
      this.ofAnimatedTerrain = flag;
      this.ofAnimatedTextures = flag;
   }

   public static int nextValue(int n, int[] ints) {
      int indexOf2 = indexOf(n, ints);
      if (indexOf2 < 0) {
         return ints[0];
      }

      if (++indexOf2 >= ints.length) {
         indexOf2 = 0;
      }

      return ints[indexOf2];
   }

   public static int limit(int n, int[] ints) {
      int indexOf2 = indexOf(n, ints);
      return indexOf2 < 0 ? ints[0] : n;
   }

   public static int indexOf(int n, int[] ints) {
      for (int i = 0; i < ints.length; i++) {
         if (ints[i] == n) {
            return i;
         }
      }

      return -1;
   }

   public enum Options {
      INVERT_MOUSE("options.invertMouse", false, true),
      SENSITIVITY("options.sensitivity", true, false),
      FOV("options.fov", true, false, 30.0F, 110.0F, 1.0F),
      GAMMA("options.gamma", true, false),
      SATURATION("options.saturation", true, false),
      RENDER_DISTANCE("options.renderDistance", true, false, 2.0F, 16.0F, 1.0F),
      VIEW_BOBBING("options.viewBobbing", false, true),
      ANAGLYPH("options.anaglyph", false, true),
      FRAMERATE_LIMIT("options.framerateLimit", true, false, 0.0F, 260.0F, 5.0F),
      FBO_ENABLE("options.fboEnable", false, true),
      RENDER_CLOUDS("options.renderClouds", false, false),
      GRAPHICS("options.graphics", false, false),
      AMBIENT_OCCLUSION("options.ao", false, false),
      GUI_SCALE("options.guiScale", false, false),
      PARTICLES("options.particles", false, false),
      CHAT_VISIBILITY("options.chat.visibility", false, false),
      CHAT_COLOR("options.chat.color", false, true),
      CHAT_LINKS("options.chat.links", false, true),
      CHAT_OPACITY("options.chat.opacity", true, false),
      CHAT_LINKS_PROMPT("options.chat.links.prompt", false, true),
      SNOOPER_ENABLED("options.snooper", false, true),
      USE_FULLSCREEN("options.fullscreen", false, true),
      ENABLE_VSYNC("options.vsync", false, true),
      USE_VBO("options.vbo", false, true),
      TOUCHSCREEN("options.touchscreen", false, true),
      CHAT_SCALE("options.chat.scale", true, false),
      CHAT_WIDTH("options.chat.width", true, false),
      CHAT_HEIGHT_FOCUSED("options.chat.height.focused", true, false),
      CHAT_HEIGHT_UNFOCUSED("options.chat.height.unfocused", true, false),
      MIPMAP_LEVELS("options.mipmapLevels", true, false, 0.0F, 4.0F, 1.0F),
      FORCE_UNICODE_FONT("options.forceUnicodeFont", false, true),
      STREAM_BYTES_PER_PIXEL("options.stream.bytesPerPixel", true, false),
      STREAM_VOLUME_MIC("options.stream.micVolumne", true, false),
      STREAM_VOLUME_SYSTEM("options.stream.systemVolume", true, false),
      STREAM_KBPS("options.stream.kbps", true, false),
      STREAM_FPS("options.stream.fps", true, false),
      STREAM_COMPRESSION("options.stream.compression", false, false),
      STREAM_SEND_METADATA("options.stream.sendMetadata", false, true),
      STREAM_CHAT_ENABLED("options.stream.chat.enabled", false, false),
      STREAM_CHAT_USER_FILTER("options.stream.chat.userFilter", false, false),
      STREAM_MIC_TOGGLE_BEHAVIOR("options.stream.micToggleBehavior", false, false),
      BLOCK_ALTERNATIVES("options.blockAlternatives", false, true),
      REDUCED_DEBUG_INFO("options.reducedDebugInfo", false, true),
      ENTITY_SHADOWS("options.entityShadows", false, true),
      REALMS_NOTIFICATIONS("options.realmsNotifications", false, true),
      FOG_FANCY("of.options.FOG_FANCY", false, false),
      FOG_START("of.options.FOG_START", false, false),
      MIPMAP_TYPE("of.options.MIPMAP_TYPE", true, false, 0.0F, 3.0F, 1.0F),
      SMOOTH_FPS("of.options.SMOOTH_FPS", false, false),
      CLOUDS("of.options.CLOUDS", false, false),
      CLOUD_HEIGHT("of.options.CLOUD_HEIGHT", true, false),
      TREES("of.options.TREES", false, false),
      RAIN("of.options.RAIN", false, false),
      ANIMATED_WATER("of.options.ANIMATED_WATER", false, false),
      ANIMATED_LAVA("of.options.ANIMATED_LAVA", false, false),
      ANIMATED_FIRE("of.options.ANIMATED_FIRE", false, false),
      ANIMATED_PORTAL("of.options.ANIMATED_PORTAL", false, false),
      AO_LEVEL("of.options.AO_LEVEL", true, false),
      LAGOMETER("of.options.LAGOMETER", false, false),
      SHOW_FPS("of.options.SHOW_FPS", false, false),
      AUTOSAVE_TICKS("of.options.AUTOSAVE_TICKS", false, false),
      BETTER_GRASS("of.options.BETTER_GRASS", false, false),
      ANIMATED_REDSTONE("of.options.ANIMATED_REDSTONE", false, false),
      ANIMATED_EXPLOSION("of.options.ANIMATED_EXPLOSION", false, false),
      ANIMATED_FLAME("of.options.ANIMATED_FLAME", false, false),
      ANIMATED_SMOKE("of.options.ANIMATED_SMOKE", false, false),
      WEATHER("of.options.WEATHER", false, false),
      SKY("of.options.SKY", false, false),
      STARS("of.options.STARS", false, false),
      SUN_MOON("of.options.SUN_MOON", false, false),
      VIGNETTE("of.options.VIGNETTE", false, false),
      CHUNK_UPDATES("of.options.CHUNK_UPDATES", false, false),
      CHUNK_UPDATES_DYNAMIC("of.options.CHUNK_UPDATES_DYNAMIC", false, false),
      TIME("of.options.TIME", false, false),
      CLEAR_WATER("of.options.CLEAR_WATER", false, false),
      SMOOTH_WORLD("of.options.SMOOTH_WORLD", false, false),
      VOID_PARTICLES("of.options.VOID_PARTICLES", false, false),
      WATER_PARTICLES("of.options.WATER_PARTICLES", false, false),
      RAIN_SPLASH("of.options.RAIN_SPLASH", false, false),
      PORTAL_PARTICLES("of.options.PORTAL_PARTICLES", false, false),
      POTION_PARTICLES("of.options.POTION_PARTICLES", false, false),
      FIREWORK_PARTICLES("of.options.FIREWORK_PARTICLES", false, false),
      PROFILER("of.options.PROFILER", false, false),
      DRIPPING_WATER_LAVA("of.options.DRIPPING_WATER_LAVA", false, false),
      BETTER_SNOW("of.options.BETTER_SNOW", false, false),
      FULLSCREEN_MODE("of.options.FULLSCREEN_MODE", true, false, 0.0F, Config.getDisplayModes().length, 1.0F),
      ANIMATED_TERRAIN("of.options.ANIMATED_TERRAIN", false, false),
      SWAMP_COLORS("of.options.SWAMP_COLORS", false, false),
      RANDOM_ENTITIES("of.options.RANDOM_ENTITIES", false, false),
      SMOOTH_BIOMES("of.options.SMOOTH_BIOMES", false, false),
      CUSTOM_FONTS("of.options.CUSTOM_FONTS", false, false),
      CUSTOM_COLORS("of.options.CUSTOM_COLORS", false, false),
      SHOW_CAPES("of.options.SHOW_CAPES", false, false),
      CONNECTED_TEXTURES("of.options.CONNECTED_TEXTURES", false, false),
      CUSTOM_ITEMS("of.options.CUSTOM_ITEMS", false, false),
      AA_LEVEL("of.options.AA_LEVEL", true, false, 0.0F, 16.0F, 1.0F),
      AF_LEVEL("of.options.AF_LEVEL", true, false, 1.0F, 16.0F, 1.0F),
      ANIMATED_TEXTURES("of.options.ANIMATED_TEXTURES", false, false),
      NATURAL_TEXTURES("of.options.NATURAL_TEXTURES", false, false),
      EMISSIVE_TEXTURES("of.options.EMISSIVE_TEXTURES", false, false),
      HELD_ITEM_TOOLTIPS("of.options.HELD_ITEM_TOOLTIPS", false, false),
      DROPPED_ITEMS("of.options.DROPPED_ITEMS", false, false),
      LAZY_CHUNK_LOADING("of.options.LAZY_CHUNK_LOADING", false, false),
      CUSTOM_SKY("of.options.CUSTOM_SKY", false, false),
      FAST_MATH("of.options.FAST_MATH", false, false),
      FAST_RENDER("of.options.FAST_RENDER", false, false),
      TRANSLUCENT_BLOCKS("of.options.TRANSLUCENT_BLOCKS", false, false),
      DYNAMIC_FOV("of.options.DYNAMIC_FOV", false, false),
      DYNAMIC_LIGHTS("of.options.DYNAMIC_LIGHTS", false, false),
      ALTERNATE_BLOCKS("of.options.ALTERNATE_BLOCKS", false, false),
      CUSTOM_ENTITY_MODELS("of.options.CUSTOM_ENTITY_MODELS", false, false),
      ADVANCED_TOOLTIPS("of.options.ADVANCED_TOOLTIPS", false, false),
      SCREENSHOT_SIZE("of.options.SCREENSHOT_SIZE", false, false),
      CUSTOM_GUIS("of.options.CUSTOM_GUIS", false, false),
      RENDER_REGIONS("of.options.RENDER_REGIONS", false, false),
      SHOW_GL_ERRORS("of.options.SHOW_GL_ERRORS", false, false),
      SMART_ANIMATIONS("of.options.SMART_ANIMATIONS", false, false);

      public String enumString;
      public boolean enumFloat;
      public boolean enumBoolean;
      public float valueMin;
      public float valueMax;
      public float valueStep;

      public static GameSettings.Options getEnumOptions(int ordinal) {
         for (GameSettings.Options options : values()) {
            if (options.returnEnumOrdinal() == ordinal) {
               return options;
            }
         }

         return null;
      }

      Options(String str, boolean isFloat, boolean isBoolean) {
         this(str, isFloat, isBoolean, 0.0F, 1.0F, 0.0F);
      }

      Options(String str, boolean isFloat, boolean isBoolean, float valMin, float valMax, float valStep) {
         this.enumString = str;
         this.enumFloat = isFloat;
         this.enumBoolean = isBoolean;
         this.valueMin = valMin;
         this.valueMax = valMax;
         this.valueStep = valStep;
      }

      public boolean getEnumFloat() {
         return this.enumFloat;
      }

      public boolean getEnumBoolean() {
         return this.enumBoolean;
      }

      public int returnEnumOrdinal() {
         return this.ordinal();
      }

      public String getEnumString() {
         return this.enumString;
      }

      public float getValueMax() {
         return this.valueMax;
      }

      public void setValueMax(float value) {
         this.valueMax = value;
      }

      public float normalizeValue(float value) {
         return MathHelper.clamp_float((this.snapToStepClamp(value) - this.valueMin) / (this.valueMax - this.valueMin), 0.0F, 1.0F);
      }

      public float denormalizeValue(float value) {
         return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin) * MathHelper.clamp_float(value, 0.0F, 1.0F));
      }

      public float snapToStepClamp(float value) {
         value = this.snapToStep(value);
         return MathHelper.clamp_float(value, this.valueMin, this.valueMax);
      }

      public float snapToStep(float value) {
         if (this.valueStep > 0.0F) {
            value = this.valueStep * Math.round(value / this.valueStep);
         }

         return value;
      }
   }
}
