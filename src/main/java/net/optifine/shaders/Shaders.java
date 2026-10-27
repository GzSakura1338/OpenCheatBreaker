package net.optifine.shaders;

import com.google.common.base.Charsets;
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
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.data.TextureMetadataSection;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.optifine.Config;
import net.optifine.CustomBlockLayers;
import net.optifine.CustomColors;
import net.optifine.GlErrors;
import net.optifine.Lang;
import net.optifine.config.ConnectedParser;
import net.optifine.expr.IExpressionBool;
import net.optifine.reflect.Reflector;
import net.optifine.render.GlAlphaState;
import net.optifine.render.GlBlendState;
import net.optifine.shaders.config.EnumShaderOption;
import net.optifine.shaders.config.MacroProcessor;
import net.optifine.shaders.config.MacroState;
import net.optifine.shaders.config.PropertyDefaultFastFancyOff;
import net.optifine.shaders.config.PropertyDefaultTrueFalse;
import net.optifine.shaders.config.RenderScale;
import net.optifine.shaders.config.ScreenShaderOptions;
import net.optifine.shaders.config.ShaderLine;
import net.optifine.shaders.config.ShaderOption;
import net.optifine.shaders.config.ShaderOptionProfile;
import net.optifine.shaders.config.ShaderOptionRest;
import net.optifine.shaders.config.ShaderPackParser;
import net.optifine.shaders.config.ShaderParser;
import net.optifine.shaders.config.ShaderProfile;
import net.optifine.shaders.uniform.CustomUniforms;
import net.optifine.shaders.uniform.ShaderUniform1f;
import net.optifine.shaders.uniform.ShaderUniform1i;
import net.optifine.shaders.uniform.ShaderUniform2i;
import net.optifine.shaders.uniform.ShaderUniform3f;
import net.optifine.shaders.uniform.ShaderUniform4f;
import net.optifine.shaders.uniform.ShaderUniform4i;
import net.optifine.shaders.uniform.ShaderUniformM4;
import net.optifine.shaders.uniform.ShaderUniforms;
import net.optifine.shaders.uniform.Smoother;
import net.optifine.texture.InternalFormat;
import net.optifine.texture.PixelFormat;
import net.optifine.texture.PixelType;
import net.optifine.texture.TextureType;
import net.optifine.util.EntityUtils;
import net.optifine.util.PropertiesOrdered;
import net.optifine.util.StrUtils;
import net.optifine.util.TimedEvent;
import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.ARBGeometryShader4;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.ARBVertexShader;
import org.lwjgl.opengl.ContextCapabilities;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector4f;

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
   public static int countResetDisplayLists = 0;
   public static int renderDisplayWidth = 0;
   public static int renderDisplayHeight = 0;
   public static int renderWidth = 0;
   public static int renderHeight = 0;
   public static boolean isRenderingWorld = false;
   public static boolean isRenderingSky = false;
   public static boolean isCompositeRendered = false;
   public static boolean isRenderingDfb = false;
   public static boolean isShadowPass = false;
   public static boolean isEntitiesGlowing = false;
   public static boolean isSleeping;
   public static boolean isRenderingFirstPersonHand;
   public static boolean isHandRenderedMain;
   public static boolean isHandRenderedOff;
   public static boolean skipRenderHandMain;
   public static boolean skipRenderHandOff;
   public static boolean renderItemKeepDepthMask = false;
   public static boolean itemToRenderMainTranslucent = false;
   public static boolean itemToRenderOffTranslucent = false;
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
   public static boolean lightmapEnabled = false;
   public static boolean fogEnabled = true;
   public static int entityAttrib = 10;
   public static int midTexCoordAttrib = 11;
   public static int tangentAttrib = 12;
   public static boolean useEntityAttrib = false;
   public static boolean useMidTexCoordAttrib = false;
   public static boolean useTangentAttrib = false;
   public static boolean progUseEntityAttrib = false;
   public static boolean progUseMidTexCoordAttrib = false;
   public static boolean progUseTangentAttrib = false;
   public static boolean progArbGeometryShader4 = false;
   public static int progMaxVerticesOut = 3;
   public static boolean hasGeometryShaders = false;
   public static int atlasSizeX = 0;
   public static int atlasSizeY = 0;
   public static ShaderUniforms shaderUniforms = new ShaderUniforms();
   public static ShaderUniform4f uniform_entityColor = shaderUniforms.make4f("entityColor");
   public static ShaderUniform1i uniform_entityId = shaderUniforms.make1i("entityId");
   public static ShaderUniform1i uniform_blockEntityId = shaderUniforms.make1i("blockEntityId");
   public static ShaderUniform1i uniform_texture = shaderUniforms.make1i("texture");
   public static ShaderUniform1i uniform_lightmap = shaderUniforms.make1i("lightmap");
   public static ShaderUniform1i uniform_normals = shaderUniforms.make1i("normals");
   public static ShaderUniform1i uniform_specular = shaderUniforms.make1i("specular");
   public static ShaderUniform1i uniform_shadow = shaderUniforms.make1i("shadow");
   public static ShaderUniform1i uniform_watershadow = shaderUniforms.make1i("watershadow");
   public static ShaderUniform1i uniform_shadowtex0 = shaderUniforms.make1i("shadowtex0");
   public static ShaderUniform1i uniform_shadowtex1 = shaderUniforms.make1i("shadowtex1");
   public static ShaderUniform1i uniform_depthtex0 = shaderUniforms.make1i("depthtex0");
   public static ShaderUniform1i uniform_depthtex1 = shaderUniforms.make1i("depthtex1");
   public static ShaderUniform1i uniform_shadowcolor = shaderUniforms.make1i("shadowcolor");
   public static ShaderUniform1i uniform_shadowcolor0 = shaderUniforms.make1i("shadowcolor0");
   public static ShaderUniform1i uniform_shadowcolor1 = shaderUniforms.make1i("shadowcolor1");
   public static ShaderUniform1i uniform_noisetex = shaderUniforms.make1i("noisetex");
   public static ShaderUniform1i uniform_gcolor = shaderUniforms.make1i("gcolor");
   public static ShaderUniform1i uniform_gdepth = shaderUniforms.make1i("gdepth");
   public static ShaderUniform1i uniform_gnormal = shaderUniforms.make1i("gnormal");
   public static ShaderUniform1i uniform_composite = shaderUniforms.make1i("composite");
   public static ShaderUniform1i uniform_gaux1 = shaderUniforms.make1i("gaux1");
   public static ShaderUniform1i uniform_gaux2 = shaderUniforms.make1i("gaux2");
   public static ShaderUniform1i uniform_gaux3 = shaderUniforms.make1i("gaux3");
   public static ShaderUniform1i uniform_gaux4 = shaderUniforms.make1i("gaux4");
   public static ShaderUniform1i uniform_colortex0 = shaderUniforms.make1i("colortex0");
   public static ShaderUniform1i uniform_colortex1 = shaderUniforms.make1i("colortex1");
   public static ShaderUniform1i uniform_colortex2 = shaderUniforms.make1i("colortex2");
   public static ShaderUniform1i uniform_colortex3 = shaderUniforms.make1i("colortex3");
   public static ShaderUniform1i uniform_colortex4 = shaderUniforms.make1i("colortex4");
   public static ShaderUniform1i uniform_colortex5 = shaderUniforms.make1i("colortex5");
   public static ShaderUniform1i uniform_colortex6 = shaderUniforms.make1i("colortex6");
   public static ShaderUniform1i uniform_colortex7 = shaderUniforms.make1i("colortex7");
   public static ShaderUniform1i uniform_gdepthtex = shaderUniforms.make1i("gdepthtex");
   public static ShaderUniform1i uniform_depthtex2 = shaderUniforms.make1i("depthtex2");
   public static ShaderUniform1i uniform_tex = shaderUniforms.make1i("tex");
   public static ShaderUniform1i uniform_heldItemId = shaderUniforms.make1i("heldItemId");
   public static ShaderUniform1i uniform_heldBlockLightValue = shaderUniforms.make1i("heldBlockLightValue");
   public static ShaderUniform1i uniform_heldItemId2 = shaderUniforms.make1i("heldItemId2");
   public static ShaderUniform1i uniform_heldBlockLightValue2 = shaderUniforms.make1i("heldBlockLightValue2");
   public static ShaderUniform1i uniform_fogMode = shaderUniforms.make1i("fogMode");
   public static ShaderUniform1f uniform_fogDensity = shaderUniforms.make1f("fogDensity");
   public static ShaderUniform3f uniform_fogColor = shaderUniforms.make3f("fogColor");
   public static ShaderUniform3f uniform_skyColor = shaderUniforms.make3f("skyColor");
   public static ShaderUniform1i uniform_worldTime = shaderUniforms.make1i("worldTime");
   public static ShaderUniform1i uniform_worldDay = shaderUniforms.make1i("worldDay");
   public static ShaderUniform1i uniform_moonPhase = shaderUniforms.make1i("moonPhase");
   public static ShaderUniform1i uniform_frameCounter = shaderUniforms.make1i("frameCounter");
   public static ShaderUniform1f uniform_frameTime = shaderUniforms.make1f("frameTime");
   public static ShaderUniform1f uniform_frameTimeCounter = shaderUniforms.make1f("frameTimeCounter");
   public static ShaderUniform1f uniform_sunAngle = shaderUniforms.make1f("sunAngle");
   public static ShaderUniform1f uniform_shadowAngle = shaderUniforms.make1f("shadowAngle");
   public static ShaderUniform1f uniform_rainStrength = shaderUniforms.make1f("rainStrength");
   public static ShaderUniform1f uniform_aspectRatio = shaderUniforms.make1f("aspectRatio");
   public static ShaderUniform1f uniform_viewWidth = shaderUniforms.make1f("viewWidth");
   public static ShaderUniform1f uniform_viewHeight = shaderUniforms.make1f("viewHeight");
   public static ShaderUniform1f uniform_near = shaderUniforms.make1f("near");
   public static ShaderUniform1f uniform_far = shaderUniforms.make1f("far");
   public static ShaderUniform3f uniform_sunPosition = shaderUniforms.make3f("sunPosition");
   public static ShaderUniform3f uniform_moonPosition = shaderUniforms.make3f("moonPosition");
   public static ShaderUniform3f uniform_shadowLightPosition = shaderUniforms.make3f("shadowLightPosition");
   public static ShaderUniform3f uniform_upPosition = shaderUniforms.make3f("upPosition");
   public static ShaderUniform3f uniform_previousCameraPosition = shaderUniforms.make3f("previousCameraPosition");
   public static ShaderUniform3f uniform_cameraPosition = shaderUniforms.make3f("cameraPosition");
   public static ShaderUniformM4 uniform_gbufferModelView = shaderUniforms.makeM4("gbufferModelView");
   public static ShaderUniformM4 uniform_gbufferModelViewInverse = shaderUniforms.makeM4("gbufferModelViewInverse");
   public static ShaderUniformM4 uniform_gbufferPreviousProjection = shaderUniforms.makeM4("gbufferPreviousProjection");
   public static ShaderUniformM4 uniform_gbufferProjection = shaderUniforms.makeM4("gbufferProjection");
   public static ShaderUniformM4 uniform_gbufferProjectionInverse = shaderUniforms.makeM4("gbufferProjectionInverse");
   public static ShaderUniformM4 uniform_gbufferPreviousModelView = shaderUniforms.makeM4("gbufferPreviousModelView");
   public static ShaderUniformM4 uniform_shadowProjection = shaderUniforms.makeM4("shadowProjection");
   public static ShaderUniformM4 uniform_shadowProjectionInverse = shaderUniforms.makeM4("shadowProjectionInverse");
   public static ShaderUniformM4 uniform_shadowModelView = shaderUniforms.makeM4("shadowModelView");
   public static ShaderUniformM4 uniform_shadowModelViewInverse = shaderUniforms.makeM4("shadowModelViewInverse");
   public static ShaderUniform1f uniform_wetness = shaderUniforms.make1f("wetness");
   public static ShaderUniform1f uniform_eyeAltitude = shaderUniforms.make1f("eyeAltitude");
   public static ShaderUniform2i uniform_eyeBrightness = shaderUniforms.make2i("eyeBrightness");
   public static ShaderUniform2i uniform_eyeBrightnessSmooth = shaderUniforms.make2i("eyeBrightnessSmooth");
   public static ShaderUniform2i uniform_terrainTextureSize = shaderUniforms.make2i("terrainTextureSize");
   public static ShaderUniform1i uniform_terrainIconSize = shaderUniforms.make1i("terrainIconSize");
   public static ShaderUniform1i uniform_isEyeInWater = shaderUniforms.make1i("isEyeInWater");
   public static ShaderUniform1f uniform_nightVision = shaderUniforms.make1f("nightVision");
   public static ShaderUniform1f uniform_blindness = shaderUniforms.make1f("blindness");
   public static ShaderUniform1f uniform_screenBrightness = shaderUniforms.make1f("screenBrightness");
   public static ShaderUniform1i uniform_hideGUI = shaderUniforms.make1i("hideGUI");
   public static ShaderUniform1f uniform_centerDepthSmooth = shaderUniforms.make1f("centerDepthSmooth");
   public static ShaderUniform2i uniform_atlasSize = shaderUniforms.make2i("atlasSize");
   public static ShaderUniform4i uniform_blendFunc = shaderUniforms.make4i("blendFunc");
   public static ShaderUniform1i uniform_instanceId = shaderUniforms.make1i("instanceId");
   public static double previousCameraPositionX;
   public static double previousCameraPositionY;
   public static double previousCameraPositionZ;
   public static double cameraPositionX;
   public static double cameraPositionY;
   public static double cameraPositionZ;
   public static int cameraOffsetX;
   public static int cameraOffsetZ;
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
   public static Vector4f[] gbuffersClearColor = new Vector4f[8];
   public static Programs programs = new Programs();
   public static Program ProgramNone = programs.getProgramNone();
   public static Program ProgramShadow = programs.makeShadow("shadow", ProgramNone);
   public static Program ProgramShadowSolid = programs.makeShadow("shadow_solid", ProgramShadow);
   public static Program ProgramShadowCutout = programs.makeShadow("shadow_cutout", ProgramShadow);
   public static Program ProgramBasic = programs.makeGbuffers("gbuffers_basic", ProgramNone);
   public static Program ProgramTextured = programs.makeGbuffers("gbuffers_textured", ProgramBasic);
   public static Program ProgramTexturedLit = programs.makeGbuffers("gbuffers_textured_lit", ProgramTextured);
   public static Program ProgramSkyBasic = programs.makeGbuffers("gbuffers_skybasic", ProgramBasic);
   public static Program ProgramSkyTextured = programs.makeGbuffers("gbuffers_skytextured", ProgramTextured);
   public static Program ProgramClouds = programs.makeGbuffers("gbuffers_clouds", ProgramTextured);
   public static Program ProgramTerrain = programs.makeGbuffers("gbuffers_terrain", ProgramTexturedLit);
   public static Program ProgramTerrainSolid = programs.makeGbuffers("gbuffers_terrain_solid", ProgramTerrain);
   public static Program ProgramTerrainCutoutMip = programs.makeGbuffers("gbuffers_terrain_cutout_mip", ProgramTerrain);
   public static Program ProgramTerrainCutout = programs.makeGbuffers("gbuffers_terrain_cutout", ProgramTerrain);
   public static Program ProgramDamagedBlock = programs.makeGbuffers("gbuffers_damagedblock", ProgramTerrain);
   public static Program ProgramBlock = programs.makeGbuffers("gbuffers_block", ProgramTerrain);
   public static Program ProgramBeaconBeam = programs.makeGbuffers("gbuffers_beaconbeam", ProgramTextured);
   public static Program ProgramItem = programs.makeGbuffers("gbuffers_item", ProgramTexturedLit);
   public static Program ProgramEntities = programs.makeGbuffers("gbuffers_entities", ProgramTexturedLit);
   public static Program ProgramEntitiesGlowing = programs.makeGbuffers("gbuffers_entities_glowing", ProgramEntities);
   public static Program ProgramArmorGlint = programs.makeGbuffers("gbuffers_armor_glint", ProgramTextured);
   public static Program ProgramSpiderEyes = programs.makeGbuffers("gbuffers_spidereyes", ProgramTextured);
   public static Program ProgramHand = programs.makeGbuffers("gbuffers_hand", ProgramTexturedLit);
   public static Program ProgramWeather = programs.makeGbuffers("gbuffers_weather", ProgramTexturedLit);
   public static Program ProgramDeferredPre = programs.makeVirtual("deferred_pre");
   public static Program[] ProgramsDeferred = programs.makeDeferreds("deferred", 16);
   public static Program ProgramDeferred = ProgramsDeferred[0];
   public static Program ProgramWater = programs.makeGbuffers("gbuffers_water", ProgramTerrain);
   public static Program ProgramHandWater = programs.makeGbuffers("gbuffers_hand_water", ProgramHand);
   public static Program ProgramCompositePre = programs.makeVirtual("composite_pre");
   public static Program[] ProgramsComposite = programs.makeComposites("composite", 16);
   public static Program ProgramComposite = ProgramsComposite[0];
   public static Program ProgramFinal = programs.makeComposite("final");
   public static int ProgramCount = programs.getCount();
   public static Program[] ProgramsAll = programs.getPrograms();
   public static Program activeProgram = ProgramNone;
   public static int activeProgramID = 0;
   public static ProgramStack programStack = new ProgramStack();
   public static boolean hasDeferredPrograms = false;
   public static IntBuffer activeDrawBuffers = null;
   public static int activeCompositeMipmapSetting = 0;
   public static Properties loadedShaders = null;
   public static Properties shadersConfig = null;
   public static ITextureObject defaultTexture = null;
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
   public static String currentShaderName;
   public static final String SHADER_PACK_NAME_NONE = "OFF";
   public static final String SHADER_PACK_NAME_DEFAULT = "(internal)";
   public static final String SHADER_PACKS_DIR_NAME = "shaderpacks";
   public static final String OPTIONS_FILE_NAME = "optionsshaders.txt";
   public static ShaderOption[] shaderPackOptions = null;
   public static Set<String> shaderPackOptionSliders = null;
   public static ShaderProfile[] shaderPackProfiles = null;
   public static Map<String, ScreenShaderOptions> shaderPackGuiScreens = null;
   public static Map<String, IExpressionBool> shaderPackProgramConditions = new HashMap<>();
   public static PropertyDefaultFastFancyOff shaderPackClouds = new PropertyDefaultFastFancyOff("clouds", "Clouds", 0);
   public static PropertyDefaultTrueFalse shaderPackOldLighting = new PropertyDefaultTrueFalse("oldLighting", "Classic Lighting", 0);
   public static final String PATH_SHADERS_PROPERTIES = "/shaders/shaders.properties";
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
   public static PropertyDefaultTrueFalse shaderPackRainDepth = new PropertyDefaultTrueFalse("rain.depth", "Rain Depth", 0);
   public static PropertyDefaultTrueFalse shaderPackBeaconBeamDepth = new PropertyDefaultTrueFalse("beacon.beam.depth", "Rain Depth", 0);
   public static PropertyDefaultTrueFalse shaderPackSeparateAo = new PropertyDefaultTrueFalse("separateAo", "Separate AO", 0);
   public static PropertyDefaultTrueFalse shaderPackFrustumCulling = new PropertyDefaultTrueFalse("frustum.culling", "Frustum Culling", 0);
   public static Map<String, String> shaderPackResources = new HashMap<>();
   public static World currentWorld = null;
   public static List<Integer> shaderPackDimensions = new ArrayList<>();
   public static ICustomTexture[] customTexturesGbuffers = null;
   public static ICustomTexture[] customTexturesComposite = null;
   public static ICustomTexture[] customTexturesDeferred = null;
   public static String noiseTexturePath = null;
   public static CustomUniforms customUniforms = null;
   public static String[] STAGE_NAMES = new String[]{"gbuffers", "composite", "deferred"};
   public static boolean saveFinalShaders = System.getProperty("shaders.debug.save", "false").equals("true");
   public static final int STAGE_GBUFFERS = 0;
   public static final int STAGE_COMPOSITE = 1;
   public static float blockLightLevel05 = 0.5F;
   public static float blockLightLevel06 = 0.6F;
   public static float blockLightLevel08 = 0.8F;
   public static float aoLevel = -1.0F;
   public static float sunPathRotation = 0.0F;
   public static float shadowAngleInterval = 0.0F;
   public static int fogMode = 0;
   public static float fogDensity = 0.0F;
   public static float shadowIntervalSize = 2.0F;
   public static int terrainIconSize = 16;
   public static float fogColorR;
   public static float fogColorG;
   public static float fogColorB;
   public static int[] terrainTextureSize = new int[2];
   public static boolean noiseTextureEnabled = false;
   public static int noiseTextureResolution = 256;
   public static ICustomTexture noiseTexture;
   public static int[] colorTextureImageUnit = new int[]{0, 1, 2, 3, 7, 8, 9, 10};
   public static int bigBufferSize = (285 + 8 * ProgramCount) * 4;
   public static ByteBuffer bigBuffer = (ByteBuffer)((Buffer)BufferUtils.createByteBuffer(bigBufferSize)).limit(0);
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
   public static IntBuffer drawBuffersNone = (IntBuffer)((Buffer)nextIntBuffer(8)).limit(0);
   public static IntBuffer drawBuffersColorAtt0 = (IntBuffer)((Buffer)nextIntBuffer(8).put(36064)).position(0).limit(1);
   public static FlipTextures dfbColorTexturesFlip = new FlipTextures(dfbColorTextures, 8);
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
   public static Map<Block, Integer> mapBlockToEntityData;
   public static Pattern patternLoadEntityDataMap = Pattern.compile("\\s*([\\w:]+)\\s*=\\s*([-]?\\d+)\\s*");
   public static int[] entityData = new int[32];
   public static int entityDataIndex = 0;
   public static File shaderPacksDir = new File(Minecraft.getMinecraft().mcDataDir, "shaderpacks");
   public static File configFile = new File(Minecraft.getMinecraft().mcDataDir, "optionsshaders.txt");
   public static final int CONSTANT_8 = 8;
   public static final int CONSTANT_2 = 2;
   public static final int CONSTANT_3 = 3;
   public static final int CONSTANT_3_2 = 3;
   public static final boolean CONSTANT_1 = true;
   public static final int CONSTANT_2_2 = 2;
   public static final int CONSTANT_2_3 = 2;
   public static final int CONSTANT_8_2 = 8;
   public static final int CONSTANT_8_3 = 8;
   public static final boolean CONSTANT_1_2 = true;

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
      SMCLog.info("Load shaders configuration.");

      try {
         if (!shaderPacksDir.exists()) {
            shaderPacksDir.mkdir();
         }
      } catch (Exception e) {
         SMCLog.severe("Failed to open the shaderpacks directory: " + shaderPacksDir);
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
            currentShaderName = str;
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
      SMCLog.info("Save shaders configuration.");
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
            return currentShaderName;
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
      currentShaderName = str;
      shadersConfig.setProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), str);
      loadShaderPack();
   }

   public static void loadShaderPack() {
      boolean shaderPackLoaded2 = shaderPackLoaded;
      boolean isOldLighting2 = isOldLighting();
      if (mc.renderGlobal != null) {
         mc.renderGlobal.pauseChunkUpdates();
      }

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
         shaderPackProgramConditions.clear();
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

      if (Config.isFastRender()) {
         SMCLog.info("Shaders can not be loaded, Fast Render is enabled.");
         flag = true;
      }

      String property = shadersConfig.getProperty(EnumShaderOption.SHADER_PACK.getPropertyKey(), "(internal)");
      if (!flag) {
         shaderPack = getShaderPack(property);
         shaderPackLoaded = shaderPack != null;
      }

      if (shaderPackLoaded) {
         SMCLog.info("Loaded shaderpack: " + getShaderPackName());
      } else {
         SMCLog.info("No shaderpack loaded.");
         shaderPack = new ShaderPackNone();
      }

      if (saveFinalShaders) {
         clearDirectory(new File(shaderPacksDir, "debug"));
      }

      loadShaderPackResources();
      loadShaderPackDimensions();
      shaderPackOptions = loadShaderPackOptions();
      loadShaderPackProperties();
      boolean flag2 = shaderPackLoaded != shaderPackLoaded2;
      boolean flag3 = isOldLighting() != isOldLighting2;
      if (flag2 || flag3) {
         DefaultVertexFormats.updateVertexFormats();
         if (Reflector.LightUtil.exists()) {
            Reflector.LightUtil_itemConsumer.setValue(null);
            Reflector.LightUtil_tessellator.setValue(null);
         }

         updateBlockLightLevel();
      }

      if (mc.getResourcePackRepository() != null) {
         CustomBlockLayers.update();
      }

      if (mc.renderGlobal != null) {
         mc.renderGlobal.resumeChunkUpdates();
      }

      if ((flag2 || flag3) && mc.getResourceManager() != null) {
         mc.scheduleResourcesRefresh();
      }
   }

   public static IShaderPack getShaderPack(String str) {
      if (str == null) {
         return null;
      }

      str = str.trim();
      if (str.isEmpty() || str.equals("OFF")) {
         return null;
      }

      if (str.equals("(internal)")) {
         return new ShaderPackDefault();
      }

      try {
         File file = new File(shaderPacksDir, str);
         return file.isDirectory()
            ? new ShaderPackFolder(str, file)
            : (file.isFile() && str.toLowerCase().endsWith(".zip") ? new ShaderPackZip(str, file) : null);
      } catch (Exception e) {
         e.printStackTrace();
         return null;
      }
   }

   public static IShaderPack getShaderPack() {
      return shaderPack;
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
      shaderPackRainDepth.resetValue();
      shaderPackBeaconBeamDepth.resetValue();
      shaderPackSeparateAo.resetValue();
      shaderPackFrustumCulling.resetValue();
      BlockAliases.reset();
      ItemAliases.reset();
      EntityAliases.reset();
      customUniforms = null;

      for (int i = 0; i < ProgramsAll.length; i++) {
         Program program = ProgramsAll[i];
         program.resetProperties();
      }

      if (shaderPack != null) {
         BlockAliases.update(shaderPack);
         ItemAliases.update(shaderPack);
         EntityAliases.update(shaderPack);
         String str = "/shaders/shaders.properties";

         try {
            InputStream resourceAsStream = shaderPack.getResourceAsStream(str);
            if (resourceAsStream == null) {
               return;
            }

            resourceAsStream = MacroProcessor.process(resourceAsStream, str);
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
            shaderPackRainDepth.loadFrom(propertiesOrdered);
            shaderPackBeaconBeamDepth.loadFrom(propertiesOrdered);
            shaderPackSeparateAo.loadFrom(propertiesOrdered);
            shaderPackFrustumCulling.loadFrom(propertiesOrdered);
            shaderPackOptionSliders = ShaderPackParser.parseOptionSliders(propertiesOrdered, shaderPackOptions);
            shaderPackProfiles = ShaderPackParser.parseProfiles(propertiesOrdered, shaderPackOptions);
            shaderPackGuiScreens = ShaderPackParser.parseGuiScreens(propertiesOrdered, shaderPackProfiles, shaderPackOptions);
            shaderPackProgramConditions = ShaderPackParser.parseProgramConditions(propertiesOrdered, shaderPackOptions);
            customTexturesGbuffers = loadCustomTextures(propertiesOrdered, 0);
            customTexturesComposite = loadCustomTextures(propertiesOrdered, 1);
            customTexturesDeferred = loadCustomTextures(propertiesOrdered, 2);
            noiseTexturePath = propertiesOrdered.getProperty("texture.noise");
            if (noiseTexturePath != null) {
               noiseTextureEnabled = true;
            }

            customUniforms = ShaderPackParser.parseCustomUniforms(propertiesOrdered);
            ShaderPackParser.parseAlphaStates(propertiesOrdered);
            ShaderPackParser.parseBlendStates(propertiesOrdered);
            ShaderPackParser.parseRenderScales(propertiesOrdered);
            ShaderPackParser.parseBuffersFlip(propertiesOrdered);
         } catch (IOException e) {
            Config.warn("[Shaders] Error reading: " + str);
         }
      }
   }

   public static ICustomTexture[] loadCustomTextures(Properties properties, int n) {
      String str = "texture." + STAGE_NAMES[n] + ".";
      Set keySet = properties.keySet();
      ArrayList list = new ArrayList();

      for (Object obj : keySet) {
         String str2 = (String)obj;
         if (str2.startsWith(str)) {
            String removePrefix = StrUtils.removePrefix(str2, str);
            removePrefix = StrUtils.removeSuffix(removePrefix, new String[]{".0", ".1", ".2", ".3", ".4", ".5", ".6", ".7", ".8", ".9"});
            String trim = properties.getProperty(str2).trim();
            int textureIndex = getTextureIndex(n, removePrefix);
            if (textureIndex < 0) {
               SMCLog.warning("Invalid texture name: " + str2);
            } else {
               ICustomTexture loadCustomTexture2 = loadCustomTexture(textureIndex, trim);
               if (loadCustomTexture2 != null) {
                  SMCLog.info("Custom texture: " + str2 + " = " + trim);
                  list.add(loadCustomTexture2);
               }
            }
         }
      }

      return list.size() <= 0 ? null : (ICustomTexture[])list.toArray(new ICustomTexture[list.size()]);
   }

   public static ICustomTexture loadCustomTexture(int n, String str) {
      if (str == null) {
         return null;
      }

      str = str.trim();
      return str.indexOf(58) >= 0
         ? loadCustomTextureLocation(n, str)
         : (str.indexOf(32) >= 0 ? loadCustomTextureRaw(n, str) : loadCustomTextureShaders(n, str));
   }

   public static ICustomTexture loadCustomTextureLocation(int n, String str) {
      String trim = str.trim();
      byte b = 0;
      if (trim.startsWith("minecraft:textures/")) {
         trim = StrUtils.addSuffixCheck(trim, ".png");
         if (trim.endsWith("_n.png")) {
            trim = StrUtils.replaceSuffix(trim, "_n.png", ".png");
            b = 1;
         } else if (trim.endsWith("_s.png")) {
            trim = StrUtils.replaceSuffix(trim, "_s.png", ".png");
            b = 2;
         }
      }

      ResourceLocation resourceLocation = new ResourceLocation(trim);
      return new CustomTextureLocation(n, resourceLocation, b);
   }

   public static ICustomTexture loadCustomTextureRaw(int n, String str) {
      ConnectedParser connectedParser = new ConnectedParser("Shaders");
      String[] tokenize = Config.tokenize(str, " ");
      ArrayDeque arrayDeque = new ArrayDeque<>(Arrays.asList(tokenize));
      String poll = (String)arrayDeque.poll();
      TextureType parseEnum = (TextureType)connectedParser.parseEnum((String)arrayDeque.poll(), TextureType.values(), "texture type");
      if (parseEnum == null) {
         SMCLog.warning("Invalid raw texture type: " + str);
         return null;
      }

      InternalFormat parseEnum2 = (InternalFormat)connectedParser.parseEnum((String)arrayDeque.poll(), InternalFormat.values(), "internal format");
      if (parseEnum2 == null) {
         SMCLog.warning("Invalid raw texture internal format: " + str);
         return null;
      }

      int n2 = 0;
      int n3 = 0;
      int n4 = 0;
      switch (parseEnum) {
         case TEXTURE_1D:
            n2 = connectedParser.parseInt((String)arrayDeque.poll(), -1);
            break;
         case TEXTURE_2D:
            n2 = connectedParser.parseInt((String)arrayDeque.poll(), -1);
            n3 = connectedParser.parseInt((String)arrayDeque.poll(), -1);
            break;
         case TEXTURE_3D:
            n2 = connectedParser.parseInt((String)arrayDeque.poll(), -1);
            n3 = connectedParser.parseInt((String)arrayDeque.poll(), -1);
            n4 = connectedParser.parseInt((String)arrayDeque.poll(), -1);
            break;
         case TEXTURE_RECTANGLE:
            n2 = connectedParser.parseInt((String)arrayDeque.poll(), -1);
            n3 = connectedParser.parseInt((String)arrayDeque.poll(), -1);
            break;
         default:
            SMCLog.warning("Invalid raw texture type: " + parseEnum);
            return null;
      }

      if (n2 >= 0 && n3 >= 0 && n4 >= 0) {
         PixelFormat parseEnum3 = (PixelFormat)connectedParser.parseEnum((String)arrayDeque.poll(), PixelFormat.values(), "pixel format");
         if (parseEnum3 == null) {
            SMCLog.warning("Invalid raw texture pixel format: " + str);
            return null;
         } else {
            PixelType parseEnum4 = (PixelType)connectedParser.parseEnum((String)arrayDeque.poll(), PixelType.values(), "pixel type");
            if (parseEnum4 == null) {
               SMCLog.warning("Invalid raw texture pixel type: " + str);
               return null;
            } else if (!arrayDeque.isEmpty()) {
               SMCLog.warning("Invalid raw texture, too many parameters: " + str);
               return null;
            } else {
               return loadCustomTextureRaw(n, str, poll, parseEnum, parseEnum2, n2, n3, n4, parseEnum3, parseEnum4);
            }
         }
      } else {
         SMCLog.warning("Invalid raw texture size: " + str);
         return null;
      }
   }

   public static ICustomTexture loadCustomTextureRaw(
      int n, String str, String str2, TextureType textureType, InternalFormat internalFormat, int n2, int n3, int n4, PixelFormat pixelFormat, PixelType pixelType
   ) {
      try {
         String str3 = "shaders/" + StrUtils.removePrefix(str2, "/");
         InputStream resourceAsStream = shaderPack.getResourceAsStream(str3);
         if (resourceAsStream == null) {
            SMCLog.warning("Raw texture not found: " + str2);
            return null;
         } else {
            byte[] readAll = Config.readAll(resourceAsStream);
            IOUtils.closeQuietly(resourceAsStream);
            ByteBuffer createDirectByteBuffer = GLAllocation.createDirectByteBuffer(readAll.length);
            createDirectByteBuffer.put(readAll);
            ((Buffer)createDirectByteBuffer).flip();
            TextureMetadataSection loadTextureMetadataSection = SimpleShaderTexture.loadTextureMetadataSection(str3, new TextureMetadataSection(true, true, new ArrayList<>()));
            return new CustomTextureRaw(textureType, internalFormat, n2, n3, n4, pixelFormat, pixelType, createDirectByteBuffer, n, loadTextureMetadataSection.getTextureBlur(), loadTextureMetadataSection.getTextureClamp());
         }
      } catch (IOException e) {
         SMCLog.warning("Error loading raw texture: " + str2);
         SMCLog.warning("" + e.getClass().getName() + ": " + e.getMessage());
         return null;
      }
   }

   public static ICustomTexture loadCustomTextureShaders(int n, String str) {
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

   public static void bindCustomTextures(ICustomTexture[] customTextures) {
      if (customTextures != null) {
         for (int i = 0; i < customTextures.length; i++) {
            ICustomTexture customTexture = customTextures[i];
            GlStateManager.setActiveTexture(33984 + customTexture.getTextureUnit());
            int textureId = customTexture.getTextureId();
            int target = customTexture.getTarget();
            if (target == 3553) {
               GlStateManager.bindTexture(textureId);
            } else {
               GL11.glBindTexture(target, textureId);
            }
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

   public static void deleteCustomTextures(ICustomTexture[] customTextures) {
      if (customTextures != null) {
         for (int i = 0; i < customTextures.length; i++) {
            ICustomTexture customTexture = customTextures[i];
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
               list.add((ShaderOption)null);
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
      PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
      if (shaderPackOptions != null) {
         for (int i = 0; i < shaderOptions.length; i++) {
            ShaderOption shaderOption = shaderOptions[i];
            if (shaderOption.isChanged() && shaderOption.isEnabled()) {
               propertiesOrdered.setProperty(shaderOption.getName(), shaderOption.getValue());
            }
         }
      }

      try {
         saveOptionProperties(shaderPack2, propertiesOrdered);
      } catch (IOException e) {
         Config.warn("[Shaders] Error saving configuration for " + shaderPack.getName());
         e.printStackTrace();
      }
   }

   public static void saveOptionProperties(IShaderPack shaderPack, Properties properties) throws IOException {
      String str = "shaderpacks/" + shaderPack.getName() + ".txt";
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
         String[] programNames = programs.getProgramNames();
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
      PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
      String str = "shaderpacks/" + shaderPack.getName() + ".txt";
      File file = new File(Minecraft.getMinecraft().mcDataDir, str);
      if (file.exists() && file.isFile() && file.canRead()) {
         FileInputStream fileInputStream = new FileInputStream(file);
         propertiesOrdered.load(fileInputStream);
         fileInputStream.close();
         return propertiesOrdered;
      } else {
         return propertiesOrdered;
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
      list.add("OFF");
      list.add("(internal)");
      int size = list.size();

      try {
         if (!shaderPacksDir.exists()) {
            shaderPacksDir.mkdir();
         }

         File[] listFiles = shaderPacksDir.listFiles();

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

      List subList = list.subList(size, list.size());
      Collections.sort(subList, String.CASE_INSENSITIVE_ORDER);
      return list;
   }

   public static int checkFramebufferStatus(String str) {
      int glCheckFramebufferStatusEXT = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
      if (glCheckFramebufferStatusEXT != 36053) {
         System.err.format("FramebufferStatus 0x%04X at %s\n", glCheckFramebufferStatusEXT, str);
      }

      return glCheckFramebufferStatusEXT;
   }

   public static int checkGLError(String str) {
      int glGetError = GlStateManager.glGetError();
      if (glGetError != 0 && GlErrors.isEnabled(glGetError)) {
         String glErrorString = Config.getGlErrorString(glGetError);
         String errorInfo = getErrorInfo(glGetError, str);
         String format = String.format("OpenGL error: %s (%s)%s, at: %s", glGetError, glErrorString, errorInfo, str);
         SMCLog.severe(format);
         if (Config.isShowGlErrors() && TimedEvent.isActive("ShowGlErrorShaders", 10000L)) {
            String format2 = I18n.format("of.message.openglError", glGetError, glErrorString);
            printChat(format2);
         }
      }

      return glGetError;
   }

   public static String getErrorInfo(int n, String str) {
      StringBuilder sb = new StringBuilder();
      if (n == 1286) {
         int glCheckFramebufferStatusEXT = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
         String framebufferStatusText = getFramebufferStatusText(glCheckFramebufferStatusEXT);
         String str2 = ", fbStatus: " + glCheckFramebufferStatusEXT + " (" + framebufferStatusText + ")";
         sb.append(str2);
      }

      String name = activeProgram.getName();
      if (name.isEmpty()) {
         name = "none";
      }

      sb.append(", program: " + name);
      Program programById = getProgramById(activeProgramID);
      if (programById != activeProgram) {
         String name2 = programById.getName();
         if (name2.isEmpty()) {
            name2 = "none";
         }

         sb.append(" (" + name2 + ")");
      }

      if (str.equals("setDrawBuffers")) {
         sb.append(", drawBuffers: " + activeProgram.getDrawBufSettings());
      }

      return sb.toString();
   }

   public static Program getProgramById(int n) {
      for (int i = 0; i < ProgramsAll.length; i++) {
         Program program = ProgramsAll[i];
         if (program.getId() == n) {
            return program;
         }
      }

      return ProgramNone;
   }

   public static String getFramebufferStatusText(int n) {
      switch (n) {
         case 33305:
            return "Undefined";
         case 36053:
            return "Complete";
         case 36054:
            return "Incomplete attachment";
         case 36055:
            return "Incomplete missing attachment";
         case 36059:
            return "Incomplete draw buffer";
         case 36060:
            return "Incomplete read buffer";
         case 36061:
            return "Unsupported";
         case 36182:
            return "Incomplete multisample";
         case 36264:
            return "Incomplete layer targets";
         default:
            return "Unknown";
      }
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

   public static boolean isRenderBackFace(EnumWorldBlockLayer enumWorldBlockLayer) {
      switch (enumWorldBlockLayer) {
         case SOLID:
            return shaderPackBackFaceSolid.isTrue();
         case CUTOUT:
            return shaderPackBackFaceCutout.isTrue();
         case CUTOUT_MIPPED:
            return shaderPackBackFaceCutoutMipped.isTrue();
         case TRANSLUCENT:
            return shaderPackBackFaceTranslucent.isTrue();
         default:
            return false;
      }
   }

   public static boolean isRainDepth() {
      return shaderPackRainDepth.isTrue();
   }

   public static boolean isBeaconBeamDepth() {
      return shaderPackBeaconBeamDepth.isTrue();
   }

   public static boolean isSeparateAo() {
      return shaderPackSeparateAo.isTrue();
   }

   public static boolean isFrustumCulling() {
      return !shaderPackFrustumCulling.isFalse();
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
         Arrays.fill(gbuffersClearColor, null);
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
         useTangentAttrib = false;
         waterShadowEnabled = false;
         hasGeometryShaders = false;
         updateBlockLightLevel();
         Smoother.resetValues();
         shaderUniforms.reset();
         if (customUniforms != null) {
            customUniforms.reset();
         }

         ShaderProfile detectProfile = ShaderUtils.detectProfile(shaderPackProfiles, shaderPackOptions, false);
         String str = "";
         if (currentWorld != null) {
            int dimensionId = currentWorld.provider.getDimensionId();
            if (shaderPackDimensions.contains(dimensionId)) {
               str = "world" + dimensionId + "/";
            }
         }

         for (int i = 0; i < ProgramsAll.length; i++) {
            Program program = ProgramsAll[i];
            program.resetId();
            program.resetConfiguration();
            if (program.getProgramStage() != ProgramStage.NONE) {
               String name = program.getName();
               String str2 = str + name;
               boolean flag2 = true;
               if (shaderPackProgramConditions.containsKey(str2)) {
                  flag2 = flag2 && shaderPackProgramConditions.get(str2).eval();
               }

               if (detectProfile != null) {
                  flag2 = flag2 && !detectProfile.isProgramDisabled(str2);
               }

               if (!flag2) {
                  SMCLog.info("Program disabled: " + str2);
                  name = "<disabled>";
                  str2 = str + name;
               }

               String str3 = "/shaders/" + str2;
               String str4 = str3 + ".vsh";
               String str5 = str3 + ".gsh";
               String str6 = str3 + ".fsh";
               setupProgram(program, str4, str5, str6);
               int id = program.getId();
               if (id > 0) {
                  SMCLog.info("Program loaded: " + str2);
               }

               initDrawBuffers(program);
               updateToggleBuffers(program);
            }
         }

         hasDeferredPrograms = false;

         for (int i = 0; i < ProgramsDeferred.length; i++) {
            if (ProgramsDeferred[i].getId() != 0) {
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
         dfbColorTexturesFlip.reset();

         for (int i = 0; i < usedDrawBuffers; i++) {
            dfbDrawBuffers.put(i, 36064 + i);
         }

         int glGetInteger = GL11.glGetInteger(34852);
         if (usedDrawBuffers > glGetInteger) {
            printChatAndLogError("[Shaders] Error: Not enough draw buffers, needed: " + usedDrawBuffers + ", available: " + glGetInteger);
         }

         ((Buffer)sfbDrawBuffers).position(0).limit(usedShadowColorBuffers);

         for (int i = 0; i < usedShadowColorBuffers; i++) {
            sfbDrawBuffers.put(i, 36064 + i);
         }

         for (int i = 0; i < ProgramsAll.length; i++) {
            Program program = ProgramsAll[i];
            Program program2 = program;

            while (program2.getId() == 0 && program2.getProgramBackup() != program2) {
               program2 = program2.getProgramBackup();
            }

            if (program2 != program && program != ProgramShadow) {
               program.copyFrom(program2);
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
         resetDisplayLists();
         if (!flag) {
         }

         checkGLError("Shaders.init");
      }
   }

   public static void initDrawBuffers(Program program) {
      int glGetInteger = GL11.glGetInteger(34852);
      Arrays.fill(program.getToggleColorTextures(), false);
      if (program == ProgramFinal) {
         program.setDrawBuffers((IntBuffer)null);
      } else if (program.getId() == 0) {
         if (program == ProgramShadow) {
            program.setDrawBuffers(drawBuffersNone);
         } else {
            program.setDrawBuffers(drawBuffersColorAtt0);
         }
      } else {
         String drawBufSettings = program.getDrawBufSettings();
         if (drawBufSettings == null) {
            if (program != ProgramShadow && program != ProgramShadowSolid && program != ProgramShadowCutout) {
               program.setDrawBuffers(dfbDrawBuffers);
               usedDrawBuffers = usedColorBuffers;
               Arrays.fill(program.getToggleColorTextures(), 0, usedColorBuffers, true);
            } else {
               program.setDrawBuffers(sfbDrawBuffers);
            }
         } else {
            IntBuffer drawBuffersBuffer = program.getDrawBuffersBuffer();
            int n = drawBufSettings.length();
            usedDrawBuffers = Math.max(usedDrawBuffers, n);
            n = Math.min(n, glGetInteger);
            program.setDrawBuffers(drawBuffersBuffer);
            ((Buffer)drawBuffersBuffer).limit(n);

            for (int i = 0; i < n; i++) {
               int drawBuffer = getDrawBuffer(program, drawBufSettings, i);
               drawBuffersBuffer.put(i, drawBuffer);
            }
         }
      }
   }

   public static int getDrawBuffer(Program program, String str, int n) {
      int n2 = 0;
      if (n >= str.length()) {
         return n2;
      }

      int n3 = str.charAt(n) - '0';
      if (program == ProgramShadow) {
         if (n3 >= 0 && n3 <= 1) {
            n2 = n3 + 36064;
            usedShadowColorBuffers = Math.max(usedShadowColorBuffers, n3);
         }

         return n2;
      } else {
         if (n3 >= 0 && n3 <= 7) {
            program.getToggleColorTextures()[n3] = true;
            n2 = n3 + 36064;
            usedColorAttachs = Math.max(usedColorAttachs, n3);
            usedColorBuffers = Math.max(usedColorBuffers, n3);
         }

         return n2;
      }
   }

   public static void updateToggleBuffers(Program program) {
      boolean[] toggleColorTextures = program.getToggleColorTextures();
      Boolean[] buffersFlip = program.getBuffersFlip();

      for (int i = 0; i < buffersFlip.length; i++) {
         Boolean bool = buffersFlip[i];
         if (bool != null) {
            toggleColorTextures[i] = bool;
         }
      }
   }

   public static void resetDisplayLists() {
      SMCLog.info("Reset model renderers");
      countResetDisplayLists++;
      SMCLog.info("Reset world renderers");
      mc.renderGlobal.loadRenderers();
   }

   public static void setupProgram(Program program, String str, String str2, String str3) {
      checkGLError("pre setupProgram");
      int glCreateProgramObjectARB = ARBShaderObjects.glCreateProgramObjectARB();
      checkGLError("create");
      if (glCreateProgramObjectARB != 0) {
         progUseEntityAttrib = false;
         progUseMidTexCoordAttrib = false;
         progUseTangentAttrib = false;
         int createVertShader2 = createVertShader(program, str);
         int createGeomShader2 = createGeomShader(program, str2);
         int createFragShader2 = createFragShader(program, str3);
         checkGLError("create");
         if (createVertShader2 == 0 && createGeomShader2 == 0 && createFragShader2 == 0) {
            ARBShaderObjects.glDeleteObjectARB(glCreateProgramObjectARB);
            boolean flag = false;
            program.resetId();
         } else {
            if (createVertShader2 != 0) {
               ARBShaderObjects.glAttachObjectARB(glCreateProgramObjectARB, createVertShader2);
               checkGLError("attach");
            }

            if (createGeomShader2 != 0) {
               ARBShaderObjects.glAttachObjectARB(glCreateProgramObjectARB, createGeomShader2);
               checkGLError("attach");
               if (progArbGeometryShader4) {
                  ARBGeometryShader4.glProgramParameteriARB(glCreateProgramObjectARB, 36315, 4);
                  ARBGeometryShader4.glProgramParameteriARB(glCreateProgramObjectARB, 36316, 5);
                  ARBGeometryShader4.glProgramParameteriARB(glCreateProgramObjectARB, 36314, progMaxVerticesOut);
                  checkGLError("arbGeometryShader4");
               }

               hasGeometryShaders = true;
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
               SMCLog.severe("Error linking program: " + glCreateProgramObjectARB + " (" + program.getName() + ")");
            }

            printLogInfo(glCreateProgramObjectARB, program.getName());
            if (createVertShader2 != 0) {
               ARBShaderObjects.glDetachObjectARB(glCreateProgramObjectARB, createVertShader2);
               ARBShaderObjects.glDeleteObjectARB(createVertShader2);
            }

            if (createGeomShader2 != 0) {
               ARBShaderObjects.glDetachObjectARB(glCreateProgramObjectARB, createGeomShader2);
               ARBShaderObjects.glDeleteObjectARB(createGeomShader2);
            }

            if (createFragShader2 != 0) {
               ARBShaderObjects.glDetachObjectARB(glCreateProgramObjectARB, createFragShader2);
               ARBShaderObjects.glDeleteObjectARB(createFragShader2);
            }

            program.setId(glCreateProgramObjectARB);
            program.setRef(glCreateProgramObjectARB);
            useProgram(program);
            ARBShaderObjects.glValidateProgramARB(glCreateProgramObjectARB);
            useProgram(ProgramNone);
            printLogInfo(glCreateProgramObjectARB, program.getName());
            int glGetProgrami = GL20.glGetProgrami(glCreateProgramObjectARB, 35715);
            if (glGetProgrami != 1) {
               String str4 = "\"";
               printChatAndLogError("[Shaders] Error: Invalid program " + str4 + program.getName() + str4);
               ARBShaderObjects.glDeleteObjectARB(glCreateProgramObjectARB);
               boolean flag = false;
               program.resetId();
            }
         }
      }
   }

   public static int createVertShader(Program program, String str) {
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
            MacroState macroState = new MacroState();

            while (true) {
               String readLine = bufferedReader.readLine();
               if (readLine == null) {
                  bufferedReader.close();
                  break;
               }

               readLine = applyOptions(readLine, changedOptions);
               sb.append(readLine).append('\n');
               if (macroState.processLine(readLine)) {
                  ShaderLine parseLine = ShaderParser.parseLine(readLine);
                  if (parseLine != null) {
                     if (parseLine.isAttribute("mc_Entity")) {
                        useEntityAttrib = true;
                        progUseEntityAttrib = true;
                     } else if (parseLine.isAttribute("mc_midTexCoord")) {
                        useMidTexCoordAttrib = true;
                        progUseMidTexCoordAttrib = true;
                     } else if (parseLine.isAttribute("at_tangent")) {
                        useTangentAttrib = true;
                        progUseTangentAttrib = true;
                     }

                     if (parseLine.isConstInt("countInstances")) {
                        program.setCountInstances(parseLine.getValueInt());
                        SMCLog.info("countInstances: " + program.getCountInstances());
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
         SMCLog.severe("Error compiling vertex shader: " + str);
      }

      printShaderLogInfo(glCreateShaderObjectARB, str, list);
      return glCreateShaderObjectARB;
   }

   public static int createGeomShader(Program program, String str) {
      int glCreateShaderObjectARB = ARBShaderObjects.glCreateShaderObjectARB(36313);
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
      progArbGeometryShader4 = false;
      progMaxVerticesOut = 3;
      if (bufferedReader != null) {
         try {
            bufferedReader = ShaderPackParser.resolveIncludes(bufferedReader, str, shaderPack, 0, list, 0);
            MacroState macroState = new MacroState();

            while (true) {
               String readLine = bufferedReader.readLine();
               if (readLine == null) {
                  bufferedReader.close();
                  break;
               }

               readLine = applyOptions(readLine, changedOptions);
               sb.append(readLine).append('\n');
               if (macroState.processLine(readLine)) {
                  ShaderLine parseLine = ShaderParser.parseLine(readLine);
                  if (parseLine != null) {
                     if (parseLine.isExtension("GL_ARB_geometry_shader4")) {
                        String normalize = Config.normalize(parseLine.getValue());
                        if (normalize.equals("enable") || normalize.equals("require") || normalize.equals("warn")) {
                           progArbGeometryShader4 = true;
                        }
                     }

                     if (parseLine.isConstInt("maxVerticesOut")) {
                        progMaxVerticesOut = parseLine.getValueInt();
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
         SMCLog.severe("Error compiling geometry shader: " + str);
      }

      printShaderLogInfo(glCreateShaderObjectARB, str, list);
      return glCreateShaderObjectARB;
   }

   public static int createFragShader(Program program, String str) {
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
            MacroState macroState = new MacroState();

            while (true) {
               String readLine = bufferedReader.readLine();
               if (readLine == null) {
                  bufferedReader.close();
                  break;
               }

               readLine = applyOptions(readLine, changedOptions);
               sb.append(readLine).append('\n');
               if (macroState.processLine(readLine)) {
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
                     } else if (parseLine.isConstVec4Suffix("ClearColor")) {
                        if (ShaderParser.isComposite(str) || ShaderParser.isDeferred(str)) {
                           String removeSuffix = StrUtils.removeSuffix(parseLine.getName(), "ClearColor");
                           int bufferIndexFromString = getBufferIndexFromString(removeSuffix);
                           if (bufferIndexFromString >= 0) {
                              Vector4f valueVec4 = parseLine.getValueVec4();
                              if (valueVec4 != null) {
                                 gbuffersClearColor[bufferIndexFromString] = valueVec4;
                                 SMCLog.info("%s clear color: %s %s %s %s", removeSuffix, valueVec4.getX(), valueVec4.getY(), valueVec4.getZ(), valueVec4.getW());
                              } else {
                                 SMCLog.warning("Invalid color value: " + parseLine.getValue());
                              }
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
                              int compositeMipmapSetting = program.getCompositeMipmapSetting();
                              compositeMipmapSetting |= 1 << bufferIndexFromString;
                              program.setCompositeMipmapSetting(compositeMipmapSetting);
                              SMCLog.info("%s mipmap enabled", removeSuffix);
                           }
                        }
                     } else if (parseLine.isProperty("DRAWBUFFERS")) {
                        String value = parseLine.getValue();
                        if (ShaderParser.isValidDrawBuffers(value)) {
                           program.setDrawBufSettings(value);
                        } else {
                           SMCLog.warning("Invalid draw buffers: " + value);
                        }
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
      return new InputStreamReader(shaderPack.getResourceAsStream(str));
   }

   public static void saveShader(String str, String str2) {
      try {
         File file = new File(shaderPacksDir, "debug/" + str);
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

         String str2 = new String(bytes, Charsets.US_ASCII);
         str2 = StrUtils.trim(str2, " \n\r\t");
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
      glGetShaderInfoLog = StrUtils.trim(glGetShaderInfoLog, " \n\r\t");
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
         checkGLError("setDrawBuffers");
      }
   }

   public static void useProgram(Program program) {
      checkGLError("pre-useProgram");
      if (isShadowPass) {
         program = ProgramShadow;
      } else if (isEntitiesGlowing) {
         program = ProgramEntitiesGlowing;
      }

      if (activeProgram != program) {
         updateAlphaBlend(activeProgram, program);
         activeProgram = program;
         int id = program.getId();
         activeProgramID = id;
         ARBShaderObjects.glUseProgramObjectARB(id);
         if (checkGLError("useProgram") != 0) {
            program.setId(0);
            id = program.getId();
            activeProgramID = id;
            ARBShaderObjects.glUseProgramObjectARB(id);
         }

         shaderUniforms.setProgram(id);
         if (customUniforms != null) {
            customUniforms.setProgram(id);
         }

         if (id != 0) {
            IntBuffer drawBuffers = program.getDrawBuffers();
            if (isRenderingDfb) {
               setDrawBuffers(drawBuffers);
            }

            activeCompositeMipmapSetting = program.getCompositeMipmapSetting();
            switch (program.getProgramStage()) {
               case GBUFFERS:
                  setProgramUniform1i(uniform_texture, 0);
                  setProgramUniform1i(uniform_lightmap, 1);
                  setProgramUniform1i(uniform_normals, 2);
                  setProgramUniform1i(uniform_specular, 3);
                  setProgramUniform1i(uniform_shadow, waterShadowEnabled ? 5 : 4);
                  setProgramUniform1i(uniform_watershadow, 4);
                  setProgramUniform1i(uniform_shadowtex0, 4);
                  setProgramUniform1i(uniform_shadowtex1, 5);
                  setProgramUniform1i(uniform_depthtex0, 6);
                  if (customTexturesGbuffers != null || hasDeferredPrograms) {
                     setProgramUniform1i(uniform_gaux1, 7);
                     setProgramUniform1i(uniform_gaux2, 8);
                     setProgramUniform1i(uniform_gaux3, 9);
                     setProgramUniform1i(uniform_gaux4, 10);
                  }

                  setProgramUniform1i(uniform_depthtex1, 11);
                  setProgramUniform1i(uniform_shadowcolor, 13);
                  setProgramUniform1i(uniform_shadowcolor0, 13);
                  setProgramUniform1i(uniform_shadowcolor1, 14);
                  setProgramUniform1i(uniform_noisetex, 15);
                  break;
               case DEFERRED:
               case COMPOSITE:
                  setProgramUniform1i(uniform_gcolor, 0);
                  setProgramUniform1i(uniform_gdepth, 1);
                  setProgramUniform1i(uniform_gnormal, 2);
                  setProgramUniform1i(uniform_composite, 3);
                  setProgramUniform1i(uniform_gaux1, 7);
                  setProgramUniform1i(uniform_gaux2, 8);
                  setProgramUniform1i(uniform_gaux3, 9);
                  setProgramUniform1i(uniform_gaux4, 10);
                  setProgramUniform1i(uniform_colortex0, 0);
                  setProgramUniform1i(uniform_colortex1, 1);
                  setProgramUniform1i(uniform_colortex2, 2);
                  setProgramUniform1i(uniform_colortex3, 3);
                  setProgramUniform1i(uniform_colortex4, 7);
                  setProgramUniform1i(uniform_colortex5, 8);
                  setProgramUniform1i(uniform_colortex6, 9);
                  setProgramUniform1i(uniform_colortex7, 10);
                  setProgramUniform1i(uniform_shadow, waterShadowEnabled ? 5 : 4);
                  setProgramUniform1i(uniform_watershadow, 4);
                  setProgramUniform1i(uniform_shadowtex0, 4);
                  setProgramUniform1i(uniform_shadowtex1, 5);
                  setProgramUniform1i(uniform_gdepthtex, 6);
                  setProgramUniform1i(uniform_depthtex0, 6);
                  setProgramUniform1i(uniform_depthtex1, 11);
                  setProgramUniform1i(uniform_depthtex2, 12);
                  setProgramUniform1i(uniform_shadowcolor, 13);
                  setProgramUniform1i(uniform_shadowcolor0, 13);
                  setProgramUniform1i(uniform_shadowcolor1, 14);
                  setProgramUniform1i(uniform_noisetex, 15);
                  break;
               case SHADOW:
                  setProgramUniform1i(uniform_tex, 0);
                  setProgramUniform1i(uniform_texture, 0);
                  setProgramUniform1i(uniform_lightmap, 1);
                  setProgramUniform1i(uniform_normals, 2);
                  setProgramUniform1i(uniform_specular, 3);
                  setProgramUniform1i(uniform_shadow, waterShadowEnabled ? 5 : 4);
                  setProgramUniform1i(uniform_watershadow, 4);
                  setProgramUniform1i(uniform_shadowtex0, 4);
                  setProgramUniform1i(uniform_shadowtex1, 5);
                  if (customTexturesGbuffers != null) {
                     setProgramUniform1i(uniform_gaux1, 7);
                     setProgramUniform1i(uniform_gaux2, 8);
                     setProgramUniform1i(uniform_gaux3, 9);
                     setProgramUniform1i(uniform_gaux4, 10);
                  }

                  setProgramUniform1i(uniform_shadowcolor, 13);
                  setProgramUniform1i(uniform_shadowcolor0, 13);
                  setProgramUniform1i(uniform_shadowcolor1, 14);
                  setProgramUniform1i(uniform_noisetex, 15);
            }

            ItemStack itemStack = mc.thePlayer != null ? mc.thePlayer.getHeldItem() : null;
            Item item = itemStack != null ? itemStack.getItem() : null;
            int n = -1;
            Block block = null;
            if (item != null) {
               n = Item.itemRegistry.getIDForObject(item);
               block = Block.blockRegistry.getObjectById(n);
               n = ItemAliases.getItemAliasId(n);
            }

            int n2 = block != null ? block.getLightValue() : 0;
            setProgramUniform1i(uniform_heldItemId, n);
            setProgramUniform1i(uniform_heldBlockLightValue, n2);
            setProgramUniform1i(uniform_fogMode, fogEnabled ? fogMode : 0);
            setProgramUniform1f(uniform_fogDensity, fogEnabled ? fogDensity : 0.0F);
            setProgramUniform3f(uniform_fogColor, fogColorR, fogColorG, fogColorB);
            setProgramUniform3f(uniform_skyColor, skyColorR, skyColorG, skyColorB);
            setProgramUniform1i(uniform_worldTime, (int)(worldTime % 24000L));
            setProgramUniform1i(uniform_worldDay, (int)(worldTime / 24000L));
            setProgramUniform1i(uniform_moonPhase, moonPhase);
            setProgramUniform1i(uniform_frameCounter, frameCounter);
            setProgramUniform1f(uniform_frameTime, frameTime);
            setProgramUniform1f(uniform_frameTimeCounter, frameTimeCounter);
            setProgramUniform1f(uniform_sunAngle, sunAngle);
            setProgramUniform1f(uniform_shadowAngle, shadowAngle);
            setProgramUniform1f(uniform_rainStrength, rainStrength);
            setProgramUniform1f(uniform_aspectRatio, (float)renderWidth / renderHeight);
            setProgramUniform1f(uniform_viewWidth, renderWidth);
            setProgramUniform1f(uniform_viewHeight, renderHeight);
            setProgramUniform1f(uniform_near, 0.05F);
            setProgramUniform1f(uniform_far, mc.gameSettings.renderDistanceChunks * 16);
            setProgramUniform3f(uniform_sunPosition, sunPosition[0], sunPosition[1], sunPosition[2]);
            setProgramUniform3f(uniform_moonPosition, moonPosition[0], moonPosition[1], moonPosition[2]);
            setProgramUniform3f(uniform_shadowLightPosition, shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
            setProgramUniform3f(uniform_upPosition, upPosition[0], upPosition[1], upPosition[2]);
            setProgramUniform3f(uniform_previousCameraPosition, (float)previousCameraPositionX, (float)previousCameraPositionY, (float)previousCameraPositionZ);
            setProgramUniform3f(uniform_cameraPosition, (float)cameraPositionX, (float)cameraPositionY, (float)cameraPositionZ);
            setProgramUniformMatrix4ARB(uniform_gbufferModelView, false, modelView);
            setProgramUniformMatrix4ARB(uniform_gbufferModelViewInverse, false, modelViewInverse);
            setProgramUniformMatrix4ARB(uniform_gbufferPreviousProjection, false, previousProjection);
            setProgramUniformMatrix4ARB(uniform_gbufferProjection, false, projection);
            setProgramUniformMatrix4ARB(uniform_gbufferProjectionInverse, false, projectionInverse);
            setProgramUniformMatrix4ARB(uniform_gbufferPreviousModelView, false, previousModelView);
            if (usedShadowDepthBuffers > 0) {
               setProgramUniformMatrix4ARB(uniform_shadowProjection, false, shadowProjection);
               setProgramUniformMatrix4ARB(uniform_shadowProjectionInverse, false, shadowProjectionInverse);
               setProgramUniformMatrix4ARB(uniform_shadowModelView, false, shadowModelView);
               setProgramUniformMatrix4ARB(uniform_shadowModelViewInverse, false, shadowModelViewInverse);
            }

            setProgramUniform1f(uniform_wetness, wetness);
            setProgramUniform1f(uniform_eyeAltitude, eyePosY);
            setProgramUniform2i(uniform_eyeBrightness, eyeBrightness & 65535, eyeBrightness >> 16);
            setProgramUniform2i(uniform_eyeBrightnessSmooth, Math.round(eyeBrightnessFadeX), Math.round(eyeBrightnessFadeY));
            setProgramUniform2i(uniform_terrainTextureSize, terrainTextureSize[0], terrainTextureSize[1]);
            setProgramUniform1i(uniform_terrainIconSize, terrainIconSize);
            setProgramUniform1i(uniform_isEyeInWater, isEyeInWater);
            setProgramUniform1f(uniform_nightVision, nightVision);
            setProgramUniform1f(uniform_blindness, blindness);
            setProgramUniform1f(uniform_screenBrightness, mc.gameSettings.gammaSetting);
            setProgramUniform1i(uniform_hideGUI, mc.gameSettings.hideGUI ? 1 : 0);
            setProgramUniform1f(uniform_centerDepthSmooth, centerDepthSmooth);
            setProgramUniform2i(uniform_atlasSize, atlasSizeX, atlasSizeY);
            if (customUniforms != null) {
               customUniforms.update();
            }

            checkGLError("end useProgram");
         }
      }
   }

   public static void updateAlphaBlend(Program program, Program program2) {
      if (program.getAlphaState() != null) {
         GlStateManager.unlockAlpha();
      }

      if (program.getBlendState() != null) {
         GlStateManager.unlockBlend();
      }

      GlAlphaState alphaState = program2.getAlphaState();
      if (alphaState != null) {
         GlStateManager.lockAlpha(alphaState);
      }

      GlBlendState blendState = program2.getBlendState();
      if (blendState != null) {
         GlStateManager.lockBlend(blendState);
      }
   }

   public static void setProgramUniform1i(ShaderUniform1i shaderUniform1i, int n) {
      shaderUniform1i.setValue(n);
   }

   public static void setProgramUniform2i(ShaderUniform2i shaderUniform2i, int n, int n2) {
      shaderUniform2i.setValue(n, n2);
   }

   public static void setProgramUniform1f(ShaderUniform1f shaderUniform1f, float f) {
      shaderUniform1f.setValue(f);
   }

   public static void setProgramUniform3f(ShaderUniform3f shaderUniform3f, float f, float f2, float f3) {
      shaderUniform3f.setValue(f, f2, f3);
   }

   public static void setProgramUniformMatrix4ARB(ShaderUniformM4 shaderUniformM4, boolean flag, FloatBuffer floatBuffer) {
      shaderUniformM4.setValue(flag, floatBuffer);
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
         for (ResourceLocation resourceLocation : Block.blockRegistry.getKeys()) {
            Block object = Block.blockRegistry.getObject(resourceLocation);
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

         for (int i = 0; i < ProgramsAll.length; i++) {
            Program program = ProgramsAll[i];
            if (program.getRef() != 0) {
               ARBShaderObjects.glDeleteObjectARB(program.getRef());
               checkGLError("del programRef");
            }

            program.setRef(0);
            program.setId(0);
            program.setDrawBufSettings((String)null);
            program.setDrawBuffers((IntBuffer)null);
            program.setCompositeMipmapSetting(0);
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
      EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
      GL20.glDrawBuffers(0);
      GL11.glReadBuffer(0);

      for (int i = 0; i < usedDepthBuffers; i++) {
         GlStateManager.bindTexture(dfbDepthTextures.get(i));
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
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
         GlStateManager.bindTexture(dfbColorTexturesFlip.getA(i));
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexImage2D(3553, 0, gbuffersFormat[i], renderWidth, renderHeight, 0, getPixelFormat(gbuffersFormat[i]), 33639, (ByteBuffer)null);
         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTexturesFlip.getA(i), 0);
         checkGLError("FT c");
      }

      for (int i = 0; i < usedColorBuffers; i++) {
         GlStateManager.bindTexture(dfbColorTexturesFlip.getB(i));
         GL11.glTexParameteri(3553, 10242, 33071);
         GL11.glTexParameteri(3553, 10243, 33071);
         GL11.glTexParameteri(3553, 10241, 9729);
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexImage2D(3553, 0, gbuffersFormat[i], renderWidth, renderHeight, 0, getPixelFormat(gbuffersFormat[i]), 33639, (ByteBuffer)null);
         checkGLError("FT ca");
      }

      int glCheckFramebufferStatusEXT = EXTFramebufferObject.glCheckFramebufferStatusEXT(36160);
      if (glCheckFramebufferStatusEXT == 36058) {
         printChatAndLogError("[Shaders] Error: Failed framebuffer incomplete formats");

         for (int i = 0; i < usedColorBuffers; i++) {
            GlStateManager.bindTexture(dfbColorTexturesFlip.getA(i));
            GL11.glTexImage2D(3553, 0, 6408, renderWidth, renderHeight, 0, 32993, 33639, (ByteBuffer)null);
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTexturesFlip.getA(i), 0);
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

   public static int getPixelFormat(int n) {
      switch (n) {
         case 33333:
         case 33334:
         case 33339:
         case 33340:
         case 36208:
         case 36209:
         case 36226:
         case 36227:
            return 36251;
         default:
            return 32993;
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
            GL11.glTexParameterf(3553, 10242, 33071.0F);
            GL11.glTexParameterf(3553, 10243, 33071.0F);
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
            GL11.glTexParameterf(3553, 10242, 33071.0F);
            GL11.glTexParameterf(3553, 10243, 33071.0F);
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
               setShaderPack("OFF");
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
      Entity renderViewEntity = mc.getRenderViewEntity();
      if (renderViewEntity != null) {
         isSleeping = renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPlayerSleeping();
         eyePosY = (float)renderViewEntity.posY * f + (float)renderViewEntity.lastTickPosY * (1.0F - f);
         eyeBrightness = renderViewEntity.getBrightnessForRender(f);
         exp = (float)diffSystemTime * 0.01F;
         float exp2 = (float)Math.exp(Math.log(0.5) * exp / eyeBrightnessHalflife);
         eyeBrightnessFadeX = eyeBrightnessFadeX * exp2 + (eyeBrightness & 65535) * (1.0F - exp2);
         eyeBrightnessFadeY = eyeBrightnessFadeY * exp2 + (eyeBrightness >> 16) * (1.0F - exp2);
         Block blockAtEntityViewpoint = ActiveRenderInfo.getBlockAtEntityViewpoint(mc.theWorld, renderViewEntity, f);
         Material material = blockAtEntityViewpoint.getMaterial();
         if (material == Material.water) {
            isEyeInWater = 1;
         } else if (material == Material.lava) {
            isEyeInWater = 2;
         } else {
            isEyeInWater = 0;
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
         skyColor = CustomColors.getWorldSkyColor(skyColor, currentWorld, renderViewEntity, f);
         skyColorR = (float)skyColor.xCoord;
         skyColorG = (float)skyColor.yCoord;
         skyColorB = (float)skyColor.zCoord;
      }

      isRenderingWorld = true;
      isCompositeRendered = false;
      isShadowPass = false;
      isHandRenderedMain = false;
      isHandRenderedOff = false;
      skipRenderHandMain = false;
      skipRenderHandOff = false;
      bindGbuffersTextures();
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
         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTexturesFlip.getA(i), 0);
      }

      checkGLError("end beginRender");
   }

   public static void bindGbuffersTextures() {
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
         GlStateManager.bindTexture(dfbColorTexturesFlip.getA(i));
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10241, 9729);
         GlStateManager.bindTexture(dfbColorTexturesFlip.getB(i));
         GL11.glTexParameteri(3553, 10240, 9729);
         GL11.glTexParameteri(3553, 10241, 9729);
      }

      GlStateManager.bindTexture(0);

      for (int i = 0; i < 4 && 4 + i < usedColorBuffers; i++) {
         GlStateManager.setActiveTexture(33991 + i);
         GlStateManager.bindTexture(dfbColorTexturesFlip.getA(4 + i));
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
   }

   public static void checkWorldChanged(World world) {
      if (currentWorld != world) {
         World currentWorld2 = currentWorld;
         currentWorld = world;
         setCameraOffset(mc.getRenderViewEntity());
         int dimensionId = getDimensionId(currentWorld2);
         int dimensionId2 = getDimensionId(world);
         if (dimensionId2 != dimensionId) {
            boolean flag = shaderPackDimensions.contains(dimensionId);
            boolean flag2 = shaderPackDimensions.contains(dimensionId2);
            if (flag || flag2) {
               uninit();
            }
         }

         Smoother.resetValues();
      }
   }

   public static int getDimensionId(World world) {
      return world == null ? Integer.MIN_VALUE : world.provider.getDimensionId();
   }

   public static void beginRenderPass(int n, float f, long l) {
      if (!isShadowPass) {
         EXTFramebufferObject.glBindFramebufferEXT(36160, dfb);
         GL11.glViewport(0, 0, renderWidth, renderHeight);
         activeDrawBuffers = null;
         ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
         useProgram(ProgramTextured);
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
         useProgram(ProgramTextured);
         checkGLError("beginRenderPass");
      }
   }

   public static void setFogMode(int n) {
      fogMode = n;
      if (fogEnabled) {
         setProgramUniform1i(uniform_fogMode, n);
      }
   }

   public static void setFogColor(float f, float f2, float f3) {
      fogColorR = f;
      fogColorG = f2;
      fogColorB = f3;
      setProgramUniform3f(uniform_fogColor, fogColorR, fogColorG, fogColorB);
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
         GL20.glDrawBuffers(ProgramShadow.getDrawBuffers());
         checkFramebufferStatus("shadow clear");
         GL11.glClear(16640);
         checkGLError("shadow clear");
      } else {
         checkGLError("clear pre");
         if (gbuffersClear[0]) {
            Vector4f vector4f = gbuffersClearColor[0];
            if (vector4f != null) {
               GL11.glClearColor(vector4f.getX(), vector4f.getY(), vector4f.getZ(), vector4f.getW());
            }

            if (dfbColorTexturesFlip.isChanged(0)) {
               EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, dfbColorTexturesFlip.getB(0), 0);
               GL20.glDrawBuffers(36064);
               GL11.glClear(16384);
               EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064, 3553, dfbColorTexturesFlip.getA(0), 0);
            }

            GL20.glDrawBuffers(36064);
            GL11.glClear(16384);
         }

         if (gbuffersClear[1]) {
            GL11.glClearColor(1.0F, 1.0F, 1.0F, 1.0F);
            Vector4f vector4f = gbuffersClearColor[1];
            if (vector4f != null) {
               GL11.glClearColor(vector4f.getX(), vector4f.getY(), vector4f.getZ(), vector4f.getW());
            }

            if (dfbColorTexturesFlip.isChanged(1)) {
               EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36065, 3553, dfbColorTexturesFlip.getB(1), 0);
               GL20.glDrawBuffers(36065);
               GL11.glClear(16384);
               EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36065, 3553, dfbColorTexturesFlip.getA(1), 0);
            }

            GL20.glDrawBuffers(36065);
            GL11.glClear(16384);
         }

         for (int i = 2; i < usedColorBuffers; i++) {
            if (gbuffersClear[i]) {
               GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
               Vector4f vector4f = gbuffersClearColor[i];
               if (vector4f != null) {
                  GL11.glClearColor(vector4f.getX(), vector4f.getY(), vector4f.getZ(), vector4f.getW());
               }

               if (dfbColorTexturesFlip.isChanged(i)) {
                  EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTexturesFlip.getB(i), 0);
                  GL20.glDrawBuffers(36064 + i);
                  GL11.glClear(16384);
                  EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTexturesFlip.getA(i), 0);
               }

               GL20.glDrawBuffers(36064 + i);
               GL11.glClear(16384);
            }
         }

         setDrawBuffers(dfbDrawBuffers);
         checkFramebufferStatus("clear");
         checkGLError("clear");
      }
   }

   public static void setCamera(float f) {
      Entity renderViewEntity = mc.getRenderViewEntity();
      double d = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * f;
      double d2 = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * f;
      double d3 = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * f;
      updateCameraOffset(renderViewEntity);
      cameraPositionX = d - cameraOffsetX;
      cameraPositionY = d2;
      cameraPositionZ = d3 - cameraOffsetZ;
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

   public static void updateCameraOffset(Entity entity) {
      double abs = Math.abs(cameraPositionX - previousCameraPositionX);
      double abs2 = Math.abs(cameraPositionZ - previousCameraPositionZ);
      double abs3 = Math.abs(cameraPositionX);
      double abs4 = Math.abs(cameraPositionZ);
      if (abs > 1000.0 || abs2 > 1000.0 || abs3 > 1000000.0 || abs4 > 1000000.0) {
         setCameraOffset(entity);
      }
   }

   public static void setCameraOffset(Entity entity) {
      if (entity == null) {
         cameraOffsetX = 0;
         cameraOffsetZ = 0;
      } else {
         cameraOffsetX = (int)entity.posX / 1000 * 1000;
         cameraOffsetZ = (int)entity.posZ / 1000 * 1000;
      }
   }

   public static void setCameraShadow(float f) {
      Entity renderViewEntity = mc.getRenderViewEntity();
      double d = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * f;
      double d2 = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * f;
      double d3 = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * f;
      updateCameraOffset(renderViewEntity);
      cameraPositionX = d - cameraOffsetX;
      cameraPositionY = d2;
      cameraPositionZ = d3 - cameraOffsetZ;
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
      setProgramUniformMatrix4ARB(uniform_gbufferProjection, false, projection);
      setProgramUniformMatrix4ARB(uniform_gbufferProjectionInverse, false, projectionInverse);
      setProgramUniformMatrix4ARB(uniform_gbufferPreviousProjection, false, previousProjection);
      setProgramUniformMatrix4ARB(uniform_gbufferModelView, false, modelView);
      setProgramUniformMatrix4ARB(uniform_gbufferModelViewInverse, false, modelViewInverse);
      setProgramUniformMatrix4ARB(uniform_gbufferPreviousModelView, false, previousModelView);
      setProgramUniformMatrix4ARB(uniform_shadowProjection, false, shadowProjection);
      setProgramUniformMatrix4ARB(uniform_shadowProjectionInverse, false, shadowProjectionInverse);
      setProgramUniformMatrix4ARB(uniform_shadowModelView, false, shadowModelView);
      setProgramUniformMatrix4ARB(uniform_shadowModelViewInverse, false, shadowModelViewInverse);
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
      setProgramUniform3f(uniform_sunPosition, sunPosition[0], sunPosition[1], sunPosition[2]);
      setProgramUniform3f(uniform_moonPosition, moonPosition[0], moonPosition[1], moonPosition[2]);
      setProgramUniform3f(uniform_shadowLightPosition, shadowLightPosition[0], shadowLightPosition[1], shadowLightPosition[2]);
      if (customUniforms != null) {
         customUniforms.update();
      }

      checkGLError("postCelestialRotate");
   }

   public static void setUpPosition() {
      FloatBuffer tempMatrixDirectBuffer2 = tempMatrixDirectBuffer;
      ((Buffer)tempMatrixDirectBuffer2).clear();
      GL11.glGetFloat(2982, tempMatrixDirectBuffer2);
      tempMatrixDirectBuffer2.get(tempMat, 0, 16);
      SMath.multiplyMat4xVec4(upPosition, tempMat, upPosModelView);
      setProgramUniform3f(uniform_upPosition, upPosition[0], upPosition[1], upPosition[2]);
      if (customUniforms != null) {
         customUniforms.update();
      }
   }

   public static void genCompositeMipmap() {
      if (hasGlGenMipmap) {
         for (int i = 0; i < usedColorBuffers; i++) {
            if ((activeCompositeMipmapSetting & 1 << i) != 0) {
               GlStateManager.setActiveTexture(33984 + colorTextureImageUnit[i]);
               GL11.glTexParameteri(3553, 10241, 9987);
               GL30.glGenerateMipmap(3553);
            }
         }

         GlStateManager.setActiveTexture(33984);
      }
   }

   public static void drawComposite() {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      drawCompositeQuad();
      int countInstances = activeProgram.getCountInstances();
      if (countInstances > 1) {
         for (int i = 1; i < countInstances; i++) {
            uniform_instanceId.setValue(i);
            drawCompositeQuad();
         }

         uniform_instanceId.setValue(0);
      }
   }

   public static void drawCompositeQuad() {
      if (!canRenderQuads()) {
         GL11.glBegin(5);
         GL11.glTexCoord2f(0.0F, 0.0F);
         GL11.glVertex3f(0.0F, 0.0F, 0.0F);
         GL11.glTexCoord2f(1.0F, 0.0F);
         GL11.glVertex3f(1.0F, 0.0F, 0.0F);
         GL11.glTexCoord2f(0.0F, 1.0F);
         GL11.glVertex3f(0.0F, 1.0F, 0.0F);
         GL11.glTexCoord2f(1.0F, 1.0F);
         GL11.glVertex3f(1.0F, 1.0F, 0.0F);
         GL11.glEnd();
      } else {
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
   }

   public static void renderDeferred() {
      if (!isShadowPass) {
         boolean checkBufferFlip2 = checkBufferFlip(ProgramDeferredPre);
         if (hasDeferredPrograms) {
            checkGLError("pre-render Deferred");
            renderComposites(ProgramsDeferred, false);
            checkBufferFlip2 = true;
         }

         if (checkBufferFlip2) {
            bindGbuffersTextures();

            for (int i = 0; i < usedColorBuffers; i++) {
               EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTexturesFlip.getA(i), 0);
            }

            if (ProgramWater.getDrawBuffers() != null) {
               setDrawBuffers(ProgramWater.getDrawBuffers());
            } else {
               setDrawBuffers(dfbDrawBuffers);
            }

            GlStateManager.setActiveTexture(33984);
            mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
         }
      }
   }

   public static void renderCompositeFinal() {
      if (!isShadowPass) {
         checkBufferFlip(ProgramCompositePre);
         checkGLError("pre-render CompositeFinal");
         renderComposites(ProgramsComposite, true);
      }
   }

   public static boolean checkBufferFlip(Program program) {
      boolean flag = false;
      Boolean[] buffersFlip = program.getBuffersFlip();

      for (int i = 0; i < usedColorBuffers; i++) {
         if (Config.isTrue(buffersFlip[i])) {
            dfbColorTexturesFlip.flip(i);
            flag = true;
         }
      }

      return flag;
   }

   public static void renderComposites(Program[] programs, boolean flag) {
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
            GlStateManager.setActiveTexture(33984 + colorTextureImageUnit[i]);
            GlStateManager.bindTexture(dfbColorTexturesFlip.getA(i));
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

         for (int i = 0; i < usedColorBuffers; i++) {
            EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + i, 3553, dfbColorTexturesFlip.getB(i), 0);
         }

         EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36096, 3553, dfbDepthTextures.get(0), 0);
         GL20.glDrawBuffers(dfbDrawBuffers);
         checkGLError("pre-composite");

         for (int i = 0; i < programs.length; i++) {
            Program program = programs[i];
            if (program.getId() != 0) {
               useProgram(program);
               checkGLError(program.getName());
               if (activeCompositeMipmapSetting != 0) {
                  genCompositeMipmap();
               }

               preDrawComposite();
               drawComposite();
               postDrawComposite();

               for (int j = 0; j < usedColorBuffers; j++) {
                  if (program.getToggleColorTextures()[j]) {
                     dfbColorTexturesFlip.flip(j);
                     GlStateManager.setActiveTexture(33984 + colorTextureImageUnit[j]);
                     GlStateManager.bindTexture(dfbColorTexturesFlip.getA(j));
                     EXTFramebufferObject.glFramebufferTexture2DEXT(36160, 36064 + j, 3553, dfbColorTexturesFlip.getB(j), 0);
                  }
               }

               GlStateManager.setActiveTexture(33984);
            }
         }

         checkGLError("composite");
         if (flag) {
            renderFinal();
            isCompositeRendered = true;
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
         useProgram(ProgramNone);
      }
   }

   public static void preDrawComposite() {
      RenderScale renderScale = activeProgram.getRenderScale();
      if (renderScale != null) {
         int n = (int)(renderWidth * renderScale.getOffsetX());
         int n2 = (int)(renderHeight * renderScale.getOffsetY());
         int n3 = (int)(renderWidth * renderScale.getScale());
         int n4 = (int)(renderHeight * renderScale.getScale());
         GL11.glViewport(n, n2, n3, n4);
      }
   }

   public static void postDrawComposite() {
      RenderScale renderScale = activeProgram.getRenderScale();
      if (renderScale != null) {
         GL11.glViewport(0, 0, renderWidth, renderHeight);
      }
   }

   public static void renderFinal() {
      isRenderingDfb = false;
      mc.getFramebuffer().bindFramebuffer(true);
      OpenGlHelper.glFramebufferTexture2D(OpenGlHelper.GL_FRAMEBUFFER, OpenGlHelper.GL_COLOR_ATTACHMENT0, 3553, mc.getFramebuffer().framebufferTexture, 0);
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
      useProgram(ProgramFinal);
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
         useProgram(ProgramNone);
         RenderHelper.disableStandardItemLighting();
         checkGLError("endRender end");
      }
   }

   public static void beginSky() {
      isRenderingSky = true;
      fogEnabled = true;
      setDrawBuffers(dfbDrawBuffers);
      useProgram(ProgramSkyTextured);
      pushEntity(-2, 0);
   }

   public static void setSkyColor(Vec3 vec3) {
      skyColorR = (float)vec3.xCoord;
      skyColorG = (float)vec3.yCoord;
      skyColorB = (float)vec3.zCoord;
      setProgramUniform3f(uniform_skyColor, skyColorR, skyColorG, skyColorB);
   }

   public static void drawHorizon() {
      WorldRenderer worldRenderer = Tessellator.getInstance().getWorldRenderer();
      float f = mc.gameSettings.renderDistanceChunks * 16;
      double d = f * 0.9238;
      double d2 = f * 0.3826;
      double d3 = -d2;
      double d4 = -d;
      double d5 = 16.0;
      double d6 = -cameraPositionY;
      worldRenderer.begin(7, DefaultVertexFormats.POSITION);
      worldRenderer.pos(d3, d6, d4).endVertex();
      worldRenderer.pos(d3, d5, d4).endVertex();
      worldRenderer.pos(d4, d5, d3).endVertex();
      worldRenderer.pos(d4, d6, d3).endVertex();
      worldRenderer.pos(d4, d6, d3).endVertex();
      worldRenderer.pos(d4, d5, d3).endVertex();
      worldRenderer.pos(d4, d5, d2).endVertex();
      worldRenderer.pos(d4, d6, d2).endVertex();
      worldRenderer.pos(d4, d6, d2).endVertex();
      worldRenderer.pos(d4, d5, d2).endVertex();
      worldRenderer.pos(d3, d5, d).endVertex();
      worldRenderer.pos(d3, d6, d).endVertex();
      worldRenderer.pos(d3, d6, d).endVertex();
      worldRenderer.pos(d3, d5, d).endVertex();
      worldRenderer.pos(d2, d5, d).endVertex();
      worldRenderer.pos(d2, d6, d).endVertex();
      worldRenderer.pos(d2, d6, d).endVertex();
      worldRenderer.pos(d2, d5, d).endVertex();
      worldRenderer.pos(d, d5, d2).endVertex();
      worldRenderer.pos(d, d6, d2).endVertex();
      worldRenderer.pos(d, d6, d2).endVertex();
      worldRenderer.pos(d, d5, d2).endVertex();
      worldRenderer.pos(d, d5, d3).endVertex();
      worldRenderer.pos(d, d6, d3).endVertex();
      worldRenderer.pos(d, d6, d3).endVertex();
      worldRenderer.pos(d, d5, d3).endVertex();
      worldRenderer.pos(d2, d5, d4).endVertex();
      worldRenderer.pos(d2, d6, d4).endVertex();
      worldRenderer.pos(d2, d6, d4).endVertex();
      worldRenderer.pos(d2, d5, d4).endVertex();
      worldRenderer.pos(d3, d5, d4).endVertex();
      worldRenderer.pos(d3, d6, d4).endVertex();
      worldRenderer.pos(d4, d6, d4).endVertex();
      worldRenderer.pos(d4, d6, d).endVertex();
      worldRenderer.pos(d, d6, d).endVertex();
      worldRenderer.pos(d, d6, d4).endVertex();
      Tessellator.getInstance().draw();
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
      useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
      popEntity();
   }

   public static void beginUpdateChunks() {
      checkGLError("beginUpdateChunks1");
      checkFramebufferStatus("beginUpdateChunks1");
      if (!isShadowPass) {
         useProgram(ProgramTerrain);
      }

      checkGLError("beginUpdateChunks2");
      checkFramebufferStatus("beginUpdateChunks2");
   }

   public static void endUpdateChunks() {
      checkGLError("endUpdateChunks1");
      checkFramebufferStatus("endUpdateChunks1");
      if (!isShadowPass) {
         useProgram(ProgramTerrain);
      }

      checkGLError("endUpdateChunks2");
      checkFramebufferStatus("endUpdateChunks2");
   }

   public static boolean shouldRenderClouds(GameSettings gameSettings) {
      if (!shaderPackLoaded) {
         return true;
      }

      checkGLError("shouldRenderClouds");
      return isShadowPass ? configCloudShadow : gameSettings.clouds > 0;
   }

   public static void beginClouds() {
      fogEnabled = true;
      pushEntity(-3, 0);
      useProgram(ProgramClouds);
   }

   public static void endClouds() {
      disableFog();
      popEntity();
      useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
   }

   public static void beginEntities() {
      if (isRenderingWorld) {
         useProgram(ProgramEntities);
      }
   }

   public static void nextEntity(Entity entity) {
      if (isRenderingWorld) {
         useProgram(ProgramEntities);
         setEntityId(entity);
      }
   }

   public static void setEntityId(Entity entity) {
      if (uniform_entityId.isDefined()) {
         int entityIdByClass = EntityUtils.getEntityIdByClass(entity);
         int entityAliasId = EntityAliases.getEntityAliasId(entityIdByClass);
         if (entityAliasId >= 0) {
            entityIdByClass = entityAliasId;
         }

         uniform_entityId.setValue(entityIdByClass);
      }
   }

   public static void beginSpiderEyes() {
      if (isRenderingWorld && ProgramSpiderEyes.getId() != ProgramNone.getId()) {
         useProgram(ProgramSpiderEyes);
         GlStateManager.enableAlpha();
         GlStateManager.alphaFunc(516, 0.0F);
         GlStateManager.blendFunc(770, 771);
      }
   }

   public static void endSpiderEyes() {
      if (isRenderingWorld && ProgramSpiderEyes.getId() != ProgramNone.getId()) {
         useProgram(ProgramEntities);
         GlStateManager.disableAlpha();
      }
   }

   public static void endEntities() {
      if (isRenderingWorld) {
         setEntityId((Entity)null);
         useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
      }
   }

   public static void beginEntitiesGlowing() {
      if (isRenderingWorld) {
         isEntitiesGlowing = true;
      }
   }

   public static void endEntitiesGlowing() {
      if (isRenderingWorld) {
         isEntitiesGlowing = false;
      }
   }

   public static void setEntityColor(float f, float f2, float f3, float f4) {
      if (isRenderingWorld && !isShadowPass) {
         uniform_entityColor.setValue(f, f2, f3, f4);
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
         setDrawBuffers(ProgramEntities.getDrawBuffers());
      }
   }

   public static void beginBlockEntities() {
      if (isRenderingWorld) {
         checkGLError("beginBlockEntities");
         useProgram(ProgramBlock);
      }
   }

   public static void nextBlockEntity(TileEntity tileEntity) {
      if (isRenderingWorld) {
         checkGLError("nextBlockEntity");
         useProgram(ProgramBlock);
         setBlockEntityId(tileEntity);
      }
   }

   public static void setBlockEntityId(TileEntity tileEntity) {
      if (uniform_blockEntityId.isDefined()) {
         int blockEntityId = getBlockEntityId(tileEntity);
         uniform_blockEntityId.setValue(blockEntityId);
      }
   }

   public static int getBlockEntityId(TileEntity tileEntity) {
      if (tileEntity == null) {
         return -1;
      }

      Block blockType = tileEntity.getBlockType();
      if (blockType == null) {
         return 0;
      }

      int idFromBlock = Block.getIdFromBlock(blockType);
      int blockMetadata = tileEntity.getBlockMetadata();
      int blockAliasId = BlockAliases.getBlockAliasId(idFromBlock, blockMetadata);
      if (blockAliasId >= 0) {
         idFromBlock = blockAliasId;
      }

      return idFromBlock;
   }

   public static void endBlockEntities() {
      if (isRenderingWorld) {
         checkGLError("endBlockEntities");
         setBlockEntityId((TileEntity)null);
         useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
         ShadersTex.bindNSTextures(defaultTexture.getMultiTexID());
      }
   }

   public static void beginLitParticles() {
      useProgram(ProgramTexturedLit);
   }

   public static void beginParticles() {
      useProgram(ProgramTextured);
   }

   public static void endParticles() {
      useProgram(ProgramTexturedLit);
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
         useProgram(ProgramWeather);
      }
   }

   public static void endWeather() {
      GlStateManager.disableBlend();
      useProgram(ProgramTexturedLit);
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
            useProgram(ProgramWater);
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

         useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
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
         useProgram(ProgramHandWater);
      } else {
         useProgram(ProgramHand);
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
         useProgram(ProgramSkyTextured);
      } else if (activeProgram == ProgramBasic) {
         useProgram(lightmapEnabled ? ProgramTexturedLit : ProgramTextured);
      }
   }

   public static void disableTexture2D() {
      if (isRenderingSky) {
         useProgram(ProgramSkyBasic);
      } else if (activeProgram == ProgramTextured || activeProgram == ProgramTexturedLit) {
         useProgram(ProgramBasic);
      }
   }

   public static void pushProgram() {
      programStack.push(activeProgram);
   }

   public static void popProgram() {
      Program pop = programStack.pop();
      useProgram(pop);
   }

   public static void beginLeash() {
      pushProgram();
      useProgram(ProgramBasic);
   }

   public static void endLeash() {
      popProgram();
   }

   public static void enableFog() {
      fogEnabled = true;
      setProgramUniform1i(uniform_fogMode, fogMode);
      setProgramUniform1f(uniform_fogDensity, fogDensity);
   }

   public static void disableFog() {
      fogEnabled = false;
      setProgramUniform1i(uniform_fogMode, 0);
   }

   public static void setFogDensity(float f) {
      fogDensity = f;
      if (fogEnabled) {
         setProgramUniform1f(uniform_fogDensity, f);
      }
   }

   public static void sglFogi(int n, int n2) {
      GL11.glFogi(n, n2);
      if (n == 2917) {
         fogMode = n2;
         if (fogEnabled) {
            setProgramUniform1i(uniform_fogMode, fogMode);
         }
      }
   }

   public static void enableLightmap() {
      lightmapEnabled = true;
      if (activeProgram == ProgramTextured) {
         useProgram(ProgramTexturedLit);
      }
   }

   public static void disableLightmap() {
      lightmapEnabled = false;
      if (activeProgram == ProgramTexturedLit) {
         useProgram(ProgramTextured);
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
      entityDataIndex++;
      int renderType = block.getRenderType();
      entityData[entityDataIndex * 2] = Block.blockRegistry.getIDForObject(block) & 65535 | renderType << 16;
      entityData[entityDataIndex * 2 + 1] = 0;
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

   public static void nextAntialiasingLevel(boolean flag) {
      if (flag) {
         configAntialiasingLevel += 2;
         if (configAntialiasingLevel > 4) {
            configAntialiasingLevel = 0;
         }
      } else {
         configAntialiasingLevel -= 2;
         if (configAntialiasingLevel < 0) {
            configAntialiasingLevel = 4;
         }
      }

      configAntialiasingLevel = configAntialiasingLevel / 2 * 2;
      configAntialiasingLevel = Config.limit(configAntialiasingLevel, 0, 4);
   }

   public static void checkShadersModInstalled() {
      try {
         Class forName = Class.forName("shadersmod.transform.SMCClassTransformer");
      } catch (Throwable e) {
         return;
      }

      throw new RuntimeException("Shaders Mod detected. Please remove it, OptiFine has built-in support for shaders.");
   }

   public static void resourcesReloaded() {
      loadShaderPackResources();
      if (shaderPackLoaded) {
         BlockAliases.resourcesReloaded();
         ItemAliases.resourcesReloaded();
         EntityAliases.resourcesReloaded();
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
                  PropertiesOrdered propertiesOrdered = new PropertiesOrdered();
                  Lang.loadLocaleData(resourceAsStream, propertiesOrdered);
                  resourceAsStream.close();

                  for (Object obj : propertiesOrdered.keySet()) {
                     String str5 = (String)obj;
                     String property = propertiesOrdered.getProperty(str5);
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

      Program program = getProgram(str);
      return program != null;
   }

   public static Program getProgram(String str) {
      return programs.getProgram(str);
   }

   public static boolean isItemToRenderMainTranslucent() {
      return itemToRenderMainTranslucent;
   }

   public static boolean isItemToRenderOffTranslucent() {
      return itemToRenderOffTranslucent;
   }

   public static boolean isBothHandsRendered() {
      return isHandRenderedMain && isHandRenderedOff;
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
      Block block = itemBlock.getBlock();
      if (block == null) {
         return false;
      }

      EnumWorldBlockLayer blockLayer = block.getBlockLayer();
      return blockLayer == EnumWorldBlockLayer.TRANSLUCENT;
   }

   public static boolean isSkipRenderHand() {
      return skipRenderHandMain;
   }

   public static boolean isRenderBothHands() {
      return !skipRenderHandMain && !skipRenderHandOff;
   }

   public static void setSkipRenderHands(boolean flag, boolean flag2) {
      skipRenderHandMain = flag;
      skipRenderHandOff = flag2;
   }

   public static void setHandsRendered(boolean flag, boolean flag2) {
      isHandRenderedMain = flag;
      isHandRenderedOff = flag2;
   }

   public static boolean isHandRenderedMain() {
      return isHandRenderedMain;
   }

   public static boolean isHandRenderedOff() {
      return isHandRenderedOff;
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
         useProgram(ProgramBeaconBeam);
      }
   }

   public static void endBeacon() {
      if (isRenderingWorld) {
         useProgram(ProgramBlock);
      }
   }

   public static World getCurrentWorld() {
      return currentWorld;
   }

   public static BlockPos getCameraPosition() {
      return new BlockPos(cameraPositionX, cameraPositionY, cameraPositionZ);
   }

   public static boolean isCustomUniforms() {
      return customUniforms != null;
   }

   public static boolean canRenderQuads() {
      return hasGeometryShaders ? capabilities.GL_NV_geometry_shader4 : true;
   }

   public static void handleItemStack(ItemStack itemStack) {
      itemToRenderMainTranslucent = isTranslucentBlock(itemStack);
   }

   public static void process(int n) {
      GL11.glDisable(n);
      disableFog();
   }

   public static void process2(int n) {
      GL11.glEnable(n);
      enableTexture2D();
   }

   public static void process3(int n) {
      GL11.glDisable(n);
      disableTexture2D();
   }

   public static void handleItemStack2(ItemStack itemStack) {
      itemToRenderOffTranslucent = isTranslucentBlock(itemStack);
   }

   public static void process4(int n) {
      GL11.glEnable(n);
      if (n == 3553) {
         enableTexture2D();
      } else if (n == 2912) {
         enableFog();
      }
   }

   public static void process5(int n) {
      GL11.glEnable(n);
      enableFog();
   }

   public static void process6(int n) {
      entityDataIndex++;
      entityData[entityDataIndex * 2] = n & 65535;
      entityData[entityDataIndex * 2 + 1] = 0;
   }

   public static void process7(int n) {
      GL11.glDisable(n);
      if (n == 3553) {
         disableTexture2D();
      } else if (n == 2912) {
         disableFog();
      }
   }
}
