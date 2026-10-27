package com.cheatbreaker.client.voice;

import com.cheatbreaker.client.CheatBreaker;
import com.cheatbreaker.client.event.ClientTickEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

public class VoicePushToTalkHandler {
   public boolean flag;
   public Map<VoiceChatPlayer, Object> voiceChatPlayerToObjectMap = new HashMap<>();
   public Minecraft mc = Minecraft.getMinecraft();
   public CheatBreaker cheatBreaker = CheatBreaker.getInstance();

   public VoicePushToTalkHandler() {
      CheatBreaker.getInstance().getEventBus().checkClass2(ClientTickEvent.class, this::onClientTickEvent);
   }

   public void onClientTickEvent(ClientTickEvent clientTickEvent) {
      if (!this.voiceChatPlayerToObjectMap.isEmpty()) {
         ArrayList list = new ArrayList();

         for (Entry entry : this.voiceChatPlayerToObjectMap.entrySet()) {
            if (System.currentTimeMillis() - (Long)entry.getValue() >= 0L) {
               list.add(entry.getKey());
            }
         }

         list.forEach(obj -> {
            Object obj2 = this.voiceChatPlayerToObjectMap.remove(obj);
         });
      }

      if (this.flag && !this.mc.inGameHasFocus) {
         this.flag = false;

         VoiceEngine.getInstance().setTransmitting(false);

         CheatBreaker.getInstance().getAudioManager().handleString("voice_up");
      }

      if (!this.flag && this.mc.inGameHasFocus && this.check(this.cheatBreaker.getKeybindSettings().voiceChat.getKeyCode())) {
         this.flag = true;

         VoiceEngine.getInstance().setTransmitting(true);

         CheatBreaker.getInstance().getAudioManager().handleString("voice_down");
      } else if (this.flag && this.mc.inGameHasFocus && !this.check(this.cheatBreaker.getKeybindSettings().voiceChat.getKeyCode())) {
         this.flag = false;

         VoiceEngine.getInstance().setTransmitting(false);

         CheatBreaker.getInstance().getAudioManager().handleString("voice_up");
      }
   }

   public boolean check(int n) {
      return n != 0 && (n < 0 ? Mouse.isButtonDown(n + 100) : Keyboard.isKeyDown(n));
   }

   public boolean isFlag() {
      return this.flag;
   }

   public Map<VoiceChatPlayer, Object> unmodifiableMap() {
      return Collections.unmodifiableMap(this.voiceChatPlayerToObjectMap);
   }

   public void handleUUID(UUID uuid) {
      VoiceChatPlayer voicePlayer = this.cheatBreaker.getNetHandler().getVoicePlayer(uuid);
      if (voicePlayer != null) {
         this.voiceChatPlayerToObjectMap.put(voicePlayer, System.currentTimeMillis() + 250L);
      }
   }
}
