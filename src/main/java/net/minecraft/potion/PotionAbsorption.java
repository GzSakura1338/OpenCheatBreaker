package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.util.ResourceLocation;

public class PotionAbsorption extends Potion {
   public PotionAbsorption(int potionID, ResourceLocation location, boolean badEffect, int potionColor) {
      super(potionID, location, badEffect, potionColor);
   }

   @Override
   public void removeAttributesModifiersFromEntity(EntityLivingBase entityLivingBaseIn, BaseAttributeMap baseAttributeMap, int amplifier) {
      entityLivingBaseIn.setAbsorptionAmount(entityLivingBaseIn.getAbsorptionAmount() - 4 * (amplifier + 1));
      super.removeAttributesModifiersFromEntity(entityLivingBaseIn, baseAttributeMap, amplifier);
   }

   @Override
   public void applyAttributesModifiersToEntity(EntityLivingBase entityLivingBaseIn, BaseAttributeMap baseAttributeMap, int amplifier) {
      entityLivingBaseIn.setAbsorptionAmount(entityLivingBaseIn.getAbsorptionAmount() + 4 * (amplifier + 1));
      super.applyAttributesModifiersToEntity(entityLivingBaseIn, baseAttributeMap, amplifier);
   }
}
