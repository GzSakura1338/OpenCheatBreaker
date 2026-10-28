package net.minecraft.network.play.client;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C17PacketCustomPayload extends Packet {
    public String channel;
    public byte[] data;
    public int length;

    public C17PacketCustomPayload() {
    }

    public C17PacketCustomPayload(String str, ByteBuf byteBuf) {
        this(str, byteBuf.array());
    }

    public C17PacketCustomPayload(String str, byte[] bytes) {
        this.channel = str;
        this.data = bytes;
        if (bytes != null) {
            this.length = bytes.length;
            if (this.length >= 32767) {
                throw new IllegalArgumentException("Payload may not be larger than 32k");
            }
        }
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.channel = data.readStringFromBuffer(20);
        this.length = data.readShort();
        if (this.length > 0 && this.length < 32767) {
            this.data = new byte[this.length];
            data.readBytes(this.data);
        }
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeStringToBuffer(this.channel);
        data.writeShort((short)this.length);
        if (this.data != null) {
            data.writeBytes(this.data);
        }
    }

    public void processPacket(INetHandlerPlayServer handler) {
        handler.processVanilla250Packet(this);
    }

    public String getChannel() {
        return this.channel;
    }

    public byte[] getData() {
        return this.data;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayServer)netHandler);
    }
}
