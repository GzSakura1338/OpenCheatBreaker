package com.cheatbreaker.client.emote;

public enum EmoteSource {
   CLIENT,
   PLUGIN,
   SERVER;

   public static EmoteSource fromName(String str) {
      return Enum.valueOf(EmoteSource.class, str);
   }
}
