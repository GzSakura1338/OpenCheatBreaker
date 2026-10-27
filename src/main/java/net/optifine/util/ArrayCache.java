package net.optifine.util;

import java.lang.reflect.Array;
import java.util.ArrayDeque;

public class ArrayCache {
   public Class elementClass = null;
   public int maxCacheSize = 0;
   public ArrayDeque cache = new ArrayDeque();

   public ArrayCache(Class clazz, int n) {
      this.elementClass = clazz;
      this.maxCacheSize = n;
   }

   public synchronized Object allocate(int n) {
      Object pollLast = this.cache.pollLast();
      if (pollLast == null || Array.getLength(pollLast) < n) {
         pollLast = Array.newInstance(this.elementClass, n);
      }

      return pollLast;
   }

   public synchronized void free(Object obj) {
      if (obj != null) {
         Class clazz = obj.getClass();
         if (clazz.getComponentType() != this.elementClass) {
            throw new IllegalArgumentException("Wrong component type");
         }

         if (this.cache.size() < this.maxCacheSize) {
            this.cache.add(obj);
         }
      }
   }
}
