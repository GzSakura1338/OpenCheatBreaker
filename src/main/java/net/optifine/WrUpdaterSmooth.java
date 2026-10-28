package net.optifine;

import java.util.List;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class WrUpdaterSmooth implements IWrUpdater {
    public long lastUpdateStartTimeNs = 0L;
    public long updateStartTimeNs = 0L;
    public long updateTimeNs = 10000000L;
    public WorldRendererSmooth currentUpdateRenderer = null;
    public int renderersUpdated = 0;
    public int renderersFound = 0;

    @Override
    public void initialize() {
    }

    @Override
    public void terminate() {
    }

    @Override
    public WorldRenderer makeWorldRenderer(World world, List list, int n, int n2, int n3, int n4) {
        return new WorldRendererSmooth(world, list, n, n2, n3, n4);
    }

    @Override
    public boolean updateRenderers(RenderGlobal renderGlobal, EntityLivingBase entityLivingBase, boolean flag) {
        this.lastUpdateStartTimeNs = this.updateStartTimeNs;
        this.updateStartTimeNs = System.nanoTime();
        long l = this.updateStartTimeNs + this.updateTimeNs;
        int updatesPerFrame = Config.getUpdatesPerFrame();
        if (Config.isDynamicUpdates() && !renderGlobal.isMoving(entityLivingBase)) {
            updatesPerFrame *= 3;
        }

        this.renderersUpdated = 0;

        do {
            this.renderersFound = 0;
            this.updateRenderersImpl(renderGlobal, entityLivingBase, flag);
        } while (this.renderersFound > 0 && System.nanoTime() - l < 0L);

        if (this.renderersFound > 0) {
            updatesPerFrame = Math.min(updatesPerFrame, this.renderersFound);
            long l2 = 400000L;
            if (this.renderersUpdated > updatesPerFrame) {
                this.updateTimeNs -= 2L * l2;
            }

            if (this.renderersUpdated < updatesPerFrame) {
                this.updateTimeNs += l2;
            }
        } else {
            this.updateTimeNs = 0L;
            this.updateTimeNs -= 200000L;
        }

        if (this.updateTimeNs < 0L) {
            this.updateTimeNs = 0L;
        }

        return this.renderersUpdated > 0;
    }

    public void updateRenderersImpl(RenderGlobal renderGlobal, EntityLivingBase entityLivingBase, boolean flag) {
        this.renderersFound = 0;
        boolean flag2 = true;
        if (this.currentUpdateRenderer != null) {
            this.renderersFound++;
            flag2 = this.updateRenderer(this.currentUpdateRenderer);
            if (flag2) {
                this.renderersUpdated++;
            }
        }

        if (renderGlobal.compactArrayList.size() > 0) {
            byte b = 4;
            WorldRendererSmooth worldRendererSmooth = null;
            float f = Float.MAX_VALUE;
            int n = -1;

            for (int i = 0; i < renderGlobal.compactArrayList.size(); i++) {
                WorldRendererSmooth worldRendererSmooth2 = (WorldRendererSmooth)renderGlobal.compactArrayList.get(i);
                if (worldRendererSmooth2 != null) {
                    this.renderersFound++;
                    if (!worldRendererSmooth2.needsUpdate) {
                        renderGlobal.compactArrayList.set(i, null);
                    } else {
                        float distanceToEntitySquared = worldRendererSmooth2.distanceToEntitySquared(entityLivingBase);
                        if (distanceToEntitySquared <= 256.0F && renderGlobal.isActingNow()) {
                            worldRendererSmooth2.updateRenderer();
                            worldRendererSmooth2.needsUpdate = false;
                            renderGlobal.compactArrayList.set(i, null);
                            this.renderersUpdated++;
                        } else {
                            if (!worldRendererSmooth2.isInFrustum) {
                                distanceToEntitySquared *= b;
                            }

                            if (worldRendererSmooth == null) {
                                worldRendererSmooth = worldRendererSmooth2;
                                f = distanceToEntitySquared;
                                n = i;
                            } else if (distanceToEntitySquared < f) {
                                worldRendererSmooth = worldRendererSmooth2;
                                f = distanceToEntitySquared;
                                n = i;
                            }
                        }
                    }
                }
            }

            if (this.currentUpdateRenderer == null || flag2) {
                if (worldRendererSmooth != null) {
                    renderGlobal.compactArrayList.set(n, null);
                    if (!this.updateRenderer(worldRendererSmooth)) {
                        return;
                    }

                    this.renderersUpdated++;
                    if (System.nanoTime() > this.updateStartTimeNs + this.updateTimeNs) {
                        return;
                    }

                    float f2 = f / 5.0F;

                    for (int i = 0; i < renderGlobal.compactArrayList.size(); i++) {
                        WorldRendererSmooth worldRendererSmooth2 = (WorldRendererSmooth)renderGlobal.compactArrayList.get(i);
                        if (worldRendererSmooth2 != null) {
                            float distanceToEntitySquared = worldRendererSmooth2.distanceToEntitySquared(entityLivingBase);
                            if (!worldRendererSmooth2.isInFrustum) {
                                distanceToEntitySquared *= b;
                            }

                            float abs = Math.abs(distanceToEntitySquared - f);
                            if (abs < f2) {
                                renderGlobal.compactArrayList.set(i, null);
                                if (!this.updateRenderer(worldRendererSmooth2)) {
                                    return;
                                }

                                this.renderersUpdated++;
                                if (System.nanoTime() > this.updateStartTimeNs + this.updateTimeNs) {
                                    break;
                                }
                            }
                        }
                    }
                }

                if (this.renderersFound == 0) {
                    renderGlobal.compactArrayList.clear();
                }

                if (renderGlobal.compactArrayList.size() > 100 && this.renderersFound < renderGlobal.compactArrayList.size() * 4 / 5) {
                    int n2 = 0;

                    for (int i = 0; i < renderGlobal.compactArrayList.size(); i++) {
                        Object obj = renderGlobal.compactArrayList.get(i);
                        if (obj != null) {
                            if (i != n2) {
                                renderGlobal.compactArrayList.set(n2, obj);
                            }

                            n2++;
                        }
                    }

                    for (int i = renderGlobal.compactArrayList.size() - 1; i >= n2; i--) {
                        renderGlobal.compactArrayList.remove(i);
                    }
                }
            }
        }
    }

    public boolean updateRenderer(WorldRendererSmooth worldRendererSmooth) {
        long l = this.updateStartTimeNs + this.updateTimeNs;
        worldRendererSmooth.needsUpdate = false;
        boolean updateRenderer = worldRendererSmooth.updateRenderer(l);
        if (!updateRenderer) {
            this.currentUpdateRenderer = worldRendererSmooth;
            return false;
        } else {
            worldRendererSmooth.finishUpdate();
            this.currentUpdateRenderer = null;
            return true;
        }
    }

    @Override
    public void finishCurrentUpdate() {
        if (this.currentUpdateRenderer != null) {
            this.currentUpdateRenderer.updateRenderer();
            this.currentUpdateRenderer = null;
        }
    }

    @Override
    public void resumeBackgroundUpdates() {
    }

    @Override
    public void pauseBackgroundUpdates() {
    }

    @Override
    public void preRender(RenderGlobal renderGlobal, EntityLivingBase entityLivingBase) {
    }

    @Override
    public void postRender() {
    }

    @Override
    public void clearAllUpdates() {
        this.finishCurrentUpdate();
    }
}
