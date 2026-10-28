package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import net.minecraft.network.PacketBuffer;

public class MessageDeserializer2 extends ByteToMessageDecoder {
    public void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List list) {
        byteBuf.markReaderIndex();
        byte[] bytes = new byte[3];

        for (int i = 0; i < bytes.length; i++) {
            if (!byteBuf.isReadable()) {
                byteBuf.resetReaderIndex();
                return;
            }

            bytes[i] = byteBuf.readByte();
            if (bytes[i] >= 0) {
                PacketBuffer packetBuffer = new PacketBuffer(Unpooled.wrappedBuffer(bytes));

                try {
                    int readVarIntFromBuffer = packetBuffer.readVarIntFromBuffer();
                    if (byteBuf.readableBytes() >= readVarIntFromBuffer) {
                        list.add(byteBuf.readBytes(readVarIntFromBuffer));
                        return;
                    }

                    byteBuf.resetReaderIndex();
                } finally {
                    packetBuffer.release();
                }

                return;
            }
        }

        throw new CorruptedFrameException("length wider than 21-bit");
    }
}
