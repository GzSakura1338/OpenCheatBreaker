package net.minecraft.util;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.UnmodifiableIterator;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class Cartesian {
   public static <T> Iterable<T[]> cartesianProduct(Class<T> clazz, Iterable<? extends Iterable<? extends T>> sets) {
      return new Cartesian.Product<>(clazz, toArray(Iterable.class, sets));
   }

   public static <T> Iterable<List<T>> cartesianProduct(Iterable<? extends Iterable<? extends T>> sets) {
      return arraysAsLists(cartesianProduct(Object.class, sets));
   }

   public static <T> Iterable<List<T>> arraysAsLists(Iterable<Object[]> arrays) {
      return Iterables.transform(arrays, new Cartesian.GetList());
   }

   public static <T> T[] toArray(Class<? super T> clazz, Iterable<? extends T> it) {
      ArrayList newArrayList = Lists.newArrayList();

      for (Object obj : it) {
         newArrayList.add(obj);
      }

      return (T[])newArrayList.toArray(createArray(clazz, newArrayList.size()));
   }

   public static <T> T[] createArray(Class<? super T> clazz, int n) {
      return (T[])((Object[])Array.newInstance(clazz, n));
   }

   static class GetList<T> implements Function<Object[], List<T>> {
      public GetList() {
      }

      public List<T> apply(Object[] objs) {
         return Arrays.asList((T[])objs);
      }
   }

   static class Product<T> implements Iterable<T[]> {
      public Class<T> clazz;
      public Iterable<? extends T>[] iterables;

      public Product(Class<T> clazz, Iterable<? extends T>[] iterables) {
         this.clazz = clazz;
         this.iterables = iterables;
      }

      @Override
      public Iterator<T[]> iterator() {
         return (Iterator<T[]>)(this.iterables.length <= 0
            ? Collections.singletonList((T)Cartesian.createArray(this.clazz, 0)).iterator()
            : new Cartesian.Product.ProductIterator<T>(this.clazz, this.iterables));
      }

      static class ProductIterator<T> extends UnmodifiableIterator<T[]> {
         public int index = -2;
         public Iterable<? extends T>[] iterables;
         public Iterator<? extends T>[] iterators;
         public T[] results;

         public ProductIterator(Class<T> clazz, Iterable<? extends T>[] iterables) {
            this.iterables = iterables;
            this.iterators = Cartesian.createArray(Iterator.class, this.iterables.length);

            for (int i = 0; i < this.iterables.length; i++) {
               this.iterators[i] = iterables[i].iterator();
            }

            this.results = (T[])Cartesian.createArray(clazz, this.iterators.length);
         }

         public void endOfData() {
            this.index = -1;
            Arrays.fill(this.iterators, null);
            Arrays.fill(this.results, null);
         }

         public boolean hasNext() {
            if (this.index == -2) {
               this.index = 0;

               for (Iterator iterator : this.iterators) {
                  if (!iterator.hasNext()) {
                     this.endOfData();
                     break;
                  }
               }

               return true;
            } else {
               if (this.index >= this.iterators.length) {
                  for (this.index = this.iterators.length - 1; this.index >= 0; this.index--) {
                     Iterator iterator = this.iterators[this.index];
                     if (iterator.hasNext()) {
                        break;
                     }

                     if (this.index == 0) {
                        this.endOfData();
                        break;
                     }

                     iterator = this.iterables[this.index].iterator();
                     this.iterators[this.index] = iterator;
                     if (!iterator.hasNext()) {
                        this.endOfData();
                        break;
                     }
                  }
               }

               return this.index >= 0;
            }
         }

         public T[] next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            }

            while (this.index < this.iterators.length) {
               this.results[this.index] = (T)this.iterators[this.index].next();
               this.index++;
            }

            return (T[])((Object[])this.results.clone());
         }
      }
   }
}
