package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RandomPositionGenerator {
   public static Vec3 staticVector = new Vec3(0.0, 0.0, 0.0);

   public static Vec3 findRandomTarget(EntityCreature entitycreatureIn, int xz, int y) {
      return findRandomTargetBlock(entitycreatureIn, xz, y, (Vec3)null);
   }

   public static Vec3 findRandomTargetBlockTowards(EntityCreature entitycreatureIn, int xz, int y, Vec3 targetVec3) {
      staticVector = targetVec3.subtract(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ);
      return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
   }

   public static Vec3 findRandomTargetBlockAwayFrom(EntityCreature entitycreatureIn, int xz, int y, Vec3 targetVec3) {
      staticVector = new Vec3(entitycreatureIn.posX, entitycreatureIn.posY, entitycreatureIn.posZ).subtract(targetVec3);
      return findRandomTargetBlock(entitycreatureIn, xz, y, staticVector);
   }

   public static Vec3 findRandomTargetBlock(EntityCreature entitycreatureIn, int xz, int y, Vec3 targetVec3) {
      Random rNG = entitycreatureIn.getRNG();
      boolean flag = false;
      int n = 0;
      int n2 = 0;
      int n3 = 0;
      float f = -99999.0F;
      boolean flag2;
      if (entitycreatureIn.hasHome()) {
         double d = entitycreatureIn.getHomePosition()
               .distanceSq(
                  MathHelper.floor_double(entitycreatureIn.posX),
                  MathHelper.floor_double(entitycreatureIn.posY),
                  MathHelper.floor_double(entitycreatureIn.posZ)
               )
            + 4.0;
         double d2 = entitycreatureIn.getMaximumHomeDistance() + xz;
         flag2 = d < d2 * d2;
      } else {
         flag2 = false;
      }

      for (int i = 0; i < 10; i++) {
         int n4 = rNG.nextInt(2 * xz + 1) - xz;
         int n5 = rNG.nextInt(2 * y + 1) - y;
         int n6 = rNG.nextInt(2 * xz + 1) - xz;
         if (targetVec3 == null || n4 * targetVec3.xCoord + n6 * targetVec3.zCoord >= 0.0) {
            if (entitycreatureIn.hasHome() && xz > 1) {
               BlockPos homePosition = entitycreatureIn.getHomePosition();
               if (entitycreatureIn.posX > homePosition.getX()) {
                  n4 -= rNG.nextInt(xz / 2);
               } else {
                  n4 += rNG.nextInt(xz / 2);
               }

               if (entitycreatureIn.posZ > homePosition.getZ()) {
                  n6 -= rNG.nextInt(xz / 2);
               } else {
                  n6 += rNG.nextInt(xz / 2);
               }
            }

            n4 += MathHelper.floor_double(entitycreatureIn.posX);
            n5 += MathHelper.floor_double(entitycreatureIn.posY);
            n6 += MathHelper.floor_double(entitycreatureIn.posZ);
            BlockPos blockPos = new BlockPos(n4, n5, n6);
            if (!flag2 || entitycreatureIn.isWithinHomeDistanceFromPosition(blockPos)) {
               float blockPathWeight = entitycreatureIn.getBlockPathWeight(blockPos);
               if (blockPathWeight > f) {
                  f = blockPathWeight;
                  n = n4;
                  n2 = n5;
                  n3 = n6;
                  flag = true;
               }
            }
         }
      }

      return flag ? new Vec3(n, n2, n3) : null;
   }
}
