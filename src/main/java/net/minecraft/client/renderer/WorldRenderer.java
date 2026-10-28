package net.minecraft.client.renderer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.optifine.ChunkCacheOF;
import net.optifine.Config;
import net.optifine.Reflector;
import org.lwjgl.opengl.GL11;

public class WorldRenderer {
    public Tessellator tessellator = Tessellator.instance;
    public boolean[] skipRenderPass = new boolean[]{true, true};
    public List tileEntityRenderers = new ArrayList();
    public boolean isVisibleFromPosition = false;
    public static volatile int chunksUpdated;
    public boolean isInFrustrumFully = false;
    public int posY;
    public int posZ;
    public int posXMinus;
    public int posYMinus;
    public int posZMinus;
    public int posXClip;
    public int posYClip;
    public int posZClip;
    public boolean needsBoxUpdate = false;
    public volatile boolean isUpdating = false;
    public int posXPlus;
    public int posYPlus;
    public int posZPlus;
    public boolean skipAllRenderPasses = true;
    public AxisAlignedBB rendererBoundingBox;
    public int chunkIndex;
    public RenderGlobal renderGlobal;
    public boolean isWaitingOnOcclusionQuery;
    public int glOcclusionQuery;
    public boolean isChunkLit;
    public int glRenderList;
    public boolean isInFrustum;
    public boolean isVisible;
    public int bytesDrawn;
    public boolean isInitialized;
    public double visibleFromX;
    public double visibleFromY;
    public double visibleFromZ;
    public World worldObj;
    public TesselatorVertexState vertexState;
    public List tileEntities;
    public float sortDistanceToEntitySquared;
    public double distanceToEntityXzSq;
    public int posX;
    public boolean inSortedWorldRenderers;
    public boolean needVertexStateResort;
    public volatile boolean needsUpdate;
    public static int globalChunkOffsetX = 0;
    public static int globalChunkOffsetZ = 0;

    public WorldRenderer(World world, List list, int n, int n2, int n3, int n4) {
        this.renderGlobal = Minecraft.getMinecraft().renderGlobal;
        this.glRenderList = -1;
        this.isInFrustum = false;
        this.isVisible = true;
        this.isInitialized = false;
        this.worldObj = world;
        this.vertexState = null;
        this.tileEntities = list;
        this.glRenderList = n4;
        this.posX = -999;
        this.setPosition(n, n2, n3);
        this.needsUpdate = false;
    }

    public void setPosition(int n, int n2, int n3) {
        if (n != this.posX || n2 != this.posY || n3 != this.posZ) {
            this.setDontDraw();
            this.posX = n;
            this.posY = n2;
            this.posZ = n3;
            this.posXPlus = n + 8;
            this.posYPlus = n2 + 8;
            this.posZPlus = n3 + 8;
            this.posXClip = n & 1023;
            this.posYClip = n2;
            this.posZClip = n3 & 1023;
            this.posXMinus = n - this.posXClip;
            this.posYMinus = n2 - this.posYClip;
            this.posZMinus = n3 - this.posZClip;
            this.rendererBoundingBox = AxisAlignedBB.getBoundingBox(n, n2, n3, n + 16, n2 + 16, n3 + 16);
            this.needsBoxUpdate = true;
            this.markDirty();
            this.isVisibleFromPosition = false;
        }
    }

    public void setupGLTranslation() {
        GL11.glTranslatef(this.posXClip, this.posYClip, this.posZClip);
    }

