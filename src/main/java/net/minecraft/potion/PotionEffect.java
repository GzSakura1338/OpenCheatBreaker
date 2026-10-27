package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PotionEffect {
   public static Logger LOGGER = LogManager.getLogger();
   public int potionID;
   public int duration;
   public int amplifier;
   public boolean isSplashPotion;
   public boolean isAmbient;
   public boolean isPotionDurationMax;
   public boolean showParticles;

   public PotionEffect(int id, int effectDuration) {
      this(id, effectDuration, 0);
   }

   public PotionEffect(int id, int effectDuration, int effectAmplifier) {
      this(id, effectDuration, effectAmplifier, false, true);
   }

   public PotionEffect(int id, int effectDuration, int effectAmplifier, boolean ambient, boolean showParticles) {
      this.potionID = id;
      this.duration = effectDuration;
      this.amplifier = effectAmplifier;
      this.isAmbient = ambient;
      this.showParticles = showParticles;
   }

   public PotionEffect(PotionEffect other) {
      this.potionID = other.potionID;
      this.duration = other.duration;
      this.amplifier = other.amplifier;
      this.isAmbient = other.isAmbient;
      this.showParticles = other.showParticles;
   }

   public void combine(PotionEffect other) {
      if (this.potionID != other.potionID) {
         LOGGER.warn("This method should only be called for matching effects!");
      }

      if (other.amplifier > this.amplifier) {
         this.amplifier = other.amplifier;
         this.duration = other.duration;
      } else if (other.amplifier == this.amplifier && this.duration < other.duration) {
         this.duration = other.duration;
      } else if (!other.isAmbient && this.isAmbient) {
         this.isAmbient = other.isAmbient;
      }

      this.showParticles = other.showParticles;
   }

   public int getPotionID() {
      return this.potionID;
   }

   public int getDuration() {
      return this.duration;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public void setSplashPotion(boolean splashPotion) {
      this.isSplashPotion = splashPotion;
   }

   public boolean getIsAmbient() {
      return this.isAmbient;
   }

   public boolean getIsShowParticles() {
      return this.showParticles;
   }

   public boolean onUpdate(EntityLivingBase entityIn) {
      if (this.duration > 0) {
         if (Potion.potionTypes[this.potionID].isReady(this.duration, this.amplifier)) {
            this.performEffect(entityIn);
         }

         this.deincrementDuration();
      }

      return this.duration > 0;
   }

   public int deincrementDuration() {
      return --this.duration;
   }

   public void performEffect(EntityLivingBase entityIn) {
      if (this.duration > 0) {
         Potion.potionTypes[this.potionID].performEffect(entityIn, this.amplifier);
      }
   }

   public String getEffectName() {
      return Potion.potionTypes[this.potionID].getName();
   }

   @Override
   public int hashCode() {
      return this.potionID;
   }

   @Override
   public String toString() {
      String str = "";
      if (this.getAmplifier() > 0) {
         str = this.getEffectName() + " x " + (this.getAmplifier() + 1) + ", Duration: " + this.getDuration();
      } else {
         str = this.getEffectName() + ", Duration: " + this.getDuration();
      }

      if (this.isSplashPotion) {
         str = str + ", Splash: true";
      }

      if (!this.showParticles) {
         str = str + ", Particles: false";
      }

      return Potion.potionTypes[this.potionID].isUsable() ? "(" + str + ")" : str;
   }

   @Override
   public boolean equals(Object obj) {
      if (!(obj instanceof PotionEffect)) {
         return false;
      }

      PotionEffect potionEffect = (PotionEffect)obj;
      return this.potionID == potionEffect.potionID
         && this.amplifier == potionEffect.amplifier
         && this.duration == potionEffect.duration
         && this.isSplashPotion == potionEffect.isSplashPotion
         && this.isAmbient == potionEffect.isAmbient;
   }

   public NBTTagCompound writeCustomPotionEffectToNBT(NBTTagCompound nbt) {
      nbt.setByte("Id", (byte)this.getPotionID());
      nbt.setByte("Amplifier", (byte)this.getAmplifier());
      nbt.setInteger("Duration", this.getDuration());
      nbt.setBoolean("Ambient", this.getIsAmbient());
      nbt.setBoolean("ShowParticles", this.getIsShowParticles());
      return nbt;
   }

   public static PotionEffect readCustomPotionEffectFromNBT(NBTTagCompound nbt) {
      byte byte2 = nbt.getByte("Id");
      if (byte2 >= 0 && byte2 < Potion.potionTypes.length && Potion.potionTypes[byte2] != null) {
         byte byte3 = nbt.getByte("Amplifier");
         int integer = nbt.getInteger("Duration");
         boolean boolean2 = nbt.getBoolean("Ambient");
         boolean flag = true;
         if (nbt.hasKey("ShowParticles", 1)) {
            flag = nbt.getBoolean("ShowParticles");
         }

         return new PotionEffect(byte2, integer, byte3, boolean2, flag);
      } else {
         return null;
      }
   }

   public void setPotionDurationMax(boolean maxDuration) {
      this.isPotionDurationMax = maxDuration;
   }

   public boolean getIsPotionDurationMax() {
      return this.isPotionDurationMax;
   }
}
