package net.minecraft.block.properties;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import net.minecraft.util.IStringSerializable;

public class PropertyEnum<T extends Enum<T> & IStringSerializable> extends PropertyHelper<T> {
   public Map<String, T> nameToValue = Maps.newHashMap();
   public ImmutableSet<T> allowedValues;

   public PropertyEnum(String name, Class<T> valueClass, Collection<T> allowedValues) {
      super(name, valueClass);
      this.allowedValues = ImmutableSet.copyOf(allowedValues);

      for (Enum enumValue : allowedValues) {
         String name2 = ((IStringSerializable)enumValue).getName();
         if (this.nameToValue.containsKey(name2)) {
            throw new IllegalArgumentException("Multiple values have the same name '" + name2 + "'");
         }

         this.nameToValue.put(name2, (T)enumValue);
      }
   }

   @Override
   public Collection<T> getAllowedValues() {
      return this.allowedValues;
   }

   public String getName(T value) {
      return ((IStringSerializable)value).getName();
   }

   public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String name, Class<T> clazz) {
      return create(name, clazz, Predicates.alwaysTrue());
   }

   public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String name, Class<T> clazz, Predicate<T> filter) {
      return create(name, clazz, Collections2.filter(Lists.newArrayList(clazz.getEnumConstants()), filter));
   }

   public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String name, Class<T> clazz, T... values) {
      return create(name, clazz, Lists.newArrayList(values));
   }

   public static <T extends Enum<T> & IStringSerializable> PropertyEnum<T> create(String name, Class<T> clazz, Collection<T> values) {
      return new PropertyEnum<>(name, clazz, values);
   }
}
