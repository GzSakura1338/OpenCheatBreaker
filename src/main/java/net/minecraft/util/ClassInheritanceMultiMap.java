package net.minecraft.util;

import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.optifine.util.IteratorCache;

public class ClassInheritanceMultiMap<T> extends AbstractSet<T> {
   public static Set<Class<?>> classValueSet = Collections.newSetFromMap(new ConcurrentHashMap<>());
   public Map<Class<?>, List<T>> map = Maps.newHashMap();
   public Set<Class<?>> knownKeys = Sets.newIdentityHashSet();
   public List<T> values = Lists.newArrayList();
   public Class<T> baseClass;
   public boolean empty;

   public ClassInheritanceMultiMap(Class<T> baseClassIn) {
      this.baseClass = baseClassIn;
      this.knownKeys.add(baseClassIn);
      this.map.put(baseClassIn, this.values);

      for (Class clazz : classValueSet) {
         this.createLookup(clazz);
      }

      this.empty = this.values.size() == 0;
   }

   public void createLookup(Class<?> clazz) {
      classValueSet.add(clazz);
      int size = this.values.size();

      for (int i = 0; i < size; i++) {
         Object obj = this.values.get(i);
         if (clazz.isAssignableFrom(obj.getClass())) {
            this.addForClass((T)obj, clazz);
         }
      }

      this.knownKeys.add(clazz);
   }

   public Class<?> initializeClassLookup(Class<?> clazz) {
      if (this.baseClass.isAssignableFrom(clazz)) {
         if (!this.knownKeys.contains(clazz)) {
            this.createLookup(clazz);
         }

         return clazz;
      } else {
         throw new IllegalArgumentException("Don't know how to search for " + clazz);
      }
   }

   @Override
   public boolean add(T value) {
      for (Class clazz : this.knownKeys) {
         if (clazz.isAssignableFrom(value.getClass())) {
            this.addForClass((T)value, clazz);
         }
      }

      this.empty = this.values.size() == 0;
      return true;
   }

   public void addForClass(T value, Class<?> parentClass) {
      List list = this.map.get(parentClass);
      if (list == null) {
         this.map.put(parentClass, (List)Lists.newArrayList(new Object[]{value}));
      } else {
         list.add(value);
      }

      this.empty = this.values.size() == 0;
   }

   @Override
   public boolean remove(Object obj) {
      Object obj2 = obj;
      boolean flag = false;

      for (Class clazz : this.knownKeys) {
         if (clazz.isAssignableFrom(obj2.getClass())) {
            List list = this.map.get(clazz);
            if (list != null && list.remove(obj2)) {
               flag = true;
            }
         }
      }

      this.empty = this.values.size() == 0;
      return flag;
   }

   @Override
   public boolean contains(Object obj) {
      return Iterators.contains(this.getByClass(obj.getClass()).iterator(), obj);
   }

   public <S> Iterable<S> getByClass(final Class<S> clazz) {
      return new Iterable<S>() {
         @Override
         public Iterator<S> iterator() {
            List list = ClassInheritanceMultiMap.this.map.get(ClassInheritanceMultiMap.this.initializeClassLookup(clazz));
            if (list == null) {
               return Collections.emptyIterator();
            }

            Iterator iterator = list.iterator();
            return Iterators.filter(iterator, clazz);
         }
      };
   }

   @Override
   public Iterator<T> iterator() {
      return this.values.isEmpty() ? Collections.emptyIterator() : (Iterator)IteratorCache.getReadOnly(this.values);
   }

   @Override
   public int size() {
      return this.values.size();
   }

   @Override
   public boolean isEmpty() {
      return this.empty;
   }
}
