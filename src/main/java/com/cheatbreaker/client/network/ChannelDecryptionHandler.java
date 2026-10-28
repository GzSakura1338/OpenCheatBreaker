package com.cheatbreaker.client.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javax.crypto.SecretKey;

public class ChannelDecryptionHandler extends ChannelInboundHandlerAdapter {
    public byte[] byteArray = "cf2O02b1QJSZOcVHphHucA".getBytes();
    public long adlerSum1 = 1L;
    public long previousChecksum;
    public long adlerSum2 = 0L;
    public long checksum;

    public ChannelDecryptionHandler(SecretKey secretKey) {
        for (byte b : secretKey.getEncoded()) {
            this.adlerSum1 = (this.adlerSum1 + (b & 0xFF)) % 65521L;
            this.adlerSum2 = (this.adlerSum2 + this.adlerSum1) % 65521L;
        }
    }

    public void channelRead(ChannelHandlerContext channelHandlerContext, Object obj) {
        ByteBuf byteBuf = (ByteBuf)obj;

        while (byteBuf.readableBytes() > 0) {
            int n = byteBuf.readByte() & 255;
            this.adlerSum1 = (this.adlerSum1 + n) % 65521L;
            this.adlerSum2 = (this.adlerSum2 + this.adlerSum1) % 65521L;
        }

        byteBuf.readerIndex(0);

        for (byte b : this.byteArray) {
            this.adlerSum1 = (this.adlerSum1 + (b & 0xFF)) % 65521L;
            this.adlerSum2 = (this.adlerSum2 + this.adlerSum1) % 65521L;
        }

        this.previousChecksum = this.checksum;
        this.checksum = this.adlerSum2 << 16 | this.adlerSum1;

        try {
            super.channelRead(channelHandlerContext, obj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getPreviousChecksum() {
        return this.previousChecksum;
    }
}
