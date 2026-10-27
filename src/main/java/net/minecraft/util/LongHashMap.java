package net.minecraft.util;

public class LongHashMap<V> {
   public transient LongHashMap.Entry<V>[] hashArray = new LongHashMap.Entry[4096];
   public transient int numHashElements;
   public int capacity = 3072;
   public final float percentUseable = 0.75F;
   public int mask = this.hashArray.length - 1;
   public transient volatile int modCount;

   public static int getHashedKey(long originalKey) {
      return (int)(originalKey ^ originalKey >>> 27);
   }

   public static int hash(int integer) {
      integer = integer ^ integer >>> 20 ^ integer >>> 12;
      return integer ^ integer >>> 7 ^ integer >>> 4;
   }

   public static int getHashIndex(int n, int n2) {
      return n & n2;
   }

   public int getNumHashElements() {
      return this.numHashElements;
   }

   public V getValueByKey(long l) {
      int hashedKey = getHashedKey(l);

      for (LongHashMap.Entry entry = this.hashArray[getHashIndex(hashedKey, this.mask)]; entry != null; entry = entry.nextEntry) {
         if (entry.key == l) {
            return (V)entry.value;
         }
      }

      return null;
   }

   public boolean containsItem(long l) {
      return this.getEntry(l) != null;
   }

   public LongHashMap.Entry<V> getEntry(long l) {
      int hashedKey = getHashedKey(l);

      for (LongHashMap.Entry entry = this.hashArray[getHashIndex(hashedKey, this.mask)]; entry != null; entry = entry.nextEntry) {
         if (entry.key == l) {
            return entry;
         }
      }

      return null;
   }

   public void add(long l, V value) {
      int hashedKey = getHashedKey(l);
      int hashIndex = getHashIndex(hashedKey, this.mask);

      for (LongHashMap.Entry entry = this.hashArray[hashIndex]; entry != null; entry = entry.nextEntry) {
         if (entry.key == l) {
            entry.value = (V)value;
            return;
         }
      }

      this.modCount++;
      this.createKey(hashedKey, l, (V)value, hashIndex);
   }

   public void resizeTable(int n) {
      LongHashMap.Entry[] hashArray = this.hashArray;
      int length = hashArray.length;
      if (length == 1073741824) {
         this.capacity = Integer.MAX_VALUE;
      } else {
         LongHashMap.Entry[] entries = new LongHashMap.Entry[n];
         this.copyHashTableTo(entries);
         this.hashArray = entries;
         this.mask = this.hashArray.length - 1;
         float f = n;
         this.getClass();
         this.capacity = (int)(f * 0.75F);
      }
   }

   public void copyHashTableTo(LongHashMap.Entry<V>[] entries) {
      LongHashMap.Entry[] hashArray = this.hashArray;
      int length = entries.length;

      for (int i = 0; i < hashArray.length; i++) {
         LongHashMap.Entry entry = hashArray[i];
         if (entry != null) {
            hashArray[i] = null;

            while (true) {
               LongHashMap.Entry nextEntry = entry.nextEntry;
               int hashIndex = getHashIndex(entry.hash, length - 1);
               entry.nextEntry = entries[hashIndex];
               entries[hashIndex] = entry;
               entry = nextEntry;
               if (nextEntry == null) {
                  break;
               }
            }
         }
      }
   }

   public V remove(long l) {
      LongHashMap.Entry removeKey = this.removeKey(l);
      return removeKey == null ? null : (V)removeKey.value;
   }

   public LongHashMap.Entry<V> removeKey(long l) {
      int hashedKey = getHashedKey(l);
      int hashIndex = getHashIndex(hashedKey, this.mask);
      LongHashMap.Entry entry = this.hashArray[hashIndex];
      LongHashMap.Entry entry2 = entry;

      while (entry2 != null) {
         LongHashMap.Entry nextEntry = entry2.nextEntry;
         if (entry2.key == l) {
            this.modCount++;
            this.numHashElements--;
            if (entry == entry2) {
               this.hashArray[hashIndex] = nextEntry;
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

   public void createKey(int n, long l, V value, int n2) {
      LongHashMap.Entry entry = this.hashArray[n2];
      this.hashArray[n2] = new LongHashMap.Entry<>(n, l, (V)value, entry);
      if (this.numHashElements++ >= this.capacity) {
         this.resizeTable(2 * this.hashArray.length);
      }
   }

   public double getKeyDistribution() {
      int n = 0;

      for (int i = 0; i < this.hashArray.length; i++) {
         if (this.hashArray[i] != null) {
            n++;
         }
      }

      return 1.0 * n / this.numHashElements;
   }

   static class Entry<V> {
      public V value;
      public LongHashMap.Entry<V> nextEntry;
      public long key;
      public int hash;

      public Entry(int n, long l, V value, LongHashMap.Entry<V> entry) {
         this.value = (V)value;
         this.nextEntry = entry;
         this.key = l;
         this.hash = n;
      }

      public long getKey() {
         return this.key;
      }

      public V getValue() {
         return this.value;
      }

      @Override
      public boolean equals(Object obj) {
         if (!(obj instanceof LongHashMap.Entry)) {
            return false;
         }

         LongHashMap.Entry entry = (LongHashMap.Entry)obj;
         Long key = this.getKey();
         Long key2 = entry.getKey();
         if (key == key2 || key != null && key.equals(key2)) {
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
         return LongHashMap.getHashedKey(this.key);
      }

      @Override
      public String toString() {
         return this.getKey() + "=" + this.getValue();
      }
   }
}
