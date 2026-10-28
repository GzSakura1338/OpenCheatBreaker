package com.cheatbreaker.client.event;

import net.minecraft.network.play.server.S01PacketJoinGame;

public class S01PacketJoinGameEvent extends Event {
    public S01PacketJoinGame s01PacketJoinGame;

    public S01PacketJoinGameEvent(S01PacketJoinGame s01PacketJoinGame) {
        this.s01PacketJoinGame = s01PacketJoinGame;
    }

    public S01PacketJoinGame getS01PacketJoinGame() {
        return this.s01PacketJoinGame;
    }
}
