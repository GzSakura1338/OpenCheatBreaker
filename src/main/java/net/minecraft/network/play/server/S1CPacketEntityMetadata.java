package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import net.minecraft.entity.DataWatcher;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S1CPacketEntityMetadata extends Packet {
    public int id;
    public List trackedValues;

    public S1CPacketEntityMetadata() {
    }

    public S1CPacketEntityMetadata(int n, DataWatcher dataWatcher, boolean flag) {
        this.id = n;
        if (flag) {
            this.trackedValues = dataWatcher.getAllWatched();
        } else {
            this.trackedValues = dataWatcher.getChanged();
        }
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.id = data.readInt();
        this.trackedValues = DataWatcher.readWatchedListFromPacketBuffer(data);
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeInt(this.id);
        DataWatcher.writeWatchedListToPacketBuffer(this.trackedValues, data);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleEntityMetadata(this);
    }

    public List getTrackedValues() {
        return this.trackedValues;
    }

    public int getId() {
        return this.id;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
