package net.optifine;

import com.cheatbreaker.client.util.EmptyHolder;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import org.lwjgl.opengl.Pbuffer;

public class WrUpdateThread extends Thread {
    public Pbuffer pbuffer = null;
    public Object lock = new Object();
    public List updateList = new LinkedList();
    public List updatedList = new LinkedList();
    public int updateCount = 0;
    public Tessellator mainTessellator = Tessellator.instance;
    public Tessellator threadTessellator = new Tessellator(2097152);
    public boolean working = false;
    public WorldRendererThreaded currentRenderer = null;
    public boolean canWork = false;
    public boolean canWorkToEndOfUpdate = false;
    public boolean terminated = false;
    public static final int MAX_UPDATE_CAPACITY = 10;

    public WrUpdateThread(Pbuffer pbuffer) {
        super("WrUpdateThread");
        this.pbuffer = pbuffer;
    }

    @Override
    public void run() {
        try {
            this.pbuffer.makeCurrent();
        } catch (Exception e) {
            e.printStackTrace();
        }

        IWrUpdateListener wrUpdateListener = new IWrUpdateListener() {
            public WrUpdateThread.ThreadUpdateControl threadUpdateControl = WrUpdateThread.this.new ThreadUpdateControl();

            @Override
            public void updating(IWrUpdateControl wrUpdateControl) {
                this.threadUpdateControl.setUpdateControl(wrUpdateControl);
                WrUpdateThread.this.checkCanWork(this.threadUpdateControl);
            }
        };

        while (!Thread.interrupted() && !this.terminated) {
            try {
                WorldRendererThreaded rendererToUpdate = this.getRendererToUpdate();
                if (rendererToUpdate == null) {
                    return;
                }

                this.checkCanWork((IWrUpdateControl)null);

                try {
                    this.currentRenderer = rendererToUpdate;
                    Tessellator.instance = this.threadTessellator;
                    rendererToUpdate.updateRenderer(wrUpdateListener);
                } finally {
                    Tessellator.instance = this.mainTessellator;
                }

                this.rendererUpdated(rendererToUpdate);
            } catch (Exception e) {
                e.printStackTrace();
                if (this.currentRenderer != null) {
                    this.currentRenderer.isUpdating = false;
                    this.currentRenderer.needsUpdate = true;
                }

                this.currentRenderer = null;
                this.working = false;
            }
        }
    }

    public void addRendererToUpdate(WorldRenderer worldRenderer, boolean flag) {
        Object lock = this.lock;
        synchronized (this.lock) {
            if (worldRenderer.isUpdating) {
                throw new IllegalArgumentException("Renderer already updating");
            }

            if (flag) {
                this.updateList.add(0, worldRenderer);
            } else {
                this.updateList.add(worldRenderer);
            }

            worldRenderer.isUpdating = true;
            this.lock.notifyAll();
        }
    }

    public WorldRendererThreaded getRendererToUpdate() {
        Object lock = this.lock;
        synchronized (this.lock) {
            while (this.updateList.size() <= 0) {
                try {
                    this.lock.wait(2000L);
                    if (this.terminated) {
                        Object obj = null;
                        return (WorldRendererThreaded)obj;
                    }
                } catch (InterruptedException e) {
                }
            }

            WorldRendererThreaded worldRendererThreaded = (WorldRendererThreaded)this.updateList.remove(0);
            this.lock.notifyAll();
            return worldRendererThreaded;
        }
    }

    public boolean hasWorkToDo() {
        Object lock = this.lock;
        synchronized (this.lock) {
            return this.updateList.size() > 0 ? true : (this.currentRenderer != null ? true : this.working);
        }
    }

    public int getUpdateCapacity() {
        Object lock = this.lock;
        synchronized (this.lock) {
            return this.updateList.size() > 10 ? 0 : 10 - this.updateList.size();
        }
    }

    public void rendererUpdated(WorldRenderer worldRenderer) {
        Object lock = this.lock;
        synchronized (this.lock) {
            this.updatedList.add(worldRenderer);
            this.updateCount++;
            this.currentRenderer = null;
            this.working = false;
            this.lock.notifyAll();
        }
    }

