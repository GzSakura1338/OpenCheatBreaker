package com.cheatbreaker.client.emote;

import java.lang.reflect.Method;
import net.minecraft.block.Block;

public class BlockReflection {
   public static Method methodRef;

   public static void handleBlock(Block block, int n) {
      try {
         if (methodRef == null) {
            methodRef = Block.class.getDeclaredMethod("setLightOpacity", int.class);
            methodRef.setAccessible(true);
         }

         methodRef.invoke(block, n);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }
}
