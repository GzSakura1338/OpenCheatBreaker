package net.minecraft.block.properties;

import com.google.common.base.MoreObjects;

public abstract class PropertyHelper<T extends Comparable<T>> implements IProperty<T> {
   public Class<T> valueClass;
   public String name;

   public PropertyHelper(String name, Class<T> valueClass) {
      this.valueClass = valueClass;
      this.name = name;
   }

   @Override
   public String getName() {
      return this.name;
   }

   @Override
   public Class<T> getValueClass() {
      return this.valueClass;
   }

   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).add("name", this.name).add("clazz", this.valueClass).add("values", this.getAllowedValues()).toString();
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (obj != null && this.getClass() == obj.getClass()) {
         PropertyHelper propertyHelper = (PropertyHelper)obj;
         return this.valueClass.equals(propertyHelper.valueClass) && this.name.equals(propertyHelper.name);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return 31 * this.valueClass.hashCode() + this.name.hashCode();
   }
}
