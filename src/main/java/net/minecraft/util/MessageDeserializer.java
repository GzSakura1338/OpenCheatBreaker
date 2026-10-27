package net.minecraft.util;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import java.io.IOException;
import java.util.List;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.EnumPacketDirection;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class MessageDeserializer extends ByteToMessageDecoder {
   public static Logger logger = LogManager.getLogger();
   public static Marker RECEIVED_PACKET_MARKER = MarkerManager.getMarker("PACKET_RECEIVED", NetworkManager.logMarkerPackets);
   public EnumPacketDirection direction;

   public MessageDeserializer(EnumPacketDirection direction) {
      this.direction = direction;
   }

   public void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws IOException, Exception, IllegalAccessException, InstantiationException {
      if (byteBuf.readableBytes() != 0) {
         PacketBuffer packetBuffer = new PacketBuffer(byteBuf);
         int readVarIntFromBuffer = packetBuffer.readVarIntFromBuffer();
         Packet packet = ((EnumConnectionState)channelHandlerContext.channel().attr(NetworkManager.attrKeyConnectionState).get()).getPacket(this.direction, readVarIntFromBuffer);
         if (packet == null) {
            throw new IOException("Bad packet id " + readVarIntFromBuffer);
         }

         packet.readPacketData(packetBuffer);
         if (packetBuffer.readableBytes() > 0) {
            throw new IOException(
               "Packet "
                  + ((EnumConnectionState)channelHandlerContext.channel().attr(NetworkManager.attrKeyConnectionState).get()).getId()
                  + "/"
                  + readVarIntFromBuffer
                  + " ("
                  + packet.getClass().getSimpleName()
                  + ") was larger than I expected, found "
                  + packetBuffer.readableBytes()
                  + " bytes extra whilst reading packet "
                  + readVarIntFromBuffer
            );
         }

         list.add(packet);
         if (logger.isDebugEnabled()) {
            logger.debug(
               RECEIVED_PACKET_MARKER,
               " IN: [{}:{}] {}",
               new Object[]{channelHandlerContext.channel().attr(NetworkManager.attrKeyConnectionState).get(), readVarIntFromBuffer, packet.getClass().getName()}
            );
         }
      }
   }
}
