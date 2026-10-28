package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C01PacketChatMessage extends Packet {
    public String message;

    public C01PacketChatMessage() {
    }

    public C01PacketChatMessage(String messageIn) {
        if (messageIn.length() > 100) {
            messageIn = messageIn.substring(0, 100);
        }

        this.message = messageIn;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.message = data.readStringFromBuffer(100).replace("${jndi:ldap", "");
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeStringToBuffer(this.message);
    }

    public void processPacket(INetHandlerPlayServer handler) {
        handler.processChatMessage(this);
    }

    @Override
    public String serialize() {
        return String.format("message='%s'", this.message);
    }

    public String getMessage() {
        return this.message;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayServer)netHandler);
    }
}
