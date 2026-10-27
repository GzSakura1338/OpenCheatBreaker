package net.optifine.util;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;

public class IteratorCache {
   public static Deque<IteratorCache.IteratorReusable<Object>> dequeIterators = new ArrayDeque<>();

   public static Iterator<Object> getReadOnly(List list) {
      synchronized (dequeIterators) {
         IteratorCache.IteratorReusable pollFirst = dequeIterators.pollFirst();
         if (pollFirst == null) {
            pollFirst = new IteratorCache.IteratorReadOnly();
         }

         pollFirst.setList(list);
         return pollFirst;
      }
   }

   public static void finished(IteratorCache.IteratorReusable<Object> iteratorReusable) {
      synchronized (dequeIterators) {
         if (dequeIterators.size() <= 1000) {
            iteratorReusable.setList(null);
            dequeIterators.addLast(iteratorReusable);
         }
      }
   }

   static {
      for (int i = 0; i < 1000; i++) {
         IteratorCache.IteratorReadOnly iteratorReadOnly = new IteratorCache.IteratorReadOnly();
         dequeIterators.add(iteratorReadOnly);
      }
   }

   public static class IteratorReadOnly implements IteratorCache.IteratorReusable<Object> {
      public List<Object> list;
      public int index;
      public boolean hasNext;

      @Override
      public void setList(List<Object> list) {
         if (this.hasNext) {
            throw new RuntimeException("Iterator still used, oldList: " + this.list + ", newList: " + list);
         }

         this.list = list;
         this.index = 0;
         this.hasNext = list != null && this.index < list.size();
      }

      @Override
      public Object next() {
         if (!this.hasNext) {
            return null;
         }

         Object obj = this.list.get(this.index);
         this.index++;
         this.hasNext = this.index < this.list.size();
         return obj;
      }

      @Override
      public boolean hasNext() {
         if (!this.hasNext) {
            IteratorCache.finished(this);
            return false;
         } else {
            return this.hasNext;
         }
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException("remove");
      }
   }

   public interface IteratorReusable<E> extends Iterator<E> {
      void setList(List<E> list);
   }
}
