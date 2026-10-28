package net.minecraft.potion;

import net.minecraft.entity.ai.attributes.AttributeModifier;

public class PotionAttackDamage extends Potion {
    public PotionAttackDamage(int n, boolean flag, int n2) {
        super(n, flag, n2);
    }

    @Override
    public double computeDouble(int n, AttributeModifier attributeModifier) {
        return this.id == Potion.weakness.id ? -0.5F * (n + 1) : 1.3 * (n + 1);
    }
}
