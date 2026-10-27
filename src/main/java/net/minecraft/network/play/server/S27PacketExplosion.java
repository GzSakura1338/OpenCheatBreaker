package net.minecraft.network.play.server;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;

public class S27PacketExplosion implements Packet<INetHandlerPlayClient> {
   public double posX;
   public double posY;
   public double posZ;
   public float strength;
   public List<BlockPos> affectedBlockPositions;
   public float playerVelocityX;
   public float playerVelocityY;
   public float playerVelocityZ;

   public S27PacketExplosion() {
   }

   public S27PacketExplosion(double d, double y, double z, float strengthIn, List<BlockPos> affectedBlocksIn, Vec3 vec3) {
      this.posX = d;
      this.posY = y;
      this.posZ = z;
      this.strength = strengthIn;
      this.affectedBlockPositions = Lists.newArrayList(affectedBlocksIn);
      if (vec3 != null) {
         this.playerVelocityX = (float)vec3.xCoord;
         this.playerVelocityY = (float)vec3.yCoord;
         this.playerVelocityZ = (float)vec3.zCoord;
      }
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.posX = buf.readFloat();
      this.posY = buf.readFloat();
      this.posZ = buf.readFloat();
      this.strength = buf.readFloat();
      int readInt = buf.readInt();
      this.affectedBlockPositions = Lists.newArrayListWithCapacity(readInt);
      int posX = (int)this.posX;
      int posY = (int)this.posY;
      int posZ = (int)this.posZ;

      for (int i = 0; i < readInt; i++) {
         int n = buf.readByte() + posX;
         int n2 = buf.readByte() + posY;
         int n3 = buf.readByte() + posZ;
         this.affectedBlockPositions.add(new BlockPos(n, n2, n3));
      }

      this.playerVelocityX = buf.readFloat();
      this.playerVelocityY = buf.readFloat();
      this.playerVelocityZ = buf.readFloat();
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeFloat((float)this.posX);
      buf.writeFloat((float)this.posY);
      buf.writeFloat((float)this.posZ);
      buf.writeFloat(this.strength);
      buf.writeInt(this.affectedBlockPositions.size());
      int posX = (int)this.posX;
      int posY = (int)this.posY;
      int posZ = (int)this.posZ;

      for (BlockPos blockPos : this.affectedBlockPositions) {
         int n = blockPos.getX() - posX;
         int n2 = blockPos.getY() - posY;
         int n3 = blockPos.getZ() - posZ;
         buf.writeByte(n);
         buf.writeByte(n2);
         buf.writeByte(n3);
      }

      buf.writeFloat(this.playerVelocityX);
      buf.writeFloat(this.playerVelocityY);
      buf.writeFloat(this.playerVelocityZ);
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handleExplosion(this);
   }

   public float getPlayerVelocityX() {
      return this.playerVelocityX;
   }

   public float getPlayerVelocityY() {
      return this.playerVelocityY;
   }

   public float getPlayerVelocityZ() {
      return this.playerVelocityZ;
   }

   public double getX() {
      return this.posX;
   }

   public double getY() {
      return this.posY;
   }

   public double getZ() {
      return this.posZ;
   }

   public float getStrength() {
      return this.strength;
   }

   public List<BlockPos> getAffectedBlockPositions() {
      return this.affectedBlockPositions;
   }
}
