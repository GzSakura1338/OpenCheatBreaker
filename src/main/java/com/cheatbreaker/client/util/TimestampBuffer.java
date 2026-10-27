package com.cheatbreaker.client.util;

import java.util.ArrayDeque;
import java.util.Deque;

public class TimestampBuffer {
   public Deque<Long> nanoTimes = new ArrayDeque<>();
   public Deque<Long> values = new ArrayDeque<>();
   public Deque<Integer> ticks = new ArrayDeque<>();

   public void add(long l) {
      this.nanoTimes.add(System.nanoTime());
      this.values.add(l);
      this.ticks.add(ClickTracker.getTickCounter());

      while (this.nanoTimes.size() > 1000) {
         this.nanoTimes.removeFirst();
         this.values.removeFirst();
         this.ticks.removeFirst();
      }
   }
}
