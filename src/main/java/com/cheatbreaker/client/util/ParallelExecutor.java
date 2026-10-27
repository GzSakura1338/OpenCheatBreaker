package com.cheatbreaker.client.util;

import java.util.concurrent.ForkJoinPool;

public class ParallelExecutor {
   public static ForkJoinPool forkJoinPool = ForkJoinPool.commonPool();

   public static void handleRunnable(Runnable runnable, long l) {
      try {
         forkJoinPool.wait(l);
         forkJoinPool.execute(runnable);
      } catch (Throwable e) {
         throw SneakyThrow.sneakyThrow(e);
      }
   }

   public static void execute(Runnable runnable) {
      try {
         forkJoinPool.execute(runnable);
      } catch (Throwable e) {
         throw SneakyThrow.sneakyThrow(e);
      }
   }
}
