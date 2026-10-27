package net.minecraft.pathfinding;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.pathfinder.WalkNodeProcessor;

public class PathNavigateGround extends PathNavigate {
   public WalkNodeProcessor nodeProcessor;
   public boolean shouldAvoidSun;

   public PathNavigateGround(EntityLiving entitylivingIn, World worldIn) {
      super(entitylivingIn, worldIn);
   }

   @Override
   public PathFinder getPathFinder() {
      this.nodeProcessor = new WalkNodeProcessor();
      this.nodeProcessor.setEnterDoors(true);
      return new PathFinder(this.nodeProcessor);
   }

   @Override
   public boolean canNavigate() {
      return this.theEntity.onGround
         || this.getCanSwim() && this.isInLiquid()
         || this.theEntity.isRiding() && this.theEntity instanceof EntityZombie && this.theEntity.ridingEntity instanceof EntityChicken;
   }

   @Override
   public Vec3 getEntityPosition() {
      return new Vec3(this.theEntity.posX, this.getPathablePosY(), this.theEntity.posZ);
   }

   public int getPathablePosY() {
      if (this.theEntity.isInWater() && this.getCanSwim()) {
         int minY = (int)this.theEntity.getEntityBoundingBox().minY;
         Block block = this.worldObj
            .getBlockState(new BlockPos(MathHelper.floor_double(this.theEntity.posX), minY, MathHelper.floor_double(this.theEntity.posZ)))
            .getBlock();
         int n = 0;

         while (block == Blocks.flowing_water || block == Blocks.water) {
            block = this.worldObj
               .getBlockState(new BlockPos(MathHelper.floor_double(this.theEntity.posX), ++minY, MathHelper.floor_double(this.theEntity.posZ)))
               .getBlock();
            if (++n > 16) {
               return (int)this.theEntity.getEntityBoundingBox().minY;
            }
         }

         return minY;
      } else {
         return (int)(this.theEntity.getEntityBoundingBox().minY + 0.5);
      }
   }

   @Override
   public void removeSunnyPath() {
      super.removeSunnyPath();
      if (this.shouldAvoidSun) {
         if (this.worldObj
            .canSeeSky(
               new BlockPos(
                  MathHelper.floor_double(this.theEntity.posX),
                  (int)(this.theEntity.getEntityBoundingBox().minY + 0.5),
                  MathHelper.floor_double(this.theEntity.posZ)
               )
            )) {
            return;
         }

         for (int i = 0; i < this.currentPath.getCurrentPathLength(); i++) {
            PathPoint pathPointFromIndex = this.currentPath.getPathPointFromIndex(i);
            if (this.worldObj.canSeeSky(new BlockPos(pathPointFromIndex.xCoord, pathPointFromIndex.yCoord, pathPointFromIndex.zCoord))) {
               this.currentPath.setCurrentPathLength(i - 1);
               return;
            }
         }
      }
   }

