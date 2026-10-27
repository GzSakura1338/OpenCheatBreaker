package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S19PacketEntityHeadLook implements Packet<INetHandlerPlayClient> {
   public int entityId;
   public byte yaw;

   public S19PacketEntityHeadLook() {
   }

   public S19PacketEntityHeadLook(Entity entityIn, byte b) {
      this.entityId = entityIn.getEntityId();
      this.yaw = b;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarIntFromBuffer();
      this.yaw = buf.readByte();
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.entityId);
      buf.writeByte(this.yaw);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleEntityHeadLook(this);
   }

   public Entity getEntity(World worldIn) {
      return worldIn.getEntityByID(this.entityId);
   }

   public byte getYaw() {
      return this.yaw;
   }
}
