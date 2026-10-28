package net.minecraft.network;

import com.google.common.collect.BiMap;
import io.netty.buffer.ByteBuf;
import java.io.IOException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Packet {
    public static Logger logger = LogManager.getLogger();

    public static Packet generatePacket(BiMap protocolMap, int packetId) {
        try {
            Class clazz = (Class)protocolMap.get(packetId);
            return clazz == null ? null : (Packet)clazz.newInstance();
        } catch (Exception e) {
            logger.error("Couldn't create packet " + packetId, e);
            return null;
        }
    }

    public static void writeBlob(ByteBuf buffer, byte[] blob) {
        buffer.writeShort(blob.length);
        buffer.writeBytes(blob);
    }

    public static byte[] readBlob(ByteBuf buffer) throws IOException {
        short readShort = buffer.readShort();
        if (readShort < 0) {
            throw new IOException("Key was smaller than nothing!  Weird key!");
        }

        byte[] bytes = new byte[readShort];
        buffer.readBytes(bytes);
        return bytes;
    }

    public abstract void readPacketData(PacketBuffer packetBuffer) throws IOException;

    public abstract void writePacketData(PacketBuffer packetBuffer) throws IOException;

    public abstract void processPacket(INetHandler netHandler) throws IOException;

    public boolean hasPriority() {
        return false;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }

    public String serialize() {
        return "";
    }
}
