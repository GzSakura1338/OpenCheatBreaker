package net.minecraft.pathfinding;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.World;

public abstract class PathNavigate {
   public Vec3 lastPosCheck = new Vec3(0.0, 0.0, 0.0);
   public float heightRequirement = 1.0F;
   public PathEntity currentPath;
   public double speed;
   public EntityLiving theEntity;
   public int totalTicks;
   public int ticksAtLastPos;
   public World worldObj;
   public IAttributeInstance pathSearchRange;
   public PathFinder pathFinder;

   public PathNavigate(EntityLiving entitylivingIn, World worldIn) {
      this.theEntity = entitylivingIn;
      this.worldObj = worldIn;
      this.pathSearchRange = entitylivingIn.getEntityAttribute(SharedMonsterAttributes.followRange);
      this.pathFinder = this.getPathFinder();
   }

   public abstract PathFinder getPathFinder();

   public void setSpeed(double speedIn) {
      this.speed = speedIn;
   }

   public float getPathSearchRange() {
      return (float)this.pathSearchRange.getAttributeValue();
   }

   public PathEntity getPathToXYZ(double x, double y, double z) {
      return this.getPathToPos(new BlockPos(MathHelper.floor_double(x), (int)y, MathHelper.floor_double(z)));
   }

   public PathEntity getPathToPos(BlockPos pos) {
      if (!this.canNavigate()) {
         return null;
      }

      float pathSearchRange = this.getPathSearchRange();
      this.worldObj.theProfiler.startSection("pathfind");
      BlockPos blockPos = new BlockPos(this.theEntity);
      int n = (int)(pathSearchRange + 8.0F);
      ChunkCache chunkCache = new ChunkCache(this.worldObj, blockPos.add(-n, -n, -n), blockPos.add(n, n, n), 0);
      PathEntity createEntityPathTo = this.pathFinder.createEntityPathTo(chunkCache, this.theEntity, pos, pathSearchRange);
      this.worldObj.theProfiler.endSection();
      return createEntityPathTo;
   }

   public boolean tryMoveToXYZ(double x, double y, double z, double speedIn) {
      PathEntity pathToXYZ = this.getPathToXYZ(MathHelper.floor_double(x), (int)y, MathHelper.floor_double(z));
      return this.setPath(pathToXYZ, speedIn);
   }

   public void setHeightRequirement(float jumpHeight) {
      this.heightRequirement = jumpHeight;
   }

   public PathEntity getPathToEntityLiving(Entity entityIn) {
      if (!this.canNavigate()) {
         return null;
      }

      float pathSearchRange = this.getPathSearchRange();
      this.worldObj.theProfiler.startSection("pathfind");
      BlockPos up = new BlockPos(this.theEntity).up();
      int n = (int)(pathSearchRange + 16.0F);
      ChunkCache chunkCache = new ChunkCache(this.worldObj, up.add(-n, -n, -n), up.add(n, n, n), 0);
      PathEntity createEntityPathTo = this.pathFinder.createEntityPathTo(chunkCache, this.theEntity, entityIn, pathSearchRange);
      this.worldObj.theProfiler.endSection();
      return createEntityPathTo;
   }

