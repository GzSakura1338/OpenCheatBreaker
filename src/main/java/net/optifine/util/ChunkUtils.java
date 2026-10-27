package net.optifine.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.optifine.Config;
import net.optifine.reflect.Reflector;
import net.optifine.reflect.ReflectorClass;
import net.optifine.reflect.ReflectorField;

public class ChunkUtils {
   public static ReflectorClass chunkClass = new ReflectorClass(Chunk.class);
   public static ReflectorField fieldHasEntities = findFieldHasEntities();
   public static ReflectorField fieldPrecipitationHeightMap = new ReflectorField(chunkClass, int[].class, 0);

   public static boolean hasEntities(Chunk chunk) {
      return Reflector.getFieldValueBoolean(chunk, fieldHasEntities, true);
   }

   public static int getPrecipitationHeight(Chunk chunk, BlockPos blockPos) {
      int[] fieldValue = (int[])Reflector.getFieldValue(chunk, fieldPrecipitationHeightMap);
      if (fieldValue != null && fieldValue.length == 256) {
         int n = blockPos.getX() & 15;
         int n2 = blockPos.getZ() & 15;
         int n3 = n | n2 << 4;
         int n4 = fieldValue[n3];
         if (n4 >= 0) {
            return n4;
         }

         BlockPos precipitationHeight = chunk.getPrecipitationHeight(blockPos);
         return precipitationHeight.getY();
      } else {
         return -1;
      }
   }

   public static ReflectorField findFieldHasEntities() {
      try {
         Chunk chunk = new Chunk((World)null, 0, 0);
         ArrayList list = new ArrayList();
         ArrayList list2 = new ArrayList();
         Field[] declaredFields = Chunk.class.getDeclaredFields();

         for (int i = 0; i < declaredFields.length; i++) {
            Field field = declaredFields[i];
            if (field.getType() == boolean.class) {
               field.setAccessible(true);
               list.add(field);
               list2.add(field.get(chunk));
            }
         }

         chunk.setHasEntities(false);
         ArrayList list3 = new ArrayList();

         for (Object obj : list) {
            Field field = (Field)obj;
            list3.add(field.get(chunk));
         }

         chunk.setHasEntities(true);
         ArrayList list4 = new ArrayList();

         for (Object obj : list) {
            Field field = (Field)obj;
            list4.add(field.get(chunk));
         }

         ArrayList list5 = new ArrayList();

         for (int i = 0; i < list.size(); i++) {
            Field field = (Field)list.get(i);
            Boolean bool = (Boolean)list3.get(i);
            Boolean bool2 = (Boolean)list4.get(i);
            if (!bool && bool2) {
               list5.add(field);
               Boolean bool3 = (Boolean)list2.get(i);
               field.set(chunk, bool3);
            }
         }

         if (list5.size() == 1) {
            Field field = (Field)list5.get(0);
            return new ReflectorField(field);
         }
      } catch (Exception e) {
         Config.warn(e.getClass().getName() + " " + e.getMessage());
      }

      Config.warn("Error finding Chunk.hasEntities");
      return new ReflectorField(new ReflectorClass(Chunk.class), "hasEntities");
   }
}
