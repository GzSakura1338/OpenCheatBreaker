package net.minecraft.network;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import net.minecraft.client.network.NetHandlerHandshakeMemory;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.network.play.server.S40PacketDisconnect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.NetHandlerHandshakeTCP;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;
import net.minecraft.util.ReportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkSystem {
    public static Logger logger = LogManager.getLogger();
    public static NioEventLoopGroup eventLoops = new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty IO #%d").setDaemon(true).build());
    public List endpoints = Collections.synchronizedList(new ArrayList());
    public List networkManagers = Collections.synchronizedList(new ArrayList());
    public MinecraftServer mcServer;
    public volatile boolean isAlive;

    public NetworkSystem(MinecraftServer server) {
        this.mcServer = server;
        this.isAlive = true;
    }

    public void addLanEndpoint(InetAddress address, int port) throws IOException {
        List endpoints = this.endpoints;
        synchronized (this.endpoints) {
            this.endpoints
                .add(
                    ((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel(NioServerSocketChannel.class))
                            .childHandler(
                                new ChannelInitializer() {
                                    public void initChannel(Channel channel) {
                                        try {
                                            channel.config().setOption(ChannelOption.IP_TOS, 24);
                                        } catch (ChannelException e) {
                                        }

                                        try {
                                            channel.config().setOption(ChannelOption.TCP_NODELAY, false);
                                        } catch (ChannelException e) {
                                        }

                                        channel.pipeline()
                                            .addLast("timeout", new ReadTimeoutHandler(30))
                                            .addLast("legacy_query", new PingResponseHandler(NetworkSystem.this))
                                            .addLast("splitter", new MessageDeserializer2())
                                            .addLast("decoder", new MessageDeserializer(NetworkManager.STATISTICS))
                                            .addLast("prepender", new MessageSerializer2())
                                            .addLast("encoder", new MessageSerializer(NetworkManager.STATISTICS));
                                        NetworkManager networkManager = new NetworkManager(false);
                                        NetworkSystem.this.networkManagers.add(networkManager);
                                        channel.pipeline().addLast("packet_handler", networkManager);
                                        networkManager.setNetHandler(new NetHandlerHandshakeTCP(NetworkSystem.this.mcServer, networkManager));
                                    }
                                }
                            )
                            .group(eventLoops)
                            .localAddress(address, port))
                        .bind()
                        .syncUninterruptibly()
                );
        }
    }

    public SocketAddress addLocalEndpoint() {
        List endpoints = this.endpoints;
        ChannelFuture channelFuture;
        synchronized (this.endpoints) {
            channelFuture = ((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel(LocalServerChannel.class)).childHandler(new ChannelInitializer() {
                public void initChannel(Channel channel) {
                    NetworkManager networkManager = new NetworkManager(false);
                    networkManager.setNetHandler(new NetHandlerHandshakeMemory(NetworkSystem.this.mcServer, networkManager));
                    NetworkSystem.this.networkManagers.add(networkManager);
                    channel.pipeline().addLast("packet_handler", networkManager);
                }
            }).group(eventLoops).localAddress(LocalAddress.ANY)).bind().syncUninterruptibly();
            this.endpoints.add(channelFuture);
        }

        return channelFuture.channel().localAddress();
    }

    public void terminateEndpoints() {
        this.isAlive = false;

        for (ChannelFuture channelFuture : (Iterable<ChannelFuture>)this.endpoints) {
            channelFuture.channel().close().syncUninterruptibly();
        }
    }

    public void networkTick() throws IOException {
        List networkManagers = this.networkManagers;
        synchronized (this.networkManagers) {
            Iterator iterator = this.networkManagers.iterator();

            while (iterator.hasNext()) {
                final NetworkManager networkManager = (NetworkManager)iterator.next();
                if (!networkManager.isChannelOpen()) {
                    iterator.remove();
                    if (networkManager.getExitMessage() != null) {
                        networkManager.getNetHandler().onDisconnect(networkManager.getExitMessage());
                    } else if (networkManager.getNetHandler() != null) {
                        networkManager.getNetHandler().onDisconnect(new ChatComponentText("Disconnected"));
                    }
                } else {
                    try {
                        networkManager.processReceivedPackets();
                    } catch (Exception e) {
                        if (networkManager.isLocalChannel()) {
                            CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Ticking memory connection");
                            CrashReportCategory makeCategory = makeCrashReport.makeCategory("Ticking connection");
                            makeCategory.addCrashSectionCallable("Connection", new Callable() {
                                public String call() {
                                    return networkManager.toString();
                                }
                            });
                            throw new ReportedException(makeCrashReport);
                        }

                        logger.warn("Failed to handle packet for " + networkManager.getRemoteAddress(), e);
                        final ChatComponentText chatComponentText = new ChatComponentText("Internal server error");
                        networkManager.scheduleOutboundPacket(new S40PacketDisconnect(chatComponentText), new GenericFutureListener() {
                            public void operationComplete(Future future) {
                                networkManager.closeChannel(chatComponentText);
                            }
                        });
                        networkManager.disableAutoRead();
                    }
                }
            }
        }
    }

    public MinecraftServer getServer() {
        return this.mcServer;
    }
}
