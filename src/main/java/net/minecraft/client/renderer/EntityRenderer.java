package net.minecraft.client.renderer;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.EntityLivingBaseFloatEvent1;
import com.cheatbreaker.client.event.FloatEvent1;
import com.cheatbreaker.client.event.FloatEvent2;
import com.cheatbreaker.client.event.FloatEvent3;
import com.cheatbreaker.client.event.FloatEvent6;
import com.cheatbreaker.client.event.SimpleEvent46;
import com.cheatbreaker.client.event.SimpleEvent47;
import com.cheatbreaker.client.event.StringFloatEvent;
import com.cheatbreaker.client.mod.KeybindSettings;
import com.cheatbreaker.client.mod.impl.EnvironmentChangerMod;
import com.cheatbreaker.client.mod.impl.FreeLookHandler;
import com.cheatbreaker.client.ui.screen.FriendsScreen;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.gui.MapItemRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.BossStatus;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.ClickEvent.Action;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldSettings.GameType;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.Config;
import net.optifine.CustomColors;
import net.optifine.GlErrors;
import net.optifine.Lagometer;
import net.optifine.RandomEntities;
import net.optifine.gui.GuiChatOF;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorForge;
import net.optifine.reflect.ReflectorResolver;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;
import net.optifine.util.MemoryMonitor;
import net.optifine.util.TextureUtils;
import net.optifine.util.TimedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GLContext;
import org.lwjgl.util.glu.Project;

