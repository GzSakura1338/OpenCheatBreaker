package net.minecraft.client;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.BooleanIntEvent;
import com.cheatbreaker.client.event.ClientTickEvent;
import com.cheatbreaker.client.event.GameRendererPostEvent;
import com.cheatbreaker.client.event.GameRendererPreEvent;
import com.cheatbreaker.client.event.GuiScreenEvent;
import com.cheatbreaker.client.event.IntEvent1;
import com.cheatbreaker.client.event.IntEvent2;
import com.cheatbreaker.client.event.IntEvent4;
import com.cheatbreaker.client.event.IntEvent6;
import com.cheatbreaker.client.event.SimpleEvent1;
import com.cheatbreaker.client.event.SimpleEvent18;
import com.cheatbreaker.client.event.SimpleEvent26;
import com.cheatbreaker.client.event.SimpleEvent34;
import com.cheatbreaker.client.event.SimpleEvent37;
import com.cheatbreaker.client.event.SimpleEvent49;
import com.cheatbreaker.client.event.WorldClientEvent;
import com.cheatbreaker.client.ui.screen.AssetServerConnectingScreen;
import com.cheatbreaker.client.ui.screen.ClassicMainMenuScreen;
import com.cheatbreaker.client.ui.screen.ClientHomeScreen;
import com.cheatbreaker.client.ui.screen.ClientValidationFailedScreen;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import com.cheatbreaker.client.ui.screen.GpuDriverUpdateScreen;
import com.cheatbreaker.client.ui.screen.InitializationScreen;
import com.cheatbreaker.client.ui.screen.MainMenuScreen;
import com.cheatbreaker.client.ui.screen.MainMenuStyle;
import com.cheatbreaker.client.ui.screen.ModsScreen;
import com.cheatbreaker.client.ui.screen.ResourcePackSync;
import com.cheatbreaker.client.ui.screen.VoiceChatScreen;
import com.cheatbreaker.client.util.ClickTracker;
import com.cheatbreaker.client.util.ClientValidator;
import com.cheatbreaker.client.util.CrashReporter;
import com.cheatbreaker.client.util.GpuDriverChecker;
import com.cheatbreaker.client.util.GpuDriverStatus;
import com.cheatbreaker.client.util.ScreenshotHelper;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.net.SocketAddress;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.audio.MusicTicker.MusicType;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSleepMP;
import net.minecraft.client.gui.GuiWinGame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.achievement.GuiAchievement;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.FoliageColorReloadListener;
import net.minecraft.client.resources.GrassColorReloadListener;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.resources.ResourceIndex;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.AnimationMetadataSectionSerializer;
import net.minecraft.client.resources.data.FontMetadataSection;
import net.minecraft.client.resources.data.FontMetadataSectionSerializer;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.LanguageMetadataSection;
import net.minecraft.client.resources.data.LanguageMetadataSectionSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.resources.data.PackMetadataSectionSerializer;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSectionSerializer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.settings.GameSettings.Options;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLeashKnot;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer.EnumChatVisibility;
import net.minecraft.init.Bootstrap;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.network.play.client.C16PacketClientStatus;
import net.minecraft.network.play.client.C16PacketClientStatus.EnumState;
import net.minecraft.potion.Potion;
import net.minecraft.profiler.IPlayerUsage;
import net.minecraft.profiler.PlayerUsageSnooper;
import net.minecraft.profiler.Profiler;
import net.minecraft.profiler.Profiler.Result;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.stats.AchievementList;
import net.minecraft.stats.StatFileWriter;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MinecraftError;
import net.minecraft.util.MouseHelper;
import net.minecraft.util.MovementInputFromOptions;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import net.minecraft.util.Util;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Util.EnumOS;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.chunk.storage.AnvilSaveConverter;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.optifine.Config;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.OpenGLException;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;

public class Minecraft implements IPlayerUsage {
    public static Logger logger = LogManager.getLogger();
    public static ResourceLocation locationMojangPng = new ResourceLocation("textures/gui/title/mojang.png");
    public static boolean isRunningOnMac = Util.getOSType() == EnumOS.OSX;
    public static byte[] memoryReserve = new byte[10485760];
    public static List<DisplayMode> macDisplayModes = Lists.newArrayList(new DisplayMode[]{new DisplayMode(2560, 1600), new DisplayMode(2880, 1800)});
    public Timer timer = new Timer(20.0F);
    public PlayerUsageSnooper usageSnooper = new PlayerUsageSnooper("client", this, MinecraftServer.getCurrentTimeMillis());
    public ServerData currentServerData;
    public TextureManager renderEngine;
    public static Minecraft theMinecraft;
    public PlayerControllerMP playerController;
    public long systemTime = getSystemTime();
    public boolean hasCrashed;
    public CrashReport crashReporter;
    public FrameTimer stream = new FrameTimer();
    public long longValue = System.nanoTime();
    public Profiler mcProfiler = new Profiler();
    public long debugCrashKeyPressTime = -1L;
    public WorldClient theWorld;
    public RenderGlobal renderGlobal;
    public EntityClientPlayerMP thePlayer;
    public EntityLivingBase renderViewEntity;
    public Entity pointedEntity;
    public EffectRenderer effectRenderer;
    public IMetadataSerializer metadataSerializer_ = new IMetadataSerializer();
    public boolean isGamePaused;
    public FontRenderer fontRendererObj;
    public FontRenderer standardGalacticFontRenderer;
    public GuiScreen currentScreen;
    public LoadingScreenRenderer loadingScreen;
    public EntityRenderer entityRenderer;
    public int leftClickCounter;
    public List defaultResourcePacks = Lists.newArrayList();
    public Queue scheduledTasks = Queues.newArrayDeque();
    public IntegratedServer theIntegratedServer;
    public GuiAchievement guiAchievement;
    public GuiIngame ingameGUI;
    public boolean skipRenderWorld;
    public MovingObjectPosition objectMouseOver;
    public GameSettings gameSettings;
    public MouseHelper mouseHelper;
    public Thread mcThread = Thread.currentThread();
    public volatile boolean running = true;
    public String debug = "";
    public long debugUpdateTime = getSystemTime();
    public ISaveFormat saveLoader;
    public static int debugFPS;
    public int rightClickDelayTimer;
    public boolean refreshTexturePacksScheduled;
    public String serverName;
    public int serverPort;
    public boolean inGameHasFocus;
    public long prevFrameTime = -1L;
    public int joinPlayerCounter;
    public String debugProfilerName = "root";
    public File mcDataDir;
    public NetworkManager myNetworkManager;
    public boolean integratedServerIsRunning;
    public File fileAssets;
    public File fileResourcepacks;
    public IReloadableResourceManager mcResourceManager;
    public String launchedVersion;
    public Multimap twitchDetails;
    public DefaultResourcePack mcDefaultResourcePack;
    public ResourcePackRepository mcResourcePackRepository;
    public LanguageManager mcLanguageManager;
    public Proxy proxy;
    public Framebuffer framebufferMc;
    public TextureMap textureMapBlocks;
    public SoundHandler mcSoundHandler;
    public MusicTicker mcMusicTicker;
    public ResourceLocation mojangLogo;
    public MinecraftSessionService sessionService;
    public SkinManager skinManager;
    public Session session;
    public boolean isDemo;
    public int displayWidth;
    public int displayHeight;
    public int tempDisplayWidth;
    public int fpsCounter;
    public int tempDisplayHeight;
    public boolean fullscreen;
    public boolean jvm64bit;
    public boolean flag;
    public ServerData serverData;
    public boolean flag2;
    public InitializationScreen initializationScreen;

    public Minecraft(
        Session sessionIn,
        int displayWidth,
        int displayHeight,
        boolean fullscreen,
        boolean isDemo,
        File dataDir,
        File assetsDir,
        File resourcePackDir,
        Proxy proxy,
        String version,
        Multimap twitchDetails,
        String assetsJsonVersion
    ) {
        theMinecraft = this;
        this.mcDataDir = dataDir;
        this.fileAssets = assetsDir;
        this.fileResourcepacks = resourcePackDir;
        this.launchedVersion = version;
        this.twitchDetails = twitchDetails;
        this.mcDefaultResourcePack = new DefaultResourcePack(new ResourceIndex(assetsDir, assetsJsonVersion).getIndex());
        this.addDefaultResourcePack();
        this.proxy = proxy == null ? Proxy.NO_PROXY : proxy;
        this.sessionService = new YggdrasilAuthenticationService(proxy, UUID.randomUUID().toString()).createMinecraftSessionService();
        this.startTimerHackThread();
        this.session = sessionIn;
        logger.info("Setting user: " + sessionIn.getUsername());
        logger.info("Setting UUID: " + sessionIn.getPlayerID());
        this.isDemo = isDemo;
        this.displayWidth = displayWidth;
        this.displayHeight = displayHeight;
        this.tempDisplayWidth = displayWidth;
        this.tempDisplayHeight = displayHeight;
        this.fullscreen = fullscreen;
        this.jvm64bit = isJvm64bit();
        ImageIO.setUseCache(false);
        Bootstrap.register();
    }

    public static boolean isJvm64bit() {
        String[] strs = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};
        String[] strs2 = strs;
        int length = strs.length;

        for (int i = 0; i < length; i++) {
            String str = strs2[i];
            String property = System.getProperty(str);
            if (property != null && property.contains("64")) {
                return true;
            }
        }

