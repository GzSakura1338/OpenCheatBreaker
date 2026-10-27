package com.cheatbreaker.client.manager;

public enum BlockedServerLevel {
   MARK,
   MINOR,
   UNSAFE,
   BLOCK;

   public static BlockedServerLevel fromName(String str) {
      return Enum.valueOf(BlockedServerLevel.class, str);
   }
}
