package net.minecraft.network.login.server;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;

public class S02PacketLoginSuccess extends Packet {
    public GameProfile profile;

    public S02PacketLoginSuccess() {
    }

    public S02PacketLoginSuccess(GameProfile profileIn) {
        this.profile = profileIn;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        String readStringFromBuffer = data.readStringFromBuffer(36);
        String readStringFromBuffer2 = data.readStringFromBuffer(16);
        UUID fromString = UUID.fromString(readStringFromBuffer);
        this.profile = new GameProfile(fromString, readStringFromBuffer2);
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        UUID id = this.profile.getId();
        data.writeStringToBuffer(id == null ? "" : id.toString());
        data.writeStringToBuffer(this.profile.getName());
    }

    public void processPacket(INetHandlerLoginClient handler) {
        handler.handleLoginSuccess(this);
    }

    @Override
    public boolean hasPriority() {
        return true;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerLoginClient)netHandler);
    }
}
