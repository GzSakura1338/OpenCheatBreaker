package net.minecraft.world.border;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.ChunkCoordIntPair;

public class WorldBorder {
   public List<IBorderListener> listeners = Lists.newArrayList();
   public double centerX = 0.0;
   public double centerZ = 0.0;
   public double startDiameter = 6.0E7;
   public double endDiameter = this.startDiameter;
   public long endTime;
   public long startTime;
   public int worldSize = 29999984;
   public double damageAmount = 0.2;
   public double damageBuffer = 5.0;
   public int warningTime = 15;
   public int warningDistance = 5;

   public boolean contains(BlockPos pos) {
      return pos.getX() + 1 > this.minX() && pos.getX() < this.maxX() && pos.getZ() + 1 > this.minZ() && pos.getZ() < this.maxZ();
   }

   public boolean contains(ChunkCoordIntPair range) {
      return range.getXEnd() > this.minX() && range.getXStart() < this.maxX() && range.getZEnd() > this.minZ() && range.getZStart() < this.maxZ();
   }

   public boolean contains(AxisAlignedBB bb) {
      return bb.maxX > this.minX() && bb.minX < this.maxX() && bb.maxZ > this.minZ() && bb.minZ < this.maxZ();
   }

   public double getClosestDistance(Entity entityIn) {
      return this.getClosestDistance(entityIn.posX, entityIn.posZ);
   }

   public double getClosestDistance(double x, double z) {
      double d = z - this.minZ();
      double d2 = this.maxZ() - z;
      double d3 = x - this.minX();
      double d4 = this.maxX() - x;
      double min = Math.min(d3, d4);
      min = Math.min(min, d);
      return Math.min(min, d2);
   }

   public EnumBorderStatus getStatus() {
      return this.endDiameter < this.startDiameter
         ? EnumBorderStatus.SHRINKING
         : (this.endDiameter > this.startDiameter ? EnumBorderStatus.GROWING : EnumBorderStatus.STATIONARY);
   }

   public double minX() {
      double d = this.getCenterX() - this.getDiameter() / 2.0;
      if (d < -this.worldSize) {
         d = -this.worldSize;
      }

      return d;
   }

   public double minZ() {
      double d = this.getCenterZ() - this.getDiameter() / 2.0;
      if (d < -this.worldSize) {
         d = -this.worldSize;
      }

      return d;
   }

   public double maxX() {
      double d = this.getCenterX() + this.getDiameter() / 2.0;
      if (d > this.worldSize) {
         d = this.worldSize;
      }

      return d;
   }

   public double maxZ() {
      double d = this.getCenterZ() + this.getDiameter() / 2.0;
      if (d > this.worldSize) {
         d = this.worldSize;
      }

      return d;
   }

   public double getCenterX() {
      return this.centerX;
   }

   public double getCenterZ() {
      return this.centerZ;
   }

   public void setCenter(double x, double z) {
      this.centerX = x;
      this.centerZ = z;

      for (IBorderListener borderListener : this.getListeners()) {
         borderListener.onCenterChanged(this, x, z);
      }
   }

   public double getDiameter() {
      if (this.getStatus() != EnumBorderStatus.STATIONARY) {
         double d = (float)(System.currentTimeMillis() - this.startTime) / (float)(this.endTime - this.startTime);
         if (d < 1.0) {
            return this.startDiameter + (this.endDiameter - this.startDiameter) * d;
         }

         this.setTransition(this.endDiameter);
      }

      return this.startDiameter;
   }

   public long getTimeUntilTarget() {
      return this.getStatus() != EnumBorderStatus.STATIONARY ? this.endTime - System.currentTimeMillis() : 0L;
   }

   public double getTargetSize() {
      return this.endDiameter;
   }

   public void setTransition(double newSize) {
      this.startDiameter = newSize;
      this.endDiameter = newSize;
      this.endTime = System.currentTimeMillis();
      this.startTime = this.endTime;

      for (IBorderListener borderListener : this.getListeners()) {
         borderListener.onSizeChanged(this, newSize);
      }
   }

   public void setTransition(double oldSize, double newSize, long time) {
      this.startDiameter = oldSize;
      this.endDiameter = newSize;
      this.startTime = System.currentTimeMillis();
      this.endTime = this.startTime + time;

      for (IBorderListener borderListener : this.getListeners()) {
         borderListener.onTransitionStarted(this, oldSize, newSize, time);
      }
   }

   public List<IBorderListener> getListeners() {
      return Lists.newArrayList(this.listeners);
   }

   public void addListener(IBorderListener listener) {
      this.listeners.add(listener);
   }

   public void setSize(int size) {
      this.worldSize = size;
   }

   public int getSize() {
      return this.worldSize;
   }

   public double getDamageBuffer() {
      return this.damageBuffer;
   }

   public void setDamageBuffer(double bufferSize) {
      this.damageBuffer = bufferSize;

      for (IBorderListener borderListener : this.getListeners()) {
         borderListener.onDamageBufferChanged(this, bufferSize);
      }
   }

   public double getDamageAmount() {
      return this.damageAmount;
   }

   public void setDamageAmount(double newAmount) {
      this.damageAmount = newAmount;

      for (IBorderListener borderListener : this.getListeners()) {
         borderListener.onDamageAmountChanged(this, newAmount);
      }
   }

   public double getResizeSpeed() {
      return this.endTime == this.startTime ? 0.0 : Math.abs(this.startDiameter - this.endDiameter) / (this.endTime - this.startTime);
   }

   public int getWarningTime() {
      return this.warningTime;
   }

   public void setWarningTime(int warningTime) {
      this.warningTime = warningTime;

      for (IBorderListener borderListener : this.getListeners()) {
         borderListener.onWarningTimeChanged(this, warningTime);
      }
   }

   public int getWarningDistance() {
      return this.warningDistance;
   }

   public void setWarningDistance(int warningDistance) {
      this.warningDistance = warningDistance;

      for (IBorderListener borderListener : this.getListeners()) {
         borderListener.onWarningDistanceChanged(this, warningDistance);
      }
   }
}
