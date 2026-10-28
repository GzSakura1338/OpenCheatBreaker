package net.minecraft.client.settings;

import com.cheatbreaker.client.CheatBreaker;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundCategory;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer.EnumChatVisibility;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.network.play.client.C15PacketClientSettings;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumDifficulty;
import net.optifine.ClearWater;
import net.optifine.Config;
import net.optifine.CustomColorizer;
import net.optifine.CustomSky;
import net.optifine.DynamicLights;
import net.optifine.IWrUpdater;
import net.optifine.Lang;
import net.optifine.NaturalTextures;
import net.optifine.RandomMobs;
import net.optifine.Reflector;
import net.optifine.TextureUtils;
import net.optifine.WrUpdaterSmooth;
import net.optifine.WrUpdaterThreaded;
import net.optifine.WrUpdates;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import shadersmod.client.Shaders;

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
    public float mouseSensitivity = 0.5F;
    public boolean invertMouse;
    public int renderDistanceChunks = -1;
    public boolean viewBobbing = true;
    public boolean anaglyph;
    public boolean advancedOpengl;
    public boolean fboEnable = true;
    public int limitFramerate = 120;
    public boolean fancyGraphics = true;
    public int ambientOcclusion = 2;
    public int ofFogType = 1;
    public float ofFogStart = 0.8F;
    public int ofMipmapType = 0;
    public boolean ofLoadFar = false;
    public int ofPreloadedChunks = 0;
    public boolean ofOcclusionFancy = false;
    public boolean ofSmoothFps = false;
    public boolean ofSmoothWorld = Config.isSingleProcessor();
    public boolean ofLazyChunkLoading = Config.isSingleProcessor();
    public float ofAoLevel = 1.0F;
    public int ofAaLevel = 0;
    public int ofClouds = 0;
    public float ofCloudsHeight = 0.0F;
    public int ofTrees = 0;
    public int ofGrass = 0;
    public int ofRain = 0;
    public int ofWater = 0;
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
    public int ofChunkLoading = 0;
    public boolean ofChunkUpdatesDynamic = false;
    public int ofTime = 0;
    public boolean ofClearWater = false;
    public boolean ofDepthFog = true;
    public boolean ofBetterSnow = false;
    public String ofFullscreenMode = "Default";
    public boolean ofSwampColors = true;
    public boolean ofRandomMobs = true;
    public boolean ofSmoothBiomes = true;
    public boolean ofCustomFonts = true;
    public boolean ofCustomColors = true;
    public boolean ofCustomSky = true;
    public boolean ofShowCapes = true;
    public int ofConnectedTextures = 2;
    public boolean ofNaturalTextures = false;
    public boolean ofFastMath = false;
    public boolean ofFastRender = false;
    public int ofTranslucentBlocks = 2;
    public boolean ofDynamicFov = true;
    public int ofDynamicLights = 3;
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
    public boolean ofDrippingWaterLava = true;
    public boolean ofAnimatedTerrain = true;
    public boolean ofAnimatedItems = true;
    public boolean ofAnimatedTextures = true;
    public static final int OFF = 3;
    public static final String DEFAULT_STR = "Default";
    public static int[] OF_DYNAMIC_LIGHTS = new int[]{3, 1, 2};
    public static String[] KEYS_DYNAMIC_LIGHTS = new String[]{"options.off", "options.graphics.fast", "options.graphics.fancy"};
    public boolean clouds = true;
    public List<String> resourcePacks = new ArrayList<>();
    public Set<EnumPlayerModelParts> enumPlayerModelPartsSet = new HashSet<>(Arrays.asList(EnumPlayerModelParts.values()));
    public EnumChatVisibility chatVisibility = EnumChatVisibility.FULL;
    public boolean chatColours = true;
    public boolean chatLinks = true;
    public boolean chatLinksPrompt = true;
    public float chatOpacity = 1.0F;
    public boolean snooperEnabled = true;
    public boolean enableVsync = true;
    public boolean fullScreen;
    public boolean pauseOnLostFocus = true;
    public boolean hideServerAddress;
    public boolean advancedItemTooltips;
    public boolean showCape = true;
    public boolean heldItemTooltips = true;
    public boolean touchscreen;
    public int overrideWidth;
    public int overrideHeight;
    public float chatScale = 1.0F;
    public float chatWidth = 1.0F;
    public float chatHeightUnfocused = 0.44366196F;
    public float chatHeightFocused = 1.0F;
    public boolean showInventoryAchievementHint = true;
    public int mipmapLevels = 4;
    public int anisotropicFiltering = 1;
    public Map mapSoundLevels = Maps.newEnumMap(SoundCategory.class);
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
    public KeyBinding keyBindForward = new KeyBinding("key.forward", 17, "key.categories.movement");
    public KeyBinding keyBindLeft = new KeyBinding("key.left", 30, "key.categories.movement");
    public KeyBinding keyBindBack = new KeyBinding("key.back", 31, "key.categories.movement");
    public KeyBinding keyBindRight = new KeyBinding("key.right", 32, "key.categories.movement");
    public KeyBinding keyBindJump = new KeyBinding("key.jump", 57, "key.categories.movement");
    public KeyBinding keyBindSneak = new KeyBinding("key.sneak", 42, "key.categories.movement");
    public KeyBinding keyBindInventory = new KeyBinding("key.inventory", 18, "key.categories.inventory");
    public KeyBinding keyBindUseItem = new KeyBinding("key.use", -99, "key.categories.gameplay");
    public KeyBinding keyBindDrop = new KeyBinding("key.drop", 16, "key.categories.gameplay");
    public KeyBinding keyBindAttack = new KeyBinding("key.attack", -100, "key.categories.gameplay");
    public KeyBinding keyBindPickBlock = new KeyBinding("key.pickItem", -98, "key.categories.gameplay");
    public KeyBinding keyBindSprint = new KeyBinding("key.sprint", 29, "key.categories.gameplay");
    public KeyBinding keyBindChat = new KeyBinding("key.chat", 20, "key.categories.multiplayer");
    public KeyBinding keyBindPlayerList = new KeyBinding("key.playerlist", 15, "key.categories.multiplayer");
    public KeyBinding keyBindCommand = new KeyBinding("key.command", 53, "key.categories.multiplayer");
    public KeyBinding keyBindScreenshot = new KeyBinding("key.screenshot", 60, "key.categories.misc");
    public KeyBinding keyBindTogglePerspective = new KeyBinding("key.togglePerspective", 63, "key.categories.misc");
    public KeyBinding keyBindSmoothCamera = new KeyBinding("key.smoothCamera", 0, "key.categories.misc");
    public KeyBinding keyBindStreamStartStop = new KeyBinding("key.fullscreen", 87, "key.categories.misc");
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
            this.keyBindDrop,
            this.keyBindInventory,
            this.keyBindChat,
            this.keyBindPlayerList,
            this.keyBindPickBlock,
            this.keyBindCommand,
            this.keyBindScreenshot,
            this.keyBindTogglePerspective,
            this.keyBindSmoothCamera,
            this.keyBindSprint,
            this.keyBindStreamStartStop
        },
        this.keyBindsHotbar
    );
    public EnumDifficulty difficulty = EnumDifficulty.NORMAL;
    public String lastServer = "";
    public float noclipRate = 1.0F;
    public float debugCamRate = 1.0F;
    public boolean hideGUI;
    public int thirdPersonView;
    public boolean showDebugInfo;
    public boolean showDebugProfilerChart;
    public float fovSetting = 70.0F;
    public boolean noclip;
    public boolean smoothCamera;
    public boolean debugCamEnable;
    public String language = "en_US";
    public boolean forceUnicodeFont = false;
    public Minecraft mc;
    public float gammaSetting;
    public float saturation;
    public int guiScale;
    public int particleSetting;
    public File optionsFile;
    public File optionsFileOF;
    public KeyBinding ofKeyBindZoom;
    public static final int CONSTANT_1 = 1;
    public static final int CONSTANT_0 = 0;
    public static final int CONSTANT_0_2 = 0;
    public static final int CONSTANT_2 = 2;
    public static final int CONSTANT_1_2 = 1;
    public static final int CONSTANT_2_2 = 2;
    public static final int CONSTANT_1_3 = 1;
    public static final int CONSTANT_2_3 = 2;
    public static final int CONSTANT_0_3 = 0;

    public GameSettings(Minecraft mc, File file) {
        this.mc = mc;
        this.optionsFile = new File(file, "options.txt");
        this.optionsFileOF = new File(file, "optionsof.txt");
        this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
        this.ofKeyBindZoom = new KeyBinding("of.key.zoom", 46, "key.categories.misc");
        this.keyBindings = (KeyBinding[])ArrayUtils.add(this.keyBindings, this.ofKeyBindZoom);
        GameSettings.Options.RENDER_DISTANCE.setValueMax(32.0F);
        this.renderDistanceChunks = mc.isJava64bit() ? 12 : 8;
        this.loadOptions();
        Config.initGameSettings(this);
    }

    public GameSettings() {
        this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
        this.ofKeyBindZoom = new KeyBinding("of.key.zoom", 46, "key.categories.misc");
        this.keyBindings = (KeyBinding[])ArrayUtils.add(this.keyBindings, this.ofKeyBindZoom);
    }

    public static String getKeyDisplayString(int n) {
        if (n < 0) {
            return I18n.format("key.mouseButton", n + 101);
        }

        String keyName = Keyboard.getKeyName(n);
        return keyName != null ? keyName : "KEY_" + n;
    }

    public static boolean isKeyDown(KeyBinding keyBinding) {
        return keyBinding.getKeyCode() == 0 ? false : (keyBinding.getKeyCode() < 0 ? Mouse.isButtonDown(keyBinding.getKeyCode() + 100) : Keyboard.isKeyDown(keyBinding.getKeyCode()));
    }

    public void setOptionKeyBinding(KeyBinding keyBinding, int n) {
        keyBinding.setKeyCode(n);
        keyBinding.run2();
        this.saveOptions();
    }

    public void setOptionFloatValue(GameSettings.Options options, float f) throws java.io.IOException {
        if (options == GameSettings.Options.CLOUD_HEIGHT) {
            this.ofCloudsHeight = f;
        }

        if (options == GameSettings.Options.AO_LEVEL) {
            this.ofAoLevel = f;
            this.mc.renderGlobal.loadRenderers();
        }

        if (options == GameSettings.Options.MIPMAP_TYPE) {
            int n = (int)f;
            this.ofMipmapType = Config.limit(n, 0, 3);
            TextureUtils.refreshBlockTextures();
        }

        if (options == GameSettings.Options.SENSITIVITY) {
            this.mouseSensitivity = f;
        }

        if (options == GameSettings.Options.FOV) {
            this.fovSetting = f;
        }

        if (options == GameSettings.Options.GAMMA) {
            CheatBreaker.getInstance().getKeybindSettings().fullbright.changeValue(false);
            this.gammaSetting = f;
        }

        if (options == GameSettings.Options.FRAMERATE_LIMIT) {
            this.limitFramerate = (int)f;
            this.enableVsync = false;
            if (this.limitFramerate <= 0) {
                this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                this.enableVsync = true;
            }

            this.updateVSync();
        }

        if (options == GameSettings.Options.CHAT_OPACITY) {
            this.chatOpacity = f;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (options == GameSettings.Options.CHAT_HEIGHT_FOCUSED) {
            this.chatHeightFocused = f;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (options == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED) {
            this.chatHeightUnfocused = f;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (options == GameSettings.Options.CHAT_WIDTH) {
            this.chatWidth = f;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (options == GameSettings.Options.CHAT_SCALE) {
            this.chatScale = f;
            this.mc.ingameGUI.getChatGUI().refreshChat();
        }

        if (options == GameSettings.Options.ANISOTROPIC_FILTERING) {
            int anisotropicFiltering = this.anisotropicFiltering;
            if (anisotropicFiltering > 1 && Config.isShaders()) {
                Config.showGuiMessage(Lang.get("of.message.af.shaders1"), Lang.get("of.message.af.shaders2"));
                return;
            }

            this.anisotropicFiltering = (int)f;
            if (anisotropicFiltering != f) {
                this.mc.getTextureMapBlocks().setAnisotropicFiltering(this.anisotropicFiltering);
                this.mc.scheduleResourcesRefresh();
            }
        }

        if (options == GameSettings.Options.MIPMAP_LEVELS) {
            int mipmapLevels = this.mipmapLevels;
            this.mipmapLevels = (int)f;
            if (mipmapLevels != f) {
                this.mc.getTextureMapBlocks().setMipmapLevels(this.mipmapLevels);
                this.mc.scheduleResourcesRefresh();
            }
        }

        if (options == GameSettings.Options.RENDER_DISTANCE) {
            this.renderDistanceChunks = (int)f;
        }

        if (options == GameSettings.Options.STREAM_BYTES_PER_PIXEL) {
            this.streamBytesPerPixel = f;
        }

        if (options == GameSettings.Options.STREAM_VOLUME_MIC) {
            this.streamMicVolume = f;
        }

        if (options == GameSettings.Options.STREAM_VOLUME_SYSTEM) {
            this.streamGameVolume = f;
        }

        if (options == GameSettings.Options.STREAM_KBPS) {
            this.streamKbps = f;
        }

        if (options == GameSettings.Options.STREAM_FPS) {
            this.streamFps = f;
        }
    }

    public void setOptionValue(GameSettings.Options options, int n) throws java.io.IOException {
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

        if (options == GameSettings.Options.LOAD_FAR) {
            this.ofLoadFar = !this.ofLoadFar;
            this.mc.renderGlobal.loadRenderers();
        }

        if (options == GameSettings.Options.PRELOADED_CHUNKS) {
            this.ofPreloadedChunks += 2;
            if (this.ofPreloadedChunks > 8) {
                this.ofPreloadedChunks = 0;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (options == GameSettings.Options.SMOOTH_FPS) {
            this.ofSmoothFps = !this.ofSmoothFps;
        }

        if (options == GameSettings.Options.SMOOTH_WORLD) {
            this.ofSmoothWorld = !this.ofSmoothWorld;
            Config.updateAvailableProcessors();
            if (!Config.isSingleProcessor()) {
                this.ofSmoothWorld = false;
            }

            Config.updateThreadPriorities();
        }

        if (options == GameSettings.Options.CLOUDS) {
            this.ofClouds++;
            if (this.ofClouds > 3) {
                this.ofClouds = 0;
            }
        }

        if (options == GameSettings.Options.TREES) {
            this.ofTrees++;
            if (this.ofTrees > 2) {
                this.ofTrees = 0;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (options == GameSettings.Options.GRASS) {
            this.ofGrass++;
            if (this.ofGrass > 2) {
                this.ofGrass = 0;
            }

            RenderBlocks.fancyGrass = Config.isGrassFancy();
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

        if (options == GameSettings.Options.WATER) {
            this.ofWater++;
            if (this.ofWater > 2) {
                this.ofWater = 0;
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

        if (options == GameSettings.Options.DRIPPING_WATER_LAVA) {
            this.ofDrippingWaterLava = !this.ofDrippingWaterLava;
        }

        if (options == GameSettings.Options.ANIMATED_TERRAIN) {
            this.ofAnimatedTerrain = !this.ofAnimatedTerrain;
        }

        if (options == GameSettings.Options.ANIMATED_TEXTURES) {
            this.ofAnimatedTextures = !this.ofAnimatedTextures;
        }

        if (options == GameSettings.Options.ANIMATED_ITEMS) {
            this.ofAnimatedItems = !this.ofAnimatedItems;
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
            this.ofAutoSaveTicks *= 10;
            if (this.ofAutoSaveTicks > 40000) {
                this.ofAutoSaveTicks = 40;
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

            this.mc.renderGlobal.loadRenderers();
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

        if (options == GameSettings.Options.CHUNK_LOADING) {
        }

        if (options == GameSettings.Options.CHUNK_UPDATES_DYNAMIC) {
            this.ofChunkUpdatesDynamic = !this.ofChunkUpdatesDynamic;
        }

        if (options == GameSettings.Options.TIME) {
            this.ofTime++;
            if (this.ofTime > 3) {
                this.ofTime = 0;
            }
        }

        if (options == GameSettings.Options.CLEAR_WATER) {
            this.ofClearWater = !this.ofClearWater;
            this.updateWaterOpacity();
        }

        if (options == GameSettings.Options.DEPTH_FOG) {
            this.ofDepthFog = !this.ofDepthFog;
        }

        if (options == GameSettings.Options.AA_LEVEL) {
            this.ofAaLevel = 0;
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
            CustomColorizer.updateUseDefaultColorMultiplier();
            this.mc.renderGlobal.loadRenderers();
        }

        if (options == GameSettings.Options.RANDOM_MOBS) {
            this.ofRandomMobs = !this.ofRandomMobs;
            RandomMobs.resetTextures();
        }

        if (options == GameSettings.Options.SMOOTH_BIOMES) {
            this.ofSmoothBiomes = !this.ofSmoothBiomes;
            CustomColorizer.updateUseDefaultColorMultiplier();
            this.mc.renderGlobal.loadRenderers();
        }

        if (options == GameSettings.Options.CUSTOM_FONTS) {
            this.ofCustomFonts = !this.ofCustomFonts;
            this.mc.fontRendererObj.onResourceManagerReload(Config.getResourceManager());
            this.mc.standardGalacticFontRenderer.onResourceManagerReload(Config.getResourceManager());
        }

        if (options == GameSettings.Options.CUSTOM_COLORS) {
            this.ofCustomColors = !this.ofCustomColors;
            CustomColorizer.update();
            this.mc.renderGlobal.loadRenderers();
        }

        if (options == GameSettings.Options.CUSTOM_SKY) {
            this.ofCustomSky = !this.ofCustomSky;
            CustomSky.update();
        }

        if (options == GameSettings.Options.SHOW_CAPES) {
            this.ofShowCapes = !this.ofShowCapes;
            this.mc.renderGlobal.updateCapes();
        }

        if (options == GameSettings.Options.NATURAL_TEXTURES) {
            this.ofNaturalTextures = !this.ofNaturalTextures;
            NaturalTextures.update();
            this.mc.renderGlobal.loadRenderers();
        }

        if (options == GameSettings.Options.FAST_MATH) {
            this.ofFastMath = !this.ofFastMath;
            MathHelper.fastMath = this.ofFastMath;
        }

        if (options == GameSettings.Options.FAST_RENDER) {
            this.ofFastRender = !this.ofFastRender;
            Config.updateFramebufferSize();
            this.mc.renderGlobal.loadRenderers();
        }

        if (options == GameSettings.Options.TRANSLUCENT_BLOCKS) {
            if (this.ofTranslucentBlocks == 1) {
                this.ofTranslucentBlocks = 2;
            } else {
                this.ofTranslucentBlocks = 1;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (options == GameSettings.Options.LAZY_CHUNK_LOADING) {
            this.ofLazyChunkLoading = !this.ofLazyChunkLoading;
            Config.updateAvailableProcessors();
            if (!Config.isSingleProcessor()) {
                this.ofLazyChunkLoading = false;
            }

            this.mc.renderGlobal.loadRenderers();
        }

        if (options == GameSettings.Options.FULLSCREEN_MODE) {
            List asList = Arrays.asList(Config.getDisplayModeNames());
            if (this.ofFullscreenMode.equals("Default")) {
                this.ofFullscreenMode = (String)asList.get(0);
            } else {
                int indexOf = asList.indexOf(this.ofFullscreenMode);
                if (indexOf < 0) {
                    this.ofFullscreenMode = "Default";
                } else if (++indexOf >= asList.size()) {
                    this.ofFullscreenMode = "Default";
                } else {
                    this.ofFullscreenMode = (String)asList.get(indexOf);
                }
            }
        }

        if (options == GameSettings.Options.DYNAMIC_FOV) {
            this.ofDynamicFov = !this.ofDynamicFov;
        }

        if (options == GameSettings.Options.DYNAMIC_LIGHTS) {
            this.ofDynamicLights = nextValue(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
            DynamicLights.removeLights(this.mc.renderGlobal);
        }

        if (options == GameSettings.Options.HELD_ITEM_TOOLTIPS) {
            this.heldItemTooltips = !this.heldItemTooltips;
        }

        if (options == GameSettings.Options.INVERT_MOUSE) {
            this.invertMouse = !this.invertMouse;
        }

        if (options == GameSettings.Options.GUI_SCALE) {
            this.guiScale += n;
            if (GuiScreen.isShiftKeyDown()) {
                this.guiScale = 0;
            }

            DisplayMode largestDisplayMode = Config.getLargestDisplayMode();
            int n2 = largestDisplayMode.getWidth() / 320;
            int n3 = largestDisplayMode.getHeight() / 240;
            int min = Math.min(n2, n3);
            if (this.guiScale < 0) {
                this.guiScale = min - 1;
            }

            if (this.mc.isUnicode() && this.guiScale % 2 != 0) {
                this.guiScale += n;
            }

            if (this.guiScale < 0 || this.guiScale >= min) {
                this.guiScale = 0;
            }
        }

        if (options == GameSettings.Options.PARTICLES) {
            this.particleSetting = (this.particleSetting + n) % 3;
        }

        if (options == GameSettings.Options.VIEW_BOBBING) {
            this.viewBobbing = !this.viewBobbing;
        }

        if (options == GameSettings.Options.RENDER_CLOUDS) {
            this.clouds = !this.clouds;
        }

        if (options == GameSettings.Options.FORCE_UNICODE_FONT) {
            this.forceUnicodeFont = !this.forceUnicodeFont;
            this.mc.fontRendererObj.setUnicodeFlag(this.mc.getLanguageManager().isCurrentLocaleUnicode() || this.forceUnicodeFont);
        }

        if (options == GameSettings.Options.ADVANCED_OPENGL) {
            if (!Config.isOcclusionAvailable()) {
                this.ofOcclusionFancy = false;
                this.advancedOpengl = false;
            } else if (!this.advancedOpengl) {
                this.advancedOpengl = true;
                this.ofOcclusionFancy = false;
            } else if (!this.ofOcclusionFancy) {
                this.ofOcclusionFancy = true;
            } else {
                this.ofOcclusionFancy = false;
                this.advancedOpengl = false;
            }

            this.mc.renderGlobal.setAllRenderersVisible();
        }

        if (options == GameSettings.Options.FBO_ENABLE) {
            this.fboEnable = !this.fboEnable;
        }

        if (options == GameSettings.Options.ANAGLYPH) {
            if (!this.anaglyph && Config.isShaders()) {
                Config.showGuiMessage(Lang.get("of.message.an.shaders1"), Lang.get("of.message.an.shaders2"));
                return;
            }

            this.anaglyph = !this.anaglyph;
            this.mc.refreshResources();
        }

        if (options == GameSettings.Options.DIFFICULTY) {
            this.difficulty = EnumDifficulty.getDifficultyEnum(this.difficulty.getDifficultyId() + n & 3);
        }

        if (options == GameSettings.Options.GRAPHICS) {
            this.fancyGraphics = !this.fancyGraphics;
            this.mc.renderGlobal.loadRenderers();
        }

        if (options == GameSettings.Options.AMBIENT_OCCLUSION) {
            this.ambientOcclusion = (this.ambientOcclusion + n) % 3;
            this.mc.renderGlobal.loadRenderers();
        }

        if (options == GameSettings.Options.CHAT_VISIBILITY) {
            this.chatVisibility = EnumChatVisibility.getEnumChatVisibility((this.chatVisibility.getChatVisibility() + n) % 3);
        }

        if (options == GameSettings.Options.STREAM_COMPRESSION) {
            this.streamCompression = (this.streamCompression + n) % 3;
        }

        if (options == GameSettings.Options.STREAM_SEND_METADATA) {
            this.streamSendMetadata = !this.streamSendMetadata;
        }

        if (options == GameSettings.Options.STREAM_CHAT_ENABLED) {
            this.streamChatEnabled = (this.streamChatEnabled + n) % 3;
        }

        if (options == GameSettings.Options.STREAM_CHAT_USER_FILTER) {
            this.streamChatUserFilter = (this.streamChatUserFilter + n) % 3;
        }

        if (options == GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
            this.streamMicToggleBehavior = (this.streamMicToggleBehavior + n) % 2;
        }

        if (options == GameSettings.Options.CHAT_COLOR) {
            this.chatColours = !this.chatColours;
        }

        if (options == GameSettings.Options.CHAT_LINKS) {
            this.chatLinks = !this.chatLinks;
        }

        if (options == GameSettings.Options.CHAT_LINKS_PROMPT) {
            this.chatLinksPrompt = !this.chatLinksPrompt;
        }

        if (options == GameSettings.Options.SNOOPER_ENABLED) {
            this.snooperEnabled = !this.snooperEnabled;
        }

        if (options == GameSettings.Options.SHOW_CAPE) {
            this.showCape = !this.showCape;
        }

        if (options == GameSettings.Options.TOUCHSCREEN) {
            this.touchscreen = !this.touchscreen;
        }

        if (options == GameSettings.Options.USE_FULLSCREEN) {
            this.fullScreen = !this.fullScreen;
            if (this.mc.isFullScreen() != this.fullScreen) {
                this.mc.toggleFullscreen();
            }
        }

        if (options == GameSettings.Options.ENABLE_VSYNC) {
            this.enableVsync = !this.enableVsync;
            Display.setVSyncEnabled(this.enableVsync);
        }

        this.saveOptions();
    }

    public float getOptionFloatValue(GameSettings.Options options) {
        return options == GameSettings.Options.CLOUD_HEIGHT
            ? this.ofCloudsHeight
            : (
                options == GameSettings.Options.AO_LEVEL
                    ? this.ofAoLevel
                    : (
                        options == GameSettings.Options.FRAMERATE_LIMIT
                            ? (this.limitFramerate == GameSettings.Options.FRAMERATE_LIMIT.getValueMax() && this.enableVsync ? 0.0F : this.limitFramerate)
                            : (
                                options == GameSettings.Options.MIPMAP_TYPE
                                    ? this.ofMipmapType
                                    : (
                                        options == GameSettings.Options.FOV
                                            ? this.fovSetting
                                            : (
                                                options == GameSettings.Options.GAMMA
                                                    ? this.gammaSetting
                                                    : (
                                                        options == GameSettings.Options.SATURATION
                                                            ? this.saturation
                                                            : (
                                                                options == GameSettings.Options.SENSITIVITY
                                                                    ? this.mouseSensitivity
                                                                    : (
                                                                        options == GameSettings.Options.CHAT_OPACITY
                                                                            ? this.chatOpacity
                                                                            : (
                                                                                options == GameSettings.Options.CHAT_HEIGHT_FOCUSED
                                                                                    ? this.chatHeightFocused
                                                                                    : (
                                                                                        options == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED
                                                                                            ? this.chatHeightUnfocused
                                                                                            : (
                                                                                                options == GameSettings.Options.CHAT_SCALE
                                                                                                    ? this.chatScale
                                                                                                    : (
                                                                                                        options == GameSettings.Options.CHAT_WIDTH
                                                                                                            ? this.chatWidth
                                                                                                            : (
                                                                                                                options == GameSettings.Options.FRAMERATE_LIMIT
                                                                                                                    ? this.limitFramerate
                                                                                                                    : (
                                                                                                                        options == GameSettings.Options.ANISOTROPIC_FILTERING
                                                                                                                            ? this.anisotropicFiltering
                                                                                                                            : (
                                                                                                                                options == GameSettings.Options.MIPMAP_LEVELS
                                                                                                                                    ? this.mipmapLevels
                                                                                                                                    : (
                                                                                                                                        options == GameSettings.Options.RENDER_DISTANCE
                                                                                                                                            ? this.renderDistanceChunks
                                                                                                                                            : (
                                                                                                                                                options == GameSettings.Options.STREAM_BYTES_PER_PIXEL
                                                                                                                                                    ? this.streamBytesPerPixel
                                                                                                                                                    : (
                                                                                                                                                        options
                                                                                                                                                                == GameSettings.Options.STREAM_VOLUME_MIC
                                                                                                                                                            ? this.streamMicVolume
                                                                                                                                                            : (
                                                                                                                                                                options
                                                                                                                                                                        == GameSettings.Options.STREAM_VOLUME_SYSTEM
                                                                                                                                                                    ? this.streamGameVolume
                                                                                                                                                                    : (
                                                                                                                                                                        options
                                                                                                                                                                                == GameSettings.Options.STREAM_KBPS
                                                                                                                                                                            ? this.streamKbps
                                                                                                                                                                            : (
                                                                                                                                                                                options
                                                                                                                                                                                        == GameSettings.Options.STREAM_FPS
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
                                            )
                                    )
                            )
                    )
            );
    }

    public boolean getOptionOrdinalValue(GameSettings.Options options) {
        switch (options) {
            case INVERT_MOUSE:
                return this.invertMouse;
            case VIEW_BOBBING:
                return this.viewBobbing;
            case ANAGLYPH:
                return this.anaglyph;
            case ADVANCED_OPENGL:
                return this.advancedOpengl;
            case FBO_ENABLE:
                return this.fboEnable;
            case RENDER_CLOUDS:
                return this.clouds;
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
            case SHOW_CAPE:
                return this.showCape;
            case TOUCHSCREEN:
                return this.touchscreen;
            case STREAM_SEND_METADATA:
                return this.streamSendMetadata;
            case FORCE_UNICODE_FONT:
                return this.forceUnicodeFont;
            default:
                return false;
        }
    }

    public static String getTranslation(String[] strs, int n) {
        if (n < 0 || n >= strs.length) {
            n = 0;
        }

        return I18n.format(strs[n]);
    }

    public String getKeyBinding(GameSettings.Options options) {
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

            int n = this.renderDistanceChunks - b;
            String str2 = format;
            if (n > 0) {
                str2 = format + "+";
            }

            return str + optionFloatValue + " " + str2 + "";
        } else {
            if (options == GameSettings.Options.ADVANCED_OPENGL) {
                return !this.advancedOpengl ? str + Lang.getOff() : (this.ofOcclusionFancy ? str + Lang.getFancy() : str + Lang.getFast());
            }

            if (options == GameSettings.Options.FOG_FANCY) {
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
                            return str + Lang.get("of.options.mipmap.nearest");
                    }
                } else {
                    if (options == GameSettings.Options.LOAD_FAR) {
                        return this.ofLoadFar ? str + Lang.getOn() : str + Lang.getOff();
                    }

                    if (options == GameSettings.Options.PRELOADED_CHUNKS) {
                        return this.ofPreloadedChunks == 0 ? str + Lang.getOff() : str + this.ofPreloadedChunks;
                    }

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
                            default:
                                return str + Lang.getDefault();
                        }
                    } else if (options == GameSettings.Options.GRASS) {
                        switch (this.ofGrass) {
                            case 1:
                                return str + Lang.getFast();
                            case 2:
                                return str + Lang.getFancy();
                            default:
                                return str + Lang.getDefault();
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
                    } else if (options == GameSettings.Options.WATER) {
                        switch (this.ofWater) {
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

                        if (options == GameSettings.Options.DRIPPING_WATER_LAVA) {
                            return this.ofDrippingWaterLava ? str + Lang.getOn() : str + Lang.getOff();
                        }

                        if (options == GameSettings.Options.ANIMATED_TERRAIN) {
                            return this.ofAnimatedTerrain ? str + Lang.getOn() : str + Lang.getOff();
                        }

                        if (options == GameSettings.Options.ANIMATED_TEXTURES) {
                            return this.ofAnimatedTextures ? str + Lang.getOn() : str + Lang.getOff();
                        }

                        if (options == GameSettings.Options.ANIMATED_ITEMS) {
                            return this.ofAnimatedItems ? str + Lang.getOn() : str + Lang.getOff();
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
                            return this.ofAutoSaveTicks <= 40
                                ? str + Lang.get("of.options.save.default")
                                : (
                                    this.ofAutoSaveTicks <= 400
                                        ? str + Lang.get("of.options.save.20s")
                                        : (this.ofAutoSaveTicks <= 4000 ? str + Lang.get("of.options.save.3min") : str + Lang.get("of.options.save.30min"))
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

                                if (options == GameSettings.Options.CHUNK_LOADING) {
                                    return this.ofChunkLoading == 1 ? str + "Smooth" : (this.ofChunkLoading == 2 ? str + "Multi-Core" : str + "Default");
                                }

                                if (options == GameSettings.Options.CHUNK_UPDATES_DYNAMIC) {
                                    return this.ofChunkUpdatesDynamic ? str + Lang.getOn() : str + Lang.getOff();
                                }

                                if (options == GameSettings.Options.TIME) {
                                    return this.ofTime == 1
                                        ? str + Lang.get("of.options.time.dayOnly")
                                        : (this.ofTime == 3 ? str + Lang.get("of.options.time.nightOnly") : str + Lang.getDefault());
                                }

                                if (options == GameSettings.Options.CLEAR_WATER) {
                                    return this.ofClearWater ? str + Lang.getOn() : str + Lang.getOff();
                                }

                                if (options == GameSettings.Options.DEPTH_FOG) {
                                    return this.ofDepthFog ? str + Lang.getOn() : str + Lang.getOff();
                                }

                                if (options == GameSettings.Options.AA_LEVEL) {
                                    return this.ofAaLevel == 0 ? str + Lang.getOff() : str + this.ofAaLevel;
                                }

                                if (options == GameSettings.Options.PROFILER) {
                                    return this.ofProfiler ? str + Lang.getOn() : str + Lang.getOff();
                                }

                                if (options == GameSettings.Options.BETTER_SNOW) {
                                    return this.ofBetterSnow ? str + Lang.getOn() : str + Lang.getOff();
                                }

                                if (options == GameSettings.Options.SWAMP_COLORS) {
                                    return this.ofSwampColors ? str + Lang.getOn() : str + Lang.getOff();
                                }

                                if (options == GameSettings.Options.RANDOM_MOBS) {
                                    return this.ofRandomMobs ? str + Lang.getOn() : str + Lang.getOff();
                                }

                                if (options == GameSettings.Options.SMOOTH_BIOMES) {
                                    return this.ofSmoothBiomes ? str + Lang.getOn() : str + Lang.getOff();
                                }

                                if (options == GameSettings.Options.CUSTOM_FONTS) {
                                    return this.ofCustomFonts ? str + Lang.getOn() : str + Lang.getOff();
                                }

                                if (options == GameSettings.Options.CUSTOM_COLORS) {
                                    return this.ofCustomColors ? str + Lang.getOn() : str + Lang.getOff();
                                }

                                if (options == GameSettings.Options.CUSTOM_SKY) {
                                    return this.ofCustomSky ? str + Lang.getOn() : str + Lang.getOff();
                                }

                                if (options == GameSettings.Options.SHOW_CAPES) {
                                    return this.ofShowCapes ? str + Lang.getOn() : str + Lang.getOff();
                                }

                                if (options == GameSettings.Options.NATURAL_TEXTURES) {
                                    return this.ofNaturalTextures ? str + Lang.getOn() : str + Lang.getOff();
                                }

                                if (options == GameSettings.Options.FAST_MATH) {
                                    return this.ofFastMath ? str + Lang.getOn() : str + Lang.getOff();
                                }

                                if (options == GameSettings.Options.FAST_RENDER) {
                                    return this.ofFastRender ? str + Lang.getOn() : str + Lang.getOff();
                                }

                                if (options == GameSettings.Options.TRANSLUCENT_BLOCKS) {
                                    return this.ofTranslucentBlocks == 1 ? str + Lang.getFast() : str + Lang.getFancy();
                                }

                                if (options == GameSettings.Options.LAZY_CHUNK_LOADING) {
                                    return this.ofLazyChunkLoading ? str + Lang.getOn() : str + Lang.getOff();
                                }

                                if (options == GameSettings.Options.DYNAMIC_FOV) {
                                    return this.ofDynamicFov ? str + Lang.getOn() : str + Lang.getOff();
                                }

                                if (options == GameSettings.Options.DYNAMIC_LIGHTS) {
                                    int indexOf2 = indexOf(this.ofDynamicLights, OF_DYNAMIC_LIGHTS);
                                    return str + getTranslation(KEYS_DYNAMIC_LIGHTS, indexOf2);
                                }

                                if (options == GameSettings.Options.FULLSCREEN_MODE) {
                                    return this.ofFullscreenMode.equals("Default") ? str + Lang.getDefault() : str + this.ofFullscreenMode;
                                }

                                if (options == GameSettings.Options.HELD_ITEM_TOOLTIPS) {
                                    return this.heldItemTooltips ? str + Lang.getOn() : str + Lang.getOff();
                                }

                                if (options == GameSettings.Options.FRAMERATE_LIMIT) {
                                    float optionFloatValue = this.getOptionFloatValue(options);
                                    return optionFloatValue == 0.0F
                                        ? str + Lang.get("of.options.framerateLimit.vsync")
                                        : (optionFloatValue == options.valueMax ? str + I18n.format("options.framerateLimit.max") : str + (int)optionFloatValue + " fps");
                                }

                                if (options.getEnumFloat()) {
                                    float optionFloatValue = this.getOptionFloatValue(options);
                                    float normalizeValue = options.normalizeValue(optionFloatValue);
                                    return options == GameSettings.Options.SENSITIVITY
                                        ? (
                                            normalizeValue == 0.0F
                                                ? str + I18n.format("options.sensitivity.min")
                                                : (normalizeValue == 1.0F ? str + I18n.format("options.sensitivity.max") : str + (int)(normalizeValue * 200.0F) + "%")
                                        )
                                        : (
                                            options == GameSettings.Options.FOV
                                                ? (
                                                    optionFloatValue == 70.0F
                                                        ? str + I18n.format("options.fov.min")
                                                        : (optionFloatValue == 110.0F ? str + I18n.format("options.fov.max") : str + (int)optionFloatValue)
                                                )
                                                : (
                                                    options == GameSettings.Options.FRAMERATE_LIMIT
                                                        ? (optionFloatValue == options.valueMax ? str + I18n.format("options.framerateLimit.max") : str + (int)optionFloatValue + " fps")
                                                        : (
                                                            options == GameSettings.Options.GAMMA
                                                                ? (
                                                                    normalizeValue == 0.0F
                                                                        ? str + I18n.format("options.gamma.min")
                                                                        : (normalizeValue == 1.0F ? str + I18n.format("options.gamma.max") : str + "+" + (int)(normalizeValue * 100.0F) + "%")
                                                                )
                                                                : (
                                                                    options == GameSettings.Options.SATURATION
                                                                        ? str + (int)(normalizeValue * 400.0F) + "%"
                                                                        : (
                                                                            options == GameSettings.Options.CHAT_OPACITY
                                                                                ? str + (int)(normalizeValue * 90.0F + 10.0F) + "%"
                                                                                : (
                                                                                    options == GameSettings.Options.CHAT_HEIGHT_UNFOCUSED
                                                                                        ? str + GuiNewChat.calculateChatboxHeight(normalizeValue) + "px"
                                                                                        : (
                                                                                            options == GameSettings.Options.CHAT_HEIGHT_FOCUSED
                                                                                                ? str + GuiNewChat.calculateChatboxHeight(normalizeValue) + "px"
                                                                                                : (
                                                                                                    options == GameSettings.Options.CHAT_WIDTH
                                                                                                        ? str + GuiNewChat.calculateChatboxWidth(normalizeValue) + "px"
                                                                                                        : (
                                                                                                            options == GameSettings.Options.RENDER_DISTANCE
                                                                                                                ? str + (int)optionFloatValue + " chunks"
                                                                                                                : (
                                                                                                                    options == GameSettings.Options.ANISOTROPIC_FILTERING
                                                                                                                        ? (
                                                                                                                            optionFloatValue == 1.0F
                                                                                                                                ? str + I18n.format("options.off")
                                                                                                                                : str + (int)optionFloatValue
                                                                                                                        )
                                                                                                                        : (
                                                                                                                            options == GameSettings.Options.MIPMAP_LEVELS
                                                                                                                                ? (
                                                                                                                                    optionFloatValue == 0.0F
                                                                                                                                        ? str + I18n.format("options.off")
                                                                                                                                        : str + (int)optionFloatValue
                                                                                                                                )
                                                                                                                                : (
                                                                                                                                    options == GameSettings.Options.STREAM_FPS
                                                                                                                                        ? str + " fps"
                                                                                                                                        : (
                                                                                                                                            options == GameSettings.Options.STREAM_KBPS
                                                                                                                                                ? str + " Kbps"
                                                                                                                                                : (
                                                                                                                                                    options
                                                                                                                                                            == GameSettings.Options.STREAM_BYTES_PER_PIXEL
                                                                                                                                                        ? str
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
                                                        )
                                                )
                                        );
                                }

                                if (options.getEnumBoolean()) {
                                    boolean optionOrdinalValue = this.getOptionOrdinalValue(options);
                                    return optionOrdinalValue ? str + I18n.format("options.on") : str + I18n.format("options.off");
                                }

                                if (options == GameSettings.Options.DIFFICULTY) {
                                    return str + I18n.format(this.difficulty.getDifficultyResourceKey());
                                }

                                if (options == GameSettings.Options.GUI_SCALE) {
                                    return this.guiScale >= GUISCALES.length ? str + this.guiScale + "x" : str + getTranslation(GUISCALES, this.guiScale);
                                }

                                if (options == GameSettings.Options.CHAT_VISIBILITY) {
                                    return str + I18n.format(this.chatVisibility.getResourceKey());
                                }

                                if (options == GameSettings.Options.PARTICLES) {
                                    return str + getTranslation(PARTICLES, this.particleSetting);
                                }

                                if (options == GameSettings.Options.AMBIENT_OCCLUSION) {
                                    return str + getTranslation(AMBIENT_OCCLUSIONS, this.ambientOcclusion);
                                }

                                if (options == GameSettings.Options.STREAM_COMPRESSION) {
                                    return str + getTranslation(STREAM_COMPRESSIONS, this.streamCompression);
                                }

                                if (options == GameSettings.Options.STREAM_CHAT_ENABLED) {
                                    return str + getTranslation(STREAM_CHAT_MODES, this.streamChatEnabled);
                                }

                                if (options == GameSettings.Options.STREAM_CHAT_USER_FILTER) {
                                    return str + getTranslation(STREAM_CHAT_FILTER_MODES, this.streamChatUserFilter);
                                }

                                if (options == GameSettings.Options.STREAM_MIC_TOGGLE_BEHAVIOR) {
                                    return str + getTranslation(STREAM_MIC_MODES, this.streamMicToggleBehavior);
                                }

                                if (options == GameSettings.Options.GRAPHICS) {
                                    if (this.fancyGraphics) {
                                        return str + I18n.format("options.graphics.fancy");
                                    }

                                    String str2 = "options.graphics.fast";
                                    return str + I18n.format("options.graphics.fast");
                                } else {
                                    return str;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void loadOptions() {
        try {
            if (!this.optionsFile.exists()) {
                return;
            }

            BufferedReader bufferedReader = new BufferedReader(new FileReader(this.optionsFile));
            String str = "";
            this.mapSoundLevels.clear();

            while ((str = bufferedReader.readLine()) != null) {
                try {
                    String[] split = str.split(":");
                    if (split[0].equals("mouseSensitivity")) {
                        this.mouseSensitivity = this.parseFloat(split[1]);
                    }

                    if (split[0].equals("invertYMouse")) {
                        this.invertMouse = split[1].equals("true");
                    }

                    if (split[0].equals("fov")) {
                        this.fovSetting = this.parseFloat(split[1]);
                    }

                    if (split[0].equals("gamma")) {
                        this.gammaSetting = this.parseFloat(split[1]);
                    }

                    if (split[0].equals("saturation")) {
                        this.saturation = this.parseFloat(split[1]);
                    }

                    if (split[0].equals("fov")) {
                        this.fovSetting = this.parseFloat(split[1]) * 40.0F + 70.0F;
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

                    if (split[0].equals("advancedOpengl")) {
                        this.advancedOpengl = split[1].equals("true");
                    }

                    if (split[0].equals("maxFps")) {
                        this.limitFramerate = Integer.parseInt(split[1]);
                        this.enableVsync = false;
                        if (this.limitFramerate <= 0) {
                            this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
                            this.enableVsync = true;
                        }

                        this.updateVSync();
                    }

                    if (split[0].equals("fboEnable")) {
                        this.fboEnable = split[1].equals("true");
                    }

                    if (split[0].equals("difficulty")) {
                        this.difficulty = EnumDifficulty.getDifficultyEnum(Integer.parseInt(split[1]));
                    }

                    if (split[0].equals("fancyGraphics")) {
                        this.fancyGraphics = split[1].equals("true");
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

                    if (split[0].equals("clouds")) {
                        this.clouds = split[1].equals("true");
                    }

                    if (split[0].equals("resourcePacks")) {
                        this.resourcePacks = (List<String>)gson.fromJson(str.substring(str.indexOf(58) + 1), typeListString);
                        if (this.resourcePacks == null) {
                            this.resourcePacks = new ArrayList<>();
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
                        this.updateVSync();
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

                    if (split[0].equals("showCape")) {
                        this.showCape = split[1].equals("true");
                    }

                    for (EnumPlayerModelParts enumPlayerModelParts : EnumPlayerModelParts.values()) {
                        if (split[0].equals("modelPart_" + enumPlayerModelParts.getText())) {
                            this.handleEnumPlayerModelParts(enumPlayerModelParts, split[1].equals("true"));
                        }
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

                    if (split[0].equals("anisotropicFiltering")) {
                        this.anisotropicFiltering = Integer.parseInt(split[1]);
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
                } catch (Exception e) {
                    logger.warn("Skipping bad option: " + str);
                    e.printStackTrace();
                }
            }

            KeyBinding.resetKeyBindingArrayAndHash();
            bufferedReader.close();
        } catch (Exception e) {
            logger.error("Failed to load options", e);
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
            printWriter.println("saturation:" + this.saturation);
            printWriter.println("renderDistance:" + Config.limit(this.renderDistanceChunks, 2, 16));
            printWriter.println("guiScale:" + this.guiScale);
            printWriter.println("particles:" + this.particleSetting);
            printWriter.println("bobView:" + this.viewBobbing);
            printWriter.println("anaglyph3d:" + this.anaglyph);
            printWriter.println("advancedOpengl:" + this.advancedOpengl);
            printWriter.println("maxFps:" + this.limitFramerate);
            printWriter.println("fboEnable:" + this.fboEnable);
            printWriter.println("difficulty:" + this.difficulty.getDifficultyId());
            printWriter.println("fancyGraphics:" + this.fancyGraphics);
            printWriter.println("ao:" + this.ambientOcclusion);
            printWriter.println("clouds:" + this.clouds);
            printWriter.println("resourcePacks:" + gson.toJson(this.resourcePacks));
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
            printWriter.println("hideServerAddress:" + this.hideServerAddress);
            printWriter.println("advancedItemTooltips:" + this.advancedItemTooltips);
            printWriter.println("pauseOnLostFocus:" + this.pauseOnLostFocus);
            printWriter.println("showCape:" + this.showCape);

            for (EnumPlayerModelParts enumPlayerModelParts : EnumPlayerModelParts.values()) {
                printWriter.println("modelPart_" + enumPlayerModelParts.getText() + ":" + this.enumPlayerModelPartsSet.contains(enumPlayerModelParts));
            }

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
            printWriter.println("anisotropicFiltering:" + this.anisotropicFiltering);
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

            for (KeyBinding keyBinding : this.keyBindings) {
                printWriter.println("key_" + keyBinding.getKeyDescription() + ":" + keyBinding.getKeyCode());
            }

            for (SoundCategory soundCategory : SoundCategory.values()) {
                printWriter.println("soundCategory_" + soundCategory.getCategoryName() + ":" + this.getSoundLevel(soundCategory));
            }

            printWriter.close();
        } catch (Exception e) {
            logger.error("Failed to save options", e);
        }

        this.saveOfOptions();
        this.sendSettingsToServer();
    }

    public float getSoundLevel(SoundCategory soundCategory) {
        return this.mapSoundLevels.containsKey(soundCategory) ? (Float)this.mapSoundLevels.get(soundCategory) : 1.0F;
    }

    public void setSoundLevel(SoundCategory soundCategory, float f) {
        this.mc.getSoundHandler().setSoundLevel(soundCategory, f);
        this.mapSoundLevels.put(soundCategory, f);
    }

    public void sendSettingsToServer() {
        if (this.mc.thePlayer != null) {
            int n = 0;

            for (EnumPlayerModelParts enumPlayerModelParts : this.enumPlayerModelPartsSet) {
                n |= enumPlayerModelParts.getPartMask();
            }

            this.mc
                .thePlayer
                .sendQueue
                .addToSendQueue(
                    new C15PacketClientSettings(this.language, this.renderDistanceChunks, this.chatVisibility, this.chatColours, this.difficulty, this.showCape)
                );
            this.mc.thePlayer.setModelPartsMask(n);
        }
    }

    public boolean shouldRenderClouds() {
        return this.renderDistanceChunks >= 4 && this.clouds;
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

            BufferedReader bufferedReader = new BufferedReader(new FileReader(optionsFileOF));
            String str = "";

            while ((str = bufferedReader.readLine()) != null) {
                try {
                    String[] split = str.split(":");
                    if (split[0].equals("ofRenderDistanceChunks") && split.length >= 2) {
                        this.renderDistanceChunks = Integer.valueOf(split[1]);
                        this.renderDistanceChunks = Config.limit(this.renderDistanceChunks, 2, 32);
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

                    if (split[0].equals("ofLoadFar") && split.length >= 2) {
                        this.ofLoadFar = Boolean.valueOf(split[1]);
                    }

                    if (split[0].equals("ofPreloadedChunks") && split.length >= 2) {
                        this.ofPreloadedChunks = Integer.valueOf(split[1]);
                        if (this.ofPreloadedChunks < 0) {
                            this.ofPreloadedChunks = 0;
                        }

                        if (this.ofPreloadedChunks > 8) {
                            this.ofPreloadedChunks = 8;
                        }
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
                    }

                    if (split[0].equals("ofCloudsHeight") && split.length >= 2) {
                        this.ofCloudsHeight = Float.valueOf(split[1]);
                        this.ofCloudsHeight = Config.limit(this.ofCloudsHeight, 0.0F, 1.0F);
                    }

                    if (split[0].equals("ofTrees") && split.length >= 2) {
                        this.ofTrees = Integer.valueOf(split[1]);
                        this.ofTrees = Config.limit(this.ofTrees, 0, 2);
                    }

                    if (split[0].equals("ofGrass") && split.length >= 2) {
                        this.ofGrass = Integer.valueOf(split[1]);
                        this.ofGrass = Config.limit(this.ofGrass, 0, 2);
                    }

                    if (split[0].equals("ofDroppedItems") && split.length >= 2) {
                        this.ofDroppedItems = Integer.valueOf(split[1]);
                        this.ofDroppedItems = Config.limit(this.ofDroppedItems, 0, 2);
                    }

                    if (split[0].equals("ofRain") && split.length >= 2) {
                        this.ofRain = Integer.valueOf(split[1]);
                        this.ofRain = Config.limit(this.ofRain, 0, 3);
                    }

                    if (split[0].equals("ofWater") && split.length >= 2) {
                        this.ofWater = Integer.valueOf(split[1]);
                        this.ofWater = Config.limit(this.ofWater, 0, 3);
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

                    if (split[0].equals("ofDrippingWaterLava") && split.length >= 2) {
                        this.ofDrippingWaterLava = Boolean.valueOf(split[1]);
                    }

                    if (split[0].equals("ofAnimatedTerrain") && split.length >= 2) {
                        this.ofAnimatedTerrain = Boolean.valueOf(split[1]);
                    }

                    if (split[0].equals("ofAnimatedTextures") && split.length >= 2) {
                        this.ofAnimatedTextures = Boolean.valueOf(split[1]);
                    }

                    if (split[0].equals("ofAnimatedItems") && split.length >= 2) {
                        this.ofAnimatedItems = Boolean.valueOf(split[1]);
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

                    if (split[0].equals("ofChunkLoading") && split.length >= 2) {
                        this.ofChunkLoading = Integer.valueOf(split[1]);
                        this.ofChunkLoading = Config.limit(this.ofChunkLoading, 0, 2);
                        this.updateChunkLoading();
                    }

                    if (split[0].equals("ofChunkUpdatesDynamic") && split.length >= 2) {
                        this.ofChunkUpdatesDynamic = Boolean.valueOf(split[1]);
                    }

                    if (split[0].equals("ofTime") && split.length >= 2) {
                        this.ofTime = Integer.valueOf(split[1]);
                        this.ofTime = Config.limit(this.ofTime, 0, 3);
                    }

                    if (split[0].equals("ofClearWater") && split.length >= 2) {
                        this.ofClearWater = Boolean.valueOf(split[1]);
                        this.updateWaterOpacity();
                    }

                    if (split[0].equals("ofDepthFog") && split.length >= 2) {
                        this.ofDepthFog = Boolean.valueOf(split[1]);
                    }

                    if (split[0].equals("ofAaLevel") && split.length >= 2) {
                        this.ofAaLevel = Integer.valueOf(split[1]);
                        this.ofAaLevel = Config.limit(this.ofAaLevel, 0, 16);
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

                    if (split[0].equals("ofRandomMobs") && split.length >= 2) {
                        this.ofRandomMobs = Boolean.valueOf(split[1]);
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

                    if (split[0].equals("ofCustomSky") && split.length >= 2) {
                        this.ofCustomSky = Boolean.valueOf(split[1]);
                    }

                    if (split[0].equals("ofShowCapes") && split.length >= 2) {
                        this.ofShowCapes = Boolean.valueOf(split[1]);
                    }

                    if (split[0].equals("ofNaturalTextures") && split.length >= 2) {
                        this.ofNaturalTextures = Boolean.valueOf(split[1]);
                    }

                    if (split[0].equals("ofLazyChunkLoading") && split.length >= 2) {
                        this.ofLazyChunkLoading = Boolean.valueOf(split[1]);
                    }

                    if (split[0].equals("ofDynamicFov") && split.length >= 2) {
                        this.ofDynamicFov = Boolean.valueOf(split[1]);
                    }

                    if (split[0].equals("ofDynamicLights") && split.length >= 2) {
                        this.ofDynamicLights = Integer.valueOf(split[1]);
                        this.ofDynamicLights = Config.limit(this.ofDynamicLights, 1, 3);
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
                        this.ofTranslucentBlocks = Config.limit(this.ofTranslucentBlocks, 1, 2);
                    }
                } catch (Exception e) {
                    Config.dbg("Skipping bad option: " + str);
                    e.printStackTrace();
                }
            }

            KeyBinding.resetKeyBindingArrayAndHash();
            bufferedReader.close();
        } catch (Exception e) {
            Config.warn("Failed to load options");
            e.printStackTrace();
        }
    }

    public void saveOfOptions() {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(this.optionsFileOF));
            printWriter.println("ofRenderDistanceChunks:" + this.renderDistanceChunks);
            printWriter.println("ofFogType:" + this.ofFogType);
            printWriter.println("ofFogStart:" + this.ofFogStart);
            printWriter.println("ofMipmapType:" + this.ofMipmapType);
            printWriter.println("ofLoadFar:" + this.ofLoadFar);
            printWriter.println("ofPreloadedChunks:" + this.ofPreloadedChunks);
            printWriter.println("ofOcclusionFancy:" + this.ofOcclusionFancy);
            printWriter.println("ofSmoothFps:" + this.ofSmoothFps);
            printWriter.println("ofSmoothWorld:" + this.ofSmoothWorld);
            printWriter.println("ofAoLevel:" + this.ofAoLevel);
            printWriter.println("ofClouds:" + this.ofClouds);
            printWriter.println("ofCloudsHeight:" + this.ofCloudsHeight);
            printWriter.println("ofTrees:" + this.ofTrees);
            printWriter.println("ofGrass:" + this.ofGrass);
            printWriter.println("ofDroppedItems:" + this.ofDroppedItems);
            printWriter.println("ofRain:" + this.ofRain);
            printWriter.println("ofWater:" + this.ofWater);
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
            printWriter.println("ofDrippingWaterLava:" + this.ofDrippingWaterLava);
            printWriter.println("ofAnimatedTerrain:" + this.ofAnimatedTerrain);
            printWriter.println("ofAnimatedTextures:" + this.ofAnimatedTextures);
            printWriter.println("ofAnimatedItems:" + this.ofAnimatedItems);
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
            printWriter.println("ofChunkLoading:" + this.ofChunkLoading);
            printWriter.println("ofChunkUpdatesDynamic:" + this.ofChunkUpdatesDynamic);
            printWriter.println("ofTime:" + this.ofTime);
            printWriter.println("ofClearWater:" + this.ofClearWater);
            printWriter.println("ofDepthFog:" + this.ofDepthFog);
            printWriter.println("ofAaLevel:" + this.ofAaLevel);
            printWriter.println("ofProfiler:" + this.ofProfiler);
            printWriter.println("ofBetterSnow:" + this.ofBetterSnow);
            printWriter.println("ofSwampColors:" + this.ofSwampColors);
            printWriter.println("ofRandomMobs:" + this.ofRandomMobs);
            printWriter.println("ofSmoothBiomes:" + this.ofSmoothBiomes);
            printWriter.println("ofCustomFonts:" + this.ofCustomFonts);
            printWriter.println("ofCustomColors:" + this.ofCustomColors);
            printWriter.println("ofCustomSky:" + this.ofCustomSky);
            printWriter.println("ofShowCapes:" + this.ofShowCapes);
            printWriter.println("ofNaturalTextures:" + this.ofNaturalTextures);
            printWriter.println("ofLazyChunkLoading:" + this.ofLazyChunkLoading);
            printWriter.println("ofDynamicFov:" + this.ofDynamicFov);
            printWriter.println("ofDynamicLights:" + this.ofDynamicLights);
            printWriter.println("ofFullscreenMode:" + this.ofFullscreenMode);
            printWriter.println("ofFastMath:" + this.ofFastMath);
            printWriter.println("ofFastRender:" + this.ofFastRender);
            printWriter.println("ofTranslucentBlocks:" + this.ofTranslucentBlocks);
            printWriter.close();
        } catch (Exception e) {
            Config.warn("Failed to save options");
            e.printStackTrace();
        }
    }

    public void resetSettings() {
        this.renderDistanceChunks = 8;
        this.viewBobbing = true;
        this.anaglyph = false;
        this.advancedOpengl = false;
        this.limitFramerate = (int)GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
        this.enableVsync = false;
        this.updateVSync();
        this.mipmapLevels = 4;
        this.anisotropicFiltering = 1;
        this.fancyGraphics = true;
        this.ambientOcclusion = 2;
        this.clouds = true;
        this.fovSetting = 70.0F;
        this.gammaSetting = 0.0F;
        this.guiScale = 0;
        this.particleSetting = 0;
        this.heldItemTooltips = true;
        this.ofFogType = 1;
        this.ofFogStart = 0.8F;
        this.ofMipmapType = 0;
        this.ofLoadFar = false;
        this.ofPreloadedChunks = 0;
        this.ofOcclusionFancy = false;
        this.ofSmoothFps = false;
        Config.updateAvailableProcessors();
        this.ofSmoothWorld = Config.isSingleProcessor();
        this.ofLazyChunkLoading = Config.isSingleProcessor();
        this.ofFastMath = false;
        this.ofFastRender = false;
        this.ofTranslucentBlocks = 2;
        this.ofDynamicFov = true;
        this.ofDynamicLights = 3;
        this.ofAoLevel = 1.0F;
        this.ofAaLevel = 0;
        this.ofClouds = 0;
        this.ofCloudsHeight = 0.0F;
        this.ofTrees = 0;
        this.ofGrass = 0;
        this.ofRain = 0;
        this.ofWater = 0;
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
        this.ofChunkLoading = 0;
        this.ofChunkUpdatesDynamic = false;
        this.ofTime = 0;
        this.ofClearWater = false;
        this.ofDepthFog = true;
        this.ofBetterSnow = false;
        this.ofFullscreenMode = "Default";
        this.ofSwampColors = true;
        this.ofRandomMobs = true;
        this.ofSmoothBiomes = true;
        this.ofCustomFonts = true;
        this.ofCustomColors = true;
        this.ofCustomSky = true;
        this.ofShowCapes = true;
        this.ofConnectedTextures = 2;
        this.ofNaturalTextures = false;
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
        this.ofDrippingWaterLava = true;
        this.ofAnimatedTerrain = true;
        this.ofAnimatedItems = true;
        this.ofAnimatedTextures = true;
        Shaders.setShaderPack(Shaders.packNameNone);
        Shaders.configAntialiasingLevel = 0;
        Shaders.uninit();
        Shaders.storeConfig();
        this.mc.renderGlobal.updateCapes();
        this.updateWaterOpacity();
        this.mc.renderGlobal.setAllRenderersVisible();
        this.mc.refreshResources();
        this.saveOptions();
    }

    public void updateVSync() {
        Display.setVSyncEnabled(this.enableVsync);
    }

    public void updateWaterOpacity() {
        if (this.mc.isIntegratedServerRunning() && this.mc.getIntegratedServer() != null) {
            Config.waterOpacityChanged = true;
        }

        ClearWater.updateWaterOpacity(this, this.mc.theWorld);
    }

    public void updateChunkLoading() {
        switch (this.ofChunkLoading) {
            case 1:
                WrUpdates.setWrUpdater(new WrUpdaterSmooth());
                break;
            case 2:
                WrUpdates.setWrUpdater(new WrUpdaterThreaded());
                break;
            default:
                WrUpdates.setWrUpdater((IWrUpdater)null);
        }

        if (this.mc.renderGlobal != null) {
            this.mc.renderGlobal.loadRenderers();
        }
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
        this.particleSetting = flag ? 0 : 2;
        this.ofDrippingWaterLava = flag;
        this.ofAnimatedTerrain = flag;
        this.ofAnimatedItems = flag;
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

    public Set<EnumPlayerModelParts> createHashSet() {
        return new HashSet<>(this.enumPlayerModelPartsSet);
    }

    public float getGammaSetting() {
        return this.gammaSetting;
    }

    public void handleEnumPlayerModelParts(EnumPlayerModelParts enumPlayerModelParts, boolean flag) {
        if (flag) {
            this.enumPlayerModelPartsSet.add(enumPlayerModelParts);
        } else {
            this.enumPlayerModelPartsSet.remove(enumPlayerModelParts);
        }

        this.sendSettingsToServer();
    }

    public void handleEnumPlayerModelParts(EnumPlayerModelParts enumPlayerModelParts) {
        this.handleEnumPlayerModelParts(enumPlayerModelParts, !this.enumPlayerModelPartsSet.contains(enumPlayerModelParts));
    }

    public enum Options {
        INVERT_MOUSE("INVERT_MOUSE", 0, "INVERT_MOUSE", 0, "options.invertMouse", false, true),
        SENSITIVITY("SENSITIVITY", 1, "SENSITIVITY", 1, "options.sensitivity", true, false),
        FOV("FOV", 2, "FOV", 2, "options.fov", true, false, 30.0F, 110.0F, 1.0F),
        GAMMA("GAMMA", 3, "GAMMA", 3, "options.gamma", true, false),
        SATURATION("SATURATION", 4, "SATURATION", 4, "options.saturation", true, false),
        RENDER_DISTANCE("RENDER_DISTANCE", 5, "RENDER_DISTANCE", 5, "options.renderDistance", true, false, 2.0F, 16.0F, 1.0F),
        VIEW_BOBBING("VIEW_BOBBING", 6, "VIEW_BOBBING", 6, "options.viewBobbing", false, true),
        ANAGLYPH("ANAGLYPH", 7, "ANAGLYPH", 7, "options.anaglyph", false, true),
        ADVANCED_OPENGL("ADVANCED_OPENGL", 8, "ADVANCED_OPENGL", 8, "options.advancedOpengl", false, true),
        FRAMERATE_LIMIT("FRAMERATE_LIMIT", 9, "FRAMERATE_LIMIT", 9, "options.framerateLimit", true, false, 0.0F, 260.0F, 5.0F),
        FBO_ENABLE("FBO_ENABLE", 10, "FBO_ENABLE", 10, "options.fboEnable", false, true),
        DIFFICULTY("DIFFICULTY", 11, "DIFFICULTY", 11, "options.difficulty", false, false),
        GRAPHICS("GRAPHICS", 12, "GRAPHICS", 12, "options.graphics", false, false),
        AMBIENT_OCCLUSION("AMBIENT_OCCLUSION", 13, "AMBIENT_OCCLUSION", 13, "options.ao", false, false),
        GUI_SCALE("GUI_SCALE", 14, "GUI_SCALE", 14, "options.guiScale", false, false),
        RENDER_CLOUDS("RENDER_CLOUDS", 15, "RENDER_CLOUDS", 15, "options.renderClouds", false, true),
        PARTICLES("PARTICLES", 16, "PARTICLES", 16, "options.particles", false, false),
        CHAT_VISIBILITY("CHAT_VISIBILITY", 17, "CHAT_VISIBILITY", 17, "options.chat.visibility", false, false),
        CHAT_COLOR("CHAT_COLOR", 18, "CHAT_COLOR", 18, "options.chat.color", false, true),
        CHAT_LINKS("CHAT_LINKS", 19, "CHAT_LINKS", 19, "options.chat.links", false, true),
        CHAT_OPACITY("CHAT_OPACITY", 20, "CHAT_OPACITY", 20, "options.chat.opacity", true, false),
        CHAT_LINKS_PROMPT("CHAT_LINKS_PROMPT", 21, "CHAT_LINKS_PROMPT", 21, "options.chat.links.prompt", false, true),
        SNOOPER_ENABLED("SNOOPER_ENABLED", 22, "SNOOPER_ENABLED", 22, "options.snooper", false, true),
        USE_FULLSCREEN("USE_FULLSCREEN", 23, "USE_FULLSCREEN", 23, "options.fullscreen", false, true),
        ENABLE_VSYNC("ENABLE_VSYNC", 24, "ENABLE_VSYNC", 24, "options.vsync", false, true),
        SHOW_CAPE("SHOW_CAPE", 25, "SHOW_CAPE", 25, "options.showCape", false, true),
        TOUCHSCREEN("TOUCHSCREEN", 26, "TOUCHSCREEN", 26, "options.touchscreen", false, true),
        CHAT_SCALE("CHAT_SCALE", 27, "CHAT_SCALE", 27, "options.chat.scale", true, false),
        CHAT_WIDTH("CHAT_WIDTH", 28, "CHAT_WIDTH", 28, "options.chat.width", true, false),
        CHAT_HEIGHT_FOCUSED("CHAT_HEIGHT_FOCUSED", 29, "CHAT_HEIGHT_FOCUSED", 29, "options.chat.height.focused", true, false),
        CHAT_HEIGHT_UNFOCUSED("CHAT_HEIGHT_UNFOCUSED", 30, "CHAT_HEIGHT_UNFOCUSED", 30, "options.chat.height.unfocused", true, false),
        MIPMAP_LEVELS("MIPMAP_LEVELS", 31, "MIPMAP_LEVELS", 31, "options.mipmapLevels", true, false, 0.0F, 4.0F, 1.0F),
        ANISOTROPIC_FILTERING(
            "ANISOTROPIC_FILTERING", 32, "ANISOTROPIC_FILTERING", 32, "options.anisotropicFiltering", true, false, 1.0F, 16.0F, 0.0F, null, null
        ) {
            @Override
            public float snapToStep(float f) {
                return MathHelper.roundUpToPowerOfTwo((int)f);
            }
        },
        FORCE_UNICODE_FONT("FORCE_UNICODE_FONT", 33, "FORCE_UNICODE_FONT", 33, "options.forceUnicodeFont", false, true),
        STREAM_BYTES_PER_PIXEL("STREAM_BYTES_PER_PIXEL", 34, "STREAM_BYTES_PER_PIXEL", 34, "options.stream.bytesPerPixel", true, false),
        STREAM_VOLUME_MIC("STREAM_VOLUME_MIC", 35, "STREAM_VOLUME_MIC", 35, "options.stream.micVolumne", true, false),
        STREAM_VOLUME_SYSTEM("STREAM_VOLUME_SYSTEM", 36, "STREAM_VOLUME_SYSTEM", 36, "options.stream.systemVolume", true, false),
        STREAM_KBPS("STREAM_KBPS", 37, "STREAM_KBPS", 37, "options.stream.kbps", true, false),
        STREAM_FPS("STREAM_FPS", 38, "STREAM_FPS", 38, "options.stream.fps", true, false),
        STREAM_COMPRESSION("STREAM_COMPRESSION", 39, "STREAM_COMPRESSION", 39, "options.stream.compression", false, false),
        STREAM_SEND_METADATA("STREAM_SEND_METADATA", 40, "STREAM_SEND_METADATA", 40, "options.stream.sendMetadata", false, true),
        STREAM_CHAT_ENABLED("STREAM_CHAT_ENABLED", 41, "STREAM_CHAT_ENABLED", 41, "options.stream.chat.enabled", false, false),
        STREAM_CHAT_USER_FILTER("STREAM_CHAT_USER_FILTER", 42, "STREAM_CHAT_USER_FILTER", 42, "options.stream.chat.userFilter", false, false),
        STREAM_MIC_TOGGLE_BEHAVIOR("STREAM_MIC_TOGGLE_BEHAVIOR", 43, "STREAM_MIC_TOGGLE_BEHAVIOR", 43, "options.stream.micToggleBehavior", false, false),
        FOG_FANCY("FOG_FANCY", 44, "", 999, "of.options.FOG_FANCY", false, false),
        FOG_START("FOG_START", 45, "", 999, "of.options.FOG_START", false, false),
        MIPMAP_TYPE("MIPMAP_TYPE", 46, "", 999, "of.options.MIPMAP_TYPE", true, false, 0.0F, 3.0F, 1.0F),
        LOAD_FAR("LOAD_FAR", 47, "", 999, "Load Far", false, false),
        PRELOADED_CHUNKS("PRELOADED_CHUNKS", 48, "", 999, "Preloaded Chunks", false, false),
        SMOOTH_FPS("SMOOTH_FPS", 49, "", 999, "of.options.SMOOTH_FPS", false, false),
        CLOUDS("CLOUDS", 50, "", 999, "of.options.CLOUDS", false, false),
        CLOUD_HEIGHT("CLOUD_HEIGHT", 51, "", 999, "of.options.CLOUD_HEIGHT", true, false),
        TREES("TREES", 52, "", 999, "of.options.TREES", false, false),
        GRASS("GRASS", 53, "", 999, "Grass", false, false),
        RAIN("RAIN", 54, "", 999, "of.options.RAIN", false, false),
        WATER("WATER", 55, "", 999, "Water", false, false),
        ANIMATED_WATER("ANIMATED_WATER", 56, "", 999, "of.options.ANIMATED_WATER", false, false),
        ANIMATED_LAVA("ANIMATED_LAVA", 57, "", 999, "of.options.ANIMATED_LAVA", false, false),
        ANIMATED_FIRE("ANIMATED_FIRE", 58, "", 999, "of.options.ANIMATED_FIRE", false, false),
        ANIMATED_PORTAL("ANIMATED_PORTAL", 59, "", 999, "of.options.ANIMATED_PORTAL", false, false),
        AO_LEVEL("AO_LEVEL", 60, "", 999, "of.options.AO_LEVEL", true, false),
        LAGOMETER("LAGOMETER", 61, "", 999, "of.options.LAGOMETER", false, false),
        SHOW_FPS("SHOW_FPS", 62, "", 999, "of.options.SHOW_FPS", false, false),
        AUTOSAVE_TICKS("AUTOSAVE_TICKS", 63, "", 999, "of.options.AUTOSAVE_TICKS", false, false),
        BETTER_GRASS("BETTER_GRASS", 64, "", 999, "of.options.BETTER_GRASS", false, false),
        ANIMATED_REDSTONE("ANIMATED_REDSTONE", 65, "", 999, "of.options.ANIMATED_REDSTONE", false, false),
        ANIMATED_EXPLOSION("ANIMATED_EXPLOSION", 66, "", 999, "of.options.ANIMATED_EXPLOSION", false, false),
        ANIMATED_FLAME("ANIMATED_FLAME", 67, "", 999, "of.options.ANIMATED_FLAME", false, false),
        ANIMATED_SMOKE("ANIMATED_SMOKE", 68, "", 999, "of.options.ANIMATED_SMOKE", false, false),
        WEATHER("WEATHER", 69, "", 999, "of.options.WEATHER", false, false),
        SKY("SKY", 70, "", 999, "of.options.SKY", false, false),
        STARS("STARS", 71, "", 999, "of.options.STARS", false, false),
        SUN_MOON("SUN_MOON", 72, "", 999, "of.options.SUN_MOON", false, false),
        VIGNETTE("VIGNETTE", 73, "", 999, "of.options.VIGNETTE", false, false),
        CHUNK_UPDATES("CHUNK_UPDATES", 74, "", 999, "of.options.CHUNK_UPDATES", false, false),
        CHUNK_UPDATES_DYNAMIC("CHUNK_UPDATES_DYNAMIC", 75, "", 999, "of.options.CHUNK_UPDATES_DYNAMIC", false, false),
        TIME("TIME", 76, "", 999, "of.options.TIME", false, false),
        CLEAR_WATER("CLEAR_WATER", 77, "", 999, "of.options.CLEAR_WATER", false, false),
        SMOOTH_WORLD("SMOOTH_WORLD", 78, "", 999, "of.options.SMOOTH_WORLD", false, false),
        DEPTH_FOG("DEPTH_FOG", 79, "", 999, "Depth Fog", false, false),
        VOID_PARTICLES("VOID_PARTICLES", 80, "", 999, "of.options.VOID_PARTICLES", false, false),
        WATER_PARTICLES("WATER_PARTICLES", 81, "", 999, "of.options.WATER_PARTICLES", false, false),
        RAIN_SPLASH("RAIN_SPLASH", 82, "", 999, "of.options.RAIN_SPLASH", false, false),
        PORTAL_PARTICLES("PORTAL_PARTICLES", 83, "", 999, "of.options.PORTAL_PARTICLES", false, false),
        POTION_PARTICLES("POTION_PARTICLES", 84, "", 999, "of.options.POTION_PARTICLES", false, false),
        PROFILER("PROFILER", 85, "", 999, "of.options.PROFILER", false, false),
        DRIPPING_WATER_LAVA("DRIPPING_WATER_LAVA", 86, "", 999, "of.options.DRIPPING_WATER_LAVA", false, false),
        BETTER_SNOW("BETTER_SNOW", 87, "", 999, "of.options.BETTER_SNOW", false, false),
        FULLSCREEN_MODE("FULLSCREEN_MODE", 88, "", 999, "of.options.FULLSCREEN_MODE", false, false),
        ANIMATED_TERRAIN("ANIMATED_TERRAIN", 89, "", 999, "of.options.ANIMATED_TERRAIN", false, false),
        ANIMATED_ITEMS("ANIMATED_ITEMS", 90, "", 999, "Items Animated", false, false),
        SWAMP_COLORS("SWAMP_COLORS", 91, "", 999, "of.options.SWAMP_COLORS", false, false),
        RANDOM_MOBS("RANDOM_MOBS", 92, "", 999, "of.options.RANDOM_MOBS", false, false),
        SMOOTH_BIOMES("SMOOTH_BIOMES", 93, "", 999, "of.options.SMOOTH_BIOMES", false, false),
        CUSTOM_FONTS("CUSTOM_FONTS", 94, "", 999, "of.options.CUSTOM_FONTS", false, false),
        CUSTOM_COLORS("CUSTOM_COLORS", 95, "", 999, "of.options.CUSTOM_COLORS", false, false),
        SHOW_CAPES("SHOW_CAPES", 96, "", 999, "of.options.SHOW_CAPES", false, false),
        CONNECTED_TEXTURES("CONNECTED_TEXTURES", 97, "", 999, "of.options.CONNECTED_TEXTURES", false, false),
        AA_LEVEL("AA_LEVEL", 98, "", 999, "of.options.AA_LEVEL", true, false, 0.0F, 16.0F, 1.0F),
        ANIMATED_TEXTURES("ANIMATED_TEXTURES", 99, "", 999, "of.options.ANIMATED_TEXTURES", false, false),
        NATURAL_TEXTURES("NATURAL_TEXTURES", 100, "", 999, "of.options.NATURAL_TEXTURES", false, false),
        CHUNK_LOADING("CHUNK_LOADING", 101, "", 999, "Chunk Loading", false, false),
        HELD_ITEM_TOOLTIPS("HELD_ITEM_TOOLTIPS", 102, "", 999, "of.options.HELD_ITEM_TOOLTIPS", false, false),
        DROPPED_ITEMS("DROPPED_ITEMS", 103, "", 999, "of.options.DROPPED_ITEMS", false, false),
        LAZY_CHUNK_LOADING("LAZY_CHUNK_LOADING", 104, "", 999, "of.options.LAZY_CHUNK_LOADING", false, false),
        CUSTOM_SKY("CUSTOM_SKY", 105, "", 999, "of.options.CUSTOM_SKY", false, false),
        FAST_MATH("FAST_MATH", 106, "", 999, "of.options.FAST_MATH", false, false),
        FAST_RENDER("FAST_RENDER", 107, "", 999, "of.options.FAST_RENDER", false, false),
        TRANSLUCENT_BLOCKS("TRANSLUCENT_BLOCKS", 108, "", 999, "of.options.TRANSLUCENT_BLOCKS", false, false),
        DYNAMIC_FOV("DYNAMIC_FOV", 109, "", 999, "of.options.DYNAMIC_FOV", false, false),
        DYNAMIC_LIGHTS("DYNAMIC_LIGHTS", 110, "", 999, "of.options.DYNAMIC_LIGHTS", false, false);

        public String enumString;
        public boolean enumFloat;
        public boolean enumBoolean;
        public float valueMin;
        public float valueMax;
        public float valueStep;
        public static GameSettings.Options[] optionsArray = new GameSettings.Options[]{
            INVERT_MOUSE,
            SENSITIVITY,
            FOV,
            GAMMA,
            SATURATION,
            RENDER_DISTANCE,
            VIEW_BOBBING,
            ANAGLYPH,
            ADVANCED_OPENGL,
            FRAMERATE_LIMIT,
            FBO_ENABLE,
            DIFFICULTY,
            GRAPHICS,
            AMBIENT_OCCLUSION,
            GUI_SCALE,
            RENDER_CLOUDS,
            PARTICLES,
            CHAT_VISIBILITY,
            CHAT_COLOR,
            CHAT_LINKS,
            CHAT_OPACITY,
            CHAT_LINKS_PROMPT,
            SNOOPER_ENABLED,
            USE_FULLSCREEN,
            ENABLE_VSYNC,
            SHOW_CAPE,
            TOUCHSCREEN,
            CHAT_SCALE,
            CHAT_WIDTH,
            CHAT_HEIGHT_FOCUSED,
            CHAT_HEIGHT_UNFOCUSED,
            MIPMAP_LEVELS,
            ANISOTROPIC_FILTERING,
            FORCE_UNICODE_FONT,
            STREAM_BYTES_PER_PIXEL,
            STREAM_VOLUME_MIC,
            STREAM_VOLUME_SYSTEM,
            STREAM_KBPS,
            STREAM_FPS,
            STREAM_COMPRESSION,
            STREAM_SEND_METADATA,
            STREAM_CHAT_ENABLED,
            STREAM_CHAT_USER_FILTER,
            STREAM_MIC_TOGGLE_BEHAVIOR
        };
        public static GameSettings.Options[] $VALUES = new GameSettings.Options[]{
            INVERT_MOUSE,
            SENSITIVITY,
            FOV,
            GAMMA,
            SATURATION,
            RENDER_DISTANCE,
            VIEW_BOBBING,
            ANAGLYPH,
            ADVANCED_OPENGL,
            FRAMERATE_LIMIT,
            FBO_ENABLE,
            DIFFICULTY,
            GRAPHICS,
            AMBIENT_OCCLUSION,
            GUI_SCALE,
            RENDER_CLOUDS,
            PARTICLES,
            CHAT_VISIBILITY,
            CHAT_COLOR,
            CHAT_LINKS,
            CHAT_OPACITY,
            CHAT_LINKS_PROMPT,
            SNOOPER_ENABLED,
            USE_FULLSCREEN,
            ENABLE_VSYNC,
            SHOW_CAPE,
            TOUCHSCREEN,
            CHAT_SCALE,
            CHAT_WIDTH,
            CHAT_HEIGHT_FOCUSED,
            CHAT_HEIGHT_UNFOCUSED,
            MIPMAP_LEVELS,
            ANISOTROPIC_FILTERING,
            FORCE_UNICODE_FONT,
            STREAM_BYTES_PER_PIXEL,
            STREAM_VOLUME_MIC,
            STREAM_VOLUME_SYSTEM,
            STREAM_KBPS,
            STREAM_FPS,
            STREAM_COMPRESSION,
            STREAM_SEND_METADATA,
            STREAM_CHAT_ENABLED,
            STREAM_CHAT_USER_FILTER,
            STREAM_MIC_TOGGLE_BEHAVIOR,
            FOG_FANCY,
            FOG_START,
            MIPMAP_TYPE,
            LOAD_FAR,
            PRELOADED_CHUNKS,
            SMOOTH_FPS,
            CLOUDS,
            CLOUD_HEIGHT,
            TREES,
            GRASS,
            RAIN,
            WATER,
            ANIMATED_WATER,
            ANIMATED_LAVA,
            ANIMATED_FIRE,
            ANIMATED_PORTAL,
            AO_LEVEL,
            LAGOMETER,
            SHOW_FPS,
            AUTOSAVE_TICKS,
            BETTER_GRASS,
            ANIMATED_REDSTONE,
            ANIMATED_EXPLOSION,
            ANIMATED_FLAME,
            ANIMATED_SMOKE,
            WEATHER,
            SKY,
            STARS,
            SUN_MOON,
            VIGNETTE,
            CHUNK_UPDATES,
            CHUNK_UPDATES_DYNAMIC,
            TIME,
            CLEAR_WATER,
            SMOOTH_WORLD,
            DEPTH_FOG,
            VOID_PARTICLES,
            WATER_PARTICLES,
            RAIN_SPLASH,
            PORTAL_PARTICLES,
            POTION_PARTICLES,
            PROFILER,
            DRIPPING_WATER_LAVA,
            BETTER_SNOW,
            FULLSCREEN_MODE,
            ANIMATED_TERRAIN,
            ANIMATED_ITEMS,
            SWAMP_COLORS,
            RANDOM_MOBS,
            SMOOTH_BIOMES,
            CUSTOM_FONTS,
            CUSTOM_COLORS,
            SHOW_CAPES,
            CONNECTED_TEXTURES,
            AA_LEVEL,
            ANIMATED_TEXTURES,
            NATURAL_TEXTURES,
            CHUNK_LOADING,
            HELD_ITEM_TOOLTIPS,
            DROPPED_ITEMS,
            LAZY_CHUNK_LOADING,
            CUSTOM_SKY,
            FAST_MATH,
            FAST_RENDER,
            TRANSLUCENT_BLOCKS,
            DYNAMIC_FOV,
            DYNAMIC_LIGHTS
        };

        public static GameSettings.Options getEnumOptions(int n) {
            for (GameSettings.Options options : values()) {
                if (options.returnEnumOrdinal() == n) {
                    return options;
                }
            }

            return null;
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

        public void setValueMax(float f) {
            this.valueMax = f;
        }

        public float normalizeValue(float f) {
            return MathHelper.clamp_float((this.snapToStepClamp(f) - this.valueMin) / (this.valueMax - this.valueMin), 0.0F, 1.0F);
        }

        public float denormalizeValue(float f) {
            return this.snapToStepClamp(this.valueMin + (this.valueMax - this.valueMin) * MathHelper.clamp_float(f, 0.0F, 1.0F));
        }

        public float snapToStepClamp(float f) {
            f = this.snapToStep(f);
            return MathHelper.clamp_float(f, this.valueMin, this.valueMax);
        }

        public float snapToStep(float f) {
            if (this.valueStep > 0.0F) {
                f = this.valueStep * Math.round(f / this.valueStep);
            }

            return f;
        }

        Options(String str, int n, String str2, int n2, String str3, boolean flag, boolean flag2, float f, float f2, float f3) {
            this.enumString = str3;
            this.enumFloat = flag;
            this.enumBoolean = flag2;
            this.valueMin = f;
            this.valueMax = f2;
            this.valueStep = f3;
        }

        Options(String str, int n, String str2, int n2, String str3, boolean flag, boolean flag2) {
            this(str, n, str2, n2, str3, flag, flag2, 0.0F, 1.0F, 0.0F);
        }

        Options(
            String str,
            int n,
            String str2,
            int n2,
            String str3,
            boolean flag,
            boolean flag2,
            float f,
            float f2,
            float f3,
            Object obj,
            Object obj2
        ) {
            this(str, n, str2, n2, str3, flag, flag2, f, f2, f3, obj);
        }

        Options(String str, int n, String str2, int n2, String str3, boolean flag, boolean flag2, float f, float f2, float f3, Object obj) {
            this(str, n, str2, n2, str3, flag, flag2, f, f2, f3);
        }
    }

    static final class SwitchOptions {
        public static int[] optionIds = new int[GameSettings.Options.values().length];

        public SwitchOptions() {
        }

        static {
            try {
                optionIds[GameSettings.Options.INVERT_MOUSE.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }

            try {
                optionIds[GameSettings.Options.VIEW_BOBBING.ordinal()] = 2;
            } catch (NoSuchFieldError e) {
            }

            try {
                optionIds[GameSettings.Options.ANAGLYPH.ordinal()] = 3;
            } catch (NoSuchFieldError e) {
            }

            try {
                optionIds[GameSettings.Options.ADVANCED_OPENGL.ordinal()] = 4;
            } catch (NoSuchFieldError e) {
            }

            try {
                optionIds[GameSettings.Options.FBO_ENABLE.ordinal()] = 5;
            } catch (NoSuchFieldError e) {
            }

            try {
                optionIds[GameSettings.Options.RENDER_CLOUDS.ordinal()] = 6;
            } catch (NoSuchFieldError e) {
            }

            try {
                optionIds[GameSettings.Options.CHAT_COLOR.ordinal()] = 7;
            } catch (NoSuchFieldError e) {
            }

            try {
                optionIds[GameSettings.Options.CHAT_LINKS.ordinal()] = 8;
            } catch (NoSuchFieldError e) {
            }

            try {
                optionIds[GameSettings.Options.CHAT_LINKS_PROMPT.ordinal()] = 9;
            } catch (NoSuchFieldError e) {
            }

            try {
                optionIds[GameSettings.Options.SNOOPER_ENABLED.ordinal()] = 10;
            } catch (NoSuchFieldError e) {
            }

            try {
                optionIds[GameSettings.Options.USE_FULLSCREEN.ordinal()] = 11;
            } catch (NoSuchFieldError e) {
            }

            try {
                optionIds[GameSettings.Options.ENABLE_VSYNC.ordinal()] = 12;
            } catch (NoSuchFieldError e) {
            }

            try {
                optionIds[GameSettings.Options.SHOW_CAPE.ordinal()] = 13;
            } catch (NoSuchFieldError e) {
            }

            try {
                optionIds[GameSettings.Options.TOUCHSCREEN.ordinal()] = 14;
            } catch (NoSuchFieldError e) {
            }

            try {
                optionIds[GameSettings.Options.STREAM_SEND_METADATA.ordinal()] = 15;
            } catch (NoSuchFieldError e) {
            }

            try {
                optionIds[GameSettings.Options.FORCE_UNICODE_FONT.ordinal()] = 16;
            } catch (NoSuchFieldError e) {
            }
        }
    }
}
