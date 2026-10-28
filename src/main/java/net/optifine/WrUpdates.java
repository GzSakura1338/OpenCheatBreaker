package net.optifine;

import java.util.List;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class WrUpdates {
    public static IWrUpdater wrUpdater = null;

    public static void setWrUpdater(IWrUpdater wrUpdater2) {
        if (wrUpdater != null) {
            wrUpdater.terminate();
        }

        wrUpdater = wrUpdater2;
        if (wrUpdater != null) {
            try {
                wrUpdater.initialize();
            } catch (Exception e) {
                wrUpdater = null;
                e.printStackTrace();
            }
        }
    }

    public static boolean hasWrUpdater() {
        return wrUpdater != null;
    }

    public static IWrUpdater getWrUpdater() {
        return wrUpdater;
    }

    public static WorldRenderer makeWorldRenderer(World world, List list, int n, int n2, int n3, int n4) {
        return wrUpdater == null ? new WorldRenderer(world, list, n, n2, n3, n4) : wrUpdater.makeWorldRenderer(world, list, n, n2, n3, n4);
    }

    public static boolean updateRenderers(RenderGlobal renderGlobal, EntityLivingBase entityLivingBase, boolean flag) {
        try {
            return wrUpdater.updateRenderers(renderGlobal, entityLivingBase, flag);
        } catch (Exception e) {
            e.printStackTrace();
            setWrUpdater((IWrUpdater)null);
            return false;
        }
    }

    public static void resumeBackgroundUpdates() {
        if (wrUpdater != null) {
            wrUpdater.resumeBackgroundUpdates();
        }
    }

    public static void pauseBackgroundUpdates() {
        if (wrUpdater != null) {
            wrUpdater.pauseBackgroundUpdates();
        }
    }

    public static void finishCurrentUpdate() {
        if (wrUpdater != null) {
            wrUpdater.finishCurrentUpdate();
        }
    }

    public static void preRender(RenderGlobal renderGlobal, EntityLivingBase entityLivingBase) {
        if (wrUpdater != null) {
            wrUpdater.preRender(renderGlobal, entityLivingBase);
        }
    }

    public static void postRender() {
        if (wrUpdater != null) {
            wrUpdater.postRender();
        }
    }

    public static void clearAllUpdates() {
        if (wrUpdater != null) {
            wrUpdater.clearAllUpdates();
        }
    }
}
