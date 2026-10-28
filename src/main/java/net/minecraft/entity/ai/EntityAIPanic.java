package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.util.Vec3;

public class EntityAIPanic extends EntityAIBase {
    public EntityCreature theEntityCreature;
    public double speed;
    public double randPosX;
    public double randPosY;
    public double randPosZ;

    public EntityAIPanic(EntityCreature entityCreature, double d) {
        this.theEntityCreature = entityCreature;
        this.speed = d;
        this.setMutexBits(1);
    }

    @Override
    public boolean shouldExecute() {
        if (this.theEntityCreature.getAITarget() == null && !this.theEntityCreature.isBurning()) {
            return false;
        }

        Vec3 findRandomTarget = RandomPositionGenerator.findRandomTarget(this.theEntityCreature, 5, 4);
        if (findRandomTarget == null) {
            return false;
        }

        this.randPosX = findRandomTarget.xCoord;
        this.randPosY = findRandomTarget.yCoord;
        this.randPosZ = findRandomTarget.zCoord;
        return true;
    }

    @Override
    public void startExecuting() {
        this.theEntityCreature.getNavigator().tryMoveToXYZ(this.randPosX, this.randPosY, this.randPosZ, this.speed);
    }

    @Override
    public boolean continueExecuting() {
        return !this.theEntityCreature.getNavigator().noPath();
    }
}
