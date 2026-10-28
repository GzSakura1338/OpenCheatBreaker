package net.optifine;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import net.minecraft.block.Block;
import net.minecraft.client.LoadingScreenRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.resources.ResourcePackRepository.Entry;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.init.Blocks;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.FrameTimer;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Util;
import net.minecraft.util.Util.EnumOS;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import shadersmod.client.Shaders;

public class Config {
    public static final String OF_NAME = "OptiFine";
    public static final String MC_VERSION = "1.7.10";
    public static final String OF_EDITION = "HD_U";
    public static final String OF_RELEASE = "E7";
    public static final String VERSION = "OptiFine_1.7.10_HD_U_E7";
    public static String newRelease = null;
    public static boolean notify64BitJava = false;
    public static String openGlVersion = null;
    public static String openGlRenderer = null;
    public static String openGlVendor = null;
    public static String[] openGlExtensions = null;
    public static GlVersion glVersion = null;
    public static GlVersion glslVersion = null;
    public static int minecraftVersionInt = -1;
    public static boolean fancyFogAvailable = false;
    public static boolean occlusionAvailable = false;
    public static GameSettings gameSettings = null;
    public static Minecraft minecraft = Minecraft.getMinecraft();
    public static boolean initialized = false;
    public static Thread minecraftThread = null;
    public static DisplayMode desktopDisplayMode = null;
    public static DisplayMode[] displayModes = null;
    public static int antialiasingLevel = 0;
    public static int availableProcessors = 0;
    public static boolean zoomMode = false;
    public static float floatValue = 1.0F;
    public static int texturePackClouds = 0;
    public static boolean waterOpacityChanged = false;
    public static boolean fullscreenModeChecked = false;
    public static boolean desktopModeChecked = false;
    public static DefaultResourcePack defaultResourcePackLazy = null;
    public static Float DEF_ALPHA_FUNC_LEVEL = 0.1F;
    public static float renderPartialTicks;
    public static Logger LOGGER = LogManager.getLogger();
    public static String text = null;
    public static int intValue = 0;

    public static String getVersion() {
        return "OptiFine_1.7.10_HD_U_E7";
    }

    public static String getVersionDebug() {
        StringBuffer sb = new StringBuffer(32);
        if (isDynamicLights()) {
            sb.append("DL: ");
            sb.append(String.valueOf(DynamicLights.getCount()));
            sb.append(", ");
        }

        sb.append("OptiFine_1.7.10_HD_U_E7");
        String shaderPackName = Shaders.getShaderPackName();
        if (shaderPackName != null) {
            sb.append(", ");
            sb.append(shaderPackName);
        }

        return sb.toString();
    }

    public static void initGameSettings(GameSettings gameSettings2) {
        if (gameSettings == null) {
            gameSettings = gameSettings2;
            desktopDisplayMode = Display.getDesktopDisplayMode();
            updateAvailableProcessors();
            ReflectorForge.putLaunchBlackboard("optifine.ForgeSplashCompatible", Boolean.TRUE);
        }
    }

    public static void initDisplay() {
        checkInitialized();
        antialiasingLevel = gameSettings.ofAaLevel;
        checkDisplaySettings();
        checkDisplayMode();
        minecraftThread = Thread.currentThread();
        updateThreadPriorities();
        Shaders.startup(Minecraft.getMinecraft());
    }

    public static void checkInitialized() {
        if (!initialized && Display.isCreated()) {
            initialized = true;
            checkOpenGlCaps();
        }
    }

