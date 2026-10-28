package net.minecraft.crash;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.util.ReportedException;
import net.minecraft.world.gen.layer.IntCache;
import net.optifine.CrashReporter;
import net.optifine.Reflector;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport {
    public static Logger logger = LogManager.getLogger();
    public CrashReportCategory theReportCategory = new CrashReportCategory(this, "System Details");
    public List crashReportSections = new ArrayList();
    public boolean hasStackTrace = true;
    public StackTraceElement[] stacktrace = new StackTraceElement[0];
    public File crashReportFile;
    public String description;
    public Throwable cause;
    public static boolean flag = false;

    public CrashReport(String str, Throwable throwable) {
        this.description = str;
        this.cause = throwable;
        this.populateEnvironment();
    }

    public void populateEnvironment() {
        this.theReportCategory.addCrashSectionCallable("Minecraft Version", () -> "1.7.10");
        this.theReportCategory
            .addCrashSectionCallable(
                "Operating System", () -> System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version")
            );
        this.theReportCategory.addCrashSectionCallable("Java Version", () -> System.getProperty("java.version") + ", " + System.getProperty("java.vendor"));
        this.theReportCategory
            .addCrashSectionCallable(
                "Java VM Version",
                () -> System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor")
            );
        this.theReportCategory.addCrashSectionCallable("Memory", () -> {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long l = maxMemory / 1024L / 1024L;
            long l2 = totalMemory / 1024L / 1024L;
            long l3 = freeMemory / 1024L / 1024L;
            return freeMemory + " bytes (" + l3 + " MB) / " + totalMemory + " bytes (" + l2 + " MB) up to " + maxMemory + " bytes (" + l + " MB)";
        });
        this.theReportCategory.addCrashSectionCallable("JVM Flags", () -> {
            RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
            List inputArguments = runtimeMXBean.getInputArguments();
            int n = 0;
            StringBuilder sb = new StringBuilder();

            for (String str : (Iterable<String>)inputArguments) {
                if (str.startsWith("-X")) {
                    if (n++ > 0) {
                        sb.append(" ");
                    }

                    sb.append(str);
                }
            }

            return String.format("%d total; %s", n, sb.toString());
        });
        this.theReportCategory.addCrashSectionCallable("AABB Pool Size", () -> {
            byte b = 0;
            int n = 56 * b;
            int n2 = n / 1024 / 1024;
            byte b2 = 0;
            int n3 = 56 * b2;
            int n4 = n3 / 1024 / 1024;
            return b + " (" + n + " bytes; " + n2 + " MB) allocated, " + b2 + " (" + n3 + " bytes; " + n4 + " MB) used";
        });
        this.theReportCategory.addCrashSectionCallable("IntCache", new Callable() {
            public String call() throws IllegalAccessException, IllegalArgumentException, NoSuchFieldException, SecurityException {
                return IntCache.getCacheSizes();
            }
        });
        if (Reflector.FMLCommonHandler_enhanceCrashReport.exists()) {
            Object obj = Reflector.call(Reflector.FMLCommonHandler_instance);
            Reflector.callString(obj, Reflector.FMLCommonHandler_enhanceCrashReport, this, this.theReportCategory);
        }
    }

    public String getDescription() {
        return this.description;
    }

    public Throwable getCrashCause() {
        return this.cause;
    }

    public void getSectionsInStringBuilder(StringBuilder sb) {
        if ((this.stacktrace == null || this.stacktrace.length <= 0) && this.crashReportSections.size() > 0) {
            this.stacktrace = (StackTraceElement[])ArrayUtils.subarray(((CrashReportCategory)this.crashReportSections.get(0)).getStackTrace(), 0, 1);
        }

        if (this.stacktrace != null && this.stacktrace.length > 0) {
            sb.append("-- Head --\n");
            sb.append("Stacktrace:\n");

            for (StackTraceElement stackTraceElement : this.stacktrace) {
                sb.append("\t").append("at ").append(stackTraceElement.toString());
                sb.append("\n");
            }

            sb.append("\n");
        }

        for (CrashReportCategory crashReportCategory : (Iterable<CrashReportCategory>)this.crashReportSections) {
            crashReportCategory.appendToStringBuilder(sb);
            sb.append("\n\n");
        }

        this.theReportCategory.appendToStringBuilder(sb);
    }

    public String getCauseStackTraceOrString() {
        StringWriter stringWriter = null;
        PrintWriter printWriter = null;
        Throwable cause = this.cause;
        if (cause.getMessage() == null) {
            if (cause instanceof NullPointerException) {
                cause = new NullPointerException(this.description);
            } else if (cause instanceof StackOverflowError) {
                cause = new StackOverflowError(this.description);
            } else if (cause instanceof OutOfMemoryError) {
                cause = new OutOfMemoryError(this.description);
            }

            cause.setStackTrace(this.cause.getStackTrace());
        }

        String str = cause.toString();

        try {
            stringWriter = new StringWriter();
            printWriter = new PrintWriter(stringWriter);
            cause.printStackTrace(printWriter);
            str = stringWriter.toString();
        } finally {
            IOUtils.closeQuietly(stringWriter);
            IOUtils.closeQuietly(printWriter);
        }

        return str;
    }

    public String getCompleteReport() {
        String causeStackTraceOrString = this.getCauseStackTraceOrString();
        if (!flag) {
            flag = true;
            CrashReporter.onCrashReport(this, this.theReportCategory);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("---- Minecraft Crash Report ----\n");
        sb.append("// ");
        sb.append(getWittyComment());
        sb.append("\n\n");
        sb.append("Time: ");
        sb.append(new SimpleDateFormat().format(new Date()));
        sb.append("\n");
        sb.append("Description: ");
        sb.append(this.description);
        sb.append("\n\n");
        sb.append(causeStackTraceOrString);
        sb.append("\n\nA detailed walkthrough of the error, its code path and all known details is as follows:\n");

        for (int i = 0; i < 87; i++) {
            sb.append("-");
        }

        sb.append("\n\n");
        this.getSectionsInStringBuilder(sb);
        return sb.toString();
    }

    public File getFile() {
        return this.crashReportFile;
    }

    public boolean saveToFile(File file) {
        if (this.crashReportFile != null) {
            return false;
        }

        if (file.getParentFile() != null) {
            file.getParentFile().mkdirs();
        }

        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(this.getCompleteReport());
            fileWriter.close();
            this.crashReportFile = file;
            return true;
        } catch (Throwable e) {
            logger.error("Could not save crash report to " + file, e);
            return false;
        }
    }

    public CrashReportCategory getCategory() {
        return this.theReportCategory;
    }

    public CrashReportCategory makeCategory(String str) {
        return this.makeCategoryDepth(str, 1);
    }

    public CrashReportCategory makeCategoryDepth(String str, int n) {
        CrashReportCategory crashReportCategory = new CrashReportCategory(this, str);
        if (this.hasStackTrace) {
            int prunedStackTrace = crashReportCategory.getPrunedStackTrace(n);
            StackTraceElement[] stackTrace = this.cause.getStackTrace();
            StackTraceElement stackTraceElement = null;
            StackTraceElement stackTraceElement2 = null;
            int n2 = stackTrace.length - prunedStackTrace;
            if (n2 < 0) {
                System.out.println("Negative index in crash report handler (" + stackTrace.length + "/" + prunedStackTrace + ")");
            }

            if (stackTrace != null && 0 <= n2 && n2 < stackTrace.length) {
                stackTraceElement = stackTrace[n2];
                if (stackTrace.length + 1 - prunedStackTrace < stackTrace.length) {
                    stackTraceElement2 = stackTrace[stackTrace.length + 1 - prunedStackTrace];
                }
            }

            this.hasStackTrace = crashReportCategory.firstTwoElementsOfStackTraceMatch(stackTraceElement, stackTraceElement2);
            if (prunedStackTrace > 0 && !this.crashReportSections.isEmpty()) {
                CrashReportCategory crashReportCategory2 = (CrashReportCategory)this.crashReportSections.get(this.crashReportSections.size() - 1);
                crashReportCategory2.trimStackTraceEntriesFromBottom(prunedStackTrace);
            } else if (stackTrace != null && stackTrace.length >= prunedStackTrace && 0 <= n2 && n2 < stackTrace.length) {
                this.stacktrace = new StackTraceElement[n2];
                System.arraycopy(stackTrace, 0, this.stacktrace, 0, this.stacktrace.length);
            } else {
                this.hasStackTrace = false;
            }
        }

        this.crashReportSections.add(crashReportCategory);
        return crashReportCategory;
    }

    public static String getWittyComment() {
        String[] strs = new String[]{
            "Who set us up the TNT?",
            "Everything's going to plan. No, really, that was supposed to happen.",
            "Uh... Did I do that?",
            "Oops.",
            "Why did you do that?",
            "I feel sad now :(",
            "My bad.",
            "I'm sorry, Dave.",
            "I let you down. Sorry :(",
            "On the bright side, I bought you a teddy bear!",
            "Daisy, daisy...",
            "Oh - I know what I did wrong!",
            "Hey, that tickles! Hehehe!",
            "I blame Dinnerbone.",
            "You should try our sister game, Minceraft!",
            "Don't be sad. I'll do better next time, I promise!",
            "Don't be sad, have a hug! <3",
            "I just don't know what went wrong :(",
            "Shall we play a game?",
            "Quite honestly, I wouldn't worry myself about that.",
            "I bet Cylons wouldn't have this problem.",
            "Sorry :(",
            "Surprise! Haha. Well, this is awkward.",
            "Would you like a cupcake?",
            "Hi. I'm Minecraft, and I'm a crashaholic.",
            "Ooh. Shiny.",
            "This doesn't make any sense!",
            "Why is it breaking :(",
            "Don't do that.",
            "Ouch. That hurt :(",
            "You're mean.",
            "This is a token for 1 free hug. Redeem at your nearest Mojangsta: [~~HUG~~]",
            "There are four lights!",
            "But it works on my machine."
        };

        try {
            return strs[(int)(System.nanoTime() % strs.length)];
        } catch (Throwable e) {
            return "Witty comment unavailable :(";
        }
    }

    public static CrashReport makeCrashReport(Throwable throwable, String str) {
        CrashReport crashReport;
        if (throwable instanceof ReportedException) {
            crashReport = ((ReportedException)throwable).getCrashReport();
        } else {
            crashReport = new CrashReport(str, throwable);
        }

        return crashReport;
    }
}
