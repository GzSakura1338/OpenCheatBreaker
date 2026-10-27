package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S32PacketConfirmTransaction implements Packet<INetHandlerPlayClient> {
   public int windowId;
   public short actionNumber;
   public boolean accepted;

   public S32PacketConfirmTransaction() {
   }

   public S32PacketConfirmTransaction(int windowIdIn, short actionNumberIn, boolean flag) {
      this.windowId = windowIdIn;
      this.actionNumber = actionNumberIn;
      this.accepted = flag;
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleConfirmTransaction(this);
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.windowId = buf.readUnsignedByte();
      this.actionNumber = buf.readShort();
      this.accepted = buf.readBoolean();
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.windowId);
      buf.writeShort(this.actionNumber);
      buf.writeBoolean(this.accepted);
   }

   public int getWindowId() {
      return this.windowId;
   }

   public short getActionNumber() {
      return this.actionNumber;
   }

   public boolean wasAccepted() {
      return this.accepted;
   }
}
