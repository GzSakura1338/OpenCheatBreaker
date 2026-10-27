package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2BPacketChangeGameState implements Packet<INetHandlerPlayClient> {
   public static String[] MESSAGE_NAMES = new String[]{"tile.bed.notValid"};
   public int state;
   public float value;

   public S2BPacketChangeGameState() {
   }

   public S2BPacketChangeGameState(int stateIn, float f) {
      this.state = stateIn;
      this.value = f;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.state = buf.readUnsignedByte();
      this.value = buf.readFloat();
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.state);
      buf.writeFloat(this.value);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleChangeGameState(this);
   }

   public int getGameState() {
      return this.state;
   }

   public float getValue() {
      return this.value;
   }
}
