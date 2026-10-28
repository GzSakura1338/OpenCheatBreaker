package com.cheatbreaker.client.voice;

import java.util.UUID;

public class VoiceChatPlayer {
    public UUID uuid;
    public String name;

    public String getName() {
        return this.name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public VoiceChatPlayer(UUID uuid, String str) {
        this.uuid = uuid;
        this.name = str;
    }
}
