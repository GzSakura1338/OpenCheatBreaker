package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public interface IWorldAccess {
   void markBlockForUpdate(BlockPos blockPos);

   void notifyLightSet(BlockPos blockPos);

   void markBlockRangeForRenderUpdate(int n, int n2, int n3, int n4, int n5, int n6);

   void playSound(String str, double d, double d2, double d3, float f, float f2);

   void playSoundToNearExcept(EntityPlayer entityPlayer, String str, double d, double d2, double d3, float f, float f2);

   void spawnParticle(int n, boolean flag, double d, double d2, double d3, double d4, double d5, double d6, int... ints);

   void onEntityAdded(Entity entity);

   void onEntityRemoved(Entity entity);

   void playRecord(String str, BlockPos blockPos);

   void broadcastSound(int n, BlockPos blockPos, int n2);

   void playAuxSFX(EntityPlayer entityPlayer, int n, BlockPos blockPos, int n2);

   void sendBlockBreakProgress(int n, BlockPos blockPos, int n2);
}
