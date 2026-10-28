package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityAIOpenDoor extends EntityAIDoorInteract {
    public boolean closeDoor;
    public int closeDoorTemporisation;

    public EntityAIOpenDoor(EntityLiving entityLiving, boolean flag) {
        super(entityLiving);
        this.theEntity = entityLiving;
        this.closeDoor = flag;
    }

    @Override
    public boolean continueExecuting() {
        return this.closeDoor && this.closeDoorTemporisation > 0 && super.continueExecuting();
    }

    @Override
    public void startExecuting() {
        this.closeDoorTemporisation = 20;
        this.doorBlock.handleWorld(this.theEntity.worldObj, this.entityPosX, this.entityPosY, this.entityPosZ, true);
    }

    @Override
    public void resetTask() {
        if (this.closeDoor) {
            this.doorBlock.handleWorld(this.theEntity.worldObj, this.entityPosX, this.entityPosY, this.entityPosZ, false);
        }
    }

    @Override
    public void updateTask() {
        this.closeDoorTemporisation--;
        super.updateTask();
    }
}
