package com.cheatbreaker.client.ui.screen;

public enum StatusLevel {
    UP("green"),
    DOWN("red"),
    BUSY("yellow"),
    UNKNOWN("unknown");

    public String name;

    public static StatusLevel findStatusLevel(String str) {
        for (StatusLevel statusLevel : values()) {
            if (statusLevel.getName().equalsIgnoreCase(str)) {
                return statusLevel;
            }
        }

        return null;
    }

    public String getName() {
        return this.name;
    }

    StatusLevel(String str) {
        this.name = str;
    }
}
