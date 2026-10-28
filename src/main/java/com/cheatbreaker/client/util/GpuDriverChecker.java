package com.cheatbreaker.client.util;

import com.cheatbreaker.client.util.GpuDriverStatus;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import net.optifine.Config;

public class GpuDriverChecker {
    public static final String TEXT_XX_HEAPDUMPPATH_MOJANGTR = "-XX:HeapDumpPath=MojangTricksIntelDriversForPerformance_javaw.exe_minecraft.exe.heapdump";
    public static Pattern pattern = Pattern.compile("(\\d+\\.\\d+(\\.\\d+)?) - Build (\\d+\\.\\d+\\.\\d+\\.\\d+)");
    public static final String TEXT_4_6_0 = "4.6.0";

    public static GpuDriverStatus getGpuDriverStatus() {
        String string = Config.openGlVersion;
        String string2 = Config.openGlVendor;
        if (System.getProperty("os.name").contains("Windows")) {
            if (string2.contains("Intel")) {
                return GpuDriverStatus.ACCEPTED_DRIVER;
            }
            if (string2.contains("NVIDIA")) {
                return GpuDriverStatus.ACCEPTED_DRIVER;
            }
            if (string2.contains("AMD") || string2.contains("ATI")) {
                return GpuDriverStatus.ACCEPTED_DRIVER;
            }
        }
        return GpuDriverStatus.ACCEPTED_DRIVER;
    }

    public static boolean hasMojangDriverFlag() {
        return GpuDriverChecker.getText().contains(TEXT_XX_HEAPDUMPPATH_MOJANGTR);
    }

    public static GpuDriverStatus findGpuDriverStatus(Matcher matcher) {
        String string = matcher.group(1);
        if (!TEXT_4_6_0.equals(string)) {
            return GpuDriverStatus.ACCEPTED_DRIVER;
        }
        int[] nArray = GpuDriverChecker.getIntArray(matcher.group(3));
        int n = nArray[0];
        int n2 = nArray[2];
        int n3 = nArray[3];
        return GpuDriverChecker.findGpuDriverStatus(n, n2, n3);
    }

    public static String getText() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> list = runtimeMXBean.getInputArguments();
        List list2 = list.stream().filter(string -> string.startsWith("-X")).collect(Collectors.toList());
        return String.format("%d total; %s", list2.size(), String.join((CharSequence)" ", list2));
    }

    public static GpuDriverStatus findGpuDriverStatus(int n, int n2, int n3) {
        if (n != 30 && n != 31) {
            return GpuDriverStatus.ACCEPTED_DRIVER;
        }
        if (n2 == 100) {
            return n3 <= 9955 ? GpuDriverStatus.ACCEPTED_DRIVER : GpuDriverStatus.REQUIRES_DOWNGRADE;
        }
        if (n2 == 101) {
            if (n3 >= 5379) {
                return GpuDriverStatus.ACCEPTED_DRIVER;
            }
            if (n3 > 3000) {
                return GpuDriverStatus.REQUIRES_UPDATE;
            }
        }
        return GpuDriverStatus.REQUIRES_DOWNGRADE;
    }

    public GpuDriverChecker() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static int[] getIntArray(String string) {
        return Arrays.stream(string.split("\\.")).mapToInt(Integer::parseInt).toArray();
    }

    public static GpuDriverStatus findGpuDriverStatus(String string) {
        if (!GpuDriverChecker.hasMojangDriverFlag()) {
            return GpuDriverStatus.ACCEPTED_DRIVER;
        }
        Matcher matcher = pattern.matcher(string);
        if (!matcher.matches()) {
            return GpuDriverStatus.ACCEPTED_DRIVER;
        }
        return GpuDriverChecker.findGpuDriverStatus(matcher);
    }
}
