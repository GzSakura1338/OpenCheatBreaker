package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityAIBeg extends EntityAIBase {
    public EntityWolf theWolf;
    public EntityPlayer thePlayer;
    public World worldObject;
    public float minPlayerDistance;
    public int timer;

    public EntityAIBeg(EntityWolf entityWolf, float f) {
        this.theWolf = entityWolf;
        this.worldObject = entityWolf.worldObj;
        this.minPlayerDistance = f;
        this.setMutexBits(2);
    }

    @Override
    public boolean shouldExecute() {
        this.thePlayer = this.worldObject.getClosestPlayerToEntity(this.theWolf, this.minPlayerDistance);
        return this.thePlayer != null && this.hasPlayerGotBoneInHand(this.thePlayer);
    }

    @Override
    public boolean continueExecuting() {
        return this.thePlayer.isEntityAlive()
            && !(this.theWolf.getDistanceSqToEntity(this.thePlayer) > this.minPlayerDistance * this.minPlayerDistance)
            && this.timer > 0
            && this.hasPlayerGotBoneInHand(this.thePlayer);
    }

    @Override
    public void startExecuting() {
        this.theWolf.setBegging(true);
        this.timer = 40 + this.theWolf.getRNG().nextInt(40);
    }

    @Override
    public void resetTask() {
        this.theWolf.setBegging(false);
        this.thePlayer = null;
    }

    @Override
    public void updateTask() {
        this.theWolf
            .getLookHelper()
            .setLookPosition(
                this.thePlayer.posX, this.thePlayer.posY + this.thePlayer.getEyeHeight(), this.thePlayer.posZ, 10.0F, this.theWolf.getVerticalFaceSpeed()
            );
        this.timer--;
    }

    public boolean hasPlayerGotBoneInHand(EntityPlayer entityPlayer) {
        ItemStack currentItem = entityPlayer.inventory.getCurrentItem();
        return currentItem != null && (!this.theWolf.isTamed() && currentItem.getItem() == Items.bone || this.theWolf.isBreedingItem(currentItem));
    }
}
