package net.minecraft.network;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
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
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;
import net.minecraft.util.ReportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NetworkSystem {
   public static Logger logger = LogManager.getLogger();
   public static LazyLoadBase<NioEventLoopGroup> eventLoops = new LazyLoadBase<NioEventLoopGroup>() {
      public NioEventLoopGroup load() {
         return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Server IO #%d").setDaemon(true).build());
      }
   };
   public static LazyLoadBase<EpollEventLoopGroup> SERVER_EPOLL_EVENTLOOP = new LazyLoadBase<EpollEventLoopGroup>() {
      public EpollEventLoopGroup load() {
         return new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
      }
   };
   public static LazyLoadBase<LocalEventLoopGroup> SERVER_LOCAL_EVENTLOOP = new LazyLoadBase<LocalEventLoopGroup>() {
      public LocalEventLoopGroup load() {
         return new LocalEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Server IO #%d").setDaemon(true).build());
      }
   };
   public List<ChannelFuture> endpoints = Collections.synchronizedList(Lists.newArrayList());
   public List<NetworkManager> networkManagers = Collections.synchronizedList(Lists.newArrayList());
   public MinecraftServer mcServer;
   public volatile boolean isAlive;

   public NetworkSystem(MinecraftServer server) {
      this.mcServer = server;
      this.isAlive = true;
   }

   public void addLanEndpoint(InetAddress address, int port) throws IOException {
      synchronized (this.endpoints) {
         Class<? extends io.netty.channel.ServerChannel> clazz;
         LazyLoadBase lazyLoadBase;
         if (Epoll.isAvailable() && this.mcServer.shouldUseNativeTransport()) {
            clazz = EpollServerSocketChannel.class;
            lazyLoadBase = SERVER_EPOLL_EVENTLOOP;
            logger.info("Using epoll channel type");
         } else {
            clazz = NioServerSocketChannel.class;
            lazyLoadBase = eventLoops;
            logger.info("Using default channel type");
         }

         this.endpoints
            .add(
               ((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel(clazz))
                     .childHandler(
                        new ChannelInitializer<Channel>() {
                           public void initChannel(Channel channel) throws Exception {
                              try {
                                 channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                              } catch (ChannelException e) {
                              }

                              channel.pipeline()
                                 .addLast("timeout", new ReadTimeoutHandler(30))
                                 .addLast("legacy_query", new PingResponseHandler(NetworkSystem.this))
                                 .addLast("splitter", new MessageDeserializer2())
                                 .addLast("decoder", new MessageDeserializer(EnumPacketDirection.SERVERBOUND))
                                 .addLast("prepender", new MessageSerializer2())
                                 .addLast("encoder", new MessageSerializer(EnumPacketDirection.CLIENTBOUND));
                              NetworkManager networkManager = new NetworkManager(EnumPacketDirection.SERVERBOUND);
                              NetworkSystem.this.networkManagers.add(networkManager);
                              channel.pipeline().addLast("packet_handler", networkManager);
                              networkManager.setNetHandler(new NetHandlerHandshakeTCP(NetworkSystem.this.mcServer, networkManager));
                           }
                        }
                     )
                     .group((EventLoopGroup)lazyLoadBase.getValue())
                     .localAddress(address, port))
                  .bind()
                  .syncUninterruptibly()
            );
      }
   }

   public SocketAddress addLocalEndpoint() {
      ChannelFuture channelFuture;
      synchronized (this.endpoints) {
         channelFuture = ((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().channel(LocalServerChannel.class)).childHandler(new ChannelInitializer<Channel>() {
            public void initChannel(Channel channel) throws Exception {
               NetworkManager networkManager = new NetworkManager(EnumPacketDirection.SERVERBOUND);
               networkManager.setNetHandler(new NetHandlerHandshakeMemory(NetworkSystem.this.mcServer, networkManager));
               NetworkSystem.this.networkManagers.add(networkManager);
               channel.pipeline().addLast("packet_handler", networkManager);
            }
         }).group((EventLoopGroup)eventLoops.getValue()).localAddress(LocalAddress.ANY)).bind().syncUninterruptibly();
         this.endpoints.add(channelFuture);
      }

      return channelFuture.channel().localAddress();
   }

   public void terminateEndpoints() {
      this.isAlive = false;

      for (ChannelFuture channelFuture : this.endpoints) {
         try {
            channelFuture.channel().close().sync();
         } catch (InterruptedException e) {
            logger.error("Interrupted whilst closing channel");
         }
      }
   }

   public void networkTick() {
      synchronized (this.networkManagers) {
         Iterator iterator = this.networkManagers.iterator();

         while (iterator.hasNext()) {
            final NetworkManager networkManager = (NetworkManager)iterator.next();
            if (!networkManager.hasNoChannel()) {
               if (!networkManager.isChannelOpen()) {
                  iterator.remove();
                  networkManager.checkDisconnected();
               } else {
                  try {
                     networkManager.processReceivedPackets();
                  } catch (Exception e) {
                     if (networkManager.isLocalChannel()) {
                        CrashReport makeCrashReport = CrashReport.makeCrashReport(e, "Ticking memory connection");
                        CrashReportCategory makeCategory = makeCrashReport.makeCategory("Ticking connection");
                        makeCategory.addCrashSectionCallable("Connection", new Callable<String>() {
                           public String call() throws Exception {
                              return networkManager.toString();
                           }
                        });
                        throw new ReportedException(makeCrashReport);
                     }

                     logger.warn("Failed to handle packet for " + networkManager.getRemoteAddress(), e);
                     final ChatComponentText chatComponentText = new ChatComponentText("Internal server error");
                     networkManager.sendPacket(new S40PacketDisconnect(chatComponentText), new GenericFutureListener<Future<? super Void>>() {
                        public void operationComplete(Future<? super Void> future) throws Exception {
                           networkManager.closeChannel(chatComponentText);
                        }
                     });
                     networkManager.disableAutoRead();
                  }
               }
            }
         }
      }
   }

   public MinecraftServer getServer() {
      return this.mcServer;
   }
}
