package net.minecraft.util;

public class Util {
    public static Util.EnumOS getOSType() {
        String toLowerCase = System.getProperty("os.name").toLowerCase();
        return toLowerCase.contains("win")
            ? Util.EnumOS.WINDOWS
            : (
                toLowerCase.contains("mac")
                    ? Util.EnumOS.OSX
                    : (
                        toLowerCase.contains("solaris")
                            ? Util.EnumOS.SOLARIS
                            : (
                                toLowerCase.contains("sunos")
                                    ? Util.EnumOS.SOLARIS
                                    : (toLowerCase.contains("linux") ? Util.EnumOS.LINUX : (toLowerCase.contains("unix") ? Util.EnumOS.LINUX : Util.EnumOS.UNKNOWN))
                            )
                    )
            );
    }

    public enum EnumOS {
        LINUX("LINUX", 0),
        SOLARIS("SOLARIS", 1),
        WINDOWS("WINDOWS", 2),
        OSX("OSX", 3),
        UNKNOWN("UNKNOWN", 4);

        EnumOS(String str, int n) {
        }
    }
}
