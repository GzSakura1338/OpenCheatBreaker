package net.minecraft.entity.ai;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EntityCreature;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIMoveThroughVillage extends EntityAIBase {
    public List doorList = new ArrayList();
    public EntityCreature theEntity;
    public PathEntity entityPathNavigate;
    public VillageDoorInfo doorInfo;
    public double movementSpeed;
    public boolean isNocturnal;

    public EntityAIMoveThroughVillage(EntityCreature entityCreature, double d, boolean flag) {
        this.theEntity = entityCreature;
        this.movementSpeed = d;
        this.isNocturnal = flag;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        this.forgetOldTarget();
        if (this.isNocturnal && this.theEntity.worldObj.isDaytime()) {
            return false;
        }

        Village findNearestVillage = this.theEntity
            .worldObj
            .villageCollectionObj
            .findNearestVillage(
                MathHelper.floor_double(this.theEntity.posX), MathHelper.floor_double(this.theEntity.posY), MathHelper.floor_double(this.theEntity.posZ), 0
            );
        if (findNearestVillage == null) {
            return false;
        }

        this.doorInfo = this.findVillageDoorInfo(findNearestVillage);
        if (this.doorInfo == null) {
            return false;
        }

        boolean canBreakDoors = this.theEntity.getNavigator().getCanBreakDoors();
        this.theEntity.getNavigator().setBreakDoors(false);
        this.entityPathNavigate = this.theEntity.getNavigator().getPathToXYZ(this.doorInfo.posX, this.doorInfo.posY, this.doorInfo.posZ);
        this.theEntity.getNavigator().setBreakDoors(canBreakDoors);
        if (this.entityPathNavigate != null) {
            return true;
        }

        Vec3 findRandomTargetBlockTowards = RandomPositionGenerator.findRandomTargetBlockTowards(
            this.theEntity, 10, 7, Vec3.createVectorHelper(this.doorInfo.posX, this.doorInfo.posY, this.doorInfo.posZ)
        );
        if (findRandomTargetBlockTowards == null) {
            return false;
        }

        this.theEntity.getNavigator().setBreakDoors(false);
        this.entityPathNavigate = this.theEntity.getNavigator().getPathToXYZ(findRandomTargetBlockTowards.xCoord, findRandomTargetBlockTowards.yCoord, findRandomTargetBlockTowards.zCoord);
        this.theEntity.getNavigator().setBreakDoors(canBreakDoors);
        return this.entityPathNavigate != null;
    }

    @Override
    public boolean continueExecuting() {
        if (this.theEntity.getNavigator().noPath()) {
            return false;
        }

        float f = this.theEntity.width + 4.0F;
        return this.theEntity.getDistanceSq(this.doorInfo.posX, this.doorInfo.posY, this.doorInfo.posZ) > f * f;
    }

    @Override
    public void startExecuting() {
        this.theEntity.getNavigator().setPath(this.entityPathNavigate, this.movementSpeed);
    }

    @Override
    public void resetTask() {
        if (this.theEntity.getNavigator().noPath() || this.theEntity.getDistanceSq(this.doorInfo.posX, this.doorInfo.posY, this.doorInfo.posZ) < 16.0) {
            this.doorList.add(this.doorInfo);
        }
    }

    public VillageDoorInfo findVillageDoorInfo(Village village) {
        VillageDoorInfo villageDoorInfo = null;
        int n = Integer.MAX_VALUE;

        for (VillageDoorInfo villageDoorInfo2 : (Iterable<VillageDoorInfo>)village.getVillageDoorInfoList()) {
            int distanceSquared = villageDoorInfo2.getDistanceSquared(
                MathHelper.floor_double(this.theEntity.posX), MathHelper.floor_double(this.theEntity.posY), MathHelper.floor_double(this.theEntity.posZ)
            );
            if (distanceSquared < n && !this.checkVillageDoorInfo(villageDoorInfo2)) {
                villageDoorInfo = villageDoorInfo2;
                n = distanceSquared;
            }
        }

        return villageDoorInfo;
    }

    public boolean checkVillageDoorInfo(VillageDoorInfo villageDoorInfo) {
        for (VillageDoorInfo villageDoorInfo2 : (Iterable<VillageDoorInfo>)this.doorList) {
            if (villageDoorInfo.posX == villageDoorInfo2.posX && villageDoorInfo.posY == villageDoorInfo2.posY && villageDoorInfo.posZ == villageDoorInfo2.posZ) {
                return true;
            }
        }

        return false;
    }

    public void forgetOldTarget() {
        if (this.doorList.size() > 15) {
            this.doorList.remove(0);
        }
    }
}
