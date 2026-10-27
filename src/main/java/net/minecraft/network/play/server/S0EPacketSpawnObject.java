package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.entity.Entity;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.MathHelper;

public class S0EPacketSpawnObject implements Packet<INetHandlerPlayClient> {
   public int entityId;
   public int x;
   public int y;
   public int z;
   public int pitch;
   public int yaw;
   public int type;
   public int dataId;
   public int speedX;
   public int speedY;
   public int speedZ;

   public S0EPacketSpawnObject() {
   }

   public S0EPacketSpawnObject(Entity entityIn, int typeIn) {
      this(entityIn, typeIn, 0);
   }

   public S0EPacketSpawnObject(Entity entityIn, int typeIn, int n) {
      this.entityId = entityIn.getEntityId();
      this.x = MathHelper.floor_double(entityIn.posX * 32.0);
      this.y = MathHelper.floor_double(entityIn.posY * 32.0);
      this.z = MathHelper.floor_double(entityIn.posZ * 32.0);
      this.pitch = MathHelper.floor_float(entityIn.rotationPitch * 256.0F / 360.0F);
      this.yaw = MathHelper.floor_float(entityIn.rotationYaw * 256.0F / 360.0F);
      this.type = typeIn;
      this.dataId = n;
      if (n > 0) {
         double motionX = entityIn.motionX;
         double motionY = entityIn.motionY;
         double motionZ = entityIn.motionZ;
         double d = 3.9;
         if (motionX < -d) {
            motionX = -d;
         }

         if (motionY < -d) {
            motionY = -d;
         }

         if (motionZ < -d) {
            motionZ = -d;
         }

         if (motionX > d) {
            motionX = d;
         }

         if (motionY > d) {
            motionY = d;
         }

         if (motionZ > d) {
            motionZ = d;
         }

         this.speedX = (int)(motionX * 8000.0);
         this.speedY = (int)(motionY * 8000.0);
         this.speedZ = (int)(motionZ * 8000.0);
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.entityId = buf.readVarIntFromBuffer();
      this.type = buf.readByte();
      this.x = buf.readInt();
      this.y = buf.readInt();
      this.z = buf.readInt();
      this.pitch = buf.readByte();
      this.yaw = buf.readByte();
      this.dataId = buf.readInt();
      if (this.dataId > 0) {
         this.speedX = buf.readShort();
         this.speedY = buf.readShort();
         this.speedZ = buf.readShort();
      }
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeVarIntToBuffer(this.entityId);
      buf.writeByte(this.type);
      buf.writeInt(this.x);
      buf.writeInt(this.y);
      buf.writeInt(this.z);
      buf.writeByte(this.pitch);
      buf.writeByte(this.yaw);
      buf.writeInt(this.dataId);
      if (this.dataId > 0) {
         buf.writeShort(this.speedX);
         buf.writeShort(this.speedY);
         buf.writeShort(this.speedZ);
      }
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleSpawnObject(this);
   }

   public int getEntityID() {
      return this.entityId;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public int getSpeedX() {
      return this.speedX;
   }

   public int getSpeedY() {
      return this.speedY;
   }

   public int getSpeedZ() {
      return this.speedZ;
   }

   public int getPitch() {
      return this.pitch;
   }

   public int getYaw() {
      return this.yaw;
   }

   public int getType() {
      return this.type;
   }

   public int getDataId() {
      return this.dataId;
   }

   public void setX(int newX) {
      this.x = newX;
   }

   public void setY(int newY) {
      this.y = newY;
   }

   public void setZ(int newZ) {
      this.z = newZ;
   }

   public void setSpeedX(int newSpeedX) {
      this.speedX = newSpeedX;
   }

   public void setSpeedY(int newSpeedY) {
      this.speedY = newSpeedY;
   }

   public void setSpeedZ(int newSpeedZ) {
      this.speedZ = newSpeedZ;
   }

   public void setDataId(int n) {
      this.dataId = n;
   }
}
