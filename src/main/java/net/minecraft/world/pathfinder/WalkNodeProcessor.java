package net.minecraft.world.pathfinder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockFenceGate;
import net.minecraft.block.BlockRailBase;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class WalkNodeProcessor extends NodeProcessor {
   public boolean canEnterDoors;
   public boolean canBreakDoors;
   public boolean avoidsWater;
   public boolean canSwim;
   public boolean shouldAvoidWater;

   @Override
   public void initProcessor(IBlockAccess iblockaccessIn, Entity entityIn) {
      super.initProcessor(iblockaccessIn, entityIn);
      this.shouldAvoidWater = this.avoidsWater;
   }

   @Override
   public void postProcess() {
      super.postProcess();
      this.avoidsWater = this.shouldAvoidWater;
   }

   @Override
   public PathPoint getPathPointTo(Entity entityIn) {
      int n;
      if (this.canSwim && entityIn.isInWater()) {
         n = (int)entityIn.getEntityBoundingBox().minY;
         MutableBlockPos mutableBlockPos = new MutableBlockPos(MathHelper.floor_double(entityIn.posX), n, MathHelper.floor_double(entityIn.posZ));

         for (Block block = this.blockaccess.getBlockState(mutableBlockPos).getBlock();
            block == Blocks.flowing_water || block == Blocks.water;
            block = this.blockaccess.getBlockState(mutableBlockPos).getBlock()
         ) {
            mutableBlockPos.set(MathHelper.floor_double(entityIn.posX), ++n, MathHelper.floor_double(entityIn.posZ));
         }

         this.avoidsWater = false;
      } else {
         n = MathHelper.floor_double(entityIn.getEntityBoundingBox().minY + 0.5);
      }

      return this.openPoint(MathHelper.floor_double(entityIn.getEntityBoundingBox().minX), n, MathHelper.floor_double(entityIn.getEntityBoundingBox().minZ));
   }

   @Override
   public PathPoint getPathPointToCoords(Entity entityIn, double x, double y, double target) {
      return this.openPoint(
         MathHelper.floor_double(x - entityIn.width / 2.0F), MathHelper.floor_double(y), MathHelper.floor_double(target - entityIn.width / 2.0F)
      );
   }

   @Override
   public int findPathOptions(PathPoint[] pathOptions, Entity entityIn, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
      int n = 0;
      byte b = 0;
      if (this.getVerticalOffset(entityIn, currentPoint.xCoord, currentPoint.yCoord + 1, currentPoint.zCoord) == 1) {
         b = 1;
      }

      PathPoint safePoint = this.getSafePoint(entityIn, currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord + 1, b);
      PathPoint safePoint2 = this.getSafePoint(entityIn, currentPoint.xCoord - 1, currentPoint.yCoord, currentPoint.zCoord, b);
      PathPoint safePoint3 = this.getSafePoint(entityIn, currentPoint.xCoord + 1, currentPoint.yCoord, currentPoint.zCoord, b);
      PathPoint safePoint4 = this.getSafePoint(entityIn, currentPoint.xCoord, currentPoint.yCoord, currentPoint.zCoord - 1, b);
      if (safePoint != null && !safePoint.visited && safePoint.distanceTo(targetPoint) < maxDistance) {
         pathOptions[n++] = safePoint;
      }

      if (safePoint2 != null && !safePoint2.visited && safePoint2.distanceTo(targetPoint) < maxDistance) {
         pathOptions[n++] = safePoint2;
      }

      if (safePoint3 != null && !safePoint3.visited && safePoint3.distanceTo(targetPoint) < maxDistance) {
         pathOptions[n++] = safePoint3;
      }

      if (safePoint4 != null && !safePoint4.visited && safePoint4.distanceTo(targetPoint) < maxDistance) {
         pathOptions[n++] = safePoint4;
      }

      return n;
   }

   public PathPoint getSafePoint(Entity entityIn, int x, int y, int z, int n) {
      PathPoint pathPoint = null;
      int verticalOffset = this.getVerticalOffset(entityIn, x, y, z);
      if (verticalOffset == 2) {
         return this.openPoint(x, y, z);
      }

      if (verticalOffset == 1) {
         pathPoint = this.openPoint(x, y, z);
      }

      if (pathPoint == null && n > 0 && verticalOffset != -3 && verticalOffset != -4 && this.getVerticalOffset(entityIn, x, y + n, z) == 1) {
         pathPoint = this.openPoint(x, y + n, z);
         y += n;
      }

      if (pathPoint != null) {
         int n2 = 0;
         int n3 = 0;

         while (y > 0) {
            n3 = this.getVerticalOffset(entityIn, x, y - 1, z);
            if (this.avoidsWater && n3 == -1) {
               return null;
            }

            if (n3 != 1) {
               break;
            }

            if (n2++ >= entityIn.getMaxFallHeight()) {
               return null;
            }

            if (--y <= 0) {
               return null;
            }

            pathPoint = this.openPoint(x, y, z);
         }

         if (n3 == -2) {
            return null;
         }
      }

      return pathPoint;
   }

   public int getVerticalOffset(Entity entityIn, int x, int y, int z) {
      return getNodeType(
         this.blockaccess, entityIn, x, y, z, this.entitySizeX, this.entitySizeY, this.entitySizeZ, this.avoidsWater, this.canBreakDoors, this.canEnterDoors
      );
   }

   public static int getNodeType(
      IBlockAccess blockaccessIn,
      Entity entityIn,
      int x,
      int y,
      int z,
      int sizeX,
      int sizeY,
      int sizeZ,
      boolean avoidWater,
      boolean breakDoors,
      boolean enterDoors
   ) {
      boolean flag = false;
      BlockPos blockPos = new BlockPos(entityIn);
      MutableBlockPos mutableBlockPos = new MutableBlockPos();

      for (int i = x; i < x + sizeX; i++) {
         for (int j = y; j < y + sizeY; j++) {
            for (int k = z; k < z + sizeZ; k++) {
               mutableBlockPos.set(i, j, k);
               Block block = blockaccessIn.getBlockState(mutableBlockPos).getBlock();
               if (block.getMaterial() != Material.air) {
                  if (block == Blocks.trapdoor || block == Blocks.iron_trapdoor) {
                     flag = true;
                  } else if (block != Blocks.flowing_water && block != Blocks.water) {
                     if (!enterDoors && block instanceof BlockDoor && block.getMaterial() == Material.wood) {
                        return 0;
                     }
                  } else {
                     if (avoidWater) {
                        return -1;
                     }

                     flag = true;
                  }

                  if (entityIn.worldObj.getBlockState(mutableBlockPos).getBlock() instanceof BlockRailBase) {
                     if (!(entityIn.worldObj.getBlockState(blockPos).getBlock() instanceof BlockRailBase)
                        && !(entityIn.worldObj.getBlockState(blockPos.down()).getBlock() instanceof BlockRailBase)) {
                        return -3;
                     }
                  } else if (!block.isPassable(blockaccessIn, mutableBlockPos) && (!breakDoors || !(block instanceof BlockDoor) || block.getMaterial() != Material.wood)) {
                     if (block instanceof BlockFence || block instanceof BlockFenceGate || block instanceof BlockWall) {
                        return -3;
                     }

                     if (block == Blocks.trapdoor || block == Blocks.iron_trapdoor) {
                        return -4;
                     }

                     Material material = block.getMaterial();
                     if (material != Material.lava) {
                        return 0;
                     }

                     if (!entityIn.isInLava()) {
                        return -2;
                     }
                  }
               }
            }
         }
      }

      return flag ? 2 : 1;
   }

   public void setEnterDoors(boolean canEnterDoorsIn) {
      this.canEnterDoors = canEnterDoorsIn;
   }

   public void setBreakDoors(boolean canBreakDoorsIn) {
      this.canBreakDoors = canBreakDoorsIn;
   }

   public void setAvoidsWater(boolean avoidsWaterIn) {
      this.avoidsWater = avoidsWaterIn;
   }

   public void setCanSwim(boolean canSwimIn) {
      this.canSwim = canSwimIn;
   }

   public boolean getEnterDoors() {
      return this.canEnterDoors;
   }

   public boolean getCanSwim() {
      return this.canSwim;
   }

   public boolean getAvoidsWater() {
      return this.avoidsWater;
   }
}
