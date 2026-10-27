package net.optifine.util;

import java.util.ArrayList;

public class CompactArrayList {
   public ArrayList list = null;
   public int initialCapacity = 0;
   public float loadFactor = 1.0F;
   public int countValid = 0;

   public CompactArrayList() {
      this(10, 0.75F);
   }

   public CompactArrayList(int n) {
      this(n, 0.75F);
   }

   public CompactArrayList(int n, float f) {
      this.list = new ArrayList(n);
      this.initialCapacity = n;
      this.loadFactor = f;
   }

   public void add(int n, Object obj) {
      if (obj != null) {
         this.countValid++;
      }

      this.list.add(n, obj);
   }

   public boolean add(Object obj) {
      if (obj != null) {
         this.countValid++;
      }

      return this.list.add(obj);
   }

   public Object set(int n, Object obj) {
      Object obj2 = this.list.set(n, obj);
      if (obj != obj2) {
         if (obj2 == null) {
            this.countValid++;
         }

         if (obj == null) {
            this.countValid--;
         }
      }

      return obj2;
   }

   public Object remove(int n) {
      Object obj = this.list.remove(n);
      if (obj != null) {
         this.countValid--;
      }

      return obj;
   }

   public void clear() {
      this.list.clear();
      this.countValid = 0;
   }

   public void compact() {
      if (this.countValid <= 0 && this.list.size() <= 0) {
         this.clear();
      } else if (this.list.size() > this.initialCapacity) {
         float f = this.countValid * 1.0F / this.list.size();
         if (f <= this.loadFactor) {
            int n = 0;

            for (int i = 0; i < this.list.size(); i++) {
               Object obj = this.list.get(i);
               if (obj != null) {
                  if (i != n) {
                     this.list.set(n, obj);
                  }

                  n++;
               }
            }

            for (int i = this.list.size() - 1; i >= n; i--) {
               this.list.remove(i);
            }
         }
      }
   }

   public boolean contains(Object obj) {
      return this.list.contains(obj);
   }

   public Object get(int n) {
      return this.list.get(n);
   }

   public boolean isEmpty() {
      return this.list.isEmpty();
   }

   public int size() {
      return this.list.size();
   }

   public int getCountValid() {
      return this.countValid;
   }
}
