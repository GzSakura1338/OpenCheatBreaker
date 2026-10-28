package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RandomPositionGenerator {
    public static Vec3 staticVector = Vec3.createVectorHelper(0.0, 0.0, 0.0);

    public static Vec3 findRandomTarget(EntityCreature entityCreature, int n, int n2) {
        return findRandomTargetBlock(entityCreature, n, n2, null);
    }

    public static Vec3 findRandomTargetBlockTowards(EntityCreature entityCreature, int n, int n2, Vec3 vec3) {
        staticVector.xCoord = vec3.xCoord - entityCreature.posX;
        staticVector.yCoord = vec3.yCoord - entityCreature.posY;
        staticVector.zCoord = vec3.zCoord - entityCreature.posZ;
        return findRandomTargetBlock(entityCreature, n, n2, staticVector);
    }

    public static Vec3 findRandomTargetBlockAwayFrom(EntityCreature entityCreature, int n, int n2, Vec3 vec3) {
        staticVector.xCoord = entityCreature.posX - vec3.xCoord;
        staticVector.yCoord = entityCreature.posY - vec3.yCoord;
        staticVector.zCoord = entityCreature.posZ - vec3.zCoord;
        return findRandomTargetBlock(entityCreature, n, n2, staticVector);
    }

    public static Vec3 findRandomTargetBlock(EntityCreature entityCreature, int n, int n2, Vec3 vec3) {
        Random rNG = entityCreature.getRNG();
        boolean flag = false;
        int n3 = 0;
        int n4 = 0;
        int n5 = 0;
        float f = -99999.0F;
        boolean flag2;
        if (entityCreature.hasHome()) {
            double d = entityCreature.getHomePosition()
                    .getDistanceSquared(MathHelper.floor_double(entityCreature.posX), MathHelper.floor_double(entityCreature.posY), MathHelper.floor_double(entityCreature.posZ))
                + 4.0F;
            double d2 = entityCreature.getMaximumHomeDistance() + n;
            flag2 = d < d2 * d2;
        } else {
            flag2 = false;
        }

        for (int i = 0; i < 10; i++) {
            int n6 = rNG.nextInt(2 * n) - n;
            int n7 = rNG.nextInt(2 * n2) - n2;
            int n8 = rNG.nextInt(2 * n) - n;
            if (vec3 == null || n6 * vec3.xCoord + n8 * vec3.zCoord >= 0.0) {
                n6 += MathHelper.floor_double(entityCreature.posX);
                n7 += MathHelper.floor_double(entityCreature.posY);
                n8 += MathHelper.floor_double(entityCreature.posZ);
                if (!flag2 || entityCreature.isWithinHomeDistance(n6, n7, n8)) {
                    float blockPathWeight = entityCreature.getBlockPathWeight(n6, n7, n8);
                    if (blockPathWeight > f) {
                        f = blockPathWeight;
                        n3 = n6;
                        n4 = n7;
                        n5 = n8;
                        flag = true;
                    }
                }
            }
        }

        return flag ? Vec3.createVectorHelper(n3, n4, n5) : null;
    }
}
