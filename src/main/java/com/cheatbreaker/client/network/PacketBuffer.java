package com.cheatbreaker.client.network;

import com.google.common.base.Charsets;
import io.netty.buffer.ByteBuf;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class PacketBuffer {
    public ByteBuf byteBuf;

    public PacketBuffer(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }

    public void writeUuid(UUID uuid) {
        this.byteBuf.writeLong(uuid.getMostSignificantBits());
        this.byteBuf.writeLong(uuid.getLeastSignificantBits());
    }

    public byte[] toByteArray() {
        return this.byteBuf.array();
    }

    public String readString() {
        int readVarInt = this.readVarInt();
        byte[] bytes = new byte[readVarInt];
        this.byteBuf.readBytes(bytes);
        return new String(bytes, Charsets.UTF_8);
    }

    public <T> void writeOptional(T value, Consumer<T> consumer) {
        this.byteBuf.writeBoolean(value != null);
        if (value != null) {
            consumer.accept(value);
        }
    }

    public void writeVarInt(int n) {
        while ((n & -128) != 0) {
            this.byteBuf.writeByte(n & 127 | 128);
            n >>>= 7;
        }

        this.byteBuf.writeByte(n);
    }

    public int readVarInt() {
        int n = 0;
        int n2 = 0;

        byte b;
        do {
            b = this.byteBuf.readByte();
            n |= (b & 127) << n2++ * 7;
            if (n2 > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while ((b & 128) == 128);

        return n;
    }

    public ByteBuf getByteBuf() {
        return this.byteBuf;
    }

    public <T> T readOptional(Supplier<T> supplier) {
        boolean readBoolean = this.byteBuf.readBoolean();
        return (T)(readBoolean ? supplier.get() : null);
    }

    public UUID readUuid() {
        long readLong = this.byteBuf.readLong();
        long readLong2 = this.byteBuf.readLong();
        return new UUID(readLong, readLong2);
    }

    public void writeString(String str) {
        byte[] bytes = str.getBytes(Charsets.UTF_8);
        this.writeVarInt(bytes.length);
        this.byteBuf.writeBytes(bytes);
    }
}
