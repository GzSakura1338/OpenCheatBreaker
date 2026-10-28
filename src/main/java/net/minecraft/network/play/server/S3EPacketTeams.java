package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScorePlayerTeam;

public class S3EPacketTeams extends Packet {
    public String teamName = "";
    public String displayName = "";
    public String playerPrefix = "";
    public String nameTagVisibilityRule = "";
    public Collection playerList = new ArrayList();
    public int mode;
    public int flags;

    public S3EPacketTeams() {
    }

    public S3EPacketTeams(ScorePlayerTeam scorePlayerTeam, int n) {
        this.teamName = scorePlayerTeam.getRegisteredName();
        this.mode = n;
        if (n == 0 || n == 2) {
            this.displayName = scorePlayerTeam.getDisplayName();
            this.playerPrefix = scorePlayerTeam.getColorPrefix();
            this.nameTagVisibilityRule = scorePlayerTeam.getColorSuffix();
            this.flags = scorePlayerTeam.getFriendlyFlagsBitwise();
        }

        if (n == 0) {
            this.playerList.addAll(scorePlayerTeam.getMembershipCollection());
        }
    }

    public S3EPacketTeams(ScorePlayerTeam scorePlayerTeam, Collection collection, int n) {
        if (n != 3 && n != 4) {
            throw new IllegalArgumentException("Method must be join or leave for player constructor");
        }

        if (collection != null && !collection.isEmpty()) {
            this.mode = n;
            this.teamName = scorePlayerTeam.getRegisteredName();
            this.playerList.addAll(collection);
        } else {
            throw new IllegalArgumentException("Players cannot be null/empty");
        }
    }

    @Override
    public void readPacketData(PacketBuffer data) throws IOException {
        this.teamName = data.readStringFromBuffer(16);
        this.mode = data.readByte();
        if (this.mode == 0 || this.mode == 2) {
            this.displayName = data.readStringFromBuffer(32);
            this.playerPrefix = data.readStringFromBuffer(16);
            this.nameTagVisibilityRule = data.readStringFromBuffer(16);
            this.flags = data.readByte();
        }

        if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
            short readShort = data.readShort();

            for (int i = 0; i < readShort; i++) {
                this.playerList.add(data.readStringFromBuffer(40));
            }
        }
    }

    @Override
    public void writePacketData(PacketBuffer data) throws IOException {
        data.writeStringToBuffer(this.teamName);
        data.writeByte(this.mode);
        if (this.mode == 0 || this.mode == 2) {
            data.writeStringToBuffer(this.displayName);
            data.writeStringToBuffer(this.playerPrefix);
            data.writeStringToBuffer(this.nameTagVisibilityRule);
            data.writeByte(this.flags);
        }

        if (this.mode == 0 || this.mode == 3 || this.mode == 4) {
            data.writeShort(this.playerList.size());

            for (String str : (Iterable<String>)this.playerList) {
                data.writeStringToBuffer(str);
            }
        }
    }

    public void processPacket(INetHandlerPlayClient handler) {
        handler.handleTeams(this);
    }

    public String getTeamName() {
        return this.teamName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public String getPlayerPrefix() {
        return this.playerPrefix;
    }

    public String getNameTagVisibilityRule() {
        return this.nameTagVisibilityRule;
    }

    public Collection getPlayerList() {
        return this.playerList;
    }

    public int getMode() {
        return this.mode;
    }

    public int getFlags() {
        return this.flags;
    }

    @Override
    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient)netHandler);
    }
}
