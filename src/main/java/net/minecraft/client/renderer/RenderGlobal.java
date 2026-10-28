package net.minecraft.client.renderer;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.BlockEvent;
import com.cheatbreaker.client.event.DoubleEvent2;
import com.cheatbreaker.client.event.DrawSelectionBoxEvent;
import com.cheatbreaker.client.event.FloatEvent5;
import com.cheatbreaker.client.event.FloatEvent8;
import com.cheatbreaker.client.event.SimpleEvent15;
import com.cheatbreaker.client.event.SimpleEvent19;
import com.cheatbreaker.client.event.SimpleEvent2;
import com.cheatbreaker.client.event.SimpleEvent21;
import com.cheatbreaker.client.render.WorldLabel;
import com.google.common.collect.Maps;
import java.nio.Buffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EntityAuraFX;
import net.minecraft.client.particle.EntityBlockDustFX;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.client.particle.EntityBubbleFX;
import net.minecraft.client.particle.EntityCloudFX;
import net.minecraft.client.particle.EntityCritFX;
import net.minecraft.client.particle.EntityDiggingFX;
import net.minecraft.client.particle.EntityDropParticleFX;
import net.minecraft.client.particle.EntityEnchantmentTableParticleFX;
import net.minecraft.client.particle.EntityExplodeFX;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFireworkSparkFX;
import net.minecraft.client.particle.EntityFishWakeFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntityFootStepFX;
import net.minecraft.client.particle.EntityHeartFX;
import net.minecraft.client.particle.EntityHugeExplodeFX;
import net.minecraft.client.particle.EntityLargeExplodeFX;
import net.minecraft.client.particle.EntityLavaFX;
import net.minecraft.client.particle.EntityNoteFX;
import net.minecraft.client.particle.EntityPortalFX;
import net.minecraft.client.particle.EntityReddustFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.particle.EntitySnowShovelFX;
import net.minecraft.client.particle.EntitySpellParticleFX;
import net.minecraft.client.particle.EntitySplashFX;
import net.minecraft.client.particle.EntitySuspendFX;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.util.RenderDistanceSorter;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemRecord;
import net.minecraft.profiler.Profiler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.world.IWorldAccess;
import net.minecraft.world.WorldProvider;
import net.optifine.CompactArrayList;
import net.optifine.Config;
import net.optifine.CustomColorizer;
import net.optifine.CustomSky;
import net.optifine.DynamicLights;
import net.optifine.EntitySorterFast;
import net.optifine.GlStateManager;
import net.optifine.RandomMobs;
import net.optifine.Reflector;
import net.optifine.WrDisplayListAllocator;
import net.optifine.WrUpdates;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ARBOcclusionQuery;
import org.lwjgl.opengl.GL11;
import shadersmod.client.Shaders;

public class RenderGlobal implements IWorldAccess {
    public static Logger logger = LogManager.getLogger();
    public static ResourceLocation locationMoonPhasesPng = new ResourceLocation("textures/environment/moon_phases.png");
    public static ResourceLocation locationSunPng = new ResourceLocation("textures/environment/sun.png");
    public static ResourceLocation locationCloudsPng = new ResourceLocation("textures/environment/clouds.png");
    public static ResourceLocation locationEndSkyPng = new ResourceLocation("textures/environment/end_sky.png");
    public List tileEntities = new ArrayList();
    public WorldClient theWorld;
    public CompactArrayList compactArrayList = new CompactArrayList(100, 0.8F);
    public Map damagedBlocks = new HashMap();
    public WorldRenderer[] sortedWorldRenderers;
    public WorldRenderer[] worldRenderers;
    public int renderChunksWide;
    public int renderChunksTall;
    public int renderChunksDeep;
    public Map mapSoundPositions = Maps.newHashMap();
    public int renderDistanceChunks = -1;
    public RenderBlocks renderBlocksRg;
    public int renderEntitiesStartupCounter = 2;
    public IntBuffer occlusionResult = GLAllocation.createDirectIntBuffer(64);
    public int cloudTickCounter;
    public List glRenderLists = new ArrayList();
    public RenderList[] allRenderLists = new RenderList[]{new RenderList(), new RenderList(), new RenderList(), new RenderList()};
    public double prevSortX = -9999.0;
    public int minBlockX;
    public int minBlockY;
    public int minBlockZ;
    public int maxBlockX;
    public int maxBlockY;
    public int maxBlockZ;
    public double prevSortY = -9999.0;
    public double prevSortZ = -9999.0;
    public IIcon[] destroyBlockIcons;
    public double prevRenderSortX = -9999.0;
    public double prevRenderSortY = -9999.0;
    public double prevRenderSortZ = -9999.0;
    public int prevChunkSortX = -999;
    public int countEntitiesTotal;
    public int countEntitiesRendered;
    public int countEntitiesHidden;
    public int prevChunkSortY = -999;
    public int renderersLoaded;
    public int renderersBeingClipped;
    public int renderersBeingOccluded;
    public int renderersBeingRendered;
    public int renderersSkippingRenderPass;
    public int dummyRenderInt;
    public int worldRenderersCheckIndex;
    public int prevChunkSortZ = -999;
    public IntBuffer glListBuffer = BufferUtils.createIntBuffer(65536);
    public int glListClouds = -1;
    public boolean isFancyGlListClouds = false;
    public int cloudTickCounterGlList = -99999;
    public double cloudPlayerX = 0.0;
    public double cloudPlayerY = 0.0;
    public double cloudPlayerZ = 0.0;
    public int countSortedWorldRenderers = 0;
    public int effectivePreloadedChunks = 0;
    public int vertexResortCounter = 30;
    public int frustumCheckOffset;
    public WrDisplayListAllocator displayListAllocator = new WrDisplayListAllocator();
    public double prevReposX;
    public double prevReposY;
    public double prevReposZ;
    public Entity renderedEntity;
    public long lastMovedTime = System.currentTimeMillis();
    public long lastActionTime = System.currentTimeMillis();
    public Minecraft mc;
    public TextureManager renderEngine;
    public int glRenderListBase;
    public boolean displayListEntitiesDirty;
    public int displayListEntities;
    public boolean occlusionEnabled;
    public IntBuffer glOcclusionQueryBase;
    public int starGLCallList;
    public EntityLivingBase renderViewEntity;
    public int countTileEntitiesRendered;
    public int glSkyList;
    public int glSkyList2;
    public static AxisAlignedBB AABB_INFINITE = AxisAlignedBB.getBoundingBox(
        Double.NEGATIVE_INFINITY,
        Double.NEGATIVE_INFINITY,
        Double.NEGATIVE_INFINITY,
        Double.POSITIVE_INFINITY,
        Double.POSITIVE_INFINITY,
        Double.POSITIVE_INFINITY
    );

    public RenderGlobal(Minecraft mc) {
        this.glListClouds = GLAllocation.generateDisplayLists(1);
        this.mc = mc;
        this.renderEngine = mc.getTextureManager();
        byte b = 65;
        byte b2 = 16;
        int n = b * b * b2;
        int n2 = n * 3;
        this.glRenderListBase = GLAllocation.generateDisplayLists(n2);
        this.displayListEntitiesDirty = false;
        this.displayListEntities = GLAllocation.generateDisplayLists(1);
        this.occlusionEnabled = OpenGlCapsChecker.checkARBOcclusion();
        if (this.occlusionEnabled) {
            ((Buffer)this.occlusionResult).clear();
            this.glOcclusionQueryBase = GLAllocation.createDirectIntBuffer(b * b * b2);
            ((Buffer)this.glOcclusionQueryBase).clear();
            ((Buffer)this.glOcclusionQueryBase).position(0);
            ((Buffer)this.glOcclusionQueryBase).limit(b * b * b2);
            ARBOcclusionQuery.glGenQueriesARB(this.glOcclusionQueryBase);
        }

        this.starGLCallList = GLAllocation.generateDisplayLists(3);
        GL11.glPushMatrix();
        GL11.glNewList(this.starGLCallList, 4864);
        this.renderStars();
        GL11.glEndList();
        GL11.glPopMatrix();
        Tessellator instance = Tessellator.instance;
        this.glSkyList = this.starGLCallList + 1;
        GL11.glNewList(this.glSkyList, 4864);
        byte b3 = 64;
        int n3 = 256 / b3 + 2;
        float f = 16.0F;

        for (int i = -b3 * n3; i <= b3 * n3; i += b3) {
            for (int j = -b3 * n3; j <= b3 * n3; j += b3) {
                instance.startDrawingQuads();
                instance.addVertex(i + 0, f, j + 0);
                instance.addVertex(i + b3, f, j + 0);
                instance.addVertex(i + b3, f, j + b3);
                instance.addVertex(i + 0, f, j + b3);
                instance.draw();
            }
        }

        GL11.glEndList();
        this.glSkyList2 = this.starGLCallList + 2;
        GL11.glNewList(this.glSkyList2, 4864);
        f = -16.0F;
        instance.startDrawingQuads();

        for (int i = -b3 * n3; i <= b3 * n3; i += b3) {
            for (int j = -b3 * n3; j <= b3 * n3; j += b3) {
                instance.addVertex(i + b3, f, j + 0);
                instance.addVertex(i + 0, f, j + 0);
                instance.addVertex(i + 0, f, j + b3);
                instance.addVertex(i + b3, f, j + b3);
            }
        }

        instance.draw();
        GL11.glEndList();
    }