   public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn) {
      PathEntity pathToEntityLiving = this.getPathToEntityLiving(entityIn);
      return pathToEntityLiving != null ? this.setPath(pathToEntityLiving, speedIn) : false;
   }

   public boolean setPath(PathEntity pathentityIn, double speedIn) {
      if (pathentityIn == null) {
         this.currentPath = null;
         return false;
      }

      if (!pathentityIn.isSamePath(this.currentPath)) {
         this.currentPath = pathentityIn;
      }

      this.removeSunnyPath();
      if (this.currentPath.getCurrentPathLength() == 0) {
         return false;
      }

      this.speed = speedIn;
      Vec3 entityPosition = this.getEntityPosition();
      this.ticksAtLastPos = this.totalTicks;
      this.lastPosCheck = entityPosition;
      return true;
   }

   public PathEntity getPath() {
      return this.currentPath;
   }

   public void onUpdateNavigation() {
      this.totalTicks++;
      if (!this.noPath()) {
         if (this.canNavigate()) {
            this.pathFollow();
         } else if (this.currentPath != null && this.currentPath.getCurrentPathIndex() < this.currentPath.getCurrentPathLength()) {
            Vec3 entityPosition = this.getEntityPosition();
            Vec3 vectorFromIndex = this.currentPath.getVectorFromIndex(this.theEntity, this.currentPath.getCurrentPathIndex());
            if (entityPosition.yCoord > vectorFromIndex.yCoord
               && !this.theEntity.onGround
               && MathHelper.floor_double(entityPosition.xCoord) == MathHelper.floor_double(vectorFromIndex.xCoord)
               && MathHelper.floor_double(entityPosition.zCoord) == MathHelper.floor_double(vectorFromIndex.zCoord)) {
               this.currentPath.setCurrentPathIndex(this.currentPath.getCurrentPathIndex() + 1);
            }
         }

         if (!this.noPath()) {
            Vec3 position = this.currentPath.getPosition(this.theEntity);
            if (position != null) {
               AxisAlignedBB expand = new AxisAlignedBB(position.xCoord, position.yCoord, position.zCoord, position.xCoord, position.yCoord, position.zCoord).expand(0.5, 0.5, 0.5);
               List collidingBoundingBoxes = this.worldObj.getCollidingBoundingBoxes(this.theEntity, expand.addCoord(0.0, -1.0, 0.0));
               double d = -1.0;
               expand = expand.offset(0.0, 1.0, 0.0);

               for (AxisAlignedBB axisAlignedBB : (Iterable<AxisAlignedBB>)collidingBoundingBoxes) {
                  d = axisAlignedBB.calculateYOffset(expand, d);
               }

               this.theEntity.getMoveHelper().setMoveTo(position.xCoord, position.yCoord + d, position.zCoord, this.speed);
            }
         }
      }
   }

   public void pathFollow() {
      Vec3 entityPosition = this.getEntityPosition();
      int currentPathLength = this.currentPath.getCurrentPathLength();

      for (int currentPathIndex = this.currentPath.getCurrentPathIndex(); currentPathIndex < this.currentPath.getCurrentPathLength(); currentPathIndex++) {
         if (this.currentPath.getPathPointFromIndex(currentPathIndex).yCoord != (int)entityPosition.yCoord) {
            currentPathLength = currentPathIndex;
            break;
         }
      }

      float f = this.theEntity.width * this.theEntity.width * this.heightRequirement;

      for (int currentPathIndex = this.currentPath.getCurrentPathIndex(); currentPathIndex < currentPathLength; currentPathIndex++) {
         Vec3 vectorFromIndex = this.currentPath.getVectorFromIndex(this.theEntity, currentPathIndex);
         if (entityPosition.squareDistanceTo(vectorFromIndex) < f) {
            this.currentPath.setCurrentPathIndex(currentPathIndex + 1);
         }
      }

      int n = MathHelper.ceiling_float_int(this.theEntity.width);
      int n2 = (int)this.theEntity.height + 1;
      int n3 = n;

      for (int i = currentPathLength - 1; i >= this.currentPath.getCurrentPathIndex(); i--) {
         if (this.isDirectPathBetweenPoints(entityPosition, this.currentPath.getVectorFromIndex(this.theEntity, i), n, n2, n3)) {
            this.currentPath.setCurrentPathIndex(i);
            break;
         }
      }

      this.checkForStuck(entityPosition);
   }

   public void checkForStuck(Vec3 positionVec3) {
      if (this.totalTicks - this.ticksAtLastPos > 100) {
         if (positionVec3.squareDistanceTo(this.lastPosCheck) < 2.25) {
            this.clearPathEntity();
         }

         this.ticksAtLastPos = this.totalTicks;
         this.lastPosCheck = positionVec3;
      }
   }

   public boolean noPath() {
      return this.currentPath == null || this.currentPath.isFinished();
   }

   public void clearPathEntity() {
      this.currentPath = null;
   }

   public abstract Vec3 getEntityPosition();

   public abstract boolean canNavigate();

   public boolean isInLiquid() {
      return this.theEntity.isInWater() || this.theEntity.isInLava();
   }

   public void removeSunnyPath() {
   }

   public abstract boolean isDirectPathBetweenPoints(Vec3 vec3, Vec3 vec32, int n, int n2, int n3);
}
