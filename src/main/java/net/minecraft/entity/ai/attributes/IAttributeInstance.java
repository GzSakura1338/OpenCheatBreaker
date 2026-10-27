package net.minecraft.entity.ai.attributes;

import java.util.Collection;
import java.util.UUID;

public interface IAttributeInstance {
   IAttribute getAttribute();

   double getBaseValue();

   void setBaseValue(double d);

   Collection<AttributeModifier> getModifiersByOperation(int n);

   Collection<AttributeModifier> getCollection();

   boolean hasModifier(AttributeModifier attributeModifier);

   AttributeModifier getModifier(UUID uuid);

   void applyModifier(AttributeModifier attributeModifier);

   void removeModifier(AttributeModifier attributeModifier);

   void removeAllModifiers();

   double getAttributeValue();
}
