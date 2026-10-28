package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

public class SoundPoolEntry {
    public ResourceLocation id;
    public double volume;
    public double pitch;
    public boolean flag;

    public SoundPoolEntry(ResourceLocation resourceLocation, double d, double d2, boolean flag) {
        this.id = resourceLocation;
        this.volume = d;
        this.pitch = d2;
        this.flag = flag;
    }

    public SoundPoolEntry(SoundPoolEntry soundPoolEntry) {
        this.id = soundPoolEntry.id;
        this.volume = soundPoolEntry.volume;
        this.pitch = soundPoolEntry.pitch;
        this.flag = soundPoolEntry.flag;
    }

    public ResourceLocation getSoundPoolEntryLocation() {
        return this.id;
    }

    public double getPitch() {
        return this.volume;
    }

    public void setPitch(double d) {
        this.volume = d;
    }

    public double getVolume() {
        return this.pitch;
    }

    public void setVolume(double d) {
        this.pitch = d;
    }

    public boolean isStreamingSound() {
        return this.flag;
    }
}
