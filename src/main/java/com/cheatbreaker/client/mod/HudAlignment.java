package com.cheatbreaker.client.mod;

public enum HudAlignment {
    BOTTOM("BOTTOM"),
    TOP("TOP"),
    CENTER("CENTER"),
    LEFT("LEFT"),
    RIGHT("RIGHT");

    public String text;

    HudAlignment(String str) {
        this.text = str;
    }

    public String getText() {
        return this.text;
    }
}
