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

   public String formatText(String str) {
      Iterator iterator = this.audioDeviceList.iterator();
      if (!iterator.hasNext()) {
         return str;
      }

      AudioDevice audioDevice;
      for (audioDevice = (AudioDevice)iterator.next(); !audioDevice.getText2().equals(str); audioDevice = (AudioDevice)iterator.next()) {
         if (!iterator.hasNext()) {
            return str;
         }
      }

      return audioDevice.getText();
   }

   public List<AudioDevice> getAudioDeviceList() {
      return this.audioDeviceList;
   }

   public void handleString(String str, float f) {
      if (!(Boolean)CheatBreaker.getInstance().getKeybindSettings().muteCheatbreakerSounds.getValue()) {
         Minecraft.getMinecraft().getSoundHandler().sndManager.handleString(str, f);
      }
   }

   public String[] getStringArray() {
      return this.audioDeviceList.stream().map(audioDevice -> audioDevice.getText2().replace("Primary Sound Capture Driver", "Default")).toArray(String[]::new);
   }

   public AudioDevice findAudioDevice(String str) {
      return this.audioDeviceList.stream().filter(audioDevice -> audioDevice.getText2().equalsIgnoreCase(str)).findFirst().orElse(null);
   }

   public void run() {
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

   public void handleString(String str) {
      this.handleString(str, 1.0F);
   }
}
