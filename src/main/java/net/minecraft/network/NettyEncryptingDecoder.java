package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class NettyEncryptingDecoder extends MessageToMessageDecoder<ByteBuf> {
   public NettyEncryptionTranslator decryptionCodec;

   public NettyEncryptingDecoder(Cipher cipher) {
      this.decryptionCodec = new NettyEncryptionTranslator(cipher);
   }

   public void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception, ShortBufferException {
      list.add(this.decryptionCodec.decipher(channelHandlerContext, byteBuf));
   }
}
