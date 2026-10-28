package com.cheatbreaker.client.title;

import com.cheatbreaker.client.mod.impl.TitleType;

public class Title {
    public long createdAt = System.currentTimeMillis();
    public String message;
    public float scale;
    public TitleType titleType;
    public long displayTime;
    public long fadeInTime;
    public long fadeOutTime;

    public long getFadeInTime() {
        return this.fadeInTime;
    }

    public long getFadeOutTime() {
        return this.fadeOutTime;
    }

    public Title(String str, TitleType titleType, float f, long l, long l2, long l3) {
        this.message = str;
        this.scale = f;
        this.titleType = titleType;
        this.displayTime = l == 0L ? 5000L : l;
        this.fadeInTime = l2;
        this.fadeOutTime = l3;
    }

    public boolean isFadingOut() {
        return System.currentTimeMillis() > this.createdAt + this.displayTime - this.fadeOutTime;
    }

    public static long getFadeOutTime(Title title) {
        return title.fadeOutTime;
    }

    public boolean isFadingIn() {
        return System.currentTimeMillis() < this.createdAt + this.fadeInTime;
    }

    public String getMessage() {
        return this.message;
    }

    public float getScale() {
        return this.scale;
    }

    public static TitleType getType(Title title) {
        return title.titleType;
    }

    public TitleType getTitleType() {
        return this.titleType;
    }

    public static float getScale(Title title) {
        return title.scale;
    }

    public long getDisplayTime() {
        return this.displayTime;
    }

    public static long getFadeInTime(Title title) {
        return title.fadeInTime;
    }

    public static String getMessage(Title title) {
        return title.message;
    }

    public static long getDisplayTime(Title title) {
        return title.displayTime;
    }
}
