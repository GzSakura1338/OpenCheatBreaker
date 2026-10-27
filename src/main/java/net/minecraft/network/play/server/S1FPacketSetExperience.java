package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S1FPacketSetExperience implements Packet<INetHandlerPlayClient> {
   public float health;
   public int totalExperience;
   public int level;

   public S1FPacketSetExperience() {
   }

   public S1FPacketSetExperience(float f, int totalExperienceIn, int levelIn) {
      this.health = f;
      this.totalExperience = totalExperienceIn;
      this.level = levelIn;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.health = buf.readFloat();
      this.level = buf.readVarIntFromBuffer();
      this.totalExperience = buf.readVarIntFromBuffer();
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeFloat(this.health);
      buf.writeVarIntToBuffer(this.level);
      buf.writeVarIntToBuffer(this.totalExperience);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleSetExperience(this);
   }

   public float getBarProgress() {
      return this.health;
   }

   public int getTotalExperience() {
      return this.totalExperience;
   }

   public int getLevel() {
      return this.level;
   }
}
