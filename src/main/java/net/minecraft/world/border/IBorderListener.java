package net.minecraft.world.border;

public interface IBorderListener {
   void onSizeChanged(WorldBorder worldBorder, double d);

   void onTransitionStarted(WorldBorder worldBorder, double d, double d2, long l);

   void onCenterChanged(WorldBorder worldBorder, double d, double d2);

   void onWarningTimeChanged(WorldBorder worldBorder, int n);

   void onWarningDistanceChanged(WorldBorder worldBorder, int n);

   void onDamageAmountChanged(WorldBorder worldBorder, double d);

   void onDamageBufferChanged(WorldBorder worldBorder, double d);
}
