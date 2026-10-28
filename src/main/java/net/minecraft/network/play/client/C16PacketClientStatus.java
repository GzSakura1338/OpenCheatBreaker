package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C16PacketClientStatus extends Packet {
    public C16PacketClientStatus.EnumState status;

    public C16PacketClientStatus() {
    }

    public C16PacketClientStatus(C16PacketClientStatus.EnumState statusIn) {
        this.status = statusIn;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.status = C16PacketClientStatus.EnumState.enumStateArray[data.readByte() % C16PacketClientStatus.EnumState.enumStateArray.length];
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeByte(this.status.id);
    }

    public void processPacket(INetHandlerPlayServer handler) {
        handler.processClientStatus(this);
    }

    public C16PacketClientStatus.EnumState getStatus() {
        return this.status;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayServer)netHandler);
    }

    public enum EnumState {
        PERFORM_RESPAWN("PERFORM_RESPAWN", 0, 0),
        REQUEST_STATS("REQUEST_STATS", 1, 1),
        OPEN_INVENTORY_ACHIEVEMENT("OPEN_INVENTORY_ACHIEVEMENT", 2, 2);

        public int id;
        public static C16PacketClientStatus.EnumState[] enumStateArray = new C16PacketClientStatus.EnumState[values().length];
        public static C16PacketClientStatus.EnumState[] BY_ID = new C16PacketClientStatus.EnumState[]{PERFORM_RESPAWN, REQUEST_STATS, OPEN_INVENTORY_ACHIEVEMENT};

        static {
            for (C16PacketClientStatus.EnumState enumState : values()) {
                enumStateArray[enumState.id] = enumState;
            }
        }

        EnumState(String str, int n, int n2) {
            this.id = n2;
        }
    }
}
