package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0CPacketInput implements Packet<INetHandlerPlayServer> {
   public float strafeSpeed;
   public float forwardSpeed;
   public boolean jumping;
   public boolean sneaking;

   public C0CPacketInput() {
   }

   public C0CPacketInput(float strafeSpeed, float forwardSpeed, boolean jumping, boolean sneaking) {
      this.strafeSpeed = strafeSpeed;
      this.forwardSpeed = forwardSpeed;
      this.jumping = jumping;
      this.sneaking = sneaking;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.strafeSpeed = buf.readFloat();
      this.forwardSpeed = buf.readFloat();
      byte readByte = buf.readByte();
      this.jumping = (readByte & 1) > 0;
      this.sneaking = (readByte & 2) > 0;
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeFloat(this.strafeSpeed);
      buf.writeFloat(this.forwardSpeed);
      byte b = 0;
      if (this.jumping) {
         b = (byte)(b | 1);
      }

      if (this.sneaking) {
         b = (byte)(b | 2);
      }

      buf.writeByte(b);
   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.processInput(this);
   }

   public float getStrafeSpeed() {
      return this.strafeSpeed;
   }

   public float getForwardSpeed() {
      return this.forwardSpeed;
   }

   public boolean isJumping() {
      return this.jumping;
   }

   public boolean isSneaking() {
      return this.sneaking;
   }
}