    public void renderStars() {
        Random random = new Random(10842L);
        Tessellator instance = Tessellator.instance;
        instance.startDrawingQuads();

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
                    double d13 = d11 * cos3 - d12 * sin3;
                    double d14 = d12 * cos3 + d11 * sin3;
                    double d15 = d13 * sin2 + d10 * cos2;
                    double d16 = d10 * sin2 - d13 * cos2;
                    double d17 = d16 * sin - d14 * cos;
                    double d18 = d14 * sin + d16 * cos;
                    instance.addVertex(d6 + d17, d7 + d15, d8 + d18);
                }
            }
        }

        instance.draw();
    }

    public void setWorldAndLoadRenderers(WorldClient worldClient) {
        if (this.theWorld != null) {
            this.theWorld.removeWorldAccess(this);
        }

        this.prevSortX = -9999.0;
        this.prevSortY = -9999.0;
        this.prevSortZ = -9999.0;
        this.prevRenderSortX = -9999.0;
        this.prevRenderSortY = -9999.0;
        this.prevRenderSortZ = -9999.0;
        this.prevChunkSortX = -9999;
        this.prevChunkSortY = -9999;
        this.prevChunkSortZ = -9999;
        RenderManager.instance.set(worldClient);
        this.theWorld = worldClient;
        this.renderBlocksRg = new RenderBlocks(worldClient);
        if (Config.isDynamicLights()) {
            DynamicLights.clear();
        }

        if (worldClient != null) {
            worldClient.addWorldAccess(this);
            this.loadRenderers();
        }
    }

    public void loadRenderers() {
        if (this.theWorld != null) {
            Blocks.leaves.setGraphicsLevel(Config.isTreesFancy());
            Blocks.leaves2.setGraphicsLevel(Config.isTreesFancy());
            this.renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;
            WrUpdates.clearAllUpdates();
            if (this.worldRenderers != null) {
                for (int i = 0; i < this.worldRenderers.length; i++) {
                    this.worldRenderers[i].stopRendering();
                }
            }

            if (Config.isDynamicLights()) {
                DynamicLights.clear();
            }

            int renderDistanceChunks = this.mc.gameSettings.renderDistanceChunks;
            byte b = 16;
            if (Config.isLoadChunksFar() && renderDistanceChunks < b) {
                renderDistanceChunks = b;
            }

            int limit = Config.limit(b - renderDistanceChunks, 0, 8);
            this.effectivePreloadedChunks = Config.limit(Config.getPreloadedChunks(), 0, limit);
            renderDistanceChunks += this.effectivePreloadedChunks;
            byte b2 = 32;
            if (renderDistanceChunks > b2) {
                renderDistanceChunks = b2;
            }

            this.prevReposX = -9999.0;
            this.prevReposY = -9999.0;
            this.prevReposZ = -9999.0;
            int n = renderDistanceChunks * 2 + 1;
            this.renderChunksWide = n;
            this.renderChunksTall = 16;
            this.renderChunksDeep = n;
            this.worldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
            this.sortedWorldRenderers = new WorldRenderer[this.renderChunksWide * this.renderChunksTall * this.renderChunksDeep];
            this.countSortedWorldRenderers = 0;
            this.displayListAllocator.resetAllocatedLists();
            int n2 = 0;
            int n3 = 0;
            this.minBlockX = 0;
            this.minBlockY = 0;
            this.minBlockZ = 0;
            this.maxBlockX = this.renderChunksWide;
            this.maxBlockY = this.renderChunksTall;
            this.maxBlockZ = this.renderChunksDeep;

            for (int i = 0; i < this.compactArrayList.size(); i++) {
                WorldRenderer worldRenderer = (WorldRenderer)this.compactArrayList.get(i);
                if (worldRenderer != null) {
                    worldRenderer.needsUpdate = false;
                }
            }

            this.compactArrayList.clear();
            this.tileEntities.clear();
            this.onStaticEntitiesChanged();

            for (int i = 0; i < this.renderChunksWide; i++) {
                for (int j = 0; j < this.renderChunksTall; j++) {
                    for (int k = 0; k < this.renderChunksDeep; k++) {
                        int n4 = (k * this.renderChunksTall + j) * this.renderChunksWide + i;
                        this.worldRenderers[n4] = WrUpdates.makeWorldRenderer(
                            this.theWorld, this.tileEntities, i * 16, j * 16, k * 16, this.glRenderListBase + n2
                        );
                        if (this.occlusionEnabled) {
                            this.worldRenderers[n4].glOcclusionQuery = this.glOcclusionQueryBase.get(n3);
                        }

                        this.worldRenderers[n4].isWaitingOnOcclusionQuery = false;
                        this.worldRenderers[n4].isVisible = true;
                        this.worldRenderers[n4].isInFrustum = false;
                        this.worldRenderers[n4].chunkIndex = n3++;
                        if (this.theWorld.blockExists(i << 4, 0, k << 4)) {
                            this.worldRenderers[n4].markDirty();
                            this.compactArrayList.add(this.worldRenderers[n4]);
                        }

                        n2 += 3;
                    }
                }
            }

            if (this.theWorld != null) {
                EntityLivingBase renderViewEntity = this.mc.renderViewEntity;
                if (renderViewEntity == null) {
                    renderViewEntity = this.mc.thePlayer;
                }

                if (renderViewEntity != null) {
                    this.markRenderersForNewPosition(MathHelper.floor_double(renderViewEntity.posX), MathHelper.floor_double(renderViewEntity.posY), MathHelper.floor_double(renderViewEntity.posZ));
                    EntitySorterFast entitySorterFast = new EntitySorterFast(renderViewEntity);
                    entitySorterFast.prepareToSort(this.sortedWorldRenderers, this.countSortedWorldRenderers);
                    Arrays.sort(this.sortedWorldRenderers, 0, this.countSortedWorldRenderers, entitySorterFast);
                }
            }

            this.renderEntitiesStartupCounter = 2;
        }
    }

    public void renderEntities(EntityLivingBase entityLivingBase, ICamera camera, float f) {
        int n = 0;
        if (Reflector.MinecraftForgeClient_getRenderPass.exists()) {
            n = Reflector.callInt(Reflector.MinecraftForgeClient_getRenderPass);
        }

        boolean exists = Reflector.ForgeEntity_shouldRenderInPass.exists();
        boolean exists2 = Reflector.ForgeTileEntity_shouldRenderInPass.exists();
        if (this.renderEntitiesStartupCounter > 0) {
            if (n > 0) {
                return;
            }

            this.renderEntitiesStartupCounter--;
        } else {
            double d = entityLivingBase.prevPosX + (entityLivingBase.posX - entityLivingBase.prevPosX) * f;
            double d2 = entityLivingBase.prevPosY + (entityLivingBase.posY - entityLivingBase.prevPosY) * f;
            double d3 = entityLivingBase.prevPosZ + (entityLivingBase.posZ - entityLivingBase.prevPosZ) * f;
            this.theWorld.theProfiler.startSection("prepare");
            TileEntityRendererDispatcher.instance
                .cacheActiveRenderInfo(this.theWorld, this.mc.getTextureManager(), this.mc.fontRendererObj, this.mc.renderViewEntity, f);
            RenderManager.instance
                .cacheActiveRenderInfo(
                    this.theWorld, this.mc.getTextureManager(), this.mc.fontRendererObj, this.mc.renderViewEntity, this.mc.pointedEntity, this.mc.gameSettings, f
                );
            if (n == 0) {
                this.countEntitiesTotal = 0;
                this.countEntitiesRendered = 0;
                this.countEntitiesHidden = 0;
                this.countTileEntitiesRendered = 0;
                EntityLivingBase renderViewEntity = this.mc.renderViewEntity;
                double d4 = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * f;
                double d5 = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * f;
                double d6 = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * f;
                TileEntityRendererDispatcher.staticPlayerX = d4;
                TileEntityRendererDispatcher.staticPlayerY = d5;
                TileEntityRendererDispatcher.staticPlayerZ = d6;
                this.theWorld.theProfiler.endStartSection("staticentities");
                if (this.displayListEntitiesDirty) {
                    RenderManager.renderPosX = 0.0;
                    RenderManager.renderPosY = 0.0;
                    RenderManager.renderPosZ = 0.0;
                    this.rebuildDisplayListEntities();
                }

                GL11.glMatrixMode(5888);
                GL11.glPushMatrix();
                GL11.glTranslated(-d4, -d5, -d6);
                GL11.glCallList(this.displayListEntities);
                GL11.glPopMatrix();
                RenderManager.renderPosX = d4;
                RenderManager.renderPosY = d5;
                RenderManager.renderPosZ = d6;
            }

            this.mc.entityRenderer.enableLightmap(f);
            this.theWorld.theProfiler.endStartSection("global");
            List loadedEntityList = this.theWorld.getLoadedEntityList();
            if (n == 0) {
                this.countEntitiesTotal = loadedEntityList.size();
            }

            if (Config.isFogOff() && this.mc.entityRenderer.fogStandard) {
                GL11.glDisable(2912);
            }

            for (int i = 0; i < this.theWorld.weatherEffects.size(); i++) {
                Entity entity = (Entity)this.theWorld.weatherEffects.get(i);
                if (!exists || Reflector.callBoolean(entity, Reflector.ForgeEntity_shouldRenderInPass, n)) {
                    this.countEntitiesRendered++;
                    if (entity.isInRangeToRender3d(d, d2, d3)) {
                        RenderManager.instance.renderEntitySimple(entity, f);
                    }
                }
            }

            this.theWorld.theProfiler.endStartSection("entities");
            boolean isShaders = Config.isShaders();
            if (isShaders) {
                Shaders.beginEntities();
            }

            boolean fancyGraphics = this.mc.gameSettings.fancyGraphics;
            this.mc.gameSettings.fancyGraphics = Config.isDroppedItemsFancy();
            WorldLabel.renderAll();

            for (int i = 0; i < loadedEntityList.size(); i++) {
                Entity entity = (Entity)loadedEntityList.get(i);
                if (!exists || Reflector.callBoolean(entity, Reflector.ForgeEntity_shouldRenderInPass, n)) {
                    boolean flag = entity.isInRangeToRender3d(d, d2, d3)
                        && (entity.ignoreFrustumCheck || camera.isBoundingBoxInFrustum(entity.boundingBox) || entity.riddenByEntity == this.mc.thePlayer);
                    if (!flag && entity instanceof EntityLiving) {
                        EntityLiving entityLiving = (EntityLiving)entity;
                        if (entityLiving.getLeashed() && entityLiving.getLeashedToEntity() != null) {
                            Entity leashedToEntity = entityLiving.getLeashedToEntity();
                            flag = camera.isBoundingBoxInFrustum(leashedToEntity.boundingBox);
                        }
                    }

                    if (flag
                        && (entity != this.mc.renderViewEntity || this.mc.gameSettings.thirdPersonView != 0 || this.mc.renderViewEntity.isPlayerSleeping())
                        && this.theWorld.blockExists(MathHelper.floor_double(entity.posX), 0, MathHelper.floor_double(entity.posZ))) {
                        this.countEntitiesRendered++;
                        if (entity.getClass() == EntityItemFrame.class) {
                            entity.renderDistanceWeight = 0.06;
                        }

                        this.renderedEntity = entity;
                        if (isShaders) {
                            Shaders.nextEntity(entity);
                        }

                        RenderManager.instance.renderEntitySimple(entity, f);
                        this.renderedEntity = null;
                    }
                }
            }

            this.mc.gameSettings.fancyGraphics = fancyGraphics;
            this.theWorld.theProfiler.endStartSection("blockentities");
            if (isShaders) {
                Shaders.endEntities();
                Shaders.beginBlockEntities();
            }

            RenderHelper.enableStandardItemLighting();

            for (int i = 0; i < this.tileEntities.size(); i++) {
                TileEntity tileEntity = (TileEntity)this.tileEntities.get(i);
                if (!exists2 || Reflector.callBoolean(tileEntity, Reflector.ForgeTileEntity_shouldRenderInPass, n)) {
                    AxisAlignedBB tileEntityBoundingBox = this.getTileEntityBoundingBox(tileEntity);
                    if (tileEntityBoundingBox == AABB_INFINITE || camera.isBoundingBoxInFrustum(tileEntityBoundingBox)) {
                        Class clazz = tileEntity.getClass();
                        if (clazz == TileEntitySign.class && !Config.zoomMode) {
                            EntityClientPlayerMP thePlayer = this.mc.thePlayer;
                            double distanceSq = tileEntity.getDistanceSq(thePlayer.posX, thePlayer.posY, thePlayer.posZ);
                            if (distanceSq > 256.0) {
                                FontRenderer fontRenderer = TileEntityRendererDispatcher.instance.getFontRenderer();
                                fontRenderer.enabled = false;
                                if (Config.isShaders()) {
                                    Shaders.nextBlockEntity(tileEntity);
                                }

                                TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, f);
                                this.countTileEntitiesRendered++;
                                fontRenderer.enabled = true;
                                continue;
                            }
                        }

                        if (clazz == TileEntityChest.class) {
                            Block block = this.theWorld.getBlock(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
                            if (!(block instanceof BlockChest)) {
                                continue;
                            }
                        }

                        if (Config.isShaders()) {
                            Shaders.nextBlockEntity(tileEntity);
                        }

                        TileEntityRendererDispatcher.instance.renderTileEntity(tileEntity, f);
                        this.countTileEntitiesRendered++;
                    }
                }
            }

            this.mc.entityRenderer.disableLightmap(f);
            if (isShaders) {
                Shaders.endBlockEntities();
            }

            this.theWorld.theProfiler.endSection();
        }
    }

    public String getDebugInfoRenders() {
        return "C: "
            + this.renderersBeingRendered
            + "/"
            + this.renderersLoaded
            + ". F: "
            + this.renderersBeingClipped
            + ", O: "
            + this.renderersBeingOccluded
            + ", E: "
            + this.renderersSkippingRenderPass;
    }

    public String getDebugInfoEntities() {
        return "E: "
            + this.countEntitiesRendered
            + "/"
            + this.countEntitiesTotal
            + ". B: "
            + this.countEntitiesHidden
            + ", I: "
            + (this.countEntitiesTotal - this.countEntitiesHidden - this.countEntitiesRendered)
            + ", "
            + Config.getVersionDebug();
    }

    @Override
    public void onStaticEntitiesChanged() {
        this.displayListEntitiesDirty = true;
    }

    public void rebuildDisplayListEntities() {
        this.theWorld.theProfiler.startSection("staticentityrebuild");
        GL11.glPushMatrix();
        GL11.glNewList(this.displayListEntities, 4864);
        List loadedEntityList = this.theWorld.getLoadedEntityList();
        this.displayListEntitiesDirty = false;

        for (int i = 0; i < loadedEntityList.size(); i++) {
            Entity entity = (Entity)loadedEntityList.get(i);
            if (RenderManager.instance.getEntityRenderObject(entity).isStaticEntity()) {
                this.displayListEntitiesDirty = this.displayListEntitiesDirty || !RenderManager.instance.renderEntityStatic(entity, 0.0F, true);
            }
        }

        GL11.glEndList();
        GL11.glPopMatrix();
        this.theWorld.theProfiler.endSection();
    }

    public void markRenderersForNewPosition(int n, int n2, int n3) {
        n -= 8;
        n2 -= 8;
        n3 -= 8;
        this.minBlockX = Integer.MAX_VALUE;
        this.minBlockY = Integer.MAX_VALUE;
        this.minBlockZ = Integer.MAX_VALUE;
        this.maxBlockX = Integer.MIN_VALUE;
        this.maxBlockY = Integer.MIN_VALUE;
        this.maxBlockZ = Integer.MIN_VALUE;
        int n4 = this.renderChunksWide * 16;
        int n5 = n4 / 2;

        for (int i = 0; i < this.renderChunksWide; i++) {
            int n6 = i * 16;
            int n7 = n6 + n5 - n;
            if (n7 < 0) {
                n7 -= n4 - 1;
            }

            n7 /= n4;
            n6 -= n7 * n4;
            if (n6 < this.minBlockX) {
                this.minBlockX = n6;
            }

            if (n6 > this.maxBlockX) {
                this.maxBlockX = n6;
            }

            for (int j = 0; j < this.renderChunksDeep; j++) {
                int n8 = j * 16;
                int n9 = n8 + n5 - n3;
                if (n9 < 0) {
                    n9 -= n4 - 1;
                }

                n9 /= n4;
                n8 -= n9 * n4;
                if (n8 < this.minBlockZ) {
                    this.minBlockZ = n8;
                }

                if (n8 > this.maxBlockZ) {
                    this.maxBlockZ = n8;
                }

                for (int k = 0; k < this.renderChunksTall; k++) {
                    int n10 = k * 16;
                    if (n10 < this.minBlockY) {
                        this.minBlockY = n10;
                    }

                    if (n10 > this.maxBlockY) {
                        this.maxBlockY = n10;
                    }

                    WorldRenderer worldRenderer = this.worldRenderers[(j * this.renderChunksTall + k) * this.renderChunksWide + i];
                    boolean needsUpdate = worldRenderer.needsUpdate;
                    worldRenderer.setPosition(n6, n10, n8);
                    if (!needsUpdate && worldRenderer.needsUpdate) {
                        this.compactArrayList.add(worldRenderer);
                    }
                }
            }
        }
    }

    public int sortAndRender(EntityLivingBase entityLivingBase, int n, double d) {
        this.renderViewEntity = entityLivingBase;
        if (Config.isDynamicLights()) {
            DynamicLights.update(this);
        }

        Profiler theProfiler = this.theWorld.theProfiler;
        theProfiler.startSection("sortchunks");
        if (this.compactArrayList.size() < 10) {
            byte b = 10;

            for (int i = 0; i < b; i++) {
                this.worldRenderersCheckIndex = (this.worldRenderersCheckIndex + 1) % this.worldRenderers.length;
                WorldRenderer worldRenderer = this.worldRenderers[this.worldRenderersCheckIndex];
                if (worldRenderer.needsUpdate && !this.compactArrayList.contains(worldRenderer)) {
                    this.compactArrayList.add(worldRenderer);
                }
            }
        }

        if (this.mc.gameSettings.renderDistanceChunks != this.renderDistanceChunks && !Config.isLoadChunksFar()) {
            this.loadRenderers();
        }

        if (n == 0) {
            this.renderersLoaded = 0;
            this.dummyRenderInt = 0;
            this.renderersBeingClipped = 0;
            this.renderersBeingOccluded = 0;
            this.renderersBeingRendered = 0;
            this.renderersSkippingRenderPass = 0;
        }

        boolean flag = this.prevChunkSortX != entityLivingBase.chunkCoordX || this.prevChunkSortY != entityLivingBase.chunkCoordY || this.prevChunkSortZ != entityLivingBase.chunkCoordZ;
        if (!flag) {
            double d2 = entityLivingBase.posX - this.prevSortX;
            double d3 = entityLivingBase.posY - this.prevSortY;
            double d4 = entityLivingBase.posZ - this.prevSortZ;
            double d5 = d2 * d2 + d3 * d3 + d4 * d4;
            flag = d5 > 16.0;
        }

        if (flag) {
            this.prevChunkSortX = entityLivingBase.chunkCoordX;
            this.prevChunkSortY = entityLivingBase.chunkCoordY;
            this.prevChunkSortZ = entityLivingBase.chunkCoordZ;
            this.prevSortX = entityLivingBase.posX;
            this.prevSortY = entityLivingBase.posY;
            this.prevSortZ = entityLivingBase.posZ;
            int n2 = this.effectivePreloadedChunks * 16;
            double d2 = entityLivingBase.posX - this.prevReposX;
            double d3 = entityLivingBase.posY - this.prevReposY;
            double d4 = entityLivingBase.posZ - this.prevReposZ;
            double d5 = d2 * d2 + d3 * d3 + d4 * d4;
            if (d5 > n2 * n2 + 16.0) {
                this.prevReposX = entityLivingBase.posX;
                this.prevReposY = entityLivingBase.posY;
                this.prevReposZ = entityLivingBase.posZ;
                this.markRenderersForNewPosition(MathHelper.floor_double(entityLivingBase.posX), MathHelper.floor_double(entityLivingBase.posY), MathHelper.floor_double(entityLivingBase.posZ));
            }

            EntitySorterFast entitySorterFast = new EntitySorterFast(entityLivingBase);
            entitySorterFast.prepareToSort(this.sortedWorldRenderers, this.countSortedWorldRenderers);
            Arrays.sort(this.sortedWorldRenderers, 0, this.countSortedWorldRenderers, entitySorterFast);
            if (Config.isFastRender()) {
                int posX = (int)entityLivingBase.posX;
                int posZ = (int)entityLivingBase.posZ;
                short s = 2000;
                if (Math.abs(posX - WorldRenderer.globalChunkOffsetX) > s || Math.abs(posZ - WorldRenderer.globalChunkOffsetZ) > s) {
                    WorldRenderer.globalChunkOffsetX = posX;
                    WorldRenderer.globalChunkOffsetZ = posZ;
                    this.loadRenderers();
                }
            }
        }

        if (Config.isTranslucentBlocksFancy()) {
            double d2 = entityLivingBase.posX - this.prevRenderSortX;
            double d3 = entityLivingBase.posY - this.prevRenderSortY;
            double d4 = entityLivingBase.posZ - this.prevRenderSortZ;
            int min = Math.min(27, this.countSortedWorldRenderers);
            if (d2 * d2 + d3 * d3 + d4 * d4 > 1.0) {
                this.prevRenderSortX = entityLivingBase.posX;
                this.prevRenderSortY = entityLivingBase.posY;
                this.prevRenderSortZ = entityLivingBase.posZ;

                for (int i = 0; i < min; i++) {
                    WorldRenderer worldRenderer = this.sortedWorldRenderers[i];
                    if (worldRenderer.vertexState != null && worldRenderer.sortDistanceToEntitySquared < 1152.0F) {
                        worldRenderer.needVertexStateResort = true;
                        if (this.vertexResortCounter > i) {
                            this.vertexResortCounter = i;
                        }
                    }
                }
            }

            if (this.vertexResortCounter < min) {
                while (this.vertexResortCounter < min) {
                    WorldRenderer worldRenderer = this.sortedWorldRenderers[this.vertexResortCounter];
                    this.vertexResortCounter++;
                    if (worldRenderer.needVertexStateResort) {
                        worldRenderer.updateRendererSort(entityLivingBase);
                        break;
                    }
                }
            }
        }

        RenderHelper.disableStandardItemLighting();
        WrUpdates.preRender(this, entityLivingBase);
        if (this.mc.gameSettings.ofSmoothFps && n == 0) {
            GL11.glFinish();
        }

        byte b = 0;
        int n2 = 0;
        int n3;
        if (this.occlusionEnabled && this.mc.gameSettings.advancedOpengl && !this.mc.gameSettings.anaglyph && n == 0) {
            double d2 = entityLivingBase.lastTickPosX + (entityLivingBase.posX - entityLivingBase.lastTickPosX) * d;
            double d3 = entityLivingBase.lastTickPosY + (entityLivingBase.posY - entityLivingBase.lastTickPosY) * d;
            double d4 = entityLivingBase.lastTickPosZ + (entityLivingBase.posZ - entityLivingBase.lastTickPosZ) * d;
            byte b2 = 0;
            int min = Math.min(20, this.countSortedWorldRenderers);
            this.checkOcclusionQueryResult(b2, min, entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ);

            for (int i = b2; i < min; i++) {
                this.sortedWorldRenderers[i].isVisible = true;
            }

            theProfiler.endStartSection("render");
            n3 = b + this.renderSortedRenderers(b2, min, n, d);
            int n4 = min;
            int n5 = 0;
            byte b3 = 40;
            int renderChunksWide = this.renderChunksWide;

            while (n4 < this.countSortedWorldRenderers) {
                theProfiler.endStartSection("occ");
                int n6 = n4;
                if (n5 < renderChunksWide) {
                    n5++;
                } else {
                    n5--;
                }

                n4 += n5 * b3;
                if (n4 <= n6) {
                    n4 = n6 + 10;
                }

                if (n4 > this.countSortedWorldRenderers) {
                    n4 = this.countSortedWorldRenderers;
                }

                GL11.glDisable(3553);
                if (Config.isShaders()) {
                    Shaders.disableTexture2D();
                }

                GL11.glDisable(2896);
                GL11.glDisable(3008);
                GL11.glDisable(2912);
                if (Config.isShaders()) {
                    Shaders.disableFog();
                }

                GL11.glColorMask(false, false, false, false);
                GL11.glDepthMask(false);
                theProfiler.startSection("check");
                this.checkOcclusionQueryResult(n6, n4, entityLivingBase.posX, entityLivingBase.posY, entityLivingBase.posZ);
                theProfiler.endSection();
                GL11.glPushMatrix();
                float f = 0.0F;
                float f2 = 0.0F;
                float f3 = 0.0F;

                for (int i = n6; i < n4; i++) {
                    WorldRenderer worldRenderer = this.sortedWorldRenderers[i];
                    if (worldRenderer.skipAllRenderPasses()) {
                        worldRenderer.isInFrustum = false;
                    } else if (worldRenderer.isUpdating || worldRenderer.needsBoxUpdate) {
                        worldRenderer.isVisible = true;
                    } else if (worldRenderer.isInFrustum) {
                        if (Config.isOcclusionFancy() && !worldRenderer.isInFrustrumFully) {
                            worldRenderer.isVisible = true;
                        } else if (worldRenderer.isInFrustum && !worldRenderer.isWaitingOnOcclusionQuery) {
                            if (worldRenderer.isVisibleFromPosition) {
                                float abs = Math.abs((float)(worldRenderer.visibleFromX - entityLivingBase.posX));
                                float abs2 = Math.abs((float)(worldRenderer.visibleFromY - entityLivingBase.posY));
                                float abs3 = Math.abs((float)(worldRenderer.visibleFromZ - entityLivingBase.posZ));
                                float f4 = abs + abs2 + abs3;
                                if (f4 < 10.0 + i / 1000.0) {
                                    worldRenderer.isVisible = true;
                                    continue;
                                }

                                worldRenderer.isVisibleFromPosition = false;
                            }

                            float f4 = (float)(worldRenderer.posXMinus - d2);
                            float f5 = (float)(worldRenderer.posYMinus - d3);
                            float f6 = (float)(worldRenderer.posZMinus - d4);
                            float f7 = f4 - f;
                            float f8 = f5 - f2;
                            float f9 = f6 - f3;
                            if (f7 != 0.0F || f8 != 0.0F || f9 != 0.0F) {
                                GL11.glTranslatef(f7, f8, f9);
                                f += f7;
                                f2 += f8;
                                f3 += f9;
                            }

                            theProfiler.startSection("bb");
                            ARBOcclusionQuery.glBeginQueryARB(35092, worldRenderer.glOcclusionQuery);
                            worldRenderer.callOcclusionQueryList();
                            ARBOcclusionQuery.glEndQueryARB(35092);
                            theProfiler.endSection();
                            worldRenderer.isWaitingOnOcclusionQuery = true;
                            n2++;
                        }
                    }
                }

                GL11.glPopMatrix();
                if (this.mc.gameSettings.anaglyph) {
                    if (EntityRenderer.anaglyphField == 0) {
                        GL11.glColorMask(false, true, true, true);
                    } else {
                        GL11.glColorMask(true, false, false, true);
                    }
                } else {
                    GL11.glColorMask(true, true, true, true);
                }

                GL11.glDepthMask(true);
                GL11.glEnable(3553);
                if (Config.isShaders()) {
                    Shaders.enableTexture2D();
                }

                GL11.glEnable(3008);
                GL11.glEnable(2912);
                if (Config.isShaders()) {
                    Shaders.enableFog();
                }

                theProfiler.endStartSection("render");
                n3 += this.renderSortedRenderers(n6, n4, n, d);
            }
        } else {
            theProfiler.endStartSection("render");
            n3 = b + this.renderSortedRenderers(0, this.countSortedWorldRenderers, n, d);
        }

        theProfiler.endSection();
        WrUpdates.postRender();
        return n3;
    }

    public void checkOcclusionQueryResult(int n, int n2, double d, double d2, double d3) {
        for (int i = n; i < n2; i++) {
            WorldRenderer worldRenderer = this.sortedWorldRenderers[i];
            if (worldRenderer.isWaitingOnOcclusionQuery) {
                ((Buffer)this.occlusionResult).clear();
                ARBOcclusionQuery.glGetQueryObjectuARB(worldRenderer.glOcclusionQuery, 34919, this.occlusionResult);
                if (this.occlusionResult.get(0) != 0) {
                    worldRenderer.isWaitingOnOcclusionQuery = false;
                    ((Buffer)this.occlusionResult).clear();
                    ARBOcclusionQuery.glGetQueryObjectuARB(worldRenderer.glOcclusionQuery, 34918, this.occlusionResult);
                    if (!worldRenderer.isUpdating && !worldRenderer.needsBoxUpdate) {
                        boolean isVisible = worldRenderer.isVisible;
                        worldRenderer.isVisible = this.occlusionResult.get(0) > 0;
                        if (isVisible && worldRenderer.isVisible) {
                            worldRenderer.isVisibleFromPosition = true;
                            worldRenderer.visibleFromX = d;
                            worldRenderer.visibleFromY = d2;
                            worldRenderer.visibleFromZ = d3;
                        }
                    } else {
                        worldRenderer.isVisible = true;
                    }
                }
            }
        }
    }

    public int renderSortedRenderers(int n, int n2, int n3, double d) {
        if (Config.isFastRender()) {
            return this.renderSortedRenderersFast(n, n2, n3, d);
        }

        this.glRenderLists.clear();
        int n4 = 0;
        int n5 = n;
        int n6 = n2;
        byte b = 1;
        if (n3 == 1) {
            n5 = this.countSortedWorldRenderers - 1 - n;
            n6 = this.countSortedWorldRenderers - 1 - n2;
            b = -1;
        }

        for (int i = n5; i != n6; i += b) {
            if (n3 == 0) {
                this.renderersLoaded++;
                if (this.sortedWorldRenderers[i].skipRenderPass[n3]) {
                    this.renderersSkippingRenderPass++;
                } else if (!this.sortedWorldRenderers[i].isInFrustum) {
                    this.renderersBeingClipped++;
                } else if (this.occlusionEnabled && !this.sortedWorldRenderers[i].isVisible) {
                    this.renderersBeingOccluded++;
                } else {
                    this.renderersBeingRendered++;
                }
            }

            if (!this.sortedWorldRenderers[i].skipRenderPass[n3]
                && this.sortedWorldRenderers[i].isInFrustum
                && (!this.occlusionEnabled || this.sortedWorldRenderers[i].isVisible)) {
                int gLCallListForPass = this.sortedWorldRenderers[i].getGLCallListForPass(n3);
                if (gLCallListForPass >= 0) {
                    this.glRenderLists.add(this.sortedWorldRenderers[i]);
                    n4++;
                }
            }
        }

        if (n4 == 0) {
            return 0;
        }

        EntityLivingBase renderViewEntity = this.mc.renderViewEntity;
        double d2 = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * d;
        double d3 = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * d;
        double d4 = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * d;
        int n7 = 0;

        for (int i = 0; i < this.allRenderLists.length; i++) {
            this.allRenderLists[i].resetList();
        }

        for (int i = 0; i < this.glRenderLists.size(); i++) {
            WorldRenderer worldRenderer = (WorldRenderer)this.glRenderLists.get(i);
            int n8 = -1;

            for (int j = 0; j < n7; j++) {
                if (this.allRenderLists[j].rendersChunk(worldRenderer.posXMinus, worldRenderer.posYMinus, worldRenderer.posZMinus)) {
                    n8 = j;
                }
            }

            if (n8 < 0) {
                n8 = n7++;
                this.allRenderLists[n8].setupRenderList(worldRenderer.posXMinus, worldRenderer.posYMinus, worldRenderer.posZMinus, d2, d3, d4);
            }

            this.allRenderLists[n8].addGLRenderList(worldRenderer.getGLCallListForPass(n3));
        }

        if (Config.isFogOff() && this.mc.entityRenderer.fogStandard) {
            GL11.glDisable(2912);
        }

        int n8 = MathHelper.floor_double(d2);
        int n9 = MathHelper.floor_double(d4);
        int n10 = n8 - (n8 & 1023);
        int n11 = n9 - (n9 & 1023);
        Arrays.sort(this.allRenderLists, new RenderDistanceSorter(n10, n11));
        this.renderAllRenderLists(n3, d);
        return n4;
    }

    public int renderSortedRenderersFast(int n, int n2, int n3, double d) {
        ((Buffer)this.glListBuffer).clear();
        int n4 = 0;
        boolean showDebugInfo = this.mc.gameSettings.showDebugInfo;
        int n5 = n;
        int n6 = n2;
        byte b = 1;
        if (n3 == 1) {
            n5 = this.countSortedWorldRenderers - 1 - n;
            n6 = this.countSortedWorldRenderers - 1 - n2;
            b = -1;
        }

        for (int i = n5; i != n6; i += b) {
            WorldRenderer worldRenderer = this.sortedWorldRenderers[i];
            if (showDebugInfo && n3 == 0) {
                this.renderersLoaded++;
                if (worldRenderer.skipRenderPass[n3]) {
                    this.renderersSkippingRenderPass++;
                } else if (!worldRenderer.isInFrustum) {
                    this.renderersBeingClipped++;
                } else if (this.occlusionEnabled && !worldRenderer.isVisible) {
                    this.renderersBeingOccluded++;
                } else {
                    this.renderersBeingRendered++;
                }
            }

            if (worldRenderer.isInFrustum && !worldRenderer.skipRenderPass[n3] && (!this.occlusionEnabled || worldRenderer.isVisible)) {
                int gLCallListForPass = worldRenderer.getGLCallListForPass(n3);
                if (gLCallListForPass >= 0) {
                    this.glListBuffer.put(gLCallListForPass);
                    n4++;
                }
            }
        }

        if (n4 == 0) {
            return 0;
        }

        if (Config.isFogOff() && this.mc.entityRenderer.fogStandard) {
            GL11.glDisable(2912);
        }

        ((Buffer)this.glListBuffer).flip();
        EntityLivingBase renderViewEntity = this.mc.renderViewEntity;
        double d2 = renderViewEntity.lastTickPosX + (renderViewEntity.posX - renderViewEntity.lastTickPosX) * d - WorldRenderer.globalChunkOffsetX;
        double d3 = renderViewEntity.lastTickPosY + (renderViewEntity.posY - renderViewEntity.lastTickPosY) * d;
        double d4 = renderViewEntity.lastTickPosZ + (renderViewEntity.posZ - renderViewEntity.lastTickPosZ) * d - WorldRenderer.globalChunkOffsetZ;
        this.mc.entityRenderer.enableLightmap(d);
        GL11.glTranslatef((float)(-d2), (float)(-d3), (float)(-d4));
        GL11.glCallLists(this.glListBuffer);
        GL11.glTranslatef((float)d2, (float)d3, (float)d4);
        this.mc.entityRenderer.disableLightmap(d);
        return n4;
    }

    public void renderAllRenderLists(int n, double d) {
        this.mc.entityRenderer.enableLightmap(d);

        for (int i = 0; i < this.allRenderLists.length; i++) {
            this.allRenderLists[i].callLists();
        }

        this.mc.entityRenderer.disableLightmap(d);
    }

    public void updateClouds() {
        if (Config.isShaders() && Keyboard.isKeyDown(61) && Keyboard.isKeyDown(19)) {
            Shaders.uninit();
            Shaders.loadShaderPack();
        }

        this.cloudTickCounter++;
        if (this.cloudTickCounter % 20 == 0) {
            Iterator iterator = this.damagedBlocks.values().iterator();

            while (iterator.hasNext()) {
                DestroyBlockProgress destroyBlockProgress = (DestroyBlockProgress)iterator.next();
                int creationCloudUpdateTick = destroyBlockProgress.getCreationCloudUpdateTick();
                if (this.cloudTickCounter - creationCloudUpdateTick > 400) {
                    iterator.remove();
                }
            }
        }
    }

    public void renderSky(float f) {
        if (Reflector.ForgeWorldProvider_getSkyRenderer.exists()) {
            WorldProvider provider = this.mc.theWorld.provider;
            Object obj = Reflector.call(provider, Reflector.ForgeWorldProvider_getSkyRenderer);
            if (obj != null) {
                Reflector.callVoid(obj, Reflector.IRenderHandler_render, f, this.theWorld, this.mc);
                return;
            }
        }

        if (this.mc.theWorld.provider.dimensionId == 1) {
            if (!Config.isSkyEnabled()) {
                return;
            }

            GL11.glDisable(2912);
            if (Config.isShaders()) {
                Shaders.disableFog();
            }

            GL11.glDisable(3008);
            GL11.glEnable(3042);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            RenderHelper.disableStandardItemLighting();
            GL11.glDepthMask(false);
            this.renderEngine.bindTexture(locationEndSkyPng);
            Tessellator instance = Tessellator.instance;

            for (int i = 0; i < 6; i++) {
                GL11.glPushMatrix();
                if (i == 1) {
                    GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (i == 2) {
                    GL11.glRotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                }

                if (i == 3) {
                    GL11.glRotatef(180.0F, 1.0F, 0.0F, 0.0F);
                }

                if (i == 4) {
                    GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                }

                if (i == 5) {
                    GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                }

                instance.startDrawingQuads();
                instance.setColorOpaque_I(2631720);
                instance.addVertexWithUV(-100.0, -100.0, -100.0, 0.0, 0.0);
                instance.addVertexWithUV(-100.0, -100.0, 100.0, 0.0, 16.0);
                instance.addVertexWithUV(100.0, -100.0, 100.0, 16.0, 16.0);
                instance.addVertexWithUV(100.0, -100.0, -100.0, 16.0, 0.0);
                instance.draw();
                GL11.glPopMatrix();
            }

            GL11.glDepthMask(true);
            GL11.glEnable(3553);
            if (Config.isShaders()) {
                Shaders.enableTexture2D();
            }

            GL11.glEnable(3008);
            GL11.glDisable(3042);
        } else if (this.mc.theWorld.provider.isSurfaceWorld()) {
            GL11.glDisable(3553);
            if (Config.isShaders()) {
                Shaders.disableTexture2D();
            }

            Vec3 skyColor = this.theWorld.getSkyColor(this.mc.renderViewEntity, f);
            skyColor = CustomColorizer.getSkyColor(
                skyColor, this.mc.theWorld, this.mc.renderViewEntity.posX, this.mc.renderViewEntity.posY + 1.0, this.mc.renderViewEntity.posZ
            );
            if (Config.isShaders()) {
                Shaders.setSkyColor(skyColor);
            }

            float xCoord = (float)skyColor.xCoord;
            float yCoord = (float)skyColor.yCoord;
            float zCoord = (float)skyColor.zCoord;
            if (this.mc.gameSettings.anaglyph) {
                float f2 = (xCoord * 30.0F + yCoord * 59.0F + zCoord * 11.0F) / 100.0F;
                float f3 = (xCoord * 30.0F + yCoord * 70.0F) / 100.0F;
                float f4 = (xCoord * 30.0F + zCoord * 70.0F) / 100.0F;
                xCoord = f2;
                yCoord = f3;
                zCoord = f4;
            }

            GL11.glColor3f(xCoord, yCoord, zCoord);
            Tessellator instance = Tessellator.instance;
            GL11.glDepthMask(false);
            GL11.glEnable(2912);
            if (Config.isShaders()) {
                Shaders.enableFog();
            }

            GL11.glColor3f(xCoord, yCoord, zCoord);
            if (Config.isShaders()) {
                Shaders.preSkyList();
            }

            if (Config.isSkyEnabled()) {
                GL11.glCallList(this.glSkyList);
            }

            GL11.glDisable(2912);
            if (Config.isShaders()) {
                Shaders.disableFog();
            }

            GL11.glDisable(3008);
            GL11.glEnable(3042);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            RenderHelper.disableStandardItemLighting();
            float[] calcSunriseSunsetColors = this.theWorld.provider.calcSunriseSunsetColors(this.theWorld.getCelestialAngle(f), f);
            if (calcSunriseSunsetColors != null && Config.isSunMoonEnabled()) {
                GL11.glDisable(3553);
                if (Config.isShaders()) {
                    Shaders.disableTexture2D();
                }

                GL11.glShadeModel(7425);
                GL11.glPushMatrix();
                GL11.glRotatef(90.0F, 1.0F, 0.0F, 0.0F);
                GL11.glRotatef(MathHelper.sin(this.theWorld.getCelestialAngleRadians(f)) < 0.0F ? 180.0F : 0.0F, 0.0F, 0.0F, 1.0F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                float f2 = calcSunriseSunsetColors[0];
                float f3 = calcSunriseSunsetColors[1];
                float f4 = calcSunriseSunsetColors[2];
                if (this.mc.gameSettings.anaglyph) {
                    float f5 = (f2 * 30.0F + f3 * 59.0F + f4 * 11.0F) / 100.0F;
                    float f6 = (f2 * 30.0F + f3 * 70.0F) / 100.0F;
                    float f7 = (f2 * 30.0F + f4 * 70.0F) / 100.0F;
                    f2 = f5;
                    f3 = f6;
                    f4 = f7;
                }

                instance.startDrawing(6);
                instance.setColorRGBA_F(f2, f3, f4, calcSunriseSunsetColors[3]);
                instance.addVertex(0.0, 100.0, 0.0);
                byte b = 16;
                instance.setColorRGBA_F(calcSunriseSunsetColors[0], calcSunriseSunsetColors[1], calcSunriseSunsetColors[2], 0.0F);

                for (int i = 0; i <= b; i++) {
                    float f5 = i * (float) Math.PI * 2.0F / b;
                    float sin = MathHelper.sin(f5);
                    float cos = MathHelper.cos(f5);
                    instance.addVertex(sin * 120.0F, cos * 120.0F, -cos * 40.0F * calcSunriseSunsetColors[3]);
                }

                instance.draw();
                GL11.glPopMatrix();
                GL11.glShadeModel(7424);
            }

            GL11.glEnable(3553);
            if (Config.isShaders()) {
                Shaders.enableTexture2D();
            }

            OpenGlHelper.glBlendFunc(770, 1, 1, 0);
            GL11.glPushMatrix();
            float f2 = 1.0F - this.theWorld.getRainStrength(f);
            float f3 = 0.0F;
            float f4 = 0.0F;
            float f5 = 0.0F;
            GL11.glColor4f(1.0F, 1.0F, 1.0F, f2);
            GL11.glTranslatef(f3, f4, f5);
            GL11.glRotatef(-90.0F, 0.0F, 1.0F, 0.0F);
            CustomSky.renderSky(this.theWorld, this.renderEngine, f);
            if (Config.isShaders()) {
                Shaders.preCelestialRotate();
            }

            GL11.glRotatef(this.theWorld.getCelestialAngle(f) * 360.0F, 1.0F, 0.0F, 0.0F);
            if (Config.isShaders()) {
                Shaders.postCelestialRotate();
            }

            float f6 = 30.0F;
            if (Config.isSunTexture()) {
                this.renderEngine.bindTexture(locationSunPng);
                instance.startDrawingQuads();
                instance.addVertexWithUV(-f6, 100.0, -f6, 0.0, 0.0);
                instance.addVertexWithUV(f6, 100.0, -f6, 1.0, 0.0);
                instance.addVertexWithUV(f6, 100.0, f6, 1.0, 1.0);
                instance.addVertexWithUV(-f6, 100.0, f6, 0.0, 1.0);
                instance.draw();
            }

            f6 = 20.0F;
            if (Config.isMoonTexture()) {
                this.renderEngine.bindTexture(locationMoonPhasesPng);
                int moonPhase = this.theWorld.getMoonPhase();
                int n = moonPhase % 4;
                int n2 = moonPhase / 4 % 2;
                float f7 = (n + 0) / 4.0F;
                float f8 = (n2 + 0) / 2.0F;
                float f9 = (n + 1) / 4.0F;
                float f10 = (n2 + 1) / 2.0F;
                instance.startDrawingQuads();
                instance.addVertexWithUV(-f6, -100.0, f6, f9, f10);
                instance.addVertexWithUV(f6, -100.0, f6, f7, f10);
                instance.addVertexWithUV(f6, -100.0, -f6, f7, f8);
                instance.addVertexWithUV(-f6, -100.0, -f6, f9, f8);
                instance.draw();
            }

            GL11.glDisable(3553);
            if (Config.isShaders()) {
                Shaders.disableTexture2D();
            }

            float f7 = this.theWorld.getStarBrightness(f) * f2;
            if (f7 > 0.0F && Config.isStarsEnabled() && !CustomSky.hasSkyLayers(this.theWorld)) {
                GL11.glColor4f(f7, f7, f7, f7);
                GL11.glCallList(this.starGLCallList);
            }

            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glDisable(3042);
            GL11.glEnable(3008);
            GL11.glEnable(2912);
            if (Config.isShaders()) {
                Shaders.enableFog();
            }

            GL11.glPopMatrix();
            GL11.glDisable(3553);
            if (Config.isShaders()) {
                Shaders.disableTexture2D();
            }

            GL11.glColor3f(0.0F, 0.0F, 0.0F);
            double d = this.mc.thePlayer.getPosition(f).yCoord - this.theWorld.getHorizon();
            if (d < 0.0) {
                GL11.glPushMatrix();
                GL11.glTranslatef(0.0F, 12.0F, 0.0F);
                GL11.glCallList(this.glSkyList2);
                GL11.glPopMatrix();
                f4 = 1.0F;
                f5 = -((float)(d + 65.0));
                f6 = -f4;
                instance.startDrawingQuads();
                instance.setColorRGBA_I(0, 255);
                instance.addVertex(-f4, f5, f4);
                instance.addVertex(f4, f5, f4);
                instance.addVertex(f4, f6, f4);
                instance.addVertex(-f4, f6, f4);
                instance.addVertex(-f4, f6, -f4);
                instance.addVertex(f4, f6, -f4);
                instance.addVertex(f4, f5, -f4);
                instance.addVertex(-f4, f5, -f4);
                instance.addVertex(f4, f6, -f4);
                instance.addVertex(f4, f6, f4);
                instance.addVertex(f4, f5, f4);
                instance.addVertex(f4, f5, -f4);
                instance.addVertex(-f4, f5, -f4);
                instance.addVertex(-f4, f5, f4);
                instance.addVertex(-f4, f6, f4);
                instance.addVertex(-f4, f6, -f4);
                instance.addVertex(-f4, f6, -f4);
                instance.addVertex(-f4, f6, f4);
                instance.addVertex(f4, f6, f4);
                instance.addVertex(f4, f6, -f4);
                instance.draw();
            }

            if (this.theWorld.provider.isSkyColored()) {
                GL11.glColor3f(xCoord * 0.2F + 0.04F, yCoord * 0.2F + 0.04F, zCoord * 0.6F + 0.1F);
            } else {
                GL11.glColor3f(xCoord, yCoord, zCoord);
            }

            if (this.mc.gameSettings.renderDistanceChunks <= 4) {
                GL11.glColor3f(this.mc.entityRenderer.fogColorRed, this.mc.entityRenderer.fogColorGreen, this.mc.entityRenderer.fogColorBlue);
            }

            GL11.glPushMatrix();
            GL11.glTranslatef(0.0F, -((float)(d - 16.0)), 0.0F);
            FloatEvent5 floatEvent5 = new FloatEvent5(f);
            CheatBreaker.getInstance().getEventBus().handleEvent(floatEvent5);
            if (Config.isSkyEnabled() && !floatEvent5.isCancelled()) {
                GL11.glCallList(this.glSkyList2);
            }

            GL11.glPopMatrix();
            GL11.glEnable(3553);
            if (Config.isShaders()) {
                Shaders.enableTexture2D();
            }

            GL11.glDepthMask(true);
        }
    }

    public void renderClouds(float f) {
        if (!Config.isCloudsOff()) {
            if (Reflector.ForgeWorldProvider_getCloudRenderer.exists()) {
                WorldProvider provider = this.mc.theWorld.provider;
                Object obj = Reflector.call(provider, Reflector.ForgeWorldProvider_getCloudRenderer);
                if (obj != null) {
                    Reflector.callVoid(obj, Reflector.IRenderHandler_render, f, this.theWorld, this.mc);
                    return;
                }
            }

            if (this.mc.theWorld.provider.isSurfaceWorld()) {
                if (Config.isCloudsFancy()) {
                    this.renderCloudsFancy(f);
                } else {
                    float f2 = f;
                    f = 0.0F;
                    GL11.glDisable(2884);
                    float f3 = (float)(this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * f);
                    byte b = 32;
                    int n = 256 / b;
                    Tessellator instance = Tessellator.instance;
                    this.renderEngine.bindTexture(locationCloudsPng);
                    GL11.glEnable(3042);
                    OpenGlHelper.glBlendFunc(770, 771, 1, 0);
                    if (this.isFancyGlListClouds || this.cloudTickCounter >= this.cloudTickCounterGlList + 20) {
                        GL11.glNewList(this.glListClouds, 4864);
                        Vec3 cloudColour = this.theWorld.getCloudColour(f);
                        float xCoord = (float)cloudColour.xCoord;
                        float yCoord = (float)cloudColour.yCoord;
                        float zCoord = (float)cloudColour.zCoord;
                        if (this.mc.gameSettings.anaglyph) {
                            float f4 = (xCoord * 30.0F + yCoord * 59.0F + zCoord * 11.0F) / 100.0F;
                            float f5 = (xCoord * 30.0F + yCoord * 70.0F) / 100.0F;
                            float f6 = (xCoord * 30.0F + zCoord * 70.0F) / 100.0F;
                            xCoord = f4;
                            yCoord = f5;
                            zCoord = f6;
                        }

                        float f4 = 4.8828125E-4F;
                        double d = this.cloudTickCounter + f;
                        double d2 = this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * f + d * 0.03F;
                        double d3 = this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * f;
                        int n2 = MathHelper.floor_double(d2 / 2048.0);
                        int n3 = MathHelper.floor_double(d3 / 2048.0);
                        d2 -= n2 * 2048;
                        d3 -= n3 * 2048;
                        float f5 = this.theWorld.provider.getCloudHeight() - f3 + 0.33F;
                        f5 += this.mc.gameSettings.ofCloudsHeight * 128.0F;
                        float f6 = (float)(d2 * f4);
                        float f7 = (float)(d3 * f4);
                        instance.startDrawingQuads();
                        instance.setColorRGBA_F(xCoord, yCoord, zCoord, 0.8F);

                        for (int i = -b * n; i < b * n; i += b) {
                            for (int j = -b * n; j < b * n; j += b) {
                                instance.addVertexWithUV(i + 0, f5, j + b, (i + 0) * f4 + f6, (j + b) * f4 + f7);
                                instance.addVertexWithUV(i + b, f5, j + b, (i + b) * f4 + f6, (j + b) * f4 + f7);
                                instance.addVertexWithUV(i + b, f5, j + 0, (i + b) * f4 + f6, (j + 0) * f4 + f7);
                                instance.addVertexWithUV(i + 0, f5, j + 0, (i + 0) * f4 + f6, (j + 0) * f4 + f7);
                            }
                        }

                        instance.draw();
                        GL11.glEndList();
                        this.isFancyGlListClouds = false;
                        this.cloudTickCounterGlList = this.cloudTickCounter;
                        this.cloudPlayerX = this.mc.renderViewEntity.prevPosX;
                        this.cloudPlayerY = this.mc.renderViewEntity.prevPosY;
                        this.cloudPlayerZ = this.mc.renderViewEntity.prevPosZ;
                    }

                    EntityLivingBase renderViewEntity = this.mc.renderViewEntity;
                    double d = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * f2;
                    double d2 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * f2;
                    double d3 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * f2;
                    double d4 = this.cloudTickCounter - this.cloudTickCounterGlList + f2;
                    float f4 = (float)(d - this.cloudPlayerX + d4 * 0.03);
                    float f5 = (float)(d2 - this.cloudPlayerY);
                    float f6 = (float)(d3 - this.cloudPlayerZ);
                    GL11.glTranslatef(-f4, -f5, -f6);
                    GL11.glCallList(this.glListClouds);
                    GL11.glTranslatef(f4, f5, f6);
                    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                    GL11.glDisable(3042);
                    GL11.glEnable(2884);
                }
            }
        }
    }

    public boolean hasCloudFog(double d, double d2, double d3, float f) {
        return false;
    }

    public void renderCloudsFancy(float f) {
        float f2 = f;
        f = 0.0F;
        GL11.glDisable(2884);
        float f3 = (float)(this.mc.renderViewEntity.lastTickPosY + (this.mc.renderViewEntity.posY - this.mc.renderViewEntity.lastTickPosY) * f);
        Tessellator instance = Tessellator.instance;
        float f4 = 12.0F;
        float f5 = 4.0F;
        double d = this.cloudTickCounter + f;
        double d2 = (this.mc.renderViewEntity.prevPosX + (this.mc.renderViewEntity.posX - this.mc.renderViewEntity.prevPosX) * f + d * 0.03F) / f4;
        double d3 = (this.mc.renderViewEntity.prevPosZ + (this.mc.renderViewEntity.posZ - this.mc.renderViewEntity.prevPosZ) * f) / f4 + 0.33F;
        float f6 = this.theWorld.provider.getCloudHeight() - f3 + 0.33F;
        f6 += this.mc.gameSettings.ofCloudsHeight * 128.0F;
        int n = MathHelper.floor_double(d2 / 2048.0);
        int n2 = MathHelper.floor_double(d3 / 2048.0);
        d2 -= n * 2048;
        d3 -= n2 * 2048;
        this.renderEngine.bindTexture(locationCloudsPng);
        GL11.glEnable(3042);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);
        if (!this.isFancyGlListClouds || this.cloudTickCounter >= this.cloudTickCounterGlList + 20) {
            GL11.glNewList(this.glListClouds, 4864);
            Vec3 cloudColour = this.theWorld.getCloudColour(f);
            float xCoord = (float)cloudColour.xCoord;
            float yCoord = (float)cloudColour.yCoord;
            float zCoord = (float)cloudColour.zCoord;
            if (this.mc.gameSettings.anaglyph) {
                float f7 = (xCoord * 30.0F + yCoord * 59.0F + zCoord * 11.0F) / 100.0F;
                float f8 = (xCoord * 30.0F + yCoord * 70.0F) / 100.0F;
                float f9 = (xCoord * 30.0F + zCoord * 70.0F) / 100.0F;
                xCoord = f7;
                yCoord = f8;
                zCoord = f9;
            }

            float f7 = (float)(d2 * 0.0);
            float f8 = (float)(d3 * 0.0);
            float f9 = 0.00390625F;
            f7 = MathHelper.floor_double(d2) * f9;
            f8 = MathHelper.floor_double(d3) * f9;
            float f10 = (float)(d2 - MathHelper.floor_double(d2));
            float f11 = (float)(d3 - MathHelper.floor_double(d3));
            byte b = 8;
            byte b2 = 4;
            float f12 = 9.765625E-4F;
            GL11.glScalef(f4, 1.0F, f4);

            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    GL11.glColorMask(false, false, false, false);
                } else if (this.mc.gameSettings.anaglyph) {
                    if (EntityRenderer.anaglyphField == 0) {
                        GL11.glColorMask(false, true, true, true);
                    } else {
                        GL11.glColorMask(true, false, false, true);
                    }
                } else {
                    GL11.glColorMask(true, true, true, true);
                }

                for (int j = -b2 + 1; j <= b2; j++) {
                    for (int k = -b2 + 1; k <= b2; k++) {
                        instance.startDrawingQuads();
                        float f13 = j * b;
                        float f14 = k * b;
                        float f15 = f13 - f10;
                        float f16 = f14 - f11;
                        if (f6 > -f5 - 1.0F) {
                            instance.setColorRGBA_F(xCoord * 0.7F, yCoord * 0.7F, zCoord * 0.7F, 0.8F);
                            instance.setNormal(0.0F, -1.0F, 0.0F);
                            instance.addVertexWithUV(f15 + 0.0F, f6 + 0.0F, f16 + b, (f13 + 0.0F) * f9 + f7, (f14 + b) * f9 + f8);
                            instance.addVertexWithUV(f15 + b, f6 + 0.0F, f16 + b, (f13 + b) * f9 + f7, (f14 + b) * f9 + f8);
                            instance.addVertexWithUV(f15 + b, f6 + 0.0F, f16 + 0.0F, (f13 + b) * f9 + f7, (f14 + 0.0F) * f9 + f8);
                            instance.addVertexWithUV(f15 + 0.0F, f6 + 0.0F, f16 + 0.0F, (f13 + 0.0F) * f9 + f7, (f14 + 0.0F) * f9 + f8);
                        }

                        if (f6 <= f5 + 1.0F) {
                            instance.setColorRGBA_F(xCoord, yCoord, zCoord, 0.8F);
                            instance.setNormal(0.0F, 1.0F, 0.0F);
                            instance.addVertexWithUV(f15 + 0.0F, f6 + f5 - f12, f16 + b, (f13 + 0.0F) * f9 + f7, (f14 + b) * f9 + f8);
                            instance.addVertexWithUV(f15 + b, f6 + f5 - f12, f16 + b, (f13 + b) * f9 + f7, (f14 + b) * f9 + f8);
                            instance.addVertexWithUV(f15 + b, f6 + f5 - f12, f16 + 0.0F, (f13 + b) * f9 + f7, (f14 + 0.0F) * f9 + f8);
                            instance.addVertexWithUV(f15 + 0.0F, f6 + f5 - f12, f16 + 0.0F, (f13 + 0.0F) * f9 + f7, (f14 + 0.0F) * f9 + f8);
                        }

                        instance.setColorRGBA_F(xCoord * 0.9F, yCoord * 0.9F, zCoord * 0.9F, 0.8F);
                        if (j > -1) {
                            instance.setNormal(-1.0F, 0.0F, 0.0F);

                            for (int m = 0; m < b; m++) {
                                instance.addVertexWithUV(
                                    f15 + m + 0.0F, f6 + 0.0F, f16 + b, (f13 + m + 0.5F) * f9 + f7, (f14 + b) * f9 + f8
                                );
                                instance.addVertexWithUV(
                                    f15 + m + 0.0F, f6 + f5, f16 + b, (f13 + m + 0.5F) * f9 + f7, (f14 + b) * f9 + f8
                                );
                                instance.addVertexWithUV(
                                    f15 + m + 0.0F, f6 + f5, f16 + 0.0F, (f13 + m + 0.5F) * f9 + f7, (f14 + 0.0F) * f9 + f8
                                );
                                instance.addVertexWithUV(
                                    f15 + m + 0.0F, f6 + 0.0F, f16 + 0.0F, (f13 + m + 0.5F) * f9 + f7, (f14 + 0.0F) * f9 + f8
                                );
                            }
                        }

                        if (j <= 1) {
                            instance.setNormal(1.0F, 0.0F, 0.0F);

                            for (int m = 0; m < b; m++) {
                                instance.addVertexWithUV(
                                    f15 + m + 1.0F - f12, f6 + 0.0F, f16 + b, (f13 + m + 0.5F) * f9 + f7, (f14 + b) * f9 + f8
                                );
                                instance.addVertexWithUV(
                                    f15 + m + 1.0F - f12, f6 + f5, f16 + b, (f13 + m + 0.5F) * f9 + f7, (f14 + b) * f9 + f8
                                );
                                instance.addVertexWithUV(
                                    f15 + m + 1.0F - f12, f6 + f5, f16 + 0.0F, (f13 + m + 0.5F) * f9 + f7, (f14 + 0.0F) * f9 + f8
                                );
                                instance.addVertexWithUV(
                                    f15 + m + 1.0F - f12, f6 + 0.0F, f16 + 0.0F, (f13 + m + 0.5F) * f9 + f7, (f14 + 0.0F) * f9 + f8
                                );
                            }
                        }

                        instance.setColorRGBA_F(xCoord * 0.8F, yCoord * 0.8F, zCoord * 0.8F, 0.8F);
                        if (k > -1) {
                            instance.setNormal(0.0F, 0.0F, -1.0F);

                            for (int m = 0; m < b; m++) {
                                instance.addVertexWithUV(
                                    f15 + 0.0F, f6 + f5, f16 + m + 0.0F, (f13 + 0.0F) * f9 + f7, (f14 + m + 0.5F) * f9 + f8
                                );
                                instance.addVertexWithUV(
                                    f15 + b, f6 + f5, f16 + m + 0.0F, (f13 + b) * f9 + f7, (f14 + m + 0.5F) * f9 + f8
                                );
                                instance.addVertexWithUV(
                                    f15 + b, f6 + 0.0F, f16 + m + 0.0F, (f13 + b) * f9 + f7, (f14 + m + 0.5F) * f9 + f8
                                );
                                instance.addVertexWithUV(
                                    f15 + 0.0F, f6 + 0.0F, f16 + m + 0.0F, (f13 + 0.0F) * f9 + f7, (f14 + m + 0.5F) * f9 + f8
                                );
                            }
                        }

                        if (k <= 1) {
                            instance.setNormal(0.0F, 0.0F, 1.0F);

                            for (int m = 0; m < b; m++) {
                                instance.addVertexWithUV(
                                    f15 + 0.0F, f6 + f5, f16 + m + 1.0F - f12, (f13 + 0.0F) * f9 + f7, (f14 + m + 0.5F) * f9 + f8
                                );
                                instance.addVertexWithUV(
                                    f15 + b, f6 + f5, f16 + m + 1.0F - f12, (f13 + b) * f9 + f7, (f14 + m + 0.5F) * f9 + f8
                                );
                                instance.addVertexWithUV(
                                    f15 + b, f6 + 0.0F, f16 + m + 1.0F - f12, (f13 + b) * f9 + f7, (f14 + m + 0.5F) * f9 + f8
                                );
                                instance.addVertexWithUV(
                                    f15 + 0.0F, f6 + 0.0F, f16 + m + 1.0F - f12, (f13 + 0.0F) * f9 + f7, (f14 + m + 0.5F) * f9 + f8
                                );
                            }
                        }

                        instance.draw();
                    }
                }
            }

            GL11.glEndList();
            this.isFancyGlListClouds = true;
            this.cloudTickCounterGlList = this.cloudTickCounter;
            this.cloudPlayerX = this.mc.renderViewEntity.prevPosX;
            this.cloudPlayerY = this.mc.renderViewEntity.prevPosY;
            this.cloudPlayerZ = this.mc.renderViewEntity.prevPosZ;
        }

        EntityLivingBase renderViewEntity = this.mc.renderViewEntity;
        double d4 = renderViewEntity.prevPosX + (renderViewEntity.posX - renderViewEntity.prevPosX) * f2;
        double d5 = renderViewEntity.prevPosY + (renderViewEntity.posY - renderViewEntity.prevPosY) * f2;
        double d6 = renderViewEntity.prevPosZ + (renderViewEntity.posZ - renderViewEntity.prevPosZ) * f2;
        double d7 = this.cloudTickCounter - this.cloudTickCounterGlList + f2;
        float f7 = (float)(d4 - this.cloudPlayerX + d7 * 0.03);
        float f8 = (float)(d5 - this.cloudPlayerY);
        float f9 = (float)(d6 - this.cloudPlayerZ);
        GL11.glTranslatef(-f7, -f8, -f9);
        GL11.glCallList(this.glListClouds);
        GL11.glTranslatef(f7, f8, f9);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glDisable(3042);
        GL11.glEnable(2884);
    }

    public boolean updateRenderers(EntityLivingBase entityLivingBase, boolean flag) {
        this.renderViewEntity = entityLivingBase;
        if (WrUpdates.hasWrUpdater()) {
            return WrUpdates.updateRenderers(this, entityLivingBase, flag);
        }

        if (this.compactArrayList.size() <= 0) {
            return false;
        }

        int n = 0;
        int updatesPerFrame = Config.getUpdatesPerFrame();
        if (Config.isDynamicUpdates() && !this.isMoving(entityLivingBase)) {
            updatesPerFrame *= 3;
        }

        byte b = 4;
        int n2 = 0;
        WorldRenderer worldRenderer = null;
        float f = Float.MAX_VALUE;
        int n3 = -1;

        for (int i = 0; i < this.compactArrayList.size(); i++) {
            WorldRenderer worldRenderer2 = (WorldRenderer)this.compactArrayList.get(i);
            if (worldRenderer2 != null) {
                n2++;
                if (!worldRenderer2.needsUpdate) {
                    this.compactArrayList.set(i, null);
                } else {
                    float distanceToEntitySquared = worldRenderer2.distanceToEntitySquared(entityLivingBase);
                    if (distanceToEntitySquared <= 256.0F && this.isActingNow()) {
                        worldRenderer2.updateRenderer(entityLivingBase);
                        worldRenderer2.needsUpdate = false;
                        this.compactArrayList.set(i, null);
                        n++;
                    } else {
                        if (distanceToEntitySquared > 256.0F && n >= updatesPerFrame) {
                            break;
                        }

                        if (!worldRenderer2.isInFrustum) {
                            distanceToEntitySquared *= b;
                        }

                        if (worldRenderer == null) {
                            worldRenderer = worldRenderer2;
                            f = distanceToEntitySquared;
                            n3 = i;
                        } else if (distanceToEntitySquared < f) {
                            worldRenderer = worldRenderer2;
                            f = distanceToEntitySquared;
                            n3 = i;
                        }
                    }
                }
            }
        }

        if (worldRenderer != null) {
            worldRenderer.updateRenderer(entityLivingBase);
            worldRenderer.needsUpdate = false;
            this.compactArrayList.set(n3, null);
            n++;
            float f2 = f / 5.0F;

            for (int i = 0; i < this.compactArrayList.size() && n < updatesPerFrame; i++) {
                WorldRenderer worldRenderer2 = (WorldRenderer)this.compactArrayList.get(i);
                if (worldRenderer2 != null) {
                    float distanceToEntitySquared = worldRenderer2.distanceToEntitySquared(entityLivingBase);
                    if (!worldRenderer2.isInFrustum) {
                        distanceToEntitySquared *= b;
                    }

                    float abs = Math.abs(distanceToEntitySquared - f);
                    if (abs < f2) {
                        worldRenderer2.updateRenderer(entityLivingBase);
                        worldRenderer2.needsUpdate = false;
                        this.compactArrayList.set(i, null);
                        n++;
                    }
                }
            }
        }

        if (n2 == 0) {
            this.compactArrayList.clear();
        }

        this.compactArrayList.compact();
        return true;
    }

    public void drawBlockDamageTexture(Tessellator tessellator, EntityPlayer entityPlayer, float f) {
        this.drawBlockDamageTexture(tessellator, entityPlayer, f);
    }

    public void drawBlockDamageTexture(Tessellator tessellator, EntityLivingBase entityLivingBase, float f) {
        double d = entityLivingBase.lastTickPosX + (entityLivingBase.posX - entityLivingBase.lastTickPosX) * f;
        double d2 = entityLivingBase.lastTickPosY + (entityLivingBase.posY - entityLivingBase.lastTickPosY) * f;
        double d3 = entityLivingBase.lastTickPosZ + (entityLivingBase.posZ - entityLivingBase.lastTickPosZ) * f;
        if (!this.damagedBlocks.isEmpty()) {
            OpenGlHelper.glBlendFunc(774, 768, 1, 0);
            this.renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 0.5F);
            GL11.glPushMatrix();
            GL11.glPolygonOffset(-3.0F, -3.0F);
            GL11.glEnable(32823);
            GL11.glAlphaFunc(516, 0.1F);
            GL11.glEnable(3008);
            if (Config.isShaders()) {
                Shaders.beginBlockDestroyProgress();
            }

            tessellator.startDrawingQuads();
            tessellator.setTranslation(-d, -d2, -d3);
            tessellator.disableColor();
            Iterator iterator = this.damagedBlocks.values().iterator();

            while (iterator.hasNext()) {
                DestroyBlockProgress destroyBlockProgress = (DestroyBlockProgress)iterator.next();
                double d4 = destroyBlockProgress.getPartialBlockX() - d;
                double d5 = destroyBlockProgress.getPartialBlockY() - d2;
                double d6 = destroyBlockProgress.getPartialBlockZ() - d3;
                if (d4 * d4 + d5 * d5 + d6 * d6 > 1024.0) {
                    iterator.remove();
                } else {
                    Block block = this.theWorld.getBlock(destroyBlockProgress.getPartialBlockX(), destroyBlockProgress.getPartialBlockY(), destroyBlockProgress.getPartialBlockZ());
                    if (block.getMaterial() != Material.air) {
                        this.renderBlocksRg
                            .renderBlockUsingTexture(
                                block,
                                destroyBlockProgress.getPartialBlockX(),
                                destroyBlockProgress.getPartialBlockY(),
                                destroyBlockProgress.getPartialBlockZ(),
                                this.destroyBlockIcons[destroyBlockProgress.getPartialBlockDamage()]
                            );
                    }
                }
            }

            tessellator.draw();
            tessellator.setTranslation(0.0, 0.0, 0.0);
            if (Config.isShaders()) {
                Shaders.endBlockDestroyProgress();
            }

            GL11.glDisable(3008);
            GL11.glPolygonOffset(0.0F, 0.0F);
            GL11.glDisable(32823);
            GL11.glEnable(3008);
            GL11.glDepthMask(true);
            GL11.glPopMatrix();
        }
    }

    public void drawSelectionBox(EntityPlayer entityPlayer, MovingObjectPosition movingObjectPosition, int n, float f) {
        if (n == 0 && movingObjectPosition.typeOfHit == MovingObjectType.BLOCK) {
            GL11.glEnable(3042);
            OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            GL11.glColor4f(0.0F, 0.0F, 0.0F, 0.4F);
            float f2 = 2.0F;
            FloatEvent8 floatEvent8 = new FloatEvent8(f2);
            CheatBreaker.getInstance().getEventBus().handleEvent(floatEvent8);
            f2 = floatEvent8.getFloatValue();
            GL11.glLineWidth(f2);
            GL11.glDisable(3553);
            if (Config.isShaders()) {
                Shaders.disableTexture2D();
            }

            GL11.glDepthMask(false);
            CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent15());
            double d = 0.002F;
            DoubleEvent2 doubleEvent2 = new DoubleEvent2(d);
            CheatBreaker.getInstance().getEventBus().handleEvent(doubleEvent2);
            d = doubleEvent2.getDoubleValue();
            Block block = this.theWorld.getBlock(movingObjectPosition.blockX, movingObjectPosition.blockY, movingObjectPosition.blockZ);
            BlockEvent blockEvent = new BlockEvent(block);
            CheatBreaker.getInstance().getEventBus().handleEvent(blockEvent);
            if (!blockEvent.isCancelled() && block.getMaterial() != Material.air) {
                block.setBlockBoundsBasedOnState(this.theWorld, movingObjectPosition.blockX, movingObjectPosition.blockY, movingObjectPosition.blockZ);
                double d2 = entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * f;
                double d3 = entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * f;
                double d4 = entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * f;
                SimpleEvent21 simpleEvent21 = new SimpleEvent21();
                CheatBreaker.getInstance().getEventBus().handleEvent(simpleEvent21);
                if (!simpleEvent21.isCancelled()) {
                    CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent2());
                    drawOutlinedBoundingBox(
                        block.getSelectedBoundingBoxFromPool(this.theWorld, movingObjectPosition.blockX, movingObjectPosition.blockY, movingObjectPosition.blockZ)
                            .expand(d, d, d)
                            .getOffsetBoundingBox(-d2, -d3, -d4),
                        -1
                    );
                    CheatBreaker.getInstance().getEventBus().handleEvent(new SimpleEvent19());
                }

                CheatBreaker.getInstance()
                    .getEventBus()
                    .handleEvent(new DrawSelectionBoxEvent(block, this.theWorld, movingObjectPosition.blockX, movingObjectPosition.blockY, movingObjectPosition.blockZ, d, d2, d3, d4));
            }

            GL11.glDepthMask(true);
            GL11.glEnable(3553);
            if (Config.isShaders()) {
                Shaders.enableTexture2D();
            }

            GlStateManager.enableDepth();
            GL11.glDisable(3042);
        }
    }

    public static void drawOutlinedBoundingBox(AxisAlignedBB axisAlignedBB, int n) {
        Tessellator instance = Tessellator.instance;
        instance.startDrawing(3);
        if (n != -1) {
            instance.setColorOpaque_I(n);
        }

        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.draw();
        instance.startDrawing(3);
        if (n != -1) {
            instance.setColorOpaque_I(n);
        }

        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.draw();
        instance.startDrawing(1);
        if (n != -1) {
            instance.setColorOpaque_I(n);
        }

        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.draw();
    }

    public void markBlocksForUpdate(int n, int n2, int n3, int n4, int n5, int n6) {
        int bucketInt = MathHelper.bucketInt(n, 16);
        int bucketInt2 = MathHelper.bucketInt(n2, 16);
        int bucketInt3 = MathHelper.bucketInt(n3, 16);
        int bucketInt4 = MathHelper.bucketInt(n4, 16);
        int bucketInt5 = MathHelper.bucketInt(n5, 16);
        int bucketInt6 = MathHelper.bucketInt(n6, 16);

        for (int i = bucketInt; i <= bucketInt4; i++) {
            int n7 = i % this.renderChunksWide;
            if (n7 < 0) {
                n7 += this.renderChunksWide;
            }

            for (int j = bucketInt2; j <= bucketInt5; j++) {
                int n8 = j % this.renderChunksTall;
                if (n8 < 0) {
                    n8 += this.renderChunksTall;
                }

                for (int k = bucketInt3; k <= bucketInt6; k++) {
                    int n9 = k % this.renderChunksDeep;
                    if (n9 < 0) {
                        n9 += this.renderChunksDeep;
                    }

                    int n10 = (n9 * this.renderChunksTall + n8) * this.renderChunksWide + n7;
                    WorldRenderer worldRenderer = this.worldRenderers[n10];
                    if (worldRenderer != null && !worldRenderer.needsUpdate) {
                        this.compactArrayList.add(worldRenderer);
                        worldRenderer.markDirty();
                    }
                }
            }
        }
    }

    @Override
    public void markBlockForUpdate(int n, int n2, int n3) {
        this.markBlocksForUpdate(n - 1, n2 - 1, n3 - 1, n + 1, n2 + 1, n3 + 1);
    }

    @Override
    public void markBlockForRenderUpdate(int n, int n2, int n3) {
        this.markBlocksForUpdate(n - 1, n2 - 1, n3 - 1, n + 1, n2 + 1, n3 + 1);
    }

    @Override
    public void markBlockRangeForRenderUpdate(int n, int n2, int n3, int n4, int n5, int n6) {
        this.markBlocksForUpdate(n - 1, n2 - 1, n3 - 1, n4 + 1, n5 + 1, n6 + 1);
    }

    public void clipRenderersByFrustum(ICamera camera, float f) {
        boolean flag = !Config.isFogOff();
        double d = this.renderDistanceChunks * 16 * this.renderDistanceChunks * 16;

        for (int i = 0; i < this.countSortedWorldRenderers; i++) {
            WorldRenderer worldRenderer = this.sortedWorldRenderers[i];
            if (!worldRenderer.skipAllRenderPasses()) {
                if (flag && worldRenderer.distanceToEntityXzSq > d) {
                    worldRenderer.isInFrustum = false;
                } else {
                    worldRenderer.updateInFrustum(camera);
                }
            }
        }
    }

    @Override
    public void playRecord(String str, int n, int n2, int n3) {
        ChunkCoordinates chunkCoordinates = new ChunkCoordinates(n, n2, n3);
        ISound sound = (ISound)this.mapSoundPositions.get(chunkCoordinates);
        if (sound != null) {
            this.mc.getSoundHandler().stopSound(sound);
            this.mapSoundPositions.remove(chunkCoordinates);
        }

        if (str != null) {
            ItemRecord record2 = ItemRecord.getRecord(str);
            if (record2 != null) {
                this.mc.ingameGUI.setRecordPlayingMessage(record2.getRecordNameLocal());
            }

            ResourceLocation resourceLocation = null;
            if (Reflector.ForgeItemRecord_getRecordResource.exists() && record2 != null) {
                resourceLocation = (ResourceLocation)Reflector.call(record2, Reflector.ForgeItemRecord_getRecordResource, str);
            }

            if (resourceLocation == null) {
                resourceLocation = new ResourceLocation(str);
            }

            PositionedSoundRecord createRecordSoundAtPosition = PositionedSoundRecord.createRecordSoundAtPosition(resourceLocation, n, n2, n3);
            this.mapSoundPositions.put(chunkCoordinates, createRecordSoundAtPosition);
            this.mc.getSoundHandler().playSound(createRecordSoundAtPosition);
        }
    }

    @Override
    public void playSound(String soundName, double x, double y, double z, float volume, float pitch) {
    }

    @Override
    public void playSoundToNearExcept(EntityPlayer entityPlayer, String str, double d, double d2, double d3, float f, float f2) {
    }

    @Override
    public void spawnParticle(String str, double d, double d2, double d3, double d4, double d5, double d6) {
        try {
            this.doSpawnParticle(str, d, d2, d3, d4, d5, d6);
        } catch (Throwable e) {
            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Exception while adding particle");
            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Particle being added");
            makeCategory.addCrashSection("Name", str);
            makeCategory.addCrashSectionCallable("Position", () -> CrashReportCategory.createPositionString(d, d2, d3));
            throw new ReportedException(makeCrashReport);
        }
    }

    public EntityFX doSpawnParticle(String str, double d, double d2, double d3, double d4, double d5, double d6) {
        if (this.mc != null && this.mc.renderViewEntity != null && this.mc.effectRenderer != null) {
            int particleSetting = this.mc.gameSettings.particleSetting;
            if (particleSetting == 1 && this.theWorld.rand.nextInt(3) == 0) {
                particleSetting = 2;
            }

            double d7 = this.mc.renderViewEntity.posX - d;
            double d8 = this.mc.renderViewEntity.posY - d2;
            double d9 = this.mc.renderViewEntity.posZ - d3;
            EntityFX entityFX = null;
            if (str.equals("hugeexplosion")) {
                if (Config.isAnimatedExplosion()) {
                    this.mc.effectRenderer.addEffect(entityFX = new EntityHugeExplodeFX(this.theWorld, d, d2, d3, d4, d5, d6));
                }
            } else if (str.equals("largeexplode")) {
                if (Config.isAnimatedExplosion()) {
                    this.mc.effectRenderer.addEffect(entityFX = new EntityLargeExplodeFX(this.renderEngine, this.theWorld, d, d2, d3, d4, d5, d6));
                }
            } else if (str.equals("fireworksSpark")) {
                this.mc.effectRenderer.addEffect(entityFX = new EntityFireworkSparkFX(this.theWorld, d, d2, d3, d4, d5, d6, this.mc.effectRenderer));
            }

            if (entityFX != null) {
                return entityFX;
            }

            double d10 = 16.0;
            double d11 = 16.0;
            if (str.equals("crit")) {
                d10 = 196.0;
            }

            if (d7 * d7 + d8 * d8 + d9 * d9 > d10 * d10) {
                return null;
            }

            if (particleSetting > 1) {
                return null;
            }

            if (str.equals("bubble")) {
                entityFX = new EntityBubbleFX(this.theWorld, d, d2, d3, d4, d5, d6);
                CustomColorizer.updateWaterFX(entityFX, this.theWorld);
            } else if (str.equals("suspended")) {
                if (Config.isWaterParticles()) {
                    entityFX = new EntitySuspendFX(this.theWorld, d, d2, d3, d4, d5, d6);
                }
            } else if (str.equals("depthsuspend")) {
                if (Config.isVoidParticles()) {
                    entityFX = new EntityAuraFX(this.theWorld, d, d2, d3, d4, d5, d6);
                }
            } else if (str.equals("townaura")) {
                entityFX = new EntityAuraFX(this.theWorld, d, d2, d3, d4, d5, d6);
                CustomColorizer.updateMyceliumFX(entityFX);
            } else if (str.equals("crit")) {
                entityFX = new EntityCritFX(this.theWorld, d, d2, d3, d4, d5, d6);
            } else if (str.equals("magicCrit")) {
                entityFX = new EntityCritFX(this.theWorld, d, d2, d3, d4, d5, d6);
                entityFX.setRBGColorF(entityFX.getRedColorF() * 0.3F, entityFX.getGreenColorF() * 0.8F, entityFX.getBlueColorF());
                entityFX.nextTextureIndexX();
            } else if (str.equals("smoke")) {
                if (Config.isAnimatedSmoke()) {
                    entityFX = new EntitySmokeFX(this.theWorld, d, d2, d3, d4, d5, d6);
                }
            } else if (str.equals("mobSpell")) {
                if (Config.isPotionParticles()
                    && (
                        !CheatBreaker.getInstance().getModManager().particlesMod.isEnabled()
                            || (Boolean)CheatBreaker.getInstance().getModManager().particlesMod.showActiveEffectParticles.getValue()
                    )) {
                    entityFX = new EntitySpellParticleFX(this.theWorld, d, d2, d3, 0.0, 0.0, 0.0);
                    if (CheatBreaker.getInstance().getModManager().particlesMod.isEnabled()) {
                        entityFX.setAlphaF((Float)CheatBreaker.getInstance().getModManager().particlesMod.activeEffectOpacity.getValue() / 100.0F);
                    }

                    entityFX.setRBGColorF((float)d4, (float)d5, (float)d6);
                }
            } else if (str.equals("mobSpellAmbient")) {
                if (Config.isPotionParticles()
                    && (
                        !CheatBreaker.getInstance().getModManager().particlesMod.isEnabled()
                            || (Boolean)CheatBreaker.getInstance().getModManager().particlesMod.showActiveEffectParticles.getValue()
                    )) {
                    entityFX = new EntitySpellParticleFX(this.theWorld, d, d2, d3, 0.0, 0.0, 0.0);
                    if (CheatBreaker.getInstance().getModManager().particlesMod.isEnabled()) {
                        entityFX.setAlphaF((Float)CheatBreaker.getInstance().getModManager().particlesMod.ambientEffectOpacity.getValue() / 100.0F);
                    } else {
                        entityFX.setAlphaF(0.15F);
                    }

                    entityFX.setRBGColorF((float)d4, (float)d5, (float)d6);
                }
            } else if (str.equals("spell")) {
                if (Config.isPotionParticles()
                    && (
                        !CheatBreaker.getInstance().getModManager().particlesMod.isEnabled()
                            || (Boolean)CheatBreaker.getInstance().getModManager().particlesMod.showSplashParticles.getValue()
                    )) {
                    entityFX = new EntitySpellParticleFX(this.theWorld, d, d2, d3, d4, d5, d6);
                    if (CheatBreaker.getInstance().getModManager().particlesMod.isEnabled()) {
                        entityFX.setAlphaF((Float)CheatBreaker.getInstance().getModManager().particlesMod.normalSplashOpacity.getValue() / 100.0F);
                    }
                }
            } else if (str.equals("instantSpell")) {
                if (Config.isPotionParticles()
                    && (
                        !CheatBreaker.getInstance().getModManager().particlesMod.isEnabled()
                            || (Boolean)CheatBreaker.getInstance().getModManager().particlesMod.showSplashParticles.getValue()
                    )) {
                    entityFX = new EntitySpellParticleFX(this.theWorld, d, d2, d3, d4, d5, d6);
                    if (CheatBreaker.getInstance().getModManager().particlesMod.isEnabled()) {
                        entityFX.setAlphaF((Float)CheatBreaker.getInstance().getModManager().particlesMod.instantSplashOpacity.getValue() / 100.0F);
                    }

                    ((EntitySpellParticleFX)entityFX).setBaseSpellTextureIndex(144);
                }
            } else if (str.equals("witchMagic")) {
                if (Config.isPotionParticles()) {
                    entityFX = new EntitySpellParticleFX(this.theWorld, d, d2, d3, d4, d5, d6);
                    ((EntitySpellParticleFX)entityFX).setBaseSpellTextureIndex(144);
                    float f = this.theWorld.rand.nextFloat() * 0.5F + 0.35F;
                    entityFX.setRBGColorF(1.0F * f, 0.0F * f, 1.0F * f);
                }
            } else if (str.equals("note")) {
                entityFX = new EntityNoteFX(this.theWorld, d, d2, d3, d4, d5, d6);
            } else if (str.equals("portal")) {
                if (Config.isPortalParticles()) {
                    entityFX = new EntityPortalFX(this.theWorld, d, d2, d3, d4, d5, d6);
                    CustomColorizer.updatePortalFX(entityFX);
                }
            } else if (str.equals("enchantmenttable")) {
                entityFX = new EntityEnchantmentTableParticleFX(this.theWorld, d, d2, d3, d4, d5, d6);
            } else if (str.equals("explode")) {
                if (Config.isAnimatedExplosion()) {
                    entityFX = new EntityExplodeFX(this.theWorld, d, d2, d3, d4, d5, d6);
                }
            } else if (str.equals("flame")) {
                if (Config.isAnimatedFlame()) {
                    entityFX = new EntityFlameFX(this.theWorld, d, d2, d3, d4, d5, d6);
                }
            } else if (str.equals("lava")) {
                entityFX = new EntityLavaFX(this.theWorld, d, d2, d3);
            } else if (str.equals("footstep")) {
                entityFX = new EntityFootStepFX(this.renderEngine, this.theWorld, d, d2, d3);
            } else if (str.equals("splash")) {
                entityFX = new EntitySplashFX(this.theWorld, d, d2, d3, d4, d5, d6);
                CustomColorizer.updateWaterFX(entityFX, this.theWorld);
            } else if (str.equals("wake")) {
                entityFX = new EntityFishWakeFX(this.theWorld, d, d2, d3, d4, d5, d6);
            } else if (str.equals("largesmoke")) {
                if (Config.isAnimatedSmoke()) {
                    entityFX = new EntitySmokeFX(this.theWorld, d, d2, d3, d4, d5, d6, 2.5F);
                }
            } else if (str.equals("cloud")) {
                entityFX = new EntityCloudFX(this.theWorld, d, d2, d3, d4, d5, d6);
            } else if (str.equals("reddust")) {
                if (Config.isAnimatedRedstone()) {
                    entityFX = new EntityReddustFX(this.theWorld, d, d2, d3, (float)d4, (float)d5, (float)d6);
                    CustomColorizer.updateReddustFX(entityFX, this.theWorld, d7, d8, d9);
                }
            } else if (str.equals("snowballpoof")) {
                entityFX = new EntityBreakingFX(this.theWorld, d, d2, d3, Items.snowball);
            } else if (str.equals("dripWater")) {
                if (Config.isDrippingWaterLava()) {
                    entityFX = new EntityDropParticleFX(this.theWorld, d, d2, d3, Material.water);
                }
            } else if (str.equals("dripLava")) {
                if (Config.isDrippingWaterLava()) {
                    entityFX = new EntityDropParticleFX(this.theWorld, d, d2, d3, Material.lava);
                }
            } else if (str.equals("snowshovel")) {
                entityFX = new EntitySnowShovelFX(this.theWorld, d, d2, d3, d4, d5, d6);
            } else if (str.equals("slime")) {
                entityFX = new EntityBreakingFX(this.theWorld, d, d2, d3, Items.slime_ball);
            } else if (str.equals("heart")) {
                entityFX = new EntityHeartFX(this.theWorld, d, d2, d3, d4, d5, d6);
            } else if (str.equals("angryVillager")) {
                entityFX = new EntityHeartFX(this.theWorld, d, d2 + 0.5, d3, d4, d5, d6);
                entityFX.setParticleTextureIndex(81);
                entityFX.setRBGColorF(1.0F, 1.0F, 1.0F);
            } else if (str.equals("happyVillager")) {
                entityFX = new EntityAuraFX(this.theWorld, d, d2, d3, d4, d5, d6);
                entityFX.setParticleTextureIndex(82);
                entityFX.setRBGColorF(1.0F, 1.0F, 1.0F);
            } else if (str.startsWith("iconcrack_")) {
                String[] split = str.split("_", 3);
                int parseInt = Integer.parseInt(split[1]);
                if (split.length > 2) {
                    int parseInt2 = Integer.parseInt(split[2]);
                    entityFX = new EntityBreakingFX(this.theWorld, d, d2, d3, d4, d5, d6, Item.getItemById(parseInt), parseInt2);
                } else {
                    entityFX = new EntityBreakingFX(this.theWorld, d, d2, d3, d4, d5, d6, Item.getItemById(parseInt), 0);
                }
            } else if (str.startsWith("blockcrack_")) {
                String[] split = str.split("_", 3);
                Block blockById = Block.getBlockById(Integer.parseInt(split[1]));
                int parseInt = Integer.parseInt(split[2]);
                entityFX = new EntityDiggingFX(this.theWorld, d, d2, d3, d4, d5, d6, blockById, parseInt).applyRenderColor(parseInt);
            } else if (str.startsWith("blockdust_")) {
                String[] split = str.split("_", 3);
                Block blockById = Block.getBlockById(Integer.parseInt(split[1]));
                int parseInt = Integer.parseInt(split[2]);
                entityFX = new EntityBlockDustFX(this.theWorld, d, d2, d3, d4, d5, d6, blockById, parseInt).applyRenderColor(parseInt);
            }

            if (entityFX != null) {
                this.mc.effectRenderer.addEffect(entityFX);
            }

            return entityFX;
        } else {
            return null;
        }
    }

    @Override
    public void onEntityCreate(Entity entity) {
        RandomMobs.entityLoaded(entity, this.theWorld);
        if (Config.isDynamicLights()) {
            DynamicLights.entityAdded(entity, this);
        }
    }

    @Override
    public void onEntityDestroy(Entity entity) {
        if (Config.isDynamicLights()) {
            DynamicLights.entityRemoved(entity, this);
        }
    }

    public void deleteAllDisplayLists() {
        GLAllocation.deleteDisplayLists(this.glRenderListBase);
        this.displayListAllocator.deleteDisplayLists();
    }

    @Override
    public void broadcastSound(int n, int n2, int n3, int n4, int n5) {
        Random rand = this.theWorld.rand;
        switch (n) {
            case 1013:
            case 1018:
                if (this.mc.renderViewEntity != null) {
                    double d = n2 - this.mc.renderViewEntity.posX;
                    double d2 = n3 - this.mc.renderViewEntity.posY;
                    double d3 = n4 - this.mc.renderViewEntity.posZ;
                    double sqrt = Math.sqrt(d * d + d2 * d2 + d3 * d3);
                    double posX = this.mc.renderViewEntity.posX;
                    double posY = this.mc.renderViewEntity.posY;
                    double posZ = this.mc.renderViewEntity.posZ;
                    if (sqrt > 0.0) {
                        posX += d / sqrt * 2.0;
                        posY += d2 / sqrt * 2.0;
                        posZ += d3 / sqrt * 2.0;
                    }

                    if (n == 1013) {
                        this.theWorld.playSound(posX, posY, posZ, "mob.wither.spawn", 1.0F, 1.0F, false);
                    } else if (n == 1018) {
                        this.theWorld.playSound(posX, posY, posZ, "mob.enderdragon.end", 5.0F, 1.0F, false);
                    }
                }
        }
    }

    @Override
    public void playAuxSFX(EntityPlayer entityPlayer, int n, int n2, int n3, int n4, int n5) {
        Random rand = this.theWorld.rand;
        Block block = null;
        switch (n) {
            case 1000:
                this.theWorld.playSound(n2, n3, n4, "random.click", 1.0F, 1.0F, false);
                break;
            case 1001:
                this.theWorld.playSound(n2, n3, n4, "random.click", 1.0F, 1.2F, false);
                break;
            case 1002:
                this.theWorld.playSound(n2, n3, n4, "random.bow", 1.0F, 1.2F, false);
                break;
            case 1003:
                if (Math.random() < 0.5) {
                    this.theWorld.playSound(n2 + 0.5, n3 + 0.5, n4 + 0.5, "random.door_open", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                } else {
                    this.theWorld.playSound(n2 + 0.5, n3 + 0.5, n4 + 0.5, "random.door_close", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                }
                break;
            case 1004:
                this.theWorld.playSound(n2 + 0.5F, n3 + 0.5F, n4 + 0.5F, "random.fizz", 0.5F, 2.6F + (rand.nextFloat() - rand.nextFloat()) * 0.8F, false);
                break;
            case 1005:
                if (Item.getItemById(n5) instanceof ItemRecord) {
                    this.theWorld.playRecord("records." + ((ItemRecord)Item.getItemById(n5)).recordName, n2, n3, n4);
                } else {
                    this.theWorld.playRecord((String)null, n2, n3, n4);
                }
                break;
            case 1007:
                this.theWorld.playSound(n2 + 0.5, n3 + 0.5, n4 + 0.5, "mob.ghast.charge", 10.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1008:
                this.theWorld
                    .playSound(n2 + 0.5, n3 + 0.5, n4 + 0.5, "mob.ghast.fireball", 10.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1009:
                this.theWorld.playSound(n2 + 0.5, n3 + 0.5, n4 + 0.5, "mob.ghast.fireball", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1010:
                this.theWorld.playSound(n2 + 0.5, n3 + 0.5, n4 + 0.5, "mob.zombie.wood", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1011:
                this.theWorld.playSound(n2 + 0.5, n3 + 0.5, n4 + 0.5, "mob.zombie.metal", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1012:
                this.theWorld
                    .playSound(n2 + 0.5, n3 + 0.5, n4 + 0.5, "mob.zombie.woodbreak", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1014:
                this.theWorld.playSound(n2 + 0.5, n3 + 0.5, n4 + 0.5, "mob.wither.shoot", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1015:
                this.theWorld.playSound(n2 + 0.5, n3 + 0.5, n4 + 0.5, "mob.bat.takeoff", 0.05F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1016:
                this.theWorld.playSound(n2 + 0.5, n3 + 0.5, n4 + 0.5, "mob.zombie.infect", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1017:
                this.theWorld.playSound(n2 + 0.5, n3 + 0.5, n4 + 0.5, "mob.zombie.unfect", 2.0F, (rand.nextFloat() - rand.nextFloat()) * 0.2F + 1.0F, false);
                break;
            case 1020:
                this.theWorld.playSound(n2 + 0.5F, n3 + 0.5F, n4 + 0.5F, "random.anvil_break", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 1021:
                this.theWorld.playSound(n2 + 0.5F, n3 + 0.5F, n4 + 0.5F, "random.anvil_use", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 1022:
                this.theWorld.playSound(n2 + 0.5F, n3 + 0.5F, n4 + 0.5F, "random.anvil_land", 0.3F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 2000:
                int n6 = n5 % 3 - 1;
                int n7 = n5 / 3 % 3 - 1;
                double d = n2 + n6 * 0.6 + 0.5;
                double d2 = n3 + 0.5;
                double d3 = n4 + n7 * 0.6 + 0.5;

                for (int i = 0; i < 10; i++) {
                    double d4 = rand.nextDouble() * 0.2 + 0.01;
                    double d5 = d + n6 * 0.01 + (rand.nextDouble() - 0.5) * n7 * 0.5;
                    double d6 = d2 + (rand.nextDouble() - 0.5) * 0.5;
                    double d7 = d3 + n7 * 0.01 + (rand.nextDouble() - 0.5) * n6 * 0.5;
                    double d8 = n6 * d4 + rand.nextGaussian() * 0.01;
                    double d9 = -0.03 + rand.nextGaussian() * 0.01;
                    double d10 = n7 * d4 + rand.nextGaussian() * 0.01;
                    this.spawnParticle("smoke", d5, d6, d7, d8, d9, d10);
                }

                return;
            case 2001:
                block = Block.getBlockById(n5 & 4095);
                if (block.getMaterial() != Material.air) {
                    this.mc
                        .getSoundHandler()
                        .playSound(
                            new PositionedSoundRecord(
                                new ResourceLocation(block.stepSound.getDigResourcePath()),
                                (block.stepSound.getVolume() + 1.0F) / 2.0F,
                                block.stepSound.getFrequency() * 0.8F,
                                n2 + 0.5F,
                                n3 + 0.5F,
                                n4 + 0.5F
                            )
                        );
                }

                this.mc.effectRenderer.addBlockDestroyEffects(n2, n3, n4, block, n5 >> 12 & 0xFF);
                break;
            case 2002:
                double d4 = n2;
                double d5 = n3;
                double d6 = n4;
                String str = "iconcrack_" + Item.getIdFromItem(Items.potionitem) + "_" + n5;

                for (int i = 0; i < 8; i++) {
                    this.spawnParticle(str, d4, d5, d6, rand.nextGaussian() * 0.15, rand.nextDouble() * 0.2, rand.nextGaussian() * 0.15);
                }

                int colorFromDamage = Items.potionitem.getColorFromDamage(n5);
                float f = (colorFromDamage >> 16 & 0xFF) / 255.0F;
                float f2 = (colorFromDamage >> 8 & 0xFF) / 255.0F;
                float f3 = (colorFromDamage >> 0 & 0xFF) / 255.0F;
                String str2 = "spell";
                if (Items.potionitem.isEffectInstant(n5)) {
                    str2 = "instantSpell";
                }

                for (int i = 0; i < 100; i++) {
                    double d7 = rand.nextDouble() * 4.0;
                    double d8 = rand.nextDouble() * Math.PI * 2.0;
                    double d9 = Math.cos(d8) * d7;
                    double d10 = 0.01 + rand.nextDouble() * 0.5;
                    double d11 = Math.sin(d8) * d7;
                    EntityFX doSpawnParticle = this.doSpawnParticle(str2, d4 + d9 * 0.1, d5 + 0.3, d6 + d11 * 0.1, d9, d10, d11);
                    if (doSpawnParticle != null) {
                        float f4 = 0.75F + rand.nextFloat() * 0.25F;
                        doSpawnParticle.setRBGColorF(f * f4, f2 * f4, f3 * f4);
                        doSpawnParticle.multiplyVelocity((float)d7);
                    }
                }

                this.theWorld.playSound(n2 + 0.5, n3 + 0.5, n4 + 0.5, "game.potion.smash", 1.0F, this.theWorld.rand.nextFloat() * 0.1F + 0.9F, false);
                break;
            case 2003:
                double d7 = n2 + 0.5;
                double d8 = n3;
                double d9 = n4 + 0.5;
                String str3 = "iconcrack_" + Item.getIdFromItem(Items.ender_eye);

                for (int i = 0; i < 8; i++) {
                    this.spawnParticle(str3, d7, d8, d9, rand.nextGaussian() * 0.15, rand.nextDouble() * 0.2, rand.nextGaussian() * 0.15);
                }

                for (double d10 = 0.0; d10 < Math.PI * 2; d10 += Math.PI / 20) {
                    this.spawnParticle(
                        "portal", d7 + Math.cos(d10) * 5.0, d8 - 0.4, d9 + Math.sin(d10) * 5.0, Math.cos(d10) * -5.0, 0.0, Math.sin(d10) * -5.0
                    );
                    this.spawnParticle(
                        "portal", d7 + Math.cos(d10) * 5.0, d8 - 0.4, d9 + Math.sin(d10) * 5.0, Math.cos(d10) * -7.0, 0.0, Math.sin(d10) * -7.0
                    );
                }

                return;
            case 2004:
                for (int i = 0; i < 20; i++) {
                    double d10 = n2 + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
                    double d11 = n3 + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
                    double d12 = n4 + 0.5 + (this.theWorld.rand.nextFloat() - 0.5) * 2.0;
                    this.theWorld.spawnParticle("smoke", d10, d11, d12, 0.0, 0.0, 0.0);
                    this.theWorld.spawnParticle("flame", d10, d11, d12, 0.0, 0.0, 0.0);
                }

                return;
            case 2005:
                ItemDye.handleWorld(this.theWorld, n2, n3, n4, n5);
                break;
            case 2006:
                block = this.theWorld.getBlock(n2, n3, n4);
                if (block.getMaterial() != Material.air) {
                    double min = Math.min(0.2F + n5 / 15.0F, 10.0F);
                    if (min > 2.5) {
                        min = 2.5;
                    }

                    int n8 = (int)(150.0 * min);

                    for (int i = 0; i < n8; i++) {
                        float randomFloatClamp = MathHelper.randomFloatClamp(rand, 0.0F, (float) (Math.PI * 2));
                        double randomFloatClamp2 = MathHelper.randomFloatClamp(rand, 0.75F, 1.0F);
                        double d10 = 0.2F + min / 100.0;
                        double d11 = MathHelper.cos(randomFloatClamp) * 0.2F * randomFloatClamp2 * randomFloatClamp2 * (min + 0.2);
                        double d12 = MathHelper.sin(randomFloatClamp) * 0.2F * randomFloatClamp2 * randomFloatClamp2 * (min + 0.2);
                        this.theWorld
                            .spawnParticle(
                                "blockdust_" + Block.getIdFromBlock(block) + "_" + this.theWorld.getBlockMetadata(n2, n3, n4),
                                n2 + 0.5F,
                                n3 + 1.0F,
                                n4 + 0.5F,
                                d11,
                                d10,
                                d12
                            );
                    }
                }
        }
    }

    @Override
    public void destroyBlockPartially(int n, int n2, int n3, int n4, int n5) {
        if (n5 >= 0 && n5 < 10) {
            DestroyBlockProgress destroyBlockProgress = (DestroyBlockProgress)this.damagedBlocks.get(n);
            if (destroyBlockProgress == null || destroyBlockProgress.getPartialBlockX() != n2 || destroyBlockProgress.getPartialBlockY() != n3 || destroyBlockProgress.getPartialBlockZ() != n4) {
                destroyBlockProgress = new DestroyBlockProgress(n, n2, n3, n4);
                this.damagedBlocks.put(n, destroyBlockProgress);
            }

            destroyBlockProgress.setPartialBlockDamage(n5);
            destroyBlockProgress.setCloudUpdateTick(this.cloudTickCounter);
        } else {
            this.damagedBlocks.remove(n);
        }
    }

    public void registerDestroyBlockIcons(IIconRegister iconRegister) {
        this.destroyBlockIcons = new IIcon[10];

        for (int i = 0; i < this.destroyBlockIcons.length; i++) {
            this.destroyBlockIcons[i] = iconRegister.registerIcon("destroy_stage_" + i);
        }
    }

    public void setAllRenderersVisible() {
        if (this.worldRenderers != null) {
            for (int i = 0; i < this.worldRenderers.length; i++) {
                this.worldRenderers[i].isVisible = true;
            }
        }
    }

    public boolean isMoving(EntityLivingBase entityLivingBase) {
        boolean isMovingNow = this.isMovingNow(entityLivingBase);
        if (isMovingNow) {
            this.lastMovedTime = System.currentTimeMillis();
            return true;
        } else {
            return System.currentTimeMillis() - this.lastMovedTime < 2000L;
        }
    }

    public boolean isMovingNow(EntityLivingBase entityLivingBase) {
        double d = 0.001;
        return entityLivingBase.isSneaking()
            || entityLivingBase.prevSwingProgress > d
            || this.mc.mouseHelper.deltaX != 0
            || this.mc.mouseHelper.deltaY != 0
            || Math.abs(entityLivingBase.posX - entityLivingBase.prevPosX) > d
            || Math.abs(entityLivingBase.posY - entityLivingBase.prevPosY) > d
            || Math.abs(entityLivingBase.posZ - entityLivingBase.prevPosZ) > d;
    }

    public boolean isActing() {
        boolean isActingNow = this.isActingNow();
        if (isActingNow) {
            this.lastActionTime = System.currentTimeMillis();
            return true;
        } else {
            return System.currentTimeMillis() - this.lastActionTime < 500L;
        }
    }

    public boolean isActingNow() {
        return Mouse.isButtonDown(0) ? true : Mouse.isButtonDown(1);
    }

    public int renderAllSortedRenderers(int n, double d) {
        return this.renderSortedRenderers(0, this.countSortedWorldRenderers, n, d);
    }

    public void updateCapes() {
    }

    public AxisAlignedBB getTileEntityBoundingBox(TileEntity tileEntity) {
        if (!tileEntity.hasWorldObj()) {
            return AABB_INFINITE;
        }

        Block blockType = tileEntity.getBlockType();
        if (blockType == Blocks.enchanting_table) {
            return AxisAlignedBB.getBoundingBox(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, tileEntity.xCoord + 1, tileEntity.yCoord + 1, tileEntity.zCoord + 1);
        }

        if (blockType != Blocks.chest && blockType != Blocks.trapped_chest) {
            if (Reflector.ForgeTileEntity_getRenderBoundingBox.exists()) {
                AxisAlignedBB axisAlignedBB = (AxisAlignedBB)Reflector.call(tileEntity, Reflector.ForgeTileEntity_getRenderBoundingBox);
                if (axisAlignedBB != null) {
                    return axisAlignedBB;
                }
            }

            if (blockType != null && blockType != Blocks.beacon) {
                AxisAlignedBB collisionBoundingBoxFromPool = blockType.getCollisionBoundingBoxFromPool(tileEntity.getWorld(), tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
                if (collisionBoundingBoxFromPool != null) {
                    return collisionBoundingBoxFromPool;
                }
            }

            return AABB_INFINITE;
        } else {
            return AxisAlignedBB.getBoundingBox(tileEntity.xCoord - 1, tileEntity.yCoord, tileEntity.zCoord - 1, tileEntity.xCoord + 2, tileEntity.yCoord + 2, tileEntity.zCoord + 2);
        }
    }

    public void addToSortedWorldRenderers(WorldRenderer worldRenderer) {
        if (!worldRenderer.inSortedWorldRenderers) {
            int countSortedWorldRenderers = this.countSortedWorldRenderers;
            worldRenderer.updateDistanceToEntitySquared(this.renderViewEntity);
            float sortDistanceToEntitySquared = worldRenderer.sortDistanceToEntitySquared;
            if (this.countSortedWorldRenderers > 0) {
                int n = 0;
                int n2 = this.countSortedWorldRenderers - 1;
                int n3 = (n + n2) / 2;

                while (n <= n2) {
                    n3 = (n + n2) / 2;
                    WorldRenderer worldRenderer2 = this.sortedWorldRenderers[n3];
                    if (sortDistanceToEntitySquared < worldRenderer2.sortDistanceToEntitySquared) {
                        n2 = n3 - 1;
                    } else {
                        n = n3 + 1;
                    }
                }

                if (n > n3) {
                    countSortedWorldRenderers = n3 + 1;
                } else {
                    countSortedWorldRenderers = n3;
                }
            }

            int n = this.countSortedWorldRenderers - countSortedWorldRenderers;
            if (n > 0) {
                System.arraycopy(this.sortedWorldRenderers, countSortedWorldRenderers, this.sortedWorldRenderers, countSortedWorldRenderers + 1, n);
            }

            this.sortedWorldRenderers[countSortedWorldRenderers] = worldRenderer;
            worldRenderer.inSortedWorldRenderers = true;
            this.countSortedWorldRenderers++;
        }
    }

    public int getCountRenderers() {
        return this.renderersLoaded;
    }

    public int getCountActiveRenderers() {
        return this.renderersBeingRendered;
    }

    public int getCountEntitiesRendered() {
        return this.countEntitiesRendered;
    }

    public int getCountTileEntitiesRendered() {
        return this.countTileEntitiesRendered;
    }

    public static void drawBox(AxisAlignedBB axisAlignedBB, int n) {
        Tessellator instance = Tessellator.instance;
        instance.startDrawing(7);
        if (n != -1) {
            instance.setColorOpaque_I(n);
        }

        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.draw();
        instance.startDrawing(7);
        if (n != -1) {
            instance.setColorOpaque_I(n);
        }

        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.draw();
        instance.startDrawing(7);
        if (n != -1) {
            instance.setColorOpaque_I(n);
        }

        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.draw();
        instance.startDrawing(7);
        if (n != -1) {
            instance.setColorOpaque_I(n);
        }

        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.draw();
        instance.startDrawing(7);
        if (n != -1) {
            instance.setColorOpaque_I(n);
        }

        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.draw();
        instance.startDrawing(7);
        if (n != -1) {
            instance.setColorOpaque_I(n);
        }

        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.minX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.minZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.maxY, axisAlignedBB.maxZ);
        instance.addVertex(axisAlignedBB.maxX, axisAlignedBB.minY, axisAlignedBB.maxZ);
        instance.draw();
    }
}
