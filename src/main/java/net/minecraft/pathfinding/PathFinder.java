package net.minecraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.pathfinder.NodeProcessor;

public class PathFinder {
   public Path path = new Path();
   public PathPoint[] pathOptions = new PathPoint[32];
   public NodeProcessor nodeProcessor;

   public PathFinder(NodeProcessor nodeProcessorIn) {
      this.nodeProcessor = nodeProcessorIn;
   }

   public PathEntity createEntityPathTo(IBlockAccess blockaccess, Entity entityFrom, Entity entityTo, float dist) {
      return this.createEntityPathTo(blockaccess, entityFrom, entityTo.posX, entityTo.getEntityBoundingBox().minY, entityTo.posZ, dist);
   }

   public PathEntity createEntityPathTo(IBlockAccess blockaccess, Entity entityIn, BlockPos targetPos, float dist) {
      return this.createEntityPathTo(blockaccess, entityIn, targetPos.getX() + 0.5F, targetPos.getY() + 0.5F, targetPos.getZ() + 0.5F, dist);
   }

   public PathEntity createEntityPathTo(IBlockAccess blockaccess, Entity entityIn, double x, double y, double z, float distance) {
      this.path.clearPath();
      this.nodeProcessor.initProcessor(blockaccess, entityIn);
      PathPoint pathPointTo = this.nodeProcessor.getPathPointTo(entityIn);
      PathPoint pathPointToCoords = this.nodeProcessor.getPathPointToCoords(entityIn, x, y, z);
      PathEntity addToPath = this.addToPath(entityIn, pathPointTo, pathPointToCoords, distance);
      this.nodeProcessor.postProcess();
      return addToPath;
   }

   public PathEntity addToPath(Entity entityIn, PathPoint pathpointStart, PathPoint pathpointEnd, float maxDistance) {
      pathpointStart.totalPathDistance = 0.0F;
      pathpointStart.distanceToNext = pathpointStart.distanceToSquared(pathpointEnd);
      pathpointStart.distanceToTarget = pathpointStart.distanceToNext;
      this.path.clearPath();
      this.path.addPoint(pathpointStart);
      PathPoint pathpointStart2 = pathpointStart;

      while (!this.path.isPathEmpty()) {
         PathPoint dequeue = this.path.dequeue();
         if (dequeue.equals(pathpointEnd)) {
            return this.createEntityPath(pathpointStart, pathpointEnd);
         }

         if (dequeue.distanceToSquared(pathpointEnd) < pathpointStart2.distanceToSquared(pathpointEnd)) {
            pathpointStart2 = dequeue;
         }

         dequeue.visited = true;
         int findPathOptions = this.nodeProcessor.findPathOptions(this.pathOptions, entityIn, dequeue, pathpointEnd, maxDistance);

         for (int i = 0; i < findPathOptions; i++) {
            PathPoint pathPoint = this.pathOptions[i];
            float f = dequeue.totalPathDistance + dequeue.distanceToSquared(pathPoint);
            if (f < maxDistance * 2.0F && (!pathPoint.isAssigned() || f < pathPoint.totalPathDistance)) {
               pathPoint.previous = dequeue;
               pathPoint.totalPathDistance = f;
               pathPoint.distanceToNext = pathPoint.distanceToSquared(pathpointEnd);
               if (pathPoint.isAssigned()) {
                  this.path.changeDistance(pathPoint, pathPoint.totalPathDistance + pathPoint.distanceToNext);
               } else {
                  pathPoint.distanceToTarget = pathPoint.totalPathDistance + pathPoint.distanceToNext;
                  this.path.addPoint(pathPoint);
               }
            }
         }
      }

      return pathpointStart2 == pathpointStart ? null : this.createEntityPath(pathpointStart, pathpointStart2);
   }

   public PathEntity createEntityPath(PathPoint start, PathPoint end) {
      int n = 1;

      for (PathPoint end2 = end; end2.previous != null; end2 = end2.previous) {
         n++;
      }

      PathPoint[] pathPoints = new PathPoint[n];
      PathPoint end2 = end;

      for (pathPoints[--n] = end; end2.previous != null; pathPoints[--n] = end2) {
         end2 = end2.previous;
      }

      return new PathEntity(pathPoints);
   }
}
