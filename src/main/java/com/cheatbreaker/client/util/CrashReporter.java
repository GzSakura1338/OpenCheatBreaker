package com.cheatbreaker.client.util;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.network.packet.CrashReportPacket;
import com.cheatbreaker.client.util.CrashCauseHints;
import com.cheatbreaker.client.util.ExceptionHints;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;
import net.minecraft.client.Minecraft;
import net.optifine.Config;

public class CrashReporter {
    public static final String CRASH_ID_FORMAT = "CB-CRASH-%s";
    public static final long EXIT_DELAY_MS = 7000L;
    public static final String DEFAULT_TITLE = "Something went wrong";
    public static Minecraft mc = Minecraft.getMinecraft();
    public static final String MEMORY_FORMAT = "%dMB/%dMB of %dMB";
    public static Map<String, String> exceptionHints = new ExceptionHints();
    public static final String DEFAULT_MESSAGE = "Your client has crashed.";
    public static Map<String, String> crashCauseHints = new CrashCauseHints();

    public static void sleepBeforeExit() {
        try {
            Thread.sleep(7000L);
        }
        catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

    public static CrashReportPacket createReport(UUID uUID, String string) {
        String string2 = CrashReporter.getMemoryInfo();
        return new CrashReportPacket(uUID.toString(), "OptiFine_1.7.10_HD_U_E7", CrashReporter.getServerAddress(), CheatBreaker.getInstance().getClientVersion(), CheatBreaker.getInstance().getGitCommit(), CheatBreaker.getInstance().getBranch(), CrashReporter.getOsInfo(), CrashReporter.getText(), CrashReporter.getGpuInfo(), string2, CrashReporter.getJavaInfo(), CrashReporter.getJvmInfo(), CrashReporter.getJvmArguments(), string);
    }

    public static String getGpuInfo() {
        return String.format("%s (%s), %s", Config.openGlRenderer, Config.openGlVendor, Config.openGlVersion);
    }

    public static String getJvmInfo() {
        return String.format("%s (%s), %s", System.getProperty("java.vm.name"), System.getProperty("java.vm.info"), System.getProperty("java.vm.vendor"));
    }

    public static String getJvmArguments() {
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        List<String> list = runtimeMXBean.getInputArguments();
        List list2 = list.stream().filter(string -> string.startsWith("-X")).collect(Collectors.toList());
        return String.format("%d total; %s", list2.size(), String.join((CharSequence)" ", list2));
    }

    public static void copyToClipboard(StringSelection stringSelection) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    public static void openLink(URI uRI) {
        try {
            Class<?> clazz = Class.forName("java.awt.Desktop");
            Object object = clazz.getMethod("getDesktop", new Class[0]).invoke(null, new Object[0]);
            clazz.getMethod("browse", URI.class).invoke(object, uRI);
        }
        catch (Throwable throwable) {
            CheatBreaker.getInstance().getLogger().error("Couldn't open link", throwable);
        }
    }

    public static String getText() {
        return String.format("%d logical processors", Config.getAvailableProcessors());
    }

    public static boolean shouldSkipReport(Throwable throwable) {
        return CheatBreaker.getInstance() == null || CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet() == null || !CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().isOpen() || throwable == null || throwable.getClass() == Throwable.class || throwable.getClass().getName().contains(".fml.client.SplashProgress");
    }

    public static void sendReport(CrashReportPacket crashReportPacket) {
        CheatBreaker.getInstance().getPlayerassetsCheatbreakerNet().sendPacket(crashReportPacket);
    }

    public static void handleCrash(Throwable throwable, String string) {
        String string2;
        int n;
        float f;
        if (CrashReporter.shouldSkipReport(throwable)) {
            return;
        }
        String string3 = CrashReporter.getOsInfo();
        if (string3.startsWith("Windows") && (f = Float.parseFloat(string3.split(" ")[1])) < (float)(n = 10)) {
            CrashReporter.showDialog("UNSUPPORTED", "Sorry, but you are not eligible for support.\n\nPlease update to Windows " + n + " or newer to receive support and avoid crashes.");
            CrashReporter.sleepBeforeExit();
            return;
        }
        String string4 = throwable.getClass().getSimpleName();
        String string5 = exceptionHints.getOrDefault(string4, DEFAULT_MESSAGE);
        boolean bl = crashCauseHints.entrySet().stream().anyMatch(entry -> string4.contains((CharSequence)entry.getKey()) || string.contains((CharSequence)entry.getKey()));
        if (bl) {
            string2 = CrashReporter.getReadableName(string4);
            string5 = string5 + "\n\nRead our Self Diagnostics for a potential solution";
            try {
                CrashReporter.openLink(new URI("https://cheatbreaker.net/self-diagnostics"));
            }
            catch (URISyntaxException uRISyntaxException) {}
        } else {
            UUID uUID = UUID.randomUUID();
            String string6 = String.format(CRASH_ID_FORMAT, uUID);
            CrashReporter.copyToClipboard(new StringSelection(string6));
            CrashReportPacket crashReportPacket = CrashReporter.createReport(uUID, string);
            CrashReporter.sendReport(crashReportPacket);
            string2 = exceptionHints.containsKey(string4) ? CrashReporter.getReadableName(string4) : DEFAULT_TITLE;
            string5 = string5 + "\n\nPlease use the following code (also copied to your clipboard) when submitting a bug report:\n\n" + string6;
        }
        CrashReporter.showDialog(string2, string5);
        CrashReporter.sleepBeforeExit();
    }

    public static String getMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        long l = runtime.maxMemory() / 1024L / 1024L;
        long l2 = runtime.totalMemory() / 1024L / 1024L;
        long l3 = runtime.freeMemory() / 1024L / 1024L;
        return String.format(MEMORY_FORMAT, l3, l2, l);
    }

    public static String getJavaInfo() {
        return String.format("%s, %s", System.getProperty("java.version"), System.getProperty("java.vendor"));
    }

    public static String getServerAddress() {
        if (mc.isSingleplayer()) {
            return "Singleplayer";
        }
        if (mc.getCurrentServerData() != null) {
            return CrashReporter.mc.getCurrentServerData().serverIP;
        }
        return "Unknown";
    }

    public static String getOsInfo() {
        return String.format("%s (%s) version %s", System.getProperty("os.name"), System.getProperty("os.arch"), System.getProperty("os.version"));
    }

    public static void showDialog(String string, String string2) {
        new Thread(() -> JOptionPane.showMessageDialog(null, string2, string, 0)).start();
    }

    public CrashReporter() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static String getReadableName(String string) {
        String string2;
        if (!string.contains(" ")) {
            string2 = string.replaceAll("(Error|Exception)$", "");
            string2 = string2.replaceAll("(\\p{Ll})(\\p{Lu})", "$1 $2");
        } else {
            string2 = DEFAULT_TITLE;
        }
        return string2;
    }
}
