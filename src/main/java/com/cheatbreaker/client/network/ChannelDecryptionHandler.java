package com.cheatbreaker.client.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import javax.crypto.SecretKey;

public class ChannelDecryptionHandler extends ChannelInboundHandlerAdapter {
   public byte[] byteArray = "cf2O02b1QJSZOcVHphHucA".getBytes();
   public long longValue = 1L;
   public long longValue2;
   public long longValue3;
   public long longValue4 = 0L;

   public ChannelDecryptionHandler(SecretKey secretKey) {
      for (byte b : secretKey.getEncoded()) {
         this.longValue = (this.longValue + (b & 0xFF)) % 65521L;
         this.longValue4 = (this.longValue4 + this.longValue) % 65521L;
      }
   }

   public void channelRead(ChannelHandlerContext channelHandlerContext, Object obj) {
      ByteBuf byteBuf = (ByteBuf)obj;

      while (byteBuf.readableBytes() > 0) {
         int n = byteBuf.readByte() & 255;
         this.longValue = (this.longValue + n) % 65521L;
         this.longValue4 = (this.longValue4 + this.longValue) % 65521L;
      }

      byteBuf.readerIndex(0);

      for (byte b : this.byteArray) {
         this.longValue = (this.longValue + (b & 0xFF)) % 65521L;
         this.longValue4 = (this.longValue4 + this.longValue) % 65521L;
      }

      this.longValue3 = this.longValue2;
      this.longValue2 = this.longValue4 << 16 | this.longValue;

      try {
         super.channelRead(channelHandlerContext, obj);
      } catch (Exception e) {
         e.printStackTrace();
      }
   }

   public long getLongValue3() {
      return this.longValue3;
   }
}
