package net.minecraft.client.audio;

import net.minecraft.util.ResourceLocation;

public abstract class PositionedSound implements ISound {
   public float volume = 1.0F;
   public float pitch = 1.0F;
   public boolean repeat = false;
   public float xPosF;
   public float yPosF;
   public float zPosF;
   public int repeatDelay = 0;
   public ISound.AttenuationType attenuationType = ISound.AttenuationType.LINEAR;
   public ResourceLocation positionedSoundLocation;

   public PositionedSound(ResourceLocation soundResource) {
      this.positionedSoundLocation = soundResource;
   }

   @Override
   public ResourceLocation getSoundLocation() {
      return this.positionedSoundLocation;
   }

   @Override
   public boolean canRepeat() {
      return this.repeat;
   }

   @Override
   public int getRepeatDelay() {
      return this.repeatDelay;
   }

   @Override
   public float getVolume() {
      return this.volume;
   }

   @Override
   public float getPitch() {
      return this.pitch;
   }

   @Override
   public float getXPosF() {
      return this.xPosF;
   }

   @Override
   public float getYPosF() {
      return this.yPosF;
   }

   @Override
   public float getZPosF() {
      return this.zPosF;
   }

   @Override
   public ISound.AttenuationType getAttenuationType() {
      return this.attenuationType;
   }
}
