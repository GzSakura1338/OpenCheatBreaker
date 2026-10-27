package net.optifine.util;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import net.minecraft.block.state.IBlockState;
import net.optifine.Config;

public class CacheObjectArray {
   public static ArrayDeque<int[]> arrays = new ArrayDeque<>();
   public static int maxCacheSize = 10;

   public static synchronized int[] allocateArray(int n) {
      int[] pollLast = arrays.pollLast();
      if (pollLast == null || pollLast.length < n) {
         pollLast = new int[n];
      }

      return pollLast;
   }

   public static synchronized void freeArray(int[] ints) {
      if (arrays.size() < maxCacheSize) {
         arrays.add(ints);
      }
   }

   public static void main(String[] strs) throws Exception {
      short s = 4096;
      int n = 500000;
      testNew(s, n);
      testClone(s, n);
      testNewObj(s, n);
      testCloneObj(s, n);
      testNewObjDyn(IBlockState.class, s, n);
      long testNew2 = testNew(s, n);
      long testClone2 = testClone(s, n);
      long testNewObj2 = testNewObj(s, n);
      long testCloneObj2 = testCloneObj(s, n);
      long testNewObjDyn2 = testNewObjDyn(IBlockState.class, s, n);
      Config.dbg("New: " + testNew2);
      Config.dbg("Clone: " + testClone2);
      Config.dbg("NewObj: " + testNewObj2);
      Config.dbg("CloneObj: " + testCloneObj2);
      Config.dbg("NewObjDyn: " + testNewObjDyn2);
   }

   public static long testClone(int n, int n2) {
      long currentTimeMillis = System.currentTimeMillis();
      int[] ints = new int[n];

      for (int i = 0; i < n2; i++) {
         int[] ints2 = (int[])ints.clone();
      }

      long currentTimeMillis2 = System.currentTimeMillis();
      return currentTimeMillis2 - currentTimeMillis;
   }

   public static long testNew(int n, int n2) {
      long currentTimeMillis = System.currentTimeMillis();

      for (int i = 0; i < n2; i++) {
         int[] newInstance = (int[])Array.newInstance(int.class, n);
      }

      long currentTimeMillis2 = System.currentTimeMillis();
      return currentTimeMillis2 - currentTimeMillis;
   }

   public static long testCloneObj(int n, int n2) {
      long currentTimeMillis = System.currentTimeMillis();
      IBlockState[] blockStates = new IBlockState[n];

      for (int i = 0; i < n2; i++) {
         IBlockState[] blockStates2 = (IBlockState[])blockStates.clone();
      }

      long currentTimeMillis2 = System.currentTimeMillis();
      return currentTimeMillis2 - currentTimeMillis;
   }

   public static long testNewObj(int n, int n2) {
      long currentTimeMillis = System.currentTimeMillis();

      for (int i = 0; i < n2; i++) {
         IBlockState[] blockStates = new IBlockState[n];
      }

      long currentTimeMillis2 = System.currentTimeMillis();
      return currentTimeMillis2 - currentTimeMillis;
   }

   public static long testNewObjDyn(Class clazz, int n, int n2) {
      long currentTimeMillis = System.currentTimeMillis();

      for (int i = 0; i < n2; i++) {
         Object[] newInstance = (Object[])Array.newInstance(clazz, n);
      }

      long currentTimeMillis2 = System.currentTimeMillis();
      return currentTimeMillis2 - currentTimeMillis;
   }
}
