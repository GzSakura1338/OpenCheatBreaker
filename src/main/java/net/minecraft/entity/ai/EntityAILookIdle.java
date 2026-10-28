package net.minecraft.entity.ai;

import net.minecraft.entity.EntityLiving;

public class EntityAILookIdle extends EntityAIBase {
    public EntityLiving idleEntity;
    public double lookX;
    public double lookZ;
    public int idleTime;

    public EntityAILookIdle(EntityLiving entityLiving) {
        this.idleEntity = entityLiving;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        return this.idleEntity.getRNG().nextFloat() < 0.02F;
    }

    @Override
    public boolean continueExecuting() {
        return this.idleTime >= 0;
    }

    @Override
    public void startExecuting() {
        double d = (Math.PI * 2) * this.idleEntity.getRNG().nextDouble();
        this.lookX = Math.cos(d);
        this.lookZ = Math.sin(d);
        this.idleTime = 20 + this.idleEntity.getRNG().nextInt(20);
    }

    @Override
    public void updateTask() {
        this.idleTime--;
        this.idleEntity
            .getLookHelper()
            .setLookPosition(
                this.idleEntity.posX + this.lookX,
                this.idleEntity.posY + this.idleEntity.getEyeHeight(),
                this.idleEntity.posZ + this.lookZ,
                10.0F,
                this.idleEntity.getVerticalFaceSpeed()
            );
    }
}
