package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;

public class PotionAbsoption extends Potion {
    public PotionAbsoption(int n, boolean flag, int n2) {
        super(n, flag, n2);
    }

    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBase, BaseAttributeMap baseAttributeMap, int n) {
        entityLivingBase.setAbsorptionAmount(entityLivingBase.getAbsorptionAmount() - 4 * (n + 1));
        super.removeAttributesModifiersFromEntity(entityLivingBase, baseAttributeMap, n);
    }

    @Override
    public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBase, BaseAttributeMap baseAttributeMap, int n) {
        entityLivingBase.setAbsorptionAmount(entityLivingBase.getAbsorptionAmount() + 4 * (n + 1));
        super.applyAttributesModifiersToEntity(entityLivingBase, baseAttributeMap, n);
    }
}
