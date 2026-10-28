package net.minecraft.entity.ai;

import net.minecraft.block.Block;
import net.minecraft.entity.EntityLiving;
import net.minecraft.world.EnumDifficulty;

public class EntityAIBreakDoor extends EntityAIDoorInteract {
    public int breakingTime;
    public int prevBreakProgress = -1;

    public EntityAIBreakDoor(EntityLiving entityLiving) {
        super(entityLiving);
    }

    @Override
    public boolean shouldExecute() {
        return super.shouldExecute()
            && this.theEntity.worldObj.getGameRules().getGameRuleBooleanValue("mobGriefing")
            && !this.doorBlock.getFullMetadata3(this.theEntity.worldObj, this.entityPosX, this.entityPosY, this.entityPosZ);
    }

    @Override
    public void startExecuting() {
        super.startExecuting();
        this.breakingTime = 0;
    }

    @Override
    public boolean continueExecuting() {
        double distanceSq = this.theEntity.getDistanceSq(this.entityPosX, this.entityPosY, this.entityPosZ);
        return this.breakingTime <= 240
            && !this.doorBlock.getFullMetadata3(this.theEntity.worldObj, this.entityPosX, this.entityPosY, this.entityPosZ)
            && distanceSq < 4.0;
    }

    @Override
    public void resetTask() {
        super.resetTask();
        this.theEntity.worldObj.destroyBlockInWorldPartially(this.theEntity.getEntityId(), this.entityPosX, this.entityPosY, this.entityPosZ, -1);
    }

    @Override
    public void updateTask() {
        super.updateTask();
        if (this.theEntity.getRNG().nextInt(20) == 0) {
            this.theEntity.worldObj.playAuxSFX(1010, this.entityPosX, this.entityPosY, this.entityPosZ, 0);
        }

        this.breakingTime++;
        int n = (int)(this.breakingTime / 240.0F * 10.0F);
        if (n != this.prevBreakProgress) {
            this.theEntity.worldObj.destroyBlockInWorldPartially(this.theEntity.getEntityId(), this.entityPosX, this.entityPosY, this.entityPosZ, n);
            this.prevBreakProgress = n;
        }

        if (this.breakingTime == 240 && this.theEntity.worldObj.difficultySetting == EnumDifficulty.HARD) {
            this.theEntity.worldObj.setBlockToAir(this.entityPosX, this.entityPosY, this.entityPosZ);
            this.theEntity.worldObj.playAuxSFX(1012, this.entityPosX, this.entityPosY, this.entityPosZ, 0);
            this.theEntity.worldObj.playAuxSFX(2001, this.entityPosX, this.entityPosY, this.entityPosZ, Block.getIdFromBlock(this.doorBlock));
        }
    }
}
