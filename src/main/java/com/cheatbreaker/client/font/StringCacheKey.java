package com.cheatbreaker.client.font;

public class StringCacheKey {
   public String text;
   public int color;
   public boolean shadow;

   public static void setText(StringCacheKey stringCacheKey, String str) {
      stringCacheKey.text = str;
   }

   public static void setColor(StringCacheKey stringCacheKey, int n) {
      stringCacheKey.color = n;
   }

   public boolean canEqual(Object obj) {
      return obj instanceof StringCacheKey;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == this) {
         return true;
      }

      if (!(obj instanceof StringCacheKey)) {
         return false;
      }

      StringCacheKey stringCacheKey = (StringCacheKey)obj;
      if (!stringCacheKey.canEqual(this)) {
         return false;
      }

      if (this.color != stringCacheKey.color) {
         return false;
      }

      if (this.shadow != stringCacheKey.shadow) {
         return false;
      }

      String text = this.text;
      String text2 = stringCacheKey.text;
      return text == null ? text2 == null : text.equals(text2);
   }

   public static void setShadow(StringCacheKey stringCacheKey, boolean flag) {
      stringCacheKey.shadow = flag;
   }

   public StringCacheKey(String str, int n, boolean flag) {
      this.text = str;
      this.color = n;
      this.shadow = flag;
   }

   @Override
   public int hashCode() {
      byte b = 59;
      int n = 1;
      n = n * 59 + this.color;
      n = n * 59 + (this.shadow ? 79 : 97);
      String text = this.text;
      return n * 59 + (text == null ? 43 : text.hashCode());
   }
}
