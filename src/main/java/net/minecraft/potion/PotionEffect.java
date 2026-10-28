package net.minecraft.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

public class PotionEffect {
    public int potionID;
    public int duration;
    public int amplifier;
    public boolean isSplashPotion;
    public boolean isAmbient;
    public boolean isPotionDurationMax;

    public PotionEffect(int n, int n2) {
        this(n, n2, 0);
    }

    public PotionEffect(int n, int n2, int n3) {
        this(n, n2, n3, false);
    }

    public PotionEffect(int n, int n2, int n3, boolean flag) {
        this.potionID = n;
        this.duration = n2;
        this.amplifier = n3;
        this.isAmbient = flag;
    }

    public PotionEffect(PotionEffect potionEffect) {
        this.potionID = potionEffect.potionID;
        this.duration = potionEffect.duration;
        this.amplifier = potionEffect.amplifier;
    }

    public void combine(PotionEffect potionEffect) {
        if (this.potionID != potionEffect.potionID) {
            System.err.println("This method should only be called for matching effects!");
        }

        if (potionEffect.amplifier > this.amplifier) {
            this.amplifier = potionEffect.amplifier;
            this.duration = potionEffect.duration;
        } else if (potionEffect.amplifier == this.amplifier && this.duration < potionEffect.duration) {
            this.duration = potionEffect.duration;
        } else if (!potionEffect.isAmbient && this.isAmbient) {
            this.isAmbient = potionEffect.isAmbient;
        }
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

    public void setSplashPotion(boolean flag) {
        this.isSplashPotion = flag;
    }

    public boolean getIsAmbient() {
        return this.isAmbient;
    }

    public boolean onUpdate(EntityLivingBase entityLivingBase) {
        if (this.duration > 0) {
            if (Potion.potionTypes[this.potionID].isReady(this.duration, this.amplifier)) {
                this.performEffect(entityLivingBase);
            }

            this.deincrementDuration();
        }

        return this.duration > 0;
    }

    public int deincrementDuration() {
        return --this.duration;
    }

    public void performEffect(EntityLivingBase entityLivingBase) {
        if (this.duration > 0) {
            Potion.potionTypes[this.potionID].performEffect(entityLivingBase, this.amplifier);
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

    public NBTTagCompound writeCustomPotionEffectToNBT(NBTTagCompound nbttagCompound) {
        nbttagCompound.setByte("Id", (byte)this.getPotionID());
        nbttagCompound.setByte("Amplifier", (byte)this.getAmplifier());
        nbttagCompound.setInteger("Duration", this.getDuration());
        nbttagCompound.setBoolean("Ambient", this.getIsAmbient());
        return nbttagCompound;
    }

    public static PotionEffect readCustomPotionEffectFromNBT(NBTTagCompound nbttagCompound) {
        byte byte2 = nbttagCompound.getByte("Id");
        if (byte2 >= 0 && byte2 < Potion.potionTypes.length && Potion.potionTypes[byte2] != null) {
            byte byte3 = nbttagCompound.getByte("Amplifier");
            int integer = nbttagCompound.getInteger("Duration");
            boolean boolean2 = nbttagCompound.getBoolean("Ambient");
            return new PotionEffect(byte2, integer, byte3, boolean2);
        } else {
            return null;
        }
    }

    public void setPotionDurationMax(boolean flag) {
        this.isPotionDurationMax = flag;
    }

    public boolean getIsPotionDurationMax() {
        return this.isPotionDurationMax;
    }
}
