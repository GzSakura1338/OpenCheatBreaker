package net.minecraft.network.login.server;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;

public class S02PacketLoginSuccess implements Packet<INetHandlerLoginClient> {
   public GameProfile profile;

   public S02PacketLoginSuccess() {
   }

   public S02PacketLoginSuccess(GameProfile profileIn) {
      this.profile = profileIn;
   }

   @Override
   public void readPacketData(PacketBuffer buf) throws IOException {
      String readStringFromBuffer = buf.readStringFromBuffer(36);
      String readStringFromBuffer2 = buf.readStringFromBuffer(16);
      UUID fromString = UUID.fromString(readStringFromBuffer);
      this.profile = new GameProfile(fromString, readStringFromBuffer2);
   }

   @Override
   public void writePacketData(PacketBuffer buf) throws IOException {
      UUID id = this.profile.getId();
      buf.writeString(id == null ? "" : id.toString());
      buf.writeString(this.profile.getName());
   }

   public void processPacket(INetHandlerLoginClient handler) {
      handler.handleLoginSuccess(this);
   }

   public GameProfile getProfile() {
      return this.profile;
   }
}