public class EntityRenderer implements IResourceManagerReloadListener {
   public static Logger logger = LogManager.getLogger();
   public static ResourceLocation locationRainPng = new ResourceLocation("textures/environment/rain.png");
   public static ResourceLocation locationSnowPng = new ResourceLocation("textures/environment/snow.png");
   public static boolean anaglyphEnable;
   public static int anaglyphField;
   public Random random = new Random();
   public MouseFilter mouseFilterXAxis = new MouseFilter();
   public MouseFilter mouseFilterYAxis = new MouseFilter();
   public float farPlaneDistance;
   public float thirdPersonDistance = 4.0F;
   public float thirdPersonDistanceTemp = 4.0F;
   public int rendererUpdateCount;
   public Entity pointedEntity;
   public boolean renderHand = true;
   public boolean drawBlockOutline = true;
   public long prevFrameTime = Minecraft.getSystemTime();
   public float[] rainXCoords = new float[1024];
   public float smoothCamYaw;
   public float smoothCamPitch;
   public float smoothCamFilterX;
   public float smoothCamFilterY;
   public float smoothCamPartialTicks;
   public float fovModifierHand;
   public float fovModifierHandPrev;
   public float bossColorModifier;
   public float bossColorModifierPrev;
   public boolean cloudFog;
   public float[] rainYCoords = new float[1024];
   public FloatBuffer fogColorBuffer = GLAllocation.createDirectFloatBuffer(16);
   public int debugViewDirection = 0;
   public long renderEndNanoTime;
   public boolean debugView = false;
   public double cameraZoom = 1.0;
   public boolean initialized = false;
   public boolean lightmapUpdateNeeded;
   public float torchFlickerX;
   public float torchFlickerDX;
   public int rainSoundCounter;
   public World updatedWorld = null;
   public boolean showDebugInfo = false;
   public boolean fogStandard = false;
   public float fogColorRed;
   public float fogColorGreen;
   public float fogColorBlue;
   public float fogColor2;
   public float fogColor1;
   public float clipDistance = 128.0F;
   public long lastServerTime = 0L;
   public int lastServerTicks = 0;
   public double cameraYaw;
   public double cameraPitch;
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
      new ResourceLocation("shaders/post/antialias.json"),
      new ResourceLocation("shaders/post/creeper.json"),
      new ResourceLocation("shaders/post/spider.json")
   };
   public static ResourceLocation animblurJson = new ResourceLocation("shaders/post/animblur.json");
   public int serverWaitTimeCurrent = 0;
   public float avgServerTimeDiff = 0.0F;
   public float avgServerTickDiff = 0.0F;
   public ShaderGroup[] fxaaShaders = new ShaderGroup[10];
   public boolean loadVisibleChunks = false;
   public float floatValue = 1.0F;
   public int shaderIndex;
   public boolean useShader;
   public int frameCount;
   public Minecraft mc;
   public IResourceManager resourceManager;
   public ItemRenderer itemRenderer;
   public MapItemRenderer theMapItemRenderer;
   public DynamicTexture lightmapTexture;
   public ResourceLocation locationLightMap;
   public int[] lightmapColors;
   public static ResourceLocation motionblurJson = new ResourceLocation("shaders/post/motionblur.json");
   public static ResourceLocation lunarMotionblurJson = new ResourceLocation("shaders/post/lunar_motionblur.json");
   public ShaderGroup shaderGroup;
   public static int shaderCount = shaderResourceLocations.length;
   public ShaderGroup theShaderGroup;

   public EntityRenderer(Minecraft mcIn, IResourceManager resourceManagerIn) {
      this.shaderIndex = shaderCount;
      this.useShader = false;
      this.frameCount = 0;
      this.mc = mcIn;
      this.resourceManager = resourceManagerIn;
      this.itemRenderer = mcIn.getItemRenderer();
      this.theMapItemRenderer = new MapItemRenderer(mcIn.getTextureManager());
      this.lightmapTexture = new DynamicTexture(16, 16);
      this.locationLightMap = mcIn.getTextureManager().getDynamicTextureLocation("lightMap", this.lightmapTexture);
      this.lightmapColors = this.lightmapTexture.getTextureData();
      this.theShaderGroup = null;

      for (int i = 0; i < 32; i++) {
         for (int j = 0; j < 32; j++) {
            float f = j - 16;
            float f2 = i - 16;
            float f3 = MathHelper.sqrt_float(f * f + f2 * f2);
            this.rainXCoords[i << 5 | j] = -f2 / f3;
            this.rainYCoords[i << 5 | j] = f / f3;
         }
      }
   }

   public boolean isShaderActive() {
      return OpenGlHelper.shadersSupported && this.theShaderGroup != null;
   }

   public void stopUseShader() {
      if (this.theShaderGroup != null) {
         this.theShaderGroup.deleteShaderGroup();
      }

      this.theShaderGroup = null;
      this.shaderIndex = shaderCount;
   }

   public void switchUseShader() {
      this.useShader = !this.useShader;
   }

   public void loadEntityShader(Entity entityIn) {
      if (OpenGlHelper.shadersSupported) {
         if (this.theShaderGroup != null) {
            this.theShaderGroup.deleteShaderGroup();
         }

         this.theShaderGroup = null;
         if (entityIn instanceof EntityCreeper) {
            this.loadShader(new ResourceLocation("shaders/post/creeper.json"));
         } else if (entityIn instanceof EntitySpider) {
            this.loadShader(new ResourceLocation("shaders/post/spider.json"));
         } else if (entityIn instanceof EntityEnderman) {
            this.loadShader(new ResourceLocation("shaders/post/invert.json"));
         } else if (Reflector.ForgeHooksClient_loadEntityShader.exists()) {
            Reflector.call(Reflector.ForgeHooksClient_loadEntityShader, entityIn, this);
         }
      }
   }

   public void activateNextShader() {
      if (OpenGlHelper.shadersSupported && this.mc.getRenderViewEntity() instanceof EntityPlayer) {
         if (this.theShaderGroup != null) {
            this.theShaderGroup.deleteShaderGroup();
         }

         this.shaderIndex = (this.shaderIndex + 1) % (shaderResourceLocations.length + 1);
         if (this.shaderIndex != shaderCount) {
            this.loadShader(shaderResourceLocations[this.shaderIndex]);
         } else {
            this.theShaderGroup = null;
         }
      }
   }

   public void loadShader(ResourceLocation resourceLocationIn) {
      if (OpenGlHelper.isFramebufferEnabled()) {
         try {
            this.theShaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), resourceLocationIn);
            this.theShaderGroup.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
            this.useShader = true;
         } catch (IOException | JsonSyntaxException e) {
            logger.warn("Failed to load shader: " + resourceLocationIn, e);
            this.shaderIndex = shaderCount;
            this.useShader = false;
         }
      }
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      if (this.theShaderGroup != null) {
         this.theShaderGroup.deleteShaderGroup();
      }

      this.theShaderGroup = null;
      if (this.shaderIndex != shaderCount) {
         this.loadShader(shaderResourceLocations[this.shaderIndex]);
      } else {
         this.loadEntityShader(this.mc.getRenderViewEntity());
      }

      this.run3();
   }

   public void updateRenderer() {
      if (OpenGlHelper.shadersSupported && ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
         ShaderLinkHelper.setNewStaticShaderLinkHelper();
      }

      this.updateFovModifierHand();
      this.updateTorchFlicker();
      this.fogColor2 = this.fogColor1;
      this.thirdPersonDistanceTemp = this.thirdPersonDistance;
      if (this.mc.gameSettings.smoothCamera) {
         float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
         float f2 = f * f * f * 8.0F;
         this.smoothCamFilterX = this.mouseFilterXAxis.smooth(this.smoothCamYaw, 0.05F * f2);
         this.smoothCamFilterY = this.mouseFilterYAxis.smooth(this.smoothCamPitch, 0.05F * f2);
         this.smoothCamPartialTicks = 0.0F;
         this.smoothCamYaw = 0.0F;
         this.smoothCamPitch = 0.0F;
      } else {
         this.smoothCamFilterX = 0.0F;
         this.smoothCamFilterY = 0.0F;
         this.mouseFilterXAxis.reset();
         this.mouseFilterYAxis.reset();
      }

      if (this.mc.getRenderViewEntity() == null) {
         this.mc.setRenderViewEntity(this.mc.thePlayer);
      }

      Entity renderViewEntity = this.mc.getRenderViewEntity();
      double posX = renderViewEntity.posX;
      double d = renderViewEntity.posY + renderViewEntity.getEyeHeight();
      double posZ = renderViewEntity.posZ;
      float lightBrightness = this.mc.theWorld.getLightBrightness(new BlockPos(posX, d, posZ));
      float f = this.mc.gameSettings.renderDistanceChunks / 16.0F;
      f = MathHelper.clamp_float(f, 0.0F, 1.0F);
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

   public void updateShaderGroupSize(int width, int height) {
      if (OpenGlHelper.shadersSupported) {
         if (this.theShaderGroup != null) {
            this.theShaderGroup.createBindFramebuffers(width, height);
         }

         if (this.shaderGroup != null) {
            this.shaderGroup.createBindFramebuffers(width, height);
         }

         if (this.mc.renderGlobal != null) {
            this.mc.renderGlobal.createBindEntityOutlineFbs(width, height);
         }
      }
   }

   public void getMouseOver(float partialTicks) {
      Entity renderViewEntity = this.mc.getRenderViewEntity();
      if (renderViewEntity != null && this.mc.theWorld != null) {
         this.mc.mcProfiler.startSection("pick");
         this.mc.pointedEntity = null;
         double blockReachDistance = this.mc.playerController.getBlockReachDistance();
         this.mc.objectMouseOver = renderViewEntity.rayTrace(blockReachDistance, partialTicks);
         double d = blockReachDistance;
         Vec3 positionEyes = renderViewEntity.getPositionEyes(partialTicks);
         boolean flag = false;
         byte b = 3;
         if (this.mc.playerController.extendedReach()) {
            blockReachDistance = 6.0;
            d = 6.0;
         } else if (blockReachDistance > 3.0) {
            flag = true;
         }

         if (this.mc.objectMouseOver != null) {
            d = this.mc.objectMouseOver.hitVec.distanceTo(positionEyes);
         }

         Vec3 look = renderViewEntity.getLook(partialTicks);
         Vec3 addVector = positionEyes.addVector(look.xCoord * blockReachDistance, look.yCoord * blockReachDistance, look.zCoord * blockReachDistance);
         this.pointedEntity = null;
         Vec3 vec3 = null;
         float f = 1.0F;
         List entitiesInAABBexcluding = this.mc
            .theWorld
            .getEntitiesInAABBexcluding(
               renderViewEntity,
               renderViewEntity.getEntityBoundingBox().addCoord(look.xCoord * blockReachDistance, look.yCoord * blockReachDistance, look.zCoord * blockReachDistance).expand(f, f, f),
               Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
                  public boolean apply(Entity entity) {
                     return entity.canBeCollidedWith();
                  }
               })
            );
         double d2 = d;

         for (int i = 0; i < entitiesInAABBexcluding.size(); i++) {
            Entity entity = (Entity)entitiesInAABBexcluding.get(i);
            float collisionBorderSize = entity.getCollisionBorderSize();
            AxisAlignedBB expand = entity.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
            MovingObjectPosition calculateIntercept = expand.calculateIntercept(positionEyes, addVector);
            if (expand.isVecInside(positionEyes)) {
               if (d2 >= 0.0) {
                  this.pointedEntity = entity;
                  vec3 = calculateIntercept == null ? positionEyes : calculateIntercept.hitVec;
                  d2 = 0.0;
               }
            } else if (calculateIntercept != null) {
               double distanceTo = positionEyes.distanceTo(calculateIntercept.hitVec);
               if (distanceTo < d2 || d2 == 0.0) {
                  boolean flag2 = false;
                  if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                     flag2 = Reflector.callBoolean(entity, Reflector.ForgeEntity_canRiderInteract);
                  }

                  if (flag2 || entity != renderViewEntity.ridingEntity) {
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

         if (this.pointedEntity != null && flag && positionEyes.distanceTo(vec3) > 3.0) {
            this.pointedEntity = null;
            this.mc.objectMouseOver = new MovingObjectPosition(MovingObjectType.MISS, vec3, (EnumFacing)null, new BlockPos(vec3));
         }

         if (this.pointedEntity != null && (d2 < d || this.mc.objectMouseOver == null)) {
            this.mc.objectMouseOver = new MovingObjectPosition(this.pointedEntity, vec3);
            if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
               this.mc.pointedEntity = this.pointedEntity;
            }
         }

         this.mc.mcProfiler.endSection();
      }
   }

   public void updateFovModifierHand() {
      float f = 1.0F;
      if (this.mc.getRenderViewEntity() instanceof AbstractClientPlayer) {
         AbstractClientPlayer renderViewEntity = (AbstractClientPlayer)this.mc.getRenderViewEntity();
         f = renderViewEntity.getFovModifier();
      }

      this.fovModifierHandPrev = this.fovModifierHand;
      this.fovModifierHand = this.fovModifierHand + (f - this.fovModifierHand) * 0.5F;
      if (this.fovModifierHand > 1.5F) {
         this.fovModifierHand = 1.5F;
      }

      if (this.fovModifierHand < 0.1F) {
         this.fovModifierHand = 0.1F;
      }
   }

   public float getFOVModifier(float partialTicks, boolean useFOVSetting) {
      if (this.debugView) {
         return 90.0F;
      }

      Entity renderViewEntity = this.mc.getRenderViewEntity();
      float f = 70.0F;
      if (useFOVSetting) {
         f = this.mc.gameSettings.fovSetting;
         FloatEvent3 floatEvent3 = new FloatEvent3(f);
         CheatBreaker.getInstance().getEventBus().handleEvent(floatEvent3);
         f = floatEvent3.floatValue;
         if (Config.isDynamicFov() && !floatEvent3.isFlag()) {
            f *= this.fovModifierHandPrev + (this.fovModifierHand - this.fovModifierHandPrev) * partialTicks;
         }
      }

      boolean flag = false;
      if (this.mc.currentScreen == null) {
         flag = GameSettings.isKeyDown(this.mc.gameSettings.ofKeyBindZoom);
      }

      if (flag) {
         if (!Config.zoomMode) {
            Config.zoomMode = true;
            Config.floatValue = 1.0F;
            Config.zoomSmoothCamera = this.mc.gameSettings.smoothCamera && CheatBreaker.getInstance().getKeybindSettings().useCinematicCamera.getBooleanValue();
            this.mc.gameSettings.smoothCamera = CheatBreaker.getInstance().getKeybindSettings().useCinematicCamera.getBooleanValue();
            this.mc.renderGlobal.displayListEntitiesDirty = true;
         }

         if (Config.zoomMode) {
            f /= 4.0F * Config.floatValue;
         }
      } else if (Config.zoomMode) {
         Config.zoomMode = false;
         this.mc.gameSettings.smoothCamera = Config.zoomSmoothCamera;
         this.mouseFilterXAxis = new MouseFilter();
         this.mouseFilterYAxis = new MouseFilter();
         this.mc.renderGlobal.displayListEntitiesDirty = true;
      }

      if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).getHealth() <= 0.0F) {
         float f2 = ((EntityLivingBase)renderViewEntity).deathTime + partialTicks;
         f /= (1.0F - 500.0F / (f2 + 500.0F)) * 2.0F + 1.0F;
      }

      Block blockAtEntityViewpoint = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, renderViewEntity, partialTicks);
      if (blockAtEntityViewpoint.getMaterial() == Material.water) {
         f = f * 60.0F / 70.0F;
      }

      return Reflector.ForgeHooksClient_getFOVModifier.exists()
         ? Reflector.callFloat(Reflector.ForgeHooksClient_getFOVModifier, this, renderViewEntity, blockAtEntityViewpoint, partialTicks, f)
         : f;
   }

   public void hurtCameraEffect(float partialTicks) {
      if (this.mc.getRenderViewEntity() instanceof EntityLivingBase) {
         EntityLivingBase renderViewEntity = (EntityLivingBase)this.mc.getRenderViewEntity();
         float f = 14.0F;
         float f2 = 1.0F;
         EntityLivingBaseFloatEvent1 entityLivingBaseFloatEvent1 = new EntityLivingBaseFloatEvent1(partialTicks, renderViewEntity, f, f2);
         CheatBreaker.getInstance().getEventBus().handleEvent(entityLivingBaseFloatEvent1);
         if (entityLivingBaseFloatEvent1.isFlag()) {
            return;
         }

         f = entityLivingBaseFloatEvent1.getFloatValue2();
         f2 = entityLivingBaseFloatEvent1.getFloatValue3();
         float f3 = renderViewEntity.hurtTime - partialTicks;
         if (renderViewEntity.getHealth() <= 0.0F) {
            float f4 = renderViewEntity.deathTime + partialTicks;
            GlStateManager.rotate((40.0F - 8000.0F / (f4 + 200.0F)) * f2, 0.0F, 0.0F, 1.0F);
         }

         if (f3 < 0.0F) {
            return;
         }

         f3 /= renderViewEntity.maxHurtTime;
         f3 = MathHelper.sin(f3 * f3 * f3 * f3 * (float) Math.PI);
         float attackedAtYaw = renderViewEntity.attackedAtYaw;
         FloatEvent1 floatEvent1 = new FloatEvent1(f3, attackedAtYaw);
         CheatBreaker.getInstance().getEventBus().handleEvent(floatEvent1);
         f3 = floatEvent1.getFloatValue1();
         attackedAtYaw = floatEvent1.getAttackedAtYaw();
         GlStateManager.rotate(-attackedAtYaw * f2, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(-f3 * f * f2, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(attackedAtYaw * f2, 0.0F, 1.0F, 0.0F);
      }
   }

   public void orientCamera(float partialTicks) {
      Entity renderViewEntity = this.mc.getRenderViewEntity();
      float eyeHeight = renderViewEntity.getEyeHeight();
      FloatEvent2 floatEvent2 = new FloatEvent2(eyeHeight, partialTicks);
      CheatBreaker.getInstance().getEventBus().handleEvent(floatEvent2);
      eyeHeight = floatEvent2.getEyeHeight();
      double d = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * partialTicks;
      double d2 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * partialTicks + eyeHeight;
      double d3 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * partialTicks;
      if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPlayerSleeping()) {
         eyeHeight = (float)(eyeHeight + 1.0);
         GlStateManager.translate(0.0F, 0.3F, 0.0F);
         if (!this.mc.gameSettings.debugCamEnable) {
            BlockPos blockPos = new BlockPos(renderViewEntity);
            IBlockState blockState = this.mc.theWorld.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (Reflector.ForgeHooksClient_orientBedCamera.exists()) {
               Reflector.callVoid(Reflector.ForgeHooksClient_orientBedCamera, this.mc.theWorld, blockPos, blockState, renderViewEntity);
            } else if (block == Blocks.bed) {
               int horizontalIndex = blockState.getValue(BlockBed.FACING).getHorizontalIndex();
               GlStateManager.rotate(horizontalIndex * 90, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotate(renderViewEntity.prevRotationYaw + (renderViewEntity.rotationYaw - renderViewEntity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, -1.0F, 0.0F);
            GlStateManager.rotate(renderViewEntity.prevRotationPitch + (renderViewEntity.rotationPitch - renderViewEntity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
         }
      } else if (this.mc.gameSettings.thirdPersonView > 0) {
         double d4 = this.thirdPersonDistanceTemp + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * partialTicks;
         if (this.mc.gameSettings.debugCamEnable) {
            GlStateManager.translate(0.0F, 0.0F, (float)(-d4));
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
               float f = (i & 1) * 2 - 1;
               float f2 = (i >> 1 & 1) * 2 - 1;
               float f3 = (i >> 2 & 1) * 2 - 1;
               f *= 0.1F;
               f2 *= 0.1F;
               f3 *= 0.1F;
               MovingObjectPosition rayTraceBlocks = this.mc
                  .theWorld
                  .rayTraceBlocks(
                     new Vec3(d + f, d2 + f2, d3 + f3), new Vec3(d - d5 + f + f3, d2 - d7 + f2, d3 - d6 + f3)
                  );
               if (rayTraceBlocks != null) {
                  double distanceTo = rayTraceBlocks.hitVec.distanceTo(new Vec3(d, d2, d3));
                  if (distanceTo < d4) {
                     d4 = distanceTo;
                  }
               }
            }

            if (this.mc.gameSettings.thirdPersonView == 2) {
               GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotate(renderViewEntity.rotationPitch - rotationPitch, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(renderViewEntity.rotationYaw - rotationYaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.0F, (float)(-d4));
            GlStateManager.rotate(rotationYaw - renderViewEntity.rotationYaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(rotationPitch - renderViewEntity.rotationPitch, 1.0F, 0.0F, 0.0F);
         }
      } else {
         GlStateManager.translate(0.0F, 0.0F, -0.1F);
      }

      if (Reflector.EntityViewRenderEvent_CameraSetup_Constructor.exists()) {
         if (!this.mc.gameSettings.debugCamEnable) {
            float f = renderViewEntity.prevRotationYaw + (renderViewEntity.rotationYaw - renderViewEntity.prevRotationYaw) * partialTicks + 180.0F;
            float f2 = renderViewEntity.prevRotationPitch + (renderViewEntity.rotationPitch - renderViewEntity.prevRotationPitch) * partialTicks;
            float f3 = 0.0F;
            if (renderViewEntity instanceof EntityAnimal) {
               EntityAnimal entityAnimal = (EntityAnimal)renderViewEntity;
               f = entityAnimal.prevRotationYawHead + (entityAnimal.rotationYawHead - entityAnimal.prevRotationYawHead) * partialTicks + 180.0F;
            }

            Block blockAtEntityViewpoint = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, renderViewEntity, partialTicks);
            Object newInstance = Reflector.newInstance(Reflector.EntityViewRenderEvent_CameraSetup_Constructor, this, renderViewEntity, blockAtEntityViewpoint, partialTicks, f, f2, f3);
            Reflector.postForgeBusEvent(newInstance);
            f3 = Reflector.getFieldValueFloat(newInstance, Reflector.EntityViewRenderEvent_CameraSetup_roll, f3);
            f2 = Reflector.getFieldValueFloat(newInstance, Reflector.EntityViewRenderEvent_CameraSetup_pitch, f2);
            f = Reflector.getFieldValueFloat(newInstance, Reflector.EntityViewRenderEvent_CameraSetup_yaw, f);
            GlStateManager.rotate(f3, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(f2, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(f, 0.0F, 1.0F, 0.0F);
         }
      } else if (!this.mc.gameSettings.debugCamEnable) {
         GlStateManager.rotate(renderViewEntity.prevRotationPitch + (renderViewEntity.rotationPitch - renderViewEntity.prevRotationPitch) * partialTicks, 1.0F, 0.0F, 0.0F);
         if (renderViewEntity instanceof EntityAnimal) {
            EntityAnimal entityAnimal = (EntityAnimal)renderViewEntity;
            GlStateManager.rotate(entityAnimal.prevRotationYawHead + (entityAnimal.rotationYawHead - entityAnimal.prevRotationYawHead) * partialTicks + 180.0F, 0.0F, 1.0F, 0.0F);
         } else {
            GlStateManager.rotate(renderViewEntity.prevRotationYaw + (renderViewEntity.rotationYaw - renderViewEntity.prevRotationYaw) * partialTicks + 180.0F, 0.0F, 1.0F, 0.0F);
         }
      }

      GlStateManager.translate(0.0F, -eyeHeight, 0.0F);
      d = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * partialTicks;
      d2 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * partialTicks + eyeHeight;
      d3 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * partialTicks;
      this.cloudFog = this.mc.renderGlobal.hasCloudFog(d, d2, d3, partialTicks);
   }

   public void setupCameraTransform(float partialTicks, int pass) {
      this.farPlaneDistance = this.mc.gameSettings.renderDistanceChunks * 16;
      if (Config.isFogFancy()) {
         this.farPlaneDistance *= 0.95F;
      }

      if (Config.isFogFast()) {
         this.farPlaneDistance *= 0.83F;
      }

      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      float f = 0.07F;
      if (this.mc.gameSettings.anaglyph) {
         GlStateManager.translate(-(pass * 2 - 1) * f, 0.0F, 0.0F);
      }

      this.clipDistance = this.farPlaneDistance * 2.0F;
      if (this.clipDistance < 173.0F) {
         this.clipDistance = 173.0F;
      }

      if (this.cameraZoom != 1.0) {
         GlStateManager.translate((float)this.cameraYaw, (float)(-this.cameraPitch), 0.0F);
         GlStateManager.scale(this.cameraZoom, this.cameraZoom, 1.0);
      }

      Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      if (this.mc.gameSettings.anaglyph) {
         GlStateManager.translate((pass * 2 - 1) * 0.1F, 0.0F, 0.0F);
      }

      this.hurtCameraEffect(partialTicks);
      if (this.mc.gameSettings.viewBobbing) {
         this.process(partialTicks, "screen");
      }

      float f2 = this.mc.thePlayer.prevTimeInPortal + (this.mc.thePlayer.timeInPortal - this.mc.thePlayer.prevTimeInPortal) * partialTicks;
      if (f2 > 0.0F) {
         byte b = 20;
         if (this.mc.thePlayer.isPotionActive(Potion.confusion)) {
            b = 7;
         }

         float f3 = 5.0F / (f2 * f2 + 5.0F) - f2 * 0.04F;
         f3 *= f3;
         GlStateManager.rotate((this.rendererUpdateCount + partialTicks) * b, 0.0F, 1.0F, 1.0F);
         GlStateManager.scale(1.0F / f3, 1.0F, 1.0F);
         GlStateManager.rotate(-(this.rendererUpdateCount + partialTicks) * b, 0.0F, 1.0F, 1.0F);
      }

      if (CheatBreaker.getInstance().getModManager().freeLookHandler.active) {
         this.process(partialTicks);
      } else {
         this.orientCamera(partialTicks);
      }

      if (this.debugView) {
         switch (this.debugViewDirection) {
            case 0:
               GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
               break;
            case 1:
               GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
               break;
            case 2:
               GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
               break;
            case 3:
               GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
               break;
            case 4:
               GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
         }
      }
   }

   public void renderHand(float partialTicks, int xOffset) {
      this.renderHand(partialTicks, xOffset, true, true, false);
   }

   public void renderHand(float f, int n, boolean flag, boolean flag2, boolean flag3) {
      if (!this.debugView) {
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         float f2 = 0.07F;
         if (this.mc.gameSettings.anaglyph) {
            GlStateManager.translate(-(n * 2 - 1) * f2, 0.0F, 0.0F);
         }

         if (Config.isShaders()) {
            Shaders.applyHandDepth();
         }

         Project.gluPerspective(this.getFOVModifier(f, false), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.farPlaneDistance * 2.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         if (this.mc.gameSettings.anaglyph) {
            GlStateManager.translate((n * 2 - 1) * 0.1F, 0.0F, 0.0F);
         }

         boolean flag4 = false;
         if (flag) {
            GlStateManager.pushMatrix();
            this.hurtCameraEffect(f);
            if (this.mc.gameSettings.viewBobbing) {
               this.process(f, "hand");
            }

            flag4 = this.mc.getRenderViewEntity() instanceof EntityLivingBase && ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping();
            boolean flag5 = !ReflectorForge.renderFirstPersonHand(this.mc.renderGlobal, f, n);
            if (flag5 && this.mc.gameSettings.thirdPersonView == 0 && !flag4 && !this.mc.gameSettings.hideGUI && !this.mc.playerController.isSpectator()) {
               this.enableLightmap();
               if (Config.isShaders()) {
                  ShadersRender.renderItemFP(this.itemRenderer, f, flag3);
               } else {
                  this.itemRenderer.renderItemInFirstPerson(f);
               }

               this.disableLightmap();
            }

            GlStateManager.popMatrix();
         }

         if (!flag2) {
            return;
         }

         this.disableLightmap();
         if (this.mc.gameSettings.thirdPersonView == 0 && !flag4) {
            this.itemRenderer.renderOverlays(f);
            this.hurtCameraEffect(f);
         }

         if (this.mc.gameSettings.viewBobbing) {
            this.process(f, "hand");
         }
      }
   }

   public void disableLightmap() {
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.disableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      if (Config.isShaders()) {
         Shaders.disableLightmap();
      }
   }

   public void enableLightmap() {
      GlStateManager.setActiveTexture(OpenGlHelper.lightmapTexUnit);
      GlStateManager.matrixMode(5890);
      GlStateManager.loadIdentity();
      float f = 0.00390625F;
      GlStateManager.scale(f, f, f);
      GlStateManager.translate(8.0F, 8.0F, 8.0F);
      GlStateManager.matrixMode(5888);
      this.mc.getTextureManager().bindTexture(this.locationLightMap);
      GL11.glTexParameteri(3553, 10241, 9729);
      GL11.glTexParameteri(3553, 10240, 9729);
      GL11.glTexParameteri(3553, 10242, 33071);
      GL11.glTexParameteri(3553, 10243, 33071);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.enableTexture2D();
      GlStateManager.setActiveTexture(OpenGlHelper.defaultTexUnit);
      if (Config.isShaders()) {
         Shaders.enableLightmap();
      }
   }

   public void updateTorchFlicker() {
      this.torchFlickerDX = (float)(this.torchFlickerDX + (Math.random() - Math.random()) * Math.random() * Math.random());
      this.torchFlickerDX = (float)(this.torchFlickerDX * 0.9);
      this.torchFlickerX = this.torchFlickerX + (this.torchFlickerDX - this.torchFlickerX) * 1.0F;
      this.lightmapUpdateNeeded = true;
   }

   public void updateLightmap(float partialTicks) {
      if (this.lightmapUpdateNeeded) {
         this.mc.mcProfiler.startSection("lightTex");
         WorldClient theWorld = this.mc.theWorld;
         if (theWorld != null) {
            if (Config.isCustomColors()
               && CustomColors.updateLightmap(theWorld, this.torchFlickerX, this.lightmapColors, this.mc.thePlayer.isPotionActive(Potion.nightVision), partialTicks)
               )
             {
               this.lightmapTexture.updateDynamicTexture();
               this.lightmapUpdateNeeded = false;
               this.mc.mcProfiler.endSection();
               return;
            }

            float sunBrightness = theWorld.getSunBrightness(1.0F);
            float f = sunBrightness * 0.95F + 0.05F;

            for (int i = 0; i < this.computeInt(256); i++) {
               float f2 = theWorld.provider.getLightBrightnessTable()[i / 16] * f;
               float f3 = theWorld.provider.getLightBrightnessTable()[i % 16] * (this.torchFlickerX * 0.1F + 1.5F);
               if (theWorld.getLastLightningBolt() > 0) {
                  f2 = theWorld.provider.getLightBrightnessTable()[i / 16];
               }

               float f4 = f2 * (sunBrightness * 0.65F + 0.35F);
               float f5 = f2 * (sunBrightness * 0.65F + 0.35F);
               float f6 = f3 * ((f3 * 0.6F + 0.4F) * 0.6F + 0.4F);
               float f7 = f3 * (f3 * f3 * 0.6F + 0.4F);
               float f8 = f4 + f3;
               float f9 = f5 + f6;
               float f10 = f2 + f7;
               f8 = f8 * 0.96F + 0.03F;
               f9 = f9 * 0.96F + 0.03F;
               f10 = f10 * 0.96F + 0.03F;
               if (this.bossColorModifier > 0.0F) {
                  float f11 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
                  f8 = f8 * (1.0F - f11) + f8 * 0.7F * f11;
                  f9 = f9 * (1.0F - f11) + f9 * 0.6F * f11;
                  f10 = f10 * (1.0F - f11) + f10 * 0.6F * f11;
               }

               if (theWorld.provider.getDimensionId() == 1) {
                  f8 = 0.22F + f3 * 0.75F;
                  f9 = 0.28F + f6 * 0.75F;
                  f10 = 0.25F + f7 * 0.75F;
               }

               if (this.mc.thePlayer.isPotionActive(Potion.nightVision)) {
                  float nightVisionBrightness = this.getNightVisionBrightness(this.mc.thePlayer, partialTicks);
                  float f11 = 1.0F / f8;
                  if (f11 > 1.0F / f9) {
                     f11 = 1.0F / f9;
                  }

                  if (f11 > 1.0F / f10) {
                     f11 = 1.0F / f10;
                  }

                  f8 = f8 * (1.0F - nightVisionBrightness) + f8 * f11 * nightVisionBrightness;
                  f9 = f9 * (1.0F - nightVisionBrightness) + f9 * f11 * nightVisionBrightness;
                  f10 = f10 * (1.0F - nightVisionBrightness) + f10 * f11 * nightVisionBrightness;
               }

               if (f8 > 1.0F) {
                  f8 = 1.0F;
               }

               if (f9 > 1.0F) {
                  f9 = 1.0F;
               }

               if (f10 > 1.0F) {
                  f10 = 1.0F;
               }

               float gammaSetting = this.mc.gameSettings.gammaSetting;
               float f11 = 1.0F - f8;
               float f12 = 1.0F - f9;
               float f13 = 1.0F - f10;
               f11 = 1.0F - f11 * f11 * f11 * f11;
               f12 = 1.0F - f12 * f12 * f12 * f12;
               f13 = 1.0F - f13 * f13 * f13 * f13;
               f8 = f8 * (1.0F - gammaSetting) + f11 * gammaSetting;
               f9 = f9 * (1.0F - gammaSetting) + f12 * gammaSetting;
               f10 = f10 * (1.0F - gammaSetting) + f13 * gammaSetting;
               f8 = f8 * 0.96F + 0.03F;
               f9 = f9 * 0.96F + 0.03F;
               f10 = f10 * 0.96F + 0.03F;
               if (f8 > 1.0F) {
                  f8 = 1.0F;
               }

               if (f9 > 1.0F) {
                  f9 = 1.0F;
               }

               if (f10 > 1.0F) {
                  f10 = 1.0F;
               }

               if (f8 < 0.0F) {
                  f8 = 0.0F;
               }

               if (f9 < 0.0F) {
                  f9 = 0.0F;
               }

               if (f10 < 0.0F) {
                  f10 = 0.0F;
               }

               short s = 255;
               int n = (int)(f8 * 255.0F);
               int n2 = (int)(f9 * 255.0F);
               int n3 = (int)(f10 * 255.0F);
               this.lightmapColors[i] = s << 24 | n << 16 | n2 << 8 | n3;
            }

            this.lightmapTexture.updateDynamicTexture();
            this.lightmapUpdateNeeded = false;
            this.mc.mcProfiler.endSection();
         }
      }
   }

   public float getNightVisionBrightness(EntityLivingBase entitylivingbaseIn, float partialTicks) {
      int duration = entitylivingbaseIn.getActivePotionEffect(Potion.nightVision).getDuration();
      return duration > 200 ? 1.0F : 0.7F + MathHelper.sin((duration - partialTicks) * (float) Math.PI * 0.2F) * 0.3F;
   }

   public void updateCameraAndRender(float partialTicks, long nanoTime) {
      Config.renderPartialTicks = partialTicks;
      this.frameInit();
      boolean isActive = Display.isActive();
      if (!isActive && this.mc.gameSettings.pauseOnLostFocus && (!this.mc.gameSettings.touchscreen || !Mouse.isButtonDown(1))) {
         if (Minecraft.getSystemTime() - this.prevFrameTime > 500L) {
            this.mc.displayInGameMenu();
         }
      } else {
         this.prevFrameTime = Minecraft.getSystemTime();
      }

      this.mc.mcProfiler.startSection("mouse");
      if (isActive && Minecraft.isRunningOnMac && this.mc.inGameHasFocus && !Mouse.isInsideWindow()) {
         Mouse.setGrabbed(false);
         Mouse.setCursorPosition(Display.getWidth() / 2, Display.getHeight() / 2);
         Mouse.setGrabbed(true);
      }

      if (this.mc.inGameHasFocus && isActive) {
         this.mc.mouseHelper.mouseXYChange();
         float f = this.mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
         float f2 = f * f * f * 8.0F;
         float f3 = this.mc.mouseHelper.deltaX * f2;
         float f4 = this.mc.mouseHelper.deltaY * f2;
         byte b = 1;
         if (this.mc.gameSettings.invertMouse) {
            b = -1;
         }

         FreeLookHandler freeLookHandler = CheatBreaker.getInstance().getModManager().freeLookHandler;
         if (!freeLookHandler.active) {
            if (this.mc.gameSettings.smoothCamera) {
               this.smoothCamYaw += f3;
               this.smoothCamPitch += f4;
               float f5 = partialTicks - this.smoothCamPartialTicks;
               this.smoothCamPartialTicks = partialTicks;
               f3 = this.smoothCamFilterX * f5;
               f4 = this.smoothCamFilterY * f5;
               this.mc.thePlayer.setAngles(f3, f4 * b);
            } else {
               this.smoothCamYaw = 0.0F;
               this.smoothCamPitch = 0.0F;
               this.mc.thePlayer.setAngles(f3, f4 * b);
            }
         } else if (this.mc.gameSettings.smoothCamera) {
            this.smoothCamYaw += f3;
            this.smoothCamPitch += f4;
            float f5 = partialTicks - this.smoothCamPartialTicks;
            this.smoothCamPartialTicks = partialTicks;
            f3 = this.smoothCamFilterX * f5;
            f4 = this.smoothCamFilterY * f5;
            freeLookHandler.onMouseMoved(f3, f4 * b);
         } else {
            this.smoothCamYaw = 0.0F;
            this.smoothCamPitch = 0.0F;
            freeLookHandler.onMouseMoved(f3, f4 * b);
         }
      }

      this.mc.mcProfiler.endSection();
      if (!this.mc.skipRenderWorld) {
         anaglyphEnable = this.mc.gameSettings.anaglyph;
         final ScaledResolution scaledResolution = new ScaledResolution(this.mc);
         int scaledWidth = scaledResolution.getScaledWidth();
         int scaledHeight = scaledResolution.getScaledHeight();
         final int n = Mouse.getX() * scaledWidth / this.mc.displayWidth;
         final int n2 = scaledHeight - Mouse.getY() * scaledHeight / this.mc.displayHeight - 1;
         int limitFramerate = this.mc.gameSettings.limitFramerate;
         if (this.mc.theWorld != null) {
            this.mc.mcProfiler.startSection("level");
            int min = Math.min(Minecraft.getDebugFPS(), limitFramerate);
            min = Math.max(min, 60);
            long l = System.nanoTime() - nanoTime;
            long max = Math.max(1000000000 / min / 4 - l, 0L);
            this.renderWorld(partialTicks, System.nanoTime() + max);
            if (OpenGlHelper.shadersSupported) {
               this.mc.renderGlobal.renderEntityOutlineFramebuffer();
               if (this.theShaderGroup != null && this.useShader) {
                  GlStateManager.matrixMode(5890);
                  GlStateManager.pushMatrix();
                  GlStateManager.loadIdentity();
                  this.theShaderGroup.loadShaderGroup(partialTicks);
                  GlStateManager.popMatrix();
               }

               if (this.shaderGroup != null) {
                  GlStateManager.matrixMode(5890);
                  GlStateManager.pushMatrix();
                  GlStateManager.loadIdentity();
                  this.shaderGroup.loadShaderGroup(partialTicks);
                  GlStateManager.popMatrix();
               }

               this.mc.getFramebuffer().bindFramebuffer(true);
            }

            this.renderEndNanoTime = System.nanoTime();
            this.mc.mcProfiler.endStartSection("gui");
            if (!this.mc.gameSettings.hideGUI || this.mc.currentScreen != null) {
               GlStateManager.alphaFunc(516, 0.1F);
               this.mc.ingameGUI.renderGameOverlay(partialTicks);
               if (this.mc.gameSettings.ofShowFps && !this.mc.gameSettings.showDebugInfo) {
                  Config.drawFps();
               }

               if (this.mc.gameSettings.showDebugInfo) {
                  Lagometer.showLagometer(scaledResolution);
               }
            }

            this.mc.mcProfiler.endSection();
         } else {
            GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
            GlStateManager.matrixMode(5889);
            GlStateManager.loadIdentity();
            GlStateManager.matrixMode(5888);
            GlStateManager.loadIdentity();
            this.setupOverlayRendering();
            this.renderEndNanoTime = System.nanoTime();
            TileEntityRendererDispatcher.instance.renderEngine = this.mc.getTextureManager();
            TileEntityRendererDispatcher.instance.fontRenderer = this.mc.fontRendererObj;
         }

         if (this.mc.currentScreen != null) {
            GlStateManager.clear(256);

            try {
               if (Reflector.ForgeHooksClient_drawScreen.exists()) {
                  Reflector.callVoid(Reflector.ForgeHooksClient_drawScreen, this.mc.currentScreen, n, n2, partialTicks);
               } else {
                  this.mc.currentScreen.drawScreen(n, n2, partialTicks);
               }

               FriendsScreen.getFriendsScreen().renderNotifications();
            } catch (Throwable e) {
               CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Rendering screen");
               CrashReportCategory makeCategory = makeCrashReport.makeCategory("Screen render details");
               makeCategory.addCrashSectionCallable("Screen name", new Callable<String>() {
                  public String call() throws Exception {
                     return EntityRenderer.this.mc.currentScreen.getClass().getCanonicalName();
                  }
               });
               makeCategory.addCrashSectionCallable("Mouse location", new Callable<String>() {
                  public String call() throws Exception {
                     return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", n, n2, Mouse.getX(), Mouse.getY());
                  }
               });
               makeCategory.addCrashSectionCallable(
                  "Screen size",
                  new Callable<String>() {
                     public String call() throws Exception {
                        return String.format(
                           "Scaled: (%d, %d). Absolute: (%d, %d). Scale factor of %d",
                           scaledResolution.getScaledWidth(),
                           scaledResolution.getScaledHeight(),
                           EntityRenderer.this.mc.displayWidth,
                           EntityRenderer.this.mc.displayHeight,
                           scaledResolution.getScaleFactor()
                        );
                     }
                  }
               );
               throw new ReportedException(makeCrashReport);
            }
         }
      }

      this.frameFinish();
      this.waitForServerThread();
      MemoryMonitor.update();
      Lagometer.updateLagometer();
      if (this.mc.gameSettings.ofProfiler) {
         this.mc.gameSettings.showDebugProfilerChart = true;
      }
   }

   public boolean isDrawBlockOutline() {
      if (!this.drawBlockOutline) {
         return false;
      }

      Entity renderViewEntity = this.mc.getRenderViewEntity();
      boolean flag = renderViewEntity instanceof EntityPlayer && !this.mc.gameSettings.hideGUI;
      if (flag && !((EntityPlayer)renderViewEntity).capabilities.allowEdit) {
         ItemStack currentEquippedItem = ((EntityPlayer)renderViewEntity).getCurrentEquippedItem();
         if (this.mc.objectMouseOver != null && this.mc.objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
            BlockPos blockPos = this.mc.objectMouseOver.getBlockPos();
            IBlockState blockState = this.mc.theWorld.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (this.mc.playerController.getCurrentGameType() == GameType.SPECTATOR) {
               flag = ReflectorForge.blockHasTileEntity(blockState) && this.mc.theWorld.getTileEntity(blockPos) instanceof IInventory;
            } else {
               flag = currentEquippedItem != null && (currentEquippedItem.canDestroy(block) || currentEquippedItem.canPlaceOn(block));
            }
         }
      }

      return flag;
   }

   public void renderWorldDirections(float partialTicks) {
      if (this.mc.gameSettings.showDebugInfo && !this.mc.gameSettings.hideGUI && !this.mc.thePlayer.hasReducedDebug() && !this.mc.gameSettings.reducedDebugInfo
         )
       {
         Entity renderViewEntity = this.mc.getRenderViewEntity();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GL11.glLineWidth(1.0F);
         GlStateManager.disableTexture2D();
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         GlStateManager.matrixMode(5888);
         GlStateManager.loadIdentity();
         if (CheatBreaker.getInstance().getModManager().freeLookHandler.active) {
            this.process(partialTicks);
         } else {
            this.orientCamera(partialTicks);
         }

         float eyeHeight = renderViewEntity.getEyeHeight();
         FloatEvent2 floatEvent2 = new FloatEvent2(eyeHeight, partialTicks);
         CheatBreaker.getInstance().getEventBus().handleEvent(floatEvent2);
         eyeHeight = floatEvent2.getEyeHeight();
         GlStateManager.translate(0.0F, eyeHeight, 0.0F);
         RenderGlobal.drawOutlinedBoundingBox(new AxisAlignedBB(0.0, 0.0, 0.0, 0.005, 1.0E-4, 1.0E-4), 255, 0, 0, 255);
         RenderGlobal.drawOutlinedBoundingBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0E-4, 1.0E-4, 0.005), 0, 0, 255, 255);
         RenderGlobal.drawOutlinedBoundingBox(new AxisAlignedBB(0.0, 0.0, 0.0, 1.0E-4, 0.0033, 1.0E-4), 0, 255, 0, 255);
         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
         GlStateManager.enableTexture2D();
         GlStateManager.disableBlend();
      }
   }

   public void renderWorld(float partialTicks, long finishTimeNano) {
      this.updateLightmap(partialTicks);
      if (this.mc.getRenderViewEntity() == null) {
         this.mc.setRenderViewEntity(this.mc.thePlayer);
      }

      this.getMouseOver(partialTicks);
      if (Config.isShaders()) {
         Shaders.beginRender(this.mc, partialTicks, finishTimeNano);
      }

      GlStateManager.enableDepth();
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(516, 0.1F);
      this.mc.mcProfiler.startSection("center");
      if (this.mc.gameSettings.anaglyph) {
         anaglyphField = 0;
         GlStateManager.colorMask(false, true, true, false);
         this.renderWorldPass(0, partialTicks, finishTimeNano);
         anaglyphField = 1;
         GlStateManager.colorMask(true, false, false, false);
         this.renderWorldPass(1, partialTicks, finishTimeNano);
         GlStateManager.colorMask(true, true, true, false);
      } else {
         this.renderWorldPass(2, partialTicks, finishTimeNano);
      }

      this.mc.mcProfiler.endSection();
   }

   public void renderWorldPass(int pass, float partialTicks, long finishTimeNano) {
      boolean isShaders = Config.isShaders();
      if (isShaders) {
         Shaders.beginRenderPass(pass, partialTicks, finishTimeNano);
      }

      RenderGlobal renderGlobal = this.mc.renderGlobal;
      EffectRenderer effectRenderer = this.mc.effectRenderer;
      boolean isDrawBlockOutline = this.isDrawBlockOutline();
      GlStateManager.enableCull();
      this.mc.mcProfiler.endStartSection("clear");
      if (isShaders) {
         Shaders.setViewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      } else {
         GlStateManager.viewport(0, 0, this.mc.displayWidth, this.mc.displayHeight);
      }

      this.updateFogColor(partialTicks);
      GlStateManager.clear(16640);
      if (isShaders) {
         Shaders.clearRenderBuffer();
      }

      this.mc.mcProfiler.endStartSection("camera");
      this.setupCameraTransform(partialTicks, pass);
      if (isShaders) {
         Shaders.setCamera(partialTicks);
      }

      ActiveRenderInfo.updateRenderInfo(this.mc.thePlayer, this.mc.gameSettings.thirdPersonView == 2);
      this.mc.mcProfiler.endStartSection("frustum");
      ClippingHelper instance = ClippingHelperImpl.getInstance();
      this.mc.mcProfiler.endStartSection("culling");
      instance.disabled = Config.isShaders() && !Shaders.isFrustumCulling();
      Frustum frustum = new Frustum(instance);
      Entity renderViewEntity = this.mc.getRenderViewEntity();
      double d = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * partialTicks;
      double d2 = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * partialTicks;
      double d3 = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * partialTicks;
      if (isShaders) {
         ShadersRender.setFrustrumPosition(frustum, d, d2, d3);
      } else {
         frustum.setPosition(d, d2, d3);
      }

      if ((Config.isSkyEnabled() || Config.isSunMoonEnabled() || Config.isStarsEnabled()) && !Shaders.isShadowPass) {
         this.setupFog(-1, partialTicks);
         this.mc.mcProfiler.endStartSection("sky");
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance);
         GlStateManager.matrixMode(5888);
         if (isShaders) {
            Shaders.beginSky();
         }

         renderGlobal.renderSky(partialTicks, pass);
         if (isShaders) {
            Shaders.endSky();
         }

         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance);
         GlStateManager.matrixMode(5888);
      } else {
         GlStateManager.disableBlend();
      }

      this.setupFog(0, partialTicks);
      GlStateManager.shadeModel(7425);
      if (renderViewEntity.posY + renderViewEntity.getEyeHeight() < 128.0 + this.mc.gameSettings.ofCloudsHeight * 128.0F) {
         this.renderCloudsCheck(renderGlobal, partialTicks, pass);
      }

      this.mc.mcProfiler.endStartSection("prepareterrain");
      this.setupFog(0, partialTicks);
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      RenderHelper.disableStandardItemLighting();
      this.mc.mcProfiler.endStartSection("terrain_setup");
      this.checkLoadVisibleChunks(renderViewEntity, partialTicks, frustum, this.mc.thePlayer.isSpectator());
      if (isShaders) {
         ShadersRender.setupTerrain(renderGlobal, renderViewEntity, partialTicks, frustum, this.frameCount++, this.mc.thePlayer.isSpectator());
      } else {
         renderGlobal.setupTerrain(renderViewEntity, partialTicks, frustum, this.frameCount++, this.mc.thePlayer.isSpectator());
      }

      if (pass == 0 || pass == 2) {
         this.mc.mcProfiler.endStartSection("updatechunks");
         Lagometer.timerChunkUpload.start();
         this.mc.renderGlobal.updateChunks(finishTimeNano);
         Lagometer.timerChunkUpload.end();
      }

      this.mc.mcProfiler.endStartSection("terrain");
      Lagometer.timerTerrain.start();
      if (this.mc.gameSettings.ofSmoothFps && pass > 0) {
         this.mc.mcProfiler.endStartSection("finish");
         GL11.glFinish();
         this.mc.mcProfiler.endStartSection("terrain");
      }

      GlStateManager.matrixMode(5888);
      GlStateManager.pushMatrix();
      GlStateManager.disableAlpha();
      if (isShaders) {
         ShadersRender.beginTerrainSolid();
      }

      renderGlobal.renderBlockLayer(EnumWorldBlockLayer.SOLID, partialTicks, pass, renderViewEntity);
      GlStateManager.enableAlpha();
      if (isShaders) {
         ShadersRender.beginTerrainCutoutMipped();
      }

      this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, this.mc.gameSettings.mipmapLevels > 0);
      renderGlobal.renderBlockLayer(EnumWorldBlockLayer.CUTOUT_MIPPED, partialTicks, pass, renderViewEntity);
      this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
      this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
      if (isShaders) {
         ShadersRender.beginTerrainCutout();
      }

      renderGlobal.renderBlockLayer(EnumWorldBlockLayer.CUTOUT, partialTicks, pass, renderViewEntity);
      this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
      if (isShaders) {
         ShadersRender.endTerrain();
      }

      Lagometer.timerTerrain.end();
      GlStateManager.shadeModel(7424);
      GlStateManager.alphaFunc(516, 0.1F);
      if (!this.debugView) {
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         RenderHelper.enableStandardItemLighting();
         this.mc.mcProfiler.endStartSection("entities");
         if (Reflector.ForgeHooksClient_setRenderPass.exists()) {
            Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, 0);
         }

         renderGlobal.renderEntities(renderViewEntity, frustum, partialTicks);
         if (Reflector.ForgeHooksClient_setRenderPass.exists()) {
            Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, -1);
         }

         RenderHelper.disableStandardItemLighting();
         this.disableLightmap();
         GlStateManager.matrixMode(5888);
         GlStateManager.popMatrix();
         GlStateManager.pushMatrix();
         if (this.mc.objectMouseOver != null && renderViewEntity.isInsideOfMaterial(Material.water) && isDrawBlockOutline) {
            EntityPlayer entityPlayer = (EntityPlayer)renderViewEntity;
            GlStateManager.disableAlpha();
            this.mc.mcProfiler.endStartSection("outline");
            renderGlobal.drawSelectionBox(entityPlayer, this.mc.objectMouseOver, 0, partialTicks);
            GlStateManager.enableAlpha();
         }
      }

      GlStateManager.matrixMode(5888);
      GlStateManager.popMatrix();
      if (isDrawBlockOutline && this.mc.objectMouseOver != null && !renderViewEntity.isInsideOfMaterial(Material.water)) {
         EntityPlayer entityPlayer = (EntityPlayer)renderViewEntity;
         GlStateManager.disableAlpha();
         this.mc.mcProfiler.endStartSection("outline");
         if ((
               !Reflector.ForgeHooksClient_onDrawBlockHighlight.exists()
                  || !Reflector.callBoolean(
                     Reflector.ForgeHooksClient_onDrawBlockHighlight, renderGlobal, entityPlayer, this.mc.objectMouseOver, 0, entityPlayer.getHeldItem(), partialTicks
                  )
            )
            && !this.mc.gameSettings.hideGUI) {
            renderGlobal.drawSelectionBox(entityPlayer, this.mc.objectMouseOver, 0, partialTicks);
         }

         GlStateManager.enableAlpha();
      }

      if (!renderGlobal.damagedBlocks.isEmpty()) {
         this.mc.mcProfiler.endStartSection("destroyProgress");
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
         this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).setBlurMipmap(false, false);
         renderGlobal.drawBlockDamageTexture(Tessellator.getInstance(), Tessellator.getInstance().getWorldRenderer(), renderViewEntity, partialTicks);
         this.mc.getTextureManager().getTexture(TextureMap.locationBlocksTexture).restoreLastBlurMipmap();
         GlStateManager.disableBlend();
      }

      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.disableBlend();
      if (!this.debugView) {
         this.enableLightmap();
         this.mc.mcProfiler.endStartSection("litParticles");
         if (isShaders) {
            Shaders.beginLitParticles();
         }

         effectRenderer.renderLitParticles(renderViewEntity, partialTicks);
         RenderHelper.disableStandardItemLighting();
         this.setupFog(0, partialTicks);
         this.mc.mcProfiler.endStartSection("particles");
         if (isShaders) {
            Shaders.beginParticles();
         }

         effectRenderer.renderParticles(renderViewEntity, partialTicks);
         if (isShaders) {
            Shaders.endParticles();
         }

         this.disableLightmap();
      }

      GlStateManager.depthMask(false);
      if (Config.isShaders()) {
         GlStateManager.depthMask(Shaders.isRainDepth());
      }

      GlStateManager.enableCull();
      this.mc.mcProfiler.endStartSection("weather");
      if (isShaders) {
         Shaders.beginWeather();
      }

      this.renderRainSnow(partialTicks);
      if (isShaders) {
         Shaders.endWeather();
      }

      GlStateManager.depthMask(true);
      renderGlobal.renderWorldBorder(renderViewEntity, partialTicks);
      if (isShaders) {
         ShadersRender.renderHand0(this, partialTicks, pass);
         Shaders.preWater();
      }

      CheatBreaker.getInstance().getEventBus().handleEvent(new FloatEvent6(partialTicks));
      GlStateManager.disableBlend();
      GlStateManager.enableCull();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      GlStateManager.alphaFunc(516, 0.1F);
      this.setupFog(0, partialTicks);
      GlStateManager.enableBlend();
      GlStateManager.depthMask(false);
      this.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
      GlStateManager.shadeModel(7425);
      this.mc.mcProfiler.endStartSection("translucent");
      if (isShaders) {
         Shaders.beginWater();
      }

      renderGlobal.renderBlockLayer(EnumWorldBlockLayer.TRANSLUCENT, partialTicks, pass, renderViewEntity);
      if (isShaders) {
         Shaders.endWater();
      }

      if (CheatBreaker.getInstance().getModManager().nametags.isEnabled()) {
         ((Buffer)CheatBreaker.getInstance().getModManager().nametags.modelviewBuffer).rewind();
         GL11.glGetFloat(2982, CheatBreaker.getInstance().getModManager().nametags.modelviewBuffer);
         ((Buffer)CheatBreaker.getInstance().getModManager().nametags.projectionBuffer).rewind();
         GL11.glGetFloat(2983, CheatBreaker.getInstance().getModManager().nametags.projectionBuffer);
      }

      if (CheatBreaker.getInstance().getModManager().teamManager.isFlag()) {
         ((Buffer)CheatBreaker.getInstance().getModManager().teamManager.floatBuffer).rewind();
         GL11.glGetFloat(2982, CheatBreaker.getInstance().getModManager().teamManager.floatBuffer);
         ((Buffer)CheatBreaker.getInstance().getModManager().teamManager.floatBuffer2).rewind();
         GL11.glGetFloat(2983, CheatBreaker.getInstance().getModManager().teamManager.floatBuffer2);
      }

      if (Reflector.ForgeHooksClient_setRenderPass.exists() && !this.debugView) {
         RenderHelper.enableStandardItemLighting();
         this.mc.mcProfiler.endStartSection("entities");
         Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, 1);
         this.mc.renderGlobal.renderEntities(renderViewEntity, frustum, partialTicks);
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         Reflector.callVoid(Reflector.ForgeHooksClient_setRenderPass, -1);
         RenderHelper.disableStandardItemLighting();
      }

      GlStateManager.shadeModel(7424);
      GlStateManager.depthMask(true);
      GlStateManager.enableCull();
      GlStateManager.disableBlend();
      GlStateManager.disableFog();
      if (renderViewEntity.posY + renderViewEntity.getEyeHeight() >= 128.0 + this.mc.gameSettings.ofCloudsHeight * 128.0F) {
         this.mc.mcProfiler.endStartSection("aboveClouds");
         this.renderCloudsCheck(renderGlobal, partialTicks, pass);
      }

      if (Reflector.ForgeHooksClient_dispatchRenderLast.exists()) {
         this.mc.mcProfiler.endStartSection("forge_render_last");
         Reflector.callVoid(Reflector.ForgeHooksClient_dispatchRenderLast, renderGlobal, partialTicks);
      }

      this.mc.mcProfiler.endStartSection("hand");
      if (this.renderHand && !Shaders.isShadowPass) {
         if (isShaders) {
            ShadersRender.renderHand1(this, partialTicks, pass);
            Shaders.renderCompositeFinal();
         }

         GlStateManager.clear(256);
         if (isShaders) {
            ShadersRender.renderFPOverlay(this, partialTicks, pass);
         } else {
            this.renderHand(partialTicks, pass);
         }

         this.renderWorldDirections(partialTicks);
      }

      if (isShaders) {
         Shaders.endRender();
      }
   }

   public void renderCloudsCheck(RenderGlobal renderGlobalIn, float partialTicks, int pass) {
      if (this.mc.gameSettings.renderDistanceChunks >= 4 && !Config.isCloudsOff() && Shaders.shouldRenderClouds(this.mc.gameSettings)) {
         this.mc.mcProfiler.endStartSection("clouds");
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance * 4.0F);
         GlStateManager.matrixMode(5888);
         GlStateManager.pushMatrix();
         this.setupFog(0, partialTicks);
         renderGlobalIn.renderClouds(partialTicks, pass);
         GlStateManager.disableFog();
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5889);
         GlStateManager.loadIdentity();
         Project.gluPerspective(this.getFOVModifier(partialTicks, true), (float)this.mc.displayWidth / this.mc.displayHeight, 0.05F, this.clipDistance);
         GlStateManager.matrixMode(5888);
      }
   }

   public void addRainParticles() {
      SimpleEvent46 simpleEvent46 = new SimpleEvent46();
      SimpleEvent47 simpleEvent47 = new SimpleEvent47();
      CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent46);
      CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent47);
      if (!simpleEvent46.isFlag()) {
         float rainStrength = this.mc.theWorld.getRainStrength(1.0F);
         if (!Config.isRainFancy()) {
            rainStrength /= 2.0F;
         }

         if (rainStrength != 0.0F && Config.isRainSplash()) {
            this.random.setSeed(this.rendererUpdateCount * 312987231L);
            Entity renderViewEntity = this.mc.getRenderViewEntity();
            WorldClient theWorld = this.mc.theWorld;
            BlockPos blockPos = new BlockPos(renderViewEntity);
            byte b = 10;
            double d = 0.0;
            double d2 = 0.0;
            double d3 = 0.0;
            int n = 0;
            int n2 = (int)(100.0F * rainStrength * rainStrength);
            if (this.mc.gameSettings.particleSetting == 1) {
               n2 >>= 1;
            } else if (this.mc.gameSettings.particleSetting == 2) {
               n2 = 0;
            }

            for (int i = 0; i < n2; i++) {
               BlockPos precipitationHeight = theWorld.getPrecipitationHeight(
                  blockPos.add(this.random.nextInt(b) - this.random.nextInt(b), 0, this.random.nextInt(b) - this.random.nextInt(b))
               );
               BiomeGenBase biomeGenForCoords = theWorld.getBiomeGenForCoords(precipitationHeight);
               BlockPos down = precipitationHeight.down();
               Block block = theWorld.getBlockState(down).getBlock();
               if (precipitationHeight.getY() <= blockPos.getY() + b && precipitationHeight.getY() >= blockPos.getY() - b && biomeGenForCoords.canRain() && biomeGenForCoords.getFloatTemperature(precipitationHeight) >= 0.15F
                  || simpleEvent47.isFlag()) {
                  double nextDouble = this.random.nextDouble();
                  double nextDouble2 = this.random.nextDouble();
                  if (block.getMaterial() == Material.lava) {
                     this.mc
                        .theWorld
                        .spawnParticle(
                           EnumParticleTypes.SMOKE_NORMAL,
                           precipitationHeight.getX() + nextDouble,
                           precipitationHeight.getY() + 0.1F - block.getBlockBoundsMinY(),
                           precipitationHeight.getZ() + nextDouble2,
                           0.0,
                           0.0,
                           0.0
                        );
                  } else if (block.getMaterial() != Material.air) {
                     block.setBlockBoundsBasedOnState(theWorld, down);
                     if (this.random.nextInt(++n) == 0) {
                        d = down.getX() + nextDouble;
                        d2 = down.getY() + 0.1F + block.getBlockBoundsMaxY() - 1.0;
                        d3 = down.getZ() + nextDouble2;
                     }

                     this.mc
                        .theWorld
                        .spawnParticle(
                           EnumParticleTypes.WATER_DROP,
                           down.getX() + nextDouble,
                           down.getY() + 0.1F + block.getBlockBoundsMaxY(),
                           down.getZ() + nextDouble2,
                           0.0,
                           0.0,
                           0.0
                        );
                  }
               }
            }

            if (n > 0 && this.random.nextInt(3) < this.rainSoundCounter++) {
               this.rainSoundCounter = 0;
               if (d2 > blockPos.getY() + 1 && theWorld.getPrecipitationHeight(blockPos).getY() > MathHelper.floor_float(blockPos.getY())) {
                  this.mc.theWorld.playSound(d, d2, d3, "ambient.weather.rain", 0.1F, 0.5F, false);
               } else {
                  this.mc.theWorld.playSound(d, d2, d3, "ambient.weather.rain", 0.2F, 1.0F, false);
               }
            }
         }
      }
   }

   public void renderRainSnow(float partialTicks) {
      EnvironmentChangerMod environmentChangerMod = CheatBreaker.getInstance().getModManager().environmentChangerMod;
      boolean booleanValue = environmentChangerMod.allowThroughBlocks.getBooleanValue();
      if (Reflector.ForgeWorldProvider_getWeatherRenderer.exists()) {
         WorldProvider provider = this.mc.theWorld.provider;
         Object obj = Reflector.call(provider, Reflector.ForgeWorldProvider_getWeatherRenderer);
         if (obj != null) {
            Reflector.callVoid(obj, Reflector.IRenderHandler_render, partialTicks, this.mc.theWorld, this.mc);
            return;
         }
      }

      float rainStrength = this.mc.theWorld.getRainStrength(partialTicks);
      if (environmentChangerMod.isEnabled() && environmentChangerMod.customWeather.getBooleanValue() && !environmentChangerMod.currentWeather.getStringValue().equalsIgnoreCase("Clear")) {
         rainStrength = 1.0F;
      }

      if (rainStrength > 0.0F) {
         if (environmentChangerMod.isEnabled() && environmentChangerMod.customWeather.getBooleanValue() && environmentChangerMod.currentWeather.getStringValue().equals("Clear")) {
            return;
         }

         if (Config.isRainOff()) {
            return;
         }

         this.enableLightmap();
         Entity renderViewEntity = this.mc.getRenderViewEntity();
         WorldClient theWorld = this.mc.theWorld;
         int n = MathHelper.floor_double(renderViewEntity.posX);
         int n2 = MathHelper.floor_double(renderViewEntity.posY);
         int n3 = MathHelper.floor_double(renderViewEntity.posZ);
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         GlStateManager.disableCull();
         GL11.glNormal3f(0.0F, 1.0F, 0.0F);
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.alphaFunc(516, 0.1F);
         double d = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * partialTicks;
         double d2 = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * partialTicks;
         double d3 = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * partialTicks;
         int n4 = MathHelper.floor_double(d2);
         byte b = 5;
         if (Config.isRainFancy()) {
            b = 10;
         }

         byte b2 = -1;
         float f = this.rendererUpdateCount + partialTicks;
         worldRenderer.setTranslation(-d, -d2, -d3);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         MutableBlockPos mutableBlockPos = new MutableBlockPos();

         for (int i = n3 - b; i <= n3 + b; i++) {
            for (int j = n - b; j <= n + b; j++) {
               int n5 = (i - n3 + 16) * 32 + j - n + 16;
               double d4 = this.rainXCoords[n5] * 0.5;
               double d5 = this.rainYCoords[n5] * 0.5;
               mutableBlockPos.set(j, 0, i);
               BiomeGenBase biomeGenForCoords = theWorld.getBiomeGenForCoords(mutableBlockPos);
               if (biomeGenForCoords.canRain() || biomeGenForCoords.getEnableSnow()) {
                  int n6 = theWorld.getPrecipitationHeight(mutableBlockPos).getY();
                  int n7 = n2 - b;
                  int n8 = n2 + b;
                  if (n7 < n6) {
                     n7 = environmentChangerMod.isEnabled() && booleanValue ? 0 : n6;
                  }

                  if (n8 < n6) {
                     n8 = n6;
                  }

                  int n9 = n6;
                  if (n6 < n4) {
                     n9 = n4;
                  }

                  if (n7 != n8 || environmentChangerMod.isEnabled() && environmentChangerMod.customWeather.getBooleanValue()) {
                     this.random.setSeed(j * j * 3121 + j * 45238971 ^ i * i * 418711 + i * 13761);
                     mutableBlockPos.set(j, n7, i);
                     float floatTemperature = biomeGenForCoords.getFloatTemperature(mutableBlockPos);
                     boolean flag;
                     if (environmentChangerMod.customWeather.getBooleanValue() && environmentChangerMod.isEnabled() && !environmentChangerMod.currentWeather.getStringValue().equals("Clear")) {
                        flag = environmentChangerMod.currentWeather.getStringValue().equals("Rain");
                     } else {
                        flag = theWorld.getWorldChunkManager().getTemperatureAtHeight(floatTemperature, n6) >= 0.15F;
                     }

                     if (flag) {
                        if (b2 != 0) {
                           if (b2 >= 0) {
                              instance.draw();
                           }

                           b2 = 0;
                           this.mc.getTextureManager().bindTexture(locationRainPng);
                           worldRenderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                        }

                        double d6 = (
                              (double)(this.rendererUpdateCount + j * j * 3121 + j * 45238971 + i * i * 418711 + i * 13761 & 31)
                                 + partialTicks
                           )
                           / 32.0
                           * (3.0 + this.random.nextDouble());
                        double d7 = j + 0.5F - renderViewEntity.posX;
                        double d8 = i + 0.5F - renderViewEntity.posZ;
                        float f2 = MathHelper.sqrt_double(d7 * d7 + d8 * d8) / b;
                        float f3 = ((1.0F - f2 * f2) * 0.5F + 0.5F) * rainStrength;
                        mutableBlockPos.set(j, n9, i);
                        int combinedLight = theWorld.getCombinedLight(mutableBlockPos, 0);
                        int n10 = combinedLight >> 16 & 65535;
                        int n11 = combinedLight & 65535;
                        worldRenderer.pos(j - d4 + 0.5, n7, i - d5 + 0.5)
                           .tex(0.0, n7 * 0.25 + d6)
                           .color(1.0F, 1.0F, 1.0F, f3)
                           .lightmap(n10, n11)
                           .endVertex();
                        worldRenderer.pos(j + d4 + 0.5, n7, i + d5 + 0.5)
                           .tex(1.0, n7 * 0.25 + d6)
                           .color(1.0F, 1.0F, 1.0F, f3)
                           .lightmap(n10, n11)
                           .endVertex();
                        worldRenderer.pos(j + d4 + 0.5, n8, i + d5 + 0.5)
                           .tex(1.0, n8 * 0.25 + d6)
                           .color(1.0F, 1.0F, 1.0F, f3)
                           .lightmap(n10, n11)
                           .endVertex();
                        worldRenderer.pos(j - d4 + 0.5, n8, i - d5 + 0.5)
                           .tex(0.0, n8 * 0.25 + d6)
                           .color(1.0F, 1.0F, 1.0F, f3)
                           .lightmap(n10, n11)
                           .endVertex();
                     } else if (theWorld.getWorldChunkManager().getTemperatureAtHeight(floatTemperature, n6) < 0.15F
                        || environmentChangerMod.currentWeather.getStringValue().equals("Snow") && environmentChangerMod.customWeather.getBooleanValue() && environmentChangerMod.isEnabled()) {
                        if (b2 != 1) {
                           if (b2 >= 0) {
                              instance.draw();
                           }

                           b2 = 1;
                           this.mc.getTextureManager().bindTexture(locationSnowPng);
                           worldRenderer.begin(7, DefaultVertexFormats.PARTICLE_POSITION_TEX_COLOR_LMAP);
                        }

                        double d6 = ((this.rendererUpdateCount & 511) + partialTicks) / 512.0F;
                        double d7 = this.random.nextDouble() + f * 0.01 * (float)this.random.nextGaussian();
                        double d8 = this.random.nextDouble() + f * (float)this.random.nextGaussian() * 0.001;
                        double d9 = j + 0.5F - renderViewEntity.posX;
                        double d10 = i + 0.5F - renderViewEntity.posZ;
                        float f2 = MathHelper.sqrt_double(d9 * d9 + d10 * d10) / b;
                        float f3 = ((1.0F - f2 * f2) * 0.3F + 0.5F) * rainStrength;
                        mutableBlockPos.set(j, n9, i);
                        int n10 = (theWorld.getCombinedLight(mutableBlockPos, 0) * 3 + 15728880) / 4;
                        int n11 = n10 >> 16 & 65535;
                        int n12 = n10 & 65535;
                        worldRenderer.pos(j - d4 + 0.5, n7, i - d5 + 0.5)
                           .tex(0.0 + d7, n7 * 0.25 + d6 + d8)
                           .color(1.0F, 1.0F, 1.0F, f3)
                           .lightmap(n11, n12)
                           .endVertex();
                        worldRenderer.pos(j + d4 + 0.5, n7, i + d5 + 0.5)
                           .tex(1.0 + d7, n7 * 0.25 + d6 + d8)
                           .color(1.0F, 1.0F, 1.0F, f3)
                           .lightmap(n11, n12)
                           .endVertex();
                        worldRenderer.pos(j + d4 + 0.5, n8, i + d5 + 0.5)
                           .tex(1.0 + d7, n8 * 0.25 + d6 + d8)
                           .color(1.0F, 1.0F, 1.0F, f3)
                           .lightmap(n11, n12)
                           .endVertex();
                        worldRenderer.pos(j - d4 + 0.5, n8, i - d5 + 0.5)
                           .tex(0.0 + d7, n8 * 0.25 + d6 + d8)
                           .color(1.0F, 1.0F, 1.0F, f3)
                           .lightmap(n11, n12)
                           .endVertex();
                     }
                  }
               }
            }
         }

         if (b2 >= 0) {
            instance.draw();
         }

         worldRenderer.setTranslation(0.0, 0.0, 0.0);
         GlStateManager.enableCull();
         GlStateManager.disableBlend();
         GlStateManager.alphaFunc(516, 0.1F);
         this.disableLightmap();
      }
   }

   public void setupOverlayRendering() {
      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      GlStateManager.clear(256);
      GlStateManager.matrixMode(5889);
      GlStateManager.loadIdentity();
      GlStateManager.ortho(0.0, scaledResolution.getScaledWidth_double(), scaledResolution.getScaledHeight_double(), 0.0, 1000.0, 3000.0);
      GlStateManager.matrixMode(5888);
      GlStateManager.loadIdentity();
      GlStateManager.translate(0.0F, 0.0F, -2000.0F);
   }

   public void updateFogColor(float partialTicks) {
      WorldClient theWorld = this.mc.theWorld;
      Entity renderViewEntity = this.mc.getRenderViewEntity();
      float f = 0.25F + 0.75F * this.mc.gameSettings.renderDistanceChunks / 32.0F;
      f = 1.0F - (float)Math.pow(f, 0.25);
      Vec3 skyColor = theWorld.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
      skyColor = CustomColors.getWorldSkyColor(skyColor, theWorld, this.mc.getRenderViewEntity(), partialTicks);
      float xCoord = (float)skyColor.xCoord;
      float yCoord = (float)skyColor.yCoord;
      float zCoord = (float)skyColor.zCoord;
      Vec3 fogColor = theWorld.getFogColor(partialTicks);
      fogColor = CustomColors.getWorldFogColor(fogColor, theWorld, this.mc.getRenderViewEntity(), partialTicks);
      this.fogColorRed = (float)fogColor.xCoord;
      this.fogColorGreen = (float)fogColor.yCoord;
      this.fogColorBlue = (float)fogColor.zCoord;
      if (this.mc.gameSettings.renderDistanceChunks >= 4) {
         double d = -1.0;
         Vec3 vec3 = MathHelper.sin(theWorld.getCelestialAngleRadians(partialTicks)) > 0.0F ? new Vec3(d, 0.0, 0.0) : new Vec3(1.0, 0.0, 0.0);
         float dotProduct = (float)renderViewEntity.getLook(partialTicks).dotProduct(vec3);
         if (dotProduct < 0.0F) {
            dotProduct = 0.0F;
         }

         if (dotProduct > 0.0F) {
            float[] calcSunriseSunsetColors = theWorld.provider.calcSunriseSunsetColors(theWorld.getCelestialAngle(partialTicks), partialTicks);
            if (calcSunriseSunsetColors != null) {
               dotProduct *= calcSunriseSunsetColors[3];
               this.fogColorRed = this.fogColorRed * (1.0F - dotProduct) + calcSunriseSunsetColors[0] * dotProduct;
               this.fogColorGreen = this.fogColorGreen * (1.0F - dotProduct) + calcSunriseSunsetColors[1] * dotProduct;
               this.fogColorBlue = this.fogColorBlue * (1.0F - dotProduct) + calcSunriseSunsetColors[2] * dotProduct;
            }
         }
      }

      this.fogColorRed = this.fogColorRed + (xCoord - this.fogColorRed) * f;
      this.fogColorGreen = this.fogColorGreen + (yCoord - this.fogColorGreen) * f;
      this.fogColorBlue = this.fogColorBlue + (zCoord - this.fogColorBlue) * f;
      float rainStrength = theWorld.getRainStrength(partialTicks);
      if (rainStrength > 0.0F) {
         float f2 = 1.0F - rainStrength * 0.5F;
         float f3 = 1.0F - rainStrength * 0.4F;
         this.fogColorRed *= f2;
         this.fogColorGreen *= f2;
         this.fogColorBlue *= f3;
      }

      float thunderStrength = theWorld.getThunderStrength(partialTicks);
      if (thunderStrength > 0.0F) {
         float f2 = 1.0F - thunderStrength * 0.5F;
         this.fogColorRed *= f2;
         this.fogColorGreen *= f2;
         this.fogColorBlue *= f2;
      }

      Block blockAtEntityViewpoint = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, renderViewEntity, partialTicks);
      if (this.cloudFog) {
         Vec3 cloudColour = theWorld.getCloudColour(partialTicks);
         this.fogColorRed = (float)cloudColour.xCoord;
         this.fogColorGreen = (float)cloudColour.yCoord;
         this.fogColorBlue = (float)cloudColour.zCoord;
      } else if (blockAtEntityViewpoint.getMaterial() == Material.water) {
         float f2 = EnchantmentHelper.getRespiration(renderViewEntity) * 0.2F;
         f2 = Config.limit(f2, 0.0F, 0.6F);
         if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPotionActive(Potion.waterBreathing)) {
            f2 = f2 * 0.3F + 0.6F;
         }

         this.fogColorRed = 0.02F + f2;
         this.fogColorGreen = 0.02F + f2;
         this.fogColorBlue = 0.2F + f2;
         Vec3 underwaterColor = CustomColors.getUnderwaterColor(
            this.mc.theWorld, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0, this.mc.getRenderViewEntity().posZ
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
         Vec3 underlavaColor = CustomColors.getUnderlavaColor(
            this.mc.theWorld, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0, this.mc.getRenderViewEntity().posZ
         );
         if (underlavaColor != null) {
            this.fogColorRed = (float)underlavaColor.xCoord;
            this.fogColorGreen = (float)underlavaColor.yCoord;
            this.fogColorBlue = (float)underlavaColor.zCoord;
         }
      }

      float f2 = this.fogColor2 + (this.fogColor1 - this.fogColor2) * partialTicks;
      this.fogColorRed *= f2;
      this.fogColorGreen *= f2;
      this.fogColorBlue *= f2;
      double d = (renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * partialTicks) * theWorld.provider.getVoidFogYFactor();
      if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPotionActive(Potion.blindness)) {
         int duration = ((EntityLivingBase)renderViewEntity).getActivePotionEffect(Potion.blindness).getDuration();
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
         float f3 = this.bossColorModifierPrev + (this.bossColorModifier - this.bossColorModifierPrev) * partialTicks;
         this.fogColorRed = this.fogColorRed * (1.0F - f3) + this.fogColorRed * 0.7F * f3;
         this.fogColorGreen = this.fogColorGreen * (1.0F - f3) + this.fogColorGreen * 0.6F * f3;
         this.fogColorBlue = this.fogColorBlue * (1.0F - f3) + this.fogColorBlue * 0.6F * f3;
      }

      if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPotionActive(Potion.nightVision)) {
         float nightVisionBrightness = this.getNightVisionBrightness((EntityLivingBase)renderViewEntity, partialTicks);
         float f3 = 1.0F / this.fogColorRed;
         if (f3 > 1.0F / this.fogColorGreen) {
            f3 = 1.0F / this.fogColorGreen;
         }

         if (f3 > 1.0F / this.fogColorBlue) {
            f3 = 1.0F / this.fogColorBlue;
         }

         if (Float.isInfinite(f3)) {
            f3 = Math.nextAfter(f3, 0.0);
         }

         this.fogColorRed = this.fogColorRed * (1.0F - nightVisionBrightness) + this.fogColorRed * f3 * nightVisionBrightness;
         this.fogColorGreen = this.fogColorGreen * (1.0F - nightVisionBrightness) + this.fogColorGreen * f3 * nightVisionBrightness;
         this.fogColorBlue = this.fogColorBlue * (1.0F - nightVisionBrightness) + this.fogColorBlue * f3 * nightVisionBrightness;
      }

      if (this.mc.gameSettings.anaglyph) {
         float f3 = (this.fogColorRed * 30.0F + this.fogColorGreen * 59.0F + this.fogColorBlue * 11.0F) / 100.0F;
         float f4 = (this.fogColorRed * 30.0F + this.fogColorGreen * 70.0F) / 100.0F;
         float f5 = (this.fogColorRed * 30.0F + this.fogColorBlue * 70.0F) / 100.0F;
         this.fogColorRed = f3;
         this.fogColorGreen = f4;
         this.fogColorBlue = f5;
      }

      if (Reflector.EntityViewRenderEvent_FogColors_Constructor.exists()) {
         Object newInstance = Reflector.newInstance(
            Reflector.EntityViewRenderEvent_FogColors_Constructor, this, renderViewEntity, blockAtEntityViewpoint, partialTicks, this.fogColorRed, this.fogColorGreen, this.fogColorBlue
         );
         Reflector.postForgeBusEvent(newInstance);
         this.fogColorRed = Reflector.getFieldValueFloat(newInstance, Reflector.EntityViewRenderEvent_FogColors_red, this.fogColorRed);
         this.fogColorGreen = Reflector.getFieldValueFloat(newInstance, Reflector.EntityViewRenderEvent_FogColors_green, this.fogColorGreen);
         this.fogColorBlue = Reflector.getFieldValueFloat(newInstance, Reflector.EntityViewRenderEvent_FogColors_blue, this.fogColorBlue);
      }

      Shaders.setClearColor(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 0.0F);
   }

   public void setupFog(int startCoords, float partialTicks) {
      this.fogStandard = false;
      Entity renderViewEntity = this.mc.getRenderViewEntity();
      boolean flag = false;
      if (renderViewEntity instanceof EntityPlayer) {
         flag = ((EntityPlayer)renderViewEntity).capabilities.isCreativeMode;
      }

      GL11.glFog(2918, this.setFogColorBuffer(this.fogColorRed, this.fogColorGreen, this.fogColorBlue, 1.0F));
      GL11.glNormal3f(0.0F, -1.0F, 0.0F);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      Block blockAtEntityViewpoint = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, renderViewEntity, partialTicks);
      float f = -1.0F;
      if (Reflector.ForgeHooksClient_getFogDensity.exists()) {
         f = Reflector.callFloat(Reflector.ForgeHooksClient_getFogDensity, this, renderViewEntity, blockAtEntityViewpoint, partialTicks, 0.1F);
      }

      if (f >= 0.0F) {
         GlStateManager.setFogDensity(f);
      } else if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPotionActive(Potion.blindness)) {
         float f2 = 5.0F;
         int duration = ((EntityLivingBase)renderViewEntity).getActivePotionEffect(Potion.blindness).getDuration();
         if (duration < 20) {
            f2 = 5.0F + (this.farPlaneDistance - 5.0F) * (1.0F - duration / 20.0F);
         }

         GlStateManager.setFog(9729);
         if (startCoords == -1) {
            GlStateManager.setFogStart(0.0F);
            GlStateManager.setFogEnd(f2 * 0.8F);
         } else {
            GlStateManager.setFogStart(f2 * 0.25F);
            GlStateManager.setFogEnd(f2);
         }

         if (GLContext.getCapabilities().GL_NV_fog_distance && Config.isFogFancy()) {
            GL11.glFogi(34138, 34139);
         }
      } else if (this.cloudFog) {
         GlStateManager.setFog(2048);
         GlStateManager.setFogDensity(0.1F);
      } else if (blockAtEntityViewpoint.getMaterial() == Material.water) {
         GlStateManager.setFog(2048);
         float f2 = Config.isClearWater() ? 0.02F : 0.1F;
         if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPotionActive(Potion.waterBreathing)) {
            GlStateManager.setFogDensity(0.01F);
         } else {
            float f3 = 0.1F - EnchantmentHelper.getRespiration(renderViewEntity) * 0.03F;
            GlStateManager.setFogDensity(Config.limit(f3, 0.0F, f2));
         }
      } else if (blockAtEntityViewpoint.getMaterial() == Material.lava) {
         GlStateManager.setFog(2048);
         GlStateManager.setFogDensity(2.0F);
      } else {
         float farPlaneDistance = this.farPlaneDistance;
         this.fogStandard = true;
         GlStateManager.setFog(9729);
         if (startCoords == -1) {
            GlStateManager.setFogStart(0.0F);
            GlStateManager.setFogEnd(farPlaneDistance);
         } else {
            GlStateManager.setFogStart(farPlaneDistance * Config.getFogStart());
            GlStateManager.setFogEnd(farPlaneDistance);
         }

         if (GLContext.getCapabilities().GL_NV_fog_distance) {
            if (Config.isFogFancy()) {
               GL11.glFogi(34138, 34139);
            }

            if (Config.isFogFast()) {
               GL11.glFogi(34138, 34140);
            }
         }

         if (this.mc.theWorld.provider.doesXZShowFog((int)renderViewEntity.posX, (int)renderViewEntity.posZ)) {
            GlStateManager.setFogStart(farPlaneDistance * 0.05F);
            GlStateManager.setFogEnd(farPlaneDistance);
         }

         if (Reflector.ForgeHooksClient_onFogRender.exists()) {
            Reflector.callVoid(Reflector.ForgeHooksClient_onFogRender, this, renderViewEntity, blockAtEntityViewpoint, partialTicks, startCoords, farPlaneDistance);
         }
      }

      GlStateManager.enableColorMaterial();
      GlStateManager.enableFog();
      GlStateManager.colorMaterial(1028, 4608);
   }

   public FloatBuffer setFogColorBuffer(float red, float green, float blue, float alpha) {
      if (Config.isShaders()) {
         Shaders.setFogColor(red, green, blue);
      }

      ((Buffer)this.fogColorBuffer).clear();
      this.fogColorBuffer.put(red).put(green).put(blue).put(alpha);
      ((Buffer)this.fogColorBuffer).flip();
      return this.fogColorBuffer;
   }

   public MapItemRenderer getMapItemRenderer() {
      return this.theMapItemRenderer;
   }

   public void waitForServerThread() {
      this.serverWaitTimeCurrent = 0;
      if (!Config.isSmoothWorld() || !Config.isSingleProcessor()) {
         this.lastServerTime = 0L;
         this.lastServerTicks = 0;
      } else if (this.mc.isIntegratedServerRunning()) {
         IntegratedServer integratedServer = this.mc.getIntegratedServer();
         if (integratedServer != null) {
            boolean isGamePaused = this.mc.isGamePaused();
            if (!isGamePaused && !(this.mc.currentScreen instanceof GuiDownloadTerrain)) {
               if (this.serverWaitTime > 0) {
                  Lagometer.timerServer.start();
                  Config.sleep(this.serverWaitTime);
                  Lagometer.timerServer.end();
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
   }

   public void frameInit() {
      GlErrors.frameStart();
      if (!this.initialized) {
         ReflectorResolver.resolve();
         TextureUtils.registerResourceListener();
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
            ChatComponentText chatComponentText = new ChatComponentText(I18n.format("of.message.newVersion", "§n" + str + "§r"));
            chatComponentText.setChatStyle(new ChatStyle().setChatClickEvent(new ClickEvent(Action.OPEN_URL, "https://optifine.net/downloads")));
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
         RandomEntities.worldChanged(this.updatedWorld, theWorld);
         Config.updateThreadPriorities();
         this.lastServerTime = 0L;
         this.lastServerTicks = 0;
         this.updatedWorld = theWorld;
      }

      if (!this.setFxaaShader(Shaders.configAntialiasingLevel)) {
         Shaders.configAntialiasingLevel = 0;
      }

      if (this.mc.currentScreen != null && this.mc.currentScreen.getClass() == GuiChat.class) {
         this.mc.displayGuiScreen(new GuiChatOF((GuiChat)this.mc.currentScreen));
      }
   }

   public void frameFinish() {
      if (this.mc.theWorld != null && Config.isShowGlErrors() && TimedEvent.isActive("CheckGlErrorFrameFinish", 10000L)) {
         int glGetError = GlStateManager.glGetError();
         if (glGetError != 0 && GlErrors.isEnabled(glGetError)) {
            String glErrorString = Config.getGlErrorString(glGetError);
            ChatComponentText chatComponentText = new ChatComponentText(I18n.format("of.message.openglError", glGetError, glErrorString));
            this.mc.ingameGUI.getChatGUI().printChatMessage(chatComponentText);
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
         return this.useShader;
      }
   }

   public void checkLoadVisibleChunks(Entity entity, float f, ICamera camera, boolean flag) {
      int n = 201435902;
      if (this.loadVisibleChunks) {
         this.loadVisibleChunks = false;
         this.loadAllVisibleChunks(entity, f, camera, flag);
         this.mc.ingameGUI.getChatGUI().deleteChatLine(n);
      }

      if (Keyboard.isKeyDown(61) && Keyboard.isKeyDown(38)) {
         if (this.mc.currentScreen != null) {
            return;
         }

         this.loadVisibleChunks = true;
         ChatComponentText chatComponentText = new ChatComponentText(I18n.format("of.message.loadingVisibleChunks"));
         this.mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(chatComponentText, n);
      }
   }

   public void loadAllVisibleChunks(Entity entity, double d, ICamera camera, boolean flag) {
      int ofChunkUpdates = this.mc.gameSettings.ofChunkUpdates;
      boolean ofLazyChunkLoading = this.mc.gameSettings.ofLazyChunkLoading;

      try {
         this.mc.gameSettings.ofChunkUpdates = 1000;
         this.mc.gameSettings.ofLazyChunkLoading = false;
         RenderGlobal renderGlobal = Config.getRenderGlobal();
         int countLoadedChunks = renderGlobal.getCountLoadedChunks();
         long currentTimeMillis = System.currentTimeMillis();
         Config.dbg("Loading visible chunks");
         long l = System.currentTimeMillis() + 5000L;
         int n = 0;
         boolean flag2 = false;

         do {
            flag2 = false;

            for (int i = 0; i < 100; i++) {
               renderGlobal.displayListEntitiesDirty = true;
               renderGlobal.setupTerrain(entity, d, camera, this.frameCount++, flag);
               if (!renderGlobal.hasNoChunkUpdates()) {
                  flag2 = true;
               }

               n += renderGlobal.getCountChunksToUpdate();

               while (!renderGlobal.hasNoChunkUpdates()) {
                  renderGlobal.updateChunks(System.nanoTime() + 1000000000L);
               }

               n -= renderGlobal.getCountChunksToUpdate();
               if (!flag2) {
                  break;
               }
            }

            if (renderGlobal.getCountLoadedChunks() != countLoadedChunks) {
               flag2 = true;
               countLoadedChunks = renderGlobal.getCountLoadedChunks();
            }

            if (System.currentTimeMillis() > l) {
               Config.log("Chunks loaded: " + n);
               l = System.currentTimeMillis() + 5000L;
            }
         } while (flag2);

         Config.log("Chunks loaded: " + n);
         Config.log("Finished loading visible chunks");
         RenderChunk.renderChunksUpdated = 0;
      } finally {
         this.mc.gameSettings.ofChunkUpdates = ofChunkUpdates;
         this.mc.gameSettings.ofLazyChunkLoading = ofLazyChunkLoading;
      }
   }

   public void run() {
      if (this.shaderGroup != null) {
         this.shaderGroup.deleteShaderGroup();
      }

      this.shaderGroup = null;
   }

   public void run2() {
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
            this.useShader = true;
         } catch (Exception e) {
         }
      }
   }

   public void process(float f) {
      FreeLookHandler freeLookHandler = CheatBreaker.getInstance().getModManager().freeLookHandler;
      Entity renderViewEntity = this.mc.getRenderViewEntity();
      float eyeHeight = renderViewEntity.getEyeHeight();
      FloatEvent2 floatEvent2 = new FloatEvent2(eyeHeight, f);
      CheatBreaker.getInstance().getEventBus().handleEvent(floatEvent2);
      eyeHeight = floatEvent2.getEyeHeight();
      double d = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * f;
      double d2 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * f + eyeHeight;
      double d3 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * f;
      if (renderViewEntity instanceof EntityLivingBase && ((EntityLivingBase)renderViewEntity).isPlayerSleeping()) {
         eyeHeight = (float)(eyeHeight + 1.0);
         GlStateManager.translate(0.0F, 0.3F, 0.0F);
         if (!this.mc.gameSettings.debugCamEnable) {
            BlockPos blockPos = new BlockPos(renderViewEntity);
            IBlockState blockState = this.mc.theWorld.getBlockState(blockPos);
            Block block = blockState.getBlock();
            if (Reflector.ForgeHooksClient_orientBedCamera.exists()) {
               Reflector.callVoid(Reflector.ForgeHooksClient_orientBedCamera, this.mc.theWorld, blockPos, blockState, renderViewEntity);
            } else if (block == Blocks.bed) {
               int horizontalIndex = blockState.getValue(BlockBed.FACING).getHorizontalIndex();
               GlStateManager.rotate(horizontalIndex * 90, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotate(freeLookHandler.prevYaw + (freeLookHandler.yaw - freeLookHandler.prevYaw) * f + 180.0F, 0.0F, -1.0F, 0.0F);
            GlStateManager.rotate(freeLookHandler.prevPitch + (freeLookHandler.pitch - freeLookHandler.prevPitch) * f, -1.0F, 0.0F, 0.0F);
         }
      } else if (this.mc.gameSettings.thirdPersonView > 0) {
         double d4 = this.thirdPersonDistanceTemp + (this.thirdPersonDistance - this.thirdPersonDistanceTemp) * f;
         if (this.mc.gameSettings.debugCamEnable) {
            GlStateManager.translate(0.0F, 0.0F, (float)(-d4));
         } else {
            float yaw = freeLookHandler.yaw;
            float pitch = freeLookHandler.pitch;
            if (this.mc.gameSettings.thirdPersonView == 2) {
               pitch += 180.0F;
            }

            double d5 = -MathHelper.sin(yaw / 180.0F * (float) Math.PI) * MathHelper.cos(pitch / 180.0F * (float) Math.PI) * d4;
            double d6 = MathHelper.cos(yaw / 180.0F * (float) Math.PI) * MathHelper.cos(pitch / 180.0F * (float) Math.PI) * d4;
            double d7 = -MathHelper.sin(pitch / 180.0F * (float) Math.PI) * d4;

            for (int i = 0; i < 8; i++) {
               float f2 = (i & 1) * 2 - 1;
               float f3 = (i >> 1 & 1) * 2 - 1;
               float f4 = (i >> 2 & 1) * 2 - 1;
               f2 *= 0.1F;
               f3 *= 0.1F;
               f4 *= 0.1F;
               MovingObjectPosition rayTraceBlocks = this.mc
                  .theWorld
                  .rayTraceBlocks(
                     new Vec3(d + f2, d2 + f3, d3 + f4), new Vec3(d - d5 + f2 + f4, d2 - d7 + f3, d3 - d6 + f4)
                  );
               if (rayTraceBlocks != null) {
                  double distanceTo = rayTraceBlocks.hitVec.distanceTo(new Vec3(d, d2, d3));
                  if (distanceTo < d4) {
                     d4 = distanceTo;
                  }
               }
            }

            if (this.mc.gameSettings.thirdPersonView == 2) {
               GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
            }

            GlStateManager.rotate(freeLookHandler.pitch - pitch, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(freeLookHandler.yaw - yaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(0.0F, 0.0F, (float)(-d4));
            GlStateManager.rotate(yaw - freeLookHandler.yaw, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(pitch - freeLookHandler.pitch, 1.0F, 0.0F, 0.0F);
         }
      } else {
         GlStateManager.translate(0.0F, 0.0F, -0.1F);
      }

      if (Reflector.EntityViewRenderEvent_CameraSetup_Constructor.exists()) {
         if (!this.mc.gameSettings.debugCamEnable) {
            float f2 = freeLookHandler.prevYaw + (freeLookHandler.yaw - freeLookHandler.prevYaw) * f + 180.0F;
            float f3 = freeLookHandler.prevPitch + (freeLookHandler.pitch - freeLookHandler.prevPitch) * f;
            float f4 = 0.0F;
            if (renderViewEntity instanceof EntityAnimal) {
               EntityAnimal entityAnimal = (EntityAnimal)renderViewEntity;
               f2 = entityAnimal.prevRotationYawHead + (entityAnimal.rotationYawHead - entityAnimal.prevRotationYawHead) * f + 180.0F;
            }

            Block blockAtEntityViewpoint = ActiveRenderInfo.getBlockAtEntityViewpoint(this.mc.theWorld, renderViewEntity, f);
            Object newInstance = Reflector.newInstance(Reflector.EntityViewRenderEvent_CameraSetup_Constructor, this, renderViewEntity, blockAtEntityViewpoint, f, f2, f3, f4);
            Reflector.postForgeBusEvent(newInstance);
            GlStateManager.rotate(f4, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(f3, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(f2, 0.0F, 1.0F, 0.0F);
         }
      } else if (!this.mc.gameSettings.debugCamEnable) {
         GlStateManager.rotate(freeLookHandler.prevPitch + (freeLookHandler.pitch - freeLookHandler.prevPitch) * f, 1.0F, 0.0F, 0.0F);
         if (renderViewEntity instanceof EntityAnimal) {
            EntityAnimal entityAnimal = (EntityAnimal)renderViewEntity;
            GlStateManager.rotate(entityAnimal.prevRotationYawHead + (entityAnimal.rotationYawHead - entityAnimal.prevRotationYawHead) * f + 180.0F, 0.0F, 1.0F, 0.0F);
         } else {
            GlStateManager.rotate(freeLookHandler.prevYaw + (freeLookHandler.yaw - freeLookHandler.prevYaw) * f + 180.0F, 0.0F, 1.0F, 0.0F);
         }
      }

      GlStateManager.translate(0.0F, -eyeHeight, 0.0F);
      d = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * f;
      d2 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * f + eyeHeight;
      d3 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * f;
      this.cloudFog = this.mc.renderGlobal.hasCloudFog(d, d2, d3, f);
   }

   public ShaderGroup getShaderGroup2() {
      return this.shaderGroup;
   }

   public boolean check() {
      return OpenGlHelper.shadersSupported && this.shaderGroup != null;
   }

   public int computeInt(int n) {
      KeybindSettings keybindSettings = CheatBreaker.getInstance().getKeybindSettings();
      boolean flag = keybindSettings != null && keybindSettings.enableFpsBoost.getBooleanValue() && keybindSettings.fullbright.getBooleanValue();
      if (flag) {
         Arrays.fill(this.lightmapColors, -1);
         return 0;
      } else {
         return n;
      }
   }

   public void process(float f, String str) {
      if (this.mc.getRenderViewEntity() instanceof EntityPlayer) {
         StringFloatEvent stringFloatEvent = new StringFloatEvent(f, str, 1.0F, 1.0F, 1.0F);
         CheatBreaker.getInstance().getEventBus().handleEvent(stringFloatEvent);
         if (stringFloatEvent.isFlag()) {
            return;
         }

         float floatValue2 = stringFloatEvent.getFloatValue2();
         float floatValue3 = stringFloatEvent.getFloatValue3();
         float floatValue4 = stringFloatEvent.getFloatValue4();
         EntityPlayer renderViewEntity = (EntityPlayer)this.mc.getRenderViewEntity();
         float f2 = renderViewEntity.distanceWalkedModified - renderViewEntity.prevDistanceWalkedModified;
         float f3 = -(renderViewEntity.distanceWalkedModified + f2 * f);
         float f4 = renderViewEntity.prevCameraYaw + (renderViewEntity.cameraYaw - renderViewEntity.prevCameraYaw) * f;
         float f5 = renderViewEntity.prevCameraPitch + (renderViewEntity.cameraPitch - renderViewEntity.prevCameraPitch) * f;
         GlStateManager.translate(
            MathHelper.sin(f3 * (float) Math.PI) * f4 * 0.5F * floatValue2, -Math.abs(MathHelper.cos(f3 * (float) Math.PI) * f4 * floatValue3), 0.0F
         );
         GlStateManager.rotate(MathHelper.sin(f3 * (float) Math.PI) * f4 * 3.0F * floatValue2, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(Math.abs(MathHelper.cos(f3 * (float) Math.PI - 0.2F) * f4) * 5.0F * floatValue3, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotate(f5 * floatValue4, 1.0F, 0.0F, 0.0F);
      }
   }

   public void run3() {
      if (OpenGlHelper.isFramebufferEnabled() && OpenGlHelper.shadersSupported) {
         if (this.shaderGroup != null) {
            this.shaderGroup.deleteShaderGroup();
            this.shaderGroup = null;
         }

         if (CheatBreaker.getInstance() != null && CheatBreaker.getInstance().getKeybindSettings().guiBlur.getBooleanValue()) {
            try {
               this.shaderGroup = new ShaderGroup(this.mc.getTextureManager(), this.resourceManager, this.mc.getFramebuffer(), animblurJson);
               this.shaderGroup.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
            } catch (Exception e) {
            }
         }
      }
   }

   public DynamicTexture getLightmapTexture() {
      return this.lightmapTexture;
   }
}
