package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class NettyEncryptionTranslator {
    public byte[] conversionBuffer = new byte[0];
    public byte[] encryptionBuffer = new byte[0];
    public Cipher cipher;

    public NettyEncryptionTranslator(Cipher cipherIn) {
        this.cipher = cipherIn;
    }

    public byte[] toByteArray(ByteBuf byteBuf) {
        int readableBytes = byteBuf.readableBytes();
        if (this.conversionBuffer.length < readableBytes) {
            this.conversionBuffer = new byte[readableBytes];
        }

        byteBuf.readBytes(this.conversionBuffer, 0, readableBytes);
        return this.conversionBuffer;
    }

    public ByteBuf decipher(ChannelHandlerContext ctx, ByteBuf buffer) throws ShortBufferException {
        int readableBytes = buffer.readableBytes();
        byte[] toByteArray = this.toByteArray(buffer);
        ByteBuf heapBuffer = ctx.alloc().heapBuffer(this.cipher.getOutputSize(readableBytes));
        heapBuffer.writerIndex(this.cipher.update(toByteArray, 0, readableBytes, heapBuffer.array(), heapBuffer.arrayOffset()));
        return heapBuffer;
    }

    public void cipher(ByteBuf byteBuf, ByteBuf byteBuf2) throws ShortBufferException {
        int readableBytes = byteBuf.readableBytes();
        byte[] toByteArray = this.toByteArray(byteBuf);
        int outputSize = this.cipher.getOutputSize(readableBytes);
        if (this.encryptionBuffer.length < outputSize) {
            this.encryptionBuffer = new byte[outputSize];
        }

        byteBuf2.writeBytes(this.encryptionBuffer, 0, this.cipher.update(toByteArray, 0, readableBytes, this.encryptionBuffer));
    }
}
