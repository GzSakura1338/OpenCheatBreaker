package com.cheatbreaker.client.event;

public class StringEvent2 extends Event {
   public String text;

   public String getText() {
      return this.text;
   }

   public void setText(String str) {
      this.text = str;
   }

   public StringEvent2(String str) {
      this.text = str;
   }
}
