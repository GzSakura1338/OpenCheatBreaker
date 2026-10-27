package com.cheatbreaker.client.emote;

public class EmoteInitializer {
   public static EmoteInitializer emoteInitializer;
   public static boolean flag = false;
   public static String text;

   public EmoteInitializer() {
      emoteInitializer = this;
      EmoteRegistry.registerDefaults();
      new EmoteModelLoader().run();
   }
}
