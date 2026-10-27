package net.minecraft.client.network;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C00PacketServerQuery;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class OldServerPinger {
   public static Splitter PING_RESPONSE_SPLITTER = Splitter.on('\u0000').limit(6);
   public static Logger logger = LogManager.getLogger();
   public List<NetworkManager> pingDestinations = Collections.synchronizedList(Lists.newArrayList());

   public void ping(final ServerData server) throws UnknownHostException {
      ServerAddress fromString = ServerAddress.fromString(server.serverIP);
      final NetworkManager createNetworkManagerAndConnect = NetworkManager.createNetworkManagerAndConnect(InetAddress.getByName(fromString.getIP()), fromString.getPort(), false);
      this.pingDestinations.add(createNetworkManagerAndConnect);
      server.serverMOTD = "Pinging...";
      server.pingToServer = -1L;
      server.playerList = null;
      createNetworkManagerAndConnect.setNetHandler(
         new INetHandlerStatusClient() {
            public boolean sentQuery = false;
            public boolean received = false;
            public long startTime = 0L;

            @Override
            public void handleServerInfo(S00PacketServerInfo packetIn) {
               if (this.received) {
                  createNetworkManagerAndConnect.closeChannel(new ChatComponentText("Received unrequested status"));
               } else {
                  this.received = true;
                  ServerStatusResponse response = packetIn.getResponse();
                  if (response.getServerDescription() != null) {
                     server.serverMOTD = response.getServerDescription().getFormattedText();
                  } else {
                     server.serverMOTD = "";
                  }

                  if (response.getProtocolVersionInfo() != null) {
                     server.gameVersion = response.getProtocolVersionInfo().getName();
                     server.version = response.getProtocolVersionInfo().getProtocol();
                  } else {
                     server.gameVersion = "Old";
                     server.version = 0;
                  }

                  if (response.getPlayerCountData() == null) {
                     server.populationInfo = EnumChatFormatting.DARK_GRAY + "???";
                  } else {
                     server.populationInfo = EnumChatFormatting.GRAY
                        + ""
                        + response.getPlayerCountData().getOnlinePlayerCount()
                        + ""
                        + EnumChatFormatting.DARK_GRAY
                        + "/"
                        + EnumChatFormatting.GRAY
                        + response.getPlayerCountData().getMaxPlayers();
                     if (ArrayUtils.isNotEmpty(response.getPlayerCountData().getPlayers())) {
                        StringBuilder sb = new StringBuilder();

                        for (GameProfile gameProfile : response.getPlayerCountData().getPlayers()) {
                           if (sb.length() > 0) {
                              sb.append("\n");
                           }

                           sb.append(gameProfile.getName());
                        }

                        if (response.getPlayerCountData().getPlayers().length < response.getPlayerCountData().getOnlinePlayerCount()) {
                           if (sb.length() > 0) {
                              sb.append("\n");
                           }

                           sb.append("... and ")
                              .append(response.getPlayerCountData().getOnlinePlayerCount() - response.getPlayerCountData().getPlayers().length)
                              .append(" more ...");
                        }

                        server.playerList = sb.toString();
                     }
                  }

                  if (response.getFavicon() != null) {
                     String favicon = response.getFavicon();
                     if (favicon.startsWith("data:image/png;base64,")) {
                        server.setBase64EncodedIconData(favicon.substring("data:image/png;base64,".length()));
                     } else {
                        OldServerPinger.logger.error("Invalid server icon (unknown format)");
                     }
                  } else {
                     server.setBase64EncodedIconData((String)null);
                  }

                  this.startTime = Minecraft.getSystemTime();
                  createNetworkManagerAndConnect.sendPacket(new C01PacketPing(this.startTime));
                  this.sentQuery = true;
               }
            }

            @Override
            public void handlePong(S01PacketPong packetIn) {
               long startTime = this.startTime;
               long systemTime = Minecraft.getSystemTime();
               server.pingToServer = systemTime - startTime;
               createNetworkManagerAndConnect.closeChannel(new ChatComponentText("Finished"));
            }

            @Override
            public void onDisconnect(IChatComponent reason) {
               if (!this.sentQuery) {
                  OldServerPinger.logger.error("Can't ping " + server.serverIP + ": " + reason.getUnformattedText());
                  server.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
                  server.populationInfo = "";
                  OldServerPinger.this.tryCompatibilityPing(server);
               }
            }
         }
      );

      try {
         createNetworkManagerAndConnect.sendPacket(new C00Handshake(47, fromString.getIP(), fromString.getPort(), EnumConnectionState.STATUS));
         createNetworkManagerAndConnect.sendPacket(new C00PacketServerQuery());
      } catch (Throwable e) {
         logger.error(e);
      }
   }

   public void tryCompatibilityPing(final ServerData server) {
      final ServerAddress fromString = ServerAddress.fromString(server.serverIP);
      ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)NetworkManager.CLIENT_NIO_EVENTLOOP.getValue()))
               .handler(
                  new ChannelInitializer<Channel>() {
                     public void initChannel(Channel channel) throws Exception {
                        try {
                           channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                        } catch (ChannelException e) {
                        }

                        channel.pipeline()
                           .addLast(
                              new ChannelHandler[]{
                                 new SimpleChannelInboundHandler<ByteBuf>() {
                                    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
                                       super.channelActive(channelHandlerContext);
                                       ByteBuf buffer = Unpooled.buffer();

                                       try {
                                          buffer.writeByte(254);
                                          buffer.writeByte(1);
                                          buffer.writeByte(250);
                                          char[] toCharArray = "MC|PingHost".toCharArray();
                                          buffer.writeShort(toCharArray.length);

                                          for (char c : toCharArray) {
                                             buffer.writeChar(c);
                                          }

                                          buffer.writeShort(7 + 2 * fromString.getIP().length());
                                          buffer.writeByte(127);
                                          toCharArray = fromString.getIP().toCharArray();
                                          buffer.writeShort(toCharArray.length);

                                          for (char c : toCharArray) {
                                             buffer.writeChar(c);
                                          }

                                          buffer.writeInt(fromString.getPort());
                                          channelHandlerContext.channel().writeAndFlush(buffer).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                                       } finally {
                                          buffer.release();
                                       }
                                    }

                                    public void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                                       short readUnsignedByte = byteBuf.readUnsignedByte();
                                       if (readUnsignedByte == 255) {
                                          String str = new String(byteBuf.readBytes(byteBuf.readShort() * 2).array(), Charsets.UTF_16BE);
                                          String[] toArray = (String[])Iterables.toArray(OldServerPinger.PING_RESPONSE_SPLITTER.split(str), String.class);
                                          if ("§1".equals(toArray[0])) {
                                             int parseIntWithDefault = MathHelper.parseIntWithDefault(toArray[1], 0);
                                             String str2 = toArray[2];
                                             String str3 = toArray[3];
                                             int parseIntWithDefault2 = MathHelper.parseIntWithDefault(toArray[4], -1);
                                             int parseIntWithDefault3 = MathHelper.parseIntWithDefault(toArray[5], -1);
                                             server.version = -1;
                                             server.gameVersion = str2;
                                             server.serverMOTD = str3;
                                             server.populationInfo = EnumChatFormatting.GRAY
                                                + ""
                                                + parseIntWithDefault2
                                                + ""
                                                + EnumChatFormatting.DARK_GRAY
                                                + "/"
                                                + EnumChatFormatting.GRAY
                                                + parseIntWithDefault3;
                                          }
                                       }

                                       channelHandlerContext.close();
                                    }

                                    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
                                       channelHandlerContext.close();
                                    }
                                 }
                              }
                           );
                     }
                  }
               ))
            .channel(NioSocketChannel.class))
         .connect(fromString.getIP(), fromString.getPort());
   }

   public void pingPendingNetworks() {
      synchronized (this.pingDestinations) {
         Iterator iterator = this.pingDestinations.iterator();

         while (iterator.hasNext()) {
            NetworkManager networkManager = (NetworkManager)iterator.next();
            if (networkManager.isChannelOpen()) {
               networkManager.processReceivedPackets();
            } else {
               iterator.remove();
               networkManager.checkDisconnected();
            }
         }
      }
   }

   public void clearPendingNetworks() {
      synchronized (this.pingDestinations) {
         Iterator iterator = this.pingDestinations.iterator();

         while (iterator.hasNext()) {
            NetworkManager networkManager = (NetworkManager)iterator.next();
            if (networkManager.isChannelOpen()) {
               iterator.remove();
               networkManager.closeChannel(new ChatComponentText("Cancelled"));
            }
         }
      }
   }
}
