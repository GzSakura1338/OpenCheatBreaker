package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.IChatComponent.Serializer;

public class S40PacketDisconnect extends Packet {
    public IChatComponent reason;

    public S40PacketDisconnect() {
    }

    public S40PacketDisconnect(IChatComponent reasonIn) {
        this.reason = reasonIn;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.reason = Serializer.jsonToComponent(data.readStringFromBuffer(32767));
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeStringToBuffer(Serializer.componentToJson(this.reason));
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleDisconnect(this);
    }

    @Override
    public boolean hasPriority() {
        return true;
    }

    public IChatComponent getReason() {
        return this.reason;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
