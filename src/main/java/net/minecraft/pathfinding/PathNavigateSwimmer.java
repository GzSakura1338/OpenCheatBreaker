package net.minecraft.pathfinding;

import net.minecraft.entity.EntityLiving;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.pathfinder.SwimNodeProcessor;

public class PathNavigateSwimmer extends PathNavigate {
   public PathNavigateSwimmer(EntityLiving entitylivingIn, World worldIn) {
      super(entitylivingIn, worldIn);
   }

   @Override
   public PathFinder getPathFinder() {
      return new PathFinder(new SwimNodeProcessor());
   }

   @Override
   public boolean canNavigate() {
      return this.isInLiquid();
   }

   @Override
   public Vec3 getEntityPosition() {
      return new Vec3(this.theEntity.posX, this.theEntity.posY + this.theEntity.height * 0.5, this.theEntity.posZ);
   }

   @Override
   public void pathFollow() {
      Vec3 entityPosition = this.getEntityPosition();
      float f = this.theEntity.width * this.theEntity.width;
      byte b = 6;
      if (entityPosition.squareDistanceTo(this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex())) < f) {
         this.currentPath.incrementPathIndex();
      }

      for (int min = Math.min(this.currentPath.getCurrentPathIndex() + b, this.currentPath.getCurrentPathLength() - 1);
         min > this.currentPath.getCurrentPathIndex();
         min--
      ) {
         Vec3 vectorFromIndex = this.currentPath.getVectorFromIndex(this.theEntity, min);
         if (vectorFromIndex.squareDistanceTo(entityPosition) <= 36.0 && this.isDirectPathBetweenPoints(entityPosition, vectorFromIndex, 0, 0, 0)) {
            this.currentPath.setCurrentPathIndex(min);
            break;
         }
      }

      this.checkForStuck(entityPosition);
   }

   @Override
   public void removeSunnyPath() {
      super.removeSunnyPath();
   }

   @Override
   public boolean isDirectPathBetweenPoints(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ) {
      MovingObjectPosition rayTraceBlocks = this.worldObj
         .rayTraceBlocks(posVec31, new Vec3(posVec32.xCoord, posVec32.yCoord + this.theEntity.height * 0.5, posVec32.zCoord), false, true, false);
      return rayTraceBlocks == null || rayTraceBlocks.typeOfHit == MovingObjectType.MISS;
   }
}
