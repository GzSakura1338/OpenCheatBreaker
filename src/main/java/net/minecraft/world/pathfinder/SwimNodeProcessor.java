package net.minecraft.world.pathfinder;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.BlockPos.MutableBlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;

public class SwimNodeProcessor extends NodeProcessor {
   @Override
   public void initProcessor(IBlockAccess iblockaccessIn, Entity entityIn) {
      super.initProcessor(iblockaccessIn, entityIn);
   }

   @Override
   public void postProcess() {
      super.postProcess();
   }

   @Override
   public PathPoint getPathPointTo(Entity entityIn) {
      return this.openPoint(
         MathHelper.floor_double(entityIn.getEntityBoundingBox().minX),
         MathHelper.floor_double(entityIn.getEntityBoundingBox().minY + 0.5),
         MathHelper.floor_double(entityIn.getEntityBoundingBox().minZ)
      );
   }

   @Override
   public PathPoint getPathPointToCoords(Entity entityIn, double x, double y, double target) {
      return this.openPoint(
         MathHelper.floor_double(x - entityIn.width / 2.0F), MathHelper.floor_double(y + 0.5), MathHelper.floor_double(target - entityIn.width / 2.0F)
      );
   }

   @Override
   public int findPathOptions(PathPoint[] pathOptions, Entity entityIn, PathPoint currentPoint, PathPoint targetPoint, float maxDistance) {
      int n = 0;

      for (EnumFacing enumFacing : EnumFacing.values()) {
         PathPoint safePoint = this.getSafePoint(
            entityIn,
            currentPoint.xCoord + enumFacing.getFrontOffsetX(),
            currentPoint.yCoord + enumFacing.getFrontOffsetY(),
            currentPoint.zCoord + enumFacing.getFrontOffsetZ()
         );
         if (safePoint != null && !safePoint.visited && safePoint.distanceTo(targetPoint) < maxDistance) {
            pathOptions[n++] = safePoint;
         }
      }

      return n;
   }

   public PathPoint getSafePoint(Entity entityIn, int x, int y, int z) {
      int nodeType = this.getNodeType(entityIn, x, y, z);
      return nodeType == -1 ? this.openPoint(x, y, z) : null;
   }

   public int getNodeType(Entity entityIn, int x, int y, int z) {
      MutableBlockPos mutableBlockPos = new MutableBlockPos();

      for (int i = x; i < x + this.entitySizeX; i++) {
         for (int j = y; j < y + this.entitySizeY; j++) {
            for (int k = z; k < z + this.entitySizeZ; k++) {
               Block block = this.blockaccess.getBlockState(mutableBlockPos.set(i, j, k)).getBlock();
               if (block.getMaterial() != Material.water) {
                  return 0;
               }
            }
         }
      }

      return -1;
   }
}
