package net.minecraft.util;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.Iterator;
import java.util.Map;

public class RegistryNamespaced<K, V> extends RegistrySimple<K, V> implements IObjectIntIterable<V> {
   public ObjectIntIdentityMap<V> underlyingIntegerMap = new ObjectIntIdentityMap<>();
   public Map<V, K> inverseObjectRegistry = ((BiMap)this.registryObjects).inverse();

   public void register(int id, K key, V value) {
      this.underlyingIntegerMap.put((V)value, id);
      this.putObject((K)key, (V)value);
   }

   @Override
   public Map<K, V> createUnderlyingMap() {
      return HashBiMap.create();
   }

   @Override
   public V getObject(K name) {
      return super.getObject((K)name);
   }

   public K getNameForObject(V value) {
      return this.inverseObjectRegistry.get(value);
   }

   @Override
   public boolean containsKey(K key) {
      return super.containsKey((K)key);
   }

   public int getIDForObject(V value) {
      return this.underlyingIntegerMap.get((V)value);
   }

   public V getObjectById(int id) {
      return this.underlyingIntegerMap.getByValue(id);
   }

   @Override
   public Iterator<V> iterator() {
      return this.underlyingIntegerMap.iterator();
   }
}
