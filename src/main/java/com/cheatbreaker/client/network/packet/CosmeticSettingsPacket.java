package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.ServerPacketHandler;
import java.util.ArrayList;
import java.util.List;

public class CosmeticSettingsPacket extends AbstractPacket {
   public List<CosmeticSettingData> cosmetics = new ArrayList<>();

   public CosmeticSettingsPacket(List<CosmeticSettingData> list) {
      this.cosmetics = list;
   }

   @Override
   public void handle(PacketHandler packetHandler) {
      ((ServerPacketHandler)packetHandler).handleCosmeticSettings(this);
   }

   @Override
   public void read(PacketBuffer packetBuffer) {
      int readInt = packetBuffer.getByteBuf().readInt();
      this.cosmetics = new ArrayList<>();

      for (int i = 0; i < readInt; i++) {
         long readLong = packetBuffer.getByteBuf().readLong();
         boolean readBoolean = packetBuffer.getByteBuf().readBoolean();
         String readString = packetBuffer.readString();
         String readString2 = packetBuffer.readString();
         float readFloat = packetBuffer.getByteBuf().readFloat();
         String readString3 = packetBuffer.readString();
         this.cosmetics.add(new CosmeticSettingData(readLong, readBoolean, readString, readString2, readFloat, readString3));
      }
   }

   public List<CosmeticSettingData> getCosmetics() {
      return this.cosmetics;
   }

   public CosmeticSettingsPacket() {
   }

   @Override
   public void write(PacketBuffer packetBuffer) {
      packetBuffer.getByteBuf().writeInt(this.cosmetics.size());

      for (CosmeticSettingData cosmeticSettingData : this.cosmetics) {
         packetBuffer.getByteBuf().writeLong(cosmeticSettingData.getId());
         packetBuffer.getByteBuf().writeBoolean(cosmeticSettingData.isEquipped());
         packetBuffer.writeString(cosmeticSettingData.getType());
         packetBuffer.writeString(cosmeticSettingData.getName());
         packetBuffer.getByteBuf().writeFloat(cosmeticSettingData.getScale());
         packetBuffer.writeString(cosmeticSettingData.getTexture());
      }
   }
}
