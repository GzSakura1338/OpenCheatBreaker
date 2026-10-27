package net.minecraft.client.renderer.chunk;

import java.util.ArrayDeque;
import java.util.BitSet;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IntegerCache;

public class VisGraph {
   public static int STEP_X = (int)Math.pow(16.0, 0.0);
   public static int STEP_Z = (int)Math.pow(16.0, 1.0);
   public static int STEP_Y = (int)Math.pow(16.0, 2.0);
   public BitSet closed = new BitSet(4096);
   public static int[] EDGE_POINTS = new int[1352];
   public int openCount = 4096;

   public void markClosed(BlockPos pos) {
      this.closed.set(getIndex(pos), true);
      this.openCount--;
   }

   public static int getIndex(BlockPos pos) {
      return getIndex(pos.getX() & 15, pos.getY() & 15, pos.getZ() & 15);
   }

   public static int getIndex(int x, int y, int z) {
      return x << 0 | y << 8 | z << 4;
   }

   public SetVisibility computeVisibility() {
      SetVisibility setVisibility = new SetVisibility();
      if (4096 - this.openCount < 256) {
         setVisibility.setAllVisible(true);
      } else if (this.openCount == 0) {
         setVisibility.setAllVisible(false);
      } else {
         for (int n : EDGE_POINTS) {
            if (!this.closed.get(n)) {
               setVisibility.setManyVisible(this.getOpenFaces(n));
            }
         }
      }

      return setVisibility;
   }

   public Set<EnumFacing> getOpenFaces(BlockPos pos) {
      return this.getOpenFaces(getIndex(pos));
   }

   public Set<EnumFacing> getOpenFaces(int n) {
      EnumSet noneOf = EnumSet.noneOf(EnumFacing.class);
      ArrayDeque arrayDeque = new ArrayDeque(384);
      arrayDeque.add(IntegerCache.getInteger(n));
      this.closed.set(n, true);

      while (!arrayDeque.isEmpty()) {
         int poll = (Integer)arrayDeque.poll();
         this.addEdgeFaces(poll, noneOf);

         for (EnumFacing enumFacing : EnumFacing.VALUES) {
            int offset = this.offset(poll, enumFacing);
            if (offset >= 0 && !this.closed.get(offset)) {
               this.closed.set(offset, true);
               arrayDeque.add(IntegerCache.getInteger(offset));
            }
         }
      }

      return noneOf;
   }

   public void addEdgeFaces(int n, Set<EnumFacing> set) {
      int n2 = n >> 0 & 15;
      if (n2 == 0) {
         set.add(EnumFacing.WEST);
      } else if (n2 == 15) {
         set.add(EnumFacing.EAST);
      }

      int n3 = n >> 8 & 15;
      if (n3 == 0) {
         set.add(EnumFacing.DOWN);
      } else if (n3 == 15) {
         set.add(EnumFacing.UP);
      }

      int n4 = n >> 4 & 15;
      if (n4 == 0) {
         set.add(EnumFacing.NORTH);
      } else if (n4 == 15) {
         set.add(EnumFacing.SOUTH);
      }
   }

   public int offset(int n, EnumFacing enumFacing) {
      switch (enumFacing) {
         case DOWN:
            if ((n >> 8 & 15) == 0) {
               return -1;
            }

            return n - STEP_Y;
         case UP:
            if ((n >> 8 & 15) == 15) {
               return -1;
            }

            return n + STEP_Y;
         case NORTH:
            if ((n >> 4 & 15) == 0) {
               return -1;
            }

            return n - STEP_Z;
         case SOUTH:
            if ((n >> 4 & 15) == 15) {
               return -1;
            }

            return n + STEP_Z;
         case WEST:
            if ((n >> 0 & 15) == 0) {
               return -1;
            }

            return n - STEP_X;
         case EAST:
            if ((n >> 0 & 15) == 15) {
               return -1;
            }

            return n + STEP_X;
         default:
            return -1;
      }
   }

   static {
      boolean flag = false;
      byte b = 15;
      int n = 0;

      for (int i = 0; i < 16; i++) {
         for (int j = 0; j < 16; j++) {
            for (int k = 0; k < 16; k++) {
               if (i == 0 || i == 15 || j == 0 || j == 15 || k == 0 || k == 15) {
                  EDGE_POINTS[n++] = getIndex(i, j, k);
               }
            }
         }
      }
   }
}
