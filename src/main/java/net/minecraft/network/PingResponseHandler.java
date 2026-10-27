package net.minecraft.network;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.InetSocketAddress;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PingResponseHandler extends ChannelInboundHandlerAdapter {
   public static Logger logger = LogManager.getLogger();
   public NetworkSystem networkSystem;

   public PingResponseHandler(NetworkSystem networkSystemIn) {
      this.networkSystem = networkSystemIn;
   }

   public void channelRead(ChannelHandlerContext channelHandlerContext, Object obj) throws Exception {
      ByteBuf byteBuf = (ByteBuf)obj;
      byteBuf.markReaderIndex();
      boolean flag = true;

      try {
         if (byteBuf.readUnsignedByte() == 254) {
            InetSocketAddress remoteAddress = (InetSocketAddress)channelHandlerContext.channel().remoteAddress();
            MinecraftServer server = this.networkSystem.getServer();
            int readableBytes = byteBuf.readableBytes();
            switch (readableBytes) {
               case 0:
                  logger.debug("Ping: (<1.3.x) from {}:{}", new Object[]{remoteAddress.getAddress(), remoteAddress.getPort()});
                  String format = String.format("%s§%d§%d", server.getMOTD(), server.getCurrentPlayerCount(), server.getMaxPlayers());
                  this.writeAndFlush(channelHandlerContext, this.getStringBuffer(format));
                  break;
               case 1:
                  if (byteBuf.readUnsignedByte() != 1) {
                     return;
                  }

                  logger.debug("Ping: (1.4-1.5.x) from {}:{}", new Object[]{remoteAddress.getAddress(), remoteAddress.getPort()});
                  String format2 = String.format(
                     "§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d",
                     127,
                     server.getMinecraftVersion(),
                     server.getMOTD(),
                     server.getCurrentPlayerCount(),
                     server.getMaxPlayers()
                  );
                  this.writeAndFlush(channelHandlerContext, this.getStringBuffer(format2));
                  break;
               default:
                  boolean flag2 = byteBuf.readUnsignedByte() == 1;
                  flag2 &= byteBuf.readUnsignedByte() == 250;
                  flag2 &= "MC|PingHost".equals(new String(byteBuf.readBytes(byteBuf.readShort() * 2).array(), Charsets.UTF_16BE));
                  int readUnsignedShort = byteBuf.readUnsignedShort();
                  flag2 &= byteBuf.readUnsignedByte() >= 73;
                  flag2 &= 3 + byteBuf.readBytes(byteBuf.readShort() * 2).array().length + 4 == readUnsignedShort;
                  flag2 &= byteBuf.readInt() <= 65535;
                  flag2 &= byteBuf.readableBytes() == 0;
                  if (!flag2) {
                     return;
                  }

                  logger.debug("Ping: (1.6) from {}:{}", new Object[]{remoteAddress.getAddress(), remoteAddress.getPort()});
                  String format3 = String.format(
                     "§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d",
                     127,
                     server.getMinecraftVersion(),
                     server.getMOTD(),
                     server.getCurrentPlayerCount(),
                     server.getMaxPlayers()
                  );
                  ByteBuf stringBuffer = this.getStringBuffer(format3);

                  try {
                     this.writeAndFlush(channelHandlerContext, stringBuffer);
                  } finally {
                     stringBuffer.release();
                  }
            }

            byteBuf.release();
            flag = false;
         }
      } catch (RuntimeException e) {
      } finally {
         if (flag) {
            byteBuf.resetReaderIndex();
            channelHandlerContext.channel().pipeline().remove("legacy_query");
            channelHandlerContext.fireChannelRead(obj);
         }
      }
   }

   public void writeAndFlush(ChannelHandlerContext ctx, ByteBuf data) {
      ctx.pipeline().firstContext().writeAndFlush(data).addListener(ChannelFutureListener.CLOSE);
   }

   public ByteBuf getStringBuffer(String string) {
      ByteBuf buffer = Unpooled.buffer();
      buffer.writeByte(255);
      char[] toCharArray = string.toCharArray();
      buffer.writeShort(toCharArray.length);

      for (char c : toCharArray) {
         buffer.writeChar(c);
      }

      return buffer;
   }
}