   @Override
   public boolean isDirectPathBetweenPoints(Vec3 posVec31, Vec3 posVec32, int sizeX, int sizeY, int sizeZ) {
      int n = MathHelper.floor_double(posVec31.xCoord);
      int n2 = MathHelper.floor_double(posVec31.zCoord);
      double d = posVec32.xCoord - posVec31.xCoord;
      double d2 = posVec32.zCoord - posVec31.zCoord;
      double d3 = d * d + d2 * d2;
      if (d3 < 1.0E-8) {
         return false;
      }

      double d4 = 1.0 / Math.sqrt(d3);
      d *= d4;
      d2 *= d4;
      sizeX += 2;
      sizeZ += 2;
      if (!this.isSafeToStandAt(n, (int)posVec31.yCoord, n2, sizeX, sizeY, sizeZ, posVec31, d, d2)) {
         return false;
      }

      sizeX -= 2;
      sizeZ -= 2;
      double d5 = 1.0 / Math.abs(d);
      double d6 = 1.0 / Math.abs(d2);
      double d7 = n * 1 - posVec31.xCoord;
      double d8 = n2 * 1 - posVec31.zCoord;
      if (d >= 0.0) {
         d7++;
      }

      if (d2 >= 0.0) {
         d8++;
      }

      d7 /= d;
      d8 /= d2;
      int n3 = d < 0.0 ? -1 : 1;
      int n4 = d2 < 0.0 ? -1 : 1;
      int n5 = MathHelper.floor_double(posVec32.xCoord);
      int n6 = MathHelper.floor_double(posVec32.zCoord);
      int n7 = n5 - n;
      int n8 = n6 - n2;

      while (n7 * n3 > 0 || n8 * n4 > 0) {
         if (d7 < d8) {
            d7 += d5;
            n += n3;
            n7 = n5 - n;
         } else {
            d8 += d6;
            n2 += n4;
            n8 = n6 - n2;
         }

         if (!this.isSafeToStandAt(n, (int)posVec31.yCoord, n2, sizeX, sizeY, sizeZ, posVec31, d, d2)) {
            return false;
         }
      }

      return true;
   }

   public boolean isSafeToStandAt(int x, int y, int z, int sizeX, int sizeY, int sizeZ, Vec3 vec31, double d, double d2) {
      int n = x - sizeX / 2;
      int n2 = z - sizeZ / 2;
      if (!this.isPositionClear(n, y, n2, sizeX, sizeY, sizeZ, vec31, d, d2)) {
         return false;
      }

      for (int i = n; i < n + sizeX; i++) {
         for (int j = n2; j < n2 + sizeZ; j++) {
            double d3 = i + 0.5 - vec31.xCoord;
            double d4 = j + 0.5 - vec31.zCoord;
            if (d3 * d + d4 * d2 >= 0.0) {
               Block block = this.worldObj.getBlockState(new BlockPos(i, y - 1, j)).getBlock();
               Material material = block.getMaterial();
               if (material == Material.air) {
                  return false;
               }

               if (material == Material.water && !this.theEntity.isInWater()) {
                  return false;
               }

               if (material == Material.lava) {
                  return false;
               }
            }
         }
      }

      return true;
   }

   public boolean isPositionClear(int n, int n2, int n3, int n4, int n5, int n6, Vec3 vec3, double d, double d2) {
      for (BlockPos blockPos : BlockPos.getAllInBox(new BlockPos(n, n2, n3), new BlockPos(n + n4 - 1, n2 + n5 - 1, n3 + n6 - 1))) {
         double d3 = blockPos.getX() + 0.5 - vec3.xCoord;
         double d4 = blockPos.getZ() + 0.5 - vec3.zCoord;
         if (d3 * d + d4 * d2 >= 0.0) {
            Block block = this.worldObj.getBlockState(blockPos).getBlock();
            if (!block.isPassable(this.worldObj, blockPos)) {
               return false;
            }
         }
      }

      return true;
   }

   public void setAvoidsWater(boolean avoidsWater) {
      this.nodeProcessor.setAvoidsWater(avoidsWater);
   }

   public boolean getAvoidsWater() {
      return this.nodeProcessor.getAvoidsWater();
   }

   public void setBreakDoors(boolean canBreakDoors) {
      this.nodeProcessor.setBreakDoors(canBreakDoors);
   }

   public void setEnterDoors(boolean par1) {
      this.nodeProcessor.setEnterDoors(par1);
   }

   public boolean getEnterDoors() {
      return this.nodeProcessor.getEnterDoors();
   }

   public void setCanSwim(boolean canSwim) {
      this.nodeProcessor.setCanSwim(canSwim);
   }

   public boolean getCanSwim() {
      return this.nodeProcessor.getCanSwim();
   }

   public void setAvoidSun(boolean par1) {
      this.shouldAvoidSun = par1;
   }
}
