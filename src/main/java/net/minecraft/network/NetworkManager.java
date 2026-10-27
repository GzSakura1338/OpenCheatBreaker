package net.minecraft.network;

import com.cheatbreaker.client.network.ChannelDecryptionHandler;
import com.cheatbreaker.client.network.ChannelEncryptionHandler;
import com.cheatbreaker.client.network.PingPongHandler;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalEventLoopGroup;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import javax.crypto.SecretKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.ITickable;
import net.minecraft.util.LazyLoadBase;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NetworkManager extends SimpleChannelInboundHandler<Packet> {
   public static Logger logger = LogManager.getLogger();
   public static Marker logMarkerNetwork = MarkerManager.getMarker("NETWORK");
   public static Marker logMarkerPackets = MarkerManager.getMarker("NETWORK_PACKETS", logMarkerNetwork);
   public static AttributeKey<EnumConnectionState> attrKeyConnectionState = AttributeKey.valueOf("protocol");
   public static LazyLoadBase<NioEventLoopGroup> CLIENT_NIO_EVENTLOOP = new LazyLoadBase<NioEventLoopGroup>() {
      public NioEventLoopGroup load() {
         return new NioEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build());
      }
   };
   public static LazyLoadBase<EpollEventLoopGroup> CLIENT_EPOLL_EVENTLOOP = new LazyLoadBase<EpollEventLoopGroup>() {
      public EpollEventLoopGroup load() {
         return new EpollEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
      }
   };
   public static LazyLoadBase<LocalEventLoopGroup> CLIENT_LOCAL_EVENTLOOP = new LazyLoadBase<LocalEventLoopGroup>() {
      public LocalEventLoopGroup load() {
         return new LocalEventLoopGroup(0, new ThreadFactoryBuilder().setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
      }
   };
   public Queue<NetworkManager.InboundHandlerTuplePacketListener> outboundPacketsQueue = Queues.newConcurrentLinkedQueue();
   public ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
   public Set<String> stringSet = ImmutableSet.of(
      "7471b8e8-27c2-4354-a7d2-bd6a82dc00a0",
      "f11c7e0b-85e6-4b4b-afbf-e6cda9f531e5",
      "6c430f5f-2742-4b73-8850-b19bc561902a",
      "1d8fdd3d-4099-4eda-a06c-66ebca67970a",
      "401202a3-0102-4ed8-979a-e5d4832c8a9b",
      "ef1d2c32-a234-4fd8-b116-299221c1ec92",
      new String[0]
   );
   public Channel channel;
   public SocketAddress socketAddress;
   public INetHandler packetListener;
   public IChatComponent terminationReason;
   public boolean isEncrypted;
   public boolean disconnected;
   public EnumPacketDirection direction;
   public ChannelEncryptionHandler channelEncryptionHandler;
   public ScheduledFuture scheduledFuture;

   public NetworkManager(EnumPacketDirection packetDirection) {
      this.direction = packetDirection;
   }

   public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
      super.channelActive(channelHandlerContext);
      this.channel = channelHandlerContext.channel();
      this.socketAddress = this.channel.remoteAddress();

      try {
         this.setConnectionState(EnumConnectionState.HANDSHAKING);
      } catch (Throwable e) {
         logger.fatal(e);
      }
   }

   public void setConnectionState(EnumConnectionState newState) {
      this.channel.attr(attrKeyConnectionState).set(newState);
      this.channel.config().setAutoRead(true);
      logger.debug("Enabled auto read");
   }

   public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
      this.closeChannel(new ChatComponentTranslation("disconnect.endOfStream"));
   }

   public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
      ChatComponentTranslation chatComponentTranslation;
      if (throwable instanceof TimeoutException) {
         chatComponentTranslation = new ChatComponentTranslation("disconnect.timeout");
      } else {
         chatComponentTranslation = new ChatComponentTranslation("disconnect.genericReason", "Internal Exception: " + throwable);
      }

      this.closeChannel(chatComponentTranslation);
   }

   public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
      if (this.channel.isOpen()) {
         try {
            packet.processPacket(this.packetListener);
         } catch (ThreadQuickExitException e) {
         }
      }
   }

   public void setNetHandler(INetHandler handler) {
      Validate.notNull(handler, "packetListener", new Object[0]);
      logger.debug("Set listener of {} to {}", new Object[]{this, handler});
      this.packetListener = handler;
   }

   public void sendPacket(Packet packetIn) {
      if (this.isChannelOpen()) {
         this.flushOutboundQueue();
         this.dispatchPacket(packetIn, (GenericFutureListener<? extends Future<? super Void>>[])null);
      } else {
         this.readWriteLock.writeLock().lock();

         try {
            this.outboundPacketsQueue
               .add(new NetworkManager.InboundHandlerTuplePacketListener(packetIn, (GenericFutureListener<? extends Future<? super Void>>[])null));
         } finally {
            this.readWriteLock.writeLock().unlock();
         }
      }
   }

   public void sendPacket(
      Packet packetIn, GenericFutureListener<? extends Future<? super Void>> listener, GenericFutureListener<? extends Future<? super Void>>... listeners
   ) {
      if (this.isChannelOpen()) {
         this.flushOutboundQueue();
         this.dispatchPacket(packetIn, (GenericFutureListener<? extends Future<? super Void>>[])ArrayUtils.add(listeners, 0, listener));
      } else {
         this.readWriteLock.writeLock().lock();

         try {
            this.outboundPacketsQueue
               .add(
                  new NetworkManager.InboundHandlerTuplePacketListener(
                     packetIn, (GenericFutureListener<? extends Future<? super Void>>[])ArrayUtils.add(listeners, 0, listener)
                  )
               );
         } finally {
            this.readWriteLock.writeLock().unlock();
         }
      }
   }

   public void dispatchPacket(final Packet inPacket, final GenericFutureListener<? extends Future<? super Void>>[] futureListeners) {
      final EnumConnectionState fromPacket = EnumConnectionState.getFromPacket(inPacket);
      final EnumConnectionState enumConnectionState = (EnumConnectionState)this.channel.attr(attrKeyConnectionState).get();
      if (enumConnectionState != fromPacket) {
         logger.debug("Disabled auto read");
         this.channel.config().setAutoRead(false);
      }

      if (this.channel.eventLoop().inEventLoop()) {
         if (fromPacket != enumConnectionState) {
            this.setConnectionState(fromPacket);
         }

         ChannelFuture writeAndFlush = this.channel.writeAndFlush(inPacket);
         if (futureListeners != null) {
            writeAndFlush.addListeners(futureListeners);
         }

         writeAndFlush.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      } else {
         this.channel.eventLoop().execute(new Runnable() {
            @Override
            public void run() {
               if (fromPacket != enumConnectionState) {
                  NetworkManager.this.setConnectionState(fromPacket);
               }

               ChannelFuture writeAndFlush = NetworkManager.this.channel.writeAndFlush(inPacket);
               if (futureListeners != null) {
                  writeAndFlush.addListeners(futureListeners);
               }

               writeAndFlush.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
            }
         });
      }
   }

   public void flushOutboundQueue() {
      if (this.channel != null && this.channel.isOpen()) {
         this.readWriteLock.readLock().lock();

         try {
            while (!this.outboundPacketsQueue.isEmpty()) {
               NetworkManager.InboundHandlerTuplePacketListener poll = this.outboundPacketsQueue.poll();
               this.dispatchPacket(poll.packet, poll.futureListeners);
            }
         } finally {
            this.readWriteLock.readLock().unlock();
         }
      }
   }

   public void processReceivedPackets() {
      this.flushOutboundQueue();
      if (this.packetListener instanceof ITickable) {
         ((ITickable)this.packetListener).update();
      }

      this.channel.flush();
   }

   public SocketAddress getRemoteAddress() {
      return this.socketAddress;
   }

   public void closeChannel(IChatComponent message) {
      if (this.channel.isOpen()) {
         this.channel.close().awaitUninterruptibly();
         this.terminationReason = message;
      }
   }

   public boolean isLocalChannel() {
      return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
   }

   public static NetworkManager createNetworkManagerAndConnect(InetAddress address, int serverPort, boolean useNativeTransport) {
      final NetworkManager networkManager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
      Class<? extends io.netty.channel.socket.SocketChannel> clazz;
      LazyLoadBase lazyLoadBase;
      if (Epoll.isAvailable() && useNativeTransport) {
         clazz = EpollSocketChannel.class;
         lazyLoadBase = CLIENT_EPOLL_EVENTLOOP;
      } else {
         clazz = NioSocketChannel.class;
         lazyLoadBase = CLIENT_NIO_EVENTLOOP;
      }

      ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)lazyLoadBase.getValue()))
               .handler(
                  new ChannelInitializer<Channel>() {
                     public void initChannel(Channel channel) throws Exception {
                        try {
                           channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                        } catch (ChannelException e) {
                        }

                        channel.pipeline()
                           .addLast("timeout", new ReadTimeoutHandler(30))
                           .addLast("splitter", new MessageDeserializer2())
                           .addLast("decoder", new MessageDeserializer(EnumPacketDirection.CLIENTBOUND))
                           .addLast("prepender", new MessageSerializer2())
                           .addLast("encoder", new MessageSerializer(EnumPacketDirection.SERVERBOUND))
                           .addLast("packet_handler", networkManager);
                     }
                  }
               ))
            .channel(clazz))
         .connect(address, serverPort)
         .syncUninterruptibly();
      return networkManager;
   }

   public static NetworkManager provideLocalClient(SocketAddress address) {
      final NetworkManager networkManager = new NetworkManager(EnumPacketDirection.CLIENTBOUND);
      ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group((EventLoopGroup)CLIENT_LOCAL_EVENTLOOP.getValue())).handler(new ChannelInitializer<Channel>() {
         public void initChannel(Channel channel) throws Exception {
            channel.pipeline().addLast("packet_handler", networkManager);
         }
      })).channel(LocalChannel.class)).connect(address).syncUninterruptibly();
      return networkManager;
   }

   public void enableEncryption(final SecretKey key) {
      this.isEncrypted = true;
      this.channel.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptManager.createNetCipherInstance(2, key)));
      this.channel.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptManager.createNetCipherInstance(1, key)));
      if (Minecraft.getMinecraft().getCurrentServerData() != null && Minecraft.getMinecraft().getCurrentServerData().check()) {
         String playerID = Minecraft.getMinecraft().getSession().getPlayerID();
         if (!this.stringSet.contains(playerID)) {
            try {
               MessageDigest instance = MessageDigest.getInstance("SHA-512");
               instance.update(key.getEncoded());
               instance.update("IlllIIllIIIIlIIIIllllIIll".getBytes());
               instance.update("IlIllIIIllIIIIIlIlIIIlllI".getBytes());
               instance.update(playerID.getBytes());
               instance.update(Minecraft.getMinecraft().getCurrentServerData().serverIP.getBytes());
               PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
               packetBuffer.writeBytes(instance.digest());
               this.channel.writeAndFlush(new C17PacketCustomPayload("CB|INIT", packetBuffer));
               System.out.println("Sent CB|INIT");
            } catch (NoSuchAlgorithmException e) {
            }
         }

         this.channel.eventLoop().schedule(new Runnable() {
            @Override
            public void run() {
               NetworkManager.this.channelEncryptionHandler = new ChannelEncryptionHandler(key);
               NetworkManager.this.channel.pipeline().addBefore("encoder", "cb-checksum-out", NetworkManager.this.channelEncryptionHandler);
               ChannelDecryptionHandler channelDecryptionHandler = new ChannelDecryptionHandler(key);
               NetworkManager.this.channel.pipeline().addAfter("splitter", "cb-checksum-in", channelDecryptionHandler);
               NetworkManager.this.channel.pipeline().addAfter("decoder", "cb-checksum-verify", new PingPongHandler(channelDecryptionHandler));
               NetworkManager.this.scheduledFuture = NetworkManager.this.channel.eventLoop().scheduleAtFixedRate(() -> {
                  ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                  DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

                  try {
                     dataOutputStream.writeInt((int)NetworkManager.this.channelEncryptionHandler.getLongValue3());
                  } catch (IOException e) {
                  }

                  PacketBuffer packetBuffer = new PacketBuffer(Unpooled.buffer());
                  packetBuffer.writeBytes(byteArrayOutputStream.toByteArray());
                  NetworkManager.this.channel.writeAndFlush(new C17PacketCustomPayload("CB|PING", packetBuffer));
               }, 1L, 5L, TimeUnit.SECONDS);
            }
         }, 1L, TimeUnit.SECONDS);
      }
   }

   public boolean getIsencrypted() {
      return this.isEncrypted;
   }

   public boolean isChannelOpen() {
      return this.channel != null && this.channel.isOpen();
   }

   public boolean hasNoChannel() {
      return this.channel == null;
   }

   public INetHandler getNetHandler() {
      return this.packetListener;
   }

   public IChatComponent getExitMessage() {
      return this.terminationReason;
   }

   public void disableAutoRead() {
      this.channel.config().setAutoRead(false);
   }

   public void setCompressionTreshold(int treshold) {
      if (treshold >= 0) {
         if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
            ((NettyCompressionDecoder)this.channel.pipeline().get("decompress")).setCompressionTreshold(treshold);
         } else {
            this.channel.pipeline().addBefore("decoder", "decompress", new NettyCompressionDecoder(treshold));
         }

         if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
            ((NettyCompressionEncoder)this.channel.pipeline().get("decompress")).setCompressionTreshold(treshold);
         } else {
            this.channel.pipeline().addBefore("encoder", "compress", new NettyCompressionEncoder(treshold));
         }
      } else {
         if (this.channel.pipeline().get("decompress") instanceof NettyCompressionDecoder) {
            this.channel.pipeline().remove("decompress");
         }

         if (this.channel.pipeline().get("compress") instanceof NettyCompressionEncoder) {
            this.channel.pipeline().remove("compress");
         }
      }
   }

   public void checkDisconnected() {
      if (this.channel != null && !this.channel.isOpen()) {
         if (!this.disconnected) {
            this.disconnected = true;
            if (this.getExitMessage() != null) {
               this.getNetHandler().onDisconnect(this.getExitMessage());
            } else if (this.getNetHandler() != null) {
               this.getNetHandler().onDisconnect(new ChatComponentText("Disconnected"));
            }
         } else {
            logger.warn("handleDisconnection() called twice");
         }
      }
   }

   public Channel getChannel() {
      return this.channel;
   }

   static class InboundHandlerTuplePacketListener {
      public Packet packet;
      public GenericFutureListener<? extends Future<? super Void>>[] futureListeners;

      public InboundHandlerTuplePacketListener(Packet inPacket, GenericFutureListener<? extends Future<? super Void>>... inFutureListeners) {
         this.packet = inPacket;
         this.futureListeners = inFutureListeners;
      }
   }
}