        return false;
    }

    public Framebuffer getFramebuffer() {
        return this.framebufferMc;
    }

    public void startTimerHackThread() {
        Thread thread = new Thread("Timer hack thread") {
            @Override
            public void run() {
                while (Minecraft.this.running) {
                    try {
                        Thread.sleep(2147483647L);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public void crashed(CrashReport crash) {
        this.hasCrashed = true;
        this.crashReporter = crash;
    }

    public void displayCrashReport(CrashReport crashReportIn) {
        File file = new File(getMinecraft().mcDataDir, "crash-reports");
        File file2 = new File(file, "crash-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + "-com.cheatbreaker.client.txt");
        System.out.println(crashReportIn.getCompleteReport());
        CrashReporter.handleCrash(crashReportIn.getCrashCause(), crashReportIn.getCompleteReport());
        if (crashReportIn.getFile() != null) {
            System.out.println("#@!@# Game crashed! Crash report saved to: #@!@# " + crashReportIn.getFile());
            System.exit(-1);
        } else if (crashReportIn.saveToFile(file2)) {
            System.out.println("#@!@# Game crashed! Crash report saved to: #@!@# " + file2.getAbsolutePath());
            System.exit(-1);
        } else {
            System.out.println("#@?@# Game crashed! Crash report could not be saved. #@?@#");
            System.exit(-2);
        }
    }

    public void setServer(String serverHostname, int serverPort) {
        this.serverName = serverHostname;
        this.serverPort = serverPort;
    }

    public void startGame() throws LWJGLException, IOException {
        this.gameSettings = new GameSettings(this, this.mcDataDir);
        if (this.gameSettings.overrideHeight > 0 && this.gameSettings.overrideWidth > 0) {
            this.displayWidth = this.gameSettings.overrideWidth;
            this.displayHeight = this.gameSettings.overrideHeight;
        }

        if (this.fullscreen) {
            Display.setFullscreen(true);
            this.displayWidth = Display.getDisplayMode().getWidth();
            this.displayHeight = Display.getDisplayMode().getHeight();
            if (this.displayWidth <= 0) {
                this.displayWidth = 1;
            }

            if (this.displayHeight <= 0) {
                this.displayHeight = 1;
            }
        } else {
            Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
        }

        Display.setResizable(true);
        if (CheatBreaker.isAprilFoolsEnabled()) {
            Display.setTitle("CheatBreaker game");
        } else {
            Display.setTitle("CheatBreaker 1.7.10");
        }

        logger.info("LWJGL Version: " + Sys.getVersion());
        EnumOS oSType = Util.getOSType();
        if (oSType != EnumOS.OSX) {
            try {
                InputStream inputStream = this.mcDefaultResourcePack.getInputStream(new ResourceLocation("client/icon-1.png"));
                InputStream inputStream2 = this.mcDefaultResourcePack.getInputStream(new ResourceLocation("client/icon-2.png"));
                if (inputStream != null && inputStream2 != null) {
                    Display.setIcon(new ByteBuffer[]{this.readImageToBuffer(inputStream), this.readImageToBuffer(inputStream2)});
                }
            } catch (IOException e) {
                logger.error("Couldn't set icon", e);
            }
        } else {
            try {
                InputStream inputStream = this.mcDefaultResourcePack.getInputStream(new ResourceLocation("client/icon-3.png"));
                if (inputStream != null) {
                    BufferedImage read = ImageIO.read(inputStream);

                    try {
                        Class forName = Class.forName("java.awt.Taskbar");
                        Method method = forName.getMethod("getTaskbar");
                        Object invoke = method.invoke(null);
                        Method methodRef2 = forName.getMethod("setIconImage", Image.class);
                        methodRef2.invoke(invoke, read);

                        try {
                            Method methodRef3 = forName.getMethod("setIconBadge", String.class);
                            methodRef3.invoke(invoke, "1.7.10");
                        } catch (NoSuchMethodException e) {
                            try {
                                Method methodRef3 = forName.getMethod("setBadge", String.class);
                                methodRef3.invoke(invoke, "1.7.10");
                            } catch (NoSuchMethodException e2) {
                            }
                        }
                    } catch (Throwable e) {
                        try {
                            Class forName = Class.forName("com.apple.eawt.Application");
                            Method method = forName.getMethod("getApplication");
                            Object invoke = method.invoke(null);
                            Method methodRef2 = forName.getMethod("setDockIconImage", Image.class);
                            methodRef2.invoke(invoke, read);
                            Method methodRef3 = forName.getMethod("setDockIconBadge", String.class);
                            methodRef3.invoke(invoke, "1.7.10");
                        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e2) {
                            System.err.println("Unable to set application icon/badge: " + e2.getMessage());
                        }
                    }
                }
            } catch (IOException e) {
                logger.error("Couldn't set icon", e);
            }
        }

        try {
            Display.create(new PixelFormat().withDepthBits(24));
        } catch (LWJGLException e) {
            logger.error("Couldn't set pixel format", e);

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e2) {
            }

            if (this.fullscreen) {
                this.updateDisplayMode();
            }

            Display.create();
        }

        OpenGlHelper.initializeTextures();
        this.framebufferMc = new Framebuffer(this.displayWidth, this.displayHeight, true);
        this.framebufferMc.setFramebufferColor(0.0F, 0.0F, 0.0F, 0.0F);
        this.guiAchievement = new GuiAchievement(this);
        this.metadataSerializer_.registerMetadataSectionType(new TextureMetadataSectionSerializer(), TextureMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new FontMetadataSectionSerializer(), FontMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new AnimationMetadataSectionSerializer(), AnimationMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new PackMetadataSectionSerializer(), PackMetadataSection.class);
        this.metadataSerializer_.registerMetadataSectionType(new LanguageMetadataSectionSerializer(), LanguageMetadataSection.class);
        this.saveLoader = new AnvilSaveConverter(new File(this.mcDataDir, "saves"));
        this.mcResourcePackRepository = new ResourcePackRepository(
            this.fileResourcepacks, new File(this.mcDataDir, "server-resource-packs"), this.mcDefaultResourcePack, this.metadataSerializer_, this.gameSettings
        );
        this.mcResourceManager = new SimpleReloadableResourceManager(this.metadataSerializer_);
        this.mcLanguageManager = new LanguageManager(this.metadataSerializer_, this.gameSettings.language);
        this.mcResourceManager.registerReloadListener(this.mcLanguageManager);
        new ResourcePackSync();
        this.refreshResources();
        this.renderEngine = new TextureManager(this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.renderEngine);
        this.skinManager = new SkinManager(this.renderEngine, new File(this.fileAssets, "skins"), this.sessionService);
        this.initializationScreen = new InitializationScreen(112);
        this.initializationScreen.drawScreenScaled(0.0F, 0.0F);
        this.initializationScreen.setPhase("Sound Handler");
        this.mcSoundHandler = new SoundHandler(this.mcResourceManager, this.gameSettings);
        this.mcResourceManager.registerReloadListener(this.mcSoundHandler);
        this.mcMusicTicker = new MusicTicker(this);
        this.initializationScreen.setPhase("Font Renderers");
        this.fontRendererObj = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii.png"), this.renderEngine, false);
        if (this.gameSettings.language != null) {
            this.fontRendererObj.setUnicodeFlag(this.isUnicode());
            this.fontRendererObj.setBidiFlag(this.mcLanguageManager.isCurrentLanguageBidirectional());
        }

        this.standardGalacticFontRenderer = new FontRenderer(this.gameSettings, new ResourceLocation("textures/font/ascii_sga.png"), this.renderEngine, false);
        this.mcResourceManager.registerReloadListener(this.fontRendererObj);
        this.mcResourceManager.registerReloadListener(this.standardGalacticFontRenderer);
        this.mcResourceManager.registerReloadListener(new GrassColorReloadListener());
        this.mcResourceManager.registerReloadListener(new FoliageColorReloadListener());
        this.initializationScreen.setPhase("Items");
        RenderManager.instance.itemRenderer = new ItemRenderer(this);
        this.initializationScreen.setPhase("Entities");
        this.entityRenderer = new EntityRenderer(this, this.mcResourceManager);
        this.mcResourceManager.registerReloadListener(this.entityRenderer);
        AchievementList.openInventory.setStatStringFormatter(str -> {
            try {
                return String.format(str, GameSettings.getKeyDisplayString(this.gameSettings.keyBindInventory.getKeyCode()));
            } catch (Exception e) {
                return "Error: " + e.getLocalizedMessage();
            }
        });
        this.mouseHelper = new MouseHelper();
        this.checkGLError("Pre startup");
        GL11.glEnable(3553);
        GL11.glShadeModel(7425);
        GL11.glClearDepth(1.0);
        GL11.glEnable(2929);
        GL11.glDepthFunc(515);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.1F);
        GL11.glCullFace(1029);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(5888);
        this.checkGLError("Startup");
        this.initializationScreen.setPhase("World");
        this.renderGlobal = new RenderGlobal(this);
        this.initializationScreen.setPhase("Blocks");
        this.textureMapBlocks = new TextureMap(0, "textures/blocks");
        this.initializationScreen.setPhase("Anisotropic Filtering");
        this.textureMapBlocks.setAnisotropicFiltering(this.gameSettings.anisotropicFiltering);
        this.initializationScreen.setPhase("Mipmaps");
        this.textureMapBlocks.setMipmapLevels(this.gameSettings.mipmapLevels);
        this.initializationScreen.setPhase("Textures");
        this.renderEngine.loadTextureMap(TextureMap.locationBlocksTexture, this.textureMapBlocks);
        this.renderEngine.loadTextureMap(TextureMap.locationItemsTexture, new TextureMap(1, "textures/items"));
        GL11.glViewport(0, 0, this.displayWidth, this.displayHeight);
        this.initializationScreen.setPhase("Effects");
        this.effectRenderer = new EffectRenderer(this.theWorld, this.renderEngine);
        this.checkGLError("Post startup");
        this.ingameGUI = new GuiIngame(this);
        new CheatBreaker().initialize();
        if (GpuDriverChecker.getGpuDriverStatus() != GpuDriverStatus.ACCEPTED_DRIVER) {
            this.displayGuiScreen(new GpuDriverUpdateScreen());
        } else if (!ClientValidator.isValid()) {
            this.displayGuiScreen(new ClientValidationFailedScreen());
        } else if (this.serverName != null) {
            MainMenuScreen.intValue++;
            ServerData serverData = new ServerData(this.serverName, this.serverName);
            this.setServerData(serverData);
            this.displayGuiScreen(new AssetServerConnectingScreen(serverData));
        } else {
            this.displayGuiScreen(new ClassicMainMenuScreen());
        }

        this.initializationScreen = null;
        this.renderEngine.deleteTexture(this.mojangLogo);
        this.mojangLogo = null;
        this.loadingScreen = new LoadingScreenRenderer(this);
        if (this.gameSettings.fullScreen && !this.fullscreen) {
            this.toggleFullscreen();
        }

        try {
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
        } catch (OpenGLException e) {
            this.gameSettings.enableVsync = false;
            this.gameSettings.saveOptions();
        }
    }

    public boolean isUnicode() {
        return this.mcLanguageManager.isCurrentLocaleUnicode() || this.gameSettings.forceUnicodeFont;
    }

    public void refreshResources() {
        ArrayList newArrayList = Lists.newArrayList(this.defaultResourcePacks);

        for (Entry entry : this.mcResourcePackRepository.getRepositoryEntries()) {
            newArrayList.add(entry.getResourcePack());
        }

        if (this.mcResourcePackRepository.getResourcePackInstance() != null) {
            newArrayList.add(this.mcResourcePackRepository.getResourcePackInstance());
        }

        try {
            this.mcResourceManager.reloadResources(newArrayList);
        } catch (RuntimeException e) {
            logger.info("Caught error stitching, removing all assigned resourcepacks", e);
            newArrayList.clear();
            newArrayList.addAll(this.defaultResourcePacks);
            this.mcResourcePackRepository.setSelectedResourcePacks(Collections.emptyList());
            this.mcResourceManager.reloadResources(newArrayList);
            this.gameSettings.resourcePacks.clear();
            this.gameSettings.saveOptions();
        }

        this.mcLanguageManager.parseLanguageMetadata(newArrayList);
        if (this.renderGlobal != null) {
            this.renderGlobal.loadRenderers();
        }
    }

    public void addDefaultResourcePack() {
        this.defaultResourcePacks.add(this.mcDefaultResourcePack);
    }

    public ByteBuffer readImageToBuffer(InputStream imageStream) throws IOException {
        BufferedImage read = ImageIO.read(imageStream);
        int[] rGB = read.getRGB(0, 0, read.getWidth(), read.getHeight(), null, 0, read.getWidth());
        ByteBuffer allocate = ByteBuffer.allocate(4 * rGB.length);
        int[] ints = rGB;
        int length = rGB.length;

        for (int i = 0; i < length; i++) {
            int n = ints[i];
            allocate.putInt(n << 8 | n >> 24 & 0xFF);
        }

        ((Buffer)allocate).flip();
        return allocate;
    }

    public void updateDisplayMode() throws LWJGLException {
        HashSet set = new HashSet();
        Collections.addAll(set, Display.getAvailableDisplayModes());
        DisplayMode desktopDisplayMode = Display.getDesktopDisplayMode();
        if (!set.contains(desktopDisplayMode) && Util.getOSType() == EnumOS.OSX) {
            for (DisplayMode displayMode : macDisplayModes) {
                boolean flag = true;

                for (DisplayMode displayMode2 : (Iterable<DisplayMode>)set) {
                    if (displayMode2.getBitsPerPixel() == 32 && displayMode2.getWidth() == displayMode.getWidth() && displayMode2.getHeight() == displayMode.getHeight()) {
                        flag = false;
                        break;
                    }
                }

                if (!flag) {
                    for (DisplayMode displayMode2 : (Iterable<DisplayMode>)set) {
                        if (displayMode2.getBitsPerPixel() == 32 && displayMode2.getWidth() == displayMode.getWidth() / 2 && displayMode2.getHeight() == displayMode.getHeight() / 2) {
                            desktopDisplayMode = displayMode2;
                            break;
                        }
                    }
                }
            }
        }

        Display.setDisplayMode(desktopDisplayMode);
        this.displayWidth = desktopDisplayMode.getWidth();
        this.displayHeight = desktopDisplayMode.getHeight();
    }

    public void loadScreen() throws LWJGLException, IOException {
        ScaledResolution scaledResolution = new ScaledResolution(this, this.displayWidth, this.displayHeight);
        int scaleFactor = scaledResolution.getScaleFactor();
        Framebuffer framebuffer = new Framebuffer(scaledResolution.getScaledWidth() * scaleFactor, scaledResolution.getScaledHeight() * scaleFactor, true);
        framebuffer.bindFramebuffer(false);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight(), 0.0, 1000.0, 3000.0);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
        GL11.glDisable(2896);
        GL11.glDisable(2912);
        GL11.glDisable(2929);
        GL11.glEnable(3553);

        try {
            this.mojangLogo = this.renderEngine
                .getDynamicTextureLocation("logo", new DynamicTexture(ImageIO.read(this.mcDefaultResourcePack.getInputStream(locationMojangPng))));
            this.renderEngine.bindTexture(this.mojangLogo);
        } catch (IOException e) {
            logger.error("Unable to load logo: " + locationMojangPng, e);
        }

        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.setColorOpaque_I(16777215);
        instance.addVertexWithUV(0.0, this.displayHeight, 0.0, 0.0, 0.0);
        instance.addVertexWithUV(this.displayWidth, this.displayHeight, 0.0, 0.0, 0.0);
        instance.addVertexWithUV(this.displayWidth, 0.0, 0.0, 0.0, 0.0);
        instance.addVertexWithUV(0.0, 0.0, 0.0, 0.0, 0.0);
        instance.draw();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        instance.setColorOpaque_I(16777215);
        short s = 256;
        short s2 = 256;
        this.scaledTessellator((scaledResolution.getScaledWidth() - s) / 2, (scaledResolution.getScaledHeight() - s2) / 2, 0, 0, s, s2);
        GL11.glDisable(2896);
        GL11.glDisable(2912);
        framebuffer.unbindFramebuffer();
        framebuffer.framebufferRender(scaledResolution.getScaledWidth() * scaleFactor, scaledResolution.getScaledHeight() * scaleFactor);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.1F);
        GL11.glFlush();
        this.resetSize();
    }

    public void scaledTessellator(int width, int height, int width2, int height2, int stdTextureWidth, int stdTextureHeight) {
        float f = 0.00390625F;
        float f2 = 0.00390625F;
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();
        instance.addVertexWithUV(width + 0, height + stdTextureHeight, 0.0, (width2 + 0) * f, (height2 + stdTextureHeight) * f2);
        instance.addVertexWithUV(width + stdTextureWidth, height + stdTextureHeight, 0.0, (width2 + stdTextureWidth) * f, (height2 + stdTextureHeight) * f2);
        instance.addVertexWithUV(width + stdTextureWidth, height + 0, 0.0, (width2 + stdTextureWidth) * f, (height2 + 0) * f2);
        instance.addVertexWithUV(width + 0, height + 0, 0.0, (width2 + 0) * f, (height2 + 0) * f2);
        instance.draw();
    }

    public ISaveFormat getSaveLoader() {
        return this.saveLoader;
    }

    public void displayGuiScreen(GuiScreen guiScreenIn) throws IOException {
        GuiScreenEvent guiScreenEvent = new GuiScreenEvent(guiScreenIn, this.currentScreen);
        CheatBreaker.getInstance().getEventBus().handleEvent(guiScreenEvent);
        if (!guiScreenEvent.isCancelled()) {
            if (this.currentScreen != null) {
                if (this.entityRenderer.isBlurActive()) {
                    try {
                        if (CheatBreaker.getInstance().getKeybindSettings().guiBlur.getBooleanValue()) {
                            this.entityRenderer.stopUseShader();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                this.currentScreen.onGuiClosed();
            }

            if (guiScreenIn == null && this.theWorld == null) {
                guiScreenIn = new ClassicMainMenuScreen();
            } else if (guiScreenIn == null && this.thePlayer.getHealth() <= 0.0F) {
                guiScreenIn = new GuiGameOver();
            }

            if (guiScreenIn instanceof ClassicMainMenuScreen) {
                this.gameSettings.showDebugInfo = false;
                SimpleEvent18 simpleEvent18 = new SimpleEvent18();
                CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent18);
                if (!simpleEvent18.isCancelled()) {
                    this.ingameGUI.getChatGUI().clearChatMessages();
                }
            }

            if (guiScreenIn instanceof ClassicMainMenuScreen) {
                guiScreenIn = MainMenuStyle.createScreen(CheatBreaker.getInstance().getKeybindSettings().getMainMenuStyle());
            }

            this.currentScreen = guiScreenIn;
            if (guiScreenIn != null) {
                this.setIngameNotInFocus();
                ScaledResolution scaledResolution = new ScaledResolution(this, this.displayWidth, this.displayHeight);
                int scaledWidth = scaledResolution.getScaledWidth();
                int scaledHeight = scaledResolution.getScaledHeight();
                guiScreenIn.setWorldAndResolution(this, scaledWidth, scaledHeight);
                this.skipRenderWorld = false;
            } else {
                this.mcSoundHandler.resumeSounds();
                this.setIngameFocus();
            }
        }
    }

    public void checkGLError(String message) {
        int glGetError = GL11.glGetError();
        if (glGetError != 0) {
            String gluErrorString = GLU.gluErrorString(glGetError);
            logger.error("########## GL ERROR ##########");
            logger.error("@ " + message);
            logger.error(glGetError + ": " + gluErrorString);
        }
    }

    public void shutdownMinecraftApplet() {
        try {
            this.gameSettings.saveOptions();
            logger.info("Stopping!");
            CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent1());

            try {
                this.loadWorld(null);
            } catch (Throwable e) {
            }

            try {
                GLAllocation.deleteTexturesAndDisplayLists();
            } catch (Throwable e) {
            }

            this.mcSoundHandler.unloadSounds();
        } finally {
            Display.destroy();
            if (!this.hasCrashed) {
                System.exit(0);
            }
        }
    }

    public void run() {
        this.running = true;

        try {
            this.startGame();
        } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Initializing game");
            makeCrashReport.makeCategory("Initialization");
            this.displayCrashReport(this.addGraphicsAndWorldToCrashReport(makeCrashReport));
            return;
        }

        try {
            try {
                while (this.running) {
                    if (this.hasCrashed && this.crashReporter != null) {
                        this.displayCrashReport(this.crashReporter);
                        return;
                    }

                    try {
                        this.runGameLoop();
                    } catch (OutOfMemoryError e) {
                        this.freeMemory();
                        this.displayGuiScreen(new GuiMemoryErrorScreen());
                    }
                }

                return;
            } catch (MinecraftError e) {
            } catch (ReportedException e) {
                this.addGraphicsAndWorldToCrashReport(e.getCrashReport());
                this.freeMemory();
                logger.fatal("Reported exception thrown!", e);
                this.displayCrashReport(e.getCrashReport());
            } catch (Throwable e) {
                CrashReport crashReport = this.addGraphicsAndWorldToCrashReport(new CrashReport("Unexpected error", e));
                this.freeMemory();
                logger.fatal("Unreported exception thrown!", e);
                this.displayCrashReport(crashReport);
            }
        } finally {
            this.shutdownMinecraftApplet();
        }
    }

    public void runGameLoop() throws IOException {
        this.mcProfiler.startSection("root");
        if (Display.isCreated() && Display.isCloseRequested()) {
            this.shutdown();
        }

        if (this.isGamePaused && this.theWorld != null) {
            float renderPartialTicks = this.timer.renderPartialTicks;
            this.timer.updateTimer();
            this.timer.renderPartialTicks = renderPartialTicks;
        } else {
            this.timer.updateTimer();
        }

        if ((this.theWorld == null || this.currentScreen == null) && this.refreshTexturePacksScheduled) {
            this.refreshTexturePacksScheduled = false;
            this.refreshResources();
        }

        long nanoTime = System.nanoTime();
        this.mcProfiler.startSection("tick");

        for (int i = 0; i < this.timer.elapsedTicks; i++) {
            this.runTick();
        }

        this.mcProfiler.endStartSection("preRenderErrors");
        long l = System.nanoTime() - nanoTime;
        this.checkGLError("Pre render");
        RenderBlocks.fancyGrass = this.gameSettings.fancyGraphics;
        this.mcProfiler.endStartSection("sound");
        this.mcSoundHandler.setListener(this.thePlayer, this.timer.renderPartialTicks);
        this.mcProfiler.endSection();
        this.mcProfiler.startSection("render");
        GL11.glPushMatrix();
        GL11.glClear(16640);
        this.framebufferMc.bindFramebuffer(true);
        this.mcProfiler.startSection("display");
        GL11.glEnable(3553);
        if (this.thePlayer != null && this.thePlayer.isEntityInsideOpaqueBlock()) {
            this.gameSettings.thirdPersonView = 0;
        }

        this.mcProfiler.endSection();
        if (!this.skipRenderWorld) {
            this.mcProfiler.endStartSection("gameRenderer");
            CheatBreaker.getInstance().getEventBus().handleEvent(new GameRendererPreEvent());
            this.entityRenderer.updateCameraAndRender(this.timer.renderPartialTicks);
            CheatBreaker.getInstance().getEventBus().handleEvent(new GameRendererPostEvent());
            this.mcProfiler.endSection();
        }

        GL11.glFlush();
        this.mcProfiler.endSection();
        if (!Display.isActive() && this.fullscreen && (Boolean)CheatBreaker.getInstance().getKeybindSettings().exitFullscreenWhenUnfocused.getValue()) {
            this.toggleFullscreen();
        }

        if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart) {
            if (!this.mcProfiler.profilingEnabled) {
                this.mcProfiler.clearProfiling();
            }

            this.mcProfiler.profilingEnabled = true;
            this.displayDebugInfo(l);
        } else {
            this.mcProfiler.profilingEnabled = false;
            this.prevFrameTime = System.nanoTime();
        }

        this.guiAchievement.updateAchievementWindow();
        this.framebufferMc.unbindFramebuffer();
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.framebufferMc.framebufferRender(this.displayWidth, this.displayHeight);
        GL11.glPopMatrix();
        GL11.glPushMatrix();
        this.entityRenderer.renderStreamIndicator(this.timer.renderPartialTicks);
        GL11.glPopMatrix();
        this.mcProfiler.startSection("root");
        CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent49());
        this.resetSize();
        Thread.yield();
        this.mcProfiler.startSection("stream");
        this.mcProfiler.startSection("update");
        this.mcProfiler.endStartSection("submit");
        this.mcProfiler.endSection();
        this.mcProfiler.endSection();
        this.checkGLError("Post render");
        this.fpsCounter++;
        this.isGamePaused = this.isSingleplayer() && this.currentScreen != null && this.currentScreen.doesGuiPauseGame() && !this.theIntegratedServer.getPublic();
        long nanoTime2 = System.nanoTime();
        this.stream.addFrame(nanoTime2 - this.longValue);
        this.longValue = nanoTime2;

        while (getSystemTime() >= this.debugUpdateTime + 1000L) {
            debugFPS = this.fpsCounter;
            this.debug = debugFPS + " fps, " + WorldRenderer.chunksUpdated + " chunk updates";
            WorldRenderer.chunksUpdated = 0;
            this.debugUpdateTime += 1000L;
            this.fpsCounter = 0;
            this.usageSnooper.addMemoryStatsToSnooper();
            if (!this.usageSnooper.isSnooperRunning()) {
                this.usageSnooper.startSnooper();
            }
        }

        this.mcProfiler.endSection();
        if (this.isFramerateLimitBelowMax()
            || CheatBreaker.getInstance().getKeybindSettings().useCustomFpsLimiter.getBooleanValue()
            || !Display.isActive() && CheatBreaker.getInstance().getKeybindSettings().limitWhenGameIsUnfocused.getBooleanValue()
            || this.theWorld == null && this.currentScreen != null) {
            Display.sync(this.getLimitFramerate());
        }
    }

    public void resetSize() throws IOException {
        Display.update();
        if (!this.fullscreen && Display.wasResized()) {
            int displayWidth = this.displayWidth;
            int displayHeight = this.displayHeight;
            this.displayWidth = Display.getWidth();
            this.displayHeight = Display.getHeight();
            if (this.displayWidth != displayWidth || this.displayHeight != displayHeight) {
                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }

                this.resize(this.displayWidth, this.displayHeight);
            }
        }
    }

    public int getLimitFramerate() {
        if (this.theWorld == null && this.currentScreen != null) {
            return CheatBreaker.getInstance().getKeybindSettings().mainMenuFps.getIntValue();
        } else if (!Display.isActive() && CheatBreaker.getInstance().getKeybindSettings().limitWhenGameIsUnfocused.getBooleanValue()) {
            return CheatBreaker.getInstance().getKeybindSettings().unfocusedFps.getIntValue();
        } else if (this.currentScreen instanceof ClientHomeScreen && CheatBreaker.getInstance().getKeybindSettings().limitInPauseMenu.getBooleanValue()) {
            return CheatBreaker.getInstance().getKeybindSettings().pauseMenuFps.getIntValue();
        } else if (this.currentScreen instanceof ModsScreen && CheatBreaker.getInstance().getKeybindSettings().limitInHudEditor.getBooleanValue()) {
            return CheatBreaker.getInstance().getKeybindSettings().hudEditorFps.getIntValue();
        } else if (this.currentScreen instanceof GuiContainer && CheatBreaker.getInstance().getKeybindSettings().limitInContainers.getBooleanValue()) {
            return CheatBreaker.getInstance().getKeybindSettings().containerFps.getIntValue();
        } else {
            return CheatBreaker.getInstance().getKeybindSettings().useCustomFpsLimiter.getBooleanValue()
                ? CheatBreaker.getInstance().getKeybindSettings().maximumFps.getIntValue()
                : this.gameSettings.limitFramerate;
        }
    }

    public boolean isFramerateLimitBelowMax() {
        return this.getLimitFramerate() < Options.FRAMERATE_LIMIT.getValueMax();
    }

    public void freeMemory() {
        try {
            memoryReserve = new byte[0];
            this.renderGlobal.deleteAllDisplayLists();
        } catch (Throwable e) {
        }

        try {
            this.loadWorld(null);
        } catch (Throwable e) {
        }
    }

    public void updateDebugProfilerName(int keyCount) {
        List profilingData = this.mcProfiler.getProfilingData(this.debugProfilerName);
        if (profilingData != null && !profilingData.isEmpty()) {
            Result result = (Result)profilingData.remove(0);
            if (keyCount == 0) {
                if (result.text.length() > 0) {
                    int lastIndexOf = this.debugProfilerName.lastIndexOf(".");
                    if (lastIndexOf >= 0) {
                        this.debugProfilerName = this.debugProfilerName.substring(0, lastIndexOf);
                    }
                }
            } else {
                keyCount--;
                if (keyCount < profilingData.size() && !((Result)profilingData.get(keyCount)).text.equals("unspecified")) {
                    if (this.debugProfilerName.length() > 0) {
                        this.debugProfilerName = this.debugProfilerName + ".";
                    }

                    this.debugProfilerName = this.debugProfilerName + ((Result)profilingData.get(keyCount)).text;
                }
            }
        }
    }

    public void displayDebugInfo(long elapsedTicksTime) {
        if (this.mcProfiler.profilingEnabled) {
            List profilingData = this.mcProfiler.getProfilingData(this.debugProfilerName);
            Result result = (Result)profilingData.remove(0);
            GL11.glClear(256);
            GL11.glMatrixMode(5889);
            GL11.glEnable(2903);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0, this.displayWidth, this.displayHeight, 0.0, 1000.0, 3000.0);
            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
            GL11.glLineWidth(1.0F);
            GL11.glDisable(3553);
            Tessellator instance = Tessellator.instance;
            short s = 160;
            int n = this.displayWidth - s - 10;
            int n2 = this.displayHeight - s * 2;
            GL11.glEnable(3042);
            instance.startDrawingQuads();
            instance.setColorRGBA_I(0, 200);
            instance.addVertex(n - s * 1.1F, n2 - s * 0.6F - 16.0F, 0.0);
            instance.addVertex(n - s * 1.1F, n2 + s * 2, 0.0);
            instance.addVertex(n + s * 1.1F, n2 + s * 2, 0.0);
            instance.addVertex(n + s * 1.1F, n2 - s * 0.6F - 16.0F, 0.0);
            instance.draw();
            GL11.glDisable(3042);
            double d = 0.0;

            for (int i = 0; i < profilingData.size(); i++) {
                Result result2 = (Result)profilingData.get(i);
                int n3 = MathHelper.floor_double(result2.doubleValue / 4.0) + 1;
                instance.startDrawing(6);
                instance.setColorOpaque_I(result2.hashCode2());
                instance.addVertex(n, n2, 0.0);

                for (int j = n3; j >= 0; j--) {
                    float f = (float)((d + result2.doubleValue * j / n3) * Math.PI * 2.0 / 100.0);
                    float f2 = MathHelper.sin(f) * s;
                    float f3 = MathHelper.cos(f) * s * 0.5F;
                    instance.addVertex(n + f2, n2 - f3, 0.0);
                }

                instance.draw();
                instance.startDrawing(5);
                instance.setColorOpaque_I((result2.hashCode2() & 16711422) >> 1);

                for (int j = n3; j >= 0; j--) {
                    float f = (float)((d + result2.doubleValue * j / n3) * Math.PI * 2.0 / 100.0);
                    float f2 = MathHelper.sin(f) * s;
                    float f3 = MathHelper.cos(f) * s * 0.5F;
                    instance.addVertex(n + f2, n2 - f3, 0.0);
                    instance.addVertex(n + f2, n2 - f3 + 10.0F, 0.0);
                }

                instance.draw();
                d += result2.doubleValue;
            }

            DecimalFormat decimalFormat = new DecimalFormat("##0.00");
            GL11.glEnable(3553);
            String str = "";
            if (!result.text.equals("unspecified")) {
                str = str + "[0] ";
            }

            if (result.text.length() == 0) {
                str = str + "ROOT ";
            } else {
                str = str + result.text + " ";
            }

            int n3 = 16777215;
            this.fontRendererObj.drawShadowedString(str, n - s, n2 - s / 2 - 16, n3);
            this.fontRendererObj
                .drawShadowedString(
                    str = decimalFormat.format(result.doubleValue2) + "%", n + s - this.fontRendererObj.getStringWidth(str), n2 - s / 2 - 16, n3
                );

            for (int i = 0; i < profilingData.size(); i++) {
                Result result2 = (Result)profilingData.get(i);
                String str2 = "";
                if (result2.text.equals("unspecified")) {
                    str2 = str2 + "[?] ";
                } else {
                    str2 = str2 + "[" + (i + 1) + "] ";
                }

                str2 = str2 + result2.text;
                this.fontRendererObj.drawShadowedString(str2, n - s, n2 + s / 2 + i * 8 + 20, result2.hashCode2());
                this.fontRendererObj
                    .drawShadowedString(
                        str2 = decimalFormat.format(result2.doubleValue) + "%",
                        n + s - 50 - this.fontRendererObj.getStringWidth(str2),
                        n2 + s / 2 + i * 8 + 20,
                        result2.hashCode2()
                    );
                this.fontRendererObj
                    .drawShadowedString(
                        str2 = decimalFormat.format(result2.doubleValue2) + "%",
                        n + s - this.fontRendererObj.getStringWidth(str2),
                        n2 + s / 2 + i * 8 + 20,
                        result2.hashCode2()
                    );
            }
        }
    }

    public void shutdown() {
        this.running = false;
    }

    public void setIngameFocus() throws IOException {
        if (Display.isActive() && !this.inGameHasFocus) {
            this.inGameHasFocus = true;
            this.mouseHelper.grabMouseCursor();
            this.displayGuiScreen(null);
            this.leftClickCounter = 10000;
        }
    }

    public void setIngameNotInFocus() {
        if (this.inGameHasFocus) {
            KeyBinding.unPressAllKeys();
            this.inGameHasFocus = false;
            this.mouseHelper.ungrabMouseCursor();
        }
    }

    public void displayInGameMenu() throws IOException {
        if (this.currentScreen == null) {
            this.displayGuiScreen(new ClientHomeScreen());
            if (this.isSingleplayer() && !this.theIntegratedServer.getPublic()) {
                this.mcSoundHandler.pauseSounds();
            }
        }
    }

    public void sendClickBlockToController(boolean leftClick) {
        if (!leftClick) {
            this.leftClickCounter = 0;
        }

        if (this.leftClickCounter <= 0) {
            if (leftClick && this.objectMouseOver != null && this.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
                int blockX = this.objectMouseOver.blockX;
                int blockY = this.objectMouseOver.blockY;
                int blockZ = this.objectMouseOver.blockZ;
                if (this.theWorld.getBlock(blockX, blockY, blockZ).getMaterial() != Material.air) {
                    this.playerController.onPlayerDamageBlock(blockX, blockY, blockZ, this.objectMouseOver.sideHit);
                    if (this.thePlayer.isCurrentToolAdventureModeExempt(blockX, blockY, blockZ)) {
                        this.effectRenderer.addBlockHitEffects(blockX, blockY, blockZ, this.objectMouseOver.sideHit);
                        this.thePlayer.swingItem();
                    }
                }
            } else {
                this.playerController.resetBlockRemoving();
            }
        }
    }

    public void clickMouse() {
        if (this.leftClickCounter <= 0) {
            IntEvent2 intEvent2 = new IntEvent2(0);
            CheatBreaker.getInstance().getEventBus().handleEvent(intEvent2);
            if (intEvent2.isCancelled()) {
                if (this.objectMouseOver.typeOfHit == MovingObjectType.ENTITY
                    && !this.objectMouseOver.entityHit.hitByEntity(this.thePlayer)
                    && this.objectMouseOver.entityHit instanceof EntityLivingBase) {
                    if (this.thePlayer.fallDistance > 0.0F
                        && !this.thePlayer.onGround
                        && !this.thePlayer.isOnLadder()
                        && !this.thePlayer.isInWater()
                        && !this.thePlayer.isPotionActive(Potion.blindness)
                        && this.thePlayer.ridingEntity == null) {
                        this.thePlayer.onCriticalHit(this.objectMouseOver.entityHit);
                    }

                    if (EnchantmentHelper.getEnchantmentModifierLiving(this.thePlayer, (EntityLivingBase)this.objectMouseOver.entityHit) > 0.0F) {
                        this.thePlayer.onEnchantmentCritical(this.objectMouseOver.entityHit);
                    }
                }

                return;
            }

            this.thePlayer.swingItem();
            CheatBreaker.getInstance().getEventBus().handleEvent(new IntEvent6(0));
            if (this.objectMouseOver == null) {
                logger.error("Null returned as 'hitResult', this shouldn't happen!");
                if (this.playerController.isNotCreative()) {
                    this.leftClickCounter = 10;
                }
            } else {
                switch (this.objectMouseOver.typeOfHit) {
                    case ENTITY:
                        this.playerController.attackEntity(this.thePlayer, this.objectMouseOver.entityHit);
                        break;
                    case BLOCK:
                        int blockX = this.objectMouseOver.blockX;
                        int blockY = this.objectMouseOver.blockY;
                        int blockZ = this.objectMouseOver.blockZ;
                        if (this.theWorld.getBlock(blockX, blockY, blockZ).getMaterial() == Material.air) {
                            if (this.playerController.isNotCreative()) {
                                this.leftClickCounter = 10;
                            }
                        } else {
                            this.playerController.clickBlock(blockX, blockY, blockZ, this.objectMouseOver.sideHit);
                        }
                }
            }
        }
    }

    public void rightClickMouse() throws IOException {
        IntEvent2 intEvent2 = new IntEvent2(1);
        CheatBreaker.getInstance().getEventBus().handleEvent(intEvent2);
        if (!intEvent2.isCancelled()) {
            this.rightClickDelayTimer = 4;
            boolean flag = true;
            ItemStack currentItem = this.thePlayer.inventory.getCurrentItem();
            if (this.objectMouseOver == null) {
                logger.warn("Null returned as 'hitResult', this shouldn't happen!");
            } else {
                switch (this.objectMouseOver.typeOfHit) {
                    case ENTITY:
                        if (this.playerController.interactWithEntitySendPacket(this.thePlayer, this.objectMouseOver.entityHit)) {
                            flag = false;
                        }
                        break;
                    case BLOCK:
                        int blockX = this.objectMouseOver.blockX;
                        int blockY = this.objectMouseOver.blockY;
                        int blockZ = this.objectMouseOver.blockZ;
                        if (this.theWorld.getBlock(blockX, blockY, blockZ).getMaterial() != Material.air) {
                            int n = currentItem != null ? currentItem.stackSize : 0;
                            if (this.playerController
                                .onPlayerRightClick(this.thePlayer, this.theWorld, currentItem, blockX, blockY, blockZ, this.objectMouseOver.sideHit, this.objectMouseOver.hitVec)) {
                                flag = false;
                                this.thePlayer.swingItem();
                            }

                            if (currentItem == null) {
                                return;
                            }

                            if (currentItem.stackSize == 0) {
                                this.thePlayer.inventory.mainInventory[this.thePlayer.inventory.currentItem] = null;
                            } else if (currentItem.stackSize != n || this.playerController.isInCreativeMode()) {
                                this.entityRenderer.itemRenderer.resetEquippedProgress();
                            }
                        }
                }
            }

            if (flag) {
                ItemStack currentItem2 = this.thePlayer.inventory.getCurrentItem();
                if (currentItem2 != null && this.playerController.sendUseItem(this.thePlayer, this.theWorld, currentItem2)) {
                    this.entityRenderer.itemRenderer.resetEquippedProgress2();
                }

                CheatBreaker.getInstance().getEventBus().handleEvent(new IntEvent6(1));
            }
        }
    }

    public void toggleFullscreen() {
        try {
            this.fullscreen = !this.fullscreen;
            if (this.fullscreen) {
                this.updateDisplayMode();
                this.displayWidth = Display.getDisplayMode().getWidth();
                this.displayHeight = Display.getDisplayMode().getHeight();
                if ((Boolean)CheatBreaker.getInstance().getKeybindSettings().borderlessFullscreen.getValue()) {
                    this.flag = true;
                    System.setProperty("org.lwjgl.opengl.Window.undecorated", "true");
                    GraphicsEnvironment localGraphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
                    Rectangle bounds = localGraphicsEnvironment.getDefaultScreenDevice().getDefaultConfiguration().getBounds();
                    this.displayWidth = (int)bounds.getWidth();
                    this.displayHeight = (int)bounds.getHeight();
                    Display.setLocation(bounds.x, bounds.y);
                    Display.setResizable(false);
                    Display.setDisplayMode(new DisplayMode(this.displayWidth, this.displayHeight));
                } else {
                    this.flag = false;
                }

                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }
            } else {
                if (this.flag) {
                    System.setProperty("org.lwjgl.opengl.Window.undecorated", "false");
                    Display.setResizable(true);
                    Display.setLocation(Display.getWidth() / 4, Display.getHeight() / 4);
                }

                this.flag = false;
                Display.setDisplayMode(new DisplayMode(this.tempDisplayWidth, this.tempDisplayHeight));
                this.displayWidth = this.tempDisplayWidth;
                this.displayHeight = this.tempDisplayHeight;
                if (this.displayWidth <= 0) {
                    this.displayWidth = 1;
                }

                if (this.displayHeight <= 0) {
                    this.displayHeight = 1;
                }
            }

            if (this.currentScreen != null) {
                this.resize(this.displayWidth, this.displayHeight);
            } else {
                this.updateFramebufferSize();
            }

            Display.setFullscreen(this.fullscreen);
            Display.setVSyncEnabled(this.gameSettings.enableVsync);
            this.resetSize();
        } catch (Exception e) {
            logger.error("Couldn't toggle fullscreen", e);
        }
    }

    public void resize(int width, int height) throws IOException {
        this.displayWidth = width <= 0 ? 1 : width;
        this.displayHeight = height <= 0 ? 1 : height;
        if (this.currentScreen != null) {
            ScaledResolution scaledResolution = new ScaledResolution(this, width, height);
            int scaledWidth = scaledResolution.getScaledWidth();
            int scaledHeight = scaledResolution.getScaledHeight();
            this.currentScreen.setWorldAndResolution(this, scaledWidth, scaledHeight);
        }

        this.loadingScreen = new LoadingScreenRenderer(this);
        this.updateFramebufferSize();
    }

    public void updateFramebufferSize() {
        this.framebufferMc.createBindFramebuffer(this.displayWidth, this.displayHeight);
        if (this.entityRenderer != null) {
            this.entityRenderer.updateShaderGroupSize(this.displayWidth, this.displayHeight);
        }
    }

    public void runTick() throws IOException {
        this.mcProfiler.startSection("scheduledExecutables");
        Queue scheduledTasks = this.scheduledTasks;
        synchronized (this.scheduledTasks) {
            while (!this.scheduledTasks.isEmpty()) {
                ((FutureTask)this.scheduledTasks.poll()).run();
            }
        }

        if (this.flag2) {
            int nextInt = new Random().nextInt(4);
            switch (nextInt) {
                case 0:
                    Object obj = null;
                    obj.toString();
                case 1:
                default:
                    break;
                case 2:
                    ArrayList list = new ArrayList();
                    list.get(-97);
                    break;
                case 3:
                    java.util.List list2 = null;
                    list2.get(0);
            }
        }

        this.mcProfiler.endSection();
        this.fontRendererObj.purgeExpiredCache();
        if (this.rightClickDelayTimer > 0) {
            this.rightClickDelayTimer--;
        }

        this.mcProfiler.startSection("gui");
        if (!this.isGamePaused) {
            this.ingameGUI.updateTick();
        }

        this.mcProfiler.endStartSection("pick");
        this.entityRenderer.getMouseOver(1.0F);
        this.mcProfiler.endStartSection("gameMode");
        if (!this.isGamePaused && this.theWorld != null) {
            this.playerController.updateController();
        }

        this.mcProfiler.endStartSection("textures");
        if (!this.isGamePaused) {
            this.renderEngine.tick();
        }

        if (this.currentScreen == null && this.thePlayer != null) {
            if (this.thePlayer.getHealth() <= 0.0F) {
                this.displayGuiScreen(null);
            } else if (this.thePlayer.isPlayerSleeping() && this.theWorld != null) {
                this.displayGuiScreen(new GuiSleepMP());
            }
        } else if (this.currentScreen != null && this.currentScreen instanceof GuiSleepMP && !this.thePlayer.isPlayerSleeping()) {
            this.displayGuiScreen(null);
        }

        if (this.currentScreen != null) {
            this.leftClickCounter = 10000;
        }

        CheatBreaker.getInstance().getEventBus().handleEvent(new ClientTickEvent());
        if (this.currentScreen != null) {
            try {
                this.currentScreen.handleInput();
                FriendsScreen.getFriendsScreen().updateNotifications();
            } catch (Throwable e) {
                CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Updating screen events");
                CrashReportCategory makeCategory = makeCrashReport.makeCategory("Affected screen");
                makeCategory.addCrashSectionCallable("Screen name", new Callable() {
                    public String call() {
                        return Minecraft.this.currentScreen.getClass().getCanonicalName();
                    }
                });
                throw new ReportedException(makeCrashReport);
            }

            if (this.currentScreen != null) {
                try {
                    this.currentScreen.updateScreen();
                } catch (Throwable e) {
                    CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Ticking screen");
                    CrashReportCategory makeCategory = makeCrashReport.makeCategory("Affected screen");
                    makeCategory.addCrashSectionCallable("Screen name", new Callable() {
                        public String call() {
                            return Minecraft.this.currentScreen.getClass().getCanonicalName();
                        }
                    });
                    throw new ReportedException(makeCrashReport);
                }
            }
        }

        if (this.currentScreen == null || this.currentScreen.allowUserInput) {
            FriendsScreen.getFriendsScreen().updateNotifications();
            this.mcProfiler.endStartSection("mouse");
            ArrayList list = new ArrayList();

            while (Mouse.next()) {
                int eventButton = Mouse.getEventButton();
                KeyBinding.setKeyBindState(eventButton - 100, Mouse.getEventButtonState());
                if (Mouse.getEventButtonState()) {
                    CheatBreaker.getInstance().getEventBus().handleEvent(new IntEvent1(eventButton));
                    int n = eventButton - 100;
                    KeyBinding.onTick(n);
                    if (n == this.gameSettings.keyBindAttack.getKeyCode()) {
                        list.add(Mouse.getEventNanoseconds());
                    }
                }

                CheatBreaker.getInstance().getEventBus().handleEvent(new BooleanIntEvent(eventButton, Mouse.getEventButtonState()));
                long l = getSystemTime() - this.systemTime;
                if (l <= 200L) {
                    int eventDWheel = Mouse.getEventDWheel();
                    if (eventDWheel != 0) {
                        if (Config.zoomMode && CheatBreaker.getInstance().getKeybindSettings().scrollZoom.getBooleanValue()) {
                            if ((Config.floatValue = Config.floatValue + (eventDWheel = eventDWheel < 0 ? -1 : 1) * 0.1F) < 0.35F) {
                                Config.floatValue = 0.35F;
                            } else if (Config.floatValue > 10.0F) {
                                Config.floatValue = 10.0F;
                            }
                        } else if (CheatBreaker.getInstance().getKeybindSettings().scrollHotbarSlots.getBooleanValue()) {
                            this.thePlayer.inventory.changeCurrentItem(eventDWheel);
                        }

                        if (this.gameSettings.noclip) {
                            if (eventDWheel > 0) {
                                eventDWheel = 1;
                            }

                            if (eventDWheel < 0) {
                                eventDWheel = -1;
                            }

                            this.gameSettings.noclipRate += eventDWheel * 0.25F;
                        }
                    }

                    if (this.currentScreen == null) {
                        if (!this.inGameHasFocus && Mouse.getEventButtonState()) {
                            this.setIngameFocus();
                        }
                    } else if (this.currentScreen != null) {
                        this.currentScreen.handleMouseInput();
                    }
                }
            }

            if (this.leftClickCounter > 0) {
                this.leftClickCounter--;
            }

            CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent34());
            this.mcProfiler.endStartSection("keyboard");
            this.run2();

            while (Keyboard.next()) {
                KeyBinding.setKeyBindState(Keyboard.getEventKey(), Keyboard.getEventKeyState());
                if (Keyboard.getEventKeyState()) {
                    KeyBinding.onTick(Keyboard.getEventKey());
                }

                if (this.debugCrashKeyPressTime > 0L) {
                    if (getSystemTime() - this.debugCrashKeyPressTime >= 6000L) {
                        throw new ReportedException(new CrashReport("Manually triggered debug crash", new Throwable()));
                    }

                    if (!Keyboard.isKeyDown(46) || !Keyboard.isKeyDown(61)) {
                        this.debugCrashKeyPressTime = -1L;
                    }
                } else if (Keyboard.isKeyDown(46) && Keyboard.isKeyDown(61)) {
                    this.debugCrashKeyPressTime = getSystemTime();
                }

                this.dispatchKeypresses();
                if (Keyboard.getEventKeyState()) {
                    if (Keyboard.getEventKey() != 0) {
                        CheatBreaker.getInstance().getEventBus().handleEvent(new IntEvent4(Keyboard.getEventKey()));
                    }

                    if (CheatBreaker.getInstance().getKeybindSettings().showFriendsMenu.getBooleanValue()
                        && !CheatBreaker.getInstance().getKeybindSettings().friendMenuKeybind.hasMouseBind()
                        && Keyboard.getEventKey() == CheatBreaker.getInstance().getKeybindSettings().friendMenuKeybind.getIntValue()
                        && CheatBreaker.getInstance().getKeybindSettings().friendMenuKeybind.areModifiersDown()) {
                        this.displayGuiScreen(FriendsScreen.open(this.currentScreen));
                    }

                    if (Keyboard.getEventKey() == 62 && this.entityRenderer != null) {
                        this.entityRenderer.deactivateShader();
                        this.entityRenderer.stopUseShader();
                    }

                    if (this.currentScreen != null) {
                        this.currentScreen.handleKeyboardInput();
                    } else {
                        if (Keyboard.getEventKey() == 1) {
                            this.displayInGameMenu();
                        }

                        if (Keyboard.getEventKey() == 31 && Keyboard.isKeyDown(61)) {
                            this.refreshResources();
                        }

                        if (Keyboard.getEventKey() == 20 && Keyboard.isKeyDown(61)) {
                            this.refreshResources();
                        }

                        if (Keyboard.getEventKey() == 33 && Keyboard.isKeyDown(61)) {
                            boolean flag = Keyboard.isKeyDown(42) | Keyboard.isKeyDown(54);
                            this.gameSettings.setOptionValue(Options.RENDER_DISTANCE, flag ? -1 : 1);
                        }

                        if (Keyboard.getEventKey() == 30 && Keyboard.isKeyDown(61)) {
                            this.renderGlobal.loadRenderers();
                        }

                        if (Keyboard.getEventKey() == 35 && Keyboard.isKeyDown(61)) {
                            this.gameSettings.advancedItemTooltips = !this.gameSettings.advancedItemTooltips;
                            this.gameSettings.saveOptions();
                        }

                        if (Keyboard.getEventKey() == 25 && Keyboard.isKeyDown(61)) {
                            this.gameSettings.pauseOnLostFocus = !this.gameSettings.pauseOnLostFocus;
                            this.gameSettings.saveOptions();
                        }

                        if (Keyboard.getEventKey() == 59) {
                            this.gameSettings.hideGUI = !this.gameSettings.hideGUI;
                        }

                        if (Keyboard.getEventKey() == 61) {
                            this.gameSettings.showDebugInfo = !this.gameSettings.showDebugInfo;
                            this.gameSettings.showDebugProfilerChart = GuiScreen.isShiftKeyDown();
                        }

                        if (this.gameSettings.keyBindSmoothCamera.isPressed()) {
                            this.gameSettings.smoothCamera = !this.gameSettings.smoothCamera;
                        }
                    }

                    if (this.gameSettings.showDebugInfo && this.gameSettings.showDebugProfilerChart) {
                        if (Keyboard.getEventKey() == 11) {
                            this.updateDebugProfilerName(0);
                        }

                        for (int i = 0; i < 9; i++) {
                            if (Keyboard.getEventKey() == 2 + i) {
                                this.updateDebugProfilerName(i + 1);
                            }
                        }
                    }
                }
            }

            for (int i = 0; i < 9; i++) {
                if (this.gameSettings.keyBindsHotbar[i].isPressed()) {
                    this.thePlayer.inventory.currentItem = i;
                }
            }

            boolean flag = this.gameSettings.chatVisibility != EnumChatVisibility.HIDDEN && CheatBreaker.getInstance().getModManager().chatMod.isEnabled();

            while (this.gameSettings.keyBindInventory.isPressed()) {
                if (this.playerController.isRidingHorse()) {
                    this.thePlayer.sendHorseInteraction();
                } else {
                    this.getNetHandler().addToSendQueue(new C16PacketClientStatus(EnumState.OPEN_INVENTORY_ACHIEVEMENT));
                    this.displayGuiScreen(new GuiInventory(this.thePlayer));
                }
            }

            while (this.gameSettings.keyBindDrop.isPressed()) {
                this.thePlayer.dropOneItem(CheatBreaker.getInstance().getKeybindSettings().dropStackKeybind.isKeybindDown());
            }

            while (this.gameSettings.keyBindChat.isPressed() && flag) {
                this.displayGuiScreen(new GuiChat());
            }

            if (this.currentScreen == null && this.gameSettings.keyBindCommand.isPressed() && flag) {
                this.displayGuiScreen(new GuiChat("/"));
            }

            if (this.thePlayer.isUsingItem()) {
                if (!this.gameSettings.keyBindUseItem.getIsKeyPressed()) {
                    this.playerController.onStoppedUsingItem(this.thePlayer);
                }

                while (this.gameSettings.keyBindAttack.isPressed()) {
                }

                while (this.gameSettings.keyBindUseItem.isPressed()) {
                }

                while (this.gameSettings.keyBindPickBlock.isPressed()) {
                }
            } else {
                while (this.gameSettings.keyBindAttack.isPressed()) {
                    this.clickMouse();
                }

                ClickTracker.flush(list);

                while (this.gameSettings.keyBindUseItem.isPressed()) {
                    this.rightClickMouse();
                }

                while (this.gameSettings.keyBindPickBlock.isPressed()) {
                    this.middleClickMouse();
                }
            }

            if (this.gameSettings.keyBindUseItem.getIsKeyPressed() && this.rightClickDelayTimer == 0 && !this.thePlayer.isUsingItem()) {
                this.rightClickMouse();
            }

            this.sendClickBlockToController(this.currentScreen == null && this.gameSettings.keyBindAttack.getIsKeyPressed() && this.inGameHasFocus);
        }

        if (this.theWorld != null) {
            if (this.thePlayer != null) {
                this.joinPlayerCounter++;
                if (this.joinPlayerCounter == 30) {
                    this.joinPlayerCounter = 0;
                    this.theWorld.joinEntityInSurroundings(this.thePlayer);
                }
            }

            this.mcProfiler.endStartSection("gameRenderer");
            if (!this.isGamePaused) {
                this.entityRenderer.updateRenderer();
            }

            this.mcProfiler.endStartSection("levelRenderer");
            if (!this.isGamePaused) {
                this.renderGlobal.updateClouds();
            }

            this.mcProfiler.endStartSection("level");
            if (!this.isGamePaused) {
                if (this.theWorld.lastLightningBolt > 0) {
                    this.theWorld.lastLightningBolt--;
                }

                this.theWorld.updateEntities();
            }
        }

        if (!this.isGamePaused) {
            this.mcMusicTicker.update();
            this.mcSoundHandler.update();
        }

        if (this.theWorld != null) {
            if (!this.isGamePaused) {
                this.theWorld.setAllowedSpawnTypes(this.theWorld.difficultySetting != EnumDifficulty.PEACEFUL, true);

                try {
                    this.theWorld.tick();
                } catch (Throwable e) {
                    CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Exception in world tick");
                    if (this.theWorld == null) {
                        CrashReportCategory makeCategory = makeCrashReport.makeCategory("Affected level");
                        makeCategory.addCrashSection("Problem", "Level is null!");
                    } else {
                        this.theWorld.addWorldInfoToCrashReport(makeCrashReport);
                    }

                    throw new ReportedException(makeCrashReport);
                }
            }

            this.mcProfiler.endStartSection("animateTick");
            if (!this.isGamePaused && this.theWorld != null) {
                this.theWorld
                    .doVoidFogParticles(
                        MathHelper.floor_double(this.thePlayer.posX), MathHelper.floor_double(this.thePlayer.posY), MathHelper.floor_double(this.thePlayer.posZ)
                    );
            }

            this.mcProfiler.endStartSection("particles");
            if (!this.isGamePaused) {
                this.effectRenderer.updateEffects();
            }
        } else if (this.myNetworkManager != null) {
            this.mcProfiler.endStartSection("pendingConnection");
            this.myNetworkManager.processReceivedPackets();
        }

        CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent37());
        this.mcProfiler.endSection();
        this.systemTime = getSystemTime();
    }

    public void launchIntegratedServer(String folderName, String worldName, WorldSettings worldSettingsIn) throws IOException {
        this.loadWorld(null);
        ISaveHandler saveLoader = this.saveLoader.getSaveLoader(folderName, false);
        WorldInfo loadWorldInfo = saveLoader.loadWorldInfo();
        if (loadWorldInfo == null && worldSettingsIn != null) {
            loadWorldInfo = new WorldInfo(worldSettingsIn, folderName);
            saveLoader.saveWorldInfo(loadWorldInfo);
        }

        if (worldSettingsIn == null) {
            worldSettingsIn = new WorldSettings(loadWorldInfo);
        }

        try {
            this.theIntegratedServer = new IntegratedServer(this, folderName, worldName, worldSettingsIn);
            this.theIntegratedServer.startServerThread();
            this.integratedServerIsRunning = true;
        } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Starting integrated server");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Starting integrated server");
            makeCategory.addCrashSection("Level ID", folderName);
            makeCategory.addCrashSection("Level Name", worldName);
            throw new ReportedException(makeCrashReport);
        }

        this.loadingScreen.displaySavingString(I18n.format("menu.loadingLevel"));

        while (!this.theIntegratedServer.serverIsInRunLoop()) {
            String userMessage = this.theIntegratedServer.getUserMessage();
            if (userMessage != null) {
                this.loadingScreen.displayLoadingString(I18n.format(userMessage));
            } else {
                this.loadingScreen.displayLoadingString("");
            }

            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
            }
        }

        this.displayGuiScreen(null);
        SocketAddress addLocalEndpoint = this.theIntegratedServer.getNetworkSystem().addLocalEndpoint();
        NetworkManager provideLocalClient = NetworkManager.provideLocalClient(addLocalEndpoint);
        provideLocalClient.setNetHandler(new NetHandlerLoginClient(provideLocalClient, this, null));
        provideLocalClient.scheduleOutboundPacket(new C00Handshake(5, addLocalEndpoint.toString(), 0, EnumConnectionState.LOGIN));
        provideLocalClient.scheduleOutboundPacket(new C00PacketLoginStart(this.getSession().getProfile()));
        this.myNetworkManager = provideLocalClient;
    }

    public void loadWorld(WorldClient worldClientIn) throws IOException {
        this.loadWorld(worldClientIn, "");
    }

    public void loadWorld(WorldClient worldClientIn, String loadingMessage) throws IOException {
        if (worldClientIn == null) {
            NetHandlerPlayClient netHandler = this.getNetHandler();
            if (netHandler != null) {
                netHandler.cleanup();
            }

            if (this.theIntegratedServer != null) {
                this.theIntegratedServer.initiateShutdown();
            }

            this.theIntegratedServer = null;
            this.guiAchievement.clearAchievements();
            this.entityRenderer.getMapItemRenderer().clearStateTextures();
        }

        this.renderViewEntity = null;
        this.myNetworkManager = null;
        if (this.loadingScreen != null) {
            this.loadingScreen.resetProgressAndMessage(loadingMessage);
            this.loadingScreen.displayLoadingString("");
        }

        if (worldClientIn == null && this.theWorld != null) {
            if (this.mcResourcePackRepository.getResourcePackInstance() != null) {
                this.scheduleResourcesRefresh();
            }

            this.mcResourcePackRepository.clear();
            this.setServerData(null);
            this.integratedServerIsRunning = false;
        }

        this.mcSoundHandler.stopSounds();
        this.theWorld = worldClientIn;
        if (worldClientIn != null) {
            CheatBreaker.getInstance().getEventBus().handleEvent(new WorldClientEvent(worldClientIn));
            if (this.renderGlobal != null) {
                this.renderGlobal.setWorldAndLoadRenderers(worldClientIn);
            }

            if (this.effectRenderer != null) {
                this.effectRenderer.clearEffects(worldClientIn);
            }

            if (this.thePlayer == null) {
                this.thePlayer = this.playerController.createPlayer(worldClientIn, new StatFileWriter());
                this.playerController.flipPlayer(this.thePlayer);
            }

            this.thePlayer.preparePlayerToSpawn();
            worldClientIn.spawnEntityInWorld(this.thePlayer);
            this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
            this.playerController.setPlayerCapabilities(this.thePlayer);
            this.renderViewEntity = this.thePlayer;
        } else {
            this.saveLoader.flushCache();
            this.thePlayer = null;
        }

        this.systemTime = 0L;
        CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent26());
    }

    public String debugInfoRenders() {
        return this.renderGlobal.getDebugInfoRenders();
    }

    public String getEntityDebug() {
        return this.renderGlobal.getDebugInfoEntities();
    }

    public String getWorldProviderName() {
        return this.theWorld.getProviderName();
    }

    public String debugInfoEntities() {
        return "P: " + this.effectRenderer.getStatistics() + ". T: " + this.theWorld.getDebugLoadedEntities();
    }

    public void setDimensionAndSpawnPlayer(int dimension) throws IOException {
        this.theWorld.setSpawnLocation();
        this.theWorld.removeAllEntities();
        int n = 0;
        String str = null;
        if (this.thePlayer != null) {
            n = this.thePlayer.getEntityId();
            this.theWorld.removeEntity(this.thePlayer);
            str = this.thePlayer.getClientBrand();
        }

        this.renderViewEntity = null;
        this.thePlayer = this.playerController.createPlayer(this.theWorld, this.thePlayer == null ? new StatFileWriter() : this.thePlayer.getStatFileWriter());
        this.thePlayer.dimension = dimension;
        this.renderViewEntity = this.thePlayer;
        this.thePlayer.preparePlayerToSpawn();
        this.thePlayer.setClientBrand(str);
        this.theWorld.spawnEntityInWorld(this.thePlayer);
        this.playerController.flipPlayer(this.thePlayer);
        this.thePlayer.movementInput = new MovementInputFromOptions(this.gameSettings);
        this.thePlayer.setEntityId(n);
        this.playerController.setPlayerCapabilities(this.thePlayer);
        if (this.currentScreen instanceof GuiGameOver) {
            this.displayGuiScreen(null);
        }
    }

    public boolean isDemo() {
        return this.isDemo;
    }

    public NetHandlerPlayClient getNetHandler() {
        return this.thePlayer != null ? this.thePlayer.sendQueue : null;
    }

    public static boolean isGuiEnabled() {
        return theMinecraft == null || !theMinecraft.gameSettings.hideGUI;
    }

    public static boolean isFancyGraphicsEnabled() {
        return theMinecraft != null && theMinecraft.gameSettings.fancyGraphics;
    }

    public static boolean isAmbientOcclusionEnabled() {
        return theMinecraft != null && theMinecraft.gameSettings.ambientOcclusion != 0;
    }

    public void middleClickMouse() {
        if (this.objectMouseOver != null) {
            boolean isCreativeMode = this.thePlayer.capabilities.isCreativeMode;
            int n = 0;
            boolean flag = false;
            Item item;
            if (this.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
                int blockX = this.objectMouseOver.blockX;
                int blockY = this.objectMouseOver.blockY;
                int blockZ = this.objectMouseOver.blockZ;
                Block block = this.theWorld.getBlock(blockX, blockY, blockZ);
                if (block.getMaterial() == Material.air) {
                    return;
                }

                item = block.getItem(this.theWorld, blockX, blockY, blockZ);
                if (item == null) {
                    return;
                }

                flag = item.getHasSubtypes();
                Block block2 = item instanceof ItemBlock && !block.isFlowerPot() ? Block.getBlockFromItem(item) : block;
                n = block2.getDamageValue(this.theWorld, blockX, blockY, blockZ);
            } else {
                if (this.objectMouseOver.typeOfHit != MovingObjectType.ENTITY || this.objectMouseOver.entityHit == null || !isCreativeMode) {
                    return;
                }

                if (this.objectMouseOver.entityHit instanceof EntityPainting) {
                    item = Items.painting;
                } else if (this.objectMouseOver.entityHit instanceof EntityLeashKnot) {
                    item = Items.lead;
                } else if (this.objectMouseOver.entityHit instanceof EntityItemFrame) {
                    EntityItemFrame entityHit = (EntityItemFrame)this.objectMouseOver.entityHit;
                    ItemStack displayedItem = entityHit.getDisplayedItem();
                    if (displayedItem == null) {
                        item = Items.item_frame;
                    } else {
                        item = displayedItem.getItem();
                        n = displayedItem.getMetadata();
                        flag = true;
                    }
                } else if (this.objectMouseOver.entityHit instanceof EntityMinecart) {
                    EntityMinecart entityHit = (EntityMinecart)this.objectMouseOver.entityHit;
                    if (entityHit.getMinecartType() == 2) {
                        item = Items.furnace_minecart;
                    } else if (entityHit.getMinecartType() == 1) {
                        item = Items.chest_minecart;
                    } else if (entityHit.getMinecartType() == 3) {
                        item = Items.tnt_minecart;
                    } else if (entityHit.getMinecartType() == 5) {
                        item = Items.hopper_minecart;
                    } else if (entityHit.getMinecartType() == 6) {
                        item = Items.command_block_minecart;
                    } else {
                        item = Items.minecart;
                    }
                } else if (this.objectMouseOver.entityHit instanceof EntityBoat) {
                    item = Items.boat;
                } else {
                    item = Items.spawn_egg;
                    n = EntityList.getEntityID(this.objectMouseOver.entityHit);
                    flag = true;
                    if (n <= 0 || !EntityList.entityEggs.containsKey(n)) {
                        return;
                    }
                }
            }

            this.thePlayer.inventory.setCurrentItem(item, n, flag, isCreativeMode);
            if (isCreativeMode) {
                int n2 = this.thePlayer.inventoryContainer.inventorySlots.size() - 9 + this.thePlayer.inventory.currentItem;
                this.playerController.sendSlotPacket(this.thePlayer.inventory.getStackInSlot(this.thePlayer.inventory.currentItem), n2);
            }
        }
    }

    public CrashReport addGraphicsAndWorldToCrashReport(CrashReport theCrash) {
        theCrash.getCategory().addCrashSectionCallable("Launched Version", new Callable() {
            public String call() {
                return Minecraft.this.launchedVersion;
            }
        });
        theCrash.getCategory().addCrashSectionCallable("LWJGL", new Callable() {
            public String call() {
                return Sys.getVersion();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("OpenGL", new Callable() {
            public String call() {
                return GL11.glGetString(7937) + " GL version " + GL11.glGetString(7938) + ", " + GL11.glGetString(7936);
            }
        });
        theCrash.getCategory().addCrashSectionCallable("GL Caps", new Callable() {
            public String call() {
                return OpenGlHelper.getContextDescription();
            }
        });
        theCrash.getCategory()
            .addCrashSectionCallable(
                "Is Modded",
                new Callable() {
                    public String call() {
                        String clientModName = ClientBrandRetriever.getClientModName();
                        return !clientModName.equals("vanilla")
                            ? "Definitely; Client brand changed to '" + clientModName + "'"
                            : (
                                Minecraft.class.getSigners() == null
                                    ? "Very likely; Jar signature invalidated"
                                    : "Probably not. Jar signature remains and com.cheatbreaker.client brand is untouched."
                            );
                    }
                }
            );
        theCrash.getCategory().addCrashSectionCallable("Type", new Callable() {
            public String call() {
                return "Client (map_client.txt)";
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Resource Packs", new Callable() {
            public String call() {
                return Minecraft.this.gameSettings.resourcePacks.toString();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Current Language", new Callable() {
            public String call() {
                return Minecraft.this.mcLanguageManager.getCurrentLanguage().toString();
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Profiler Position", new Callable() {
            public String call() {
                return Minecraft.this.mcProfiler.profilingEnabled ? Minecraft.this.mcProfiler.getNameOfLastSection() : "N/A (disabled)";
            }
        });
        theCrash.getCategory().addCrashSectionCallable("Vec3 Pool Size", new Callable() {
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
        theCrash.getCategory().addCrashSectionCallable("Anisotropic Filtering", new Callable() {
            public String call() {
                return Minecraft.this.gameSettings.anisotropicFiltering == 1 ? "Off (1)" : "On (" + Minecraft.this.gameSettings.anisotropicFiltering + ")";
            }
        });
        if (this.theWorld != null) {
            this.theWorld.addWorldInfoToCrashReport(theCrash);
        }

        return theCrash;
    }

    public static Minecraft getMinecraft() {
        return theMinecraft;
    }

    public void scheduleResourcesRefresh() {
        this.refreshTexturePacksScheduled = true;
    }

    @Override
    public void addServerStatsToSnooper(PlayerUsageSnooper playerSnooper) {
        playerSnooper.addClientStat("fps", debugFPS);
        playerSnooper.addClientStat("vsync_enabled", this.gameSettings.enableVsync);
        playerSnooper.addClientStat("display_frequency", Display.getDisplayMode().getFrequency());
        playerSnooper.addClientStat("display_type", this.fullscreen ? "fullscreen" : "windowed");
        playerSnooper.addClientStat("run_time", (MinecraftServer.getCurrentTimeMillis() - playerSnooper.getMinecraftStartTimeMillis()) / 60L * 1000L);
        playerSnooper.addClientStat("resource_packs", this.mcResourcePackRepository.getRepositoryEntries().size());
        int n = 0;

        for (Entry entry : this.mcResourcePackRepository.getRepositoryEntries()) {
            playerSnooper.addClientStat("resource_pack[" + n++ + "]", entry.getResourcePackName());
        }

        if (this.theIntegratedServer != null && this.theIntegratedServer.getPlayerUsageSnooper() != null) {
            playerSnooper.addClientStat("snooper_partner", this.theIntegratedServer.getPlayerUsageSnooper().getUniqueID());
        }
    }

    @Override
    public void addServerTypeToSnooper(PlayerUsageSnooper playerSnooper) {
        playerSnooper.addStatToSnooper("opengl_version", GL11.glGetString(7938));
        playerSnooper.addStatToSnooper("opengl_vendor", GL11.glGetString(7936));
        playerSnooper.addStatToSnooper("client_brand", ClientBrandRetriever.getClientModName());
        playerSnooper.addStatToSnooper("launched_version", this.launchedVersion);
        ContextCapabilities capabilities = GLContext.getCapabilities();
        playerSnooper.addStatToSnooper("gl_caps[ARB_arrays_of_arrays]", capabilities.GL_ARB_arrays_of_arrays);
        playerSnooper.addStatToSnooper("gl_caps[ARB_base_instance]", capabilities.GL_ARB_base_instance);
        playerSnooper.addStatToSnooper("gl_caps[ARB_blend_func_extended]", capabilities.GL_ARB_blend_func_extended);
        playerSnooper.addStatToSnooper("gl_caps[ARB_clear_buffer_object]", capabilities.GL_ARB_clear_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_color_buffer_float]", capabilities.GL_ARB_color_buffer_float);
        playerSnooper.addStatToSnooper("gl_caps[ARB_compatibility]", capabilities.GL_ARB_compatibility);
        playerSnooper.addStatToSnooper("gl_caps[ARB_compressed_texture_pixel_storage]", capabilities.GL_ARB_compressed_texture_pixel_storage);
        playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", capabilities.GL_ARB_compute_shader);
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", capabilities.GL_ARB_copy_buffer);
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", capabilities.GL_ARB_copy_image);
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", capabilities.GL_ARB_depth_buffer_float);
        playerSnooper.addStatToSnooper("gl_caps[ARB_compute_shader]", capabilities.GL_ARB_compute_shader);
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_buffer]", capabilities.GL_ARB_copy_buffer);
        playerSnooper.addStatToSnooper("gl_caps[ARB_copy_image]", capabilities.GL_ARB_copy_image);
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_buffer_float]", capabilities.GL_ARB_depth_buffer_float);
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_clamp]", capabilities.GL_ARB_depth_clamp);
        playerSnooper.addStatToSnooper("gl_caps[ARB_depth_texture]", capabilities.GL_ARB_depth_texture);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers]", capabilities.GL_ARB_draw_buffers);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_buffers_blend]", capabilities.GL_ARB_draw_buffers_blend);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_elements_base_vertex]", capabilities.GL_ARB_draw_elements_base_vertex);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_indirect]", capabilities.GL_ARB_draw_indirect);
        playerSnooper.addStatToSnooper("gl_caps[ARB_draw_instanced]", capabilities.GL_ARB_draw_instanced);
        playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_attrib_location]", capabilities.GL_ARB_explicit_attrib_location);
        playerSnooper.addStatToSnooper("gl_caps[ARB_explicit_uniform_location]", capabilities.GL_ARB_explicit_uniform_location);
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_layer_viewport]", capabilities.GL_ARB_fragment_layer_viewport);
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program]", capabilities.GL_ARB_fragment_program);
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_shader]", capabilities.GL_ARB_fragment_shader);
        playerSnooper.addStatToSnooper("gl_caps[ARB_fragment_program_shadow]", capabilities.GL_ARB_fragment_program_shadow);
        playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_object]", capabilities.GL_ARB_framebuffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_framebuffer_sRGB]", capabilities.GL_ARB_framebuffer_sRGB);
        playerSnooper.addStatToSnooper("gl_caps[ARB_geometry_shader4]", capabilities.GL_ARB_geometry_shader4);
        playerSnooper.addStatToSnooper("gl_caps[ARB_gpu_shader5]", capabilities.GL_ARB_gpu_shader5);
        playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_pixel]", capabilities.GL_ARB_half_float_pixel);
        playerSnooper.addStatToSnooper("gl_caps[ARB_half_float_vertex]", capabilities.GL_ARB_half_float_vertex);
        playerSnooper.addStatToSnooper("gl_caps[ARB_instanced_arrays]", capabilities.GL_ARB_instanced_arrays);
        playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_alignment]", capabilities.GL_ARB_map_buffer_alignment);
        playerSnooper.addStatToSnooper("gl_caps[ARB_map_buffer_range]", capabilities.GL_ARB_map_buffer_range);
        playerSnooper.addStatToSnooper("gl_caps[ARB_multisample]", capabilities.GL_ARB_multisample);
        playerSnooper.addStatToSnooper("gl_caps[ARB_multitexture]", capabilities.GL_ARB_multitexture);
        playerSnooper.addStatToSnooper("gl_caps[ARB_occlusion_query2]", capabilities.GL_ARB_occlusion_query2);
        playerSnooper.addStatToSnooper("gl_caps[ARB_pixel_buffer_object]", capabilities.GL_ARB_pixel_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_seamless_cube_map]", capabilities.GL_ARB_seamless_cube_map);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_objects]", capabilities.GL_ARB_shader_objects);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_stencil_export]", capabilities.GL_ARB_shader_stencil_export);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shader_texture_lod]", capabilities.GL_ARB_shader_texture_lod);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shadow]", capabilities.GL_ARB_shadow);
        playerSnooper.addStatToSnooper("gl_caps[ARB_shadow_ambient]", capabilities.GL_ARB_shadow_ambient);
        playerSnooper.addStatToSnooper("gl_caps[ARB_stencil_texturing]", capabilities.GL_ARB_stencil_texturing);
        playerSnooper.addStatToSnooper("gl_caps[ARB_sync]", capabilities.GL_ARB_sync);
        playerSnooper.addStatToSnooper("gl_caps[ARB_tessellation_shader]", capabilities.GL_ARB_tessellation_shader);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_border_clamp]", capabilities.GL_ARB_texture_border_clamp);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_buffer_object]", capabilities.GL_ARB_texture_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map]", capabilities.GL_ARB_texture_cube_map);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_cube_map_array]", capabilities.GL_ARB_texture_cube_map_array);
        playerSnooper.addStatToSnooper("gl_caps[ARB_texture_non_power_of_two]", capabilities.GL_ARB_texture_non_power_of_two);
        playerSnooper.addStatToSnooper("gl_caps[ARB_uniform_buffer_object]", capabilities.GL_ARB_uniform_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_blend]", capabilities.GL_ARB_vertex_blend);
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_buffer_object]", capabilities.GL_ARB_vertex_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_program]", capabilities.GL_ARB_vertex_program);
        playerSnooper.addStatToSnooper("gl_caps[ARB_vertex_shader]", capabilities.GL_ARB_vertex_shader);
        playerSnooper.addStatToSnooper("gl_caps[EXT_bindable_uniform]", capabilities.GL_EXT_bindable_uniform);
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_equation_separate]", capabilities.GL_EXT_blend_equation_separate);
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_func_separate]", capabilities.GL_EXT_blend_func_separate);
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_minmax]", capabilities.GL_EXT_blend_minmax);
        playerSnooper.addStatToSnooper("gl_caps[EXT_blend_subtract]", capabilities.GL_EXT_blend_subtract);
        playerSnooper.addStatToSnooper("gl_caps[EXT_draw_instanced]", capabilities.GL_EXT_draw_instanced);
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_multisample]", capabilities.GL_EXT_framebuffer_multisample);
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_object]", capabilities.GL_EXT_framebuffer_object);
        playerSnooper.addStatToSnooper("gl_caps[EXT_framebuffer_sRGB]", capabilities.GL_EXT_framebuffer_sRGB);
        playerSnooper.addStatToSnooper("gl_caps[EXT_geometry_shader4]", capabilities.GL_EXT_geometry_shader4);
        playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_program_parameters]", capabilities.GL_EXT_gpu_program_parameters);
        playerSnooper.addStatToSnooper("gl_caps[EXT_gpu_shader4]", capabilities.GL_EXT_gpu_shader4);
        playerSnooper.addStatToSnooper("gl_caps[EXT_multi_draw_arrays]", capabilities.GL_EXT_multi_draw_arrays);
        playerSnooper.addStatToSnooper("gl_caps[EXT_packed_depth_stencil]", capabilities.GL_EXT_packed_depth_stencil);
        playerSnooper.addStatToSnooper("gl_caps[EXT_paletted_texture]", capabilities.GL_EXT_paletted_texture);
        playerSnooper.addStatToSnooper("gl_caps[EXT_rescale_normal]", capabilities.GL_EXT_rescale_normal);
        playerSnooper.addStatToSnooper("gl_caps[EXT_separate_shader_objects]", capabilities.GL_EXT_separate_shader_objects);
        playerSnooper.addStatToSnooper("gl_caps[EXT_shader_image_load_store]", capabilities.GL_EXT_shader_image_load_store);
        playerSnooper.addStatToSnooper("gl_caps[EXT_shadow_funcs]", capabilities.GL_EXT_shadow_funcs);
        playerSnooper.addStatToSnooper("gl_caps[EXT_shared_texture_palette]", capabilities.GL_EXT_shared_texture_palette);
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_clear_tag]", capabilities.GL_EXT_stencil_clear_tag);
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_two_side]", capabilities.GL_EXT_stencil_two_side);
        playerSnooper.addStatToSnooper("gl_caps[EXT_stencil_wrap]", capabilities.GL_EXT_stencil_wrap);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_3d]", capabilities.GL_EXT_texture_3d);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_array]", capabilities.GL_EXT_texture_array);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_buffer_object]", capabilities.GL_EXT_texture_buffer_object);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_filter_anisotropic]", capabilities.GL_EXT_texture_filter_anisotropic);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_integer]", capabilities.GL_EXT_texture_integer);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_lod_bias]", capabilities.GL_EXT_texture_lod_bias);
        playerSnooper.addStatToSnooper("gl_caps[EXT_texture_sRGB]", capabilities.GL_EXT_texture_sRGB);
        playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_shader]", capabilities.GL_EXT_vertex_shader);
        playerSnooper.addStatToSnooper("gl_caps[EXT_vertex_weighting]", capabilities.GL_EXT_vertex_weighting);
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_uniforms]", GL11.glGetInteger(35658));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_fragment_uniforms]", GL11.glGetInteger(35657));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_attribs]", GL11.glGetInteger(34921));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_vertex_texture_image_units]", GL11.glGetInteger(35660));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(34930));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_caps[gl_max_texture_image_units]", GL11.glGetInteger(35071));
        GL11.glGetError();
        playerSnooper.addStatToSnooper("gl_max_texture_size", getGLMaximumTextureSize());
    }

    public static int getGLMaximumTextureSize() {
        for (int i = 16384; i > 0; i >>= 1) {
            GL11.glTexImage2D(32868, 0, 6408, i, i, 0, 6408, 5121, (ByteBuffer)null);
            int glGetTexLevelParameteri = GL11.glGetTexLevelParameteri(32868, 0, 4096);
            if (glGetTexLevelParameteri != 0) {
                return i;
            }
        }

        return -1;
    }

    @Override
    public boolean isSnooperEnabled() {
        return false;
    }

    public void setServerData(ServerData serverDataIn) {
        this.serverData = this.currentServerData;
        this.currentServerData = serverDataIn;
    }

    public ServerData getCurrentServerData() {
        return this.currentServerData;
    }

    public boolean isIntegratedServerRunning() {
        return this.integratedServerIsRunning;
    }

    public boolean isSingleplayer() {
        return this.integratedServerIsRunning && this.theIntegratedServer != null;
    }

    public IntegratedServer getIntegratedServer() {
        return this.theIntegratedServer;
    }

    public static void stopIntegratedServer() throws IOException {
        if (theMinecraft != null) {
            IntegratedServer integratedServer = theMinecraft.getIntegratedServer();
            if (integratedServer != null) {
                integratedServer.stopServer();
            }
        }
    }

    public PlayerUsageSnooper getPlayerUsageSnooper() {
        return this.usageSnooper;
    }

    public static long getSystemTime() {
        return Sys.getTime() * 1000L / Sys.getTimerResolution();
    }

    public boolean isFullScreen() {
        return this.fullscreen;
    }

    public Session getSession() {
        return this.session;
    }

    public Multimap getTwitchDetails() {
        return this.twitchDetails;
    }

    public Proxy getProxy() {
        return this.proxy;
    }

    public TextureManager getTextureManager() {
        return this.renderEngine;
    }

    public IResourceManager getResourceManager() {
        return this.mcResourceManager;
    }

    public ResourcePackRepository getResourcePackRepository() {
        return this.mcResourcePackRepository;
    }

    public LanguageManager getLanguageManager() {
        return this.mcLanguageManager;
    }

    public TextureMap getTextureMapBlocks() {
        return this.textureMapBlocks;
    }

    public boolean isJava64bit() {
        return this.jvm64bit;
    }

    public boolean isGamePaused() {
        return this.isGamePaused;
    }

    public SoundHandler getSoundHandler() {
        return this.mcSoundHandler;
    }

    public MusicType getAmbientMusicType() {
        return this.currentScreen instanceof GuiWinGame
            ? MusicType.CREDITS
            : (
                this.thePlayer != null
                    ? (
                        this.thePlayer.worldObj.provider instanceof WorldProviderHell
                            ? MusicType.NETHER
                            : (
                                this.thePlayer.worldObj.provider instanceof WorldProviderEnd
                                    ? (BossStatus.bossName != null && BossStatus.statusBarTime > 0 ? MusicType.END_BOSS : MusicType.END)
                                    : (this.thePlayer.capabilities.isCreativeMode && this.thePlayer.capabilities.allowFlying ? MusicType.CREATIVE : MusicType.GAME)
                            )
                    )
                    : MusicType.MENU
            );
    }

    public FrameTimer getTwitchStream() {
        return this.stream;
    }

    public void dispatchKeypresses() {
        int eventKey = Keyboard.getEventKey();
        if (eventKey != 0
            && !Keyboard.isRepeatEvent()
            && (!(this.currentScreen instanceof GuiControls) || ((GuiControls)this.currentScreen).time <= getSystemTime() - 20L)
            && Keyboard.getEventKeyState()) {
            if (eventKey == this.gameSettings.keyBindStreamStartStop.getKeyCode()) {
                this.toggleFullscreen();
            } else if (eventKey == this.gameSettings.keyBindScreenshot.getKeyCode()) {
                ScreenshotHelper.saveScreenshot(this.mcDataDir, this.displayWidth, this.displayHeight, this.framebufferMc);
            }
        }
    }

    public ListenableFuture addScheduledTask(Callable callableToSchedule) {
        Validate.notNull(callableToSchedule);
        if (!this.isCallingFromMinecraftThread()) {
            ListenableFutureTask listenableFutureTask = ListenableFutureTask.create(callableToSchedule);
            Queue scheduledTasks = this.scheduledTasks;
            synchronized (this.scheduledTasks) {
                this.scheduledTasks.add(listenableFutureTask);
                return listenableFutureTask;
            }
        } else {
            try {
                return Futures.immediateFuture(callableToSchedule.call());
            } catch (Exception e) {
                return Futures.immediateFailedFuture(e);
            }
        }
    }

    public ListenableFuture addScheduledTask(Runnable runnableToSchedule) {
        Validate.notNull(runnableToSchedule);
        return this.addScheduledTask(Executors.callable(runnableToSchedule));
    }

    public boolean isCallingFromMinecraftThread() {
        return Thread.currentThread() == this.mcThread;
    }

    public MinecraftSessionService getSessionService() {
        return this.sessionService;
    }

    public SkinManager getSkinManager() {
        return this.skinManager;
    }

    public void run2() throws IOException {
        while (this.gameSettings.keyBindTogglePerspective.isPressed()) {
            this.gameSettings.thirdPersonView++;
            if (this.gameSettings.thirdPersonView > 2) {
                this.gameSettings.thirdPersonView = 0;
            }
        }

        while (CheatBreaker.getInstance().getKeybindSettings().openMenu.isPressed()) {
            if (!CheatBreaker.getInstance().getKeybindSettings().streamerMode.getBooleanValue()
                || !CheatBreaker.getInstance().getKeybindSettings().disableModMenuKeybind.getBooleanValue()) {
                this.displayGuiScreen(new ModsScreen());
            } else if (CheatBreaker.getInstance().getKeybindSettings().notifyWhenPressed.getBooleanValue()) {
                String str = CheatBreaker.getInstance().getKeybindSettings().holdModsButton.getBooleanValue()
                    ? "hold "
                        + EnumChatFormatting.GOLD
                        + "Mods"
                        + EnumChatFormatting.RESET
                        + " for "
                        + EnumChatFormatting.AQUA
                        + CheatBreaker.getInstance().getKeybindSettings().holdModsDuration.getFloatValue()
                        + " seconds"
                    : "click " + EnumChatFormatting.GOLD + "Mods";
                CheatBreaker.getInstance()
                    .getModManager()
                    .notificationsMod
                    .addNotification(
                        "info",
                        "Streamer mode is "
                            + EnumChatFormatting.GREEN
                            + "enabled"
                            + EnumChatFormatting.RESET
                            + "! Press escape and "
                            + str
                            + EnumChatFormatting.RESET
                            + " to open the Mod Menu.",
                        5000L
                    );
            }
        }

        while (CheatBreaker.getInstance().getKeybindSettings().openVoiceMenu.isPressed()) {
            this.displayGuiScreen(new VoiceChatScreen());
        }
    }

    public DefaultResourcePack getMcDefaultResourcePack() {
        return this.mcDefaultResourcePack;
    }

    public void setSession(Session session) {
        this.session = session;
    }
}
