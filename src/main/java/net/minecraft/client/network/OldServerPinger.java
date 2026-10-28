package net.minecraft.client.network;

import com.google.common.base.Charsets;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
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
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
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
    public List pingDestinations = Collections.synchronizedList(new ArrayList());

    public void ping(final ServerData server) throws UnknownHostException {
        ServerAddress parse = ServerAddress.parse(server.serverIP);
        final NetworkManager provideLanClient = NetworkManager.provideLanClient(InetAddress.getByName(parse.getIP()), parse.getPort());
        this.pingDestinations.add(provideLanClient);
        server.serverMOTD = "Pinging...";
        server.pingToServer = -1L;
        server.playerList = null;
        provideLanClient.setNetHandler(
            new INetHandlerStatusClient() {
                public boolean flag = false;

                @Override
                public void handleServerInfo(S00PacketServerInfo packetIn) {
                    ServerStatusResponse serverMetadata = packetIn.getServerMetadata();
                    server.flag3 = serverMetadata.text != null;
                    if (serverMetadata.getServerDescription() != null) {
                        server.serverMOTD = serverMetadata.getServerDescription().getFormattedText();
                    } else {
                        server.serverMOTD = "";
                    }

                    if (serverMetadata.getProtocolVersionInfo() != null) {
                        server.gameVersion = serverMetadata.getProtocolVersionInfo().getName();
                        server.version = serverMetadata.getProtocolVersionInfo().getProtocol();
                        server.flag2 = true;
                    } else {
                        server.gameVersion = "Old";
                        server.version = 0;
                    }

                    if (serverMetadata.getPlayerCountData() != null) {
                        server.populationInfo = EnumChatFormatting.GRAY
                            + ""
                            + serverMetadata.getPlayerCountData().getOnlinePlayerCount()
                            + ""
                            + EnumChatFormatting.DARK_GRAY
                            + "/"
                            + EnumChatFormatting.GRAY
                            + serverMetadata.getPlayerCountData().getMaxPlayers();
                        if (ArrayUtils.isNotEmpty(serverMetadata.getPlayerCountData().getPlayers())) {
                            StringBuilder sb = new StringBuilder();

                            for (GameProfile gameProfile : serverMetadata.getPlayerCountData().getPlayers()) {
                                if (sb.length() > 0) {
                                    sb.append("\n");
                                }

                                sb.append(gameProfile.getName());
                            }

                            if (serverMetadata.getPlayerCountData().getPlayers().length < serverMetadata.getPlayerCountData().getOnlinePlayerCount()) {
                                if (sb.length() > 0) {
                                    sb.append("\n");
                                }

                                sb.append("... and ")
                                    .append(serverMetadata.getPlayerCountData().getOnlinePlayerCount() - serverMetadata.getPlayerCountData().getPlayers().length)
                                    .append(" more ...");
                            }

                            server.playerList = sb.toString();
                        }
                    } else {
                        server.populationInfo = EnumChatFormatting.DARK_GRAY + "???";
                    }

                    if (serverMetadata.getFavicon() != null) {
                        String favicon = serverMetadata.getFavicon();
                        if (favicon.startsWith("data:image/png;base64,")) {
                            server.setBase64EncodedIconData(favicon.substring("data:image/png;base64,".length()));
                        } else {
                            OldServerPinger.logger.error("Invalid server icon (unknown format)");
                        }
                    } else {
                        server.setBase64EncodedIconData(null);
                    }

                    provideLanClient.scheduleOutboundPacket(new C01PacketPing(Minecraft.getSystemTime()));
                    this.flag = true;
                }

                @Override
                public void handlePong(S01PacketPong packetIn) {
                    long clientTime = packetIn.getClientTime();
                    long systemTime = Minecraft.getSystemTime();
                    server.pingToServer = systemTime - clientTime;
                    provideLanClient.closeChannel(new ChatComponentText("Finished"));
                }

                @Override
                public void onDisconnect(IChatComponent reason) {
                    if (!this.flag) {
                        OldServerPinger.logger.error("Can't ping " + server.serverIP + ": " + reason.getUnformattedText());
                        server.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
                        server.populationInfo = "";
                        OldServerPinger.this.tryCompatibilityPing(server);
                    }
                }

                @Override
                public void onConnectionStateTransition(EnumConnectionState oldState, EnumConnectionState newState) {
                    if (newState != EnumConnectionState.STATUS) {
                        throw new UnsupportedOperationException("Unexpected change in protocol to " + newState);
                    }
                }

                @Override
                public void onNetworkTick() {
                }
            }
        );

        try {
            provideLanClient.scheduleOutboundPacket(new C00Handshake(5, parse.getIP(), parse.getPort(), EnumConnectionState.STATUS));
            provideLanClient.scheduleOutboundPacket(new C00PacketServerQuery());
        } catch (Throwable e) {
            logger.error(e);
        }
    }

    public void tryCompatibilityPing(final ServerData server) {
        final ServerAddress parse = ServerAddress.parse(server.serverIP);
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(NetworkManager.eventLoops)).handler(new ChannelInitializer() {
            public void initChannel(Channel channel) {
                try {
                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                } catch (ChannelException e) {
                }

                channel.pipeline().addLast(new ChannelHandler[]{new SimpleChannelInboundHandler() {
                    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
                        super.channelActive(channelHandlerContext);
                        ByteBuf buffer = Unpooled.buffer();

                        try {
                            buffer.writeByte(254);
                            buffer.writeByte(1);
                            buffer.writeByte(250);
                            char[] toCharArray = "MC|PingHost".toCharArray();
                            buffer.writeShort(toCharArray.length);
                            char[] chars = toCharArray;
                            int length = toCharArray.length;

                            for (int i = 0; i < length; i++) {
                                char c = chars[i];
                                buffer.writeChar(c);
                            }

                            buffer.writeShort(7 + 2 * parse.getIP().length());
                            buffer.writeByte(127);
                            toCharArray = parse.getIP().toCharArray();
                            buffer.writeShort(toCharArray.length);
                            chars = toCharArray;
                            length = toCharArray.length;

                            for (int i = 0; i < length; i++) {
                                char c = chars[i];
                                buffer.writeChar(c);
                            }

                            buffer.writeInt(parse.getPort());
                            channelHandlerContext.channel().writeAndFlush(buffer).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                        } finally {
                            buffer.release();
                        }
                    }

                    public void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
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
                                server.populationInfo = EnumChatFormatting.GRAY + "" + parseIntWithDefault2 + "" + EnumChatFormatting.DARK_GRAY + "/" + EnumChatFormatting.GRAY + parseIntWithDefault3;
                            }
                        }

                        channelHandlerContext.close();
                    }

                    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {
                        channelHandlerContext.close();
                    }

                    public void channelRead0(ChannelHandlerContext channelHandlerContext, Object obj) {
                        this.channelRead0(channelHandlerContext, (ByteBuf)obj);
                    }
                }});
            }
        })).channel(NioSocketChannel.class)).connect(parse.getIP(), parse.getPort());
    }

    public void pingPendingNetworks() throws java.io.IOException {
        List pingDestinations = this.pingDestinations;
        synchronized (this.pingDestinations) {
            Iterator iterator = this.pingDestinations.iterator();

            while (iterator.hasNext()) {
                NetworkManager networkManager = (NetworkManager)iterator.next();
                if (networkManager.isChannelOpen()) {
                    networkManager.processReceivedPackets();
                } else {
                    iterator.remove();
                    if (networkManager.getExitMessage() != null) {
                        networkManager.getNetHandler().onDisconnect(networkManager.getExitMessage());
                    }
                }
            }
        }
    }

    public void clearPendingNetworks() {
        List pingDestinations = this.pingDestinations;
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

    public void pingWithCallback(final ServerData serverData, final Consumer<Long> consumer) throws UnknownHostException {
        ServerAddress parse = ServerAddress.parse(serverData.serverIP);
        final NetworkManager provideLanClient = NetworkManager.provideLanClient(InetAddress.getByName(parse.getIP()), parse.getPort());
        this.pingDestinations.add(provideLanClient);
        serverData.serverMOTD = "Pinging...";
        serverData.pingToServer = -1L;
        serverData.playerList = null;
        provideLanClient.setNetHandler(
            new INetHandlerStatusClient() {
                public boolean flag = false;

                @Override
                public void onNetworkTick() {
                }

                @Override
                public void handleServerInfo(S00PacketServerInfo s00PacketServerInfo) {
                    ServerStatusResponse serverMetadata = s00PacketServerInfo.getServerMetadata();
                    serverData.flag3 = serverMetadata.text != null;
                    if (serverMetadata.getServerDescription() != null) {
                        serverData.serverMOTD = serverMetadata.getServerDescription().getFormattedText();
                    } else {
                        serverData.serverMOTD = "";
                    }

                    if (serverMetadata.getProtocolVersionInfo() != null) {
                        serverData.gameVersion = serverMetadata.getProtocolVersionInfo().getName();
                        serverData.version = serverMetadata.getProtocolVersionInfo().getProtocol();
                        serverData.flag2 = true;
                    } else {
                        serverData.gameVersion = "Old";
                        serverData.version = 0;
                    }

                    if (serverMetadata.getPlayerCountData() != null) {
                        serverData.populationInfo = EnumChatFormatting.GRAY
                            + ""
                            + serverMetadata.getPlayerCountData().getOnlinePlayerCount()
                            + ""
                            + EnumChatFormatting.DARK_GRAY
                            + "/"
                            + EnumChatFormatting.GRAY
                            + serverMetadata.getPlayerCountData().getMaxPlayers();
                        if (ArrayUtils.isNotEmpty(serverMetadata.getPlayerCountData().getPlayers())) {
                            StringBuilder sb = new StringBuilder();

                            for (GameProfile gameProfile : serverMetadata.getPlayerCountData().getPlayers()) {
                                if (sb.length() > 0) {
                                    sb.append("\n");
                                }

                                sb.append(gameProfile.getName());
                            }

                            if (serverMetadata.getPlayerCountData().getPlayers().length < serverMetadata.getPlayerCountData().getOnlinePlayerCount()) {
                                if (sb.length() > 0) {
                                    sb.append("\n");
                                }

                                sb.append("... and ")
                                    .append(serverMetadata.getPlayerCountData().getOnlinePlayerCount() - serverMetadata.getPlayerCountData().getPlayers().length)
                                    .append(" more ...");
                            }

                            serverData.playerList = sb.toString();
                        }
                    } else {
                        serverData.populationInfo = EnumChatFormatting.DARK_GRAY + "???";
                    }

                    if (serverMetadata.getFavicon() != null) {
                        String favicon = serverMetadata.getFavicon();
                        if (favicon.startsWith("data:image/png;base64,")) {
                            serverData.setBase64EncodedIconData(favicon.substring("data:image/png;base64,".length()));
                        } else {
                            OldServerPinger.logger.error("Invalid server icon (unknown format)");
                        }
                    } else {
                        serverData.setBase64EncodedIconData(null);
                    }

                    provideLanClient.scheduleOutboundPacket(new C01PacketPing(Minecraft.getSystemTime()));
                    this.flag = true;
                }

                @Override
                public void onConnectionStateTransition(EnumConnectionState enumConnectionState, EnumConnectionState enumConnectionState2) {
                    if (enumConnectionState2 != EnumConnectionState.STATUS) {
                        throw new UnsupportedOperationException("Unexpected change in protocol to " + enumConnectionState2);
                    }
                }

                @Override
                public void onDisconnect(IChatComponent chatComponent) {
                    if (!this.flag) {
                        OldServerPinger.logger.error("Can't ping " + serverData.serverIP + ": " + chatComponent.getUnformattedText());
                        serverData.serverMOTD = EnumChatFormatting.DARK_RED + "Can't connect to server.";
                        serverData.populationInfo = "";
                        OldServerPinger.this.tryCompatibilityPing(serverData);
                    }
                }

                @Override
                public void handlePong(S01PacketPong s01PacketPong) {
                    long clientTime = s01PacketPong.getClientTime();
                    long systemTime = Minecraft.getSystemTime();
                    serverData.pingToServer = systemTime - clientTime;
                    consumer.accept(serverData.pingToServer);
                    provideLanClient.closeChannel(new ChatComponentText("Finished"));
                }
            }
        );

        try {
            provideLanClient.scheduleOutboundPacket(new C00Handshake(5, parse.getIP(), parse.getPort(), EnumConnectionState.STATUS));
            provideLanClient.scheduleOutboundPacket(new C00PacketServerQuery());
        } catch (Throwable e) {
            logger.error(e);
        }
    }
}
