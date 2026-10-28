package net.optifine;

import java.util.List;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.Drawable;
import org.lwjgl.opengl.Pbuffer;
import org.lwjgl.opengl.PixelFormat;

public class WrUpdaterThreaded implements IWrUpdater {
    public WrUpdateThread updateThread = null;
    public float timePerUpdateMs = 10.0F;
    public long updateStartTimeNs = 0L;
    public boolean firstUpdate = true;
    public int updateTargetNum = 0;

    @Override
    public void terminate() {
        if (this.updateThread != null) {
            this.updateThread.terminate();
            this.updateThread.unpauseToEndOfUpdate();
        }
    }

    @Override
    public void initialize() {
    }

    public void delayedInit() {
        if (this.updateThread == null) {
            this.createUpdateThread(Display.getDrawable());
        }
    }

    @Override
    public WorldRenderer makeWorldRenderer(World world, List list, int n, int n2, int n3, int n4) {
        return new WorldRendererThreaded(world, list, n, n2, n3, n4);
    }

    public WrUpdateThread createUpdateThread(Drawable drawable) {
        if (this.updateThread != null) {
            throw new IllegalStateException("UpdateThread is already existing");
        }

        try {
            Pbuffer pbuffer = new Pbuffer(1, 1, new PixelFormat(), drawable);
            this.updateThread = new WrUpdateThread(pbuffer);
            this.updateThread.setPriority(1);
            this.updateThread.start();
            this.updateThread.pause();
            return this.updateThread;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isUpdateThread() {
        return Thread.currentThread() == this.updateThread;
    }

    public static boolean isBackgroundChunkLoading() {
        return true;
    }

    @Override
    public void preRender(RenderGlobal renderGlobal, EntityLivingBase entityLivingBase) {
        this.updateTargetNum = 0;
        if (this.updateThread != null) {
            if (this.updateStartTimeNs == 0L) {
                this.updateStartTimeNs = System.nanoTime();
            }

            if (this.updateThread.hasWorkToDo()) {
                this.updateTargetNum = Config.getUpdatesPerFrame();
                if (Config.isDynamicUpdates() && !renderGlobal.isMoving(entityLivingBase)) {
                    this.updateTargetNum *= 3;
                }

                this.updateTargetNum = Math.min(this.updateTargetNum, this.updateThread.getPendingUpdatesCount());
                if (this.updateTargetNum > 0) {
                    this.updateThread.unpause();
                }
            }
        }
    }

    @Override
    public void postRender() {
        if (this.updateThread != null) {
            float f = 0.0F;
            if (this.updateTargetNum > 0) {
                long l = System.nanoTime() - this.updateStartTimeNs;
                float f2 = this.timePerUpdateMs * (1.0F + (this.updateTargetNum - 1) / 2.0F);
                if (f2 > 0.0F) {
                    int n = (int)f2;
                    Config.sleep(n);
                }

                this.updateThread.pause();
            }

            float f2 = 0.2F;
            if (this.updateTargetNum > 0) {
                int resetUpdateCount = this.updateThread.resetUpdateCount();
                if (resetUpdateCount < this.updateTargetNum) {
                    this.timePerUpdateMs += f2;
                }

                if (resetUpdateCount > this.updateTargetNum) {
                    this.timePerUpdateMs -= f2;
                }

                if (resetUpdateCount == this.updateTargetNum) {
                    this.timePerUpdateMs -= f2;
                }
            } else {
                this.timePerUpdateMs -= f2 / 5.0F;
            }

            if (this.timePerUpdateMs < 0.0F) {
                this.timePerUpdateMs = 0.0F;
            }

            this.updateStartTimeNs = System.nanoTime();
        }
    }

    @Override
    public boolean updateRenderers(RenderGlobal renderGlobal, EntityLivingBase entityLivingBase, boolean flag) {
        this.delayedInit();
        if (renderGlobal.compactArrayList.size() <= 0) {
            return true;
        }

        int n = 0;
        byte b = 4;
        int n2 = 0;
        WorldRenderer worldRenderer = null;
        float f = Float.MAX_VALUE;
        int n3 = -1;

        for (int i = 0; i < renderGlobal.compactArrayList.size(); i++) {
            WorldRenderer worldRenderer2 = (WorldRenderer)renderGlobal.compactArrayList.get(i);
            if (worldRenderer2 != null) {
                n2++;
                if (!worldRenderer2.isUpdating) {
                    if (!worldRenderer2.needsUpdate) {
                        renderGlobal.compactArrayList.set(i, null);
                    } else {
                        float distanceToEntitySquared = worldRenderer2.distanceToEntitySquared(entityLivingBase);
                        if (distanceToEntitySquared < 512.0F) {
                            if (distanceToEntitySquared < 256.0F && renderGlobal.isActingNow() && worldRenderer2.isInFrustum || this.firstUpdate) {
                                if (this.updateThread != null) {
                                    this.updateThread.unpauseToEndOfUpdate();
                                }

                                worldRenderer2.updateRenderer(entityLivingBase);
                                worldRenderer2.needsUpdate = false;
                                renderGlobal.compactArrayList.set(i, null);
                                n++;
                                continue;
                            }

                            if (this.updateThread != null) {
                                this.updateThread.addRendererToUpdate(worldRenderer2, true);
                                worldRenderer2.needsUpdate = false;
                                renderGlobal.compactArrayList.set(i, null);
                                n++;
                                continue;
                            }
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

        int updatesPerFrame = Config.getUpdatesPerFrame();
        boolean flag2 = false;
        if (Config.isDynamicUpdates() && !renderGlobal.isMoving(entityLivingBase)) {
            updatesPerFrame *= 3;
            flag2 = true;
        }

        if (this.updateThread != null) {
            updatesPerFrame = this.updateThread.getUpdateCapacity();
            if (updatesPerFrame <= 0) {
                return true;
            }
        }

        if (worldRenderer != null) {
            this.updateRenderer(worldRenderer, entityLivingBase);
            renderGlobal.compactArrayList.set(n3, null);
            n++;
            float f2 = f / 5.0F;

            for (int i = 0; i < renderGlobal.compactArrayList.size() && n < updatesPerFrame; i++) {
                WorldRenderer worldRenderer2 = (WorldRenderer)renderGlobal.compactArrayList.get(i);
                if (worldRenderer2 != null && !worldRenderer2.isUpdating) {
                    float distanceToEntitySquared = worldRenderer2.distanceToEntitySquared(entityLivingBase);
                    if (!worldRenderer2.isInFrustum) {
                        distanceToEntitySquared *= b;
                    }

                    float abs = Math.abs(distanceToEntitySquared - f);
                    if (abs < f2) {
                        this.updateRenderer(worldRenderer2, entityLivingBase);
                        renderGlobal.compactArrayList.set(i, null);
                        n++;
                    }
                }
            }
        }

        if (n2 == 0) {
            renderGlobal.compactArrayList.clear();
        }

        if (renderGlobal.compactArrayList.size() > 100 && n2 < renderGlobal.compactArrayList.size() * 4 / 5) {
            int n4 = 0;

            for (int i = 0; i < renderGlobal.compactArrayList.size(); i++) {
                Object obj = renderGlobal.compactArrayList.get(i);
                if (obj != null) {
                    if (i != n4) {
                        renderGlobal.compactArrayList.set(n4, obj);
                    }

                    n4++;
                }
            }

            for (int i = renderGlobal.compactArrayList.size() - 1; i >= n4; i--) {
                renderGlobal.compactArrayList.remove(i);
            }
        }

        this.firstUpdate = false;
        return true;
    }

    public void updateRenderer(WorldRenderer worldRenderer, EntityLivingBase entityLivingBase) {
        WrUpdateThread updateThread = this.updateThread;
        if (updateThread != null) {
            updateThread.addRendererToUpdate(worldRenderer, false);
            worldRenderer.needsUpdate = false;
        } else {
            worldRenderer.updateRenderer(entityLivingBase);
            worldRenderer.needsUpdate = false;
            worldRenderer.isUpdating = false;
        }
    }

    @Override
    public void finishCurrentUpdate() {
        if (this.updateThread != null) {
            this.updateThread.unpauseToEndOfUpdate();
        }
    }

    @Override
    public void resumeBackgroundUpdates() {
        if (this.updateThread != null) {
            this.updateThread.unpause();
        }
    }

    @Override
    public void pauseBackgroundUpdates() {
        if (this.updateThread != null) {
            this.updateThread.pause();
        }
    }

    @Override
    public void clearAllUpdates() {
        if (this.updateThread != null) {
            this.updateThread.clearAllUpdates();
        }

        this.firstUpdate = true;
    }
}
