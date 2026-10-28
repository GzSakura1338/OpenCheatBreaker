package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.Vec3;

public class EntityAIWander extends EntityAIBase {
    public EntityCreature entity;
    public double xPosition;
    public double yPosition;
    public double zPosition;
    public double speed;

    public EntityAIWander(EntityCreature entityCreature, double d) {
        this.entity = entityCreature;
        this.speed = d;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (this.entity.getAge() >= 100) {
            return false;
        }

        if (this.entity.getRNG().nextInt(120) != 0) {
            return false;
        }

        Vec3 findRandomTarget = RandomPositionGenerator.findRandomTarget(this.entity, 10, 7);
        if (findRandomTarget == null) {
            return false;
        }

        this.xPosition = findRandomTarget.xCoord;
        this.yPosition = findRandomTarget.yCoord;
        this.zPosition = findRandomTarget.zCoord;
        return true;
    }

    @Override
    public boolean continueExecuting() {
        return !this.entity.getNavigator().noPath();
    }

    @Override
    public void startExecuting() {
        this.entity.getNavigator().tryMoveToXYZ(this.xPosition, this.yPosition, this.zPosition, this.speed);
    }
}
