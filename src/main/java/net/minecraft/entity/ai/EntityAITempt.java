package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class EntityAITempt extends EntityAIBase {
    public EntityCreature temptedEntity;
    public double speed;
    public double targetX;
    public double targetY;
    public double targetZ;
    public double lastPlayerPitch;
    public double lastPlayerYaw;
    public EntityPlayer temptingPlayer;
    public int delayTemptCounter;
    public boolean isRunning;
    public Item food;
    public boolean scaredByPlayerMovement;
    public boolean flag;

    public EntityAITempt(EntityCreature entityCreature, double d, Item item, boolean flag) {
        this.temptedEntity = entityCreature;
        this.speed = d;
        this.food = item;
        this.scaredByPlayerMovement = flag;
        this.setMutexBits(3);
    }

    @Override
    public boolean shouldExecute() {
        if (this.delayTemptCounter > 0) {
            this.delayTemptCounter--;
            return false;
        }

        this.temptingPlayer = this.temptedEntity.worldObj.getClosestPlayerToEntity(this.temptedEntity, 10.0);
        if (this.temptingPlayer == null) {
            return false;
        }

        ItemStack currentEquippedItem = this.temptingPlayer.getCurrentEquippedItem();
        return currentEquippedItem != null && currentEquippedItem.getItem() == this.food;
    }

    @Override
    public boolean continueExecuting() {
        if (this.scaredByPlayerMovement) {
            if (this.temptedEntity.getDistanceSqToEntity(this.temptingPlayer) < 36.0) {
                if (this.temptingPlayer.getDistanceSq(this.targetX, this.targetY, this.targetZ) > 0.010000000000000002) {
                    return false;
                }

                if (Math.abs(this.temptingPlayer.rotationPitch - this.lastPlayerPitch) > 5.0
                    || Math.abs(this.temptingPlayer.rotationYaw - this.lastPlayerYaw) > 5.0) {
                    return false;
                }
            } else {
                this.targetX = this.temptingPlayer.posX;
                this.targetY = this.temptingPlayer.posY;
                this.targetZ = this.temptingPlayer.posZ;
            }

            this.lastPlayerPitch = this.temptingPlayer.rotationPitch;
            this.lastPlayerYaw = this.temptingPlayer.rotationYaw;
        }

        return this.shouldExecute();
    }

    @Override
    public void startExecuting() {
        this.targetX = this.temptingPlayer.posX;
        this.targetY = this.temptingPlayer.posY;
        this.targetZ = this.temptingPlayer.posZ;
        this.isRunning = true;
        this.flag = this.temptedEntity.getNavigator().getAvoidsWater();
        this.temptedEntity.getNavigator().setAvoidsWater(false);
    }

    @Override
    public void resetTask() {
        this.temptingPlayer = null;
        this.temptedEntity.getNavigator().clearPathEntity();
        this.delayTemptCounter = 100;
        this.isRunning = false;
        this.temptedEntity.getNavigator().setAvoidsWater(this.flag);
    }

    @Override
    public void updateTask() {
        this.temptedEntity.getLookHelper().setLookPositionWithEntity(this.temptingPlayer, 30.0F, this.temptedEntity.getVerticalFaceSpeed());
        if (this.temptedEntity.getDistanceSqToEntity(this.temptingPlayer) < 6.25) {
            this.temptedEntity.getNavigator().clearPathEntity();
        } else {
            this.temptedEntity.getNavigator().tryMoveToEntityLiving(this.temptingPlayer, this.speed);
        }
    }

    public boolean isRunning() {
        return this.isRunning;
    }
}
