package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;

public class ThreadedFileIOBase implements Runnable {
   public static ThreadedFileIOBase threadedIOInstance = new ThreadedFileIOBase();
   public List<IThreadedFileIO> threadedIOQueue = Collections.synchronizedList(Lists.newArrayList());
   public volatile long writeQueuedCounter;
   public volatile long savedIOCounter;
   public volatile boolean isThreadWaiting;

   public ThreadedFileIOBase() {
      Thread thread = new Thread(this, "File IO Thread");
      thread.setPriority(1);
      thread.start();
   }

   public static ThreadedFileIOBase getThreadedIOInstance() {
      return threadedIOInstance;
   }

   @Override
   public void run() {
      while (true) {
         this.processQueue();
      }
   }

   public void processQueue() {
      for (int i = 0; i < this.threadedIOQueue.size(); i++) {
         IThreadedFileIO threadedFileIO = this.threadedIOQueue.get(i);
         boolean writeNextIO = threadedFileIO.writeNextIO();
         if (!writeNextIO) {
            this.threadedIOQueue.remove(i--);
            this.savedIOCounter++;
         }

         try {
            Thread.sleep(this.isThreadWaiting ? 0L : 10L);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }

      if (this.threadedIOQueue.isEmpty()) {
         try {
            Thread.sleep(25L);
         } catch (InterruptedException e) {
            e.printStackTrace();
         }
      }
   }

   public void queueIO(IThreadedFileIO threadedFileIO) {
      if (!this.threadedIOQueue.contains(threadedFileIO)) {
         this.writeQueuedCounter++;
         this.threadedIOQueue.add(threadedFileIO);
      }
   }

   public void waitForFinish() throws InterruptedException {
      this.isThreadWaiting = true;

      while (this.writeQueuedCounter != this.savedIOCounter) {
         Thread.sleep(10L);
      }

      this.isThreadWaiting = false;
   }
}
