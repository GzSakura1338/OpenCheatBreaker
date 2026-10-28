package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.village.VillageDoorInfo;

public class EntityAIRestrictOpenDoor extends EntityAIBase {
    public EntityCreature entityObj;
    public VillageDoorInfo frontDoor;

    public EntityAIRestrictOpenDoor(EntityCreature entityCreature) {
        this.entityObj = entityCreature;
    }

    @Override
    public boolean shouldExecute() {
        if (this.entityObj.worldObj.isDaytime()) {
            return false;
        }

        Village findNearestVillage = this.entityObj
            .worldObj
            .villageCollectionObj
            .findNearestVillage(
                MathHelper.floor_double(this.entityObj.posX), MathHelper.floor_double(this.entityObj.posY), MathHelper.floor_double(this.entityObj.posZ), 16
            );
        if (findNearestVillage == null) {
            return false;
        }

        this.frontDoor = findNearestVillage.findNearestDoor(
            MathHelper.floor_double(this.entityObj.posX), MathHelper.floor_double(this.entityObj.posY), MathHelper.floor_double(this.entityObj.posZ)
        );
        return this.frontDoor != null
            && this.frontDoor
                    .getInsideDistanceSquare(
                        MathHelper.floor_double(this.entityObj.posX), MathHelper.floor_double(this.entityObj.posY), MathHelper.floor_double(this.entityObj.posZ)
                    )
                < 2.25;
    }

    @Override
    public boolean continueExecuting() {
        return !this.entityObj.worldObj.isDaytime()
            && !this.frontDoor.isDetachedFromVillageFlag
            && this.frontDoor.isInside(MathHelper.floor_double(this.entityObj.posX), MathHelper.floor_double(this.entityObj.posZ));
    }

    @Override
    public void startExecuting() {
        this.entityObj.getNavigator().setBreakDoors(false);
        this.entityObj.getNavigator().setEnterDoors(false);
    }

    @Override
    public void resetTask() {
        this.entityObj.getNavigator().setBreakDoors(true);
        this.entityObj.getNavigator().setEnterDoors(true);
        this.frontDoor = null;
    }

    @Override
    public void updateTask() {
        this.frontDoor.incrementDoorOpeningRestrictionCounter();
    }
}
