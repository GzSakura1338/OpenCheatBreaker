package net.minecraft.util;

public class RegistryDefaulted<K, V> extends RegistrySimple<K, V> {
   public V defaultObject;

   public RegistryDefaulted(V defaultObjectIn) {
      this.defaultObject = (V)defaultObjectIn;
   }

   @Override
   public V getObject(K name) {
      Object object = super.getObject((K)name);
      return (V)(object == null ? this.defaultObject : object);
   }
}
