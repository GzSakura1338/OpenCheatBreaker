package net.optifine;

import com.google.common.collect.Iterators;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import net.minecraft.util.BlockPos;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.NextTickListEntry;

public class NextTickHashSet extends TreeSet {
   public LongHashMap longHashMap = new LongHashMap();
   public int minX = Integer.MIN_VALUE;
   public int minZ = Integer.MIN_VALUE;
   public int maxX = Integer.MIN_VALUE;
   public int maxZ = Integer.MIN_VALUE;
   public static final int UNDEFINED = Integer.MIN_VALUE;

   public NextTickHashSet(Set set) {
      for (Object obj : set) {
         this.add(obj);
      }
   }

   @Override
   public boolean contains(Object obj) {
      if (!(obj instanceof NextTickListEntry)) {
         return false;
      }

      NextTickListEntry nextTickListEntry = (NextTickListEntry)obj;
      Set subSet = this.getSubSet(nextTickListEntry, false);
      return subSet == null ? false : subSet.contains(nextTickListEntry);
   }

   @Override
   public boolean add(Object obj) {
      if (!(obj instanceof NextTickListEntry)) {
         return false;
      } else {
         NextTickListEntry nextTickListEntry = (NextTickListEntry)obj;
         if (nextTickListEntry == null) {
            return false;
         } else {
            Set subSet = this.getSubSet(nextTickListEntry, true);
            boolean flag = subSet.add(nextTickListEntry);
            boolean flag2 = super.add(obj);
            if (flag != flag2) {
               throw new IllegalStateException("Added: " + flag + ", addedParent: " + flag2);
            } else {
               return flag2;
            }
         }
      }
   }

   @Override
   public boolean remove(Object obj) {
      if (!(obj instanceof NextTickListEntry)) {
         return false;
      } else {
         NextTickListEntry nextTickListEntry = (NextTickListEntry)obj;
         Set subSet = this.getSubSet(nextTickListEntry, false);
         if (subSet == null) {
            return false;
         } else {
            boolean flag = subSet.remove(nextTickListEntry);
            boolean flag2 = super.remove(nextTickListEntry);
            if (flag != flag2) {
               throw new IllegalStateException("Added: " + flag + ", addedParent: " + flag2);
            } else {
               return flag2;
            }
         }
      }
   }

   public Set getSubSet(NextTickListEntry nextTickListEntry, boolean flag) {
      if (nextTickListEntry == null) {
         return null;
      }

      BlockPos position = nextTickListEntry.position;
      int n = position.getX() >> 4;
      int n2 = position.getZ() >> 4;
      return this.getSubSet(n, n2, flag);
   }

   public Set getSubSet(int n, int n2, boolean flag) {
      long chunkXZ2Int = ChunkCoordIntPair.chunkXZ2Int(n, n2);
      HashSet valueByKey = (HashSet)this.longHashMap.getValueByKey(chunkXZ2Int);
      if (valueByKey == null && flag) {
         valueByKey = new HashSet();
         this.longHashMap.add(chunkXZ2Int, valueByKey);
      }

      return valueByKey;
   }

   @Override
   public Iterator iterator() {
      if (this.minX == Integer.MIN_VALUE) {
         return super.iterator();
      }

      if (this.size() <= 0) {
         return Collections.emptyIterator();
      }

      int n = this.minX >> 4;
      int n2 = this.minZ >> 4;
      int n3 = this.maxX >> 4;
      int n4 = this.maxZ >> 4;
      ArrayList list = new ArrayList();

      for (int i = n; i <= n3; i++) {
         for (int j = n2; j <= n4; j++) {
            Set subSet = this.getSubSet(i, j, false);
            if (subSet != null) {
               list.add(subSet.iterator());
            }
         }
      }

      if (list.size() <= 0) {
         return Collections.emptyIterator();
      } else {
         return list.size() == 1 ? (Iterator)list.get(0) : Iterators.concat(list.iterator());
      }
   }

   public void setIteratorLimits(int n, int n2, int n3, int n4) {
      this.minX = Math.min(n, n3);
      this.minZ = Math.min(n2, n4);
      this.maxX = Math.max(n, n3);
      this.maxZ = Math.max(n2, n4);
   }

   public void clearIteratorLimits() {
      this.minX = Integer.MIN_VALUE;
      this.minZ = Integer.MIN_VALUE;
      this.maxX = Integer.MIN_VALUE;
      this.maxZ = Integer.MIN_VALUE;
   }
}
