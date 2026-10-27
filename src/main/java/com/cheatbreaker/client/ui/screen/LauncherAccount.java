package com.cheatbreaker.client.ui.screen;

import com.cheatbreaker.client.CheatBreaker;
import net.minecraft.util.ResourceLocation;

public class LauncherAccount {
   public ResourceLocation resourceLocation;
   public String activeUsername;
   public String username;
   public String accessToken;
   public String displayName;
   public String uuid;

   public String getActiveUsername() {
      return this.activeUsername;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public ResourceLocation getResourceLocation() {
      return this.resourceLocation;
   }

   public String getUsername() {
      return this.username;
   }

   public String getAccessToken() {
      return this.accessToken;
   }

   public String getUuid() {
      return this.uuid;
   }

   public LauncherAccount(ResourceLocation resourceLocation, String str, String str2, String str3, String str4, String str5) {
      this.resourceLocation = resourceLocation;
      this.activeUsername = str;
      this.username = str2;
      this.accessToken = str3;
      this.displayName = str4;
      this.uuid = str5;
   }

   public LauncherAccount(String str, String str2, String str3, String str4, String str5) {
      this.username = str;
      this.activeUsername = str2;
      this.accessToken = str3;
      this.displayName = str4;
      this.uuid = str5;
      this.resourceLocation = CheatBreaker.getInstance().getFontManager().getHeadTexture(str4);
   }
}
