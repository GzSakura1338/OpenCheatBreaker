package com.cheatbreaker.client.ui.screen;

public class ServiceStatus {
   public StatusLevel statusLevel = StatusLevel.UNKNOWN;
   public String text;
   public String text2;

   public String getText2() {
      return this.text2;
   }

   public String getText() {
      return this.text;
   }

   public void setStatusLevel(StatusLevel statusLevel) {
      this.statusLevel = statusLevel;
   }

   public ServiceStatus(String str, String str2) {
      this.text = str;
      this.text2 = str2;
   }

   public StatusLevel getStatusLevel() {
      return this.statusLevel;
   }
}
