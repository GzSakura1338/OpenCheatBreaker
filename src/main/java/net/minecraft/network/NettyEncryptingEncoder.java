package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class NettyEncryptingEncoder extends MessageToByteEncoder {
    public NettyEncryptionTranslator encryptionCodec;

    public NettyEncryptingEncoder(Cipher cipher) {
        this.encryptionCodec = new NettyEncryptionTranslator(cipher);
    }

    public void encode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, ByteBuf byteBuf2) throws ShortBufferException {
        this.encryptionCodec.cipher(byteBuf, byteBuf2);
    }

    public void encode(ChannelHandlerContext channelHandlerContext, Object obj, ByteBuf byteBuf) throws ShortBufferException {
        this.encode(channelHandlerContext, (ByteBuf)obj, byteBuf);
    }
}
