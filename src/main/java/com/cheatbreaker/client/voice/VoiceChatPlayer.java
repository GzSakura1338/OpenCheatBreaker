package com.cheatbreaker.client.voice;

import java.util.UUID;

public class VoiceChatPlayer {
   public UUID uuid;
   public String name;

   public VoiceChatPlayer(UUID uuid, String str) {
      this.uuid = uuid;
      this.name = str;
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public String getName() {
      return this.name;
   }
}
