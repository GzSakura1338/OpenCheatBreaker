package com.cheatbreaker.client.mod.impl;

public enum PlaytimeDisplayType {
   DEFAULT("12:34:56", "%d:%02d:%02d", false),
   COMPACT_1("12:34", "%d:%02d", false, false),
   COMPACT_2("12:34:56.789", "%d:%02d:%02d.%03d"),
   COMPACT_3("12:34:56:789", "%d:%02d:%02d:%03d"),
   SPREAD_1("12 : 34 : 56", "%d : %02d : %02d", false),
   SPREAD_2("12 : 34", "%d : %02d", false, false),
   SPREAD_3("12 : 34 : 56 . 789", "%d : %02d : %02d . %03d"),
   SPREAD_4("12 : 34 : 56 : 789", "%d : %02d : %02d : %03d"),
   EASY_1("12h 34m", "%2dh %2dm", false, false),
   EASY_2("12h 34m 56s", "%2dh %2dm %02ds", false),
   EASY_3("12h 34m 56s 789ms", "%2dh %2dm %2ds %03dms"),
   STOPWATCH("123.45s", "%d.%03ds", true, true, false),
   MILLISECONDS("12345ms", "%dms", true, false, false);

   public String text;
   public String text2;
   public boolean flag;
   public boolean flag2;
   public boolean flag3;

   public String getText2() {
      return this.text2;
   }

   PlaytimeDisplayType(String str, String str2, boolean flag, boolean flag2) {
      this(str, str2, flag, flag2, true);
   }

   public String getText() {
      return this.text;
   }

   PlaytimeDisplayType(String str, String str2, boolean flag, boolean flag2, boolean flag3) {
      this.text = str;
      this.text2 = str2;
      this.flag = flag3;
      this.flag2 = flag2;
      this.flag3 = flag;
   }

   public boolean isFlag2() {
      return this.flag2;
   }

   public boolean isFlag3() {
      return this.flag3;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public static PlaytimeDisplayType valueOf(String str, PlaytimeDisplayType playtimeDisplayType) {
      try {
         return valueOf(str);
      } catch (IllegalArgumentException e) {
         return playtimeDisplayType;
      }
   }

   @Override
   public String toString() {
      return this.text;
   }

   PlaytimeDisplayType(String str, String str2) {
      this(str, str2, true);
   }

   PlaytimeDisplayType(String str, String str2, boolean flag) {
      this(str, str2, flag, true);
   }
}
