package com.cheatbreaker.client.util;

import optifine.Utils.OS;

public class OperatingSystemDetector {
    public static OS getOS() {
        String toLowerCase = System.getProperty("os.name").toLowerCase();
        return toLowerCase.contains("win")
            ? OS.WINDOWS
            : (
                toLowerCase.contains("mac")
                    ? OS.MACOS
                    : (
                        !toLowerCase.contains("solaris") && !toLowerCase.contains("sunos")
                            ? (!toLowerCase.contains("linux") && !toLowerCase.contains("unix") ? OS.UNKNOWN : OS.LINUX)
                            : OS.SOLARIS
                    )
            );
    }
}
