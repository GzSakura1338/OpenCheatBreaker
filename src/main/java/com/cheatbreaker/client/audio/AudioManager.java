package com.cheatbreaker.client.audio;

import com.cheatbreaker.client.CheatBreaker;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Line;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.TargetDataLine;
import net.minecraft.client.Minecraft;

public class AudioManager {
    public List<AudioDevice> audioDeviceList = new ArrayList<>();

    public String[] getStringArray() {
        return this.audioDeviceList.stream().map(audioDevice -> audioDevice.getName().replace("Primary Sound Capture Driver", "Default")).toArray(String[]::new);
    }

    public void playSound(String str) {
        this.playSound(str, 1.0F);
    }

    public void playSound(String str, float f) {
        if (!(Boolean)CheatBreaker.getInstance().getKeybindSettings().muteCheatbreakerSounds.getValue()) {
            Minecraft.getMinecraft().getSoundHandler().sndManager.playClientSound(str, f);
        }
    }

    public String getDeviceName(String str) {
        Iterator iterator = this.audioDeviceList.iterator();
        if (!iterator.hasNext()) {
            return str;
        }

        AudioDevice audioDevice;
        for (audioDevice = (AudioDevice)iterator.next(); !audioDevice.getName().equals(str); audioDevice = (AudioDevice)iterator.next()) {
            if (!iterator.hasNext()) {
                return str;
            }
        }

        return audioDevice.getDescription();
    }

    public void refreshInputDevices() {
        Mixer.Info[] mixerInfo = AudioSystem.getMixerInfo();

        for (Mixer.Info info : mixerInfo) {
            if (info != null) {
                Line.Info[] targetLineInfo = AudioSystem.getMixer(info).getTargetLineInfo();
                if (targetLineInfo.length >= 1 && targetLineInfo[0].getLineClass().equals(TargetDataLine.class)) {
                    this.audioDeviceList.add(new AudioDevice(info.getDescription(), info.getName()));
                }
            }
        }
    }

    public List<AudioDevice> getAudioDeviceList() {
        return this.audioDeviceList;
    }

    public AudioDevice findAudioDevice(String str) {
        return this.audioDeviceList.stream().filter(audioDevice -> audioDevice.getName().equalsIgnoreCase(str)).findFirst().orElse(null);
    }
}
