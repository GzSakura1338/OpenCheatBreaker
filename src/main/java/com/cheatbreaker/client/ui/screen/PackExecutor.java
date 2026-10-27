package com.cheatbreaker.client.ui.screen;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PackExecutor {
   public static ExecutorService executorService = Executors.newSingleThreadExecutor();

   public static void shutdown() {
      executorService.shutdown();
   }

   public static void execute(Runnable runnable) {
      executorService.execute(runnable);
   }
}
