package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.manager.BlockedServerLevel;
import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;
import java.util.ArrayList;
import java.util.List;

public class BlockedServerPacket extends AbstractPacket {
   public List<String> domains = new ArrayList<>();
   public String text;
   public String text2;
   public BlockedServerLevel blockedServerLevel;
   public int intValue;
   public boolean flag;
   public String text3;

   public String getText2() {
      return this.text2;
   }

   public BlockedServerLevel getBlockedServerLevel() {
      return this.blockedServerLevel;
   }

   public int getIntValue() {
      return this.intValue;
   }

   public List<String> getDomains() {
      return this.domains;
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      ((WebsocketPacketHandler)packetHandler).handleBlockedServer(this);
   }

   public BlockedServerPacket() {
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      this.text = packetBuffer.readString();
      int readInt = packetBuffer.getByteBuf().readInt();
      this.domains = new ArrayList<>();

      for (int i = 0; i < readInt; i++) {
         this.domains.add(packetBuffer.readString());
      }

      this.text2 = packetBuffer.readString();
      int readInt2 = packetBuffer.getByteBuf().readInt();
      this.blockedServerLevel = BlockedServerLevel.values()[readInt2];
      this.intValue = packetBuffer.getByteBuf().readInt();
      this.flag = packetBuffer.getByteBuf().readBoolean();
      this.text3 = packetBuffer.readString();
   }

   public String getText() {
      return this.text;
   }

   public boolean isFlag() {
      return this.flag;
   }

   public String getText3() {
      return this.text3;
   }

   @Override
   public void write(PacketBuffer packetBuffer) {
      packetBuffer.writeString(this.text);
      packetBuffer.getByteBuf().writeInt(this.domains.size());

      for (String str : this.domains) {
         packetBuffer.writeString(str);
      }

      packetBuffer.writeString(this.text2);
      packetBuffer.getByteBuf().writeInt(this.blockedServerLevel.ordinal());
      packetBuffer.getByteBuf().writeInt(this.intValue);
      packetBuffer.getByteBuf().writeBoolean(this.flag);
      packetBuffer.writeString(this.text3);
   }

   public BlockedServerPacket(String str, List<String> list, String str2, BlockedServerLevel blockedServerLevel, int n, boolean flag, String str3) {
      this.text = str;
      this.domains = list;
      this.text2 = str2;
      this.blockedServerLevel = blockedServerLevel;
      this.intValue = n;
      this.flag = flag;
      this.text3 = str3;
   }
}
