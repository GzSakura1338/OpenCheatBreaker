package net.minecraft.entity.player;

import net.minecraft.nbt.NBTTagCompound;

public class PlayerCapabilities {
    public boolean disableDamage;
    public boolean isFlying;
    public boolean allowFlying;
    public boolean isCreativeMode;
    public boolean allowEdit = true;
    public float flySpeed = 0.05F;
    public float walkSpeed = 0.1F;

    public void writeCapabilitiesToNBT(NBTTagCompound nbttagCompound) {
        NBTTagCompound nbttagCompound2 = new NBTTagCompound();
        nbttagCompound2.setBoolean("invulnerable", this.disableDamage);
        nbttagCompound2.setBoolean("flying", this.isFlying);
        nbttagCompound2.setBoolean("mayfly", this.allowFlying);
        nbttagCompound2.setBoolean("instabuild", this.isCreativeMode);
        nbttagCompound2.setBoolean("mayBuild", this.allowEdit);
        nbttagCompound2.setFloat("flySpeed", this.flySpeed);
        nbttagCompound2.setFloat("walkSpeed", this.walkSpeed);
        nbttagCompound.setTag("abilities", nbttagCompound2);
    }

    public void readCapabilitiesFromNBT(NBTTagCompound nbttagCompound) {
        if (nbttagCompound.hasKey("abilities", 10)) {
            NBTTagCompound compoundTag = nbttagCompound.getCompoundTag("abilities");
            this.disableDamage = compoundTag.getBoolean("invulnerable");
            this.isFlying = compoundTag.getBoolean("flying");
            this.allowFlying = compoundTag.getBoolean("mayfly");
            this.isCreativeMode = compoundTag.getBoolean("instabuild");
            if (compoundTag.hasKey("flySpeed", 99)) {
                this.flySpeed = compoundTag.getFloat("flySpeed");
                this.walkSpeed = compoundTag.getFloat("walkSpeed");
            }

            if (compoundTag.hasKey("mayBuild", 1)) {
                this.allowEdit = compoundTag.getBoolean("mayBuild");
            }
        }
    }

    public float getFlySpeed() {
        return this.flySpeed;
    }

    public void setFlySpeed(float f) {
        this.flySpeed = f;
    }

    public float getWalkSpeed() {
        return this.walkSpeed;
    }

    public void setPlayerWalkSpeed(float f) {
        this.walkSpeed = f;
    }
}
