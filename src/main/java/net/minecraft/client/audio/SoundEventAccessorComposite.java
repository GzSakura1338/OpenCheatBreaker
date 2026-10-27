package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.util.ResourceLocation;

public class SoundEventAccessorComposite implements ISoundEventAccessor<SoundPoolEntry> {
   public List<ISoundEventAccessor<SoundPoolEntry>> soundPool = Lists.newArrayList();
   public Random rnd = new Random();
   public ResourceLocation soundLocation;
   public double eventVolume;
   public double eventPitch;
   public SoundCategory category;

   public SoundEventAccessorComposite(ResourceLocation soundLocation, double pitch, double volume, SoundCategory category) {
      this.soundLocation = soundLocation;
      this.eventVolume = volume;
      this.eventPitch = pitch;
      this.category = category;
   }

   @Override
   public int getWeight() {
      int n = 0;

      for (ISoundEventAccessor soundEventAccessor : this.soundPool) {
         n += soundEventAccessor.getWeight();
      }

      return n;
   }

   public SoundPoolEntry cloneEntry() {
      int weight = this.getWeight();
      if (!this.soundPool.isEmpty() && weight != 0) {
         int nextInt = this.rnd.nextInt(weight);

         for (ISoundEventAccessor soundEventAccessor : this.soundPool) {
            nextInt -= soundEventAccessor.getWeight();
            if (nextInt < 0) {
               SoundPoolEntry cloneEntry = (SoundPoolEntry)soundEventAccessor.cloneEntry();
               cloneEntry.setPitch(cloneEntry.getPitch() * this.eventPitch);
               cloneEntry.setVolume(cloneEntry.getVolume() * this.eventVolume);
               return cloneEntry;
            }
         }

         return SoundHandler.missing_sound;
      } else {
         return SoundHandler.missing_sound;
      }
   }

   public void addSoundToEventPool(ISoundEventAccessor<SoundPoolEntry> sound) {
      this.soundPool.add(sound);
   }

   public ResourceLocation getSoundEventLocation() {
      return this.soundLocation;
   }

   public SoundCategory getSoundCategory() {
      return this.category;
   }
}
