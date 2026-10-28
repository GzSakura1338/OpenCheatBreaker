package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIMoveIndoors extends EntityAIBase {
    public int insidePosX = -1;
    public VillageDoorInfo doorInfo;
    public int insidePosZ = -1;
    public EntityCreature entityObj;

    public EntityAIMoveIndoors(EntityCreature entityCreature) {
        this.entityObj = entityCreature;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        int n = MathHelper.floor_double(this.entityObj.posX);
        int n2 = MathHelper.floor_double(this.entityObj.posY);
        int n3 = MathHelper.floor_double(this.entityObj.posZ);
        if ((
                !this.entityObj.worldObj.isDaytime()
                    || this.entityObj.worldObj.isRaining()
                    || !this.entityObj.worldObj.getBiomeGenForCoords(n, n3).canSpawnLightningBolt()
            )
            && !this.entityObj.worldObj.provider.hasNoSky) {
            if (this.entityObj.getRNG().nextInt(50) != 0) {
                return false;
            }

            if (this.insidePosX != -1 && this.entityObj.getDistanceSq(this.insidePosX, this.entityObj.posY, this.insidePosZ) < 4.0) {
                return false;
            }

            Village findNearestVillage = this.entityObj.worldObj.villageCollectionObj.findNearestVillage(n, n2, n3, 14);
            if (findNearestVillage == null) {
                return false;
            }

            this.doorInfo = findNearestVillage.findNearestDoorUnrestricted(n, n2, n3);
            return this.doorInfo != null;
        } else {
            return false;
        }
    }

    @Override
    public boolean continueExecuting() {
        return !this.entityObj.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        this.insidePosX = -1;
        if (this.entityObj.getDistanceSq(this.doorInfo.getInsidePosX(), this.doorInfo.posY, this.doorInfo.getInsidePosZ()) > 256.0) {
            Vec3 findRandomTargetBlockTowards = RandomPositionGenerator.findRandomTargetBlockTowards(
                this.entityObj,
                14,
                3,
                Vec3.createVectorHelper(this.doorInfo.getInsidePosX() + 0.5, this.doorInfo.getInsidePosY(), this.doorInfo.getInsidePosZ() + 0.5)
            );
            if (findRandomTargetBlockTowards != null) {
                this.entityObj.getNavigator().tryMoveToXYZ(findRandomTargetBlockTowards.xCoord, findRandomTargetBlockTowards.yCoord, findRandomTargetBlockTowards.zCoord, 1.0);
            }
        } else {
            this.entityObj
                .getNavigator()
                .tryMoveToXYZ(this.doorInfo.getInsidePosX() + 0.5, this.doorInfo.getInsidePosY(), this.doorInfo.getInsidePosZ() + 0.5, 1.0);
        }
    }

    @Override
    public void resetTask() {
        this.insidePosX = this.doorInfo.getInsidePosX();
        this.insidePosZ = this.doorInfo.getInsidePosZ();
        this.doorInfo = null;
    }
}
