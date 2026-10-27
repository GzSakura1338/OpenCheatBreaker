package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S08PacketPlayerPosLook implements Packet<INetHandlerPlayClient> {
   public double x;
   public double y;
   public double z;
   public float yaw;
   public float pitch;
   public Set<S08PacketPlayerPosLook.EnumFlags> flags;

   public S08PacketPlayerPosLook() {
   }

   public S08PacketPlayerPosLook(double xIn, double yIn, double zIn, float yawIn, float pitchIn, Set<S08PacketPlayerPosLook.EnumFlags> set) {
      this.x = xIn;
      this.y = yIn;
      this.z = zIn;
      this.yaw = yawIn;
      this.pitch = pitchIn;
      this.flags = set;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      this.x = buf.readDouble();
      this.y = buf.readDouble();
      this.z = buf.readDouble();
      this.yaw = buf.readFloat();
      this.pitch = buf.readFloat();
      this.flags = S08PacketPlayerPosLook.EnumFlags.getFlags(buf.readUnsignedByte());
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      buf.writeDouble(this.x);
      buf.writeDouble(this.y);
      buf.writeDouble(this.z);
      buf.writeFloat(this.yaw);
      buf.writeFloat(this.pitch);
      buf.writeByte(S08PacketPlayerPosLook.EnumFlags.getBitField(this.flags));
   }

   public void processPacket(INetHandlerPlayClient handler) {
      handler.handlePlayerPosLook(this);
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public float getYaw() {
      return this.yaw;
   }

   public float getPitch() {
      return this.pitch;
   }

   public Set<S08PacketPlayerPosLook.EnumFlags> getFlags() {
      return this.flags;
   }

   public enum EnumFlags {
      X(0),
      Y(1),
      Z(2),
      Y_ROT(3),
      X_ROT(4);

      public int shift;

      EnumFlags(int n) {
         this.shift = n;
      }

      public int getMask() {
         return 1 << this.shift;
      }

      public boolean isSet(int n) {
         return (n & this.getMask()) == this.getMask();
      }

      public static Set<S08PacketPlayerPosLook.EnumFlags> getFlags(int n) {
         EnumSet noneOf = EnumSet.noneOf(S08PacketPlayerPosLook.EnumFlags.class);

         for (S08PacketPlayerPosLook.EnumFlags enumFlags : values()) {
            if (enumFlags.isSet(n)) {
               noneOf.add(enumFlags);
            }
         }

         return noneOf;
      }

      public static int getBitField(Set<S08PacketPlayerPosLook.EnumFlags> set) {
         int n = 0;

         for (S08PacketPlayerPosLook.EnumFlags enumFlags : set) {
            n |= enumFlags.getMask();
         }

         return n;
      }
   }
}
