package net.minecraft.entity.ai;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAILookAtTradePlayer extends EntityAIWatchClosest {
    public EntityVillager theMerchant;

    public EntityAILookAtTradePlayer(EntityVillager entityVillager) {
        super(entityVillager, EntityPlayer.class, 8.0F);
        this.theMerchant = entityVillager;
    }

    @Override
    public boolean shouldExecute() {
        if (this.theMerchant.isTrading()) {
            this.closestEntity = this.theMerchant.getCustomer();
            return true;
        } else {
            return false;
        }
    }
}
