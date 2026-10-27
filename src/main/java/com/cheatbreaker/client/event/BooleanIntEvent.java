package com.cheatbreaker.client.event;

public class BooleanIntEvent extends Event {
   public int eventButton;
   public boolean flag;

   public int getEventButton() {
      return this.eventButton;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public BooleanIntEvent(int n, boolean flag) {
      this.eventButton = n;
      this.flag = flag;
   }
}
