package com.cheatbreaker.client.event;

public class CancellableEvent extends Event {
   public boolean flag = false;

   public void setFlag(boolean flag) {
      this.flag = flag;
   }

   public boolean isFlag() {
      return this.flag;
   }
}
