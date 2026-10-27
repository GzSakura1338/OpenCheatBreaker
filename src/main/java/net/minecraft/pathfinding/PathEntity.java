package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.util.Vec3;

public class PathEntity {
   public PathPoint[] points;
   public int currentPathIndex;
   public int pathLength;

   public PathEntity(PathPoint[] pathpoints) {
      this.points = pathpoints;
      this.pathLength = pathpoints.length;
   }

   public void incrementPathIndex() {
      this.currentPathIndex++;
   }

   public boolean isFinished() {
      return this.currentPathIndex >= this.pathLength;
   }

   public PathPoint getFinalPathPoint() {
      return this.pathLength > 0 ? this.points[this.pathLength - 1] : null;
   }

   public PathPoint getPathPointFromIndex(int index) {
      return this.points[index];
   }

   public int getCurrentPathLength() {
      return this.pathLength;
   }

   public void setCurrentPathLength(int length) {
      this.pathLength = length;
   }

   public int getCurrentPathIndex() {
      return this.currentPathIndex;
   }

   public void setCurrentPathIndex(int currentPathIndexIn) {
      this.currentPathIndex = currentPathIndexIn;
   }

   public Vec3 getVectorFromIndex(Entity entityIn, int index) {
      double d = this.points[index].xCoord + (int)(entityIn.width + 1.0F) * 0.5;
      double yCoord = this.points[index].yCoord;
      double d2 = this.points[index].zCoord + (int)(entityIn.width + 1.0F) * 0.5;
      return new Vec3(d, yCoord, d2);
   }

   public Vec3 getPosition(Entity entityIn) {
      return this.getVectorFromIndex(entityIn, this.currentPathIndex);
   }

   public boolean isSamePath(PathEntity pathentityIn) {
      if (pathentityIn == null) {
         return false;
      }

      if (pathentityIn.points.length != this.points.length) {
         return false;
      }

      for (int i = 0; i < this.points.length; i++) {
         if (this.points[i].xCoord != pathentityIn.points[i].xCoord
            || this.points[i].yCoord != pathentityIn.points[i].yCoord
            || this.points[i].zCoord != pathentityIn.points[i].zCoord) {
            return false;
         }
      }

      return true;
   }

   public boolean isDestinationSame(Vec3 vec) {
      PathPoint finalPathPoint = this.getFinalPathPoint();
      return finalPathPoint == null ? false : finalPathPoint.xCoord == (int)vec.xCoord && finalPathPoint.zCoord == (int)vec.zCoord;
   }
}
