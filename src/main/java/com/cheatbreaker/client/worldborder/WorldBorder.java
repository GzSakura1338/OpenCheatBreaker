package com.cheatbreaker.client.worldborder;

import com.cheatbreaker.client.CheatBreaker;
import com.sun.javafx.geom.Vec2d;
import java.awt.Color;
import net.minecraft.entity.Entity;

public class WorldBorder {
   public Vec2d targetMax;
   public WorldBorderManager worldBorderManager;
   public int transitionTicks;
   public String id;
   public String serverId;
   public Color color;
   public int transitionProgress;
   public Vec2d targetMin;
   public boolean moving;
   public boolean enabled;
   public Vec2d initialMin;
   public Vec2d min;
   public Vec2d initialMax;
   public Vec2d max;

   public Vec2d getTargetMax() {
      return this.targetMax;
   }

   public static Vec2d setTargetMin(WorldBorder worldBorder, Vec2d vec2d) {
      worldBorder.targetMin = vec2d;
      return worldBorder.targetMin;
   }

   public double getMinZ() {
      return this.min.y;
   }

   public static int setTransitionTicks(WorldBorder worldBorder, int n) {
      worldBorder.transitionTicks = n;
      return worldBorder.transitionTicks;
   }

   public double getMaxZ() {
      return this.max.y;
   }

   public static Vec2d setTargetMax(WorldBorder worldBorder, Vec2d vec2d) {
      worldBorder.targetMax = vec2d;
      return worldBorder.targetMax;
   }

   public double getDistanceToBorder(Entity entity) {
      return this.getDistanceToBorderAt(entity.posX, entity.posZ);
   }

   public static String getId(WorldBorder worldBorder) {
      return worldBorder.id;
   }

   public Color getColor() {
      return this.color;
   }

   public boolean isInsideOrIgnored(double d, double d2) {
      return !this.enabled
         || !this.serverId.equals(WorldBorderManager.findCheatBreaker(this.worldBorderManager).getNetHandler().getWorldName())
         || d + 1.0 > this.getMinX() && d < this.getMaxX() && d2 + 1.0 > this.getMinZ() && d2 < this.getMaxZ();
   }

   public String getServerId() {
      return this.serverId;
   }

   public double getDistanceToBorderAt(double d, double d2) {
      double d3 = d2 - this.getMinZ();
      double d4 = this.getMaxZ() - d2;
      double d5 = d - this.getMinX();
      double d6 = this.getMaxX() - d;
      double min = Math.min(d5, d6);
      min = Math.min(min, d3);
      return Math.min(min, d4);
   }

   public static boolean isMoving(WorldBorder worldBorder) {
      return worldBorder.moving;
   }

   public boolean isTransitioning() {
      return this.moving && this.transitionTicks != 0 && this.transitionProgress < this.transitionTicks;
   }

   public Vec2d getTargetMin() {
      return this.targetMin;
   }

   public boolean isMoving() {
      return this.moving;
   }

   public WorldBorder(
      WorldBorderManager worldBorderManager, String str, String str2, int n, double d, double d2, double d3, double d4, boolean flag, boolean flag2
   ) {
      this.worldBorderManager = worldBorderManager;
      this.id = str;
      this.serverId = str2;
      this.color = new Color(n, true);
      this.moving = flag;
      this.enabled = flag2;
      this.min = this.initialMin = new Vec2d(d, d2);
      this.max = this.initialMax = new Vec2d(d3, d4);
   }

   public double getMinX() {
      return this.min.x;
   }

   public double getMaxX() {
      return this.max.x;
   }

   public static Color getColor(WorldBorder worldBorder) {
      return worldBorder.color;
   }

   public Vec2d getMin() {
      return this.min;
   }

   public String getId() {
      return this.id;
   }

   public Vec2d getInitialMax() {
      return this.initialMax;
   }

   public boolean isOnCurrentServer() {
      return CheatBreaker.getInstance().getNetHandler().getWorldName().equals(this.serverId);
   }

   public void tick() {
      if (this.isTransitioning()) {
         double d = this.initialMin.x - this.targetMin.x;
         double d2 = this.initialMax.x - this.targetMax.x;
         double d3 = this.initialMin.y - this.targetMin.y;
         double d4 = this.initialMax.y - this.targetMax.y;
         double d5 = (float)this.transitionProgress / this.transitionTicks;
         double d6 = this.initialMin.x - d * d5;
         double d7 = this.initialMax.x - d2 * d5;
         double d8 = this.initialMin.y - d3 * d5;
         double d9 = this.initialMax.y - d4 * d5;
         this.min = new Vec2d(d6, d8);
         this.max = new Vec2d(d7, d9);
         this.transitionProgress++;
      } else if (this.initialMax != this.max || this.initialMin != this.min) {
         this.initialMin = this.min;
         this.initialMax = this.max;
         this.max = this.targetMax;
         this.min = this.targetMin;
         this.transitionTicks = 0;
         this.transitionProgress = 0;
      }
   }

   public static int setTransitionProgress(WorldBorder worldBorder, int n) {
      worldBorder.transitionProgress = n;
      return worldBorder.transitionProgress;
   }

   public boolean isEnabled() {
      return this.enabled;
   }

   public Vec2d getMax() {
      return this.max;
   }

   public int getTransitionProgress() {
      return this.transitionProgress;
   }

   public int getTransitionTicks() {
      return this.transitionTicks;
   }

   public Vec2d getInitialMin() {
      return this.initialMin;
   }
}