    public void updateRenderer(EntityLivingBase entityLivingBase) {
        if (this.worldObj != null) {
            if (this.needsBoxUpdate) {
                GL11.glNewList(this.glRenderList + 2, 4864);
                RenderItem.renderAABB(
                    AxisAlignedBB.getBoundingBox(this.posXClip, this.posYClip, this.posZClip, this.posXClip + 16, this.posYClip + 16, this.posZClip + 16)
                );
                GL11.glEndList();
                this.needsBoxUpdate = false;
            }

            this.isVisible = true;
            this.isVisibleFromPosition = false;
            if (this.needsUpdate) {
                this.needsUpdate = false;
                int posX = this.posX;
                int posY = this.posY;
                int posZ = this.posZ;
                int n = this.posX + 16;
                int n2 = this.posY + 16;
                int n3 = this.posZ + 16;

                for (int i = 0; i < 2; i++) {
                    this.skipRenderPass[i] = true;
                }

                this.skipAllRenderPasses = true;
                if (Reflector.LightCache.exists()) {
                    Object fieldValue = Reflector.getFieldValue(Reflector.LightCache_cache);
                    Reflector.callVoid(fieldValue, Reflector.LightCache_clear);
                    Reflector.callVoid(Reflector.BlockCoord_resetPool);
                }

                Chunk.isLit = false;
                HashSet set = new HashSet();
                set.addAll(this.tileEntityRenderers);
                this.tileEntityRenderers.clear();
                Minecraft minecraft = Minecraft.getMinecraft();
                EntityLivingBase renderViewEntity = minecraft.renderViewEntity;
                int n4 = MathHelper.floor_double(renderViewEntity.posX);
                int n5 = MathHelper.floor_double(renderViewEntity.posY);
                int n6 = MathHelper.floor_double(renderViewEntity.posZ);
                byte b = 1;
                ChunkCacheOF chunkCacheOF = new ChunkCacheOF(this.worldObj, posX - b, posY - b, posZ - b, n + b, n2 + b, n3 + b, b);
                if (!chunkCacheOF.extendedLevelsInChunkCache()) {
                    chunksUpdated++;
                    chunkCacheOF.renderStart();
                    RenderBlocks renderBlocks = new RenderBlocks(chunkCacheOF);
                    Reflector.callVoid(Reflector.ForgeHooksClient_setWorldRendererRB, renderBlocks);
                    this.bytesDrawn = 0;
                    this.vertexState = null;
                    this.tessellator = Tessellator.instance;
                    boolean exists = Reflector.ForgeHooksClient.exists();

                    for (int i = 0; i < 2; i++) {
                        boolean flag = false;
                        boolean flag2 = false;
                        boolean flag3 = false;

                        for (int j = posY; j < n2; j++) {
                            for (int k = posZ; k < n3; k++) {
                                for (int m = posX; m < n; m++) {
                                    Block block = chunkCacheOF.getBlock(m, j, k);
                                    if (block.getMaterial() != Material.air) {
                                        if (!flag3) {
                                            flag3 = true;
                                            this.preRenderBlocks(i);
                                        }

                                        boolean flag4 = false;
                                        if (exists) {
                                            flag4 = Reflector.callBoolean(block, Reflector.ForgeBlock_hasTileEntity, chunkCacheOF.getBlockMetadata(m, j, k));
                                        } else {
                                            flag4 = block.hasTileEntity();
                                        }

                                        if (i == 0 && flag4) {
                                            TileEntity tileEntity = chunkCacheOF.getTileEntity(m, j, k);
                                            if (TileEntityRendererDispatcher.instance.hasSpecialRenderer(tileEntity)) {
                                                this.tileEntityRenderers.add(tileEntity);
                                            }
                                        }

                                        int renderBlockPass = block.getRenderBlockPass();
                                        if (renderBlockPass > i) {
                                            flag = true;
                                        }

                                        boolean flag5 = renderBlockPass == i;
                                        if (Reflector.ForgeBlock_canRenderInPass.exists()) {
                                            flag5 = Reflector.callBoolean(block, Reflector.ForgeBlock_canRenderInPass, i);
                                        }

                                        if (flag5) {
                                            flag2 |= renderBlocks.renderBlockByRenderType(block, m, j, k);
                                            if (block.getRenderType() == 0 && m == n4 && j == n5 && k == n6) {
                                                renderBlocks.setRenderFromInside(true);
                                                renderBlocks.setRenderAllFaces(true);
                                                renderBlocks.renderBlockByRenderType(block, m, j, k);
                                                renderBlocks.setRenderFromInside(false);
                                                renderBlocks.setRenderAllFaces(false);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (flag2) {
                            this.skipRenderPass[i] = false;
                        }

                        if (flag3) {
                            this.postRenderBlocks(i, entityLivingBase);
                        } else {
                            flag2 = false;
                        }

                        if (!flag) {
                            break;
                        }
                    }

                    Reflector.callVoid(Reflector.ForgeHooksClient_setWorldRendererRB, (RenderBlocks)null);
                    chunkCacheOF.renderFinish();
                }

                HashSet set2 = new HashSet();
                set2.addAll(this.tileEntityRenderers);
                set2.removeAll(set);
                this.tileEntities.addAll(set2);
                set.removeAll(this.tileEntityRenderers);
                this.tileEntities.removeAll(set);
                this.isChunkLit = Chunk.isLit;
                this.isInitialized = true;
                this.skipAllRenderPasses = this.skipRenderPass[0] && this.skipRenderPass[1];
                this.updateFinished();
            }
        }
    }

    public void preRenderBlocks(int n) {
        GL11.glNewList(this.glRenderList + n, 4864);
        this.tessellator.setRenderingChunk(true);
        if (Config.isFastRender()) {
            Reflector.callVoid(Reflector.ForgeHooksClient_onPreRenderWorld, this, n);
            this.tessellator.startDrawingQuads();
            this.tessellator.setTranslation(-globalChunkOffsetX, 0.0, -globalChunkOffsetZ);
        } else {
            GL11.glPushMatrix();
            this.setupGLTranslation();
            float f = 1.000001F;
            GL11.glTranslatef(-8.0F, -8.0F, -8.0F);
            GL11.glScalef(f, f, f);
            GL11.glTranslatef(8.0F, 8.0F, 8.0F);
            Reflector.callVoid(Reflector.ForgeHooksClient_onPreRenderWorld, this, n);
            this.tessellator.startDrawingQuads();
            this.tessellator.setTranslation(-this.posX, -this.posY, -this.posZ);
        }
    }

    public void postRenderBlocks(int n, EntityLivingBase entityLivingBase) {
        if (Config.isTranslucentBlocksFancy() && n == 1 && !this.skipRenderPass[n]) {
            this.vertexState = this.tessellator.getVertexState((float)entityLivingBase.posX, (float)entityLivingBase.posY, (float)entityLivingBase.posZ);
        }

        this.bytesDrawn = this.bytesDrawn + this.tessellator.draw();
        Reflector.callVoid(Reflector.ForgeHooksClient_onPostRenderWorld, this, n);
        this.tessellator.setRenderingChunk(false);
        if (!Config.isFastRender()) {
            GL11.glPopMatrix();
        }

        GL11.glEndList();
        this.tessellator.setTranslation(0.0, 0.0, 0.0);
    }

    public void updateRendererSort(EntityLivingBase entityLivingBase) {
        if (this.vertexState != null && !this.skipRenderPass[1]) {
            this.tessellator = Tessellator.instance;
            this.preRenderBlocks(1);
            this.tessellator.setVertexState(this.vertexState);
            this.postRenderBlocks(1, entityLivingBase);
        }
    }

    public float distanceToEntitySquared(Entity entity) {
        float f = (float)(entity.posX - this.posXPlus);
        float f2 = (float)(entity.posY - this.posYPlus);
        float f3 = (float)(entity.posZ - this.posZPlus);
        return f * f + f2 * f2 + f3 * f3;
    }

    public void setDontDraw() {
        for (int i = 0; i < 2; i++) {
            this.skipRenderPass[i] = true;
        }

        this.skipAllRenderPasses = true;
        this.isInFrustum = false;
        this.isInitialized = false;
        this.vertexState = null;
    }

    public void stopRendering() {
        this.setDontDraw();
        this.worldObj = null;
    }

    public int getGLCallListForPass(int n) {
        return this.glRenderList + n;
    }

    public void updateInFrustum(ICamera camera) {
        this.isInFrustum = camera.isBoundingBoxInFrustum(this.rendererBoundingBox);
        if (this.isInFrustum && Config.isOcclusionFancy()) {
            this.isInFrustrumFully = camera.isBoundingBoxInFrustumFully(this.rendererBoundingBox);
        } else {
            this.isInFrustrumFully = false;
        }
    }

    public void callOcclusionQueryList() {
        GL11.glCallList(this.glRenderList + 2);
    }

    public boolean skipAllRenderPasses() {
        return this.skipAllRenderPasses;
    }

    public void markDirty() {
        this.needsUpdate = true;
    }

    public void updateFinished() {
        if (!this.skipAllRenderPasses && !this.inSortedWorldRenderers) {
            Minecraft.getMinecraft().renderGlobal.addToSortedWorldRenderers(this);
        }
    }

    public void updateDistanceToEntitySquared(Entity entity) {
        double d = entity.posX - this.posXPlus;
        double d2 = entity.posY - this.posYPlus;
        double d3 = entity.posZ - this.posZPlus;
        double d4 = d * d + d3 * d3;
        this.distanceToEntityXzSq = d4;
        this.sortDistanceToEntitySquared = (float)(d4 + d2 * d2);
    }
}
