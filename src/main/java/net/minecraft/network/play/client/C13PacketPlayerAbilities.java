package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C13PacketPlayerAbilities implements Packet<INetHandlerPlayServer> {
   public boolean invulnerable;
   public boolean flying;
   public boolean allowFlying;
   public boolean creativeMode;
   public float flySpeed;
   public float walkSpeed;

   public C13PacketPlayerAbilities() {
   }

   public C13PacketPlayerAbilities(PlayerCapabilities capabilities) {
      this.setInvulnerable(capabilities.disableDamage);
      this.setFlying(capabilities.isFlying);
      this.setAllowFlying(capabilities.allowFlying);
      this.setCreativeMode(capabilities.isCreativeMode);
      this.setFlySpeed(capabilities.getFlySpeed());
      this.setWalkSpeed(capabilities.getWalkSpeed());
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      byte readByte = buf.readByte();
      this.setInvulnerable((readByte & 1) > 0);
      this.setFlying((readByte & 2) > 0);
      this.setAllowFlying((readByte & 4) > 0);
      this.setCreativeMode((readByte & 8) > 0);
      this.setFlySpeed(buf.readFloat());
      this.setWalkSpeed(buf.readFloat());
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      byte b = 0;
      if (this.isInvulnerable()) {
         b = (byte)(b | 1);
      }

      if (this.isFlying()) {
         b = (byte)(b | 2);
      }

      if (this.isAllowFlying()) {
         b = (byte)(b | 4);
      }

      if (this.isCreativeMode()) {
         b = (byte)(b | 8);
      }

      buf.writeByte(b);
      buf.writeFloat(this.flySpeed);
      buf.writeFloat(this.walkSpeed);
   }

   public void processPacket(INetHandlerPlayServer handler) {
      handler.processPlayerAbilities(this);
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   public void setInvulnerable(boolean isInvulnerable) {
      this.invulnerable = isInvulnerable;
   }

   public boolean isFlying() {
      return this.flying;
   }

   public void setFlying(boolean isFlying) {
      this.flying = isFlying;
   }

   public boolean isAllowFlying() {
      return this.allowFlying;
   }

   public void setAllowFlying(boolean isAllowFlying) {
      this.allowFlying = isAllowFlying;
   }

   public boolean isCreativeMode() {
      return this.creativeMode;
   }

   public void setCreativeMode(boolean isCreativeMode) {
      this.creativeMode = isCreativeMode;
   }

   public void setFlySpeed(float flySpeedIn) {
      this.flySpeed = flySpeedIn;
   }

   public void setWalkSpeed(float walkSpeedIn) {
      this.walkSpeed = walkSpeedIn;
   }
}
