package net.minecraft.util;

public class IntHashMap<V> {
   public transient IntHashMap.Entry<V>[] slots = new IntHashMap.Entry[16];
   public transient int count;
   public int threshold = 12;
   public final float growFactor = 0.75F;

   public static int computeHash(int integer) {
      integer = integer ^ integer >>> 20 ^ integer >>> 12;
      return integer ^ integer >>> 7 ^ integer >>> 4;
   }

   public static int getSlotIndex(int hash, int slotCount) {
      return hash & slotCount - 1;
   }

   public V lookup(int n) {
      int computeHash2 = computeHash(n);

      for (IntHashMap.Entry entry = this.slots[getSlotIndex(computeHash2, this.slots.length)]; entry != null; entry = entry.nextEntry) {
         if (entry.hashEntry == n) {
            return (V)entry.valueEntry;
         }
      }

      return null;
   }

   public boolean containsItem(int n) {
      return this.lookupEntry(n) != null;
   }

   public IntHashMap.Entry<V> lookupEntry(int n) {
      int computeHash2 = computeHash(n);

      for (IntHashMap.Entry entry = this.slots[getSlotIndex(computeHash2, this.slots.length)]; entry != null; entry = entry.nextEntry) {
         if (entry.hashEntry == n) {
            return entry;
         }
      }

      return null;
   }

   public void addKey(int n, V value) {
      int computeHash2 = computeHash(n);
      int slotIndex = getSlotIndex(computeHash2, this.slots.length);

      for (IntHashMap.Entry entry = this.slots[slotIndex]; entry != null; entry = entry.nextEntry) {
         if (entry.hashEntry == n) {
            entry.valueEntry = (V)value;
            return;
         }
      }

      this.insert(computeHash2, n, (V)value, slotIndex);
   }

   public void grow(int n) {
      IntHashMap.Entry[] slots = this.slots;
      int length = slots.length;
      if (length == 1073741824) {
         this.threshold = Integer.MAX_VALUE;
      } else {
         IntHashMap.Entry[] entries = new IntHashMap.Entry[n];
         this.copyTo(entries);
         this.slots = entries;
         this.threshold = (int)(n * 0.75F);
      }
   }

   public void copyTo(IntHashMap.Entry<V>[] entries) {
      IntHashMap.Entry[] slots = this.slots;
      int length = entries.length;

      for (int i = 0; i < slots.length; i++) {
         IntHashMap.Entry entry = slots[i];
         if (entry != null) {
            slots[i] = null;

            while (true) {
               IntHashMap.Entry nextEntry = entry.nextEntry;
               int slotIndex = getSlotIndex(entry.slotHash, length);
               entry.nextEntry = entries[slotIndex];
               entries[slotIndex] = entry;
               entry = nextEntry;
               if (nextEntry == null) {
                  break;
               }
            }
         }
      }
   }

   public V removeObject(int n) {
      IntHashMap.Entry removeEntry = this.removeEntry(n);
      return removeEntry == null ? null : (V)removeEntry.valueEntry;
   }

   public IntHashMap.Entry<V> removeEntry(int n) {
      int computeHash2 = computeHash(n);
      int slotIndex = getSlotIndex(computeHash2, this.slots.length);
      IntHashMap.Entry entry = this.slots[slotIndex];
      IntHashMap.Entry entry2 = entry;

      while (entry2 != null) {
         IntHashMap.Entry nextEntry = entry2.nextEntry;
         if (entry2.hashEntry == n) {
            this.count--;
            if (entry == entry2) {
               this.slots[slotIndex] = nextEntry;
            } else {
               entry.nextEntry = nextEntry;
            }

            return entry2;
         }

         entry = entry2;
         entry2 = nextEntry;
      }

      return entry2;
   }

   public void clearMap() {
      IntHashMap.Entry[] slots = this.slots;

      for (int i = 0; i < slots.length; i++) {
         slots[i] = null;
      }

      this.count = 0;
   }

   public void insert(int n, int n2, V value, int n3) {
      IntHashMap.Entry entry = this.slots[n3];
      this.slots[n3] = new IntHashMap.Entry<>(n, n2, (V)value, entry);
      if (this.count++ >= this.threshold) {
         this.grow(2 * this.slots.length);
      }
   }

   static class Entry<V> {
      public V valueEntry;
      public IntHashMap.Entry<V> nextEntry;
      public int hashEntry;
      public int slotHash;

      public Entry(int n, int n2, V value, IntHashMap.Entry<V> entry) {
         this.valueEntry = (V)value;
         this.nextEntry = entry;
         this.hashEntry = n2;
         this.slotHash = n;
      }

      public int getHash() {
         return this.hashEntry;
      }

      public V getValue() {
         return this.valueEntry;
      }

      @Override
      public boolean equals(Object obj) {
         if (!(obj instanceof IntHashMap.Entry)) {
            return false;
         }

         IntHashMap.Entry entry = (IntHashMap.Entry)obj;
         Integer hash = this.getHash();
         Integer hash2 = entry.getHash();
         if (hash == hash2 || hash != null && hash.equals(hash2)) {
            Object value = this.getValue();
            Object value2 = entry.getValue();
            if (value == value2 || value != null && value.equals(value2)) {
               return true;
            }
         }

         return false;
      }

      @Override
      public int hashCode() {
         return IntHashMap.computeHash(this.hashEntry);
      }

      @Override
      public String toString() {
         return this.getHash() + "=" + this.getValue();
      }
   }
}
