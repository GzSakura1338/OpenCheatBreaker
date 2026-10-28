package net.minecraft.util;

import java.util.HashSet;
import java.util.Set;

public class IntHashMap {
    public transient IntHashMap.Entry[] slots = new IntHashMap.Entry[16];
    public transient int count;
    public int threshold = 12;
    public final float growFactor = 0.75F;
    public transient volatile int versionStamp;
    public Set keySet = new HashSet();

    public static int computeHash(int n) {
        n ^= n >>> 20 ^ n >>> 12;
        return n ^ n >>> 7 ^ n >>> 4;
    }

    public static int getSlotIndex(int n, int n2) {
        return n & n2 - 1;
    }

    public Object lookup(int n) {
        int computeHash2 = computeHash(n);

        for (IntHashMap.Entry entry = this.slots[getSlotIndex(computeHash2, this.slots.length)]; entry != null; entry = entry.nextEntry) {
            if (entry.hashEntry == n) {
                return entry.valueEntry;
            }
        }

        return null;
    }

    public boolean containsItem(int n) {
        return this.lookupEntry(n) != null;
    }

    public IntHashMap.Entry lookupEntry(int n) {
        int computeHash2 = computeHash(n);

        for (IntHashMap.Entry entry = this.slots[getSlotIndex(computeHash2, this.slots.length)]; entry != null; entry = entry.nextEntry) {
            if (entry.hashEntry == n) {
                return entry;
            }
        }

        return null;
    }

    public void addKey(int n, Object obj) {
        this.keySet.add(n);
        int computeHash2 = computeHash(n);
        int slotIndex = getSlotIndex(computeHash2, this.slots.length);

        for (IntHashMap.Entry entry = this.slots[slotIndex]; entry != null; entry = entry.nextEntry) {
            if (entry.hashEntry == n) {
                entry.valueEntry = obj;
                return;
            }
        }

        this.versionStamp++;
        this.insert(computeHash2, n, obj, slotIndex);
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

    public void copyTo(IntHashMap.Entry[] entries) {
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

    public Object removeObject(int n) {
        this.keySet.remove(n);
        IntHashMap.Entry removeEntry = this.removeEntry(n);
        return removeEntry == null ? null : removeEntry.valueEntry;
    }

    public IntHashMap.Entry removeEntry(int n) {
        int computeHash2 = computeHash(n);
        int slotIndex = getSlotIndex(computeHash2, this.slots.length);
        IntHashMap.Entry entry = this.slots[slotIndex];
        IntHashMap.Entry entry2 = entry;

        while (entry2 != null) {
            IntHashMap.Entry nextEntry = entry2.nextEntry;
            if (entry2.hashEntry == n) {
                this.versionStamp++;
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
        this.versionStamp++;
        IntHashMap.Entry[] slots = this.slots;

        for (int i = 0; i < slots.length; i++) {
            slots[i] = null;
        }

        this.count = 0;
    }

    public void insert(int n, int n2, Object obj, int n3) {
        IntHashMap.Entry entry = this.slots[n3];
        this.slots[n3] = new IntHashMap.Entry(n, n2, obj, entry);
        if (this.count++ >= this.threshold) {
            this.grow(2 * this.slots.length);
        }
    }

    public static class Entry {
        public Object valueEntry;
        public IntHashMap.Entry nextEntry;
        public int hashEntry;
        public int slotHash;

        public Entry(int n, int n2, Object obj, IntHashMap.Entry entry) {
            this.valueEntry = obj;
            this.nextEntry = entry;
            this.hashEntry = n2;
            this.slotHash = n;
        }

        public int getHash() {
            return this.hashEntry;
        }

        public Object getValue() {
            return this.valueEntry;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IntHashMap.Entry)) {
                return false;
            } else {
                IntHashMap.Entry entry = (IntHashMap.Entry)obj;
                Integer hash = this.getHash();
                Integer hash2 = entry.getHash();
                if (hash == hash2 || hash != null && hash.equals(hash2)) {
                    Object value = this.getValue();
                    Object value2 = entry.getValue();
                    return value == value2 || value != null && value.equals(value2);
                } else {
                    return false;
                }
            }
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
