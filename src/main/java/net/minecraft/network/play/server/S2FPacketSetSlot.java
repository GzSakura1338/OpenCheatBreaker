package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S2FPacketSetSlot implements Packet<INetHandlerPlayClient> {
   public int windowId;
   public int slot;
   public ItemStack item;

   public S2FPacketSetSlot() {
   }

   public S2FPacketSetSlot(int windowIdIn, int slotIn, ItemStack itemIn) {
      this.windowId = windowIdIn;
      this.slot = slotIn;
      this.item = itemIn == null ? null : itemIn.copy();
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleSetSlot(this);
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.windowId = buf.readByte();
      this.slot = buf.readShort();
      this.item = buf.readItemStackFromBuffer();
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeByte(this.windowId);
      buf.writeShort(this.slot);
      buf.writeItemStackToBuffer(this.item);
   }

   public int getSyncId() {
      return this.windowId;
   }

   public int getSlot() {
      return this.slot;
   }

   public ItemStack getItemStack() {
      return this.item;
   }
}
