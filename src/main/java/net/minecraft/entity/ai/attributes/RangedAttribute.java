package net.minecraft.entity.ai.attributes;

import net.minecraft.util.MathHelper;

public class RangedAttribute extends BaseAttribute {
   public double minimumValue;
   public double maximumValue;
   public String description;

   public RangedAttribute(IAttribute attribute, String unlocalizedNameIn, double defaultValue, double minimumValueIn, double maximumValueIn) {
      super(attribute, unlocalizedNameIn, defaultValue);
      this.minimumValue = minimumValueIn;
      this.maximumValue = maximumValueIn;
      if (minimumValueIn > maximumValueIn) {
         throw new IllegalArgumentException("Minimum value cannot be bigger than maximum value!");
      }

      if (defaultValue < minimumValueIn) {
         throw new IllegalArgumentException("Default value cannot be lower than minimum value!");
      }

      if (defaultValue > maximumValueIn) {
         throw new IllegalArgumentException("Default value cannot be bigger than maximum value!");
      }
   }

   public RangedAttribute setDescription(String descriptionIn) {
      this.description = descriptionIn;
      return this;
   }

   public String getDescription() {
      return this.description;
   }

   @Override
   public double clampValue(double d) {
      return MathHelper.clamp_double(d, this.minimumValue, this.maximumValue);
   }
}
