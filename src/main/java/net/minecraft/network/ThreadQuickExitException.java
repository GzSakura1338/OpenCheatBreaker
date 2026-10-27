package net.minecraft.network;

public class ThreadQuickExitException extends RuntimeException {
   public static ThreadQuickExitException INSTANCE = new ThreadQuickExitException();

   public ThreadQuickExitException() {
      this.setStackTrace(new StackTraceElement[0]);
   }

   @Override
   public synchronized Throwable fillInStackTrace() {
      this.setStackTrace(new StackTraceElement[0]);
      return this;
   }
}
