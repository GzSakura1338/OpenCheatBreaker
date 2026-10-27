package com.cheatbreaker.client.minimap;

public class CustomMob {
   public String name = "notLoaded";
   public boolean enabled = true;
   public boolean hostile = false;
   public boolean neutral = false;

   public CustomMob(String str, boolean flag, boolean flag2) {
      this.name = str;
      this.hostile = flag;
      this.neutral = flag2;
   }

   public CustomMob(String str, boolean flag) {
      this.name = str;
      this.enabled = flag;
   }
}
