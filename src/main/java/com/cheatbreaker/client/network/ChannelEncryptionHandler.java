package com.cheatbreaker.client.network;

import com.cheatbreaker.client.util.SneakyThrow;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import javax.crypto.SecretKey;

public class ChannelEncryptionHandler extends ChannelOutboundHandlerAdapter {
    public long checksum;
    public byte[] byteArray = "ZB9hEJy5l+u8QARAlX9T0w".getBytes();
    public long adlerSum1 = 1L;
    public long adlerSum2 = 0L;

    public ChannelEncryptionHandler(SecretKey secretKey) {
        for (byte b : secretKey.getEncoded()) {
            this.adlerSum1 = (this.adlerSum1 + (b & 0xFF)) % 65521L;
            this.adlerSum2 = (this.adlerSum2 + this.adlerSum1) % 65521L;
        }
    }

    public long getChecksum() {
        return this.checksum;
    }

    public void write(ChannelHandlerContext channelHandlerContext, Object obj, ChannelPromise channelPromise) {
        try {
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

            this.checksum = this.adlerSum2 << 16 | this.adlerSum1;
            super.write(channelHandlerContext, obj, channelPromise);
        } catch (Throwable e) {
            throw SneakyThrow.sneakyThrow(e);
        }
    }
}
