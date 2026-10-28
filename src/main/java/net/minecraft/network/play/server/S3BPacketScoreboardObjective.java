package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;

public class S3BPacketScoreboardObjective extends Packet {
    public String name;
    public String displayName;
    public int mode;

    public S3BPacketScoreboardObjective() {
    }

    public S3BPacketScoreboardObjective(ScoreObjective scoreObjective, int n) {
        this.name = scoreObjective.getName();
        this.displayName = scoreObjective.getDisplayName();
        this.mode = n;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.name = data.readStringFromBuffer(16);
        this.displayName = data.readStringFromBuffer(32);
        this.mode = data.readByte();
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeStringToBuffer(this.name);
        data.writeStringToBuffer(this.displayName);
        data.writeByte(this.mode);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleScoreboardObjective(this);
    }

    public String getName() {
        return this.name;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public int getMode() {
        return this.mode;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
