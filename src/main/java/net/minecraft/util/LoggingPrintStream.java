package net.minecraft.util;

import java.io.OutputStream;
import java.io.PrintStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggingPrintStream extends PrintStream {
   public static Logger LOGGER = LogManager.getLogger();
   public String domain;

   public LoggingPrintStream(String domainIn, OutputStream outStream) {
      super(outStream);
      this.domain = domainIn;
   }

   @Override
   public void println(String str) {
      this.logString(str);
   }

   @Override
   public void println(Object obj) {
      this.logString(String.valueOf(obj));
   }

   public void logString(String string) {
      StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
      StackTraceElement stackTraceElement = stackTrace[Math.min(3, stackTrace.length)];
      LOGGER.info("[{}]@.({}:{}): {}", new Object[]{this.domain, stackTraceElement.getFileName(), stackTraceElement.getLineNumber(), string});
   }
}
