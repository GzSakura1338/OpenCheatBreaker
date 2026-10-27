package net.minecraft.util;

import org.apache.commons.lang3.Validate;

public class RegistryNamespacedDefaultedByKey<K, V> extends RegistryNamespaced<K, V> {
   public K defaultValueKey;
   public V defaultValue;

   public RegistryNamespacedDefaultedByKey(K defaultValueKeyIn) {
      this.defaultValueKey = (K)defaultValueKeyIn;
   }

   @Override
   public void register(int id, K key, V value) {
      if (this.defaultValueKey.equals(key)) {
         this.defaultValue = (V)value;
      }

      super.register(id, (K)key, (V)value);
   }

   public void validateKey() {
      Validate.notNull(this.defaultValueKey);
   }

   @Override
   public V getObject(K name) {
      Object object = super.getObject((K)name);
      return (V)(object == null ? this.defaultValue : object);
   }

   @Override
   public V getObjectById(int id) {
      Object objectById = super.getObjectById(id);
      return (V)(objectById == null ? this.defaultValue : objectById);
   }
}
