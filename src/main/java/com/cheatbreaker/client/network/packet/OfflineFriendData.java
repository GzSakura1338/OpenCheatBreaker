package com.cheatbreaker.client.network.packet;

public class OfflineFriendData {
   public String playerId;
   public String name;
   public long lastSeen;

   public String getName() {
      return this.name;
   }

   public OfflineFriendData(String str, String str2, long l) {
      this.playerId = str;
      this.name = str2;
      this.lastSeen = l;
   }

   public String getPlayerId() {
      return this.playerId;
   }

   public long getLastSeen() {
      return this.lastSeen;
   }
}
