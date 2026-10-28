package net.optifine;

import java.util.List;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public interface IWrUpdater {
    void initialize();

    WorldRenderer makeWorldRenderer(World world, List list, int n, int n2, int n3, int n4);

    void preRender(RenderGlobal renderGlobal, EntityLivingBase entityLivingBase);

    void postRender();

    boolean updateRenderers(RenderGlobal renderGlobal, EntityLivingBase entityLivingBase, boolean flag);

    void resumeBackgroundUpdates();

    void pauseBackgroundUpdates();

    void finishCurrentUpdate();

    void clearAllUpdates();

    void terminate();
}
