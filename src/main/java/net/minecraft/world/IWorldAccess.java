package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

public interface IWorldAccess {
    void markBlockForUpdate(int n, int n2, int n3);

    void markBlockForRenderUpdate(int n, int n2, int n3);

    void markBlockRangeForRenderUpdate(int n, int n2, int n3, int n4, int n5, int n6);

    void playSound(String str, double d, double d2, double d3, float f, float f2);

    void playSoundToNearExcept(EntityPlayer entityPlayer, String str, double d, double d2, double d3, float f, float f2);

    void spawnParticle(String str, double d, double d2, double d3, double d4, double d5, double d6);

    void onEntityCreate(Entity entity);

    void onEntityDestroy(Entity entity);

    void playRecord(String str, int n, int n2, int n3);

    void broadcastSound(int n, int n2, int n3, int n4, int n5);

    void playAuxSFX(EntityPlayer entityPlayer, int n, int n2, int n3, int n4, int n5);

    void destroyBlockPartially(int n, int n2, int n3, int n4, int n5);

    void onStaticEntitiesChanged();
}
