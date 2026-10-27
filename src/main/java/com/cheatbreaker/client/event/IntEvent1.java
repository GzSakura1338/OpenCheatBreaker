package com.cheatbreaker.client.event;

public class IntEvent1 extends CancellableEvent {
   public int eventButton;

   public int getEventButton() {
      return this.eventButton;
   }

   public IntEvent1(int n) {
      this.eventButton = n;
   }
}
