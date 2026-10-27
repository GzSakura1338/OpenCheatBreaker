package net.minecraft.server.management;

import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class LowerStringMap<V> implements Map<String, V> {
   public Map<String, V> internalMap = Maps.newLinkedHashMap();

   @Override
   public int size() {
      return this.internalMap.size();
   }

   @Override
   public boolean isEmpty() {
      return this.internalMap.isEmpty();
   }

   @Override
   public boolean containsKey(Object obj) {
      return this.internalMap.containsKey(obj.toString().toLowerCase());
   }

   @Override
   public boolean containsValue(Object obj) {
      return this.internalMap.containsKey(obj);
   }

   @Override
   public V get(Object obj) {
      return this.internalMap.get(obj.toString().toLowerCase());
   }

   public V put(String str, V value) {
      return this.internalMap.put(str.toLowerCase(), (V)value);
   }

   @Override
   public V remove(Object obj) {
      return this.internalMap.remove(obj.toString().toLowerCase());
   }

   @Override
   public void putAll(Map<? extends String, ? extends V> map) {
      for (Entry entry : map.entrySet()) {
         this.put((String)entry.getKey(), (V)entry.getValue());
      }
   }

   @Override
   public void clear() {
      this.internalMap.clear();
   }

   @Override
   public Set<String> keySet() {
      return this.internalMap.keySet();
   }

   @Override
   public Collection<V> values() {
      return this.internalMap.values();
   }

   @Override
   public Set<Entry<String, V>> entrySet() {
      return this.internalMap.entrySet();
   }
}
