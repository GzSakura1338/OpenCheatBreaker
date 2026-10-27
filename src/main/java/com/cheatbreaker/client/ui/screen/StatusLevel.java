package com.cheatbreaker.client.ui.screen;

public enum StatusLevel {
   UP("green"),
   DOWN("red"),
   BUSY("yellow"),
   UNKNOWN("unknown");

   public String text;

   public String getText() {
      return this.text;
   }

   public static StatusLevel findStatusLevel(String str) {
      for (StatusLevel statusLevel : values()) {
         if (statusLevel.getText().equalsIgnoreCase(str)) {
            return statusLevel;
         }
      }

      return null;
   }

   StatusLevel(String str) {
      this.text = str;
   }
}
