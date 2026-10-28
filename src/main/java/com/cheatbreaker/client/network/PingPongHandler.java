package com.cheatbreaker.client.network;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C17PacketCustomPayload;
import net.minecraft.network.play.server.S3FPacketCustomPayload;

public class PingPongHandler extends ChannelInboundHandlerAdapter {
    public Minecraft mc = Minecraft.getMinecraft();
    public long lastPongNanos = System.nanoTime() - 30000000000L;
    public long counter = 0L;
    public ChannelDecryptionHandler channelDecryptionHandler;

    public PingPongHandler(ChannelDecryptionHandler channelDecryptionHandler) {
        this.channelDecryptionHandler = channelDecryptionHandler;
    }

    public void channelRead(ChannelHandlerContext channelHandlerContext, Object obj) throws IOException, Exception {
        if (obj instanceof S3FPacketCustomPayload) {
            S3FPacketCustomPayload s3FPacketCustomPayload = (S3FPacketCustomPayload)obj;
            if (s3FPacketCustomPayload.getChannel().equals("CB|PING")) {
                this.lastPongNanos = System.nanoTime();
                long l = new DataInputStream(new ByteArrayInputStream(s3FPacketCustomPayload.getPayload())).readInt() & 4294967295L;
                if (l != this.channelDecryptionHandler.getPreviousChecksum()) {
                    throw new IOException("CheatBreaker Protocol Error -2a\n(Try updating your client)");
                }

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);

                try {
                    dataOutputStream.write(s3FPacketCustomPayload.getPayload());
                    if ((this.counter++ & 7L) == 0L) {
                        dataOutputStream.writeLong(this.counter);
                        dataOutputStream.writeUTF(this.mc.getCurrentServerData().serverIP);
                        dataOutputStream.writeUTF(this.mc.getSession().getPlayerID());
                        dataOutputStream.writeUTF("IlIIIllIIlIlIlIIIIllIlIll");
                    }
                } catch (IOException e) {
                }

                channelHandlerContext.channel().eventLoop().execute(() -> channelHandlerContext.channel().writeAndFlush(new C17PacketCustomPayload("CB|PONG", byteArrayOutputStream.toByteArray())));
            }

            if (System.nanoTime() - this.lastPongNanos > 45000000000L) {
                throw new IOException("CheatBreaker Protocol Error -2b\n(Try updating your client)");
            }

            super.channelRead(channelHandlerContext, obj);
        }
    }
}