    public void finishUpdatedRenderers() {
        Object lock = this.lock;
        synchronized (this.lock) {
            for (int i = 0; i < this.updatedList.size(); i++) {
                WorldRendererThreaded worldRendererThreaded = (WorldRendererThreaded)this.updatedList.get(i);
                worldRendererThreaded.finishUpdate();
                worldRendererThreaded.isUpdating = false;
            }

            this.updatedList.clear();
        }
    }

    public void pause() {
        Object lock = this.lock;
        synchronized (this.lock) {
            this.canWork = false;
            this.canWorkToEndOfUpdate = false;
            this.lock.notifyAll();

            while (this.working) {
                try {
                    this.lock.wait();
                } catch (InterruptedException e) {
                }
            }

            this.finishUpdatedRenderers();
        }
    }

    public void unpause() {
        Object lock = this.lock;
        synchronized (this.lock) {
            if (this.working) {
                Config.warn("UpdateThread still working in unpause()!!!");
            }

            this.canWork = true;
            this.canWorkToEndOfUpdate = false;
            this.lock.notifyAll();
        }
    }

    public void unpauseToEndOfUpdate() {
        Object lock = this.lock;
        synchronized (this.lock) {
            if (this.working) {
                Config.warn("UpdateThread still working in unpause()!!!");
            }

            if (this.currentRenderer != null) {
                while (this.currentRenderer != null) {
                    this.canWork = false;
                    this.canWorkToEndOfUpdate = true;
                    this.lock.notifyAll();

                    try {
                        this.lock.wait();
                    } catch (InterruptedException e) {
                    }
                }

                this.pause();
            }
        }
    }

    public void checkCanWork(IWrUpdateControl wrUpdateControl) {
        Thread.yield();
        Object lock = this.lock;
        synchronized (this.lock) {
            while (!this.canWork && (!this.canWorkToEndOfUpdate || this.currentRenderer == null)) {
                if (wrUpdateControl != null) {
                    wrUpdateControl.pause();
                }

                this.working = false;
                this.lock.notifyAll();

                try {
                    this.lock.wait();
                } catch (InterruptedException e) {
                }
            }

            this.working = true;
            if (wrUpdateControl != null) {
                wrUpdateControl.resume();
            }

            this.lock.notifyAll();
        }
    }

    public void clearAllUpdates() {
        Object lock = this.lock;
        synchronized (this.lock) {
            this.unpauseToEndOfUpdate();

            for (int i = 0; i < this.updateList.size(); i++) {
                WorldRenderer worldRenderer = (WorldRenderer)this.updateList.get(i);
                worldRenderer.needsUpdate = true;
                worldRenderer.isUpdating = false;
            }

            this.updateList.clear();
            this.lock.notifyAll();
        }
    }

    public int getPendingUpdatesCount() {
        Object lock = this.lock;
        synchronized (this.lock) {
            int size = this.updateList.size();
            if (this.currentRenderer != null) {
                size++;
            }

            return size;
        }
    }

    public int resetUpdateCount() {
        Object lock = this.lock;
        synchronized (this.lock) {
            int updateCount = this.updateCount;
            this.updateCount = 0;
            return updateCount;
        }
    }

    public void terminate() {
        this.terminated = true;
    }

    private class ThreadUpdateControl implements IWrUpdateControl {
        public IWrUpdateControl updateControl = null;
        public boolean paused = false;

        public ThreadUpdateControl() {
        }

        @Override
        public void pause() {
            if (!this.paused) {
                this.paused = true;
                this.updateControl.pause();
                Tessellator.instance = WrUpdateThread.this.mainTessellator;
            }
        }

        @Override
        public void resume() {
            if (this.paused) {
                this.paused = false;
                Tessellator.instance = WrUpdateThread.this.threadTessellator;
                this.updateControl.resume();
            }
        }

        public void setUpdateControl(IWrUpdateControl wrUpdateControl) {
            this.updateControl = wrUpdateControl;
        }
    }
}
