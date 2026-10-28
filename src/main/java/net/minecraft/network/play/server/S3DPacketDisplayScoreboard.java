package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;

public class S3DPacketDisplayScoreboard extends Packet {
    public int slot;
    public String name;

    public S3DPacketDisplayScoreboard() {
    }

    public S3DPacketDisplayScoreboard(int n, ScoreObjective scoreObjective) {
        this.slot = n;
        if (scoreObjective == null) {
            this.name = "";
        } else {
            this.name = scoreObjective.getName();
        }
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.slot = data.readByte();
        this.name = data.readStringFromBuffer(16);
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeByte(this.slot);
        data.writeStringToBuffer(this.name);
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleDisplayScoreboard(this);
    }

    public int getSlot() {
        return this.slot;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