    public static void checkOpenGlCaps() {
        log("");
        log(getVersion());
        log("Build: " + getBuild());
        log("OS: " + System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version"));
        log("Java: " + System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        log("VM: " + System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor"));
        log("LWJGL: " + Sys.getVersion());
        openGlVersion = GL11.glGetString(7938);
        openGlRenderer = GL11.glGetString(7937);
        openGlVendor = GL11.glGetString(7936);
        log("OpenGL: " + openGlRenderer + ", version " + openGlVersion + ", " + openGlVendor);
        log("OpenGL Version: " + getOpenGlVersionString());
        if (!GLContext.getCapabilities().OpenGL12) {
            log("OpenGL Mipmap levels: Not available (GL12.GL_TEXTURE_MAX_LEVEL)");
        }

        fancyFogAvailable = GLContext.getCapabilities().GL_NV_fog_distance;
        if (!fancyFogAvailable) {
            log("OpenGL Fancy fog: Not available (GL_NV_fog_distance)");
        }

        occlusionAvailable = GLContext.getCapabilities().GL_ARB_occlusion_query;
        if (!occlusionAvailable) {
            log("OpenGL Occlussion culling: Not available (GL_ARB_occlusion_query)");
        }

        int gLMaximumTextureSize = TextureUtils.getGLMaximumTextureSize();
        dbg("Maximum texture size: " + gLMaximumTextureSize + "x" + gLMaximumTextureSize);
    }

    public static String getBuild() {
        try {
            InputStream resourceAsStream = Config.class.getResourceAsStream("/buildof.txt");
            return resourceAsStream == null ? null : readLines(resourceAsStream)[0];
        } catch (Exception e) {
            warn("" + e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }

    public static boolean isFancyFogAvailable() {
        return fancyFogAvailable;
    }

    public static boolean isOcclusionAvailable() {
        return occlusionAvailable;
    }

    public static int getMinecraftVersionInt() {
        if (minecraftVersionInt < 0) {
            String[] tokenize2 = tokenize("1.7.10", ".");
            int n = 0;
            if (tokenize2.length > 0) {
                n += 10000 * parseInt(tokenize2[0], 0);
            }

            if (tokenize2.length > 1) {
                n += 100 * parseInt(tokenize2[1], 0);
            }

            if (tokenize2.length > 2) {
                n += 1 * parseInt(tokenize2[2], 0);
            }

            minecraftVersionInt = n;
        }

        return minecraftVersionInt;
    }

    public static String getOpenGlVersionString() {
        GlVersion glVersion = getGlVersion();
        return "" + glVersion.getMajor() + "." + glVersion.getMinor() + "." + glVersion.getRelease();
    }

    public static GlVersion getGlVersionLwjgl() {
        return GLContext.getCapabilities().OpenGL44
            ? new GlVersion(4, 4)
            : (
                GLContext.getCapabilities().OpenGL43
                    ? new GlVersion(4, 3)
                    : (
                        GLContext.getCapabilities().OpenGL42
                            ? new GlVersion(4, 2)
                            : (
                                GLContext.getCapabilities().OpenGL41
                                    ? new GlVersion(4, 1)
                                    : (
                                        GLContext.getCapabilities().OpenGL40
                                            ? new GlVersion(4, 0)
                                            : (
                                                GLContext.getCapabilities().OpenGL33
                                                    ? new GlVersion(3, 3)
                                                    : (
                                                        GLContext.getCapabilities().OpenGL32
                                                            ? new GlVersion(3, 2)
                                                            : (
                                                                GLContext.getCapabilities().OpenGL31
                                                                    ? new GlVersion(3, 1)
                                                                    : (
                                                                        GLContext.getCapabilities().OpenGL30
                                                                            ? new GlVersion(3, 0)
                                                                            : (
                                                                                GLContext.getCapabilities().OpenGL21
                                                                                    ? new GlVersion(2, 1)
                                                                                    : (
                                                                                        GLContext.getCapabilities().OpenGL20
                                                                                            ? new GlVersion(2, 0)
                                                                                            : (
                                                                                                GLContext.getCapabilities().OpenGL15
                                                                                                    ? new GlVersion(1, 5)
                                                                                                    : (
                                                                                                        GLContext.getCapabilities().OpenGL14
                                                                                                            ? new GlVersion(1, 4)
                                                                                                            : (
                                                                                                                GLContext.getCapabilities().OpenGL13
                                                                                                                    ? new GlVersion(1, 3)
                                                                                                                    : (
                                                                                                                        GLContext.getCapabilities().OpenGL12
                                                                                                                            ? new GlVersion(1, 2)
                                                                                                                            : (
                                                                                                                                GLContext.getCapabilities().OpenGL11
                                                                                                                                    ? new GlVersion(1, 1)
                                                                                                                                    : new GlVersion(1, 0)
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

    public static GlVersion getGlVersion() {
        if (glVersion == null) {
            String glGetString = GL11.glGetString(7938);
            glVersion = parseGlVersion(glGetString, (GlVersion)null);
            if (glVersion == null) {
                glVersion = getGlVersionLwjgl();
            }

            if (glVersion == null) {
                glVersion = new GlVersion(1, 0);
            }
        }

        return glVersion;
    }

    public static GlVersion getGlslVersion() {
        if (glslVersion == null) {
            String glGetString = GL11.glGetString(35724);
            glslVersion = parseGlVersion(glGetString, (GlVersion)null);
            if (glslVersion == null) {
                glslVersion = new GlVersion(1, 10);
            }
        }

        return glslVersion;
    }

    public static GlVersion parseGlVersion(String str, GlVersion glVersion) {
        try {
            if (str == null) {
                return glVersion;
            }

            Pattern compile = Pattern.compile("([0-9]+)\\.([0-9]+)(\\.([0-9]+))?(.+)?");
            Matcher matcher = compile.matcher(str);
            if (!matcher.matches()) {
                return glVersion;
            }

            int parseInt = Integer.parseInt(matcher.group(1));
            int parseInt2 = Integer.parseInt(matcher.group(2));
            int n = matcher.group(4) != null ? Integer.parseInt(matcher.group(4)) : 0;
            String group = matcher.group(5);
            return new GlVersion(parseInt, parseInt2, n, group);
        } catch (Exception e) {
            e.printStackTrace();
            return glVersion;
        }
    }

    public static String[] getOpenGlExtensions() {
        if (openGlExtensions == null) {
            openGlExtensions = detectOpenGlExtensions();
        }

        return openGlExtensions;
    }

    public static String[] detectOpenGlExtensions() {
        try {
            GlVersion glVersion = getGlVersion();
            if (glVersion.getMajor() >= 3) {
                int glGetInteger = GL11.glGetInteger(33309);
                if (glGetInteger > 0) {
                    String[] strs = new String[glGetInteger];

                    for (int i = 0; i < glGetInteger; i++) {
                        strs[i] = GL30.glGetStringi(7939, i);
                    }

                    return strs;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String glGetString = GL11.glGetString(7939);
            return glGetString.split(" ");
        } catch (Exception e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    public static void updateThreadPriorities() {
        updateAvailableProcessors();
        boolean flag = true;
        if (isSingleProcessor()) {
            if (isSmoothWorld()) {
                minecraftThread.setPriority(10);
                setThreadPriority("Server thread", 1);
            } else {
                minecraftThread.setPriority(5);
                setThreadPriority("Server thread", 5);
            }
        } else {
            minecraftThread.setPriority(10);
            setThreadPriority("Server thread", 5);
        }
    }

    public static void setThreadPriority(String str, int n) {
        try {
            ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
            if (threadGroup == null) {
                return;
            }

            int n2 = (threadGroup.activeCount() + 10) * 2;
            Thread[] threads = new Thread[n2];
            threadGroup.enumerate(threads, false);

            for (int i = 0; i < threads.length; i++) {
                Thread thread = threads[i];
                if (thread != null && thread.getName().startsWith(str)) {
                    thread.setPriority(n);
                }
            }
        } catch (Throwable e) {
            warn(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public static boolean isMinecraftThread() {
        return Thread.currentThread() == minecraftThread;
    }

    public static void startVersionCheckThread() {
        VersionCheckThread versionCheckThread = new VersionCheckThread();
        versionCheckThread.start();
    }

    public static int getMipmapLevels() {
        return gameSettings.mipmapLevels;
    }

    public static int getMipmapType() {
        switch (gameSettings.ofMipmapType) {
            case 0:
                return 9986;
            case 1:
                return 9986;
            case 2:
                if (isMultiTexture()) {
                    return 9985;
                }

                return 9986;
            case 3:
                if (isMultiTexture()) {
                    return 9987;
                }

                return 9986;
            default:
                return 9986;
        }
    }

    public static boolean isUseAlphaFunc() {
        float alphaFuncLevel = getAlphaFuncLevel();
        return alphaFuncLevel > DEF_ALPHA_FUNC_LEVEL + 1.0E-5F;
    }

    public static float getAlphaFuncLevel() {
        return DEF_ALPHA_FUNC_LEVEL;
    }

    public static boolean isFogFancy() {
        return !isFancyFogAvailable() ? false : gameSettings.ofFogType == 2;
    }

    public static boolean isFogFast() {
        return gameSettings.ofFogType == 1;
    }

    public static boolean isFogOff() {
        return gameSettings.ofFogType == 3;
    }

    public static float getFogStart() {
        return gameSettings.ofFogStart;
    }

    public static boolean isOcclusionEnabled() {
        return gameSettings.advancedOpengl;
    }

    public static boolean isOcclusionFancy() {
        return !isOcclusionEnabled() ? false : gameSettings.ofOcclusionFancy;
    }

    public static boolean isLoadChunksFar() {
        return gameSettings.ofLoadFar;
    }

    public static int getPreloadedChunks() {
        return gameSettings.ofPreloadedChunks;
    }

    public static void dbg(String str) {
        LOGGER.info("[OptiFine] " + str);
    }

    public static void warn(String str) {
        LOGGER.warn("[OptiFine] " + str);
    }

    public static void error(String str) {
        LOGGER.error("[OptiFine] " + str);
    }

    public static void log(String str) {
        dbg(str);
    }

    public static int getUpdatesPerFrame() {
        return gameSettings.ofChunkUpdates;
    }

    public static boolean isDynamicUpdates() {
        return gameSettings.ofChunkUpdatesDynamic;
    }

    public static boolean isRainFancy() {
        return gameSettings.ofRain == 0 ? gameSettings.fancyGraphics : gameSettings.ofRain == 2;
    }

    public static boolean isWaterFancy() {
        return gameSettings.ofWater == 0 ? gameSettings.fancyGraphics : gameSettings.ofWater == 2;
    }

    public static boolean isRainOff() {
        return gameSettings.ofRain == 3;
    }

    public static boolean isCloudsFancy() {
        return gameSettings.ofClouds != 0
            ? gameSettings.ofClouds == 2
            : (
                isShaders() && !Shaders.shaderPackClouds.isDefault()
                    ? Shaders.shaderPackClouds.isFancy()
                    : (texturePackClouds != 0 ? texturePackClouds == 2 : gameSettings.fancyGraphics)
            );
    }

    public static boolean isCloudsOff() {
        return gameSettings.ofClouds != 0
            ? gameSettings.ofClouds == 3
            : (isShaders() && !Shaders.shaderPackClouds.isDefault() ? Shaders.shaderPackClouds.isOff() : (texturePackClouds != 0 ? texturePackClouds == 3 : false));
    }

    public static void updateTexturePackClouds() {
        texturePackClouds = 0;
        IResourceManager resourceManager = getResourceManager();
        if (resourceManager != null) {
            try {
                InputStream inputStream = resourceManager.getResource(new ResourceLocation("mcpatcher/color.properties")).getInputStream();
                if (inputStream == null) {
                    return;
                }

                Properties properties = new Properties();
                properties.load(inputStream);
                inputStream.close();
                String property = properties.getProperty("clouds");
                if (property == null) {
                    return;
                }

                dbg("Texture pack clouds: " + property);
                property = property.toLowerCase();
                if (property.equals("fast")) {
                    texturePackClouds = 1;
                }

                if (property.equals("fancy")) {
                    texturePackClouds = 2;
                }

                if (property.equals("off")) {
                    texturePackClouds = 3;
                }
            } catch (Exception e) {
            }
        }
    }

    public static boolean isTreesFancy() {
        return gameSettings.ofTrees == 0 ? gameSettings.fancyGraphics : gameSettings.ofTrees == 2;
    }

    public static boolean isGrassFancy() {
        return gameSettings.ofGrass == 0 ? gameSettings.fancyGraphics : gameSettings.ofGrass == 2;
    }

    public static boolean isDroppedItemsFancy() {
        return gameSettings.ofDroppedItems == 0 ? gameSettings.fancyGraphics : gameSettings.ofDroppedItems == 2;
    }

    public static int limit(int n, int n2, int n3) {
        return n < n2 ? n2 : (n > n3 ? n3 : n);
    }

    public static float limit(float f, float f2, float f3) {
        return f < f2 ? f2 : (f > f3 ? f3 : f);
    }

    public static double limit(double d, double d2, double d3) {
        return d < d2 ? d2 : (d > d3 ? d3 : d);
    }

    public static float limitTo1(float f) {
        return f < 0.0F ? 0.0F : (f > 1.0F ? 1.0F : f);
    }

    public static boolean isAnimatedPortal() {
        return gameSettings.ofAnimatedPortal;
    }

    public static boolean isAnimatedRedstone() {
        return gameSettings.ofAnimatedRedstone;
    }

    public static boolean isAnimatedExplosion() {
        return gameSettings.ofAnimatedExplosion;
    }

    public static boolean isAnimatedFlame() {
        return gameSettings.ofAnimatedFlame;
    }

    public static boolean isAnimatedSmoke() {
        return gameSettings.ofAnimatedSmoke;
    }

    public static boolean isVoidParticles() {
        return gameSettings.ofVoidParticles;
    }

    public static boolean isWaterParticles() {
        return gameSettings.ofWaterParticles;
    }

    public static boolean isRainSplash() {
        return gameSettings.ofRainSplash;
    }

    public static boolean isPortalParticles() {
        return gameSettings.ofPortalParticles;
    }

    public static boolean isPotionParticles() {
        return gameSettings.ofPotionParticles;
    }

    public static boolean isDepthFog() {
        return gameSettings.ofDepthFog;
    }

    public static float getAmbientOcclusionLevel() {
        return isShaders() && Shaders.aoLevel >= 0.0F ? Shaders.aoLevel : gameSettings.ofAoLevel;
    }

    public static String listToString(List list) {
        return listToString(list, ", ");
    }

    public static String listToString(List list, String str) {
        if (list == null) {
            return "";
        }

        StringBuffer sb = new StringBuffer(list.size() * 5);

        for (int i = 0; i < list.size(); i++) {
            Object obj = list.get(i);
            if (i > 0) {
                sb.append(str);
            }

            sb.append(String.valueOf(obj));
        }

        return sb.toString();
    }

    public static String arrayToString(Object[] objs) {
        return arrayToString(objs, ", ");
    }

    public static String arrayToString(Object[] objs, String str) {
        if (objs == null) {
            return "";
        }

        StringBuffer sb = new StringBuffer(objs.length * 5);

        for (int i = 0; i < objs.length; i++) {
            Object obj = objs[i];
            if (i > 0) {
                sb.append(str);
            }

            sb.append(String.valueOf(obj));
        }

        return sb.toString();
    }

    public static String arrayToString(int[] ints) {
        return arrayToString(ints, ", ");
    }

    public static String arrayToString(int[] ints, String str) {
        if (ints == null) {
            return "";
        }

        StringBuffer sb = new StringBuffer(ints.length * 5);

        for (int i = 0; i < ints.length; i++) {
            int n = ints[i];
            if (i > 0) {
                sb.append(str);
            }

            sb.append(String.valueOf(n));
        }

        return sb.toString();
    }

    public static Minecraft getMinecraft() {
        return minecraft;
    }

    public static TextureManager getTextureManager() {
        return minecraft.getTextureManager();
    }

    public static IResourceManager getResourceManager() {
        return minecraft.getResourceManager();
    }

    public static InputStream getResourceStream(ResourceLocation resourceLocation) throws IOException {
        return getResourceStream(minecraft.getResourceManager(), resourceLocation);
    }

    public static InputStream getResourceStream(IResourceManager resourceManager, ResourceLocation resourceLocation) throws IOException {
        IResource resource = resourceManager.getResource(resourceLocation);
        return resource == null ? null : resource.getInputStream();
    }

    public static IResource getResource(ResourceLocation resourceLocation) throws IOException {
        return minecraft.getResourceManager().getResource(resourceLocation);
    }

    public static boolean hasResource(ResourceLocation resourceLocation) {
        try {
            IResource resource = getResource(resourceLocation);
            return resource != null;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean hasResource(IResourceManager resourceManager, ResourceLocation resourceLocation) {
        try {
            IResource resource = resourceManager.getResource(resourceLocation);
            return resource != null;
        } catch (IOException e) {
            return false;
        }
    }

    public static IResourcePack[] getResourcePacks() {
        ResourcePackRepository resourcePackRepository = minecraft.getResourcePackRepository();
        List repositoryEntries = resourcePackRepository.getRepositoryEntries();
        ArrayList list = new ArrayList();

        for (Entry entry : (Iterable<Entry>)repositoryEntries) {
            list.add(entry.getResourcePack());
        }

        if (resourcePackRepository.getResourcePackInstance() != null) {
            list.add(resourcePackRepository.getResourcePackInstance());
        }

        return (IResourcePack[])list.toArray(new IResourcePack[list.size()]);
    }

    public static String getResourcePackNames() {
        if (minecraft.getResourcePackRepository() == null) {
            return "";
        }

        IResourcePack[] resourcePacks = getResourcePacks();
        if (resourcePacks.length <= 0) {
            return getDefaultResourcePack().getPackName();
        }

        String[] strs = new String[resourcePacks.length];

        for (int i = 0; i < resourcePacks.length; i++) {
            strs[i] = resourcePacks[i].getPackName();
        }

        return arrayToString(strs);
    }

    public static IResourcePack getDefaultResourcePack() {
        return minecraft.getResourcePackRepository().rprDefaultResourcePack;
    }

    public static boolean isFromDefaultResourcePack(ResourceLocation resourceLocation) {
        IResourcePack definingResourcePack = getDefiningResourcePack(resourceLocation);
        return definingResourcePack == getDefaultResourcePack();
    }

    public static IResourcePack getDefiningResourcePack(ResourceLocation resourceLocation) {
        IResourcePack[] resourcePacks = getResourcePacks();

        for (int i = resourcePacks.length - 1; i >= 0; i--) {
            IResourcePack resourcePack = resourcePacks[i];
            if (resourcePack.resourceExists(resourceLocation)) {
                return resourcePack;
            }
        }

        return getDefaultResourcePack().resourceExists(resourceLocation) ? getDefaultResourcePack() : null;
    }

    public static RenderGlobal getRenderGlobal() {
        return minecraft == null ? null : minecraft.renderGlobal;
    }

    public static int getMaxDynamicTileWidth() {
        return 64;
    }

    public static IIcon getSideGrassTexture(IBlockAccess blockAccess, int n, int n2, int n3, int n4, IIcon icon) {
        if (!isBetterGrass()) {
            return icon;
        }

        IIcon iconGrassTop = TextureUtils.iconGrassTop;
        Block grass = Blocks.grass;
        if (icon == TextureUtils.iconMyceliumSide) {
            iconGrassTop = TextureUtils.iconMyceliumTop;
            grass = Blocks.mycelium;
        }

        if (isBetterGrassFancy()) {
            n2--;
            switch (n4) {
                case 2:
                    n3--;
                    break;
                case 3:
                    n3++;
                    break;
                case 4:
                    n--;
                    break;
                case 5:
                    n++;
            }

            Block block = blockAccess.getBlock(n, n2, n3);
            if (block != grass) {
                return icon;
            }
        }

        return iconGrassTop;
    }

    public static IIcon getSideSnowGrassTexture(IBlockAccess blockAccess, int n, int n2, int n3, int n4) {
        if (!isBetterGrass()) {
            return TextureUtils.iconGrassSideSnowed;
        }

        if (isBetterGrassFancy()) {
            switch (n4) {
                case 2:
                    n3--;
                    break;
                case 3:
                    n3++;
                    break;
                case 4:
                    n--;
                    break;
                case 5:
                    n++;
            }

            Block block = blockAccess.getBlock(n, n2, n3);
            if (block != Blocks.snow_layer && block != Blocks.snow) {
                return TextureUtils.iconGrassSideSnowed;
            }
        }

        return TextureUtils.iconSnow;
    }

    public static boolean isBetterGrass() {
        return gameSettings.ofBetterGrass != 3;
    }

    public static boolean isBetterGrassFancy() {
        return gameSettings.ofBetterGrass == 2;
    }

    public static boolean isWeatherEnabled() {
        return gameSettings.ofWeather;
    }

    public static boolean isSkyEnabled() {
        return gameSettings.ofSky;
    }

    public static boolean isSunMoonEnabled() {
        return gameSettings.ofSunMoon;
    }

    public static boolean isSunTexture() {
        return !isSunMoonEnabled() ? false : !isShaders() || Shaders.isSun();
    }

    public static boolean isMoonTexture() {
        return !isSunMoonEnabled() ? false : !isShaders() || Shaders.isMoon();
    }

    public static boolean isVignetteEnabled() {
        return isShaders() && !Shaders.isVignette() ? false : (gameSettings.ofVignette == 0 ? gameSettings.fancyGraphics : gameSettings.ofVignette == 2);
    }

    public static boolean isStarsEnabled() {
        return gameSettings.ofStars;
    }

    public static void sleep(long l) {
        try {
            Thread.sleep(l);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static boolean isTimeDayOnly() {
        return gameSettings.ofTime == 1;
    }

    public static boolean isTimeDefault() {
        return gameSettings.ofTime == 0 || gameSettings.ofTime == 2;
    }

    public static boolean isTimeNightOnly() {
        return gameSettings.ofTime == 3;
    }

    public static boolean isClearWater() {
        return gameSettings.ofClearWater;
    }

    public static int getAnisotropicFilterLevel() {
        return gameSettings.anisotropicFiltering;
    }

    public static boolean isAnisotropicFiltering() {
        return getAnisotropicFilterLevel() > 1;
    }

    public static int getAntialiasingLevel() {
        return antialiasingLevel;
    }

    public static boolean isAntialiasing() {
        return false;
    }

    public static boolean isAntialiasingConfigured() {
        return false;
    }

    public static boolean isMultiTexture() {
        return false;
    }

    public static boolean between(int n, int n2, int n3) {
        return n >= n2 && n <= n3;
    }

    public static boolean isDrippingWaterLava() {
        return gameSettings.ofDrippingWaterLava;
    }

    public static boolean isBetterSnow() {
        return gameSettings.ofBetterSnow;
    }

    public static Dimension getFullscreenDimension() {
        if (desktopDisplayMode == null) {
            return null;
        }

        if (gameSettings == null) {
            return new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight());
        }

        String ofFullscreenMode = gameSettings.ofFullscreenMode;
        if (ofFullscreenMode.equals("Default")) {
            return new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight());
        }

        String[] tokenize2 = tokenize(ofFullscreenMode, " x");
        return tokenize2.length < 2
            ? new Dimension(desktopDisplayMode.getWidth(), desktopDisplayMode.getHeight())
            : new Dimension(parseInt(tokenize2[0], -1), parseInt(tokenize2[1], -1));
    }

    public static int parseInt(String str, int n) {
        try {
            if (str == null) {
                return n;
            }

            str = str.trim();
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return n;
        }
    }

    public static float parseFloat(String str, float f) {
        try {
            if (str == null) {
                return f;
            }

            str = str.trim();
            return Float.parseFloat(str);
        } catch (NumberFormatException e) {
            return f;
        }
    }

    public static boolean parseBoolean(String str, boolean flag) {
        try {
            if (str == null) {
                return flag;
            }

            str = str.trim();
            return Boolean.parseBoolean(str);
        } catch (NumberFormatException e) {
            return flag;
        }
    }

    public static String[] tokenize(String str, String str2) {
        StringTokenizer stringTokenizer = new StringTokenizer(str, str2);
        ArrayList list = new ArrayList();

        while (stringTokenizer.hasMoreTokens()) {
            String nextToken = stringTokenizer.nextToken();
            list.add(nextToken);
        }

        return (String[])list.toArray(new String[list.size()]);
    }

    public static DisplayMode getDesktopDisplayMode() {
        return desktopDisplayMode;
    }

    public static DisplayMode[] getDisplayModes() {
        if (displayModes == null) {
            try {
                DisplayMode[] availableDisplayModes = Display.getAvailableDisplayModes();
                Set displayModeDimensions = getDisplayModeDimensions(availableDisplayModes);
                ArrayList list = new ArrayList();

                for (Dimension dimension : (Iterable<Dimension>)displayModeDimensions) {
                    DisplayMode[] displayModes = getDisplayModes(availableDisplayModes, dimension);
                    DisplayMode displayMode = getDisplayMode(displayModes, desktopDisplayMode);
                    if (displayMode != null) {
                        list.add(displayMode);
                    }
                }

                DisplayMode[] toArray = (DisplayMode[])list.toArray(new DisplayMode[list.size()]);
                Arrays.sort(toArray, new DisplayModeComparator());
                return toArray;
            } catch (Exception e) {
                e.printStackTrace();
                displayModes = new DisplayMode[]{desktopDisplayMode};
            }
        }

        return displayModes;
    }

    public static DisplayMode getLargestDisplayMode() {
        DisplayMode[] displayModes = getDisplayModes();
        if (displayModes != null && displayModes.length >= 1) {
            DisplayMode displayMode = displayModes[displayModes.length - 1];
            return desktopDisplayMode.getWidth() > displayMode.getWidth()
                ? desktopDisplayMode
                : (desktopDisplayMode.getWidth() == displayMode.getWidth() && desktopDisplayMode.getHeight() > displayMode.getHeight() ? desktopDisplayMode : displayMode);
        } else {
            return desktopDisplayMode;
        }
    }

    public static Set<Dimension> getDisplayModeDimensions(DisplayMode[] displayModes) {
        HashSet set = new HashSet();

        for (int i = 0; i < displayModes.length; i++) {
            DisplayMode displayMode = displayModes[i];
            Dimension dimension = new Dimension(displayMode.getWidth(), displayMode.getHeight());
            set.add(dimension);
        }

        return set;
    }

    public static DisplayMode[] getDisplayModes(DisplayMode[] displayModes, Dimension dimension) {
        ArrayList list = new ArrayList();

        for (int i = 0; i < displayModes.length; i++) {
            DisplayMode displayMode = displayModes[i];
            if (displayMode.getWidth() == dimension.getWidth() && displayMode.getHeight() == dimension.getHeight()) {
                list.add(displayMode);
            }
        }

        return (DisplayMode[])list.toArray(new DisplayMode[list.size()]);
    }

    public static DisplayMode getDisplayMode(DisplayMode[] displayModes, DisplayMode displayMode) {
        if (displayMode != null) {
            for (int i = 0; i < displayModes.length; i++) {
                DisplayMode displayMode2 = displayModes[i];
                if (displayMode2.getBitsPerPixel() == displayMode.getBitsPerPixel() && displayMode2.getFrequency() == displayMode.getFrequency()) {
                    return displayMode2;
                }
            }
        }

        if (displayModes.length <= 0) {
            return null;
        }

        Arrays.sort(displayModes, new DisplayModeComparator());
        return displayModes[displayModes.length - 1];
    }

    public static String[] getDisplayModeNames() {
        DisplayMode[] displayModes = getDisplayModes();
        String[] strs = new String[displayModes.length];

        for (int i = 0; i < displayModes.length; i++) {
            DisplayMode displayMode = displayModes[i];
            String str = "" + displayMode.getWidth() + "x" + displayMode.getHeight();
            strs[i] = str;
        }

        return strs;
    }

    public static DisplayMode getDisplayMode(Dimension dimension) {
        DisplayMode[] displayModes = getDisplayModes();

        for (int i = 0; i < displayModes.length; i++) {
            DisplayMode displayMode = displayModes[i];
            if (displayMode.getWidth() == dimension.width && displayMode.getHeight() == dimension.height) {
                return displayMode;
            }
        }

        return desktopDisplayMode;
    }

    public static boolean isAnimatedTerrain() {
        return gameSettings.ofAnimatedWater != 2;
    }

    public static boolean isAnimatedItems() {
        return gameSettings.ofAnimatedItems;
    }

    public static boolean isAnimatedTextures() {
        return gameSettings.ofAnimatedTextures;
    }

    public static boolean isSwampColors() {
        return gameSettings.ofSwampColors;
    }

    public static boolean isRandomMobs() {
        return gameSettings.ofRandomMobs;
    }

    public static void checkGlError(String str) {
        int glGetError = GL11.glGetError();
        if (glGetError != 0) {
            String gluErrorString = GLU.gluErrorString(glGetError);
            error("OpenGlError: " + glGetError + " (" + gluErrorString + "), at: " + str);
        }
    }

    public static boolean isSmoothBiomes() {
        return gameSettings.ofSmoothBiomes;
    }

    public static boolean isCustomColors() {
        return gameSettings.ofCustomColors;
    }

    public static boolean isCustomSky() {
        return gameSettings.ofCustomSky;
    }

    public static boolean isCustomFonts() {
        return gameSettings.ofCustomFonts;
    }

    public static boolean isShowCapes() {
        return gameSettings.ofShowCapes;
    }

    public static boolean isConnectedTextures() {
        return gameSettings.ofConnectedTextures != 3;
    }

    public static boolean isNaturalTextures() {
        return gameSettings.ofNaturalTextures;
    }

    public static boolean isConnectedTexturesFancy() {
        return gameSettings.ofConnectedTextures == 2;
    }

    public static boolean isFastRender() {
        return gameSettings.ofFastRender;
    }

    public static boolean isTranslucentBlocksFancy() {
        return gameSettings.ofTranslucentBlocks == 2;
    }

    public static boolean isShaders() {
        return Shaders.shaderPackLoaded;
    }

    public static String[] readLines(File file) throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException, IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        return readLines(fileInputStream);
    }

    public static String[] readLines(InputStream inputStream) throws java.io.UnsupportedEncodingException, IOException {
        ArrayList list = new ArrayList();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "ASCII");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                return (String[])list.toArray(new String[list.size()]);
            }

            list.add(readLine);
        }
    }

    public static String readFile(File file) throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException, IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        return readInputStream(fileInputStream, "ASCII");
    }

    public static String readInputStream(InputStream inputStream) throws java.io.UnsupportedEncodingException, IOException {
        return readInputStream(inputStream, "ASCII");
    }

    public static String readInputStream(InputStream inputStream, String str) throws java.io.UnsupportedEncodingException, IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, str);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuffer sb = new StringBuffer();

        while (true) {
            String readLine = bufferedReader.readLine();
            if (readLine == null) {
                return sb.toString();
            }

            sb.append(readLine);
            sb.append("\n");
        }
    }

    public static byte[] readAll(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];

        while (true) {
            int read = inputStream.read(bytes);
            if (read < 0) {
                inputStream.close();
                return byteArrayOutputStream.toByteArray();
            }

            byteArrayOutputStream.write(bytes, 0, read);
        }
    }

    public static GameSettings getGameSettings() {
        return gameSettings;
    }

    public static String getNewRelease() {
        return newRelease;
    }

    public static void setNewRelease(String str) {
    }

    public static int compareRelease(String str, String str2) {
        String[] splitRelease2 = splitRelease(str);
        String[] splitRelease3 = splitRelease(str2);
        String str3 = splitRelease2[0];
        String str4 = splitRelease3[0];
        if (!str3.equals(str4)) {
            return str3.compareTo(str4);
        }

        int parseInt2 = parseInt(splitRelease2[1], -1);
        int parseInt3 = parseInt(splitRelease3[1], -1);
        if (parseInt2 != parseInt3) {
            return parseInt2 - parseInt3;
        }

        String str5 = splitRelease2[2];
        String str6 = splitRelease3[2];
        if (!str5.equals(str6)) {
            if (str5.isEmpty()) {
                return 1;
            }

            if (str6.isEmpty()) {
                return -1;
            }
        }

        return str5.compareTo(str6);
    }

    public static String[] splitRelease(String str) {
        if (str != null && str.length() > 0) {
            Pattern compile = Pattern.compile("([A-Z])([0-9]+)(.*)");
            Matcher matcher = compile.matcher(str);
            if (!matcher.matches()) {
                return new String[]{"", "", ""};
            }

            String normalize2 = normalize(matcher.group(1));
            String normalize3 = normalize(matcher.group(2));
            String normalize4 = normalize(matcher.group(3));
            return new String[]{normalize2, normalize3, normalize4};
        } else {
            return new String[]{"", "", ""};
        }
    }

    public static int intHash(int n) {
        n = n ^ 61 ^ n >> 16;
        n += n << 3;
        n ^= n >> 4;
        n *= 668265261;
        return n ^ n >> 15;
    }

    public static int getRandom(int n, int n2, int n3, int n4) {
        int intHash2 = intHash(n4 + 37);
        intHash2 = intHash(intHash2 + n);
        intHash2 = intHash(intHash2 + n3);
        return intHash(intHash2 + n2);
    }

    public static WorldServer getWorldServer() {
        WorldClient theWorld = minecraft.theWorld;
        if (theWorld == null) {
            return null;
        }

        if (!minecraft.isIntegratedServerRunning()) {
            return null;
        }

        IntegratedServer integratedServer = minecraft.getIntegratedServer();
        if (integratedServer == null) {
            return null;
        }

        WorldProvider provider = theWorld.provider;
        if (provider == null) {
            return null;
        }

        int dimensionId = provider.dimensionId;

        try {
            return integratedServer.worldServerForDimension(dimensionId);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public static int getAvailableProcessors() {
        return availableProcessors;
    }

    public static void updateAvailableProcessors() {
        availableProcessors = Runtime.getRuntime().availableProcessors();
    }

    public static boolean isSingleProcessor() {
        return getAvailableProcessors() <= 1;
    }

    public static boolean isSmoothWorld() {
        return !isSingleProcessor() ? false : gameSettings.ofSmoothWorld;
    }

    public static boolean isLazyChunkLoading() {
        return gameSettings.ofLazyChunkLoading;
    }

    public static int getChunkViewDistance() {
        return gameSettings == null ? 10 : gameSettings.renderDistanceChunks;
    }

    public static boolean equals(Object obj, Object obj2) {
        return obj == obj2 ? true : (obj == null ? false : obj.equals(obj2));
    }

    public static boolean equalsOne(int n, int[] ints) {
        for (int i = 0; i < ints.length; i++) {
            if (ints[i] == n) {
                return true;
            }
        }

        return false;
    }

    public static String normalize(String str) {
        return str == null ? "" : str;
    }

    public static void checkDisplaySettings() {
        int antialiasingLevel = getAntialiasingLevel();
        if (antialiasingLevel > 0) {
            DisplayMode displayMode = Display.getDisplayMode();
            dbg("FSAA Samples: " + antialiasingLevel);

            try {
                Display.destroy();
                Display.setDisplayMode(displayMode);
                Display.create(new PixelFormat().withDepthBits(24).withSamples(antialiasingLevel));
                Display.setResizable(false);
                Display.setResizable(true);
            } catch (LWJGLException e) {
                warn("Error setting FSAA: " + antialiasingLevel + "x");
                e.printStackTrace();

                try {
                    Display.setDisplayMode(displayMode);
                    Display.create(new PixelFormat().withDepthBits(24));
                    Display.setResizable(false);
                    Display.setResizable(true);
                } catch (LWJGLException e2) {
                    e2.printStackTrace();

                    try {
                        Display.setDisplayMode(displayMode);
                        Display.create();
                        Display.setResizable(false);
                        Display.setResizable(true);
                    } catch (LWJGLException e3) {
                        e3.printStackTrace();
                    }
                }
            }

            if (Util.getOSType() != EnumOS.OSX) {
                try {
                    File file = new File(minecraft.mcDataDir, "assets");
                    ByteBuffer readIconImage2 = readIconImage(new File(file, "/icons/icon_16x16.png"));
                    ByteBuffer readIconImage3 = readIconImage(new File(file, "/icons/icon_32x32.png"));
                    ByteBuffer[] byteBuffers = new ByteBuffer[]{readIconImage2, readIconImage3};
                    Display.setIcon(byteBuffers);
                } catch (IOException e) {
                    warn(e.getClass().getName() + ": " + e.getMessage());
                }
            }
        }
    }

    public static ByteBuffer readIconImage(File file) throws IOException {
        BufferedImage read = ImageIO.read(file);
        int[] rGB = read.getRGB(0, 0, read.getWidth(), read.getHeight(), (int[])null, 0, read.getWidth());
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

    public static void checkDisplayMode() {
        try {
            if (minecraft.isFullScreen()) {
                if (fullscreenModeChecked) {
                    return;
                }

                fullscreenModeChecked = true;
                desktopModeChecked = false;
                DisplayMode displayMode = Display.getDisplayMode();
                Dimension fullscreenDimension = getFullscreenDimension();
                if (fullscreenDimension == null) {
                    return;
                }

                if (displayMode.getWidth() == fullscreenDimension.width && displayMode.getHeight() == fullscreenDimension.height) {
                    return;
                }

                DisplayMode displayMode2 = getDisplayMode(fullscreenDimension);
                if (displayMode2 == null) {
                    return;
                }

                Display.setDisplayMode(displayMode2);
                minecraft.displayWidth = Display.getDisplayMode().getWidth();
                minecraft.displayHeight = Display.getDisplayMode().getHeight();
                if (minecraft.displayWidth <= 0) {
                    minecraft.displayWidth = 1;
                }

                if (minecraft.displayHeight <= 0) {
                    minecraft.displayHeight = 1;
                }

                if (minecraft.currentScreen != null) {
                    ScaledResolution scaledResolution = new ScaledResolution(minecraft, minecraft.displayWidth, minecraft.displayHeight);
                    int scaledWidth = scaledResolution.getScaledWidth();
                    int scaledHeight = scaledResolution.getScaledHeight();
                    minecraft.currentScreen.setWorldAndResolution(minecraft, scaledWidth, scaledHeight);
                }

                minecraft.loadingScreen = new LoadingScreenRenderer(minecraft);
                updateFramebufferSize();
                Display.setFullscreen(true);
                minecraft.gameSettings.updateVSync();
                GL11.glEnable(3553);
            } else {
                if (desktopModeChecked) {
                    return;
                }

                desktopModeChecked = true;
                fullscreenModeChecked = false;
                minecraft.gameSettings.updateVSync();
                Display.update();
                GL11.glEnable(3553);
                if (Util.getOSType() == EnumOS.WINDOWS) {
                    Display.setResizable(false);
                    Display.setResizable(true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            gameSettings.ofFullscreenMode = "Default";
            gameSettings.saveOfOptions();
        }
    }

    public static void updateFramebufferSize() {
        minecraft.getFramebuffer().createBindFramebuffer(minecraft.displayWidth, minecraft.displayHeight);
        if (minecraft.entityRenderer != null) {
            minecraft.entityRenderer.updateShaderGroupSize(minecraft.displayWidth, minecraft.displayHeight);
        }
    }

    public static Object[] addObjectToArray(Object[] objs, Object obj) {
        if (objs == null) {
            throw new NullPointerException("The given array is NULL");
        }

        int length = objs.length;
        int n = length + 1;
        Object[] newInstance = (Object[])Array.newInstance(objs.getClass().getComponentType(), n);
        System.arraycopy(objs, 0, newInstance, 0, length);
        newInstance[length] = obj;
        return newInstance;
    }

    public static Object[] addObjectToArray(Object[] objs, Object obj, int n) {
        ArrayList list = new ArrayList<>(Arrays.asList(objs));
        list.add(n, obj);
        Object[] newInstance = (Object[])Array.newInstance(objs.getClass().getComponentType(), list.size());
        return list.toArray(newInstance);
    }

    public static Object[] addObjectsToArray(Object[] objs, Object[] objs2) {
        if (objs == null) {
            throw new NullPointerException("The given array is NULL");
        }

        if (objs2.length == 0) {
            return objs;
        }

        int length = objs.length;
        int n = length + objs2.length;
        Object[] newInstance = (Object[])Array.newInstance(objs.getClass().getComponentType(), n);
        System.arraycopy(objs, 0, newInstance, 0, length);
        System.arraycopy(objs2, 0, newInstance, length, objs2.length);
        return newInstance;
    }

    public static boolean isCustomItems() {
        return false;
    }

    public static void drawFps() {
        String debug = minecraft.debug;
        int indexOf = debug.indexOf(32);
        if (indexOf < 0) {
            indexOf = 0;
        }

        String substring = debug.substring(0, indexOf);
        String updates = getUpdates(minecraft.debug);
        int countActiveRenderers = minecraft.renderGlobal.getCountActiveRenderers();
        int countEntitiesRendered = minecraft.renderGlobal.getCountEntitiesRendered();
        int countTileEntitiesRendered = minecraft.renderGlobal.getCountTileEntitiesRendered();
        String str = substring + " fps, C: " + countActiveRenderers + ", E: " + countEntitiesRendered + "+" + countTileEntitiesRendered + ", U: " + updates;
        minecraft.fontRendererObj.drawString(str, 2, 2, -2039584);
    }

    public static String getUpdates(String str) {
        int indexOf = str.indexOf(", ");
        if (indexOf < 0) {
            return "";
        }

        indexOf += 2;
        int indexOf2 = str.indexOf(32, indexOf);
        return indexOf2 < 0 ? "" : str.substring(indexOf, indexOf2);
    }

    public static int getBitsOs() {
        String getenv = System.getenv("ProgramFiles(X86)");
        return getenv != null ? 64 : 32;
    }

    public static int getBitsJre() {
        String[] strs = new String[]{"sun.arch.data.model", "com.ibm.vm.bitmode", "os.arch"};

        for (int i = 0; i < strs.length; i++) {
            String str = strs[i];
            String property = System.getProperty(str);
            if (property != null && property.contains("64")) {
                return 64;
            }
        }

        return 32;
    }

    public static boolean isNotify64BitJava() {
        return notify64BitJava;
    }

    public static void setNotify64BitJava(boolean flag) {
        notify64BitJava = flag;
    }

    public static boolean isConnectedModels() {
        return false;
    }

    public static void showGuiMessage(String str, String str2) throws IOException {
        GuiMessage guiMessage = new GuiMessage(minecraft.currentScreen, str, str2);
        minecraft.displayGuiScreen(guiMessage);
    }

    public static int[] addIntToArray(int[] ints, int n) {
        return addIntsToArray(ints, new int[]{n});
    }

    public static int[] addIntsToArray(int[] ints, int[] ints2) {
        if (ints != null && ints2 != null) {
            int length = ints.length;
            int n = length + ints2.length;
            int[] ints3 = new int[n];
            System.arraycopy(ints, 0, ints3, 0, length);

            for (int i = 0; i < ints2.length; i++) {
                ints3[i + length] = ints2[i];
            }

            return ints3;
        } else {
            throw new NullPointerException("The given array is NULL");
        }
    }

    public static DynamicTexture getMojangLogoTexture(DynamicTexture dynamicTexture) {
        try {
            ResourceLocation resourceLocation = new ResourceLocation("textures/gui/title/mojang.png");
            InputStream resourceStream = getResourceStream(resourceLocation);
            if (resourceStream == null) {
                return dynamicTexture;
            }

            BufferedImage read = ImageIO.read(resourceStream);
            return read == null ? dynamicTexture : new DynamicTexture(read);
        } catch (Exception e) {
            warn(e.getClass().getName() + ": " + e.getMessage());
            return dynamicTexture;
        }
    }

    public static void writeFile(File file, String str) throws java.io.FileNotFoundException, java.io.UnsupportedEncodingException, IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        byte[] bytes = str.getBytes("ASCII");
        fileOutputStream.write(bytes);
        fileOutputStream.close();
    }

    public static boolean isDynamicFov() {
        return gameSettings.ofDynamicFov;
    }

    public static TextureMap getTextureMap() {
        return getMinecraft().getTextureMapBlocks();
    }

    public static boolean isDynamicLights() {
        return gameSettings.ofDynamicLights != 3;
    }

    public static boolean isDynamicLightsFast() {
        return gameSettings.ofDynamicLights == 1;
    }

    public static boolean isDynamicHandLight() {
        return !isDynamicLights() ? false : (isShaders() ? Shaders.isDynamicHandLight() : true);
    }

    public static int[] toPrimitive(Integer[] integers) {
        if (integers == null) {
            return null;
        }

        if (integers.length == 0) {
            return new int[0];
        }

        int[] ints = new int[integers.length];

        for (int i = 0; i < ints.length; i++) {
            ints[i] = integers[i];
        }

        return ints;
    }

    public static boolean equals(Object obj, Object[] objs) {
        if (objs == null) {
            return false;
        }

        for (int i = 0; i < objs.length; i++) {
            Object obj2 = objs[i];
            if (equals(obj, obj2)) {
                return true;
            }
        }

        return false;
    }

    public static boolean checkObject(Object obj, Object[] objs) {
        if (objs == null) {
            return false;
        }

        for (int i = 0; i < objs.length; i++) {
            Object obj2 = objs[i];
            if (obj == obj2) {
                return true;
            }
        }

        return false;
    }

    public static boolean check() {
        return gameSettings.ofAnimatedTerrain;
    }

    public static boolean check2() {
        return gameSettings.ofAnimatedLava != 2;
    }

    public static boolean check3() {
        return gameSettings.ofAnimatedLava == 1;
    }

    public static boolean check4() {
        return gameSettings.ofAnimatedWater == 1;
    }

    public static boolean check5() {
        return gameSettings.mipmapLevels > 0;
    }

    public static int getIntValue() {
        if (minecraft.debug == text) {
            return intValue;
        }

        text = minecraft.debug;
        FrameTimer twitchStream = minecraft.getTwitchStream();
        long[] frames = twitchStream.getFrames();
        int index = twitchStream.getIndex();
        int lastIndex = twitchStream.getLastIndex();
        if (index == lastIndex) {
            return intValue;
        }

        int debugFPS = Minecraft.debugFPS;
        if (debugFPS <= 0) {
            debugFPS = 1;
        }

        long l = (long)(1.0 / debugFPS * 1.0E9);
        long l2 = l;
        long l3 = 0L;

        for (int floorMod = MathHelper.floorMod(index - 1, frames.length); floorMod != lastIndex && l3 < 1.0E9; floorMod = MathHelper.floorMod(floorMod - 1, frames.length)) {
            long l4 = frames[floorMod];
            if (l4 > l2) {
                l2 = l4;
            }

            l3 += l4;
        }

        double d = l2 / 1.0E9;
        intValue = (int)(1.0 / d);
        return intValue;
    }

    public static boolean check6() {
        return gameSettings.ofAnimatedFire;
    }
}
