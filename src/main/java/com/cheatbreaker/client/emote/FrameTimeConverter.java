package com.cheatbreaker.client.emote;

public class FrameTimeConverter {
    public static int framesToTicks(int n) {
        return (int)Math.floor(n / 30.0F * 20.0F);
    }
}
