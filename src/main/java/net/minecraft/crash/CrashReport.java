package net.minecraft.crash;

import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.util.ReportedException;
import net.minecraft.world.gen.layer.IntCache;
import net.optifine.CrashReporter;
import net.optifine.reflect.Reflector;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CrashReport {
   public static Logger logger = LogManager.getLogger();
   public CrashReportCategory theReportCategory = new CrashReportCategory(this, "System Details");
   public List<CrashReportCategory> crashReportSections = Lists.newArrayList();
   public boolean firstCategoryInCrashReport = true;
   public StackTraceElement[] stacktrace = new StackTraceElement[0];
   public File crashReportFile;
   public String description;
   public Throwable cause;
   public static boolean flag = false;

   public CrashReport(String descriptionIn, Throwable causeThrowable) {
      this.description = descriptionIn;
      this.cause = causeThrowable;
      this.populateEnvironment();
   }

   public void populateEnvironment() {
      this.theReportCategory.addCrashSectionCallable("Minecraft Version", new Callable<String>() {
         public String call() {
            return "1.8.9";
         }
      });
      this.theReportCategory.addCrashSectionCallable("Operating System", new Callable<String>() {
         public String call() {
            return System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version");
         }
      });
      this.theReportCategory.addCrashSectionCallable("Java Version", new Callable<String>() {
         public String call() {
            return System.getProperty("java.version") + ", " + System.getProperty("java.vendor");
         }
      });
      this.theReportCategory.addCrashSectionCallable("Java VM Version", new Callable<String>() {
         public String call() {
            return System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor");
         }
      });
      this.theReportCategory.addCrashSectionCallable("Memory", new Callable<String>() {
         public String call() {
            Runtime runtime = Runtime.getRuntime();
            long maxMemory = runtime.maxMemory();
            long totalMemory = runtime.totalMemory();
            long freeMemory = runtime.freeMemory();
            long l = maxMemory / 1024L / 1024L;
            long l2 = totalMemory / 1024L / 1024L;
            long l3 = freeMemory / 1024L / 1024L;
            return freeMemory + " bytes (" + l3 + " MB) / " + totalMemory + " bytes (" + l2 + " MB) up to " + maxMemory + " bytes (" + l + " MB)";
         }
      });
      this.theReportCategory.addCrashSectionCallable("JVM Flags", new Callable<String>() {
         public String call() {
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
         }
      });
      this.theReportCategory.addCrashSectionCallable("IntCache", new Callable<String>() {
         public String call() throws Exception {
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

   public void getSectionsInStringBuilder(StringBuilder builder) {
      if ((this.stacktrace == null || this.stacktrace.length <= 0) && this.crashReportSections.size() > 0) {
         this.stacktrace = (StackTraceElement[])ArrayUtils.subarray(this.crashReportSections.get(0).getStackTrace(), 0, 1);
      }

      if (this.stacktrace != null && this.stacktrace.length > 0) {
         builder.append("-- Head --\n");
         builder.append("Stacktrace:\n");

         for (StackTraceElement stackTraceElement : this.stacktrace) {
            builder.append("\t").append("at ").append(stackTraceElement.toString());
            builder.append("\n");
         }

         builder.append("\n");
      }

      for (CrashReportCategory crashReportCategory : this.crashReportSections) {
         crashReportCategory.appendToStringBuilder(builder);
         builder.append("\n\n");
      }

      this.theReportCategory.appendToStringBuilder(builder);
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
      Reflector.call(Reflector.BlamingTransformer_onCrash, sb);
      Reflector.call(Reflector.CoreModManager_onCrash, sb);
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

   public boolean saveToFile(File toFile) {
      if (this.crashReportFile != null) {
         return false;
      }

      if (toFile.getParentFile() != null) {
         toFile.getParentFile().mkdirs();
      }

      try {
         FileWriter fileWriter = new FileWriter(toFile);
         fileWriter.write(this.getCompleteReport());
         fileWriter.close();
         this.crashReportFile = toFile;
         return true;
      } catch (Throwable e) {
         logger.error("Could not save crash report to " + toFile, e);
         return false;
      }
   }

   public CrashReportCategory getCategory() {
      return this.theReportCategory;
   }

   public CrashReportCategory makeCategory(String name) {
      return this.makeCategoryDepth(name, 1);
   }

   public CrashReportCategory makeCategoryDepth(String categoryName, int stacktraceLength) {
      CrashReportCategory crashReportCategory = new CrashReportCategory(this, categoryName);
      if (this.firstCategoryInCrashReport) {
         int prunedStackTrace = crashReportCategory.getPrunedStackTrace(stacktraceLength);
         StackTraceElement[] stackTrace = this.cause.getStackTrace();
         StackTraceElement stackTraceElement = null;
         StackTraceElement stackTraceElement2 = null;
         int n = stackTrace.length - prunedStackTrace;
         if (n < 0) {
            System.out.println("Negative index in crash report handler (" + stackTrace.length + "/" + prunedStackTrace + ")");
         }

         if (stackTrace != null && 0 <= n && n < stackTrace.length) {
            stackTraceElement = stackTrace[n];
            if (stackTrace.length + 1 - prunedStackTrace < stackTrace.length) {
               stackTraceElement2 = stackTrace[stackTrace.length + 1 - prunedStackTrace];
            }
         }

         this.firstCategoryInCrashReport = crashReportCategory.firstTwoElementsOfStackTraceMatch(stackTraceElement, stackTraceElement2);
         if (prunedStackTrace > 0 && !this.crashReportSections.isEmpty()) {
            CrashReportCategory crashReportCategory2 = this.crashReportSections.get(this.crashReportSections.size() - 1);
            crashReportCategory2.trimStackTraceEntriesFromBottom(prunedStackTrace);
         } else if (stackTrace != null && stackTrace.length >= prunedStackTrace && 0 <= n && n < stackTrace.length) {
            this.stacktrace = new StackTraceElement[n];
            System.arraycopy(stackTrace, 0, this.stacktrace, 0, this.stacktrace.length);
         } else {
            this.firstCategoryInCrashReport = false;
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

   public static CrashReport makeCrashReport(Throwable causeIn, String descriptionIn) {
      CrashReport crashReport;
      if (causeIn instanceof ReportedException) {
         crashReport = ((ReportedException)causeIn).getCrashReport();
      } else {
         crashReport = new CrashReport(descriptionIn, causeIn);
      }

      return crashReport;
   }
}
