package com.cheatbreaker.client.network.packet;

import com.cheatbreaker.client.network.AbstractPacket;
import com.cheatbreaker.client.network.PacketBuffer;
import com.cheatbreaker.client.network.PacketHandler;
import com.cheatbreaker.client.network.WebsocketPacketHandler;
import java.util.UUID;

public class PlayerStopEmotePacket extends AbstractPacket {
    public UUID playerId;

    @Override
    public void read(PacketBuffer packetBuffer) {
        this.playerId = packetBuffer.readUuid();
    }

    public PlayerStopEmotePacket() {
    }

    public UUID getPlayerId() {
        return this.playerId;
    }

    public PlayerStopEmotePacket(UUID uuid) {
        this.playerId = uuid;
    }

    @Override
    public void write(PacketBuffer packetBuffer) {
        packetBuffer.writeUuid(this.playerId);
    }

    @Override
    public void handle(PacketHandler packetHandler) {
        ((WebsocketPacketHandler)packetHandler).handlePlayerStopEmote(this);
    }
}
