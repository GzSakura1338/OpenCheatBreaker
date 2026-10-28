package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S13PacketDestroyEntities extends Packet {
    public int[] entityIds;

    public S13PacketDestroyEntities() {
    }

    public S13PacketDestroyEntities(int... ints) {
        this.entityIds = ints;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.entityIds = new int[data.readByte()];

        for (int i = 0; i < this.entityIds.length; i++) {
            this.entityIds[i] = data.readInt();
        }
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeByte(this.entityIds.length);

        for (int i = 0; i < this.entityIds.length; i++) {
            data.writeInt(this.entityIds[i]);
        }
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleDestroyEntities(this);
    }

    @Override
    public String serialize() {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < this.entityIds.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }

            sb.append(this.entityIds[i]);
        }

        return String.format("entities=%d[%s]", this.entityIds.length, sb);
    }

    public int[] getEntityIds() {
        return this.entityIds;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
