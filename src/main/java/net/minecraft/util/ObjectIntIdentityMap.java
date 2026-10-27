package net.minecraft.util;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

public class ObjectIntIdentityMap<T> implements IObjectIntIterable<T> {
   public IdentityHashMap<T, Integer> identityMap = new IdentityHashMap<>(512);
   public List<T> objectList = Lists.newArrayList();

   public void put(T key, int value) {
      this.identityMap.put((T)key, value);

      while (this.objectList.size() <= value) {
         this.objectList.add(null);
      }

      this.objectList.set(value, (T)key);
   }

   public int get(T key) {
      Integer integer = this.identityMap.get(key);
      return integer == null ? -1 : integer;
   }

   public T getByValue(int value) {
      return value >= 0 && value < this.objectList.size() ? this.objectList.get(value) : null;
   }

   @Override
   public Iterator<T> iterator() {
      return Iterators.filter(this.objectList.iterator(), Predicates.notNull());
   }
}
