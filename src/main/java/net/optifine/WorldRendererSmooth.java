package net.optifine;

import java.util.HashSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
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

public class WorldRendererSmooth extends WorldRenderer {
    public WrUpdateState updateState = new WrUpdateState();
    public int activeSet = 0;
    public int[] activeListIndex = new int[]{0, 0};
    public int[][][] glWorkLists = new int[2][2][16];
    public boolean[] tempSkipRenderPass = new boolean[2];
    public TesselatorVertexState tempVertexState;

    public WorldRendererSmooth(World world, List list, int n, int n2, int n3, int n4) {
        super(world, list, n, n2, n3, n4);
    }

    public void checkGlWorkLists() {
        if (this.glWorkLists[0][0][0] <= 0) {
            int allocateDisplayLists = this.renderGlobal.displayListAllocator.allocateDisplayLists(64);

            for (int i = 0; i < 2; i++) {
                int n = allocateDisplayLists + i * 2 * 16;

                for (int j = 0; j < 2; j++) {
                    int n2 = n + j * 16;

                    for (int k = 0; k < 16; k++) {
                        this.glWorkLists[i][j][k] = n2 + k;
                    }
                }
            }
        }
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
            this.updateRenderer(0L);
            this.finishUpdate();
        }
    }

    public boolean updateRenderer(long l) {
        if (this.worldObj == null) {
            return true;
        }

        this.needsUpdate = false;
        if (!this.isUpdating) {
            if (this.needsBoxUpdate) {
                GL11.glNewList(this.glRenderList + 2, 4864);
                RenderItem.renderAABB(
                    AxisAlignedBB.getBoundingBox(this.posXClip, this.posYClip, this.posZClip, this.posXClip + 16, this.posYClip + 16, this.posZClip + 16)
                );
                GL11.glEndList();
                this.needsBoxUpdate = false;
            }

            if (Reflector.LightCache.exists()) {
                Object fieldValue = Reflector.getFieldValue(Reflector.LightCache_cache);
                Reflector.callVoid(fieldValue, Reflector.LightCache_clear);
                Reflector.callVoid(Reflector.BlockCoord_resetPool);
            }

            Chunk.isLit = false;
        }

        int posX = this.posX;
        int posY = this.posY;
        int posZ = this.posZ;
        int n = this.posX + 16;
        int n2 = this.posY + 16;
        int n3 = this.posZ + 16;
        ChunkCacheOF chunkCacheOF = null;
        RenderBlocks renderBlocks = null;
        HashSet set = null;
        int n4 = 0;
        int n5 = 0;
        int n6 = 0;
        if (!this.isUpdating) {
            for (int i = 0; i < 2; i++) {
                this.tempSkipRenderPass[i] = true;
            }

            this.tempVertexState = null;
            Minecraft minecraft = Minecraft.getMinecraft();
            EntityLivingBase renderViewEntity = minecraft.renderViewEntity;
            n4 = MathHelper.floor_double(renderViewEntity.posX);
            n5 = MathHelper.floor_double(renderViewEntity.posY);
            n6 = MathHelper.floor_double(renderViewEntity.posZ);
            byte b = 1;
            chunkCacheOF = new ChunkCacheOF(this.worldObj, posX - b, posY - b, posZ - b, n + b, n2 + b, n3 + b, b);
            renderBlocks = new RenderBlocks(chunkCacheOF);
            Reflector.callVoid(Reflector.ForgeHooksClient_setWorldRendererRB, renderBlocks);
            set = new HashSet();
            set.addAll(this.tileEntityRenderers);
            this.tileEntityRenderers.clear();
        }

        if (this.isUpdating || !chunkCacheOF.extendedLevelsInChunkCache()) {
            this.bytesDrawn = 0;
            if (!this.isUpdating) {
                chunkCacheOF.renderStart();
            }

            this.tessellator = Tessellator.instance;
            boolean exists = Reflector.ForgeHooksClient.exists();
            this.checkGlWorkLists();

            for (int i = 0; i < 2; i++) {
                boolean flag = false;
                boolean flag2 = false;
                boolean flag3 = false;

                for (int j = posY; j < n2; j++) {
                    if (this.isUpdating) {
                        this.isUpdating = false;
                        chunkCacheOF = this.updateState.chunkcache;
                        renderBlocks = this.updateState.renderblocks;
                        Reflector.callVoid(Reflector.ForgeHooksClient_setWorldRendererRB, renderBlocks);
                        set = this.updateState.setOldEntityRenders;
                        n4 = this.updateState.viewEntityPosX;
                        n5 = this.updateState.viewEntityPosY;
                        n6 = this.updateState.viewEntityPosZ;
                        i = this.updateState.renderPass;
                        j = this.updateState.intValue;
                        flag = this.updateState.flag;
                        flag2 = this.updateState.hasRenderedBlocks;
                        flag3 = this.updateState.hasGlList;
                        if (flag3) {
                            this.preRenderBlocksSmooth(i);
                        }
                    } else if (flag3 && l != 0L && System.nanoTime() - l > 0L && this.activeListIndex[i] < 15) {
                        if (flag2) {
                            this.tempSkipRenderPass[i] = false;
                        }

                        this.postRenderBlocksSmooth(i, this.renderGlobal.renderViewEntity, false);
                        this.activeListIndex[i]++;
                        this.updateState.chunkcache = chunkCacheOF;
                        this.updateState.renderblocks = renderBlocks;
                        this.updateState.setOldEntityRenders = set;
                        this.updateState.viewEntityPosX = n4;
                        this.updateState.viewEntityPosY = n5;
                        this.updateState.viewEntityPosZ = n6;
                        this.updateState.renderPass = i;
                        this.updateState.intValue = j;
                        this.updateState.flag = flag;
                        this.updateState.hasRenderedBlocks = flag2;
                        this.updateState.hasGlList = flag3;
                        this.isUpdating = true;
                        return false;
                    }

                    for (int k = posZ; k < n3; k++) {
                        for (int m = posX; m < n; m++) {
                            Block block = chunkCacheOF.getBlock(m, j, k);
                            if (block.getMaterial() != Material.air) {
                                if (!flag3) {
                                    flag3 = true;
                                    this.preRenderBlocksSmooth(i);
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
                    this.postRenderBlocksSmooth(i, this.renderGlobal.renderViewEntity, true);
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
        chunksUpdated++;
        this.isVisible = true;
        this.isVisibleFromPosition = false;
        this.skipRenderPass[0] = this.tempSkipRenderPass[0];
        this.skipRenderPass[1] = this.tempSkipRenderPass[1];
        this.skipAllRenderPasses = this.skipRenderPass[0] && this.skipRenderPass[1];
        this.vertexState = this.tempVertexState;
        this.isUpdating = false;
        this.updateFinished();
        return true;
    }

    public void preRenderBlocksSmooth(int n) {
        GL11.glNewList(this.glWorkLists[this.activeSet][n][this.activeListIndex[n]], 4864);
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

    public void postRenderBlocksSmooth(int n, EntityLivingBase entityLivingBase, boolean flag) {
        if (Config.isTranslucentBlocksFancy() && n == 1 && !this.tempSkipRenderPass[n]) {
            TesselatorVertexState vertexState = this.tessellator.getVertexState((float)entityLivingBase.posX, (float)entityLivingBase.posY, (float)entityLivingBase.posZ);
            if (this.tempVertexState == null) {
                this.tempVertexState = vertexState;
            } else {
                this.tempVertexState.addTessellatorVertexState(vertexState);
            }
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
        for (int i = 0; i < 2; i++) {
            if (!this.skipRenderPass[i]) {
                GL11.glNewList(this.glRenderList + i, 4864);

                for (int j = 0; j <= this.activeListIndex[i]; j++) {
                    int n = this.glWorkLists[this.activeSet][i][j];
                    GL11.glCallList(n);
                }

                GL11.glEndList();
            }
        }

        if (this.activeSet == 0) {
            this.activeSet = 1;
        } else {
            this.activeSet = 0;
        }

        for (int i = 0; i < 2; i++) {
            if (!this.skipRenderPass[i]) {
                for (int j = 0; j <= this.activeListIndex[i]; j++) {
                    int n = this.glWorkLists[this.activeSet][i][j];
                    GL11.glNewList(n, 4864);
                    GL11.glEndList();
                }
            }
        }

        for (int i = 0; i < 2; i++) {
            this.activeListIndex[i] = 0;
        }
    }
}
