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
    public Map<VoiceChatPlayer, Object> voiceChatPlayerToObjectMap = new HashMap<>();
    public boolean pushToTalkActive;
    public Minecraft mc = Minecraft.getMinecraft();
    public CheatBreaker cheatBreaker = CheatBreaker.getInstance();

    public void handleUUID(UUID uuid) {
        VoiceChatPlayer voicePlayer = this.cheatBreaker.getNetHandler().getVoicePlayer(uuid);
        if (voicePlayer != null) {
            this.voiceChatPlayerToObjectMap.put(voicePlayer, System.currentTimeMillis() + 250L);
        }
    }

    public VoicePushToTalkHandler() {
        CheatBreaker.getInstance().getEventBus().checkClass2(ClientTickEvent.class, this::onClientTickEvent);
    }

    public boolean isKeyDown(int n) {
        return n != 0 && (n < 0 ? Mouse.isButtonDown(n + 100) : Keyboard.isKeyDown(n));
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

        if (this.pushToTalkActive && !this.mc.inGameHasFocus) {
            this.pushToTalkActive = false;

            VoiceEngine.getInstance().setTransmitting(false);

            CheatBreaker.getInstance().getAudioManager().playSound("voice_up");
        }

        if (!this.pushToTalkActive && this.mc.inGameHasFocus && this.isKeyDown(this.cheatBreaker.getKeybindSettings().voiceChat.getKeyCode())) {
            this.pushToTalkActive = true;

            VoiceEngine.getInstance().setTransmitting(true);

            CheatBreaker.getInstance().getAudioManager().playSound("voice_down");
        } else if (this.pushToTalkActive && this.mc.inGameHasFocus && !this.isKeyDown(this.cheatBreaker.getKeybindSettings().voiceChat.getKeyCode())) {
            this.pushToTalkActive = false;

            VoiceEngine.getInstance().setTransmitting(false);

            CheatBreaker.getInstance().getAudioManager().playSound("voice_up");
        }
    }

    public Map<VoiceChatPlayer, Object> unmodifiableMap() {
        return Collections.unmodifiableMap(this.voiceChatPlayerToObjectMap);
    }

    public boolean isPushToTalkActive() {
        return this.pushToTalkActive;
    }
}
