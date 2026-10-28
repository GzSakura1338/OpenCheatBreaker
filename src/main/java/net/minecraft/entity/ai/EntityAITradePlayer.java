package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class EntityAITradePlayer extends EntityAIBase {
    public EntityVillager villager;

    public EntityAITradePlayer(EntityVillager entityVillager) {
        this.villager = entityVillager;
        this.setMutexBits(5);
    }

    @Override
    public boolean shouldExecute() {
        if (!this.villager.isEntityAlive()) {
            return false;
        }

        if (this.villager.isInWater()) {
            return false;
        }

        if (!this.villager.onGround) {
            return false;
        }

        if (this.villager.velocityChanged) {
            return false;
        }

        EntityPlayer customer = this.villager.getCustomer();
        return customer != null && !(this.villager.getDistanceSqToEntity(customer) > 16.0) && customer.openContainer instanceof Container;
    }

    @Override
    public void startExecuting() {
        this.villager.getNavigator().clearPathEntity();
    }

    @Override
    public void resetTask() {
        this.villager.setCustomer(null);
    }
}
