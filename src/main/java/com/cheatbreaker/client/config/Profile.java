package com.cheatbreaker.client.config;

public class Profile {
   public int intValue = 0;
   public String text;
   public boolean flag;

   public Profile(String str, boolean flag) {
      this.text = str;
      this.flag = flag;
   }

   public String getText() {
      return this.text;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public void setText(String str) {
      this.text = str;
   }
}
