package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.Score;

public class S3CPacketUpdateScore extends Packet {
    public String playerName = "";
    public String objectiveName = "";
    public int score;
    public int intValue;

    public S3CPacketUpdateScore() {
    }

    public S3CPacketUpdateScore(Score score, int n) {
        this.playerName = score.getPlayerName();
        this.objectiveName = score.getObjective().getName();
        this.score = score.getScorePoints();
        this.intValue = n;
    }

    public S3CPacketUpdateScore(String str) {
        this.playerName = str;
        this.objectiveName = "";
        this.score = 0;
        this.intValue = 1;
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.playerName = data.readStringFromBuffer(16);
        this.intValue = data.readByte();
        if (this.intValue != 1) {
            this.objectiveName = data.readStringFromBuffer(16);
            this.score = data.readInt();
        }
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeStringToBuffer(this.playerName);
        data.writeByte(this.intValue);
        if (this.intValue != 1) {
            data.writeStringToBuffer(this.objectiveName);
            data.writeInt(this.score);
        }
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleUpdateScore(this);
    }

    public String getPlayerName() {
        return this.playerName;
    }

    public String getObjectiveName() {
        return this.objectiveName;
    }

    public int getScore() {
        return this.score;
    }

    public int getIntValue() {
        return this.intValue;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
