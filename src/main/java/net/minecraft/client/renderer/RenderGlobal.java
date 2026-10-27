package net.minecraft.client.renderer;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.AxisAlignedBBDoubleEvent;
import com.cheatbreaker.client.event.BlockEvent;
import com.cheatbreaker.client.event.DoubleEvent2;
import com.cheatbreaker.client.event.FloatEvent5;
import com.cheatbreaker.client.event.FloatEvent8;
import com.cheatbreaker.client.event.SimpleEvent15;
import com.cheatbreaker.client.event.SimpleEvent19;
import com.cheatbreaker.client.event.SimpleEvent2;
import com.cheatbreaker.client.event.SimpleEvent21;
import com.cheatbreaker.client.render.WorldLabel;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.block.Block.EnumOffsetType;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockSign;
import net.minecraft.block.BlockSkull;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.IRenderChunkFactory;
import net.minecraft.client.renderer.chunk.ListChunkFactory;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.client.renderer.chunk.VboChunkFactory;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.client.renderer.culling.ClippingHelper;
import net.minecraft.client.renderer.culling.ClippingHelperImpl;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.RenderItemFrame;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySignRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexBuffer;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumType;
import net.minecraft.client.renderer.vertex.VertexFormatElement.EnumUsage;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.client.shader.ShaderLinkHelper;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityWitherSkull;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.LongHashMap;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Matrix4f;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vector3d;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkProvider;
import net.optifine.Config;
import net.optifine.CustomColors;
import net.optifine.CustomSky;
import net.optifine.DynamicLights;
import net.optifine.Lagometer;
import net.optifine.RandomEntities;
import net.optifine.SmartAnimations;
import net.optifine.model.BlockModelUtils;
import net.optifine.reflect.Reflector;
import net.optifine.render.ChunkVisibility;
import net.optifine.render.CloudRenderer;
import net.optifine.render.RenderEnv;
import net.optifine.shaders.Shaders;
import net.optifine.shaders.ShadersRender;
import net.optifine.shaders.ShadowUtils;
import net.optifine.shaders.gui.GuiShaderOptions;
import net.optifine.util.ChunkUtils;
import net.optifine.util.RenderChunkUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

