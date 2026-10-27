package com.cheatbreaker.client.voice;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import net.minecraft.util.EnumChatFormatting;

public class VoiceChannel {
   public List<VoiceChatPlayer> voiceChatPlayerList = new ArrayList<>();
   public List<UUID> uuidList = new ArrayList<>();
   public UUID uuid;
   public String text;

   public String getText() {
      return this.text;
   }

   public boolean checkUUID(UUID uuid) {
      return this.voiceChatPlayerList.stream().anyMatch(voiceChatPlayer -> voiceChatPlayer.getUuid().equals(uuid));
   }

   public boolean removeIf(UUID uuid) {
      return this.voiceChatPlayerList.removeIf(voiceChatPlayer -> voiceChatPlayer.getUuid().equals(uuid));
   }

   public void handleUUID(UUID uuid, String str) {
      if (this.checkUUID(uuid)) {
         this.uuidList.add(uuid);
      }
   }

   public VoiceChatPlayer createVoiceChatPlayer(UUID uuid, String str) {
      VoiceChatPlayer voiceChatPlayer = new VoiceChatPlayer(uuid, EnumChatFormatting.getTextWithoutFormattingCodes(str));
      if (!this.checkUUID(uuid)) {
         this.voiceChatPlayerList.add(voiceChatPlayer);
      }

      return voiceChatPlayer;
   }

   public UUID getUuid() {
      return this.uuid;
   }

   public VoiceChannel(UUID uuid, String str) {
      this.uuid = uuid;
      this.text = str;
   }

   public boolean checkUUID2(UUID uuid) {
      return this.uuidList.stream().anyMatch(uuid2 -> uuid2.equals(uuid));
   }

   public boolean removeIf2(UUID uuid) {
      return this.uuidList.removeIf(uuid2 -> uuid2.equals(uuid));
   }

   public List<VoiceChatPlayer> getVoiceChatPlayerList() {
      return this.voiceChatPlayerList;
   }

   public List<UUID> getUuidList() {
      return this.uuidList;
   }
}
