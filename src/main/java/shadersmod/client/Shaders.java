package shadersmod.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RendererLivingEntity;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.optifine.Config;
import net.optifine.CustomColorizer;
import net.optifine.GlStateManager;
import net.optifine.Lang;
import net.optifine.PropertiesOrdered;
import net.optifine.Reflector;
import net.optifine.StrUtils;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;
import shadersmod.common.SMCLog;
import shadersmod.uniform.CustomUniforms;
import shadersmod.uniform.LegacyUniforms;
import shadersmod.uniform.ShaderUniformFloat4;
import shadersmod.uniform.ShaderUniformInt;
import shadersmod.uniform.Smoother;

public class Shaders {
    public static Minecraft mc;
    public static EntityRenderer entityRenderer;
    public static boolean isInitializedOnce = false;
    public static boolean isShaderPackInitialized = false;
    public static ContextCapabilities capabilities;
    public static String glVersionString;
    public static String glVendorString;
    public static String glRendererString;
    public static boolean hasGlGenMipmap = false;
    public static boolean hasForge = false;
    public static int numberResetDisplayList = 0;
    public static boolean needResetModels = false;
    public static int renderDisplayWidth = 0;
    public static int renderDisplayHeight = 0;
    public static int renderWidth = 0;
    public static int renderHeight = 0;
    public static boolean isRenderingWorld = false;
    public static boolean isRenderingSky = false;
    public static boolean isCompositeRendered = false;
    public static boolean isRenderingDfb = false;
    public static boolean isShadowPass = false;
    public static boolean isSleeping;
    public static boolean isRenderingFirstPersonHand;
    public static boolean isHandRenderedMain;
    public static boolean renderItemKeepDepthMask = false;
    public static boolean itemToRenderMainTranslucent = false;
    public static float[] sunPosition = new float[4];
    public static float[] moonPosition = new float[4];
    public static float[] shadowLightPosition = new float[4];
    public static float[] upPosition = new float[4];
    public static float[] shadowLightPositionVector = new float[4];
    public static float[] upPosModelView = new float[]{0.0F, 100.0F, 0.0F, 0.0F};
    public static float[] sunPosModelView = new float[]{0.0F, 100.0F, 0.0F, 0.0F};
    public static float[] moonPosModelView = new float[]{0.0F, -100.0F, 0.0F, 0.0F};
    public static float[] tempMat = new float[16];
    public static float clearColorR;
    public static float clearColorG;
    public static float clearColorB;
    public static float skyColorR;
    public static float skyColorG;
    public static float skyColorB;
    public static long worldTime = 0L;
    public static long lastWorldTime = 0L;
    public static long diffWorldTime = 0L;
    public static float celestialAngle = 0.0F;
    public static float sunAngle = 0.0F;
    public static float shadowAngle = 0.0F;
    public static int moonPhase = 0;
    public static long systemTime = 0L;
    public static long lastSystemTime = 0L;
    public static long diffSystemTime = 0L;
    public static int frameCounter = 0;
    public static float frameTime = 0.0F;
    public static float frameTimeCounter = 0.0F;
    public static int systemTimeInt32 = 0;
    public static float rainStrength = 0.0F;
    public static float wetness = 0.0F;
    public static float wetnessHalfLife = 600.0F;
    public static float drynessHalfLife = 200.0F;
    public static float eyeBrightnessHalflife = 10.0F;
    public static boolean usewetness = false;
    public static int isEyeInWater = 0;
    public static int eyeBrightness = 0;
    public static float eyeBrightnessFadeX = 0.0F;
    public static float eyeBrightnessFadeY = 0.0F;
    public static float eyePosY = 0.0F;
    public static float centerDepth = 0.0F;
    public static float centerDepthSmooth = 0.0F;
    public static float centerDepthSmoothHalflife = 1.0F;
    public static boolean centerDepthSmoothEnabled = false;
    public static int superSamplingLevel = 1;
    public static float nightVision = 0.0F;
    public static float blindness = 0.0F;
    public static boolean updateChunksErrorRecorded = false;
    public static boolean lightmapEnabled = false;
    public static boolean fogEnabled = true;
    public static int entityAttrib = 10;
    public static int midTexCoordAttrib = 11;
    public static int tangentAttrib = 12;
    public static boolean useEntityAttrib = false;
    public static boolean useMidTexCoordAttrib = false;
    public static boolean useMultiTexCoord3Attrib = false;
    public static boolean useTangentAttrib = false;
    public static boolean progUseEntityAttrib = false;
    public static boolean progUseMidTexCoordAttrib = false;
    public static boolean progUseTangentAttrib = false;
    public static int atlasSizeX = 0;
    public static int atlasSizeY = 0;
    public static boolean useEntityColor = true;
    public static ShaderUniformFloat4 uniformEntityColor = new ShaderUniformFloat4("entityColor");
    public static ShaderUniformInt uniformEntityId = new ShaderUniformInt("entityId");
    public static ShaderUniformInt uniformBlockEntityId = new ShaderUniformInt("blockEntityId");
    public static double previousCameraPositionX;
    public static double previousCameraPositionY;
    public static double previousCameraPositionZ;
    public static double cameraPositionX;
    public static double cameraPositionY;
    public static double cameraPositionZ;
    public static int shadowPassInterval = 0;
    public static boolean needResizeShadow = false;
    public static int shadowMapWidth = 1024;
    public static int shadowMapHeight = 1024;
    public static int spShadowMapWidth = 1024;
    public static int spShadowMapHeight = 1024;
    public static float shadowMapFOV = 90.0F;
    public static float shadowMapHalfPlane = 160.0F;
    public static boolean shadowMapIsOrtho = true;
    public static float shadowDistanceRenderMul = -1.0F;
    public static int shadowPassCounter = 0;
    public static int preShadowPassThirdPersonView;
    public static boolean shouldSkipDefaultShadow = false;
    public static boolean waterShadowEnabled = false;
    public static int usedColorBuffers = 0;
    public static int usedDepthBuffers = 0;
    public static int usedShadowColorBuffers = 0;
    public static int usedShadowDepthBuffers = 0;
    public static int usedColorAttachs = 0;
    public static int usedDrawBuffers = 0;
    public static int dfb = 0;
    public static int sfb = 0;
    public static int[] gbuffersFormat = new int[8];
    public static boolean[] gbuffersClear = new boolean[8];
    public static int activeProgram = 0;
    public static final int ProgramSkyBasic = 4;
    public static final int ProgramSkyTextured = 5;
    public static final int ProgramClouds = 6;
    public static final int ProgramTerrain = 7;
    public static final int ProgramTerrainCutoutMip = 9;
    public static final int ProgramTerrainCutout = 10;
    public static final int ProgramDamagedBlock = 11;
    public static final int ProgramWater = 12;
    public static final int ProgramBlock = 13;
    public static final int ProgramBeaconBeam = 14;
    public static final int ProgramItem = 15;
    public static final int ProgramEntities = 16;
    public static final int ProgramArmorGlint = 17;
    public static final int ProgramSpiderEyes = 18;
    public static final int ProgramHand = 19;
    public static final int ProgramWeather = 20;
    public static final int ProgramComposite = 21;
    public static final int ProgramComposite1 = 22;
    public static final int ProgramComposite2 = 23;
    public static final int ProgramComposite3 = 24;
    public static final int ProgramComposite4 = 25;
    public static final int ProgramComposite5 = 26;
    public static final int ProgramComposite6 = 27;
    public static final int ProgramComposite7 = 28;
    public static final int ProgramFinal = 29;
    public static final int ProgramShadow = 30;
    public static final int ProgramShadowSolid = 31;
    public static final int ProgramShadowCutout = 32;
    public static final int ProgramDeferred = 33;
    public static final int ProgramDeferred1 = 34;
    public static final int ProgramDeferred2 = 35;
    public static final int ProgramDeferred3 = 36;
    public static final int ProgramDeferred4 = 37;
    public static final int ProgramDeferred5 = 38;
    public static final int ProgramDeferred6 = 39;
    public static final int ProgramDeferred7 = 40;
    public static final int ProgramHandWater = 41;
    public static final int ProgramDeferredLast = 42;
    public static final int ProgramCompositeLast = 43;
    public static final int ProgramCount = 44;
    public static String[] programNames = new String[]{
        "",
        "gbuffers_basic",
        "gbuffers_textured",
        "gbuffers_textured_lit",
        "gbuffers_skybasic",
        "gbuffers_skytextured",
        "gbuffers_clouds",
        "gbuffers_terrain",
        "gbuffers_terrain_solid",
        "gbuffers_terrain_cutout_mip",
        "gbuffers_terrain_cutout",
        "gbuffers_damagedblock",
        "gbuffers_water",
        "gbuffers_block",
        "gbuffers_beaconbeam",
        "gbuffers_item",
        "gbuffers_entities",
        "gbuffers_armor_glint",
        "gbuffers_spidereyes",
        "gbuffers_hand",
        "gbuffers_weather",
        "composite",
        "composite1",
        "composite2",
        "composite3",
        "composite4",
        "composite5",
        "composite6",
        "composite7",
        "final",
        "shadow",
        "shadow_solid",
        "shadow_cutout",
        "deferred",
        "deferred1",
        "deferred2",
        "deferred3",
        "deferred4",
        "deferred5",
        "deferred6",
        "deferred7",
        "gbuffers_hand_water",
        "deferred_last",
        "composite_last"
    };
    public static int[] programBackups = new int[]{
        0, 0, 1, 2, 1, 2, 2, 3, 7, 7, 7, 7, 7, 7, 2, 3, 3, 2, 2, 3, 3, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 30, 30, 0, 0, 0, 0, 0, 0, 0, 0, 19, 0, 0
    };
    public static int[] programsID = new int[44];
    public static int[] programsRef = new int[44];
    public static int programIDCopyDepth = 0;
    public static boolean hasDeferredPrograms = false;
    public static String[] programsDrawBufSettings = new String[44];
    public static String newDrawBufSetting = null;
    public static IntBuffer[] programsDrawBuffers = new IntBuffer[44];
    public static IntBuffer activeDrawBuffers = null;
    public static String[] programsColorAtmSettings = new String[44];
    public static String newColorAtmSetting = null;
    public static String activeColorAtmSettings = null;
    public static int[] programsCompositeMipmapSetting = new int[44];
    public static int newCompositeMipmapSetting = 0;
    public static int activeCompositeMipmapSetting = 0;
    public static Properties loadedShaders = null;
    public static Properties shadersConfig = null;
    public static ITextureObject defaultTexture = null;
    public static boolean normalMapEnabled = false;
    public static boolean[] shadowHardwareFilteringEnabled = new boolean[2];
    public static boolean[] shadowMipmapEnabled = new boolean[2];
    public static boolean[] shadowFilterNearest = new boolean[2];
    public static boolean[] shadowColorMipmapEnabled = new boolean[8];
    public static boolean[] shadowColorFilterNearest = new boolean[8];
    public static boolean configTweakBlockDamage = false;
    public static boolean configCloudShadow = false;
    public static float configHandDepthMul = 0.125F;
    public static float configRenderResMul = 1.0F;
    public static float configShadowResMul = 1.0F;
    public static int configTexMinFilB = 0;
    public static int configTexMinFilN = 0;
    public static int configTexMinFilS = 0;
    public static int configTexMagFilB = 0;
    public static int configTexMagFilN = 0;
    public static int configTexMagFilS = 0;
    public static boolean configShadowClipFrustrum = true;
    public static boolean configNormalMap = true;
    public static boolean configSpecularMap = true;
    public static PropertyDefaultTrueFalse configOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
    public static PropertyDefaultTrueFalse configOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light", 0);
    public static int configAntialiasingLevel = 0;
    public static String[] texMinFilDesc = new String[]{"Nearest", "Nearest-Nearest", "Nearest-Linear"};
    public static String[] texMagFilDesc = new String[]{"Nearest", "Linear"};
    public static int[] texMinFilValue = new int[]{9728, 9984, 9986};
    public static int[] texMagFilValue = new int[]{9728, 9729};
    public static IShaderPack shaderPack = null;
    public static boolean shaderPackLoaded = false;
    public static File currentshader;
    public static String currentshadername;
    public static String packNameNone = "OFF";
    public static String packNameDefault = "(internal)";
    public static String shaderpacksdirname = "shaderpacks";
    public static String optionsfilename = "optionsshaders.txt";
    public static File shadersdir = new File(Minecraft.getMinecraft().mcDataDir, "shaders");
    public static File shaderpacksdir = new File(Minecraft.getMinecraft().mcDataDir, shaderpacksdirname);
    public static File configFile = new File(Minecraft.getMinecraft().mcDataDir, optionsfilename);
    public static ShaderOption[] shaderPackOptions = null;
    public static Set<String> shaderPackOptionSliders = null;
    public static ShaderProfile[] shaderPackProfiles = null;
    public static Map<String, ScreenShaderOptions> shaderPackGuiScreens = null;
    public static PropertyDefaultFastFancyOff shaderPackClouds = new PropertyDefaultFastFancyOff("clouds", "Clouds", 0);
    public static PropertyDefaultTrueFalse shaderPackOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
    public static PropertyDefaultTrueFalse shaderPackOldHandLight = new PropertyDefaultTrueFalse("oldHandLight", "Old Hand Light", 0);
    public static PropertyDefaultTrueFalse shaderPackDynamicHandLight = new PropertyDefaultTrueFalse("dynamicHandLight", "Dynamic Hand Light", 0);
    public static PropertyDefaultTrueFalse shaderPackShadowTranslucent = new PropertyDefaultTrueFalse("shadowTranslucent", "Shadow Translucent", 0);
    public static PropertyDefaultTrueFalse shaderPackUnderwaterOverlay = new PropertyDefaultTrueFalse("underwaterOverlay", "Underwater Overlay", 0);
    public static PropertyDefaultTrueFalse shaderPackSun = new PropertyDefaultTrueFalse("sun", "Sun", 0);
    public static PropertyDefaultTrueFalse shaderPackMoon = new PropertyDefaultTrueFalse("moon", "Moon", 0);
    public static PropertyDefaultTrueFalse shaderPackVignette = new PropertyDefaultTrueFalse("vignette", "Vignette", 0);
    public static PropertyDefaultTrueFalse shaderPackBackFaceSolid = new PropertyDefaultTrueFalse("backFace.solid", "Back-face Solid", 0);
    public static PropertyDefaultTrueFalse shaderPackBackFaceCutout = new PropertyDefaultTrueFalse("backFace.cutout", "Back-face Cutout", 0);
    public static PropertyDefaultTrueFalse shaderPackBackFaceCutoutMipped = new PropertyDefaultTrueFalse("backFace.cutoutMipped", "Back-face Cutout Mipped", 0);
    public static PropertyDefaultTrueFalse shaderPackBackFaceTranslucent = new PropertyDefaultTrueFalse("backFace.translucent", "Back-face Translucent", 0);
    public static Map<String, String> shaderPackResources = new HashMap<>();
    public static World currentWorld = null;
    public static List<Integer> shaderPackDimensions = new ArrayList<>();
    public static CustomTexture[] customTexturesGbuffers = null;
    public static CustomTexture[] customTexturesComposite = null;
    public static CustomTexture[] customTexturesDeferred = null;
    public static String noiseTexturePath = null;
    public static CustomUniforms customUniforms = null;
    public static String[] STAGE_NAMES = new String[]{"gbuffers", "composite", "deferred"};
    public static boolean saveFinalShaders = System.getProperty("shaders.debug.save", "false").equals("true");
    public static float blockLightLevel05 = 0.5F;
    public static float blockLightLevel06 = 0.6F;
    public static float blockLightLevel08 = 0.8F;
    public static float aoLevel = -1.0F;
    public static float sunPathRotation = 0.0F;
    public static float shadowAngleInterval = 0.0F;
    public static int fogMode = 0;
    public static float fogColorR;
    public static float fogColorG;
    public static float fogColorB;
    public static float shadowIntervalSize = 2.0F;
    public static int terrainIconSize = 16;
    public static int[] terrainTextureSize = new int[2];
    public static ICustomTexture noiseTexture;
    public static boolean noiseTextureEnabled = false;
    public static int noiseTextureResolution = 256;
    public static int[] dfbColorTexturesA = new int[16];
    public static int[] colorTexturesToggle = new int[8];
    public static int[] colorTextureTextureImageUnit = new int[]{0, 1, 2, 3, 7, 8, 9, 10};
    public static boolean[][] programsToggleColorTextures = new boolean[44][8];
    public static final int bigBufferSize = 2548;
    public static ByteBuffer bigBuffer = (ByteBuffer)((Buffer)BufferUtils.createByteBuffer(2548)).limit(0);
    public static float[] faProjection = new float[16];
    public static float[] faProjectionInverse = new float[16];
    public static float[] faModelView = new float[16];
    public static float[] faModelViewInverse = new float[16];
    public static float[] faShadowProjection = new float[16];
    public static float[] faShadowProjectionInverse = new float[16];
    public static float[] faShadowModelView = new float[16];
    public static float[] faShadowModelViewInverse = new float[16];
    public static FloatBuffer projection = nextFloatBuffer(16);
    public static FloatBuffer projectionInverse = nextFloatBuffer(16);
    public static FloatBuffer modelView = nextFloatBuffer(16);
    public static FloatBuffer modelViewInverse = nextFloatBuffer(16);
    public static FloatBuffer shadowProjection = nextFloatBuffer(16);
    public static FloatBuffer shadowProjectionInverse = nextFloatBuffer(16);
    public static FloatBuffer shadowModelView = nextFloatBuffer(16);
    public static FloatBuffer shadowModelViewInverse = nextFloatBuffer(16);
    public static FloatBuffer previousProjection = nextFloatBuffer(16);
    public static FloatBuffer previousModelView = nextFloatBuffer(16);
    public static FloatBuffer tempMatrixDirectBuffer = nextFloatBuffer(16);
    public static FloatBuffer tempDirectFloatBuffer = nextFloatBuffer(16);
    public static IntBuffer dfbColorTextures = nextIntBuffer(16);
    public static IntBuffer dfbDepthTextures = nextIntBuffer(3);
    public static IntBuffer sfbColorTextures = nextIntBuffer(8);
    public static IntBuffer sfbDepthTextures = nextIntBuffer(2);
    public static IntBuffer dfbDrawBuffers = nextIntBuffer(8);
    public static IntBuffer sfbDrawBuffers = nextIntBuffer(8);
    public static IntBuffer drawBuffersNone = nextIntBuffer(8);
    public static IntBuffer drawBuffersAll = nextIntBuffer(8);
    public static IntBuffer drawBuffersClear0 = nextIntBuffer(8);
    public static IntBuffer drawBuffersClear1 = nextIntBuffer(8);
    public static IntBuffer drawBuffersClearColor = nextIntBuffer(8);
    public static IntBuffer drawBuffersColorAtt0 = nextIntBuffer(8);
    public static IntBuffer[] drawBuffersBuffer = nextIntBufferArray(44, 8);
    public static Map<Block, Integer> mapBlockToEntityData;
    public static String[] formatNames = new String[]{
        "R8",
        "RG8",
        "RGB8",
        "RGBA8",
        "R8_SNORM",
        "RG8_SNORM",
        "RGB8_SNORM",
        "RGBA8_SNORM",
        "R16",
        "RG16",
        "RGB16",
        "RGBA16",
        "R16_SNORM",
        "RG16_SNORM",
        "RGB16_SNORM",
        "RGBA16_SNORM",
        "R16F",
        "RG16F",
        "RGB16F",
        "RGBA16F",
        "R32F",
        "RG32F",
        "RGB32F",
        "RGBA32F",
        "R32I",
        "RG32I",
        "RGB32I",
        "RGBA32I",
        "R32UI",
        "RG32UI",
        "RGB32UI",
        "RGBA32UI",
        "R3_G3_B2",
        "RGB5_A1",
        "RGB10_A2",
        "R11F_G11F_B10F",
        "RGB9_E5"
    };
    public static int[] formatIds = new int[]{
        33321,
        33323,
        32849,
        32856,
        36756,
        36757,
        36758,
        36759,
        33322,
        33324,
        32852,
        32859,
        36760,
        36761,
        36762,
        36763,
        33325,
        33327,
        34843,
        34842,
        33326,
        33328,
        34837,
        34836,
        33333,
        33339,
        36227,
        36226,
        33334,
        33340,
        36209,
        36208,
        10768,
        32855,
        32857,
        35898,
        35901
    };
    public static Pattern patternLoadEntityDataMap = Pattern.compile("\\s*([\\w:]+)\\s*=\\s*([-]?\\d+)\\s*");
    public static int[] entityData = new int[32];
    public static int entityDataIndex = 0;
    public static final int CONSTANT_8 = 8;
    public static final int CONSTANT_2 = 2;
    public static final int CONSTANT_1 = 1;
    public static final boolean CONSTANT_1_2 = true;
    public static final int CONSTANT_8_2 = 8;
    public static final int CONSTANT_3 = 3;
    public static final int CONSTANT_2_2 = 2;
    public static final int CONSTANT_8_3 = 8;
    public static final int CONSTANT_2_3 = 2;
    public static final int CONSTANT_1_3 = 1;
    public static final int CONSTANT_8_4 = 8;
    public static final int CONSTANT_2_4 = 2;
    public static final int CONSTANT_3_2 = 3;
    public static final int CONSTANT_8_5 = 8;
    public static final int CONSTANT_8_6 = 8;
    public static final int CONSTANT_3_3 = 3;
    public static final int CONSTANT_0 = 0;
    public static final int CONSTANT_0_2 = 0;
    public static final boolean CONSTANT_1_4 = true;

