package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import net.minecraft.util.ResourceLocation;

public class SoundEventAccessorComposite implements ISoundEventAccessor {
    public List soundPool = Lists.newArrayList();
    public Random rnd = new Random();
    public ResourceLocation id;
    public double eventVolume;
    public double eventPitch;
    public SoundCategory soundCategory;

    public SoundEventAccessorComposite(ResourceLocation resourceLocation, double d, double d2, SoundCategory soundCategory) {
        this.id = resourceLocation;
        this.eventVolume = d2;
        this.eventPitch = d;
        this.soundCategory = soundCategory;
    }

    @Override
    public int getWeight() {
        int n = 0;

        for (ISoundEventAccessor soundEventAccessor : (Iterable<ISoundEventAccessor>)this.soundPool) {
            n += soundEventAccessor.getWeight();
        }

        return n;
    }

    public SoundPoolEntry getSound() {
        int weight = this.getWeight();
        if (!this.soundPool.isEmpty() && weight != 0) {
            int nextInt = this.rnd.nextInt(weight);

            for (ISoundEventAccessor soundEventAccessor : (Iterable<ISoundEventAccessor>)this.soundPool) {
                nextInt -= soundEventAccessor.getWeight();
                if (nextInt < 0) {
                    SoundPoolEntry sound = (SoundPoolEntry)soundEventAccessor.getSound();
                    sound.setPitch(sound.getPitch() * this.eventPitch);
                    sound.setVolume(sound.getVolume() * this.eventVolume);
                    return sound;
                }
            }

            return SoundHandler.missing_sound;
        } else {
            return SoundHandler.missing_sound;
        }
    }

    public void addSoundToEventPool(ISoundEventAccessor soundEventAccessor) {
        this.soundPool.add(soundEventAccessor);
    }

    public ResourceLocation getSoundEventLocation() {
        return this.id;
    }

    public SoundCategory getSoundCategory() {
        return this.soundCategory;
    }
}
