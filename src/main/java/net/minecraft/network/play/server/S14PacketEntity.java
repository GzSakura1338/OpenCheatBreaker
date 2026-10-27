package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

public class S14PacketEntity implements Packet<INetHandlerPlayClient> {
   public int entityId;
   public byte posX;
   public byte posY;
   public byte posZ;
   public byte yaw;
   public byte pitch;
   public boolean onGround;
   public boolean rotate;

   public S14PacketEntity() {
   }

   public S14PacketEntity(int entityIdIn) {
      this.entityId = entityIdIn;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarIntFromBuffer();
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.entityId);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleEntityMovement(this);
   }

   @Override
   public String toString() {
      return "Entity_" + super.toString();
   }

   public Entity getEntity(World worldIn) {
      return worldIn.getEntityByID(this.entityId);
   }

   public byte getX() {
      return this.posX;
   }

   public byte getY() {
      return this.posY;
   }

   public byte getZ() {
      return this.posZ;
   }

   public byte getYaw() {
      return this.yaw;
   }

   public byte getPitch() {
      return this.pitch;
   }

   public boolean shouldRotate() {
      return this.rotate;
   }

   public boolean getOnGround() {
      return this.onGround;
   }

   public static class S15PacketEntityRelMove extends S14PacketEntity {
      public S15PacketEntityRelMove() {
      }

      public S15PacketEntityRelMove(int entityIdIn, byte x, byte y, byte z, boolean onGroundIn) {
         super(entityIdIn);
         this.posX = x;
         this.posY = y;
         this.posZ = z;
         this.onGround = onGroundIn;
      }

      @Override
      public void readPacketData(PacketBuffer buf) throws IOException {
         super.readPacketData(buf);
         this.posX = buf.readByte();
         this.posY = buf.readByte();
         this.posZ = buf.readByte();
         this.onGround = buf.readBoolean();
      }

      @Override
      public void writePacketData(PacketBuffer buf) throws IOException {
         super.writePacketData(buf);
         buf.writeByte(this.posX);
         buf.writeByte(this.posY);
         buf.writeByte(this.posZ);
         buf.writeBoolean(this.onGround);
      }
   }

   public static class S16PacketEntityLook extends S14PacketEntity {
      public S16PacketEntityLook() {
         this.rotate = true;
      }

      public S16PacketEntityLook(int entityIdIn, byte yawIn, byte pitchIn, boolean onGroundIn) {
         super(entityIdIn);
         this.yaw = yawIn;
         this.pitch = pitchIn;
         this.rotate = true;
         this.onGround = onGroundIn;
      }

      @Override
      public void readPacketData(PacketBuffer buf) throws IOException {
         super.readPacketData(buf);
         this.yaw = buf.readByte();
         this.pitch = buf.readByte();
         this.onGround = buf.readBoolean();
      }

      @Override
      public void writePacketData(PacketBuffer buf) throws IOException {
         super.writePacketData(buf);
         buf.writeByte(this.yaw);
         buf.writeByte(this.pitch);
         buf.writeBoolean(this.onGround);
      }
   }

   public static class S17PacketEntityLookMove extends S14PacketEntity {
      public S17PacketEntityLookMove() {
         this.rotate = true;
      }

      public S17PacketEntityLookMove(int n, byte b, byte b2, byte b3, byte b4, byte b5, boolean flag) {
         super(n);
         this.posX = b;
         this.posY = b2;
         this.posZ = b3;
         this.yaw = b4;
         this.pitch = b5;
         this.onGround = flag;
         this.rotate = true;
      }

      @Override
      public void readPacketData(PacketBuffer buf) throws IOException {
         super.readPacketData(buf);
         this.posX = buf.readByte();
         this.posY = buf.readByte();
         this.posZ = buf.readByte();
         this.yaw = buf.readByte();
         this.pitch = buf.readByte();
         this.onGround = buf.readBoolean();
      }

      @Override
      public void writePacketData(PacketBuffer buf) throws IOException {
         super.writePacketData(buf);
         buf.writeByte(this.posX);
         buf.writeByte(this.posY);
         buf.writeByte(this.posZ);
         buf.writeByte(this.yaw);
         buf.writeByte(this.pitch);
         buf.writeBoolean(this.onGround);
      }
   }
}
