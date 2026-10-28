package net.optifine;

import java.util.HashSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.shader.TesselatorVertexState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.lwjgl.opengl.GL11;

public class WorldRendererThreaded extends WorldRenderer {
    public boolean[] tempSkipRenderPass = new boolean[2];
    public Tessellator tessellatorWork = null;
    public int glRenderListWork;
    public TesselatorVertexState tempVertexState;
    public int glRenderListBoundingBox;

    public WorldRendererThreaded(World world, List list, int n, int n2, int n3, int n4) {
        super(world, list, n, n2, n3, n4);
        RenderGlobal renderGlobal = Minecraft.getMinecraft().renderGlobal;
        this.glRenderListWork = renderGlobal.displayListAllocator.allocateDisplayLists(2);
        this.glRenderListBoundingBox = this.glRenderList + 2;
    }

    @Override
    public void setPosition(int n, int n2, int n3) {
        if (this.isUpdating) {
            WrUpdates.finishCurrentUpdate();
        }

        super.setPosition(n, n2, n3);
    }

    public void updateRenderer() {
        if (this.worldObj != null) {
            this.updateRenderer((IWrUpdateListener)null);
            this.finishUpdate();
        }
    }

    public void updateRenderer(IWrUpdateListener wrUpdateListener) {
        if (this.worldObj != null) {
            this.needsUpdate = false;
            int posX = this.posX;
            int posY = this.posY;
            int posZ = this.posZ;
            int n = this.posX + 16;
            int n2 = this.posY + 16;
            int n3 = this.posZ + 16;

            for (int i = 0; i < this.tempSkipRenderPass.length; i++) {
                this.tempSkipRenderPass[i] = true;
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
                this.tempVertexState = null;
                this.tessellator = Tessellator.instance;
                boolean exists = Reflector.ForgeHooksClient.exists();
                WrUpdateControl wrUpdateControl = new WrUpdateControl();

                for (int i = 0; i < 2; i++) {
                    wrUpdateControl.setRenderPass(i);
                    boolean flag = false;
                    boolean flag2 = false;
                    boolean flag3 = false;

                    for (int j = posY; j < n2; j++) {
                        if (flag2 && wrUpdateListener != null) {
                            wrUpdateListener.updating(wrUpdateControl);
                            this.tessellator = Tessellator.instance;
                        }

                        for (int k = posZ; k < n3; k++) {
                            for (int m = posX; m < n; m++) {
                                Block block = chunkCacheOF.getBlock(m, j, k);
                                if (block.getMaterial() != Material.air) {
                                    if (!flag3) {
                                        flag3 = true;
                                        this.preRenderBlocksThreaded(i);
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
                        this.tempSkipRenderPass[i] = false;
                    }

                    if (flag3) {
                        if (wrUpdateListener != null) {
                            wrUpdateListener.updating(wrUpdateControl);
                        }

                        this.tessellator = Tessellator.instance;
                        this.postRenderBlocksThreaded(i, this.renderGlobal.renderViewEntity);
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
        }
    }

    public void preRenderBlocksThreaded(int n) {
        GL11.glNewList(this.glRenderListWork + n, 4864);
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

    public void postRenderBlocksThreaded(int n, EntityLivingBase entityLivingBase) {
        if (Config.isTranslucentBlocksFancy() && n == 1 && !this.tempSkipRenderPass[n]) {
            this.tempVertexState = this.tessellator.getVertexState((float)entityLivingBase.posX, (float)entityLivingBase.posY, (float)entityLivingBase.posZ);
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

    public void finishUpdate() {
        int glRenderList = this.glRenderList;
        this.glRenderList = this.glRenderListWork;
        this.glRenderListWork = glRenderList;

        for (int i = 0; i < 2; i++) {
            if (!this.skipRenderPass[i]) {
                GL11.glNewList(this.glRenderListWork + i, 4864);
                GL11.glEndList();
            }
        }

        for (int i = 0; i < 2; i++) {
            this.skipRenderPass[i] = this.tempSkipRenderPass[i];
        }

        this.skipAllRenderPasses = this.skipRenderPass[0] && this.skipRenderPass[1];
        if (this.needsBoxUpdate && !this.skipAllRenderPasses()) {
            GL11.glNewList(this.glRenderListBoundingBox, 4864);
            RenderItem.renderAABB(
                AxisAlignedBB.getBoundingBox(this.posXClip, this.posYClip, this.posZClip, this.posXClip + 16, this.posYClip + 16, this.posZClip + 16)
            );
            GL11.glEndList();
            this.needsBoxUpdate = false;
        }

        this.vertexState = this.tempVertexState;
        this.isVisible = true;
        this.isVisibleFromPosition = false;
        if (Reflector.LightCache.exists()) {
            Object fieldValue = Reflector.getFieldValue(Reflector.LightCache_cache);
            Reflector.callVoid(fieldValue, Reflector.LightCache_clear);
            Reflector.callVoid(Reflector.BlockCoord_resetPool);
        }

        this.updateFinished();
    }

    @Override
    public void callOcclusionQueryList() {
        GL11.glCallList(this.glRenderListBoundingBox);
    }
}
