package net.minecraft.block.properties;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;

public class PropertyInteger extends PropertyHelper<Integer> {
   public ImmutableSet<Integer> allowedValues;

   public PropertyInteger(String name, int min, int max) {
      super(name, Integer.class);
      if (min < 0) {
         throw new IllegalArgumentException("Min value of " + name + " must be 0 or greater");
      }

      if (max <= min) {
         throw new IllegalArgumentException("Max value of " + name + " must be greater than min (" + min + ")");
      }

      HashSet newHashSet = Sets.newHashSet();

      for (int min2 = min; min2 <= max; min2++) {
         newHashSet.add(min2);
      }

      this.allowedValues = ImmutableSet.copyOf(newHashSet);
   }

   @Override
   public Collection<Integer> getAllowedValues() {
      return this.allowedValues;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (obj == null || this.getClass() != obj.getClass()) {
         return false;
      }

      if (!super.equals(obj)) {
         return false;
      }

      PropertyInteger propertyInteger = (PropertyInteger)obj;
      return this.allowedValues.equals(propertyInteger.allowedValues);
   }

   @Override
   public int hashCode() {
      int n = super.hashCode();
      return 31 * n + this.allowedValues.hashCode();
   }

   public static PropertyInteger create(String name, int min, int max) {
      return new PropertyInteger(name, min, max);
   }

   public String getName(Integer value) {
      return value.toString();
   }
}
