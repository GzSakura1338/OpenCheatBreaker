package com.cheatbreaker.client.event;

public class StringEvent3 extends CancellableEvent {
   public String text;

   public String getText() {
      return this.text;
   }

   public StringEvent3(String str) {
      this.text = str;
   }
}
