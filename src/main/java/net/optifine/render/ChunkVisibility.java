package net.optifine.render;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ClassInheritanceMultiMap;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class ChunkVisibility {
   public static final int MASK_FACINGS = 63;
   public static EnumFacing[][] enumFacingArrays = makeEnumFacingArrays(false);
   public static EnumFacing[][] enumFacingOppositeArrays = makeEnumFacingArrays(true);
   public static int counter = 0;
   public static int iMaxStatic = -1;
   public static int iMaxStaticFinal = 16;
   public static World worldLast = null;
   public static int pcxLast = Integer.MIN_VALUE;
   public static int pczLast = Integer.MIN_VALUE;

   public static int getMaxChunkY(World world, Entity entity, int n) {
      int n2 = MathHelper.floor_double(entity.posX) >> 4;
      int n3 = MathHelper.floor_double(entity.posY) >> 4;
      int n4 = MathHelper.floor_double(entity.posZ) >> 4;
      Chunk chunkFromChunkCoords = world.getChunkFromChunkCoords(n2, n4);
      int n5 = n2 - n;
      int n6 = n2 + n;
      int n7 = n4 - n;
      int n8 = n4 + n;
      if (world != worldLast || n2 != pcxLast || n4 != pczLast) {
         counter = 0;
         iMaxStaticFinal = 16;
         worldLast = world;
         pcxLast = n2;
         pczLast = n4;
      }

      if (counter == 0) {
         iMaxStatic = -1;
      }

      int iMaxStatic2 = iMaxStatic;
      switch (counter) {
         case 0:
            n6 = n2;
            n8 = n4;
            break;
         case 1:
            n5 = n2;
            n8 = n4;
            break;
         case 2:
            n6 = n2;
            n7 = n4;
            break;
         case 3:
            n5 = n2;
            n7 = n4;
      }

      for (int i = n5; i < n6; i++) {
         for (int j = n7; j < n8; j++) {
            Chunk chunkFromChunkCoords2 = world.getChunkFromChunkCoords(i, j);
            if (!chunkFromChunkCoords2.isEmpty()) {
               ExtendedBlockStorage[] blockStorageArray = chunkFromChunkCoords2.getBlockStorageArray();

               for (int k = blockStorageArray.length - 1; k > iMaxStatic2; k--) {
                  ExtendedBlockStorage extendedBlockStorage = blockStorageArray[k];
                  if (extendedBlockStorage != null && !extendedBlockStorage.isEmpty()) {
                     if (k > iMaxStatic2) {
                        iMaxStatic2 = k;
                     }
                     break;
                  }
               }

               try {
                  Map tileEntityMap = chunkFromChunkCoords2.getTileEntityMap();
                  if (!tileEntityMap.isEmpty()) {
                     for (BlockPos blockPos : (Iterable<BlockPos>)tileEntityMap.keySet()) {
                        int n9 = blockPos.getY() >> 4;
                        if (n9 > iMaxStatic2) {
                           iMaxStatic2 = n9;
                        }
                     }
                  }
               } catch (ConcurrentModificationException e) {
               }

               ClassInheritanceMultiMap[] entityLists = chunkFromChunkCoords2.getEntityLists();

               for (int k = entityLists.length - 1; k > iMaxStatic2; k--) {
                  ClassInheritanceMultiMap classInheritanceMultiMap = entityLists[k];
                  if (!classInheritanceMultiMap.isEmpty() && (chunkFromChunkCoords2 != chunkFromChunkCoords || k != n3 || classInheritanceMultiMap.size() != 1)) {
                     if (k > iMaxStatic2) {
                        iMaxStatic2 = k;
                     }
                     break;
                  }
               }
            }
         }
      }

      if (counter < 3) {
         iMaxStatic = iMaxStatic2;
         iMaxStatic2 = iMaxStaticFinal;
      } else {
         iMaxStaticFinal = iMaxStatic2;
         iMaxStatic = -1;
      }

      counter = (counter + 1) % 4;
      return iMaxStatic2 << 4;
   }

   public static boolean isFinished() {
      return counter == 0;
   }

   public static EnumFacing[][] makeEnumFacingArrays(boolean flag) {
      byte b = 64;
      EnumFacing[][] enumFacingses = new EnumFacing[b][];

      for (int i = 0; i < b; i++) {
         ArrayList list = new ArrayList();

         for (int j = 0; j < EnumFacing.VALUES.length; j++) {
            EnumFacing enumFacing = EnumFacing.VALUES[j];
            EnumFacing enumFacing2 = flag ? enumFacing.getOpposite() : enumFacing;
            int n = 1 << enumFacing2.ordinal();
            if ((i & n) != 0) {
               list.add(enumFacing);
            }
         }

         EnumFacing[] toArray = (EnumFacing[])list.toArray(new EnumFacing[list.size()]);
         enumFacingses[i] = toArray;
      }

      return enumFacingses;
   }

   public static EnumFacing[] getFacingsNotOpposite(int n) {
      int n2 = ~n & 63;
      return enumFacingOppositeArrays[n2];
   }

   public static void reset() {
      worldLast = null;
   }
}
