package net.minecraft.client.renderer;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.EntityLivingBaseFloatEvent1;
import com.cheatbreaker.client.event.FloatEvent1;
import com.cheatbreaker.client.event.FloatEvent3;
import com.cheatbreaker.client.event.FloatEvent6;
import com.cheatbreaker.client.event.RenderGlobalFloatEvent;
import com.cheatbreaker.client.event.RenderRainSnowEvent;
import com.cheatbreaker.client.event.SimpleEvent46;
import com.cheatbreaker.client.event.SimpleEvent47;
import com.cheatbreaker.client.event.StringFloatEvent;
import com.cheatbreaker.client.mod.KeybindSettings;
import com.cheatbreaker.client.mod.impl.EnvironmentChangerMod;
import com.cheatbreaker.client.mod.impl.FreeLookHandler;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import com.cheatbreaker.client.util.MouseSensitivityConverter;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.particle.EntityRainFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustrum;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.Potion;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.Config;
import net.optifine.CustomColorizer;
import net.optifine.ItemRendererOF;
import net.optifine.RandomMobs;
import net.optifine.Reflector;
import net.optifine.ReflectorForge;
import net.optifine.RenderPlayerOF;
import net.optifine.TextureUtils;
import net.optifine.WrUpdates;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.glu.Project;
import shadersmod.client.Shaders;
import shadersmod.client.ShadersRender;

public class EntityRenderer implements IResourceManagerReloadListener {
    public static Logger logger = LogManager.getLogger();
    public static ResourceLocation locationRainPng = new ResourceLocation("textures/environment/rain.png");
    public static ResourceLocation locationSnowPng = new ResourceLocation("textures/environment/snow.png");
    public static boolean anaglyphEnable;
    public static int anaglyphField;
    public MouseFilter mouseFilterXAxis = new MouseFilter();
    public float farPlaneDistance;
    public MouseFilter mouseFilterYAxis = new MouseFilter();
    public MouseFilter mouseFilterDummy1 = new MouseFilter();
    public int rendererUpdateCount;
    public Entity pointedEntity;
    public MouseFilter mouseFilterDummy2 = new MouseFilter();
    public MouseFilter mouseFilterDummy3 = new MouseFilter();
    public MouseFilter mouseFilterDummy4 = new MouseFilter();
    public float thirdPersonDistance = 4.0F;
    public float thirdPersonDistanceTemp = 4.0F;
    public float floatValue = 0.0F;
    public boolean initialized = false;
    public World updatedWorld = null;
    public float debugCamYaw;
    public float prevDebugCamYaw;
    public float debugCamPitch;
    public float prevDebugCamPitch;
    public float smoothCamYaw;
    public float smoothCamPitch;
    public float smoothCamFilterX;
    public float smoothCamFilterY;
    public float smoothCamPartialTicks;
    public float debugCamFOV;
    public float prevDebugCamFOV;
    public float camRoll;
    public float prevCamRoll;
    public boolean showDebugInfo = false;
    public boolean fogStandard = false;
    public long lastServerTime = 0L;
    public float fovModifierHand;
    public float fovModifierHandPrev;
    public float fovMultiplierTemp;
    public float bossColorModifier;
    public float bossColorModifierPrev;
    public boolean cloudFog;
    public int lastServerTicks = 0;
    public int serverWaitTime = 0;
    public static ResourceLocation[] shaderResourceLocations = new ResourceLocation[]{
        new ResourceLocation("shaders/post/notch.json"),
        new ResourceLocation("shaders/post/fxaa.json"),
        new ResourceLocation("shaders/post/art.json"),
        new ResourceLocation("shaders/post/bumpy.json"),
        new ResourceLocation("shaders/post/blobs2.json"),
        new ResourceLocation("shaders/post/pencil.json"),
        new ResourceLocation("shaders/post/color_convolve.json"),
        new ResourceLocation("shaders/post/deconverge.json"),
        new ResourceLocation("shaders/post/flip.json"),
        new ResourceLocation("shaders/post/invert.json"),
        new ResourceLocation("shaders/post/ntsc.json"),
        new ResourceLocation("shaders/post/outline.json"),
        new ResourceLocation("shaders/post/phosphor.json"),
        new ResourceLocation("shaders/post/scan_pincushion.json"),
        new ResourceLocation("shaders/post/sobel.json"),
        new ResourceLocation("shaders/post/bits.json"),
        new ResourceLocation("shaders/post/desaturate.json"),
        new ResourceLocation("shaders/post/green.json"),
        new ResourceLocation("shaders/post/blur.json"),
        new ResourceLocation("shaders/post/wobble.json"),
        new ResourceLocation("shaders/post/blobs.json"),
        new ResourceLocation("shaders/post/antialias.json")
    };
    public static ResourceLocation animblurJson = new ResourceLocation("shaders/post/animblur.json");
    public int serverWaitTimeCurrent = 0;
    public float avgServerTimeDiff = 0.0F;
    public double cameraYaw;
    public double cameraPitch;
    public float avgServerTickDiff = 0.0F;
    public long renderEndNanoTime;
    public boolean lightmapUpdateNeeded;
    public float torchFlickerX;
    public float torchFlickerDX;
    public float torchFlickerY;
    public float torchFlickerDY;
    public long[] frameTimes = new long[512];
    public int rainSoundCounter;
    public float[] rainXCoords;
    public float[] rainYCoords;
    public long[] tickTimes = new long[512];
    public float fogColorRed;
    public float fogColorGreen;
    public float fogColorBlue;
    public float fogColor2;
    public float fogColor1;
    public int debugViewDirection;
    public long[] chunkTimes = new long[512];
    public long[] serverTimes = new long[512];
    public int numRecordedFrameTimes = 0;
    public long prevFrameTimeNano = -1L;
    public boolean lastShowDebugInfo = false;
    public boolean showExtendedDebugInfo = false;
    public long lastErrorCheckTimeMs = 0L;
    public ShaderGroup[] fxaaShaders = new ShaderGroup[10];
    public int shaderIndex;
    public double cameraZoom;
    public long prevFrameTime;
    public Random random;
    public FloatBuffer fogColorBuffer;
    public Minecraft mc;
    public IResourceManager resourceManager;
    public MapItemRenderer theMapItemRenderer;
    public ItemRenderer itemRenderer;
    public DynamicTexture lightmapTexture;
    public ResourceLocation locationLightMap;
    public int[] lightmapColors;
    public int frameCount;
    public ShaderGroup shaderGroup;
    public static ResourceLocation motionblurJson = new ResourceLocation("shaders/post/motionblur.json");
    public static ResourceLocation lunarMotionblurJson = new ResourceLocation("shaders/post/lunar_motionblur.json");
    public ShaderGroup theShaderGroup;
    public static int shaderCount = shaderResourceLocations.length;

    public EntityRenderer(Minecraft mc, IResourceManager resourceManager) {
        this.shaderIndex = shaderCount;
        this.cameraZoom = 1.0;
        this.prevFrameTime = Minecraft.getSystemTime();
        this.random = new Random();
        this.fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
        this.mc = mc;
        this.resourceManager = resourceManager;
        this.theMapItemRenderer = new MapItemRenderer(mc.getTextureManager());
        this.itemRenderer = new ItemRenderer(mc);
        this.lightmapTexture = new DynamicTexture(16, 16);
        this.locationLightMap = mc.getTextureManager().getDynamicTextureLocation("lightMap", this.lightmapTexture);
        this.lightmapColors = this.lightmapTexture.getTextureData();
        this.theShaderGroup = null;
    }

    public boolean isShaderActive() {
        return OpenGlHelper.shadersSupported && this.theShaderGroup != null;
    }

    public void stopUseShader() {
        if (this.shaderGroup != null) {
            this.shaderGroup.deleteShaderGroup();
        }

        this.shaderGroup = null;
    }

    public void deactivateShader() {
        if (this.theShaderGroup != null) {
            this.theShaderGroup.deleteShaderGroup();
        }

        this.theShaderGroup = null;
        this.shaderIndex = shaderCount;
    }

    public void activateNextShader() {
        if (OpenGlHelper.isFramebufferEnabled() && OpenGlHelper.shadersSupported) {
            if (this.theShaderGroup != null) {
                this.theShaderGroup.deleteShaderGroup();
            }

            this.shaderIndex = (this.shaderIndex + 1) % (shaderResourceLocations.length + 1);
            if (this.shaderIndex != shaderCount) {
                try {
                    logger.info("Selecting effect " + shaderResourceLocations[this.shaderIndex]);
                    this.theShaderGroup = new ShaderGroup(
                        this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), shaderResourceLocations[this.shaderIndex]
                    );
                    this.theShaderGroup.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
                } catch (IOException e) {
                    logger.warn("Failed to load shader: " + shaderResourceLocations[this.shaderIndex], e);
                    this.shaderIndex = shaderCount;
                } catch (JsonSyntaxException e) {
                    logger.warn("Failed to load shader: " + shaderResourceLocations[this.shaderIndex], e);
                    this.shaderIndex = shaderCount;
                }
            } else {
                this.theShaderGroup = null;
                logger.info("No effect selected");
            }
        }
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        if (this.theShaderGroup != null) {
            this.theShaderGroup.deleteShaderGroup();
        }

        if (this.shaderGroup != null) {
            this.shaderGroup.deleteShaderGroup();
            this.shaderGroup = null;
        }

