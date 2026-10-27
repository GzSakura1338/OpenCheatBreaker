package net.optifine.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import net.optifine.Config;

public class NativeMemory {
   public static LongSupplier bufferAllocatedSupplier = makeLongSupplier(
      new String[][]{
         {"sun.misc.SharedSecrets", "getJavaNioAccess", "getDirectBufferPool", "getMemoryUsed"},
         {"jdk.internal.misc.SharedSecrets", "getJavaNioAccess", "getDirectBufferPool", "getMemoryUsed"}
      }
   );
   public static LongSupplier bufferMaximumSupplier = makeLongSupplier(
      new String[][]{{"sun.misc.VM", "maxDirectMemory"}, {"jdk.internal.misc.VM", "maxDirectMemory"}}
   );

   public static long getBufferAllocated() {
      return bufferAllocatedSupplier == null ? -1L : bufferAllocatedSupplier.getAsLong();
   }

   public static long getBufferMaximum() {
      return bufferMaximumSupplier == null ? -1L : bufferMaximumSupplier.getAsLong();
   }

   public static LongSupplier makeLongSupplier(String[][] strses) {
      ArrayList list = new ArrayList();

      for (int i = 0; i < strses.length; i++) {
         String[] strs = strses[i];

         try {
            return makeLongSupplier(strs);
         } catch (Throwable e) {
            list.add(e);
         }
      }

      for (Throwable throwable : (Iterable<Throwable>)list) {
         Config.warn("" + throwable.getClass().getName() + ": " + throwable.getMessage());
      }

      return null;
   }

   public static LongSupplier makeLongSupplier(String[] strs) throws Exception {
      if (strs.length < 2) {
         return null;
      }

      Class forName = Class.forName(strs[0]);
      Method method = forName.getMethod(strs[1]);
      method.setAccessible(true);
      Object obj = null;

      for (int i = 2; i < strs.length; i++) {
         String str = strs[i];
         obj = method.invoke(obj);
         method = obj.getClass().getMethod(str);
         method.setAccessible(true);
      }

      final Method methodRef2 = method;
      final Object obj2 = obj;
      return new LongSupplier() {
         public boolean disabled = false;

         @Override
         public long getAsLong() {
            if (this.disabled) {
               return -1L;
            }

            try {
               return (Long)methodRef2.invoke(obj2);
            } catch (Throwable e) {
               Config.warn("" + e.getClass().getName() + ": " + e.getMessage());
               this.disabled = true;
               return -1L;
            }
         }
      };
   }
}
