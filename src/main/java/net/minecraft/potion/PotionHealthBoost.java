package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;

public class PotionHealthBoost extends Potion {
    public PotionHealthBoost(int n, boolean flag, int n2) {
        super(n, flag, n2);
    }

    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBase, BaseAttributeMap baseAttributeMap, int n) {
        super.removeAttributesModifiersFromEntity(entityLivingBase, baseAttributeMap, n);
        if (entityLivingBase.getHealth() > entityLivingBase.getMaxHealth()) {
            entityLivingBase.setHealth(entityLivingBase.getMaxHealth());
        }
    }
}
