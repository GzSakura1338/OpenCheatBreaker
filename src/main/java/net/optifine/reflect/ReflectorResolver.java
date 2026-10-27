package net.optifine.reflect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReflectorResolver {
   public static List<IResolvable> RESOLVABLES = Collections.synchronizedList(new ArrayList<>());
   public static boolean resolved = false;

   public static void register(IResolvable resolvable) {
      if (!resolved) {
         RESOLVABLES.add(resolvable);
      } else {
         resolvable.resolve();
      }
   }

   public static void resolve() {
      if (!resolved) {
         for (IResolvable resolvable : RESOLVABLES) {
            resolvable.resolve();
         }

         resolved = true;
      }
   }
}
