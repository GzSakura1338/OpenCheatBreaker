package com.cheatbreaker.client.util;

import java.io.File;
import net.minecraft.util.Util.EnumOS;

public class DirectoryHelper {
    public static EnumOS getEnumOS() {
        String toLowerCase = System.getProperty("os.name").toLowerCase();
        return toLowerCase.contains("unix")
            ? EnumOS.LINUX
            : (
                toLowerCase.contains("linux")
                    ? EnumOS.LINUX
                    : (
                        toLowerCase.contains("sunos")
                            ? EnumOS.SOLARIS
                            : (
                                toLowerCase.contains("solaris")
                                    ? EnumOS.SOLARIS
                                    : (toLowerCase.contains("mac") ? EnumOS.OSX : (toLowerCase.contains("win") ? EnumOS.WINDOWS : EnumOS.UNKNOWN))
                            )
                    )
            );
    }

    public static File getAppDirectory(String str, boolean flag) {
        String property = System.getProperty("user.home", ".");
        File file;
        switch (getEnumOS()) {
            case LINUX:
            case SOLARIS:
                file = new File(property, '.' + str + '/');
                break;
            case WINDOWS:
                String getenv = System.getenv("APPDATA");
                if (getenv != null) {
                    file = new File(getenv, "." + str + '/');
                } else {
                    file = new File(property, '.' + str + '/');
                }
                break;
            case OSX:
                file = new File(property, "Library/Application Support/" + str);
                break;
            default:
                file = new File(property, str + '/');
        }

        if (flag && !file.exists() && !file.mkdirs()) {
            throw new RuntimeException("The working directory could not be created: " + file);
        } else {
            return file;
        }
    }
}