    public static ByteBuffer nextByteBuffer(int n) {
        ByteBuffer bigBuffer2 = bigBuffer;
        int limit = bigBuffer2.limit();
        ((Buffer)bigBuffer2).position(limit).limit(limit + n);
        return bigBuffer2.slice();
    }

    public static IntBuffer nextIntBuffer(int n) {
        ByteBuffer bigBuffer2 = bigBuffer;
        int limit = bigBuffer2.limit();
        ((Buffer)bigBuffer2).position(limit).limit(limit + n * 4);
        return bigBuffer2.asIntBuffer();
    }

    public static FloatBuffer nextFloatBuffer(int n) {
        ByteBuffer bigBuffer2 = bigBuffer;
        int limit = bigBuffer2.limit();
        ((Buffer)bigBuffer2).position(limit).limit(limit + n * 4);
        return bigBuffer2.asFloatBuffer();
    }

    public static IntBuffer[] nextIntBufferArray(int n, int n2) {
        IntBuffer[] intBuffers = new IntBuffer[n];

        for (int i = 0; i < n; i++) {
            intBuffers[i] = nextIntBuffer(n2);
        }

        return intBuffers;
    }

    public static void loadConfig() {
        SMCLog.info("Load ShadersMod configuration.");

        try {
            if (!shaderpacksdir.exists()) {
                shaderpacksdir.mkdir();
            }
        } catch (Exception e) {
            SMCLog.severe("Failed to open the shaderpacks directory: " + shaderpacksdir);
        }

        shadersConfig = new PropertiesOrdered();
        shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), "");
        if (configFile.exists()) {
            try {
                FileReader fileReader = new FileReader(configFile);
                shadersConfig.load(fileReader);
                fileReader.close();
            } catch (Exception e) {
            }
        }

        if (!configFile.exists()) {
            try {
                storeConfig();
            } catch (Exception e) {
            }
        }

        EnumShaderOption[] values = EnumShaderOption.values();

        for (int i = 0; i < values.length; i++) {
            EnumShaderOption enumShaderOption = values[i];
            String propertyKey = enumShaderOption.getPropertyKey();
            String valueDefault = enumShaderOption.getValueDefault();
            String property = shadersConfig.getProperty(propertyKey, valueDefault);
            setEnumShaderOption(enumShaderOption, property);
        }

        loadShaderPack();
    }

    public static void setEnumShaderOption(EnumShaderOption enumShaderOption, String str) {
        if (str == null) {
            str = enumShaderOption.getValueDefault();
        }

        switch (enumShaderOption) {
            case ANTIALIASING:
                configAntialiasingLevel = Config.parseInt(str, 0);
                break;
            case NORMAL_MAP:
                configNormalMap = Config.parseBoolean(str, true);
                break;
            case SPECULAR_MAP:
                configSpecularMap = Config.parseBoolean(str, true);
                break;
            case RENDER_RES_MUL:
                configRenderResMul = Config.parseFloat(str, 1.0F);
                break;
            case SHADOW_RES_MUL:
                configShadowResMul = Config.parseFloat(str, 1.0F);
                break;
            case HAND_DEPTH_MUL:
                configHandDepthMul = Config.parseFloat(str, 0.125F);
                break;
            case CLOUD_SHADOW:
                configCloudShadow = Config.parseBoolean(str, true);
                break;
            case OLD_HAND_LIGHT:
                configOldHandLight.setPropertyValue(str);
                break;
            case OLD_LIGHTING:
                configOldLighting.setPropertyValue(str);
                break;
            case SHADER_PACK:
                currentshadername = str;
                break;
            case TWEAK_BLOCK_DAMAGE:
                configTweakBlockDamage = Config.parseBoolean(str, true);
                break;
            case SHADOW_CLIP_FRUSTRUM:
                configShadowClipFrustrum = Config.parseBoolean(str, true);
                break;
            case TEX_MIN_FIL_B:
                configTexMinFilB = Config.parseInt(str, 0);
                break;
            case TEX_MIN_FIL_N:
                configTexMinFilN = Config.parseInt(str, 0);
                break;
            case TEX_MIN_FIL_S:
                configTexMinFilS = Config.parseInt(str, 0);
                break;
            case TEX_MAG_FIL_B:
                configTexMagFilB = Config.parseInt(str, 0);
                break;
            case TEX_MAG_FIL_N:
                configTexMagFilB = Config.parseInt(str, 0);
                break;
            case TEX_MAG_FIL_S:
                configTexMagFilB = Config.parseInt(str, 0);
                break;
            default:
                throw new IllegalArgumentException("Unknown option: " + enumShaderOption);
        }
    }

    public static void storeConfig() {
        SMCLog.info("Save ShadersMod configuration.");
        if (shadersConfig == null) {
            shadersConfig = new PropertiesOrdered();
        }

        EnumShaderOption[] values = EnumShaderOption.values();

        for (int i = 0; i < values.length; i++) {
            EnumShaderOption enumShaderOption = values[i];
            String propertyKey = enumShaderOption.getPropertyKey();
            String enumShaderOption2 = getEnumShaderOption(enumShaderOption);
            shadersConfig.setProperty(propertyKey, enumShaderOption2);
        }

        try {
            FileWriter fileWriter = new FileWriter(configFile);
            shadersConfig.store(fileWriter, (String)null);
            fileWriter.close();
        } catch (Exception e) {
            SMCLog.severe("Error saving configuration: " + e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public static String getEnumShaderOption(EnumShaderOption enumShaderOption) {
        switch (enumShaderOption) {
            case ANTIALIASING:
                return Integer.toString(configAntialiasingLevel);
            case NORMAL_MAP:
                return Boolean.toString(configNormalMap);
            case SPECULAR_MAP:
                return Boolean.toString(configSpecularMap);
            case RENDER_RES_MUL:
                return Float.toString(configRenderResMul);
            case SHADOW_RES_MUL:
                return Float.toString(configShadowResMul);
            case HAND_DEPTH_MUL:
                return Float.toString(configHandDepthMul);
            case CLOUD_SHADOW:
                return Boolean.toString(configCloudShadow);
            case OLD_HAND_LIGHT:
                return configOldHandLight.getPropertyValue();
            case OLD_LIGHTING:
                return configOldLighting.getPropertyValue();
            case SHADER_PACK:
                return currentshadername;
            case TWEAK_BLOCK_DAMAGE:
                return Boolean.toString(configTweakBlockDamage);
            case SHADOW_CLIP_FRUSTRUM:
                return Boolean.toString(configShadowClipFrustrum);
            case TEX_MIN_FIL_B:
                return Integer.toString(configTexMinFilB);
            case TEX_MIN_FIL_N:
                return Integer.toString(configTexMinFilN);
            case TEX_MIN_FIL_S:
                return Integer.toString(configTexMinFilS);
            case TEX_MAG_FIL_B:
                return Integer.toString(configTexMagFilB);
            case TEX_MAG_FIL_N:
                return Integer.toString(configTexMagFilB);
            case TEX_MAG_FIL_S:
                return Integer.toString(configTexMagFilB);
            default:
                throw new IllegalArgumentException("Unknown option: " + enumShaderOption);
        }
    }

    public static void setShaderPack(String str) {
        currentshadername = str;
        shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), str);
        loadShaderPack();
    }

    public static void loadShaderPack() {
        boolean shaderPackLoaded2 = shaderPackLoaded;
        boolean isOldLighting2 = isOldLighting();
        shaderPackLoaded = false;
        if (shaderPack != null) {
            shaderPack.close();
            shaderPack = null;
            shaderPackResources.clear();
            shaderPackDimensions.clear();
            shaderPackOptions = null;
            shaderPackOptionSliders = null;
            shaderPackProfiles = null;
            shaderPackGuiScreens = null;
            shaderPackClouds.resetValue();
            shaderPackOldHandLight.resetValue();
            shaderPackDynamicHandLight.resetValue();
            shaderPackOldLighting.resetValue();
            resetCustomTextures();
            noiseTexturePath = null;
        }

        boolean flag = false;
        if (Config.isAntialiasing()) {
            SMCLog.info("Shaders can not be loaded, Antialiasing is enabled: " + Config.getAntialiasingLevel() + "x");
            flag = true;
        }

        if (Config.isAnisotropicFiltering()) {
            SMCLog.info("Shaders can not be loaded, Anisotropic Filtering is enabled: " + Config.getAnisotropicFilterLevel() + "x");
            flag = true;
        }

        String property = shadersConfig.getProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), packNameDefault);
        if (!property.isEmpty() && !property.equals(packNameNone) && !flag) {
            if (property.equals(packNameDefault)) {
                shaderPack = new ShaderPackDefault();
                shaderPackLoaded = true;
            } else {
                try {
                    File file = new File(shaderpacksdir, property);
                    if (file.isDirectory()) {
                        shaderPack = new ShaderPackFolder(property, file);
                        shaderPackLoaded = true;
                    } else if (file.isFile() && property.toLowerCase().endsWith(".zip")) {
                        shaderPack = new ShaderPackZip(property, file);
                        shaderPackLoaded = true;
                    }
                } catch (Exception e) {
                }
            }
        }

        if (shaderPack != null) {
            SMCLog.info("Loaded shaderpack: " + getShaderPackName());
        } else {
            SMCLog.info("No shaderpack loaded.");
            shaderPack = new ShaderPackNone();
        }

        loadShaderPackResources();
        loadShaderPackDimensions();
        shaderPackOptions = loadShaderPackOptions();
        loadShaderPackProperties();
        boolean flag2 = shaderPackLoaded != shaderPackLoaded2;
        boolean flag3 = isOldLighting() != isOldLighting2;
        if ((flag2 || flag3) && mc.entityRenderer != null) {
            mc.entityRenderer.setFxaaShader(0);
            mc.refreshResources();
        }
    }

    public static void loadShaderPackDimensions() {
        shaderPackDimensions.clear();

        for (int i = -128; i <= 128; i++) {
            String str = "/shaders/world" + i;
            if (shaderPack.hasDirectory(str)) {
                shaderPackDimensions.add(i);
            }
        }

        if (shaderPackDimensions.size() > 0) {
            Integer[] toArray = shaderPackDimensions.toArray(new Integer[shaderPackDimensions.size()]);
            Config.dbg("[Shaders] Worlds: " + Config.arrayToString(toArray));
        }
    }

    public static void loadShaderPackProperties() {
        shaderPackClouds.resetValue();
        shaderPackOldHandLight.resetValue();
        shaderPackDynamicHandLight.resetValue();
        shaderPackOldLighting.resetValue();
        shaderPackShadowTranslucent.resetValue();
        shaderPackUnderwaterOverlay.resetValue();
        shaderPackSun.resetValue();
        shaderPackMoon.resetValue();
        shaderPackVignette.resetValue();
        shaderPackBackFaceSolid.resetValue();
        shaderPackBackFaceCutout.resetValue();
        shaderPackBackFaceCutoutMipped.resetValue();
        shaderPackBackFaceTranslucent.resetValue();
        BlockAliases.reset();
        customUniforms = null;
        LegacyUniforms.reset();
        if (shaderPack != null) {
            BlockAliases.update(shaderPack);
            String str = "/shaders/shaders.properties";

            try {
                InputStream resourceAsStream = shaderPack.getResourceAsStream(str);
                if (resourceAsStream == null) {
                    return;
                }

                PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
                propertiesOrdered.load(resourceAsStream);
                resourceAsStream.close();
                shaderPackClouds.loadFrom(propertiesOrdered);
                shaderPackOldHandLight.loadFrom(propertiesOrdered);
                shaderPackDynamicHandLight.loadFrom(propertiesOrdered);
                shaderPackOldLighting.loadFrom(propertiesOrdered);
                shaderPackShadowTranslucent.loadFrom(propertiesOrdered);
                shaderPackUnderwaterOverlay.loadFrom(propertiesOrdered);
                shaderPackSun.loadFrom(propertiesOrdered);
                shaderPackVignette.loadFrom(propertiesOrdered);
                shaderPackMoon.loadFrom(propertiesOrdered);
                shaderPackBackFaceSolid.loadFrom(propertiesOrdered);
                shaderPackBackFaceCutout.loadFrom(propertiesOrdered);
                shaderPackBackFaceCutoutMipped.loadFrom(propertiesOrdered);
                shaderPackBackFaceTranslucent.loadFrom(propertiesOrdered);
                shaderPackOptionSliders = ShaderPackParser.parseOptionSliders(propertiesOrdered, shaderPackOptions);
                shaderPackProfiles = ShaderPackParser.parseProfiles(propertiesOrdered, shaderPackOptions);
                shaderPackGuiScreens = ShaderPackParser.parseGuiScreens(propertiesOrdered, shaderPackProfiles, shaderPackOptions);
                customTexturesGbuffers = loadCustomTextures(propertiesOrdered, 0);
                customTexturesComposite = loadCustomTextures(propertiesOrdered, 1);
                customTexturesDeferred = loadCustomTextures(propertiesOrdered, 2);
                noiseTexturePath = propertiesOrdered.getProperty("texture.noise");
                if (noiseTexturePath != null) {
                    noiseTextureEnabled = true;
                }

                customUniforms = ShaderPackParser.parseCustomUniforms(propertiesOrdered);
            } catch (IOException e) {
                Config.warn("[Shaders] Error reading: " + str);
            }
        }
    }

    public static CustomTexture[] loadCustomTextures(Properties properties, int n) {
        String str = "texture." + STAGE_NAMES[n] + ".";
        Set keySet = properties.keySet();
        ArrayList list = new ArrayList();

        for (String str2 : (Iterable<String>)keySet) {
            if (str2.startsWith(str)) {
                String substring = str2.substring(str.length());
                String trim = properties.getProperty(str2).trim();
                int textureIndex = getTextureIndex(n, substring);
                if (textureIndex < 0) {
                    SMCLog.warning("Invalid texture name: " + str2);
                } else {
                    CustomTexture loadCustomTexture2 = loadCustomTexture(textureIndex, trim);
                    if (loadCustomTexture2 != null) {
                        list.add(loadCustomTexture2);
                    }
                }
            }
        }

        return list.size() <= 0 ? null : (CustomTexture[])list.toArray(new CustomTexture[list.size()]);
    }

    public static CustomTexture loadCustomTexture(int n, String str) {
        if (str == null) {
            return null;
        }

        str = str.trim();
        if (str.indexOf(46) < 0) {
            str = str + ".png";
        }

        try {
            String str2 = "shaders/" + StrUtils.removePrefix(str, "/");
            InputStream resourceAsStream = shaderPack.getResourceAsStream(str2);
            if (resourceAsStream == null) {
                SMCLog.warning("Texture not found: " + str);
                return null;
            } else {
                IOUtils.closeQuietly(resourceAsStream);
                SimpleShaderTexture simpleShaderTexture = new SimpleShaderTexture(str2);
                simpleShaderTexture.loadTexture(mc.getResourceManager());
                return new CustomTexture(n, str2, simpleShaderTexture);
            }
        } catch (IOException e) {
            SMCLog.warning("Error loading texture: " + str);
            SMCLog.warning("" + e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }

    public static int getTextureIndex(int n, String str) {
        if (n == 0) {
            if (str.equals("texture")) {
                return 0;
            }

            if (str.equals("lightmap")) {
                return 1;
            }

            if (str.equals("normals")) {
                return 2;
            }

            if (str.equals("specular")) {
                return 3;
            }

            if (str.equals("shadowtex0") || str.equals("watershadow")) {
                return 4;
            }

            if (str.equals("shadow")) {
                return waterShadowEnabled ? 5 : 4;
            }

            if (str.equals("shadowtex1")) {
                return 5;
            }

            if (str.equals("depthtex0")) {
                return 6;
            }

            if (str.equals("gaux1")) {
                return 7;
            }

            if (str.equals("gaux2")) {
                return 8;
            }

            if (str.equals("gaux3")) {
                return 9;
            }

            if (str.equals("gaux4")) {
                return 10;
            }

            if (str.equals("depthtex1")) {
                return 12;
            }

            if (str.equals("shadowcolor0") || str.equals("shadowcolor")) {
                return 13;
            }

            if (str.equals("shadowcolor1")) {
                return 14;
            }

            if (str.equals("noisetex")) {
                return 15;
            }
        }

        if (n == 1 || n == 2) {
            if (str.equals("colortex0") || str.equals("colortex0")) {
                return 0;
            }

            if (str.equals("colortex1") || str.equals("gdepth")) {
                return 1;
            }

            if (str.equals("colortex2") || str.equals("gnormal")) {
                return 2;
            }

            if (str.equals("colortex3") || str.equals("composite")) {
                return 3;
            }

            if (str.equals("shadowtex0") || str.equals("watershadow")) {
                return 4;
            }

            if (str.equals("shadow")) {
                return waterShadowEnabled ? 5 : 4;
            }

            if (str.equals("shadowtex1")) {
                return 5;
            }

            if (str.equals("depthtex0") || str.equals("gdepthtex")) {
                return 6;
            }

            if (str.equals("colortex4") || str.equals("gaux1")) {
                return 7;
            }

            if (str.equals("colortex5") || str.equals("gaux2")) {
                return 8;
            }

            if (str.equals("colortex6") || str.equals("gaux3")) {
                return 9;
            }

            if (str.equals("colortex7") || str.equals("gaux4")) {
                return 10;
            }

            if (str.equals("depthtex1")) {
                return 11;
            }

            if (str.equals("depthtex2")) {
                return 12;
            }

            if (str.equals("shadowcolor0") || str.equals("shadowcolor")) {
                return 13;
            }

            if (str.equals("shadowcolor1")) {
                return 14;
            }

            if (str.equals("noisetex")) {
                return 15;
            }
        }

        return -1;
    }

    public static void bindCustomTextures(CustomTexture[] customTextures) {
        if (customTextures != null) {
            for (int i = 0; i < customTextures.length; i++) {
                CustomTexture customTexture = customTextures[i];
                GlStateManager.setActiveTexture(33984 + customTexture.getTextureUnit());
                ITextureObject texture = customTexture.getTexture();
                GlStateManager.bindTexture(texture.getGlTextureId());
            }
        }
    }

    public static void resetCustomTextures() {
        deleteCustomTextures(customTexturesGbuffers);
        deleteCustomTextures(customTexturesComposite);
        deleteCustomTextures(customTexturesDeferred);
        customTexturesGbuffers = null;
        customTexturesComposite = null;
        customTexturesDeferred = null;
    }

    public static void deleteCustomTextures(CustomTexture[] customTextures) {
        if (customTextures != null) {
            for (int i = 0; i < customTextures.length; i++) {
                CustomTexture customTexture = customTextures[i];
                customTexture.deleteTexture();
            }
        }
    }

    public static ShaderOption[] getShaderPackOptions(String str) {
        ShaderOption[] shaderOptions = (ShaderOption[])shaderPackOptions.clone();
        if (shaderPackGuiScreens == null) {
            if (shaderPackProfiles != null) {
                ShaderOptionProfile shaderOptionProfile = new ShaderOptionProfile(shaderPackProfiles, shaderOptions);
                shaderOptions = (ShaderOption[])Config.addObjectToArray(shaderOptions, shaderOptionProfile, 0);
            }

            return getVisibleOptions(shaderOptions);
        } else {
            String str2 = str != null ? "screen." + str : "screen";
            ScreenShaderOptions screenShaderOptions = shaderPackGuiScreens.get(str2);
            if (screenShaderOptions == null) {
                return new ShaderOption[0];
            }

            ShaderOption[] shaderOptions2 = screenShaderOptions.getShaderOptions();
            ArrayList list = new ArrayList();

            for (int i = 0; i < shaderOptions2.length; i++) {
                ShaderOption shaderOption = shaderOptions2[i];
                if (shaderOption == null) {
                    list.add(null);
                } else if (shaderOption instanceof ShaderOptionRest) {
                    ShaderOption[] shaderOptionsRest = getShaderOptionsRest(shaderPackGuiScreens, shaderOptions);
                    list.addAll(Arrays.asList(shaderOptionsRest));
                } else {
                    list.add(shaderOption);
                }
            }

            return (ShaderOption[])list.toArray(new ShaderOption[list.size()]);
        }
    }

    public static int getShaderPackColumns(String str, int n) {
        String str2 = str != null ? "screen." + str : "screen";
        if (shaderPackGuiScreens == null) {
            return n;
        }

        ScreenShaderOptions screenShaderOptions = shaderPackGuiScreens.get(str2);
        return screenShaderOptions == null ? n : screenShaderOptions.getColumns();
    }

    public static ShaderOption[] getShaderOptionsRest(Map<String, ScreenShaderOptions> map, ShaderOption[] shaderOptions) {
        HashSet set = new HashSet();

        for (String str : map.keySet()) {
            ScreenShaderOptions screenShaderOptions = (ScreenShaderOptions)map.get(str);
            ShaderOption[] shaderOptions2 = screenShaderOptions.getShaderOptions();

            for (int i = 0; i < shaderOptions2.length; i++) {
                ShaderOption shaderOption = shaderOptions2[i];
                if (shaderOption != null) {
                    set.add(shaderOption.getName());
                }
            }
        }

        ArrayList list = new ArrayList();

        for (int i = 0; i < shaderOptions.length; i++) {
            ShaderOption shaderOption = shaderOptions[i];
            if (shaderOption.isVisible()) {
                String name = shaderOption.getName();
                if (!set.contains(name)) {
                    list.add(shaderOption);
                }
            }
        }

        return (ShaderOption[])list.toArray(new ShaderOption[list.size()]);
    }

    public static ShaderOption getShaderOption(String str) {
        return ShaderUtils.getShaderOption(str, shaderPackOptions);
    }

    public static ShaderOption[] getShaderPackOptions() {
        return shaderPackOptions;
    }

    public static boolean isShaderPackOptionSlider(String str) {
        return shaderPackOptionSliders == null ? false : shaderPackOptionSliders.contains(str);
    }

    public static ShaderOption[] getVisibleOptions(ShaderOption[] shaderOptions) {
        ArrayList list = new ArrayList();

        for (int i = 0; i < shaderOptions.length; i++) {
            ShaderOption shaderOption = shaderOptions[i];
            if (shaderOption.isVisible()) {
                list.add(shaderOption);
            }
        }

        return (ShaderOption[])list.toArray(new ShaderOption[list.size()]);
    }

    public static void saveShaderPackOptions() {
        saveShaderPackOptions(shaderPackOptions, shaderPack);
    }

    public static void saveShaderPackOptions(ShaderOption[] shaderOptions, IShaderPack shaderPack2) {
        Properties properties = new Properties();
        if (shaderPackOptions != null) {
            for (int i = 0; i < shaderOptions.length; i++) {
                ShaderOption shaderOption = shaderOptions[i];
                if (shaderOption.isChanged() && shaderOption.isEnabled()) {
                    properties.setProperty(shaderOption.getName(), shaderOption.getValue());
                }
            }
        }

        try {
            saveOptionProperties(shaderPack2, properties);
        } catch (IOException e) {
            Config.warn("[Shaders] Error saving configuration for " + shaderPack.getName());
            e.printStackTrace();
        }
    }

    public static void saveOptionProperties(IShaderPack shaderPack, Properties properties) throws IOException {
        String str = shaderpacksdirname + "/" + shaderPack.getName() + ".txt";
        File file = new File(Minecraft.getMinecraft().mcDataDir, str);
        if (properties.isEmpty()) {
            file.delete();
        } else {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            properties.store(fileOutputStream, (String)null);
            fileOutputStream.flush();
            fileOutputStream.close();
        }
    }

    public static ShaderOption[] loadShaderPackOptions() {
        try {
            ShaderOption[] parseShaderPackOptions = ShaderPackParser.parseShaderPackOptions(shaderPack, programNames, shaderPackDimensions);
            Properties loadOptionProperties2 = loadOptionProperties(shaderPack);

            for (int i = 0; i < parseShaderPackOptions.length; i++) {
                ShaderOption shaderOption = parseShaderPackOptions[i];
                String property = loadOptionProperties2.getProperty(shaderOption.getName());
                if (property != null) {
                    shaderOption.resetValue();
                    if (!shaderOption.setValue(property)) {
                        Config.warn("[Shaders] Invalid value, option: " + shaderOption.getName() + ", value: " + property);
                    }
                }
            }

            return parseShaderPackOptions;
        } catch (IOException e) {
            Config.warn("[Shaders] Error reading configuration for " + shaderPack.getName());
            e.printStackTrace();
            return null;
        }
    }

    public static Properties loadOptionProperties(IShaderPack shaderPack) throws IOException {
        Properties properties = new Properties();
        String str = shaderpacksdirname + "/" + shaderPack.getName() + ".txt";
        File file = new File(Minecraft.getMinecraft().mcDataDir, str);
        if (file.exists() && file.isFile() && file.canRead()) {
            FileInputStream fileInputStream = new FileInputStream(file);
            properties.load(fileInputStream);
            fileInputStream.close();
            return properties;
        } else {
            return properties;
        }
    }

    public static ShaderOption[] getChangedOptions(ShaderOption[] shaderOptions) {
        ArrayList list = new ArrayList();

        for (int i = 0; i < shaderOptions.length; i++) {
            ShaderOption shaderOption = shaderOptions[i];
            if (shaderOption.isEnabled() && shaderOption.isChanged()) {
                list.add(shaderOption);
            }
        }

        return (ShaderOption[])list.toArray(new ShaderOption[list.size()]);
    }

    public static String applyOptions(String str, ShaderOption[] shaderOptions) {
        if (shaderOptions != null && shaderOptions.length > 0) {
            for (int i = 0; i < shaderOptions.length; i++) {
                ShaderOption shaderOption = shaderOptions[i];
                String name = shaderOption.getName();
                if (shaderOption.matchesLine(str)) {
                    str = shaderOption.getSourceLine();
                    break;
                }
            }

            return str;
        } else {
            return str;
        }
    }

    public static ArrayList listOfShaders() {
        ArrayList list = new ArrayList();
        list.add(packNameNone);
        list.add(packNameDefault);

        try {
            if (!shaderpacksdir.exists()) {
                shaderpacksdir.mkdir();
            }

            File[] listFiles = shaderpacksdir.listFiles();

            for (int i = 0; i < listFiles.length; i++) {
                File file = listFiles[i];
                String name = file.getName();
                if (file.isDirectory()) {
                    if (!name.equals("debug")) {
                        File file2 = new File(file, "shaders");
                        if (file2.exists() && file2.isDirectory()) {
                            list.add(name);
                        }
                    }
                } else if (file.isFile() && name.toLowerCase().endsWith(".zip")) {
                    list.add(name);
                }
            }
        } catch (Exception e) {
        }

        return list;
    }

    public static String versiontostring(int n) {
        String str = Integer.toString(n);
        return Integer.toString(Integer.parseInt(str.substring(1, 3)))
            + "."
            + Integer.toString(Integer.parseInt(str.substring(3, 5)))
            + "."
            + Integer.toString(Integer.parseInt(str.substring(5)));
    }

    public static void checkOptifine() {
    }

    public static int checkFramebufferStatus(String str) {
        int glCheckFramebufferStatusEXT = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
        if (glCheckFramebufferStatusEXT != 36053) {
            System.err.format("FramebufferStatus 0x%04X at %s\n", glCheckFramebufferStatusEXT, str);
        }

        return glCheckFramebufferStatusEXT;
    }

    public static int checkGLError(String str) {
        int glGetError = GL11.glGetError();
        if (glGetError != 0) {
            boolean flag = false;
            if (!flag) {
                if (glGetError == 1286) {
                    int glCheckFramebufferStatusEXT = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
                    System.err.format("GL error 0x%04X: %s (Fb status 0x%04X) at %s\n", glGetError, GLU.gluErrorString(glGetError), glCheckFramebufferStatusEXT, str);
                } else {
                    System.err.format("GL error 0x%04X: %s at %s\n", glGetError, GLU.gluErrorString(glGetError), str);
                }
            }
        }

        return glGetError;
    }

    public static int checkGLError(String str, String str2) {
        int glGetError = GL11.glGetError();
        if (glGetError != 0) {
            System.err.format("GL error 0x%04x: %s at %s %s\n", glGetError, GLU.gluErrorString(glGetError), str, str2);
        }

        return glGetError;
    }

    public static int checkGLError(String str, String str2, String str3) {
        int glGetError = GL11.glGetError();
        if (glGetError != 0) {
            System.err.format("GL error 0x%04x: %s at %s %s %s\n", glGetError, GLU.gluErrorString(glGetError), str, str2, str3);
        }

        return glGetError;
    }

    public static void printChat(String str) {
        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(str));
    }

    public static void printChatAndLogError(String str) {
        SMCLog.severe(str);
        mc.ingameGUI.getChatGUI().printChatMessage(new ChatComponentText(str));
    }

    public static void printIntBuffer(String str, IntBuffer intBuffer) {
        StringBuilder sb = new StringBuilder(128);
        sb.append(str).append(" [pos ").append(intBuffer.position()).append(" lim ").append(intBuffer.limit()).append(" cap ").append(intBuffer.capacity()).append(" :");
        int limit = intBuffer.limit();

        for (int i = 0; i < limit; i++) {
            sb.append(" ").append(intBuffer.get(i));
        }

        sb.append("]");
        SMCLog.info(sb.toString());
    }

    public static void startup(Minecraft mc2) {
        checkShadersModInstalled();
        mc = mc2;
        mc = Minecraft.getMinecraft();
        capabilities = GLContext.getCapabilities();
        glVersionString = GL11.glGetString(7938);
        glVendorString = GL11.glGetString(7936);
        glRendererString = GL11.glGetString(7937);
        SMCLog.info("ShadersMod version: 2.4.12");
        SMCLog.info("OpenGL Version: " + glVersionString);
        SMCLog.info("Vendor:  " + glVendorString);
        SMCLog.info("Renderer: " + glRendererString);
        SMCLog.info(
            "Capabilities: "
                + (capabilities.OpenGL20 ? " 2.0 " : " - ")
                + (capabilities.OpenGL21 ? " 2.1 " : " - ")
                + (capabilities.OpenGL30 ? " 3.0 " : " - ")
                + (capabilities.OpenGL32 ? " 3.2 " : " - ")
                + (capabilities.OpenGL40 ? " 4.0 " : " - ")
        );
        SMCLog.info("GL_MAX_DRAW_BUFFERS: " + GL11.glGetInteger(34852));
        SMCLog.info("GL_MAX_COLOR_ATTACHMENTS_EXT: " + GL11.glGetInteger(36063));
        SMCLog.info("GL_MAX_TEXTURE_IMAGE_UNITS: " + GL11.glGetInteger(34930));
        hasGlGenMipmap = capabilities.OpenGL30;
        loadConfig();
    }

    public static String toStringYN(boolean flag) {
        return flag ? "Y" : "N";
    }

    public static void updateBlockLightLevel() {
        if (isOldLighting()) {
            blockLightLevel05 = 0.5F;
            blockLightLevel06 = 0.6F;
            blockLightLevel08 = 0.8F;
        } else {
            blockLightLevel05 = 1.0F;
            blockLightLevel06 = 1.0F;
            blockLightLevel08 = 1.0F;
        }
    }

    public static boolean isOldHandLight() {
        return !configOldHandLight.isDefault() ? configOldHandLight.isTrue() : (!shaderPackOldHandLight.isDefault() ? shaderPackOldHandLight.isTrue() : true);
    }

    public static boolean isDynamicHandLight() {
        return !shaderPackDynamicHandLight.isDefault() ? shaderPackDynamicHandLight.isTrue() : true;
    }

    public static boolean isOldLighting() {
        return !configOldLighting.isDefault() ? configOldLighting.isTrue() : (!shaderPackOldLighting.isDefault() ? shaderPackOldLighting.isTrue() : true);
    }

    public static boolean isRenderShadowTranslucent() {
        return !shaderPackShadowTranslucent.isFalse();
    }

    public static boolean isUnderwaterOverlay() {
        return !shaderPackUnderwaterOverlay.isFalse();
    }

    public static boolean isSun() {
        return !shaderPackSun.isFalse();
    }

    public static boolean isMoon() {
        return !shaderPackMoon.isFalse();
    }

    public static boolean isVignette() {
        return !shaderPackVignette.isFalse();
    }

    public static void init() {
        boolean flag;
        if (!isInitializedOnce) {
            isInitializedOnce = true;
            flag = true;
        } else {
            flag = false;
        }

        if (!isShaderPackInitialized) {
            checkGLError("Shaders.init pre");
            if (getShaderPackName() != null) {
            }

            if (!capabilities.OpenGL20) {
                printChatAndLogError("No OpenGL 2.0");
            }

            if (!capabilities.GL_EXT_framebuffer_object) {
                printChatAndLogError("No EXT_framebuffer_object");
            }

            ((Buffer)dfbDrawBuffers).position(0).limit(8);
            ((Buffer)dfbColorTextures).position(0).limit(16);
            ((Buffer)dfbDepthTextures).position(0).limit(3);
            ((Buffer)sfbDrawBuffers).position(0).limit(8);
            ((Buffer)sfbDepthTextures).position(0).limit(2);
            ((Buffer)sfbColorTextures).position(0).limit(8);
            usedColorBuffers = 4;
            usedDepthBuffers = 1;
            usedShadowColorBuffers = 0;
            usedShadowDepthBuffers = 0;
            usedColorAttachs = 1;
            usedDrawBuffers = 1;
            Arrays.fill(gbuffersFormat, 6408);
            Arrays.fill(gbuffersClear, true);
            Arrays.fill(shadowHardwareFilteringEnabled, false);
            Arrays.fill(shadowMipmapEnabled, false);
            Arrays.fill(shadowFilterNearest, false);
            Arrays.fill(shadowColorMipmapEnabled, false);
            Arrays.fill(shadowColorFilterNearest, false);
            centerDepthSmoothEnabled = false;
            noiseTextureEnabled = false;
            sunPathRotation = 0.0F;
            shadowIntervalSize = 2.0F;
            shadowMapWidth = 1024;
            shadowMapHeight = 1024;
            spShadowMapWidth = 1024;
            spShadowMapHeight = 1024;
            shadowMapFOV = 90.0F;
            shadowMapHalfPlane = 160.0F;
            shadowMapIsOrtho = true;
            shadowDistanceRenderMul = -1.0F;
            aoLevel = -1.0F;
            useEntityAttrib = false;
            useMidTexCoordAttrib = false;
            useMultiTexCoord3Attrib = false;
            useTangentAttrib = false;
            waterShadowEnabled = false;
            updateChunksErrorRecorded = false;
            updateBlockLightLevel();
            Smoother.reset();
            LegacyUniforms.reset();
            ShaderProfile detectProfile = ShaderUtils.detectProfile(shaderPackProfiles, shaderPackOptions, false);
            String str = "";
            if (currentWorld != null) {
                int dimensionId = currentWorld.provider.dimensionId;
                if (shaderPackDimensions.contains(dimensionId)) {
                    str = "world" + dimensionId + "/";
                }
            }

            if (saveFinalShaders) {
                clearDirectory(new File(shaderpacksdir, "debug"));
            }

            for (int i = 0; i < 44; i++) {
                String str2 = programNames[i];
                if (str2.equals("")) {
                    programsID[i] = programsRef[i] = 0;
                    programsDrawBufSettings[i] = null;
                    programsColorAtmSettings[i] = null;
                    programsCompositeMipmapSetting[i] = 0;
                } else {
                    newDrawBufSetting = null;
                    newColorAtmSetting = null;
                    newCompositeMipmapSetting = 0;
                    String str3 = str + str2;
                    if (detectProfile != null && detectProfile.isProgramDisabled(str3)) {
                        SMCLog.info("Program disabled: " + str3);
                        str2 = "<disabled>";
                        str3 = str + str2;
                    }

                    String str4 = "/shaders/" + str3;
                    int setupProgram2 = setupProgram(i, str4 + ".vsh", str4 + ".fsh");
                    if (setupProgram2 > 0) {
                        SMCLog.info("Program loaded: " + str3);
                    }

                    programsID[i] = programsRef[i] = setupProgram2;
                    programsDrawBufSettings[i] = setupProgram2 != 0 ? newDrawBufSetting : null;
                    programsColorAtmSettings[i] = setupProgram2 != 0 ? newColorAtmSetting : null;
                    programsCompositeMipmapSetting[i] = setupProgram2 != 0 ? newCompositeMipmapSetting : 0;
                }
            }

            int glGetInteger = GL11.glGetInteger(34852);
            new HashMap();

            for (int i = 0; i < 44; i++) {
                Arrays.fill(programsToggleColorTextures[i], false);
                if (i == 29) {
                    programsDrawBuffers[i] = null;
                } else if (programsID[i] == 0) {
                    if (i == 30) {
                        programsDrawBuffers[i] = drawBuffersNone;
                    } else {
                        programsDrawBuffers[i] = drawBuffersColorAtt0;
                    }
                } else {
                    String str2 = programsDrawBufSettings[i];
                    if (str2 != null) {
                        IntBuffer intBuffer = drawBuffersBuffer[i];
                        int n = str2.length();
                        if (n > usedDrawBuffers) {
                            usedDrawBuffers = n;
                        }

                        if (n > glGetInteger) {
                            n = glGetInteger;
                        }

                        programsDrawBuffers[i] = intBuffer;
                        ((Buffer)intBuffer).limit(n);

                        for (int j = 0; j < n; j++) {
                            int n2 = 0;
                            if (str2.length() > j) {
                                int n3 = str2.charAt(j) - '0';
                                if (i != 30) {
                                    if (n3 >= 0 && n3 <= 7) {
                                        programsToggleColorTextures[i][n3] = true;
                                        n2 = n3 + 36064;
                                        if (n3 > usedColorAttachs) {
                                            usedColorAttachs = n3;
                                        }

                                        if (n3 > usedColorBuffers) {
                                            usedColorBuffers = n3;
                                        }
                                    }
                                } else if (n3 >= 0 && n3 <= 1) {
                                    n2 = n3 + 36064;
                                    if (n3 > usedShadowColorBuffers) {
                                        usedShadowColorBuffers = n3;
                                    }
                                }
                            }

                            intBuffer.put(j, n2);
                        }
                    } else if (i != 30 && i != 31 && i != 32) {
                        programsDrawBuffers[i] = dfbDrawBuffers;
                        usedDrawBuffers = usedColorBuffers;
                        Arrays.fill(programsToggleColorTextures[i], 0, usedColorBuffers, true);
                    } else {
                        programsDrawBuffers[i] = sfbDrawBuffers;
                    }
                }
            }

            hasDeferredPrograms = false;

            for (int i = 0; i < 8; i++) {
                if (programsID[33 + i] != 0) {
                    hasDeferredPrograms = true;
                    break;
                }
            }

            usedColorAttachs = usedColorBuffers;
            shadowPassInterval = usedShadowDepthBuffers > 0 ? 1 : 0;
            shouldSkipDefaultShadow = usedShadowDepthBuffers > 0;
            SMCLog.info("usedColorBuffers: " + usedColorBuffers);
            SMCLog.info("usedDepthBuffers: " + usedDepthBuffers);
            SMCLog.info("usedShadowColorBuffers: " + usedShadowColorBuffers);
            SMCLog.info("usedShadowDepthBuffers: " + usedShadowDepthBuffers);
            SMCLog.info("usedColorAttachs: " + usedColorAttachs);
            SMCLog.info("usedDrawBuffers: " + usedDrawBuffers);
            ((Buffer)dfbDrawBuffers).position(0).limit(usedDrawBuffers);
            ((Buffer)dfbColorTextures).position(0).limit(usedColorBuffers * 2);

            for (int i = 0; i < usedDrawBuffers; i++) {
                dfbDrawBuffers.put(i, 36064 + i);
            }

            if (usedDrawBuffers > glGetInteger) {
                printChatAndLogError("[Shaders] Error: Not enough draw buffers, needed: " + usedDrawBuffers + ", available: " + glGetInteger);
            }

            ((Buffer)sfbDrawBuffers).position(0).limit(usedShadowColorBuffers);

            for (int i = 0; i < usedShadowColorBuffers; i++) {
                sfbDrawBuffers.put(i, 36064 + i);
            }

            for (int i = 0; i < 44; i++) {
                int n = i;

                while (programsID[n] == 0 && programBackups[n] != n) {
                    n = programBackups[n];
                }

                if (n != i && i != 30) {
                    programsID[i] = programsID[n];
                    programsDrawBufSettings[i] = programsDrawBufSettings[n];
                    programsDrawBuffers[i] = programsDrawBuffers[n];
                }
            }

            resize();
            resizeShadow();
            if (noiseTextureEnabled) {
                setupNoiseTexture();
            }

            if (defaultTexture == null) {
                defaultTexture = ShadersTex.createDefaultTexture();
            }

            GlStateManager.pushMatrix();
            GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
            preCelestialRotate();
            postCelestialRotate();
            GlStateManager.popMatrix();
            isShaderPackInitialized = true;
            loadEntityDataMap();
            resetDisplayList();
            if (!flag) {
            }

            checkGLError("Shaders.init");
        }
    }

    public static void resetDisplayList() {
        numberResetDisplayList++;
        needResetModels = true;
        SMCLog.info("Reset world renderers");
        mc.renderGlobal.loadRenderers();
    }

    public static void resetDisplayListModels() {
        if (needResetModels) {
            needResetModels = false;
            SMCLog.info("Reset model renderers");

            for (Render render : RenderManager.instance.getEntityRenderMap().values()) {
                if (render instanceof RendererLivingEntity) {
                    RendererLivingEntity rendererLivingEntity = (RendererLivingEntity)render;
                    resetDisplayListModel(rendererLivingEntity.mainModel);
                }
            }
        }
    }

    public static void resetDisplayListModel(ModelBase modelBase) {
        if (modelBase != null) {
            for (Object obj : modelBase.boxList) {
                if (obj instanceof ModelRenderer) {
                    resetDisplayListModelRenderer((ModelRenderer)obj);
                }
            }
        }
    }

    public static void resetDisplayListModelRenderer(ModelRenderer modelRenderer) {
        modelRenderer.resetDisplayList();
        if (modelRenderer.childModels != null) {
            int n = 0;

            for (int size = modelRenderer.childModels.size(); n < size; n++) {
                resetDisplayListModelRenderer((ModelRenderer)modelRenderer.childModels.get(n));
            }
        }
    }

    public static int setupProgram(int n, String str, String str2) {
        checkGLError("pre setupProgram");
        int glCreateProgramObjectARB = ARBShaderObjects.glCreateProgramObjectARB();
        checkGLError("create");
        if (glCreateProgramObjectARB != 0) {
            progUseEntityAttrib = false;
            progUseMidTexCoordAttrib = false;
            progUseTangentAttrib = false;
            int createVertShader2 = createVertShader(str);
            int createFragShader2 = createFragShader(str2);
            checkGLError("create");
            if (createVertShader2 == 0 && createFragShader2 == 0) {
                ARBShaderObjects.glDeleteObjectARB(glCreateProgramObjectARB);
                glCreateProgramObjectARB = 0;
            } else {
                if (createVertShader2 != 0) {
                    ARBShaderObjects.glAttachObjectARB(glCreateProgramObjectARB, createVertShader2);
                    checkGLError("attach");
                }

                if (createFragShader2 != 0) {
                    ARBShaderObjects.glAttachObjectARB(glCreateProgramObjectARB, createFragShader2);
                    checkGLError("attach");
                }

                if (progUseEntityAttrib) {
                    ARBVertexShader.glBindAttribLocationARB(glCreateProgramObjectARB, entityAttrib, "mc_Entity");
                    checkGLError("mc_Entity");
                }

                if (progUseMidTexCoordAttrib) {
                    ARBVertexShader.glBindAttribLocationARB(glCreateProgramObjectARB, midTexCoordAttrib, "mc_midTexCoord");
                    checkGLError("mc_midTexCoord");
                }

                if (progUseTangentAttrib) {
                    ARBVertexShader.glBindAttribLocationARB(glCreateProgramObjectARB, tangentAttrib, "at_tangent");
                    checkGLError("at_tangent");
                }

                ARBShaderObjects.glLinkProgramARB(glCreateProgramObjectARB);
                if (GL20.glGetProgrami(glCreateProgramObjectARB, 35714) != 1) {
                    SMCLog.severe("Error linking program: " + glCreateProgramObjectARB);
                }

                printLogInfo(glCreateProgramObjectARB, str + ", " + str2);
                if (createVertShader2 != 0) {
                    ARBShaderObjects.glDetachObjectARB(glCreateProgramObjectARB, createVertShader2);
                    ARBShaderObjects.glDeleteObjectARB(createVertShader2);
                }

                if (createFragShader2 != 0) {
                    ARBShaderObjects.glDetachObjectARB(glCreateProgramObjectARB, createFragShader2);
                    ARBShaderObjects.glDeleteObjectARB(createFragShader2);
                }

                programsID[n] = glCreateProgramObjectARB;
                useProgram(n);
                ARBShaderObjects.glValidateProgramARB(glCreateProgramObjectARB);
                useProgram(0);
                printLogInfo(glCreateProgramObjectARB, str + ", " + str2);
                int glGetProgrami = GL20.glGetProgrami(glCreateProgramObjectARB, 35715);
                if (glGetProgrami != 1) {
                    String str3 = "\"";
                    printChatAndLogError("[Shaders] Error: Invalid program " + str3 + programNames[n] + str3);
                    ARBShaderObjects.glDeleteObjectARB(glCreateProgramObjectARB);
                    glCreateProgramObjectARB = 0;
                }
            }
        }

        return glCreateProgramObjectARB;
    }

    public static int createVertShader(String str) {
        int glCreateShaderObjectARB = ARBShaderObjects.glCreateShaderObjectARB(35633);
        if (glCreateShaderObjectARB == 0) {
            return 0;
        }

        StringBuilder sb = new StringBuilder(131072);
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(getShaderReader(str));
        } catch (Exception e) {
            ARBShaderObjects.glDeleteObjectARB(glCreateShaderObjectARB);
            return 0;
        }

        ShaderOption[] changedOptions = getChangedOptions(shaderPackOptions);
        ArrayList list = new ArrayList();
        if (bufferedReader != null) {
            try {
                bufferedReader = ShaderPackParser.resolveIncludes(bufferedReader, str, shaderPack, 0, list, 0);

                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        bufferedReader.close();
                        break;
                    }

                    readLine = applyOptions(readLine, changedOptions);
                    sb.append(readLine).append('\n');
                    ShaderLine parseLine = ShaderParser.parseLine(readLine);
                    if (parseLine != null) {
                        if (parseLine.isAttribute("mc_Entity")) {
                            useEntityAttrib = true;
                            progUseEntityAttrib = true;
                        } else if (parseLine.isAttribute("mc_midTexCoord")) {
                            useMidTexCoordAttrib = true;
                            progUseMidTexCoordAttrib = true;
                        } else if (readLine.contains("gl_MultiTexCoord3")) {
                            useMultiTexCoord3Attrib = true;
                        } else if (parseLine.isAttribute("at_tangent")) {
                            useTangentAttrib = true;
                            progUseTangentAttrib = true;
                        }
                    }
                }
            } catch (Exception e) {
                SMCLog.severe("Couldn't read " + str + "!");
                e.printStackTrace();
                ARBShaderObjects.glDeleteObjectARB(glCreateShaderObjectARB);
                return 0;
            }
        }

        if (saveFinalShaders) {
            saveShader(str, sb.toString());
        }

        ARBShaderObjects.glShaderSourceARB(glCreateShaderObjectARB, sb);
        ARBShaderObjects.glCompileShaderARB(glCreateShaderObjectARB);
        if (GL20.glGetShaderi(glCreateShaderObjectARB, 35713) != 1) {
            SMCLog.severe("Error compiling vertex shader: " + str);
        }

        printShaderLogInfo(glCreateShaderObjectARB, str, list);
        return glCreateShaderObjectARB;
    }

    public static int createFragShader(String str) {
        int glCreateShaderObjectARB = ARBShaderObjects.glCreateShaderObjectARB(35632);
        if (glCreateShaderObjectARB == 0) {
            return 0;
        }

        StringBuilder sb = new StringBuilder(131072);
        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(getShaderReader(str));
        } catch (Exception e) {
            ARBShaderObjects.glDeleteObjectARB(glCreateShaderObjectARB);
            return 0;
        }

        ShaderOption[] changedOptions = getChangedOptions(shaderPackOptions);
        ArrayList list = new ArrayList();
        if (bufferedReader != null) {
            try {
                bufferedReader = ShaderPackParser.resolveIncludes(bufferedReader, str, shaderPack, 0, list, 0);

                while (true) {
                    String readLine = bufferedReader.readLine();
                    if (readLine == null) {
                        bufferedReader.close();
                        break;
                    }

                    readLine = applyOptions(readLine, changedOptions);
                    sb.append(readLine).append('\n');
                    ShaderLine parseLine = ShaderParser.parseLine(readLine);
                    if (parseLine != null) {
                        if (parseLine.isUniform()) {
                            String name = parseLine.getName();
                            int n;
                            if ((n = ShaderParser.getShadowDepthIndex(name)) >= 0) {
                                usedShadowDepthBuffers = Math.max(usedShadowDepthBuffers, n + 1);
                            } else if ((n = ShaderParser.getShadowColorIndex(name)) >= 0) {
                                usedShadowColorBuffers = Math.max(usedShadowColorBuffers, n + 1);
                            } else if ((n = ShaderParser.getDepthIndex(name)) >= 0) {
                                usedDepthBuffers = Math.max(usedDepthBuffers, n + 1);
                            } else if (name.equals("gdepth") && gbuffersFormat[1] == 6408) {
                                gbuffersFormat[1] = 34836;
                            } else if ((n = ShaderParser.getColorIndex(name)) >= 0) {
                                usedColorBuffers = Math.max(usedColorBuffers, n + 1);
                            } else if (name.equals("centerDepthSmooth")) {
                                centerDepthSmoothEnabled = true;
                            }
                        } else if (parseLine.isConstInt("shadowMapResolution") || parseLine.isProperty("SHADOWRES")) {
                            spShadowMapWidth = spShadowMapHeight = parseLine.getValueInt();
                            shadowMapWidth = shadowMapHeight = Math.round(spShadowMapWidth * configShadowResMul);
                            SMCLog.info("Shadow map resolution: " + spShadowMapWidth);
                        } else if (parseLine.isConstFloat("shadowMapFov") || parseLine.isProperty("SHADOWFOV")) {
                            shadowMapFOV = parseLine.getValueFloat();
                            shadowMapIsOrtho = false;
                            SMCLog.info("Shadow map field of view: " + shadowMapFOV);
                        } else if (parseLine.isConstFloat("shadowDistance") || parseLine.isProperty("SHADOWHPL")) {
                            shadowMapHalfPlane = parseLine.getValueFloat();
                            shadowMapIsOrtho = true;
                            SMCLog.info("Shadow map distance: " + shadowMapHalfPlane);
                        } else if (parseLine.isConstFloat("shadowDistanceRenderMul")) {
                            shadowDistanceRenderMul = parseLine.getValueFloat();
                            SMCLog.info("Shadow distance render mul: " + shadowDistanceRenderMul);
                        } else if (parseLine.isConstFloat("shadowIntervalSize")) {
                            shadowIntervalSize = parseLine.getValueFloat();
                            SMCLog.info("Shadow map interval size: " + shadowIntervalSize);
                        } else if (parseLine.isConstBool("generateShadowMipmap", true)) {
                            Arrays.fill(shadowMipmapEnabled, true);
                            SMCLog.info("Generate shadow mipmap");
                        } else if (parseLine.isConstBool("generateShadowColorMipmap", true)) {
                            Arrays.fill(shadowColorMipmapEnabled, true);
                            SMCLog.info("Generate shadow color mipmap");
                        } else if (parseLine.isConstBool("shadowHardwareFiltering", true)) {
                            Arrays.fill(shadowHardwareFilteringEnabled, true);
                            SMCLog.info("Hardware shadow filtering enabled.");
                        } else if (parseLine.isConstBool("shadowHardwareFiltering0", true)) {
                            shadowHardwareFilteringEnabled[0] = true;
                            SMCLog.info("shadowHardwareFiltering0");
                        } else if (parseLine.isConstBool("shadowHardwareFiltering1", true)) {
                            shadowHardwareFilteringEnabled[1] = true;
                            SMCLog.info("shadowHardwareFiltering1");
                        } else if (parseLine.isConstBool("shadowtex0Mipmap", "shadowtexMipmap", true)) {
                            shadowMipmapEnabled[0] = true;
                            SMCLog.info("shadowtex0Mipmap");
                        } else if (parseLine.isConstBool("shadowtex1Mipmap", true)) {
                            shadowMipmapEnabled[1] = true;
                            SMCLog.info("shadowtex1Mipmap");
                        } else if (parseLine.isConstBool("shadowcolor0Mipmap", "shadowColor0Mipmap", true)) {
                            shadowColorMipmapEnabled[0] = true;
                            SMCLog.info("shadowcolor0Mipmap");
                        } else if (parseLine.isConstBool("shadowcolor1Mipmap", "shadowColor1Mipmap", true)) {
                            shadowColorMipmapEnabled[1] = true;
                            SMCLog.info("shadowcolor1Mipmap");
                        } else if (parseLine.isConstBool("shadowtex0Nearest", "shadowtexNearest", "shadow0MinMagNearest", true)) {
                            shadowFilterNearest[0] = true;
                            SMCLog.info("shadowtex0Nearest");
                        } else if (parseLine.isConstBool("shadowtex1Nearest", "shadow1MinMagNearest", true)) {
                            shadowFilterNearest[1] = true;
                            SMCLog.info("shadowtex1Nearest");
                        } else if (parseLine.isConstBool("shadowcolor0Nearest", "shadowColor0Nearest", "shadowColor0MinMagNearest", true)) {
                            shadowColorFilterNearest[0] = true;
                            SMCLog.info("shadowcolor0Nearest");
                        } else if (parseLine.isConstBool("shadowcolor1Nearest", "shadowColor1Nearest", "shadowColor1MinMagNearest", true)) {
                            shadowColorFilterNearest[1] = true;
                            SMCLog.info("shadowcolor1Nearest");
                        } else if (parseLine.isConstFloat("wetnessHalflife") || parseLine.isProperty("WETNESSHL")) {
                            wetnessHalfLife = parseLine.getValueFloat();
                            SMCLog.info("Wetness halflife: " + wetnessHalfLife);
                        } else if (parseLine.isConstFloat("drynessHalflife") || parseLine.isProperty("DRYNESSHL")) {
                            drynessHalfLife = parseLine.getValueFloat();
                            SMCLog.info("Dryness halflife: " + drynessHalfLife);
                        } else if (parseLine.isConstFloat("eyeBrightnessHalflife")) {
                            eyeBrightnessHalflife = parseLine.getValueFloat();
                            SMCLog.info("Eye brightness halflife: " + eyeBrightnessHalflife);
                        } else if (parseLine.isConstFloat("centerDepthHalflife")) {
                            centerDepthSmoothHalflife = parseLine.getValueFloat();
                            SMCLog.info("Center depth halflife: " + centerDepthSmoothHalflife);
                        } else if (parseLine.isConstFloat("sunPathRotation")) {
                            sunPathRotation = parseLine.getValueFloat();
                            SMCLog.info("Sun path rotation: " + sunPathRotation);
                        } else if (parseLine.isConstFloat("ambientOcclusionLevel")) {
                            aoLevel = Config.limit(parseLine.getValueFloat(), 0.0F, 1.0F);
                            SMCLog.info("AO Level: " + aoLevel);
                        } else if (parseLine.isConstInt("superSamplingLevel")) {
                            int valueInt = parseLine.getValueInt();
                            if (valueInt > 1) {
                                SMCLog.info("Super sampling level: " + valueInt + "x");
                                superSamplingLevel = valueInt;
                            } else {
                                superSamplingLevel = 1;
                            }
                        } else if (parseLine.isConstInt("noiseTextureResolution")) {
                            noiseTextureResolution = parseLine.getValueInt();
                            noiseTextureEnabled = true;
                            SMCLog.info("Noise texture enabled");
                            SMCLog.info("Noise texture resolution: " + noiseTextureResolution);
                        } else if (parseLine.isConstIntSuffix("Format")) {
                            String removeSuffix = StrUtils.removeSuffix(parseLine.getName(), "Format");
                            String value = parseLine.getValue();
                            int bufferIndexFromString = getBufferIndexFromString(removeSuffix);
                            int textureFormatFromString = getTextureFormatFromString(value);
                            if (bufferIndexFromString >= 0 && textureFormatFromString != 0) {
                                gbuffersFormat[bufferIndexFromString] = textureFormatFromString;
                                SMCLog.info("%s format: %s", removeSuffix, value);
                            }
                        } else if (parseLine.isConstBoolSuffix("Clear", false)) {
                            if (ShaderParser.isComposite(str) || ShaderParser.isDeferred(str)) {
                                String removeSuffix = StrUtils.removeSuffix(parseLine.getName(), "Clear");
                                int bufferIndexFromString = getBufferIndexFromString(removeSuffix);
                                if (bufferIndexFromString >= 0) {
                                    gbuffersClear[bufferIndexFromString] = false;
                                    SMCLog.info("%s clear disabled", removeSuffix);
                                }
                            }
                        } else if (parseLine.isProperty("GAUX4FORMAT", "RGBA32F")) {
                            gbuffersFormat[7] = 34836;
                            SMCLog.info("gaux4 format : RGB32AF");
                        } else if (parseLine.isProperty("GAUX4FORMAT", "RGB32F")) {
                            gbuffersFormat[7] = 34837;
                            SMCLog.info("gaux4 format : RGB32F");
                        } else if (parseLine.isProperty("GAUX4FORMAT", "RGB16")) {
                            gbuffersFormat[7] = 32852;
                            SMCLog.info("gaux4 format : RGB16");
                        } else if (parseLine.isConstBoolSuffix("MipmapEnabled", true)) {
                            if (ShaderParser.isComposite(str) || ShaderParser.isDeferred(str) || ShaderParser.isFinal(str)) {
                                String removeSuffix = StrUtils.removeSuffix(parseLine.getName(), "MipmapEnabled");
                                int bufferIndexFromString = getBufferIndexFromString(removeSuffix);
                                if (bufferIndexFromString >= 0) {
                                    newCompositeMipmapSetting |= 1 << bufferIndexFromString;
                                    SMCLog.info("%s mipmap enabled", removeSuffix);
                                }
                            }
                        } else if (parseLine.isProperty("DRAWBUFFERS")) {
                            String value = parseLine.getValue();
                            if (ShaderParser.isValidDrawBuffers(value)) {
                                newDrawBufSetting = value;
                            } else {
                                SMCLog.warning("Invalid draw buffers: " + value);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                SMCLog.severe("Couldn't read " + str + "!");
                e.printStackTrace();
                ARBShaderObjects.glDeleteObjectARB(glCreateShaderObjectARB);
                return 0;
            }
        }

        if (saveFinalShaders) {
            saveShader(str, sb.toString());
        }

        ARBShaderObjects.glShaderSourceARB(glCreateShaderObjectARB, sb);
        ARBShaderObjects.glCompileShaderARB(glCreateShaderObjectARB);
        if (GL20.glGetShaderi(glCreateShaderObjectARB, 35713) != 1) {
            SMCLog.severe("Error compiling fragment shader: " + str);
        }

        printShaderLogInfo(glCreateShaderObjectARB, str, list);
        return glCreateShaderObjectARB;
    }

    public static Reader getShaderReader(String str) {
        Reader shaderReader = ShadersBuiltIn.getShaderReader(str);
        return shaderReader != null ? shaderReader : new InputStreamReader(shaderPack.getResourceAsStream(str));
    }

    public static void saveShader(String str, String str2) {
        try {
            File file = new File(shaderpacksdir, "debug/" + str);
            file.getParentFile().mkdirs();
            Config.writeFile(file, str2);
        } catch (IOException e) {
            Config.warn("Error saving: " + str);
            e.printStackTrace();
        }
    }

    public static void clearDirectory(File file) {
        if (file.exists() && file.isDirectory()) {
            File[] listFiles = file.listFiles();
            if (listFiles != null) {
                for (int i = 0; i < listFiles.length; i++) {
                    File file2 = listFiles[i];
                    if (file2.isDirectory()) {
                        clearDirectory(file2);
                    }

                    file2.delete();
                }
            }
        }
    }

    public static boolean printLogInfo(int n, String str) {
        IntBuffer createIntBuffer = BufferUtils.createIntBuffer(1);
        ARBShaderObjects.glGetObjectParameterARB(n, 35716, createIntBuffer);
        int n2 = createIntBuffer.get();
        if (n2 > 1) {
            ByteBuffer createByteBuffer = BufferUtils.createByteBuffer(n2);
            ((Buffer)createIntBuffer).flip();
            ARBShaderObjects.glGetInfoLogARB(n, createIntBuffer, createByteBuffer);
            byte[] bytes = new byte[n2];
            createByteBuffer.get(bytes);
            if (bytes[n2 - 1] == 0) {
                bytes[n2 - 1] = 10;
            }

            String str2 = new String(bytes);
            SMCLog.info("Info log: " + str + "\n" + str2);
            return false;
        } else {
            return true;
        }
    }

    public static boolean printShaderLogInfo(int n, String str, List<String> list) {
        IntBuffer createIntBuffer = BufferUtils.createIntBuffer(1);
        int glGetShaderi = GL20.glGetShaderi(n, 35716);
        if (glGetShaderi <= 1) {
            return true;
        }

        for (int i = 0; i < list.size(); i++) {
            String str2 = (String)list.get(i);
            SMCLog.info("File: " + (i + 1) + " = " + str2);
        }

        String glGetShaderInfoLog = GL20.glGetShaderInfoLog(n, glGetShaderi);
        SMCLog.info("Shader info log: " + str + "\n" + glGetShaderInfoLog);
        return false;
    }

    public static void setDrawBuffers(IntBuffer intBuffer) {
        if (intBuffer == null) {
            intBuffer = drawBuffersNone;
        }

        if (activeDrawBuffers != intBuffer) {
            activeDrawBuffers = intBuffer;
            GL20.glDrawBuffers(intBuffer);
        }
    }

    public static void useProgram(int n) {
        checkGLError("pre-useProgram");
        if (isShadowPass) {
            n = 30;
            if (programsID[30] == 0) {
                normalMapEnabled = false;
                return;
            }
        }

        if (activeProgram != n) {
            activeProgram = n;
            ARBShaderObjects.glUseProgramObjectARB(programsID[n]);
            if (programsID[n] == 0) {
                normalMapEnabled = false;
            } else {
                if (checkGLError("useProgram ", programNames[n]) != 0) {
                    programsID[n] = 0;
                }

                IntBuffer intBuffer = programsDrawBuffers[n];
                if (isRenderingDfb) {
                    setDrawBuffers(intBuffer);
                    checkGLError(programNames[n], " draw buffers = ", programsDrawBufSettings[n]);
                }

                activeCompositeMipmapSetting = programsCompositeMipmapSetting[n];
                uniformEntityColor.setProgram(programsID[activeProgram]);
                uniformEntityId.setProgram(programsID[activeProgram]);
                uniformBlockEntityId.setProgram(programsID[activeProgram]);
                switch (n) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                    case 10:
                    case 11:
                    case 12:
                    case 13:
                    case 16:
                    case 18:
                    case 19:
                    case 20:
                    case 41:
                        normalMapEnabled = true;
                        setProgramUniform1i("texture", 0);
                        setProgramUniform1i("lightmap", 1);
                        setProgramUniform1i("normals", 2);
                        setProgramUniform1i("specular", 3);
                        setProgramUniform1i("shadow", waterShadowEnabled ? 5 : 4);
                        setProgramUniform1i("watershadow", 4);
                        setProgramUniform1i("shadowtex0", 4);
                        setProgramUniform1i("shadowtex1", 5);
                        setProgramUniform1i("depthtex0", 6);
                        if (customTexturesGbuffers != null || hasDeferredPrograms) {
                            setProgramUniform1i("gaux1", 7);
                            setProgramUniform1i("gaux2", 8);
                            setProgramUniform1i("gaux3", 9);
                            setProgramUniform1i("gaux4", 10);
                        }

                        setProgramUniform1i("depthtex1", 12);
                        setProgramUniform1i("shadowcolor", 13);
                        setProgramUniform1i("shadowcolor0", 13);
                        setProgramUniform1i("shadowcolor1", 14);
                        setProgramUniform1i("noisetex", 15);
                        break;
                    case 14:
                    case 15:
                    case 17:
                    default:
                        normalMapEnabled = false;
                        break;
                    case 21:
                    case 22:
                    case 23:
                    case 24:
                    case 25:
                    case 26:
                    case 27:
                    case 28:
                    case 29:
                    case 33:
                    case 34:
                    case 35:
                    case 36:
                    case 37:
                    case 38:
                    case 39:
                    case 40:
                    case 42:
                    case 43:
                        normalMapEnabled = false;
                        setProgramUniform1i("gcolor", 0);
                        setProgramUniform1i("gdepth", 1);
                        setProgramUniform1i("gnormal", 2);
                        setProgramUniform1i("composite", 3);
                        setProgramUniform1i("gaux1", 7);
                        setProgramUniform1i("gaux2", 8);
                        setProgramUniform1i("gaux3", 9);
                        setProgramUniform1i("gaux4", 10);
                        setProgramUniform1i("colortex0", 0);
                        setProgramUniform1i("colortex1", 1);
                        setProgramUniform1i("colortex2", 2);
                        setProgramUniform1i("colortex3", 3);
                        setProgramUniform1i("colortex4", 7);
                        setProgramUniform1i("colortex5", 8);
                        setProgramUniform1i("colortex6", 9);
                        setProgramUniform1i("colortex7", 10);
                        setProgramUniform1i("shadow", waterShadowEnabled ? 5 : 4);
                        setProgramUniform1i("watershadow", 4);
                        setProgramUniform1i("shadowtex0", 4);
                        setProgramUniform1i("shadowtex1", 5);
                        setProgramUniform1i("gdepthtex", 6);
                        setProgramUniform1i("depthtex0", 6);
                        setProgramUniform1i("depthtex1", 11);
                        setProgramUniform1i("depthtex2", 12);
                        setProgramUniform1i("shadowcolor", 13);
                        setProgramUniform1i("shadowcolor0", 13);
                        setProgramUniform1i("shadowcolor1", 14);
                        setProgramUniform1i("noisetex", 15);
                        break;
                    case 30:
                    case 31:
                    case 32:
                        setProgramUniform1i("tex", 0);
                        setProgramUniform1i("texture", 0);
                        setProgramUniform1i("lightmap", 1);
                        setProgramUniform1i("normals", 2);
                        setProgramUniform1i("specular", 3);
                        setProgramUniform1i("shadow", waterShadowEnabled ? 5 : 4);
                        setProgramUniform1i("watershadow", 4);
                        setProgramUniform1i("shadowtex0", 4);
                        setProgramUniform1i("shadowtex1", 5);
                        if (customTexturesGbuffers != null) {
                            setProgramUniform1i("gaux1", 7);
                            setProgramUniform1i("gaux2", 8);
                            setProgramUniform1i("gaux3", 9);
                            setProgramUniform1i("gaux4", 10);
                        }

                        setProgramUniform1i("shadowcolor", 13);
                        setProgramUniform1i("shadowcolor0", 13);
                        setProgramUniform1i("shadowcolor1", 14);
                        setProgramUniform1i("noisetex", 15);
                }

                ItemStack itemStack = mc.thePlayer != null ? mc.thePlayer.getHeldItem() : null;
                Item item = itemStack != null ? itemStack.getItem() : null;
                int n2 = -1;
                Block block = null;
                if (item != null) {
                    n2 = Item.itemRegistry.getIDForObject(item);
                    block = (Block)Block.blockRegistry.getObjectById(n2);
                }

                int n3 = block != null ? block.getLightValue() : 0;
                setProgramUniform1i("heldItemId", n2);
                setProgramUniform1i("heldBlockLightValue", n3);
                setProgramUniform1i("fogMode", fogEnabled ? fogMode : 0);
                setProgramUniform3f("fogColor", fogColorR, fogColorG, fogColorB);
                setProgramUniform3f("skyColor", skyColorR, skyColorG, skyColorB);
                setProgramUniform1i("worldTime", (int)(worldTime % 24000L));
                setProgramUniform1i("worldDay", (int)(worldTime / 24000L));
                setProgramUniform1i("moonPhase", moonPhase);
                setProgramUniform1i("frameCounter", frameCounter);
                setProgramUniform1f("frameTime", frameTime);
                setProgramUniform1f("frameTimeCounter", frameTimeCounter);
                setProgramUniform1f("sunAngle", sunAngle);
                setProgramUniform1f("shadowAngle", shadowAngle);
                setProgramUniform1f("rainStrength", rainStrength);
                setProgramUniform1f("aspectRatio", (float)renderWidth / renderHeight);
                setProgramUniform1f("viewWidth", renderWidth);
                setProgramUniform1f("viewHeight", renderHeight);
                setProgramUniform1f("near", 0.05F);
                setProgramUniform1f("far", mc.gameSettings.renderDistanceChunks * 16);
                setProgramUniform3f("sunPosition", sunPosition[0], sunPosition[1], sunPosition[2]);
                setProgramUniform3f("moonPosition", moonPosition[0], moonPosition[1], moonPosition[2]);
                setProgramUniform3f("shadowLightPosition", shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
                setProgramUniform3f("upPosition", upPosition[0], upPosition[1], upPosition[2]);
                setProgramUniform3f("previousCameraPosition", (float)previousCameraPositionX, (float)previousCameraPositionY, (float)previousCameraPositionZ);
                setProgramUniform3f("cameraPosition", (float)cameraPositionX, (float)cameraPositionY, (float)cameraPositionZ);
                setProgramUniformMatrix4ARB("gbufferModelView", false, modelView);
                setProgramUniformMatrix4ARB("gbufferModelViewInverse", false, modelViewInverse);
                setProgramUniformMatrix4ARB("gbufferPreviousProjection", false, previousProjection);
                setProgramUniformMatrix4ARB("gbufferProjection", false, projection);
                setProgramUniformMatrix4ARB("gbufferProjectionInverse", false, projectionInverse);
                setProgramUniformMatrix4ARB("gbufferPreviousModelView", false, previousModelView);
                if (usedShadowDepthBuffers > 0) {
                    setProgramUniformMatrix4ARB("shadowProjection", false, shadowProjection);
                    setProgramUniformMatrix4ARB("shadowProjectionInverse", false, shadowProjectionInverse);
                    setProgramUniformMatrix4ARB("shadowModelView", false, shadowModelView);
                    setProgramUniformMatrix4ARB("shadowModelViewInverse", false, shadowModelViewInverse);
                }

                setProgramUniform1f("wetness", wetness);
                setProgramUniform1f("eyeAltitude", eyePosY);
                setProgramUniform2i("eyeBrightness", eyeBrightness & 65535, eyeBrightness >> 16);
                setProgramUniform2i("eyeBrightnessSmooth", Math.round(eyeBrightnessFadeX), Math.round(eyeBrightnessFadeY));
                setProgramUniform2i("terrainTextureSize", terrainTextureSize[0], terrainTextureSize[1]);
                setProgramUniform1i("terrainIconSize", terrainIconSize);
                setProgramUniform1i("isEyeInWater", isEyeInWater);
                setProgramUniform1f("nightVision", nightVision);
                setProgramUniform1f("blindness", blindness);
                setProgramUniform1f("screenBrightness", mc.gameSettings.gammaSetting);
                setProgramUniform1i("hideGUI", mc.gameSettings.hideGUI ? 1 : 0);
                setProgramUniform1f("centerDepthSmooth", centerDepthSmooth);
                setProgramUniform2i("atlasSize", atlasSizeX, atlasSizeY);
                if (customUniforms != null) {
                    customUniforms.setProgram(programsID[activeProgram]);
                    customUniforms.update();
                }

                checkGLError("useProgram ", programNames[n]);
            }
        }
    }

    public static void setProgramUniform1i(String str, int n) {
        int n2 = programsID[activeProgram];
        if (n2 != 0) {
            int glGetUniformLocationARB = ARBShaderObjects.glGetUniformLocationARB(n2, str);
            ARBShaderObjects.glUniform1iARB(glGetUniformLocationARB, n);
            if (isCustomUniforms()) {
                LegacyUniforms.setInt(str, n);
            }

            checkGLError(programNames[activeProgram], str);
        }
    }

    public static void setProgramUniform2i(String str, int n, int n2) {
        int n3 = programsID[activeProgram];
        if (n3 != 0) {
            int glGetUniformLocationARB = ARBShaderObjects.glGetUniformLocationARB(n3, str);
            ARBShaderObjects.glUniform2iARB(glGetUniformLocationARB, n, n2);
            if (isCustomUniforms()) {
                LegacyUniforms.setIntXy(str, n, n2);
            }

            checkGLError(programNames[activeProgram], str);
        }
    }

    public static void setProgramUniform1f(String str, float f) {
        int n = programsID[activeProgram];
        if (n != 0) {
            int glGetUniformLocationARB = ARBShaderObjects.glGetUniformLocationARB(n, str);
            ARBShaderObjects.glUniform1fARB(glGetUniformLocationARB, f);
            if (isCustomUniforms()) {
                LegacyUniforms.setFloat(str, f);
            }

            checkGLError(programNames[activeProgram], str);
        }
    }

    public static void setProgramUniform3f(String str, float f, float f2, float f3) {
        int n = programsID[activeProgram];
        if (n != 0) {
            int glGetUniformLocationARB = ARBShaderObjects.glGetUniformLocationARB(n, str);
            ARBShaderObjects.glUniform3fARB(glGetUniformLocationARB, f, f2, f3);
            if (isCustomUniforms()) {
                if (str.endsWith("Color")) {
                    LegacyUniforms.setFloatRgb(str, f, f2, f3);
                } else {
                    LegacyUniforms.setFloatXyz(str, f, f2, f3);
                }
            }

            checkGLError(programNames[activeProgram], str);
        }
    }

    public static void setProgramUniformMatrix4ARB(String str, boolean flag, FloatBuffer floatBuffer) {
        int n = programsID[activeProgram];
        if (n != 0 && floatBuffer != null) {
            int glGetUniformLocationARB = ARBShaderObjects.glGetUniformLocationARB(n, str);
            ARBShaderObjects.glUniformMatrix4ARB(glGetUniformLocationARB, flag, floatBuffer);
            checkGLError(programNames[activeProgram], str);
        }
    }

    public static int getBufferIndexFromString(String str) {
        return str.equals("colortex0") || str.equals("gcolor")
            ? 0
            : (
                str.equals("colortex1") || str.equals("gdepth")
                    ? 1
                    : (
                        str.equals("colortex2") || str.equals("gnormal")
                            ? 2
                            : (
                                str.equals("colortex3") || str.equals("composite")
                                    ? 3
                                    : (
                                        str.equals("colortex4") || str.equals("gaux1")
                                            ? 4
                                            : (
                                                str.equals("colortex5") || str.equals("gaux2")
                                                    ? 5
                                                    : (
                                                        !str.equals("colortex6") && !str.equals("gaux3")
                                                            ? (!str.equals("colortex7") && !str.equals("gaux4") ? -1 : 7)
                                                            : 6
                                                    )
                                            )
                                    )
                            )
                    )
            );
    }

    public static int getTextureFormatFromString(String str) {
        str = str.trim();

        for (int i = 0; i < formatNames.length; i++) {
            String str2 = formatNames[i];
            if (str.equals(str2)) {
                return formatIds[i];
            }
        }

        return 0;
    }

    public static void setupNoiseTexture() {
        if (noiseTexture == null && noiseTexturePath != null) {
            noiseTexture = loadCustomTexture(15, noiseTexturePath);
        }

        if (noiseTexture == null) {
            noiseTexture = new HFNoiseTexture(noiseTextureResolution, noiseTextureResolution);
        }
    }

    public static void loadEntityDataMap() {
        mapBlockToEntityData = new IdentityHashMap<>(300);
        if (mapBlockToEntityData.isEmpty()) {
            for (Object obj : Block.blockRegistry.getKeys()) {
                String str = (String)obj;
                Block object = (Block)Block.blockRegistry.getObject(str);
                int iDForObject = Block.blockRegistry.getIDForObject(object);
                mapBlockToEntityData.put(object, iDForObject);
            }
        }

        BufferedReader bufferedReader = null;

        try {
            bufferedReader = new BufferedReader(new InputStreamReader(shaderPack.getResourceAsStream("/mc_Entity_x.txt")));
        } catch (Exception e) {
        }

        if (bufferedReader != null) {
            String str;
            try {
                while ((str = bufferedReader.readLine()) != null) {
                    Matcher matcher = patternLoadEntityDataMap.matcher(str);
                    if (matcher.matches()) {
                        String group = matcher.group(1);
                        String group2 = matcher.group(2);
                        int parseInt = Integer.parseInt(group2);
                        Block blockFromName = Block.getBlockFromName(group);
                        if (blockFromName != null) {
                            mapBlockToEntityData.put(blockFromName, parseInt);
                        } else {
                            SMCLog.warning("Unknown block name %s", group);
                        }
                    } else {
                        SMCLog.warning("unmatched %s\n", str);
                    }
                }
            } catch (Exception e) {
                SMCLog.warning("Error parsing mc_Entity_x.txt");
            }
        }

        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (Exception e) {
            }
        }
    }

    public static IntBuffer fillIntBufferZero(IntBuffer intBuffer) {
        int limit = intBuffer.limit();

        for (int position = intBuffer.position(); position < limit; position++) {
            intBuffer.put(position, 0);
        }

        return intBuffer;
    }

    public static void uninit() {
        if (isShaderPackInitialized) {
            checkGLError("Shaders.uninit pre");

            for (int i = 0; i < 44; i++) {
                if (programsRef[i] != 0) {
                    ARBShaderObjects.glDeleteObjectARB(programsRef[i]);
                    checkGLError("del programRef");
                }

                programsRef[i] = 0;
                programsID[i] = 0;
                programsDrawBufSettings[i] = null;
                programsDrawBuffers[i] = null;
                programsCompositeMipmapSetting[i] = 0;
            }

            hasDeferredPrograms = false;
            if (dfb != 0) {
                EXTFramebufferObject.glDeleteFramebuffersEXT(dfb);
                dfb = 0;
                checkGLError("del dfb");
            }

            if (sfb != 0) {
                EXTFramebufferObject.glDeleteFramebuffersEXT(sfb);
                sfb = 0;
                checkGLError("del sfb");
            }

            if (dfbDepthTextures != null) {
                GlStateManager.deleteTextures(dfbDepthTextures);
                fillIntBufferZero(dfbDepthTextures);
                checkGLError("del dfbDepthTextures");
            }

            if (dfbColorTextures != null) {
                GlStateManager.deleteTextures(dfbColorTextures);
                fillIntBufferZero(dfbColorTextures);
                checkGLError("del dfbTextures");
            }

            if (sfbDepthTextures != null) {
                GlStateManager.deleteTextures(sfbDepthTextures);
                fillIntBufferZero(sfbDepthTextures);
                checkGLError("del shadow depth");
            }

            if (sfbColorTextures != null) {
                GlStateManager.deleteTextures(sfbColorTextures);
                fillIntBufferZero(sfbColorTextures);
                checkGLError("del shadow color");
            }

            if (dfbDrawBuffers != null) {
                fillIntBufferZero(dfbDrawBuffers);
            }

            if (noiseTexture != null) {
                noiseTexture.deleteTexture();
                noiseTexture = null;
            }

            SMCLog.info("Uninit");
            shadowPassInterval = 0;
            shouldSkipDefaultShadow = false;
            isShaderPackInitialized = false;
            checkGLError("Shaders.uninit");
        }
    }

    public static void scheduleResize() {
        renderDisplayHeight = 0;
    }

    public static void scheduleResizeShadow() {
        needResizeShadow = true;
    }

    public static void resize() {
        renderDisplayWidth = mc.displayWidth;
        renderDisplayHeight = mc.displayHeight;
        renderWidth = Math.round(renderDisplayWidth * configRenderResMul);
        renderHeight = Math.round(renderDisplayHeight * configRenderResMul);
        setupFrameBuffer();
    }

    public static void resizeShadow() {
        needResizeShadow = false;
        shadowMapWidth = Math.round(spShadowMapWidth * configShadowResMul);
        shadowMapHeight = Math.round(spShadowMapHeight * configShadowResMul);
        setupShadowFrameBuffer();
    }

    public static void setupFrameBuffer() {
        if (dfb != 0) {
            EXTFramebufferObject.glDeleteFramebuffersEXT(dfb);
            GlStateManager.deleteTextures(dfbDepthTextures);
            GlStateManager.deleteTextures(dfbColorTextures);
        }

        dfb = EXTFramebufferObject.glGenFramebuffersEXT();
        GL11.glGenTextures((IntBuffer)((Buffer)dfbDepthTextures).clear().limit(usedDepthBuffers));
        GL11.glGenTextures((IntBuffer)((Buffer)dfbColorTextures).clear().limit(16));
        ((Buffer)dfbDepthTextures).position(0);
        ((Buffer)dfbColorTextures).position(0);
        ((Buffer)dfbColorTextures.get(dfbColorTexturesA)).position(0);
        EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
        GL20.glDrawBuffers(0);
        GL11.glReadBuffer(0);

        for (int i = 0; i < usedDepthBuffers; i++) {
            GlStateManager.bindTexture(dfbDepthTextures.get(i));
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
            GL11.glTexParameteri(3553, 10241, 9728);
            GL11.glTexParameteri(3553, 10240, 9728);
            GL11.glTexParameteri(3553, 34891, 6409);
            GL11.glTexImage2D(3553, 0, 6402, renderWidth, renderHeight, 0, 6402, 5126, (FloatBuffer)null);
        }

        EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, dfbDepthTextures.get(0), 0);
        GL20.glDrawBuffers(dfbDrawBuffers);
        GL11.glReadBuffer(0);
        checkGLError("FT d");

        for (int i = 0; i < usedColorBuffers; i++) {
            GlStateManager.bindTexture(dfbColorTexturesA[i]);
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexImage2D(3553, 0, gbuffersFormat[i], renderWidth, renderHeight, 0, 32993, 33639, (ByteBuffer)null);
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTexturesA[i], 0);
            checkGLError("FT c");
        }

        for (int i = 0; i < usedColorBuffers; i++) {
            GlStateManager.bindTexture(dfbColorTexturesA[8 + i]);
            GL11.glTexParameteri(3553, 10242, 10496);
            GL11.glTexParameteri(3553, 10243, 10496);
            GL11.glTexParameteri(3553, 10241, 9729);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexImage2D(3553, 0, gbuffersFormat[i], renderWidth, renderHeight, 0, 32993, 33639, (ByteBuffer)null);
            checkGLError("FT ca");
        }

        int glCheckFramebufferStatusEXT = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
        if (glCheckFramebufferStatusEXT == 36058) {
            printChatAndLogError("[Shaders] Error: Failed framebuffer incomplete formats");

            for (int i = 0; i < usedColorBuffers; i++) {
                GlStateManager.bindTexture(dfbColorTextures.get(i));
                GL11.glTexImage2D(3553, 0, 6408, renderWidth, renderHeight, 0, 32993, 33639, (ByteBuffer)null);
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTextures.get(i), 0);
                checkGLError("FT c");
            }

            glCheckFramebufferStatusEXT = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
            if (glCheckFramebufferStatusEXT == 36053) {
                SMCLog.info("complete");
            }
        }

        GlStateManager.bindTexture(0);
        if (glCheckFramebufferStatusEXT != 36053) {
            printChatAndLogError("[Shaders] Error: Failed creating framebuffer! (Status " + glCheckFramebufferStatusEXT + ")");
        } else {
            SMCLog.info("Framebuffer created.");
        }
    }

    public static void setupShadowFrameBuffer() {
        if (usedShadowDepthBuffers != 0) {
            if (sfb != 0) {
                EXTFramebufferObject.glDeleteFramebuffersEXT(sfb);
                GlStateManager.deleteTextures(sfbDepthTextures);
                GlStateManager.deleteTextures(sfbColorTextures);
            }

            sfb = EXTFramebufferObject.glGenFramebuffersEXT();
            EXTFramebufferObject.glBindFramebufferEXT(36160, sfb);
            GL11.glDrawBuffer(0);
            GL11.glReadBuffer(0);
            GL11.glGenTextures((IntBuffer)((Buffer)sfbDepthTextures).clear().limit(usedShadowDepthBuffers));
            GL11.glGenTextures((IntBuffer)((Buffer)sfbColorTextures).clear().limit(usedShadowColorBuffers));
            ((Buffer)sfbDepthTextures).position(0);
            ((Buffer)sfbColorTextures).position(0);

            for (int i = 0; i < usedShadowDepthBuffers; i++) {
                GlStateManager.bindTexture(sfbDepthTextures.get(i));
                GL11.glTexParameterf(3553, 10242, 10496.0F);
                GL11.glTexParameterf(3553, 10243, 10496.0F);
                int n = shadowFilterNearest[i] ? 9728 : 9729;
                GL11.glTexParameteri(3553, 10241, n);
                GL11.glTexParameteri(3553, 10240, n);
                if (shadowHardwareFilteringEnabled[i]) {
                    GL11.glTexParameteri(3553, 34892, 34894);
                }

                GL11.glTexImage2D(3553, 0, 6402, shadowMapWidth, shadowMapHeight, 0, 6402, 5126, (FloatBuffer)null);
            }

            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, sfbDepthTextures.get(0), 0);
            checkGLError("FT sd");

            for (int i = 0; i < usedShadowColorBuffers; i++) {
                GlStateManager.bindTexture(sfbColorTextures.get(i));
                GL11.glTexParameterf(3553, 10242, 10496.0F);
                GL11.glTexParameterf(3553, 10243, 10496.0F);
                int n = shadowColorFilterNearest[i] ? 9728 : 9729;
                GL11.glTexParameteri(3553, 10241, n);
                GL11.glTexParameteri(3553, 10240, n);
                GL11.glTexImage2D(3553, 0, 6408, shadowMapWidth, shadowMapHeight, 0, 32993, 33639, (ByteBuffer)null);
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, sfbColorTextures.get(i), 0);
                checkGLError("FT sc");
            }

            GlStateManager.bindTexture(0);
            if (usedShadowColorBuffers > 0) {
                GL20.glDrawBuffers(sfbDrawBuffers);
            }

            int glCheckFramebufferStatusEXT = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
            if (glCheckFramebufferStatusEXT != 36053) {
                printChatAndLogError("[Shaders] Error: Failed creating shadow framebuffer! (Status " + glCheckFramebufferStatusEXT + ")");
            } else {
                SMCLog.info("Shadow framebuffer created.");
            }
        }
    }

    public static void beginRender(Minecraft mc2, float f, long l) {
        checkGLError("pre beginRender");
        checkWorldChanged(mc.theWorld);
        mc = mc2;
        mc.mcProfiler.startSection("init");
        entityRenderer = mc.entityRenderer;
        if (!isShaderPackInitialized) {
            try {
                init();
            } catch (IllegalStateException e) {
                if (Config.normalize(e.getMessage()).equals("Function is not supported")) {
                    printChatAndLogError("[Shaders] Error: " + e.getMessage());
                    e.printStackTrace();
                    setShaderPack(packNameNone);
                    return;
                }
            }
        }

        if (mc.displayWidth != renderDisplayWidth || mc.displayHeight != renderDisplayHeight) {
            resize();
        }

        if (needResizeShadow) {
            resizeShadow();
        }

        worldTime = mc.theWorld.getWorldTime();
        diffWorldTime = (worldTime - lastWorldTime) % 24000L;
        if (diffWorldTime < 0L) {
            diffWorldTime += 24000L;
        }

        lastWorldTime = worldTime;
        moonPhase = mc.theWorld.getMoonPhase();
        frameCounter++;
        if (frameCounter >= 720720) {
            frameCounter = 0;
        }

        systemTime = System.currentTimeMillis();
        if (lastSystemTime == 0L) {
            lastSystemTime = systemTime;
        }

        diffSystemTime = systemTime - lastSystemTime;
        lastSystemTime = systemTime;
        frameTime = (float)diffSystemTime / 1000.0F;
        frameTimeCounter = frameTimeCounter + frameTime;
        frameTimeCounter %= 3600.0F;
        rainStrength = mc2.theWorld.getRainStrength(f);
        float f2 = (float)diffSystemTime * 0.01F;
        float exp = (float)Math.exp(Math.log(0.5) * f2 / (wetness < rainStrength ? drynessHalfLife : wetnessHalfLife));
        wetness = wetness * exp + rainStrength * (1.0F - exp);
        EntityLivingBase renderViewEntity = mc.renderViewEntity;
        if (renderViewEntity != null) {
            isSleeping = renderViewEntity.isPlayerSleeping();
            eyePosY = (float)renderViewEntity.posY * f + (float)renderViewEntity.lastTickPosY * (1.0F - f);
            eyeBrightness = renderViewEntity.getBrightnessForRender(f);
            exp = (float)diffSystemTime * 0.01F;
            float exp2 = (float)Math.exp(Math.log(0.5) * exp / eyeBrightnessHalflife);
            eyeBrightnessFadeX = eyeBrightnessFadeX * exp2 + (eyeBrightness & 65535) * (1.0F - exp2);
            eyeBrightnessFadeY = eyeBrightnessFadeY * exp2 + (eyeBrightness >> 16) * (1.0F - exp2);
            isEyeInWater = 0;
            if (mc.gameSettings.thirdPersonView == 0 && !isSleeping) {
                if (renderViewEntity.isInsideOfMaterial(Material.water)) {
                    isEyeInWater = 1;
                } else if (renderViewEntity.isInsideOfMaterial(Material.lava)) {
                    isEyeInWater = 2;
                }
            }

            if (mc.thePlayer != null) {
                nightVision = 0.0F;
                if (mc.thePlayer.isPotionActive(Potion.nightVision)) {
                    nightVision = Config.getMinecraft().entityRenderer.getNightVisionBrightness(mc.thePlayer, f);
                }

                blindness = 0.0F;
                if (mc.thePlayer.isPotionActive(Potion.blindness)) {
                    int duration = mc.thePlayer.getActivePotionEffect(Potion.blindness).getDuration();
                    blindness = Config.limit(duration / 20.0F, 0.0F, 1.0F);
                }
            }

            Vec3 skyColor = mc.theWorld.getSkyColor(renderViewEntity, f);
            skyColor = CustomColorizer.getWorldSkyColor(skyColor, currentWorld, renderViewEntity, f);
            skyColorR = (float)skyColor.xCoord;
            skyColorG = (float)skyColor.yCoord;
            skyColorB = (float)skyColor.zCoord;
        }

        isRenderingWorld = true;
        isCompositeRendered = false;
        isHandRenderedMain = false;
        if (usedShadowDepthBuffers >= 1) {
            GlStateManager.setActiveTexture(33988);
            GlStateManager.bindTexture(sfbDepthTextures.get(0));
            if (usedShadowDepthBuffers >= 2) {
                GlStateManager.setActiveTexture(33989);
                GlStateManager.bindTexture(sfbDepthTextures.get(1));
            }
        }

        GlStateManager.setActiveTexture(33984);

        for (int i = 0; i < usedColorBuffers; i++) {
            GlStateManager.bindTexture(dfbColorTexturesA[i]);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10241, 9729);
            GlStateManager.bindTexture(dfbColorTexturesA[8 + i]);
            GL11.glTexParameteri(3553, 10240, 9729);
            GL11.glTexParameteri(3553, 10241, 9729);
        }

        GlStateManager.bindTexture(0);

        for (int i = 0; i < 4 && 4 + i < usedColorBuffers; i++) {
            GlStateManager.setActiveTexture(33991 + i);
            GlStateManager.bindTexture(dfbColorTextures.get(4 + i));
        }

        GlStateManager.setActiveTexture(33990);
        GlStateManager.bindTexture(dfbDepthTextures.get(0));
        if (usedDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33995);
            GlStateManager.bindTexture(dfbDepthTextures.get(1));
            if (usedDepthBuffers >= 3) {
                GlStateManager.setActiveTexture(33996);
                GlStateManager.bindTexture(dfbDepthTextures.get(2));
            }
        }

        for (int i = 0; i < usedShadowColorBuffers; i++) {
            GlStateManager.setActiveTexture(33997 + i);
            GlStateManager.bindTexture(sfbColorTextures.get(i));
        }

        if (noiseTextureEnabled) {
            GlStateManager.setActiveTexture(33984 + noiseTexture.getTextureUnit());
            GlStateManager.bindTexture(noiseTexture.getTextureId());
        }

        bindCustomTextures(customTexturesGbuffers);
        GlStateManager.setActiveTexture(33984);
        previousCameraPositionX = cameraPositionX;
        previousCameraPositionY = cameraPositionY;
        previousCameraPositionZ = cameraPositionZ;
        ((Buffer)previousProjection).position(0);
        ((Buffer)projection).position(0);
        previousProjection.put(projection);
        ((Buffer)previousProjection).position(0);
        ((Buffer)projection).position(0);
        ((Buffer)previousModelView).position(0);
        ((Buffer)modelView).position(0);
        previousModelView.put(modelView);
        ((Buffer)previousModelView).position(0);
        ((Buffer)modelView).position(0);
        checkGLError("beginRender");
        ShadersRender.renderShadowMap(entityRenderer, 0, f, l);
        mc.mcProfiler.endSection();
        EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);

        for (int i = 0; i < usedColorBuffers; i++) {
            colorTexturesToggle[i] = 0;
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTexturesA[i], 0);
        }

        checkGLError("end beginRender");
    }

    public static void checkWorldChanged(World world) {
        if (currentWorld != world) {
            World currentWorld2 = currentWorld;
            currentWorld = world;
            if (currentWorld2 != null && world != null) {
                int dimensionId = currentWorld2.provider.dimensionId;
                int dimensionId2 = world.provider.dimensionId;
                boolean flag = shaderPackDimensions.contains(dimensionId);
                boolean flag2 = shaderPackDimensions.contains(dimensionId2);
                if (flag || flag2) {
                    uninit();
                }
            }

            Smoother.reset();
        }
    }

    public static void beginRenderPass(int n, float f, long l) {
        if (!isShadowPass) {
            EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
            GL11.glViewport(0, 0, renderWidth, renderHeight);
            activeDrawBuffers = null;
            ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
            useProgram(2);
            checkGLError("end beginRenderPass");
        }
    }

    public static void setViewport(int n, int n2, int n3, int n4) {
        GlStateManager.colorMask(true, true, true, true);
        if (isShadowPass) {
            GL11.glViewport(0, 0, shadowMapWidth, shadowMapHeight);
        } else {
            GL11.glViewport(0, 0, renderWidth, renderHeight);
            EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
            isRenderingDfb = true;
            GlStateManager.enableCull();
            GlStateManager.enableDepth();
            setDrawBuffers(drawBuffersNone);
            useProgram(2);
            checkGLError("beginRenderPass");
        }
    }

    public static int setFogMode(int n) {
        fogMode = n;
        return n;
    }

    public static void setFogColor(float f, float f2, float f3) {
        fogColorR = f;
        fogColorG = f2;
        fogColorB = f3;
        setProgramUniform3f("fogColor", fogColorR, fogColorG, fogColorB);
    }

    public static void setClearColor(float f, float f2, float f3, float f4) {
        GlStateManager.clearColor(f, f2, f3, f4);
        clearColorR = f;
        clearColorG = f2;
        clearColorB = f3;
    }

    public static void clearRenderBuffer() {
        if (isShadowPass) {
            checkGLError("shadow clear pre");
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, sfbDepthTextures.get(0), 0);
            GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
            GL20.glDrawBuffers(programsDrawBuffers[30]);
            checkFramebufferStatus("shadow clear");
            GL11.glClear(16640);
            checkGLError("shadow clear");
        } else {
            checkGLError("clear pre");
            if (gbuffersClear[0]) {
                GL20.glDrawBuffers(36064);
                GL11.glClear(16384);
            }

            if (gbuffersClear[1]) {
                GL20.glDrawBuffers(36065);
                GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
                GL11.glClear(16384);
            }

            for (int i = 2; i < usedColorBuffers; i++) {
                if (gbuffersClear[i]) {
                    GL20.glDrawBuffers(36064 + i);
                    GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
                    GL11.glClear(16384);
                }
            }

            setDrawBuffers(dfbDrawBuffers);
            checkFramebufferStatus("clear");
            checkGLError("clear");
        }
    }

    public static void setCamera(float f) {
        EntityLivingBase renderViewEntity = mc.renderViewEntity;
        double d = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * f;
        double d2 = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * f;
        double d3 = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * f;
        cameraPositionX = d;
        cameraPositionY = d2;
        cameraPositionZ = d3;
        GL11.glGetFloat(2983, (FloatBuffer)((Buffer)projection).position(0));
        SMath.invertMat4FBFA(
            (FloatBuffer)((Buffer)projectionInverse).position(0), (FloatBuffer)((Buffer)projection).position(0), faProjectionInverse, faProjection
        );
        ((Buffer)projection).position(0);
        ((Buffer)projectionInverse).position(0);
        GL11.glGetFloat(2982, (FloatBuffer)((Buffer)modelView).position(0));
        SMath.invertMat4FBFA((FloatBuffer)((Buffer)modelViewInverse).position(0), (FloatBuffer)((Buffer)modelView).position(0), faModelViewInverse, faModelView);
        ((Buffer)modelView).position(0);
        ((Buffer)modelViewInverse).position(0);
        checkGLError("setCamera");
    }

    public static void setCameraShadow(float f) {
        EntityLivingBase renderViewEntity = mc.renderViewEntity;
        double d = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * f;
        double d2 = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * f;
        double d3 = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * f;
        cameraPositionX = d;
        cameraPositionY = d2;
        cameraPositionZ = d3;
        GL11.glGetFloat(2983, (FloatBuffer)((Buffer)projection).position(0));
        SMath.invertMat4FBFA(
            (FloatBuffer)((Buffer)projectionInverse).position(0), (FloatBuffer)((Buffer)projection).position(0), faProjectionInverse, faProjection
        );
        ((Buffer)projection).position(0);
        ((Buffer)projectionInverse).position(0);
        GL11.glGetFloat(2982, (FloatBuffer)((Buffer)modelView).position(0));
        SMath.invertMat4FBFA((FloatBuffer)((Buffer)modelViewInverse).position(0), (FloatBuffer)((Buffer)modelView).position(0), faModelViewInverse, faModelView);
        ((Buffer)modelView).position(0);
        ((Buffer)modelViewInverse).position(0);
        GL11.glViewport(0, 0, shadowMapWidth, shadowMapHeight);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        if (shadowMapIsOrtho) {
            GL11.glOrtho(-shadowMapHalfPlane, shadowMapHalfPlane, -shadowMapHalfPlane, shadowMapHalfPlane, 0.05F, 256.0);
        } else {
            GLU.gluPerspective(shadowMapFOV, (float)shadowMapWidth / shadowMapHeight, 0.05F, 256.0F);
        }

        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -100.0F);
        GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
        celestialAngle = mc.theWorld.getCelestialAngle(f);
        sunAngle = celestialAngle < 0.75F ? celestialAngle + 0.25F : celestialAngle - 0.75F;
        float f2 = celestialAngle * -360.0F;
        float f3 = shadowAngleInterval > 0.0F ? f2 % shadowAngleInterval - shadowAngleInterval * 0.5F : 0.0F;
        if (sunAngle <= 0.5) {
            GL11.glRotatef(f2 - f3, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(sunPathRotation, 1.0F, 0.0F, 0.0F);
            shadowAngle = sunAngle;
        } else {
            GL11.glRotatef(f2 + 180.0F - f3, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(sunPathRotation, 1.0F, 0.0F, 0.0F);
            shadowAngle = sunAngle - 0.5F;
        }

        if (shadowMapIsOrtho) {
            float shadowIntervalSize2 = shadowIntervalSize;
            float f4 = shadowIntervalSize2 / 2.0F;
            GL11.glTranslatef((float)d % shadowIntervalSize2 - f4, (float)d2 % shadowIntervalSize2 - f4, (float)d3 % shadowIntervalSize2 - f4);
        }

        float f4 = sunAngle * (float) (Math.PI * 2);
        float cos = (float)Math.cos(f4);
        float sin = (float)Math.sin(f4);
        float f5 = sunPathRotation * (float) (Math.PI * 2);
        float f6 = cos;
        float f7 = sin * (float)Math.cos(f5);
        float f8 = sin * (float)Math.sin(f5);
        if (sunAngle > 0.5) {
            f6 = -cos;
            f7 = -f7;
            f8 = -f8;
        }

        shadowLightPositionVector[0] = f6;
        shadowLightPositionVector[1] = f7;
        shadowLightPositionVector[2] = f8;
        shadowLightPositionVector[3] = 0.0F;
        GL11.glGetFloat(2983, (FloatBuffer)((Buffer)shadowProjection).position(0));
        SMath.invertMat4FBFA(
            (FloatBuffer)((Buffer)shadowProjectionInverse).position(0),
            (FloatBuffer)((Buffer)shadowProjection).position(0),
            faShadowProjectionInverse,
            faShadowProjection
        );
        ((Buffer)shadowProjection).position(0);
        ((Buffer)shadowProjectionInverse).position(0);
        GL11.glGetFloat(2982, (FloatBuffer)((Buffer)shadowModelView).position(0));
        SMath.invertMat4FBFA(
            (FloatBuffer)((Buffer)shadowModelViewInverse).position(0),
            (FloatBuffer)((Buffer)shadowModelView).position(0),
            faShadowModelViewInverse,
            faShadowModelView
        );
        ((Buffer)shadowModelView).position(0);
        ((Buffer)shadowModelViewInverse).position(0);
        setProgramUniformMatrix4ARB("gbufferProjection", false, projection);
        setProgramUniformMatrix4ARB("gbufferProjectionInverse", false, projectionInverse);
        setProgramUniformMatrix4ARB("gbufferPreviousProjection", false, previousProjection);
        setProgramUniformMatrix4ARB("gbufferModelView", false, modelView);
        setProgramUniformMatrix4ARB("gbufferModelViewInverse", false, modelViewInverse);
        setProgramUniformMatrix4ARB("gbufferPreviousModelView", false, previousModelView);
        setProgramUniformMatrix4ARB("shadowProjection", false, shadowProjection);
        setProgramUniformMatrix4ARB("shadowProjectionInverse", false, shadowProjectionInverse);
        setProgramUniformMatrix4ARB("shadowModelView", false, shadowModelView);
        setProgramUniformMatrix4ARB("shadowModelViewInverse", false, shadowModelViewInverse);
        mc.gameSettings.thirdPersonView = 1;
        checkGLError("setCamera");
    }

    public static void preCelestialRotate() {
        GL11.glRotatef(sunPathRotation * 1.0F, 0.0F, 0.0F, 1.0F);
        checkGLError("preCelestialRotate");
    }

    public static void postCelestialRotate() {
        FloatBuffer tempMatrixDirectBuffer2 = tempMatrixDirectBuffer;
        ((Buffer)tempMatrixDirectBuffer2).clear();
        GL11.glGetFloat(2982, tempMatrixDirectBuffer2);
        tempMatrixDirectBuffer2.get(tempMat, 0, 16);
        SMath.multiplyMat4xVec4(sunPosition, tempMat, sunPosModelView);
        SMath.multiplyMat4xVec4(moonPosition, tempMat, moonPosModelView);
        System.arraycopy(shadowAngle == sunAngle ? sunPosition : moonPosition, 0, shadowLightPosition, 0, 3);
        setProgramUniform3f("sunPosition", sunPosition[0], sunPosition[1], sunPosition[2]);
        setProgramUniform3f("moonPosition", moonPosition[0], moonPosition[1], moonPosition[2]);
        setProgramUniform3f("shadowLightPosition", shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
        checkGLError("postCelestialRotate");
    }

    public static void setUpPosition() {
        FloatBuffer tempMatrixDirectBuffer2 = tempMatrixDirectBuffer;
        ((Buffer)tempMatrixDirectBuffer2).clear();
        GL11.glGetFloat(2982, tempMatrixDirectBuffer2);
        tempMatrixDirectBuffer2.get(tempMat, 0, 16);
        SMath.multiplyMat4xVec4(upPosition, tempMat, upPosModelView);
        setProgramUniform3f("upPosition", upPosition[0], upPosition[1], upPosition[2]);
    }

    public static void genCompositeMipmap() {
        if (hasGlGenMipmap) {
            for (int i = 0; i < usedColorBuffers; i++) {
                if ((activeCompositeMipmapSetting & 1 << i) != 0) {
                    GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[i]);
                    GL11.glTexParameteri(3553, 10241, 9987);
                    GL30.glGenerateMipmap(3553);
                }
            }

            GlStateManager.setActiveTexture(33984);
        }
    }

    public static void drawComposite() {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glBegin(7);
        GL11.glTexCoord2f(0.0F, 0.0F);
        GL11.glVertex3f(0.0F, 0.0F, 0.0F);
        GL11.glTexCoord2f(1.0F, 0.0F);
        GL11.glVertex3f(1.0F, 0.0F, 0.0F);
        GL11.glTexCoord2f(1.0F, 1.0F);
        GL11.glVertex3f(1.0F, 1.0F, 0.0F);
        GL11.glTexCoord2f(0.0F, 1.0F);
        GL11.glVertex3f(0.0F, 1.0F, 0.0F);
        GL11.glEnd();
    }

    public static void renderDeferred() {
        if (hasDeferredPrograms) {
            checkGLError("pre-render Deferred");
            renderComposites(33, 8, false);
            mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        }
    }

    public static void renderCompositeFinal() {
        checkGLError("pre-render CompositeFinal");
        renderComposites(21, 8, true);
    }

    public static void renderComposites(int n, int n2, boolean flag) {
        if (!isShadowPass) {
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glMatrixMode(5889);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0, 1.0, 0.0, 1.0, 0.0, 1.0);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            GlStateManager.enableDepth();
            GlStateManager.depthFunc(519);
            GlStateManager.depthMask(false);
            GlStateManager.disableLighting();
            if (usedShadowDepthBuffers >= 1) {
                GlStateManager.setActiveTexture(33988);
                GlStateManager.bindTexture(sfbDepthTextures.get(0));
                if (usedShadowDepthBuffers >= 2) {
                    GlStateManager.setActiveTexture(33989);
                    GlStateManager.bindTexture(sfbDepthTextures.get(1));
                }
            }

            for (int i = 0; i < usedColorBuffers; i++) {
                GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[i]);
                GlStateManager.bindTexture(dfbColorTexturesA[i]);
            }

            GlStateManager.setActiveTexture(33990);
            GlStateManager.bindTexture(dfbDepthTextures.get(0));
            if (usedDepthBuffers >= 2) {
                GlStateManager.setActiveTexture(33995);
                GlStateManager.bindTexture(dfbDepthTextures.get(1));
                if (usedDepthBuffers >= 3) {
                    GlStateManager.setActiveTexture(33996);
                    GlStateManager.bindTexture(dfbDepthTextures.get(2));
                }
            }

            for (int i = 0; i < usedShadowColorBuffers; i++) {
                GlStateManager.setActiveTexture(33997 + i);
                GlStateManager.bindTexture(sfbColorTextures.get(i));
            }

            if (noiseTextureEnabled) {
                GlStateManager.setActiveTexture(33984 + noiseTexture.getTextureUnit());
                GlStateManager.bindTexture(noiseTexture.getTextureId());
            }

            if (flag) {
                bindCustomTextures(customTexturesComposite);
            } else {
                bindCustomTextures(customTexturesDeferred);
            }

            GlStateManager.setActiveTexture(33984);
            boolean flag2 = true;

            for (int i = 0; i < usedColorBuffers; i++) {
                EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTexturesA[8 + i], 0);
            }

            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, dfbDepthTextures.get(0), 0);
            GL20.glDrawBuffers(dfbDrawBuffers);
            checkGLError("pre-composite");

            for (int i = 0; i < n2; i++) {
                if (programsID[n + i] != 0) {
                    useProgram(n + i);
                    checkGLError(programNames[n + i]);
                    if (activeCompositeMipmapSetting != 0) {
                        genCompositeMipmap();
                    }

                    drawComposite();

                    for (int j = 0; j < usedColorBuffers; j++) {
                        if (programsToggleColorTextures[n + i][j]) {
                            int n3 = colorTexturesToggle[j];
                            int n4 = colorTexturesToggle[j] = 8 - n3;
                            GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[j]);
                            GlStateManager.bindTexture(dfbColorTexturesA[n4 + j]);
                            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + j, 3553, dfbColorTexturesA[n3 + j], 0);
                        }
                    }

                    GlStateManager.setActiveTexture(33984);
                }
            }

            checkGLError("composite");
            int n3 = flag ? 43 : 42;
            if (programsID[n3] != 0) {
                useProgram(n3);
                checkGLError(programNames[n3]);
                if (activeCompositeMipmapSetting != 0) {
                    genCompositeMipmap();
                }

                drawComposite();

                for (int i = 0; i < usedColorBuffers; i++) {
                    if (programsToggleColorTextures[n3][i]) {
                        int n4 = colorTexturesToggle[i];
                        int n5 = colorTexturesToggle[i] = 8 - n4;
                        GlStateManager.setActiveTexture(33984 + colorTextureTextureImageUnit[i]);
                        GlStateManager.bindTexture(dfbColorTexturesA[n5 + i]);
                        EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTexturesA[n4 + i], 0);
                    }
                }

                GlStateManager.setActiveTexture(33984);
            }

            if (flag) {
                renderFinal();
            }

            if (flag) {
                isCompositeRendered = true;
            }

            if (!flag) {
                for (int i = 0; i < usedColorBuffers; i++) {
                    EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTexturesA[i], 0);
                }

                setDrawBuffers(programsDrawBuffers[12]);
            }

            GlStateManager.enableLighting();
            GlStateManager.enableTexture2D();
            GlStateManager.enableAlpha();
            GlStateManager.enableBlend();
            GlStateManager.depthFunc(515);
            GlStateManager.depthMask(true);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
            GL11.glPopMatrix();
            useProgram(0);
        }
    }

    public static void renderFinal() {
        isRenderingDfb = false;
        mc.getFramebuffer().bindFramebuffer(true);
        OpenGlHelper.advancedFrameBufferTexture2D(
            GlStateManager.GL_FRAMEBUFFER, GlStateManager.GL_COLOR_ATTACHMENT0, 3553, mc.getFramebuffer().framebufferTexture, 0
        );
        GL11.glViewport(0, 0, mc.displayWidth, mc.displayHeight);
        if (EntityRenderer.anaglyphEnable) {
            boolean flag = EntityRenderer.anaglyphField != 0;
            GlStateManager.colorMask(flag, !flag, !flag, true);
        }

        GlStateManager.depthMask(true);
        GL11.glClearColor(clearColorR, clearColorG, clearColorB, 1.0F);
        GL11.glClear(16640);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.depthFunc(519);
        GlStateManager.depthMask(false);
        checkGLError("pre-final");
        useProgram(29);
        checkGLError("final");
        if (activeCompositeMipmapSetting != 0) {
            genCompositeMipmap();
        }

        drawComposite();
        checkGLError("renderCompositeFinal");
    }

    public static void endRender() {
        if (isShadowPass) {
            checkGLError("shadow endRender");
        } else {
            if (!isCompositeRendered) {
                renderCompositeFinal();
            }

            isRenderingWorld = false;
            GlStateManager.colorMask(true, true, true, true);
            useProgram(0);
            RenderHelper.disableStandardItemLighting();
            checkGLError("endRender end");
        }
    }

    public static void beginSky() {
        isRenderingSky = true;
        fogEnabled = true;
        setDrawBuffers(dfbDrawBuffers);
        useProgram(5);
        pushEntity(-2, 0);
    }

    public static void setSkyColor(Vec3 vec3) {
        skyColorR = (float)vec3.xCoord;
        skyColorG = (float)vec3.yCoord;
        skyColorB = (float)vec3.zCoord;
        setProgramUniform3f("skyColor", skyColorR, skyColorG, skyColorB);
    }

    public static void drawHorizon() {
        Tessellator instance = Tessellator.instance;
        float f = mc.gameSettings.renderDistanceChunks * 16;
        double d = f * 0.9238;
        double d2 = f * 0.3826;
        double d3 = -d2;
        double d4 = -d;
        double d5 = 16.0;
        double d6 = -cameraPositionY;
        instance.startDrawingQuads();
        instance.addVertex(d3, d6, d4);
        instance.addVertex(d3, d5, d4);
        instance.addVertex(d4, d5, d3);
        instance.addVertex(d4, d6, d3);
        instance.addVertex(d4, d6, d3);
        instance.addVertex(d4, d5, d3);
        instance.addVertex(d4, d5, d2);
        instance.addVertex(d4, d6, d2);
        instance.addVertex(d4, d6, d2);
        instance.addVertex(d4, d5, d2);
        instance.addVertex(d3, d5, d2);
        instance.addVertex(d3, d6, d2);
        instance.addVertex(d3, d6, d2);
        instance.addVertex(d3, d5, d2);
        instance.addVertex(d2, d5, d);
        instance.addVertex(d2, d6, d);
        instance.addVertex(d2, d6, d);
        instance.addVertex(d2, d5, d);
        instance.addVertex(d, d5, d2);
        instance.addVertex(d, d6, d2);
        instance.addVertex(d, d6, d2);
        instance.addVertex(d, d5, d2);
        instance.addVertex(d, d5, d3);
        instance.addVertex(d, d6, d3);
        instance.addVertex(d, d6, d3);
        instance.addVertex(d, d5, d3);
        instance.addVertex(d2, d5, d4);
        instance.addVertex(d2, d6, d4);
        instance.addVertex(d2, d6, d4);
        instance.addVertex(d2, d5, d4);
        instance.addVertex(d3, d5, d4);
        instance.addVertex(d3, d6, d4);
        instance.draw();
    }

    public static void preSkyList() {
        setUpPosition();
        GL11.glColor3f(fogColorR, fogColorG, fogColorB);
        drawHorizon();
        GL11.glColor3f(skyColorR, skyColorG, skyColorB);
    }

    public static void endSky() {
        isRenderingSky = false;
        setDrawBuffers(dfbDrawBuffers);
        useProgram(lightmapEnabled ? 3 : 2);
        popEntity();
    }

    public static void beginUpdateChunks() {
        checkGLError("beginUpdateChunks1");
        checkFramebufferStatus("beginUpdateChunks1");
        if (!isShadowPass) {
            useProgram(7);
        }

        checkGLError("beginUpdateChunks2");
        checkFramebufferStatus("beginUpdateChunks2");
    }

    public static void endUpdateChunks() {
        checkGLError("endUpdateChunks1");
        checkFramebufferStatus("endUpdateChunks1");
        if (!isShadowPass) {
            useProgram(7);
        }

        checkGLError("endUpdateChunks2");
        checkFramebufferStatus("endUpdateChunks2");
    }

    public static boolean shouldRenderClouds(GameSettings gameSettings) {
        if (!shaderPackLoaded) {
            return true;
        }

        checkGLError("shouldRenderClouds");
        return isShadowPass ? configCloudShadow : gameSettings.clouds;
    }

    public static void beginClouds() {
        fogEnabled = true;
        pushEntity(-3, 0);
        useProgram(6);
    }

    public static void endClouds() {
        disableFog();
        popEntity();
        useProgram(lightmapEnabled ? 3 : 2);
    }

    public static void beginTerrain() {
        if (isRenderingWorld) {
            if (isShadowPass) {
                GL11.glDisable(2884);
            }

            fogEnabled = true;
            useProgram(7);
        }
    }

    public static void endTerrain() {
        if (isRenderingWorld) {
            if (isShadowPass) {
                GL11.glEnable(2884);
            }

            useProgram(lightmapEnabled ? 3 : 2);
        }
    }

    public static void beginEntities() {
        if (isRenderingWorld) {
            useProgram(16);
            resetDisplayListModels();
        }
    }

    public static void nextEntity(Entity entity) {
        if (isRenderingWorld) {
            useProgram(16);
            setEntityId(entity);
        }
    }

    public static void setEntityId(Entity entity) {
        if (isRenderingWorld && !isShadowPass && uniformEntityId.isDefined()) {
            int entityID = EntityList.getEntityID(entity);
            uniformEntityId.setValue(entityID);
        }
    }

    public static void beginSpiderEyes() {
        if (isRenderingWorld && programsID[18] != programsID[0]) {
            useProgram(18);
            GlStateManager.enableAlpha();
            GlStateManager.alphaFunc(516, 0.0F);
            GlStateManager.blendFunc(770, 771);
        }
    }

    public static void endSpiderEyes() {
        if (isRenderingWorld && programsID[18] != programsID[0]) {
            useProgram(16);
            GlStateManager.disableAlpha();
        }
    }

    public static void endEntities() {
        if (isRenderingWorld) {
            useProgram(lightmapEnabled ? 3 : 2);
        }
    }

    public static void setEntityColor(float f, float f2, float f3, float f4) {
        if (isRenderingWorld && !isShadowPass) {
            uniformEntityColor.setValue(f, f2, f3, f4);
        }
    }

    public static void beginLivingDamage() {
        if (isRenderingWorld) {
            ShadersTex.bindTexture(defaultTexture);
            if (!isShadowPass) {
                setDrawBuffers(drawBuffersColorAtt0);
            }
        }
    }

    public static void endLivingDamage() {
        if (isRenderingWorld && !isShadowPass) {
            setDrawBuffers(programsDrawBuffers[16]);
        }
    }

    public static void beginBlockEntities() {
        if (isRenderingWorld) {
            checkGLError("beginBlockEntities");
            useProgram(13);
        }
    }

    public static void nextBlockEntity(TileEntity tileEntity) {
        if (isRenderingWorld) {
            checkGLError("nextBlockEntity");
            useProgram(13);
            setBlockEntityId(tileEntity);
        }
    }

    public static void setBlockEntityId(TileEntity tileEntity) {
        if (isRenderingWorld && !isShadowPass && uniformBlockEntityId.isDefined()) {
            Block blockType = tileEntity.getBlockType();
            int idFromBlock = Block.getIdFromBlock(blockType);
            uniformBlockEntityId.setValue(idFromBlock);
        }
    }

    public static void endBlockEntities() {
        if (isRenderingWorld) {
            checkGLError("endBlockEntities");
            useProgram(lightmapEnabled ? 3 : 2);
            ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
        }
    }

    public static void beginBlockDestroyProgress() {
        if (isRenderingWorld) {
            useProgram(7);
            if (configTweakBlockDamage) {
                setDrawBuffers(drawBuffersColorAtt0);
                GL11.glDepthMask(false);
            }
        }
    }

    public static void endBlockDestroyProgress() {
        if (isRenderingWorld) {
            GL11.glDepthMask(true);
            useProgram(3);
        }
    }

    public static void beginLitParticles() {
        useProgram(3);
    }

    public static void beginParticles() {
        useProgram(2);
    }

    public static void endParticles() {
        useProgram(3);
    }

    public static void readCenterDepth() {
        if (!isShadowPass && centerDepthSmoothEnabled) {
            ((Buffer)tempDirectFloatBuffer).clear();
            GL11.glReadPixels(renderWidth / 2, renderHeight / 2, 1, 1, 6402, 5126, tempDirectFloatBuffer);
            centerDepth = tempDirectFloatBuffer.get(0);
            float f = (float)diffSystemTime * 0.01F;
            float exp = (float)Math.exp(Math.log(0.5) * f / centerDepthSmoothHalflife);
            centerDepthSmooth = centerDepthSmooth * exp + centerDepth * (1.0F - exp);
        }
    }

    public static void beginWeather() {
        if (!isShadowPass) {
            if (usedDepthBuffers >= 3) {
                GlStateManager.setActiveTexture(33996);
                GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, renderWidth, renderHeight);
                GlStateManager.setActiveTexture(33984);
            }

            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(770, 771);
            GlStateManager.enableAlpha();
            useProgram(20);
        }
    }

    public static void endWeather() {
        GlStateManager.disableBlend();
        useProgram(3);
    }

    public static void preWater() {
        if (usedDepthBuffers >= 2) {
            GlStateManager.setActiveTexture(33995);
            checkGLError("pre copy depth");
            GL11.glCopyTexSubImage2D(3553, 0, 0, 0, 0, 0, renderWidth, renderHeight);
            checkGLError("copy depth");
            GlStateManager.setActiveTexture(33984);
        }

        ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
    }

    public static void beginWater() {
        if (isRenderingWorld) {
            if (!isShadowPass) {
                renderDeferred();
                useProgram(12);
                GlStateManager.enableBlend();
                GlStateManager.depthMask(true);
            } else {
                GlStateManager.depthMask(true);
            }
        }
    }

    public static void endWater() {
        if (isRenderingWorld) {
            if (isShadowPass) {
            }

            useProgram(lightmapEnabled ? 3 : 2);
        }
    }

    public static void beginProjectRedHalo() {
        if (isRenderingWorld) {
            useProgram(1);
        }
    }

    public static void endProjectRedHalo() {
        if (isRenderingWorld) {
            useProgram(3);
        }
    }

    public static void applyHandDepth() {
        if (configHandDepthMul != 1.0) {
            GL11.glScaled(1.0, 1.0, configHandDepthMul);
        }
    }

    public static void beginHand(boolean flag) {
        GL11.glMatrixMode(5888);
        GL11.glPushMatrix();
        GL11.glMatrixMode(5889);
        GL11.glPushMatrix();
        GL11.glMatrixMode(5888);
        if (flag) {
            useProgram(41);
        } else {
            useProgram(19);
        }

        checkGLError("beginHand");
        checkFramebufferStatus("beginHand");
    }

    public static void endHand() {
        checkGLError("pre endHand");
        checkFramebufferStatus("pre endHand");
        GL11.glMatrixMode(5889);
        GL11.glPopMatrix();
        GL11.glMatrixMode(5888);
        GL11.glPopMatrix();
        GlStateManager.blendFunc(770, 771);
        checkGLError("endHand");
    }

    public static void beginFPOverlay() {
        GlStateManager.disableLighting();
        GlStateManager.disableBlend();
    }

    public static void endFPOverlay() {
    }

    public static void enableTexture2D() {
        if (isRenderingSky) {
            useProgram(5);
        } else if (activeProgram == 1) {
            useProgram(lightmapEnabled ? 3 : 2);
        }
    }

    public static void disableTexture2D() {
        if (isRenderingSky) {
            useProgram(4);
        } else if (activeProgram == 2 || activeProgram == 3) {
            useProgram(1);
        }
    }

    public static void beginLeash() {
        useProgram(1);
    }

    public static void endLeash() {
        useProgram(16);
    }

    public static void enableFog() {
        fogEnabled = true;
        setProgramUniform1i("fogMode", fogMode);
    }

    public static void disableFog() {
        fogEnabled = false;
        setProgramUniform1i("fogMode", 0);
    }

    public static void sglFogi(int n, int n2) {
        GL11.glFogi(n, n2);
        if (n == 2917) {
            fogMode = n2;
            if (fogEnabled) {
                setProgramUniform1i("fogMode", fogMode);
            }
        }
    }

    public static void enableLightmap() {
        lightmapEnabled = true;
        if (activeProgram == 2) {
            useProgram(3);
        }
    }

    public static void disableLightmap() {
        lightmapEnabled = false;
        if (activeProgram == 3) {
            useProgram(2);
        }
    }

    public static int getEntityData() {
        return entityData[entityDataIndex * 2];
    }

    public static int getEntityData2() {
        return entityData[entityDataIndex * 2 + 1];
    }

    public static int setEntityData1(int n) {
        entityData[entityDataIndex * 2] = entityData[entityDataIndex * 2] & 65535 | n << 16;
        return n;
    }

    public static int setEntityData2(int n) {
        entityData[entityDataIndex * 2 + 1] = entityData[entityDataIndex * 2 + 1] & -65536 | n & 65535;
        return n;
    }

    public static void pushEntity(int n, int n2) {
        entityDataIndex++;
        entityData[entityDataIndex * 2] = n & 65535 | n2 << 16;
        entityData[entityDataIndex * 2 + 1] = 0;
    }

    public static void pushEntity(Block block) {
        int iDForObject = Block.blockRegistry.getIDForObject(block);
        byte b = 0;
        iDForObject = BlockAliases.getMappedBlockId(iDForObject, b);
        entityDataIndex++;
        entityData[entityDataIndex * 2] = iDForObject & 65535 | block.getRenderType() << 16;
        entityData[entityDataIndex * 2 + 1] = b;
    }

    public static void pushEntity(RenderBlocks renderBlocks, Block block, int n, int n2, int n3) {
        int iDForObject = Block.blockRegistry.getIDForObject(block);
        int blockMetadata = renderBlocks.blockAccess.getBlockMetadata(n, n2, n3);
        iDForObject = BlockAliases.getMappedBlockId(iDForObject, blockMetadata);
        entityDataIndex++;
        entityData[entityDataIndex * 2] = iDForObject & 65535 | block.getRenderType() << 16;
        entityData[entityDataIndex * 2 + 1] = blockMetadata;
    }

    public static void popEntity() {
        entityData[entityDataIndex * 2] = 0;
        entityData[entityDataIndex * 2 + 1] = 0;
        entityDataIndex--;
    }

    public static void mcProfilerEndSection() {
        mc.mcProfiler.endSection();
    }

    public static String getShaderPackName() {
        return shaderPack == null ? null : (shaderPack instanceof ShaderPackNone ? null : shaderPack.getName());
    }

    public static InputStream getShaderPackResourceStream(String str) {
        return shaderPack == null ? null : shaderPack.getResourceAsStream(str);
    }

    public static void nextAntialiasingLevel() {
        configAntialiasingLevel += 2;
        configAntialiasingLevel = configAntialiasingLevel / 2 * 2;
        if (configAntialiasingLevel > 4) {
            configAntialiasingLevel = 0;
        }

        configAntialiasingLevel = Config.limit(configAntialiasingLevel, 0, 4);
    }

    public static void checkShadersModInstalled() {
        Class clazz = null;

        try {
            clazz = Class.forName("shadersmod.transform.SMCClassTransformer");
        } catch (Throwable e) {
        }

        try {
            clazz = Class.forName("shadersmodcore.transform.SMCClassTransformer");
        } catch (Throwable e) {
        }

        if (clazz != null) {
            throw new RuntimeException("Shaders Mod detected. Please remove it, OptiFine has built-in support for shaders.");
        }
    }

    public static void resourcesReloaded() {
        loadShaderPackResources();
        if (shaderPackLoaded) {
            BlockAliases.resourcesReloaded();
        }
    }

    public static void loadShaderPackResources() {
        shaderPackResources = new HashMap<>();
        if (shaderPackLoaded) {
            ArrayList list = new ArrayList();
            String str = "/shaders/lang/";
            String str2 = "en_US";
            String str3 = ".lang";
            list.add(str + str2 + str3);
            if (!Config.getGameSettings().language.equals(str2)) {
                list.add(str + Config.getGameSettings().language + str3);
            }

            try {
                for (String str4 : (Iterable<String>)list) {
                    InputStream resourceAsStream = shaderPack.getResourceAsStream(str4);
                    if (resourceAsStream != null) {
                        Properties properties = new Properties();
                        Lang.loadLocaleData(resourceAsStream, properties);
                        resourceAsStream.close();

                        for (String str5 : (Iterable<String>)(Iterable)properties.keySet()) {
                            String property = properties.getProperty(str5);
                            shaderPackResources.put(str5, property);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String translate(String str, String str2) {
        String str3 = shaderPackResources.get(str);
        return str3 == null ? str2 : str3;
    }

    public static boolean isProgramPath(String str) {
        if (str == null) {
            return false;
        }

        if (str.length() <= 0) {
            return false;
        }

        int lastIndexOf = str.lastIndexOf("/");
        if (lastIndexOf >= 0) {
            str = str.substring(lastIndexOf + 1);
        }

        return Arrays.asList(programNames).contains(str);
    }

    public static void setItemToRenderMain(ItemStack itemStack) {
        itemToRenderMainTranslucent = isTranslucentBlock(itemStack);
    }

    public static boolean isItemToRenderMainTranslucent() {
        return itemToRenderMainTranslucent;
    }

    public static boolean isTranslucentBlock(ItemStack itemStack) {
        if (itemStack == null) {
            return false;
        }

        Item item = itemStack.getItem();
        if (item == null) {
            return false;
        }

        if (!(item instanceof ItemBlock)) {
            return false;
        }

        ItemBlock itemBlock = (ItemBlock)item;
        Block fieldValue = (Block)Reflector.getFieldValue((ItemBlock)item, Reflector.ItemBlock_block);
        if (fieldValue == null) {
            return false;
        }

        int renderBlockPass = fieldValue.getRenderBlockPass();
        return renderBlockPass != 0;
    }

    public static boolean isRenderBothHands() {
        return false;
    }

    public static boolean isHandRenderedMain() {
        return isHandRenderedMain;
    }

    public static void setHandRenderedMain(boolean flag) {
    }

    public static float getShadowRenderDistance() {
        return shadowDistanceRenderMul < 0.0F ? -1.0F : shadowMapHalfPlane * shadowDistanceRenderMul;
    }

    public static void setRenderingFirstPersonHand(boolean flag) {
        isRenderingFirstPersonHand = flag;
    }

    public static boolean isRenderingFirstPersonHand() {
        return isRenderingFirstPersonHand;
    }

    public static void beginBeacon() {
        if (isRenderingWorld) {
            useProgram(14);
        }
    }

    public static void endBeacon() {
        if (isRenderingWorld) {
            useProgram(13);
        }
    }

    public static World getCurrentWorld() {
        return currentWorld;
    }

    public static Vec3 getCameraPosition() {
        return Vec3.createVectorHelper(cameraPositionX, cameraPositionY, cameraPositionZ);
    }

    public static boolean isCustomUniforms() {
        return customUniforms != null;
    }

    static {
        ((Buffer)drawBuffersNone).limit(0);
        ((Buffer)drawBuffersColorAtt0.put(36064)).position(0).limit(1);
    }

    public static void process(int n) {
        GL11.glDisable(n);
        disableFog();
    }

    public static void process2(int n) {
        GlStateManager.setFog(n);
        n = n;
        if (fogEnabled) {
            setProgramUniform1i("fogMode", n);
        }
    }

    public static void process3(int n) {
        GL11.glEnable(n);
        enableTexture2D();
    }

    public static void process4(int n) {
        GL11.glDisable(n);
        if (n == 3553) {
            disableTexture2D();
        } else if (n == 2912) {
            disableFog();
        }
    }

    public static void process5(int n) {
        GL11.glEnable(n);
        if (n == 3553) {
            enableTexture2D();
        } else if (n == 2912) {
            enableFog();
        }
    }

    public static void process6(int n) {
        GL11.glDisable(n);
        disableTexture2D();
    }

    public static void process7(int n) {
        entityDataIndex++;
        entityData[entityDataIndex * 2] = n & 65535;
        entityData[entityDataIndex * 2 + 1] = 0;
    }

    public static void process8(int n) {
        GL11.glEnable(n);
        enableFog();
    }
}
