package net.minecraft.network.play.server;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S3FPacketCustomPayload extends Packet {
    public String channel;
    public byte[] data;

    public S3FPacketCustomPayload() {
    }

    public S3FPacketCustomPayload(String channelName, ByteBuf dataIn) {
        this(channelName, dataIn.array());
    }

    public S3FPacketCustomPayload(String channelName, byte[] dataIn) {
        this.channel = channelName;
        this.data = dataIn;
        if (dataIn.length >= 1048576) {
            throw new IllegalArgumentException("Payload may not be larger than 1048576 bytes");
        }
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.channel = data.readStringFromBuffer(20);
        this.data = new byte[data.readUnsignedShort()];
        data.readBytes(this.data);
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeStringToBuffer(this.channel);
        data.writeShort(this.data.length);
        data.writeBytes(this.data);
    }

    public void processPacket(INetHandlerPlayClient handler) throws IOException {
        handler.handleCustomPayload(this);
    }

    public String getChannel() {
        return this.channel;
    }

    public byte[] getPayload() {
        return this.data;
    }

    @Override
    public void processPacket(INetHandler netHandler) throws IOException {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