        if (OpenGlHelper.isFramebufferEnabled()) {
            if (this.shaderIndex != shaderCount) {
                try {
                    this.theShaderGroup = new ShaderGroup(this.mc.getTextureManager(), resourceManager, this.mc.getFramebuffer(), shaderResourceLocations[this.shaderIndex]);
                    this.theShaderGroup.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
                } catch (IOException e) {
                    logger.warn("Failed to load shader: " + shaderResourceLocations[this.shaderIndex], e);
                    this.shaderIndex = shaderCount;
                }
            }

            if (CheatBreaker.getInstance() != null && CheatBreaker.getInstance().getKeybindSettings().guiBlur.getBooleanValue()) {
                try {
                    this.shaderGroup = new ShaderGroup(this.mc.getTextureManager(), resourceManager, this.mc.getFramebuffer(), animblurJson);
                    this.shaderGroup.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
                } catch (Exception e) {
                }
            }
        }
    }

    public void updateRenderer() {
        if (OpenGlHelper.shadersSupported && ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
            ShaderLinkHelper.setNewStaticShaderLinkHelper();
        }

        this.updateFovModifierHand();
        this.updateTorchFlicker();
        this.fogColor2 = this.fogColor1;
        this.thirdPersonDistanceTemp = this.thirdPersonDistance;
        this.prevDebugCamYaw = this.debugCamYaw;
        this.prevDebugCamPitch = this.debugCamPitch;
        this.prevDebugCamFOV = this.debugCamFOV;
        this.prevCamRoll = this.camRoll;
        if (this.mc.gameSettings.smoothCamera) {
            float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
            float f2 = f * f * f * 8.0F;
            this.smoothCamFilterX = this.mouseFilterXAxis.smooth(this.smoothCamYaw, 0.05F * f2);
            this.smoothCamFilterY = this.mouseFilterYAxis.smooth(this.smoothCamPitch, 0.05F * f2);
            this.smoothCamPartialTicks = 0.0F;
            this.smoothCamYaw = 0.0F;
            this.smoothCamPitch = 0.0F;
        }

        if (this.mc.renderViewEntity == null) {
            this.mc.renderViewEntity = this.mc.thePlayer;
        }

        float lightBrightness = this.mc
            .theWorld
            .getLightBrightness(
                MathHelper.floor_double(this.mc.renderViewEntity.posX),
                MathHelper.floor_double(this.mc.renderViewEntity.posY),
                MathHelper.floor_double(this.mc.renderViewEntity.posZ)
            );
        float f = this.mc.gameSettings.renderDistanceChunks / 16.0F;
        float f2 = lightBrightness * (1.0F - f) + f;
        this.fogColor1 = this.fogColor1 + (f2 - this.fogColor1) * 0.1F;
        this.rendererUpdateCount++;
        this.itemRenderer.updateEquippedItem();
        this.addRainParticles();
        this.bossColorModifierPrev = this.bossColorModifier;
        if (BossStatus.hasColorModifier) {
            this.bossColorModifier += 0.05F;
            if (this.bossColorModifier > 1.0F) {
                this.bossColorModifier = 1.0F;
            }

            BossStatus.hasColorModifier = false;
        } else if (this.bossColorModifier > 0.0F) {
            this.bossColorModifier -= 0.0125F;
        }
    }

    public ShaderGroup getShaderGroup() {
        return this.theShaderGroup;
    }

    public void updateShaderGroupSize(int n, int n2) {
        if (OpenGlHelper.shadersSupported && this.theShaderGroup != null) {
            this.theShaderGroup.createBindFramebuffers(n, n2);
        }

        if (OpenGlHelper.shadersSupported && this.shaderGroup != null) {
            this.shaderGroup.createBindFramebuffers(n, n2);
        }
    }

    public void getMouseOver(float f) {
        if (this.mc.renderViewEntity != null && this.mc.theWorld != null) {
            this.mc.pointedEntity = null;
            double blockReachDistance = this.mc.playerController.getBlockReachDistance();
            this.mc.objectMouseOver = this.mc.renderViewEntity.rayTrace(blockReachDistance, f);
            double d = blockReachDistance;
            Vec3 position = this.mc.renderViewEntity.getPosition(f);
            if (this.mc.playerController.extendedReach()) {
                blockReachDistance = 6.0;
                d = 6.0;
            } else {
                if (blockReachDistance > 3.0) {
                    d = 3.0;
                }

                blockReachDistance = d;
            }

            if (this.mc.objectMouseOver != null) {
                d = this.mc.objectMouseOver.hitVec.distanceTo(position);
            }

            Vec3 look = this.mc.renderViewEntity.getLook(f);
            Vec3 addVector = position.addVector(look.xCoord * blockReachDistance, look.yCoord * blockReachDistance, look.zCoord * blockReachDistance);
            this.pointedEntity = null;
            Vec3 vec3 = null;
            float f2 = 1.0F;
            List list = this.mc
                .theWorld
                .getEntitiesWithinAABBExcludingEntity(
                    this.mc.renderViewEntity,
                    this.mc.renderViewEntity.boundingBox.addCoord(look.xCoord * blockReachDistance, look.yCoord * blockReachDistance, look.zCoord * blockReachDistance).expand(f2, f2, f2)
                );
            double d2 = d;

            for (int i = 0; i < list.size(); i++) {
                Entity entity = (Entity)list.get(i);
                if (entity.canBeCollidedWith()) {
                    float collisionBorderSize = entity.getCollisionBorderSize();
                    AxisAlignedBB expand = entity.boundingBox.expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                    MovingObjectPosition calculateIntercept = expand.calculateIntercept(position, addVector);
                    if (expand.isVecInside(position)) {
                        if (0.0 < d2 || d2 == 0.0) {
                            this.pointedEntity = entity;
                            vec3 = calculateIntercept == null ? position : calculateIntercept.hitVec;
                            d2 = 0.0;
                        }
                    } else if (calculateIntercept != null) {
                        double distanceTo = position.distanceTo(calculateIntercept.hitVec);
                        if (distanceTo < d2 || d2 == 0.0) {
                            boolean flag = false;
                            if (entity != this.mc.renderViewEntity.ridingEntity || flag) {
                                this.pointedEntity = entity;
                                vec3 = calculateIntercept.hitVec;
                                d2 = distanceTo;
                            } else if (d2 == 0.0) {
                                this.pointedEntity = entity;
                                vec3 = calculateIntercept.hitVec;
                            }
                        }
                    }
                }
            }

            if (this.pointedEntity != null && (d2 < d || this.mc.objectMouseOver == null)) {
                this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, vec3);
                if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
                    this.mc.pointedEntity = this.pointedEntity;
                }
            }
        }
    }

    public void updateFovModifierHand() {
        if (this.mc.renderViewEntity instanceof EntityPlayerSP) {
            EntityPlayerSP renderViewEntity = (EntityPlayerSP)this.mc.renderViewEntity;
            this.fovMultiplierTemp = renderViewEntity.getFOVMultiplier();
        } else {
            this.fovMultiplierTemp = this.mc.thePlayer.getFOVMultiplier();
        }

        this.fovModifierHandPrev = this.fovModifierHand;
        this.fovModifierHand = this.fovModifierHand + (this.fovMultiplierTemp - this.fovModifierHand) * 0.5F;
        if (this.fovModifierHand > 1.5F) {
            this.fovModifierHand = 1.5F;
        }

        if (this.fovModifierHand < 0.1F) {
            this.fovModifierHand = 0.1F;
        }
    }

    public float getFOVModifier(float f, boolean flag) {
        if (this.debugViewDirection > 0) {
            return 90.0F;
        }

        EntityLivingBase renderViewEntity = this.mc.renderViewEntity;
        float f2 = 70.0F;
        if (flag) {
            f2 = this.mc.gameSettings.fovSetting;
            FloatEvent3 floatEvent3 = new FloatEvent3(f2);
            CheatBreaker.getInstance().getEventBus().handleEvent(floatEvent3);
            f2 = floatEvent3.floatValue;
            if (Config.isDynamicFov() && !floatEvent3.isCancelled()) {
                f2 *= this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * f;
            }
        }

        boolean flag2 = false;
        if (this.mc.currentScreen == null) {
            if (this.mc.gameSettings.ofKeyBindZoom.getKeyCode() < 0) {
                flag2 = Mouse.isButtonDown(this.mc.gameSettings.ofKeyBindZoom.getKeyCode() + 100);
            } else {
                flag2 = Keyboard.isKeyDown(this.mc.gameSettings.ofKeyBindZoom.getKeyCode());
            }
        }

        if (flag2) {
            if (!Config.zoomMode) {
                Config.zoomMode = true;
                Config.floatValue = 1.0F;
                this.mc.gameSettings.smoothCamera = CheatBreaker.getInstance().getKeybindSettings().useCinematicCamera.getBooleanValue();
            }

            if (Config.zoomMode) {
                f2 /= 4.0F * Config.floatValue;
            }
        } else if (Config.zoomMode) {
            Config.zoomMode = false;
            this.mc.gameSettings.smoothCamera = false;
            this.mouseFilterXAxis = new MouseFilter();
            this.mouseFilterYAxis = new MouseFilter();
        }

        if (renderViewEntity.getHealth() <= 0.0F) {
            float f3 = renderViewEntity.deathTime + f;
            f2 /= (1.0F - 500.0F / (f3 + 500.0F)) * 2.0F + 1.0F;
        }

        Block blockAtEntityViewpoint = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, renderViewEntity, f);
        if (blockAtEntityViewpoint.getMaterial() == Material.water) {
            f2 = f2 * 60.0F / 70.0F;
        }

        return f2 + this.prevDebugCamFOV + (this.debugCamFOV - this.prevDebugCamFOV) * f;
    }

    public void hurtCameraEffect(float f) {
        float f2 = 14.0F;
        float f3 = 1.0F;
        EntityLivingBase renderViewEntity = this.mc.renderViewEntity;
        EntityLivingBaseFloatEvent1 entityLivingBaseFloatEvent1 = new EntityLivingBaseFloatEvent1(f, renderViewEntity, f2, f3);
        CheatBreaker.getInstance().getEventBus().handleEvent(entityLivingBaseFloatEvent1);
        if (!entityLivingBaseFloatEvent1.isCancelled()) {
            f2 = entityLivingBaseFloatEvent1.getFloatValue2();
            f3 = entityLivingBaseFloatEvent1.getFloatValue3();
            float f4 = renderViewEntity.hurtTime - f;
            if (renderViewEntity.getHealth() <= 0.0F) {
                float f5 = renderViewEntity.deathTime + f;
                GL11.glRotatef((40.0F - 8000.0F / (f5 + 200.0F)) * f3, 0.0F, 0.0F, 1.0F);
            }

            if (f4 >= 0.0F) {
                f4 /= renderViewEntity.maxHurtTime;
                f4 = MathHelper.sin(f4 * f4 * f4 * f4 * (float) Math.PI);
                float attackedAtYaw = renderViewEntity.attackedAtYaw;
                FloatEvent1 floatEvent1 = new FloatEvent1(f4, attackedAtYaw);
                CheatBreaker.getInstance().getEventBus().handleEvent(floatEvent1);
                f4 = floatEvent1.getFloatValue1();
                attackedAtYaw = floatEvent1.getAttackedAtYaw();
                GL11.glRotatef(-attackedAtYaw * f3, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(-f4 * f2 * f3, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(attackedAtYaw * f3, 0.0F, 1.0F, 0.0F);
            }
        }
    }

    public void setupViewBobbing(float f) {
        FreeLookHandler freeLookHandler = CheatBreaker.getInstance().getModManager().freeLookHandler;
        EntityLivingBase renderViewEntity = this.mc.renderViewEntity;
        float f2 = renderViewEntity.yOffset - 1.62F;
        double d = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * f;
        double d2 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * f - f2;
        double d3 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * f;
        GL11.glRotatef(this.prevCamRoll + (this.camRoll - this.prevCamRoll) * f, 0.0F, 0.0F, 1.0F);
        if (renderViewEntity.isPlayerSleeping()) {
            f2 = (float)(f2 + 1.0);
            GL11.glTranslatef(0.0F, 0.3F, 0.0F);
            if (!this.mc.gameSettings.debugCamEnable) {
                Block block = this.mc.theWorld.getBlock(MathHelper.floor_double(renderViewEntity.posX), MathHelper.floor_double(renderViewEntity.posY), MathHelper.floor_double(renderViewEntity.posZ));
                if (block == Blocks.bed) {
                    int blockMetadata = this.mc
                        .theWorld
                        .getBlockMetadata(MathHelper.floor_double(renderViewEntity.posX), MathHelper.floor_double(renderViewEntity.posY), MathHelper.floor_double(renderViewEntity.posZ));
                    int n = blockMetadata & 3;
                    GL11.glRotatef(n * 90, 0.0F, 1.0F, 0.0F);
                }

                GL11.glRotatef(freeLookHandler.prevYaw + (freeLookHandler.yaw - freeLookHandler.prevYaw) * f + 180.0F, 0.0F, -1.0F, 0.0F);
                GL11.glRotatef(freeLookHandler.prevPitch + (freeLookHandler.pitch - freeLookHandler.prevPitch) * f, -1.0F, 0.0F, 0.0F);
            }
        } else if (this.mc.gameSettings.thirdPersonView > 0) {
            double d4 = this.thirdPersonDistanceTemp + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * f;
            if (this.mc.gameSettings.debugCamEnable) {
                float f3 = this.prevDebugCamYaw + (this.debugCamYaw - this.prevDebugCamYaw) * f;
                float f4 = this.prevDebugCamPitch + (this.debugCamPitch - this.prevDebugCamPitch) * f;
                GL11.glTranslatef(0.0F, 0.0F, (float)(-d4));
                GL11.glRotatef(f4, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(f3, 0.0F, 1.0F, 0.0F);
            } else {
                float yaw = freeLookHandler.yaw;
                float pitch = freeLookHandler.pitch;
                if (this.mc.gameSettings.thirdPersonView == 2) {
                    pitch += 180.0F;
                }

                double d5 = -MathHelper.sin(yaw / 180.0F * 1.4545455F * 2.1598449F) * MathHelper.cos(pitch / 180.0F * 15.315265F * 0.20512821F) * d4;
                double d6 = MathHelper.cos(yaw / 180.0F * 1.6923077F * 1.8563957F) * MathHelper.cos(pitch / 180.0F * 0.97590363F * 3.219163F) * d4;
                double d7 = -MathHelper.sin(pitch / 180.0F * 2.8235295F * 1.1126474F) * d4;

                for (int i = 0; i < 8; i++) {
                    float f3 = (i & 1) * 2 - 1;
                    float f4 = (i >> 1 & 1) * 2 - 1;
                    float f5 = (i >> 2 & 1) * 2 - 1;
                    double d8;
                    MovingObjectPosition movingObjectPosition;
                    if ((
                                movingObjectPosition = this.mc
                                    .theWorld
                                    .rayTraceBlocks(
                                        Vec3.createVectorHelper(d + (f3 = f3 * 0.1F), d2 + (f4 = f4 * 0.1F), d3 + (f5 = f5 * 0.1F)),
                                        Vec3.createVectorHelper(d - d5 + f3 + f5, d2 - d7 + f4, d3 - d6 + f5)
                                    )
                            )
                            != null
                        && (d8 = movingObjectPosition.hitVec.distanceTo(Vec3.createVectorHelper(d, d2, d3))) < d4) {
                        d4 = d8;
                    }
                }

                if (this.mc.gameSettings.thirdPersonView == 2) {
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                }

                GL11.glRotatef(freeLookHandler.pitch - pitch, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(freeLookHandler.yaw - yaw, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 0.0F, (float)(-d4));
                GL11.glRotatef(yaw - freeLookHandler.yaw, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(pitch - freeLookHandler.pitch, 1.0F, 0.0F, 0.0F);
            }
        } else {
            GL11.glTranslatef(0.0F, 0.0F, -0.1F);
        }

        if (!this.mc.gameSettings.debugCamEnable) {
            GL11.glRotatef(freeLookHandler.prevPitch + (freeLookHandler.pitch - freeLookHandler.prevPitch) * f, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(freeLookHandler.prevYaw + (freeLookHandler.yaw - freeLookHandler.prevYaw) * f + 180.0F, 0.0F, 1.0F, 0.0F);
        }

        GL11.glTranslatef(0.0F, f2, 0.0F);
        d = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * f;
        d2 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * f - f2;
        d3 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * f;
        this.cloudFog = this.mc.renderGlobal.hasCloudFog(d, d2, d3, f);
    }

    public void orientCamera(float f) {
        EntityLivingBase renderViewEntity = this.mc.renderViewEntity;
        float f2 = renderViewEntity.yOffset - 1.62F;
        double d = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * f;
        double d2 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * f - f2;
        double d3 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * f;
        GL11.glRotatef(this.prevCamRoll + (this.camRoll - this.prevCamRoll) * f, 0.0F, 0.0F, 1.0F);
        if (renderViewEntity.isPlayerSleeping()) {
            f2 = (float)(f2 + 1.0);
            GL11.glTranslatef(0.0F, 0.3F, 0.0F);
            if (!this.mc.gameSettings.debugCamEnable) {
                Block block = this.mc.theWorld.getBlock(MathHelper.floor_double(renderViewEntity.posX), MathHelper.floor_double(renderViewEntity.posY), MathHelper.floor_double(renderViewEntity.posZ));
                if (block == Blocks.bed) {
                    int blockMetadata = this.mc
                        .theWorld
                        .getBlockMetadata(MathHelper.floor_double(renderViewEntity.posX), MathHelper.floor_double(renderViewEntity.posY), MathHelper.floor_double(renderViewEntity.posZ));
                    int n = blockMetadata & 3;
                    GL11.glRotatef(n * 90, 0.0F, 1.0F, 0.0F);
                }

                GL11.glRotatef(renderViewEntity.prevRotationYaw + (renderViewEntity.rotationYaw - renderViewEntity.prevRotationYaw) * f + 180.0F, 0.0F, -1.0F, 0.0F);
                GL11.glRotatef(renderViewEntity.prevRotationPitch + (renderViewEntity.rotationPitch - renderViewEntity.prevRotationPitch) * f, -1.0F, 0.0F, 0.0F);
            }
        } else if (this.mc.gameSettings.thirdPersonView > 0) {
            double d4 = this.thirdPersonDistanceTemp + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * f;
            if (this.mc.gameSettings.debugCamEnable) {
                float f3 = this.prevDebugCamYaw + (this.debugCamYaw - this.prevDebugCamYaw) * f;
                float f4 = this.prevDebugCamPitch + (this.debugCamPitch - this.prevDebugCamPitch) * f;
                GL11.glTranslatef(0.0F, 0.0F, (float)(-d4));
                GL11.glRotatef(f4, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(f3, 0.0F, 1.0F, 0.0F);
            } else {
                float rotationYaw = renderViewEntity.rotationYaw;
                float rotationPitch = renderViewEntity.rotationPitch;
                if (this.mc.gameSettings.thirdPersonView == 2) {
                    rotationPitch += 180.0F;
                }

                double d5 = -MathHelper.sin(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI) * d4;
                double d6 = MathHelper.cos(rotationYaw / 180.0F * (float) Math.PI) * MathHelper.cos(rotationPitch / 180.0F * (float) Math.PI) * d4;
                double d7 = -MathHelper.sin(rotationPitch / 180.0F * (float) Math.PI) * d4;

                for (int i = 0; i < 8; i++) {
                    float f3 = (i & 1) * 2 - 1;
                    float f4 = (i >> 1 & 1) * 2 - 1;
                    float f5 = (i >> 2 & 1) * 2 - 1;
                    f3 *= 0.1F;
                    f4 *= 0.1F;
                    f5 *= 0.1F;
                    MovingObjectPosition rayTraceBlocks = this.mc
                        .theWorld
                        .rayTraceBlocks(
                            Vec3.createVectorHelper(d + f3, d2 + f4, d3 + f5),
                            Vec3.createVectorHelper(d - d5 + f3 + f5, d2 - d7 + f4, d3 - d6 + f5)
                        );
                    if (rayTraceBlocks != null) {
                        double distanceTo = rayTraceBlocks.hitVec.distanceTo(Vec3.createVectorHelper(d, d2, d3));
                        if (distanceTo < d4) {
                            d4 = distanceTo;
                        }
                    }
                }

                if (this.mc.gameSettings.thirdPersonView == 2) {
                    GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
                }

                GL11.glRotatef(renderViewEntity.rotationPitch - rotationPitch, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(renderViewEntity.rotationYaw - rotationYaw, 0.0F, 1.0F, 0.0F);
                GL11.glTranslatef(0.0F, 0.0F, (float)(-d4));
                GL11.glRotatef(rotationYaw - renderViewEntity.rotationYaw, 0.0F, 1.0F, 0.0F);
                GL11.glRotatef(rotationPitch - renderViewEntity.rotationPitch, 1.0F, 0.0F, 0.0F);
            }
        } else {
            GL11.glTranslatef(0.0F, 0.0F, -0.1F);
        }

        if (!this.mc.gameSettings.debugCamEnable) {
            GL11.glRotatef(renderViewEntity.prevRotationPitch + (renderViewEntity.rotationPitch - renderViewEntity.prevRotationPitch) * f, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(renderViewEntity.prevRotationYaw + (renderViewEntity.rotationYaw - renderViewEntity.prevRotationYaw) * f + 180.0F, 0.0F, 1.0F, 0.0F);
        }

        GL11.glTranslatef(0.0F, f2, 0.0F);
        d = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * f;
        d2 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * f - f2;
        d3 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * f;
        this.cloudFog = this.mc.renderGlobal.hasCloudFog(d, d2, d3, f);
    }

    public void renderHand(float f, int n) {
        this.renderHand(f, n, true, true, false);
    }

    public void renderHand(float f, int n, boolean flag, boolean flag2, boolean flag3) {
        if (this.debugViewDirection <= 0) {
            GL11.glMatrixMode(5889);
            GL11.glLoadIdentity();
            float f2 = 0.07F;
            if (this.mc.gameSettings.anaglyph) {
                GL11.glTranslatef(-(n * 2 - 1) * f2, 0.0F, 0.0F);
            }

            if (this.cameraZoom != 1.0) {
                GL11.glTranslatef((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
                GL11.glScaled(this.cameraZoom, this.cameraZoom, 1.0);
            }

            if (Config.isShaders()) {
                Shaders.applyHandDepth();
            }

            Project.gluPerspective(this.getFOVModifier(f, false), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
            if (this.mc.playerController.enableEverythingIsScrewedUpMode()) {
                float f3 = 0.6666667F;
                GL11.glScalef(1.0F, f3, 1.0F);
            }

            GL11.glMatrixMode(5888);
            GL11.glLoadIdentity();
            if (this.mc.gameSettings.anaglyph) {
                GL11.glTranslatef((n * 2 - 1) * 0.1F, 0.0F, 0.0F);
            }

            if (flag) {
                GL11.glPushMatrix();
                this.hurtCameraEffect(f);
                if (this.mc.gameSettings.viewBobbing) {
                    this.render(f, "hand");
                }

                boolean flag4 = !ReflectorForge.renderFirstPersonHand(this.mc.renderGlobal, f, n);
                if (flag4
                    && this.mc.gameSettings.thirdPersonView == 0
                    && !this.mc.renderViewEntity.isPlayerSleeping()
                    && !this.mc.gameSettings.hideGUI
                    && !this.mc.playerController.enableEverythingIsScrewedUpMode()) {
                    this.enableLightmap(f);
                    if (Config.isShaders()) {
                        ShadersRender.renderItemFP(this.itemRenderer, f, flag3);
                    } else {
                        this.itemRenderer.renderItemInFirstPerson(f);
                    }

                    this.disableLightmap(f);
                }

                GL11.glPopMatrix();
            }

            if (!flag2) {
                return;
            }

            this.disableLightmap(f);
            if (this.mc.gameSettings.thirdPersonView == 0 && !this.mc.renderViewEntity.isPlayerSleeping()) {
                this.itemRenderer.renderOverlays(f);
                this.hurtCameraEffect(f);
            }

            if (this.mc.gameSettings.viewBobbing) {
                this.render(f, "hand");
            }
        }
    }

    public void disableLightmap(double d) {
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glDisable(3553);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        if (Config.isShaders()) {
            Shaders.disableLightmap();
        }
    }

    public void enableLightmap(double d) {
        OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        GL11.glMatrixMode(5890);
        GL11.glLoadIdentity();
        float f = 0.00390625F;
        GL11.glScalef(f, f, f);
        GL11.glTranslatef(8.0F, 8.0F, 8.0F);
        GL11.glMatrixMode(5888);
        this.mc.getTextureManager().bindTexture(this.locationLightMap);
        GL11.glTexParameteri(3553, 10241, 9729);
        GL11.glTexParameteri(3553, 10240, 9729);
        GL11.glTexParameteri(3553, 10242, 10496);
        GL11.glTexParameteri(3553, 10243, 10496);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(3553);
        OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
        if (Config.isShaders()) {
            Shaders.enableLightmap();
        }
    }

    public void updateTorchFlicker() {
        this.torchFlickerDX = (float)(this.torchFlickerDX + (Math.random() - Math.random()) * Math.random() * Math.random());
        this.torchFlickerDY = (float)(this.torchFlickerDY + (Math.random() - Math.random()) * Math.random() * Math.random());
        this.torchFlickerDX = (float)(this.torchFlickerDX * 0.9);
        this.torchFlickerDY = (float)(this.torchFlickerDY * 0.9);
        this.torchFlickerX = this.torchFlickerX + (this.torchFlickerDX - this.torchFlickerX) * 1.0F;
        this.torchFlickerY = this.torchFlickerY + (this.torchFlickerDY - this.torchFlickerY) * 1.0F;
        this.lightmapUpdateNeeded = true;
    }

    public void updateLightmap(float f) {
        WorldClient theWorld = this.mc.theWorld;
        if (theWorld != null) {
            if (CustomColorizer.updateLightmap(theWorld, this.torchFlickerX, this.lightmapColors, this.mc.thePlayer.isPotionActive(Potion.nightVision))) {
                this.lightmapTexture.updateDynamicTexture();
                this.lightmapUpdateNeeded = false;
                return;
            }

            for (int i = 0; i < this.applyFullbrightLightmap(256); i++) {
                float f2 = theWorld.getSunBrightness(1.0F) * 0.95F + 0.05F;
                float f3 = theWorld.provider.lightBrightnessTable[i / 16] * f2;
                float f4 = theWorld.provider.lightBrightnessTable[i % 16] * (this.torchFlickerX * 0.1F + 1.5F);
                if (theWorld.lastLightningBolt > 0) {
                    f3 = theWorld.provider.lightBrightnessTable[i / 16];
                }

                float f5 = f3 * (theWorld.getSunBrightness(1.0F) * 0.65F + 0.35F);
                float f6 = f3 * (theWorld.getSunBrightness(1.0F) * 0.65F + 0.35F);
                float f7 = f4 * ((f4 * 0.6F + 0.4F) * 0.6F + 0.4F);
                float f8 = f4 * (f4 * f4 * 0.6F + 0.4F);
                float f9 = f5 + f4;
                float f10 = f6 + f7;
                float f11 = f3 + f8;
                f9 = f9 * 0.96F + 0.03F;
                f10 = f10 * 0.96F + 0.03F;
                f11 = f11 * 0.96F + 0.03F;
                if (this.bossColorModifier > 0.0F) {
                    float f12 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * f;
                    f9 = f9 * (1.0F - f12) + f9 * 0.7F * f12;
                    f10 = f10 * (1.0F - f12) + f10 * 0.6F * f12;
                    f11 = f11 * (1.0F - f12) + f11 * 0.6F * f12;
                }

                if (theWorld.provider.dimensionId == 1) {
                    f9 = 0.22F + f4 * 0.75F;
                    f10 = 0.28F + f7 * 0.75F;
                    f11 = 0.25F + f8 * 0.75F;
                }

                if (this.mc.thePlayer.isPotionActive(Potion.nightVision)) {
                    float nightVisionBrightness = this.getNightVisionBrightness(this.mc.thePlayer, f);
                    float f12 = 1.0F / f9;
                    if (f12 > 1.0F / f10) {
                        f12 = 1.0F / f10;
                    }

                    if (f12 > 1.0F / f11) {
                        f12 = 1.0F / f11;
                    }

                    f9 = f9 * (1.0F - nightVisionBrightness) + f9 * f12 * nightVisionBrightness;
                    f10 = f10 * (1.0F - nightVisionBrightness) + f10 * f12 * nightVisionBrightness;
                    f11 = f11 * (1.0F - nightVisionBrightness) + f11 * f12 * nightVisionBrightness;
                }

                if (f9 > 1.0F) {
                    f9 = 1.0F;
                }

                if (f10 > 1.0F) {
                    f10 = 1.0F;
                }

                if (f11 > 1.0F) {
                    f11 = 1.0F;
                }

                float gammaSetting = this.mc.gameSettings.getGammaSetting();
                float f12 = 1.0F - f9;
                float f13 = 1.0F - f10;
                float f14 = 1.0F - f11;
                f12 = 1.0F - f12 * f12 * f12 * f12;
                f13 = 1.0F - f13 * f13 * f13 * f13;
                f14 = 1.0F - f14 * f14 * f14 * f14;
                f9 = f9 * (1.0F - gammaSetting) + f12 * gammaSetting;
                f10 = f10 * (1.0F - gammaSetting) + f13 * gammaSetting;
                f11 = f11 * (1.0F - gammaSetting) + f14 * gammaSetting;
                f9 = f9 * 0.96F + 0.03F;
                f10 = f10 * 0.96F + 0.03F;
                f11 = f11 * 0.96F + 0.03F;
                if (f9 > 1.0F) {
                    f9 = 1.0F;
                }

                if (f10 > 1.0F) {
                    f10 = 1.0F;
                }

                if (f11 > 1.0F) {
                    f11 = 1.0F;
                }

                if (f9 < 0.0F) {
                    f9 = 0.0F;
                }

                if (f10 < 0.0F) {
                    f10 = 0.0F;
                }

                if (f11 < 0.0F) {
                    f11 = 0.0F;
                }

                short s = 255;
                int n = (int)(f9 * 255.0F);
                int n2 = (int)(f10 * 255.0F);
                int n3 = (int)(f11 * 255.0F);
                this.lightmapColors[i] = s << 24 | n << 16 | n2 << 8 | n3;
            }

            this.lightmapTexture.updateDynamicTexture();
            this.lightmapUpdateNeeded = false;
        }
    }

    public float getNightVisionBrightness(EntityPlayer entityPlayer, float f) {
        int duration = entityPlayer.getActivePotionEffect(Potion.nightVision).getDuration();
        return duration > 200 ? 1.0F : 0.7F + MathHelper.sin((duration - f) * (float) Math.PI * 0.2F) * 0.3F;
    }

    public void updateCameraAndRender(float f) throws IOException {
        this.mc.mcProfiler.startSection("lightTex");
        if (!this.initialized) {
            TextureUtils.registerResourceListener();
            RenderPlayerOF.register();
            ItemRendererOF itemRendererOF = new ItemRendererOF(this.mc);
            this.itemRenderer = itemRendererOF;
            RenderManager.instance.itemRenderer = itemRendererOF;
            if (Config.getBitsOs() == 64 && Config.getBitsJre() == 32) {
                Config.setNotify64BitJava(true);
            }

            this.initialized = true;
        }

        Config.checkDisplayMode();
        WorldClient theWorld = this.mc.theWorld;
        if (theWorld != null) {
            if (Config.getNewRelease() != null) {
                String replace = "HD_U".replace("HD_U", "HD Ultra").replace("L", "Light");
                String str = replace + " " + Config.getNewRelease();
                ChatComponentText chatComponentText = new ChatComponentText(I18n.format("of.message.newVersion", str));
                this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText);
                Config.setNewRelease((String)null);
            }

            if (Config.isNotify64BitJava()) {
                Config.setNotify64BitJava(false);
                ChatComponentText chatComponentText = new ChatComponentText(I18n.format("of.message.java64Bit"));
                this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText);
            }
        }

        if (this.updatedWorld != theWorld) {
            RandomMobs.worldChanged(this.updatedWorld, theWorld);
            Config.updateThreadPriorities();
            this.lastServerTime = 0L;
            this.lastServerTicks = 0;
            this.updatedWorld = theWorld;
        }

        if (ShaderLinkHelper.getStaticShaderLinkHelper() != null && !this.setFxaaShader(Shaders.configAntialiasingLevel)) {
            Shaders.configAntialiasingLevel = 0;
        }

        RenderBlocks.fancyGrass = Config.isGrassFancy() || Config.isBetterGrassFancy();
        Blocks.leaves.setGraphicsLevel(Config.isTreesFancy());
        if (this.lightmapUpdateNeeded) {
            this.updateLightmap(f);
        }

        this.mc.mcProfiler.endSection();
        boolean isActive = Display.isActive();
        if (!isActive && this.mc.gameSettings.pauseOnLostFocus && (!this.mc.gameSettings.touchscreen || !Mouse.isButtonDown(1))) {
            if (Minecraft.getSystemTime() - this.prevFrameTime > 500L) {
                this.mc.displayInGameMenu();
            }
        } else {
            this.prevFrameTime = Minecraft.getSystemTime();
        }

        this.mc.mcProfiler.startSection("mouse");
        if (this.mc.inGameHasFocus && isActive) {
            this.mc.mouseHelper.mouseXYChange();
            double floatValue = CheatBreaker.getInstance().getKeybindSettings().cm360Value.getFloatValue();
            double d = CheatBreaker.getInstance().getKeybindSettings().useCm360Calculation.getBooleanValue()
                ? MouseSensitivityConverter.distanceToSensitivity(floatValue)
                : this.mc.gameSettings.mouseSensitivity;
            float f2 = (float)d * 0.6F + 0.2F;
            float f3 = f2 * f2 * f2 * 8.0F;
            float f4 = this.mc.mouseHelper.deltaX * f3;
            float f5 = this.mc.mouseHelper.deltaY * f3;
            byte b = 1;
            if (this.mc.gameSettings.invertMouse) {
                b = -1;
            }

            FreeLookHandler freeLookHandler = CheatBreaker.getInstance().getModManager().freeLookHandler;
            if (!freeLookHandler.active) {
                if (this.mc.gameSettings.smoothCamera) {
                    this.smoothCamYaw += f4;
                    this.smoothCamPitch += f5;
                    float f6 = f - this.smoothCamPartialTicks;
                    this.smoothCamPartialTicks = f;
                    f4 = this.smoothCamFilterX * f6;
                    f5 = this.smoothCamFilterY * f6;
                    this.mc.thePlayer.setAngles(f4, f5 * b);
                } else {
                    this.mc.thePlayer.setAngles(f4, f5 * b);
                }
            } else if (this.mc.gameSettings.smoothCamera) {
                this.smoothCamYaw += f4;
                this.smoothCamPitch += f5;
                float f6 = f - this.smoothCamPartialTicks;
                this.smoothCamPartialTicks = f;
                f4 = this.smoothCamFilterX * f6;
                f5 = this.smoothCamFilterY * f6;
                freeLookHandler.onMouseMoved(f4, f5 * b);
            } else {
                freeLookHandler.onMouseMoved(f4, f5 * b);
            }
        }

        this.mc.mcProfiler.endSection();
        if (!this.mc.skipRenderWorld) {
            anaglyphEnable = this.mc.gameSettings.anaglyph;
            ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
            int scaledWidth = scaledResolution.getScaledWidth();
            int scaledHeight = scaledResolution.getScaledHeight();
            int n = Mouse.getX() * scaledWidth / this.mc.displayWidth;
            int n2 = scaledHeight - Mouse.getY() * scaledHeight / this.mc.displayHeight - 1;
            int limitFramerate = this.mc.gameSettings.limitFramerate;
            if (this.mc.theWorld != null) {
                this.mc.mcProfiler.startSection("level");
                if (this.mc.isFramerateLimitBelowMax()) {
                    this.renderWorld(f, this.renderEndNanoTime + 1000000000 / limitFramerate);
                } else {
                    this.renderWorld(f, 0L);
                }

                if (OpenGlHelper.shadersSupported) {
                    if (this.theShaderGroup != null) {
                        GL11.glMatrixMode(5890);
                        GL11.glPushMatrix();
                        GL11.glLoadIdentity();
                        this.theShaderGroup.loadShaderGroup(f);
                        GL11.glPopMatrix();
                    }

                    if (this.shaderGroup != null) {
                        GL11.glMatrixMode(5890);
                        GL11.glPushMatrix();
                        GL11.glLoadIdentity();
                        this.shaderGroup.loadShaderGroup(f);
                        GL11.glPopMatrix();
                    }

                    this.mc.getFramebuffer().bindFramebuffer(true);
                }

                this.renderEndNanoTime = System.nanoTime();
                this.mc.mcProfiler.endStartSection("gui");
                if (!this.mc.gameSettings.hideGUI || this.mc.currentScreen != null) {
                    GL11.glAlphaFunc(516, 0.1F);
                    boolean fancyGraphics = this.mc.gameSettings.fancyGraphics;
                    if (!Config.isVignetteEnabled()) {
                        this.mc.gameSettings.fancyGraphics = false;
                    }

                    this.mc.ingameGUI.renderGameOverlay(f, this.mc.currentScreen != null, n, n2);
                    this.mc.gameSettings.fancyGraphics = fancyGraphics;
                    if (this.mc.gameSettings.ofShowFps && !this.mc.gameSettings.showDebugInfo) {
                        Config.drawFps();
                    }
                }

                this.mc.mcProfiler.endSection();
            } else {
                GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
                GL11.glMatrixMode(5889);
                GL11.glLoadIdentity();
                GL11.glMatrixMode(5888);
                GL11.glLoadIdentity();
                this.setupOverlayRendering();
                this.renderEndNanoTime = System.nanoTime();
            }

            if (this.mc.currentScreen != null) {
                GL11.glClear(256);

                try {
                    boolean flag = false;
                    if (Reflector.EventBus_post.exists()) {
                        flag = Reflector.postForgeBusEvent(Reflector.DrawScreenEvent_Pre_Constructor, this.mc.currentScreen, n, n2, f);
                    }

                    if (!flag) {
                        this.mc.currentScreen.drawScreen(n, n2, f);
                    }

                    Reflector.postForgeBusEvent(Reflector.DrawScreenEvent_Post_Constructor, this.mc.currentScreen, n, n2, f);
                    FriendsScreen.getFriendsScreen().renderNotifications();
                } catch (Throwable e) {
                    CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Rendering screen");
                    CrashReportCategory makeCategory = makeCrashReport.makeCategory("Screen render details");
                    makeCategory.addCrashSectionCallable("Screen name", () -> this.mc.currentScreen.getClass().getCanonicalName());
                    makeCategory.addCrashSectionCallable(
                        "Mouse location", () -> String.format("Scaled: (%d, %d). Absolute: (%d, %d)", n, n2, Mouse.getX(), Mouse.getY())
                    );
                    makeCategory.addCrashSectionCallable(
                        "Screen size",
                        () -> String.format(
                            "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d",
                            scaledResolution.getScaledWidth(),
                            scaledResolution.getScaledHeight(),
                            this.mc.displayWidth,
                            this.mc.displayHeight,
                            scaledResolution.getScaleFactor()
                        )
                    );
                    throw new ReportedException(makeCrashReport);
                }
            }
        }

        this.frameFinish();
        this.waitForServerThread();
        if (this.mc.gameSettings.showDebugInfo != this.lastShowDebugInfo) {
            this.showExtendedDebugInfo = this.mc.gameSettings.showDebugProfilerChart;
            this.lastShowDebugInfo = this.mc.gameSettings.showDebugInfo;
        }

        if (this.mc.gameSettings.showDebugInfo) {
            this.showLagometer(this.mc.mcProfiler.timeTickNano, this.mc.mcProfiler.timeUpdateChunksNano);
        }

        if (this.mc.gameSettings.ofProfiler) {
            this.mc.gameSettings.showDebugProfilerChart = true;
        }
    }

    public void renderStreamIndicator(float f) {
        this.setupOverlayRendering();
    }

    public void renderWorld(float f, long l) {
        boolean isShaders = Config.isShaders();
        if (isShaders) {
            Shaders.beginRender(this.mc, f, l);
        }

        float value = (Float)CheatBreaker.getInstance().getKeybindSettings().slowChunkLoading.getValue();
        if (value == 0.0F || !(Boolean)CheatBreaker.getInstance().getKeybindSettings().enableFpsBoost.getValue()) {
            this.floatValue = 0.0F;
        }

        float f2 = f;
        this.mc.mcProfiler.startSection("lightTex");
        if (this.lightmapUpdateNeeded) {
            this.updateLightmap(f);
        }

        GL11.glEnable(2884);
        GL11.glEnable(2929);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.1F);
        if (this.mc.renderViewEntity == null) {
            this.mc.renderViewEntity = this.mc.thePlayer;
        }

        boolean active = CheatBreaker.getInstance().getModManager().freeLookHandler.active;
        this.mc.mcProfiler.endStartSection("pick");
        this.getMouseOver(f);
        EntityLivingBase renderViewEntity = this.mc.renderViewEntity;
        RenderGlobal renderGlobal = this.mc.renderGlobal;
        EffectRenderer effectRenderer = this.mc.effectRenderer;
        double d = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * f;
        double d2 = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * f;
        double d3 = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * f;
        this.mc.mcProfiler.endStartSection("center");

        for (int i = 0; i < 2; i++) {
            if (this.mc.gameSettings.anaglyph) {
                anaglyphField = i;
                if (anaglyphField == 0) {
                    GL11.glColorMask(false, true, true, false);
                } else {
                    GL11.glColorMask(true, false, false, false);
                }
            }

            if (isShaders) {
                Shaders.beginRenderPass(2, f, l);
            }

            this.mc.mcProfiler.endStartSection("clear");
            if (isShaders) {
                Shaders.setViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
            } else {
                GL11.glViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
            }

            this.updateFogColor(f);
            GL11.glClear(16640);
            if (isShaders) {
                Shaders.clearRenderBuffer();
            }

            GL11.glEnable(2884);
            this.mc.mcProfiler.endStartSection("camera");
            this.render(f, i, active);
            if (isShaders) {
                Shaders.setCamera(f);
            }

            ActiveRenderInfo.updateRenderInfo(this.mc.thePlayer, this.mc.gameSettings.thirdPersonView == 2);
            this.mc.mcProfiler.endStartSection("frustrum");
            ClippingHelperImpl.getInstance();
            if ((Config.isSkyEnabled() || Config.isSunMoonEnabled() || Config.isStarsEnabled()) && !Shaders.isShadowPass) {
                this.setupFog(-1, f);
                this.mc.mcProfiler.endStartSection("sky");
                if (isShaders) {
                    Shaders.beginSky();
                }

                renderGlobal.renderSky(f);
                if (isShaders) {
                    Shaders.endSky();
                }
            } else {
                GL11.glDisable(3042);
            }

            GL11.glEnable(2912);
            this.setupFog(1, f);
            if (this.mc.gameSettings.ambientOcclusion != 0) {
                GL11.glShadeModel(7425);
            }

            this.mc.mcProfiler.endStartSection("culling");
            Frustrum frustrum = new Frustrum();
            if (isShaders) {
                ShadersRender.setFrustrumPosition(frustrum, d, d2, d3);
            }

            frustrum.setPosition(d, d2, d3);
            this.mc.renderGlobal.clipRenderersByFrustum(frustrum, f);
            if (i == 0) {
                if (Config.isShaders()) {
                    Shaders.beginUpdateChunks();
                }

                this.frameCount++;
                this.mc.mcProfiler.endStartSection("updatechunks");
                if (this.floatValue == 0.0F) {
                    while (!this.mc.renderGlobal.updateRenderers(renderViewEntity, false) && l != 0L) {
                        long l2 = l - System.nanoTime();
                        if (l2 < 0L || l2 > 1000000000L) {
                            break;
                        }
                    }
                }

                this.floatValue++;
                if (this.floatValue >= 100.0 / (100.0 - value * 0.995)) {
                    this.floatValue = 0.0F;
                }

                if (Config.isShaders()) {
                    Shaders.endUpdateChunks();
                }
            }

            if (renderViewEntity.posY < 128.0) {
                this.renderCloudsCheck(renderGlobal, f);
            }

            this.mc.mcProfiler.endStartSection("prepareterrain");
            this.setupFog(0, f);
            GL11.glEnable(2912);
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            RenderHelper.disableStandardItemLighting();
            this.mc.mcProfiler.endStartSection("terrain");
            GL11.glMatrixMode(5888);
            GL11.glPushMatrix();
            if (Config.isShaders()) {
                Shaders.beginTerrain();
            }

            renderGlobal.sortAndRender(renderViewEntity, 0, f);
            if (Config.isShaders()) {
                Shaders.endTerrain();
            }

            GL11.glShadeModel(7424);
            GL11.glAlphaFunc(516, 0.1F);
            boolean exists = Reflector.ForgeHooksClient.exists();
            if (this.debugViewDirection == 0) {
                GL11.glMatrixMode(5888);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                RenderHelper.enableStandardItemLighting();
                this.mc.mcProfiler.endStartSection("entities");
                if (exists) {
                    Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, 0);
                }

                renderGlobal.renderEntities(renderViewEntity, frustrum, f);
                if (exists) {
                    Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, -1);
                }

                RenderHelper.disableStandardItemLighting();
                this.disableLightmap(f);
                GL11.glMatrixMode(5888);
                GL11.glPopMatrix();
                GL11.glPushMatrix();
                if (this.mc.objectMouseOver != null && renderViewEntity.isInsideOfMaterial(Material.water) && renderViewEntity instanceof EntityPlayer && !this.mc.gameSettings.hideGUI) {
                    EntityPlayer entityPlayer = (EntityPlayer)renderViewEntity;
                    GL11.glDisable(3008);
                    this.mc.mcProfiler.endStartSection("outline");
                    if (!this.mc.gameSettings.hideGUI) {
                        renderGlobal.drawSelectionBox(entityPlayer, this.mc.objectMouseOver, 0, f);
                    }

                    GL11.glEnable(3008);
                }
            }

            GL11.glMatrixMode(5888);
            GL11.glPopMatrix();
            if (this.cameraZoom == 1.0
                && renderViewEntity instanceof EntityPlayer
                && !this.mc.gameSettings.hideGUI
                && this.mc.objectMouseOver != null
                && !renderViewEntity.isInsideOfMaterial(Material.water)) {
                EntityPlayer entityPlayer = (EntityPlayer)renderViewEntity;
                GL11.glDisable(3008);
                this.mc.mcProfiler.endStartSection("outline");
                if (!this.mc.gameSettings.hideGUI) {
                    renderGlobal.drawSelectionBox(entityPlayer, this.mc.objectMouseOver, 0, f);
                }

                GL11.glEnable(3008);
            }

            this.mc.mcProfiler.endStartSection("destroyProgress");
            GL11.glEnable(3042);
            OpenGlHelper.glBlendFunc(770, 1, 1, 0);
            renderGlobal.drawBlockDamageTexture(Tessellator.instance, renderViewEntity, f);
            GL11.glDisable(3042);
            if (this.debugViewDirection == 0) {
                this.enableLightmap(f);
                this.mc.mcProfiler.endStartSection("litParticles");
                if (isShaders) {
                    Shaders.beginLitParticles();
                }

                effectRenderer.renderLitParticles(renderViewEntity, f);
                RenderHelper.disableStandardItemLighting();
                this.setupFog(0, f);
                this.mc.mcProfiler.endStartSection("particles");
                if (isShaders) {
                    Shaders.beginParticles();
                }

                effectRenderer.renderParticles(renderViewEntity, f);
                if (isShaders) {
                    Shaders.endParticles();
                }

                this.disableLightmap(f);
            }

            GL11.glDepthMask(false);
            GL11.glEnable(2884);
            this.mc.mcProfiler.endStartSection("weather");
            if (isShaders) {
                Shaders.beginWeather();
            }

            this.renderRainSnow(f);
            if (isShaders) {
                Shaders.endWeather();
            }

            GL11.glDepthMask(true);
            if (isShaders) {
                ShadersRender.renderHand0(this, f, 2);
                Shaders.preWater();
            }

            CheatBreaker.getInstance().getEventBus().handleEvent(new FloatEvent6(f));
            GL11.glDisable(3042);
            GL11.glEnable(2884);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glAlphaFunc(516, 0.1F);
            this.setupFog(0, f);
            GL11.glEnable(3042);
            GL11.glDepthMask(false);
            this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
            WrUpdates.resumeBackgroundUpdates();
            if (Config.isWaterFancy()) {
                this.mc.mcProfiler.endStartSection("water");
                if (this.mc.gameSettings.ambientOcclusion != 0) {
                    GL11.glShadeModel(7425);
                }

                GL11.glEnable(3042);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                if (this.mc.gameSettings.anaglyph) {
                    if (anaglyphField == 0) {
                        GL11.glColorMask(false, true, true, true);
                    } else {
                        GL11.glColorMask(true, false, false, true);
                    }

                    if (isShaders) {
                        Shaders.beginWater();
                    }

                    renderGlobal.renderAllSortedRenderers(1, f);
                    if (isShaders) {
                        Shaders.endWater();
                    }
                } else {
                    if (isShaders) {
                        Shaders.beginWater();
                    }

                    renderGlobal.renderAllSortedRenderers(1, f);
                    if (isShaders) {
                        Shaders.endWater();
                    }
                }

                GL11.glDisable(3042);
                GL11.glShadeModel(7424);
            } else {
                this.mc.mcProfiler.endStartSection("water");
                if (isShaders) {
                    Shaders.beginWater();
                }

                renderGlobal.renderAllSortedRenderers(1, f);
                if (isShaders) {
                    Shaders.endWater();
                }
            }

            if (CheatBreaker.getInstance().getModManager().nametags.isEnabled()) {
                ((Buffer)CheatBreaker.getInstance().getModManager().nametags.modelviewBuffer).rewind();
                GL11.glGetFloat(2982, CheatBreaker.getInstance().getModManager().nametags.modelviewBuffer);
                ((Buffer)CheatBreaker.getInstance().getModManager().nametags.projectionBuffer).rewind();
                GL11.glGetFloat(2983, CheatBreaker.getInstance().getModManager().nametags.projectionBuffer);
            }

            if (CheatBreaker.getInstance().getModManager().teamManager.isActive()) {
                ((Buffer)CheatBreaker.getInstance().getModManager().teamManager.modelViewMatrix).rewind();
                GL11.glGetFloat(2982, CheatBreaker.getInstance().getModManager().teamManager.modelViewMatrix);
                ((Buffer)CheatBreaker.getInstance().getModManager().teamManager.projectionMatrix).rewind();
                GL11.glGetFloat(2983, CheatBreaker.getInstance().getModManager().teamManager.projectionMatrix);
            }

            WrUpdates.pauseBackgroundUpdates();
            if (exists && this.debugViewDirection == 0) {
                RenderHelper.enableStandardItemLighting();
                this.mc.mcProfiler.endStartSection("entities");
                Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, 1);
                this.mc.renderGlobal.renderEntities(renderViewEntity, frustrum, f);
                Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, -1);
                RenderHelper.disableStandardItemLighting();
            }

            GL11.glDepthMask(true);
            GL11.glEnable(2884);
            GL11.glDisable(3042);
            GL11.glDisable(2912);
            if (renderViewEntity.posY >= 128.0 + this.mc.gameSettings.ofCloudsHeight * 128.0F) {
                this.mc.mcProfiler.endStartSection("aboveClouds");
                this.renderCloudsCheck(renderGlobal, f);
            }

            if (exists) {
                this.mc.mcProfiler.endStartSection("FRenderLast");
                Reflector.callVoid(Reflector.ForgeHooksClient_dispatchRenderLast, renderGlobal, f);
            }

            CheatBreaker.getInstance().getEventBus().handleEvent(new RenderGlobalFloatEvent(renderGlobal, f));
            this.mc.mcProfiler.endStartSection("hand");
            boolean callBoolean = Reflector.callBoolean(Reflector.ForgeHooksClient_renderFirstPersonHand, this.mc.renderGlobal, f, i);
            if (!callBoolean && this.cameraZoom == 1.0 && !Shaders.isShadowPass) {
                if (isShaders) {
                    ShadersRender.renderHand1(this, f, i);
                    Shaders.renderCompositeFinal();
                }

                GL11.glClear(256);
                if (isShaders) {
                    ShadersRender.renderFPOverlay(this, f2, i);
                } else {
                    this.renderHand(f, i);
                }
            }

            if (isShaders) {
                Shaders.endRender();
            }

            if (!this.mc.gameSettings.anaglyph) {
                this.mc.mcProfiler.endSection();
                return;
            }
        }

        GL11.glColorMask(true, true, true, false);
        this.mc.mcProfiler.endSection();
    }

    public void renderCloudsCheck(RenderGlobal renderGlobal, float f) {
        if (this.mc.gameSettings.shouldRenderClouds() && Shaders.shouldRenderClouds(this.mc.gameSettings)) {
            this.mc.mcProfiler.endStartSection("clouds");
            GL11.glPushMatrix();
            this.setupFog(0, f);
            GL11.glEnable(2912);
            if (Config.isShaders()) {
                Shaders.beginClouds();
            }

            renderGlobal.renderClouds(f);
            if (Config.isShaders()) {
                Shaders.endClouds();
            }

            GL11.glDisable(2912);
            this.setupFog(1, f);
            GL11.glPopMatrix();
        }
    }

    public void addRainParticles() {
        SimpleEvent46 simpleEvent46 = new SimpleEvent46();
        SimpleEvent47 simpleEvent47 = new SimpleEvent47();
        CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent46);
        CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent47);
        if (!simpleEvent46.isCancelled()) {
            float rainStrength = this.mc.theWorld.getRainStrength(1.0F);
            if (!Config.isRainFancy()) {
                rainStrength /= 2.0F;
            }

            if (rainStrength != 0.0F && Config.isRainSplash()) {
                this.random.setSeed(this.rendererUpdateCount * 312987231L);
                EntityLivingBase renderViewEntity = this.mc.renderViewEntity;
                WorldClient theWorld = this.mc.theWorld;
                int n = MathHelper.floor_double(renderViewEntity.posX);
                int n2 = MathHelper.floor_double(renderViewEntity.posY);
                int n3 = MathHelper.floor_double(renderViewEntity.posZ);
                byte b = 10;
                double d = 0.0;
                double d2 = 0.0;
                double d3 = 0.0;
                int n4 = 0;
                int n5 = (int)(100.0F * rainStrength * rainStrength);
                if (this.mc.gameSettings.particleSetting == 1) {
                    n5 >>= 1;
                } else if (this.mc.gameSettings.particleSetting == 2) {
                    n5 = 0;
                }

                for (int i = 0; i < n5; i++) {
                    int n6 = n + this.random.nextInt(b) - this.random.nextInt(b);
                    int n7 = n3 + this.random.nextInt(b) - this.random.nextInt(b);
                    int precipitationHeight = theWorld.getPrecipitationHeight(n6, n7);
                    Block block = theWorld.getBlock(n6, precipitationHeight - 1, n7);
                    BiomeGenBase biomeGenForCoords = theWorld.getBiomeGenForCoords(n6, n7);
                    if (precipitationHeight <= n2 + b && precipitationHeight >= n2 - b && biomeGenForCoords.canSpawnLightningBolt() && biomeGenForCoords.getFloatTemperature(n6, precipitationHeight, n7) >= 0.15F
                        || simpleEvent47.isCancelled()) {
                        float nextFloat = this.random.nextFloat();
                        float nextFloat2 = this.random.nextFloat();
                        if (block.getMaterial() == Material.lava) {
                            this.mc
                                .effectRenderer
                                .addEffect(new EntitySmokeFX(theWorld, n6 + nextFloat, precipitationHeight + 0.1F - block.getBlockBoundsMinY(), n7 + nextFloat2, 0.0, 0.0, 0.0));
                        } else if (block.getMaterial() != Material.air) {
                            if (this.random.nextInt(++n4) == 0) {
                                d = n6 + nextFloat;
                                d2 = precipitationHeight + 0.1F - block.getBlockBoundsMinY();
                                d3 = n7 + nextFloat2;
                            }

                            EntityRainFX entityRainFX = new EntityRainFX(theWorld, n6 + nextFloat, precipitationHeight + 0.1F - block.getBlockBoundsMinY(), n7 + nextFloat2);
                            CustomColorizer.updateWaterFX(entityRainFX, theWorld);
                            this.mc.effectRenderer.addEffect(entityRainFX);
                        }
                    }
                }

                if (n4 > 0 && this.random.nextInt(3) < this.rainSoundCounter++) {
                    this.rainSoundCounter = 0;
                    if (d2 > renderViewEntity.posY + 1.0
                        && theWorld.getPrecipitationHeight(MathHelper.floor_double(renderViewEntity.posX), MathHelper.floor_double(renderViewEntity.posZ)) > MathHelper.floor_double(renderViewEntity.posY)) {
                        this.mc.theWorld.playSound(d, d2, d3, "ambient.weather.rain", 0.1F, 0.5F, false);
                    } else {
                        this.mc.theWorld.playSound(d, d2, d3, "ambient.weather.rain", 0.2F, 1.0F, false);
                    }
                }
            }
        }
    }

    public void renderRainSnow(float f) {
        EnvironmentChangerMod environmentChangerMod = CheatBreaker.getInstance().getModManager().environmentChangerMod;
        if (Reflector.ForgeWorldProvider_getWeatherRenderer.exists()) {
            WorldProvider provider = this.mc.theWorld.provider;
            Object obj = Reflector.call(provider, Reflector.ForgeWorldProvider_getWeatherRenderer);
            if (obj != null) {
                Reflector.callVoid(obj, Reflector.IRenderHandler_render, f, this.mc.theWorld, this.mc);
                return;
            }
        }

        float rainStrength = this.mc.theWorld.getRainStrength(f);
        if (environmentChangerMod.isEnabled() && environmentChangerMod.customWeather.getBooleanValue() && !environmentChangerMod.currentWeather.getStringValue().equalsIgnoreCase("Clear")) {
            rainStrength = 1.0F;
        }

        if (rainStrength > 0.0F) {
            this.enableLightmap(f);
            if (this.rainXCoords == null) {
                this.rainXCoords = new float[1024];
                this.rainYCoords = new float[1024];

                for (int i = 0; i < 32; i++) {
                    for (int j = 0; j < 32; j++) {
                        float f2 = j - 16;
                        float f3 = i - 16;
                        float f4 = MathHelper.sqrt_float(f2 * f2 + f3 * f3);
                        this.rainXCoords[i << 5 | j] = -f3 / f4;
                        this.rainYCoords[i << 5 | j] = f2 / f4;
                    }
                }
            }

            if (Config.isRainOff()) {
                return;
            }

            EntityLivingBase renderViewEntity = this.mc.renderViewEntity;
            WorldClient theWorld = this.mc.theWorld;
            int n = MathHelper.floor_double(renderViewEntity.posX);
            int n2 = MathHelper.floor_double(renderViewEntity.posY);
            int n3 = MathHelper.floor_double(renderViewEntity.posZ);
            Tessellator instance = Tessellator.instance;
            GL11.glDisable(2884);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glEnable(3042);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glAlphaFunc(516, 0.1F);
            double d = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * f;
            double d2 = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * f;
            double d3 = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * f;
            int n4 = MathHelper.floor_double(d2);
            byte b = 5;
            if (Config.isRainFancy()) {
                b = 10;
            }

            byte b2 = -1;
            float f2 = this.rendererUpdateCount + f;
            if (Config.isRainFancy()) {
                b = 10;
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

            for (int i = n3 - b; i <= n3 + b; i++) {
                for (int j = n - b; j <= n + b; j++) {
                    int n5 = (i - n3 + 16) * 32 + j - n + 16;
                    float f3 = this.rainXCoords[n5] * 0.5F;
                    float f4 = this.rainYCoords[n5] * 0.5F;
                    BiomeGenBase biomeGenForCoords = theWorld.getBiomeGenForCoords(j, i);
                    if (biomeGenForCoords.canSpawnLightningBolt() || biomeGenForCoords.getEnableSnow()) {
                        int precipitationHeight = theWorld.getPrecipitationHeight(j, i);
                        int n6 = n2 - b;
                        int n7 = n2 + b;
                        if (n6 < precipitationHeight) {
                            RenderRainSnowEvent renderRainSnowEvent = new RenderRainSnowEvent();
                            CheatBreaker.getInstance().getEventBus().handleEvent(renderRainSnowEvent);
                            n6 = renderRainSnowEvent.isCancelled() ? 0 : precipitationHeight;
                        }

                        if (n7 < precipitationHeight) {
                            n7 = precipitationHeight;
                        }

                        float f5 = 1.0F;
                        int n8 = precipitationHeight;
                        if (precipitationHeight < n4) {
                            n8 = n4;
                        }

                        if (environmentChangerMod.isEnabled() && environmentChangerMod.customWeather.getBooleanValue() && environmentChangerMod.currentWeather.getStringValue().equals("Clear")) {
                            return;
                        }

                        if (n6 != n7 || environmentChangerMod.isEnabled() && environmentChangerMod.customWeather.getBooleanValue()) {
                            this.random.setSeed(j * j * 3121 + j * 45238971 ^ i * i * 418711 + i * 13761);
                            float floatTemperature = biomeGenForCoords.getFloatTemperature(j, n6, i);
                            boolean flag;
                            if (environmentChangerMod.customWeather.getBooleanValue() && environmentChangerMod.isEnabled() && !environmentChangerMod.currentWeather.getStringValue().equals("Clear")) {
                                flag = environmentChangerMod.currentWeather.getStringValue().equals("Rain");
                            } else {
                                flag = theWorld.getWorldChunkManager().getTemperatureAtHeight(floatTemperature, precipitationHeight) >= 0.15F;
                            }

                            if (flag) {
                                if (b2 != 0) {
                                    if (b2 >= 0) {
                                        instance.draw();
                                    }

                                    b2 = 0;
                                    this.mc.getTextureManager().bindTexture(locationRainPng);
                                    instance.startDrawingQuads();
                                }

                                float f6 = (
                                        (this.rendererUpdateCount + j * j * 3121 + j * 45238971 + i * i * 418711 + i * 13761 & 31) + f
                                    )
                                    / 32.0F
                                    * (3.0F + this.random.nextFloat());
                                double d4 = j + 0.5F - renderViewEntity.posX;
                                double d5 = i + 0.5F - renderViewEntity.posZ;
                                float f7 = MathHelper.sqrt_double(d4 * d4 + d5 * d5) / b;
                                float f8 = 1.0F;
                                instance.setBrightness(theWorld.getLightBrightnessForSkyBlocks(j, n8, i, 0));
                                instance.setColorRGBA_F(f8, f8, f8, ((1.0F - f7 * f7) * 0.5F + 0.5F) * rainStrength);
                                instance.setTranslation(-d * 1.0, -d2 * 1.0, -d3 * 1.0);
                                instance.addVertexWithUV(j - f3 + 0.5, n6, i - f4 + 0.5, 0.0F * f5, n6 * f5 / 4.0F + f6 * f5);
                                instance.addVertexWithUV(j + f3 + 0.5, n6, i + f4 + 0.5, 1.0F * f5, n6 * f5 / 4.0F + f6 * f5);
                                instance.addVertexWithUV(j + f3 + 0.5, n7, i + f4 + 0.5, 1.0F * f5, n7 * f5 / 4.0F + f6 * f5);
                                instance.addVertexWithUV(j - f3 + 0.5, n7, i - f4 + 0.5, 0.0F * f5, n7 * f5 / 4.0F + f6 * f5);
                                instance.setTranslation(0.0, 0.0, 0.0);
                            } else if (theWorld.getWorldChunkManager().getTemperatureAtHeight(floatTemperature, precipitationHeight) < 0.15F
                                || environmentChangerMod.currentWeather.getStringValue().equals("Snow") && environmentChangerMod.customWeather.getBooleanValue() && environmentChangerMod.isEnabled()) {
                                if (b2 != 1) {
                                    if (b2 >= 0) {
                                        instance.draw();
                                    }

                                    b2 = 1;
                                    this.mc.getTextureManager().bindTexture(locationSnowPng);
                                    instance.startDrawingQuads();
                                }

                                float f6 = ((this.rendererUpdateCount & 511) + f) / 512.0F;
                                float f7 = this.random.nextFloat() + f2 * 0.01F * (float)this.random.nextGaussian();
                                float f8 = this.random.nextFloat() + f2 * (float)this.random.nextGaussian() * 0.001F;
                                double d4 = j + 0.5F - renderViewEntity.posX;
                                double d5 = i + 0.5F - renderViewEntity.posZ;
                                float f9 = MathHelper.sqrt_double(d4 * d4 + d5 * d5) / b;
                                float f10 = 1.0F;
                                instance.setBrightness((theWorld.getLightBrightnessForSkyBlocks(j, n8, i, 0) * 3 + 15728880) / 4);
                                instance.setColorRGBA_F(f10, f10, f10, ((1.0F - f9 * f9) * 0.3F + 0.5F) * rainStrength);
                                instance.setTranslation(-d * 1.0, -d2 * 1.0, -d3 * 1.0);
                                instance.addVertexWithUV(
                                    j - f3 + 0.5, n6, i - f4 + 0.5, 0.0F * f5 + f7, n6 * f5 / 4.0F + f6 * f5 + f8
                                );
                                instance.addVertexWithUV(
                                    j + f3 + 0.5, n6, i + f4 + 0.5, 1.0F * f5 + f7, n6 * f5 / 4.0F + f6 * f5 + f8
                                );
                                instance.addVertexWithUV(
                                    j + f3 + 0.5, n7, i + f4 + 0.5, 1.0F * f5 + f7, n7 * f5 / 4.0F + f6 * f5 + f8
                                );
                                instance.addVertexWithUV(
                                    j - f3 + 0.5, n7, i - f4 + 0.5, 0.0F * f5 + f7, n7 * f5 / 4.0F + f6 * f5 + f8
                                );
                                instance.setTranslation(0.0, 0.0, 0.0);
                            }
                        }
                    }
                }
            }

            if (b2 >= 0) {
                instance.draw();
            }

            GL11.glEnable(2884);
            GL11.glDisable(3042);
            GL11.glAlphaFunc(516, 0.1F);
            this.disableLightmap(f);
        }
    }

    public void setupOverlayRendering() {
        ScaledResolution scaledResolution = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double(), 0.0, 1000.0, 3000.0);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
    }

    public void updateFogColor(float f) {
        WorldClient theWorld = this.mc.theWorld;
        EntityLivingBase renderViewEntity = this.mc.renderViewEntity;
        float f2 = 0.25F + 0.75F * this.mc.gameSettings.renderDistanceChunks / 16.0F;
        f2 = 1.0F - (float)Math.pow(f2, 0.25);
        Vec3 skyColor = theWorld.getSkyColor(this.mc.renderViewEntity, f);
        skyColor = CustomColorizer.getWorldSkyColor(skyColor, theWorld, this.mc.renderViewEntity, f);
        float xCoord = (float)skyColor.xCoord;
        float yCoord = (float)skyColor.yCoord;
        float zCoord = (float)skyColor.zCoord;
        Vec3 fogColor = theWorld.getFogColor(f);
        fogColor = CustomColorizer.getWorldFogColor(fogColor, theWorld, f);
        this.fogColorRed = (float)fogColor.xCoord;
        this.fogColorGreen = (float)fogColor.yCoord;
        this.fogColorBlue = (float)fogColor.zCoord;
        if (this.mc.gameSettings.renderDistanceChunks >= 4) {
            Vec3 vec3 = MathHelper.sin(theWorld.getCelestialAngleRadians(f)) > 0.0F
                ? Vec3.createVectorHelper(-1.0, 0.0, 0.0)
                : Vec3.createVectorHelper(1.0, 0.0, 0.0);
            float dotProduct = (float)renderViewEntity.getLook(f).dotProduct(vec3);
            if (dotProduct < 0.0F) {
                dotProduct = 0.0F;
            }

            if (dotProduct > 0.0F) {
                float[] calcSunriseSunsetColors = theWorld.provider.calcSunriseSunsetColors(theWorld.getCelestialAngle(f), f);
                if (calcSunriseSunsetColors != null) {
                    dotProduct *= calcSunriseSunsetColors[3];
                    this.fogColorRed = this.fogColorRed * (1.0F - dotProduct) + calcSunriseSunsetColors[0] * dotProduct;
                    this.fogColorGreen = this.fogColorGreen * (1.0F - dotProduct) + calcSunriseSunsetColors[1] * dotProduct;
                    this.fogColorBlue = this.fogColorBlue * (1.0F - dotProduct) + calcSunriseSunsetColors[2] * dotProduct;
                }
            }
        }

        this.fogColorRed = this.fogColorRed + (xCoord - this.fogColorRed) * f2;
        this.fogColorGreen = this.fogColorGreen + (yCoord - this.fogColorGreen) * f2;
        this.fogColorBlue = this.fogColorBlue + (zCoord - this.fogColorBlue) * f2;
        float rainStrength = theWorld.getRainStrength(f);
        if (rainStrength > 0.0F) {
            float f3 = 1.0F - rainStrength * 0.5F;
            float f4 = 1.0F - rainStrength * 0.4F;
            this.fogColorRed *= f3;
            this.fogColorGreen *= f3;
            this.fogColorBlue *= f4;
        }

        float weightedThunderStrength = theWorld.getWeightedThunderStrength(f);
        if (weightedThunderStrength > 0.0F) {
            float f3 = 1.0F - weightedThunderStrength * 0.5F;
            this.fogColorRed *= f3;
            this.fogColorGreen *= f3;
            this.fogColorBlue *= f3;
        }

        Block blockAtEntityViewpoint = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, renderViewEntity, f);
        if (this.cloudFog) {
            Vec3 cloudColour = theWorld.getCloudColour(f);
            this.fogColorRed = (float)cloudColour.xCoord;
            this.fogColorGreen = (float)cloudColour.yCoord;
            this.fogColorBlue = (float)cloudColour.zCoord;
        } else if (blockAtEntityViewpoint.getMaterial() == Material.water) {
            float f3 = EnchantmentHelper.getRespiration(renderViewEntity) * 0.2F;
            this.fogColorRed = 0.02F + f3;
            this.fogColorGreen = 0.02F + f3;
            this.fogColorBlue = 0.2F + f3;
            Vec3 underwaterColor = CustomColorizer.getUnderwaterColor(
                this.mc.theWorld, this.mc.renderViewEntity.posX, this.mc.renderViewEntity.posY + 1.0, this.mc.renderViewEntity.posZ
            );
            if (underwaterColor != null) {
                this.fogColorRed = (float)underwaterColor.xCoord;
                this.fogColorGreen = (float)underwaterColor.yCoord;
                this.fogColorBlue = (float)underwaterColor.zCoord;
            }
        } else if (blockAtEntityViewpoint.getMaterial() == Material.lava) {
            this.fogColorRed = 0.6F;
            this.fogColorGreen = 0.1F;
            this.fogColorBlue = 0.0F;
        }

        float f3 = this.fogColor2 + (this.fogColor1 - this.fogColor2) * f;
        this.fogColorRed *= f3;
        this.fogColorGreen *= f3;
        this.fogColorBlue *= f3;
        double voidFogYFactor = theWorld.provider.getVoidFogYFactor();
        if (!Config.isDepthFog()) {
            voidFogYFactor = 1.0;
        }

        double d = (renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * f) * voidFogYFactor;
        if (renderViewEntity.isPotionActive(Potion.blindness)) {
            int duration = renderViewEntity.getActivePotionEffect(Potion.blindness).getDuration();
            if (duration < 20) {
                d *= 1.0F - duration / 20.0F;
            } else {
                d = 0.0;
            }
        }

        if (d < 1.0) {
            if (d < 0.0) {
                d = 0.0;
            }

            d *= d;
            this.fogColorRed = (float)(this.fogColorRed * d);
            this.fogColorGreen = (float)(this.fogColorGreen * d);
            this.fogColorBlue = (float)(this.fogColorBlue * d);
        }

        if (this.bossColorModifier > 0.0F) {
            float f4 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * f;
            this.fogColorRed = this.fogColorRed * (1.0F - f4) + this.fogColorRed * 0.7F * f4;
            this.fogColorGreen = this.fogColorGreen * (1.0F - f4) + this.fogColorGreen * 0.6F * f4;
            this.fogColorBlue = this.fogColorBlue * (1.0F - f4) + this.fogColorBlue * 0.6F * f4;
        }

        if (renderViewEntity.isPotionActive(Potion.nightVision)) {
            float nightVisionBrightness = this.getNightVisionBrightness(this.mc.thePlayer, f);
            float f4 = 1.0F / this.fogColorRed;
            if (f4 > 1.0F / this.fogColorGreen) {
                f4 = 1.0F / this.fogColorGreen;
            }

            if (f4 > 1.0F / this.fogColorBlue) {
                f4 = 1.0F / this.fogColorBlue;
            }

            this.fogColorRed = this.fogColorRed * (1.0F - nightVisionBrightness) + this.fogColorRed * f4 * nightVisionBrightness;
            this.fogColorGreen = this.fogColorGreen * (1.0F - nightVisionBrightness) + this.fogColorGreen * f4 * nightVisionBrightness;
            this.fogColorBlue = this.fogColorBlue * (1.0F - nightVisionBrightness) + this.fogColorBlue * f4 * nightVisionBrightness;
        }

        if (this.mc.gameSettings.anaglyph) {
            float f4 = (this.fogColorRed * 30.0F + this.fogColorGreen * 59.0F + this.fogColorBlue * 11.0F) / 100.0F;
            float f5 = (this.fogColorRed * 30.0F + this.fogColorGreen * 70.0F) / 100.0F;
            float f6 = (this.fogColorRed * 30.0F + this.fogColorBlue * 70.0F) / 100.0F;
            this.fogColorRed = f4;
            this.fogColorGreen = f5;
            this.fogColorBlue = f6;
        }

        if (Reflector.EntityViewRenderEvent_FogColors_Constructor.exists()) {
            Object newInstance = Reflector.newInstance(
                Reflector.EntityViewRenderEvent_FogColors_Constructor, this, renderViewEntity, blockAtEntityViewpoint, f, this.fogColorRed, this.fogColorGreen, this.fogColorBlue
            );
            Reflector.postForgeBusEvent(newInstance);
            this.fogColorRed = Reflector.getFieldValueFloat(newInstance, Reflector.EntityViewRenderEvent_FogColors_red, this.fogColorRed);
            this.fogColorGreen = Reflector.getFieldValueFloat(newInstance, Reflector.EntityViewRenderEvent_FogColors_green, this.fogColorGreen);
            this.fogColorBlue = Reflector.getFieldValueFloat(newInstance, Reflector.EntityViewRenderEvent_FogColors_blue, this.fogColorBlue);
        }

        if (Config.isShaders()) {
            Shaders.setClearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
        } else {
            GL11.glClearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
        }
    }

    public void setupFog(int n, float f) {
        EntityLivingBase renderViewEntity = this.mc.renderViewEntity;
        boolean flag = false;
        this.fogStandard = false;
        if (renderViewEntity instanceof EntityPlayer) {
            flag = ((EntityPlayer)renderViewEntity).capabilities.isCreativeMode;
        }

        if (n == 999) {
            GL11.glFog(2918, this.setFogColorBuffer(0.0F, 0.0F, 0.0F, 1.0F));
            if (Config.isShaders()) {
                Shaders.sglFogi(2917, 9729);
            } else {
                GL11.glFogi(2917, 9729);
            }

            GL11.glFogf(2915, 0.0F);
            GL11.glFogf(2916, 8.0F);
            if (GLContext.getCapabilities().GL_NV_fog_distance) {
                if (Config.isShaders()) {
                    Shaders.sglFogi(34138, 34139);
                } else {
                    GL11.glFogi(34138, 34139);
                }
            }

            GL11.glFogf(2915, 0.0F);
        } else {
            GL11.glFog(2918, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
            GL11.glNormal3f(0.0F, -1.0F, 0.0F);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            Block blockAtEntityViewpoint = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, renderViewEntity, f);
            Object newInstance = Reflector.newInstance(Reflector.EntityViewRenderEvent_FogDensity_Constructor, this, renderViewEntity, blockAtEntityViewpoint, f, 0.1F);
            if (Reflector.postForgeBusEvent(newInstance)) {
                float fieldValueFloat = Reflector.getFieldValueFloat(newInstance, Reflector.EntityViewRenderEvent_FogDensity_density, 0.0F);
                GL11.glFogf(2914, fieldValueFloat);
            } else if (renderViewEntity.isPotionActive(Potion.blindness)) {
                float f2 = 5.0F;
                int duration = renderViewEntity.getActivePotionEffect(Potion.blindness).getDuration();
                if (duration < 20) {
                    f2 = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - duration / 20.0F);
                }

                if (Config.isShaders()) {
                    Shaders.sglFogi(2917, 9729);
                } else {
                    GL11.glFogi(2917, 9729);
                }

                if (n < 0) {
                    GL11.glFogf(2915, 0.0F);
                    GL11.glFogf(2916, f2 * 0.8F);
                } else {
                    GL11.glFogf(2915, f2 * 0.25F);
                    GL11.glFogf(2916, f2);
                }

                if (Config.isFogFancy()) {
                    if (Config.isShaders()) {
                        Shaders.sglFogi(34138, 34139);
                    } else {
                        GL11.glFogi(34138, 34139);
                    }
                }
            } else if (this.cloudFog) {
                if (Config.isShaders()) {
                    Shaders.sglFogi(2917, 2048);
                } else {
                    GL11.glFogi(2917, 2048);
                }

                GL11.glFogf(2914, 0.1F);
            } else if (blockAtEntityViewpoint.getMaterial() == Material.water) {
                if (Config.isShaders()) {
                    Shaders.sglFogi(2917, 2048);
                } else {
                    GL11.glFogi(2917, 2048);
                }

                if (renderViewEntity.isPotionActive(Potion.waterBreathing)) {
                    GL11.glFogf(2914, 0.05F);
                } else {
                    GL11.glFogf(2914, 0.1F - EnchantmentHelper.getRespiration(renderViewEntity) * 0.03F);
                }

                if (Config.isClearWater()) {
                    GL11.glFogf(2914, 0.02F);
                }
            } else if (blockAtEntityViewpoint.getMaterial() == Material.lava) {
                if (Config.isShaders()) {
                    Shaders.sglFogi(2917, 2048);
                } else {
                    GL11.glFogi(2917, 2048);
                }

                GL11.glFogf(2914, 2.0F);
            } else {
                float farPlaneDistance = this.farPlaneDistance;
                this.fogStandard = true;
                if (Config.isDepthFog() && this.mc.theWorld.provider.getWorldHasVoidParticles() && !flag) {
                    double d = ((renderViewEntity.getBrightnessForRender(f) & 15728640) >> 20) / 16.0
                        + (renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * f + 4.0) / 32.0;
                    if (d < 1.0) {
                        if (d < 0.0) {
                            d = 0.0;
                        }

                        d *= d;
                        float f2 = 100.0F * (float)d;
                        if (f2 < 5.0F) {
                            f2 = 5.0F;
                        }

                        if (farPlaneDistance > f2) {
                            farPlaneDistance = f2;
                        }
                    }
                }

                if (Config.isShaders()) {
                    Shaders.sglFogi(2917, 9729);
                } else {
                    GL11.glFogi(2917, 9729);
                }

                if (n < 0) {
                    GL11.glFogf(2915, 0.0F);
                    GL11.glFogf(2916, farPlaneDistance);
                } else {
                    GL11.glFogf(2915, farPlaneDistance * Config.getFogStart());
                    GL11.glFogf(2916, farPlaneDistance);
                }

                if (GLContext.getCapabilities().GL_NV_fog_distance) {
                    if (Config.isFogFancy()) {
                        if (Config.isShaders()) {
                            Shaders.sglFogi(34138, 34139);
                        } else {
                            GL11.glFogi(34138, 34139);
                        }
                    }

                    if (Config.isFogFast()) {
                        if (Config.isShaders()) {
                            Shaders.sglFogi(34138, 34140);
                        } else {
                            GL11.glFogi(34138, 34140);
                        }
                    }
                }

                if (this.mc.theWorld.provider.doesXZShowFog((int)renderViewEntity.posX, (int)renderViewEntity.posZ)) {
                    farPlaneDistance = this.farPlaneDistance;
                    GL11.glFogf(2915, farPlaneDistance * 0.05F);
                    GL11.glFogf(2916, farPlaneDistance);
                }

                Reflector.postForgeBusEvent(Reflector.newInstance(Reflector.EntityViewRenderEvent_RenderFogEvent_Constructor, this, renderViewEntity, blockAtEntityViewpoint, f, n, farPlaneDistance));
            }

            GL11.glEnable(2903);
            GL11.glColorMaterial(1028, 4608);
        }
    }

    public FloatBuffer setFogColorBuffer(float f, float f2, float f3, float f4) {
        if (Config.isShaders()) {
            Shaders.setFogColor(f, f2, f3);
        }

        ((Buffer)this.fogColorBuffer).clear();
        this.fogColorBuffer.put(f).put(f2).put(f3).put(f4);
        ((Buffer)this.fogColorBuffer).flip();
        return this.fogColorBuffer;
    }

    public MapItemRenderer getMapItemRenderer() {
        return this.theMapItemRenderer;
    }

    public void waitForServerThread() {
        this.serverWaitTimeCurrent = 0;
        if (!Config.isSmoothWorld()) {
            this.lastServerTime = 0L;
            this.lastServerTicks = 0;
        } else if (this.mc.getIntegratedServer() != null) {
            IntegratedServer integratedServer = this.mc.getIntegratedServer();
            boolean isGamePaused = this.mc.isGamePaused();
            if (!isGamePaused && !(this.mc.currentScreen instanceof GuiDownloadTerrain)) {
                if (this.serverWaitTime > 0) {
                    Config.sleep(this.serverWaitTime);
                    this.serverWaitTimeCurrent = this.serverWaitTime;
                }

                long l = System.nanoTime() / 1000000L;
                if (this.lastServerTime != 0L && this.lastServerTicks != 0) {
                    long l2 = l - this.lastServerTime;
                    if (l2 < 0L) {
                        this.lastServerTime = l;
                        l2 = 0L;
                    }

                    if (l2 >= 50L) {
                        this.lastServerTime = l;
                        int tickCounter = integratedServer.getTickCounter();
                        int n = tickCounter - this.lastServerTicks;
                        if (n < 0) {
                            this.lastServerTicks = tickCounter;
                            n = 0;
                        }

                        if (n < 1 && this.serverWaitTime < 100) {
                            this.serverWaitTime += 2;
                        }

                        if (n > 1 && this.serverWaitTime > 0) {
                            this.serverWaitTime--;
                        }

                        this.lastServerTicks = tickCounter;
                    }
                } else {
                    this.lastServerTime = l;
                    this.lastServerTicks = integratedServer.getTickCounter();
                    this.avgServerTickDiff = 1.0F;
                    this.avgServerTimeDiff = 50.0F;
                }
            } else {
                if (this.mc.currentScreen instanceof GuiDownloadTerrain) {
                    Config.sleep(20L);
                }

                this.lastServerTime = 0L;
                this.lastServerTicks = 0;
            }
        }
    }

    public void showLagometer(long l, long l2) {
        if (this.mc.gameSettings.ofLagometer || this.showExtendedDebugInfo) {
            if (this.prevFrameTimeNano == -1L) {
                this.prevFrameTimeNano = System.nanoTime();
            }

            long nanoTime = System.nanoTime();
            int n = this.numRecordedFrameTimes & this.frameTimes.length - 1;
            this.tickTimes[n] = l;
            this.chunkTimes[n] = l2;
            this.serverTimes[n] = this.serverWaitTimeCurrent;
            this.frameTimes[n] = nanoTime - this.prevFrameTimeNano;
            this.numRecordedFrameTimes++;
            this.prevFrameTimeNano = nanoTime;
            GL11.glClear(256);
            GL11.glMatrixMode(5889);
            GL11.glPushMatrix();
            GL11.glEnable(2903);
            GL11.glLoadIdentity();
            GL11.glOrtho(0.0, this.mc.displayWidth, this.mc.displayHeight, 0.0, 1000.0, 3000.0);
            GL11.glMatrixMode(5888);
            GL11.glPushMatrix();
            GL11.glLoadIdentity();
            GL11.glTranslatef(0.0F, 0.0F, -2000.0F);
            GL11.glLineWidth(1.0F);
            GL11.glDisable(3553);
            Tessellator instance = Tessellator.instance;
            instance.startDrawing(1);

            for (int i = 0; i < this.frameTimes.length; i++) {
                int n2 = (i - this.numRecordedFrameTimes & this.frameTimes.length - 1) * 255 / this.frameTimes.length;
                long l3 = this.frameTimes[i] / 200000L;
                float displayHeight = this.mc.displayHeight;
                instance.setColorOpaque_I(-16777216 + n2 * 256);
                instance.addVertex(i + 0.5F, displayHeight - (float)l3 + 0.5F, 0.0);
                instance.addVertex(i + 0.5F, displayHeight + 0.5F, 0.0);
                displayHeight -= (float)l3;
                long l4 = this.tickTimes[i] / 200000L;
                instance.setColorOpaque_I(-16777216 + n2 * 65536 + n2 * 256 + n2 * 1);
                instance.addVertex(i + 0.5F, displayHeight + 0.5F, 0.0);
                instance.addVertex(i + 0.5F, displayHeight + (float)l4 + 0.5F, 0.0);
                displayHeight += (float)l4;
                long l5 = this.chunkTimes[i] / 200000L;
                instance.setColorOpaque_I(-16777216 + n2 * 65536);
                instance.addVertex(i + 0.5F, displayHeight + 0.5F, 0.0);
                instance.addVertex(i + 0.5F, displayHeight + (float)l5 + 0.5F, 0.0);
                displayHeight += (float)l5;
                long l6 = this.serverTimes[i];
                if (l6 > 0L) {
                    long l7 = l6 * 1000000L / 200000L;
                    instance.setColorOpaque_I(-16777216 + n2 * 1);
                    instance.addVertex(i + 0.5F, displayHeight + 0.5F, 0.0);
                    instance.addVertex(i + 0.5F, displayHeight + (float)l7 + 0.5F, 0.0);
                }
            }

            instance.draw();
            GL11.glMatrixMode(5889);
            GL11.glPopMatrix();
            GL11.glMatrixMode(5888);
            GL11.glPopMatrix();
            GL11.glEnable(3553);
        }
    }

    public void frameFinish() {
        if (this.mc.theWorld != null) {
            long currentTimeMillis = System.currentTimeMillis();
            if (currentTimeMillis > this.lastErrorCheckTimeMs + 10000L) {
                this.lastErrorCheckTimeMs = currentTimeMillis;
                int glGetError = GL11.glGetError();
                if (glGetError != 0) {
                    String gluErrorString = GLU.gluErrorString(glGetError);
                    ChatComponentText chatComponentText = new ChatComponentText(I18n.format("of.message.openglError", glGetError, gluErrorString));
                    this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText);
                }
            }
        }
    }

    public boolean setFxaaShader(int n) {
        if (!OpenGlHelper.isFramebufferEnabled()) {
            return false;
        }

        if (this.theShaderGroup != null && this.theShaderGroup != this.fxaaShaders[2] && this.theShaderGroup != this.fxaaShaders[4]) {
            return true;
        }

        if (n != 2 && n != 4) {
            if (this.theShaderGroup == null) {
                return true;
            }

            this.theShaderGroup.deleteShaderGroup();
            this.theShaderGroup = null;
            return true;
        } else {
            if (this.theShaderGroup != null && this.theShaderGroup == this.fxaaShaders[n]) {
                return true;
            }

            if (this.mc.theWorld == null) {
                return true;
            }

            this.loadShader(new ResourceLocation("shaders/post/fxaa_of_" + n + "x.json"));
            this.fxaaShaders[n] = this.theShaderGroup;
            return this.theShaderGroup != null;
        }
    }

    public void loadShader(ResourceLocation resourceLocation) {
        if (OpenGlHelper.isFramebufferEnabled()) {
            try {
                this.theShaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), resourceLocation);
                this.theShaderGroup.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
            } catch (IOException e) {
                logger.warn("Failed to load shader: " + resourceLocation, e);
                this.shaderIndex = shaderCount;
            } catch (JsonSyntaxException e) {
                logger.warn("Failed to load shader: " + resourceLocation, e);
                this.shaderIndex = shaderCount;
            }
        }
    }

    public void reloadBlurShader() {
        if (OpenGlHelper.isFramebufferEnabled() && OpenGlHelper.shadersSupported) {
            if (this.shaderGroup != null) {
                this.shaderGroup.deleteShaderGroup();
                this.shaderGroup = null;
            }

            if (CheatBreaker.getInstance().getKeybindSettings().guiBlur.getBooleanValue()) {
                try {
                    this.shaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), animblurJson);
                    this.shaderGroup.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
                } catch (Exception e) {
                }
            }
        }
    }

    public int applyFullbrightLightmap(int n) {
        KeybindSettings keybindSettings = CheatBreaker.getInstance().getKeybindSettings();
        boolean flag = keybindSettings != null && keybindSettings.enableFpsBoost.getBooleanValue() && keybindSettings.fullbright.getBooleanValue();
        if (flag) {
            Arrays.fill(this.lightmapColors, -1);
            return 0;
        } else {
            return n;
        }
    }

    public boolean isBlurActive() {
        return OpenGlHelper.shadersSupported && this.shaderGroup != null;
    }

    public void render(float f, String str) {
        if (this.mc.renderViewEntity instanceof EntityPlayer) {
            StringFloatEvent stringFloatEvent = new StringFloatEvent(f, str, 1.0F, 1.0F, 1.0F);
            CheatBreaker.getInstance().getEventBus().handleEvent(stringFloatEvent);
            if (stringFloatEvent.isCancelled()) {
                return;
            }

            float floatValue2 = stringFloatEvent.getFloatValue2();
            float floatValue3 = stringFloatEvent.getFloatValue3();
            float floatValue4 = stringFloatEvent.getFloatValue4();
            EntityPlayer renderViewEntity = (EntityPlayer)this.mc.renderViewEntity;
            float f2 = renderViewEntity.distanceWalkedModified - renderViewEntity.prevDistanceWalkedModified;
            float f3 = -(renderViewEntity.distanceWalkedModified + f2 * f);
            float f4 = renderViewEntity.prevCameraYaw + (renderViewEntity.cameraYaw - renderViewEntity.prevCameraYaw) * f;
            float f5 = renderViewEntity.prevCameraPitch + (renderViewEntity.cameraPitch - renderViewEntity.prevCameraPitch) * f;
            GL11.glTranslatef(MathHelper.sin(f3 * (float) Math.PI) * f4 * 0.5F * floatValue2, -Math.abs(MathHelper.cos(f3 * (float) Math.PI) * f4 * floatValue3), 0.0F);
            GL11.glRotatef(MathHelper.sin(f3 * (float) Math.PI) * f4 * 3.0F * floatValue2, 0.0F, 0.0F, 1.0F);
            GL11.glRotatef(Math.abs(MathHelper.cos(f3 * (float) Math.PI - 0.2F) * f4) * 5.0F * floatValue3, 1.0F, 0.0F, 0.0F);
            GL11.glRotatef(f5 * floatValue4, 1.0F, 0.0F, 0.0F);
        }
    }

    public void reloadMotionBlurShader() {
        if (OpenGlHelper.isFramebufferEnabled() && OpenGlHelper.shadersSupported) {
            if (this.theShaderGroup != null) {
                this.theShaderGroup.deleteShaderGroup();
            }

            ResourceLocation resourceLocation = CheatBreaker.getInstance().getModManager().motionBlurMod.blurType.getValue().equals("Phosphor")
                ? motionblurJson
                : lunarMotionblurJson;

            try {
                this.theShaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), resourceLocation);
                this.theShaderGroup.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
            } catch (Exception e) {
                logger.warn("Failed to load shader: " + resourceLocation, e);
            }
        }
    }

    public ShaderGroup getBlurShaderGroup() {
        return this.shaderGroup;
    }

    public void render(float f, int n, boolean flag) {
        this.farPlaneDistance = this.mc.gameSettings.renderDistanceChunks * 16;
        if (Config.isFogFancy()) {
            this.farPlaneDistance *= 0.95F;
        }

        if (Config.isFogFast()) {
            this.farPlaneDistance *= 0.83F;
        }

        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        float f2 = 0.07F;
        if (this.mc.gameSettings.anaglyph) {
            GL11.glTranslatef(-(n * 2 - 1) * f2, 0.0F, 0.0F);
        }

        float f3 = this.farPlaneDistance * 2.0F;
        if (f3 < 128.0F) {
            f3 = 128.0F;
        }

        if (this.mc.theWorld.provider.dimensionId == 1) {
            f3 = 256.0F;
        }

        if (this.cameraZoom != 1.0) {
            GL11.glTranslatef((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
            GL11.glScaled(this.cameraZoom, this.cameraZoom, 1.0);
        }

        Project.gluPerspective(this.getFOVModifier(f, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, f3);
        if (this.mc.playerController.enableEverythingIsScrewedUpMode()) {
            float f4 = 0.6666667F;
            GL11.glScalef(1.0F, f4, 1.0F);
        }

        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        if (this.mc.gameSettings.anaglyph) {
            GL11.glTranslatef((n * 2 - 1) * 0.1F, 0.0F, 0.0F);
        }

        this.hurtCameraEffect(f);
        if (this.mc.gameSettings.viewBobbing) {
            this.render(f, "screen");
        }

        float f4 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * f;
        if (f4 > 0.0F) {
            byte b = 20;
            if (this.mc.thePlayer.isPotionActive(Potion.confusion)) {
                b = 7;
            }

            float f5 = 5.0F / (f4 * f4 + 5.0F) - f4 * 0.04F;
            f5 *= f5;
            GL11.glRotatef((this.rendererUpdateCount + f) * b, 0.0F, 1.0F, 1.0F);
            GL11.glScalef(1.0F / f5, 1.0F, 1.0F);
            GL11.glRotatef(-(this.rendererUpdateCount + f) * b, 0.0F, 1.0F, 1.0F);
        }

        if (flag) {
            this.setupViewBobbing(f);
        } else {
            this.orientCamera(f);
        }

        if (this.debugViewDirection > 0) {
            int n2 = this.debugViewDirection - 1;
            if (n2 == 1) {
                GL11.glRotatef(90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (n2 == 2) {
                GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
            }

            if (n2 == 3) {
                GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            }

            if (n2 == 4) {
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (n2 == 5) {
                GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
            }
        }
    }

    public DynamicTexture getLightmapTexture() {
        return this.lightmapTexture;
    }
}
