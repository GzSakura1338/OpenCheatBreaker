package com.cheatbreaker.client.network;

import com.cheatbreaker.client.util.SneakyThrow;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import javax.crypto.SecretKey;

public class ChannelEncryptionHandler extends ChannelOutboundHandlerAdapter {
   public byte[] byteArray = "ZB9hEJy5l+u8QARAlX9T0w".getBytes();
   public long longValue = 1L;
   public long longValue2 = 0L;
   public long longValue3;

   public ChannelEncryptionHandler(SecretKey secretKey) {
      for (byte b : secretKey.getEncoded()) {
         this.longValue = (this.longValue + (b & 0xFF)) % 65521L;
         this.longValue2 = (this.longValue2 + this.longValue) % 65521L;
      }
   }

   public void write(ChannelHandlerContext channelHandlerContext, Object obj, ChannelPromise channelPromise) {
      try {
         ByteBuf byteBuf = (ByteBuf)obj;

         while (byteBuf.readableBytes() > 0) {
            int n = byteBuf.readByte() & 255;
            this.longValue = (this.longValue + n) % 65521L;
            this.longValue2 = (this.longValue2 + this.longValue) % 65521L;
         }

         byteBuf.readerIndex(0);

         for (byte b : this.byteArray) {
            this.longValue = (this.longValue + (b & 0xFF)) % 65521L;
            this.longValue2 = (this.longValue2 + this.longValue) % 65521L;
         }

         this.longValue3 = this.longValue2 << 16 | this.longValue;
         super.write(channelHandlerContext, obj, channelPromise);
      } catch (Throwable e) {
         throw SneakyThrow.sneakyThrow(e);
      }
   }

   public long getLongValue3() {
      return this.longValue3;
   }
}
