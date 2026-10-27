package net.minecraft.potion;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.util.ResourceLocation;

public class PotionAttackDamage extends Potion {
   public PotionAttackDamage(int potionID, ResourceLocation location, boolean badEffect, int potionColor) {
      super(potionID, location, badEffect, potionColor);
   }

   @Override
   public double getAttributeModifierAmount(int n, AttributeModifier modifier) {
      return this.id == Potion.weakness.id ? -0.5F * (n + 1) : 1.3 * (n + 1);
   }
}
