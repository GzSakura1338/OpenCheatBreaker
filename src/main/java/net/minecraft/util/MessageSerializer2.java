package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import net.minecraft.network.PacketBuffer;

public class MessageSerializer2 extends MessageToByteEncoder {
    public void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) {
        int readableBytes = byteBuf.readableBytes();
        int varIntSize = PacketBuffer.getVarIntSize(readableBytes);
        if (varIntSize > 3) {
            throw new IllegalArgumentException("unable to fit " + readableBytes + " into " + 3);
        }

        PacketBuffer packetBuffer = new PacketBuffer(byteBuf2);
        packetBuffer.ensureWritable(varIntSize + readableBytes);
        packetBuffer.writeVarIntToBuffer(readableBytes);
        packetBuffer.writeBytes(byteBuf, byteBuf.readerIndex(), readableBytes);
    }

    public void encode(ChannelHandlerContext channelHandlerContext, Object obj, ByteBuf byteBuf) {
        this.encode(channelHandlerContext, (ByteBuf)obj, byteBuf);
    }
}
