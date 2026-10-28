package com.cheatbreaker.client.util;

public enum OperatingSystem {
    LINUX("LINUX", 0),
    SOLARIS("SOLARIS", 1),
    WINDOWS("WINDOWS", 2),
    MACOS("MACOS", 3),
    UNKNOWN("UNKNOWN", 4);

    public static OperatingSystem[] operatingSystemArray2 = new OperatingSystem[]{LINUX, SOLARIS, WINDOWS, MACOS, UNKNOWN};

    OperatingSystem(String str, int n) {
    }
}
