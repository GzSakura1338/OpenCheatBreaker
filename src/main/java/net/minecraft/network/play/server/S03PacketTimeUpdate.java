package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S03PacketTimeUpdate extends Packet {
    public long time;
    public long timeOfDay;

    public S03PacketTimeUpdate() {
    }

    public S03PacketTimeUpdate(long l, long l2, boolean flag) {
        this.time = l;
        this.timeOfDay = l2;
        if (!flag) {
            this.timeOfDay = -this.timeOfDay;
            if (this.timeOfDay == 0L) {
                this.timeOfDay = -1L;
            }
        }
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.time = data.readLong();
        this.timeOfDay = data.readLong();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeLong(this.time);
        data.writeLong(this.timeOfDay);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleTimeUpdate(this);
    }

    @Override
    public String serialize() {
        return String.format("time=%d,dtime=%d", this.time, this.timeOfDay);
    }

    public long getTime() {
        return this.time;
    }

    public long getTimeOfDay() {
        return this.timeOfDay;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
