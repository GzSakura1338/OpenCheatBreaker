package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.List;

public class SoundList {
   public List<SoundList.SoundEntry> soundList = Lists.newArrayList();
   public boolean replaceExisting;
   public SoundCategory category;

   public List<SoundList.SoundEntry> getSoundList() {
      return this.soundList;
   }

   public boolean canReplaceExisting() {
      return this.replaceExisting;
   }

   public void setReplaceExisting(boolean flag) {
      this.replaceExisting = flag;
   }

   public SoundCategory getSoundCategory() {
      return this.category;
   }

   public void setSoundCategory(SoundCategory soundCat) {
      this.category = soundCat;
   }

   public static class SoundEntry {
      public String name;
      public float volume = 1.0F;
      public float pitch = 1.0F;
      public int weight = 1;
      public SoundList.SoundEntry.Type type = SoundList.SoundEntry.Type.FILE;
      public boolean streaming = false;

      public String getSoundEntryName() {
         return this.name;
      }

      public void setSoundEntryName(String nameIn) {
         this.name = nameIn;
      }

      public float getSoundEntryVolume() {
         return this.volume;
      }

      public void setSoundEntryVolume(float volumeIn) {
         this.volume = volumeIn;
      }

      public float getSoundEntryPitch() {
         return this.pitch;
      }

      public void setSoundEntryPitch(float pitchIn) {
         this.pitch = pitchIn;
      }

      public int getSoundEntryWeight() {
         return this.weight;
      }

      public void setSoundEntryWeight(int weightIn) {
         this.weight = weightIn;
      }

      public SoundList.SoundEntry.Type getSoundEntryType() {
         return this.type;
      }

      public void setSoundEntryType(SoundList.SoundEntry.Type typeIn) {
         this.type = typeIn;
      }

      public boolean isStreaming() {
         return this.streaming;
      }

      public void setStreaming(boolean isStreaming) {
         this.streaming = isStreaming;
      }

      public enum Type {
         FILE("file"),
         SOUND_EVENT("event");

         public String name;

         Type(String str) {
            this.name = str;
         }

         public static SoundList.SoundEntry.Type getType(String str) {
            for (SoundList.SoundEntry.Type type : values()) {
               if (type.name.equals(str)) {
                  return type;
               }
            }

            return null;
         }
      }
   }
}