public class RenderGlobal implements IWorldAccess, IResourceManagerReloadListener {
   public static Logger logger = LogManager.getLogger();
   public static ResourceLocation locationMoonPhasesPng = new ResourceLocation("textures/environment/moon_phases.png");
   public static ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");
   public static ResourceLocation locationCloudsPng = new ResourceLocation("textures/environment/clouds.png");
   public static ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
   public static ResourceLocation locationForcefieldPng = new ResourceLocation("textures/misc/forcefield.png");
   public Set<RenderChunk> chunksToUpdate = Sets.newLinkedHashSet();
   public List<RenderGlobal.ContainerLocalRenderInformation> renderInfos = Lists.newArrayListWithCapacity(69696);
   public Set<TileEntity> setTileEntities = Sets.newHashSet();
   public WorldClient theWorld;
   public int starGLCallList = -1;
   public int glSkyList = -1;
   public int glSkyList2 = -1;
   public ViewFrustum viewFrustum;
   public Map<Integer, DestroyBlockProgress> damagedBlocks = Maps.newHashMap();
   public Map<BlockPos, ISound> mapSoundPositions = Maps.newHashMap();
   public TextureAtlasSprite[] destroyBlockIcons = new TextureAtlasSprite[10];
   public double frustumUpdatePosX = Double.MIN_VALUE;
   public VertexBuffer starVBO;
   public VertexBuffer skyVBO;
   public VertexBuffer sky2VBO;
   public int cloudTickCounter;
   public double frustumUpdatePosY = Double.MIN_VALUE;
   public double frustumUpdatePosZ = Double.MIN_VALUE;
   public int frustumUpdatePosChunkX = Integer.MIN_VALUE;
   public Framebuffer entityOutlineFramebuffer;
   public ShaderGroup entityOutlineShader;
   public int frustumUpdatePosChunkY = Integer.MIN_VALUE;
   public int frustumUpdatePosChunkZ = Integer.MIN_VALUE;
   public double lastViewEntityX = Double.MIN_VALUE;
   public double lastViewEntityY = Double.MIN_VALUE;
   public double lastViewEntityZ = Double.MIN_VALUE;
   public double lastViewEntityPitch = Double.MIN_VALUE;
   public double lastViewEntityYaw = Double.MIN_VALUE;
   public ChunkRenderDispatcher renderDispatcher = new ChunkRenderDispatcher();
   public int renderDistanceChunks = -1;
   public int renderEntitiesStartupCounter = 2;
   public boolean debugFixTerrainFrustum = false;
   public Vector4f[] debugTerrainMatrix = new Vector4f[8];
   public Vector3d debugTerrainFrustumPosition = new Vector3d();
   public boolean vboEnabled = false;
   public boolean displayListEntitiesDirty = true;
   public int countEntitiesTotal;
   public int countEntitiesRendered;
   public int countEntitiesHidden;
   public Set chunksToResortTransparency = new LinkedHashSet();
   public ClippingHelper debugFixedClippingHelper;
   public Set chunksToUpdateForced = new LinkedHashSet();
   public Deque visibilityDeque = new ArrayDeque();
   public List renderInfosEntities = new ArrayList(1024);
   public List renderInfosTileEntities = new ArrayList(1024);
   public double prevRenderSortX;
   public double prevRenderSortY;
   public double prevRenderSortZ;
   public List renderInfosNormal = new ArrayList(1024);
   public List renderInfosEntitiesNormal = new ArrayList(1024);
   public Entity renderedEntity;
   public List renderInfosTileEntitiesNormal = new ArrayList(1024);
   public List renderInfosShadow = new ArrayList(1024);
   public List renderInfosEntitiesShadow = new ArrayList(1024);
   public List renderInfosTileEntitiesShadow = new ArrayList(1024);
   public int renderDistance = 0;
   public int renderDistanceSq = 0;
   public IChunkProvider worldChunkProvider = null;
   public LongHashMap worldChunkProviderMap = null;
   public int countLoadedChunksPrev = 0;
   public RenderEnv renderEnv = new RenderEnv(Blocks.air.getDefaultState(), new BlockPos(0, 0, 0));
   public boolean renderOverlayDamaged = false;
   public boolean renderOverlayEyes = false;
   public boolean firstWorldLoad = false;
   public static Set SET_ALL_FACINGS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(EnumFacing.VALUES)));
   public int countTileEntitiesRendered;
   public CloudRenderer cloudRenderer;
   public Minecraft mc;
   public RenderManager renderManager;
   public TextureManager renderEngine;
   public ChunkRenderContainer renderContainer;
   public IRenderChunkFactory renderChunkFactory;
   public VertexFormat vertexBufferFormat;
   public static int renderEntitiesCounter = 0;
   public int intValue;

   public RenderGlobal(Minecraft mcIn) {
      this.cloudRenderer = new CloudRenderer(mcIn);
      this.mc = mcIn;
      this.renderManager = mcIn.getRenderManager();
      this.renderEngine = mcIn.getTextureManager();
      this.renderEngine.bindTexture(locationForcefieldPng);
      GL11.glTexParameteri(3553, 10242, 10497);
      GL11.glTexParameteri(3553, 10243, 10497);
      GlStateManager.bindTexture(0);
      this.updateDestroyBlockIcons();
      this.vboEnabled = OpenGlHelper.useVbo();
      if (this.vboEnabled) {
         this.renderContainer = new VboRenderList();
         this.renderChunkFactory = new VboChunkFactory();
      } else {
         this.renderContainer = new RenderList();
         this.renderChunkFactory = new ListChunkFactory();
      }

      this.vertexBufferFormat = new VertexFormat();
      this.vertexBufferFormat.addElement(new VertexFormatElement(0, EnumType.FLOAT, EnumUsage.POSITION, 3));
      this.generateStars();
      this.generateSky();
      this.generateSky2();
   }

   @Override
   public void onResourceManagerReload(IResourceManager resourceManager) {
      this.updateDestroyBlockIcons();
   }

   public void updateDestroyBlockIcons() {
      TextureMap textureMapBlocks = this.mc.getTextureMapBlocks();

      for (int i = 0; i < this.destroyBlockIcons.length; i++) {
         this.destroyBlockIcons[i] = textureMapBlocks.getAtlasSprite("minecraft:blocks/destroy_stage_" + i);
      }
   }

   public void makeEntityOutlineShader() {
      if (OpenGlHelper.shadersSupported) {
         if (ShaderLinkHelper.getStaticShaderLinkHelper() == null) {
            ShaderLinkHelper.setNewStaticShaderLinkHelper();
         }

         ResourceLocation resourceLocation = new ResourceLocation("shaders/post/entity_outline.json");

         try {
            this.entityOutlineShader = new ShaderGroup(this.mc.getTextureManager(), this.mc.getResourceManager(), this.mc.getFramebuffer(), resourceLocation);
            this.entityOutlineShader.createBindFramebuffers(this.mc.displayWidth, this.mc.displayHeight);
            this.entityOutlineFramebuffer = this.entityOutlineShader.getFramebufferRaw("final");
         } catch (IOException e) {
            logger.warn("Failed to load shader: " + resourceLocation, e);
            this.entityOutlineShader = null;
            this.entityOutlineFramebuffer = null;
         } catch (JsonSyntaxException e) {
            logger.warn("Failed to load shader: " + resourceLocation, e);
            this.entityOutlineShader = null;
            this.entityOutlineFramebuffer = null;
         }
      } else {
         this.entityOutlineShader = null;
         this.entityOutlineFramebuffer = null;
      }
   }

   public void renderEntityOutlineFramebuffer() {
      if (this.isRenderEntityOutlines()) {
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
         this.entityOutlineFramebuffer.framebufferRenderExt(this.mc.displayWidth, this.mc.displayHeight, false);
         GlStateManager.disableBlend();
      }
   }

   public boolean isRenderEntityOutlines() {
      return !Config.isFastRender() && !Config.isShaders() && !Config.isAntialiasing()
         ? this.entityOutlineFramebuffer != null
            && this.entityOutlineShader != null
            && this.mc.thePlayer != null
            && this.mc.thePlayer.isSpectator()
            && this.mc.gameSettings.keyBindSpectatorOutlines.isKeyDown()
         : false;
   }

   public void generateSky2() {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      if (this.sky2VBO != null) {
         this.sky2VBO.deleteGlBuffers();
      }

      if (this.glSkyList2 >= 0) {
         GLAllocation.deleteDisplayLists(this.glSkyList2);
         this.glSkyList2 = -1;
      }

      if (this.vboEnabled) {
         this.sky2VBO = new VertexBuffer(this.vertexBufferFormat);
         this.renderSky(worldRenderer, -16.0F, true);
         worldRenderer.finishDrawing();
         worldRenderer.reset();
         this.sky2VBO.bufferData(worldRenderer.getByteBuffer());
      } else {
         this.glSkyList2 = GLAllocation.generateDisplayLists(1);
         GL11.glNewList(this.glSkyList2, 4864);
         this.renderSky(worldRenderer, -16.0F, true);
         instance.draw();
         GL11.glEndList();
      }
   }

   public void generateSky() {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      if (this.skyVBO != null) {
         this.skyVBO.deleteGlBuffers();
      }

      if (this.glSkyList >= 0) {
         GLAllocation.deleteDisplayLists(this.glSkyList);
         this.glSkyList = -1;
      }

      if (this.vboEnabled) {
         this.skyVBO = new VertexBuffer(this.vertexBufferFormat);
         this.renderSky(worldRenderer, 16.0F, false);
         worldRenderer.finishDrawing();
         worldRenderer.reset();
         this.skyVBO.bufferData(worldRenderer.getByteBuffer());
      } else {
         this.glSkyList = GLAllocation.generateDisplayLists(1);
         GL11.glNewList(this.glSkyList, 4864);
         this.renderSky(worldRenderer, 16.0F, false);
         instance.draw();
         GL11.glEndList();
      }
   }

   public void renderSky(WorldRenderer worldRendererIn, float posY, boolean reverseX) {
      byte b = 64;
      byte b2 = 6;
      worldRendererIn.begin(7, DefaultVertexFormats.POSITION);
      int n = (this.renderDistance / 64 + 1) * 64 + 64;

      for (int i = -n; i <= n; i += 64) {
         for (int j = -n; j <= n; j += 64) {
            float f = i;
            float f2 = i + 64;
            if (reverseX) {
               f2 = i;
               f = i + 64;
            }

            worldRendererIn.pos(f, posY, j).endVertex();
            worldRendererIn.pos(f2, posY, j).endVertex();
            worldRendererIn.pos(f2, posY, j + 64).endVertex();
            worldRendererIn.pos(f, posY, j + 64).endVertex();
         }
      }
   }

   public void generateStars() {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      if (this.starVBO != null) {
         this.starVBO.deleteGlBuffers();
      }

      if (this.starGLCallList >= 0) {
         GLAllocation.deleteDisplayLists(this.starGLCallList);
         this.starGLCallList = -1;
      }

      if (this.vboEnabled) {
         this.starVBO = new VertexBuffer(this.vertexBufferFormat);
         this.renderStars(worldRenderer);
         worldRenderer.finishDrawing();
         worldRenderer.reset();
         this.starVBO.bufferData(worldRenderer.getByteBuffer());
      } else {
         this.starGLCallList = GLAllocation.generateDisplayLists(1);
         GlStateManager.pushMatrix();
         GL11.glNewList(this.starGLCallList, 4864);
         this.renderStars(worldRenderer);
         instance.draw();
         GL11.glEndList();
         GlStateManager.popMatrix();
      }
   }

   public void renderStars(WorldRenderer worldRendererIn) {
      Random random = new Random(10842L);
      worldRendererIn.begin(7, DefaultVertexFormats.POSITION);

      for (int i = 0; i < 1500; i++) {
         double d = random.nextFloat() * 2.0F - 1.0F;
         double d2 = random.nextFloat() * 2.0F - 1.0F;
         double d3 = random.nextFloat() * 2.0F - 1.0F;
         double d4 = 0.15F + random.nextFloat() * 0.1F;
         double d5 = d * d + d2 * d2 + d3 * d3;
         if (d5 < 1.0 && d5 > 0.01) {
            d5 = 1.0 / Math.sqrt(d5);
            d *= d5;
            d2 *= d5;
            d3 *= d5;
            double d6 = d * 100.0;
            double d7 = d2 * 100.0;
            double d8 = d3 * 100.0;
            double atan2 = Math.atan2(d, d3);
            double sin = Math.sin(atan2);
            double cos = Math.cos(atan2);
            double atan22 = Math.atan2(Math.sqrt(d * d + d3 * d3), d2);
            double sin2 = Math.sin(atan22);
            double cos2 = Math.cos(atan22);
            double d9 = random.nextDouble() * Math.PI * 2.0;
            double sin3 = Math.sin(d9);
            double cos3 = Math.cos(d9);

            for (int j = 0; j < 4; j++) {
               double d10 = 0.0;
               double d11 = ((j & 2) - 1) * d4;
               double d12 = ((j + 1 & 2) - 1) * d4;
               double d13 = 0.0;
               double d14 = d11 * cos3 - d12 * sin3;
               double d15 = d12 * cos3 + d11 * sin3;
               double d16 = d14 * sin2 + 0.0 * cos2;
               double d17 = 0.0 * sin2 - d14 * cos2;
               double d18 = d17 * sin - d15 * cos;
               double d19 = d15 * sin + d17 * cos;
               worldRendererIn.pos(d6 + d18, d7 + d16, d8 + d19).endVertex();
            }
         }
      }
   }

   public void setWorldAndLoadRenderers(WorldClient worldClientIn) {
      if (this.theWorld != null) {
         this.theWorld.removeWorldAccess(this);
      }

      this.frustumUpdatePosX = Double.MIN_VALUE;
      this.frustumUpdatePosY = Double.MIN_VALUE;
      this.frustumUpdatePosZ = Double.MIN_VALUE;
      this.frustumUpdatePosChunkX = Integer.MIN_VALUE;
      this.frustumUpdatePosChunkY = Integer.MIN_VALUE;
      this.frustumUpdatePosChunkZ = Integer.MIN_VALUE;
      this.renderManager.set(worldClientIn);
      this.theWorld = worldClientIn;
      if (Config.isDynamicLights()) {
         DynamicLights.clear();
      }

      ChunkVisibility.reset();
      this.worldChunkProvider = null;
      this.worldChunkProviderMap = null;
      this.renderEnv.reset((IBlockState)null, (BlockPos)null);
      Shaders.checkWorldChanged(this.theWorld);
      if (worldClientIn != null) {
         worldClientIn.addWorldAccess(this);
         this.loadRenderers();
      } else {
         this.chunksToUpdate.clear();
         this.clearRenderInfos();
         if (this.viewFrustum != null) {
            this.viewFrustum.deleteGlResources();
         }

         this.viewFrustum = null;
      }
   }

   public void loadRenderers() {
      if (this.theWorld != null) {
         this.displayListEntitiesDirty = true;
         Blocks.leaves.setGraphicsLevel(Config.isTreesFancy());
         Blocks.leaves2.setGraphicsLevel(Config.isTreesFancy());
         BlockModelRenderer.updateAoLightValue();
         if (Config.isDynamicLights()) {
            DynamicLights.clear();
         }

         SmartAnimations.update();
         this.renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;
         this.renderDistance = this.renderDistanceChunks * 16;
         this.renderDistanceSq = this.renderDistance * this.renderDistance;
         boolean vboEnabled = this.vboEnabled;
         this.vboEnabled = OpenGlHelper.useVbo();
         if (vboEnabled && !this.vboEnabled) {
            this.renderContainer = new RenderList();
            this.renderChunkFactory = new ListChunkFactory();
         } else if (!vboEnabled && this.vboEnabled) {
            this.renderContainer = new VboRenderList();
            this.renderChunkFactory = new VboChunkFactory();
         }

         this.generateStars();
         this.generateSky();
         this.generateSky2();
         if (this.viewFrustum != null) {
            this.viewFrustum.deleteGlResources();
         }

         this.stopChunkUpdates();
         synchronized (this.setTileEntities) {
            this.setTileEntities.clear();
         }

         this.viewFrustum = new ViewFrustum(this.theWorld, this.mc.gameSettings.renderDistanceChunks, this, this.renderChunkFactory);
         if (this.theWorld != null) {
            Entity renderViewEntity = this.mc.getRenderViewEntity();
            if (renderViewEntity != null) {
               this.viewFrustum.updateChunkPositions(renderViewEntity.posX, renderViewEntity.posZ);
            }
         }

         this.renderEntitiesStartupCounter = 2;
      }

      if (this.mc.thePlayer == null) {
         this.firstWorldLoad = true;
      }
   }

   public void stopChunkUpdates() {
      this.chunksToUpdate.clear();
      this.renderDispatcher.stopChunkUpdates();
   }

   public void createBindEntityOutlineFbs(int width, int height) {
      if (OpenGlHelper.shadersSupported && this.entityOutlineShader != null) {
         this.entityOutlineShader.createBindFramebuffers(width, height);
      }
   }

   public void renderEntities(Entity renderViewEntity, ICamera camera, float partialTicks) {
      int n = 0;
      if (Reflector.MinecraftForgeClient_getRenderPass.exists()) {
         n = Reflector.callInt(Reflector.MinecraftForgeClient_getRenderPass);
      }

      if (this.renderEntitiesStartupCounter > 0) {
         if (n > 0) {
            return;
         }

         this.renderEntitiesStartupCounter--;
      } else {
         double d = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * partialTicks;
         double d2 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * partialTicks;
         double d3 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * partialTicks;
         this.theWorld.theProfiler.startSection("prepare");
         TileEntityRendererDispatcher.instance
            .cacheActiveRenderInfo(this.theWorld, this.mc.getTextureManager(), this.mc.fontRendererObj, this.mc.getRenderViewEntity(), partialTicks);
         this.renderManager
            .cacheActiveRenderInfo(
               this.theWorld, this.mc.fontRendererObj, this.mc.getRenderViewEntity(), this.mc.pointedEntity, this.mc.gameSettings, partialTicks
            );
         renderEntitiesCounter++;
         if (n == 0) {
            this.countEntitiesTotal = 0;
            this.countEntitiesRendered = 0;
            this.countEntitiesHidden = 0;
            this.countTileEntitiesRendered = 0;
         }

         Entity renderViewEntity2 = this.mc.getRenderViewEntity();
         double d4 = renderViewEntity2.lastTickPosX + (renderViewEntity2.posX - renderViewEntity2.lastTickPosX) * partialTicks;
         double d5 = renderViewEntity2.lastTickPosY + (renderViewEntity2.posY - renderViewEntity2.lastTickPosY) * partialTicks;
         double d6 = renderViewEntity2.lastTickPosZ + (renderViewEntity2.posZ - renderViewEntity2.lastTickPosZ) * partialTicks;
         TileEntityRendererDispatcher.staticPlayerX = d4;
         TileEntityRendererDispatcher.staticPlayerY = d5;
         TileEntityRendererDispatcher.staticPlayerZ = d6;
         this.renderManager.setRenderPosition(d4, d5, d6);
         this.mc.entityRenderer.enableLightmap();
         this.theWorld.theProfiler.endStartSection("global");
         List loadedEntityList = this.theWorld.getLoadedEntityList();
         if (n == 0) {
            this.countEntitiesTotal = loadedEntityList.size();
         }

         if (Config.isFogOff() && this.mc.entityRenderer.fogStandard) {
            GlStateManager.disableFog();
         }

         boolean exists = Reflector.ForgeEntity_shouldRenderInPass.exists();
         boolean exists2 = Reflector.ForgeTileEntity_shouldRenderInPass.exists();

         for (int i = 0; i < this.theWorld.weatherEffects.size(); i++) {
            Entity entity = this.theWorld.weatherEffects.get(i);
            if (!exists || Reflector.callBoolean(entity, Reflector.ForgeEntity_shouldRenderInPass, n)) {
               this.countEntitiesRendered++;
               if (entity.isInRangeToRender3d(d, d2, d3)) {
                  this.renderManager.renderEntitySimple(entity, partialTicks);
               }
            }
         }

         if (this.isRenderEntityOutlines()) {
            GlStateManager.depthFunc(519);
            GlStateManager.disableFog();
            this.entityOutlineFramebuffer.framebufferClear();
            this.entityOutlineFramebuffer.bindFramebuffer(false);
            this.theWorld.theProfiler.endStartSection("entityOutlines");
            RenderHelper.disableStandardItemLighting();
            this.renderManager.setRenderOutlines(true);

            for (int i = 0; i < loadedEntityList.size(); i++) {
               Entity entity = (Entity)loadedEntityList.get(i);
               boolean flag = this.mc.getRenderViewEntity() instanceof EntityLivingBase
                  && ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping();
               boolean flag2 = entity.isInRangeToRender3d(d, d2, d3)
                  && (entity.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(entity.getEntityBoundingBox()) || entity.riddenByEntity == this.mc.thePlayer)
                  && entity instanceof EntityPlayer;
               if ((entity != this.mc.getRenderViewEntity() || this.mc.gameSettings.thirdPersonView != 0 || flag) && flag2) {
                  this.renderManager.renderEntitySimple(entity, partialTicks);
               }
            }

            this.renderManager.setRenderOutlines(false);
            RenderHelper.enableStandardItemLighting();
            GlStateManager.depthMask(false);
            this.entityOutlineShader.loadShaderGroup(partialTicks);
            GlStateManager.enableLighting();
            GlStateManager.depthMask(true);
            this.mc.getFramebuffer().bindFramebuffer(false);
            GlStateManager.enableFog();
            GlStateManager.enableBlend();
            GlStateManager.enableColorMaterial();
            GlStateManager.depthFunc(515);
            GlStateManager.enableDepth();
            GlStateManager.enableAlpha();
         }

         this.theWorld.theProfiler.endStartSection("entities");
         boolean isShaders = Config.isShaders();
         if (isShaders) {
            Shaders.beginEntities();
         }

         RenderItemFrame.updateItemRenderDistance();
         boolean fancyGraphics = this.mc.gameSettings.fancyGraphics;
         this.mc.gameSettings.fancyGraphics = Config.isDroppedItemsFancy();
         WorldLabel.renderAll();
         boolean flag = Shaders.isShadowPass && !this.mc.thePlayer.isSpectator();

         for (Object obj : this.renderInfosEntities) {
            RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation = (RenderGlobal.ContainerLocalRenderInformation)obj;
            Chunk chunk = containerLocalRenderInformation.renderChunk.getChunk();
            ClassInheritanceMultiMap classInheritanceMultiMap = chunk.getEntityLists()[containerLocalRenderInformation.renderChunk.getPosition().getY() / 16];
            if (!classInheritanceMultiMap.isEmpty()) {
               for (Entity entity : (Iterable<Entity>)classInheritanceMultiMap) {
                  if (!exists || Reflector.callBoolean(entity, Reflector.ForgeEntity_shouldRenderInPass, n)) {
                     boolean flag2 = this.renderManager.shouldRender(entity, camera, d, d2, d3) || entity.riddenByEntity == this.mc.thePlayer;
                     if (flag2) {
                        boolean flag3 = this.mc.getRenderViewEntity() instanceof EntityLivingBase
                           && ((EntityLivingBase)this.mc.getRenderViewEntity()).isPlayerSleeping();
                        if (entity == this.mc.getRenderViewEntity() && !flag && this.mc.gameSettings.thirdPersonView == 0 && !flag3
                           || !(entity.posY < 0.0) && !(entity.posY >= 256.0) && !this.theWorld.isBlockLoaded(new BlockPos(entity))) {
                           continue;
                        }

                        this.countEntitiesRendered++;
                        this.renderedEntity = entity;
                        if (isShaders) {
                           Shaders.nextEntity(entity);
                        }

                        this.renderManager.renderEntitySimple(entity, partialTicks);
                        this.renderedEntity = null;
                     }

                     if (!flag2
                        && entity instanceof EntityWitherSkull
                        && (!exists || Reflector.callBoolean(entity, Reflector.ForgeEntity_shouldRenderInPass, n))) {
                        this.renderedEntity = entity;
                        if (isShaders) {
                           Shaders.nextEntity(entity);
                        }

                        this.mc.getRenderManager().renderWitherSkull(entity, partialTicks);
                        this.renderedEntity = null;
                     }
                  }
               }
            }
         }

         this.mc.gameSettings.fancyGraphics = fancyGraphics;
         FontRenderer fontRenderer = TileEntityRendererDispatcher.instance.getFontRenderer();
         if (isShaders) {
            Shaders.endEntities();
            Shaders.beginBlockEntities();
         }

         this.theWorld.theProfiler.endStartSection("blockentities");
         RenderHelper.enableStandardItemLighting();
         if (Reflector.ForgeTileEntity_hasFastRenderer.exists()) {
            TileEntityRendererDispatcher.instance.preDrawBatch();
         }

         TileEntitySignRenderer.updateTextRenderDistance();

         for (Object obj : this.renderInfosTileEntities) {
            RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation = (RenderGlobal.ContainerLocalRenderInformation)obj;
            List tileEntities = containerLocalRenderInformation.renderChunk.getCompiledChunk().getTileEntities();
            if (!tileEntities.isEmpty()) {
               for (TileEntity tileEntity : (Iterable<TileEntity>)tileEntities) {
                  if (tileEntity.getClass() == TileEntitySign.class && !Config.zoomMode) {
                     EntityPlayerSP thePlayer = this.mc.thePlayer;
                     if (tileEntity.getDistanceSq(thePlayer.posX, thePlayer.posY, thePlayer.posZ) > 256.0) {
                        fontRenderer.flag = false;
                     }
                  }

                  if (isShaders) {
                     Shaders.nextBlockEntity(tileEntity);
                  }

                  TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, partialTicks, -1);
                  this.countTileEntitiesRendered++;
                  fontRenderer.flag = true;
               }
            }
         }

         synchronized (this.setTileEntities) {
            for (TileEntity tileEntity : this.setTileEntities) {
               if (tileEntity.getClass() == TileEntitySign.class && !Config.zoomMode) {
                  EntityPlayerSP thePlayer = this.mc.thePlayer;
                  if (tileEntity.getDistanceSq(thePlayer.posX, thePlayer.posY, thePlayer.posZ) > 256.0) {
                     fontRenderer.flag = false;
                  }
               }

               if (isShaders) {
                  Shaders.nextBlockEntity(tileEntity);
               }

               TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, partialTicks, -1);
               fontRenderer.flag = true;
            }
         }

         if (Reflector.ForgeTileEntity_hasFastRenderer.exists()) {
            TileEntityRendererDispatcher.instance.drawBatch(n);
         }

         this.renderOverlayDamaged = true;
         this.preRenderDamagedBlocks();

         for (DestroyBlockProgress destroyBlockProgress : this.damagedBlocks.values()) {
            BlockPos position = destroyBlockProgress.getPosition();
            TileEntity tileEntity = this.theWorld.getTileEntity(position);
            if (tileEntity instanceof TileEntityChest) {
               TileEntityChest tileEntityChest = (TileEntityChest)tileEntity;
               if (tileEntityChest.adjacentChestXNeg != null) {
                  position = position.offset(EnumFacing.WEST);
                  tileEntity = this.theWorld.getTileEntity(position);
               } else if (tileEntityChest.adjacentChestZNeg != null) {
                  position = position.offset(EnumFacing.NORTH);
                  tileEntity = this.theWorld.getTileEntity(position);
               }
            }

            Block block = this.theWorld.getBlockState(position).getBlock();
            boolean flag2;
            if (exists2) {
               flag2 = false;
               if (tileEntity != null
                  && Reflector.callBoolean(tileEntity, Reflector.ForgeTileEntity_shouldRenderInPass, n)
                  && Reflector.callBoolean(tileEntity, Reflector.ForgeTileEntity_canRenderBreaking)) {
                  AxisAlignedBB axisAlignedBB = (AxisAlignedBB)Reflector.call(tileEntity, Reflector.ForgeTileEntity_getRenderBoundingBox);
                  if (axisAlignedBB != null) {
                     flag2 = camera.isBoundingBoxInFrustum(axisAlignedBB);
                  }
               }
            } else {
               flag2 = tileEntity != null
                  && (block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockSign || block instanceof BlockSkull);
            }

            if (flag2) {
               if (isShaders) {
                  Shaders.nextBlockEntity(tileEntity);
               }

               TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, partialTicks, destroyBlockProgress.getPartialBlockDamage());
            }
         }

         this.postRenderDamagedBlocks();
         this.renderOverlayDamaged = false;
         if (isShaders) {
            Shaders.endBlockEntities();
         }

         renderEntitiesCounter--;
         this.mc.entityRenderer.disableLightmap();
         this.mc.mcProfiler.endSection();
      }
   }

   public String getDebugInfoRenders() {
      int length = this.viewFrustum.renderChunks.length;
      int n = 0;

      for (RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation : this.renderInfos) {
         CompiledChunk compiledChunk = containerLocalRenderInformation.renderChunk.compiledChunk;
         if (compiledChunk != CompiledChunk.DUMMY && !compiledChunk.isEmpty()) {
            n++;
         }
      }

      return String.format(
         "C: %d/%d %sD: %d, %s", n, length, this.mc.renderChunksMany ? "(s) " : "", this.renderDistanceChunks, this.renderDispatcher.getDebugInfo()
      );
   }

   public String getDebugInfoEntities() {
      return "E: "
         + this.countEntitiesRendered
         + "/"
         + this.countEntitiesTotal
         + ", B: "
         + this.countEntitiesHidden
         + ", I: "
         + (this.countEntitiesTotal - this.countEntitiesHidden - this.countEntitiesRendered)
         + ", "
         + Config.getVersionDebug();
   }

   public void setupTerrain(Entity viewEntity, double partialTicks, ICamera camera, int frameCount, boolean playerSpectator) {
      if (this.mc.gameSettings.renderDistanceChunks != this.renderDistanceChunks) {
         this.loadRenderers();
      }

      this.theWorld.theProfiler.startSection("camera");
      double d = viewEntity.posX - this.frustumUpdatePosX;
      double d2 = viewEntity.posY - this.frustumUpdatePosY;
      double d3 = viewEntity.posZ - this.frustumUpdatePosZ;
      if (this.frustumUpdatePosChunkX != viewEntity.chunkCoordX
         || this.frustumUpdatePosChunkY != viewEntity.chunkCoordY
         || this.frustumUpdatePosChunkZ != viewEntity.chunkCoordZ
         || d * d + d2 * d2 + d3 * d3 > 16.0) {
         this.frustumUpdatePosX = viewEntity.posX;
         this.frustumUpdatePosY = viewEntity.posY;
         this.frustumUpdatePosZ = viewEntity.posZ;
         this.frustumUpdatePosChunkX = viewEntity.chunkCoordX;
         this.frustumUpdatePosChunkY = viewEntity.chunkCoordY;
         this.frustumUpdatePosChunkZ = viewEntity.chunkCoordZ;
         this.viewFrustum.updateChunkPositions(viewEntity.posX, viewEntity.posZ);
      }

      if (Config.isDynamicLights()) {
         DynamicLights.update(this);
      }

      this.theWorld.theProfiler.endStartSection("renderlistcamera");
      double d4 = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
      double d5 = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
      double d6 = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;
      this.renderContainer.initialize(d4, d5, d6);
      this.theWorld.theProfiler.endStartSection("cull");
      if (this.debugFixedClippingHelper != null) {
         Frustum frustum = new Frustum(this.debugFixedClippingHelper);
         frustum.setPosition(this.debugTerrainFrustumPosition.x, this.debugTerrainFrustumPosition.y, this.debugTerrainFrustumPosition.z);
         camera = frustum;
      }

      this.mc.mcProfiler.endStartSection("culling");
      BlockPos blockPos = new BlockPos(d4, d5 + viewEntity.getEyeHeight(), d6);
      RenderChunk renderChunk = this.viewFrustum.getRenderChunk(blockPos);
      new BlockPos(MathHelper.floor_double(d4 / 16.0) * 16, MathHelper.floor_double(d5 / 16.0) * 16, MathHelper.floor_double(d6 / 16.0) * 16);
      this.displayListEntitiesDirty = this.displayListEntitiesDirty
         || !this.chunksToUpdate.isEmpty()
         || viewEntity.posX != this.lastViewEntityX
         || viewEntity.posY != this.lastViewEntityY
         || viewEntity.posZ != this.lastViewEntityZ
         || viewEntity.rotationPitch != this.lastViewEntityPitch
         || viewEntity.rotationYaw != this.lastViewEntityYaw;
      this.lastViewEntityX = viewEntity.posX;
      this.lastViewEntityY = viewEntity.posY;
      this.lastViewEntityZ = viewEntity.posZ;
      this.lastViewEntityPitch = viewEntity.rotationPitch;
      this.lastViewEntityYaw = viewEntity.rotationYaw;
      boolean flag = this.debugFixedClippingHelper != null;
      this.mc.mcProfiler.endStartSection("update");
      Lagometer.timerVisibility.start();
      int countLoadedChunks = this.getCountLoadedChunks();
      if (countLoadedChunks != this.countLoadedChunksPrev) {
         this.countLoadedChunksPrev = countLoadedChunks;
         this.displayListEntitiesDirty = true;
      }

      int n = 256;
      if (!ChunkVisibility.isFinished()) {
         this.displayListEntitiesDirty = true;
      }

      if (!flag && this.displayListEntitiesDirty && Config.isIntegratedServerRunning()) {
         n = ChunkVisibility.getMaxChunkY(this.theWorld, viewEntity, this.renderDistanceChunks);
      }

      RenderChunk renderChunk2 = this.viewFrustum.getRenderChunk(new BlockPos(viewEntity.posX, viewEntity.posY, viewEntity.posZ));
      if (Shaders.isShadowPass) {
         this.renderInfos = this.renderInfosShadow;
         this.renderInfosEntities = this.renderInfosEntitiesShadow;
         this.renderInfosTileEntities = this.renderInfosTileEntitiesShadow;
         if (!flag && this.displayListEntitiesDirty) {
            this.clearRenderInfos();
            if (renderChunk2 != null && renderChunk2.getPosition().getY() > n) {
               this.renderInfosEntities.add(renderChunk2.getRenderInfo());
            }

            Iterator makeShadowChunkIterator = ShadowUtils.makeShadowChunkIterator(this.theWorld, partialTicks, viewEntity, this.renderDistanceChunks, this.viewFrustum);

            while (makeShadowChunkIterator.hasNext()) {
               RenderChunk renderChunk3 = (RenderChunk)makeShadowChunkIterator.next();
               if (renderChunk3 != null && renderChunk3.getPosition().getY() <= n) {
                  RenderGlobal.ContainerLocalRenderInformation renderInfo = renderChunk3.getRenderInfo();
                  if (!renderChunk3.compiledChunk.isEmpty() || renderChunk3.isNeedsUpdate()) {
                     this.renderInfos.add(renderInfo);
                  }

                  if (ChunkUtils.hasEntities(renderChunk3.getChunk())) {
                     this.renderInfosEntities.add(renderInfo);
                  }

                  if (renderChunk3.getCompiledChunk().getTileEntities().size() > 0) {
                     this.renderInfosTileEntities.add(renderInfo);
                  }
               }
            }
         }
      } else {
         this.renderInfos = this.renderInfosNormal;
         this.renderInfosEntities = this.renderInfosEntitiesNormal;
         this.renderInfosTileEntities = this.renderInfosTileEntitiesNormal;
      }

      if (!flag && this.displayListEntitiesDirty && !Shaders.isShadowPass) {
         this.displayListEntitiesDirty = false;
         this.clearRenderInfos();
         this.visibilityDeque.clear();
         Deque visibilityDeque = this.visibilityDeque;
         boolean renderChunksMany = this.mc.renderChunksMany;
         if (renderChunk != null && renderChunk.getPosition().getY() <= n) {
            boolean flag2 = false;
            RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation = new RenderGlobal.ContainerLocalRenderInformation(renderChunk, (EnumFacing)null, 0);
            Set set = SET_ALL_FACINGS;
            if (set.size() == 1) {
               Vector3f viewVector = this.getViewVector(viewEntity, partialTicks);
               EnumFacing opposite = EnumFacing.getFacingFromVector(viewVector.x, viewVector.y, viewVector.z).getOpposite();
               set.remove(opposite);
            }

            if (set.isEmpty()) {
               flag2 = true;
            }

            if (flag2 && !playerSpectator) {
               this.renderInfos.add(containerLocalRenderInformation);
            } else {
               if (playerSpectator && this.theWorld.getBlockState(blockPos).getBlock().isOpaqueCube()) {
                  renderChunksMany = false;
               }

               renderChunk.setFrameIndex(frameCount);
               visibilityDeque.add(containerLocalRenderInformation);
            }
         } else {
            int n2 = blockPos.getY() > 0 ? Math.min(n, 248) : 8;
            if (renderChunk2 != null) {
               this.renderInfosEntities.add(renderChunk2.getRenderInfo());
            }

            for (int i = -this.renderDistanceChunks; i <= this.renderDistanceChunks; i++) {
               for (int j = -this.renderDistanceChunks; j <= this.renderDistanceChunks; j++) {
                  RenderChunk renderChunk3 = this.viewFrustum.getRenderChunk(new BlockPos((i << 4) + 8, n2, (j << 4) + 8));
                  if (renderChunk3 != null && renderChunk3.isBoundingBoxInFrustum(camera, frameCount)) {
                     renderChunk3.setFrameIndex(frameCount);
                     RenderGlobal.ContainerLocalRenderInformation renderInfo = renderChunk3.getRenderInfo();
                     renderInfo.initialize((EnumFacing)null, 0);
                     visibilityDeque.add(renderInfo);
                  }
               }
            }
         }

         this.mc.mcProfiler.startSection("iteration");
         boolean isFogOn = Config.isFogOn();

         while (!visibilityDeque.isEmpty()) {
            RenderGlobal.ContainerLocalRenderInformation poll = (RenderGlobal.ContainerLocalRenderInformation)visibilityDeque.poll();
            RenderChunk renderChunk3 = poll.renderChunk;
            EnumFacing facing = poll.facing;
            CompiledChunk compiledChunk = renderChunk3.compiledChunk;
            if (!compiledChunk.isEmpty() || renderChunk3.isNeedsUpdate()) {
               this.renderInfos.add(poll);
            }

            if (ChunkUtils.hasEntities(renderChunk3.getChunk())) {
               this.renderInfosEntities.add(poll);
            }

            if (compiledChunk.getTileEntities().size() > 0) {
               this.renderInfosTileEntities.add(poll);
            }

            for (EnumFacing enumFacing : renderChunksMany ? ChunkVisibility.getFacingsNotOpposite(poll.intValue) : EnumFacing.VALUES) {
               if (!renderChunksMany || facing == null || compiledChunk.isVisible(facing.getOpposite(), enumFacing)) {
                  RenderChunk renderChunkOffset = this.getRenderChunkOffset(blockPos, renderChunk3, enumFacing, isFogOn, n);
                  if (renderChunkOffset != null && renderChunkOffset.setFrameIndex(frameCount) && renderChunkOffset.isBoundingBoxInFrustum(camera, frameCount)) {
                     int n2 = poll.intValue | 1 << enumFacing.ordinal();
                     RenderGlobal.ContainerLocalRenderInformation renderInfo = renderChunkOffset.getRenderInfo();
                     renderInfo.initialize(enumFacing, n2);
                     visibilityDeque.add(renderInfo);
                  }
               }
            }
         }

         this.mc.mcProfiler.endSection();
      }

      this.mc.mcProfiler.endStartSection("captureFrustum");
      if (this.debugFixTerrainFrustum) {
         this.fixTerrainFrustum(d4, d5, d6);
         this.debugFixTerrainFrustum = false;
      }

      Lagometer.timerVisibility.end();
      if (Shaders.isShadowPass) {
         Shaders.mcProfilerEndSection();
      } else {
         this.mc.mcProfiler.endStartSection("rebuildNear");
         this.renderDispatcher.clearChunkUpdates();
         Set chunksToUpdate = this.chunksToUpdate;
         this.chunksToUpdate = Sets.newLinkedHashSet();
         Lagometer.timerChunkUpdate.start();

         for (RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation : this.renderInfos) {
            RenderChunk renderChunk3 = containerLocalRenderInformation.renderChunk;
            if (renderChunk3.isNeedsUpdate() || chunksToUpdate.contains(renderChunk3)) {
               this.displayListEntitiesDirty = true;
               BlockPos position = renderChunk3.getPosition();
               boolean flag2 = blockPos.distanceSq(position.getX() + 8, position.getY() + 8, position.getZ() + 8) < 768.0;
               if (!flag2) {
                  this.chunksToUpdate.add(renderChunk3);
               } else if (!renderChunk3.isPlayerUpdate()) {
                  this.chunksToUpdateForced.add(renderChunk3);
               } else {
                  this.mc.mcProfiler.startSection("build near");
                  this.renderDispatcher.updateChunkNow(renderChunk3);
                  renderChunk3.setNeedsUpdate(false);
                  this.mc.mcProfiler.endSection();
               }
            }
         }

         Lagometer.timerChunkUpdate.end();
         this.chunksToUpdate.addAll(chunksToUpdate);
         this.mc.mcProfiler.endSection();
      }
   }

   public boolean isPositionInRenderChunk(BlockPos pos, RenderChunk renderChunkIn) {
      BlockPos position = renderChunkIn.getPosition();
      return MathHelper.abs_int(pos.getX() - position.getX()) > 16
         ? false
         : (MathHelper.abs_int(pos.getY() - position.getY()) > 16 ? false : MathHelper.abs_int(pos.getZ() - position.getZ()) <= 16);
   }

   public Set<EnumFacing> getVisibleFacings(BlockPos pos) {
      VisGraph visGraph = new VisGraph();
      BlockPos blockPos = new BlockPos(pos.getX() >> 4 << 4, pos.getY() >> 4 << 4, pos.getZ() >> 4 << 4);
      Chunk chunkFromBlockCoords = this.theWorld.getChunkFromBlockCoords(blockPos);

      for (MutableBlockPos mutableBlockPos : BlockPos.getAllInBoxMutable(blockPos, blockPos.add(15, 15, 15))) {
         if (chunkFromBlockCoords.getBlock(mutableBlockPos).isOpaqueCube()) {
            visGraph.markClosed(mutableBlockPos);
         }
      }

      return visGraph.getOpenFaces(pos);
   }

   public RenderChunk getRenderChunkOffset(BlockPos blockPos, RenderChunk renderChunk, EnumFacing enumFacing, boolean flag, int n) {
      RenderChunk renderChunkNeighbour = renderChunk.getRenderChunkNeighbour(enumFacing);
      if (renderChunkNeighbour == null) {
         return null;
      }

      if (renderChunkNeighbour.getPosition().getY() > n) {
         return null;
      }

      if (flag) {
         BlockPos position = renderChunkNeighbour.getPosition();
         int n2 = blockPos.getX() - position.getX();
         int n3 = blockPos.getZ() - position.getZ();
         int n4 = n2 * n2 + n3 * n3;
         if (n4 > this.renderDistanceSq) {
            return null;
         }
      }

      return renderChunkNeighbour;
   }

   public void fixTerrainFrustum(double x, double y, double z) {
      this.debugFixedClippingHelper = new ClippingHelperImpl();
      ((ClippingHelperImpl)this.debugFixedClippingHelper).init();
      Matrix4f matrix4f = new Matrix4f(this.debugFixedClippingHelper.modelviewMatrix);
      matrix4f.transpose();
      Matrix4f matrix4f2 = new Matrix4f(this.debugFixedClippingHelper.projectionMatrix);
      matrix4f2.transpose();
      Matrix4f matrix4f3 = new Matrix4f();
      Matrix4f.mul(matrix4f2, matrix4f, matrix4f3);
      matrix4f3.invert();
      this.debugTerrainFrustumPosition.x = x;
      this.debugTerrainFrustumPosition.y = y;
      this.debugTerrainFrustumPosition.z = z;
      this.debugTerrainMatrix[0] = new Vector4f(-1.0F, -1.0F, -1.0F, 1.0F);
      this.debugTerrainMatrix[1] = new Vector4f(1.0F, -1.0F, -1.0F, 1.0F);
      this.debugTerrainMatrix[2] = new Vector4f(1.0F, 1.0F, -1.0F, 1.0F);
      this.debugTerrainMatrix[3] = new Vector4f(-1.0F, 1.0F, -1.0F, 1.0F);
      this.debugTerrainMatrix[4] = new Vector4f(-1.0F, -1.0F, 1.0F, 1.0F);
      this.debugTerrainMatrix[5] = new Vector4f(1.0F, -1.0F, 1.0F, 1.0F);
      this.debugTerrainMatrix[6] = new Vector4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.debugTerrainMatrix[7] = new Vector4f(-1.0F, 1.0F, 1.0F, 1.0F);

      for (int i = 0; i < 8; i++) {
         Matrix4f.transform(matrix4f3, this.debugTerrainMatrix[i], this.debugTerrainMatrix[i]);
         this.debugTerrainMatrix[i].x = this.debugTerrainMatrix[i].x / this.debugTerrainMatrix[i].w;
         this.debugTerrainMatrix[i].y = this.debugTerrainMatrix[i].y / this.debugTerrainMatrix[i].w;
         this.debugTerrainMatrix[i].z = this.debugTerrainMatrix[i].z / this.debugTerrainMatrix[i].w;
         this.debugTerrainMatrix[i].w = 1.0F;
      }
   }

   public Vector3f getViewVector(Entity entityIn, double partialTicks) {
      float f = (float)(entityIn.prevRotationPitch + (entityIn.rotationPitch - entityIn.prevRotationPitch) * partialTicks);
      float f2 = (float)(entityIn.prevRotationYaw + (entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks);
      if (Minecraft.getMinecraft().gameSettings.thirdPersonView == 2) {
         f += 180.0F;
      }

      float cos = MathHelper.cos(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float sin = MathHelper.sin(-f2 * (float) (Math.PI / 180.0) - (float) Math.PI);
      float f3 = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
      float sin2 = MathHelper.sin(-f * (float) (Math.PI / 180.0));
      return new Vector3f(sin * f3, sin2, cos * f3);
   }

   public int renderBlockLayer(EnumWorldBlockLayer blockLayerIn, double partialTicks, int pass, Entity entityIn) {
      RenderHelper.disableStandardItemLighting();
      if (blockLayerIn == EnumWorldBlockLayer.TRANSLUCENT && !Shaders.isShadowPass) {
         this.mc.mcProfiler.startSection("translucent_sort");
         double d = entityIn.posX - this.prevRenderSortX;
         double d2 = entityIn.posY - this.prevRenderSortY;
         double d3 = entityIn.posZ - this.prevRenderSortZ;
         if (d * d + d2 * d2 + d3 * d3 > 1.0) {
            this.prevRenderSortX = entityIn.posX;
            this.prevRenderSortY = entityIn.posY;
            this.prevRenderSortZ = entityIn.posZ;
            int n = 0;
            this.chunksToResortTransparency.clear();

            for (RenderGlobal.ContainerLocalRenderInformation containerLocalRenderInformation : this.renderInfos) {
               if (containerLocalRenderInformation.renderChunk.compiledChunk.isLayerStarted(blockLayerIn) && n++ < 15) {
                  this.chunksToResortTransparency.add(containerLocalRenderInformation.renderChunk);
               }
            }
         }

         this.mc.mcProfiler.endSection();
      }

      this.mc.mcProfiler.startSection("filterempty");
      int n = 0;
      boolean flag = blockLayerIn == EnumWorldBlockLayer.TRANSLUCENT;
      int n2 = flag ? this.renderInfos.size() - 1 : 0;
      int n3 = flag ? -1 : this.renderInfos.size();
      int n4 = flag ? -1 : 1;

      for (int i = n2; i != n3; i += n4) {
         RenderChunk renderChunk = this.renderInfos.get(i).renderChunk;
         if (!renderChunk.getCompiledChunk().isLayerEmpty(blockLayerIn)) {
            n++;
            this.renderContainer.addRenderChunk(renderChunk, blockLayerIn);
         }
      }

      if (n == 0) {
         this.mc.mcProfiler.endSection();
         return n;
      }

      if (Config.isFogOff() && this.mc.entityRenderer.fogStandard) {
         GlStateManager.disableFog();
      }

      this.mc.mcProfiler.endStartSection("render_" + blockLayerIn);
      this.renderBlockLayer(blockLayerIn);
      this.mc.mcProfiler.endSection();
      return n;
   }

   public void renderBlockLayer(EnumWorldBlockLayer blockLayerIn) {
      this.mc.entityRenderer.enableLightmap();
      if (OpenGlHelper.useVbo()) {
         GL11.glEnableClientState(32884);
         OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
         GL11.glEnableClientState(32888);
         OpenGlHelper.setClientActiveTexture(OpenGlHelper.lightmapTexUnit);
         GL11.glEnableClientState(32888);
         OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
         GL11.glEnableClientState(32886);
      }

      if (Config.isShaders()) {
         ShadersRender.preRenderChunkLayer(blockLayerIn);
      }

      this.renderContainer.renderChunkLayer(blockLayerIn);
      if (Config.isShaders()) {
         ShadersRender.postRenderChunkLayer(blockLayerIn);
      }

      if (OpenGlHelper.useVbo()) {
         for (VertexFormatElement vertexFormatElement : DefaultVertexFormats.BLOCK.getElements()) {
            EnumUsage usage = vertexFormatElement.getUsage();
            int index = vertexFormatElement.getIndex();
            switch (usage) {
               case POSITION:
                  GL11.glDisableClientState(32884);
                  break;
               case UV:
                  OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit + index);
                  GL11.glDisableClientState(32888);
                  OpenGlHelper.setClientActiveTexture(OpenGlHelper.defaultTexUnit);
                  break;
               case COLOR:
                  GL11.glDisableClientState(32886);
                  GlStateManager.resetColor();
            }
         }
      }

      this.mc.entityRenderer.disableLightmap();
   }

   public void cleanupDamagedBlocks(Iterator<DestroyBlockProgress> iteratorIn) {
      while (iteratorIn.hasNext()) {
         DestroyBlockProgress destroyBlockProgress = (DestroyBlockProgress)iteratorIn.next();
         int creationCloudUpdateTick = destroyBlockProgress.getCreationCloudUpdateTick();
         if (this.cloudTickCounter - creationCloudUpdateTick > 400) {
            iteratorIn.remove();
         }
      }
   }

   public void updateClouds() {
      if (Config.isShaders()) {
         if (Keyboard.isKeyDown(61) && Keyboard.isKeyDown(24)) {
            GuiShaderOptions guiShaderOptions = new GuiShaderOptions((GuiScreen)null, Config.getGameSettings());
            Config.getMinecraft().displayGuiScreen(guiShaderOptions);
         }

         if (Keyboard.isKeyDown(61) && Keyboard.isKeyDown(19)) {
            Shaders.uninit();
            Shaders.loadShaderPack();
         }
      }

      this.cloudTickCounter++;
      if (this.cloudTickCounter % 20 == 0) {
         this.cleanupDamagedBlocks(this.damagedBlocks.values().iterator());
      }
   }

   public void renderSkyEnd() {
      if (Config.isSkyEnabled()) {
         GlStateManager.disableFog();
         GlStateManager.disableAlpha();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         RenderHelper.disableStandardItemLighting();
         GlStateManager.depthMask(false);
         this.renderEngine.bindTexture(locationEndSkyPng);
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();

         for (int i = 0; i < 6; i++) {
            GlStateManager.pushMatrix();
            if (i == 1) {
               GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (i == 2) {
               GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
            }

            if (i == 3) {
               GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
            }

            if (i == 4) {
               GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            }

            if (i == 5) {
               GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
            }

            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
            int n = 40;
            int n2 = 40;
            int n3 = 40;
            if (Config.isCustomColors()) {
               Vec3 vec3 = new Vec3(n / 255.0, n2 / 255.0, n3 / 255.0);
               vec3 = CustomColors.getWorldSkyColor(vec3, this.theWorld, this.mc.getRenderViewEntity(), 0.0F);
               n = (int)(vec3.xCoord * 255.0);
               n2 = (int)(vec3.yCoord * 255.0);
               n3 = (int)(vec3.zCoord * 255.0);
            }

            worldRenderer.pos(-100.0, -100.0, -100.0).tex(0.0, 0.0).color(n, n2, n3, 255).endVertex();
            worldRenderer.pos(-100.0, -100.0, 100.0).tex(0.0, 16.0).color(n, n2, n3, 255).endVertex();
            worldRenderer.pos(100.0, -100.0, 100.0).tex(16.0, 16.0).color(n, n2, n3, 255).endVertex();
            worldRenderer.pos(100.0, -100.0, -100.0).tex(16.0, 0.0).color(n, n2, n3, 255).endVertex();
            instance.draw();
            GlStateManager.popMatrix();
         }

         GlStateManager.depthMask(true);
         GlStateManager.enableTexture2D();
         GlStateManager.enableAlpha();
         GlStateManager.disableBlend();
      }
   }

   public void renderSky(float partialTicks, int pass) {
      if (Reflector.ForgeWorldProvider_getSkyRenderer.exists()) {
         WorldProvider provider = this.mc.theWorld.provider;
         Object obj = Reflector.call(provider, Reflector.ForgeWorldProvider_getSkyRenderer);
         if (obj != null) {
            Reflector.callVoid(obj, Reflector.IRenderHandler_render, partialTicks, this.theWorld, this.mc);
            return;
         }
      }

      if (this.mc.theWorld.provider.getDimensionId() == 1) {
         this.renderSkyEnd();
      } else if (this.mc.theWorld.provider.isSurfaceWorld()) {
         GlStateManager.disableTexture2D();
         boolean isShaders = Config.isShaders();
         if (isShaders) {
            Shaders.disableTexture2D();
         }

         Vec3 skyColor = this.theWorld.getSkyColor(this.mc.getRenderViewEntity(), partialTicks);
         skyColor = CustomColors.getSkyColor(
            skyColor, this.mc.theWorld, this.mc.getRenderViewEntity().posX, this.mc.getRenderViewEntity().posY + 1.0, this.mc.getRenderViewEntity().posZ
         );
         if (isShaders) {
            Shaders.setSkyColor(skyColor);
         }

         float xCoord = (float)skyColor.xCoord;
         float yCoord = (float)skyColor.yCoord;
         float zCoord = (float)skyColor.zCoord;
         if (pass != 2) {
            float f = (xCoord * 30.0F + yCoord * 59.0F + zCoord * 11.0F) / 100.0F;
            float f2 = (xCoord * 30.0F + yCoord * 70.0F) / 100.0F;
            float f3 = (xCoord * 30.0F + zCoord * 70.0F) / 100.0F;
            xCoord = f;
            yCoord = f2;
            zCoord = f3;
         }

         GlStateManager.color(xCoord, yCoord, zCoord);
         Tessellator instance = Tessellator.getInstance();
         WorldRenderer worldRenderer = instance.getWorldRenderer();
         GlStateManager.depthMask(false);
         GlStateManager.enableFog();
         if (isShaders) {
            Shaders.enableFog();
         }

         GlStateManager.color(xCoord, yCoord, zCoord);
         if (isShaders) {
            Shaders.preSkyList();
         }

         if (Config.isSkyEnabled()) {
            if (this.vboEnabled) {
               this.skyVBO.bindBuffer();
               GL11.glEnableClientState(32884);
               GL11.glVertexPointer(3, 5126, 12, 0L);
               this.skyVBO.drawArrays(7);
               this.skyVBO.unbindBuffer();
               GL11.glDisableClientState(32884);
            } else {
               GlStateManager.callList(this.glSkyList);
            }
         }

         GlStateManager.disableFog();
         if (isShaders) {
            Shaders.disableFog();
         }

         GlStateManager.disableAlpha();
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         RenderHelper.disableStandardItemLighting();
         float[] calcSunriseSunsetColors = this.theWorld.provider.calcSunriseSunsetColors(this.theWorld.getCelestialAngle(partialTicks), partialTicks);
         if (calcSunriseSunsetColors != null && Config.isSunMoonEnabled()) {
            GlStateManager.disableTexture2D();
            if (isShaders) {
               Shaders.disableTexture2D();
            }

            GlStateManager.shadeModel(7425);
            GlStateManager.pushMatrix();
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(MathHelper.sin(this.theWorld.getCelestialAngleRadians(partialTicks)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
            float f = calcSunriseSunsetColors[0];
            float f2 = calcSunriseSunsetColors[1];
            float f3 = calcSunriseSunsetColors[2];
            if (pass != 2) {
               float f4 = (f * 30.0F + f2 * 59.0F + f3 * 11.0F) / 100.0F;
               float f5 = (f * 30.0F + f2 * 70.0F) / 100.0F;
               float f6 = (f * 30.0F + f3 * 70.0F) / 100.0F;
               f = f4;
               f2 = f5;
               f3 = f6;
            }

            worldRenderer.begin(6, DefaultVertexFormats.POSITION_COLOR);
            worldRenderer.pos(0.0, 100.0, 0.0).color(f, f2, f3, calcSunriseSunsetColors[3]).endVertex();
            byte b = 16;

            for (int i = 0; i <= 16; i++) {
               float f4 = i * (float) Math.PI * 2.0F / 16.0F;
               float sin = MathHelper.sin(f4);
               float cos = MathHelper.cos(f4);
               worldRenderer.pos(sin * 120.0F, cos * 120.0F, -cos * 40.0F * calcSunriseSunsetColors[3]).color(calcSunriseSunsetColors[0], calcSunriseSunsetColors[1], calcSunriseSunsetColors[2], 0.0F).endVertex();
            }

            instance.draw();
            GlStateManager.popMatrix();
            GlStateManager.shadeModel(7424);
         }

         GlStateManager.enableTexture2D();
         if (isShaders) {
            Shaders.enableTexture2D();
         }

         GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
         GlStateManager.pushMatrix();
         float f = 1.0F - this.theWorld.getRainStrength(partialTicks);
         GlStateManager.color(1.0F, 1.0F, 1.0F, f);
         GlStateManager.rotate(-90.0F, 0.0F, 1.0F, 0.0F);
         CustomSky.renderSky(this.theWorld, this.renderEngine, partialTicks);
         if (isShaders) {
            Shaders.preCelestialRotate();
         }

         GlStateManager.rotate(this.theWorld.getCelestialAngle(partialTicks) * 360.0F, 1.0F, 0.0F, 0.0F);
         if (isShaders) {
            Shaders.postCelestialRotate();
         }

         float f2 = 30.0F;
         if (Config.isSunTexture()) {
            this.renderEngine.bindTexture(locationSunPng);
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldRenderer.pos(-f2, 100.0, -f2).tex(0.0, 0.0).endVertex();
            worldRenderer.pos(f2, 100.0, -f2).tex(1.0, 0.0).endVertex();
            worldRenderer.pos(f2, 100.0, f2).tex(1.0, 1.0).endVertex();
            worldRenderer.pos(-f2, 100.0, f2).tex(0.0, 1.0).endVertex();
            instance.draw();
         }

         f2 = 20.0F;
         if (Config.isMoonTexture()) {
            this.renderEngine.bindTexture(locationMoonPhasesPng);
            int moonPhase = this.theWorld.getMoonPhase();
            int n = moonPhase % 4;
            int n2 = moonPhase / 4 % 2;
            float f3 = (n + 0) / 4.0F;
            float f4 = (n2 + 0) / 2.0F;
            float f5 = (n + 1) / 4.0F;
            float f6 = (n2 + 1) / 2.0F;
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
            worldRenderer.pos(-f2, -100.0, f2).tex(f5, f6).endVertex();
            worldRenderer.pos(f2, -100.0, f2).tex(f3, f6).endVertex();
            worldRenderer.pos(f2, -100.0, -f2).tex(f3, f4).endVertex();
            worldRenderer.pos(-f2, -100.0, -f2).tex(f5, f4).endVertex();
            instance.draw();
         }

         GlStateManager.disableTexture2D();
         if (isShaders) {
            Shaders.disableTexture2D();
         }

         float f3 = this.theWorld.getStarBrightness(partialTicks) * f;
         if (f3 > 0.0F && Config.isStarsEnabled() && !CustomSky.hasSkyLayers(this.theWorld)) {
            GlStateManager.color(f3, f3, f3, f3);
            if (this.vboEnabled) {
               this.starVBO.bindBuffer();
               GL11.glEnableClientState(32884);
               GL11.glVertexPointer(3, 5126, 12, 0L);
               this.starVBO.drawArrays(7);
               this.starVBO.unbindBuffer();
               GL11.glDisableClientState(32884);
            } else {
               GlStateManager.callList(this.starGLCallList);
            }
         }

         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.disableBlend();
         GlStateManager.enableAlpha();
         GlStateManager.enableFog();
         if (isShaders) {
            Shaders.enableFog();
         }

         GlStateManager.popMatrix();
         GlStateManager.disableTexture2D();
         if (isShaders) {
            Shaders.disableTexture2D();
         }

         GlStateManager.color(0.0F, 0.0F, 0.0F);
         double d = this.mc.thePlayer.getPositionEyes(partialTicks).yCoord - this.theWorld.getHorizon();
         if (d < 0.0) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(0.0F, 12.0F, 0.0F);
            if (this.vboEnabled) {
               this.sky2VBO.bindBuffer();
               GL11.glEnableClientState(32884);
               GL11.glVertexPointer(3, 5126, 12, 0L);
               this.sky2VBO.drawArrays(7);
               this.sky2VBO.unbindBuffer();
               GL11.glDisableClientState(32884);
            } else {
               GlStateManager.callList(this.glSkyList2);
            }

            GlStateManager.popMatrix();
            float f4 = 1.0F;
            float f5 = -((float)(d + 65.0));
            float f6 = -1.0F;
            worldRenderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
            worldRenderer.pos(-1.0, f5, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, f5, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, f5, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(-1.0, f5, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, f5, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, f5, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(-1.0, f5, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(-1.0, f5, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(-1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(-1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, -1.0, 1.0).color(0, 0, 0, 255).endVertex();
            worldRenderer.pos(1.0, -1.0, -1.0).color(0, 0, 0, 255).endVertex();
            instance.draw();
         }

         if (this.theWorld.provider.isSkyColored()) {
            GlStateManager.color(xCoord * 0.2F + 0.04F, yCoord * 0.2F + 0.04F, zCoord * 0.6F + 0.1F);
         } else {
            GlStateManager.color(xCoord, yCoord, zCoord);
         }

         if (this.mc.gameSettings.renderDistanceChunks <= 4) {
            GlStateManager.color(this.mc.entityRenderer.fogColorRed, this.mc.entityRenderer.fogColorGreen, this.mc.entityRenderer.fogColorBlue);
         }

         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, -((float)(d - 16.0)), 0.0F);
         FloatEvent5 floatEvent5 = new FloatEvent5(partialTicks);
         CheatBreaker.getInstance().getEventBus().handleEvent(floatEvent5);
         if (Config.isSkyEnabled() && !floatEvent5.isFlag()) {
            if (this.vboEnabled) {
               this.sky2VBO.bindBuffer();
               GlStateManager.glEnableClientState(32884);
               GlStateManager.glVertexPointer(3, 5126, 12, 0);
               this.sky2VBO.drawArrays(7);
               this.sky2VBO.unbindBuffer();
               GlStateManager.glDisableClientState(32884);
            } else {
               GlStateManager.callList(this.glSkyList2);
            }
         }

         GlStateManager.popMatrix();
         GlStateManager.enableTexture2D();
         if (isShaders) {
            Shaders.enableTexture2D();
         }

         GlStateManager.depthMask(true);
      }
   }

   public void renderClouds(float partialTicks, int pass) {
      if (!Config.isCloudsOff()) {
         if (Reflector.ForgeWorldProvider_getCloudRenderer.exists()) {
            WorldProvider provider = this.mc.theWorld.provider;
            Object obj = Reflector.call(provider, Reflector.ForgeWorldProvider_getCloudRenderer);
            if (obj != null) {
               Reflector.callVoid(obj, Reflector.IRenderHandler_render, partialTicks, this.theWorld, this.mc);
               return;
            }
         }

         if (this.mc.theWorld.provider.isSurfaceWorld()) {
            if (Config.isShaders()) {
               Shaders.beginClouds();
            }

            if (Config.isCloudsFancy()) {
               this.renderCloudsFancy(partialTicks, pass);
            } else {
               float partialTicks2 = partialTicks;
               partialTicks = 0.0F;
               GlStateManager.disableCull();
               float f = (float)(
                  this.mc.getRenderViewEntity().lastTickPosY + (this.mc.getRenderViewEntity().posY - this.mc.getRenderViewEntity().lastTickPosY) * partialTicks
               );
               byte b = 32;
               byte b2 = 8;
               Tessellator instance = Tessellator.getInstance();
               WorldRenderer worldRenderer = instance.getWorldRenderer();
               this.renderEngine.bindTexture(locationCloudsPng);
               GlStateManager.enableBlend();
               GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
               Vec3 cloudColour = this.theWorld.getCloudColour(partialTicks);
               float xCoord = (float)cloudColour.xCoord;
               float yCoord = (float)cloudColour.yCoord;
               float zCoord = (float)cloudColour.zCoord;
               this.cloudRenderer.prepareToRender(false, this.cloudTickCounter, partialTicks2, cloudColour);
               if (this.cloudRenderer.shouldUpdateGlList()) {
                  this.cloudRenderer.startUpdateGlList();
                  if (pass != 2) {
                     float f2 = (xCoord * 30.0F + yCoord * 59.0F + zCoord * 11.0F) / 100.0F;
                     float f3 = (xCoord * 30.0F + yCoord * 70.0F) / 100.0F;
                     float f4 = (xCoord * 30.0F + zCoord * 70.0F) / 100.0F;
                     xCoord = f2;
                     yCoord = f3;
                     zCoord = f4;
                  }

                  float f2 = 4.8828125E-4F;
                  double d = this.cloudTickCounter + partialTicks;
                  double d2 = this.mc.getRenderViewEntity().prevPosX
                     + (this.mc.getRenderViewEntity().posX - this.mc.getRenderViewEntity().prevPosX) * partialTicks
                     + d * 0.03F;
                  double d3 = this.mc.getRenderViewEntity().prevPosZ
                     + (this.mc.getRenderViewEntity().posZ - this.mc.getRenderViewEntity().prevPosZ) * partialTicks;
                  int n = MathHelper.floor_double(d2 / 2048.0);
                  int n2 = MathHelper.floor_double(d3 / 2048.0);
                  d2 -= n * 2048;
                  d3 -= n2 * 2048;
                  float f3 = this.theWorld.provider.getCloudHeight() - f + 0.33F;
                  f3 += this.mc.gameSettings.ofCloudsHeight * 128.0F;
                  float f4 = (float)(d2 * 4.8828125E-4);
                  float f5 = (float)(d3 * 4.8828125E-4);
                  worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);

                  for (int i = -256; i < 256; i += 32) {
                     for (int j = -256; j < 256; j += 32) {
                        worldRenderer.pos(i + 0, f3, j + 32)
                           .tex((i + 0) * 4.8828125E-4F + f4, (j + 32) * 4.8828125E-4F + f5)
                           .color(xCoord, yCoord, zCoord, 0.8F)
                           .endVertex();
                        worldRenderer.pos(i + 32, f3, j + 32)
                           .tex((i + 32) * 4.8828125E-4F + f4, (j + 32) * 4.8828125E-4F + f5)
                           .color(xCoord, yCoord, zCoord, 0.8F)
                           .endVertex();
                        worldRenderer.pos(i + 32, f3, j + 0)
                           .tex((i + 32) * 4.8828125E-4F + f4, (j + 0) * 4.8828125E-4F + f5)
                           .color(xCoord, yCoord, zCoord, 0.8F)
                           .endVertex();
                        worldRenderer.pos(i + 0, f3, j + 0)
                           .tex((i + 0) * 4.8828125E-4F + f4, (j + 0) * 4.8828125E-4F + f5)
                           .color(xCoord, yCoord, zCoord, 0.8F)
                           .endVertex();
                     }
                  }

                  instance.draw();
                  this.cloudRenderer.endUpdateGlList();
               }

               this.cloudRenderer.renderGlList();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.disableBlend();
               GlStateManager.enableCull();
            }

            if (Config.isShaders()) {
               Shaders.endClouds();
            }
         }
      }
   }

   public boolean hasCloudFog(double x, double y, double z, float partialTicks) {
      return false;
   }

   public void renderCloudsFancy(float partialTicks, int pass) {
      partialTicks = 0.0F;
      GlStateManager.disableCull();
      float f = (float)(
         this.mc.getRenderViewEntity().lastTickPosY + (this.mc.getRenderViewEntity().posY - this.mc.getRenderViewEntity().lastTickPosY) * partialTicks
      );
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      float f2 = 12.0F;
      float f3 = 4.0F;
      double d = this.cloudTickCounter + partialTicks;
      double d2 = (
            this.mc.getRenderViewEntity().prevPosX
               + (this.mc.getRenderViewEntity().posX - this.mc.getRenderViewEntity().prevPosX) * partialTicks
               + d * 0.03F
         )
         / 12.0;
      double d3 = (this.mc.getRenderViewEntity().prevPosZ + (this.mc.getRenderViewEntity().posZ - this.mc.getRenderViewEntity().prevPosZ) * partialTicks)
            / 12.0
         + 0.33F;
      float f4 = this.theWorld.provider.getCloudHeight() - f + 0.33F;
      f4 += this.mc.gameSettings.ofCloudsHeight * 128.0F;
      int n = MathHelper.floor_double(d2 / 2048.0);
      int n2 = MathHelper.floor_double(d3 / 2048.0);
      d2 -= n * 2048;
      d3 -= n2 * 2048;
      this.renderEngine.bindTexture(locationCloudsPng);
      GlStateManager.enableBlend();
      GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
      Vec3 cloudColour = this.theWorld.getCloudColour(partialTicks);
      float xCoord = (float)cloudColour.xCoord;
      float yCoord = (float)cloudColour.yCoord;
      float zCoord = (float)cloudColour.zCoord;
      this.cloudRenderer.prepareToRender(true, this.cloudTickCounter, partialTicks, cloudColour);
      if (pass != 2) {
         float f5 = (xCoord * 30.0F + yCoord * 59.0F + zCoord * 11.0F) / 100.0F;
         float f6 = (xCoord * 30.0F + yCoord * 70.0F) / 100.0F;
         float f7 = (xCoord * 30.0F + zCoord * 70.0F) / 100.0F;
         xCoord = f5;
         yCoord = f6;
         zCoord = f7;
      }

      float f5 = xCoord * 0.9F;
      float f6 = yCoord * 0.9F;
      float f7 = zCoord * 0.9F;
      float f8 = xCoord * 0.7F;
      float f9 = yCoord * 0.7F;
      float f10 = zCoord * 0.7F;
      float f11 = xCoord * 0.8F;
      float f12 = yCoord * 0.8F;
      float f13 = zCoord * 0.8F;
      float f14 = 0.00390625F;
      float f15 = MathHelper.floor_double(d2) * 0.00390625F;
      float f16 = MathHelper.floor_double(d3) * 0.00390625F;
      float f17 = (float)(d2 - MathHelper.floor_double(d2));
      float f18 = (float)(d3 - MathHelper.floor_double(d3));
      byte b = 8;
      byte b2 = 4;
      float f19 = 9.765625E-4F;
      GlStateManager.scale(12.0F, 1.0F, 12.0F);

      for (int i = 0; i < 2; i++) {
         if (i == 0) {
            GlStateManager.colorMask(false, false, false, false);
         } else {
            switch (pass) {
               case 0:
                  GlStateManager.colorMask(false, true, true, true);
                  break;
               case 1:
                  GlStateManager.colorMask(true, false, false, true);
                  break;
               case 2:
                  GlStateManager.colorMask(true, true, true, true);
            }
         }

         this.cloudRenderer.renderGlList();
      }

      if (this.cloudRenderer.shouldUpdateGlList()) {
         this.cloudRenderer.startUpdateGlList();

         for (int i = -3; i <= 4; i++) {
            for (int j = -3; j <= 4; j++) {
               worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
               float f20 = i * 8;
               float f21 = j * 8;
               float f22 = f20 - f17;
               float f23 = f21 - f18;
               if (f4 > -5.0F) {
                  worldRenderer.pos(f22 + 0.0F, f4 + 0.0F, f23 + 8.0F)
                     .tex((f20 + 0.0F) * 0.00390625F + f15, (f21 + 8.0F) * 0.00390625F + f16)
                     .color(f8, f9, f10, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
                  worldRenderer.pos(f22 + 8.0F, f4 + 0.0F, f23 + 8.0F)
                     .tex((f20 + 8.0F) * 0.00390625F + f15, (f21 + 8.0F) * 0.00390625F + f16)
                     .color(f8, f9, f10, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
                  worldRenderer.pos(f22 + 8.0F, f4 + 0.0F, f23 + 0.0F)
                     .tex((f20 + 8.0F) * 0.00390625F + f15, (f21 + 0.0F) * 0.00390625F + f16)
                     .color(f8, f9, f10, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
                  worldRenderer.pos(f22 + 0.0F, f4 + 0.0F, f23 + 0.0F)
                     .tex((f20 + 0.0F) * 0.00390625F + f15, (f21 + 0.0F) * 0.00390625F + f16)
                     .color(f8, f9, f10, 0.8F)
                     .normal(0.0F, -1.0F, 0.0F)
                     .endVertex();
               }

               if (f4 <= 5.0F) {
                  worldRenderer.pos(f22 + 0.0F, f4 + 4.0F - 9.765625E-4F, f23 + 8.0F)
                     .tex((f20 + 0.0F) * 0.00390625F + f15, (f21 + 8.0F) * 0.00390625F + f16)
                     .color(xCoord, yCoord, zCoord, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
                  worldRenderer.pos(f22 + 8.0F, f4 + 4.0F - 9.765625E-4F, f23 + 8.0F)
                     .tex((f20 + 8.0F) * 0.00390625F + f15, (f21 + 8.0F) * 0.00390625F + f16)
                     .color(xCoord, yCoord, zCoord, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
                  worldRenderer.pos(f22 + 8.0F, f4 + 4.0F - 9.765625E-4F, f23 + 0.0F)
                     .tex((f20 + 8.0F) * 0.00390625F + f15, (f21 + 0.0F) * 0.00390625F + f16)
                     .color(xCoord, yCoord, zCoord, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
                  worldRenderer.pos(f22 + 0.0F, f4 + 4.0F - 9.765625E-4F, f23 + 0.0F)
                     .tex((f20 + 0.0F) * 0.00390625F + f15, (f21 + 0.0F) * 0.00390625F + f16)
                     .color(xCoord, yCoord, zCoord, 0.8F)
                     .normal(0.0F, 1.0F, 0.0F)
                     .endVertex();
               }

               if (i > -1) {
                  for (int k = 0; k < 8; k++) {
                     worldRenderer.pos(f22 + k + 0.0F, f4 + 0.0F, f23 + 8.0F)
                        .tex((f20 + k + 0.5F) * 0.00390625F + f15, (f21 + 8.0F) * 0.00390625F + f16)
                        .color(f5, f6, f7, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + k + 0.0F, f4 + 4.0F, f23 + 8.0F)
                        .tex((f20 + k + 0.5F) * 0.00390625F + f15, (f21 + 8.0F) * 0.00390625F + f16)
                        .color(f5, f6, f7, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + k + 0.0F, f4 + 4.0F, f23 + 0.0F)
                        .tex((f20 + k + 0.5F) * 0.00390625F + f15, (f21 + 0.0F) * 0.00390625F + f16)
                        .color(f5, f6, f7, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + k + 0.0F, f4 + 0.0F, f23 + 0.0F)
                        .tex((f20 + k + 0.5F) * 0.00390625F + f15, (f21 + 0.0F) * 0.00390625F + f16)
                        .color(f5, f6, f7, 0.8F)
                        .normal(-1.0F, 0.0F, 0.0F)
                        .endVertex();
                  }
               }

               if (i <= 1) {
                  for (int k = 0; k < 8; k++) {
                     worldRenderer.pos(f22 + k + 1.0F - 9.765625E-4F, f4 + 0.0F, f23 + 8.0F)
                        .tex((f20 + k + 0.5F) * 0.00390625F + f15, (f21 + 8.0F) * 0.00390625F + f16)
                        .color(f5, f6, f7, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + k + 1.0F - 9.765625E-4F, f4 + 4.0F, f23 + 8.0F)
                        .tex((f20 + k + 0.5F) * 0.00390625F + f15, (f21 + 8.0F) * 0.00390625F + f16)
                        .color(f5, f6, f7, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + k + 1.0F - 9.765625E-4F, f4 + 4.0F, f23 + 0.0F)
                        .tex((f20 + k + 0.5F) * 0.00390625F + f15, (f21 + 0.0F) * 0.00390625F + f16)
                        .color(f5, f6, f7, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + k + 1.0F - 9.765625E-4F, f4 + 0.0F, f23 + 0.0F)
                        .tex((f20 + k + 0.5F) * 0.00390625F + f15, (f21 + 0.0F) * 0.00390625F + f16)
                        .color(f5, f6, f7, 0.8F)
                        .normal(1.0F, 0.0F, 0.0F)
                        .endVertex();
                  }
               }

               if (j > -1) {
                  for (int k = 0; k < 8; k++) {
                     worldRenderer.pos(f22 + 0.0F, f4 + 4.0F, f23 + k + 0.0F)
                        .tex((f20 + 0.0F) * 0.00390625F + f15, (f21 + k + 0.5F) * 0.00390625F + f16)
                        .color(f11, f12, f13, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + 8.0F, f4 + 4.0F, f23 + k + 0.0F)
                        .tex((f20 + 8.0F) * 0.00390625F + f15, (f21 + k + 0.5F) * 0.00390625F + f16)
                        .color(f11, f12, f13, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + 8.0F, f4 + 0.0F, f23 + k + 0.0F)
                        .tex((f20 + 8.0F) * 0.00390625F + f15, (f21 + k + 0.5F) * 0.00390625F + f16)
                        .color(f11, f12, f13, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + 0.0F, f4 + 0.0F, f23 + k + 0.0F)
                        .tex((f20 + 0.0F) * 0.00390625F + f15, (f21 + k + 0.5F) * 0.00390625F + f16)
                        .color(f11, f12, f13, 0.8F)
                        .normal(0.0F, 0.0F, -1.0F)
                        .endVertex();
                  }
               }

               if (j <= 1) {
                  for (int k = 0; k < 8; k++) {
                     worldRenderer.pos(f22 + 0.0F, f4 + 4.0F, f23 + k + 1.0F - 9.765625E-4F)
                        .tex((f20 + 0.0F) * 0.00390625F + f15, (f21 + k + 0.5F) * 0.00390625F + f16)
                        .color(f11, f12, f13, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + 8.0F, f4 + 4.0F, f23 + k + 1.0F - 9.765625E-4F)
                        .tex((f20 + 8.0F) * 0.00390625F + f15, (f21 + k + 0.5F) * 0.00390625F + f16)
                        .color(f11, f12, f13, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + 8.0F, f4 + 0.0F, f23 + k + 1.0F - 9.765625E-4F)
                        .tex((f20 + 8.0F) * 0.00390625F + f15, (f21 + k + 0.5F) * 0.00390625F + f16)
                        .color(f11, f12, f13, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                     worldRenderer.pos(f22 + 0.0F, f4 + 0.0F, f23 + k + 1.0F - 9.765625E-4F)
                        .tex((f20 + 0.0F) * 0.00390625F + f15, (f21 + k + 0.5F) * 0.00390625F + f16)
                        .color(f11, f12, f13, 0.8F)
                        .normal(0.0F, 0.0F, 1.0F)
                        .endVertex();
                  }
               }

               instance.draw();
            }
         }

         this.cloudRenderer.endUpdateGlList();
      }

      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.enableCull();
   }

   public void updateChunks(long finishTimeNano) {
      finishTimeNano = (long)(finishTimeNano + 1.0E8);
      this.displayListEntitiesDirty = this.displayListEntitiesDirty | this.renderDispatcher.runChunkUploads(finishTimeNano);
      if (this.chunksToUpdateForced.size() > 0) {
         Iterator iterator = this.chunksToUpdateForced.iterator();

         while (iterator.hasNext()) {
            RenderChunk renderChunk = (RenderChunk)iterator.next();
            if (!this.renderDispatcher.updateChunkLater(renderChunk)) {
               break;
            }

            renderChunk.setNeedsUpdate(false);
            iterator.remove();
            this.chunksToUpdate.remove(renderChunk);
            this.chunksToResortTransparency.remove(renderChunk);
         }
      }

      if (this.chunksToResortTransparency.size() > 0) {
         Iterator iterator = this.chunksToResortTransparency.iterator();
         if (iterator.hasNext()) {
            RenderChunk renderChunk = (RenderChunk)iterator.next();
            if (this.renderDispatcher.updateTransparencyLater(renderChunk)) {
               iterator.remove();
            }
         }
      }

      double d = 0.0;
      int updatesPerFrame = Config.getUpdatesPerFrame();
      if (!this.chunksToUpdate.isEmpty()) {
         Iterator iterator = this.chunksToUpdate.iterator();
         int n = 1;
         if (CheatBreaker.getInstance().getKeybindSettings().enableFpsBoost.getBooleanValue()) {
            n = (int)CheatBreaker.getInstance().getKeybindSettings().slowChunkLoading.getFloatValue();
         }

         while (iterator.hasNext()) {
            if (n != 1) {
               if (this.intValue > 0) {
                  this.intValue--;
                  break;
               }

               this.intValue = n;
            }

            RenderChunk renderChunk = (RenderChunk)iterator.next();
            boolean isChunkRegionEmpty = renderChunk.isChunkRegionEmpty();
            boolean flag;
            if (isChunkRegionEmpty) {
               flag = this.renderDispatcher.updateChunkNow(renderChunk);
            } else {
               flag = this.renderDispatcher.updateChunkLater(renderChunk);
            }

            if (!flag) {
               break;
            }

            renderChunk.setNeedsUpdate(false);
            iterator.remove();
            if (!isChunkRegionEmpty) {
               double d2 = 2.0 * RenderChunkUtils.getRelativeBufferSize(renderChunk);
               d += d2;
               if (d > updatesPerFrame) {
                  break;
               }
            }
         }
      }
   }

   public void renderWorldBorder(Entity entityIn, float partialTicks) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      WorldBorder worldBorder = this.theWorld.getWorldBorder();
      double d = this.mc.gameSettings.renderDistanceChunks * 16;
      if (entityIn.posX >= worldBorder.maxX() - d
         || entityIn.posX <= worldBorder.minX() + d
         || entityIn.posZ >= worldBorder.maxZ() - d
         || entityIn.posZ <= worldBorder.minZ() + d) {
         if (Config.isShaders()) {
            Shaders.pushProgram();
            Shaders.useProgram(Shaders.ProgramTexturedLit);
         }

         double d2 = 1.0 - worldBorder.getClosestDistance(entityIn) / d;
         d2 = Math.pow(d2, 4.0);
         double d3 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
         double d4 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
         double d5 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 1, 1, 0);
         this.renderEngine.bindTexture(locationForcefieldPng);
         GlStateManager.depthMask(false);
         GlStateManager.pushMatrix();
         int iD = worldBorder.getStatus().getID();
         float f = (iD >> 16 & 0xFF) / 255.0F;
         float f2 = (iD >> 8 & 0xFF) / 255.0F;
         float f3 = (iD & 0xFF) / 255.0F;
         GlStateManager.color(f, f2, f3, (float)d2);
         GlStateManager.doPolygonOffset(-3.0F, -3.0F);
         GlStateManager.enablePolygonOffset();
         GlStateManager.alphaFunc(516, 0.1F);
         GlStateManager.enableAlpha();
         GlStateManager.disableCull();
         float f4 = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F;
         float f5 = 0.0F;
         float f6 = 0.0F;
         float f7 = 128.0F;
         worldRenderer.begin(7, DefaultVertexFormats.POSITION_TEX);
         worldRenderer.setTranslation(-d3, -d4, -d5);
         double max = Math.max(MathHelper.floor_double(d5 - d), worldBorder.minZ());
         double min = Math.min(MathHelper.ceiling_double_int(d5 + d), worldBorder.maxZ());
         if (d3 > worldBorder.maxX() - d) {
            float f8 = 0.0F;

            for (double d6 = max; d6 < min; f8 += 0.5F) {
               double min2 = Math.min(1.0, min - d6);
               float f9 = (float)min2 * 0.5F;
               worldRenderer.pos(worldBorder.maxX(), 256.0, d6).tex(f4 + f8, f4 + 0.0F).endVertex();
               worldRenderer.pos(worldBorder.maxX(), 256.0, d6 + min2).tex(f4 + f9 + f8, f4 + 0.0F).endVertex();
               worldRenderer.pos(worldBorder.maxX(), 0.0, d6 + min2).tex(f4 + f9 + f8, f4 + 128.0F).endVertex();
               worldRenderer.pos(worldBorder.maxX(), 0.0, d6).tex(f4 + f8, f4 + 128.0F).endVertex();
               d6++;
            }
         }

         if (d3 < worldBorder.minX() + d) {
            float f8 = 0.0F;

            for (double d6 = max; d6 < min; f8 += 0.5F) {
               double min2 = Math.min(1.0, min - d6);
               float f9 = (float)min2 * 0.5F;
               worldRenderer.pos(worldBorder.minX(), 256.0, d6).tex(f4 + f8, f4 + 0.0F).endVertex();
               worldRenderer.pos(worldBorder.minX(), 256.0, d6 + min2).tex(f4 + f9 + f8, f4 + 0.0F).endVertex();
               worldRenderer.pos(worldBorder.minX(), 0.0, d6 + min2).tex(f4 + f9 + f8, f4 + 128.0F).endVertex();
               worldRenderer.pos(worldBorder.minX(), 0.0, d6).tex(f4 + f8, f4 + 128.0F).endVertex();
               d6++;
            }
         }

         max = Math.max(MathHelper.floor_double(d3 - d), worldBorder.minX());
         min = Math.min(MathHelper.ceiling_double_int(d3 + d), worldBorder.maxX());
         if (d5 > worldBorder.maxZ() - d) {
            float f8 = 0.0F;

            for (double d6 = max; d6 < min; f8 += 0.5F) {
               double min2 = Math.min(1.0, min - d6);
               float f9 = (float)min2 * 0.5F;
               worldRenderer.pos(d6, 256.0, worldBorder.maxZ()).tex(f4 + f8, f4 + 0.0F).endVertex();
               worldRenderer.pos(d6 + min2, 256.0, worldBorder.maxZ()).tex(f4 + f9 + f8, f4 + 0.0F).endVertex();
               worldRenderer.pos(d6 + min2, 0.0, worldBorder.maxZ()).tex(f4 + f9 + f8, f4 + 128.0F).endVertex();
               worldRenderer.pos(d6, 0.0, worldBorder.maxZ()).tex(f4 + f8, f4 + 128.0F).endVertex();
               d6++;
            }
         }

         if (d5 < worldBorder.minZ() + d) {
            float f8 = 0.0F;

            for (double d6 = max; d6 < min; f8 += 0.5F) {
               double min2 = Math.min(1.0, min - d6);
               float f9 = (float)min2 * 0.5F;
               worldRenderer.pos(d6, 256.0, worldBorder.minZ()).tex(f4 + f8, f4 + 0.0F).endVertex();
               worldRenderer.pos(d6 + min2, 256.0, worldBorder.minZ()).tex(f4 + f9 + f8, f4 + 0.0F).endVertex();
               worldRenderer.pos(d6 + min2, 0.0, worldBorder.minZ()).tex(f4 + f9 + f8, f4 + 128.0F).endVertex();
               worldRenderer.pos(d6, 0.0, worldBorder.minZ()).tex(f4 + f8, f4 + 128.0F).endVertex();
               d6++;
            }
         }

         instance.draw();
         worldRenderer.setTranslation(0.0, 0.0, 0.0);
         GlStateManager.enableCull();
         GlStateManager.disableAlpha();
         GlStateManager.doPolygonOffset(0.0F, 0.0F);
         GlStateManager.disablePolygonOffset();
         GlStateManager.enableAlpha();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.disableBlend();
         GlStateManager.popMatrix();
         GlStateManager.depthMask(true);
         if (Config.isShaders()) {
            Shaders.popProgram();
         }
      }
   }

   public void preRenderDamagedBlocks() {
      GlStateManager.tryBlendFuncSeparate(774, 768, 1, 0);
      GlStateManager.enableBlend();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
      GlStateManager.doPolygonOffset(-1.0F, -10.0F);
      GlStateManager.enablePolygonOffset();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableAlpha();
      GlStateManager.pushMatrix();
      if (Config.isShaders()) {
         ShadersRender.beginBlockDamage();
      }
   }

   public void postRenderDamagedBlocks() {
      GlStateManager.disableAlpha();
      GlStateManager.doPolygonOffset(0.0F, 0.0F);
      GlStateManager.disablePolygonOffset();
      GlStateManager.enableAlpha();
      GlStateManager.depthMask(true);
      GlStateManager.popMatrix();
      if (Config.isShaders()) {
         ShadersRender.endBlockDamage();
      }
   }

   public void drawBlockDamageTexture(Tessellator tessellatorIn, WorldRenderer worldRendererIn, Entity entityIn, float partialTicks) {
      double d = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * partialTicks;
      double d2 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * partialTicks;
      double d3 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * partialTicks;
      if (!this.damagedBlocks.isEmpty()) {
         this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
         this.preRenderDamagedBlocks();
         worldRendererIn.begin(7, DefaultVertexFormats.BLOCK);
         worldRendererIn.setTranslation(-d, -d2, -d3);
         worldRendererIn.noColor();
         Iterator iterator = this.damagedBlocks.values().iterator();

         while (iterator.hasNext()) {
            DestroyBlockProgress destroyBlockProgress = (DestroyBlockProgress)iterator.next();
            BlockPos position = destroyBlockProgress.getPosition();
            double d4 = position.getX() - d;
            double d5 = position.getY() - d2;
            double d6 = position.getZ() - d3;
            Block block = this.theWorld.getBlockState(position).getBlock();
            boolean flag;
            if (Reflector.ForgeTileEntity_canRenderBreaking.exists()) {
               boolean flag2 = block instanceof BlockChest || block instanceof BlockEnderChest || block instanceof BlockSign || block instanceof BlockSkull;
               if (!flag2) {
                  TileEntity tileEntity = this.theWorld.getTileEntity(position);
                  if (tileEntity != null) {
                     flag2 = Reflector.callBoolean(tileEntity, Reflector.ForgeTileEntity_canRenderBreaking);
                  }
               }

               flag = !flag2;
            } else {
               flag = !(block instanceof BlockChest) && !(block instanceof BlockEnderChest) && !(block instanceof BlockSign) && !(block instanceof BlockSkull);
            }

            if (flag) {
               if (d4 * d4 + d5 * d5 + d6 * d6 > 1024.0) {
                  iterator.remove();
               } else {
                  IBlockState blockState = this.theWorld.getBlockState(position);
                  if (blockState.getBlock().getMaterial() != Material.air) {
                     int partialBlockDamage = destroyBlockProgress.getPartialBlockDamage();
                     TextureAtlasSprite textureAtlasSprite = this.destroyBlockIcons[partialBlockDamage];
                     BlockRendererDispatcher blockRendererDispatcher = this.mc.getBlockRendererDispatcher();
                     blockRendererDispatcher.renderBlockDamage(blockState, position, textureAtlasSprite, this.theWorld);
                  }
               }
            }
         }

         tessellatorIn.draw();
         worldRendererIn.setTranslation(0.0, 0.0, 0.0);
         this.postRenderDamagedBlocks();
      }
   }

   public void drawSelectionBox(EntityPlayer player, MovingObjectPosition movingObjectPositionIn, int execute, float partialTicks) {
      if (execute == 0 && movingObjectPositionIn.typeOfHit == MovingObjectType.BLOCK) {
         GlStateManager.enableBlend();
         GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
         GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
         float f = 2.0F;
         FloatEvent8 floatEvent8 = new FloatEvent8(f);
         CheatBreaker.getInstance().getEventBus().handleEvent(floatEvent8);
         f = floatEvent8.getFloatValue();
         GL11.glLineWidth(f);
         GlStateManager.disableTexture2D();
         if (Config.isShaders()) {
            Shaders.disableTexture2D();
         }

         GlStateManager.depthMask(false);
         CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent15());
         double d = 0.002F;
         DoubleEvent2 doubleEvent2 = new DoubleEvent2(d);
         CheatBreaker.getInstance().getEventBus().handleEvent(doubleEvent2);
         d = doubleEvent2.getDoubleValue();
         BlockPos blockPos = movingObjectPositionIn.getBlockPos();
         Block block = this.theWorld.getBlockState(blockPos).getBlock();
         BlockEvent blockEvent = new BlockEvent(block);
         CheatBreaker.getInstance().getEventBus().handleEvent(blockEvent);
         if (!blockEvent.isFlag() && block.getMaterial() != Material.air && this.theWorld.getWorldBorder().contains(blockPos)) {
            block.setBlockBoundsBasedOnState(this.theWorld, blockPos);
            double d2 = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
            double d3 = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
            double d4 = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;
            AxisAlignedBB selectedBoundingBox = block.getSelectedBoundingBox(this.theWorld, blockPos);
            EnumOffsetType offsetType = block.getOffsetType();
            if (offsetType != EnumOffsetType.NONE) {
               selectedBoundingBox = BlockModelUtils.getOffsetBoundingBox(selectedBoundingBox, offsetType, blockPos);
            }

            SimpleEvent21 simpleEvent21 = new SimpleEvent21();
            CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent21);
            if (!simpleEvent21.isFlag()) {
               CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent2());
               drawSelectionBoundingBox(selectedBoundingBox.expand(d, d, d).offset(-d2, -d3, -d4));
               CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent19());
            }

            CheatBreaker.getInstance().getEventBus().handleEvent(new AxisAlignedBBDoubleEvent(selectedBoundingBox, d, d2, d3, d4));
         }

         GlStateManager.depthMask(true);
         GlStateManager.enableTexture2D();
         if (Config.isShaders()) {
            Shaders.enableTexture2D();
         }

         GlStateManager.enableDepth();
         GlStateManager.disableBlend();
      }
   }

   public static void drawSelectionBoundingBox(AxisAlignedBB boundingBox) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(3, DefaultVertexFormats.POSITION);
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
      instance.draw();
      worldRenderer.begin(3, DefaultVertexFormats.POSITION);
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
      instance.draw();
      worldRenderer.begin(1, DefaultVertexFormats.POSITION);
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).endVertex();
      instance.draw();
   }

   public static void drawOutlinedBoundingBox(AxisAlignedBB boundingBox, int red, int green, int blue, int alpha) {
      Tessellator instance = Tessellator.getInstance();
      WorldRenderer worldRenderer = instance.getWorldRenderer();
      worldRenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      instance.draw();
      worldRenderer.begin(3, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      instance.draw();
      worldRenderer.begin(1, DefaultVertexFormats.POSITION_COLOR);
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.minZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.minY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.minY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
      worldRenderer.pos(boundingBox.minX, boundingBox.maxY, boundingBox.maxZ).color(red, green, blue, alpha).endVertex();
      instance.draw();
   }

   public void markBlocksForUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
      this.viewFrustum.markBlocksForUpdate(x1, y1, z1, x2, y2, z2);
   }

   @Override
   public void markBlockForUpdate(BlockPos pos) {
      int n = pos.getX();
      int n2 = pos.getY();
      int n3 = pos.getZ();
      this.markBlocksForUpdate(n - 1, n2 - 1, n3 - 1, n + 1, n2 + 1, n3 + 1);
   }

   @Override
   public void notifyLightSet(BlockPos pos) {
      int n = pos.getX();
      int n2 = pos.getY();
      int n3 = pos.getZ();
      this.markBlocksForUpdate(n - 1, n2 - 1, n3 - 1, n + 1, n2 + 1, n3 + 1);
   }

   @Override
   public void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2) {
      this.markBlocksForUpdate(x1 - 1, y1 - 1, z1 - 1, x2 + 1, y2 + 1, z2 + 1);
   }

   @Override
   public void playRecord(String recordName, BlockPos blockPosIn) {
      ISound sound = this.mapSoundPositions.get(blockPosIn);
      if (sound != null) {
         this.mc.getSoundHandler().stopSound(sound);
         this.mapSoundPositions.remove(blockPosIn);
      }

      if (recordName != null) {
         ItemRecord record2 = ItemRecord.getRecord(recordName);
         if (record2 != null) {
            this.mc.ingameGUI.setRecordPlayingMessage(record2.getRecordNameLocal());
         }

         PositionedSoundRecord positionedSoundRecord = PositionedSoundRecord.create(new ResourceLocation(recordName), blockPosIn.getX(), blockPosIn.getY(), blockPosIn.getZ());
         this.mapSoundPositions.put(blockPosIn, positionedSoundRecord);
         this.mc.getSoundHandler().playSound(positionedSoundRecord);
      }
   }

   @Override
   public void playSound(String soundName, double x, double y, double z, float volume, float pitch) {
   }

   @Override
   public void playSoundToNearExcept(EntityPlayer except, String soundName, double x, double y, double z, float volume, float pitch) {
   }

   @Override
   public void spawnParticle(
      int particleID,
      boolean ignoreRange,
      final double xCoord,
      final double yCoord,
      final double zCoord,
      double xOffset,
      double yOffset,
      double zOffset,
      int... parameters
   ) {
      try {
         this.spawnEntityFX(particleID, ignoreRange, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, parameters);
      } catch (Throwable e) {
         CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Exception while adding particle");
         CrashReportCategory makeCategory = makeCrashReport.makeCategory("Particle being added");
         makeCategory.addCrashSection("ID", particleID);
         if (parameters != null) {
            makeCategory.addCrashSection("Parameters", parameters);
         }

         makeCategory.addCrashSectionCallable("Position", new Callable<String>() {
            public String call() throws Exception {
               return CrashReportCategory.getCoordinateInfo(xCoord, yCoord, zCoord);
            }
         });
         throw new ReportedException(makeCrashReport);
      }
   }

   public void spawnParticle(
      EnumParticleTypes particleIn, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... parameters
   ) {
      this.spawnParticle(particleIn.getParticleID(), particleIn.getShouldIgnoreRange(), xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, parameters);
   }

   public EntityFX spawnEntityFX(
      int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... parameters
   ) {
      if (this.mc != null && this.mc.getRenderViewEntity() != null && this.mc.effectRenderer != null) {
         int particleSetting = this.mc.gameSettings.particleSetting;
         if (particleSetting == 1 && this.theWorld.rand.nextInt(3) == 0) {
            particleSetting = 2;
         }

         double d = this.mc.getRenderViewEntity().posX - xCoord;
         double d2 = this.mc.getRenderViewEntity().posY - yCoord;
         double d3 = this.mc.getRenderViewEntity().posZ - zCoord;
         if (particleID == EnumParticleTypes.EXPLOSION_HUGE.getParticleID() && !Config.isAnimatedExplosion()) {
            return null;
         }

         if (particleID == EnumParticleTypes.EXPLOSION_LARGE.getParticleID() && !Config.isAnimatedExplosion()) {
            return null;
         }

         if (particleID == EnumParticleTypes.EXPLOSION_NORMAL.getParticleID() && !Config.isAnimatedExplosion()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SUSPENDED.getParticleID() && !Config.isWaterParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SUSPENDED_DEPTH.getParticleID() && !Config.isVoidParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SMOKE_NORMAL.getParticleID() && !Config.isAnimatedSmoke()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SMOKE_LARGE.getParticleID() && !Config.isAnimatedSmoke()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SPELL_MOB.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SPELL_MOB_AMBIENT.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SPELL.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SPELL_INSTANT.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.SPELL_WITCH.getParticleID() && !Config.isPotionParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.PORTAL.getParticleID() && !Config.isPortalParticles()) {
            return null;
         }

         if (particleID == EnumParticleTypes.FLAME.getParticleID() && !Config.isAnimatedFlame()) {
            return null;
         }

         if (particleID == EnumParticleTypes.REDSTONE.getParticleID() && !Config.isAnimatedRedstone()) {
            return null;
         }

         if (particleID == EnumParticleTypes.DRIP_WATER.getParticleID() && !Config.isDrippingWaterLava()) {
            return null;
         }

         if (particleID == EnumParticleTypes.DRIP_LAVA.getParticleID() && !Config.isDrippingWaterLava()) {
            return null;
         }

         if (particleID == EnumParticleTypes.FIREWORKS_SPARK.getParticleID() && !Config.isFireworkParticles()) {
            return null;
         }

         if (!ignoreRange) {
            double d4 = 256.0;
            if (particleID == EnumParticleTypes.CRIT.getParticleID()) {
               d4 = 38416.0;
            }

            if (d * d + d2 * d2 + d3 * d3 > d4) {
               return null;
            }

            if (particleSetting > 1) {
               return null;
            }
         }

         EntityFX spawnEffectParticle = this.mc.effectRenderer.spawnEffectParticle(particleID, xCoord, yCoord, zCoord, xOffset, yOffset, zOffset, parameters);
         if (particleID == EnumParticleTypes.WATER_BUBBLE.getParticleID()) {
            CustomColors.updateWaterFX(spawnEffectParticle, this.theWorld, xCoord, yCoord, zCoord, this.renderEnv);
         }

         if (particleID == EnumParticleTypes.WATER_SPLASH.getParticleID()) {
            CustomColors.updateWaterFX(spawnEffectParticle, this.theWorld, xCoord, yCoord, zCoord, this.renderEnv);
         }

         if (particleID == EnumParticleTypes.WATER_DROP.getParticleID()) {
            CustomColors.updateWaterFX(spawnEffectParticle, this.theWorld, xCoord, yCoord, zCoord, this.renderEnv);
         }

         if (particleID == EnumParticleTypes.TOWN_AURA.getParticleID()) {
            CustomColors.updateMyceliumFX(spawnEffectParticle);
         }

         if (particleID == EnumParticleTypes.PORTAL.getParticleID()) {
            CustomColors.updatePortalFX(spawnEffectParticle);
         }

         if (particleID == EnumParticleTypes.REDSTONE.getParticleID()) {
            CustomColors.updateReddustFX(spawnEffectParticle, this.theWorld, xCoord, yCoord, zCoord);
         }

         return spawnEffectParticle;
      } else {
         return null;
      }
   }

   @Override
   public void onEntityAdded(Entity entityIn) {
      RandomEntities.entityLoaded(entityIn, this.theWorld);
      if (Config.isDynamicLights()) {
         DynamicLights.entityAdded(entityIn, this);
      }
   }

   @Override
   public void onEntityRemoved(Entity entityIn) {
      RandomEntities.entityUnloaded(entityIn, this.theWorld);
      if (Config.isDynamicLights()) {
         DynamicLights.entityRemoved(entityIn, this);
      }
   }

   public void deleteAllDisplayLists() {
   }

   @Override
   public void broadcastSound(int soundID, BlockPos pos, int data) {
      switch (soundID) {
         case 1013:
         case 1018:
            if (this.mc.getRenderViewEntity() != null) {
               double d = pos.getX() - this.mc.getRenderViewEntity().posX;
               double d2 = pos.getY() - this.mc.getRenderViewEntity().posY;
               double d3 = pos.getZ() - this.mc.getRenderViewEntity().posZ;
               double sqrt = Math.sqrt(d * d + d2 * d2 + d3 * d3);
               double posX = this.mc.getRenderViewEntity().posX;
               double posY = this.mc.getRenderViewEntity().posY;
               double posZ = this.mc.getRenderViewEntity().posZ;
               if (sqrt > 0.0) {
                  posX += d / sqrt * 2.0;
                  posY += d2 / sqrt * 2.0;
                  posZ += d3 / sqrt * 2.0;
               }

               if (soundID == 1013) {
                  this.theWorld.playSound(posX, posY, posZ, "mob.wither.spawn", 1.0F, 1.0F, false);
               } else {
                  this.theWorld.playSound(posX, posY, posZ, "mob.enderdragon.end", 5.0F, 1.0F, false);
               }
            }
      }
   }

   @Override
   public void playAuxSFX(EntityPlayer player, int sfxType, BlockPos blockPosIn, int data) {
      Random rand = this.theWorld.rand;
      switch (sfxType) {
         case 1000:
            this.theWorld.playSoundAtPos(blockPosIn, "random.click", 1.0F, 1.0F, false);
            break;
         case 1001:
            this.theWorld.playSoundAtPos(blockPosIn, "random.click", 1.0F, 1.2F, false);
            break;
         case 1002:
            this.theWorld.playSoundAtPos(blockPosIn, "random.bow", 1.0F, 1.2F, false);
            break;
         case 1003:
            this.theWorld.playSoundAtPos(blockPosIn, "random.door_open", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1004:
            this.theWorld.playSoundAtPos(blockPosIn, "random.fizz", 0.5F, 2.6F + (rand.nextFloat() - rand.nextFloat()) * 0.8F, false);
            break;
         case 1005:
            if (Item.getItemById(data) instanceof ItemRecord) {
               this.theWorld.playRecord(blockPosIn, "records." + ((ItemRecord)Item.getItemById(data)).recordName);
            } else {
               this.theWorld.playRecord(blockPosIn, (String)null);
            }
            break;
         case 1006:
            this.theWorld.playSoundAtPos(blockPosIn, "random.door_close", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1007:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.ghast.charge", 10.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1008:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.ghast.fireball", 10.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1009:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.ghast.fireball", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1010:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.wood", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1011:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.metal", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1012:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.woodbreak", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1014:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.wither.shoot", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1015:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.bat.takeoff", 0.05F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1016:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.infect", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1017:
            this.theWorld.playSoundAtPos(blockPosIn, "mob.zombie.unfect", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
            break;
         case 1020:
            this.theWorld.playSoundAtPos(blockPosIn, "random.anvil_break", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1021:
            this.theWorld.playSoundAtPos(blockPosIn, "random.anvil_use", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 1022:
            this.theWorld.playSoundAtPos(blockPosIn, "random.anvil_land", 0.3F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 2000:
            int n = data % 3 - 1;
            int n2 = data / 3 % 3 - 1;
            double d = blockPosIn.getX() + n * 0.6 + 0.5;
            double d2 = blockPosIn.getY() + 0.5;
            double d3 = blockPosIn.getZ() + n2 * 0.6 + 0.5;

            for (int i = 0; i < 10; i++) {
               double d4 = rand.nextDouble() * 0.2 + 0.01;
               double d5 = d + n * 0.01 + (rand.nextDouble() - 0.5) * n2 * 0.5;
               double d6 = d2 + (rand.nextDouble() - 0.5) * 0.5;
               double d7 = d3 + n2 * 0.01 + (rand.nextDouble() - 0.5) * n * 0.5;
               double d8 = n * d4 + rand.nextGaussian() * 0.01;
               double d9 = -0.03 + rand.nextGaussian() * 0.01;
               double d10 = n2 * d4 + rand.nextGaussian() * 0.01;
               this.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d5, d6, d7, d8, d9, d10);
            }

            return;
         case 2001:
            Block blockById = Block.getBlockById(data & 4095);
            if (blockById.getMaterial() != Material.air) {
               this.mc
                  .getSoundHandler()
                  .playSound(
                     new PositionedSoundRecord(
                        new ResourceLocation(blockById.stepSound.getBreakSound()),
                        (blockById.stepSound.getVolume() + 1.0F) / 2.0F,
                        blockById.stepSound.getFrequency() * 0.8F,
                        blockPosIn.getX() + 0.5F,
                        blockPosIn.getY() + 0.5F,
                        blockPosIn.getZ() + 0.5F
                     )
                  );
            }

            this.mc.effectRenderer.addBlockDestroyEffects(blockPosIn, blockById.getStateFromMeta(data >> 12 & 0xFF));
            break;
         case 2002:
            double d4 = blockPosIn.getX();
            double d5 = blockPosIn.getY();
            double d6 = blockPosIn.getZ();

            for (int i = 0; i < 8; i++) {
               this.spawnParticle(
                  EnumParticleTypes.ITEM_CRACK,
                  d4,
                  d5,
                  d6,
                  rand.nextGaussian() * 0.15,
                  rand.nextDouble() * 0.2,
                  rand.nextGaussian() * 0.15,
                  Item.getIdFromItem(Items.potionitem),
                  data
               );
            }

            int colorFromDamage = Items.potionitem.getColorFromDamage(data);
            float f = (colorFromDamage >> 16 & 0xFF) / 255.0F;
            float f2 = (colorFromDamage >> 8 & 0xFF) / 255.0F;
            float f3 = (colorFromDamage >> 0 & 0xFF) / 255.0F;
            EnumParticleTypes enumParticleTypes = EnumParticleTypes.SPELL;
            if (Items.potionitem.isEffectInstant(data)) {
               enumParticleTypes = EnumParticleTypes.SPELL_INSTANT;
            }

            for (int i = 0; i < 100; i++) {
               double d7 = rand.nextDouble() * 4.0;
               double d8 = rand.nextDouble() * Math.PI * 2.0;
               double d9 = Math.cos(d8) * d7;
               double d10 = 0.01 + rand.nextDouble() * 0.5;
               double d11 = Math.sin(d8) * d7;
               EntityFX spawnEntityFX = this.spawnEntityFX(
                  enumParticleTypes.getParticleID(), enumParticleTypes.getShouldIgnoreRange(), d4 + d9 * 0.1, d5 + 0.3, d6 + d11 * 0.1, d9, d10, d11
               );
               if (spawnEntityFX != null
                  && (
                     !CheatBreaker.getInstance().getModManager().particlesMod.isEnabled()
                        || (Boolean)CheatBreaker.getInstance().getModManager().particlesMod.showSplashParticles.getValue()
                  )) {
                  if (CheatBreaker.getInstance().getModManager().particlesMod.isEnabled()) {
                     spawnEntityFX.setAlphaF((Float)CheatBreaker.getInstance().getModManager().particlesMod.normalSplashOpacity.getValue() / 100.0F);
                  }

                  float f4 = 0.75F + rand.nextFloat() * 0.25F;
                  spawnEntityFX.setRBGColorF(f * f4, f2 * f4, f3 * f4);
                  spawnEntityFX.multiplyVelocity((float)d7);
               }
            }

            this.theWorld.playSoundAtPos(blockPosIn, "game.potion.smash", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
            break;
         case 2003:
            double d7 = blockPosIn.getX() + 0.5;
            double d8 = blockPosIn.getY();
            double d9 = blockPosIn.getZ() + 0.5;

            for (int i = 0; i < 8; i++) {
               this.spawnParticle(
                  EnumParticleTypes.ITEM_CRACK,
                  d7,
                  d8,
                  d9,
                  rand.nextGaussian() * 0.15,
                  rand.nextDouble() * 0.2,
                  rand.nextGaussian() * 0.15,
                  Item.getIdFromItem(Items.ender_eye)
               );
            }

            for (double d10 = 0.0; d10 < Math.PI * 2; d10 += Math.PI / 20) {
               this.spawnParticle(
                  EnumParticleTypes.PORTAL,
                  d7 + Math.cos(d10) * 5.0,
                  d8 - 0.4,
                  d9 + Math.sin(d10) * 5.0,
                  Math.cos(d10) * -5.0,
                  0.0,
                  Math.sin(d10) * -5.0
               );
               this.spawnParticle(
                  EnumParticleTypes.PORTAL,
                  d7 + Math.cos(d10) * 5.0,
                  d8 - 0.4,
                  d9 + Math.sin(d10) * 5.0,
                  Math.cos(d10) * -7.0,
                  0.0,
                  Math.sin(d10) * -7.0
               );
            }

            return;
         case 2004:
            for (int i = 0; i < 20; i++) {
               double d10 = blockPosIn.getX() + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
               double d11 = blockPosIn.getY() + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
               double d12 = blockPosIn.getZ() + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
               this.theWorld.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d10, d11, d12, 0.0, 0.0, 0.0);
               this.theWorld.spawnParticle(EnumParticleTypes.FLAME, d10, d11, d12, 0.0, 0.0, 0.0);
            }

            return;
         case 2005:
            ItemDye.spawnBonemealParticles(this.theWorld, blockPosIn, data);
      }
   }

   @Override
   public void sendBlockBreakProgress(int breakerId, BlockPos pos, int progress) {
      if (progress >= 0 && progress < 10) {
         DestroyBlockProgress destroyBlockProgress = this.damagedBlocks.get(breakerId);
         if (destroyBlockProgress == null || destroyBlockProgress.getPosition().getX() != pos.getX() || destroyBlockProgress.getPosition().getY() != pos.getY() || destroyBlockProgress.getPosition().getZ() != pos.getZ()) {
            destroyBlockProgress = new DestroyBlockProgress(breakerId, pos);
            this.damagedBlocks.put(breakerId, destroyBlockProgress);
         }

         destroyBlockProgress.setPartialBlockDamage(progress);
         destroyBlockProgress.setCloudUpdateTick(this.cloudTickCounter);
      } else {
         this.damagedBlocks.remove(breakerId);
      }
   }

   public void setDisplayListEntitiesDirty() {
      this.displayListEntitiesDirty = true;
   }

   public boolean hasNoChunkUpdates() {
      return this.chunksToUpdate.isEmpty() && this.renderDispatcher.hasChunkUpdates();
   }

   public void resetClouds() {
      this.cloudRenderer.reset();
   }

   public int getCountRenderers() {
      return this.viewFrustum.renderChunks.length;
   }

   public int getCountActiveRenderers() {
      return this.renderInfos.size();
   }

   public int getCountEntitiesRendered() {
      return this.countEntitiesRendered;
   }

   public int getCountTileEntitiesRendered() {
      return this.countTileEntitiesRendered;
   }

   public int getCountLoadedChunks() {
      if (this.theWorld == null) {
         return 0;
      }

      IChunkProvider chunkProvider = this.theWorld.getChunkProvider();
      if (chunkProvider == null) {
         return 0;
      }

      if (chunkProvider != this.worldChunkProvider) {
         this.worldChunkProvider = chunkProvider;
         this.worldChunkProviderMap = (LongHashMap)Reflector.getFieldValue(chunkProvider, Reflector.ChunkProviderClient_chunkMapping);
      }

      return this.worldChunkProviderMap == null ? 0 : this.worldChunkProviderMap.getNumHashElements();
   }

   public int getCountChunksToUpdate() {
      return this.chunksToUpdate.size();
   }

   public RenderChunk getRenderChunk(BlockPos blockPos) {
      return this.viewFrustum.getRenderChunk(blockPos);
   }

   public WorldClient getWorld() {
      return this.theWorld;
   }

   public void clearRenderInfos() {
      if (renderEntitiesCounter > 0) {
         this.renderInfos = new ArrayList<>(this.renderInfos.size() + 16);
         this.renderInfosEntities = new ArrayList(this.renderInfosEntities.size() + 16);
         this.renderInfosTileEntities = new ArrayList(this.renderInfosTileEntities.size() + 16);
      } else {
         this.renderInfos.clear();
         this.renderInfosEntities.clear();
         this.renderInfosTileEntities.clear();
      }
   }

   public void onPlayerPositionSet() {
      if (this.firstWorldLoad) {
         this.loadRenderers();
         this.firstWorldLoad = false;
      }
   }

   public void pauseChunkUpdates() {
      if (this.renderDispatcher != null) {
         this.renderDispatcher.pauseChunkUpdates();
      }
   }

   public void resumeChunkUpdates() {
      if (this.renderDispatcher != null) {
         this.renderDispatcher.resumeChunkUpdates();
      }
   }

   public void updateTileEntities(Collection<TileEntity> tileEntitiesToRemove, Collection<TileEntity> tileEntitiesToAdd) {
      synchronized (this.setTileEntities) {
         this.setTileEntities.removeAll(tileEntitiesToRemove);
         this.setTileEntities.addAll(tileEntitiesToAdd);
      }
   }

   public static class ContainerLocalRenderInformation {
      public RenderChunk renderChunk;
      public EnumFacing facing;
      public int intValue;

      public ContainerLocalRenderInformation(RenderChunk renderChunk, EnumFacing enumFacing, int n) {
         this.renderChunk = renderChunk;
         this.facing = enumFacing;
         this.intValue = n;
      }

      public void setFacingBit(byte b, EnumFacing enumFacing) {
         this.intValue = this.intValue | b | 1 << enumFacing.ordinal();
      }

      public boolean isFacingBit(EnumFacing enumFacing) {
         return (this.intValue & 1 << enumFacing.ordinal()) > 0;
      }

      public void initialize(EnumFacing enumFacing, int n) {
         this.facing = enumFacing;
         this.intValue = n;
      }
   }
}
