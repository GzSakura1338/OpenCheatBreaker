package net.minecraft.network.play.server;

import com.google.common.collect.Maps;
import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

public class S37PacketStatistics extends Packet {
    public Map stats;

    public S37PacketStatistics() {
    }

    public S37PacketStatistics(Map map) {
        this.stats = map;
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleStatistics(this);
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        int readVarIntFromBuffer = data.readVarIntFromBuffer();
        this.stats = Maps.newHashMap();

        for (int i = 0; i < readVarIntFromBuffer; i++) {
            StatBase oneShotStat = StatList.getOneShotStat(data.readStringFromBuffer(32767));
            int readVarIntFromBuffer2 = data.readVarIntFromBuffer();
            if (oneShotStat != null) {
                this.stats.put(oneShotStat, readVarIntFromBuffer2);
            }
        }
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeVarIntToBuffer(this.stats.size());

        for (Entry entry : (Iterable<Entry>)this.stats.entrySet()) {
            data.writeStringToBuffer(((StatBase)entry.getKey()).statId);
            data.writeVarIntToBuffer((Integer)entry.getValue());
        }
    }

    @Override
    public String serialize() {
        return String.format("count=%d", this.stats.size());
    }

    public Map getStatMap() {
        return this.stats;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
