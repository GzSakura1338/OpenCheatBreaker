package com.cheatbreaker.client.emote;

public class EmoteInitializer {
    public static EmoteInitializer emoteInitializer;
    public static String text;
    public static boolean flag = false;

    public EmoteInitializer() {
        emoteInitializer = this;
        EmoteRegistry.registerDefaults();
        new EmoteModelLoader().load();
    }
}
