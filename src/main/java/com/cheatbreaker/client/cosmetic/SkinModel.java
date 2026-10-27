package com.cheatbreaker.client.cosmetic;

public enum SkinModel {
   STEVE("classic"),
   ALEX("slim");

   public String text;

   SkinModel(String str) {
      this.text = str;
   }

   public String getText() {
      return this.text;
   }
}
