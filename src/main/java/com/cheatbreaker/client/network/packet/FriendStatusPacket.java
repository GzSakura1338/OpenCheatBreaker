package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;

public class FriendStatusPacket extends AbstractPacket {
   public String playerId;
   public String name;
   public int iconColor;
   public long statusOrLastSeen;
   public boolean online;
   public String gameVersion;

   @Override
   public void write(PacketBuffer packetBuffer) {
      packetBuffer.writeString(this.playerId);
      packetBuffer.writeString(this.name);
      packetBuffer.getByteBuf().writeInt(this.iconColor);
      packetBuffer.getByteBuf().writeLong(this.statusOrLastSeen);
      packetBuffer.getByteBuf().writeBoolean(this.online);
      packetBuffer.writeString(this.gameVersion);
   }

   public String getGameVersion() {
      return this.gameVersion;
   }

   public String getName() {
      return this.name;
   }

   public FriendStatusPacket() {
   }

   public boolean isOnline() {
      return this.online;
   }

   public int getIconColor() {
      return this.iconColor;
   }

   public long getStatusOrLastSeen() {
      return this.statusOrLastSeen;
   }

   public FriendStatusPacket(String str, String str2, int n, long l, boolean flag, String str3) {
      this.playerId = str;
      this.name = str2;
      this.iconColor = n;
      this.statusOrLastSeen = l;
      this.online = flag;
      this.gameVersion = str3;
   }

   public String getPlayerId() {
      return this.playerId;
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.playerId = packetBuffer.readString();
      this.name = packetBuffer.readString();
      this.iconColor = packetBuffer.getByteBuf().readInt();
      this.statusOrLastSeen = packetBuffer.getByteBuf().readLong();
      this.online = packetBuffer.getByteBuf().readBoolean();
      this.gameVersion = packetBuffer.readString();
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      packetHandler.handleFriendStatus(this);
   }
}
