package net.optifine.util;

import java.util.Arrays;
import java.util.HashSet;
import net.minecraft.client.settings.KeyBinding;

public class KeyUtils {
   public static void fixKeyConflicts(KeyBinding[] keyBindings, KeyBinding[] keyBindings2) {
      HashSet set = new HashSet();

      for (int i = 0; i < keyBindings2.length; i++) {
         KeyBinding keyBinding = keyBindings2[i];
         set.add(keyBinding.getKeyCode());
      }

      HashSet set2 = new HashSet<>(Arrays.asList(keyBindings));
      set2.removeAll(Arrays.asList(keyBindings2));

      for (KeyBinding keyBinding : (Iterable<KeyBinding>)set2) {
         Integer keyCode = keyBinding.getKeyCode();
         if (set.contains(keyCode)) {
            keyBinding.setKeyCode(0);
         }
      }
   }
}
