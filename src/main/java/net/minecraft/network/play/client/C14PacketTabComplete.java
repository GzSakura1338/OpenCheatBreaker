package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import org.apache.commons.lang3.StringUtils;

public class C14PacketTabComplete extends Packet {
    public String message;

    public C14PacketTabComplete() {
    }

    public C14PacketTabComplete(String msg) {
        this.message = msg;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.message = data.readStringFromBuffer(32767);
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeStringToBuffer(StringUtils.substring(this.message, 0, 32767));
    }

    public void processPacket(INetHandlerPlayServer handler) {
        handler.processTabComplete(this);
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public String serialize() {
        return String.format("message='%s'", this.message);
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayServer)netHandler);
    }
}
