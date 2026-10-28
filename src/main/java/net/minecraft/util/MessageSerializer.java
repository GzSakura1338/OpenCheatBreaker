package net.minecraft.util;

import com.google.common.collect.BiMap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.NetworkStatistics;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class MessageSerializer extends MessageToByteEncoder {
    public static Logger logger = LogManager.getLogger();
    public static Marker MARKER = MarkerManager.getMarker("PACKET_SENT", NetworkManager.logMarkerPackets);
    public NetworkStatistics networkStatistics;

    public MessageSerializer(NetworkStatistics networkStatistics) {
        this.networkStatistics = networkStatistics;
    }

    public void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws IOException {
        Integer integer = (Integer)((BiMap)channelHandlerContext.channel().attr(NetworkManager.attrKeySendable).get()).inverse().get(packet.getClass());
        if (logger.isDebugEnabled()) {
            logger.debug(
                MARKER, "OUT: [{}:{}] {}[{}]", channelHandlerContext.channel().attr(NetworkManager.attrKeyConnectionState).get(), integer, packet.getClass().getName(), packet.serialize()
            );
        }

        if (integer == null) {
            throw new IOException("Can't serialize unregistered packet");
        }

        PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
        packetBuffer.writeVarIntToBuffer(integer);
        packet.writePacketData(packetBuffer);
        this.networkStatistics.process2(integer, packetBuffer.readableBytes());
    }

    public void encode(ChannelHandlerContext channelHandlerContext, Object obj, ByteBuf byteBuf) throws IOException {
        this.encode(channelHandlerContext, (Packet)obj, byteBuf);
    }
}
