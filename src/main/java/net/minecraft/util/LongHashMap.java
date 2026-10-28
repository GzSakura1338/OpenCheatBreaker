package net.minecraft.util;

public class LongHashMap {
    public transient LongHashMap.Entry[] hashArray = new LongHashMap.Entry[1024];
    public transient int numHashElements;
    public int capacity = (int)(0.75F * this.hashArray.length);
    public float percentUseable = 0.75F;
    public transient volatile int modCount;

    public static int getHashedKey(long l) {
        return (int)(l ^ l >>> 27);
    }

    public static int hash(int n) {
        n ^= n >>> 20 ^ n >>> 12;
        return n ^ n >>> 7 ^ n >>> 4;
    }

    public static int getHashIndex(int n, int n2) {
        return n & n2 - 1;
    }

    public int getNumHashElements() {
        return this.numHashElements;
    }

    public Object getValueByKey(long l) {
        int hashedKey = getHashedKey(l);

        for (LongHashMap.Entry entry = this.hashArray[getHashIndex(hashedKey, this.hashArray.length)]; entry != null; entry = entry.nextEntry) {
            if (entry.key == l) {
                return entry.value;
            }
        }

        return null;
    }

    public boolean containsItem(long l) {
        return this.getEntry(l) != null;
    }

    public LongHashMap.Entry getEntry(long l) {
        int hashedKey = getHashedKey(l);

        for (LongHashMap.Entry entry = this.hashArray[getHashIndex(hashedKey, this.hashArray.length)]; entry != null; entry = entry.nextEntry) {
            if (entry.key == l) {
                return entry;
            }
        }

        return null;
    }

    public void add(long l, Object obj) {
        int hashedKey = getHashedKey(l);
        int hashIndex = getHashIndex(hashedKey, this.hashArray.length);

        for (LongHashMap.Entry entry = this.hashArray[hashIndex]; entry != null; entry = entry.nextEntry) {
            if (entry.key == l) {
                entry.value = obj;
                return;
            }
        }

        this.modCount++;
        this.createKey(hashedKey, l, obj, hashIndex);
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
            float f = n;
            this.getClass();
            this.capacity = (int)(f * 0.75F);
        }
    }

    public void copyHashTableTo(LongHashMap.Entry[] entries) {
        LongHashMap.Entry[] hashArray = this.hashArray;
        int length = entries.length;

        for (int i = 0; i < hashArray.length; i++) {
            LongHashMap.Entry entry = hashArray[i];
            if (entry != null) {
                hashArray[i] = null;

                while (true) {
                    LongHashMap.Entry nextEntry = entry.nextEntry;
                    int hashIndex = getHashIndex(entry.hash, length);
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

    public Object remove(long l) {
        LongHashMap.Entry removeKey = this.removeKey(l);
        return removeKey == null ? null : removeKey.value;
    }

    public LongHashMap.Entry removeKey(long l) {
        int hashedKey = getHashedKey(l);
        int hashIndex = getHashIndex(hashedKey, this.hashArray.length);
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

    public void createKey(int n, long l, Object obj, int n2) {
        LongHashMap.Entry entry = this.hashArray[n2];
        this.hashArray[n2] = new LongHashMap.Entry(n, l, obj, entry);
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

    static class Entry {
        public Object value;
        public LongHashMap.Entry nextEntry;
        public long key;
        public int hash;

        public Entry(int n, long l, Object obj, LongHashMap.Entry entry) {
            this.value = obj;
            this.nextEntry = entry;
            this.key = l;
            this.hash = n;
        }

        public long getKey() {
            return this.key;
        }

        public Object getValue() {
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
