package com.cheatbreaker.client.event;

public class StringBooleanIntEvent extends Event {
   public int intValue;
   public String text;
   public boolean flag;

   public StringBooleanIntEvent(int n, String str, boolean flag) {
      this.intValue = n;
      this.text = str;
      this.flag = flag;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public String getText() {
      return this.text;
   }

   public void setIntValue(int n) {
      this.intValue = n;
   }

   public void setText(String str) {
      this.text = str;
   }

   public void setFlag(boolean flag) {
      this.flag = flag;
   }
}
