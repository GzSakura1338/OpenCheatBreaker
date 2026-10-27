package net.minecraft.client.audio;

public class SoundEventAccessor implements ISoundEventAccessor<SoundPoolEntry> {
   public SoundPoolEntry entry;
   public int weight;

   public SoundEventAccessor(SoundPoolEntry entry, int weight) {
      this.entry = entry;
      this.weight = weight;
   }

   @Override
   public int getWeight() {
      return this.weight;
   }

   public SoundPoolEntry cloneEntry() {
      return new SoundPoolEntry(this.entry);
   }
}
