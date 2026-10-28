package net.minecraft.client.audio;

public class SoundEventAccessor implements ISoundEventAccessor {
    public SoundPoolEntry sound;
    public int weight;

    public SoundEventAccessor(SoundPoolEntry soundPoolEntry, int n) {
        this.sound = soundPoolEntry;
        this.weight = n;
    }

    @Override
    public int getWeight() {
        return this.weight;
    }

    public SoundPoolEntry getSound() {
        return new SoundPoolEntry(this.sound);
    }
}
