package net.minecraft.network;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.network.ChannelDecryptionHandler;
import com.cheatbreaker.client.network.ChannelEncryptionHandler;
import com.cheatbreaker.client.network.PingPongHandler;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
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
import javax.crypto.SecretKey;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.CryptManager;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.MessageDeserializer;
import net.minecraft.util.MessageDeserializer2;
import net.minecraft.util.MessageSerializer;
import net.minecraft.util.MessageSerializer2;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class NetworkManager extends SimpleChannelInboundHandler {
    public static Marker logMarkerNetwork = MarkerManager.getMarker("NETWORK");
    public static Marker logMarkerPackets = MarkerManager.getMarker("NETWORK_PACKETS", logMarkerNetwork);
    public static Marker logMarkerStat = MarkerManager.getMarker("NETWORK_STAT", logMarkerNetwork);
    public static AttributeKey attrKeyConnectionState = AttributeKey.valueOf("protocol");
    public static AttributeKey attrKeyReceivable = AttributeKey.valueOf("receivable_packets");
    public static AttributeKey attrKeySendable = AttributeKey.valueOf("sendable_packets");
    public static NioEventLoopGroup eventLoops = new NioEventLoopGroup(
        0, new ThreadFactoryBuilder().setNameFormat("Netty Client IO #%d").setDaemon(true).build()
    );
    public static NetworkStatistics STATISTICS = new NetworkStatistics();
    public static Logger logger = LogManager.getLogger();
    public Queue receivedPacketsQueue = Queues.newConcurrentLinkedQueue();
    public Queue outboundPacketsQueue = Queues.newConcurrentLinkedQueue();
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
    public EnumConnectionState connectionState;
    public IChatComponent terminationReason;
    public boolean isEncrypted;
    public boolean isClientSide;
    public ChannelEncryptionHandler channelEncryptionHandler;
    public ScheduledFuture scheduledFuture;

    public NetworkManager(boolean isClient) {
        this.isClientSide = isClient;
    }

    public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
        super.channelActive(channelHandlerContext);
        this.channel = channelHandlerContext.channel();
        this.socketAddress = this.channel.remoteAddress();
        this.setConnectionState(EnumConnectionState.HANDSHAKING);
    }

    public void setConnectionState(EnumConnectionState newState) {
        this.connectionState = (EnumConnectionState)this.channel.attr(attrKeyConnectionState).getAndSet(newState);
        this.channel.attr(attrKeyReceivable).set(newState.getReceivablePackets(this.isClientSide));
        this.channel.attr(attrKeySendable).set(newState.getSendablePackets(this.isClientSide));
        this.channel.config().setAutoRead(true);
        logger.debug("Enabled auto read");
    }

    public void channelInactive(ChannelHandlerContext channelHandlerContext) {
        this.closeChannel(new ChatComponentTranslation("disconnect.endOfStream"));
    }

    public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {
        ChatComponentTranslation chatComponentTranslation;
        if (throwable instanceof TimeoutException) {
            chatComponentTranslation = new ChatComponentTranslation("disconnect.timeout");
        } else {
            chatComponentTranslation = new ChatComponentTranslation("disconnect.genericReason", "Internal Exception: " + throwable);
        }

        this.closeChannel(chatComponentTranslation);
    }

    public void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws IOException {
        if (this.channel.isOpen()) {
            if (packet.hasPriority()) {
                packet.processPacket(this.packetListener);
            } else {
                this.receivedPacketsQueue.add(packet);
            }
        }
    }

    public void setNetHandler(INetHandler handler) {
        Validate.notNull(handler, "packetListener", new Object[0]);
        logger.debug("Set listener of {} to {}", this, handler);
        this.packetListener = handler;
    }

    public void scheduleOutboundPacket(Packet inPacket, GenericFutureListener... futureListeners) {
        if (this.channel != null && this.channel.isOpen()) {
            this.flushOutboundQueue();
            this.dispatchPacket(inPacket, futureListeners);
        } else {
            this.outboundPacketsQueue.add(new NetworkManager.InboundHandlerTuplePacketListener(inPacket, futureListeners));
        }
    }

    public void dispatchPacket(final Packet inPacket, final GenericFutureListener[] futureListeners) {
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

            this.channel.writeAndFlush(inPacket).addListeners(futureListeners).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
        } else {
            this.channel.eventLoop().execute(new Runnable() {
                @Override
                public void run() {
                    if (fromPacket != enumConnectionState) {
                        NetworkManager.this.setConnectionState(fromPacket);
                    }

                    NetworkManager.this.channel.writeAndFlush(inPacket).addListeners(futureListeners).addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
                }
            });
        }
    }

    public void flushOutboundQueue() {
        if (this.channel != null && this.channel.isOpen()) {
            while (!this.outboundPacketsQueue.isEmpty()) {
                NetworkManager.InboundHandlerTuplePacketListener poll = (NetworkManager.InboundHandlerTuplePacketListener)this.outboundPacketsQueue.poll();
                this.dispatchPacket(poll.packet, poll.futureListeners);
            }
        }
    }

    public void processReceivedPackets() throws IOException {
        this.flushOutboundQueue();
        if (this.channel != null) {
            EnumConnectionState enumConnectionState = (EnumConnectionState)this.channel.attr(attrKeyConnectionState).get();
            if (this.connectionState != enumConnectionState) {
                if (this.connectionState != null) {
                    this.packetListener.onConnectionStateTransition(this.connectionState, enumConnectionState);
                }

                this.connectionState = enumConnectionState;
            }

            if (this.packetListener != null) {
                for (int i = 1000; !this.receivedPacketsQueue.isEmpty() && i >= 0; i--) {
                    Packet poll = (Packet)this.receivedPacketsQueue.poll();
                    poll.processPacket(this.packetListener);
                }

                this.packetListener.onNetworkTick();
            }

            this.channel.flush();
        }
    }

    public SocketAddress getRemoteAddress() {
        return this.socketAddress;
    }

    public void closeChannel(IChatComponent message) {
        if (this.channel.isOpen()) {
            this.channel.close();
            this.terminationReason = message;
        }
    }

    public boolean isLocalChannel() {
        return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
    }

    public static NetworkManager provideLanClient(InetAddress inetAddress, int n) {
        final NetworkManager networkManager = new NetworkManager(true);
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(eventLoops))
                    .handler(
                        new ChannelInitializer() {
                            public void initChannel(Channel channel) {
                                try {
                                    channel.config().setOption(ChannelOption.TCP_NODELAY, true);
                                } catch (ChannelException e) {
                                }

                                channel.pipeline()
                                    .addLast("timeout", new ReadTimeoutHandler(20))
                                    .addLast("splitter", new MessageDeserializer2())
                                    .addLast("decoder", new MessageDeserializer(NetworkManager.STATISTICS))
                                    .addLast("prepender", new MessageSerializer2())
                                    .addLast("encoder", new MessageSerializer(NetworkManager.STATISTICS))
                                    .addLast("packet_handler", networkManager);
                            }
                        }
                    ))
                .channel(NioSocketChannel.class))
            .connect(inetAddress, n)
            .syncUninterruptibly();
        return networkManager;
    }

    public static NetworkManager provideLocalClient(SocketAddress socketAddress) {
        final NetworkManager networkManager = new NetworkManager(true);
        ((Bootstrap)((Bootstrap)((Bootstrap)new Bootstrap().group(eventLoops)).handler(new ChannelInitializer() {
            public void initChannel(Channel channel) {
                channel.pipeline().addLast("packet_handler", networkManager);
            }
        })).channel(LocalChannel.class)).connect(socketAddress).syncUninterruptibly();
        return networkManager;
    }

    public void enableEncryption(final SecretKey key) {
        this.channel.pipeline().addBefore("splitter", "decrypt", new NettyEncryptingDecoder(CryptManager.cipherFromKey(2, key)));
        this.channel.pipeline().addBefore("prepender", "encrypt", new NettyEncryptingEncoder(CryptManager.cipherFromKey(1, key)));
        if (Minecraft.getMinecraft().currentServerData != null && Minecraft.getMinecraft().currentServerData.check()) {
            String playerID = Minecraft.getMinecraft().getSession().getPlayerID();
            if (!this.stringSet.contains(playerID)) {
                try {
                    MessageDigest instance = MessageDigest.getInstance("SHA-512");
                    instance.update(key.getEncoded());
                    instance.update("IlIIIllIIlIlIlIIIIllIlIll".getBytes());
                    instance.update("IlIlIIIlIIlIIIIlIIIIIIIII".getBytes());
                    instance.update(playerID.getBytes());
                    instance.update(Minecraft.getMinecraft().currentServerData.serverIP.getBytes());
                    this.channel.writeAndFlush(new C17PacketCustomPayload("CB|INIT", instance.digest()));
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
                            dataOutputStream.writeInt((int)NetworkManager.this.channelEncryptionHandler.getChecksum());
                        } catch (IOException e) {
                        }

                        NetworkManager.this.channel.writeAndFlush(new C17PacketCustomPayload("CB|PING", byteArrayOutputStream.toByteArray()));
                    }, 1L, 5L, TimeUnit.SECONDS);
                }
            }, 1L, TimeUnit.SECONDS);
        }

        this.isEncrypted = true;
    }

    public boolean isChannelOpen() {
        return this.channel != null && this.channel.isOpen();
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

    public void channelRead0(ChannelHandlerContext channelHandlerContext, Object obj) throws IOException {
        this.channelRead0(channelHandlerContext, (Packet)obj);
    }

    public Channel getChannel() {
        return this.channel;
    }

    public static class InboundHandlerTuplePacketListener {
        public Packet packet;
        public GenericFutureListener[] futureListeners;

        public InboundHandlerTuplePacketListener(Packet inPacket, GenericFutureListener... inFutureListeners) {
            this.packet = inPacket;
            this.futureListeners = inFutureListeners;
        }
    }
}
