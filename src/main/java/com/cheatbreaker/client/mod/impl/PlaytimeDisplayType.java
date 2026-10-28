package com.cheatbreaker.client.mod.impl;

public enum PlaytimeDisplayType {
    DEFAULT("12:34:56", "%d:%02d:%02d", false),
    COMPACT_1("12:34", "%d:%02d", false, false),
    COMPACT_2("12:34:56.789", "%d:%02d:%02d.%03d"),
    COMPACT_3("12:34:56:789", "%d:%02d:%02d:%03d"),
    SPREAD_1("12 : 34 : 56", "%d : %02d : %02d", false),
    SPREAD_2("12 : 34", "%d : %02d", false, false),
    SPREAD_3("12 : 34 : 56 . 789", "%d : %02d : %02d . %03d"),
    SPREAD_4("12 : 34 : 56 : 789", "%d : %02d : %02d : %03d"),
    EASY_1("12h 34m", "%2dh %2dm", false, false),
    EASY_2("12h 34m 56s", "%2dh %2dm %02ds", false),
    EASY_3("12h 34m 56s 789ms", "%2dh %2dm %2ds %03dms"),
    STOPWATCH("123.45s", "%d.%03ds", true, true, false),
    MILLISECONDS("12345ms", "%dms", true, false, false);

    public String name;
    public String format;
    public boolean hasHours;
    public boolean hasSeconds;
    public boolean hasMillis;

    public boolean hasMillis() {
        return this.hasMillis;
    }

    public String getName() {
        return this.name;
    }

    PlaytimeDisplayType(String str, String str2, boolean flag, boolean flag2) {
        this(str, str2, flag, flag2, true);
    }

    public String getFormat() {
        return this.format;
    }

    public static PlaytimeDisplayType findPlaytimeDisplayType(String str, PlaytimeDisplayType playtimeDisplayType) {
        try {
            return valueOf(str);
        } catch (IllegalArgumentException e) {
            return playtimeDisplayType;
        }
    }

    PlaytimeDisplayType(String str, String str2) {
        this(str, str2, true);
    }

    public boolean hasSeconds() {
        return this.hasSeconds;
    }

    PlaytimeDisplayType(String str, String str2, boolean flag, boolean flag2, boolean flag3) {
        this.name = str;
        this.format = str2;
        this.hasHours = flag3;
        this.hasSeconds = flag2;
        this.hasMillis = flag;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public boolean hasHours() {
        return this.hasHours;
    }

    PlaytimeDisplayType(String str, String str2, boolean flag) {
        this(str, str2, flag, true);
    }
}
