package com.cheatbreaker.client.util.mumble;

public class MumbleIdentity {
   public String uuid;
   public String playerName;
   public String mumbleName;

   public MumbleIdentity(String str, String str2, String str3) {
      this.uuid = str;
      this.playerName = str2;
      this.mumbleName = str3;
   }

   public String getUuid() {
      return this.uuid;
   }

   public String getName() {
      return this.playerName != null ? this.playerName : this.mumbleName;
   }
}
