package net.optifine;

import java.util.AbstractSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.NextTickListEntry;

public class NextTickHashSet extends AbstractSet {
    public LongHashMap longHashMap = new LongHashMap();
    public int size = 0;
    public HashSet emptySet = new HashSet();

    public NextTickHashSet(Set set) {
        this.addAll(set);
    }

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean contains(Object obj) {
        if (!(obj instanceof NextTickListEntry)) {
            return false;
        }

        NextTickListEntry nextTickListEntry = (NextTickListEntry)obj;
        if (nextTickListEntry == null) {
            return false;
        }

        long chunkXZ2Int = ChunkCoordIntPair.chunkXZ2Int(nextTickListEntry.xCoord >> 4, nextTickListEntry.zCoord >> 4);
        HashSet valueByKey = (HashSet)this.longHashMap.getValueByKey(chunkXZ2Int);
        return valueByKey == null ? false : valueByKey.contains(nextTickListEntry);
    }

    @Override
    public boolean add(Object obj) {
        if (!(obj instanceof NextTickListEntry)) {
            return false;
        }

        NextTickListEntry nextTickListEntry = (NextTickListEntry)obj;
        if (nextTickListEntry == null) {
            return false;
        }

        long chunkXZ2Int = ChunkCoordIntPair.chunkXZ2Int(nextTickListEntry.xCoord >> 4, nextTickListEntry.zCoord >> 4);
        HashSet valueByKey = (HashSet)this.longHashMap.getValueByKey(chunkXZ2Int);
        if (valueByKey == null) {
            valueByKey = new HashSet();
            this.longHashMap.add(chunkXZ2Int, valueByKey);
        }

        boolean flag = valueByKey.add(nextTickListEntry);
        if (flag) {
            this.size++;
        }

        return flag;
    }

    @Override
    public boolean remove(Object obj) {
        if (!(obj instanceof NextTickListEntry)) {
            return false;
        }

        NextTickListEntry nextTickListEntry = (NextTickListEntry)obj;
        if (nextTickListEntry == null) {
            return false;
        }

        long chunkXZ2Int = ChunkCoordIntPair.chunkXZ2Int(nextTickListEntry.xCoord >> 4, nextTickListEntry.zCoord >> 4);
        HashSet valueByKey = (HashSet)this.longHashMap.getValueByKey(chunkXZ2Int);
        if (valueByKey == null) {
            return false;
        }

        boolean flag = valueByKey.remove(nextTickListEntry);
        if (flag) {
            this.size--;
        }

        return flag;
    }

    public Iterator getNextTickEntries(int n, int n2) {
        HashSet nextTickEntriesSet = this.getNextTickEntriesSet(n, n2);
        return nextTickEntriesSet.iterator();
    }

    public HashSet getNextTickEntriesSet(int n, int n2) {
        long chunkXZ2Int = ChunkCoordIntPair.chunkXZ2Int(n, n2);
        HashSet valueByKey = (HashSet)this.longHashMap.getValueByKey(chunkXZ2Int);
        if (valueByKey == null) {
            valueByKey = this.emptySet;
        }

        return valueByKey;
    }

    @Override
    public Iterator iterator() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
