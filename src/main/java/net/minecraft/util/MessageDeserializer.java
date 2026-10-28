package net.minecraft.util;

import com.google.common.collect.BiMap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkStatistics;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class MessageDeserializer extends ByteToMessageDecoder {
    public static Logger logger = LogManager.getLogger();
    public static Marker MARKER = MarkerManager.getMarker("PACKET_RECEIVED", NetworkManager.logMarkerPackets);
    public NetworkStatistics networkStatistics;

    public MessageDeserializer(NetworkStatistics networkStatistics) {
        this.networkStatistics = networkStatistics;
    }

    public void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List list) throws IOException {
        int readableBytes = byteBuf.readableBytes();
        if (readableBytes != 0) {
            PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
            int readVarIntFromBuffer = packetBuffer.readVarIntFromBuffer();
            Packet generatePacket = Packet.generatePacket((BiMap)channelHandlerContext.channel().attr(NetworkManager.attrKeyReceivable).get(), readVarIntFromBuffer);
            if (generatePacket == null) {
                throw new IOException("Bad packet id " + readVarIntFromBuffer);
            }

            generatePacket.readPacketData(packetBuffer);
            if (packetBuffer.readableBytes() > 0) {
                throw new IOException("Packet was larger than I expected, found " + packetBuffer.readableBytes() + " bytes extra whilst reading packet " + readVarIntFromBuffer);
            }

            list.add(generatePacket);
            this.networkStatistics.process(readVarIntFromBuffer, readableBytes);
            if (logger.isDebugEnabled()) {
                logger.debug(
                    MARKER,
                    " IN: [{}:{}] {}[{}]",
                    channelHandlerContext.channel().attr(NetworkManager.attrKeyConnectionState).get(),
                    readVarIntFromBuffer,
                    generatePacket.getClass().getName(),
                    generatePacket.serialize()
                );
            }
        }
    }
}
