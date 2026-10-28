package com.cheatbreaker.client.event;

public class PlayerSkinLookupEvent extends CancellableEvent {
    public String playerName;

    public String getPlayerName() {
        return this.playerName;
    }

    public PlayerSkinLookupEvent(String str) {
        this.playerName = str;
    }
}
