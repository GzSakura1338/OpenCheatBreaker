package net.minecraft.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import org.apache.logging.log4j.Logger;

public class Util {
   public static Util.EnumOS getOSType() {
      String toLowerCase = System.getProperty("os.name").toLowerCase();
      return toLowerCase.contains("win")
         ? Util.EnumOS.WINDOWS
         : (
            toLowerCase.contains("mac")
               ? Util.EnumOS.OSX
               : (
                  toLowerCase.contains("solaris")
                     ? Util.EnumOS.SOLARIS
                     : (
                        toLowerCase.contains("sunos")
                           ? Util.EnumOS.SOLARIS
                           : (toLowerCase.contains("linux") ? Util.EnumOS.LINUX : (toLowerCase.contains("unix") ? Util.EnumOS.LINUX : Util.EnumOS.UNKNOWN))
                     )
               )
         );
   }

   public static <V> V runTask(FutureTask<V> task, Logger logger) {
      try {
         task.run();
         return (V)task.get();
      } catch (ExecutionException e) {
         logger.fatal("Error executing task", e);
         if (e.getCause() instanceof OutOfMemoryError) {
            OutOfMemoryError cause = (OutOfMemoryError)e.getCause();
            throw cause;
         }
      } catch (InterruptedException e) {
         logger.fatal("Error executing task", e);
      }

      return null;
   }

   public enum EnumOS {
      LINUX,
      SOLARIS,
      WINDOWS,
      OSX,
      UNKNOWN;
   }
}
