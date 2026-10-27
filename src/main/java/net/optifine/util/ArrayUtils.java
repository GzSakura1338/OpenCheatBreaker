package net.optifine.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

public class ArrayUtils {
   public static boolean contains(Object[] objs, Object obj) {
      if (objs == null) {
         return false;
      }

      for (int i = 0; i < objs.length; i++) {
         Object obj2 = objs[i];
         if (obj2 == obj) {
            return true;
         }
      }

      return false;
   }

   public static int[] addIntsToArray(int[] ints, int[] ints2) {
      if (ints != null && ints2 != null) {
         int length = ints.length;
         int n = length + ints2.length;
         int[] ints3 = new int[n];
         System.arraycopy(ints, 0, ints3, 0, length);

         for (int i = 0; i < ints2.length; i++) {
            ints3[i + length] = ints2[i];
         }

         return ints3;
      } else {
         throw new NullPointerException("The given array is NULL");
      }
   }

   public static int[] addIntToArray(int[] ints, int n) {
      return addIntsToArray(ints, new int[]{n});
   }

   public static Object[] addObjectsToArray(Object[] objs, Object[] objs2) {
      if (objs == null) {
         throw new NullPointerException("The given array is NULL");
      }

      if (objs2.length == 0) {
         return objs;
      }

      int length = objs.length;
      int n = length + objs2.length;
      Object[] newInstance = (Object[])Array.newInstance(objs.getClass().getComponentType(), n);
      System.arraycopy(objs, 0, newInstance, 0, length);
      System.arraycopy(objs2, 0, newInstance, length, objs2.length);
      return newInstance;
   }

   public static Object[] addObjectToArray(Object[] objs, Object obj) {
      if (objs == null) {
         throw new NullPointerException("The given array is NULL");
      }

      int length = objs.length;
      int n = length + 1;
      Object[] newInstance = (Object[])Array.newInstance(objs.getClass().getComponentType(), n);
      System.arraycopy(objs, 0, newInstance, 0, length);
      newInstance[length] = obj;
      return newInstance;
   }

   public static Object[] addObjectToArray(Object[] objs, Object obj, int n) {
      ArrayList list = new ArrayList<>(Arrays.asList(objs));
      list.add(n, obj);
      Object[] newInstance = (Object[])Array.newInstance(objs.getClass().getComponentType(), list.size());
      return list.toArray(newInstance);
   }

   public static String arrayToString(boolean[] flags, String str) {
      if (flags == null) {
         return "";
      }

      StringBuffer sb = new StringBuffer(flags.length * 5);

      for (int i = 0; i < flags.length; i++) {
         boolean flag = flags[i];
         if (i > 0) {
            sb.append(str);
         }

         sb.append(String.valueOf(flag));
      }

      return sb.toString();
   }

   public static String arrayToString(float[] floats) {
      return arrayToString(floats, ", ");
   }

   public static String arrayToString(float[] floats, String str) {
      if (floats == null) {
         return "";
      }

      StringBuffer sb = new StringBuffer(floats.length * 5);

      for (int i = 0; i < floats.length; i++) {
         float f = floats[i];
         if (i > 0) {
            sb.append(str);
         }

         sb.append(String.valueOf(f));
      }

      return sb.toString();
   }

   public static String arrayToString(float[] floats, String str, String str2) {
      if (floats == null) {
         return "";
      }

      StringBuffer sb = new StringBuffer(floats.length * 5);

      for (int i = 0; i < floats.length; i++) {
         float f = floats[i];
         if (i > 0) {
            sb.append(str);
         }

         sb.append(String.format(str2, f));
      }

      return sb.toString();
   }

   public static String arrayToString(int[] ints) {
      return arrayToString(ints, ", ");
   }

   public static String arrayToString(int[] ints, String str) {
      if (ints == null) {
         return "";
      }

      StringBuffer sb = new StringBuffer(ints.length * 5);

      for (int i = 0; i < ints.length; i++) {
         int n = ints[i];
         if (i > 0) {
            sb.append(str);
         }

         sb.append(String.valueOf(n));
      }

      return sb.toString();
   }

   public static String arrayToHexString(int[] ints, String str) {
      if (ints == null) {
         return "";
      }

      StringBuffer sb = new StringBuffer(ints.length * 5);

      for (int i = 0; i < ints.length; i++) {
         int n = ints[i];
         if (i > 0) {
            sb.append(str);
         }

         sb.append("0x");
         sb.append(Integer.toHexString(n));
      }

      return sb.toString();
   }

   public static String arrayToString(Object[] objs) {
      return arrayToString(objs, ", ");
   }

   public static String arrayToString(Object[] objs, String str) {
      if (objs == null) {
         return "";
      }

      StringBuffer sb = new StringBuffer(objs.length * 5);

      for (int i = 0; i < objs.length; i++) {
         Object obj = objs[i];
         if (i > 0) {
            sb.append(str);
         }

         sb.append(String.valueOf(obj));
      }

      return sb.toString();
   }

   public static Object[] collectionToArray(Collection collection, Class clazz) {
      if (collection == null) {
         return null;
      }

      if (clazz == null) {
         return null;
      }

      if (clazz.isPrimitive()) {
         throw new IllegalArgumentException("Can not make arrays with primitive elements (int, double), element class: " + clazz);
      }

      Object[] newInstance = (Object[])Array.newInstance(clazz, collection.size());
      return collection.toArray(newInstance);
   }

   public static boolean equalsOne(int n, int[] ints) {
      for (int i = 0; i < ints.length; i++) {
         if (ints[i] == n) {
            return true;
         }
      }

      return false;
   }

   public static boolean equals(Object obj, Object obj2) {
      return obj == obj2 ? true : (obj == null ? false : obj.equals(obj2));
   }

   public static Object[] removeObjectFromArray(Object[] objs, Object obj) {
      ArrayList list = new ArrayList<>(Arrays.asList(objs));
      list.remove(obj);
      return collectionToArray(list, objs.getClass().getComponentType());
   }

   public static int[] toPrimitive(Integer[] integers) {
      if (integers == null) {
         return null;
      }

      if (integers.length == 0) {
         return new int[0];
      }

      int[] ints = new int[integers.length];

      for (int i = 0; i < ints.length; i++) {
         ints[i] = integers[i];
      }

      return ints;
   }

   public static boolean checkObject(Object obj, Object[] objs) {
      if (objs == null) {
         return false;
      }

      for (int i = 0; i < objs.length; i++) {
         Object obj2 = objs[i];
         if (obj == obj2) {
            return true;
         }
      }

      return false;
   }

   public static boolean equals(Object obj, Object[] objs) {
      if (objs == null) {
         return false;
      }

      for (int i = 0; i < objs.length; i++) {
         Object obj2 = objs[i];
         if (equals(obj, obj2)) {
            return true;
         }
      }

      return false;
   }
}
