package com.cheatbreaker.client.voice;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.util.EnumChatFormatting;

public class VoiceChannel {
    public List<VoiceChatPlayer> voiceChatPlayerList = new ArrayList<>();
    public List<UUID> uuidList = new ArrayList<>();
    public UUID uuid;
    public String name;

    public VoiceChannel(UUID uuid, String str) {
        this.uuid = uuid;
        this.name = str;
    }

    public boolean removeIf(UUID uuid) {
        return this.uuidList.removeIf(uuid2 -> uuid2.equals(uuid));
    }

    public List<UUID> getUuidList() {
        return this.uuidList;
    }

    public String getName() {
        return this.name;
    }

    public List<VoiceChatPlayer> getVoiceChatPlayerList() {
        return this.voiceChatPlayerList;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public boolean removeIf2(UUID uuid) {
        return this.voiceChatPlayerList.removeIf(voiceChatPlayer -> voiceChatPlayer.getUuid().equals(uuid));
    }

    public boolean checkUUID(UUID uuid) {
        return this.voiceChatPlayerList.stream().anyMatch(voiceChatPlayer -> voiceChatPlayer.getUuid().equals(uuid));
    }

    public VoiceChatPlayer addPlayer(UUID uuid, String str) {
        VoiceChatPlayer voiceChatPlayer = new VoiceChatPlayer(uuid, EnumChatFormatting.getTextWithoutFormattingCodes(str));
        if (!this.checkUUID(uuid)) {
            this.voiceChatPlayerList.add(voiceChatPlayer);
        }

        return voiceChatPlayer;
    }

    public void handleUUID(UUID uuid, String str) {
        if (this.checkUUID(uuid)) {
            this.uuidList.add(uuid);
        }
    }

    public boolean checkUUID2(UUID uuid) {
        return this.uuidList.stream().anyMatch(uuid2 -> uuid2.equals(uuid));
    }
}
