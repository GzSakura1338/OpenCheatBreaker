package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.util.ResourceLocation;

public class PotionHealthBoost extends Potion {
   public PotionHealthBoost(int potionID, ResourceLocation location, boolean badEffect, int potionColor) {
      super(potionID, location, badEffect, potionColor);
   }

   @Override
   public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, BaseAttributeMap baseAttributeMap, int amplifier) {
      super.removeAttributesModifiersFromEntity(entityLivingBaseIn, baseAttributeMap, amplifier);
      if (entityLivingBaseIn.getHealth() > entityLivingBaseIn.getMaxHealth()) {
         entityLivingBaseIn.setHealth(entityLivingBaseIn.getMaxHealth());
      }
   }
}
